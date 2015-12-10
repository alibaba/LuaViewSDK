/*
** $Id: lVgc.h,v 2.15.1.1 2007/12/27 13:02:25 roberto Exp $
** Garbage Collector
** See Copyright Notice in lV.h
*/

#ifndef lgc_h
#define lgc_h


#include "lVobject.h"


/*
** Possible states of the Garbage Collector
*/
#define LV_GCSpause	0
#define LV_GCSpropagate	1
#define LV_GCSsweepstring	2
#define LV_GCSsweep	3
#define LV_GCSfinalize	4


/*
** some userful bit tricks
*/
#define resetbits(x,m)	((x) &= cast(lu_byte, ~(m)))
#define setbits(x,m)	((x) |= (m))
#define testbits(x,m)	((x) & (m))
#define bitmask(b)	(1<<(b))
#define bit2mask(b1,b2)	(bitmask(b1) | bitmask(b2))
#define l_setbit(x,b)	setbits(x, bitmask(b))
#define resetbit(x,b)	resetbits(x, bitmask(b))
#define testbit(x,b)	testbits(x, bitmask(b))
#define set2bits(x,b1,b2)	setbits(x, (bit2mask(b1, b2)))
#define reset2bits(x,b1,b2)	resetbits(x, (bit2mask(b1, b2)))
#define test2bits(x,b1,b2)	testbits(x, (bit2mask(b1, b2)))



/*
** Layout for bit use in `marked' field:
** bit 0 - object is white (type 0)
** bit 1 - object is white (type 1)
** bit 2 - object is black
** bit 3 - for userdata: has been finalized
** bit 3 - for tables: has weak keys
** bit 4 - for tables: has weak values
** bit 5 - object is fixed (should not be collected)
** bit 6 - object is "super" fixed (only the main thread)
*/


#define WHITE0BIT	0
#define WHITE1BIT	1
#define BLACKBIT	2
#define FINALIZEDBIT	3
#define KEYWEAKBIT	3
#define VALUEWEAKBIT	4
#define FIXEDBIT	5
#define SFIXEDBIT	6
#define WHITEBITS	bit2mask(WHITE0BIT, WHITE1BIT)


#define iswhite(x)      test2bits((x)->gch.marked, WHITE0BIT, WHITE1BIT)
#define isblack(x)      testbit((x)->gch.marked, BLACKBIT)
#define isgray(x)	(!isblack(x) && !iswhite(x))

#define otherwhite(g)	(g->currentwhite ^ WHITEBITS)
#define isdead(g,v)	((v)->gch.marked & otherwhite(g) & WHITEBITS)

#define changewhite(x)	((x)->gch.marked ^= WHITEBITS)
#define gray2black(x)	l_setbit((x)->gch.marked, BLACKBIT)

#define valiswhite(x)	(iscollectable(x) && iswhite(gcvalue(x)))

#define lvC_white(g)	cast(lu_byte, (g)->currentwhite & WHITEBITS)


#define lvC_checkGC(L) { \
  condhardstacktests(lvD_reallocstack(L, L->stacksize - EXTRA_STACK - 1)); \
  if (G(L)->totalbytes >= G(L)->GCthreshold) \
	lvC_step(L); }


#define lvC_barrier(L,p,v) { if (valiswhite(v) && isblack(obj2gco(p)))  \
	lvC_barrierf(L,obj2gco(p),gcvalue(v)); }

#define lvC_barriert(L,t,v) { if (valiswhite(v) && isblack(obj2gco(t)))  \
	lvC_barrierback(L,t); }

#define lvC_objbarrier(L,p,o)  \
	{ if (iswhite(obj2gco(o)) && isblack(obj2gco(p))) \
		lvC_barrierf(L,obj2gco(p),obj2gco(o)); }

#define lvC_objbarriert(L,t,o)  \
   { if (iswhite(obj2gco(o)) && isblack(obj2gco(t))) lvC_barrierback(L,t); }

LVI_FUNC size_t lvC_separateudata (lv_State *L, int all);
LVI_FUNC void lvC_callGCTM (lv_State *L);
LVI_FUNC void lvC_freeall (lv_State *L);
LVI_FUNC void lvC_step (lv_State *L);
LVI_FUNC void lvC_fullgc (lv_State *L);
LVI_FUNC void lvC_link (lv_State *L, GCObject *o, lu_byte tt);
LVI_FUNC void lvC_linkupval (lv_State *L, UpVal *uv);
LVI_FUNC void lvC_barrierf (lv_State *L, GCObject *o, GCObject *v);
LVI_FUNC void lvC_barrierback (lv_State *L, Table *t);


#endif
