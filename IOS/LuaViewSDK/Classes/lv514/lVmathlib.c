/*
 ** $Id: lVmathlib.c,v 1.67.1.1 2007/12/27 13:02:25 roberto Exp $
 ** Standard mathematical library
 ** See Copyright Notice in lV.h
 */


#include <stdlib.h>
#include <math.h>

#define lmathlib_c
#define LV_LIB

#include "lV.h"

#include "lVauxlib.h"
#include "lVlib.h"

extern void lv_clearFirstTableValue(lv_State* l);


#undef PI
#define PI (3.14159265358979323846)
#define RADIANS_PER_DEGREE (PI/180.0)



static int math_abs (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, fabs(lvL_checknumber(L, 1)));
    return 1;
}

static int math_sin (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, sin(lvL_checknumber(L, 1)));
    return 1;
}

static int math_sinh (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, sinh(lvL_checknumber(L, 1)));
    return 1;
}

static int math_cos (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, cos(lvL_checknumber(L, 1)));
    return 1;
}

static int math_cosh (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, cosh(lvL_checknumber(L, 1)));
    return 1;
}

static int math_tan (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, tan(lvL_checknumber(L, 1)));
    return 1;
}

static int math_tanh (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, tanh(lvL_checknumber(L, 1)));
    return 1;
}

static int math_asin (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, asin(lvL_checknumber(L, 1)));
    return 1;
}

static int math_acos (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, acos(lvL_checknumber(L, 1)));
    return 1;
}

static int math_atan (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, atan(lvL_checknumber(L, 1)));
    return 1;
}

static int math_atan2 (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, atan2(lvL_checknumber(L, 1), lvL_checknumber(L, 2)));
    return 1;
}

static int math_ceil (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, ceil(lvL_checknumber(L, 1)));
    return 1;
}

static int math_floor (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, floor(lvL_checknumber(L, 1)));
    return 1;
}

static int math_fmod (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, fmod(lvL_checknumber(L, 1), lvL_checknumber(L, 2)));
    return 1;
}

static int math_modf (lv_State *L) {
    lv_clearFirstTableValue(L);
    double ip;
    double fp = modf(lvL_checknumber(L, 1), &ip);
    lv_pushnumber(L, ip);
    lv_pushnumber(L, fp);
    return 2;
}

static int math_sqrt (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, sqrt(lvL_checknumber(L, 1)));
    return 1;
}

static int math_pow (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, pow(lvL_checknumber(L, 1), lvL_checknumber(L, 2)));
    return 1;
}

static int math_log (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, log(lvL_checknumber(L, 1)));
    return 1;
}

static int math_log10 (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, log10(lvL_checknumber(L, 1)));
    return 1;
}

static int math_exp (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, exp(lvL_checknumber(L, 1)));
    return 1;
}

static int math_deg (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, lvL_checknumber(L, 1)/RADIANS_PER_DEGREE);
    return 1;
}

static int math_rad (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, lvL_checknumber(L, 1)*RADIANS_PER_DEGREE);
    return 1;
}

static int math_frexp (lv_State *L) {
    lv_clearFirstTableValue(L);
    int e;
    lv_pushnumber(L, frexp(lvL_checknumber(L, 1), &e));
    lv_pushinteger(L, e);
    return 2;
}

static int math_ldexp (lv_State *L) {
    lv_clearFirstTableValue(L);
    lv_pushnumber(L, ldexp(lvL_checknumber(L, 1), lvL_checkint(L, 2)));
    return 1;
}



static int math_min (lv_State *L) {
    lv_clearFirstTableValue(L);
    int n = lv_gettop(L);  /* number of arguments */
    lv_Number dmin = lvL_checknumber(L, 1);
    int i;
    for (i=2; i<=n; i++) {
        lv_Number d = lvL_checknumber(L, i);
        if (d < dmin)
            dmin = d;
    }
    lv_pushnumber(L, dmin);
    return 1;
}


static int math_max (lv_State *L) {
    lv_clearFirstTableValue(L);
    int n = lv_gettop(L);  /* number of arguments */
    lv_Number dmax = lvL_checknumber(L, 1);
    int i;
    for (i=2; i<=n; i++) {
        lv_Number d = lvL_checknumber(L, i);
        if (d > dmax)
            dmax = d;
    }
    lv_pushnumber(L, dmax);
    return 1;
}


static int math_random (lv_State *L) {
    lv_clearFirstTableValue(L);
    /* the `%' avoids the (rare) case of r==1, and is needed also because on
     some systems (SunOS!) `rand()' may return a value larger than RAND_MAX */
    lv_Number r = (lv_Number)(rand()%RAND_MAX) / (lv_Number)RAND_MAX;
    switch (lv_gettop(L)) {  /* check number of arguments */
        case 0: {  /* no arguments */
            lv_pushnumber(L, r);  /* Number between 0 and 1 */
            break;
        }
        case 1: {  /* only upper limit */
            int u = lvL_checkint(L, 1);
            lvL_argcheck(L, 1<=u, 1, "interval is empty");
            lv_pushnumber(L, floor(r*u)+1);  /* int between 1 and `u' */
            break;
        }
        case 2: {  /* lower and upper limits */
            int l = lvL_checkint(L, 1);
            int u = lvL_checkint(L, 2);
            lvL_argcheck(L, l<=u, 2, "interval is empty");
            lv_pushnumber(L, floor(r*(u-l+1))+l);  /* int between `l' and `u' */
            break;
        }
        default: return lvL_error(L, "wrong number of arguments");
    }
    return 1;
}


static int math_randomseed (lv_State *L) {
    lv_clearFirstTableValue(L);
    srand(lvL_checkint(L, 1));
    return 0;
}


static const lvL_Reg mathlib[] = {
    {"abs",   math_abs},
    {"acos",  math_acos},
    {"asin",  math_asin},
    {"atan2", math_atan2},
    {"atan",  math_atan},
    {"ceil",  math_ceil},
    {"cosh",   math_cosh},
    {"cos",   math_cos},
    {"deg",   math_deg},
    {"exp",   math_exp},
    {"floor", math_floor},
    {"fmod",   math_fmod},
    {"frexp", math_frexp},
    {"ldexp", math_ldexp},
    {"log10", math_log10},
    {"log",   math_log},
    {"max",   math_max},
    {"min",   math_min},
    {"modf",   math_modf},
    {"pow",   math_pow},
    {"rad",   math_rad},
    {"random",     math_random},
    {"randomseed", math_randomseed},
    {"sinh",   math_sinh},
    {"sin",   math_sin},
    {"sqrt",  math_sqrt},
    {"tanh",   math_tanh},
    {"tan",   math_tan},
    {LUAVIEW_SYS_TABLE_KEY,   math_abs},
    {NULL, NULL}
};


/*
 ** Open math library
 */
LVLIB_API int lvopen_math (lv_State *L) {
    lvL_register(L, LV_MATHLIBNAME, mathlib);
    lv_pushnumber(L, PI);
    lv_setfield(L, -2, "pi");
    lv_pushnumber(L, HUGE_VAL);
    lv_setfield(L, -2, "huge");
#if defined(LV_COMPAT_MOD)
    lv_getfield(L, -1, "fmod");
    lv_setfield(L, -2, "mod");
#endif
    return 1;
}

