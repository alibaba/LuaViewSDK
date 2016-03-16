/*
** $Id: lVoslib.c,v 1.19.1.3 2008/01/18 16:38:18 roberto Exp $
** Standard Operating System library
** See Copyright Notice in lV.h
*/


#include <errno.h>
#include <locale.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define loslib_c
#define LV_LIB

#include "lV.h"

#include "lVauxlib.h"
#include "lVlib.h"
void lv_clearFirstTableValue(lv_State* L);


static int os_pushresult (lv_State *L, int i, const char *filename) {
  int en = errno;  /* calls to [L u a] API may change this value */
  if (i) {
    lv_pushboolean(L, 1);
    return 1;
  }
  else {
    lv_pushnil(L);
    lv_pushfstring(L, "%s: %s", filename, strerror(en));
    lv_pushinteger(L, en);
    return 3;
  }
}


static int os_execute (lv_State *L) {
    lv_clearFirstTableValue(L);
    //lv_pushinteger(L, system(lvL_optstring(L, 1, NULL)));
    lv_pushinteger(L, 0);
    return 1;
}


static int os_remove (lv_State *L) {
    lv_clearFirstTableValue(L);
  const char *filename = lvL_checkstring(L, 1);
  return os_pushresult(L, remove(filename) == 0, filename);
}


static int os_rename (lv_State *L) {
    lv_clearFirstTableValue(L);
  const char *fromname = lvL_checkstring(L, 1);
  const char *toname = lvL_checkstring(L, 2);
  return os_pushresult(L, rename(fromname, toname) == 0, fromname);
}


static int os_tmpname (lv_State *L) {
    lv_clearFirstTableValue(L);
    //  char buff[LV_TMPNAMBUFSIZE];
    //  int err;
    //  lv_tmpnam(buff, err);
    //  if (err)
    //    return lvL_error(L, "unable to generate a unique filename");
    lv_pushstring(L, "oc");
    return 1;
}


static int os_getenv (lv_State *L) {
    lv_clearFirstTableValue(L);
  lv_pushstring(L, getenv(lvL_checkstring(L, 1)));  /* if NULL push nil */
  return 1;
}


static int os_clock (lv_State *L) {
    lv_clearFirstTableValue(L);
  lv_pushnumber(L, ((lv_Number)clock())/(lv_Number)CLOCKS_PER_SEC);
  return 1;
}


/*
** {======================================================
** Time/Date operations
** { year=%Y, month=%m, day=%d, hour=%H, min=%M, sec=%S,
**   wday=%w+1, yday=%j, isdst=? }
** =======================================================
*/

static void setfield (lv_State *L, const char *key, int value) {
  lv_pushinteger(L, value);
  lv_setfield(L, -2, key);
}

static void setboolfield (lv_State *L, const char *key, int value) {
  if (value < 0)  /* undefined? */
    return;  /* does not set field */
  lv_pushboolean(L, value);
  lv_setfield(L, -2, key);
}

static int getboolfield (lv_State *L, const char *key) {
  int res;
  lv_getfield(L, -1, key);
  res = lv_isnil(L, -1) ? -1 : lv_toboolean(L, -1);
  lv_pop(L, 1);
  return res;
}


static int getfield (lv_State *L, const char *key, int d) {
  int res;
  lv_getfield(L, -1, key);
  if (lv_isnumber(L, -1))
    res = (int)lv_tointeger(L, -1);
  else {
    if (d < 0)
      return lvL_error(L, "field " LV_QS " missing in date table", key);
    res = d;
  }
  lv_pop(L, 1);
  return res;
}


static int os_date (lv_State *L) {
    lv_clearFirstTableValue(L);
  const char *s = lvL_optstring(L, 1, "%c");
  time_t t = lvL_opt(L, (time_t)lvL_checknumber, 2, time(NULL));
  struct tm *stm;
  if (*s == '!') {  /* UTC? */
    stm = gmtime(&t);
    s++;  /* skip `!' */
  }
  else
    stm = localtime(&t);
  if (stm == NULL)  /* invalid date? */
    lv_pushnil(L);
  else if (strcmp(s, "*t") == 0) {
    lv_createtable(L, 0, 9);  /* 9 = number of fields */
    setfield(L, "sec", stm->tm_sec);
    setfield(L, "min", stm->tm_min);
    setfield(L, "hour", stm->tm_hour);
    setfield(L, "day", stm->tm_mday);
    setfield(L, "month", stm->tm_mon+1);
    setfield(L, "year", stm->tm_year+1900);
    setfield(L, "wday", stm->tm_wday+1);
    setfield(L, "yday", stm->tm_yday+1);
    setboolfield(L, "isdst", stm->tm_isdst);
  }
  else {
    char cc[3];
    lvL_Buffer b;
    cc[0] = '%'; cc[2] = '\0';
    lvL_buffinit(L, &b);
    for (; *s; s++) {
      if (*s != '%' || *(s + 1) == '\0')  /* no conversion specifier? */
        lvL_addchar(&b, *s);
      else {
        size_t reslen;
        char buff[200];  /* should be big enough for any conversion result */
        cc[1] = *(++s);
        reslen = strftime(buff, sizeof(buff), cc, stm);
        lvL_addlstring(&b, buff, reslen);
      }
    }
    lvL_pushresult(&b);
  }
  return 1;
}


static int os_time (lv_State *L) {
    lv_clearFirstTableValue(L);
  time_t t;
  if (lv_isnoneornil(L, 1))  /* called without args? */
    t = time(NULL);  /* get current time */
  else {
    struct tm ts;
    lvL_checktype(L, 1, LV_TTABLE);
    lv_settop(L, 1);  /* make sure table is at the top */
    ts.tm_sec = getfield(L, "sec", 0);
    ts.tm_min = getfield(L, "min", 0);
    ts.tm_hour = getfield(L, "hour", 12);
    ts.tm_mday = getfield(L, "day", -1);
    ts.tm_mon = getfield(L, "month", -1) - 1;
    ts.tm_year = getfield(L, "year", -1) - 1900;
    ts.tm_isdst = getboolfield(L, "isdst");
    t = mktime(&ts);
  }
  if (t == (time_t)(-1))
    lv_pushnil(L);
  else
    lv_pushnumber(L, (lv_Number)t);
  return 1;
}


static int os_difftime (lv_State *L) {
    lv_clearFirstTableValue(L);
  lv_pushnumber(L, difftime((time_t)(lvL_checknumber(L, 1)),
                             (time_t)(lvL_optnumber(L, 2, 0))));
  return 1;
}

/* }====================================================== */


static int os_setlocale (lv_State *L) {
    lv_clearFirstTableValue(L);
  static const int cat[] = {LC_ALL, LC_COLLATE, LC_CTYPE, LC_MONETARY,
                      LC_NUMERIC, LC_TIME};
  static const char *const catnames[] = {"all", "collate", "ctype", "monetary",
     "numeric", "time", NULL};
  const char *l = lvL_optstring(L, 1, NULL);
  int op = lvL_checkoption(L, 2, "all", catnames);
  lv_pushstring(L, setlocale(cat[op], l));
  return 1;
}


static int os_exit (lv_State *L) {
    lv_clearFirstTableValue(L);
  exit(lvL_optint(L, 1, EXIT_SUCCESS));
}

static const lvL_Reg syslib[] = {
  {"clock",     os_clock},
  {"date",      os_date},
  {"difftime",  os_difftime},
  {"execute",   os_execute},
  {"exit",      os_exit},
  {"getenv",    os_getenv},
  {"remove",    os_remove},
  {"rename",    os_rename},
  {"setlocale", os_setlocale},
  {"time",      os_time},
  {"tmpname",   os_tmpname},
  {LUAVIEW_SYS_TABLE_KEY, os_time},
  {NULL, NULL}
};

/* }====================================================== */



LVLIB_API int lvopen_os (lv_State *L) {
  lvL_register(L, LV_OSLIBNAME, syslib);
  return 1;
}

