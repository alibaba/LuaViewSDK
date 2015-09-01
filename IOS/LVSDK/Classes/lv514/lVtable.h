/*
** $Id: lVtable.h,v 2.10.1.1 2007/12/27 13:02:25 roberto Exp $
** [L u a] tables (hash)
** See Copyright Notice in lV.h
*/

#ifndef ltable_h
#define ltable_h

#include "lVobject.h"


#define gnode(t,i)	(&(t)->node[i])
#define gkey(n)		(&(n)->i_key.nk)
#define gval(n)		(&(n)->i_val)
#define gnext(n)	((n)->i_key.nk.next)

#define key2tval(n)	(&(n)->i_key.tvk)


LVI_FUNC const TValue *lvH_getnum (Table *t, int key);
LVI_FUNC TValue *lvH_setnum (lv_State *L, Table *t, int key);
LVI_FUNC const TValue *lvH_getstr (Table *t, TString *key);
LVI_FUNC TValue *lvH_setstr (lv_State *L, Table *t, TString *key);
LVI_FUNC const TValue *lvH_get (Table *t, const TValue *key);
LVI_FUNC TValue *lvH_set (lv_State *L, Table *t, const TValue *key);
LVI_FUNC Table *lvH_new (lv_State *L, int narray, int lnhash);
LVI_FUNC void lvH_resizearray (lv_State *L, Table *t, int nasize);
LVI_FUNC void lvH_free (lv_State *L, Table *t);
LVI_FUNC int lvH_next (lv_State *L, Table *t, StkId key);
LVI_FUNC int lvH_getn (Table *t);


#if defined(LV_DEBUG)
LVI_FUNC Node *lvH_mainposition (const Table *t, const TValue *key);
LVI_FUNC int lvH_isdummy (Node *n);
#endif


#endif
