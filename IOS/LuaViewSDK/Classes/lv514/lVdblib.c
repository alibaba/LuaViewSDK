/*
 ** $Id: lVdblib.c,v 1.104.1.3 2008/01/21 13:11:21 roberto Exp $
 ** Interface from [L u a] to its debug API
 ** See Copyright Notice in lV.h
 */


#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ldblib_c
#define LV_LIB

#include "lV.h"

#include "lVauxlib.h"
#include "lVlib.h"



static int db_getregistry (lv_State *L) {
    lv_pushvalue(L, LV_REGISTRYINDEX);
    return 1;
}


static int db_getmetatable (lv_State *L) {
    lvL_checkany(L, 1);
    if (!lv_getmetatable(L, 1)) {
        lv_pushnil(L);  /* no metatable */
    }
    return 1;
}


static int db_setmetatable (lv_State *L) {
    int t = lv_type(L, 2);
    lvL_argcheck(L, t == LV_TNIL || t == LV_TTABLE, 2,
                 "nil or table expected");
    lv_settop(L, 2);
    lv_pushboolean(L, lv_setmetatable(L, 1));
    return 1;
}


static int db_getfenv (lv_State *L) {
    lv_getfenv(L, 1);
    return 1;
}


static int db_setfenv (lv_State *L) {
    lvL_checktype(L, 2, LV_TTABLE);
    lv_settop(L, 2);
    if (lv_setfenv(L, 1) == 0)
        lvL_error(L, LV_QL("setfenv")
                  " cannot change environment of given object");
    return 1;
}


static void settabss (lv_State *L, const char *i, const char *v) {
    lv_pushstring(L, v);
    lv_setfield(L, -2, i);
}


static void settabsi (lv_State *L, const char *i, int v) {
    lv_pushinteger(L, v);
    lv_setfield(L, -2, i);
}


static lv_State *getthread (lv_State *L, int *arg) {
    if (lv_isthread(L, 1)) {
        *arg = 1;
        return lv_tothread(L, 1);
    }
    else {
        *arg = 0;
        return L;
    }
}


static void treatstackoption (lv_State *L, lv_State *L1, const char *fname) {
    if (L == L1) {
        lv_pushvalue(L, -2);
        lv_remove(L, -3);
    }
    else
        lv_xmove(L1, L, 1);
    lv_setfield(L, -2, fname);
}


static int db_getinfo (lv_State *L) {
    lv_Debug ar;
    int arg;
    lv_State *L1 = getthread(L, &arg);
    const char *options = lvL_optstring(L, arg+2, "flnSu");
    if (lv_isnumber(L, arg+1)) {
        if (!lv_getstack(L1, (int)lv_tointeger(L, arg+1), &ar)) {
            lv_pushnil(L);  /* level out of range */
            return 1;
        }
    }
    else if (lv_isfunction(L, arg+1)) {
        lv_pushfstring(L, ">%s", options);
        options = lv_tostring(L, -1);
        lv_pushvalue(L, arg+1);
        lv_xmove(L, L1, 1);
    }
    else
        return lvL_argerror(L, arg+1, "function or level expected");
    if (!lv_getinfo(L1, options, &ar))
        return lvL_argerror(L, arg+2, "invalid option");
    lv_createtable(L, 0, 2);
    if (strchr(options, 'S')) {
        settabss(L, "source", ar.source);
        settabss(L, "short_src", ar.short_src);
        settabsi(L, "linedefined", ar.linedefined);
        settabsi(L, "lastlinedefined", ar.lastlinedefined);
        settabss(L, "what", ar.what);
    }
    if (strchr(options, 'l'))
        settabsi(L, "currentline", ar.currentline);
    if (strchr(options, 'u'))
        settabsi(L, "nups", ar.nups);
    if (strchr(options, 'n')) {
        settabss(L, "name", ar.name);
        settabss(L, "namewhat", ar.namewhat);
    }
    if (strchr(options, 'L'))
        treatstackoption(L, L1, "activelines");
    if (strchr(options, 'f'))
        treatstackoption(L, L1, "func");
    return 1;  /* return table */
}


static int db_getlocal (lv_State *L) {
    int arg;
    lv_State *L1 = getthread(L, &arg);
    lv_Debug ar;
    const char *name;
    if (!lv_getstack(L1, lvL_checkint(L, arg+1), &ar))  /* out of range? */
        return lvL_argerror(L, arg+1, "level out of range");
    name = lv_getlocal(L1, &ar, lvL_checkint(L, arg+2));
    if (name) {
        lv_xmove(L1, L, 1);
        lv_pushstring(L, name);
        lv_pushvalue(L, -2);
        return 2;
    }
    else {
        lv_pushnil(L);
        return 1;
    }
}


static int db_setlocal (lv_State *L) {
    int arg;
    lv_State *L1 = getthread(L, &arg);
    lv_Debug ar;
    if (!lv_getstack(L1, lvL_checkint(L, arg+1), &ar))  /* out of range? */
        return lvL_argerror(L, arg+1, "level out of range");
    lvL_checkany(L, arg+3);
    lv_settop(L, arg+3);
    lv_xmove(L, L1, 1);
    lv_pushstring(L, lv_setlocal(L1, &ar, lvL_checkint(L, arg+2)));
    return 1;
}


static int auxupvalue (lv_State *L, int get) {
    const char *name;
    int n = lvL_checkint(L, 2);
    lvL_checktype(L, 1, LV_TFUNCTION);
    if (lv_iscfunction(L, 1)) return 0;  /* cannot touch C upvalues from [L u a] */
    name = get ? lv_getupvalue(L, 1, n) : lv_setupvalue(L, 1, n);
    if (name == NULL) return 0;
    lv_pushstring(L, name);
    lv_insert(L, -(get+1));
    return get + 1;
}


static int db_getupvalue (lv_State *L) {
    return auxupvalue(L, 1);
}


static int db_setupvalue (lv_State *L) {
    lvL_checkany(L, 3);
    return auxupvalue(L, 0);
}



static const char KEY_HOOK = 'h';


static void hookf (lv_State *L, lv_Debug *ar) {
    static const char *const hooknames[] =
    {"call", "return", "line", "count", "tail return"};
    lv_pushlightuserdata(L, (void *)&KEY_HOOK);
    lv_rawget(L, LV_REGISTRYINDEX);
    lv_pushlightuserdata(L, L);
    lv_rawget(L, -2);
    if (lv_isfunction(L, -1)) {
        lv_pushstring(L, hooknames[(int)ar->event]);
        if (ar->currentline >= 0)
            lv_pushinteger(L, ar->currentline);
        else lv_pushnil(L);
        lv_assert(lv_getinfo(L, "lS", ar));
        lv_call(L, 2, 0);
    }
}


static int makemask (const char *smask, int count) {
    int mask = 0;
    if (strchr(smask, 'c')) mask |= LV_MASKCALL;
    if (strchr(smask, 'r')) mask |= LV_MASKRET;
    if (strchr(smask, 'l')) mask |= LV_MASKLINE;
    if (count > 0) mask |= LV_MASKCOUNT;
    return mask;
}


static char *unmakemask (int mask, char *smask) {
    int i = 0;
    if (mask & LV_MASKCALL) smask[i++] = 'c';
    if (mask & LV_MASKRET) smask[i++] = 'r';
    if (mask & LV_MASKLINE) smask[i++] = 'l';
    smask[i] = '\0';
    return smask;
}


static void gethooktable (lv_State *L) {
    lv_pushlightuserdata(L, (void *)&KEY_HOOK);
    lv_rawget(L, LV_REGISTRYINDEX);
    if (!lv_istable(L, -1)) {
        lv_pop(L, 1);
        lv_createtable(L, 0, 1);
        lv_pushlightuserdata(L, (void *)&KEY_HOOK);
        lv_pushvalue(L, -2);
        lv_rawset(L, LV_REGISTRYINDEX);
    }
}


static int db_sethook (lv_State *L) {
    int arg, mask, count;
    lv_Hook func;
    lv_State *L1 = getthread(L, &arg);
    if (lv_isnoneornil(L, arg+1)) {
        lv_settop(L, arg+1);
        func = NULL; mask = 0; count = 0;  /* turn off hooks */
    }
    else {
        const char *smask = lvL_checkstring(L, arg+2);
        lvL_checktype(L, arg+1, LV_TFUNCTION);
        count = lvL_optint(L, arg+3, 0);
        func = hookf; mask = makemask(smask, count);
    }
    gethooktable(L);
    lv_pushlightuserdata(L, L1);
    lv_pushvalue(L, arg+1);
    lv_rawset(L, -3);  /* set new hook */
    lv_pop(L, 1);  /* remove hook table */
    lv_sethook(L1, func, mask, count);  /* set hooks */
    return 0;
}


static int db_gethook (lv_State *L) {
    int arg;
    lv_State *L1 = getthread(L, &arg);
    char buff[5];
    int mask = lv_gethookmask(L1);
    lv_Hook hook = lv_gethook(L1);
    if (hook != NULL && hook != hookf)  /* external hook? */
        lv_pushliteral(L, "external hook");
    else {
        gethooktable(L);
        lv_pushlightuserdata(L, L1);
        lv_rawget(L, -2);   /* get hook */
        lv_remove(L, -2);  /* remove hook table */
    }
    lv_pushstring(L, unmakemask(mask, buff));
    lv_pushinteger(L, lv_gethookcount(L1));
    return 3;
}


static int db_debug (lv_State *L) {
    for (;;) {
        char buffer[250];
        fputs("lv_debug> ", stderr);
        if (fgets(buffer, sizeof(buffer), stdin) == 0 ||
            strcmp(buffer, "cont\n") == 0)
            return 0;
        if (lvL_loadbuffer(L, buffer, strlen(buffer), "=(debug command)") ||
            lv_pcall(L, 0, 0, 0)) {
            fputs(lv_tostring(L, -1), stderr);
            fputs("\n", stderr);
        }
        lv_settop(L, 0);  /* remove eventual returns */
    }
}


#define LEVELS1	12	/* size of the first part of the stack */
#define LEVELS2	10	/* size of the second part of the stack */

static int db_errorfb (lv_State *L) {
    int level;
    int firstpart = 1;  /* still before eventual `...' */
    int arg;
    lv_State *L1 = getthread(L, &arg);
    lv_Debug ar;
    if (lv_isnumber(L, arg+2)) {
        level = (int)lv_tointeger(L, arg+2);
        lv_pop(L, 1);
    }
    else
        level = (L == L1) ? 1 : 0;  /* level 0 may be this own function */
    if (lv_gettop(L) == arg)
        lv_pushliteral(L, "");
    else if (!lv_isstring(L, arg+1)) return 1;  /* message is not a string */
    else lv_pushliteral(L, "\n");
    lv_pushliteral(L, "调用栈:");
    while (lv_getstack(L1, level++, &ar)) {
        if (level > LEVELS1 && firstpart) {
            /* no more than `LEVELS2' more levels? */
            if (!lv_getstack(L1, level+LEVELS2, &ar))
                level--;  /* keep going */
            else {
                lv_pushliteral(L, "\n\t...");  /* too many levels */
                while (lv_getstack(L1, level+LEVELS2, &ar))  /* find last levels */
                    level++;
            }
            firstpart = 0;
            continue;
        }
        lv_pushliteral(L, "\n\t");
        lv_getinfo(L1, "Snl", &ar);
        lv_pushfstring(L, "%s:", ar.source);
        if (ar.currentline > 0)
            lv_pushfstring(L, "%d:", ar.currentline);
        if (*ar.namewhat != '\0')  /* is there a name? */
            lv_pushfstring(L, " in function " LV_QS, ar.name);
        else {
            if (*ar.what == 'm')  /* main? */
                lv_pushfstring(L, " in main chunk");
            else if (*ar.what == 'C' || *ar.what == 't')
                lv_pushliteral(L, " ?");  /* C function or tail call */
            else
                lv_pushfstring(L, " in function <%s:%d>",
                               ar.source, ar.linedefined);
        }
        lv_concat(L, lv_gettop(L) - arg);
    }
    lv_concat(L, lv_gettop(L) - arg);
    return 1;
}

static int db_traceback_count (lv_State *L) {
    lv_Debug ar;
    int index = 1;
    while (lv_getstack(L, index, &ar))
        index++;
    lv_pushnumber( L, index - 1 );
    return 1;
}

static const lvL_Reg dblib[] = {
    {"debug", db_debug},
    {"getfenv", db_getfenv},
    {"gethook", db_gethook},
    {"getinfo", db_getinfo},
    {"getlocal", db_getlocal},
    {"getregistry", db_getregistry},
    {"getmetatable", db_getmetatable},
    {"getupvalue", db_getupvalue},
    {"setfenv", db_setfenv},
    {"sethook", db_sethook},
    {"setlocal", db_setlocal},
    {"setmetatable", db_setmetatable},
    {"setupvalue", db_setupvalue},
    {"traceback", db_errorfb},
    {"traceback_count", db_traceback_count},
    {LUAVIEW_SYS_TABLE_KEY,   db_debug},
    {NULL, NULL}
};

LVLIB_API int lvopen_debug (lv_State *L) {
    lvL_register(L, LV_DBLIBNAME, dblib);
    return 1;
}


int beginDebug( lv_State *L ) {
    lv_getglobal( L, "begin_debug" );
    int error = lv_pcall( L, 0, 0, 0 );
    if ( error )
    {
        printf( "%s/n", lv_tostring( L, -1 ) );
        lv_pop( L, 1 );
    }
    return 0;
}
