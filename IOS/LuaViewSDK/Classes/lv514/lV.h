/*
** $Id: lV.h,v 1.218.1.5 2008/08/06 13:30:12 roberto Exp $
** [L u a] - An Extensible Extension Language
** [L u a].org, PUC-Rio, Brazil (http://www. l u a .org)
** See Copyright Notice at the end of this file
*/


#ifndef lv_h
#define lv_h

#include <stdarg.h>
#include <stddef.h>


#include "lVconf.h"

#define LUAVIEW_SYS_TABLE_KEY "..::luaview::.."

#define LV_VERSION	"[L u a] 5.1"
#define LV_RELEASE	"[L u a] 5.1.4"
#define LV_VERSION_NUM	501
#define LV_COPYRIGHT	"Copyright (C) 1994-2008 [L u a].org, PUC-Rio"
#define LV_AUTHORS 	"R. Ierusalimschy, L. H. de Figueiredo & W. Celes"


/* mark for precompiled code (`<esc>[L u a]') */
#define	LV_SIGNATURE	"\033L u a"

/* option for multiple returns in `lv_pcall' and `lv_call' */
#define LV_MULTRET	(-1)


/*
** pseudo-indices
*/
#define LV_REGISTRYINDEX	(-10000)
#define LV_ENVIRONINDEX	(-10001)
#define LV_GLOBALSINDEX	(-10002)
#define lv_upvalueindex(i)	(LV_GLOBALSINDEX-(i))


/* thread status; 0 is OK */
#define LV_YIELD	1
#define LV_ERRRUN	2
#define LV_ERRSYNTAX	3
#define LV_ERRMEM	4
#define LV_ERRERR	5


typedef struct lv_State lv_State;

typedef int (*lv_CFunction) (lv_State *L);


/*
** functions that read/write blocks when loading/dumping [L u a] chunks
*/
typedef const char * (*lv_Reader) (lv_State *L, void *ud, size_t *sz);

typedef int (*lv_Writer) (lv_State *L, const void* p, size_t sz, void* ud);


/*
** prototype for memory-allocation functions
*/
typedef void * (*lv_Alloc) (void *ud, void *ptr, size_t osize, size_t nsize);


/*
** basic types
*/
#define LV_TNONE		(-1)

#define LV_TNIL		0
#define LV_TBOOLEAN		1
#define LV_TLIGHTUSERDATA	2
#define LV_TNUMBER		3
#define LV_TSTRING		4
#define LV_TTABLE		5
#define LV_TFUNCTION		6
#define LV_TUSERDATA		7
#define LV_TTHREAD		8



/* minimum [L u a] stack available to a C function */
#define LV_MINSTACK	20


/*
** generic extra include file
*/
#if defined(LV_USER_H)
#include LV_USER_H
#endif


/* type of numbers in [L u a] */
typedef LV_NUMBER lv_Number;


/* type for integer functions */
typedef LV_INTEGER lv_Integer;



/*
** state manipulation
*/
LV_API lv_State *(lv_newstate) (lv_Alloc f, void *ud);
LV_API void       (lv_close) (lv_State *L);
LV_API lv_State *(lv_newthread) (lv_State *L);

LV_API lv_CFunction (lv_atpanic) (lv_State *L, lv_CFunction panicf);


/*
** basic stack manipulation
*/
LV_API int   (lv_gettop) (lv_State *L);
LV_API void  (lv_settop) (lv_State *L, int idx);
LV_API void  (lv_pushvalue) (lv_State *L, int idx);
LV_API void  (lv_remove) (lv_State *L, int idx);
LV_API void  (lv_insert) (lv_State *L, int idx);
LV_API void  (lv_replace) (lv_State *L, int idx);
LV_API int   (lv_checkstack) (lv_State *L, int sz);

LV_API void  (lv_xmove) (lv_State *from, lv_State *to, int n);


/*
** access functions (stack -> C)
*/

LV_API int             (lv_isnumber) (lv_State *L, int idx);
LV_API int             (lv_isstring) (lv_State *L, int idx);
LV_API int             (lv_iscfunction) (lv_State *L, int idx);
LV_API int             (lv_isuserdata) (lv_State *L, int idx);
LV_API int             (lv_type) (lv_State *L, int idx);
LV_API const char     *(lv_typename) (lv_State *L, int tp);

LV_API int            (lv_equal) (lv_State *L, int idx1, int idx2);
LV_API int            (lv_rawequal) (lv_State *L, int idx1, int idx2);
LV_API int            (lv_lessthan) (lv_State *L, int idx1, int idx2);

LV_API lv_Number      (lv_tonumber) (lv_State *L, int idx);
LV_API lv_Integer     (lv_tointeger) (lv_State *L, int idx);
LV_API int             (lv_toboolean) (lv_State *L, int idx);
LV_API const char     *(lv_tolstring) (lv_State *L, int idx, size_t *len);
LV_API size_t          (lv_objlen) (lv_State *L, int idx);
LV_API lv_CFunction   (lv_tocfunction) (lv_State *L, int idx);
LV_API void	       *(lv_touserdata) (lv_State *L, int idx);
LV_API lv_State      *(lv_tothread) (lv_State *L, int idx);
LV_API const void     *(lv_topointer) (lv_State *L, int idx);


/*
** push functions (C -> stack)
*/
LV_API void  (lv_pushnil) (lv_State *L);
LV_API void  (lv_pushnumber) (lv_State *L, lv_Number n);
LV_API void  (lv_pushinteger) (lv_State *L, lv_Integer n);
LV_API void  (lv_pushlstring) (lv_State *L, const char *s, size_t l);
LV_API void  (lv_pushstring) (lv_State *L, const char *s);
LV_API const char *(lv_pushvfstring) (lv_State *L, const char *fmt,
                                                      va_list argp);
LV_API const char *(lv_pushfstring) (lv_State *L, const char *fmt, ...);
LV_API void  (lv_pushcclosure) (lv_State *L, lv_CFunction fn, int n);
LV_API void  (lv_pushboolean) (lv_State *L, int b);
LV_API void  (lv_pushlightuserdata) (lv_State *L, void *p);
LV_API int   (lv_pushthread) (lv_State *L);


/*
** get functions ([L u a] -> stack)
*/
LV_API void  (lv_gettable) (lv_State *L, int idx);
LV_API void  (lv_getfield) (lv_State *L, int idx, const char *k);
LV_API void  (lv_rawget) (lv_State *L, int idx);
LV_API void  (lv_rawgeti) (lv_State *L, int idx, int n);
LV_API void  (lv_createtable) (lv_State *L, int narr, int nrec);
LV_API void *(lv_newuserdata) (lv_State *L, size_t sz);
LV_API int   (lv_getmetatable) (lv_State *L, int objindex);
LV_API void  (lv_getfenv) (lv_State *L, int idx);


/*
** set functions (stack -> [L u a])
*/
LV_API void  (lv_settable) (lv_State *L, int idx);
LV_API void  (lv_setfield) (lv_State *L, int idx, const char *k);
LV_API void  (lv_rawset) (lv_State *L, int idx);
LV_API void  (lv_rawseti) (lv_State *L, int idx, int n);
LV_API int   (lv_setmetatable) (lv_State *L, int objindex);
LV_API int   (lv_setfenv) (lv_State *L, int idx);



LV_API int lv_createUDataLuatable (lv_State *L, int objindex);
LV_API int lv_setUDataLuatable (lv_State *L, int objindex);
LV_API int lv_getUDataLuaTable (lv_State *L, int objindex);

/*
** `load' and `call' functions (load and run [L u a] code)
*/
LV_API void  (lv_call) (lv_State *L, int nargs, int nresults);
LV_API int   (lv_pcall) (lv_State *L, int nargs, int nresults, int errfunc);
LV_API int   (lv_cpcall) (lv_State *L, lv_CFunction func, void *ud);
LV_API int   (lv_load) (lv_State *L, lv_Reader reader, void *dt,
                                        const char *chunkname);

LV_API int (lv_dump) (lv_State *L, lv_Writer writer, void *data);


/*
** coroutine functions
*/
LV_API int  (lv_yield) (lv_State *L, int nresults);
LV_API int  (lv_resume) (lv_State *L, int narg);
LV_API int  (lv_status) (lv_State *L);

/*
** garbage-collection function and options
*/

#define LV_GCSTOP		0
#define LV_GCRESTART		1
#define LV_GCCOLLECT		2
#define LV_GCCOUNT		3
#define LV_GCCOUNTB		4
#define LV_GCSTEP		5
#define LV_GCSETPAUSE		6
#define LV_GCSETSTEPMUL	7

LV_API int (lv_gc) (lv_State *L, int what, int data);


/*
** miscellaneous functions
*/

LV_API int   (lv_error) (lv_State *L);

LV_API int   (lv_next) (lv_State *L, int idx);

LV_API void  (lv_concat) (lv_State *L, int n);

LV_API lv_Alloc (lv_getallocf) (lv_State *L, void **ud);
LV_API void lv_setallocf (lv_State *L, lv_Alloc f, void *ud);



/* 
** ===============================================================
** some useful macros
** ===============================================================
*/

#define lv_pop(L,n)		lv_settop(L, -(n)-1)

#define lv_newtable(L)		lv_createtable(L, 0, 0)

#define lv_register(L,n,f) (lv_pushcfunction(L, (f)), lv_setglobal(L, (n)))

#define lv_pushcfunction(L,f)	lv_pushcclosure(L, (f), 0)

#define lv_strlen(L,i)		lv_objlen(L, (i))

#define lv_isfunction(L,n)	(lv_type(L, (n)) == LV_TFUNCTION)
#define lv_istable(L,n)	(lv_type(L, (n)) == LV_TTABLE)
#define lv_islightuserdata(L,n)	(lv_type(L, (n)) == LV_TLIGHTUSERDATA)
#define lv_isnil(L,n)		(lv_type(L, (n)) == LV_TNIL)
#define lv_isboolean(L,n)	(lv_type(L, (n)) == LV_TBOOLEAN)
#define lv_isthread(L,n)	(lv_type(L, (n)) == LV_TTHREAD)
#define lv_isnone(L,n)		(lv_type(L, (n)) == LV_TNONE)
#define lv_isnoneornil(L, n)	(lv_type(L, (n)) <= 0)

#define lv_pushliteral(L, s)	\
	lv_pushlstring(L, "" s, (sizeof(s)/sizeof(char))-1)

#define lv_setglobal(L,s)	lv_setfield(L, LV_GLOBALSINDEX, (s))
#define lv_getglobal(L,s)	lv_getfield(L, LV_GLOBALSINDEX, (s))

#define lv_tostring(L,i)	lv_tolstring(L, (i), NULL)



/*
** compatibility macros and functions
*/

#define lv_open()	lvL_newstate()

#define lv_getregistry(L)	lv_pushvalue(L, LV_REGISTRYINDEX)

#define lv_getgccount(L)	lv_gc(L, LV_GCCOUNT, 0)

#define lv_Chunkreader		lv_Reader
#define lv_Chunkwriter		lv_Writer


/* hack */
LV_API void lv_setlevel	(lv_State *from, lv_State *to);


/*
** {======================================================================
** Debug API
** =======================================================================
*/


/*
** Event codes
*/
#define LV_HOOKCALL	0
#define LV_HOOKRET	1
#define LV_HOOKLINE	2
#define LV_HOOKCOUNT	3
#define LV_HOOKTAILRET 4


/*
** Event masks
*/
#define LV_MASKCALL	(1 << LV_HOOKCALL)
#define LV_MASKRET	(1 << LV_HOOKRET)
#define LV_MASKLINE	(1 << LV_HOOKLINE)
#define LV_MASKCOUNT	(1 << LV_HOOKCOUNT)

typedef struct lv_Debug lv_Debug;  /* activation record */


/* Functions to be called by the debuger in specific events */
typedef void (*lv_Hook) (lv_State *L, lv_Debug *ar);


LV_API int lv_getstack (lv_State *L, int level, lv_Debug *ar);
LV_API int lv_getinfo (lv_State *L, const char *what, lv_Debug *ar);
LV_API const char *lv_getlocal (lv_State *L, const lv_Debug *ar, int n);
LV_API const char *lv_setlocal (lv_State *L, const lv_Debug *ar, int n);
LV_API const char *lv_getupvalue (lv_State *L, int funcindex, int n);
LV_API const char *lv_setupvalue (lv_State *L, int funcindex, int n);

LV_API int lv_sethook (lv_State *L, lv_Hook func, int mask, int count);
LV_API lv_Hook lv_gethook (lv_State *L);
LV_API int lv_gethookmask (lv_State *L);
LV_API int lv_gethookcount (lv_State *L);


struct lv_Debug {
  int event;
  const char *name;	/* (n) */
  const char *namewhat;	/* (n) `global', `local', `field', `method' */
  const char *what;	/* (S) `[L u a]', `C', `main', `tail' */
  const char *source;	/* (S) */
  int currentline;	/* (l) */
  int nups;		/* (u) number of upvalues */
  int linedefined;	/* (S) */
  int lastlinedefined;	/* (S) */
  char short_src[LV_IDSIZE]; /* (S) */
  /* private part */
  int i_ci;  /* active function */
};

/* }====================================================================== */


/******************************************************************************
* Copyright (C) 1994-2008 [L u a].org, PUC-Rio.  All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining
* a copy of this software and associated documentation files (the
* "Software"), to deal in the Software without restriction, including
* without limitation the rights to use, copy, modify, merge, publish,
* distribute, sublicense, and/or sell copies of the Software, and to
* permit persons to whom the Software is furnished to do so, subject to
* the following conditions:
*
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
* CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
* SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
******************************************************************************/


#endif
