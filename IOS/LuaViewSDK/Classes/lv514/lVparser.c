/*
 ** $Id: lVparser.c,v 2.42.1.3 2007/12/28 15:32:23 roberto Exp $
 ** [L u a] Parser
 ** See Copyright Notice in lV.h
 */


#include <string.h>

#define lparser_c
#define LV_CORE

#include "lV.h"

#include "lVcode.h"
#include "lVdebug.h"
#include "lVdo.h"
#include "lVfunc.h"
#include "lVlex.h"
#include "lVmem.h"
#include "lVobject.h"
#include "lVopcodes.h"
#include "lVparser.h"
#include "lVstate.h"
#include "lVstring.h"
#include "lVtable.h"



#define hasmultret(k)		((k) == VCALL || (k) == VVARARG)

#define getlocvar(fs, i)	((fs)->f->locvars[(fs)->actvar[i]])

#define lvY_checklimit(fs,v,l,m)	if ((v)>(l)) errorlimit(fs,l,m)


/*
 ** nodes for block list (list of active blocks)
 */
typedef struct BlockCnt {
    struct BlockCnt *previous;  /* chain */
    int breaklist;  /* list of jumps out of this loop */
    lu_byte nactvar;  /* # active locals outside the breakable structure */
    lu_byte upval;  /* true if some variable in the block is an upvalue */
    lu_byte isbreakable;  /* true if `block' is a loop */
} BlockCnt;



/*
 ** prototypes for recursive non-terminal functions
 */
static void chunk (LexState *ls);
static void chunk_oneline (LexState *ls);
static void expr (LexState *ls, expdesc *v);


static void anchor_token (LexState *ls) {
    if (ls->t.token == TK_NAME || ls->t.token == TK_STRING) {
        TString *ts = ls->t.seminfo.ts;
        lvX_newstring(ls, getstr(ts), ts->tsv.len);
    }
}


static void error_expected (LexState *ls, int token) {
    lvX_syntaxerror(ls,
                    lvO_pushfstring(ls->L, LV_QS " expected", lvX_token2str(ls, token)));
}


static void errorlimit (FuncState *fs, int limit, const char *what) {
    const char *msg = (fs->f->linedefined == 0) ?
    lvO_pushfstring(fs->L, "main function has more than %d %s", limit, what) :
    lvO_pushfstring(fs->L, "function at line %d has more than %d %s",
                    fs->f->linedefined, limit, what);
    lvX_lexerror(fs->ls, msg, 0);
}


static int testnext (LexState *ls, int c) {
    if (ls->t.token == c) {
        lvX_next(ls);
        return 1;
    }
    else return 0;
}


static void check (LexState *ls, int c) {
    if (ls->t.token != c)
        error_expected(ls, c);
}

static void checknext (LexState *ls, int c) {
    check(ls, c);
    lvX_next(ls);
}


#define check_condition(ls,c,msg)	{ if (!(c)) lvX_syntaxerror(ls, msg); }



static void check_match (LexState *ls, int what, int who, int where) {
    if (!testnext(ls, what)) {
        if (where == ls->linenumber)
            error_expected(ls, what);
        else {
            lvX_syntaxerror(ls, lvO_pushfstring(ls->L,
                                                LV_QS " expected (to close " LV_QS " at line %d)",
                                                lvX_token2str(ls, what), lvX_token2str(ls, who), where));
        }
    }
}


static TString *str_checkname (LexState *ls) {
    TString *ts;
    check(ls, TK_NAME);
    ts = ls->t.seminfo.ts;
    lvX_next(ls);
    return ts;
}

static TString *str_checkname2 (LexState *ls) {
    TString *ts;
    check(ls, TK_STRING);
    ts = ls->t.seminfo.ts;
    lvX_next(ls);
    return ts;
}


static void init_exp (expdesc *e, expkind k, int i) {
    e->f = e->t = NO_JUMP;
    e->k = k;
    e->u.s.info = i;
}


static void codestring (LexState *ls, expdesc *e, TString *s) {
    init_exp(e, VK, lvK_stringK(ls->fs, s));
}


static void checkname(LexState *ls, expdesc *e) {
    codestring(ls, e, str_checkname(ls));
}

static void checkname2(LexState *ls, expdesc *e) {
    codestring(ls, e, str_checkname2(ls));
}


static int registerlocalvar (LexState *ls, TString *varname) {
    FuncState *fs = ls->fs;
    Proto *f = fs->f;
    int oldsize = f->sizelocvars;
    lvM_growvector(ls->L, f->locvars, fs->nlocvars, f->sizelocvars,
                   LocVar, SHRT_MAX, "too many local variables");
    while (oldsize < f->sizelocvars) f->locvars[oldsize++].varname = NULL;
    f->locvars[fs->nlocvars].varname = varname;
    lvC_objbarrier(ls->L, f, varname);
    return fs->nlocvars++;
}


#define new_localvarliteral(ls,v,n) \
new_localvar(ls, lvX_newstring(ls, "" v, (sizeof(v)/sizeof(char))-1), n)


static void new_localvar (LexState *ls, TString *name, int n) {
    FuncState *fs = ls->fs;
    lvY_checklimit(fs, fs->nactvar+n+1, LVI_MAXVARS, "local variables");
    fs->actvar[fs->nactvar+n] = cast(unsigned short, registerlocalvar(ls, name));
}


static void adjustlocalvars (LexState *ls, int nvars) {
    FuncState *fs = ls->fs;
    fs->nactvar = cast_byte(fs->nactvar + nvars);
    for (; nvars; nvars--) {
        getlocvar(fs, fs->nactvar - nvars).startpc = fs->pc;
    }
}


static void removevars (LexState *ls, int tolevel) {
    FuncState *fs = ls->fs;
    while (fs->nactvar > tolevel)
        getlocvar(fs, --fs->nactvar).endpc = fs->pc;
}


static int indexupvalue (FuncState *fs, TString *name, expdesc *v) {
    int i;
    Proto *f = fs->f;
    int oldsize = f->sizeupvalues;
    for (i=0; i<f->nups; i++) {
        if (fs->upvalues[i].k == v->k && fs->upvalues[i].info == v->u.s.info) {
            lv_assert(f->upvalues[i] == name);
            return i;
        }
    }
    /* new one */
    lvY_checklimit(fs, f->nups + 1, LVI_MAXUPVALUES, "upvalues");
    lvM_growvector(fs->L, f->upvalues, f->nups, f->sizeupvalues,
                   TString *, MAX_INT, "");
    while (oldsize < f->sizeupvalues) f->upvalues[oldsize++] = NULL;
    f->upvalues[f->nups] = name;
    lvC_objbarrier(fs->L, f, name);
    lv_assert(v->k == VLOCAL || v->k == VUPVAL);
    fs->upvalues[f->nups].k = cast_byte(v->k);
    fs->upvalues[f->nups].info = cast_byte(v->u.s.info);
    return f->nups++;
}


static int searchvar (FuncState *fs, TString *n) {
    int i;
    for (i=fs->nactvar-1; i >= 0; i--) {
        if (n == getlocvar(fs, i).varname)
            return i;
    }
    return -1;  /* not found */
}


static void markupval (FuncState *fs, int level) {
    BlockCnt *bl = fs->bl;
    while (bl && bl->nactvar > level) bl = bl->previous;
    if (bl) bl->upval = 1;
}


static int singlevaraux (FuncState *fs, TString *n, expdesc *var, int base) {
    if (fs == NULL) {  /* no more levels? */
        init_exp(var, VGLOBAL, NO_REG);  /* default is global variable */
        return VGLOBAL;
    }
    else {
        int v = searchvar(fs, n);  /* look up at current level */
        if (v >= 0) {
            init_exp(var, VLOCAL, v);
            if (!base)
                markupval(fs, v);  /* local will be used as an upval */
            return VLOCAL;
        }
        else {  /* not found at current level; try upper one */
            if (singlevaraux(fs->prev, n, var, 0) == VGLOBAL)
                return VGLOBAL;
            var->u.s.info = indexupvalue(fs, n, var);  /* else was LOCAL or UPVAL */
            var->k = VUPVAL;  /* upvalue in this level */
            return VUPVAL;
        }
    }
}


static void singlevar (LexState *ls, expdesc *var) {
    TString *varname = str_checkname(ls);
    FuncState *fs = ls->fs;
    if (singlevaraux(fs, varname, var, 1) == VGLOBAL)
        var->u.s.info = lvK_stringK(fs, varname);  /* info points to global name */
}


static void adjust_assign (LexState *ls, int nvars, int nexps, expdesc *e) {
    FuncState *fs = ls->fs;
    int extra = nvars - nexps;
    if (hasmultret(e->k)) {
        extra++;  /* includes call itself */
        if (extra < 0) extra = 0;
        lvK_setreturns(fs, e, extra);  /* last exp. provides the difference */
        if (extra > 1) lvK_reserveregs(fs, extra-1);
    }
    else {
        if (e->k != VVOID) lvK_exp2nextreg(fs, e);  /* close last expression */
        if (extra > 0) {
            int reg = fs->freereg;
            lvK_reserveregs(fs, extra);
            lvK_nil(fs, reg, extra);
        }
    }
}


static void enterlevel (LexState *ls) {
    if (++ls->L->nCcalls > LVI_MAXCCALLS)
        lvX_lexerror(ls, "chunk has too many syntax levels", 0);
}


#define leavelevel(ls)	((ls)->L->nCcalls--)


static void enterblock (FuncState *fs, BlockCnt *bl, lu_byte isbreakable) {
    bl->breaklist = NO_JUMP;
    bl->isbreakable = isbreakable;
    bl->nactvar = fs->nactvar;
    bl->upval = 0;
    bl->previous = fs->bl;
    fs->bl = bl;
    lv_assert(fs->freereg == fs->nactvar);
}


static void leaveblock (FuncState *fs) {
    BlockCnt *bl = fs->bl;
    fs->bl = bl->previous;
    removevars(fs->ls, bl->nactvar);
    if (bl->upval)
        lvK_codeABC(fs, OP_CLOSE, bl->nactvar, 0, 0);
    /* a block either controls scope or breaks (never both) */
    lv_assert(!bl->isbreakable || !bl->upval);
    lv_assert(bl->nactvar == fs->nactvar);
    fs->freereg = fs->nactvar;  /* free registers */
    lvK_patchtohere(fs, bl->breaklist);
}


static void pushclosure (LexState *ls, FuncState *func, expdesc *v) {
    FuncState *fs = ls->fs;
    Proto *f = fs->f;
    int oldsize = f->sizep;
    int i;
    lvM_growvector(ls->L, f->p, fs->np, f->sizep, Proto *,
                   MAXARG_Bx, "constant table overflow");
    while (oldsize < f->sizep) f->p[oldsize++] = NULL;
    f->p[fs->np++] = func->f;
    lvC_objbarrier(ls->L, f, func->f);
    init_exp(v, VRELOCABLE, lvK_codeABx(fs, OP_CLOSURE, 0, fs->np-1));
    for (i=0; i<func->f->nups; i++) {
        OpCode o = (func->upvalues[i].k == VLOCAL) ? OP_MOVE : OP_GETUPVAL;
        lvK_codeABC(fs, o, 0, func->upvalues[i].info, 0);
    }
}


static void open_func (LexState *ls, FuncState *fs) {
    lv_State *L = ls->L;
    Proto *f = lvF_newproto(L);
    fs->f = f;
    fs->prev = ls->fs;  /* linked list of funcstates */
    fs->ls = ls;
    fs->L = L;
    ls->fs = fs;
    fs->pc = 0;
    fs->lasttarget = -1;
    fs->jpc = NO_JUMP;
    fs->freereg = 0;
    fs->nk = 0;
    fs->np = 0;
    fs->nlocvars = 0;
    fs->nactvar = 0;
    fs->bl = NULL;
    f->source = ls->source;
    f->maxstacksize = 2;  /* registers 0/1 are always valid */
    fs->h = lvH_new(L, 0, 0);
    /* anchor table of constants and prototype (to avoid being collected) */
    sethvalue2s(L, L->top, fs->h);
    incr_top(L);
    setptvalue2s(L, L->top, f);
    incr_top(L);
}


static void close_func (LexState *ls) {
    lv_State *L = ls->L;
    FuncState *fs = ls->fs;
    Proto *f = fs->f;
    removevars(ls, 0);
    lvK_ret(fs, 0, 0);  /* final return */
    lvM_reallocvector(L, f->code, f->sizecode, fs->pc, Instruction);
    f->sizecode = fs->pc;
    lvM_reallocvector(L, f->lineinfo, f->sizelineinfo, fs->pc, int);
    f->sizelineinfo = fs->pc;
    lvM_reallocvector(L, f->k, f->sizek, fs->nk, TValue);
    f->sizek = fs->nk;
    lvM_reallocvector(L, f->p, f->sizep, fs->np, Proto *);
    f->sizep = fs->np;
    lvM_reallocvector(L, f->locvars, f->sizelocvars, fs->nlocvars, LocVar);
    f->sizelocvars = fs->nlocvars;
    lvM_reallocvector(L, f->upvalues, f->sizeupvalues, f->nups, TString *);
    f->sizeupvalues = f->nups;
    lv_assert(lvG_checkcode(f));
    lv_assert(fs->bl == NULL);
    ls->fs = fs->prev;
    L->top -= 2;  /* remove table and prototype from the stack */
    /* last token read was anchored in defunct function; must reanchor it */
    if (fs) anchor_token(ls);
}


Proto *lvY_parser (lv_State *L, ZIO *z, Mbuffer *buff, const char *name) {
    struct LexState lexstate;
    struct FuncState funcstate;
    lexstate.buff = buff;
    lvX_setinput(L, &lexstate, z, lvS_new(L, name));
    open_func(&lexstate, &funcstate);
    funcstate.f->is_vararg = VARARG_ISVARARG;  /* main func. is always vararg */
    lvX_next(&lexstate);  /* read first token */
    chunk(&lexstate);
    check(&lexstate, TK_EOS);
    close_func(&lexstate);
    lv_assert(funcstate.prev == NULL);
    lv_assert(funcstate.f->nups == 0);
    lv_assert(lexstate.fs == NULL);
    return funcstate.f;
}



/*============================================================*/
/* GRAMMAR RULES */
/*============================================================*/


static void field (LexState *ls, expdesc *v) {
    /* field -> ['.' | ':'] NAME */
    FuncState *fs = ls->fs;
    expdesc key;
    lvK_exp2anyreg(fs, v);
    lvX_next(ls);  /* skip the dot or colon */
    checkname(ls, &key);
    lvK_indexed(fs, v, &key);
}


static void yindex (LexState *ls, expdesc *v) {
    /* index -> '[' expr ']' */
    lvX_next(ls);  /* skip the '[' */
    expr(ls, v);
    lvK_exp2val(ls->fs, v);
    checknext(ls, ']');
}


/*
 ** {======================================================================
 ** Rules for Constructors
 ** =======================================================================
 */


struct ConsControl {
    expdesc v;  /* last list item read */
    expdesc *t;  /* table descriptor */
    int nh;  /* total number of `record' elements */
    int na;  /* total number of array elements */
    int tostore;  /* number of array elements pending to be stored */
};


static void recfield (LexState *ls, struct ConsControl *cc) {
    /* recfield -> (NAME | `['exp1`]') = exp1 */
    FuncState *fs = ls->fs;
    int reg = ls->fs->freereg;
    expdesc key, val;
    int rkkey;
    
    if( ls->t.token == TK_STRING ){
        lvY_checklimit(fs, cc->nh, MAX_INT, "items in a constructor");
        checkname2(ls, &key);
    } else
        if (ls->t.token == TK_NAME) {
            lvY_checklimit(fs, cc->nh, MAX_INT, "items in a constructor");
            checkname(ls, &key);
        }
        else  /* ls->t.token == '[' */
            yindex(ls, &key);
    cc->nh++;
    checknext(ls, '=');
    rkkey = lvK_exp2RK(fs, &key);
    expr(ls, &val);
    lvK_codeABC(fs, OP_SETTABLE, cc->t->u.s.info, rkkey, lvK_exp2RK(fs, &val));
    fs->freereg = reg;  /* free registers */
}


static void closelistfield (FuncState *fs, struct ConsControl *cc) {
    if (cc->v.k == VVOID) return;  /* there is no list item */
    lvK_exp2nextreg(fs, &cc->v);
    cc->v.k = VVOID;
    if (cc->tostore == LFIELDS_PER_FLUSH) {
        lvK_setlist(fs, cc->t->u.s.info, cc->na, cc->tostore);  /* flush */
        cc->tostore = 0;  /* no more items pending */
    }
}


static void lastlistfield (FuncState *fs, struct ConsControl *cc) {
    if (cc->tostore == 0) return;
    if (hasmultret(cc->v.k)) {
        lvK_setmultret(fs, &cc->v);
        lvK_setlist(fs, cc->t->u.s.info, cc->na, LV_MULTRET);
        cc->na--;  /* do not count last expression (unknown number of elements) */
    }
    else {
        if (cc->v.k != VVOID)
            lvK_exp2nextreg(fs, &cc->v);
        lvK_setlist(fs, cc->t->u.s.info, cc->na, cc->tostore);
    }
}


static void listfield (LexState *ls, struct ConsControl *cc) {
    expr(ls, &cc->v);
    lvY_checklimit(ls->fs, cc->na, MAX_INT, "items in a constructor");
    cc->na++;
    cc->tostore++;
}


static void constructor (LexState *ls, expdesc *t) {
    int special = 0;//'['
    /* constructor -> ?? */
    FuncState *fs = ls->fs;
    int line = ls->linenumber;
    int pc = lvK_codeABC(fs, OP_NEWTABLE, 0, 0, 0);
    struct ConsControl cc;
    cc.na = cc.nh = cc.tostore = 0;
    cc.t = t;
    init_exp(t, VRELOCABLE, pc);
    init_exp(&cc.v, VVOID, 0);  /* no value (yet) */
    lvK_exp2nextreg(ls->fs, t);  /* fix it at stack top (for gc) */
    if( ls->t.token=='[' ) {
        special = 1;
        checknext(ls, '[');
    } else {
        checknext(ls, '{');
    }
    do {
        lv_assert(cc.v.k == VVOID || cc.tostore > 0);
        if( special ){
            if (ls->t.token == ']') break;
        }
        if (ls->t.token == '}') break;
        closelistfield(fs, &cc);
        switch(ls->t.token) {
            case TK_STRING: {
                lvX_lookahead(ls);
                if (ls->lookahead.token == '=' || ls->lookahead.token == ':') {
                    ls->lookahead.token = '=';
                    recfield(ls, &cc);
                } else {
                    listfield(ls, &cc);
                }
                break;
            }
            case TK_NAME: {  /* may be listfields or recfields */
                lvX_lookahead(ls);
                if (ls->lookahead.token == '='  || ls->lookahead.token == ':') {  /* expression? dongxicheng */
                    ls->lookahead.token = '=';
                    recfield(ls, &cc);
                } else {
                    listfield(ls, &cc);
                }
                break;
            }
            case '[': {  /* constructor_item -> recfield */
                recfield(ls, &cc);
                break;
            }
            default: {  /* constructor_part -> listfield */
                listfield(ls, &cc);
                break;
            }
        }
    } while (testnext(ls, ',') || testnext(ls, ';'));
    if( special ){
        check_match(ls, ']', '[', line);
    } else {
        check_match(ls, '}', '{', line);
    }
    lastlistfield(fs, &cc);
    SETARG_B(fs->f->code[pc], lvO_int2fb(cc.na)); /* set initial array size */
    SETARG_C(fs->f->code[pc], lvO_int2fb(cc.nh));  /* set initial table size */
}

/* }====================================================================== */



static void parlist (LexState *ls) {
    /* parlist -> [ param { `,' param } ] */
    FuncState *fs = ls->fs;
    Proto *f = fs->f;
    int nparams = 0;
    f->is_vararg = 0;
    if (ls->t.token != ')') {  /* is `parlist' not empty? */
        do {
            switch (ls->t.token) {
                case TK_NAME: {  /* param -> NAME */
                    new_localvar(ls, str_checkname(ls), nparams++);
                    break;
                }
                case TK_DOTS: {  /* param -> `...' */
                    lvX_next(ls);
#if defined(LV_COMPAT_VARARG)
                    /* use `arg' as default name */
                    new_localvarliteral(ls, "arg", nparams++);
                    f->is_vararg = VARARG_HASARG | VARARG_NEEDSARG;
#endif
                    f->is_vararg |= VARARG_ISVARARG;
                    break;
                }
                default: lvX_syntaxerror(ls, "<name> or " LV_QL("...") " expected");
            }
        } while (!f->is_vararg && testnext(ls, ','));
    }
    adjustlocalvars(ls, nparams);
    f->numparams = cast_byte(fs->nactvar - (f->is_vararg & VARARG_HASARG));
    lvK_reserveregs(fs, fs->nactvar);  /* reserve register for parameters */
}


static void body (LexState *ls, expdesc *e, int needself, int line) {
    /* body ->  `(' parlist `)' chunk END */
    FuncState new_fs;
    open_func(ls, &new_fs);
    new_fs.f->linedefined = line;
    checknext(ls, '(');
    if (needself ) {
        new_localvarliteral(ls, "self", 0);
        adjustlocalvars(ls, 1);
    }
    parlist(ls);
    checknext(ls, ')');
    int javaFuntion = 0;
    if( ls->t.token=='{' ){
        javaFuntion = 1;
        lvX_next(ls);
    }
    chunk(ls);
    new_fs.f->lastlinedefined = ls->linenumber;
    if( javaFuntion ) {
        check_match(ls, '}',    TK_FUNCTION, line);
    } else {
        check_match(ls, TK_END, TK_FUNCTION, line);
    }
    close_func(ls);
    pushclosure(ls, &new_fs, e);
}


static int explist1 (LexState *ls, expdesc *v) {
    /* explist1 -> expr { `,' expr } */
    int n = 1;  /* at least one expression */
    expr(ls, v);
    while (testnext(ls, ',')) {
        lvK_exp2nextreg(ls->fs, v);
        expr(ls, v);
        n++;
    }
    return n;
}


static void funcargs (LexState *ls, expdesc *f) {
    FuncState *fs = ls->fs;
    expdesc args;
    int base, nparams;
    int line = ls->linenumber;
    switch (ls->t.token) {
        case '(': {  /* funcargs -> `(' [ explist1 ] `)' */
            if (line != ls->lastline)
                lvX_syntaxerror(ls,"ambiguous syntax (function call x new statement)");
            lvX_next(ls);
            if (ls->t.token == ')')  /* arg list is empty? */
                args.k = VVOID;
            else {
                explist1(ls, &args);
                lvK_setmultret(fs, &args);
            }
            check_match(ls, ')', '(', line);
            break;
        }
        case '{': {  /* funcargs -> constructor */
            constructor(ls, &args);
            break;
        }
        case TK_STRING: {  /* funcargs -> STRING */
            codestring(ls, &args, ls->t.seminfo.ts);
            lvX_next(ls);  /* must use `seminfo' before `next' */
            break;
        }
        default: {
            lvX_syntaxerror(ls, "function arguments expected");
            return;
        }
    }
    lv_assert(f->k == VNONRELOC);
    base = f->u.s.info;  /* base register for call */
    if (hasmultret(args.k))
        nparams = LV_MULTRET;  /* open call */
    else {
        if (args.k != VVOID)
            lvK_exp2nextreg(fs, &args);  /* close last argument */
        nparams = fs->freereg - (base+1);
    }
    init_exp(f, VCALL, lvK_codeABC(fs, OP_CALL, base, nparams+1, 2));
    lvK_fixline(fs, line);
    fs->freereg = base+1;  /* call remove function and arguments and leaves
                            (unless changed) one result */
}




/*
 ** {======================================================================
 ** Expression parsing
 ** =======================================================================
 */


static void prefixexp (LexState *ls, expdesc *v) {
    /* prefixexp -> NAME | '(' expr ')' */
    switch (ls->t.token) {
        case '(': {
            int line = ls->linenumber;
            lvX_next(ls);
            expr(ls, v);
            check_match(ls, ')', '(', line);
            lvK_dischargevars(ls->fs, v);
            return;
        }
        case TK_NAME: {
            singlevar(ls, v);
            return;
        }
        default: {
            lvX_syntaxerror(ls, "unexpected symbol");
            return;
        }
    }
}


static void primaryexp (LexState *ls, expdesc *v) {
    /* primaryexp ->
     prefixexp { `.' NAME | `[' exp `]' | `:' NAME funcargs | funcargs } */
    FuncState *fs = ls->fs;
    prefixexp(ls, v);
    for (;;) {
        switch (ls->t.token) {
            case '.': {  /* field */
                lvX_lookahead(ls);
                if( ls->t.token =='.' && ls->lookahead.token==TK_NAME
                   && (ls->lookahead2.token=='('||ls->lookahead2.token==TK_STRING||ls->lookahead2.token=='{') ){// dongxicheng
                    ls->t.token = ':';
                    expdesc key;
                    lvX_next(ls);
                    checkname(ls, &key);
                    lvK_self(fs, v, &key);
                    funcargs(ls, v);
                } else {
                    field(ls, v);
                }
                break;
            }
            case '[': {  /* `[' exp1 `]' */
                expdesc key;
                lvK_exp2anyreg(fs, v);
                yindex(ls, &key);
                lvK_indexed(fs, v, &key);
                break;
            }
            case ':': {  /* `:' NAME funcargs */
                field(ls, v);
                //        expdesc key;
                //        lvX_next(ls);
                //        checkname(ls, &key);
                //        lvK_self(fs, v, &key);
                //        funcargs(ls, v);
                break;
            }
            case '(': case TK_STRING: case '{': {  /* funcargs */
                lvK_exp2nextreg(fs, v);
                funcargs(ls, v);
                break;
            }
            default: return;
        }
    }
}


static void simpleexp (LexState *ls, expdesc *v) {
    /* simpleexp -> NUMBER | STRING | NIL | true | false | ... |
     constructor | FUNCTION body | primaryexp */
    switch (ls->t.token) {
        case TK_NUMBER: {
            init_exp(v, VKNUM, 0);
            v->u.nval = ls->t.seminfo.r;
            break;
        }
        case TK_STRING: {
            codestring(ls, v, ls->t.seminfo.ts);
            break;
        }
        case TK_NIL: {
            init_exp(v, VNIL, 0);
            break;
        }
        case TK_TRUE: {
            init_exp(v, VTRUE, 0);
            break;
        }
        case TK_FALSE: {
            init_exp(v, VFALSE, 0);
            break;
        }
        case TK_DOTS: {  /* vararg */
            FuncState *fs = ls->fs;
            check_condition(ls, fs->f->is_vararg,
                            "cannot use " LV_QL("...") " outside a vararg function");
            fs->f->is_vararg &= ~VARARG_NEEDSARG;  /* don't need 'arg' */
            init_exp(v, VVARARG, lvK_codeABC(fs, OP_VARARG, 0, 1, 0));
            break;
        }
        case '[':
        case '{': {  /* constructor */
            constructor(ls, v);
            return;
        }
        case '^':{// dongxicheng
            lvX_lookahead(ls);
            if( ls->lookahead.token=='(' ){
                lvX_next(ls);
                body(ls, v, 0, ls->linenumber);
                return;
            } else {
                primaryexp(ls, v);
                return;
            }
        }
        case TK_FUNCTION: {
            lvX_next(ls);
            body(ls, v, 0, ls->linenumber);
            return;
        }
        default: {
            primaryexp(ls, v);
            return;
        }
    }
    lvX_next(ls);
}


static UnOpr getunopr (int op) {
    switch (op) {
        case TK_NOT: return OPR_NOT;
        case '-': return OPR_MINUS;
        case '#': return OPR_LEN;
        default: return OPR_NOUNOPR;
    }
}


static BinOpr getbinopr (int op) {
    switch (op) {
        case '+': return OPR_ADD;
        case '-': return OPR_SUB;
        case '*': return OPR_MUL;
        case '/': return OPR_DIV;
        case '%': return OPR_MOD;
        case '^': return OPR_POW;
        case TK_CONCAT: return OPR_CONCAT;
        case TK_NE: return OPR_NE;
        case TK_EQ: return OPR_EQ;
        case '<': return OPR_LT;
        case TK_LE: return OPR_LE;
        case '>': return OPR_GT;
        case TK_GE: return OPR_GE;
        case TK_AND: return OPR_AND;
        case TK_OR: return OPR_OR;
        default: return OPR_NOBINOPR;
    }
}


static const struct {
    lu_byte left;  /* left priority for each binary operator */
    lu_byte right; /* right priority */
} priority[] = {  /* ORDER OPR */
    {6, 6}, {6, 6}, {7, 7}, {7, 7}, {7, 7},  /* `+' `-' `/' `%' */
    {10, 9}, {5, 4},                 /* power and concat (right associative) */
    {3, 3}, {3, 3},                  /* equality and inequality */
    {3, 3}, {3, 3}, {3, 3}, {3, 3},  /* order */
    {2, 2}, {1, 1}                   /* logical (and/or) */
};

#define UNARY_PRIORITY	8  /* priority for unary operators */


/*
 ** subexpr -> (simpleexp | unop subexpr) { binop subexpr }
 ** where `binop' is any binary operator with a priority higher than `limit'
 */
static BinOpr subexpr (LexState *ls, expdesc *v, unsigned int limit) {
    BinOpr op;
    UnOpr uop;
    enterlevel(ls);
    uop = getunopr(ls->t.token);
    if (uop != OPR_NOUNOPR) {
        lvX_next(ls);
        subexpr(ls, v, UNARY_PRIORITY);
        lvK_prefix(ls->fs, uop, v);
    }
    else simpleexp(ls, v);
    /* expand while operators have priorities higher than `limit' */
    op = getbinopr(ls->t.token);
    while (op != OPR_NOBINOPR && priority[op].left > limit) {
        expdesc v2;
        BinOpr nextop;
        lvX_next(ls);
        lvK_infix(ls->fs, op, v);
        /* read sub-expression with higher priority */
        nextop = subexpr(ls, &v2, priority[op].right);
        lvK_posfix(ls->fs, op, v, &v2);
        op = nextop;
    }
    leavelevel(ls);
    return op;  /* return first untreated operator */
}


static void expr (LexState *ls, expdesc *v) {
    subexpr(ls, v, 0);
}

/* }==================================================================== */



/*
 ** {======================================================================
 ** Rules for Statements
 ** =======================================================================
 */


static int block_follow (int token) {
    switch (token) {
        case TK_ELSE: case TK_ELSEIF: case TK_END:
        case TK_UNTIL: case TK_EOS: case '}'://dongxicheng 这里修改可能有误 警告!!!!
            return 1;
        default: return 0;
    }
}


static void block (LexState *ls) {
    /* block -> chunk */
    FuncState *fs = ls->fs;
    BlockCnt bl;
    enterblock(fs, &bl, 0);
    chunk(ls);
    lv_assert(bl.breaklist == NO_JUMP);
    leaveblock(fs);
}

static void block_oneline (LexState *ls) {
    /* block -> chunk */
    FuncState *fs = ls->fs;
    BlockCnt bl;
    enterblock(fs, &bl, 0);
    chunk_oneline(ls);
    lv_assert(bl.breaklist == NO_JUMP);
    leaveblock(fs);
}


/*
 ** structure to chain all variables in the left-hand side of an
 ** assignment
 */
struct LHS_assign {
    struct LHS_assign *prev;
    expdesc v;  /* variable (global, local, upvalue, or indexed) */
};


/*
 ** check whether, in an assignment to a local variable, the local variable
 ** is needed in a previous assignment (to a table). If so, save original
 ** local value in a safe place and use this safe copy in the previous
 ** assignment.
 */
static void check_conflict (LexState *ls, struct LHS_assign *lh, expdesc *v) {
    FuncState *fs = ls->fs;
    int extra = fs->freereg;  /* eventual position to save local variable */
    int conflict = 0;
    for (; lh; lh = lh->prev) {
        if (lh->v.k == VINDEXED) {
            if (lh->v.u.s.info == v->u.s.info) {  /* conflict? */
                conflict = 1;
                lh->v.u.s.info = extra;  /* previous assignment will use safe copy */
            }
            if (lh->v.u.s.aux == v->u.s.info) {  /* conflict? */
                conflict = 1;
                lh->v.u.s.aux = extra;  /* previous assignment will use safe copy */
            }
        }
    }
    if (conflict) {
        lvK_codeABC(fs, OP_MOVE, fs->freereg, v->u.s.info, 0);  /* make copy */
        lvK_reserveregs(fs, 1);
    }
}


static void assignment (LexState *ls, struct LHS_assign *lh, int nvars) {
    expdesc e;
    check_condition(ls, VLOCAL <= lh->v.k && lh->v.k <= VINDEXED,
                    "syntax error");
    if (testnext(ls, ',')) {  /* assignment -> `,' primaryexp assignment */
        struct LHS_assign nv;
        nv.prev = lh;
        primaryexp(ls, &nv.v);
        if (nv.v.k == VLOCAL)
            check_conflict(ls, lh, &nv.v);
        lvY_checklimit(ls->fs, nvars, LVI_MAXCCALLS - ls->L->nCcalls,
                       "variables in assignment");
        assignment(ls, &nv, nvars+1);
    }
    else {  /* assignment -> `=' explist1 */
        int nexps;
        checknext(ls, '=');
        nexps = explist1(ls, &e);
        if (nexps != nvars) {
            adjust_assign(ls, nvars, nexps, &e);
            if (nexps > nvars)
                ls->fs->freereg -= nexps - nvars;  /* remove extra values */
        }
        else {
            lvK_setoneret(ls->fs, &e);  /* close last expression */
            lvK_storevar(ls->fs, &lh->v, &e);
            return;  /* avoid default */
        }
    }
    init_exp(&e, VNONRELOC, ls->fs->freereg-1);  /* default assignment */
    lvK_storevar(ls->fs, &lh->v, &e);
}


static int cond (LexState *ls, int* javaMode) {
    /* cond -> exp */
    expdesc v;
    if( testnext(ls, '(') ){// dongxicheng 条件表达式一定要 '(' 开头 ')' 结束
        *javaMode = 1;
    }
    expr(ls, &v);  /* read condition */
    if (v.k == VNIL) v.k = VFALSE;  /* `falses' are all equal here */
    lvK_goiftrue(ls->fs, &v);
    if( *javaMode ) {
        checknext(ls, ')');// dongxicheng
    }
    return v.f;
}


static void breakstat (LexState *ls) {
    FuncState *fs = ls->fs;
    BlockCnt *bl = fs->bl;
    int upval = 0;
    while (bl && !bl->isbreakable) {
        upval |= bl->upval;
        bl = bl->previous;
    }
    if (!bl)
        lvX_syntaxerror(ls, "no loop to break");
    if( bl ) {
        if (upval)
            lvK_codeABC(fs, OP_CLOSE, bl->nactvar, 0, 0);
        lvK_concat(fs, &bl->breaklist, lvK_jump(fs));
    }
}


static void whilestat (LexState *ls, int line) {
    /* whilestat -> WHILE cond DO block END */
    FuncState *fs = ls->fs;
    int whileinit;
    int condexit;
    int javaMode = 0;
    int towLinesMore = 0;
    BlockCnt bl;
    lvX_next(ls);  /* skip WHILE */
    whileinit = lvK_getlabel(fs);
    condexit = cond(ls, &javaMode);
    enterblock(fs, &bl, 1);
    
    if( testnext(ls, TK_DO) ) {
        javaMode = 0;
        block(ls);
    } else if ( testnext(ls, '{') ){
        javaMode = 1;
        towLinesMore = 1;
        block(ls);
    } else {
        javaMode = 1;
        towLinesMore = 0;
        block_oneline(ls);
    }
    
    lvK_patchlist(fs, lvK_jump(fs), whileinit);
    if( javaMode ) {
        if( towLinesMore ) {
            check_match(ls, '}', TK_WHILE, line);
        }
    } else {
        check_match(ls, TK_END, TK_WHILE, line);
    }
    leaveblock(fs);
    lvK_patchtohere(fs, condexit);  /* false conditions finish the loop */
}


static void repeatstat (LexState *ls, int line) {
    /* repeatstat -> REPEAT block UNTIL cond */
    int condexit;
    int javaMode = 0;
    FuncState *fs = ls->fs;
    int repeat_init = lvK_getlabel(fs);
    BlockCnt bl1, bl2;
    enterblock(fs, &bl1, 1);  /* loop block */
    enterblock(fs, &bl2, 0);  /* scope block */
    lvX_next(ls);  /* skip REPEAT */
    chunk(ls);
    check_match(ls, TK_UNTIL, TK_REPEAT, line);
    condexit = cond(ls, &javaMode);  /* read condition (inside scope block) */
    if (!bl2.upval) {  /* no upvalues? */
        leaveblock(fs);  /* finish scope */
        lvK_patchlist(ls->fs, condexit, repeat_init);  /* close the loop */
    }
    else {  /* complete semantics when there are upvalues */
        breakstat(ls);  /* if condition then break */
        lvK_patchtohere(ls->fs, condexit);  /* else... */
        leaveblock(fs);  /* finish scope... */
        lvK_patchlist(ls->fs, lvK_jump(fs), repeat_init);  /* and repeat */
    }
    leaveblock(fs);  /* finish loop */
}


static int exp1 (LexState *ls) {
    expdesc e;
    int k;
    expr(ls, &e);
    k = e.k;
    lvK_exp2nextreg(ls->fs, &e);
    return k;
}


static void forbody (LexState *ls, int base, int line, int nvars, int isnum, int javaMode) {
    /* forbody -> DO block */
    BlockCnt bl;
    FuncState *fs = ls->fs;
    int prep, endfor;
    adjustlocalvars(ls, 3);  /* control variables */
    if( javaMode ) {
        
    } else {
        checknext(ls, TK_DO);
    }
    prep = isnum ? lvK_codeAsBx(fs, OP_FORPREP, base, NO_JUMP) : lvK_jump(fs);
    enterblock(fs, &bl, 0);  /* scope for declared variables */
    adjustlocalvars(ls, nvars);
    lvK_reserveregs(fs, nvars);
    block(ls);
    leaveblock(fs);  /* end of scope for declared variables */
    lvK_patchtohere(fs, prep);
    endfor = (isnum) ? lvK_codeAsBx(fs, OP_FORLOOP, base, NO_JUMP) :
    lvK_codeABC(fs, OP_TFORLOOP, base, 0, nvars);
    lvK_fixline(fs, line);  /* pretend that `OP_FOR' starts the loop */
    lvK_patchlist(fs, (isnum ? endfor : lvK_jump(fs)), prep + 1);
}


static void fornum (LexState *ls, TString *varname, int line ,int javaMode) {
    /* fornum -> NAME = exp1,exp1[,exp1] forbody */
    FuncState *fs = ls->fs;
    int base = fs->freereg;
    new_localvarliteral(ls, "(for index)", 0);
    new_localvarliteral(ls, "(for limit)", 1);
    new_localvarliteral(ls, "(for step)", 2);
    new_localvar(ls, varname, 3);
    checknext(ls, '=');
    exp1(ls);  /* initial value */
    if( testnext(ls, ';') ){
    } else {
        checknext(ls, ',');
    }
    exp1(ls);  /* limit */
    if ( testnext(ls, ',') || testnext(ls, ';') )
        exp1(ls);  /* optional step */
    else {  /* default step = 1 */
        lvK_codeABx(fs, OP_LOADK, fs->freereg, lvK_numberK(fs, 1));
        lvK_reserveregs(fs, 1);
    }
    if( javaMode ){
        checknext(ls, ')');
        checknext(ls, '{');
    }
    forbody(ls, base, line, 1, 1, javaMode);
    if( javaMode ) {
        checknext(ls, '}');
    }
}


static void forlist (LexState *ls, TString *indexname, int javaMode) {
    /* forlist -> NAME {,NAME} IN explist1 forbody */
    FuncState *fs = ls->fs;
    expdesc e;
    int nvars = 0;
    int line;
    int base = fs->freereg;
    /* create control variables */
    new_localvarliteral(ls, "(for generator)", nvars++);
    new_localvarliteral(ls, "(for state)", nvars++);
    new_localvarliteral(ls, "(for control)", nvars++);
    /* create declared variables */
    new_localvar(ls, indexname, nvars++);
    while (testnext(ls, ','))
        new_localvar(ls, str_checkname(ls), nvars++);
    checknext(ls, TK_IN);
    line = ls->linenumber;
    adjust_assign(ls, 3, explist1(ls, &e), &e);
    lvK_checkstack(fs, 3);  /* extra space to call generator */
    if( javaMode ){
        checknext(ls, ')');
        checknext(ls, '{');
    }
    forbody(ls, base, line, nvars - 3, 0, javaMode);
    if( javaMode ) {
        checknext(ls, '}');
    }
}


static void forstat (LexState *ls, int line) {
    /* forstat -> FOR (fornum | forlist) END */
    FuncState *fs = ls->fs;
    TString *varname;
    BlockCnt bl;
    int javaMode = 0;
    enterblock(fs, &bl, 1);  /* scope for loop and control variables */
    lvX_next(ls);  /* skip `for' */
    if( testnext(ls, '(') ) {
        javaMode = 1;
    }
    varname = str_checkname(ls);  /* first variable name */
    switch (ls->t.token) {
        case '=': fornum(ls, varname, line, javaMode); break;
        case ',': case TK_IN: forlist(ls, varname, javaMode); break;
        default: lvX_syntaxerror(ls, LV_QL("=") " or " LV_QL("in") " expected");
    }
    if( javaMode ) {
        
    } else {
        check_match(ls, TK_END, TK_FOR, line);
    }
    leaveblock(fs);  /* loop scope (`break' jumps to this point) */
}


static int test_cond_then_block (LexState *ls, int* javaMode) {
    /* test_then_block -> [IF | ELSEIF] cond THEN block */
    int condexit;
    lvX_next(ls);  /* skip IF or ELSEIF */
    condexit = cond(ls, javaMode);
    
    if( testnext(ls, '{') ) {
        block(ls);  /* `then' part */
        *javaMode = 1;// {} 模式
        checknext(ls, '}');
    } else if( testnext(ls, TK_THEN) ){
        *javaMode = 0;
        block(ls);  /* `then' part */
    } else
    {
        *javaMode = 1;
        chunk_oneline(ls);
    }
    return condexit;
}


static void ifstat (LexState *ls, int line) {
    /* ifstat -> IF cond THEN block {ELSEIF cond THEN block} [ELSE block] END */
    FuncState *fs = ls->fs;
    int flist;
    int escapelist = NO_JUMP;
    int javaMode = 0;
    flist = test_cond_then_block(ls, &javaMode);  /* IF cond THEN block */
    while (1) {
        lvX_lookahead(ls);
        if(  ls->t.token == TK_ELSEIF ){
            lvK_concat(fs, &escapelist, lvK_jump(fs));
            lvK_patchtohere(fs, flist);
            flist = test_cond_then_block(ls,&javaMode);  /* ELSEIF cond THEN block */
//        } else if(  ls->t.token == TK_ELSE && ls->lookahead.token==TK_IF ){//新加语法 elseif == else if
//            lvX_next(ls);  /* skip IF */
//            lvK_concat(fs, &escapelist, lvK_jump(fs));
//            lvK_patchtohere(fs, flist);
//            flist = test_cond_then_block(ls,&javaMode);  /* ELSEIF cond THEN block */
        } else {
            break;
        }
    }
    if (ls->t.token == TK_ELSE) {
        lvK_concat(fs, &escapelist, lvK_jump(fs));
        lvK_patchtohere(fs, flist);
        lvX_next(ls);  /* skip ELSE (after patch, for correct line info) */
        if( testnext(ls, '{') ) {
            block(ls);  /* `else' part */
            checknext(ls, '}');
            javaMode = 1;
        } else if( javaMode ) {// 兼容java无大括号的模式
            block_oneline(ls);  /* `else' part */
        } else {
            block(ls);  /* `else' part */
            javaMode = 0;
        }
    }
    else {
        lvK_concat(fs, &escapelist, flist);
    }
    lvK_patchtohere(fs, escapelist);
    if( javaMode ){
    } else {
        check_match(ls, TK_END, TK_IF, line);
    }
}


static void localfunc (LexState *ls) {
    expdesc v, b;
    FuncState *fs = ls->fs;
    new_localvar(ls, str_checkname(ls), 0);
    init_exp(&v, VLOCAL, fs->freereg);
    lvK_reserveregs(fs, 1);
    adjustlocalvars(ls, 1);
    body(ls, &b, 0, ls->linenumber);
    lvK_storevar(fs, &v, &b);
    /* debug information will only see the variable after this point! */
    getlocvar(fs, fs->nactvar - 1).startpc = fs->pc;
}


static void localstat (LexState *ls) {
    /* stat -> LOCAL NAME {`,' NAME} [`=' explist1] */
    int nvars = 0;
    int nexps;
    expdesc e;
    do {
        new_localvar(ls, str_checkname(ls), nvars++);
    } while (testnext(ls, ','));
    if (testnext(ls, '='))
        nexps = explist1(ls, &e);
    else {
        e.k = VVOID;
        nexps = 0;
    }
    adjust_assign(ls, nvars, nexps, &e);
    adjustlocalvars(ls, nvars);
}


static int funcname (LexState *ls, expdesc *v) {
    /* funcname -> NAME {field} [`:' NAME] */
    int needself = 0;
    singlevar(ls, v);
    while (1) {
        if( ls->t.token == '.' ){
            field(ls, v);
            needself = 1;
        } else {
            break;
        }
    }
    if (ls->t.token == ':') {
        needself = 0;
        field(ls, v);
    }
    return needself;
}


static void funcstat (LexState *ls, int line) {
    /* funcstat -> FUNCTION funcname body */
    int needself;
    expdesc v, b;
    lvX_next(ls);  /* skip FUNCTION */
    needself = funcname(ls, &v);
    body(ls, &b, needself, line);
    lvK_storevar(ls->fs, &v, &b);
    lvK_fixline(ls->fs, line);  /* definition `happens' in the first line */
}


static void exprstat (LexState *ls) {
    /* stat -> func | assignment */
    FuncState *fs = ls->fs;
    struct LHS_assign v = {0};
    primaryexp(ls, &v.v);
    if (v.v.k == VCALL)  /* stat -> func */
        SETARG_C(getcode(fs, &v.v), 1);  /* call statement uses no results */
    else {  /* stat -> assignment */
        v.prev = NULL;
        assignment(ls, &v, 1);
    }
}


static void retstat (LexState *ls) {
    /* stat -> RETURN explist */
    FuncState *fs = ls->fs;
    expdesc e;
    int first, nret;  /* registers with returned values */
    lvX_next(ls);  /* skip RETURN */
    if (block_follow(ls->t.token) || ls->t.token == ';')
        first = nret = 0;  /* return no values */
    else {
        nret = explist1(ls, &e);  /* optional return values */
        if (hasmultret(e.k)) {
            lvK_setmultret(fs, &e);
            if (e.k == VCALL && nret == 1) {  /* tail call? */
                SET_OPCODE(getcode(fs,&e), OP_TAILCALL);
                lv_assert(GETARG_A(getcode(fs,&e)) == fs->nactvar);
            }
            first = fs->nactvar;
            nret = LV_MULTRET;  /* return all values */
        }
        else {
            if (nret == 1)  /* only one single value? */
                first = lvK_exp2anyreg(fs, &e);
            else {
                lvK_exp2nextreg(fs, &e);  /* values must go to the `stack' */
                first = fs->nactvar;  /* return all `active' values */
                lv_assert(nret == fs->freereg - first);
            }
        }
    }
    lvK_ret(fs, first, nret);
}


static int statement (LexState *ls) {
    int line = ls->linenumber;  /* may be needed for error messages */
    switch (ls->t.token) {
        case TK_IF: {  /* stat -> ifstat */
            ifstat(ls, line);
            return 0;
        }
        case TK_WHILE: {  /* stat -> whilestat */
            whilestat(ls, line);
            return 0;
        }
        case TK_DO: {  /* stat -> DO block END */
            lvX_next(ls);  /* skip DO */
            block(ls);
            check_match(ls, TK_END, TK_DO, line);
            return 0;
        }
        case '{': {  /* stat -> { block } */
            lvX_next(ls);  /* skip { */
            block(ls);
            check_match(ls, '}', '{', line);
            return 0;
        }
        case TK_FOR: {  /* stat -> forstat */
            forstat(ls, line);
            return 0;
        }
        case TK_REPEAT: {  /* stat -> repeatstat */
            repeatstat(ls, line);
            return 0;
        }
        case TK_FUNCTION: {
            funcstat(ls, line);  /* stat -> funcstat */
            return 0;
        }
        case TK_LOCAL: {  /* stat -> localstat */
            lvX_next(ls);  /* skip LOCAL */
            if (testnext(ls, TK_FUNCTION))  /* local function? */
                localfunc(ls);
            else
                localstat(ls);
            return 0;
        }
        case TK_RETURN: {  /* stat -> retstat */
            retstat(ls);
            return 1;  /* must be last statement */
        }
        case TK_BREAK: {  /* stat -> breakstat */
            lvX_next(ls);  /* skip BREAK */
            breakstat(ls);
            return 1;  /* must be last statement */
        }
        default: {
            exprstat(ls);
            return 0;  /* to avoid warnings */
        }
    }
}


static void chunk (LexState *ls) {
    /* chunk -> { stat [`;'] } */
    int islast = 0;
    enterlevel(ls);
    while (!islast && !block_follow(ls->t.token)) {
        islast = statement(ls);
        testnext(ls, ';');
        lv_assert(ls->fs->f->maxstacksize >= ls->fs->freereg &&
                  ls->fs->freereg >= ls->fs->nactvar);
        ls->fs->freereg = ls->fs->nactvar;  /* free registers */
    }
    leavelevel(ls);
}

static void chunk_oneline (LexState *ls) {
    /* chunk -> { stat [`;'] } */
    enterlevel(ls);
    if ( !block_follow(ls->t.token)) {
        statement(ls);
        testnext(ls, ';');
        lv_assert(ls->fs->f->maxstacksize >= ls->fs->freereg &&
                  ls->fs->freereg >= ls->fs->nactvar);
        ls->fs->freereg = ls->fs->nactvar;  /* free registers */
    }
    leavelevel(ls);
}

/* }====================================================================== */
