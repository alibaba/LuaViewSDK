/*
** $Id: lVfunc.h,v 2.4.1.1 2007/12/27 13:02:25 roberto Exp $
** Auxiliary functions to manipulate prototypes and closures
** See Copyright Notice in lV.h
*/

#ifndef lfunc_h
#define lfunc_h


#include "lVobject.h"


#define sizeCclosure(n)	(cast(int, sizeof(CClosure)) + \
                         cast(int, sizeof(TValue)*((n)-1)))

#define sizeLclosure(n)	(cast(int, sizeof(LClosure)) + \
                         cast(int, sizeof(TValue *)*((n)-1)))


LVI_FUNC Proto *lvF_newproto (lv_State *L);
LVI_FUNC Closure *lvF_newCclosure (lv_State *L, int nelems, Table *e);
LVI_FUNC Closure *lvF_newLclosure (lv_State *L, int nelems, Table *e);
LVI_FUNC UpVal *lvF_newupval (lv_State *L);
LVI_FUNC UpVal *lvF_findupval (lv_State *L, StkId level);
LVI_FUNC void lvF_close (lv_State *L, StkId level);
LVI_FUNC void lvF_freeproto (lv_State *L, Proto *f);
LVI_FUNC void lvF_freeclosure (lv_State *L, Closure *c);
LVI_FUNC void lvF_freeupval (lv_State *L, UpVal *uv);
LVI_FUNC const char *lvF_getlocalname (const Proto *func, int local_number,
                                         int pc);


#endif
