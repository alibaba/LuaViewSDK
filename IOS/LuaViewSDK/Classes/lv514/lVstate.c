/*
** $Id: lVstate.c,v 2.36.1.2 2008/01/03 15:20:39 roberto Exp $
** Global State
** See Copyright Notice in lV.h
*/


#include <stddef.h>

#define lstate_c
#define LV_CORE

#include "lV.h"

#include "lVdebug.h"
#include "lVdo.h"
#include "lVfunc.h"
#include "lVgc.h"
#include "lVlex.h"
#include "lVmem.h"
#include "lVstate.h"
#include "lVstring.h"
#include "lVtable.h"
#include "lVtm.h"


#define state_size(x)	(sizeof(x) + LVI_EXTRASPACE)
#define fromstate(l)	(cast(lu_byte *, (l)) - LVI_EXTRASPACE)
#define tostate(l)   (cast(lv_State *, cast(lu_byte *, l) + LVI_EXTRASPACE))


/*
** Main thread combines a thread state and the global state
*/
typedef struct LG {
  lv_State l;
  global_State g;
} LG;
  


static void stack_init (lv_State *L1, lv_State *L) {
  /* initialize CallInfo array */
  L1->base_ci = lvM_newvector(L, BASIC_CI_SIZE, CallInfo);
  L1->ci = L1->base_ci;
  L1->size_ci = BASIC_CI_SIZE;
  L1->end_ci = L1->base_ci + L1->size_ci - 1;
  /* initialize stack array */
  L1->stack = lvM_newvector(L, BASIC_STACK_SIZE + EXTRA_STACK, TValue);
  L1->stacksize = BASIC_STACK_SIZE + EXTRA_STACK;
  L1->top = L1->stack;
  L1->stack_last = L1->stack+(L1->stacksize - EXTRA_STACK)-1;
  /* initialize first ci */
  L1->ci->func = L1->top;
  setnilvalue(L1->top++);  /* `function' entry for this `ci' */
  L1->base = L1->ci->base = L1->top;
  L1->ci->top = L1->top + LV_MINSTACK;
}


static void freestack (lv_State *L, lv_State *L1) {
  lvM_freearray(L, L1->base_ci, L1->size_ci, CallInfo);
  lvM_freearray(L, L1->stack, L1->stacksize, TValue);
}


/*
** open parts that may cause memory-allocation errors
*/
static void f_LVopen (lv_State *L, void *ud) {
  global_State *g = G(L);
  UNUSED(ud);
  stack_init(L, L);  /* init stack */
  sethvalue(L, gt(L), lvH_new(L, 0, 2));  /* table of globals */
  sethvalue(L, registry(L), lvH_new(L, 0, 2));  /* registry */
  lvS_resize(L, MINSTRTABSIZE);  /* initial size of string table */
  lvT_init(L);
  lvX_init(L);
  lvS_fix(lvS_newliteral(L, MEMERRMSG));
  g->GCthreshold = 4*g->totalbytes;
}


static void preinit_state (lv_State *L, global_State *g) {
  G(L) = g;
  L->stack = NULL;
  L->stacksize = 0;
  L->errorJmp = NULL;
  L->hook = NULL;
  L->hookmask = 0;
  L->basehookcount = 0;
  L->allowhook = 1;
  resethookcount(L);
  L->openupval = NULL;
  L->size_ci = 0;
  L->nCcalls = L->baseCcalls = 0;
  L->status = 0;
  L->base_ci = L->ci = NULL;
  L->savedpc = NULL;
  L->errfunc = 0;
  setnilvalue(gt(L));
}


static void close_state (lv_State *L) {
  global_State *g = G(L);
  lvF_close(L, L->stack);  /* close all upvalues for this thread */
  lvC_freeall(L);  /* collect all objects */
  lv_assert(g->rootgc == obj2gco(L));
  lv_assert(g->strt.nuse == 0);
  lvM_freearray(L, G(L)->strt.hash, G(L)->strt.size, TString *);
  lvZ_freebuffer(L, &g->buff);
  freestack(L, L);
  lv_assert(g->totalbytes == sizeof(LG));
  (*g->frealloc)(g->ud, fromstate(L), state_size(LG), 0);
}


lv_State *lvE_newthread (lv_State *L) {
  lv_State *L1 = tostate(lvM_malloc(L, state_size(lv_State)));
  lvC_link(L, obj2gco(L1), LV_TTHREAD);
  preinit_state(L1, G(L));
  stack_init(L1, L);  /* init stack */
  setobj2n(L, gt(L1), gt(L));  /* share table of globals */
  L1->hookmask = L->hookmask;
  L1->basehookcount = L->basehookcount;
  L1->hook = L->hook;
  resethookcount(L1);
  lv_assert(iswhite(obj2gco(L1)));
  return L1;
}


void lvE_freethread (lv_State *L, lv_State *L1) {
  lvF_close(L1, L1->stack);  /* close all upvalues for this thread */
  lv_assert(L1->openupval == NULL);
  lvi_userstatefree(L1);
  freestack(L, L1);
  lvM_freemem(L, fromstate(L1), state_size(lv_State));
}


LV_API lv_State *lv_newstate (lv_Alloc f, void *ud) {
  int i;
  lv_State *L;
  global_State *g;
  void *l = (*f)(ud, NULL, 0, state_size(LG));
  if (l == NULL) return NULL;
  L = tostate(l);
  g = &((LG *)L)->g;
  L->next = NULL;
  L->tt = LV_TTHREAD;
  g->currentwhite = bit2mask(WHITE0BIT, FIXEDBIT);
  L->marked = lvC_white(g);
  set2bits(L->marked, FIXEDBIT, SFIXEDBIT);
  preinit_state(L, g);
  g->frealloc = f;
  g->ud = ud;
  g->mainthread = L;
  g->uvhead.u.l.prev = &g->uvhead;
  g->uvhead.u.l.next = &g->uvhead;
  g->GCthreshold = 0;  /* mark it as unfinished state */
  g->strt.size = 0;
  g->strt.nuse = 0;
  g->strt.hash = NULL;
  setnilvalue(registry(L));
  lvZ_initbuffer(L, &g->buff);
  g->panic = NULL;
  g->gcstate = LV_GCSpause;
  g->rootgc = obj2gco(L);
  g->sweepstrgc = 0;
  g->sweepgc = &g->rootgc;
  g->gray = NULL;
  g->grayagain = NULL;
  g->weak = NULL;
  g->tmudata = NULL;
  g->totalbytes = sizeof(LG);
  g->gcpause = LVI_GCPAUSE;
  g->gcstepmul = LVI_GCMUL;
  g->gcdept = 0;
  for (i=0; i<NUM_TAGS; i++) g->mt[i] = NULL;
  if (lvD_rawrunprotected(L, f_LVopen, NULL) != 0) {
    /* memory allocation error: free partial state */
    close_state(L);
    L = NULL;
  }
  else
    lvi_userstateopen(L);
  return L;
}


static void callallgcTM (lv_State *L, void *ud) {
  UNUSED(ud);
  lvC_callGCTM(L);  /* call GC metamethods for all udata */
}


LV_API void lv_close (lv_State *L) {
  L = G(L)->mainthread;  /* only the main thread can be closed */
  lv_lock(L);
  lvF_close(L, L->stack);  /* close all upvalues for this thread */
  lvC_separateudata(L, 1);  /* separate udata that have GC metamethods */
  L->errfunc = 0;  /* no error function during GC metamethods */
  do {  /* repeat until no more errors */
    L->ci = L->base_ci;
    L->base = L->top = L->ci->base;
    L->nCcalls = L->baseCcalls = 0;
  } while (lvD_rawrunprotected(L, callallgcTM, NULL) != 0);
  lv_assert(G(L)->tmudata == NULL);
  lvi_userstateclose(L);
  close_state(L);
}

