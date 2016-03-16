------
-- Defining the ldoc document model.

local class = require 'pl.class'
local utils = require 'pl.utils'
local List = require 'pl.List'
local Map = require 'pl.Map'

local doc = {}
local global = require 'ldoc.builtin.globals'
local tools = require 'ldoc.tools'
local split_dotted_name = tools.split_dotted_name

local TAG_MULTI,TAG_ID,TAG_SINGLE,TAG_TYPE,TAG_FLAG,TAG_MULTI_LINE = 'M','id','S','T','N','ML'

-- these are the basic tags known to ldoc. They come in several varieties:
--  - 'M' tags with multiple values like 'param' (TAG_MULTI)
--  - 'ML' tags which have a single multi-lined value like 'usage' (TAG_MULTI_LINE)
--  - 'id' tags which are identifiers, like 'name' (TAG_ID)
--  - 'S' tags with a single value, like 'release' (TAG_SINGLE)
--  - 'N' tags which have no associated value, like 'local` (TAG_FLAG)
--  - 'T' tags which represent a type, like 'function' (TAG_TYPE)
local known_tags = {
   param = 'M', see = 'M', comment = 'M', usage = 'ML', ['return'] = 'M', field = 'M', author='M',set='M';
   class = 'id', name = 'id', pragma = 'id', alias = 'id', within = 'id',
   copyright = 'S', summary = 'S', description = 'S', release = 'S', license = 'S',
   fixme = 'S', todo = 'S', warning = 'S', raise = 'S', charset = 'S',
   ['local'] = 'N', export = 'N', private = 'N', constructor = 'N', static = 'N',include = 'S',
   -- project-level
   module = 'T', script = 'T', example = 'T', topic = 'T', submodule='T', 
   classmodUIItem='T', 
   classmodUIView='T', 
   classmodOther='T', 
   classmodAnimation='T', 
   classmodAudio='T', 
   classmodNet='T', 
   classmodNavigation='T',  
   classmod='T', 
   file='T',
   -- module-level
   ['function'] = 'T', lfunction = 'T', table = 'T', section = 'T', type = 'T',
   annotation = 'T', factory = 'T';

}
known_tags._alias = {}
known_tags._project_level = {
   module = true,
   script = true,
   example = true,
   topic = true,
   submodule = true,
   classmod = true,
   classmodUIItem = true,
   classmodUIView = true,
   classmodOther = true,
   classmodAnimation = true,
   classmodAudio = true,
   classmodNet = true,
   classmodNavigation = true,
   file = true,
}

known_tags._code_types = {
   module = true,
   script = true,
   classmod = true,
   classmodUIItem = true,
   classmodUIView = true,
   classmodOther = true,
   classmodAnimation = true,
   classmodAudio = true,
   classmodNet = true,
   classmodNavigation = true,
}

known_tags._presentation_names = {
   classmod = 'Class',   
   classmodUIItem = 'Class', 
   classmodUIView = 'Class', 
   classmodOther = 'Class',
   classmodAnimation = 'Class',
   classmodAudio = 'Class',
   classmodNet = 'Class',
   classmodNavigation = 'Class',
}

known_tags._module_info = {
   'copyright','release','license','author'
}

local see_reference_handlers = {}


doc.TAG_MULTI,doc.TAG_ID,doc.TAG_SINGLE,doc.TAG_TYPE,doc.TAG_FLAG =
    TAG_MULTI,TAG_ID,TAG_SINGLE,TAG_TYPE,TAG_FLAG

-- add a new tag.
function doc.add_tag(tag,type,project_level)
   if not known_tags[tag] then
      known_tags[tag] = type
      known_tags._project_level[tag] = project_level
   end
end

function doc.add_custom_see_handler(pat,action)
   see_reference_handlers[pat] = action
end

-- add an alias to an existing tag (exposed through ldoc API)
function doc.add_alias (a,tag)
   known_tags._alias[a] = tag
end

-- get the tag alias value, if it exists.
function doc.get_alias(tag)
   return known_tags._alias[tag]
end

-- is it a'project level' tag, such as 'module' or 'script'?
function doc.project_level(tag)
   return known_tags._project_level[tag]
end

-- is it a project level tag containing code?
function doc.code_tag (tag)
   return known_tags._code_types[tag]
end

-- is it a section tag?
function doc.section_tag (tag)
   return tag == 'section' or doc.class_tag(tag)
end

-- is it a class tag, like 'type' or 'factory'?
function doc.class_tag (tag)
   return tag == 'type' or tag == 'factory'
end

-- how the type wants to be formally presented; e.g. 'module' becomes 'Module'
-- but 'classmod' will become 'Class'
function doc.presentation_name (tag)
   local name = known_tags._presentation_names[tag]
   if not name then
      name = tag:gsub('(%a)(%a*)',function(f,r)
         return f:upper()..r
      end)
   end
   return name
end

function doc.module_info_tags ()
   return List.iter(known_tags._module_info)
end

-- annotation tags can appear anywhere in the code and may contain any of these tags:
known_tags._annotation_tags = {
   fixme = true, todo = true, warning = true
}

local acount = 1

function doc.expand_annotation_item (tags, last_item)
   if tags.summary ~= '' or last_item == nil then return false end
   local item_name = last_item.tags.name
   for tag, value in pairs(tags) do
      if known_tags._annotation_tags[tag] then
         tags.summary = nil
         tags:add('class','annotation')
         tags:add('summary',value)
         tags:add('name',item_name..'-'..tag..acount)
         acount = acount + 1
         return true
      elseif tag == 'return' then
         last_item:set_tag(tag,value)
      end
   end
   return false
end

-- we process each file, resulting in a File object, which has a list of Item objects.
-- Items can be modules, scripts ('project level') or functions, tables, etc.
-- (In the code 'module' refers to any project level tag.)
-- When the File object is finalized, we specialize some items as modules which
-- are 'container' types containing functions and tables, etc.

local File = class()
local Item = class()
local Module = class(Item) -- a specialized kind of Item

doc.File = File
doc.Item = Item
doc.Module = Module

function File:_init(filename)
   self.filename = filename
   self.items = List()
   self.modules = List()
   self.sections = List()
end

function File:new_item(tags,line)
   local item = Item(tags,self,line or 1)
   self.items:append(item)
   return item
end

function File:export_item (name)
   for item in self.items:iter() do
      local tags = item.tags
      if tags.name == name then
         tags.export = true
         if tags['local'] then
            tags['local'] = nil
         end
         return
      end
   end
   -- warn if any of these guys are not found, indicating no
   -- documentation was given.
   self:warning('no docs '..tools.quote(name))
end


local function has_prefix (name,prefix)
   local i1,i2 = name:find(prefix)
   return i1 == 1 and i2 == #prefix
end

local function mod_section_type (this_mod)
   return this_mod and this_mod.section and this_mod.section.type
end

function File:find_module_in_files (name)
   for f in File.list:iter() do
      for m in f.modules:iter() do
         if m.name == name then
            return m,f.filename
         end
      end
   end
end

local function init_within_section (mod,name)
   mod.kinds:add_kind(name, name)
   mod.enclosing_section = mod.section
   mod.section = nil
   return name
end

function File:finish()
   local this_mod
   local items = self.items
   local tagged_inside
   self.args = self.args or {}
   for item in items:iter() do
      if mod_section_type(this_mod) == 'factory' and item.tags then
         local klass = '@{'..this_mod.section.name..'}'
         -- Factory constructors return the object type, and methods all have implicit self argument
         if item.tags.constructor and not item.tags['return'] then
            item.tags['return'] = List{klass}
         elseif item.tags.param then
            item.tags.param:put('self '..klass)
         end
      end
      item:finish()
      -- the default is not to show local functions in the documentation.
      if not self.args.all and (item.type=='lfunction' or (item.tags and item.tags['local'])) then
         -- don't add to the module --
      elseif doc.project_level(item.type) then
         this_mod = item
         local package,mname,submodule
         if item.type == 'module' or item.type == 'classmod'  or string.find(item.type,'classmod') then
            -- if name is 'package.mod', then mod_name is 'mod'
            package,mname = split_dotted_name(this_mod.name)
            if self.args.merge then
               local mod,mf = self:find_module_in_files(item.name)
               if mod then
                  print('found master module',mf)
                  this_mod = mod
                  if this_mod.section then
                     print '***closing section from master module***'
                     this_mod.section = nil
                  end
                  submodule = true
               end
            end
         elseif item.type == 'submodule' then
            local mf
            submodule = true
            this_mod,mf = self:find_module_in_files(item.name)
            if this_mod == nil then
               self:error("'"..item.name.."' not found for submodule")
            end
            tagged_inside = tools.this_module_name(self.base,self.filename)..' Functions'
            this_mod.kinds:add_kind(tagged_inside, tagged_inside)
         end
         if not package then
            mname = this_mod.name
            package = ''
         end
         if not submodule then
            this_mod.package = package
            this_mod.mod_name = mname
            this_mod.kinds = doc.ModuleMap() -- the iterator over the module contents
            self.modules:append(this_mod)
         end
      elseif doc.section_tag(item.type) then
         local display_name = item.name
         if display_name == 'end' then
            this_mod.section = nil
         else
            local summary = item.summary:gsub('%.$','')
            local lookup_name
            if doc.class_tag(item.type) then
               display_name = 'Class '..item.name
               lookup_name = item.name
               item.module = this_mod
               this_mod.items.by_name[item.name] = item
            else
               display_name = summary
               lookup_name = summary
               item.summary = ''
            end
            item.display_name = display_name
            this_mod.section = item
            this_mod.kinds:add_kind(display_name,display_name..' ',nil,item)
            this_mod.sections:append(item)
            this_mod.sections.by_name[lookup_name:gsub('%A','_')] = item
         end
      else
         local to_be_removed
         -- add the item to the module's item list
         if this_mod then
            -- new-style modules will have qualified names like 'mod.foo'
            if item.name == nil then
               self:error("item's name is nil")
            end
            local mod,fname = split_dotted_name(item.name)
            -- warning for inferred unqualified names in new style modules
            -- (retired until we handle methods like Set:unset() properly)
            if not mod and not this_mod.old_style and item.inferred then
               --item:warning(item.name .. ' is declared in global scope')
            end
            -- the function may be qualified with a module alias...
            local alias = this_mod.tags.alias
            if (alias and mod == alias) or mod == 'M' or mod == '_M' then
               mod = this_mod.mod_name
            end
            -- if that's the mod_name, then we want to only use 'foo'
            -- if mod == this_mod.mod_name and this_mod.tags.pragma ~= 'nostrip' then
            --    item.name = fname
            -- end

            if tagged_inside then
               item.tags.within = tagged_inside
            end
            if item.tags.within then
               init_within_section(this_mod,item.tags.within)
            end

            -- right, this item was within a section or a 'class'
            local section_description
            local classmod = (this_mod.type == 'classmod' or string.find(this_mod.type,'classmod') )
            if this_mod.section or classmod then
               local stype
               local this_section = this_mod.section
               if this_section then
                  item.section = this_section.display_name
                  stype = this_section.type
               end
               -- if it was a class, then if the name is unqualified then it becomes
               -- 'Class:foo' (unless flagged as being a constructor, static or not a function)
               if doc.class_tag(stype) or classmod then
                  if not item.name:match '[:%.]' then -- not qualified name!
                     -- a class is either a @type section or a @classmod module. Is this a _method_?
                     local class = classmod and this_mod.name or this_section.name
                     if( item.name==class ) then
                        item.tags.constructor = true;
                     end
                     local static = item.tags.constructor or item.tags.static or item.type ~= 'function'
                     -- methods and metamethods go into their own special sections...
                     if classmod and item.type == 'function' then
                        local inferred_section
                        if item.name:match '^__' then
                           inferred_section = '元方法' -- 'Metamethods'
                        elseif not static then
                           inferred_section = '实例方法' --'Methods'
                        end
                        if inferred_section then
                           item.tags.within = init_within_section(this_mod,inferred_section)
                        end
                     end
                     -- Whether to use '.' or the language's version of ':' (e.g. \ for Moonscript)
                     -- item.name = class..(not static and this_mod.file.lang.method_call or '.')..item.name
                     -- dongxicheng
                     if ( item.tags.constructor or item.tags.static or classmod) then
                     else
                        -- item.name = string.lower(class.."") .. '.'..item.name
                        -- item.name = class .. '.'..item.name
                     end
                     -- print(item.name);
                  end
                  if stype == 'factory'  then
                     if item.tags.private then to_be_removed = true
                     elseif item.type == 'lfunction' then
                        item.type = 'function'
                     end
                     if item.tags.constructor then
                        item.section = item.type
                     end
                  end
               end
               if this_section then
                  --section_description = this_section.summary..' '..(this_section.description or '')
                  --this_section.summary = ''
               elseif item.tags.within then
                  item.section = item.tags.within
               else
                  if item.type == 'function' or item.type == 'lfunction' then
                     -- section_description = "Methods"
                  end
                  item.section = item.type
               end
            elseif item.tags.within then -- ad-hoc section...
               item.section = item.tags.within
            else -- otherwise, just goes into the default sections (Functions,Tables,etc)
               item.section = item.type;
            end

            item.module = this_mod
            if not to_be_removed then
               local these_items = this_mod.items
               these_items.by_name[item.name] = item
               these_items:append(item)
               this_mod.kinds:add(item,these_items,section_description)
            end

            -- restore current section after a 'within'
            if this_mod.enclosing_section then
               this_mod.section = this_mod.enclosing_section
               this_mod.enclosing_section = nil
            end

         else
            -- must be a free-standing function (sometimes a problem...)
         end
      end
   end
end

-- some serious hackery. We force sections into this 'module',
-- and ensure that there is a dummy item so that the section
-- is not empty.

function File:add_document_section(title)
   local section = title:gsub('%W','_')
   self:new_item {
      name = section,
      class = 'section',
      summary = title
   }
   self:new_item {
      name = 'dumbo',
      class = 'function',
   }
   return section
end

function Item:_init(tags,file,line)
   self.file = file
   self.lineno = line
   self.summary = tags.summary
   self.description = tags.description
   tags.summary = nil
   tags.description = nil
   self.tags = {}
   self.formal_args = tags.formal_args
   tags.formal_args = nil
   local iter = tags.iter or Map.iter
   for tag in iter(tags) do
      self:set_tag(tag,tags[tag])
   end
end

function Item:add_to_description (rest)
   if type(rest) == 'string' then
      self.description = (self.description or '') .. rest
   end
end

function Item:trailing_warning (kind,tag,rest)
   if type(rest)=='string' and #rest > 0 then
      Item.warning(self,kind.." tag: '"..tag..'" has trailing text ; use not_luadoc=true if you want description to continue between tags\n"'..rest..'"')
   end
end

local function is_list (l)
   return getmetatable(l) == List
end

function Item:set_tag (tag,value)
   local ttype = known_tags[tag]
   local args = self.file.args

   -- print(ttype, tag, value);

   if ttype == TAG_MULTI or ttype == TAG_MULTI_LINE then -- value is always a List!
      local ovalue = self.tags[tag]
      if ovalue then -- already defined, must be a list
         --print(tag,ovalue,value)
         if is_list(value) then
            ovalue:extend(value)
         else
            ovalue:append(value)
         end
         value = ovalue
      end
      -- these multiple values are always represented as lists
      if not is_list(value) then
         value = List{value}
      end
      if ttype ~= TAG_MULTI_LINE and args and args.not_luadoc then
         local last = value[#value]
         if type(last) == 'string' and last:match '\n' then
            local line,rest = last:match('([^\n]+)(.*)')
            value[#value] = line
            self:add_to_description(rest)
         end
      end
      self.tags[tag] = value
   elseif ttype == TAG_ID then
      local modifiers
      if type(value) == 'table' then
         if value.append then -- it was a List!
            -- such tags are _not_ multiple, e.g. name
            if tag == 'class' and value:contains 'example' then
               self:error("cannot use 'example' tag for functions or tables. Use 'usage'")
            else
               self:error("'"..tag.."' cannot have multiple values; "..tostring(value))
            end
         end
         value = value[1]
         modifiers = value.modifiers
      end
      if value == nil then self:error("Tag without value: "..tag) end
      local id, rest = tools.extract_identifier(value)
      self.tags[tag] = id
      if args and args.not_luadoc then
         self:add_to_description(rest)
      else
         self:trailing_warning('id',tag,rest)
      end
   elseif ttype == TAG_SINGLE then
      self.tags[tag] = value
   elseif ttype == TAG_FLAG then
      self.tags[tag] = true
      if args.not_luadoc then
         self:add_to_description(value)
      else
         self:trailing_warning('flag',tag,value)
      end
   else
      Item.warning(self,"dxc, unknown tag: '"..tag.."' "..tostring(ttype))
   end
end

-- preliminary processing of tags. We check for any aliases, and for tags
-- which represent types. This implements the shortcut notation.
function Item.check_tag(tags,tag, value, modifiers)
   local alias = doc.get_alias(tag)
   if alias then
      if type(alias) == 'string' then
         tag = alias
      elseif type(alias) == 'table' then --{ tag, value=, modifiers = }
         local avalue,amod
         tag, avalue, amod = alias[1],alias.value,alias.modifiers
         if avalue then value = avalue..' '..value end
         if amod then
            modifiers = modifiers or {}
            for m,v in pairs(amod) do
               local idx = v:match('^%$(%d+)')
               if idx then
                  v, value = value:match('(%S+)(.*)')
               end
               modifiers[m] = v
            end
         end
      else -- has to be a function that at least returns tag, value
         return alias(tags,value,modifiers)
      end
   end
   local ttype = known_tags[tag]
   if ttype == TAG_TYPE then
      tags:add('class',tag)
      tag = 'name'
   end
   return tag, value, modifiers
end

-- any tag (except name and classs) may have associated modifiers,
-- in the form @tag[m1,...] where  m1 is either name1=value1 or name1.
-- At this stage, these are encoded
-- in the tag value table and need to be extracted.

local function extract_value_modifier (p)
   if type(p)~='table' then
      return p, { }
   else
      return p[1], p.modifiers or { }
   end
end

local function extract_tag_modifiers (tags)
   local modifiers, mods = {}
   for tag, value in pairs(tags) do
      if type(value)=='table' and value.append then -- i.e. it is a List!
         local tmods = {}
         for i, v in ipairs(value) do
            v, mods = extract_value_modifier(v)
            tmods[i] = mods
            value[i] = v
         end
         modifiers[tag] = tmods
      else
         value, mods = extract_value_modifier(value)
         modifiers[tag] = mods
         tags[tag] = value
      end
   end
   return modifiers
end

local function read_del (tags,name)
   local ret = tags[name]
   tags[name] = nil
   return ret
end

local build_arg_list, split_iden  -- forward declaration

function Item:split_param (line)
   local name, comment = line:match('%s*([%w_%.:]+)(.*)')
   if not name then
      self:error("bad param name format '"..line.."'. Are you missing a parameter name?")
   end
   return name, comment
end

function Item:finish()
   local tags = self.tags
   local quote = tools.quote
   self.name = read_del(tags,'name')
   self.type = read_del(tags,'class')
   self.modifiers = extract_tag_modifiers(tags)
   self.usage = read_del(tags,'usage')
   tags.see = read_del(tags,'see')
   if tags.see then
      tags.see = tools.identifier_list(tags.see)
   end
   if  doc.project_level(self.type) then
      -- we are a module, so become one!
      self.items = List()
      self.sections = List()
      self.items.by_name = {}
      self.sections.by_name = {}
      setmetatable(self,Module)
   elseif not doc.section_tag(self.type) then
      -- params are either a function's arguments, or a table's fields, etc.
      if self.type == 'function' then
         self.parameter = 'param'
         self.ret = read_del(tags,'return')
         self.raise = read_del(tags,'raise')
         if tags['local'] then
            self.type = 'lfunction'
         end
      else
         self.parameter = 'field'
      end
      local field = self.parameter
      local params = read_del(tags,field)
      -- use of macros like @string (which is short for '@tparam string')
      -- can lead to param tags associated with a table.
      if self.parameter == 'field' and tags.param then
         local tparams = read_del(tags,'param')
         if params then
            params:extend(tparams)
            List(self.modifiers.field):extend(self.modifiers.param)
         else
            params = tparams
            self.modifiers.field = self.modifiers.param
         end
      end
      local param_names, comments = List(), List()
      if params then
         for line in params:iter() do
            local name, comment = self:split_param(line)
            param_names:append(name)
            comments:append(comment)
         end
      end
      self.modifiers['return'] = self.modifiers['return'] or List()
      self.modifiers[field] = self.modifiers[field] or List()
      -- we use the formal arguments (if available) as the authoritative list.
      -- If there are both params and formal args, then they must match;
      -- (A formal argument of ... may match any number of params at the end, however.)
      -- If there are formal args and no params, we see if the args have any suitable comments.
      -- Params may have subfields.
      local fargs, formal = self.formal_args
      if fargs then
         if #param_names == 0 then
            --docs may be embedded in argument comments; in either case, use formal arg names
            local ret
            formal,comments,ret = self:parse_formal_arguments(fargs)
            if ret and not self.ret then self.ret = ret end
         elseif #fargs > 0 then -- consistency check!
            local varargs = fargs[#fargs] == '...'
            if varargs then table.remove(fargs) end
            if tags.export then
               if fargs[1] == 'self' then
                  table.remove(fargs,1)
               else
                  tags.static = true
               end
            end
            local k = 0
            for _,pname in ipairs(param_names) do
               local _,field = split_iden(pname)
               if not field then
                  k = k + 1
                  if k > #fargs then
                     if not varargs then
                        self:warning("extra param with no formal argument: "..quote(pname))
                     end
                  elseif pname ~= fargs[k] then
                     self:warning("param and formal argument name mismatch: "..quote(pname).." "..quote(fargs[k]))
                  end
               end
            end
            if k < #fargs then
               for i = k+1,#fargs do if fargs[i] ~= '...' then
                  self:warning("undocumented formal argument: "..quote(fargs[i]))
               end end
            end
         end -- #fargs > 0
         -- formal arguments may come with types, inferred by the
         -- appropriate code in ldoc.lang
         if fargs.types then
            self.modifiers[field] = List()
            for t in fargs.types:iter() do
               self:add_type(field,t)
            end
            if fargs.return_type then
               if not self.ret then -- type, but no comment; no worries
                  self.ret = List{''}
               end
               self.modifiers['return'] = List()
               self:add_type('return',fargs.return_type)
            end
         end
      end -- fargs

      -- the comments are associated with each parameter by
      -- adding name-value pairs to the params list (this is
      -- also done for any associated modifiers)
      -- (At this point we patch up any subparameter references)
      local pmods = self.modifiers[field]
      local params, fields = List()
      local original_names = formal and formal or param_names
      local names = List()
      self.subparams = {}
      params.map = {}

      for i,name in ipairs(original_names) do
         if type(name) ~= 'string' then
            self:error("declared table cannot have array entries")
         end
         local pname,field = split_iden(name)
         if field then
            if not fields then
               fields = List()
               self.subparams[pname] = fields
            end
            fields:append(name)
         else
            names:append(name)
            params:append(name)
            fields = nil
         end

         params.map[name] = comments[i]
         if pmods then
            pmods[name] = pmods[i]
         end
      end
      self.params = params
      self.args = build_arg_list (names,pmods)
   end
   if self.ret then
      self:build_return_groups()
   end
end

function Item:add_type(field,type)
   self.modifiers[field]:append {type = type}
end

-- ldoc allows comments in the formal arg list to be used, if they aren't specified with @param
-- Further, these comments may start with a type followed by a colon, and are then equivalent
-- to a @tparam
function Item:parse_argument_comment (comment,field)
   if comment then
      comment = comment:gsub('^%-+%s*','')
      local type,rest = comment:match '([^:]+):(.*)'
      if type then
         self:add_type(field,type)
         comment = rest
      end
   end
   return comment or ''
end

function Item:parse_formal_arguments (fargs)
   local formal, comments, ret = List(), List()
   if fargs.return_comment then
      local retc = self:parse_argument_comment(fargs.return_comment,'return')
      ret = List{retc}
   end
   for i, name in ipairs(fargs) do
      formal:append(name)
      comments:append(self:parse_argument_comment(fargs.comments[name],self.parameter))
   end
   return formal, comments, ret
end

function split_iden (name)
   if name == '...' then return name end
   local pname,field = name:match('(.-)%.(.+)')
   if not pname then
      return name
   else
      return pname,field
   end
end

function build_arg_list (names,pmods)
   -- build up the string representation of the argument list,
   -- using any opt and optchain modifiers if present.
   -- For instance, '(a [, b])' if b is marked as optional
   -- with @param[opt] b
   local buffer, npending = { }, 0
   local function acc(x) table.insert(buffer, x) end
   -- a number of trailing [opt]s will be usually converted to [opt],[optchain],...
   -- *unless* a person uses convert_opt.
   if pmods and not doc.ldoc.convert_opt then
      local m = pmods[#names]
      if m and m.opt then
         m.optchain = m.opt
         for i = #names-1,1,-1 do
            m = pmods[i]
            if not m or not m.opt then break end
            m.optchain = m.opt
         end
      end
   end
   for i = 1, #names  do
      local m = pmods and pmods[i]
      local opt
      if m then
         if not m.optchain then
            acc ((']'):rep(npending))
            npending=0
         end
         opt = m.optchain or m.opt
         if opt then
            acc('[')
            npending=npending+1
         end
      end
      if i>1 then acc (', ') end
      acc(names[i])
      if opt and opt ~= true then acc('='..opt) end
   end
   acc ((']'):rep(npending))
   return  '('..table.concat(buffer)..')'
end

------ retrieving information about parameters -----
-- The template leans on these guys heavily....

function Item:param_modifiers (p)
   local mods = self.modifiers[self.parameter]
   if not mods then return '' end
   return rawget(mods,p)
end

function Item:type_of_param(p)
   local mparam = self:param_modifiers(p)
   return mparam and mparam.type or ''
end

-- default value for param; if optional but no default, it's just `true`.
function Item:default_of_param(p)
   local m = self:param_modifiers(p)
   if not m then return nil end
   local opt = m.optchain or m.opt
   return opt
end

function Item:readonly(p)
   local m = self:param_modifiers(p)
   if not m then return nil end
   return m.readonly
end

function Item:subparam(p)
   local subp = rawget(self.subparams,p)
   if subp then
      return subp,p
   else
      return {p},nil
   end
end

function Item:display_name_of(p)
   local pname,field = split_iden(p)
   if field then
      return field
   else
      return pname
   end
end

-------- return values and types -------

function Item:type_of_ret(idx)
   local rparam = self.modifiers['return'][idx]
   return rparam and rparam.type or ''
end

local function integer_keys(t)
   if type(t) ~= 'table' then return 0 end
   for k in pairs(t) do
      local num = tonumber(k)
      if num then return num end
   end
   return 0
end

function Item:return_type(r)
   if not r.type then return '' end
   return r.type, r.ctypes
end

local struct_return_type = '*'

function Item:build_return_groups()
   local quote = tools.quote
   local modifiers = self.modifiers
   local retmod = modifiers['return']
   local groups = List()
   local lastg, group
   for i,ret in ipairs(self.ret) do
      local mods = retmod[i]
      local g = integer_keys(mods)
      if g ~= lastg then
         group = List()
         group.g = g
         groups:append(group)
         lastg = g
      end
      --require 'pl.pretty'.dump(ret)
      if not mods then
         self:error(quote(self.name)..' had no return?')
      end
      group:append({text=ret, type = mods and (mods.type or '') or '',mods = mods})
   end
   -- order by groups to force error groups to the end
   table.sort(groups,function(g1,g2) return g1.g < g2.g end)
   self.retgroups = groups
   --require 'pl.pretty'.dump(groups)
   -- cool, now see if there are any treturns that have tfields to associate with
   local fields = self.tags.field
   if fields then
      local fcomments = List()
      for i,f in ipairs(fields) do
         local name, comment = self:split_param(f)
         fields[i] = name
         fcomments[i] = comment
      end
      local fmods = modifiers.field
      for group in groups:iter() do for r in group:iter() do
         if r.mods and r.mods.type  then
            local ctypes, T = List(), r.mods.type
            for i,f in  ipairs(fields) do if fmods[i][T] then
               ctypes:append {name=f,type=fmods[i].type,comment=fcomments[i]}
            end end
            r.ctypes = ctypes
            --require 'pl.pretty'.dump(ctypes)
         end
      end end
   end
end

local ecount = 0

-- this alias macro implements @error.
-- Alias macros need to return the same results as Item:check_tags...
function doc.error_macro(tags,value,modifiers)
   local merge_groups = doc.ldoc.merge_error_groups
   local g = '2' -- our default group id
   -- Were we given an explicit group modifier?
   local key = integer_keys(modifiers)
   if key > 0 then
      g = tostring(key)
   else
      local l = tags:get 'return'
      if l then -- there were returns already......
         -- maximum group of _existing_ error return
         local grp, lastr = 0
         for r in l:iter() do if type(r) == 'table' then
            local rg = r.modifiers._err
            if rg then
               lastr = r
               grp = math.max(grp,rg)
            end
         end end
         if grp > 0 then -- cool, create new group
            if not merge_groups then
               g = tostring(grp+1)
            else
               local mods, text, T = lastr.modifiers
               local new = function(text)
                  return mods._collected..' '..text,{type='string',[T]=true}
               end
               if not mods._collected then
                  text = lastr[1]
                  lastr[1] = merge_groups
                  T = '@'..ecount
                  mods.type = T
                  mods._collected = 1
                  ecount = ecount + 1
                  tags:add('field',new(text))
               else
                  T = mods.type
               end
               mods._collected = mods._collected + 1
               return 'field',new(value)
            end
         end
      end
   end
   tags:add('return','',{[g]=true,type='nil'})
   -- note that this 'return' is tagged with _err!
   return 'return', value, {[g]=true,_err=tonumber(g),type='string'}
end

---------- bothering the user --------------------

function Item:warning(msg)
   local file = self.file and self.file.filename
   if type(file) == 'table' then require 'pl.pretty'.dump(file); file = '?' end
   file = file or '?'
   io.stderr:write(file,':',self.lineno or '1',': ',self.name or '?',': ',msg,'\n')
   return nil
end

function Item:error(msg)
   self:warning(msg)
   os.exit(1)
end

Module.warning, Module.error = Item.warning, Item.error

-------- Resolving References -----------------

function Module:hunt_for_reference (packmod, modules)
   local mod_ref
   local package = self.package or ''
   repeat -- same package?
      local nmod = package..'.'..packmod
      mod_ref = modules.by_name[nmod]
      if mod_ref then break end -- cool
      package = split_dotted_name(package)
   until not package
   return mod_ref
end

local err = io.stderr

local function custom_see_references (s)
   for pat, action in pairs(see_reference_handlers) do
      if s:match(pat) then
         local label, href = action(s:match(pat))
         if not label then print('custom rule failed',s,pat,href) end
         return {href = href, label = label}
      end
   end
end

local function reference (s, mod_ref, item_ref)
   local name = item_ref and item_ref.name or ''
   -- this is deeply hacky; classes have 'Class ' prepended.
--~    if item_ref and doc.class_tag(item_ref.type) then
--~       name = 'Class_'..name
--~    end
   return {mod = mod_ref, name = name, label=s}
end

function Module:lookup_class_item (packmod, s)
   local klass = packmod --"Class_"..packmod
   local qs = klass..':'..s
   local klass_section = self.sections.by_name[klass]
   if not klass_section then return nil end -- no such class
   for item in self.items:iter() do
      --print('item',qs,item.name)
      if s == item.name or qs == item.name then
         return reference(s,self,item)
      end
   end
   return nil
end

function Module:process_see_reference (s,modules,istype)
   if s == nil then return nil end
   local mod_ref,fun_ref,name,packmod
   local ref = custom_see_references(s)
   if ref then return ref end
   if not s:match '^[%w_%.%:%-]+$' or not s:match '[%w_]$' then
      return nil, "malformed see reference: '"..s..'"'
   end

   -- `istype` means that we are looking up strictly in a _type_ context, so then only
   -- allow `classmod` module references.
   local function ismod(item)
      if item == nil then return false end
      if not istype then return true
      else
         return item.type == 'classmod' or  string.find(item.type, 'classmod')
      end
   end

   -- it is _entirely_ possible that someone does not want auto references for standard Lua libraries!
   local lua_manual_ref
   local ldoc = tools.item_ldoc(self)
   if ldoc and ldoc.no_lua_ref then
      lua_manual_ref = function(s) return false end
   else
      lua_manual_ref = global.lua_manual_ref
   end
   -- pure C projects use global lookup (no namespaces)
   if ldoc and ldoc.global_lookup == nil then
      local using_c = ldoc.parse_extra and ldoc.parse_extra.C
      ldoc.global_lookup = using_c or false
   end

   -- is this a fully qualified module name?
   local mod_ref = modules.by_name[s]
   if ismod(mod_ref) then return reference(s, mod_ref,nil) end
   -- module reference?
   mod_ref = self:hunt_for_reference(s, modules)
   if ismod(mod_ref) then return mod_ref end
   -- method reference? (These are of form CLASS.NAME)
   fun_ref = self.items.by_name[s]
   if fun_ref then return reference(s,self,fun_ref) end
   -- otherwise, start splitting!
   local packmod,name = split_dotted_name(s) -- e.g. 'pl.utils','split'
   if packmod then -- qualified name
      mod_ref = modules.by_name[packmod] -- fully qualified mod name?
      if not mod_ref then
         mod_ref = self:hunt_for_reference(packmod, modules)
         if not mod_ref then
            local ref = self:lookup_class_item(packmod,s)
            if ref then return ref end
            local mod, klass = split_dotted_name(packmod)
            mod_ref = modules.by_name[mod]
            if mod_ref then
               ref = mod_ref:lookup_class_item(klass,name)
               if ref then return ref end
            end
            ref = lua_manual_ref(s)
            if ref then return ref end
            return nil,"module not found: "..packmod
         end
      end
      fun_ref = mod_ref:get_fun_ref(name)
      if fun_ref then
         return reference(s,mod_ref,fun_ref)
      else
         fun_ref = mod_ref.sections.by_name[name]
         if not fun_ref then
            return nil,"function or section not found: "..s.." in "..mod_ref.name
         else
            return reference(fun_ref.name:gsub('_',' '),mod_ref,fun_ref)
         end
      end
   else -- plain jane name; module in this package, function in this module
      if ldoc and ldoc.global_lookup then
        for m in modules:iter() do
            fun_ref = m:get_fun_ref(s)
            if fun_ref then return reference(s,m,fun_ref) end
        end
        return nil,"function: "..s.." not found globally"
      end
      mod_ref = modules.by_name[self.package..'.'..s]
      if ismod(mod_ref) then return reference(s, mod_ref,nil) end
      fun_ref = self:get_fun_ref(s)
      if fun_ref then return reference(s,self,fun_ref)
      else
         local ref = lua_manual_ref (s)
         if ref then return ref end
         return nil, "function not found: "..s.." in this module"
      end
   end
end

function Module:get_fun_ref(s)
   local fun_ref = self.items.by_name[s]
   -- did not get an exact match, so try to match by the unqualified fun name
   if not fun_ref then
      local patt = '[.:]'..s..'$'
      for qname,ref in pairs(self.items.by_name) do
         if qname:match(patt) then
            fun_ref = ref
            break
         end
      end
   end
   return fun_ref
end


-- resolving @see references. A word may be either a function in this module,
-- or a module in this package. A MOD.NAME reference is within this package.
-- Otherwise, the full qualified name must be used.
-- First, check whether it is already a fully qualified module name.
-- Then split it and see if the module part is a qualified module
-- and try look up the name part in that module.
-- If this isn't successful then try prepending the current package to the reference,
-- and try to to resolve this.
function Module:resolve_references(modules)
   local found = List()
   for item in self.items:iter() do
      local see = item.tags.see
      if see then -- this guy has @see references
         item.see = List()
         for s in see:iter() do
            local href, err = self:process_see_reference(s,modules)
            if href then
               item.see:append (href)
               found:append{item,s}
            elseif err then
               item:warning(err)
            end
         end
      end
   end
   -- mark as found, so we don't waste time re-searching
   for f in found:iter() do
      f[1].tags.see:remove_value(f[2])
   end
end

function Item:dump_tags (taglist)
   for tag, value in pairs(self.tags) do
      if not taglist or taglist[tag] then
         Item.warning(self,tag..' '..tostring(value))
      end
   end
end

function Module:dump_tags (taglist)
   Item.dump_tags(self,taglist)
   for item in self.items:iter() do
      item:dump_tags(taglist)
   end
end

--------- dumping out modules and items -------------

local function dump_tags (tags)
   if next(tags) then
      print 'tags:'
      for tag, value in pairs(tags) do
         print('\t',tag,value)
      end
   end
end

function Module:dump(verbose)
   if not doc.project_level(self.type) then return end
   print '----'
   print(self.type..':',self.name,self.summary)
   if self.description then print(self.description) end
   dump_tags (self.tags)
   for item in self.items:iter() do
      item:dump(verbose)
   end
end

-- make a text dump of the contents of this File object.
-- The level of detail is controlled by the 'verbose' parameter.
-- Primarily intended as a debugging tool.
function File:dump(verbose)
   for mod in self.modules:iter() do
      mod:dump(verbose)
   end
end

function Item:dump(verbose)
   local tags = self.tags
   local name = self.name
   if self.type == 'function' then
      name = name .. self.args
   end
   if verbose then
      print()
      print(self.type,name)
      print(self.summary)
      if self.description and self.description:match '%S' then
         print 'description:'
         print(self.description)
      end
      if #self.params > 0 then
         print 'parameters:'
         for _,p in ipairs(self.params) do
            print('',p,self.params.map[p])
         end
      end
      if self.ret and #self.ret > 0 then
         print 'returns:'
         for _,r in ipairs(self.ret) do
            print('',r)
         end
      end
      dump_tags(self.tags)
   else
      print('* '..name..' - '..self.summary)
   end
end

function doc.filter_objects_through_function(filter, module_list)
   local quit, quote = utils.quit, tools.quote
   if filter == 'dump' then filter = 'pl.pretty.dump' end
   local mod,name = tools.split_dotted_name(filter)
   local ok,P = pcall(require,mod)
   if not ok then quit("cannot find module "..quote(mod)) end
   local ok,f = pcall(function() return P[name] end)
   if not ok or type(f) ~= 'function' then quit("dump module: no function "..quote(name)) end

   -- clean up some redundant and cyclical references--
   module_list.by_name = nil
   for mod in module_list:iter() do
      mod.kinds = nil
      mod.file = mod.file.filename
      for item in mod.items:iter() do
         item.module = nil
         item.file = nil
         item.formal_args = nil
         item.tags['return'] = nil
         item.see = nil
      end
      mod.items.by_name = nil
   end

   local ok,err = pcall(f,module_list)
   if not ok then quit("dump failed: "..err) end
end

return doc

