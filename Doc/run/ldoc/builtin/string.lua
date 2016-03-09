--- string operations like searching and matching.
-- @module string

local string = {}

---
-- Returns the internal numerical codes of the characters `s[i]`, `s[i+1]`,
-- ..., `s[j]`. The default value for `i` is 1; the default value for `j`
-- is `i`.
-- Note that numerical codes are not necessarily portable across platforms.
function string.byte(s , i , j) end

---
-- Receives zero or more integers. Returns a string with length equal to
-- the number of arguments, in which each character has the internal numerical
-- code equal to its corresponding argument.
-- Note that numerical codes are not necessarily portable across platforms.
function string.char(...) end

---
-- Returns a string containing a binary representation of the given
-- function, so that a later `loadstring` on this string returns a copy of
-- the function. `function` must be a Lua function without upvalues.
function string.dump(function) end

---
-- Looks for the first match of `pattern` in the string `s`. If it finds a
-- match, then `find` returns the indices of `s` where this occurrence starts
-- and ends; otherwise, it returns nil. A third, optional numerical argument
-- `init` specifies where to start the search; its default value is 1 and
-- can be negative. A value of true as a fourth, optional argument `plain`
-- turns off the pattern matching facilities, so the function does a plain
-- "find substring" operation, with no characters in `pattern` being considered
-- "magic". Note that if `plain` is given, then `init` must be given as well.
-- If the pattern has captures, then in a successful match the captured values
-- are also returned, after the two indices.
function string.find(s, pattern , init , plain) end

---
-- Returns a formatted version of its variable number of arguments following
-- the description given in its first argument (which must be a string). The
-- format string follows the same rules as the `printf` family of standard C
-- functions. The only differences are that the options/modifiers `*`, `l`,
-- `L`, `n`, `p`, and `h` are not supported and that there is an extra option,
-- `q`. The `q` option formats a string in a form suitable to be safely read
-- back by the Lua interpreter: the string is written between double quotes,
-- and all double quotes, newlines, embedded zeros, and backslashes in the
-- string are correctly escaped when written. For instance, the call
--
--     string.format('%q', 'a string with "quotes" and \n new line')
--
-- will produce the string:
--
--     "a string with \"quotes\" and \
--         new line"
--
-- The options `c`, `d`, `E`, `e`, `f`, `g`, `G`, `i`, `o`, `u`, `X`, and
-- `x` all expect a number as argument, whereas `q` and `s` expect a string.
-- This function does not accept string values containing embedded zeros,
-- except as arguments to the `q` option.
function string.format(formatstring, ...) end

---
-- Returns an iterator function that, each time it is called, returns the
-- next captures from `pattern` over string `s`. If `pattern` specifies no
-- captures, then the whole match is produced in each call.
-- As an example, the following loop
--
--     s = "hello world from Lua"
--     for w in string.gmatch(s, "%a+") do
--       print(w)
--     end
--
-- will iterate over all the words from string `s`, printing one per line. The
-- next example collects all pairs `key=value` from the given string into
-- a table:
--
--     t = {}
--     s = "from=world, to=Lua"
--     for k, v in string.gmatch(s, "(%w+)=(%w+)") do
--       t[k] = v
--     end
--
-- For this function, a '`^`' at the start of a pattern does not work as an
-- anchor, as this would prevent the iteration.
function string.gmatch(s, pattern) end

---
-- Returns a copy of `s` in which all (or the first `n`, if given)
-- occurrences of the `pattern` have been replaced by a replacement string
-- specified by `repl`, which can be a string, a table, or a function. `gsub`
-- also returns, as its second value, the total number of matches that occurred.
--
-- If `repl` is a string, then its value is used for replacement. The character
-- `%` works as an escape character: any sequence in `repl` of the form `%n`,
-- with *n* between 1 and 9, stands for the value of the *n*-th captured
-- substring (see below). The sequence `%0` stands for the whole match. The
-- sequence `%%` stands for a single `%`.
--
-- If `repl` is a table, then the table is queried for every match, using
-- the first capture as the key; if the pattern specifies no captures, then
-- the whole match is used as the key.
--
-- If `repl` is a function, then this function is called every time a match
-- occurs, with all captured substrings passed as arguments, in order; if
-- the pattern specifies no captures, then the whole match is passed as a
-- sole argument.
--
-- If the value returned by the table query or by the function call is a
-- string or a number, then it is used as the replacement string; otherwise,
-- if it is false or nil, then there is no replacement (that is, the original
-- match is kept in the string).
--
-- Here are some examples:
--     x = string.gsub("hello world", "(%w+)", "%1 %1")
--     --> x="hello hello world world"
--     x = string.gsub("hello world", "%w+", "%0 %0", 1)
--     --> x="hello hello world"
--     x = string.gsub("hello world from Lua", "(%w+)%s*(%w+)", "%2 %1")
--     --> x="world hello Lua from"
--     x = string.gsub("home = $HOME, user = $USER", "%$(%w+)", os.getenv)
--     --> x="home = /home/roberto, user = roberto"
--     x = string.gsub("4+5 = $return 4+5$", "%$(.-)%$", function (s)
--           return loadstring(s)()
--         end)
--     --> x="4+5 = 9"
--     local t = {name="lua", version="5.1"}
--     x = string.gsub("$name-$version.tar.gz", "%$(%w+)", t)
--     --> x="lua-5.1.tar.gz"
function string.gsub(s, pattern, repl , n) end

---
-- Receives a string and returns its length. The empty string `""` has
-- length 0. Embedded zeros are counted, so `"a\000bc\000"` has length 5.
function string.len(s) end

---
-- Receives a string and returns a copy of this string with all uppercase
-- letters changed to lowercase. All other characters are left unchanged. The
-- definition of what an uppercase letter is depends on the current locale.
function string.lower(s) end

---
-- Looks for the first *match* of `pattern` in the string `s`. If it
-- finds one, then `match` returns the captures from the pattern; otherwise
-- it returns nil. If `pattern` specifies no captures, then the whole match
-- is returned. A third, optional numerical argument `init` specifies where
-- to start the search; its default value is 1 and can be negative.
function string.match(s, pattern , init) end

---
-- Returns a string that is the concatenation of `n` copies of the string
-- `s`.
function string.rep(s, n) end

---
-- Returns a string that is the string `s` reversed.
function string.reverse(s) end

---
-- Returns the substring of `s` that starts at `i` and continues until
-- `j`; `i` and `j` can be negative. If `j` is absent, then it is assumed to
-- be equal to -1 (which is the same as the string length). In particular,
-- the call `string.sub(s,1,j)` returns a prefix of `s` with length `j`, and
-- `string.sub(s, -i)` returns a suffix of `s` with length `i`.
function string.sub(s, i , j) end

---
-- Receives a string and returns a copy of this string with all lowercase
-- letters changed to uppercase. All other characters are left unchanged. The
-- definition of what a lowercase letter is depends on the current locale.
function string.upper(s) end

return string

