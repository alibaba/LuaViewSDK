--- Lexical scanner for creating a sequence of tokens from text. <br>
-- <p><code>lexer.scan(s)</code> returns an iterator over all tokens found in the
-- string <code>s</code>. This iterator returns two values, a token type string
-- (such as 'string' for quoted string, 'iden' for identifier) and the value of the
-- token.
-- <p>
-- Versions specialized for Lua and C are available; these also handle block comments
-- and classify keywords as 'keyword' tokens. For example:
-- <pre class=example>
-- > s = 'for i=1,n do'
-- > for t,v in lexer.lua(s)  do print(t,v) end
-- keyword for
-- iden    i
-- =       =
-- number  1
-- ,       ,
-- iden    n
-- keyword do
-- </pre>
--
-- Based on pl.lexer from Penlight

local strfind = string.find
local strsub = string.sub
local append = table.insert

local function assert_arg(idx,val,tp)
    if type(val) ~= tp then
        error("argument "..idx.." must be "..tp, 2)
    end
end

local lexer = {}

local NUMBER1 = '^[%+%-]?%d+%.?%d*[eE][%+%-]?%d+'
local NUMBER2 = '^[%+%-]?%d+%.?%d*'
local NUMBER3 = '^0x[%da-fA-F]+'
local NUMBER4 = '^%d+%.?%d*[eE][%+%-]?%d+'
local NUMBER5 = '^%d+%.?%d*'
local IDEN = '^[%a_][%w_]*'
local WSPACE = '^%s+'
local STRING1 = [[^'.-[^\\]']]
local STRING2 = [[^".-[^\\]"]]
local STRING3 = "^((['\"])%2)" -- empty string
local PREPRO = '^#.-[^\\]\n'

local plain_matches,lua_matches,cpp_matches,cpp_matches_no_string,lua_keyword,cpp_keyword

local function tdump(tok)
    return tok,tok
end

local function ndump(tok,options)
    if options and options.number then
        tok = tonumber(tok)
    end
    return "number",tok
end

-- regular strings, single or double quotes; usually we want them
-- without the quotes
local function sdump(tok,options)
    if options and options.string then
        tok = tok:sub(2,-2)
    end
    return "string",tok
end

-- strings enclosed in back ticks
local function bdump(tok,options)
    if options and options.string then
        tok = tok:sub(2,-2)
    end
    return "backtick",tok
end

-- long Lua strings need extra work to get rid of the quotes
local function sdump_l(tok,options)
    if options and options.string then
        tok = tok:sub(3,-3)
    end
    return "string",tok
end

local function chdump(tok,options)
    if options and options.string then
        tok = tok:sub(2,-2)
    end
    return "char",tok
end

local function cdump(tok)
    return 'comment',tok
end

local function wsdump (tok)
    return "space",tok
end

local function pdump (tok)
    return 'prepro',tok
end

local function plain_vdump(tok)
    return "iden",tok
end

local function lua_vdump(tok)
    if lua_keyword[tok] then
        return "keyword",tok
    else
        return "iden",tok
    end
end

local function cpp_vdump(tok)
    if cpp_keyword[tok] then
        return "keyword",tok
    else
        return "iden",tok
    end
end

local function count_lines(line, text)
   local index, limit = 1, #text
   while index <= limit do
      local start, stop = text:find('\r\n', index, true)
      if not start then
         start, stop = text:find('[\r\n\f]', index)
         if not start then break end
      end
      index = stop + 1
      line = line + 1
   end
   return line
end

local multiline = { comment = true, space = true }


--- create a plain token iterator from a string or file-like object.
-- @param s the string
-- @param matches an optional match table (set of pattern-action pairs)
-- @param filter a table of token types to exclude, by default {space=true}
-- @param options a table of options; by default, {number=true,string=true},
-- which means convert numbers and strip string quotes.
function lexer.scan (s,matches,filter,options)
    --assert_arg(1,s,'string')
    local file = type(s) ~= 'string' and s
    filter = filter or {space=true}
    options = options or {number=true,string=true}
    if filter then
        if filter.space then filter[wsdump] = true end
        if filter.comments then
            filter[cdump] = true
        end
    end
    if not matches then
        if not plain_matches then
            plain_matches = {
                {WSPACE,wsdump},
                {NUMBER3,ndump},
                {IDEN,plain_vdump},
                {NUMBER1,ndump},
                {NUMBER2,ndump},
                {STRING3,sdump},
                {STRING1,sdump},
                {STRING2,sdump},
                {'^.',tdump}
            }
        end
        matches = plain_matches
    end
    local i1,i2,idx,res1,res2,tok,pat,fun,capt
    local line = 1
    if file then
        s = file:read()
        if not s then return nil end -- empty file
        if s:match '^\239\187' then -- UTF-8 BOM Abomination
           s = s:sub(4)
         end
        s = s ..'\n'
     end
    local sz = #s
    local idx = 1
    if sz == 0 then return nil end -- empty file

    local res = {}
    local mt = {}
    mt.__index = mt
    setmetatable(res,mt)

    function mt.lineno() return line end

    function mt.getline()
        if idx < sz then
            tok = strsub(s,idx,-2)
            idx = sz + 1
            line = line + 1
            return tok
        else
            idx = sz + 1
            line = line + 1
            return file:read()
        end
    end

    function mt.next (tok)
        local t,v = tok()
        while t == 'space' do
            t,v = tok()
        end
        return t,v
    end

    function mt.__call ()
      if not s then return end
      while true do
        for _,m in ipairs(matches) do
            pat,fun = m[1],m[2]
            if fun == nil then error("no match for "..pat) end
            i1,i2 = strfind(s,pat,idx)
            if i1 then
                tok = strsub(s,i1,i2)
                idx = i2 + 1
                if not (filter and filter[fun]) then
                    lexer.finished = idx > sz
                    local t,v = fun(tok,options)
                    if not file and multiline[t] then
                        line = count_lines(line,v)
                    end
                    return t,v
                end
            end
        end
        if idx > sz then
            if file then
                line = line + 1
                s = file:read()
                if not s then return end
                s = s .. '\n'
                idx ,sz = 1,#s
            else
                return
            end
        end
      end
    end
    return res
end

--- get everything in a stream upto a newline.
-- @param tok a token stream
-- @return a string
function lexer.getline (tok)
    return tok:getline()
end

--- get current line number. <br>
-- Only available if the input source is a file-like object.
-- @param tok a token stream
-- @return the line number and current column
function lexer.lineno (tok)
    return tok:lineno()
end

--- get the Lua keywords as a set-like table.
-- So <code>res["and"]</code> etc would be <code>true</code>.
-- @return a table
function lexer.get_keywords ()
    if not lua_keyword then
        lua_keyword = {
            ["and"] = true, ["break"] = true,  ["do"] = true,
            ["else"] = true, ["elseif"] = true, ["end"] = true,
            ["false"] = true, ["for"] = true, ["function"] = true,
            ["if"] = true, ["in"] = true,  ["local"] = true, ["nil"] = true,
            ["not"] = true, ["or"] = true, ["repeat"] = true,
            ["return"] = true, ["then"] = true, ["true"] = true,
            ["until"] = true,  ["while"] = true
        }
    end
    return lua_keyword
end


--- create a Lua token iterator from a string or file-like object.
-- Will return the token type and value.
-- @param s the string
-- @param filter a table of token types to exclude, by default {space=true,comments=true}
-- @param options a table of options; by default, {number=true,string=true},
-- which means convert numbers and strip string quotes.
function lexer.lua(s,filter,options)
    filter = filter or {space=true,comments=true}
    lexer.get_keywords()
    if not lua_matches then
        lua_matches = {
            {WSPACE,wsdump},
            {NUMBER3,ndump},
            {IDEN,lua_vdump},
            {NUMBER4,ndump},
            {NUMBER5,ndump},
            {STRING3,sdump},
            {STRING1,sdump},
            {STRING2,sdump},
            {'^`[^`]+`',bdump},
            {'^%-%-%[(=*)%[.-%]%1%]',cdump},
            {'^%-%-.-\n',cdump},
            {'^%[(=*)%[.-%]%1%]',sdump_l},
            {'^==',tdump},
            {'^~=',tdump},
            {'^<=',tdump},
            {'^>=',tdump},
            {'^%.%.%.',tdump},
            {'^%.%.',tdump},
            {'^.',tdump}
        }
    end
    return lexer.scan(s,lua_matches,filter,options)
end

--- create a C/C++ token iterator from a string or file-like object.
-- Will return the token type type and value.
-- @param s the string
-- @param filter a table of token types to exclude, by default {space=true,comments=true}
-- @param options a table of options; by default, {number=true,string=true},
-- which means convert numbers and strip string quotes.
function lexer.cpp(s,filter,options,no_string)
    filter = filter or {comments=true}
    if not cpp_keyword then
        cpp_keyword = {
            ["class"] = true, ["break"] = true,  ["do"] = true, ["sizeof"] = true,
            ["else"] = true, ["continue"] = true, ["struct"] = true,
            ["false"] = true, ["for"] = true, ["public"] = true, ["void"] = true,
            ["private"] = true, ["protected"] = true, ["goto"] = true,
            ["if"] = true, ["static"] = true,  ["const"] = true, ["typedef"] = true,
            ["enum"] = true, ["char"] = true, ["int"] = true, ["bool"] = true,
            ["long"] = true, ["float"] = true, ["true"] = true, ["delete"] = true,
            ["double"] = true,  ["while"] = true, ["new"] = true,
            ["namespace"] = true, ["try"] = true, ["catch"] = true,
            ["switch"] = true, ["case"] = true, ["extern"] = true,
            ["return"] = true,["default"] = true,['unsigned']  = true,['signed'] = true,
            ["union"] =  true, ["volatile"] = true, ["register"] = true,["short"] = true,
        }
    end
    if not cpp_matches then
        cpp_matches = {
            {WSPACE,wsdump},
            {PREPRO,pdump},
            {NUMBER3,ndump},
            {IDEN,cpp_vdump},
            {NUMBER4,ndump},
            {NUMBER5,ndump},
            {STRING3,sdump},
            {STRING1,chdump},
            {STRING2,sdump},
            {'^//.-\n',cdump},
            {'^/%*.-%*/',cdump},
            {'^==',tdump},
            {'^!=',tdump},
            {'^<=',tdump},
            {'^>=',tdump},
            {'^->',tdump},
            {'^&&',tdump},
            {'^||',tdump},
            {'^%+%+',tdump},
            {'^%-%-',tdump},
            {'^%+=',tdump},
            {'^%-=',tdump},
            {'^%*=',tdump},
            {'^/=',tdump},
            {'^|=',tdump},
            {'^%^=',tdump},
            {'^::',tdump},
            {'^%.%.%.',tdump},
            {'^.',tdump}
        }
    end
    if not cpp_matches_no_string then
        cpp_matches_no_string = {
            {WSPACE,wsdump},
            {PREPRO,pdump},
            {NUMBER3,ndump},
            {IDEN,cpp_vdump},
            {NUMBER4,ndump},
            {NUMBER5,ndump},
            {'^//.-\n',cdump},
            {'^/%*.-%*/',cdump},
            {'^==',tdump},
            {'^!=',tdump},
            {'^<=',tdump},
            {'^>=',tdump},
            {'^->',tdump},
            {'^&&',tdump},
            {'^||',tdump},
            {'^%+%+',tdump},
            {'^%-%-',tdump},
            {'^%+=',tdump},
            {'^%-=',tdump},
            {'^%*=',tdump},
            {'^/=',tdump},
            {'^|=',tdump},
            {'^%^=',tdump},
            {'^::',tdump},
            {'^%.%.%.',tdump},
            {'^.',tdump}
        }
    end
    return lexer.scan(s,
      not no_string and cpp_matches or cpp_matches_no_string,
      filter,options)
end

--- get a list of parameters separated by a delimiter from a stream.
-- @param tok the token stream
-- @param endtoken end of list (default ')'). Can be '\n'
-- @param delim separator (default ',')
-- @return a list of token lists.
function lexer.get_separated_list(tok,endtoken,delim)
    endtoken = endtoken or ')'
    delim = delim or ','
    local function tappend (tl,t,val)
        val = val or t
        append(tl,{t,val})
    end
    local is_end
    if endtoken == '\n' then
        is_end = function(t,val)
            return t == 'space' and val:find '\n'
        end
    else
        is_end = function (t)
            return t == endtoken
        end
    end
    local is_delim
    if type(delim) == 'function' then
        is_delim = delim
    else
        is_delim = function(t)
            return t == delim
        end
    end
    local parm_values = {}
    local level = 1 -- used to count ( and )
    local tl = {}
    local token,value
    while true do
        token,value=tok()
        if not token then return nil,'EOS' end -- end of stream is an error!
        if is_end(token,value) and level == 1 then
            if next(tl) then
                append(parm_values,tl)
            end
            break
        elseif token == '(' then
            level = level + 1
            tappend(tl,'(')
        elseif token == ')' then
            level = level - 1
            if level == 0 then -- finished with parm list
                append(parm_values,tl)
                break
            else
                tappend(tl,')')
            end
        elseif level == 1 and is_delim(token) then
            append(parm_values,tl) -- a new parm
            tl = {}
        else
            tappend(tl,token,value)
        end
    end
    return parm_values,{token,value}
end

--- get the next non-space token from the stream.
-- @param tok the token stream.
function lexer.skipws (tok)
    return tok:next()
end

local skipws = lexer.skipws

--- get the next token, which must be of the expected type.
-- Throws an error if this type does not match!
-- @param tok the token stream
-- @param expected_type the token type
-- @param no_skip_ws whether we should skip whitespace
function lexer.expecting (tok,expected_type,no_skip_ws)
    assert_arg(1,tok,'function')
    assert_arg(2,expected_type,'string')
    local t,v
    if no_skip_ws then
        t,v = tok()
    else
        t,v = skipws(tok)
    end
    if t ~= expected_type then error ("expecting "..expected_type,2) end
    return v
end

return lexer
