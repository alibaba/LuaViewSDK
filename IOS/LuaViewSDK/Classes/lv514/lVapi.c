/*
 ** $Id: lVapi.c,v 2.55.1.5 2008/07/04 18:41:18 roberto Exp $
 ** [L u a] API
 ** See Copyright Notice in lV.h
 */


#include <assert.h>
#include <math.h>
#include <stdarg.h>
#include <string.h>
#include <stdio.h>

#define lapi_c
#define LV_CORE

#include "lV.h"

#include "lVapi.h"
#include "lVdebug.h"
#include "lVdo.h"
#include "lVfunc.h"
#include "lVgc.h"
#include "lVmem.h"
#include "lVobject.h"
#include "lVstate.h"
#include "lVstring.h"
#include "lVtable.h"
#include "lVtm.h"
#include "lVundump.h"
#include "lVvm.h"



const char lv_ident[] =
"$[L u a]: " LV_RELEASE " " LV_COPYRIGHT " $\n"
"$Authors: " LV_AUTHORS " $\n"
"$URL: www. l u a .org $\n";



#define api_checknelems(L, n)	api_check(L, (n) <= (L->top - L->base))

#define api_checkvalidindex(L, i)	api_check(L, (i) != lvO_nilobject)

#define api_incr_top(L)   {api_check(L, L->top < L->ci->top); L->top++;}



static TValue *index2adr (lv_State *L, int idx) {
    if (idx > 0) {
        TValue *o = L->base + (idx - 1);
        api_check(L, idx <= L->ci->top - L->base);
        if (o >= L->top) return cast(TValue *, lvO_nilobject);
        else return o;
    }
    else if (idx > LV_REGISTRYINDEX) {
        api_check(L, idx != 0 && -idx <= L->top - L->base);
        return L->top + idx;
    }
    else switch (idx) {  /* pseudo-indices */
        case LV_REGISTRYINDEX: return registry(L);
        case LV_ENVIRONINDEX: {
            Closure *func = curr_func(L);
            sethvalue(L, &L->env, func->c.env);
            return &L->env;
        }
        case LV_GLOBALSINDEX: return gt(L);
        default: {
            Closure *func = curr_func(L);
            idx = LV_GLOBALSINDEX - idx;
            return (idx <= func->c.nupvalues)
            ? &func->c.upvalue[idx-1]
            : cast(TValue *, lvO_nilobject);
        }
    }
}


static Table *getcurrenv (lv_State *L) {
    if (L->ci == L->base_ci)  /* no enclosing function? */
        return hvalue(gt(L));  /* use global table as environment */
    else {
        Closure *func = curr_func(L);
        return func->c.env;
    }
}


void lvA_pushobject (lv_State *L, const TValue *o) {
    setobj2s(L, L->top, o);
    api_incr_top(L);
}


LV_API int lv_checkstack (lv_State *L, int size) {
    int res = 1;
    lv_lock(L);
    if (size > LVI_MAXCSTACK || (L->top - L->base + size) > LVI_MAXCSTACK)
        res = 0;  /* stack overflow */
    else if (size > 0) {
        lvD_checkstack(L, size);
        if (L->ci->top < L->top + size)
            L->ci->top = L->top + size;
    }
    lv_unlock(L);
    return res;
}


LV_API void lv_xmove (lv_State *from, lv_State *to, int n) {
    int i;
    if (from == to) return;
    lv_lock(to);
    api_checknelems(from, n);
    api_check(from, G(from) == G(to));
    api_check(from, to->ci->top - to->top >= n);
    from->top -= n;
    for (i = 0; i < n; i++) {
        setobj2s(to, to->top++, from->top + i);
    }
    lv_unlock(to);
}


LV_API void lv_setlevel (lv_State *from, lv_State *to) {
    to->nCcalls = from->nCcalls;
}


LV_API lv_CFunction lv_atpanic (lv_State *L, lv_CFunction panicf) {
    lv_CFunction old;
    lv_lock(L);
    old = G(L)->panic;
    G(L)->panic = panicf;
    lv_unlock(L);
    return old;
}


LV_API lv_State *lv_newthread (lv_State *L) {
    lv_State *L1;
    lv_lock(L);
    lvC_checkGC(L);
    L1 = lvE_newthread(L);
    setthvalue(L, L->top, L1);
    api_incr_top(L);
    lv_unlock(L);
    lvi_userstatethread(L, L1);
    return L1;
}



/*
 ** basic stack manipulation
 */


LV_API int lv_gettop (lv_State *L) {
    return cast_int(L->top - L->base);
}


LV_API void lv_settop (lv_State *L, int idx) {
    lv_lock(L);
    if (idx >= 0) {
        api_check(L, idx <= L->stack_last - L->base);
        while (L->top < L->base + idx)
            setnilvalue(L->top++);
        L->top = L->base + idx;
    }
    else {
        api_check(L, -(idx+1) <= (L->top - L->base));
        L->top += idx+1;  /* `subtract' index (index is negative) */
    }
    lv_unlock(L);
}


LV_API void lv_remove (lv_State *L, int idx) {
    StkId p;
    lv_lock(L);
    p = index2adr(L, idx);
    api_checkvalidindex(L, p);
    while (++p < L->top) setobjs2s(L, p-1, p);
    L->top--;
    lv_unlock(L);
}


LV_API void lv_insert (lv_State *L, int idx) {
    StkId p;
    StkId q;
    lv_lock(L);
    p = index2adr(L, idx);
    api_checkvalidindex(L, p);
    for (q = L->top; q>p; q--) setobjs2s(L, q, q-1);
    setobjs2s(L, p, L->top);
    lv_unlock(L);
}


LV_API void lv_replace (lv_State *L, int idx) {
    StkId o;
    lv_lock(L);
    /* explicit test for incompatible code */
    if (idx == LV_ENVIRONINDEX && L->ci == L->base_ci)
        lvG_runerror(L, "no calling environment");
    api_checknelems(L, 1);
    o = index2adr(L, idx);
    api_checkvalidindex(L, o);
    if (idx == LV_ENVIRONINDEX) {
        Closure *func = curr_func(L);
        api_check(L, ttistable(L->top - 1));
        func->c.env = hvalue(L->top - 1);
        lvC_barrier(L, func, L->top - 1);
    }
    else {
        setobj(L, o, L->top - 1);
        if (idx < LV_GLOBALSINDEX)  /* function upvalue? */
            lvC_barrier(L, curr_func(L), L->top - 1);
    }
    L->top--;
    lv_unlock(L);
}


LV_API void lv_pushvalue (lv_State *L, int idx) {
    lv_lock(L);
    setobj2s(L, L->top, index2adr(L, idx));
    api_incr_top(L);
    lv_unlock(L);
}



/*
 ** access functions (stack -> C)
 */


LV_API int lv_type (lv_State *L, int idx) {
    StkId o = index2adr(L, idx);
    return (o == lvO_nilobject) ? LV_TNONE : ttype(o);
}


LV_API const char *lv_typename (lv_State *L, int t) {
    UNUSED(L);
    return (t == LV_TNONE) ? "no value" : lvT_typenames[t];
}


LV_API int lv_iscfunction (lv_State *L, int idx) {
    StkId o = index2adr(L, idx);
    return iscfunction(o);
}


LV_API int lv_isnumber (lv_State *L, int idx) {
    TValue n;
    const TValue *o = index2adr(L, idx);
    return tonumber(o, &n);
}


LV_API int lv_isstring (lv_State *L, int idx) {
    int t = lv_type(L, idx);
    return (t == LV_TSTRING || t == LV_TNUMBER);
}


LV_API int lv_isuserdata (lv_State *L, int idx) {
    const TValue *o = index2adr(L, idx);
    return (ttisuserdata(o) || ttislightuserdata(o));
}


LV_API int lv_rawequal (lv_State *L, int index1, int index2) {
    StkId o1 = index2adr(L, index1);
    StkId o2 = index2adr(L, index2);
    return (o1 == lvO_nilobject || o2 == lvO_nilobject) ? 0
    : lvO_rawequalObj(o1, o2);
}


LV_API int lv_equal (lv_State *L, int index1, int index2) {
    StkId o1, o2;
    int i;
    lv_lock(L);  /* may call tag method */
    o1 = index2adr(L, index1);
    o2 = index2adr(L, index2);
    i = (o1 == lvO_nilobject || o2 == lvO_nilobject) ? 0 : equalobj(L, o1, o2);
    lv_unlock(L);
    return i;
}


LV_API int lv_lessthan (lv_State *L, int index1, int index2) {
    StkId o1, o2;
    int i;
    lv_lock(L);  /* may call tag method */
    o1 = index2adr(L, index1);
    o2 = index2adr(L, index2);
    i = (o1 == lvO_nilobject || o2 == lvO_nilobject) ? 0
    : lvV_lessthan(L, o1, o2);
    lv_unlock(L);
    return i;
}



LV_API lv_Number lv_tonumber (lv_State *L, int idx) {
    TValue n;
    const TValue *o = index2adr(L, idx);
    if (tonumber(o, &n))
        return nvalue(o);
    else
        return 0;
}


LV_API lv_Integer lv_tointeger (lv_State *L, int idx) {
    TValue n;
    const TValue *o = index2adr(L, idx);
    if (tonumber(o, &n)) {
        lv_Integer res;
        lv_Number num = nvalue(o);
        lv_number2integer(res, num);
        return res;
    }
    else
        return 0;
}


LV_API int lv_toboolean (lv_State *L, int idx) {
    const TValue *o = index2adr(L, idx);
    return !l_isfalse(o);
}


LV_API const char *lv_tolstring (lv_State *L, int idx, size_t *len) {
    StkId o = index2adr(L, idx);
    if (!ttisstring(o)) {
        lv_lock(L);  /* `lvV_tostring' may create a new string */
        if (!lvV_tostring(L, o)) {  /* conversion failed? */
            if (len != NULL) *len = 0;
            lv_unlock(L);
            return NULL;
        }
        lvC_checkGC(L);
        o = index2adr(L, idx);  /* previous call may reallocate the stack */
        lv_unlock(L);
    }
    if (len != NULL) *len = tsvalue(o)->len;
    return svalue(o);
}


LV_API size_t lv_objlen (lv_State *L, int idx) {
    StkId o = index2adr(L, idx);
    switch (ttype(o)) {
        case LV_TSTRING: return tsvalue(o)->len;
        case LV_TUSERDATA: return uvalue(o)->len;
        case LV_TTABLE: return lvH_getn(hvalue(o));
        case LV_TNUMBER: {
            size_t l;
            lv_lock(L);  /* `lvV_tostring' may create a new string */
            l = (lvV_tostring(L, o) ? tsvalue(o)->len : 0);
            lv_unlock(L);
            return l;
        }
        default: return 0;
    }
}


LV_API lv_CFunction lv_tocfunction (lv_State *L, int idx) {
    StkId o = index2adr(L, idx);
    return (!iscfunction(o)) ? NULL : clvalue(o)->c.f;
}


LV_API void *lv_touserdata (lv_State *L, int idx) {
    StkId o = index2adr(L, idx);
    switch (ttype(o)) {
        case LV_TUSERDATA: return (rawuvalue(o) + 1);
        case LV_TLIGHTUSERDATA: return pvalue(o);
        default: return NULL;
    }
}


LV_API lv_State *lv_tothread (lv_State *L, int idx) {
    StkId o = index2adr(L, idx);
    return (!ttisthread(o)) ? NULL : thvalue(o);
}


LV_API const void *lv_topointer (lv_State *L, int idx) {
    StkId o = index2adr(L, idx);
    switch (ttype(o)) {
        case LV_TTABLE: return hvalue(o);
        case LV_TFUNCTION: return clvalue(o);
        case LV_TTHREAD: return thvalue(o);
        case LV_TUSERDATA:
        case LV_TLIGHTUSERDATA:
            return lv_touserdata(L, idx);
        default: return NULL;
    }
}



/*
 ** push functions (C -> stack)
 */


LV_API void lv_pushnil (lv_State *L) {
    lv_lock(L);
    setnilvalue(L->top);
    api_incr_top(L);
    lv_unlock(L);
}


LV_API void lv_pushnumber (lv_State *L, lv_Number n) {
    lv_lock(L);
    setnvalue(L->top, n);
    api_incr_top(L);
    lv_unlock(L);
}


LV_API void lv_pushinteger (lv_State *L, lv_Integer n) {
    lv_lock(L);
    setnvalue(L->top, cast_num(n));
    api_incr_top(L);
    lv_unlock(L);
}


LV_API void lv_pushlstring (lv_State *L, const char *s, size_t len) {
    lv_lock(L);
    lvC_checkGC(L);
    setsvalue2s(L, L->top, lvS_newlstr(L, s, len));
    api_incr_top(L);
    lv_unlock(L);
}


LV_API void lv_pushstring (lv_State *L, const char *s) {
    if (s == NULL)
        lv_pushnil(L);
    else
        lv_pushlstring(L, s, strlen(s));
}


LV_API const char *lv_pushvfstring (lv_State *L, const char *fmt,
                                    va_list argp) {
    const char *ret;
    lv_lock(L);
    lvC_checkGC(L);
    ret = lvO_pushvfstring(L, fmt, argp);
    lv_unlock(L);
    return ret;
}


LV_API const char *lv_pushfstring (lv_State *L, const char *fmt, ...) {
    const char *ret;
    va_list argp;
    lv_lock(L);
    lvC_checkGC(L);
    va_start(argp, fmt);
    ret = lvO_pushvfstring(L, fmt, argp);
    va_end(argp);
    lv_unlock(L);
    return ret;
}


LV_API void lv_pushcclosure (lv_State *L, lv_CFunction fn, int n) {
    Closure *cl;
    lv_lock(L);
    lvC_checkGC(L);
    api_checknelems(L, n);
    cl = lvF_newCclosure(L, n, getcurrenv(L));
    cl->c.f = fn;
    L->top -= n;
    while (n--)
        setobj2n(L, &cl->c.upvalue[n], L->top+n);
    setclvalue(L, L->top, cl);
    lv_assert(iswhite(obj2gco(cl)));
    api_incr_top(L);
    lv_unlock(L);
}


LV_API void lv_pushboolean (lv_State *L, int b) {
    lv_lock(L);
    setbvalue(L->top, (b != 0));  /* ensure that true is 1 */
    api_incr_top(L);
    lv_unlock(L);
}


LV_API void lv_pushlightuserdata (lv_State *L, void *p) {
    lv_lock(L);
    setpvalue(L->top, p);
    api_incr_top(L);
    lv_unlock(L);
}


LV_API int lv_pushthread (lv_State *L) {
    lv_lock(L);
    setthvalue(L, L->top, L);
    api_incr_top(L);
    lv_unlock(L);
    return (G(L)->mainthread == L);
}



/*
 ** get functions ([L u a] -> stack)
 */


LV_API void lv_gettable (lv_State *L, int idx) {
    StkId t;
    lv_lock(L);
    t = index2adr(L, idx);
    api_checkvalidindex(L, t);
    lvV_gettable(L, t, L->top - 1, L->top - 1);
    lv_unlock(L);
}


LV_API void lv_getfield (lv_State *L, int idx, const char *k) {
    StkId t;
    TValue key;
    lv_lock(L);
    t = index2adr(L, idx);
    api_checkvalidindex(L, t);
    setsvalue(L, &key, lvS_new(L, k));
    lvV_gettable(L, t, &key, L->top);
    api_incr_top(L);
    lv_unlock(L);
}


LV_API void lv_rawget (lv_State *L, int idx) {
    StkId t;
    lv_lock(L);
    t = index2adr(L, idx);
    api_check(L, ttistable(t));
    setobj2s(L, L->top - 1, lvH_get(hvalue(t), L->top - 1));
    lv_unlock(L);
}


LV_API void lv_rawgeti (lv_State *L, int idx, int n) {
    StkId o;
    lv_lock(L);
    o = index2adr(L, idx);
    api_check(L, ttistable(o));
    setobj2s(L, L->top, lvH_getnum(hvalue(o), n));
    api_incr_top(L);
    lv_unlock(L);
}


LV_API void lv_createtable (lv_State *L, int narray, int nrec) {
    lv_lock(L);
    lvC_checkGC(L);
    sethvalue(L, L->top, lvH_new(L, narray, nrec));
    api_incr_top(L);
    lv_unlock(L);
}

LV_API int lv_getmetatable (lv_State *L, int objindex) {
    const TValue *obj;
    Table *mt = NULL;
    int res;
    lv_lock(L);
    obj = index2adr(L, objindex);
    switch (ttype(obj)) {
        case LV_TTABLE:
            mt = hvalue(obj)->metatable;
            break;
        case LV_TUSERDATA:
            mt = uvalue(obj)->metatable;
            break;
        default:
            mt = G(L)->mt[ttype(obj)];
            break;
    }
    if (mt == NULL)
        res = 0;
    else {
        sethvalue(L, L->top, mt);
        api_incr_top(L);
        res = 1;
    }
    lv_unlock(L);
    return res;
}


LV_API void lv_getfenv (lv_State *L, int idx) {
    StkId o;
    lv_lock(L);
    o = index2adr(L, idx);
    api_checkvalidindex(L, o);
    switch (ttype(o)) {
        case LV_TFUNCTION:
            sethvalue(L, L->top, clvalue(o)->c.env);
            break;
        case LV_TUSERDATA:
            sethvalue(L, L->top, uvalue(o)->env);
            break;
        case LV_TTHREAD:
            setobj2s(L, L->top,  gt(thvalue(o)));
            break;
        default:
            setnilvalue(L->top);
            break;
    }
    api_incr_top(L);
    lv_unlock(L);
}


/*
 ** set functions (stack -> [L u a])
 */


LV_API void lv_settable (lv_State *L, int idx) {
    StkId t;
    lv_lock(L);
    api_checknelems(L, 2);
    t = index2adr(L, idx);
    api_checkvalidindex(L, t);
    lvV_settable(L, t, L->top - 2, L->top - 1);
    L->top -= 2;  /* pop index and value */
    lv_unlock(L);
}


LV_API void lv_setfield (lv_State *L, int idx, const char *k) {
    StkId t;
    TValue key;
    lv_lock(L);
    api_checknelems(L, 1);
    t = index2adr(L, idx);
    api_checkvalidindex(L, t);
    setsvalue(L, &key, lvS_new(L, k));
    lvV_settable(L, t, &key, L->top - 1);
    L->top--;  /* pop value */
    lv_unlock(L);
}


LV_API void lv_rawset (lv_State *L, int idx) {
    StkId t;
    lv_lock(L);
    api_checknelems(L, 2);
    t = index2adr(L, idx);
    api_check(L, ttistable(t));
    setobj2t(L, lvH_set(L, hvalue(t), L->top-2), L->top-1);
    lvC_barriert(L, hvalue(t), L->top-1);
    L->top -= 2;
    lv_unlock(L);
}


LV_API void lv_rawseti (lv_State *L, int idx, int n) {
    StkId o;
    lv_lock(L);
    api_checknelems(L, 1);
    o = index2adr(L, idx);
    api_check(L, ttistable(o));
    setobj2t(L, lvH_setnum(L, hvalue(o), n), L->top-1);
    lvC_barriert(L, hvalue(o), L->top-1);
    L->top--;
    lv_unlock(L);
}


LV_API int lv_setmetatable (lv_State *L, int objindex) {
    TValue *obj;
    Table *mt;
    lv_lock(L);
    api_checknelems(L, 1);
    obj = index2adr(L, objindex);
    api_checkvalidindex(L, obj);
    if (ttisnil(L->top - 1))
        mt = NULL;
    else {
        api_check(L, ttistable(L->top - 1));
        mt = hvalue(L->top - 1);
    }
    switch (ttype(obj)) {
        case LV_TTABLE: {
            hvalue(obj)->metatable = mt;
            if (mt)
                lvC_objbarriert(L, hvalue(obj), mt);
            break;
        }
        case LV_TUSERDATA: {
            uvalue(obj)->metatable = mt;
            if (mt)
                lvC_objbarrier(L, rawuvalue(obj), mt);
            break;
        }
        default: {
            G(L)->mt[ttype(obj)] = mt;
            break;
        }
    }
    L->top--;
    lv_unlock(L);
    return 1;
}


LV_API int lv_createUDataLuatable (lv_State *L, int objindex){
    lv_checkstack(L, 8);
    lv_pushvalue(L, objindex);
    lv_createtable(L, 8, 0);
    lv_setUDataLuatable(L, -2);
    lv_pop(L, 1);
    return 1;
}

LV_API int lv_setUDataLuatable (lv_State *L, int objindex) {
    TValue *obj;
    Table *lt;
    lv_lock(L);
    api_checknelems(L, 1);
    obj = index2adr(L, objindex);
    api_checkvalidindex(L, obj);
    if (ttisnil(L->top - 1))
        lt = NULL;
    else {
        api_check(L, ttistable(L->top - 1));
        lt = hvalue(L->top - 1);
    }
    switch (ttype(obj)) {
        case LV_TUSERDATA: {
            uvalue(obj)->luaTable = lt;
            if (lt)
                lvC_objbarrier(L, rawuvalue(obj), lt);
            break;
        }
        default: {
            printf("[luaview] error lv_setudataLuatable");
            break;
        }
    }
    L->top--;
    lv_unlock(L);
    return 1;
}

LV_API int lv_getUDataLuaTable (lv_State *L, int objindex) {
    const TValue *obj;
    Table *mt = NULL;
    int res;
    lv_lock(L);
    obj = index2adr(L, objindex);
    switch (ttype(obj)) {
        case LV_TUSERDATA:
            mt = uvalue(obj)->luaTable;
            break;
        default:
            break;
    }
    if (mt == NULL)
        res = 0;
    else {
        sethvalue(L, L->top, mt);
        api_incr_top(L);
        res = 1;
    }
    lv_unlock(L);
    return res;
}

LV_API int lv_setfenv (lv_State *L, int idx) {
    StkId o;
    int res = 1;
    lv_lock(L);
    api_checknelems(L, 1);
    o = index2adr(L, idx);
    api_checkvalidindex(L, o);
    api_check(L, ttistable(L->top - 1));
    switch (ttype(o)) {
        case LV_TFUNCTION:
            clvalue(o)->c.env = hvalue(L->top - 1);
            break;
        case LV_TUSERDATA:
            uvalue(o)->env = hvalue(L->top - 1);
            break;
        case LV_TTHREAD:
            sethvalue(L, gt(thvalue(o)), hvalue(L->top - 1));
            break;
        default:
            res = 0;
            break;
    }
    if (res) lvC_objbarrier(L, gcvalue(o), hvalue(L->top - 1));
    L->top--;
    lv_unlock(L);
    return res;
}


/*
 ** `load' and `call' functions (run [L u a] code)
 */


#define adjustresults(L,nres) \
{ if (nres == LV_MULTRET && L->top >= L->ci->top) L->ci->top = L->top; }


#define checkresults(L,na,nr) \
api_check(L, (nr) == LV_MULTRET || (L->ci->top - L->top >= (nr) - (na)))


LV_API void lv_call (lv_State *L, int nargs, int nresults) {
    StkId func;
    lv_lock(L);
    api_checknelems(L, nargs+1);
    checkresults(L, nargs, nresults);
    func = L->top - (nargs+1);
    lvD_call(L, func, nresults);
    adjustresults(L, nresults);
    lv_unlock(L);
}



/*
 ** Execute a protected call.
 */
struct CallS {  /* data to `f_call' */
    StkId func;
    int nresults;
};


static void f_call (lv_State *L, void *ud) {
    struct CallS *c = cast(struct CallS *, ud);
    lvD_call(L, c->func, c->nresults);
}



LV_API int lv_pcall (lv_State *L, int nargs, int nresults, int errfunc) {
    struct CallS c;
    int status;
    ptrdiff_t func;
    lv_lock(L);
    api_checknelems(L, nargs+1);
    checkresults(L, nargs, nresults);
    if (errfunc == 0)
        func = 0;
    else {
        StkId o = index2adr(L, errfunc);
        api_checkvalidindex(L, o);
        func = savestack(L, o);
    }
    c.func = L->top - (nargs+1);  /* function to be called */
    c.nresults = nresults;
    status = lvD_pcall(L, f_call, &c, savestack(L, c.func), func);
    adjustresults(L, nresults);
    lv_unlock(L);
    return status;
}


/*
 ** Execute a protected C call.
 */
struct CCallS {  /* data to `f_Ccall' */
    lv_CFunction func;
    void *ud;
};


static void f_Ccall (lv_State *L, void *ud) {
    struct CCallS *c = cast(struct CCallS *, ud);
    Closure *cl;
    cl = lvF_newCclosure(L, 0, getcurrenv(L));
    cl->c.f = c->func;
    setclvalue(L, L->top, cl);  /* push function */
    api_incr_top(L);
    setpvalue(L->top, c->ud);  /* push only argument */
    api_incr_top(L);
    lvD_call(L, L->top - 2, 0);
}


LV_API int lv_cpcall (lv_State *L, lv_CFunction func, void *ud) {
    struct CCallS c;
    int status;
    lv_lock(L);
    c.func = func;
    c.ud = ud;
    status = lvD_pcall(L, f_Ccall, &c, savestack(L, L->top), 0);
    lv_unlock(L);
    return status;
}


LV_API int lv_load (lv_State *L, lv_Reader reader, void *data,
                    const char *chunkname) {
    ZIO z;
    int status;
    lv_lock(L);
    if (!chunkname) chunkname = "?";
    lvZ_init(L, &z, reader, data);
    status = lvD_protectedparser(L, &z, chunkname);
    lv_unlock(L);
    return status;
}


LV_API int lv_dump (lv_State *L, lv_Writer writer, void *data) {
    int status;
    TValue *o;
    lv_lock(L);
    api_checknelems(L, 1);
    o = L->top - 1;
    if (isLfunction(o))
        status = lvU_dump(L, clvalue(o)->l.p, writer, data, 0);
    else
        status = 1;
    lv_unlock(L);
    return status;
}


LV_API int  lv_status (lv_State *L) {
    return L->status;
}


/*
 ** Garbage-collection function
 */

LV_API int lv_gc (lv_State *L, int what, int data) {
    int res = 0;
    global_State *g;
    lv_lock(L);
    g = G(L);
    switch (what) {
        case LV_GCSTOP: {
            g->GCthreshold = MAX_LUMEM;
            break;
        }
        case LV_GCRESTART: {
            g->GCthreshold = g->totalbytes;
            break;
        }
        case LV_GCCOLLECT: {
            lvC_fullgc(L);
            break;
        }
        case LV_GCCOUNT: {
            /* GC values are expressed in Kbytes: #bytes/2^10 */
            res = cast_int(g->totalbytes >> 10);
            break;
        }
        case LV_GCCOUNTB: {
            res = cast_int(g->totalbytes & 0x3ff);
            break;
        }
        case LV_GCSTEP: {
            lu_mem a = (cast(lu_mem, data) << 10);
            if (a <= g->totalbytes)
                g->GCthreshold = g->totalbytes - a;
            else
                g->GCthreshold = 0;
            while (g->GCthreshold <= g->totalbytes) {
                lvC_step(L);
                if (g->gcstate == LV_GCSpause) {  /* end of cycle? */
                    res = 1;  /* signal it */
                    break;
                }
            }
            break;
        }
        case LV_GCSETPAUSE: {
            res = g->gcpause;
            g->gcpause = data;
            break;
        }
        case LV_GCSETSTEPMUL: {
            res = g->gcstepmul;
            g->gcstepmul = data;
            break;
        }
        default: res = -1;  /* invalid option */
    }
    lv_unlock(L);
    return res;
}



/*
 ** miscellaneous functions
 */


LV_API int lv_error (lv_State *L) {
    lv_lock(L);
    api_checknelems(L, 1);
    lvG_errormsg(L);
    lv_unlock(L);
    return 0;  /* to avoid warnings */
}


LV_API int lv_next (lv_State *L, int idx) {
    StkId t;
    int more;
    lv_lock(L);
    t = index2adr(L, idx);
    api_check(L, ttistable(t));
    more = lvH_next(L, hvalue(t), L->top - 1);
    if (more) {
        api_incr_top(L);
    }
    else  /* no more elements */
        L->top -= 1;  /* remove key */
    lv_unlock(L);
    return more;
}


LV_API void lv_concat (lv_State *L, int n) {
    lv_lock(L);
    api_checknelems(L, n);
    if (n >= 2) {
        lvC_checkGC(L);
        lvV_concat(L, n, cast_int(L->top - L->base) - 1);
        L->top -= (n-1);
    }
    else if (n == 0) {  /* push empty string */
        setsvalue2s(L, L->top, lvS_newlstr(L, "", 0));
        api_incr_top(L);
    }
    /* else n == 1; nothing to do */
    lv_unlock(L);
}


LV_API lv_Alloc lv_getallocf (lv_State *L, void **ud) {
    lv_Alloc f;
    lv_lock(L);
    if (ud) *ud = G(L)->ud;
    f = G(L)->frealloc;
    lv_unlock(L);
    return f;
}


LV_API void lv_setallocf (lv_State *L, lv_Alloc f, void *ud) {
    lv_lock(L);
    G(L)->ud = ud;
    G(L)->frealloc = f;
    lv_unlock(L);
}


LV_API void *lv_newuserdata (lv_State *L, size_t size) {
    Udata *u;
    lv_lock(L);
    lvC_checkGC(L);
    u = lvS_newudata(L, size, getcurrenv(L));
    setuvalue(L, L->top, u);
    api_incr_top(L);
    lv_unlock(L);
    memset(u+1, 0, size);
    return u + 1;
}




static const char *aux_upvalue (StkId fi, int n, TValue **val) {
    Closure *f;
    if (!ttisfunction(fi)) return NULL;
    f = clvalue(fi);
    if (f->c.isC) {
        if (!(1 <= n && n <= f->c.nupvalues)) return NULL;
        *val = &f->c.upvalue[n-1];
        return "";
    }
    else {
        Proto *p = f->l.p;
        if (!(1 <= n && n <= p->sizeupvalues)) return NULL;
        *val = f->l.upvals[n-1]->v;
        return getstr(p->upvalues[n-1]);
    }
}


LV_API const char *lv_getupvalue (lv_State *L, int funcindex, int n) {
    const char *name;
    TValue *val;
    lv_lock(L);
    name = aux_upvalue(index2adr(L, funcindex), n, &val);
    if (name) {
        setobj2s(L, L->top, val);
        api_incr_top(L);
    }
    lv_unlock(L);
    return name;
}


LV_API const char *lv_setupvalue (lv_State *L, int funcindex, int n) {
    const char *name;
    TValue *val;
    StkId fi;
    lv_lock(L);
    fi = index2adr(L, funcindex);
    api_checknelems(L, 1);
    name = aux_upvalue(fi, n, &val);
    if (name) {
        L->top--;
        setobj(L, val, L->top);
        lvC_barrier(L, clvalue(fi), L->top);
    }
    lv_unlock(L);
    return name;
}

