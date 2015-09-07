/*
** $Id: lVstring.h,v 1.43.1.1 2007/12/27 13:02:25 roberto Exp $
** String table (keep all strings handled by [L u a])
** See Copyright Notice in lV.h
*/

#ifndef lstring_h
#define lstring_h


#include "lVgc.h"
#include "lVobject.h"
#include "lVstate.h"


#define sizestring(s)	(sizeof(union TString)+((s)->len+1)*sizeof(char))

#define sizeudata(u)	(sizeof(union Udata)+(u)->len)

#define lvS_new(L, s)	(lvS_newlstr(L, s, strlen(s)))
#define lvS_newliteral(L, s)	(lvS_newlstr(L, "" s, \
                                 (sizeof(s)/sizeof(char))-1))

#define lvS_fix(s)	l_setbit((s)->tsv.marked, FIXEDBIT)

LVI_FUNC void lvS_resize (lv_State *L, int newsize);
LVI_FUNC Udata *lvS_newudata (lv_State *L, size_t s, Table *e);
LVI_FUNC TString *lvS_newlstr (lv_State *L, const char *str, size_t l);


#endif
