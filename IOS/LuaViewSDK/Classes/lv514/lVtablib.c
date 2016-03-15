/*
** $Id: lVtablib.c,v 1.38.1.3 2008/02/14 16:46:58 roberto Exp $
** Library for Table Manipulation
** See Copyright Notice in lV.h
*/


#include <stddef.h>

#define ltablib_c
#define LV_LIB

#include "lV.h"

#include "lVauxlib.h"
#include "lVlib.h"

void lv_clearFirstTableValue(lv_State* l);

#define aux_getn(L,n)	(lvL_checktype(L, n, LV_TTABLE), lvL_getn(L, n))


static int foreachi (lv_State *L) {
  lv_clearFirstTableValue(L);
  int i;
  int n = aux_getn(L, 1);
  lvL_checktype(L, 2, LV_TFUNCTION);
  for (i=1; i <= n; i++) {
    lv_pushvalue(L, 2);  /* function */
    lv_pushinteger(L, i);  /* 1st argument */
    lv_rawgeti(L, 1, i);  /* 2nd argument */
    lv_call(L, 2, 1);
    if (!lv_isnil(L, -1))
      return 1;
    lv_pop(L, 1);  /* remove nil result */
  }
  return 0;
}


static int foreach (lv_State *L) {
  lv_clearFirstTableValue(L);
  lvL_checktype(L, 1, LV_TTABLE);
  lvL_checktype(L, 2, LV_TFUNCTION);
  lv_pushnil(L);  /* first key */
  while (lv_next(L, 1)) {
    lv_pushvalue(L, 2);  /* function */
    lv_pushvalue(L, -3);  /* key */
    lv_pushvalue(L, -3);  /* value */
    lv_call(L, 2, 1);
    if (!lv_isnil(L, -1))
      return 1;
    lv_pop(L, 2);  /* remove value and result */
  }
  return 0;
}


static int maxn (lv_State *L) {
  lv_clearFirstTableValue(L);
  lv_Number max = 0;
  lvL_checktype(L, 1, LV_TTABLE);
  lv_pushnil(L);  /* first key */
  while (lv_next(L, 1)) {
    lv_pop(L, 1);  /* remove value */
    if (lv_type(L, -1) == LV_TNUMBER) {
      lv_Number v = lv_tonumber(L, -1);
      if (v > max) max = v;
    }
  }
  lv_pushnumber(L, max);
  return 1;
}


static int getn (lv_State *L) {
  lv_clearFirstTableValue(L);
  lv_pushinteger(L, aux_getn(L, 1));
  return 1;
}


static int setn (lv_State *L) {
  lv_clearFirstTableValue(L);
  lvL_checktype(L, 1, LV_TTABLE);
#ifndef lvL_setn
  lvL_setn(L, 1, lvL_checkint(L, 2));
#else
  lvL_error(L, LV_QL("setn") " is obsolete");
#endif
  lv_pushvalue(L, 1);
  return 1;
}


static int tinsert (lv_State *L) {
  lv_clearFirstTableValue(L);
  int e = aux_getn(L, 1) + 1;  /* first empty element */
  int pos;  /* where to insert new element */
  switch (lv_gettop(L)) {
    case 2: {  /* called with only 2 arguments */
      pos = e;  /* insert new element at the end */
      break;
    }
    case 3: {
      int i;
      pos = lvL_checkint(L, 2);  /* 2nd argument is the position */
      if (pos > e) e = pos;  /* `grow' array if necessary */
      for (i = e; i > pos; i--) {  /* move up elements */
        lv_rawgeti(L, 1, i-1);
        lv_rawseti(L, 1, i);  /* t[i] = t[i-1] */
      }
      break;
    }
    default: {
      return lvL_error(L, "wrong number of arguments to " LV_QL("insert"));
    }
  }
  lvL_setn(L, 1, e);  /* new size */
  lv_rawseti(L, 1, pos);  /* t[pos] = v */
  return 0;
}


static int tremove (lv_State *L) {
  lv_clearFirstTableValue(L);
  int e = aux_getn(L, 1);
  int pos = lvL_optint(L, 2, e);
  if (!(1 <= pos && pos <= e))  /* position is outside bounds? */
   return 0;  /* nothing to remove */
  lvL_setn(L, 1, e - 1);  /* t.n = n-1 */
  lv_rawgeti(L, 1, pos);  /* result = t[pos] */
  for ( ;pos<e; pos++) {
    lv_rawgeti(L, 1, pos+1);
    lv_rawseti(L, 1, pos);  /* t[pos] = t[pos+1] */
  }
  lv_pushnil(L);
  lv_rawseti(L, 1, e);  /* t[e] = nil */
  return 1;
}


static void addfield (lv_State *L, lvL_Buffer *b, int i) {
  lv_rawgeti(L, 1, i);
  if (!lv_isstring(L, -1))
    lvL_error(L, "invalid value (%s) at index %d in table for "
                  LV_QL("concat"), lvL_typename(L, -1), i);
    lvL_addvalue(b);
}


static int tconcat (lv_State *L) {
  lv_clearFirstTableValue(L);
  lvL_Buffer b;
  size_t lsep;
  int i, last;
  const char *sep = lvL_optlstring(L, 2, "", &lsep);
  lvL_checktype(L, 1, LV_TTABLE);
  i = lvL_optint(L, 3, 1);
  last = lvL_opt(L, lvL_checkint, 4, lvL_getn(L, 1));
  lvL_buffinit(L, &b);
  for (; i < last; i++) {
    addfield(L, &b, i);
    lvL_addlstring(&b, sep, lsep);
  }
  if (i == last)  /* add last value (if interval was not empty) */
    addfield(L, &b, i);
  lvL_pushresult(&b);
  return 1;
}



/*
** {======================================================
** Quicksort
** (based on `Algorithms in MODULA-3', Robert Sedgewick;
**  Addison-Wesley, 1993.)
*/


static void set2 (lv_State *L, int i, int j) {
  lv_rawseti(L, 1, i);
  lv_rawseti(L, 1, j);
}

static int sort_comp (lv_State *L, int a, int b) {
  if (!lv_isnil(L, 2)) {  /* function? */
    int res;
    lv_pushvalue(L, 2);
    lv_pushvalue(L, a-1);  /* -1 to compensate function */
    lv_pushvalue(L, b-2);  /* -2 to compensate function and `a' */
    lv_call(L, 2, 1);
    res = lv_toboolean(L, -1);
    lv_pop(L, 1);
    return res;
  }
  else  /* a < b? */
    return lv_lessthan(L, a, b);
}

static void auxsort (lv_State *L, int l, int u) {
  while (l < u) {  /* for tail recursion */
    int i, j;
    /* sort elements a[l], a[(l+u)/2] and a[u] */
    lv_rawgeti(L, 1, l);
    lv_rawgeti(L, 1, u);
    if (sort_comp(L, -1, -2))  /* a[u] < a[l]? */
      set2(L, l, u);  /* swap a[l] - a[u] */
    else
      lv_pop(L, 2);
    if (u-l == 1) break;  /* only 2 elements */
    i = (l+u)/2;
    lv_rawgeti(L, 1, i);
    lv_rawgeti(L, 1, l);
    if (sort_comp(L, -2, -1))  /* a[i]<a[l]? */
      set2(L, i, l);
    else {
      lv_pop(L, 1);  /* remove a[l] */
      lv_rawgeti(L, 1, u);
      if (sort_comp(L, -1, -2))  /* a[u]<a[i]? */
        set2(L, i, u);
      else
        lv_pop(L, 2);
    }
    if (u-l == 2) break;  /* only 3 elements */
    lv_rawgeti(L, 1, i);  /* Pivot */
    lv_pushvalue(L, -1);
    lv_rawgeti(L, 1, u-1);
    set2(L, i, u-1);
    /* a[l] <= P == a[u-1] <= a[u], only need to sort from l+1 to u-2 */
    i = l; j = u-1;
    for (;;) {  /* invariant: a[l..i] <= P <= a[j..u] */
      /* repeat ++i until a[i] >= P */
      while (lv_rawgeti(L, 1, ++i), sort_comp(L, -1, -2)) {
        if (i>u) lvL_error(L, "invalid order function for sorting");
        lv_pop(L, 1);  /* remove a[i] */
      }
      /* repeat --j until a[j] <= P */
      while (lv_rawgeti(L, 1, --j), sort_comp(L, -3, -1)) {
        if (j<l) lvL_error(L, "invalid order function for sorting");
        lv_pop(L, 1);  /* remove a[j] */
      }
      if (j<i) {
        lv_pop(L, 3);  /* pop pivot, a[i], a[j] */
        break;
      }
      set2(L, i, j);
    }
    lv_rawgeti(L, 1, u-1);
    lv_rawgeti(L, 1, i);
    set2(L, u-1, i);  /* swap pivot (a[u-1]) with a[i] */
    /* a[l..i-1] <= a[i] == P <= a[i+1..u] */
    /* adjust so that smaller half is in [j..i] and larger one in [l..u] */
    if (i-l < u-i) {
      j=l; i=i-1; l=i+2;
    }
    else {
      j=i+1; i=u; u=j-2;
    }
    auxsort(L, j, i);  /* call recursively the smaller one */
  }  /* repeat the routine for the larger one */
}

static int sort (lv_State *L) {
  lv_clearFirstTableValue(L);
  int n = aux_getn(L, 1);
  lvL_checkstack(L, 40, "");  /* assume array is smaller than 2^40 */
  if (!lv_isnoneornil(L, 2))  /* is there a 2nd argument? */
    lvL_checktype(L, 2, LV_TFUNCTION);
  lv_settop(L, 2);  /* make sure there is two arguments */
  auxsort(L, 1, n);
  return 0;
}

/* }====================================================== */


static const lvL_Reg tab_funcs[] = {
  {"concat", tconcat},
  {"foreach", foreach},
  {"foreachi", foreachi},
  {"getn", getn},
  {"maxn", maxn},
  {"insert", tinsert},
  {"remove", tremove},
  {"setn", setn},
  {"sort", sort},
  {LUAVIEW_SYS_TABLE_KEY, getn},
  {NULL, NULL}
};


LVLIB_API int lvopen_table (lv_State *L) {
  lvL_register(L, LV_TABLIBNAME, tab_funcs);
  return 1;
}

