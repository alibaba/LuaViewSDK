/*
 ** $Id: lVbaselib.c,v 1.191.1.6 2008/02/14 16:46:22 roberto Exp $
 ** Basic library
 ** See Copyright Notice in lV.h
 */



#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define lbaselib_c
#define LV_LIB

#include "lV.h"

#include "lVauxlib.h"
#include "lVlib.h"
#include "lVstate.h"

// 调试信息
extern void lv_printToServer(lv_State* L, const char* cs, int withTabChar);




/*
 ** If your system does not support `stdout', you can just remove this function.
 ** If you need, you can define your own `print' function, following this
 ** model but changing `fputs' to put the strings at a proper place
 ** (a console window or a log file, for instance).
 */
static int lvB_print (lv_State *L) {
    int n = lv_gettop(L);  /* number of arguments */
    int i;
    lv_getglobal(L, "tostring");
    for (i=1; i<=n; i++) {
        const char *s;
        lv_pushvalue(L, -1);  /* function to be called */
        lv_pushvalue(L, i);   /* value to print */
        lv_call(L, 1, 1);
        s = lv_tostring(L, -1);  /* get result */
        if ( s == NULL )
            return lvL_error(L, LV_QL("tostring") " must return a string to " LV_QL("print"));
        if (i>1) {
            fputs("\t", stdout);
            fputs(s, stdout);
            lv_printToServer(L, s, 1);
        } else {
            fputs(s, stdout);
            lv_printToServer(L, s, 0);
        }
        lv_pop(L, 1);  /* pop result */
    }
    fputs("\n", stdout);
    lv_printToServer(L, "\n", 0);
    return 0;
}


static int lvB_tonumber (lv_State *L) {
    int base = lvL_optint(L, 2, 10);
    if (base == 10) {  /* standard conversion */
        lvL_checkany(L, 1);
        if (lv_isnumber(L, 1)) {
            lv_pushnumber(L, lv_tonumber(L, 1));
            return 1;
        }
    }
    else {
        const char *s1 = lvL_checkstring(L, 1);
        char *s2;
        unsigned long n;
        lvL_argcheck(L, 2 <= base && base <= 36, 2, "base out of range");
        n = strtoul(s1, &s2, base);
        if (s1 != s2) {  /* at least one valid digit? */
            while (isspace((unsigned char)(*s2))) s2++;  /* skip trailing spaces */
            if (*s2 == '\0') {  /* no invalid trailing characters? */
                lv_pushnumber(L, (lv_Number)n);
                return 1;
            }
        }
    }
    lv_pushnil(L);  /* else not a number */
    return 1;
}


static int lvB_error (lv_State *L) {
    int level = lvL_optint(L, 2, 1);
    lv_settop(L, 1);
    if (lv_isstring(L, 1) && level > 0) {  /* add extra information? */
        lvL_where(L, level);
        lv_pushvalue(L, 1);
        lv_concat(L, 2);
    }
    return lv_error(L);
}


static int lvB_getmetatable (lv_State *L) {
    lvL_checkany(L, 1);
    if (!lv_getmetatable(L, 1)) {
        lv_pushnil(L);
        return 1;  /* no metatable */
    }
    lvL_getmetafield(L, 1, "__metatable");
    return 1;  /* returns either __metatable field (if present) or metatable */
}


static int lvB_setmetatable (lv_State *L) {
    int t = lv_type(L, 2);
    lvL_checktype(L, 1, LV_TTABLE);
    lvL_argcheck(L, t == LV_TNIL || t == LV_TTABLE, 2,
                 "nil or table expected");
    if (lvL_getmetafield(L, 1, "__metatable"))
        lvL_error(L, "cannot change a protected metatable");
    lv_settop(L, 2);
    lv_setmetatable(L, 1);
    return 1;
}


static void getfunc (lv_State *L, int opt) {
    if (lv_isfunction(L, 1)) lv_pushvalue(L, 1);
    else {
        lv_Debug ar;
        int level = opt ? lvL_optint(L, 1, 1) : lvL_checkint(L, 1);
        lvL_argcheck(L, level >= 0, 1, "level must be non-negative");
        if (lv_getstack(L, level, &ar) == 0)
            lvL_argerror(L, 1, "invalid level");
        lv_getinfo(L, "f", &ar);
        if (lv_isnil(L, -1))
            lvL_error(L, "no function environment for tail call at level %d",
                      level);
    }
}


static int lvB_getfenv (lv_State *L) {
    getfunc(L, 1);
    if (lv_iscfunction(L, -1))  /* is a C function? */
        lv_pushvalue(L, LV_GLOBALSINDEX);  /* return the thread's global env. */
    else
        lv_getfenv(L, -1);
    return 1;
}


static int lvB_setfenv (lv_State *L) {
    lvL_checktype(L, 2, LV_TTABLE);
    getfunc(L, 0);
    lv_pushvalue(L, 2);
    if (lv_isnumber(L, 1) && lv_tonumber(L, 1) == 0) {
        /* change environment of current thread */
        lv_pushthread(L);
        lv_insert(L, -2);
        lv_setfenv(L, -2);
        return 0;
    }
    else if (lv_iscfunction(L, -2) || lv_setfenv(L, -2) == 0)
        lvL_error(L,
                  LV_QL("setfenv") " cannot change environment of given object");
    return 1;
}


static int lvB_rawequal (lv_State *L) {
    lvL_checkany(L, 1);
    lvL_checkany(L, 2);
    lv_pushboolean(L, lv_rawequal(L, 1, 2));
    return 1;
}


static int lvB_rawget (lv_State *L) {
    lvL_checktype(L, 1, LV_TTABLE);
    lvL_checkany(L, 2);
    lv_settop(L, 2);
    lv_rawget(L, 1);
    return 1;
}

static int lvB_rawset (lv_State *L) {
    lvL_checktype(L, 1, LV_TTABLE);
    lvL_checkany(L, 2);
    lvL_checkany(L, 3);
    lv_settop(L, 3);
    lv_rawset(L, 1);
    return 1;
}


static int lvB_gcinfo (lv_State *L) {
    lv_pushinteger(L, lv_getgccount(L));
    return 1;
}


static int lvB_collectgarbage (lv_State *L) {
    static const char *const opts[] = {"stop", "restart", "collect",
        "count", "step", "setpause", "setstepmul", NULL};
    static const int optsnum[] = {LV_GCSTOP, LV_GCRESTART, LV_GCCOLLECT,
        LV_GCCOUNT, LV_GCSTEP, LV_GCSETPAUSE, LV_GCSETSTEPMUL};
    int o = lvL_checkoption(L, 1, "collect", opts);
    int ex = lvL_optint(L, 2, 0);
    int res = lv_gc(L, optsnum[o], ex);
    switch (optsnum[o]) {
        case LV_GCCOUNT: {
            int b = lv_gc(L, LV_GCCOUNTB, 0);
            lv_pushnumber(L, res + ((lv_Number)b/1024));
            return 1;
        }
        case LV_GCSTEP: {
            lv_pushboolean(L, res);
            return 1;
        }
        default: {
            lv_pushnumber(L, res);
            return 1;
        }
    }
}


static int lvB_type (lv_State *L) {
    lvL_checkany(L, 1);
    lv_pushstring(L, lvL_typename(L, 1));
    return 1;
}


static int lvB_next (lv_State *L) {
    lvL_checktype(L, 1, LV_TTABLE);
    lv_settop(L, 2);  /* create a 2nd argument if there isn't one */
    if (lv_next(L, 1))
        return 2;
    else {
        lv_pushnil(L);
        return 1;
    }
}


static int lvB_pairs (lv_State *L) {
    lvL_checktype(L, 1, LV_TTABLE);
    lv_pushvalue(L, lv_upvalueindex(1));  /* return generator, */
    lv_pushvalue(L, 1);  /* state, */
    lv_pushnil(L);  /* and initial value */
    return 3;
}


static int ipairsaux (lv_State *L) {
    int i = lvL_checkint(L, 2);
    lvL_checktype(L, 1, LV_TTABLE);
    i++;  /* next value */
    lv_pushinteger(L, i);
    lv_rawgeti(L, 1, i);
    return (lv_isnil(L, -1)) ? 0 : 2;
}


static int lvB_ipairs (lv_State *L) {
    lvL_checktype(L, 1, LV_TTABLE);
    lv_pushvalue(L, lv_upvalueindex(1));  /* return generator, */
    lv_pushvalue(L, 1);  /* state, */
    lv_pushinteger(L, 0);  /* and initial value */
    return 3;
}


static int load_aux (lv_State *L, int status) {
    if (status == 0)  /* OK? */
        return 1;
    else {
        lv_pushnil(L);
        lv_insert(L, -2);  /* put before error message */
        return 2;  /* return nil plus error message */
    }
}


static int lvB_loadstring (lv_State *L) {
    size_t l;
    const char *s = lvL_checklstring(L, 1, &l);
    const char *chunkname = lvL_optstring(L, 2, s);
    return load_aux(L, lvL_loadbuffer(L, s, l, chunkname));
}


static int lvB_loadfile (lv_State *L) {
    const char *fname = lvL_optstring(L, 1, NULL);
    return load_aux(L, lvL_loadfile(L, fname));
}


/*
 ** Reader for generic `load' function: `lv_load' uses the
 ** stack for internal stuff, so the reader cannot change the
 ** stack top. Instead, it keeps its resulting string in a
 ** reserved slot inside the stack.
 */
static const char *generic_reader (lv_State *L, void *ud, size_t *size) {
    (void)ud;  /* to avoid warnings */
    lvL_checkstack(L, 2, "too many nested functions");
    lv_pushvalue(L, 1);  /* get function */
    lv_call(L, 0, 1);  /* call it */
    if (lv_isnil(L, -1)) {
        *size = 0;
        return NULL;
    }
    else if (lv_isstring(L, -1)) {
        lv_replace(L, 3);  /* save string in a reserved stack slot */
        return lv_tolstring(L, 3, size);
    }
    else lvL_error(L, "reader function must return a string");
    return NULL;  /* to avoid warnings */
}


static int lvB_load (lv_State *L) {
    int status;
    const char *cname = lvL_optstring(L, 2, "=(load)");
    lvL_checktype(L, 1, LV_TFUNCTION);
    lv_settop(L, 3);  /* function, eventual name, plus one reserved slot */
    status = lv_load(L, generic_reader, NULL, cname);
    return load_aux(L, status);
}


static int lvB_dofile (lv_State *L) {
    const char *fname = lvL_optstring(L, 1, NULL);
    int n = lv_gettop(L);
    if (lvL_loadfile(L, fname) != 0) lv_error(L);
    lv_call(L, 0, LV_MULTRET);
    return lv_gettop(L) - n;
}


static int lvB_assert (lv_State *L) {
    lvL_checkany(L, 1);
    if (!lv_toboolean(L, 1))
        return lvL_error(L, "%s", lvL_optstring(L, 2, "assertion failed!"));
    return lv_gettop(L);
}


static int lvB_unpack (lv_State *L) {
    int i, e, n;
    lvL_checktype(L, 1, LV_TTABLE);
    i = lvL_optint(L, 2, 1);
    e = lvL_opt(L, lvL_checkint, 3, lvL_getn(L, 1));
    if (i > e) return 0;  /* empty range */
    n = e - i + 1;  /* number of elements */
    if (n <= 0 || !lv_checkstack(L, n))  /* n <= 0 means arith. overflow */
        return lvL_error(L, "too many results to unpack");
    lv_rawgeti(L, 1, i);  /* push arg[i] (avoiding overflow problems) */
    while (i++ < e)  /* push arg[i + 1...e] */
        lv_rawgeti(L, 1, i);
    return n;
}


static int lvB_select (lv_State *L) {
    int n = lv_gettop(L);
    if (lv_type(L, 1) == LV_TSTRING && *lv_tostring(L, 1) == '#') {
        lv_pushinteger(L, n-1);
        return 1;
    }
    else {
        int i = lvL_checkint(L, 1);
        if (i < 0) i = n + i;
        else if (i > n) i = n;
        lvL_argcheck(L, 1 <= i, 1, "index out of range");
        return n - i;
    }
}


static int lvB_pcall (lv_State *L) {
    int status;
    lvL_checkany(L, 1);
    status = lv_pcall(L, lv_gettop(L) - 1, LV_MULTRET, 0);
    lv_pushboolean(L, (status == 0));
    lv_insert(L, 1);
    return lv_gettop(L);  /* return status + all results */
}


static int lvB_xpcall (lv_State *L) {
    int status;
    lvL_checkany(L, 2);
    lv_settop(L, 2);
    lv_insert(L, 1);  /* put error function under function to be called */
    status = lv_pcall(L, 0, LV_MULTRET, 1);
    lv_pushboolean(L, (status == 0));
    lv_replace(L, 1);
    return lv_gettop(L);  /* return status + all results */
}


static int lvB_tostring (lv_State *L) {
    lvL_checkany(L, 1);
    if (lvL_callmeta(L, 1, "__tostring"))  /* is there a metafield? */
        return 1;  /* use its value */
    switch (lv_type(L, 1)) {
        case LV_TNUMBER:
            lv_pushstring(L, lv_tostring(L, 1));
            break;
        case LV_TSTRING:
            lv_pushvalue(L, 1);
            break;
        case LV_TBOOLEAN:
            lv_pushstring(L, (lv_toboolean(L, 1) ? "true" : "false"));
            break;
        case LV_TNIL:
            lv_pushliteral(L, "nil");
            break;
        default:
            lv_pushfstring(L, "%s: %p", lvL_typename(L, 1), lv_topointer(L, 1));
            break;
    }
    return 1;
}


static int lvB_newproxy (lv_State *L) {
    lv_settop(L, 1);
    lv_newuserdata(L, 0);  /* create proxy */
    if (lv_toboolean(L, 1) == 0)
        return 1;  /* no metatable */
    else if (lv_isboolean(L, 1)) {
        lv_newtable(L);  /* create a new metatable `m' ... */
        lv_pushvalue(L, -1);  /* ... and mark `m' as a valid metatable */
        lv_pushboolean(L, 1);
        lv_rawset(L, lv_upvalueindex(1));  /* weaktable[m] = true */
    }
    else {
        int validproxy = 0;  /* to check if weaktable[metatable(u)] == true */
        if (lv_getmetatable(L, 1)) {
            lv_rawget(L, lv_upvalueindex(1));
            validproxy = lv_toboolean(L, -1);
            lv_pop(L, 1);  /* remove value */
        }
        lvL_argcheck(L, validproxy, 1, "boolean or proxy expected");
        lv_getmetatable(L, 1);  /* metatable is valid; get it */
    }
    lv_setmetatable(L, 2);
    return 1;
}

static const char printChinese[] = {230,137,147,229,141,176,0};//打印的UTF8编码

static const lvL_Reg base_funcs[] = {
    {"assert", lvB_assert},
    {"collectgarbage", lvB_collectgarbage},
    {"dofile", lvB_dofile},
    {"error", lvB_error},
    {"gcinfo", lvB_gcinfo},
    {"getfenv", lvB_getfenv},
    {"getmetatable", lvB_getmetatable},
    {"loadfile", lvB_loadfile},
    {"load", lvB_load},
    {"loadstring", lvB_loadstring},
    {"next", lvB_next},
    {"pcall", lvB_pcall},
    {"print", lvB_print},
    {printChinese, lvB_print},
    {"rawequal", lvB_rawequal},
    {"rawget", lvB_rawget},
    {"rawset", lvB_rawset},
    {"select", lvB_select},
    {"setfenv", lvB_setfenv},
    {"setmetatable", lvB_setmetatable},
    {"tonumber", lvB_tonumber},
    {"tostring", lvB_tostring},
    {"type", lvB_type},
    {"unpack", lvB_unpack},
    {"xpcall", lvB_xpcall},
    {NULL, NULL}
};


/*
 ** {======================================================
 ** Coroutine library
 ** =======================================================
 */

#define CO_RUN	0	/* running */
#define CO_SUS	1	/* suspended */
#define CO_NOR	2	/* 'normal' (it resumed another coroutine) */
#define CO_DEAD	3

static const char *const statnames[] =
{"running", "suspended", "normal", "dead"};

static int costatus (lv_State *L, lv_State *co) {
    if (L == co) return CO_RUN;
    switch (lv_status(co)) {
        case LV_YIELD:
            return CO_SUS;
        case 0: {
            lv_Debug ar;
            if (lv_getstack(co, 0, &ar) > 0)  /* does it have frames? */
                return CO_NOR;  /* it is running */
            else if (lv_gettop(co) == 0)
                return CO_DEAD;
            else
                return CO_SUS;  /* initial state */
        }
        default:  /* some error occured */
            return CO_DEAD;
    }
}


static int lvB_costatus (lv_State *L) {
    lv_State *co = lv_tothread(L, 1);
    lvL_argcheck(L, co, 1, "coroutine expected");
    lv_pushstring(L, statnames[costatus(L, co)]);
    return 1;
}


static int auxresume (lv_State *L, lv_State *co, int narg) {
    int status = costatus(L, co);
    if (!lv_checkstack(co, narg))
        lvL_error(L, "too many arguments to resume");
    if (status != CO_SUS) {
        lv_pushfstring(L, "cannot resume %s coroutine", statnames[status]);
        return -1;  /* error flag */
    }
    lv_xmove(L, co, narg);
    lv_setlevel(L, co);
    status = lv_resume(co, narg);
    if (status == 0 || status == LV_YIELD) {
        int nres = lv_gettop(co);
        if (!lv_checkstack(L, nres + 1))
            lvL_error(L, "too many results to resume");
        lv_xmove(co, L, nres);  /* move yielded values */
        return nres;
    }
    else {
        lv_xmove(co, L, 1);  /* move error message */
        return -1;  /* error flag */
    }
}


static int lvB_coresume (lv_State *L) {
    lv_State *co = lv_tothread(L, 1);
    int r;
    lvL_argcheck(L, co, 1, "coroutine expected");
    r = auxresume(L, co, lv_gettop(L) - 1);
    if (r < 0) {
        lv_pushboolean(L, 0);
        lv_insert(L, -2);
        return 2;  /* return false + error message */
    }
    else {
        lv_pushboolean(L, 1);
        lv_insert(L, -(r + 1));
        return r + 1;  /* return true + `resume' returns */
    }
}


static int lvB_auxwrap (lv_State *L) {
    lv_State *co = lv_tothread(L, lv_upvalueindex(1));
    int r = auxresume(L, co, lv_gettop(L));
    if (r < 0) {
        if (lv_isstring(L, -1)) {  /* error object is a string? */
            lvL_where(L, 1);  /* add extra info */
            lv_insert(L, -2);
            lv_concat(L, 2);
        }
        lv_error(L);  /* propagate error */
    }
    return r;
}


static int lvB_cocreate (lv_State *L) {
    lv_State *NL = lv_newthread(L);
    NL->lView = L->lView;
    
    lvL_argcheck(L, lv_isfunction(L, 1) && !lv_iscfunction(L, 1), 1,
                 "[L u a] function expected");
    lv_pushvalue(L, 1);  /* move function to top */
    lv_xmove(L, NL, 1);  /* move function from L to NL */
    return 1;
}


static int lvB_cowrap (lv_State *L) {
    lvB_cocreate(L);
    lv_pushcclosure(L, lvB_auxwrap, 1);
    return 1;
}


static int lvB_yield (lv_State *L) {
    return lv_yield(L, lv_gettop(L));
}


static int lvB_corunning (lv_State *L) {
    if (lv_pushthread(L))
        lv_pushnil(L);  /* main thread is not a coroutine */
    return 1;
}


static const lvL_Reg co_funcs[] = {
    {"create", lvB_cocreate},
    {"resume", lvB_coresume},
    {"running", lvB_corunning},
    {"status", lvB_costatus},
    {"wrap", lvB_cowrap},
    {"yield", lvB_yield},
    {NULL, NULL}
};

/* }====================================================== */


static void auxopen (lv_State *L, const char *name,
                     lv_CFunction f, lv_CFunction u) {
    lv_pushcfunction(L, u);
    lv_pushcclosure(L, f, 1);
    lv_setfield(L, -2, name);
}


static void base_open (lv_State *L) {
    /* set global _G */
    lv_pushvalue(L, LV_GLOBALSINDEX);
    lv_setglobal(L, "_G");
    /* open lib into global table */
    lvL_register(L, "_G", base_funcs);
    lv_pushliteral(L, LV_VERSION);
    lv_setglobal(L, "_VERSION");  /* set global _VERSION */
    /* `ipairs' and `pairs' need auxliliary functions as upvalues */
    auxopen(L, "ipairs", lvB_ipairs, ipairsaux);
    auxopen(L, "pairs", lvB_pairs, lvB_next);
    /* `newproxy' needs a weaktable as upvalue */
    lv_createtable(L, 0, 1);  /* new table `w' */
    lv_pushvalue(L, -1);  /* `w' will be its own metatable */
    lv_setmetatable(L, -2);
    lv_pushliteral(L, "kv");
    lv_setfield(L, -2, "__mode");  /* metatable(w).__mode = "kv" */
    lv_pushcclosure(L, lvB_newproxy, 1);
    lv_setglobal(L, "newproxy");  /* set global `newproxy' */
}


LVLIB_API int lvopen_base (lv_State *L) {
    base_open(L);
    lvL_register(L, LV_COLIBNAME, co_funcs);
    return 2;
}

