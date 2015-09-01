/*
 ** $Id: lVvm.c,v 2.63.1.3 2007/12/28 15:32:23 roberto Exp $
 ** [L u a] virtual machine
 ** See Copyright Notice in lV.h
 */


#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define lvm_c
#define LV_CORE

#include "lV.h"

#include "lVdebug.h"
#include "lVdo.h"
#include "lVfunc.h"
#include "lVgc.h"
#include "lVobject.h"
#include "lVopcodes.h"
#include "lVstate.h"
#include "lVstring.h"
#include "lVtable.h"
#include "lVtm.h"
#include "lVvm.h"



/* limit for table tag-method chains (to avoid loops) */
#define MAXTAGLOOP	100


const TValue *lvV_tonumber (const TValue *obj, TValue *n) {
    lv_Number num;
    if (ttisnumber(obj)) return obj;
    if (ttisstring(obj) && lvO_str2d(svalue(obj), &num)) {
        setnvalue(n, num);
        return n;
    }
    else
        return NULL;
}


int lvV_tostring (lv_State *L, StkId obj) {
    if (!ttisnumber(obj))
        return 0;
    else {
        char s[LVI_MAXNUMBER2STR];
        lv_Number n = nvalue(obj);
        lv_number2str(s, n);
        setsvalue2s(L, obj, lvS_new(L, s));
        return 1;
    }
}


static void traceexec (lv_State *L, const Instruction *pc) {
    lu_byte mask = L->hookmask;
    const Instruction *oldpc = L->savedpc;
    L->savedpc = pc;
    if ((mask & LV_MASKCOUNT) && L->hookcount == 0) {
        resethookcount(L);
        lvD_callhook(L, LV_HOOKCOUNT, -1);
    }
    if (mask & LV_MASKLINE) {
        Proto *p = ci_func(L->ci)->l.p;
        int npc = pcRel(pc, p);
        int newline = getline(p, npc);
        /* call linehook when enter a new function, when jump back (loop),
         or when enter a new line */
        if (npc == 0 || pc <= oldpc || newline != getline(p, pcRel(oldpc, p)))
            lvD_callhook(L, LV_HOOKLINE, newline);
    }
}


static void callTMres (lv_State *L, StkId res, const TValue *f,
                       const TValue *p1, const TValue *p2) {
    ptrdiff_t result = savestack(L, res);
    setobj2s(L, L->top, f);  /* push function */
    setobj2s(L, L->top+1, p1);  /* 1st argument */
    setobj2s(L, L->top+2, p2);  /* 2nd argument */
    lvD_checkstack(L, 3);
    L->top += 3;
    lvD_call(L, L->top - 3, 1);
    res = restorestack(L, result);
    L->top--;
    setobjs2s(L, res, L->top);
}



static void callTM (lv_State *L, const TValue *f, const TValue *p1,
                    const TValue *p2, const TValue *p3) {
    setobj2s(L, L->top, f);  /* push function */
    setobj2s(L, L->top+1, p1);  /* 1st argument */
    setobj2s(L, L->top+2, p2);  /* 2nd argument */
    setobj2s(L, L->top+3, p3);  /* 3th argument */
    lvD_checkstack(L, 4);
    L->top += 4;
    lvD_call(L, L->top - 4, 0);
}


void lvV_gettable (lv_State *L, const TValue *t, TValue *key, StkId val) {
    int loop;
    for (loop = 0; loop < MAXTAGLOOP; loop++) {
        const TValue *tm;
        if (ttistable(t)) {  /* `t' is a table? */
            Table *h = hvalue(t);
            const TValue *res = lvH_get(h, key); /* do a primitive get */
            if (!ttisnil(res) ||  /* result is no nil? */
                (tm = fasttm(L, h->metatable, TM_INDEX)) == NULL) { /* or no TM? */
                setobj2s(L, val, res);
                return;
            }
            /* else will try the tag method */
        }
        else if (ttisnil(tm = lvT_gettmbyobj(L, t, TM_INDEX))) {
            if( ttisfunction(t) ) {
                callTMres(L, val, t, key, key);//支持函数索引调用
                return;
            } else {
                lvG_typeerror(L, t, "index");
            }
        }
        if (ttisfunction(tm)) {
            callTMres(L, val, tm, t, key);
            return;
        }
        t = tm;  /* else repeat with `tm' */
    }
    lvG_runerror(L, "loop in gettable");
}


void lvV_settable (lv_State *L, const TValue *t, TValue *key, StkId val) {
    int loop;
    for (loop = 0; loop < MAXTAGLOOP; loop++) {
        const TValue *tm;
        if (ttistable(t)) {  /* `t' is a table? */
            Table *h = hvalue(t);
            TValue *oldval = lvH_set(L, h, key); /* do a primitive set */
            if (!ttisnil(oldval) ||  /* result is no nil? */
                (tm = fasttm(L, h->metatable, TM_NEWINDEX)) == NULL) { /* or no TM? */
                setobj2t(L, oldval, val);
                lvC_barriert(L, h, val);
                return;
            }
            /* else will try the tag method */
        }
        else if (ttisnil(tm = lvT_gettmbyobj(L, t, TM_NEWINDEX)))
            lvG_typeerror(L, t, "index");
        if (ttisfunction(tm)) {
            callTM(L, tm, t, key, val);
            return;
        }
        t = tm;  /* else repeat with `tm' */
    }
    lvG_runerror(L, "loop in settable");
}


static int call_binTM (lv_State *L, const TValue *p1, const TValue *p2,
                       StkId res, TMS event) {
    const TValue *tm = lvT_gettmbyobj(L, p1, event);  /* try first operand */
    if (ttisnil(tm))
        tm = lvT_gettmbyobj(L, p2, event);  /* try second operand */
    if (ttisnil(tm)) return 0;
    callTMres(L, res, tm, p1, p2);
    return 1;
}


static const TValue *get_compTM (lv_State *L, Table *mt1, Table *mt2,
                                 TMS event) {
    const TValue *tm1 = fasttm(L, mt1, event);
    const TValue *tm2;
    if (tm1 == NULL) return NULL;  /* no metamethod */
    if (mt1 == mt2) return tm1;  /* same metatables => same metamethods */
    tm2 = fasttm(L, mt2, event);
    if (tm2 == NULL) return NULL;  /* no metamethod */
    if (lvO_rawequalObj(tm1, tm2))  /* same metamethods? */
        return tm1;
    return NULL;
}


static int call_orderTM (lv_State *L, const TValue *p1, const TValue *p2,
                         TMS event) {
    const TValue *tm1 = lvT_gettmbyobj(L, p1, event);
    const TValue *tm2;
    if (ttisnil(tm1)) return -1;  /* no metamethod? */
    tm2 = lvT_gettmbyobj(L, p2, event);
    if (!lvO_rawequalObj(tm1, tm2))  /* different metamethods? */
        return -1;
    callTMres(L, L->top, tm1, p1, p2);
    return !l_isfalse(L->top);
}


static int l_strcmp (const TString *ls, const TString *rs) {
    const char *l = getstr(ls);
    size_t ll = ls->tsv.len;
    const char *r = getstr(rs);
    size_t lr = rs->tsv.len;
    for (;;) {
        int temp = strcoll(l, r);
        if (temp != 0) return temp;
        else {  /* strings are equal up to a `\0' */
            size_t len = strlen(l);  /* index of first `\0' in both strings */
            if (len == lr)  /* r is finished? */
                return (len == ll) ? 0 : 1;
            else if (len == ll)  /* l is finished? */
                return -1;  /* l is smaller than r (because r is not finished) */
            /* both strings longer than `len'; go on comparing (after the `\0') */
            len++;
            l += len; ll -= len; r += len; lr -= len;
        }
    }
}


int lvV_lessthan (lv_State *L, const TValue *l, const TValue *r) {
    int res;
    if (ttype(l) != ttype(r))
        return lvG_ordererror(L, l, r);
    else if (ttisnumber(l))
        return lvi_numlt(nvalue(l), nvalue(r));
    else if (ttisstring(l))
        return l_strcmp(rawtsvalue(l), rawtsvalue(r)) < 0;
    else if ((res = call_orderTM(L, l, r, TM_LT)) != -1)
        return res;
    return lvG_ordererror(L, l, r);
}


static int lessequal (lv_State *L, const TValue *l, const TValue *r) {
    int res;
    if (ttype(l) != ttype(r))
        return lvG_ordererror(L, l, r);
    else if (ttisnumber(l))
        return lvi_numle(nvalue(l), nvalue(r));
    else if (ttisstring(l))
        return l_strcmp(rawtsvalue(l), rawtsvalue(r)) <= 0;
    else if ((res = call_orderTM(L, l, r, TM_LE)) != -1)  /* first try `le' */
        return res;
    else if ((res = call_orderTM(L, r, l, TM_LT)) != -1)  /* else try `lt' */
        return !res;
    return lvG_ordererror(L, l, r);
}


int lvV_equalval (lv_State *L, const TValue *t1, const TValue *t2) {
    const TValue *tm;
    lv_assert(ttype(t1) == ttype(t2));
    switch (ttype(t1)) {
        case LV_TNIL: return 1;
        case LV_TNUMBER: return lvi_numeq(nvalue(t1), nvalue(t2));
        case LV_TBOOLEAN: return bvalue(t1) == bvalue(t2);  /* true must be 1 !! */
        case LV_TLIGHTUSERDATA: return pvalue(t1) == pvalue(t2);
        case LV_TUSERDATA: {
            if (uvalue(t1) == uvalue(t2)) return 1;
            tm = get_compTM(L, uvalue(t1)->metatable, uvalue(t2)->metatable,
                            TM_EQ);
            break;  /* will try TM */
        }
        case LV_TTABLE: {
            if (hvalue(t1) == hvalue(t2)) return 1;
            tm = get_compTM(L, hvalue(t1)->metatable, hvalue(t2)->metatable, TM_EQ);
            break;  /* will try TM */
        }
        default: return gcvalue(t1) == gcvalue(t2);
    }
    if (tm == NULL) return 0;  /* no TM? */
    callTMres(L, L->top, tm, t1, t2);  /* call TM */
    return !l_isfalse(L->top);
}


void lvV_concat (lv_State *L, int total, int last) {
    do {
        StkId top = L->base + last + 1;
        int n = 2;  /* number of elements handled in this pass (at least 2) */
        if (!(ttisstring(top-2) || ttisnumber(top-2)) || !tostring(L, top-1)) {
            if (!call_binTM(L, top-2, top-1, top-2, TM_CONCAT))
                lvG_concaterror(L, top-2, top-1);
        } else if (tsvalue(top-1)->len == 0)  /* second op is empty? */
            (void)tostring(L, top - 2);  /* result is first op (as string) */
        else {
            /* at least two string values; get as many as possible */
            size_t tl = tsvalue(top-1)->len;
            char *buffer;
            int i;
            /* collect total length */
            for (n = 1; n < total && tostring(L, top-n-1); n++) {
                size_t l = tsvalue(top-n-1)->len;
                if (l >= MAX_SIZET - tl) lvG_runerror(L, "string length overflow");
                tl += l;
            }
            buffer = lvZ_openspace(L, &G(L)->buff, tl);
            tl = 0;
            for (i=n; i>0; i--) {  /* concat all strings */
                size_t l = tsvalue(top-i)->len;
                memcpy(buffer+tl, svalue(top-i), l);
                tl += l;
            }
            setsvalue2s(L, top-n, lvS_newlstr(L, buffer, tl));
        }
        total -= n-1;  /* got `n' strings to create 1 new */
        last -= n-1;
    } while (total > 1);  /* repeat until only 1 result left */
}


static void Arith (lv_State *L, StkId ra, const TValue *rb,
                   const TValue *rc, TMS op) {
    TValue tempb, tempc;
    const TValue *b, *c;
    if ((b = lvV_tonumber(rb, &tempb)) != NULL &&
        (c = lvV_tonumber(rc, &tempc)) != NULL) {
        lv_Number nb = nvalue(b), nc = nvalue(c);
        switch (op) {
            case TM_ADD: setnvalue(ra, lvi_numadd(nb, nc)); break;
            case TM_SUB: setnvalue(ra, lvi_numsub(nb, nc)); break;
            case TM_MUL: setnvalue(ra, lvi_nummul(nb, nc)); break;
            case TM_DIV: setnvalue(ra, lvi_numdiv(nb, nc)); break;
            case TM_MOD: setnvalue(ra, lvi_nummod(nb, nc)); break;
            case TM_POW: setnvalue(ra, lvi_numpow(nb, nc)); break;
            case TM_UNM: setnvalue(ra, lvi_numunm(nb)); break;
            default: lv_assert(0); break;
        }
    }
    else if (!call_binTM(L, rb, rc, ra, op))
        lvG_aritherror(L, rb, rc);
}



/*
 ** some macros for common tasks in `lvV_execute'
 */

#define runtime_check(L, c)	{ if (!(c)) break; }

#define RA(i)	(base+GETARG_A(i))
/* to be used after possible stack reallocation */
#define RB(i)	check_exp(getBMode(GET_OPCODE(i)) == OpArgR, base+GETARG_B(i))
#define RC(i)	check_exp(getCMode(GET_OPCODE(i)) == OpArgR, base+GETARG_C(i))
#define RKB(i)	check_exp(getBMode(GET_OPCODE(i)) == OpArgK, \
ISK(GETARG_B(i)) ? k+INDEXK(GETARG_B(i)) : base+GETARG_B(i))
#define RKC(i)	check_exp(getCMode(GET_OPCODE(i)) == OpArgK, \
ISK(GETARG_C(i)) ? k+INDEXK(GETARG_C(i)) : base+GETARG_C(i))
#define KBx(i)	check_exp(getBMode(GET_OPCODE(i)) == OpArgK, k+GETARG_Bx(i))


#define dojump(L,pc,i)	{(pc) += (i); lvi_threadyield(L);}


#define Protect(x)	{ L->savedpc = pc; {x;}; base = L->base; }


#define arith_op(op,tm) { \
TValue *rb = RKB(i); \
TValue *rc = RKC(i); \
if (ttisnumber(rb) && ttisnumber(rc)) { \
lv_Number nb = nvalue(rb), nc = nvalue(rc); \
setnvalue(ra, op(nb, nc)); \
} \
else \
Protect(Arith(L, ra, rb, rc, tm)); \
}



void lvV_execute (lv_State *L, int nexeccalls) {
    LClosure *cl;
    StkId base;
    TValue *k;
    const Instruction *pc;
reentry:  /* entry point */
    lv_assert(isL_u_a(L->ci));
    pc = L->savedpc;
    cl = &clvalue(L->ci->func)->l;
    base = L->base;
    k = cl->p->k;
    /* main loop of interpreter */
    for (;;) {
        const Instruction i = *pc++;
        StkId ra;
        if ((L->hookmask & (LV_MASKLINE | LV_MASKCOUNT)) &&
            (--L->hookcount == 0 || L->hookmask & LV_MASKLINE)) {
            traceexec(L, pc);
            if (L->status == LV_YIELD) {  /* did hook yield? */
                L->savedpc = pc - 1;
                return;
            }
            base = L->base;
        }
        /* warning!! several calls may realloc the stack and invalidate `ra' */
        ra = RA(i);
        lv_assert(base == L->base && L->base == L->ci->base);
        lv_assert(base <= L->top && L->top <= L->stack + L->stacksize);
        lv_assert(L->top == L->ci->top || lvG_checkopenop(i));
        switch (GET_OPCODE(i)) {
            case OP_MOVE: {
                StkId rb = RB(i);
                setobjs2s(L, ra, rb );
                continue;
            }
            case OP_LOADK: {
                TValue* temp = KBx(i);
                setobj2s(L, ra, temp );
                continue;
            }
            case OP_LOADBOOL: {
                setbvalue(ra, GETARG_B(i));
                if (GETARG_C(i)) pc++;  /* skip next instruction (if C) */
                continue;
            }
            case OP_LOADNIL: {
                TValue *rb = RB(i);
                do {
                    setnilvalue(rb--);
                } while (rb >= ra);
                continue;
            }
            case OP_GETUPVAL: {
                int b = GETARG_B(i);
                setobj2s(L, ra, cl->upvals[b]->v);
                continue;
            }
            case OP_GETGLOBAL: {
                TValue g;
                TValue *rb = KBx(i);
                sethvalue(L, &g, cl->env);
                lv_assert(ttisstring(rb));
                Protect(lvV_gettable(L, &g, rb, ra));
                continue;
            }
            case OP_GETTABLE: {
                Protect(lvV_gettable(L, RB(i), RKC(i), ra));
                continue;
            }
            case OP_SETGLOBAL: {
                TValue g;
                sethvalue(L, &g, cl->env);
                lv_assert(ttisstring(KBx(i)));
                Protect(lvV_settable(L, &g, KBx(i), ra));
                continue;
            }
            case OP_SETUPVAL: {
                UpVal *uv = cl->upvals[GETARG_B(i)];
                setobj(L, uv->v, ra);
                lvC_barrier(L, uv, ra);
                continue;
            }
            case OP_SETTABLE: {
                Protect(lvV_settable(L, ra, RKB(i), RKC(i)));
                continue;
            }
            case OP_NEWTABLE: {
                int b = GETARG_B(i);
                int c = GETARG_C(i);
                sethvalue(L, ra, lvH_new(L, lvO_fb2int(b), lvO_fb2int(c)));
                Protect(lvC_checkGC(L));
                continue;
            }
            case OP_SELF: {
                StkId rb = RB(i);
                setobjs2s(L, ra+1, rb);
                Protect(lvV_gettable(L, rb, RKC(i), ra));
                continue;
            }
            case OP_ADD: {
                arith_op(lvi_numadd, TM_ADD);
                continue;
            }
            case OP_SUB: {
                arith_op(lvi_numsub, TM_SUB);
                continue;
            }
            case OP_MUL: {
                arith_op(lvi_nummul, TM_MUL);
                continue;
            }
            case OP_DIV: {
                arith_op(lvi_numdiv, TM_DIV);
                continue;
            }
            case OP_MOD: {
                arith_op(lvi_nummod, TM_MOD);
                continue;
            }
            case OP_POW: {
                arith_op(lvi_numpow, TM_POW);
                continue;
            }
            case OP_UNM: {
                TValue *rb = RB(i);
                if (ttisnumber(rb)) {
                    lv_Number nb = nvalue(rb);
                    setnvalue(ra, lvi_numunm(nb));
                }
                else {
                    Protect(Arith(L, ra, rb, rb, TM_UNM));
                }
                continue;
            }
            case OP_NOT: {
                int res = l_isfalse(RB(i));  /* next assignment may change this value */
                setbvalue(ra, res);
                continue;
            }
            case OP_LEN: {
                const TValue *rb = RB(i);
                switch (ttype(rb)) {
                    case LV_TTABLE: {
                        setnvalue(ra, cast_num(lvH_getn(hvalue(rb))));
                        break;
                    }
                    case LV_TSTRING: {
                        setnvalue(ra, cast_num(tsvalue(rb)->len));
                        break;
                    }
                    default: {  /* try metamethod */
                        Protect(
                                if (!call_binTM(L, rb, lvO_nilobject, ra, TM_LEN))
                                lvG_typeerror(L, rb, "get length of");
                                )
                    }
                }
                continue;
            }
            case OP_CONCAT: {
                int b = GETARG_B(i);
                int c = GETARG_C(i);
                Protect(lvV_concat(L, c-b+1, c); lvC_checkGC(L));
                setobjs2s(L, RA(i), base+b);
                continue;
            }
            case OP_JMP: {
                dojump(L, pc, GETARG_sBx(i));
                continue;
            }
            case OP_EQ: {
                TValue *rb = RKB(i);
                TValue *rc = RKC(i);
                Protect(
                        if (equalobj(L, rb, rc) == GETARG_A(i))
                        dojump(L, pc, GETARG_sBx(*pc));
                        )
                pc++;
                continue;
            }
            case OP_LT: {
                Protect(
                        if (lvV_lessthan(L, RKB(i), RKC(i)) == GETARG_A(i))
                        dojump(L, pc, GETARG_sBx(*pc));
                        )
                pc++;
                continue;
            }
            case OP_LE: {
                Protect(
                        if (lessequal(L, RKB(i), RKC(i)) == GETARG_A(i))
                        dojump(L, pc, GETARG_sBx(*pc));
                        )
                pc++;
                continue;
            }
            case OP_TEST: {
                if (l_isfalse(ra) != GETARG_C(i))
                    dojump(L, pc, GETARG_sBx(*pc));
                pc++;
                continue;
            }
            case OP_TESTSET: {
                TValue *rb = RB(i);
                if (l_isfalse(rb) != GETARG_C(i)) {
                    setobjs2s(L, ra, rb);
                    dojump(L, pc, GETARG_sBx(*pc));
                }
                pc++;
                continue;
            }
            case OP_CALL: {
                int b = GETARG_B(i);
                int nresults = GETARG_C(i) - 1;
                if (b != 0) L->top = ra+b;  /* else previous instruction set top */
                L->savedpc = pc;
                switch (lvD_precall(L, ra, nresults)) {
                    case PCR_L_U_A: {
                        nexeccalls++;
                        goto reentry;  /* restart lvV_execute over new [L u a] function */
                    }
                    case PCRC: {
                        /* it was a C function (`precall' called it); adjust results */
                        if (nresults >= 0) L->top = L->ci->top;
                        base = L->base;
                        continue;
                    }
                    default: {
                        return;  /* yield */
                    }
                }
            }
            case OP_TAILCALL: {
                int b = GETARG_B(i);
                if (b != 0) L->top = ra+b;  /* else previous instruction set top */
                L->savedpc = pc;
                lv_assert(GETARG_C(i) - 1 == LV_MULTRET);
                switch (lvD_precall(L, ra, LV_MULTRET)) {
                    case PCR_L_U_A: {
                        /* tail call: put new frame in place of previous one */
                        CallInfo *ci = L->ci - 1;  /* previous frame */
                        int aux;
                        StkId func = ci->func;
                        StkId pfunc = (ci+1)->func;  /* previous function index */
                        if (L->openupval) lvF_close(L, ci->base);
                        L->base = ci->base = ci->func + ((ci+1)->base - pfunc);
                        for (aux = 0; pfunc+aux < L->top; aux++)  /* move frame down */
                            setobjs2s(L, func+aux, pfunc+aux);
                        ci->top = L->top = func+aux;  /* correct top */
                        lv_assert(L->top == L->base + clvalue(func)->l.p->maxstacksize);
                        ci->savedpc = L->savedpc;
                        ci->tailcalls++;  /* one more call lost */
                        L->ci--;  /* remove new frame */
                        goto reentry;
                    }
                    case PCRC: {  /* it was a C function (`precall' called it) */
                        base = L->base;
                        continue;
                    }
                    default: {
                        return;  /* yield */
                    }
                }
            }
            case OP_RETURN: {
                int b = GETARG_B(i);
                if (b != 0) L->top = ra+b-1;
                if (L->openupval) lvF_close(L, base);
                L->savedpc = pc;
                b = lvD_poscall(L, ra);
                if (--nexeccalls == 0)  /* was previous function running `here'? */
                    return;  /* no: return */
                else {  /* yes: continue its execution */
                    if (b) L->top = L->ci->top;
                    lv_assert(isL_u_a(L->ci));
                    lv_assert(GET_OPCODE(*((L->ci)->savedpc - 1)) == OP_CALL);
                    goto reentry;
                }
            }
            case OP_FORLOOP: {
                lv_Number step = nvalue(ra+2);
                lv_Number idx = lvi_numadd(nvalue(ra), step); /* increment index */
                lv_Number limit = nvalue(ra+1);
                if (lvi_numlt(0, step) ? lvi_numle(idx, limit)
                    : lvi_numle(limit, idx)) {
                    dojump(L, pc, GETARG_sBx(i));  /* jump back */
                    setnvalue(ra, idx);  /* update internal index... */
                    setnvalue(ra+3, idx);  /* ...and external index */
                }
                continue;
            }
            case OP_FORPREP: {
                const TValue *init = ra;
                const TValue *plimit = ra+1;
                const TValue *pstep = ra+2;
                L->savedpc = pc;  /* next steps may throw errors */
                if (!tonumber(init, ra))
                    lvG_runerror(L, LV_QL("for") " initial value must be a number");
                else if (!tonumber(plimit, ra+1))
                    lvG_runerror(L, LV_QL("for") " limit must be a number");
                else if (!tonumber(pstep, ra+2))
                    lvG_runerror(L, LV_QL("for") " step must be a number");
                setnvalue(ra, lvi_numsub(nvalue(ra), nvalue(pstep)));
                dojump(L, pc, GETARG_sBx(i));
                continue;
            }
            case OP_TFORLOOP: {
                StkId cb = ra + 3;  /* call base */
                setobjs2s(L, cb+2, ra+2);
                setobjs2s(L, cb+1, ra+1);
                setobjs2s(L, cb, ra);
                L->top = cb+3;  /* func. + 2 args (state and index) */
                Protect(lvD_call(L, cb, GETARG_C(i)));
                L->top = L->ci->top;
                cb = RA(i) + 3;  /* previous call may change the stack */
                if (!ttisnil(cb)) {  /* continue loop? */
                    setobjs2s(L, cb-1, cb);  /* save control variable */
                    dojump(L, pc, GETARG_sBx(*pc));  /* jump back */
                }
                pc++;
                continue;
            }
            case OP_SETLIST: {
                int n = GETARG_B(i);
                int c = GETARG_C(i);
                int last;
                Table *h;
                if (n == 0) {
                    n = cast_int(L->top - ra) - 1;
                    L->top = L->ci->top;
                }
                if (c == 0) c = cast_int(*pc++);
                runtime_check(L, ttistable(ra));
                h = hvalue(ra);
                last = ((c-1)*LFIELDS_PER_FLUSH) + n;
                if (last > h->sizearray)  /* needs more space? */
                    lvH_resizearray(L, h, last);  /* pre-alloc it at once */
                for (; n > 0; n--) {
                    TValue *val = ra+n;
                    setobj2t(L, lvH_setnum(L, h, last--), val);
                    lvC_barriert(L, h, val);
                }
                continue;
            }
            case OP_CLOSE: {
                lvF_close(L, ra);
                continue;
            }
            case OP_CLOSURE: {
                Proto *p;
                Closure *ncl;
                int nup, j;
                p = cl->p->p[GETARG_Bx(i)];
                nup = p->nups;
                ncl = lvF_newLclosure(L, nup, cl->env);
                ncl->l.p = p;
                for (j=0; j<nup; j++, pc++) {
                    if (GET_OPCODE(*pc) == OP_GETUPVAL)
                        ncl->l.upvals[j] = cl->upvals[GETARG_B(*pc)];
                    else {
                        lv_assert(GET_OPCODE(*pc) == OP_MOVE);
                        ncl->l.upvals[j] = lvF_findupval(L, base + GETARG_B(*pc));
                    }
                }
                setclvalue(L, ra, ncl);
                Protect(lvC_checkGC(L));
                continue;
            }
            case OP_VARARG: {
                int b = GETARG_B(i) - 1;
                int j;
                CallInfo *ci = L->ci;
                int n = cast_int(ci->base - ci->func) - cl->p->numparams - 1;
                if (b == LV_MULTRET) {
                    Protect(lvD_checkstack(L, n));
                    ra = RA(i);  /* previous call may change the stack */
                    b = n;
                    L->top = ra + n;
                }
                for (j = 0; j < b; j++) {
                    if (j < n) {
                        setobjs2s(L, ra + j, ci->base - n + j);
                    }
                    else {
                        setnilvalue(ra + j);
                    }
                }
                continue;
            }
        }
    }
}

