---------
-- General utility functions for ldoc
-- @module tools

local class = require 'pl.class'
local List = require 'pl.List'
local path = require 'pl.path'
local utils = require 'pl.utils'
local tablex = require 'pl.tablex'
local stringx = require 'pl.stringx'
local dir = require 'pl.dir'
local tools = {}
local M = tools
local append = table.insert
local lexer = require 'ldoc.lexer'
local quit = utils.quit
local lfs = require 'lfs'

-- at rendering time, can access the ldoc table from any module item,
-- or the item itself if it's a module
function M.item_ldoc (item)
   local mod = item and (item.module or item)
   return mod and mod.ldoc
end

-- this constructs an iterator over a list of objects which returns only
-- those objects where a field has a certain value. It's used to iterate
-- only over functions or tables, etc.  If the list of item has a module
-- with a context, then use that to pre-sort the fltered items.
-- (something rather similar exists in LuaDoc)
function M.type_iterator (list,field,value)
   return function()
      local fls = list:filter(function(item)
         return item[field] == value
      end)
      local ldoc = M.item_ldoc(fls[1])
      if ldoc and ldoc.sort then
         fls:sort(function(ia,ib)
            return ia.name < ib.name
         end)
      end
      return fls:iter()
   end
end

-- KindMap is used to iterate over a set of categories, called _kinds_,
-- and the associated iterator over all items in that category.
-- For instance, a module contains functions, tables, etc and we will
-- want to iterate over these categories in a specified order:
--
--  for kind, items in module.kinds() do
--    print('kind',kind)
--    for item in items() do print(item.name) end
--  end
--
-- The kind is typically used as a label or a Title, so for type 'function' the
-- kind is 'Functions' and so on.

local KindMap = class()
M.KindMap = KindMap

-- calling a KindMap returns an iterator. This returns the kind, the iterator
-- over the items of that type, and the actual type tag value.
function KindMap:__call ()
   local i = 1
   local klass = self.klass
   return function()
      local kind = klass.kinds[i]
      if not kind then return nil end -- no more kinds
      while not self[kind] do
         i = i + 1
         kind = klass.kinds[i]
         if not kind then return nil end
      end
      i = i + 1
      local type = klass.types_by_kind [kind].type
      return kind, self[kind], type
   end
end

function KindMap:put_kind_first (kind)
   -- find this kind in our kind list
   local kinds = self.klass.kinds,kind
   local idx = tablex.find(kinds,kind)
   -- and swop with the start!
   if idx then
      kinds[1],kinds[idx] = kinds[idx],kinds[1]
   end
end

function KindMap:type_of (item)
   local klass = self.klass
   local kind = klass.types_by_tag[item.type]
   return klass.types_by_kind [kind]
end

function KindMap:get_section_description (kind)
   return self.klass.descriptions[kind]
end

function KindMap:get_item (kind)
   return self.klass.items_by_kind[kind]
end

-- called for each new item. It does not actually create separate lists,
-- (although that would not break the interface) but creates iterators
-- for that item type if not already created.
function KindMap:add (item,items,description)
   local group = item[self.fieldname] -- which wd be item's type or section
   local kname = self.klass.types_by_tag[group] -- the kind name
   if not self[kname] then
    -- print(kname,group,self.fieldname)
      self[kname] = M.type_iterator (items,self.fieldname,group)
      self.klass.descriptions[kname] = description
   end
   item.kind = kname:lower()
end

-- KindMap has a 'class constructor' which is used to modify
-- any new base class.
function KindMap._class_init (klass)
   klass.kinds = {} -- list in correct order of kinds
   klass.types_by_tag = {} -- indexed by tag
   klass.types_by_kind = {} -- indexed by kind
   klass.descriptions = {} -- optional description for each kind
   klass.items_by_kind = {}  -- some kinds are items
end


function KindMap.add_kind (klass,tag,kind,subnames,item)
   if not klass.types_by_kind[kind] then
      klass.types_by_tag[tag] = kind
      klass.types_by_kind[kind] = {type=tag,subnames=subnames}
      if item then
         klass.items_by_kind[kind] = item
      end
      append(klass.kinds,kind)
   end
end

----- some useful utility functions ------

function M.module_basepath()
   local lpath = List.split(package.path,';')
   for p in lpath:iter() do
      local p = path.dirname(p)
      if path.isabs(p) then
         return p
      end
   end
end

-- split a qualified name into the module part and the name part,
-- e.g 'pl.utils.split' becomes 'pl.utils' and 'split'. Also
-- must understand colon notation!
function M.split_dotted_name (s)
   local s1,s2 = s:match '^(.+)[%.:](.+)$'
   if s1 then -- we can split
      return s1,s2
   else
      return nil
   end
--~    local s1,s2 = path.splitext(s)
--~    if s2=='' then return nil
--~    else  return s1,s2:sub(2)
--~    end
end

-- grab lines from a line iterator `iter` until the line matches the pattern.
-- Returns the joined lines and the line, which may be nil if we run out of
-- lines.
function M.grab_while_not(iter,pattern)
   local line = iter()
   local res = {}
   while line and not line:match(pattern) do
      append(res,line)
      line = iter()
   end
   res = table.concat(res,'\n')
   return res,line
end


function M.extract_identifier (value)
   return value:match('([%.:%-_%w]+)(.*)$')
end

function M.identifier_list (ls)
   local ns = List()
   if type(ls) == 'string' then ls = List{ns} end
   for s in ls:iter() do
      if s:match ',' then
         ns:extend(List.split(s,'[,%s]+'))
      else
         ns:append(s)
      end
   end
   return ns
end

function M.strip (s)
   return s:gsub('^%s+',''):gsub('%s+$','')
end

-- Joins strings using a separator.
--
-- Empty strings and nil arguments are ignored:
--
--    assert(join('+', 'one', '', 'two', nil, 'three') == 'one+two+three')
--    assert(join(' ', '', '') == '')
--
-- This is especially useful for the last case demonstrated above,
-- where "conventional" solutions (".." or table.concat) would result
-- in a spurious space.
function M.join(sep, ...)
  local contents = {}
  for i = 1, select('#', ...) do
    local value = select(i, ...)
    if value and value ~= "" then
      contents[#contents + 1] = value
    end
  end
  return table.concat(contents, sep)
end

function M.check_directory(d)
   if not path.isdir(d) then
      lfs.mkdir(d)
   end
end

function M.check_file (f,original)
   if not path.exists(f) or path.getmtime(original) > path.getmtime(f) then
      local text,err = utils.readfile(original)
      if text then
         text,err = utils.writefile(f,text)
      end
      if err then
         quit("Could not copy "..original.." to "..f)
      end
   end
end

function M.writefile(name,text)
   local ok,err = utils.writefile(name,text)
   if err then quit(err) end
end

function M.name_of (lpath)
   local ext
   lpath,ext = path.splitext(lpath)
   return lpath
end

function M.this_module_name (basename,fname)
   local ext
   if basename == '' then
      return M.name_of(fname)
   end
   basename = path.abspath(basename)
   if basename:sub(-1,-1) ~= path.sep then
      basename = basename..path.sep
   end
   local lpath,cnt = fname:gsub('^'..utils.escape(basename),'')
   --print('deduce',lpath,cnt,basename)
   if cnt ~= 1 then quit("module(...) name deduction failed: base "..basename.." "..fname) end
   lpath = lpath:gsub(path.sep,'.')
   return (M.name_of(lpath):gsub('%.init$',''))
end

function M.find_existing_module (name, dname, searchfn)
   local fullpath,lua = searchfn(name)
   local mod = true
   if not fullpath then -- maybe it's a function reference?
      -- try again with the module part
      local  mpath,fname = M.split_dotted_name(name)
      if mpath then
         fullpath,lua = searchfn(mpath)
      else
         fullpath = nil
      end
      if not fullpath then
         return nil, "module or function '"..dname.."' not found on module path"
      else
         mod = fname
      end
   end
   if not lua then return nil, "module '"..name.."' is a binary extension" end
   return fullpath, mod
end

function M.lookup_existing_module_or_function (name, docpath)
   -- first look up on the Lua module path
   local on_docpath
   local fullpath, mod = M.find_existing_module(name,name,path.package_path)
   -- no go; but see if we can find it on the doc path
   if not fullpath then
      fullpath, mod = M.find_existing_module("ldoc.builtin." .. name,name,path.package_path)
      on_docpath = true
--~       fullpath, mod = M.find_existing_module(name, function(name)
--~          local fpath = package.searchpath(name,docpath)
--~          return fpath,true  -- result must always be 'lua'!
--~       end)
   end
   return fullpath, mod, on_docpath -- `mod` can be the error message
end


--------- lexer tools -----

local tnext = lexer.skipws

local function type_of (tok) return tok and tok[1] or 'end' end
local function value_of (tok) return tok[2] end

-- This parses Lua formal argument lists. It will return a list of argument
-- names, which also has a comments field, which will contain any commments
-- following the arguments. ldoc will use these in addition to explicit
-- param tags.

function M.get_parameters (tok,endtoken,delim,lang)
   tok = M.space_skip_getter(tok)
   local args = List()
   args.comments = {}
   local ltl,tt = lexer.get_separated_list(tok,endtoken,delim)

   if not ltl or not ltl[1] or #ltl[1] == 0 then return args end -- no arguments

   local strip_comment, extract_arg

   if lang then
      strip_comment = utils.bind1(lang.trim_comment,lang)
      extract_arg = utils.bind1(lang.extract_arg,lang)
   else
      strip_comment = function(text)
         return text:match("%s*%-%-+%s*(.*)")
      end
      extract_arg = function(tl,idx)
         return value_of(tl[idx or 1])
      end
   end

   local function set_comment (idx,tok)
      local text = stringx.rstrip(value_of(tok))
      text = strip_comment(text)
      local arg = args[idx]
      local current_comment = args.comments[arg]
      if current_comment then
        text = current_comment .. " " .. text
      end
      args.comments[arg] = text
   end

   local function add_arg (tl,idx)
      local name, type = extract_arg(tl,idx)
      args:append(name)
      if type then
         if not args.types then args.types = List() end
         args.types:append(type)
      end
   end

   for i = 1,#ltl do
      local tl = ltl[i] -- token list for argument
      if #tl > 0 then
         local j = 1
         if type_of(tl[1]) == 'comment' then
            -- the comments for the i-1 th arg are in the i th arg...
            if i > 1 then
               while type_of(tl[j]) == 'comment' do
                  set_comment(i-1,tl[j])
                  j = j + 1
               end
            else -- first comment however is for the function return comment!
               args.return_comment = strip_comment(value_of(tl[i]))
               j = j + 1
            end
            if #tl > 1 then
               add_arg(tl,j)
            end
         else
            add_arg(tl,1)
         end
         if i == #ltl and #tl > 1 then
            while j <= #tl and type_of(tl[j]) ~= 'comment' do
               j = j + 1
            end
            if j > #tl then break end -- was no comments!
            while type_of(tl[j]) == 'comment' do
               set_comment(i,tl[j])
               j = j + 1
            end
         end
      else
         return nil,"empty argument"
      end
   end

   -- we had argument comments
   -- but the last one may be outside the parens! (Geoff style)
   -- (only try this stunt if it's a function parameter list!)
   if (not endtoken or endtoken == ')') and (#args > 0 or next(args.comments)) then
      local n = #args
      local last_arg = args[n]
      if not args.comments[last_arg] then
         while true do
            tt = {tok()}
            if type_of(tt) == 'comment' then
               set_comment(n,tt)
            else
               break
            end
         end
      end
   end
   -- return what token we ended on as well - can be token _past_ ')'
   return args,tt[1],tt[2]
end

-- parse a Lua identifier - contains names separated by . and (optionally) :.
-- Set `colon` to be the secondary separator, '' for none.
function M.get_fun_name (tok,first,colon)
   local res = {}
   local t,name,sep
   colon = colon or ':'
   if not first then
      t,name = tnext(tok)
   else
      t,name = 'iden',first
   end
   if t ~= 'iden' then return nil end
   t,sep = tnext(tok)
   while sep == '.' or sep == colon do
      append(res,name)
      append(res,sep)
      t,name = tnext(tok)
      t,sep = tnext(tok)
   end
   append(res,name)
   return table.concat(res),t,sep
end

-- space-skipping version of token iterator
function M.space_skip_getter(tok)
   return function ()
      local t,v = tok()
      while t and t == 'space' do
         t,v = tok()
      end
      return t,v
   end
end

function M.quote (s)
   return "'"..s.."'"
end

-- The PL Lua lexer does not do block comments
-- when used in line-grabbing mode, so this function grabs each line
-- until we meet the end of the comment
function M.grab_block_comment (v,tok,patt)
   local res = {v}
   repeat
      v = lexer.getline(tok)
      if v:match (patt) then break end
      append(res,v)
      append(res,'\n')
   until false
   res = table.concat(res)
   --print(res)
   return 'comment',res
end

local prel = path.normcase('/[^/]-/%.%.')


function M.abspath (f)
   local count
   local res = path.normcase(path.abspath(f))
   while true do
      res,count = res:gsub(prel,'')
      if count == 0 then break end
   end
   return res
end

function M.getallfiles(root,mask)
   local res = List(dir.getallfiles(root,mask))
   res:sort()
   return res
end

function M.expand_file_list (list, mask)
   local exclude_list = list.exclude and M.files_from_list(list.exclude, mask)
   local files = List()
   local function process (f)
      f = M.abspath(f)
      if not exclude_list or exclude_list and exclude_list:index(f) == nil then
         files:append(f)
      end
   end
   for _,f in ipairs(list) do
      if path.isdir(f) then
         local dfiles = M.getallfiles(f,mask)
         for f in dfiles:iter() do
            process(f)
         end
      elseif path.isfile(f) then
         process(f)
      else
         quit("file or directory does not exist: "..M.quote(f))
      end
   end
   return files
end

function M.process_file_list (list, mask, operation, ...)
   local files = M.expand_file_list(list,mask)
   for f in files:iter() do
      operation(f,...)
   end
end

function M.files_from_list (list, mask)
   local excl = List()
   M.process_file_list (list, mask, function(f)
      excl:append(f)
   end)
   return excl
end



return tools
