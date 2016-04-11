/*
** $Id: lVoadlib.c,v 1.52.1.3 2008/08/06 13:29:28 roberto Exp $
** Dynamic library loader for [L u a]
** See Copyright Notice in lV.h
**
** This module contains an implementation of loadlib for Unix systems
** that have dlfcn, an implementation for Darwin (Mac OS X), an
** implementation for Windows, and a stub for other systems.
*/


#include <stdlib.h>
#include <string.h>


#define loadlib_c
#define LV_LIB

#include "lV.h"

#include "lVauxlib.h"
#include "lVlib.h"


/* prefix for open functions in C libraries */
#define LV_POF		"lvopen_"

/* separator for open functions in C libraries */
#define LV_OFSEP	"_"


#define LIBPREFIX	"LOADLIB: "

#define POF		LV_POF
#define LIB_FAIL	"open"


/* error codes for ll_loadfunc */
#define ERRLIB		1
#define ERRFUNC		2

#define setprogdir(L)		((void)0)


static void ll_unloadlib (void *lib);
static void *ll_load (lv_State *L, const char *path);
static lv_CFunction ll_sym (lv_State *L, void *lib, const char *sym);



#if defined(LV_DL_DLOPEN)
/*
** {========================================================================
** This is an implementation of loadlib based on the dlfcn interface.
** The dlfcn interface is available in Linux, SunOS, Solaris, IRIX, FreeBSD,
** NetBSD, AIX 4.2, HPUX 11, and  probably most other Unix flavors, at least
** as an emulation layer on top of native functions.
** =========================================================================
*/

#include <dlfcn.h>

static void ll_unloadlib (void *lib) {
  dlclose(lib);
}


static void *ll_load (lv_State *L, const char *path) {
  void *lib = dlopen(path, RTLD_NOW);
  if (lib == NULL) lv_pushstring(L, dlerror());
  return lib;
}


static lv_CFunction ll_sym (lv_State *L, void *lib, const char *sym) {
  lv_CFunction f = (lv_CFunction)dlsym(lib, sym);
  if (f == NULL) lv_pushstring(L, dlerror());
  return f;
}

/* }====================================================== */



#elif defined(LV_DL_DLL)
/*
** {======================================================================
** This is an implementation of loadlib for Windows using native functions.
** =======================================================================
*/

#include <windows.h>


#undef setprogdir

static void setprogdir (lv_State *L) {
  char buff[MAX_PATH + 1];
  char *lb;
  DWORD nsize = sizeof(buff)/sizeof(char);
  DWORD n = GetModuleFileNameA(NULL, buff, nsize);
  if (n == 0 || n == nsize || (lb = strrchr(buff, '\\')) == NULL)
    lvL_error(L, "unable to get ModuleFileName");
  else {
    *lb = '\0';
    lvL_gsub(L, lv_tostring(L, -1), LV_EXECDIR, buff);
    lv_remove(L, -2);  /* remove original string */
  }
}


static void pusherror (lv_State *L) {
  int error = GetLastError();
  char buffer[128];
  if (FormatMessageA(FORMAT_MESSAGE_IGNORE_INSERTS | FORMAT_MESSAGE_FROM_SYSTEM,
      NULL, error, 0, buffer, sizeof(buffer), NULL))
    lv_pushstring(L, buffer);
  else
    lv_pushfstring(L, "system error %d\n", error);
}

static void ll_unloadlib (void *lib) {
  FreeLibrary((HINSTANCE)lib);
}


static void *ll_load (lv_State *L, const char *path) {
  HINSTANCE lib = LoadLibraryA(path);
  if (lib == NULL) pusherror(L);
  return lib;
}


static lv_CFunction ll_sym (lv_State *L, void *lib, const char *sym) {
  lv_CFunction f = (lv_CFunction)GetProcAddress((HINSTANCE)lib, sym);
  if (f == NULL) pusherror(L);
  return f;
}

/* }====================================================== */



#elif defined(LV_DL_DYLD)
/*
** {======================================================================
** Native Mac OS X / Darwin Implementation
** =======================================================================
*/

#include <mach-o/dyld.h>


/* Mac appends a `_' before C function names */
#undef POF
#define POF	"_" LV_POF


static void pusherror (lv_State *L) {
  const char *err_str;
  const char *err_file;
  NSLinkEditErrors err;
  int err_num;
  NSLinkEditError(&err, &err_num, &err_file, &err_str);
  lv_pushstring(L, err_str);
}


static const char *errorfromcode (NSObjectFileImageReturnCode ret) {
  switch (ret) {
    case NSObjectFileImageInappropriateFile:
      return "file is not a bundle";
    case NSObjectFileImageArch:
      return "library is for wrong CPU type";
    case NSObjectFileImageFormat:
      return "bad format";
    case NSObjectFileImageAccess:
      return "cannot access file";
    case NSObjectFileImageFailure:
    default:
      return "unable to load library";
  }
}


static void ll_unloadlib (void *lib) {
  NSUnLinkModule((NSModule)lib, NSUNLINKMODULE_OPTION_RESET_LAZY_REFERENCES);
}


static void *ll_load (lv_State *L, const char *path) {
  NSObjectFileImage img;
  NSObjectFileImageReturnCode ret;
  /* this would be a rare case, but prevents crashing if it happens */
  if(!_dyld_present()) {
    lv_pushliteral(L, "dyld not present");
    return NULL;
  }
  ret = NSCreateObjectFileImageFromFile(path, &img);
  if (ret == NSObjectFileImageSuccess) {
    NSModule mod = NSLinkModule(img, path, NSLINKMODULE_OPTION_PRIVATE |
                       NSLINKMODULE_OPTION_RETURN_ON_ERROR);
    NSDestroyObjectFileImage(img);
    if (mod == NULL) pusherror(L);
    return mod;
  }
  lv_pushstring(L, errorfromcode(ret));
  return NULL;
}


static lv_CFunction ll_sym (lv_State *L, void *lib, const char *sym) {
  NSSymbol nss = NSLookupSymbolInModule((NSModule)lib, sym);
  if (nss == NULL) {
    lv_pushfstring(L, "symbol " LV_QS " not found", sym);
    return NULL;
  }
  return (lv_CFunction)NSAddressOfSymbol(nss);
}

/* }====================================================== */



#else
/*
** {======================================================
** Fallback for other systems
** =======================================================
*/

#undef LIB_FAIL
#define LIB_FAIL	"absent"


#define DLMSG	"dynamic libraries not enabled; check your [L u a] installation"


static void ll_unloadlib (void *lib) {
  (void)lib;  /* to avoid warnings */
}


static void *ll_load (lv_State *L, const char *path) {
  (void)path;  /* to avoid warnings */
  lv_pushliteral(L, DLMSG);
  return NULL;
}


static lv_CFunction ll_sym (lv_State *L, void *lib, const char *sym) {
  (void)lib; (void)sym;  /* to avoid warnings */
  lv_pushliteral(L, DLMSG);
  return NULL;
}

/* }====================================================== */
#endif



static void **ll_register (lv_State *L, const char *path) {
  void **plib;
  lv_pushfstring(L, "%s%s", LIBPREFIX, path);
  lv_gettable(L, LV_REGISTRYINDEX);  /* check library in registry? */
  if (!lv_isnil(L, -1))  /* is there an entry? */
    plib = (void **)lv_touserdata(L, -1);
  else {  /* no entry yet; create one */
    lv_pop(L, 1);
    plib = (void **)lv_newuserdata(L, sizeof(const void *));
    *plib = NULL;
    lvL_getmetatable(L, "_LOADLIB");
    lv_setmetatable(L, -2);
    lv_pushfstring(L, "%s%s", LIBPREFIX, path);
    lv_pushvalue(L, -2);
    lv_settable(L, LV_REGISTRYINDEX);
  }
  return plib;
}


/*
** __gc tag method: calls library's `ll_unloadlib' function with the lib
** handle
*/
static int gctm (lv_State *L) {
  void **lib = (void **)lvL_checkudata(L, 1, "_LOADLIB");
  if (*lib) ll_unloadlib(*lib);
  *lib = NULL;  /* mark library as closed */
  return 0;
}


static int ll_loadfunc (lv_State *L, const char *path, const char *sym) {
  void **reg = ll_register(L, path);
  if (*reg == NULL) *reg = ll_load(L, path);
  if (*reg == NULL)
    return ERRLIB;  /* unable to load library */
  else {
    lv_CFunction f = ll_sym(L, *reg, sym);
    if (f == NULL)
      return ERRFUNC;  /* unable to find function */
    lv_pushcfunction(L, f);
    return 0;  /* return function */
  }
}


static int ll_loadlib (lv_State *L) {
  const char *path = lvL_checkstring(L, 1);
  const char *init = lvL_checkstring(L, 2);
  int stat = ll_loadfunc(L, path, init);
  if (stat == 0)  /* no errors? */
    return 1;  /* return the loaded function */
  else {  /* error; error message is on stack top */
    lv_pushnil(L);
    lv_insert(L, -2);
    lv_pushstring(L, (stat == ERRLIB) ?  LIB_FAIL : "init");
    return 3;  /* return nil, error message, and where */
  }
}



/*
** {======================================================
** 'require' function
** =======================================================
*/


static int readable (const char *filename) {
  FILE *f = fopen(filename, "r");  /* try to open file */
  if (f == NULL) return 0;  /* open failed */
  fclose(f);
  return 1;
}


static const char *pushnexttemplate (lv_State *L, const char *path) {
  const char *l;
  while (*path == *LV_PATHSEP) path++;  /* skip separators */
  if (*path == '\0') return NULL;  /* no more templates */
  l = strchr(path, *LV_PATHSEP);  /* find next separator */
  if (l == NULL) l = path + strlen(path);
  lv_pushlstring(L, path, l - path);  /* template */
  return l;
}


static const char *findfile (lv_State *L, const char *name,
                                           const char *pname) {
  const char *path;
  name = lvL_gsub(L, name, ".", LV_DIRSEP);
  lv_getfield(L, LV_ENVIRONINDEX, pname);
  path = lv_tostring(L, -1);
  if (path == NULL)
    lvL_error(L, LV_QL("package.%s") " must be a string", pname);
  lv_pushliteral(L, "");  /* error accumulator */
  while ((path = pushnexttemplate(L, path)) != NULL) {
    const char *filename;
    filename = lvL_gsub(L, lv_tostring(L, -1), LV_PATH_MARK, name);
    lv_remove(L, -2);  /* remove path template */
    if (readable(filename))  /* does file exist and is readable? */
      return filename;  /* return that file name */
    lv_pushfstring(L, "\n\tno file " LV_QS, filename);
    lv_remove(L, -2);  /* remove file name */
    lv_concat(L, 2);  /* add entry to possible error message */
  }
  return NULL;  /* not found */
}


static void loaderror (lv_State *L, const char *filename) {
  lvL_error(L, "error loading module " LV_QS " from file " LV_QS ":\n\t%s",
                lv_tostring(L, 1), filename, lv_tostring(L, -1));
}


static int loader_lv (lv_State *L) {
  const char *filename;
  const char *name = lvL_checkstring(L, 1);
  filename = findfile(L, name, "path");
  if (filename == NULL) return 1;  /* library not found in this path */
  if (lvL_loadfile(L, filename) != 0)
    loaderror(L, filename);
  return 1;  /* library loaded successfully */
}


static const char *mkfuncname (lv_State *L, const char *modname) {
  const char *funcname;
  const char *mark = strchr(modname, *LV_IGMARK);
  if (mark) modname = mark + 1;
  funcname = lvL_gsub(L, modname, ".", LV_OFSEP);
  funcname = lv_pushfstring(L, POF"%s", funcname);
  lv_remove(L, -2);  /* remove 'gsub' result */
  return funcname;
}


static int loader_C (lv_State *L) {
  const char *funcname;
  const char *name = lvL_checkstring(L, 1);
  const char *filename = findfile(L, name, "cpath");
  if (filename == NULL) return 1;  /* library not found in this path */
  funcname = mkfuncname(L, name);
  if (ll_loadfunc(L, filename, funcname) != 0)
    loaderror(L, filename);
  return 1;  /* library loaded successfully */
}


static int loader_Croot (lv_State *L) {
  const char *funcname;
  const char *filename;
  const char *name = lvL_checkstring(L, 1);
  const char *p = strchr(name, '.');
  int stat;
  if (p == NULL) return 0;  /* is root */
  lv_pushlstring(L, name, p - name);
  filename = findfile(L, lv_tostring(L, -1), "cpath");
  if (filename == NULL) return 1;  /* root not found */
  funcname = mkfuncname(L, name);
  if ((stat = ll_loadfunc(L, filename, funcname)) != 0) {
    if (stat != ERRFUNC) loaderror(L, filename);  /* real error */
    lv_pushfstring(L, "\n\tno module " LV_QS " in file " LV_QS,
                       name, filename);
    return 1;  /* function not found */
  }
  return 1;
}


static int loader_preload (lv_State *L) {
  const char *name = lvL_checkstring(L, 1);
  lv_getfield(L, LV_ENVIRONINDEX, "preload");
  if (!lv_istable(L, -1))
    lvL_error(L, LV_QL("package.preload") " must be a table");
  lv_getfield(L, -1, name);
  if (lv_isnil(L, -1))  /* not found? */
    lv_pushfstring(L, "\n\tno field package.preload['%s']", name);
  return 1;
}


static const int sentinel_ = 0;
#define sentinel	((void *)&sentinel_)


static int ll_require (lv_State *L) {
  const char *name = lvL_checkstring(L, 1);
  int i;
  lv_settop(L, 1);  /* _LOADED table will be at index 2 */
  lv_getfield(L, LV_REGISTRYINDEX, "_LOADED");
  lv_getfield(L, 2, name);
  if (lv_toboolean(L, -1)) {  /* is it there? */
    if (lv_touserdata(L, -1) == sentinel)  /* check loops */
      lvL_error(L, "loop or previous error loading module " LV_QS, name);
    return 1;  /* package is already loaded */
  }
  /* else must load it; iterate over available loaders */
  lv_getfield(L, LV_ENVIRONINDEX, "loaders");
  if (!lv_istable(L, -1))
    lvL_error(L, LV_QL("package.loaders") " must be a table");
  lv_pushliteral(L, "");  /* error message accumulator */
  for (i=1; ; i++) {
    lv_rawgeti(L, -2, i);  /* get a loader */
    if (lv_isnil(L, -1))
      lvL_error(L, "module " LV_QS " not found:%s",
                    name, lv_tostring(L, -2));
    lv_pushstring(L, name);
    lv_call(L, 1, 1);  /* call it */
    if (lv_isfunction(L, -1))  /* did it find module? */
      break;  /* module loaded successfully */
    else if (lv_isstring(L, -1))  /* loader returned error message? */
      lv_concat(L, 2);  /* accumulate it */
    else
      lv_pop(L, 1);
  }
  lv_pushlightuserdata(L, sentinel);
  lv_setfield(L, 2, name);  /* _LOADED[name] = sentinel */
  lv_pushstring(L, name);  /* pass name as argument to module */
  int status = lv_pcall(L, 1, 1, 0);  /* run loaded module */
  /* error occured? */
  if (status != 0) {
    printf("require %s error: %s\n", name, lv_tostring(L, lv_gettop(L))); /* dirty code */
    return 0;  /* return nil */
  }
  if (!lv_isnil(L, -1))  /* non-nil return? */
    lv_setfield(L, 2, name);  /* _LOADED[name] = returned value */
  lv_getfield(L, 2, name);
  if (lv_touserdata(L, -1) == sentinel) {   /* module did not set a value? */
    lv_pushboolean(L, 1);  /* use true as result */
    lv_pushvalue(L, -1);  /* extra copy to be returned */
    lv_setfield(L, 2, name);  /* _LOADED[name] = true */
  }
  return 1;
}

/* }====================================================== */



/*
** {======================================================
** 'module' function
** =======================================================
*/
  

static void setfenv (lv_State *L) {
  lv_Debug ar;
  if (lv_getstack(L, 1, &ar) == 0 ||
      lv_getinfo(L, "f", &ar) == 0 ||  /* get calling function */
      lv_iscfunction(L, -1))
    lvL_error(L, LV_QL("module") " not called from a [L u a] function");
  lv_pushvalue(L, -2);
  lv_setfenv(L, -2);
  lv_pop(L, 1);
}


static void dooptions (lv_State *L, int n) {
  int i;
  for (i = 2; i <= n; i++) {
    lv_pushvalue(L, i);  /* get option (a function) */
    lv_pushvalue(L, -2);  /* module */
    lv_call(L, 1, 0);
  }
}


static void modinit (lv_State *L, const char *modname) {
  const char *dot;
  lv_pushvalue(L, -1);
  lv_setfield(L, -2, "_M");  /* module._M = module */
  lv_pushstring(L, modname);
  lv_setfield(L, -2, "_NAME");
  dot = strrchr(modname, '.');  /* look for last dot in module name */
  if (dot == NULL) dot = modname;
  else dot++;
  /* set _PACKAGE as package name (full module name minus last part) */
  lv_pushlstring(L, modname, dot - modname);
  lv_setfield(L, -2, "_PACKAGE");
}


static int ll_module (lv_State *L) {
  const char *modname = lvL_checkstring(L, 1);
  int loaded = lv_gettop(L) + 1;  /* index of _LOADED table */
  lv_getfield(L, LV_REGISTRYINDEX, "_LOADED");
  lv_getfield(L, loaded, modname);  /* get _LOADED[modname] */
  if (!lv_istable(L, -1)) {  /* not found? */
    lv_pop(L, 1);  /* remove previous result */
    /* try global variable (and create one if it does not exist) */
    if (lvL_findtable(L, LV_GLOBALSINDEX, modname, 1) != NULL)
      return lvL_error(L, "name conflict for module " LV_QS, modname);
    lv_pushvalue(L, -1);
    lv_setfield(L, loaded, modname);  /* _LOADED[modname] = new table */
  }
  /* check whether table already has a _NAME field */
  lv_getfield(L, -1, "_NAME");
  if (!lv_isnil(L, -1))  /* is table an initialized module? */
    lv_pop(L, 1);
  else {  /* no; initialize it */
    lv_pop(L, 1);
    modinit(L, modname);
  }
  lv_pushvalue(L, -1);
  setfenv(L);
  dooptions(L, loaded - 1);
  return 0;
}


static int ll_seeall (lv_State *L) {
  lvL_checktype(L, 1, LV_TTABLE);
  if (!lv_getmetatable(L, 1)) {
    lv_createtable(L, 0, 1); /* create new metatable */
    lv_pushvalue(L, -1);
    lv_setmetatable(L, 1);
  }
  lv_pushvalue(L, LV_GLOBALSINDEX);
  lv_setfield(L, -2, "__index");  /* mt.__index = _G */
  return 0;
}


/* }====================================================== */



/* auxiliary mark (for internal use) */
#define AUXMARK		"\1"

static void setpath (lv_State *L, const char *fieldname, const char *envname,
                                   const char *def) {
  const char *path = getenv(envname);
  if (path == NULL)  /* no environment variable? */
    lv_pushstring(L, def);  /* use default */
  else {
    /* replace ";;" by ";AUXMARK;" and then AUXMARK by default path */
    path = lvL_gsub(L, path, LV_PATHSEP LV_PATHSEP,
                              LV_PATHSEP AUXMARK LV_PATHSEP);
    lvL_gsub(L, path, AUXMARK, def);
    lv_remove(L, -2);
  }
  setprogdir(L);
  lv_setfield(L, -2, fieldname);
}


static const lvL_Reg pk_funcs[] = {
  {"loadlib", ll_loadlib},
  {"seeall", ll_seeall},
  {NULL, NULL}
};


static const lvL_Reg ll_funcs[] = {
  {"module", ll_module},
  {"require", ll_require},
  {NULL, NULL}
};


static const lv_CFunction loaders[] =
  {loader_preload, loader_lv, loader_C, loader_Croot, NULL};


LVLIB_API int lvopen_package (lv_State *L) {
  int i;
  /* create new type _LOADLIB */
  lvL_newmetatable(L, "_LOADLIB");
  lv_pushcfunction(L, gctm);
  lv_setfield(L, -2, "__gc");
  /* create `package' table */
  lvL_register(L, LV_LOADLIBNAME, pk_funcs);
#if defined(LV_COMPAT_LOADLIB) 
  lv_getfield(L, -1, "loadlib");
  lv_setfield(L, LV_GLOBALSINDEX, "loadlib");
#endif
  lv_pushvalue(L, -1);
  lv_replace(L, LV_ENVIRONINDEX);
  /* create `loaders' table */
  lv_createtable(L, 0, sizeof(loaders)/sizeof(loaders[0]) - 1);
  /* fill it with pre-defined loaders */
  for (i=0; loaders[i] != NULL; i++) {
    lv_pushcfunction(L, loaders[i]);
    lv_rawseti(L, -2, i+1);
  }
  lv_setfield(L, -2, "loaders");  /* put it in field `loaders' */
  setpath(L, "path", LV_PATH, LV_PATH_DEFAULT);  /* set field `path' */
  setpath(L, "cpath", LV_CPATH, LV_CPATH_DEFAULT); /* set field `cpath' */
  /* store config information */
  lv_pushliteral(L, LV_DIRSEP "\n" LV_PATHSEP "\n" LV_PATH_MARK "\n"
                     LV_EXECDIR "\n" LV_IGMARK);
  lv_setfield(L, -2, "config");
  /* set field `loaded' */
  lvL_findtable(L, LV_REGISTRYINDEX, "_LOADED", 2);
  lv_setfield(L, -2, "loaded");
  /* set field `preload' */
  lv_newtable(L);
  lv_setfield(L, -2, "preload");
  lv_pushvalue(L, LV_GLOBALSINDEX);
  lvL_register(L, NULL, ll_funcs);  /* open lib into global table */
  lv_pop(L, 1);
  return 1;  /* return 'package' table */
}

