/*
** $Id: lVauxlib.h,v 1.88.1.1 2007/12/27 13:02:25 roberto Exp $
** Auxiliary functions for building [L u a] libraries
** See Copyright Notice in lV.h
*/


#ifndef lauxlib_h
#define lauxlib_h


#include <stddef.h>
#include <stdio.h>

#include "lV.h"


#if defined(LV_COMPAT_GETN)
LVLIB_API int (lvL_getn) (lv_State *L, int t);
LVLIB_API void (lvL_setn) (lv_State *L, int t, int n);
#else
#define lvL_getn(L,i)          ((int)lv_objlen(L, i))
#define lvL_setn(L,i,j)        ((void)0)  /* no op! */
#endif

#if defined(LV_COMPAT_OPENLIB)
#define lvI_openlib	lvL_openlib
#endif


/* extra error code for `lvL_load' */
#define LV_ERRFILE     (LV_ERRERR+1)


typedef struct lvL_Reg {
  const char *name;
  lv_CFunction func;
} lvL_Reg;



LVLIB_API void (lvI_openlib) (lv_State *L, const char *libname,
                                const lvL_Reg *l, int nup);
LVLIB_API void (lvL_register) (lv_State *L, const char *libname,
                                const lvL_Reg *l);
LVLIB_API int (lvL_getmetafield) (lv_State *L, int obj, const char *e);
LVLIB_API int (lvL_callmeta) (lv_State *L, int obj, const char *e);
LVLIB_API int (lvL_typerror) (lv_State *L, int narg, const char *tname);
LVLIB_API int (lvL_argerror) (lv_State *L, int numarg, const char *extramsg);
LVLIB_API const char *(lvL_checklstring) (lv_State *L, int numArg,
                                                          size_t *l);
LVLIB_API const char *(lvL_optlstring) (lv_State *L, int numArg,
                                          const char *def, size_t *l);
LVLIB_API lv_Number (lvL_checknumber) (lv_State *L, int numArg);
LVLIB_API lv_Number (lvL_optnumber) (lv_State *L, int nArg, lv_Number def);

LVLIB_API lv_Integer (lvL_checkinteger) (lv_State *L, int numArg);
LVLIB_API lv_Integer (lvL_optinteger) (lv_State *L, int nArg,
                                          lv_Integer def);


LVLIB_API lv_Integer (lvL_checkbool) (lv_State *L, int narg);

LVLIB_API void (lvL_checkstack) (lv_State *L, int sz, const char *msg);
LVLIB_API void (lvL_checktype) (lv_State *L, int narg, int t);
LVLIB_API void (lvL_checkany) (lv_State *L, int narg);

LVLIB_API int   (lvL_newmetatable) (lv_State *L, const char *tname);
LVLIB_API void *(lvL_checkudata) (lv_State *L, int ud, const char *tname);

LVLIB_API void (lvL_where) (lv_State *L, int lvl);
LVLIB_API int (lvL_error) (lv_State *L, const char *fmt, ...);

LVLIB_API int (lvL_checkoption) (lv_State *L, int narg, const char *def,
                                   const char *const lst[]);

LVLIB_API int (lvL_ref) (lv_State *L, int t);
LVLIB_API void (lvL_unref) (lv_State *L, int t, int ref);

LVLIB_API int (lvL_loadfile) (lv_State *L, const char *filename);
LVLIB_API int (lvL_loadbuffer) (lv_State *L, const char *buff, size_t sz,
                                  const char *name);
LVLIB_API int (lvL_loadstring) (lv_State *L, const char *s);

LVLIB_API lv_State *(lvL_newstate) (void);


LVLIB_API const char *(lvL_gsub) (lv_State *L, const char *s, const char *p,
                                                  const char *r);

LVLIB_API const char *(lvL_findtable) (lv_State *L, int idx,
                                         const char *fname, int szhint);




/*
** ===============================================================
** some useful macros
** ===============================================================
*/

#define lvL_argcheck(L, cond,numarg,extramsg)	\
		((void)((cond) || lvL_argerror(L, (numarg), (extramsg))))
#define lvL_checkstring(L,n)	(lvL_checklstring(L, (n), NULL))
#define lvL_optstring(L,n,d)	(lvL_optlstring(L, (n), (d), NULL))
#define lvL_checkint(L,n)	((int)lvL_checkinteger(L, (n)))
#define lvL_optint(L,n,d)	((int)lvL_optinteger(L, (n), (d)))
#define lvL_checklong(L,n)	((long)lvL_checkinteger(L, (n)))
#define lvL_optlong(L,n,d)	((long)lvL_optinteger(L, (n), (d)))

#define lvL_typename(L,i)	lv_typename(L, lv_type(L,(i)))

#define lvL_dofile(L, fn) \
	(lvL_loadfile(L, fn) || lv_pcall(L, 0, LV_MULTRET, 0))

#define lvL_dostring(L, s) \
	(lvL_loadstring(L, s) || lv_pcall(L, 0, LV_MULTRET, 0))

#define lvL_getmetatable(L,n)	(lv_getfield(L, LV_REGISTRYINDEX, (n)))

#define lvL_opt(L,f,n,d)	(lv_isnoneornil(L,(n)) ? (d) : f(L,(n)))

/*
** {======================================================
** Generic Buffer manipulation
** =======================================================
*/



typedef struct lvL_Buffer {
  char *p;			/* current position in buffer */
  int lvl;  /* number of strings in the stack (level) */
  lv_State *L;
  char buffer[LVL_BUFFERSIZE];
} lvL_Buffer;

#define lvL_addchar(B,c) \
  ((void)((B)->p < ((B)->buffer+LVL_BUFFERSIZE) || lvL_prepbuffer(B)), \
   (*(B)->p++ = (char)(c)))

/* compatibility only */
#define lvL_putchar(B,c)	lvL_addchar(B,c)

#define lvL_addsize(B,n)	((B)->p += (n))

LVLIB_API void (lvL_buffinit) (lv_State *L, lvL_Buffer *B);
LVLIB_API char *(lvL_prepbuffer) (lvL_Buffer *B);
LVLIB_API void (lvL_addlstring) (lvL_Buffer *B, const char *s, size_t l);
LVLIB_API void (lvL_addstring) (lvL_Buffer *B, const char *s);
LVLIB_API void (lvL_addvalue) (lvL_Buffer *B);
LVLIB_API void (lvL_pushresult) (lvL_Buffer *B);


/* }====================================================== */


/* compatibility with ref system */

/* pre-defined references */
#define LV_NOREF       (-2)
#define LV_REFNIL      (-1)

#define lv_ref(L,lock) ((lock) ? lvL_ref(L, LV_REGISTRYINDEX) : \
      (lv_pushstring(L, "unlocked references are obsolete"), lv_error(L), 0))

#define lv_unref(L,ref)        lvL_unref(L, LV_REGISTRYINDEX, (ref))

#define lv_getref(L,ref)       lv_rawgeti(L, LV_REGISTRYINDEX, (ref))


#define lvL_reg	lvL_Reg

#endif


