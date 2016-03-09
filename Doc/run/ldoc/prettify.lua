-- Making Lua source code look pretty.
-- A simple scanner based prettifier, which scans comments for @{ref} and code
-- for known modules and functions.
-- A module reference to an example `test-fun.lua` would look like
-- `@{example:test-fun}`.
local List = require 'pl.List'
local tablex = require 'pl.tablex'
local globals = require 'ldoc.builtin.globals'
local prettify = {}

local user_keywords = {}

local escaped_chars = {
   ['&'] = '&amp;',
   ['<'] = '&lt;',
   ['>'] = '&gt;',
}
local escape_pat = '[&<>]'

local function escape(str)
   return (str:gsub(escape_pat,escaped_chars))
end

local function span(t,val)
   return ('<span class="%s">%s</span>'):format(t,val)
end

local spans = {keyword=true,number=true,string=true,comment=true,global=true,backtick=true}

local cpp_lang = {C = true, c = true, cpp = true, cxx = true, h = true}

function prettify.lua (lang, fname, code, initial_lineno, pre, linenos)
   local res, lexer, tokenizer = List(), require 'ldoc.lexer'
   local tnext = lexer.skipws
   local ik = 1
   if not cpp_lang[lang] then
      tokenizer = lexer.lua
   else
      tokenizer = lexer.cpp
   end

   if pre then
      res:append '<pre>\n'
   end
   initial_lineno = initial_lineno or 0

   local tok = tokenizer(code,{},{})
   local error_reporter = {
      warning = function (self,msg)
         io.stderr:write(fname..':'..tok:lineno()+initial_lineno..': '..msg,'\n')
      end
   }
   local t,val = tok()
   if not t then return nil,"empty file" end
   while t do
      val = escape(val)
      if linenos and tok:lineno() == linenos[ik] then
         res:append('<a id="'..linenos[ik]..'"></a>')
         ik = ik + 1
      end
      if globals.functions[val] or globals.tables[val] then
         t = 'global'
      end
      if user_keywords[val] then
        res:append(span('user-keyword keyword-' .. val,val))
      elseif spans[t] then
         if t == 'comment' or t == 'backtick' then -- may contain @{ref} or `..`
            val = prettify.resolve_inline_references(val,error_reporter)
         end
         res:append(span(t,val))
      else
         res:append(val)
      end
      t,val = tok()
   end
   local last = res[#res]
   if last:match '\n$' then
      res[#res] = last:gsub('\n+','')
   end
   if pre then
      res:append '</pre>\n'
   end
   return res:join ()
end

local lxsh

local lxsh_highlighers = {bib=true,c=true,lua=true,sh=true}

function prettify.code (lang,fname,code,initial_lineno,pre)
   if not lxsh then
      return prettify.lua (lang,fname, code, initial_lineno, pre)
   else
      if not lxsh_highlighers[lang] then
         lang = 'lua'
      end
      code = lxsh.highlighters[lang](code, {
         formatter = lxsh.formatters.html,
         external = true
      })
      if not pre then
         code = code:gsub("^<pre.->(.-)%s*</pre>$", '%1')
      end
      return code
   end
end

function prettify.set_prettifier (pretty)
   local ok
   if pretty == 'lxsh' then
      ok,lxsh = pcall(require,'lxsh')
      if not ok then
         print('pretty: '..pretty..' not found, using built-in Lua')
         lxsh = nil
      end
   end
end

function prettify.set_user_keywords(keywords)
  if keywords then
    user_keywords = tablex.makeset(keywords)
  end
end

return prettify

