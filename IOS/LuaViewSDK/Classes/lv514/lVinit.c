/*
** $Id: lVinit.c,v 1.14.1.1 2007/12/27 13:02:25 roberto Exp $
** Initialization of libraries for lV.c
** See Copyright Notice in lV.h
*/


#define linit_c
#define LV_LIB

#include "lV.h"

#include "lVlib.h"
#include "lVauxlib.h"


static const lvL_Reg lvlibs[] = {
  {"", lvopen_base},
  {LV_LOADLIBNAME, lvopen_package},
  {LV_TABLIBNAME, lvopen_table},
  {LV_IOLIBNAME, lvopen_io},
  {LV_OSLIBNAME, lvopen_os},
  {LV_STRLIBNAME, lvopen_string},
  {LV_MATHLIBNAME, lvopen_math},
  {LV_DBLIBNAME, lvopen_debug},
  {NULL, NULL}
};


LVLIB_API void lvL_openlibs (lv_State *L) {
  const lvL_Reg *lib = lvlibs;
  for (; lib->func; lib++) {
    lv_pushcfunction(L, lib->func);
    lv_pushstring(L, lib->name);
    lv_call(L, 1, 0);
  }
}

