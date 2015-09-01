/*
** $Id: lVauxlib.c,v 1.159.1.3 2008/01/21 13:20:51 roberto Exp $
** Auxiliary functions for building [L u a] libraries
** See Copyright Notice in lV.h
*/


#include <ctype.h>
#include <errno.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>


/* This file uses only the official API of [L u a].
** Any function declared here could be written as an application function.
*/

#define lauxlib_c
#define LV_LIB

#include "lV.h"

#include "lVauxlib.h"


#define FREELIST_REF	0	/* free list of references */


/* convert a stack index to positive */
#define abs_index(L, i)		((i) > 0 || (i) <= LV_REGISTRYINDEX ? (i) : \
					lv_gettop(L) + (i) + 1)


/*
** {======================================================
** Error-report functions
** =======================================================
*/


LVLIB_API int lvL_argerror (lv_State *L, int narg, const char *extramsg) {
  lv_Debug ar;
  if (!lv_getstack(L, 0, &ar))  /* no stack frame? */
    return lvL_error(L, "bad argument #%d (%s)", narg, extramsg);
  lv_getinfo(L, "n", &ar);
  if (strcmp(ar.namewhat, "method") == 0) {
    narg--;  /* do not count `self' */
    if (narg == 0)  /* error is in the self argument itself? */
      return lvL_error(L, "calling " LV_QS " on bad self (%s)",
                           ar.name, extramsg);
  }
  if (ar.name == NULL)
    ar.name = "?";
  return lvL_error(L, "bad argument #%d to " LV_QS " (%s)",
                        narg, ar.name, extramsg);
}


LVLIB_API int lvL_typerror (lv_State *L, int narg, const char *tname) {
  const char *msg = lv_pushfstring(L, "%s expected, got %s",
                                    tname, lvL_typename(L, narg));
  return lvL_argerror(L, narg, msg);
}


static void tag_error (lv_State *L, int narg, int tag) {
  lvL_typerror(L, narg, lv_typename(L, tag));
}


LVLIB_API void lvL_where (lv_State *L, int level) {
  lv_Debug ar;
  if (lv_getstack(L, level, &ar)) {  /* check function at level */
    lv_getinfo(L, "Sl", &ar);  /* get info about it */
    if (ar.currentline > 0) {  /* is there info? */
      lv_pushfstring(L, "%s:%d: ", ar.short_src, ar.currentline);
      return;
    }
  }
  lv_pushliteral(L, "");  /* else, no information available... */
}


LVLIB_API int lvL_error (lv_State *L, const char *fmt, ...) {
  va_list argp;
  va_start(argp, fmt);
  lvL_where(L, 1);
  lv_pushvfstring(L, fmt, argp);
  va_end(argp);
  lv_concat(L, 2);
  return lv_error(L);
}

/* }====================================================== */


LVLIB_API int lvL_checkoption (lv_State *L, int narg, const char *def,
                                 const char *const lst[]) {
  const char *name = (def) ? lvL_optstring(L, narg, def) :
                             lvL_checkstring(L, narg);
  int i;
  for (i=0; lst[i]; i++)
    if (strcmp(lst[i], name) == 0)
      return i;
  return lvL_argerror(L, narg,
                       lv_pushfstring(L, "invalid option " LV_QS, name));
}


LVLIB_API int lvL_newmetatable (lv_State *L, const char *tname) {
  lv_getfield(L, LV_REGISTRYINDEX, tname);  /* get registry.name */
  if (!lv_isnil(L, -1))  /* name already in use? */
    return 0;  /* leave previous value on top, but return 0 */
  lv_pop(L, 1);
  lv_newtable(L);  /* create metatable */
  lv_pushvalue(L, -1);
  lv_setfield(L, LV_REGISTRYINDEX, tname);  /* registry.name = metatable */
  return 1;
}


LVLIB_API void *lvL_checkudata (lv_State *L, int ud, const char *tname) {
  void *p = lv_touserdata(L, ud);
  if (p != NULL) {  /* value is a userdata? */
    if (lv_getmetatable(L, ud)) {  /* does it have a metatable? */
      lv_getfield(L, LV_REGISTRYINDEX, tname);  /* get correct metatable */
      if (lv_rawequal(L, -1, -2)) {  /* does it have the correct mt? */
        lv_pop(L, 2);  /* remove both metatables */
        return p;
      }
    }
  }
  lvL_typerror(L, ud, tname);  /* else error */
  return NULL;  /* to avoid warnings */
}


LVLIB_API void lvL_checkstack (lv_State *L, int space, const char *mes) {
  if (!lv_checkstack(L, space))
    lvL_error(L, "stack overflow (%s)", mes);
}


LVLIB_API void lvL_checktype (lv_State *L, int narg, int t) {
  if (lv_type(L, narg) != t)
    tag_error(L, narg, t);
}


LVLIB_API void lvL_checkany (lv_State *L, int narg) {
  if (lv_type(L, narg) == LV_TNONE)
    lvL_argerror(L, narg, "value expected");
}


LVLIB_API const char *lvL_checklstring (lv_State *L, int narg, size_t *len) {
  const char *s = lv_tolstring(L, narg, len);
  if (!s) tag_error(L, narg, LV_TSTRING);
  return s;
}


LVLIB_API const char *lvL_optlstring (lv_State *L, int narg,
                                        const char *def, size_t *len) {
  if (lv_isnoneornil(L, narg)) {
    if (len)
      *len = (def ? strlen(def) : 0);
    return def;
  }
  else return lvL_checklstring(L, narg, len);
}


LVLIB_API lv_Number lvL_checknumber (lv_State *L, int narg) {
  lv_Number d = lv_tonumber(L, narg);
  if (d == 0 && !lv_isnumber(L, narg))  /* avoid extra test when d is not 0 */
    tag_error(L, narg, LV_TNUMBER);
  return d;
}


LVLIB_API lv_Number lvL_optnumber (lv_State *L, int narg, lv_Number def) {
  return lvL_opt(L, lvL_checknumber, narg, def);
}


LVLIB_API lv_Integer lvL_checkinteger (lv_State *L, int narg) {
    lv_Integer d = lv_tointeger(L, narg);
    if (d == 0 && !lv_isnumber(L, narg))  /* avoid extra test when d is not 0 */
        tag_error(L, narg, LV_TNUMBER);
    return d;
}

LVLIB_API lv_Integer lvL_checkbool (lv_State *L, int narg) {
    int d = lv_toboolean( L, narg);
    if ( !lv_isboolean( L, narg))  /* avoid extra test when d is not 0 */
        tag_error(L, narg, LV_TNUMBER);
    return d;
}


LVLIB_API lv_Integer lvL_optinteger (lv_State *L, int narg,
                                                      lv_Integer def) {
  return lvL_opt(L, lvL_checkinteger, narg, def);
}


LVLIB_API int lvL_getmetafield (lv_State *L, int obj, const char *event) {
  if (!lv_getmetatable(L, obj))  /* no metatable? */
    return 0;
  lv_pushstring(L, event);
  lv_rawget(L, -2);
  if (lv_isnil(L, -1)) {
    lv_pop(L, 2);  /* remove metatable and metafield */
    return 0;
  }
  else {
    lv_remove(L, -2);  /* remove only metatable */
    return 1;
  }
}


LVLIB_API int lvL_callmeta (lv_State *L, int obj, const char *event) {
  obj = abs_index(L, obj);
  if (!lvL_getmetafield(L, obj, event))  /* no metafield? */
    return 0;
  lv_pushvalue(L, obj);
  lv_call(L, 1, 1);
  return 1;
}


LVLIB_API void (lvL_register) (lv_State *L, const char *libname,
                                const lvL_Reg *l) {
  lvI_openlib(L, libname, l, 0);
}


static int libsize (const lvL_Reg *l) {
  int size = 0;
  for (; l->name; l++) size++;
  return size;
}


LVLIB_API void lvI_openlib (lv_State *L, const char *libname,
                              const lvL_Reg *l, int nup) {
  if (libname) {
    int size = libsize(l);
    /* check whether lib already exists */
    lvL_findtable(L, LV_REGISTRYINDEX, "_LOADED", 1);
    lv_getfield(L, -1, libname);  /* get _LOADED[libname] */
    if (!lv_istable(L, -1)) {  /* not found? */
      lv_pop(L, 1);  /* remove previous result */
      /* try global variable (and create one if it does not exist) */
      if (lvL_findtable(L, LV_GLOBALSINDEX, libname, size) != NULL)
        lvL_error(L, "name conflict for module " LV_QS, libname);
      lv_pushvalue(L, -1);
      lv_setfield(L, -3, libname);  /* _LOADED[libname] = new table */
    }
    lv_remove(L, -2);  /* remove _LOADED table */
    lv_insert(L, -(nup+1));  /* move library table to below upvalues */
  }
  for (; l->name; l++) {
    int i;
    for (i=0; i<nup; i++)  /* copy upvalues to the top */
      lv_pushvalue(L, -nup);
    lv_pushcclosure(L, l->func, nup);
    lv_setfield(L, -(nup+2), l->name);
  }
  lv_pop(L, nup);  /* remove upvalues */
}



/*
** {======================================================
** getn-setn: size for arrays
** =======================================================
*/

#if defined(LV_COMPAT_GETN)

static int checkint (lv_State *L, int topop) {
  int n = (lv_type(L, -1) == LV_TNUMBER) ? lv_tointeger(L, -1) : -1;
  lv_pop(L, topop);
  return n;
}


static void getsizes (lv_State *L) {
  lv_getfield(L, LV_REGISTRYINDEX, "LV_SIZES");
  if (lv_isnil(L, -1)) {  /* no `size' table? */
    lv_pop(L, 1);  /* remove nil */
    lv_newtable(L);  /* create it */
    lv_pushvalue(L, -1);  /* `size' will be its own metatable */
    lv_setmetatable(L, -2);
    lv_pushliteral(L, "kv");
    lv_setfield(L, -2, "__mode");  /* metatable(N).__mode = "kv" */
    lv_pushvalue(L, -1);
    lv_setfield(L, LV_REGISTRYINDEX, "LV_SIZES");  /* store in register */
  }
}


LVLIB_API void lvL_setn (lv_State *L, int t, int n) {
  t = abs_index(L, t);
  lv_pushliteral(L, "n");
  lv_rawget(L, t);
  if (checkint(L, 1) >= 0) {  /* is there a numeric field `n'? */
    lv_pushliteral(L, "n");  /* use it */
    lv_pushinteger(L, n);
    lv_rawset(L, t);
  }
  else {  /* use `sizes' */
    getsizes(L);
    lv_pushvalue(L, t);
    lv_pushinteger(L, n);
    lv_rawset(L, -3);  /* sizes[t] = n */
    lv_pop(L, 1);  /* remove `sizes' */
  }
}


LVLIB_API int lvL_getn (lv_State *L, int t) {
  int n;
  t = abs_index(L, t);
  lv_pushliteral(L, "n");  /* try t.n */
  lv_rawget(L, t);
  if ((n = checkint(L, 1)) >= 0) return n;
  getsizes(L);  /* else try sizes[t] */
  lv_pushvalue(L, t);
  lv_rawget(L, -2);
  if ((n = checkint(L, 2)) >= 0) return n;
  return (int)lv_objlen(L, t);
}

#endif

/* }====================================================== */



LVLIB_API const char *lvL_gsub (lv_State *L, const char *s, const char *p,
                                                               const char *r) {
  const char *wild;
  size_t l = strlen(p);
  lvL_Buffer b;
  lvL_buffinit(L, &b);
  while ((wild = strstr(s, p)) != NULL) {
    lvL_addlstring(&b, s, wild - s);  /* push prefix */
    lvL_addstring(&b, r);  /* push replacement in place of pattern */
    s = wild + l;  /* continue after `p' */
  }
  lvL_addstring(&b, s);  /* push last suffix */
  lvL_pushresult(&b);
  return lv_tostring(L, -1);
}


LVLIB_API const char *lvL_findtable (lv_State *L, int idx,
                                       const char *fname, int szhint) {
  const char *e;
  lv_pushvalue(L, idx);
  do {
    e = strchr(fname, '.');
    if (e == NULL) e = fname + strlen(fname);
    lv_pushlstring(L, fname, e - fname);
    lv_rawget(L, -2);
    if (lv_isnil(L, -1)) {  /* no such field? */
      lv_pop(L, 1);  /* remove this nil */
      lv_createtable(L, 0, (*e == '.' ? 1 : szhint)); /* new table for field */
      lv_pushlstring(L, fname, e - fname);
      lv_pushvalue(L, -2);
      lv_settable(L, -4);  /* set new table into field */
    }
    else if (!lv_istable(L, -1)) {  /* field has a non-table value? */
      lv_pop(L, 2);  /* remove table and value */
      return fname;  /* return problematic part of the name */
    }
    lv_remove(L, -2);  /* remove previous table */
    fname = e + 1;
  } while (*e == '.');
  return NULL;
}



/*
** {======================================================
** Generic Buffer manipulation
** =======================================================
*/


#define bufflen(B)	((B)->p - (B)->buffer)
#define bufffree(B)	((size_t)(LVL_BUFFERSIZE - bufflen(B)))

#define LIMIT	(LV_MINSTACK/2)


static int emptybuffer (lvL_Buffer *B) {
  size_t l = bufflen(B);
  if (l == 0) return 0;  /* put nothing on stack */
  else {
    lv_pushlstring(B->L, B->buffer, l);
    B->p = B->buffer;
    B->lvl++;
    return 1;
  }
}


static void adjuststack (lvL_Buffer *B) {
  if (B->lvl > 1) {
    lv_State *L = B->L;
    int toget = 1;  /* number of levels to concat */
    size_t toplen = lv_strlen(L, -1);
    do {
      size_t l = lv_strlen(L, -(toget+1));
      if (B->lvl - toget + 1 >= LIMIT || toplen > l) {
        toplen += l;
        toget++;
      }
      else break;
    } while (toget < B->lvl);
    lv_concat(L, toget);
    B->lvl = B->lvl - toget + 1;
  }
}


LVLIB_API char *lvL_prepbuffer (lvL_Buffer *B) {
  if (emptybuffer(B))
    adjuststack(B);
  return B->buffer;
}


LVLIB_API void lvL_addlstring (lvL_Buffer *B, const char *s, size_t l) {
  while (l--)
    lvL_addchar(B, *s++);
}


LVLIB_API void lvL_addstring (lvL_Buffer *B, const char *s) {
  lvL_addlstring(B, s, strlen(s));
}


LVLIB_API void lvL_pushresult (lvL_Buffer *B) {
  emptybuffer(B);
  lv_concat(B->L, B->lvl);
  B->lvl = 1;
}


LVLIB_API void lvL_addvalue (lvL_Buffer *B) {
  lv_State *L = B->L;
  size_t vl;
  const char *s = lv_tolstring(L, -1, &vl);
  if (vl <= bufffree(B)) {  /* fit into buffer? */
    memcpy(B->p, s, vl);  /* put it there */
    B->p += vl;
    lv_pop(L, 1);  /* remove from stack */
  }
  else {
    if (emptybuffer(B))
      lv_insert(L, -2);  /* put buffer before new value */
    B->lvl++;  /* add new value into B stack */
    adjuststack(B);
  }
}


LVLIB_API void lvL_buffinit (lv_State *L, lvL_Buffer *B) {
  B->L = L;
  B->p = B->buffer;
  B->lvl = 0;
}

/* }====================================================== */


LVLIB_API int lvL_ref (lv_State *L, int t) {
  int ref;
  t = abs_index(L, t);
  if (lv_isnil(L, -1)) {
    lv_pop(L, 1);  /* remove from stack */
    return LV_REFNIL;  /* `nil' has a unique fixed reference */
  }
  lv_rawgeti(L, t, FREELIST_REF);  /* get first free element */
  ref = (int)lv_tointeger(L, -1);  /* ref = t[FREELIST_REF] */
  lv_pop(L, 1);  /* remove it from stack */
  if (ref != 0) {  /* any free element? */
    lv_rawgeti(L, t, ref);  /* remove it from list */
    lv_rawseti(L, t, FREELIST_REF);  /* (t[FREELIST_REF] = t[ref]) */
  }
  else {  /* no free elements */
    ref = (int)lv_objlen(L, t);
    ref++;  /* create new reference */
  }
  lv_rawseti(L, t, ref);
  return ref;
}


LVLIB_API void lvL_unref (lv_State *L, int t, int ref) {
  if (ref >= 0) {
    t = abs_index(L, t);
    lv_rawgeti(L, t, FREELIST_REF);
    lv_rawseti(L, t, ref);  /* t[ref] = t[FREELIST_REF] */
    lv_pushinteger(L, ref);
    lv_rawseti(L, t, FREELIST_REF);  /* t[FREELIST_REF] = ref */
  }
}



/*
** {======================================================
** Load functions
** =======================================================
*/

typedef struct LoadF {
  int extraline;
  FILE *f;
  char buff[LVL_BUFFERSIZE];
} LoadF;


static const char *getF (lv_State *L, void *ud, size_t *size) {
  LoadF *lf = (LoadF *)ud;
  (void)L;
  if (lf->extraline) {
    lf->extraline = 0;
    *size = 1;
    return "\n";
  }
  if (feof(lf->f)) return NULL;
  *size = fread(lf->buff, 1, sizeof(lf->buff), lf->f);
  return (*size > 0) ? lf->buff : NULL;
}


static int errfile (lv_State *L, const char *what, int fnameindex) {
  const char *serr = strerror(errno);
  const char *filename = lv_tostring(L, fnameindex) + 1;
  lv_pushfstring(L, "cannot %s %s: %s", what, filename, serr);
  lv_remove(L, fnameindex);
  return LV_ERRFILE;
}


LVLIB_API int lvL_loadfile (lv_State *L, const char *filename) {
  LoadF lf;
  int status, readstatus;
  int c;
  int fnameindex = lv_gettop(L) + 1;  /* index of filename on the stack */
  lf.extraline = 0;
  if (filename == NULL) {
    lv_pushliteral(L, "=stdin");
    lf.f = stdin;
  }
  else {
    lv_pushfstring(L, "@%s", filename);
    lf.f = fopen(filename, "r");
    if (lf.f == NULL) return errfile(L, "open", fnameindex);
  }
  c = getc(lf.f);
  if (c == '#') {  /* Unix exec. file? */
    lf.extraline = 1;
    while ((c = getc(lf.f)) != EOF && c != '\n') ;  /* skip first line */
    if (c == '\n') c = getc(lf.f);
  }
  if (c == LV_SIGNATURE[0] && filename) {  /* binary file? */
    lf.f = freopen(filename, "rb", lf.f);  /* reopen in binary mode */
    if (lf.f == NULL) return errfile(L, "reopen", fnameindex);
    /* skip eventual `#!...' */
      while ((c = getc(lf.f)) != EOF && c != LV_SIGNATURE[0]){
          ;
      }
    lf.extraline = 0;
  }
  ungetc(c, lf.f);
  status = lv_load(L, getF, &lf, lv_tostring(L, -1));
  readstatus = ferror(lf.f);
  if (filename) fclose(lf.f);  /* close file (even in case of errors) */
  if (readstatus) {
    lv_settop(L, fnameindex);  /* ignore results from `lv_load' */
    return errfile(L, "read", fnameindex);
  }
  lv_remove(L, fnameindex);
  return status;
}


typedef struct LoadS {
  const char *s;
  size_t size;
} LoadS;


static const char *getS (lv_State *L, void *ud, size_t *size) {
  LoadS *ls = (LoadS *)ud;
  (void)L;
  if (ls->size == 0) return NULL;
  *size = ls->size;
  ls->size = 0;
  return ls->s;
}


LVLIB_API int lvL_loadbuffer (lv_State *L, const char *buff, size_t size,
                                const char *name) {
  LoadS ls;
  ls.s = buff;
  ls.size = size;
  return lv_load(L, getS, &ls, name);
}


LVLIB_API int (lvL_loadstring) (lv_State *L, const char *s) {
  return lvL_loadbuffer(L, s, strlen(s), s);
}



/* }====================================================== */
int g_totalSize = 0;

static void *l_alloc (void *ud, void *ptr, size_t osize, size_t nsize) {
  (void)ud;
  (void)osize;
  if (nsize == 0) {
    free(ptr);
      g_totalSize -= osize;
    return NULL;
  }
  else {
      g_totalSize -= osize;
      g_totalSize += nsize;
    return realloc(ptr, nsize);
  }
}


static int panic (lv_State *L) {
  (void)L;  /* to avoid warnings */
  fprintf(stderr, "PANIC: unprotected error in call to [L u a] API (%s)\n",
                   lv_tostring(L, -1));
  return 0;
}


LVLIB_API lv_State *lvL_newstate (void) {
  lv_State *L = lv_newstate(l_alloc, NULL);
  if (L) lv_atpanic(L, &panic);
  return L;
}

