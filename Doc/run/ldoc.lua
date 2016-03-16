#!/usr/bin/env lua
---------------
-- ## ldoc, a Lua documentation generator.
--
-- Compatible with luadoc-style annotations, but providing
-- easier customization options.
--
-- C/C++ support for Lua extensions is provided.
--
-- Available from LuaRocks as 'ldoc' and as a [Zip file](http://stevedonovan.github.com/files/ldoc-1.4.3.zip)
--
-- [Github Page](https://github.com/stevedonovan/ldoc)
--
-- @author Steve Donovan
-- @copyright 2011
-- @license MIT/X11
-- @script ldoc

local class = require 'pl.class'
local app = require 'pl.app'
local path = require 'pl.path'
local dir = require 'pl.dir'
local utils = require 'pl.utils'
local List = require 'pl.List'
local stringx = require 'pl.stringx'
local tablex = require 'pl.tablex'

-- Penlight compatibility
utils.unpack = utils.unpack or unpack or table.unpack

local append = table.insert

local lapp = require 'pl.lapp'

-- so we can find our private modules
app.require_here()

--- @usage
local usage = [[
ldoc, a documentation generator for Lua, vs 1.4.3
  -d,--dir (default doc) output directory
  -o,--output  (default 'index') output name
  -v,--verbose          verbose
  -a,--all              show local functions, etc, in docs
  -q,--quiet            suppress output
  -m,--module           module docs as text
  -s,--style (default !) directory for style sheet (ldoc.css)
  -l,--template (default !) directory for template (ldoc.ltp)
  -p,--project (default ldoc) project name
  -t,--title (default Reference) page title
  -f,--format (default plain) formatting - can be markdown, discount or plain
  -b,--package  (default .) top-level package basename (needed for module(...))
  -x,--ext (default html) output file extension
  -c,--config (default config.ld) configuration name
  -u,--unqualified     don't show package name in sidebar links
  -i,--ignore ignore any 'no doc comment or no module' warnings
  -X,--not_luadoc break LuaDoc compatibility. Descriptions may continue after tags.
  -D,--define (default none) set a flag to be used in config.ld
  -C,--colon use colon style
  -B,--boilerplate ignore first comment in source files
  -M,--merge allow module merging
  -S,--simple no return or params, no summary
  -O,--one one-column output layout
  --dump                debug output dump
  --filter (default none) filter output as Lua data (e.g pl.pretty.dump)
  --tags (default none) show all references to given tags, comma-separated
  <file> (string) source file or directory containing source

  `ldoc .` reads options from an `config.ld` file in same directory;
  `ldoc -c path/to/myconfig.ld <file>` reads options from `path/to/myconfig.ld`
  and processes <file> if 'file' was not defined in the ld file.
]]
local args = lapp(usage)
local lfs = require 'lfs'
local doc = require 'ldoc.doc'
local lang = require 'ldoc.lang'
local tools = require 'ldoc.tools'
local global = require 'ldoc.builtin.globals'
local markup = require 'ldoc.markup'
local parse = require 'ldoc.parse'
local KindMap = tools.KindMap
local Item,File,Module = doc.Item,doc.File,doc.Module
local quit = utils.quit


local ModuleMap = class(KindMap)
doc.ModuleMap = ModuleMap

function ModuleMap:_init ()
   self.klass = ModuleMap
   self.fieldname = 'section'
end

local ProjectMap = class(KindMap)
ProjectMap.project_level = true

function ProjectMap:_init ()
   self.klass = ProjectMap
   self.fieldname = 'type'
end



local lua, cc = lang.lua, lang.cc

local file_types = {
   ['.lua'] = lua,
   ['.ldoc'] = lua,
   ['.luadoc'] = lua,
   ['.c'] = cc,
   ['.h'] = cc,
   ['.cpp'] = cc,
   ['.cxx'] = cc,
   ['.C'] = cc,
   ['.mm'] = cc,
   ['.moon'] = lang.moon,
}
------- ldoc external API ------------

-- the ldoc table represents the API available in `config.ld`.
local ldoc = { charset = 'UTF-8' }

local known_types, kind_names = {}

local function lookup (itype,igroup,isubgroup)
   local kn = kind_names[itype]
   known_types[itype] = true
   if kn then
      if type(kn) == 'string' then
         igroup = kn
      else
         igroup = kn[1]
         isubgroup = kn[2]
      end
   end
   return itype, igroup, isubgroup
end

local function setup_kinds ()
   kind_names = ldoc.kind_names or {}

   ModuleMap:add_kind(lookup('function','全局函数','参数'))
   -- ModuleMap:add_kind(lookup('function','Functions','参数(Parameters)'))
   ModuleMap:add_kind(lookup('table','Tables','Fields'))
   ModuleMap:add_kind(lookup('field','常量'))
   ModuleMap:add_kind(lookup('lfunction','Local Functions','参数'))
   -- ModuleMap:add_kind(lookup('lfunction','Local Functions','参数(Parameters)'))
   ModuleMap:add_kind(lookup('annotation','Issues'))

   ProjectMap:add_kind(lookup('module','静态方法和常量'))--'Modules'))
   ProjectMap:add_kind(lookup('script','Scripts'))
   ProjectMap:add_kind(lookup('classmod', '基础类'))--'Classes'))
   ProjectMap:add_kind(lookup('classmodUIItem', 'UI控件'))--'Classes'))
   ProjectMap:add_kind(lookup('classmodUIView', 'UI容器'))--'Classes'))
   ProjectMap:add_kind(lookup('classmodAnimation', '动画'))--'Classes'))
   ProjectMap:add_kind(lookup('classmodAudio', '音视频'))--'Classes'))
   ProjectMap:add_kind(lookup('classmodNet', '网络'))--'Classes'))
   ProjectMap:add_kind(lookup('classmodNavigation', 'Navigation'))--'Classes'))
   ProjectMap:add_kind(lookup('classmodOther', '其他'))--'Classes'))
   ProjectMap:add_kind(lookup('topic','Topics'))
   ProjectMap:add_kind(lookup('example','Examples'))
   ProjectMap:add_kind(lookup('file','Source'))

   for k in pairs(kind_names) do
      if not known_types[k] then
         quit("unknown item type "..tools.quote(k).." in kind_names")
      end
   end
end


local add_language_extension
-- hacky way for doc module to be passed options...
doc.ldoc = ldoc

-- if the corresponding argument was the default, then any ldoc field overrides
local function override (field,defval)
   defval = defval or false
   if args[field] == defval and ldoc[field] ~= nil then args[field] = ldoc[field] end
end

-- aliases to existing tags can be defined. E.g. just 'p' for 'param'
function ldoc.alias (a,tag)
   doc.add_alias(a,tag)
end

-- standard aliases --

ldoc.alias('tparam',{'param',modifiers={type="$1"}})
ldoc.alias('treturn',{'return',modifiers={type="$1"}})
ldoc.alias('tfield',{'field',modifiers={type="$1"}})

function ldoc.tparam_alias (name,type)
   type = type or name
   ldoc.alias(name,{'param',modifiers={type=type}})
end

ldoc.alias ('error',doc.error_macro)

ldoc.tparam_alias 'string'
ldoc.tparam_alias 'number'
ldoc.tparam_alias 'int'
ldoc.tparam_alias 'bool'
ldoc.tparam_alias 'func'
ldoc.tparam_alias 'table'
ldoc.tparam_alias 'tab'
ldoc.tparam_alias 'thread'
ldoc.tparam_alias 'function'
ldoc.tparam_alias 'View'
ldoc.tparam_alias 'Button'
ldoc.tparam_alias 'Label'
ldoc.tparam_alias 'Transform3D'
ldoc.tparam_alias 'StyledString'
ldoc.tparam_alias 'Image'
ldoc.tparam_alias 'HScrollView'
ldoc.tparam_alias 'VScrollView'
ldoc.tparam_alias 'ScrollView'
ldoc.tparam_alias 'Gesture'
ldoc.tparam_alias 'TableView'
ldoc.tparam_alias 'TextField'
ldoc.tparam_alias 'Alert'
ldoc.tparam_alias 'CollectionView'
ldoc.tparam_alias 'Align'
-- ldoc.tparam_alias 'ErrorView'
-- ldoc.tparam_alias 'UILoadingView'
-- ldoc.tparam_alias 'UINotice'
ldoc.tparam_alias 'PagerIndicator'
ldoc.tparam_alias 'LoadingIndicator'
-- ldoc.tparam_alias 'Transform3D'

function ldoc.add_language_extension(ext, lang)
   lang = (lang=='c' and cc) or (lang=='lua' and lua) or quit('unknown language')
   if ext:sub(1,1) ~= '.' then ext = '.'..ext end
   file_types[ext] = lang
end

function ldoc.add_section (name, title, subname)
   ModuleMap:add_kind(name,title,subname)
end

-- new tags can be added, which can be on a project level.
function ldoc.new_type (tag, header, project_level,subfield)
   doc.add_tag(tag,doc.TAG_TYPE,project_level)
   if project_level then
      ProjectMap:add_kind(tag,header,subfield)
   else
      ModuleMap:add_kind(tag,header,subfield)
   end
end

function ldoc.manual_url (url)
   global.set_manual_url(url)
end

function ldoc.custom_see_handler(pat, handler)
   doc.add_custom_see_handler(pat, handler)
end

local ldoc_contents = {
   'alias','add_language_extension','custom_tags','new_type','add_section', 'tparam_alias',
   'file','project','title','package','format','output','dir','ext', 'topics',
   'one','style','template','description','examples', 'pretty', 'charset', 'plain',
   'readme','all','manual_url', 'ignore', 'colon', 'sort', 'module_file','vars',
   'boilerplate','merge', 'wrap', 'not_luadoc', 'template_escape','merge_error_groups',
   'no_return_or_parms','no_summary','full_description','backtick_references', 'custom_see_handler',
   'no_space_before_args','parse_extra','no_lua_ref','sort_modules','use_markdown_titles',
   'unqualified', 'custom_display_name_handler', 'kind_names', 'custom_references',
   'dont_escape_underscore','global_lookup','prettify_files','convert_opt', 'user_keywords',
   'postprocess_html',
}
ldoc_contents = tablex.makeset(ldoc_contents)

local function loadstr (ldoc,txt)
   local chunk, err
   local load
   -- Penlight's Lua 5.2 compatibility has wobbled over the years...
   if not rawget(_G,'loadin') then -- Penlight 0.9.5
       -- Penlight 0.9.7; no more global load() override
      load = load or utils.load
      chunk,err = load(txt,'config',nil,ldoc)
   else
      chunk,err = loadin(ldoc,txt)
   end
   return chunk, err
end

-- any file called 'config.ld' found in the source tree will be
-- handled specially. It will be loaded using 'ldoc' as the environment.
local function read_ldoc_config (fname)
   local directory = path.dirname(fname)
   if directory == '' then
      directory = '.'
   end
   local chunk, err, ok
   if args.filter == 'none' then
      print('reading configuration from '..fname)
   end
   local txt,not_found = utils.readfile(fname)
   if txt then
      chunk, err = loadstr(ldoc,txt)
      if chunk then
         if args.define ~= 'none' then ldoc[args.define] = true end
         ok,err = pcall(chunk)
      end
    end
   if err then quit('error loading config file '..fname..': '..err) end
   for k in pairs(ldoc) do
      if not ldoc_contents[k] then
         quit("this config file field/function is unrecognized: "..k)
      end
   end
   return directory, not_found
end

local quote = tools.quote
--- processing command line and preparing for output ---

local F
local file_list = List()
File.list = file_list
local config_dir


local ldoc_dir = arg[0]:gsub('[^/\\]+$','')
local doc_path = ldoc_dir..'/ldoc/builtin/?.lua'

-- ldoc -m is expecting a Lua package; this converts this to a file path
if args.module then
   -- first check if we've been given a global Lua lib function
   if args.file:match '^%a+$' and global.functions[args.file] then
      args.file = 'global.'..args.file
   end
   local fullpath,mod,on_docpath = tools.lookup_existing_module_or_function (args.file, doc_path)
   if not fullpath then
      quit(mod)
   else
      args.file = fullpath
      args.module = mod
   end
end

local abspath = tools.abspath

-- a special case: 'ldoc .' can get all its parameters from config.ld
if args.file == '.' then
   local err
   config_dir,err = read_ldoc_config(args.config)
   if err then quit("no "..quote(args.config).." found") end
   local config_path = path.dirname(args.config)
   if config_path ~= '' then
      print('changing to directory',config_path)
      lfs.chdir(config_path)
   end
   config_is_read = true
   args.file = ldoc.file or '.'
   if args.file == '.' then
      args.file = lfs.currentdir()
   elseif type(args.file) == 'table' then
      for i,f in ipairs(args.file) do
         args.file[i] = abspath(f)
      end
   else
      args.file = abspath(args.file)
   end
else
   -- user-provided config file
   if args.config ~= 'config.ld' then
      local err
      config_dir,err = read_ldoc_config(args.config)
      if err then quit("no "..quote(args.config).." found") end
   end
   -- with user-provided file
   args.file = abspath(args.file)
end

if type(ldoc.custom_tags) == 'table' then -- custom tags
  for i, custom in ipairs(ldoc.custom_tags) do
    if type(custom) == 'string' then
      custom = {custom}
      ldoc.custom_tags[i] = custom
    end
    doc.add_tag(custom[1], 'ML')
  end
end -- custom tags

local source_dir = args.file
if type(source_dir) == 'table' then
   source_dir = source_dir[1]
end
if type(source_dir) == 'string' and path.isfile(source_dir) then
   source_dir = path.splitpath(source_dir)
end
source_dir = source_dir:gsub('[/\\]%.$','')

---------- specifying the package for inferring module names --------
-- If you use module(...), or forget to explicitly use @module, then
-- ldoc has to infer the module name. There are three sensible values for
-- `args.package`:
--
--  * '.' the actual source is in an immediate subdir of the path given
--  * '..' the path given points to the source directory
--  * 'NAME' explicitly give the base module package name
--

override ('package','.')

local function setup_package_base()
   if ldoc.package then args.package = ldoc.package end
   if args.package == '.' then
      args.package = source_dir
   elseif args.package == '..' then
      args.package = path.splitpath(source_dir)
   elseif not args.package:find '[\\/]' then
      local subdir,dir = path.splitpath(source_dir)
      if dir == args.package then
         args.package = subdir
      elseif path.isdir(path.join(source_dir,args.package)) then
         args.package = source_dir
      else
         quit("args.package is not the name of the source directory")
      end
   end
end


--------- processing files ---------------------
-- ldoc may be given a file, or a directory. `args.file` may also be specified in config.ld
-- where it is a list of files or directories. If specified on the command-line, we have
-- to find an optional associated config.ld, if not already loaded.

if ldoc.ignore then args.ignore = true end

local function process_file (f, flist)
   local ext = path.extension(f)
   local ftype = file_types[ext]
   if ftype then
      if args.verbose then print(f) end
      ftype.extra = ldoc.parse_extra or {}
      local F,err = parse.file(f,ftype,args)
      if err then
         if F then
            F:warning("internal LDoc error")
         end
         quit(err)
      end
      flist:append(F)
   end
end

local process_file_list = tools.process_file_list

setup_package_base()

override 'colon'
override 'merge'
override 'not_luadoc'
override 'module_file'
override 'boilerplate'
override 'all'

setup_kinds()

-- LDoc is doing plain ole C, don't want random Lua references!
if ldoc.parse_extra and ldoc.parse_extra.C then
   ldoc.no_lua_ref = true
end

if ldoc.merge_error_groups == nil then
   ldoc.merge_error_groups = 'Error Message'
end

-- ldoc.module_file establishes a partial ordering where the
-- master module files are processed first.
local function reorder_module_file ()
   if args.module_file then
      local mf = {}
      for mname, f in pairs(args.module_file) do
         local fullpath = abspath(f)
         mf[fullpath] = true
      end
      return function(x,y)
         return mf[x] and not mf[y]
      end
   end
end

-- process files, optionally in order that respects master module files
local function process_all_files(files)
   local sortfn = reorder_module_file()
   local files = tools.expand_file_list(files,'*.*')
   if sortfn then files:sort(sortfn) end
   for f in files:iter() do
      process_file(f, file_list)
   end
   if #file_list == 0 then quit "no source files found" end
end

if type(args.file) == 'table' then
   -- this can only be set from config file so we can assume config is already read
   process_all_files(args.file)

elseif path.isdir(args.file) then
   -- use any configuration file we find, if not already specified
   if not config_dir then
      local files = List(dir.getallfiles(args.file,'*.*'))
      local config_files = files:filter(function(f)
         return path.basename(f) == args.config
      end)
      if #config_files > 0 then
         config_dir = read_ldoc_config(config_files[1])
         if #config_files > 1 then
            print('warning: other config files found: '..config_files[2])
         end
      end
   end

   process_all_files({args.file})

elseif path.isfile(args.file) then
   -- a single file may be accompanied by a config.ld in the same dir
   if not config_dir then
      config_dir = path.dirname(args.file)
      if config_dir == '' then config_dir = '.' end
      local config = path.join(config_dir,args.config)
      if path.isfile(config) then
         read_ldoc_config(config)
      end
   end
   process_file(args.file, file_list)
   if #file_list == 0 then quit "unsupported file extension" end
else
   quit ("file or directory does not exist: "..quote(args.file))
end


-- create the function that renders text (descriptions and summaries)
-- (this also will initialize the code prettifier used)
override ('format','plain')
override 'pretty'
ldoc.markup = markup.create(ldoc, args.format, args.pretty, ldoc.user_keywords)

------ 'Special' Project-level entities ---------------------------------------
-- Examples and Topics do not contain code to be processed for doc comments.
-- Instead, they are intended to be rendered nicely as-is, whether as pretty-lua
-- or as Markdown text. Treating them as 'modules' does stretch the meaning of
-- of the term, but allows them to be treated much as modules or scripts.
-- They define an item 'body' field (containing the file's text) and a 'postprocess'
-- field which is used later to convert them into HTML. They may contain @{ref}s.

local function add_special_project_entity (f,tags,process)
   local F = File(f)
   tags.name = path.basename(f)
   local text = utils.readfile(f)
   local item = F:new_item(tags,1)
   if process then
      text = process(F, text)
   end
   F:finish()
   file_list:append(F)
   item.body = text
   return item, F
end

local function prettify_source_files(files,class,linemap)
   local prettify = require 'ldoc.prettify'

   process_file_list (files, '*.*', function(f)
      local ext = path.extension(f)
      local ftype = file_types[ext]
      if ftype then
         local item = add_special_project_entity(f,{
            class = class,
         })
         -- wrap prettify for this example so it knows which file to blame
         -- if there's a problem
         local lang = ext:sub(2)
         item.postprocess = function(code)
            return '<h2>'..path.basename(f)..'</h2>\n' ..
                prettify.lua(lang,f,code,0,true,linemap and linemap[f])
         end
      end
   end)
end

if type(ldoc.examples) == 'string' then
   ldoc.examples = {ldoc.examples}
end
if type(ldoc.examples) == 'table' then
   prettify_source_files(ldoc.examples,"example")
end

if ldoc.prettify_files then
   local files = List()
   local linemap = {}
   for F in file_list:iter() do
      files:append(F.filename)
      local mod = F.modules[1]
      local ls = List()
      for item in mod.items:iter() do
         ls:append(item.lineno)
      end
      linemap[F.filename] = ls
   end
   prettify_source_files(files,"file",linemap)
end

if args.simple then
    ldoc.no_return_or_parms=true
    ldoc.no_summary=true
end

ldoc.readme = ldoc.readme or ldoc.topics
if type(ldoc.readme) == 'string' then
   ldoc.readme = {ldoc.readme}
end
if type(ldoc.readme) == 'table' then
   process_file_list(ldoc.readme, '*.md', function(f)
      local item, F = add_special_project_entity(f,{
         class = 'topic'
      }, markup.add_sections)
      -- add_sections above has created sections corresponding to the 2nd level
      -- headers in the readme, which are attached to the File. So
      -- we pass the File to the postprocesser, which will insert the section markers
      -- and resolve inline @ references.
      if ldoc.use_markdown_titles then
         item.display_name = F.display_name
      end
      item.postprocess = function(txt) return ldoc.markup(txt,F) end
   end)
end

-- extract modules from the file objects, resolve references and sort appropriately ---

local first_module
local project = ProjectMap()
local module_list = List()
module_list.by_name = {}

local modcount = 0

for F in file_list:iter() do
   for mod in F.modules:iter() do
      if not first_module then first_module = mod end
      if doc.code_tag(mod.type) then modcount = modcount + 1 end
      module_list:append(mod)
      module_list.by_name[mod.name] = mod
   end
end

for mod in module_list:iter() do
   if not args.module then -- no point if we're just showing docs on the console
      mod:resolve_references(module_list)
   end
   project:add(mod,module_list)
end


if ldoc.sort_modules then
   table.sort(module_list,function(m1,m2)
      return m1.name < m2.name
   end)
end

ldoc.single = modcount == 1 and first_module or nil

--do return end

-------- three ways to dump the object graph after processing -----

-- ldoc -m will give a quick & dirty dump of the module's documentation;
-- using -v will make it more verbose
if args.module then
   if #module_list == 0 then quit("no modules found") end
   if args.module == true then
      file_list[1]:dump(args.verbose)
   else
      local M,name = module_list[1], args.module
      local fun = M.items.by_name[name]
      if not fun then
         fun = M.items.by_name[M.mod_name..'.'..name]
      end
      if not fun then quit(quote(name).." is not part of "..quote(args.file)) end
      fun:dump(true)
   end
   return
end

-- ldoc --dump will do the same as -m, except for the currently specified files
if args.dump then
   for mod in module_list:iter() do
      mod:dump(true)
   end
   os.exit()
end
if args.tags ~= 'none' then
   local tagset = {}
   for t in stringx.split(args.tags,','):iter() do
      tagset[t] = true
   end
   for mod in module_list:iter() do
      mod:dump_tags(tagset)
   end
   os.exit()
end

-- ldoc --filter mod.name will load the module `mod` and pass the object graph
-- to the function `name`. As a special case --filter dump will use pl.pretty.dump.
if args.filter ~= 'none' then
   doc.filter_objects_through_function(args.filter, module_list)
   os.exit()
end

-- can specify format, output, dir and ext in config.ld
override ('output','index')
override ('dir','doc')
override ('ext','html')
override 'one'

-- handling styling and templates --
ldoc.css, ldoc.templ = 'ldoc.css','ldoc.ltp'

-- special case: user wants to generate a .md file from a .lua file
if args.ext == 'md' then
   if #module_list ~= 1 then
      quit("can currently only generate Markdown output from one module only")
   end
   if ldoc.template == '!' then
      ldoc.template = '!md'
   end
   args.output = module_list[1].name
   args.dir = '.'
   ldoc.template_escape = '>'
   ldoc.style = false
   args.ext = '.md'
end

local function match_bang (s)
   if type(s) ~= 'string' then return end
   return s:match '^!(.*)'
end

local function style_dir (sname)
   local style = ldoc[sname]
   local dir
   if style==false and sname == 'style' then
      args.style = false
      ldoc.css = false
   end
   if style then
      if style == true then
         dir = config_dir
      elseif type(style) == 'string' and (path.isdir(style) or match_bang(style)) then
         dir = style
      else
         quit(quote(tostring(style)).." is not a directory")
      end
      args[sname] = dir
   end
end

-- the directories for template and stylesheet can be specified
-- either by command-line '--template','--style' arguments or by 'template and
-- 'style' fields in config.ld.
-- The assumption here is that if these variables are simply true then the directory
-- containing config.ld contains a ldoc.css and a ldoc.ltp respectively. Otherwise
-- they must be a valid subdirectory.

style_dir 'style'
style_dir 'template'

if not args.ext:find '^%.' then
   args.ext = '.'..args.ext
end

if args.one then
   ldoc.style = '!one'
end

local builtin_style, builtin_template = match_bang(args.style),match_bang(args.template)
if builtin_style or builtin_template then
   -- '!' here means 'use built-in templates'
   local tmpdir = path.join(path.is_windows and os.getenv('TMP') or '/tmp','ldoc')
   if not path.isdir(tmpdir) then
      lfs.mkdir(tmpdir)
   end
   local function tmpwrite (name)
      local ok,text = pcall(require,'ldoc.html.'..name:gsub('%.','_'))
      if not ok then
         quit("cannot find builtin template "..name.." ("..text..")")
      end
      if not utils.writefile(path.join(tmpdir,name),text) then
         quit("cannot write to temp directory "..tmpdir)
      end
   end
   if builtin_style then
      if builtin_style ~= '' then
         ldoc.css = 'ldoc_'..builtin_style..'.css'
      end
      tmpwrite(ldoc.css)
      args.style = tmpdir
   end
   if builtin_template then
      if builtin_template ~= '' then
         ldoc.templ = 'ldoc_'..builtin_template..'.ltp'
      end
      tmpwrite(ldoc.templ)
      args.template = tmpdir
   end
end

ldoc.log = print
ldoc.kinds = project
ldoc.modules = module_list
ldoc.title = ldoc.title or args.title
ldoc.project = ldoc.project or args.project
ldoc.package = args.package:match '%a+' and args.package or nil
ldoc.updatetime = os.date("%Y-%m-%d %H:%M:%S")

local html = require 'ldoc.html'

html.generate_output(ldoc, args, project)

if args.verbose then
   print 'modules'
   for k in pairs(module_list.by_name) do print(k) end
end
