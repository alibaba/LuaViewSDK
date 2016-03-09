--- Lua global functions.

module 'global'

---
-- Issues an error when the value of its argument `v` is false (i.e.,
-- nil or false); otherwise, returns all its arguments. `message` is an error
-- message; when absent, it defaults to "assertion failed!"
function assert(v , message) end

---
-- This function is a generic interface to the garbage collector. It
-- performs different functions according to its first argument, `opt`:
--
--   * "stop": stops the garbage collector.
--   * "restart": restarts the garbage collector.
--   * "collect": performs a full garbage-collection cycle.
--   * "count": returns the total memory in use by Lua (in Kbytes).
--   * "step": performs a garbage-collection step. The step "size" is controlled
--   by `arg` (larger values mean more steps) in a non-specified way. If you
--   want to control the step size you must experimentally tune the value of
--   * "arg". Returns true if the step finished a collection cycle.
--   * "setpause": sets `arg` as the new value for the *pause* of the collector
--   (see §2.10). Returns the previous value for *pause*.
--   * "setstepmul": sets `arg` as the new value for the *step multiplier*
--   of the collector (see §2.10). Returns the previous value for *step*.
--
function collectgarbage(opt , arg) end

---
-- Opens the named file and executes its contents as a Lua chunk. When
-- called without arguments,
-- `dofile` executes the contents of the standard input (`stdin`). Returns
-- all values returned by the chunk. In case of errors, `dofile` propagates
-- the error to its caller (that is, `dofile` does not run in protected mode).
function dofile(filename) end

---
-- Terminates the last protected function called and returns `message`
-- as the error message. Function `error` never returns.
-- Usually, `error` adds some information about the error position at the
-- beginning of the message. The `level` argument specifies how to get the
-- error position. With level 1 (the default), the error position is where the
-- `error` function was called. Level 2 points the error to where the function
-- that called `error` was called; and so on. Passing a level 0 avoids the
-- addition of error position information to the message.
function error(message , level) end

---
-- A global variable (not a function) that holds the global environment
-- (that is, `_G._G = _G`). Lua itself does not use this variable; changing
-- its value does not affect any environment, nor vice-versa. (Use `setfenv`
-- to change environments.)
-- function _G end
-- * `_G._G`: _G._G

---
-- Returns the current environment in use by the function.
-- `f` can be a Lua function or a number that specifies the function at that
-- stack level: Level 1 is the function calling `getfenv`. If the given
-- function is not a Lua function, or if `f` is 0, `getfenv` returns the
-- global environment. The default for `f` is 1.
function getfenv(f) end

---
-- If `object` does not have a metatable, returns nil. Otherwise, if the
-- object's metatable has a `"__metatable"` field, returns the associated
-- value. Otherwise, returns the metatable of the given object.
function getmetatable(object) end

---
-- Returns three values: an iterator function, the table `t`, and 0,
-- so that the construction
--   for i,v in ipairs(t) do *body* end
-- will iterate over the pairs (`1,t[1]`), (`2,t[2]`), ..., up to the
-- first integer key absent from the table.
function ipairs(t) end

---
-- Loads a chunk using function `func` to get its pieces. Each call to
-- `func` must return a string that concatenates with previous results. A
-- return of an empty string, nil, or no value signals the end of the chunk.
-- If there are no errors, returns the compiled chunk as a function; otherwise,
-- returns nil plus the error message. The environment of the returned function
-- is the global environment.
-- `chunkname` is used as the chunk name for error messages and debug
-- information. When absent, it defaults to "`=(load)`".
function load(func , chunkname) end

---
-- Similar to `load`, but gets the chunk from file `filename` or from the
-- standard input, if no file name is given.
function loadfile(filename) end

---
-- Similar to `load`, but gets the chunk from the given string.
-- To load and run a given string, use the idiom
--   assert(loadstring(s))()
-- When absent, `chunkname` defaults to the given string.
function loadstring(string , chunkname) end

---
-- Allows a program to traverse all fields of a table. Its first argument is
-- a table and its second argument is an index in this table. `next` returns
-- the next index of the table and its associated value.
--
-- When called with nil
-- as its second argument, `next` returns an initial index and its associated
-- value. When called with the last index, or with nil in an empty table, `next`
-- returns nil.
--
-- If the second argument is absent, then it is interpreted as
-- nil. In particular, you can use `next(t)` to check whether a table is empty.
-- The order in which the indices are enumerated is not specified, *even for
-- numeric indices*. (To traverse a table in numeric order, use a numerical
-- for or the `ipairs` function.)
--
-- The behavior of `next` is *undefined* if, during the traversal, you assign
-- any value to a non-existent field in the table. You may however modify
-- existing fields. In particular, you may clear existing fields.
function next(table , index) end

---
-- Returns three values: the `next` function, the table `t`, and nil,
-- so that the construction
--   for k,v in pairs(t) do *body* end
-- will iterate over all key–value pairs of table `t`.
-- See function `next` for the caveats of modifying the table during its
-- traversal.
function pairs(t) end

---
-- Calls function `f` with the given arguments in *protected mode*. This
-- means that any error inside `f` is not propagated; instead, `pcall` catches
-- the error and returns a status code. Its first result is the status code (a
-- boolean), which is true if the call succeeds without errors. In such case,
-- `pcall` also returns all results from the call, after this first result. In
-- case of any error, `pcall` returns false plus the error message.
function pcall(f, arg1, ...) end

---
-- Receives any number of arguments, and prints their values to `stdout`,
-- using the `tostring` function to convert them to strings. `print` is not
-- intended for formatted output, but only as a quick way to show a value,
-- typically for debugging. For formatted output, use `string.format`.
function print(...) end

---
-- Checks whether `v1` is equal to `v2`, without invoking any
-- metamethod. Returns a boolean.
function rawequal(v1, v2) end

---
-- Gets the real value of `table[index]`, without invoking any
-- metamethod. `table` must be a table; `index` may be any value.
function rawget(table, index) end

---
-- Sets the real value of `table[index]` to `value`, without invoking any
-- metamethod. `table` must be a table, `index` any value different from nil,
-- and `value` any Lua value.
-- This function returns `table`.
function rawset(table, index, value) end

---
-- If `index` is a number, returns all arguments after argument number
-- `index`. Otherwise, `index` must be the string `"#"`, and `select` returns
-- the total number of extra arguments it received.
function select(index, ...) end

---
-- Sets the environment to be used by the given function. `f` can be a Lua
-- function or a number that specifies the function at that stack level: Level
-- 1 is the function calling `setfenv`. `setfenv` returns the given function.
-- As a special case, when `f` is 0 `setfenv` changes the environment of the
-- running thread. In this case, `setfenv` returns no values.
function setfenv(f, table) end

---
-- Sets the metatable for the given table. (You cannot change the metatable
-- of other types from Lua, only from C.) If `metatable` is nil, removes the
-- metatable of the given table. If the original metatable has a `"__metatable"`
-- field, raises an error.
-- This function returns `table`.
function setmetatable(table, metatable) end

---
-- Tries to convert its argument to a number. If the argument is already
-- a number or a string convertible to a number, then `tonumber` returns this
-- number; otherwise, it returns nil.
-- An optional argument specifies the base to interpret the numeral. The base
-- may be any integer between 2 and 36, inclusive. In bases above 10, the
-- letter '`A`' (in either upper or lower case) represents 10, '`B`' represents
-- 11, and so forth, with '`Z`' representing 35. In base 10 (the default),
-- the number can have a decimal part, as well as an optional exponent part
-- (see §2.1). In other bases, only unsigned integers are accepted.
function tonumber(e , base) end

---
-- Receives an argument of any type and converts it to a string in a
-- reasonable format. For complete control of how numbers are converted, use
-- `string.format`.
-- If the metatable of `e` has a `"__tostring"` field, then `tostring` calls
-- the corresponding value with `e` as argument, and uses the result of the
-- call as its result.
function tostring(e) end

---
-- Returns the type of its only argument, coded as a string. The possible
-- results of this function are "
-- `nil`" (a string, not the value nil), "`number`", "`string`", "`boolean`",
-- "`table`", "`function`", "`thread`", and "`userdata`".
function type(v) end

---
-- Returns the elements from the given table. This function is equivalent to
--   return list[i], list[i+1], ..., list[j]
-- except that the above code can be written only for a fixed number of
-- elements. By default, `i` is 1 and `j` is the length of the list, as
-- defined by the length operator (see §2.5.5).
function unpack(list , i , j) end

---
-- A global variable (not a function) that holds a string containing the
-- current interpreter version. The current contents of this variable is
-- "`Lua 5.1`".
-- function _VERSION end
-- * `_G._VERSION`: _G._VERSION

---
-- This function is similar to `pcall`, except that you can set a new
-- error handler.
-- `xpcall` calls function `f` in protected mode, using `err` as the error
-- handler. Any error inside `f` is not propagated; instead, `xpcall` catches
-- the error, calls the `err` function with the original error object, and
-- returns a status code. Its first result is the status code (a boolean),
-- which is true if the call succeeds without errors. In this case, `xpcall`
-- also returns all results from the call, after this first result. In case
-- of any error, `xpcall` returns false plus the result from `err`.
function xpcall(f, err) end

---
-- Creates a module. If there is a table in `package.loaded[name]`,
-- this table is the module. Otherwise, if there is a global table `t`
-- with the given name, this table is the module. Otherwise creates a new
-- table `t` and sets it as the value of the global `name` and the value of
-- `package.loaded[name]`. This function also initializes `t._NAME` with the
-- given name, `t._M` with the module (`t` itself), and `t._PACKAGE` with the
-- package name (the full module name minus last component; see below). Finally,
-- `module` sets `t` as the new environment of the current function and the
-- new value of `package.loaded[name]`, so that `require` returns `t`.
-- If `name` is a compound name (that is, one with components separated by
-- dots), `module` creates (or reuses, if they already exist) tables for each
-- component. For instance, if `name` is `a.b.c`, then `module` stores the
-- module table in field `c` of field `b` of global `a`.
-- This function can receive optional *options* after the module name, where
-- each option is a function to be applied over the module.
function module(name , ...) end

---
-- Loads the given module. The function starts by looking into the
-- `package.loaded` table to determine whether `modname` is already
-- loaded. If it is, then `require` returns the value stored at
-- `package.loaded[modname]`. Otherwise, it tries to find a *loader* for
-- the module.
-- To find a loader, `require` is guided by the `package.loaders` array. By
-- changing this array, we can change how `require` looks for a module. The
-- following explanation is based on the default configuration for
-- `package.loaders`.
-- First `require` queries `package.preload[modname]`. If it has a value,
-- this value (which should be a function) is the loader. Otherwise `require`
-- searches for a Lua loader using the path stored in `package.path`. If
-- that also fails, it searches for a C loader using the path stored in
-- `package.cpath`. If that also fails, it tries an *all-in-one* loader (see
-- `package.loaders`).
-- Once a loader is found, `require` calls the loader with a single argument,
-- `modname`. If the loader returns any value, `require` assigns the returned
-- value to `package.loaded[modname]`. If the loader returns no value and
-- has not assigned any value to `package.loaded[modname]`, then `require`
-- assigns true to this entry. In any case, `require` returns the final value
-- of `package.loaded[modname]`.
-- If there is any error loading or running the module, or if it cannot find
-- any loader for the module, then `require` signals an error.
function require(modname) end

