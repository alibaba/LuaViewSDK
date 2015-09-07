/*
** $Id: lVlimits.h,v 1.69.1.1 2007/12/27 13:02:25 roberto Exp $
** Limits, basic types, and some other `installation-dependent' definitions
** See Copyright Notice in lV.h
*/

#ifndef llimits_h
#define llimits_h


#include <limits.h>
#include <stddef.h>


#include "lV.h"


typedef LVI_UINT32 lu_int32;

typedef LVI_UMEM lu_mem;

typedef LVI_MEM l_mem;



/* chars used as small naturals (so that `char' is reserved for characters) */
typedef unsigned char lu_byte;


#define MAX_SIZET	((size_t)(~(size_t)0)-2)

#define MAX_LUMEM	((lu_mem)(~(lu_mem)0)-2)


#define MAX_INT (INT_MAX-2)  /* maximum value of an int (-2 for safety) */

/*
** conversion of pointer to integer
** this is for hashing only; there is no problem if the integer
** cannot hold the whole pointer value
*/
#define IntPoint(p)  ((unsigned int)(lu_mem)(p))



/* type to ensure maximum alignment */
typedef LVI_USER_ALIGNMENT_T L_Umaxalign;


/* result of a `usual argument conversion' over lv_Number */
typedef LVI_UACNUMBER l_uacNumber;


/* internal assertions for in-house debugging */
#ifdef lv_assert

#define check_exp(c,e)		(lv_assert(c), (e))
#define api_check(l,e)		lv_assert(e)

#else

#define lv_assert(c)		((void)0)
#define check_exp(c,e)		(e)
#define api_check		lvi_apicheck

#endif


#ifndef UNUSED
#define UNUSED(x)	((void)(x))	/* to avoid warnings */
#endif


#ifndef cast
#define cast(t, exp)	((t)(exp))
#endif

#define cast_byte(i)	cast(lu_byte, (i))
#define cast_num(i)	cast(lv_Number, (i))
#define cast_int(i)	cast(int, (i))



/*
** type for virtual-machine instructions
** must be an unsigned with (at least) 4 bytes (see details in lVopcodes.h)
*/
typedef lu_int32 Instruction;



/* maximum stack for a [L u a] function */
#define MAXSTACK	250



/* minimum size for the string table (must be power of 2) */
#ifndef MINSTRTABSIZE
#define MINSTRTABSIZE	32
#endif


/* minimum size for string buffer */
#ifndef LV_MINBUFFER
#define LV_MINBUFFER	32
#endif


#ifndef lv_lock
#define lv_lock(L)     ((void) 0) 
#define lv_unlock(L)   ((void) 0)
#endif

#ifndef lvi_threadyield
#define lvi_threadyield(L)     {lv_unlock(L); lv_lock(L);}
#endif


/*
** macro to control inclusion of some hard tests on stack reallocation
*/ 
#ifndef HARDSTACKTESTS
#define condhardstacktests(x)	((void)0)
#else
#define condhardstacktests(x)	x
#endif

#endif
