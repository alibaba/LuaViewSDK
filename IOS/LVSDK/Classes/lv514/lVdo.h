/*
** $Id: lVdo.h,v 2.7.1.1 2007/12/27 13:02:25 roberto Exp $
** Stack and Call structure of [L u a]
** See Copyright Notice in lV.h
*/

#ifndef ldo_h
#define ldo_h


#include "lVobject.h"
#include "lVstate.h"
#include "lVzio.h"


#define lvD_checkstack(L,n)	\
  if ((char *)L->stack_last - (char *)L->top <= (n)*(int)sizeof(TValue)) \
    lvD_growstack(L, n); \
  else condhardstacktests(lvD_reallocstack(L, L->stacksize - EXTRA_STACK - 1));


#define incr_top(L) {lvD_checkstack(L,1); L->top++;}

#define savestack(L,p)		((char *)(p) - (char *)L->stack)
#define restorestack(L,n)	((TValue *)((char *)L->stack + (n)))

#define saveci(L,p)		((char *)(p) - (char *)L->base_ci)
#define restoreci(L,n)		((CallInfo *)((char *)L->base_ci + (n)))


/* results from lvD_precall */
#define PCR_L_U_A		0	/* initiated a call to a [L u a] function */
#define PCRC		1	/* did a call to a C function */
#define PCRYIELD	2	/* C funtion yielded */


/* type of protected functions, to be ran by `runprotected' */
typedef void (*Pfunc) (lv_State *L, void *ud);

LVI_FUNC int lvD_protectedparser (lv_State *L, ZIO *z, const char *name);
LVI_FUNC void lvD_callhook (lv_State *L, int event, int line);
LVI_FUNC int lvD_precall (lv_State *L, StkId func, int nresults);
LVI_FUNC void lvD_call (lv_State *L, StkId func, int nResults);
LVI_FUNC int lvD_pcall (lv_State *L, Pfunc func, void *u,
                                        ptrdiff_t oldtop, ptrdiff_t ef);
LVI_FUNC int lvD_poscall (lv_State *L, StkId firstResult);
LVI_FUNC void lvD_reallocCI (lv_State *L, int newsize);
LVI_FUNC void lvD_reallocstack (lv_State *L, int newsize);
LVI_FUNC void lvD_growstack (lv_State *L, int n);

LVI_FUNC void lvD_throw (lv_State *L, int errcode);
LVI_FUNC int lvD_rawrunprotected (lv_State *L, Pfunc f, void *ud);

LVI_FUNC void lvD_seterrorobj (lv_State *L, int errcode, StkId oldtop);

#endif

