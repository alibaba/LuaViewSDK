/*
** $Id: lVtm.h,v 2.6.1.1 2007/12/27 13:02:25 roberto Exp $
** Tag methods
** See Copyright Notice in lV.h
*/

#ifndef ltm_h
#define ltm_h


#include "lVobject.h"


/*
* WARNING: if you change the order of this enumeration,
* grep "ORDER TM"
*/
typedef enum {
  TM_INDEX,
  TM_NEWINDEX,
  TM_GC,
  TM_MODE,
  TM_EQ,  /* last tag method with `fast' access */
  TM_ADD,
  TM_SUB,
  TM_MUL,
  TM_DIV,
  TM_MOD,
  TM_POW,
  TM_UNM,
  TM_LEN,
  TM_LT,
  TM_LE,
  TM_CONCAT,
  TM_CALL,
  TM_N		/* number of elements in the enum */
} TMS;



#define gfasttm(g,et,e) ((et) == NULL ? NULL : \
  ((et)->flags & (1u<<(e))) ? NULL : lvT_gettm(et, e, (g)->tmname[e]))

#define fasttm(l,et,e)	gfasttm(G(l), et, e)

LVI_DATA const char *const lvT_typenames[];


LVI_FUNC const TValue *lvT_gettm (Table *events, TMS event, TString *ename);
LVI_FUNC const TValue *lvT_gettmbyobj (lv_State *L, const TValue *o,
                                                       TMS event);
LVI_FUNC void lvT_init (lv_State *L);

#endif
