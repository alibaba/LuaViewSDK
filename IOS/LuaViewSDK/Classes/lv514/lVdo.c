/*
** $Id: lVdo.c,v 2.38.1.3 2008/01/18 22:31:22 roberto Exp $
** Stack and Call structure of [L u a]
** See Copyright Notice in lV.h
*/


#include <setjmp.h>
#include <stdlib.h>
#include <string.h>

#define ldo_c
#define LV_CORE

#include "lV.h"

#include "lVdebug.h"
#include "lVdo.h"
#include "lVfunc.h"
#include "lVgc.h"
#include "lVmem.h"
#include "lVobject.h"
#include "lVopcodes.h"
#include "lVparser.h"
#include "lVstate.h"
#include "lVstring.h"
#include "lVtable.h"
#include "lVtm.h"
#include "lVundump.h"
#include "lVvm.h"
#include "lVzio.h"




/*
** {======================================================
** Error-recovery functions
** =======================================================
*/


/* chain list of long jump buffers */
struct lv_longjmp {
  struct lv_longjmp *previous;
  lvi_jmpbuf b;
  volatile int status;  /* error code */
};


void lvD_seterrorobj (lv_State *L, int errcode, StkId oldtop) {
  switch (errcode) {
    case LV_ERRMEM: {
      setsvalue2s(L, oldtop, lvS_newliteral(L, MEMERRMSG));
      break;
    }
    case LV_ERRERR: {
      setsvalue2s(L, oldtop, lvS_newliteral(L, "error in error handling"));
      break;
    }
    case LV_ERRSYNTAX:
    case LV_ERRRUN: {
      setobjs2s(L, oldtop, L->top - 1);  /* error message on current top */
      break;
    }
  }
  L->top = oldtop + 1;
}


static void restore_stack_limit (lv_State *L) {
  lv_assert(L->stack_last - L->stack == L->stacksize - EXTRA_STACK - 1);
  if (L->size_ci > LVI_MAXCALLS) {  /* there was an overflow? */
    int inuse = cast_int(L->ci - L->base_ci);
    if (inuse + 1 < LVI_MAXCALLS)  /* can `undo' overflow? */
      lvD_reallocCI(L, LVI_MAXCALLS);
  }
}


static void resetstack (lv_State *L, int status) {
  L->ci = L->base_ci;
  L->base = L->ci->base;
  lvF_close(L, L->base);  /* close eventual pending closures */
  lvD_seterrorobj(L, status, L->base);
  L->nCcalls = L->baseCcalls;
  L->allowhook = 1;
  restore_stack_limit(L);
  L->errfunc = 0;
  L->errorJmp = NULL;
}


void lvD_throw (lv_State *L, int errcode) {
  if (L->errorJmp) {
    L->errorJmp->status = errcode;
    LVI_THROW(L, L->errorJmp);
  }
  else {
    L->status = cast_byte(errcode);
    if (G(L)->panic) {
      resetstack(L, errcode);
      lv_unlock(L);
      G(L)->panic(L);
    }
    exit(EXIT_FAILURE);
  }
}


int lvD_rawrunprotected (lv_State *L, Pfunc f, void *ud) {
  struct lv_longjmp lj;
  lj.status = 0;
  lj.previous = L->errorJmp;  /* chain new error handler */
  L->errorJmp = &lj;
  LVI_TRY(L, &lj,
    (*f)(L, ud);
  );
  L->errorJmp = lj.previous;  /* restore old error handler */
  return lj.status;
}

/* }====================================================== */


static void correctstack (lv_State *L, TValue *oldstack) {
  CallInfo *ci;
  GCObject *up;
  L->top = (L->top - oldstack) + L->stack;
  for (up = L->openupval; up != NULL; up = up->gch.next)
    gco2uv(up)->v = (gco2uv(up)->v - oldstack) + L->stack;
  for (ci = L->base_ci; ci <= L->ci; ci++) {
    ci->top = (ci->top - oldstack) + L->stack;
    ci->base = (ci->base - oldstack) + L->stack;
    ci->func = (ci->func - oldstack) + L->stack;
  }
  L->base = (L->base - oldstack) + L->stack;
}


void lvD_reallocstack (lv_State *L, int newsize) {
  TValue *oldstack = L->stack;
  int realsize = newsize + 1 + EXTRA_STACK;
  lv_assert(L->stack_last - L->stack == L->stacksize - EXTRA_STACK - 1);
  lvM_reallocvector(L, L->stack, L->stacksize, realsize, TValue);
  L->stacksize = realsize;
  L->stack_last = L->stack+newsize;
  correctstack(L, oldstack);
}


void lvD_reallocCI (lv_State *L, int newsize) {
  CallInfo *oldci = L->base_ci;
  lvM_reallocvector(L, L->base_ci, L->size_ci, newsize, CallInfo);
  L->size_ci = newsize;
  L->ci = (L->ci - oldci) + L->base_ci;
  L->end_ci = L->base_ci + L->size_ci - 1;
}


void lvD_growstack (lv_State *L, int n) {
  if (n <= L->stacksize)  /* double size is enough? */
    lvD_reallocstack(L, 2*L->stacksize);
  else
    lvD_reallocstack(L, L->stacksize + n);
}


static CallInfo *growCI (lv_State *L) {
  if (L->size_ci > LVI_MAXCALLS)  /* overflow while handling overflow? */
    lvD_throw(L, LV_ERRERR);
  else {
    lvD_reallocCI(L, 2*L->size_ci);
    if (L->size_ci > LVI_MAXCALLS)
      lvG_runerror(L, "stack overflow");
  }
  return ++L->ci;
}


void lvD_callhook (lv_State *L, int event, int line) {
  lv_Hook hook = L->hook;
  if (hook && L->allowhook) {
    ptrdiff_t top = savestack(L, L->top);
    ptrdiff_t ci_top = savestack(L, L->ci->top);
    lv_Debug ar;
    ar.event = event;
    ar.currentline = line;
    if (event == LV_HOOKTAILRET)
      ar.i_ci = 0;  /* tail call; no debug information about it */
    else
      ar.i_ci = cast_int(L->ci - L->base_ci);
    lvD_checkstack(L, LV_MINSTACK);  /* ensure minimum stack size */
    L->ci->top = L->top + LV_MINSTACK;
    lv_assert(L->ci->top <= L->stack_last);
    L->allowhook = 0;  /* cannot call hooks inside a hook */
    lv_unlock(L);
    (*hook)(L, &ar);
    lv_lock(L);
    lv_assert(!L->allowhook);
    L->allowhook = 1;
    L->ci->top = restorestack(L, ci_top);
    L->top = restorestack(L, top);
  }
}


static StkId adjust_varargs (lv_State *L, Proto *p, int actual) {
  int i;
  int nfixargs = p->numparams;
  Table *htab = NULL;
  StkId base, fixed;
  for (; actual < nfixargs; ++actual)
    setnilvalue(L->top++);
#if defined(LV_COMPAT_VARARG)
  if (p->is_vararg & VARARG_NEEDSARG) { /* compat. with old-style vararg? */
    int nvar = actual - nfixargs;  /* number of extra arguments */
    lv_assert(p->is_vararg & VARARG_HASARG);
    lvC_checkGC(L);
    htab = lvH_new(L, nvar, 1);  /* create `arg' table */
    for (i=0; i<nvar; i++)  /* put extra arguments into `arg' table */
      setobj2n(L, lvH_setnum(L, htab, i+1), L->top - nvar + i);
    /* store counter in field `n' */
    setnvalue(lvH_setstr(L, htab, lvS_newliteral(L, "n")), cast_num(nvar));
  }
#endif
  /* move fixed parameters to final position */
  fixed = L->top - actual;  /* first fixed argument */
  base = L->top;  /* final position of first argument */
  for (i=0; i<nfixargs; i++) {
    setobjs2s(L, L->top++, fixed+i);
    setnilvalue(fixed+i);
  }
  /* add `arg' parameter */
  if (htab) {
    sethvalue(L, L->top++, htab);
    lv_assert(iswhite(obj2gco(htab)));
  }
  return base;
}


static StkId tryfuncTM (lv_State *L, StkId func) {
  const TValue *tm = lvT_gettmbyobj(L, func, TM_CALL);
  StkId p;
  ptrdiff_t funcr = savestack(L, func);
  if (!ttisfunction(tm))
    lvG_typeerror(L, func, "call");
  /* Open a hole inside the stack at `func' */
  for (p = L->top; p > func; p--) setobjs2s(L, p, p-1);
  incr_top(L);
  func = restorestack(L, funcr);  /* previous call may change stack */
  setobj2s(L, func, tm);  /* tag method is the new function to be called */
  return func;
}



#define inc_ci(L) \
  ((L->ci == L->end_ci) ? growCI(L) : \
   (condhardstacktests(lvD_reallocCI(L, L->size_ci)), ++L->ci))


int lvD_precall (lv_State *L, StkId func, int nresults) {
  LClosure *cl;
  ptrdiff_t funcr;
  if (!ttisfunction(func)) /* `func' is not a function? */
    func = tryfuncTM(L, func);  /* check the `function' tag method */
  funcr = savestack(L, func);
  cl = &clvalue(func)->l;
  L->ci->savedpc = L->savedpc;
  if (!cl->isC) {  /* [L u a] function? prepare its call */
    CallInfo *ci;
    StkId st, base;
    Proto *p = cl->p;
    lvD_checkstack(L, p->maxstacksize);
    func = restorestack(L, funcr);
    if (!p->is_vararg) {  /* no varargs? */
      base = func + 1;
      if (L->top > base + p->numparams)
        L->top = base + p->numparams;
    }
    else {  /* vararg function */
      int nargs = cast_int(L->top - func) - 1;
      base = adjust_varargs(L, p, nargs);
      func = restorestack(L, funcr);  /* previous call may change the stack */
    }
    ci = inc_ci(L);  /* now `enter' new function */
    ci->func = func;
    L->base = ci->base = base;
    ci->top = L->base + p->maxstacksize;
    lv_assert(ci->top <= L->stack_last);
    L->savedpc = p->code;  /* starting point */
    ci->tailcalls = 0;
    ci->nresults = nresults;
    for (st = L->top; st < ci->top; st++)
      setnilvalue(st);
    L->top = ci->top;
    if (L->hookmask & LV_MASKCALL) {
      L->savedpc++;  /* hooks assume 'pc' is already incremented */
      lvD_callhook(L, LV_HOOKCALL, -1);
      L->savedpc--;  /* correct 'pc' */
    }
    return PCR_L_U_A;
  }
  else {  /* if is a C function, call it */
    CallInfo *ci;
    int n;
    lvD_checkstack(L, LV_MINSTACK);  /* ensure minimum stack size */
    ci = inc_ci(L);  /* now `enter' new function */
    ci->func = restorestack(L, funcr);
    L->base = ci->base = ci->func + 1;
    ci->top = L->top + LV_MINSTACK;
    lv_assert(ci->top <= L->stack_last);
    ci->nresults = nresults;
    if (L->hookmask & LV_MASKCALL)
      lvD_callhook(L, LV_HOOKCALL, -1);
    lv_unlock(L);
    n = (*curr_func(L)->c.f)(L);  /* do the actual call */
    lv_lock(L);
    if (n < 0)  /* yielding? */
      return PCRYIELD;
    else {
      lvD_poscall(L, L->top - n);
      return PCRC;
    }
  }
}


static StkId callrethooks (lv_State *L, StkId firstResult) {
  ptrdiff_t fr = savestack(L, firstResult);  /* next call may change stack */
  lvD_callhook(L, LV_HOOKRET, -1);
  if (f_isL_u_a(L->ci)) {  /* [L u a] function? */
    while ((L->hookmask & LV_MASKRET) && L->ci->tailcalls--) /* tail calls */
      lvD_callhook(L, LV_HOOKTAILRET, -1);
  }
  return restorestack(L, fr);
}


int lvD_poscall (lv_State *L, StkId firstResult) {
  StkId res;
  int wanted, i;
  CallInfo *ci;
  if (L->hookmask & LV_MASKRET)
    firstResult = callrethooks(L, firstResult);
  ci = L->ci--;
  res = ci->func;  /* res == final position of 1st result */
  wanted = ci->nresults;
  L->base = (ci - 1)->base;  /* restore base */
  L->savedpc = (ci - 1)->savedpc;  /* restore savedpc */
  /* move results to correct place */
  for (i = wanted; i != 0 && firstResult < L->top; i--)
    setobjs2s(L, res++, firstResult++);
  while (i-- > 0)
    setnilvalue(res++);
  L->top = res;
  return (wanted - LV_MULTRET);  /* 0 iff wanted == LV_MULTRET */
}


/*
** Call a function (C or [L u a]). The function to be called is at *func.
** The arguments are on the stack, right after the function.
** When returns, all the results are on the stack, starting at the original
** function position.
*/ 
void lvD_call (lv_State *L, StkId func, int nResults) {
  if (++L->nCcalls >= LVI_MAXCCALLS) {
    if (L->nCcalls == LVI_MAXCCALLS)
      lvG_runerror(L, "C stack overflow");
    else if (L->nCcalls >= (LVI_MAXCCALLS + (LVI_MAXCCALLS>>3)))
      lvD_throw(L, LV_ERRERR);  /* error while handing stack error */
  }
  if (lvD_precall(L, func, nResults) == PCR_L_U_A)  /* is a [L u a] function? */
    lvV_execute(L, 1);  /* call it */
  L->nCcalls--;
  lvC_checkGC(L);
}


static void resume (lv_State *L, void *ud) {
  StkId firstArg = cast(StkId, ud);
  CallInfo *ci = L->ci;
  if (L->status == 0) {  /* start coroutine? */
    lv_assert(ci == L->base_ci && firstArg > L->base);
    if (lvD_precall(L, firstArg - 1, LV_MULTRET) != PCR_L_U_A)
      return;
  }
  else {  /* resuming from previous yield */
    lv_assert(L->status == LV_YIELD);
    L->status = 0;
    if (!f_isL_u_a(ci)) {  /* `common' yield? */
      /* finish interrupted execution of `OP_CALL' */
      lv_assert(GET_OPCODE(*((ci-1)->savedpc - 1)) == OP_CALL ||
                 GET_OPCODE(*((ci-1)->savedpc - 1)) == OP_TAILCALL);
      if (lvD_poscall(L, firstArg))  /* complete it... */
        L->top = L->ci->top;  /* and correct top if not multiple results */
    }
    else  /* yielded inside a hook: just continue its execution */
      L->base = L->ci->base;
  }
  lvV_execute(L, cast_int(L->ci - L->base_ci));
}


static int resume_error (lv_State *L, const char *msg) {
  L->top = L->ci->base;
  setsvalue2s(L, L->top, lvS_new(L, msg));
  incr_top(L);
  lv_unlock(L);
  return LV_ERRRUN;
}


LV_API int lv_resume (lv_State *L, int nargs) {
  int status;
  lv_lock(L);
  if (L->status != LV_YIELD && (L->status != 0 || L->ci != L->base_ci))
      return resume_error(L, "cannot resume non-suspended coroutine");
  if (L->nCcalls >= LVI_MAXCCALLS)
    return resume_error(L, "C stack overflow");
  lvi_userstateresume(L, nargs);
  lv_assert(L->errfunc == 0);
  L->baseCcalls = ++L->nCcalls;
  status = lvD_rawrunprotected(L, resume, L->top - nargs);
  if (status != 0) {  /* error? */
    L->status = cast_byte(status);  /* mark thread as `dead' */
    lvD_seterrorobj(L, status, L->top);
    L->ci->top = L->top;
  }
  else {
    lv_assert(L->nCcalls == L->baseCcalls);
    status = L->status;
  }
  --L->nCcalls;
  lv_unlock(L);
  return status;
}


LV_API int lv_yield (lv_State *L, int nresults) {
  lvi_userstateyield(L, nresults);
  lv_lock(L);
  if (L->nCcalls > L->baseCcalls)
    lvG_runerror(L, "attempt to yield across metamethod/C-call boundary");
  L->base = L->top - nresults;  /* protect stack slots below */
  L->status = LV_YIELD;
  lv_unlock(L);
  return -1;
}


int lvD_pcall (lv_State *L, Pfunc func, void *u,
                ptrdiff_t old_top, ptrdiff_t ef) {
  int status;
  unsigned short oldnCcalls = L->nCcalls;
  ptrdiff_t old_ci = saveci(L, L->ci);
  lu_byte old_allowhooks = L->allowhook;
  ptrdiff_t old_errfunc = L->errfunc;
  L->errfunc = ef;
  status = lvD_rawrunprotected(L, func, u);
  if (status != 0) {  /* an error occurred? */
    StkId oldtop = restorestack(L, old_top);
    lvF_close(L, oldtop);  /* close eventual pending closures */
    lvD_seterrorobj(L, status, oldtop);
    L->nCcalls = oldnCcalls;
    L->ci = restoreci(L, old_ci);
    L->base = L->ci->base;
    L->savedpc = L->ci->savedpc;
    L->allowhook = old_allowhooks;
    restore_stack_limit(L);
  }
  L->errfunc = old_errfunc;
  return status;
}



/*
** Execute a protected parser.
*/
struct SParser {  /* data to `f_parser' */
  ZIO *z;
  Mbuffer buff;  /* buffer to be used by the scanner */
  const char *name;
};

static void f_parser (lv_State *L, void *ud) {
  int i;
  Proto *tf;
  Closure *cl;
  struct SParser *p = cast(struct SParser *, ud);
  int c = lvZ_lookahead(p->z);
  lvC_checkGC(L);
  tf = ((c == LV_SIGNATURE[0]) ? lvU_undump : lvY_parser)(L, p->z,
                                                             &p->buff, p->name);
  cl = lvF_newLclosure(L, tf->nups, hvalue(gt(L)));
  cl->l.p = tf;
  for (i = 0; i < tf->nups; i++)  /* initialize eventual upvalues */
    cl->l.upvals[i] = lvF_newupval(L);
  setclvalue(L, L->top, cl);
  incr_top(L);
}


int lvD_protectedparser (lv_State *L, ZIO *z, const char *name) {
  struct SParser p;
  int status;
  p.z = z; p.name = name;
  lvZ_initbuffer(L, &p.buff);
  status = lvD_pcall(L, f_parser, &p, savestack(L, L->top), L->errfunc);
  lvZ_freebuffer(L, &p.buff);
  return status;
}


