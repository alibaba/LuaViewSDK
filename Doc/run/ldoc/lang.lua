------------
-- Language-dependent parsing of code.
-- This encapsulates the different strategies needed for parsing C and Lua
-- source code.

local class = require 'pl.class'
local utils = require 'pl.utils'
local List =  require 'pl.List'
local tools = require 'ldoc.tools'
local lexer = require 'ldoc.lexer'
local quit = utils.quit
local tnext = lexer.skipws


local Lang = class()

function Lang:trim_comment (s)
   return s:gsub(self.line_comment,'')
end

function Lang:start_comment (v)
   local line = v:match (self.start_comment_)
   if line and self.end_comment_ and v:match (self.end_comment_) then
      return nil
   end
   local block = v:match(self.block_comment)
   return line or block, block
end

function Lang:empty_comment (v)
   return v:match(self.empty_comment_)
end

function Lang:grab_block_comment(v,tok)
   v = v:gsub(self.block_comment,'')
   return tools.grab_block_comment(v,tok,self.end_comment)
end

function Lang:find_module(tok,t,v)
   return '...',t,v
end

function Lang:item_follows(t,v)
   return false
end

function Lang:finalize()
   self.empty_comment_ = self.start_comment_..'%s*$'
end

function Lang:search_for_token (tok,type,value,t,v)
   while t and not (t == type and v == value) do
      if t == 'comment' and self:start_comment(v) then return nil,t,v end
      t,v = tnext(tok)
   end
   return t ~= nil,t,v
end

function Lang:parse_extra (tags,tok)
end

function Lang:is_module_modifier ()
   return false
end

function Lang:parse_module_modifier (tags, tok)
   return nil, "@usage or @exports deduction not implemented for this language"
end


local Lua = class(Lang)

function Lua:_init()
   self.line_comment = '^%-%-+' -- used for stripping
   self.start_comment_ = '^%-%-%-+'     -- used for doc comment line start
   self.block_comment = '^%-%-%[=*%[%-+' -- used for block doc comments
   self.end_comment_ = '[^%-]%-%-+[^-]*\n$' ---- exclude --- this kind of comment ---
   self.method_call = ':'
   self:finalize()
end

function Lua.lexer(fname)
   local f,e = io.open(fname)
   if not f then quit(e) end
   return lexer.lua(f,{}),f
end

function Lua:grab_block_comment(v,tok)
   local equals = v:match('^%-%-%[(=*)%[')
   if not equals then return v end
   v = v:gsub(self.block_comment,'')
   return tools.grab_block_comment(v,tok,'%]'..equals..'%]')
end


function Lua:parse_module_call(tok,t,v)
   t,v = tnext(tok)
   if t == '(' then t,v = tnext(tok) end
   if t == 'string' then -- explicit name, cool
      return v,t,v
   elseif t == '...' then -- we have to guess!
      return '...',t,v
   end
end

-- If a module name was not provided, then we look for an explicit module()
-- call. However, we should not try too hard; if we hit a doc comment then
-- we should go back and process it. Likewise, module(...) also means
-- that we must infer the module name.
function Lua:find_module(tok,t,v)
   local res
   res,t,v = self:search_for_token(tok,'iden','module',t,v)
   if not res then return nil,t,v end
   return self:parse_module_call(tok,t,v)
end

local function parse_lua_parameters (tags,tok)
   tags.formal_args = tools.get_parameters(tok)
   tags:add('class','function')
end

local function parse_lua_function_header (tags,tok)
   if not tags.name then
      tags:add('name',tools.get_fun_name(tok))
   end
   if not tags.name then return 'function has no name' end
   parse_lua_parameters(tags,tok)
end

local function parse_lua_table (tags,tok)
   tags.formal_args = tools.get_parameters(tok,'}',function(s)
      return s == ',' or s == ';'
   end)
end

--------------- function and variable inferrence -----------
-- After a doc comment, there may be a local followed by:
-- [1] (l)function: function NAME
-- [2] (l)function: NAME = function
-- [3] table: NAME = {
-- [4] field: NAME = <anything else>  (this is a module-level field)
--
-- Depending on the case successfully detected, returns a function which
-- will be called later to fill in inferred item tags
function Lua:item_follows(t,v,tok)
   local parser, case
   local is_local = t == 'keyword' and v == 'local'
   if is_local then t,v = tnext(tok) end
   if t == 'keyword' and v == 'function' then -- case [1]
      case = 1
      parser = parse_lua_function_header
   elseif t == 'iden' then
      local name,t,v = tools.get_fun_name(tok,v)
      if t ~= '=' then return nil end -- probably invalid code...
      t,v = tnext(tok)
      if t == 'keyword' and v == 'function' then -- case [2]
         tnext(tok) -- skip '('
         case = 2
         parser = function(tags,tok)
            tags:add('name',name)
            parse_lua_parameters(tags,tok)
         end
      elseif t == '{' then -- case [3]
         case = 3
         parser = function(tags,tok)
            tags:add('class','table')
            tags:add('name',name)
            parse_lua_table (tags,tok)
         end
      else -- case [4]
         case = 4
         parser = function(tags)
            tags:add('class','field')
            tags:add('name',name)
         end
      end
   elseif t == 'keyword' and v == 'return' then
      t, v = tnext(tok)
      if t == 'keyword' and v == 'function' then
         -- return function(a, b, c)
         tnext(tok) -- skip '('
         case = 2
         parser = parse_lua_parameters
      elseif t ==  '{' then
         -- return {...}
         case = 5
         parser = function(tags,tok)
            tags:add('class','table')
            parse_lua_table(tags,tok)
         end
      else
         return nil
      end
   end
   return parser, is_local, case
end


-- we only call the function returned by the item_follows above if there
-- is not already a name and a type.
-- Otherwise, this is called. Currrently only tries to fill in the fields
-- of a table from a table definition as identified above
function Lua:parse_extra (tags,tok,case)
   if tags.class == 'table' and not tags.field and case == 3 then
      parse_lua_table(tags,tok)
   end
end

-- For Lua, a --- @usage comment means that a long
-- string containing the usage follows, which we
-- use to update the module usage tag. Likewise, the @export
-- tag alone in a doc comment refers to the following returned
-- Lua table of functions


function Lua:is_module_modifier (tags)
   return tags.summary == '' and (tags.usage or tags.export)
end

--  Allow for private name convention.
function Lua:is_private_var (name)
   return name:match '^_' or name:match '_$'
end

function Lua:parse_module_modifier (tags, tok, F)
   if tags.usage then
      if tags.class ~= 'field' then return nil,"cannot deduce @usage" end
      local t1= tnext(tok)
      if t1 ~= '[' then return nil, t1..' '..': not a long string' end
      local t, v = tools.grab_block_comment('',tok,'%]%]')
      return true, v, 'usage'
   elseif tags.export then
      if tags.class ~= 'table' then return nil, "cannot deduce @export" end
      for f in tags.formal_args:iter() do
         if not self:is_private_var(f) then
            F:export_item(f)
         end
      end
      return true
   end
end


-- note a difference here: we scan C/C++ code in full-text mode, not line by line.
-- This is because we can't detect multiline comments in line mode.
-- Note: this applies to C/C++ code used to generate _Lua_ documentation!

local CC = class(Lang)

function CC:_init()
   self.line_comment = '^//+'
   self.start_comment_ = '^///+'
   self.block_comment = '^/%*%*+'
   self.method_call = ':'
   self:finalize()
end

function CC.lexer(f)
   local err
   f,err = utils.readfile(f)
   if not f then quit(err) end
   return lexer.cpp(f,{},nil,true)
end

function CC:grab_block_comment(v,tok)
   v = v:gsub(self.block_comment,''):gsub('\n%s*%*','\n')
   return 'comment',v:sub(1,-3)
end

--- here the argument name is always last, and the type is composed of any tokens before
function CC:extract_arg (tl,idx)
   idx = idx or 1
   local res = List()
   for i = idx,#tl-1 do
      res:append(tl[i][2])
   end
   local type = res:join ' '
   return tl[#tl][2], type
end

function CC:item_follows (t,v,tok)
   if not self.extra.C then
      return false
   end
   if t == 'iden' or t == 'keyword' then --
      if v == self.extra.export then -- this is not part of the return type!
         t,v = tnext(tok)
      end
      -- types may have multiple tokens: example, const char *bonzo(...)
      local return_type, name = v
      t,v = tnext(tok)
      name = v
      t,v = tnext(tok)
      while t ~= '(' do
         return_type = return_type .. ' ' .. name
         name = v
         t,v = tnext(tok)
      end
      --print ('got',name,t,v,return_type)
      return function(tags,tok)
         if not tags.name then
            tags:add('name',name)
         end
         tags:add('class','function')
         if t == '(' then
            tags.formal_args,t,v = tools.get_parameters(tok,')',',',self)
            if return_type ~= 'void' then
               tags.formal_args.return_type = return_type
            end
         end
      end
   end
   return false
end

local Moon = class(Lua)

function Moon:_init()
   self.line_comment = '^%-%-+' -- used for stripping
   self.start_comment_ = '^%s*%-%-%-+'     -- used for doc comment line start
   self.block_comment = '^%-%-%[=*%[%-+' -- used for block doc comments
   self.end_comment_ = '[^%-]%-%-+\n$' ---- exclude --- this kind of comment ---
   self.method_call = '.'
   self:finalize()
end

function Moon:item_follows (t,v,tok)
   if t == '.' then -- enclosed in with statement
      t,v = tnext(tok)
   end
   if t == 'iden' then
      local name,t,v = tools.get_fun_name(tok,v,'')
      if name == 'class' then
         name,t,v = tools.get_fun_name(tok,v,'')
         -- class!
         return function(tags,tok)
            tags:add('class','type')
            tags:add('name',name)
         end
      elseif t == '=' or t == ':' then -- function/method
         t,v = tnext(tok)
         return function(tags,tok)
            if not tags.name then
               tags:add('name',name)
            end
            if t == '(' then
               tags.formal_args,t,v = tools.get_parameters(tok)
            else
               tags.formal_args = List()
            end
            tags:add('class','function')
            if t == '=' then
               tags.formal_args:insert(1,'self')
               tags.formal_args.comments = {self=''}
            end
         end
      else
         return nil
      end
   end
end

return { lua = Lua(), cc = CC(), moon = Moon() }
