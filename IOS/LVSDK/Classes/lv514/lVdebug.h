/*
** $Id: lVdebug.h,v 2.3.1.1 2007/12/27 13:02:25 roberto Exp $
** Auxiliary functions from Debug Interface module
** See Copyright Notice in lV.h
*/

#ifndef ldebug_h
#define ldebug_h


#include "lVstate.h"


#define pcRel(pc, p)	(cast(int, (pc) - (p)->code) - 1)

#define getline(f,pc)	(((f)->lineinfo) ? (f)->lineinfo[pc] : 0)

#define resethookcount(L)	(L->hookcount = L->basehookcount)


LVI_FUNC void lvG_typeerror (lv_State *L, const TValue *o,
                                             const char *opname);
LVI_FUNC void lvG_concaterror (lv_State *L, StkId p1, StkId p2);
LVI_FUNC void lvG_aritherror (lv_State *L, const TValue *p1,
                                              const TValue *p2);
LVI_FUNC int lvG_ordererror (lv_State *L, const TValue *p1,
                                             const TValue *p2);
LVI_FUNC void lvG_runerror (lv_State *L, const char *fmt, ...);
LVI_FUNC void lvG_errormsg (lv_State *L);
LVI_FUNC int lvG_checkcode (const Proto *pt);
LVI_FUNC int lvG_checkopenop (Instruction i);

#endif
