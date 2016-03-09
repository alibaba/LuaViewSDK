--- LPeg PEG pattern matching.
-- @module lpeg

local lpeg = {}

---
-- The matching function. It attempts to match the given pattern against the
-- subject string. If the match succeeds, returns the index in the subject of
-- the first character after the match, or the captured values (if the pattern
-- captured any value).
--
-- An optional numeric argument init makes the match starts at that position in
-- the subject string. As usual in Lua libraries, a negative value counts from
-- the end.
--
-- Unlike typical pattern-matching functions, match works only in anchored mode;
-- that is, it tries to match the pattern with a prefix of the given subject
-- string (at position init), not with an arbitrary substring of the subject.
-- So, if we want to find a pattern anywhere in a string, we must either write a
-- loop in Lua or write a pattern that matches anywhere. This second approach is
-- easy and quite efficient; see examples.
function lpeg.match(pattern, subject , init) end

---
-- If the given value is a pattern, returns the string "pattern". Otherwise
-- returns nil.
function lpeg.type(value) end

---
-- Returns a string with the running version of LPeg.
function lpeg.version() end

---
-- Sets the maximum size for the backtrack stack used by LPeg to track calls and
-- choices. Most well-written patterns need little backtrack levels and
-- therefore you seldom need to change this maximum; but a few useful patterns
-- may need more space. Before changing this maximum you should try to rewrite
-- your pattern to avoid the need for extra space.
function lpeg.setmaxstack(max) end

---
-- Converts the given value into a proper pattern, according to the following
-- rules:
--   * If the argument is a pattern, it is returned unmodified.
--   * If the argument is a string, it is translated to a pattern that matches
--     literally the string.
--   * If the argument is a non-negative number n, the result is a pattern that
--     matches exactly n characters.
--   * If the argument is a negative number -n, the result is a pattern that
--     succeeds only if the input string does not have n characters: lpeg.P(-n)
--     is equivalent to -lpeg.P(n) (see the unary minus operation).
--   * If the argument is a boolean, the result is a pattern that always
--     succeeds or always fails (according to the boolean value), without
--     consuming any input.
--   * If the argument is a table, it is interpreted as a grammar (see
--     Grammars).
--   * If the argument is a function, returns a pattern equivalent to a
--     match-time capture over the empty string.
function lpeg.P(value) end

---
-- Returns a pattern that matches any single character belonging to one of the
-- given ranges. Each range is a string xy of length 2, representing all
-- characters with code between the codes of x and y (both inclusive).
-- As an example, the pattern `lpeg.R("09")` matches any digit, and `lpeg.R("az",
-- "AZ")` matches any ASCII letter.
function lpeg.R({range}) end

---
-- Returns a pattern that matches any single character that appears in the given
-- string. (The S stands for Set.)
-- As an example, the pattern lpeg.S("+-*/") matches any arithmetic operator.
-- Note that, if s is a character (that is, a string of length 1), then
-- lpeg.P(s) is equivalent to lpeg.S(s) which is equivalent to lpeg.R(s..s).
-- Note also that both lpeg.S("") and lpeg.R() are patterns that always fail.
function lpeg.S(string) end

---
-- This operation creates a non-terminal (a variable) for a grammar. The created
-- non-terminal refers to the rule indexed by v in the enclosing grammar. (See
-- Grammars for details.)
function lpeg.V(v) end

---
-- Returns a table with patterns for matching some character classes according
-- to the current locale. The table has fields:
--
--   * alnum
--   * alpha
--   * cntrl
--   * digit
--   * graph
--   * lower
--   * print
--   * punct
--   * space
--   * upper
--   * xdigit
--
-- each one containing a
-- correspondent pattern. Each pattern matches any single character that belongs
-- to its class.
--
-- If called with an argument table, then it creates those fields inside the
-- given table and returns that table.
function lpeg.locale(table) end

---
-- Creates a simple capture, which captures the substring of the subject that
-- matches patt. The captured value is a string. If patt has other captures,
-- their values are returned after this one.
function lpeg.C(patt) end

---
-- Creates an argument capture. This pattern matches the empty string and
-- produces the value given as the nth extra argument given in the call to
-- lpeg.match.
function lpeg.Carg(n) end

---
-- Creates a back capture. This pattern matches the empty string and produces
-- the values produced by the most recent group capture named name.
-- Most recent means the last complete outermost group capture with the given
-- name. A Complete capture means that the entire pattern corresponding to the
-- capture has matched. An Outermost capture means that the capture is not
-- inside another complete capture.
function lpeg.Cb(name) end

---
-- Creates a constant capture. This pattern matches the empty string and
-- produces all given values as its captured values.
function lpeg.Cc(...) end

---
-- Creates a fold capture. If patt produces a list of captures C1 C2 ... Cn,
-- this capture will produce the value func(...func(func(C1, C2), C3)..., Cn),
-- that is, it will fold (or accumulate, or reduce) the captures from patt using
-- function func.
--
-- This capture assumes that patt should produce at least one capture with at
-- least one value (of any type), which becomes the initial value of an
-- accumulator. (If you need a specific initial value, you may prefix a constant
-- capture to patt.) For each subsequent capture LPeg calls func with this
-- accumulator as the first argument and all values produced by the capture as
-- extra arguments; the value returned by this call becomes the new value for
-- the accumulator. The final value of the accumulator becomes the captured
-- value.
--
-- As an example, the following pattern matches a list of numbers separated by
-- commas and returns their addition:
--
--      -- matches a numeral and captures its value
--      number = lpeg.R"09"^1 / tonumber
--      -- matches a list of numbers, captures their values
--      list = number * ("," * number)^0
--      -- auxiliary function to add two numbers
--      function add (acc, newvalue) return acc + newvalue end
--      -- folds the list of numbers adding them
--      sum = lpeg.Cf(list, add)
--      -- example of use
--      print(sum:match("10,30,43"))   --> 83
--
function lpeg.Cf(patt, func) end

---
-- Creates a group capture. It groups all values returned by patt into a single
-- capture. The group may be anonymous (if no name is given) or named with the
-- given name.
-- An anonymous group serves to join values from several captures into a single
-- capture. A named group has a different behavior. In most situations, a named
-- group returns no values at all. Its values are only relevant for a following
-- back capture or when used inside a table capture.
function lpeg.Cg(patt , name) end

---
-- Creates a position capture. It matches the empty string and captures the
-- position in the subject where the match occurs. The captured value is a
-- number.
function lpeg.Cp() end

---
-- Creates a substitution capture, which captures the substring of the subject
-- that matches patt, with substitutions. For any capture inside patt with a
-- value, the substring that matched the capture is replaced by the capture
-- value (which should be a string). The final captured value is the string
-- resulting from all replacements.
function lpeg.Cs(patt) end

---
-- Creates a table capture. This capture creates a table and puts all values
-- from all anonymous captures made by patt inside this table in successive
-- integer keys, starting at 1. Moreover, for each named capture group created
-- by patt, the first value of the group is put into the table with the group
-- name as its key. The captured value is only the table.
function lpeg.Ct(patt) end

---
-- Creates a match-time capture. Unlike all other captures, this one is
-- evaluated immediately when a match occurs. It forces the immediate evaluation
-- of all its nested captures and then calls function.
-- The given function gets as arguments the entire subject, the current position
-- (after the match of patt), plus any capture values produced by patt.
-- The first value returned by function defines how the match happens. If the
-- call returns a number, the match succeeds and the returned number becomes the
-- new current position. (Assuming a subject s and current position i, the
-- returned number must be in the range [i, len(s) + 1].) If the call returns
-- true, the match succeeds without consuming any input. (So, to return true is
-- equivalent to return i.) If the call returns false, nil, or no value, the
-- match fails.
-- Any extra values returned by the function become the values produced by the
-- capture.
function lpeg.Cmt(patt, function) end

return lpeg
