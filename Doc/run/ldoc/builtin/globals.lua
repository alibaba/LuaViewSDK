-------
-- global functions and tables
local tools = require 'ldoc.tools'
local globals = {}
local lua52 = _VERSION:match '5.2'


globals.functions = {
   assert = true,
   collectgarbage = true,
   dofile = true,
   getmetatable = true,
   setmetatable = true,
   pairs = true,
   ipairs = true,
   load = true,
   loadfile = true,
   loadstring = true,
   next = true,
   pcall = true,
   print = true,
   rawequal = true,
   rawget = true,
   rawset = true,
   select = true,
   tonumber = true,
   tostring = true,
   type = true,
   xpcall = true,
   module = true,
   require = true,
}
local functions = globals.functions

if not lua52 then
   functions.setfenv = true
   functions.getfenv = true
   functions.unpack = true
else
   functions.rawlen = true
end

local manual, fun_ref

function globals.set_manual_url(url)
   manual = url .. '#'
   fun_ref = manual..'pdf-'
end

if lua52 then
   globals.tables = {
      io = '6.8',
      package = '6.3',
      math = '6.6',
      os = '6.9',
      string = '6.4',
      table = '6.5',
      coroutine = '6.2',
      debug = '6.10'
    }
   globals.set_manual_url 'http://www.lua.org/manual/5.2/manual.html'
else
   globals.tables = {
      io = '5.7',
      package = '5.3',
      math = '5.6',
      os = '5.8',
      string = '5.4',
      table = '5.5',
      coroutine = '5.2',
      debug = '5.9'
   }
   globals.set_manual_url 'http://www.lua.org/manual/5.1/manual.html'
end

local file_methods = {
    close = true,
    flush = true,
    lines = true,
    read = true,
    seek = true,
    setvbuf = true,
    write = true,
}

-- external libs tracked by LDoc using LDoc style
local xlibs = {
   lfs='lfs.html', lpeg='lpeg.html',
}
local xlib_url = 'http://stevedonovan.github.io/lua-stdlibs/modules/'

local tables = globals.tables

local function function_ref (name,tbl)
   local href
   if not tbl then -- can only be a standard Lua global function
      if globals.functions[name] then
        return {href = fun_ref..name, label = name}
      else
         return nil
      end
   end
   if tbl == 'file' then -- special case: file objects!
      if not file_methods[name] then
        return nil
      end
      name = 'file:'..name
      href = fun_ref..name
   elseif tables[tbl] then -- function inside standard Lua table
      local t = rawget(_G,tbl) -- do a quick sanity check
      if not rawget(t,name) then
         return nil
      end
      name = tbl..'.'..name
      href = fun_ref..name
   elseif xlibs[tbl] then -- in external libs, use LDoc style
      local t = require('ldoc.builtin.'..tbl)
      if not rawget(t,name) then
         return nil
      end
      href = xlib_url..xlibs[tbl]..'#'..name
      name = tbl..'.'..name
   else
      return nil
   end
   return {href = href, label = name}
end

local function module_ref (tbl)
   local href
   if tables[tbl] ~= nil then -- standard Lua table
      href = manual..tables[tbl]
   elseif xlibs[tbl] then -- external lib
      href = xlib_url..xlibs[tbl]
   else
      return nil
   end
   -- print(label, href, tbl); -- dongxicheng 修改table跳转
   href = "../全局函数/table.html";
   return {href = href, label = tbl}
end

function globals.lua_manual_ref (name)
   local tbl,fname = tools.split_dotted_name(name)
   local ref
   if not tbl then -- plain symbol
      ref = function_ref(name)
      if ref then return ref end
      ref = module_ref(name)
      if ref then return ref end
   else
      ref = function_ref(fname,tbl)
      if ref then return ref end
   end
   return nil
end

return globals
