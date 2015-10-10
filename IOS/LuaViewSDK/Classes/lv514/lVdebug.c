/*
 ** $Id: lVdebug.c,v 2.29.1.6 2008/05/08 16:56:26 roberto Exp $
 ** Debug Interface
 ** See Copyright Notice in lV.h
 */


#include <stdarg.h>
#include <stddef.h>
#include <string.h>


#define ldebug_c
#define LV_CORE

#include "lV.h"

#include "lVapi.h"
#include "lVcode.h"
#include "lVdebug.h"
#include "lVdo.h"
#include "lVfunc.h"
#include "lVobject.h"
#include "lVopcodes.h"
#include "lVstate.h"
#include "lVstring.h"
#include "lVtable.h"
#include "lVtm.h"
#include "lVvm.h"



static const char *getfuncname (lv_State *L, CallInfo *ci, const char **name);


static int currentpc (lv_State *L, CallInfo *ci) {
    if (!isL_u_a(ci)) return -1;  /* function is not a [L u a] function? */
    if (ci == L->ci)
        ci->savedpc = L->savedpc;
    return pcRel(ci->savedpc, ci_func(ci)->l.p);
}


static int currentline (lv_State *L, CallInfo *ci) {
    int pc = currentpc(L, ci);
    if (pc < 0)
        return -1;  /* only active (l u a) functions have current-line information */
    else
        return getline(ci_func(ci)->l.p, pc);
}


/*
 ** this function can be called asynchronous (e.g. during a signal)
 */
LV_API int lv_sethook (lv_State *L, lv_Hook func, int mask, int count) {
    if (func == NULL || mask == 0) {  /* turn off hooks? */
        mask = 0;
        func = NULL;
    }
    L->hook = func;
    L->basehookcount = count;
    resethookcount(L);
    L->hookmask = cast_byte(mask);
    return 1;
}


LV_API lv_Hook lv_gethook (lv_State *L) {
    return L->hook;
}


LV_API int lv_gethookmask (lv_State *L) {
    return L->hookmask;
}


LV_API int lv_gethookcount (lv_State *L) {
    return L->basehookcount;
}


LV_API int lv_getstack (lv_State *L, int level, lv_Debug *ar) {
    int status;
    CallInfo *ci;
    lv_lock(L);
    for (ci = L->ci; level > 0 && ci > L->base_ci; ci--) {
        level--;
        if (f_isL_u_a(ci))  /* [L u a] function? */
            level -= ci->tailcalls;  /* skip lost tail calls */
    }
    if (level == 0 && ci > L->base_ci) {  /* level found? */
        status = 1;
        ar->i_ci = cast_int(ci - L->base_ci);
    }
    else if (level < 0) {  /* level is of a lost tail call? */
        status = 1;
        ar->i_ci = 0;
    }
    else status = 0;  /* no such level */
    lv_unlock(L);
    return status;
}


static Proto *get_l_u_a_proto (CallInfo *ci) {
    return (isL_u_a(ci) ? ci_func(ci)->l.p : NULL);
}


static const char *findlocal (lv_State *L, CallInfo *ci, int n) {
    const char *name;
    Proto *fp = get_l_u_a_proto(ci);
    if (fp && (name = lvF_getlocalname(fp, n, currentpc(L, ci))) != NULL)
        return name;  /* is a local variable in a [L u a] function */
    else {
        StkId limit = (ci == L->ci) ? L->top : (ci+1)->func;
        if (limit - ci->base >= n && n > 0)  /* is 'n' inside 'ci' stack? */
            return "(*temporary)";
        else
            return NULL;
    }
}


LV_API const char *lv_getlocal (lv_State *L, const lv_Debug *ar, int n) {
    CallInfo *ci = L->base_ci + ar->i_ci;
    const char *name = findlocal(L, ci, n);
    lv_lock(L);
    if (name)
        lvA_pushobject(L, ci->base + (n - 1));
    lv_unlock(L);
    return name;
}


LV_API const char *lv_setlocal (lv_State *L, const lv_Debug *ar, int n) {
    CallInfo *ci = L->base_ci + ar->i_ci;
    const char *name = findlocal(L, ci, n);
    lv_lock(L);
    if (name)
        setobjs2s(L, ci->base + (n - 1), L->top - 1);
    L->top--;  /* pop value */
    lv_unlock(L);
    return name;
}


static void funcinfo (lv_Debug *ar, Closure *cl) {
    if (cl->c.isC) {
        ar->source = "=[C]";
        ar->linedefined = -1;
        ar->lastlinedefined = -1;
        ar->what = "C";
    }
    else {
        ar->source = getstr(cl->l.p->source);
        ar->linedefined = cl->l.p->linedefined;
        ar->lastlinedefined = cl->l.p->lastlinedefined;
        ar->what = (ar->linedefined == 0) ? "main" : "[L u a]";
    }
    lvO_chunkid(ar->short_src, ar->source, LV_IDSIZE);
}


static void info_tailcall (lv_Debug *ar) {
    ar->name = ar->namewhat = "";
    ar->what = "tail";
    ar->lastlinedefined = ar->linedefined = ar->currentline = -1;
    ar->source = "=(tail call)";
    lvO_chunkid(ar->short_src, ar->source, LV_IDSIZE);
    ar->nups = 0;
}


static void collectvalidlines (lv_State *L, Closure *f) {
    if (f == NULL || f->c.isC) {
        setnilvalue(L->top);
    }
    else {
        Table *t = lvH_new(L, 0, 0);
        int *lineinfo = f->l.p->lineinfo;
        int i;
        for (i=0; i<f->l.p->sizelineinfo; i++)
            setbvalue(lvH_setnum(L, t, lineinfo[i]), 1);
        sethvalue(L, L->top, t);
    }
    incr_top(L);
}


static int auxgetinfo (lv_State *L, const char *what, lv_Debug *ar,
                       Closure *f, CallInfo *ci) {
    int status = 1;
    if (f == NULL) {
        info_tailcall(ar);
        return status;
    }
    for (; *what; what++) {
        switch (*what) {
            case 'S': {
                funcinfo(ar, f);
                break;
            }
            case 'l': {
                ar->currentline = (ci) ? currentline(L, ci) : -1;
                break;
            }
            case 'u': {
                ar->nups = f->c.nupvalues;
                break;
            }
            case 'n': {
                ar->namewhat = (ci) ? getfuncname(L, ci, &ar->name) : NULL;
                if (ar->namewhat == NULL) {
                    ar->namewhat = "";  /* not found */
                    ar->name = NULL;
                }
                break;
            }
            case 'L':
            case 'f':  /* handled by lv_getinfo */
                break;
            default: status = 0;  /* invalid option */
        }
    }
    return status;
}


LV_API int lv_getinfo (lv_State *L, const char *what, lv_Debug *ar) {
    int status;
    Closure *f = NULL;
    CallInfo *ci = NULL;
    lv_lock(L);
    if (*what == '>') {
        StkId func = L->top - 1;
        lvi_apicheck(L, ttisfunction(func));
        what++;  /* skip the '>' */
        f = clvalue(func);
        L->top--;  /* pop function */
    }
    else if (ar->i_ci != 0) {  /* no tail call? */
        ci = L->base_ci + ar->i_ci;
        lv_assert(ttisfunction(ci->func));
        f = clvalue(ci->func);
    }
    status = auxgetinfo(L, what, ar, f, ci);
    if (strchr(what, 'f')) {
        if (f == NULL) setnilvalue(L->top);
        else setclvalue(L, L->top, f);
        incr_top(L);
    }
    if (strchr(what, 'L'))
        collectvalidlines(L, f);
    lv_unlock(L);
    return status;
}


/*
 ** {======================================================
 ** Symbolic Execution and code checker
 ** =======================================================
 */

#define check(x)		if (!(x)) return 0;

#define checkjump(pt,pc)	check(0 <= pc && pc < pt->sizecode)

#define checkreg(pt,reg)	check((reg) < (pt)->maxstacksize)



static int precheck (const Proto *pt) {
    check(pt->maxstacksize <= MAXSTACK);
    check(pt->numparams+(pt->is_vararg & VARARG_HASARG) <= pt->maxstacksize);
    check(!(pt->is_vararg & VARARG_NEEDSARG) ||
          (pt->is_vararg & VARARG_HASARG));
    check(pt->sizeupvalues <= pt->nups);
    check(pt->sizelineinfo == pt->sizecode || pt->sizelineinfo == 0);
    check(pt->sizecode > 0 && GET_OPCODE(pt->code[pt->sizecode-1]) == OP_RETURN);
    return 1;
}


#define checkopenop(pt,pc)	lvG_checkopenop((pt)->code[(pc)+1])

int lvG_checkopenop (Instruction i) {
    switch (GET_OPCODE(i)) {
        case OP_CALL:
        case OP_TAILCALL:
        case OP_RETURN:
        case OP_SETLIST: {
            check(GETARG_B(i) == 0);
            return 1;
        }
        default: return 0;  /* invalid instruction after an open call */
    }
}


static int checkArgMode (const Proto *pt, int r, enum OpArgMask mode) {
    switch (mode) {
        case OpArgN: check(r == 0); break;
        case OpArgU: break;
        case OpArgR: checkreg(pt, r); break;
        case OpArgK:
            check(ISK(r) ? INDEXK(r) < pt->sizek : r < pt->maxstacksize);
            break;
    }
    return 1;
}


static Instruction symbexec (const Proto *pt, int lastpc, int reg) {
    int pc;
    int last;  /* stores position of last instruction that changed `reg' */
    last = pt->sizecode-1;  /* points to final return (a `neutral' instruction) */
    check(precheck(pt));
    for (pc = 0; pc < lastpc; pc++) {
        Instruction i = pt->code[pc];
        OpCode op = GET_OPCODE(i);
        int a = GETARG_A(i);
        int b = 0;
        int c = 0;
        check(op < NUM_OPCODES);
        checkreg(pt, a);
        switch (getOpMode(op)) {
            case iABC: {
                b = GETARG_B(i);
                c = GETARG_C(i);
                check(checkArgMode(pt, b, getBMode(op)));
                check(checkArgMode(pt, c, getCMode(op)));
                break;
            }
            case iABx: {
                b = GETARG_Bx(i);
                if (getBMode(op) == OpArgK) check(b < pt->sizek);
                break;
            }
            case iAsBx: {
                b = GETARG_sBx(i);
                if (getBMode(op) == OpArgR) {
                    int dest = pc+1+b;
                    check(0 <= dest && dest < pt->sizecode);
                    if (dest > 0) {
                        int j;
                        /* check that it does not jump to a setlist count; this
                         is tricky, because the count from a previous setlist may
                         have the same value of an invalid setlist; so, we must
                         go all the way back to the first of them (if any) */
                        for (j = 0; j < dest; j++) {
                            Instruction d = pt->code[dest-1-j];
                            if (!(GET_OPCODE(d) == OP_SETLIST && GETARG_C(d) == 0)) break;
                        }
                        /* if 'j' is even, previous value is not a setlist (even if
                         it looks like one) */
                        check((j&1) == 0);
                    }
                }
                break;
            }
        }
        if (testAMode(op)) {
            if (a == reg) last = pc;  /* change register `a' */
        }
        if (testTMode(op)) {
            check(pc+2 < pt->sizecode);  /* check skip */
            check(GET_OPCODE(pt->code[pc+1]) == OP_JMP);
        }
        switch (op) {
            case OP_LOADBOOL: {
                if (c == 1) {  /* does it jump? */
                    check(pc+2 < pt->sizecode);  /* check its jump */
                    check(GET_OPCODE(pt->code[pc+1]) != OP_SETLIST ||
                          GETARG_C(pt->code[pc+1]) != 0);
                }
                break;
            }
            case OP_LOADNIL: {
                if (a <= reg && reg <= b)
                    last = pc;  /* set registers from `a' to `b' */
                break;
            }
            case OP_GETUPVAL:
            case OP_SETUPVAL: {
                check(b < pt->nups);
                break;
            }
            case OP_GETGLOBAL:
            case OP_SETGLOBAL: {
                check(ttisstring(&pt->k[b]));
                break;
            }
            case OP_SELF: {
                checkreg(pt, a+1);
                if (reg == a+1) last = pc;
                break;
            }
            case OP_CONCAT: {
                check(b < c);  /* at least two operands */
                break;
            }
            case OP_TFORLOOP: {
                check(c >= 1);  /* at least one result (control variable) */
                checkreg(pt, a+2+c);  /* space for results */
                if (reg >= a+2) last = pc;  /* affect all regs above its base */
                break;
            }
            case OP_FORLOOP:
            case OP_FORPREP:
                checkreg(pt, a+3);
                /* go through */
            case OP_JMP: {
                int dest = pc+1+b;
                /* not full check and jump is forward and do not skip `lastpc'? */
                if (reg != NO_REG && pc < dest && dest <= lastpc)
                    pc += b;  /* do the jump */
                break;
            }
            case OP_CALL:
            case OP_TAILCALL: {
                if (b != 0) {
                    checkreg(pt, a+b-1);
                }
                c--;  /* c = num. returns */
                if (c == LV_MULTRET) {
                    check(checkopenop(pt, pc));
                }
                else if (c != 0)
                    checkreg(pt, a+c-1);
                if (reg >= a) last = pc;  /* affect all registers above base */
                break;
            }
            case OP_RETURN: {
                b--;  /* b = num. returns */
                if (b > 0) checkreg(pt, a+b-1);
                break;
            }
            case OP_SETLIST: {
                if (b > 0) checkreg(pt, a + b);
                if (c == 0) {
                    pc++;
                    check(pc < pt->sizecode - 1);
                }
                break;
            }
            case OP_CLOSURE: {
                int nup, j;
                check(b < pt->sizep);
                nup = pt->p[b]->nups;
                check(pc + nup < pt->sizecode);
                for (j = 1; j <= nup; j++) {
                    OpCode op1 = GET_OPCODE(pt->code[pc + j]);
                    check(op1 == OP_GETUPVAL || op1 == OP_MOVE);
                }
                if (reg != NO_REG)  /* tracing? */
                    pc += nup;  /* do not 'execute' these pseudo-instructions */
                break;
            }
            case OP_VARARG: {
                check((pt->is_vararg & VARARG_ISVARARG) &&
                      !(pt->is_vararg & VARARG_NEEDSARG));
                b--;
                if (b == LV_MULTRET) check(checkopenop(pt, pc));
                checkreg(pt, a+b-1);
                break;
            }
            default: break;
        }
    }
    return pt->code[last];
}

#undef check
#undef checkjump
#undef checkreg

/* }====================================================== */


int lvG_checkcode (const Proto *pt) {
    return (symbexec(pt, pt->sizecode, NO_REG) != 0);
}


static const char *kname (Proto *p, int c) {
    if (ISK(c) && ttisstring(&p->k[INDEXK(c)]))
        return svalue(&p->k[INDEXK(c)]);
    else
        return "?";
}


static const char *getobjname (lv_State *L, CallInfo *ci, int stackpos,
                               const char **name) {
    if (isL_u_a(ci)) {  /* a [L u a] function? */
        Proto *p = ci_func(ci)->l.p;
        int pc = currentpc(L, ci);
        Instruction i;
        *name = lvF_getlocalname(p, stackpos+1, pc);
        if (*name)  /* is a local? */
            return "local";
        i = symbexec(p, pc, stackpos);  /* try symbolic execution */
        lv_assert(pc != -1);
        switch (GET_OPCODE(i)) {
            case OP_GETGLOBAL: {
                int g = GETARG_Bx(i);  /* global index */
                lv_assert(ttisstring(&p->k[g]));
                *name = svalue(&p->k[g]);
                return "global";
            }
            case OP_MOVE: {
                int a = GETARG_A(i);
                int b = GETARG_B(i);  /* move from `b' to `a' */
                if (b < a)
                    return getobjname(L, ci, b, name);  /* get name for `b' */
                break;
            }
            case OP_GETTABLE: {
                int k = GETARG_C(i);  /* key index */
                *name = kname(p, k);
                return "field";
            }
            case OP_GETUPVAL: {
                int u = GETARG_B(i);  /* upvalue index */
                *name = p->upvalues ? getstr(p->upvalues[u]) : "?";
                return "upvalue";
            }
            case OP_SELF: {
                int k = GETARG_C(i);  /* key index */
                *name = kname(p, k);
                return "method";
            }
            default: break;
        }
    }
    return NULL;  /* no useful name found */
}


static const char *getfuncname (lv_State *L, CallInfo *ci, const char **name) {
    Instruction i;
    if ((isL_u_a(ci) && ci->tailcalls > 0) || !isL_u_a(ci - 1))
        return NULL;  /* calling function is not [L u a] (or is unknown) */
    ci--;  /* calling function */
    i = ci_func(ci)->l.p->code[currentpc(L, ci)];
    if (GET_OPCODE(i) == OP_CALL || GET_OPCODE(i) == OP_TAILCALL ||
        GET_OPCODE(i) == OP_TFORLOOP)
        return getobjname(L, ci, GETARG_A(i), name);
    else
        return NULL;  /* no useful name can be found */
}


/* only ANSI way to check whether a pointer points to an array */
static int isinstack (CallInfo *ci, const TValue *o) {
    StkId p;
    for (p = ci->base; p < ci->top; p++)
        if (o == p) return 1;
    return 0;
}


void lvG_typeerror (lv_State *L, const TValue *o, const char *op) {
    const char *name = NULL;
    const char *t = lvT_typenames[ttype(o)];
    const char *kind = (isinstack(L->ci, o)) ?
    getobjname(L, L->ci, cast_int(o - L->base), &name) :
    NULL;
    if (kind)
        lvG_runerror(L, "attempt to %s %s " LV_QS " (a %s value)",
                     op, kind, name, t);
    else
        lvG_runerror(L, "attempt to %s a %s value", op, t);
}


void lvG_concaterror (lv_State *L, StkId p1, StkId p2) {
    if (ttisstring(p1) || ttisnumber(p1)) p1 = p2;
    lv_assert(!ttisstring(p1) && !ttisnumber(p1));
    lvG_typeerror(L, p1, "concatenate");
}


void lvG_aritherror (lv_State *L, const TValue *p1, const TValue *p2) {
    TValue temp;
    if (lvV_tonumber(p1, &temp) == NULL)
        p2 = p1;  /* first operand is wrong */
    lvG_typeerror(L, p2, "perform arithmetic on");
}


int lvG_ordererror (lv_State *L, const TValue *p1, const TValue *p2) {
    const char *t1 = lvT_typenames[ttype(p1)];
    const char *t2 = lvT_typenames[ttype(p2)];
    if (t1[2] == t2[2])
        lvG_runerror(L, "attempt to compare two %s values", t1);
    else
        lvG_runerror(L, "attempt to compare %s with %s", t1, t2);
    return 0;
}


static void addinfo (lv_State *L, const char *msg) {
    CallInfo *ci = L->ci;
    if (isL_u_a(ci)) {  /* is [L u a] code? */
        char buff[LV_IDSIZE];  /* add file:line information */
        int line = currentline(L, ci);
        lvO_chunkid(buff, getstr(get_l_u_a_proto(ci)->source), LV_IDSIZE);
        lvO_pushfstring(L, "<%s:%d> %s", buff, line, msg);
    }
}


void lvG_errormsg (lv_State *L) {
    if (L->errfunc != 0) {  /* is there an error handling function? */
        StkId errfunc = restorestack(L, L->errfunc);
        if (!ttisfunction(errfunc)) lvD_throw(L, LV_ERRERR);
        setobjs2s(L, L->top, L->top - 1);  /* move argument */
        setobjs2s(L, L->top - 1, errfunc);  /* push function */
        incr_top(L);
        lvD_call(L, L->top - 2, 1);  /* call it */
    }
    lvD_throw(L, LV_ERRRUN);
}


void lvG_runerror (lv_State *L, const char *fmt, ...) {
    va_list argp;
    va_start(argp, fmt);
    addinfo(L, lvO_pushvfstring(L, fmt, argp));
    va_end(argp);
    lvG_errormsg(L);
}

