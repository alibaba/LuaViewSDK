/*
** $Id: lVfunc.c,v 2.12.1.2 2007/12/28 14:58:43 roberto Exp $
** Auxiliary functions to manipulate prototypes and closures
** See Copyright Notice in lV.h
*/


#include <stddef.h>

#define lfunc_c
#define LV_CORE

#include "lV.h"

#include "lVfunc.h"
#include "lVgc.h"
#include "lVmem.h"
#include "lVobject.h"
#include "lVstate.h"



Closure *lvF_newCclosure (lv_State *L, int nelems, Table *e) {
  Closure *c = cast(Closure *, lvM_malloc(L, sizeCclosure(nelems)));
  lvC_link(L, obj2gco(c), LV_TFUNCTION);
  c->c.isC = 1;
  c->c.env = e;
  c->c.nupvalues = cast_byte(nelems);
  return c;
}


Closure *lvF_newLclosure (lv_State *L, int nelems, Table *e) {
  Closure *c = cast(Closure *, lvM_malloc(L, sizeLclosure(nelems)));
  lvC_link(L, obj2gco(c), LV_TFUNCTION);
  c->l.isC = 0;
  c->l.env = e;
  c->l.nupvalues = cast_byte(nelems);
  while (nelems--) c->l.upvals[nelems] = NULL;
  return c;
}


UpVal *lvF_newupval (lv_State *L) {
  UpVal *uv = lvM_new(L, UpVal);
  lvC_link(L, obj2gco(uv), LV_TUPVAL);
  uv->v = &uv->u.value;
  setnilvalue(uv->v);
  return uv;
}


UpVal *lvF_findupval (lv_State *L, StkId level) {
  global_State *g = G(L);
  GCObject **pp = &L->openupval;
  UpVal *p;
  UpVal *uv;
  while (*pp != NULL && (p = ngcotouv(*pp))->v >= level) {
    lv_assert(p->v != &p->u.value);
    if (p->v == level) {  /* found a corresponding upvalue? */
      if (isdead(g, obj2gco(p)))  /* is it dead? */
        changewhite(obj2gco(p));  /* ressurect it */
      return p;
    }
    pp = &p->next;
  }
  uv = lvM_new(L, UpVal);  /* not found: create a new one */
  uv->tt = LV_TUPVAL;
  uv->marked = lvC_white(g);
  uv->v = level;  /* current value lives in the stack */
  uv->next = *pp;  /* chain it in the proper position */
  *pp = obj2gco(uv);
  uv->u.l.prev = &g->uvhead;  /* double link it in `uvhead' list */
  uv->u.l.next = g->uvhead.u.l.next;
  uv->u.l.next->u.l.prev = uv;
  g->uvhead.u.l.next = uv;
  lv_assert(uv->u.l.next->u.l.prev == uv && uv->u.l.prev->u.l.next == uv);
  return uv;
}


static void unlinkupval (UpVal *uv) {
  lv_assert(uv->u.l.next->u.l.prev == uv && uv->u.l.prev->u.l.next == uv);
  uv->u.l.next->u.l.prev = uv->u.l.prev;  /* remove from `uvhead' list */
  uv->u.l.prev->u.l.next = uv->u.l.next;
}


void lvF_freeupval (lv_State *L, UpVal *uv) {
  if (uv->v != &uv->u.value)  /* is it open? */
    unlinkupval(uv);  /* remove from open list */
  lvM_free(L, uv);  /* free upvalue */
}


void lvF_close (lv_State *L, StkId level) {
  UpVal *uv;
  global_State *g = G(L);
  while (L->openupval != NULL && (uv = ngcotouv(L->openupval))->v >= level) {
    GCObject *o = obj2gco(uv);
    lv_assert(!isblack(o) && uv->v != &uv->u.value);
    L->openupval = uv->next;  /* remove from `open' list */
    if (isdead(g, o))
      lvF_freeupval(L, uv);  /* free upvalue */
    else {
      unlinkupval(uv);
      setobj(L, &uv->u.value, uv->v);
      uv->v = &uv->u.value;  /* now current value lives here */
      lvC_linkupval(L, uv);  /* link upvalue into `gcroot' list */
    }
  }
}


Proto *lvF_newproto (lv_State *L) {
  Proto *f = lvM_new(L, Proto);
  lvC_link(L, obj2gco(f), LV_TPROTO);
  f->k = NULL;
  f->sizek = 0;
  f->p = NULL;
  f->sizep = 0;
  f->code = NULL;
  f->sizecode = 0;
  f->sizelineinfo = 0;
  f->sizeupvalues = 0;
  f->nups = 0;
  f->upvalues = NULL;
  f->numparams = 0;
  f->is_vararg = 0;
  f->maxstacksize = 0;
  f->lineinfo = NULL;
  f->sizelocvars = 0;
  f->locvars = NULL;
  f->linedefined = 0;
  f->lastlinedefined = 0;
  f->source = NULL;
  return f;
}


void lvF_freeproto (lv_State *L, Proto *f) {
  lvM_freearray(L, f->code, f->sizecode, Instruction);
  lvM_freearray(L, f->p, f->sizep, Proto *);
  lvM_freearray(L, f->k, f->sizek, TValue);
  lvM_freearray(L, f->lineinfo, f->sizelineinfo, int);
  lvM_freearray(L, f->locvars, f->sizelocvars, struct LocVar);
  lvM_freearray(L, f->upvalues, f->sizeupvalues, TString *);
  lvM_free(L, f);
}


void lvF_freeclosure (lv_State *L, Closure *c) {
  int size = (c->c.isC) ? sizeCclosure(c->c.nupvalues) :
                          sizeLclosure(c->l.nupvalues);
  lvM_freemem(L, c, size);
}


/*
** Look for n-th local variable at line `line' in function `func'.
** Returns NULL if not found.
*/
const char *lvF_getlocalname (const Proto *f, int local_number, int pc) {
  int i;
  for (i = 0; i<f->sizelocvars && f->locvars[i].startpc <= pc; i++) {
    if (pc < f->locvars[i].endpc) {  /* is variable active? */
      local_number--;
      if (local_number == 0)
        return getstr(f->locvars[i].varname);
    }
  }
  return NULL;  /* not found */
}

