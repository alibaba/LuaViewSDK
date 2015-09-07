/*
** $Id: lVmem.h,v 1.31.1.1 2007/12/27 13:02:25 roberto Exp $
** Interface to Memory Manager
** See Copyright Notice in lV.h
*/

#ifndef lmem_h
#define lmem_h


#include <stddef.h>

#include "lVlimits.h"
#include "lV.h"

#define MEMERRMSG	"not enough memory"


#define lvM_reallocv(L,b,on,n,e) \
	((cast(size_t, (n)+1) <= MAX_SIZET/(e)) ?  /* +1 to avoid warnings */ \
		lvM_realloc_(L, (b), (on)*(e), (n)*(e)) : \
		lvM_toobig(L))

#define lvM_freemem(L, b, s)	lvM_realloc_(L, (b), (s), 0)
#define lvM_free(L, b)		lvM_realloc_(L, (b), sizeof(*(b)), 0)
#define lvM_freearray(L, b, n, t)   lvM_reallocv(L, (b), n, 0, sizeof(t))

#define lvM_malloc(L,t)	lvM_realloc_(L, NULL, 0, (t))
#define lvM_new(L,t)		cast(t *, lvM_malloc(L, sizeof(t)))
#define lvM_newvector(L,n,t) \
		cast(t *, lvM_reallocv(L, NULL, 0, n, sizeof(t)))

#define lvM_growvector(L,v,nelems,size,t,limit,e) \
          if ((nelems)+1 > (size)) \
            ((v)=cast(t *, lvM_growaux_(L,v,&(size),sizeof(t),limit,e)))

#define lvM_reallocvector(L, v,oldn,n,t) \
   ((v)=cast(t *, lvM_reallocv(L, v, oldn, n, sizeof(t))))


LVI_FUNC void *lvM_realloc_ (lv_State *L, void *block, size_t oldsize,
                                                          size_t size);
LVI_FUNC void *lvM_toobig (lv_State *L);
LVI_FUNC void *lvM_growaux_ (lv_State *L, void *block, int *size,
                               size_t size_elem, int limit,
                               const char *errormsg);

#endif

