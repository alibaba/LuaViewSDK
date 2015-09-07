/*
** $Id: lVtm.c,v 2.8.1.1 2007/12/27 13:02:25 roberto Exp $
** Tag methods
** See Copyright Notice in lV.h
*/


#include <string.h>

#define ltm_c
#define LV_CORE

#include "lV.h"

#include "lVobject.h"
#include "lVstate.h"
#include "lVstring.h"
#include "lVtable.h"
#include "lVtm.h"



const char *const lvT_typenames[] = {
  "nil", "boolean", "userdata", "number",
  "string", "table", "function", "userdata", "thread",
  "proto", "upval"
};


void lvT_init (lv_State *L) {
  static const char *const lvT_eventname[] = {  /* ORDER TM */
    "__index", "__newindex",
    "__gc", "__mode", "__eq",
    "__add", "__sub", "__mul", "__div", "__mod",
    "__pow", "__unm", "__len", "__lt", "__le",
    "__concat", "__call"
  };
  int i;
  for (i=0; i<TM_N; i++) {
    G(L)->tmname[i] = lvS_new(L, lvT_eventname[i]);
    lvS_fix(G(L)->tmname[i]);  /* never collect these names */
  }
}


/*
** function to be used with macro "fasttm": optimized for absence of
** tag methods
*/
const TValue *lvT_gettm (Table *events, TMS event, TString *ename) {
  const TValue *tm = lvH_getstr(events, ename);
  lv_assert(event <= TM_EQ);
  if (ttisnil(tm)) {  /* no tag method? */
    events->flags |= cast_byte(1u<<event);  /* cache this fact */
    return NULL;
  }
  else return tm;
}


const TValue *lvT_gettmbyobj (lv_State *L, const TValue *o, TMS event) {
  Table *mt;
  switch (ttype(o)) {
    case LV_TTABLE:
      mt = hvalue(o)->metatable;
      break;
    case LV_TUSERDATA:
      mt = uvalue(o)->metatable;
      break;
    default:
      mt = G(L)->mt[ttype(o)];
  }
  return (mt ? lvH_getstr(mt, G(L)->tmname[event]) : lvO_nilobject);
}

