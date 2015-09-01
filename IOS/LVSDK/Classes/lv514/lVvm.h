/*
** $Id: lVvm.h,v 2.5.1.1 2007/12/27 13:02:25 roberto Exp $
** [L u a] virtual machine
** See Copyright Notice in lV.h
*/

#ifndef lvm_h
#define lvm_h


#include "lVdo.h"
#include "lVobject.h"
#include "lVtm.h"


#define tostring(L,o) ((ttype(o) == LV_TSTRING) || (lvV_tostring(L, o)))

#define tonumber(o,n)	(ttype(o) == LV_TNUMBER || \
                         (((o) = lvV_tonumber(o,n)) != NULL))

#define equalobj(L,o1,o2) \
	(ttype(o1) == ttype(o2) && lvV_equalval(L, o1, o2))


LVI_FUNC int lvV_lessthan (lv_State *L, const TValue *l, const TValue *r);
LVI_FUNC int lvV_equalval (lv_State *L, const TValue *t1, const TValue *t2);
LVI_FUNC const TValue *lvV_tonumber (const TValue *obj, TValue *n);
LVI_FUNC int lvV_tostring (lv_State *L, StkId obj);
LVI_FUNC void lvV_gettable (lv_State *L, const TValue *t, TValue *key,
                                            StkId val);
LVI_FUNC void lvV_settable (lv_State *L, const TValue *t, TValue *key,
                                            StkId val);
LVI_FUNC void lvV_execute (lv_State *L, int nexeccalls);
LVI_FUNC void lvV_concat (lv_State *L, int total, int last);

#endif
