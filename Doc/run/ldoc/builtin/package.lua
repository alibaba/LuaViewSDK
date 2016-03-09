--- controlling how `require` finds packages.
-- @module package

local package = {}

---
-- The path used by `require` to search for a C loader.
-- Lua initializes the C path `package.cpath` in the same way it initializes
-- the Lua path `package.path`, using the environment variable `LUA_CPATH`
-- or a default path defined in `luaconf.h`.
-- function package.cpath end
-- * `package.cpath`: package.cpath

---
-- A table used by `require` to control which modules are already
-- loaded. When you require a module `modname` and `package.loaded[modname]`
-- is not false, `require` simply returns the value stored there.
-- function package.loaded end
-- * `package.loaded`: package.loaded

---
-- A table used by `require` to control how to load modules.
-- Each entry in this table is a *searcher function*. When looking for a module,
-- `require` calls each of these searchers in ascending order, with the module
-- name (the argument given to `require`) as its sole parameter. The function
-- can return another function (the module *loader*) or a string explaining
-- why it did not find that module (or nil if it has nothing to say). Lua
-- initializes this table with four functions.
-- The first searcher simply looks for a loader in the `package.preload` table.
-- The second searcher looks for a loader as a Lua library, using the path
-- stored at `package.path`. A path is a sequence of *templates* separated by
-- semicolons. For each template, the searcher will change each interrogation
-- mark in the template by `filename`, which is the module name with each dot
-- replaced by a "directory separator" (such as "`/`" in Unix); then it will
-- try to open the resulting file name. So, for instance, if the Lua path is
-- the string
--   "./?.lua;./?.lc;/usr/local/?/init.lua"
-- the search for a Lua file for module `foo` will try to open the files
-- `./foo.lua`, `./foo.lc`, and `/usr/local/foo/init.lua`, in that order.
-- The third searcher looks for a loader as a C library, using the path given
-- by the variable `package.cpath`. For instance, if the C path is the string
--   "./?.so;./?.dll;/usr/local/?/init.so"
-- the searcher for module `foo` will try to open the files `./foo.so`,
-- `./foo.dll`, and `/usr/local/foo/init.so`, in that order. Once it finds
-- a C library, this searcher first uses a dynamic link facility to link the
-- application with the library. Then it tries to find a C function inside the
-- library to be used as the loader. The name of this C function is the string
-- "`luaopen_`" concatenated with a copy of the module name where each dot
-- is replaced by an underscore. Moreover, if the module name has a hyphen,
-- its prefix up to (and including) the first hyphen is removed. For instance,
-- if the module name is `a.v1-b.c`, the function name will be `luaopen_b_c`.
-- The fourth searcher tries an *all-in-one loader*. It searches the C
-- path for a library for the root name of the given module. For instance,
-- when requiring `a.b.c`, it will search for a C library for `a`. If found,
-- it looks into it for an open function for the submodule; in our example,
-- that would be `luaopen_a_b_c`. With this facility, a package can pack
-- several C submodules into one single library, with each submodule keeping
-- its original open function.
-- function package.loaders end
-- * `package.loaders`: package.loaders

---
-- Dynamically links the host program with the C library `libname`. Inside
-- this library, looks for a function `funcname` and returns this function as a
-- C function. (So, `funcname` must follow the protocol (see `lua_CFunction`)).
-- This is a low-level function. It completely bypasses the package and module
-- system. Unlike `require`, it does not perform any path searching and does
-- not automatically adds extensions. `libname` must be the complete file name
-- of the C library, including if necessary a path and extension. `funcname`
-- must be the exact name exported by the C library (which may depend on the
-- C compiler and linker used).
-- This function is not supported by ANSI C. As such, it is only available
-- on some platforms (Windows, Linux, Mac OS X, Solaris, BSD, plus other Unix
-- systems that support the `dlfcn` standard).
function package.loadlib(libname, funcname) end

---
-- The path used by `require` to search for a Lua loader.
-- At start-up, Lua initializes this variable with the value of the environment
-- variable `LUA_PATH` or with a default path defined in `luaconf.h`, if
-- the environment variable is not defined. Any "`;;`" in the value of the
-- environment variable is replaced by the default path.
-- function package.path end
-- * `package.path`: package.path

---
-- A table to store loaders for specific modules (see `require`).
-- function package.preload end
-- * `package.preload`: package.preload

---
-- Sets a metatable for `module` with its `__index` field referring to the
-- global environment, so that this module inherits values from the global
-- environment. To be used as an option to function `module`.
function package.seeall(module) end

return package
