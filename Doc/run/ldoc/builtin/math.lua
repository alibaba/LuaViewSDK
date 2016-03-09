--- standard mathematical functions.
-- @module math

local math = {}

---
-- Returns the absolute value of `x`.
function math.abs(x) end

---
-- Returns the arc cosine of `x` (in radians).
function math.acos(x) end

---
-- Returns the arc sine of `x` (in radians).
function math.asin(x) end

---
-- Returns the arc tangent of `x` (in radians).
function math.atan(x) end

---
-- Returns the arc tangent of `y/x` (in radians), but uses the signs
-- of both parameters to find the quadrant of the result. (It also handles
-- correctly the case of `x` being zero.)
function math.atan2(y, x) end

---
-- Returns the smallest integer larger than or equal to `x`.
function math.ceil(x) end

---
-- Returns the cosine of `x` (assumed to be in radians).
function math.cos(x) end

---
-- Returns the hyperbolic cosine of `x`.
function math.cosh(x) end

---
-- Returns the angle `x` (given in radians) in degrees.
function math.deg(x) end

---
-- Returns the value *e^x*.
function math.exp(x) end

---
-- Returns the largest integer smaller than or equal to `x`.
function math.floor(x) end

---
-- Returns the remainder of the division of `x` by `y` that rounds the
-- quotient towards zero.
function math.fmod(x, y) end

---
-- Returns `m` and `e` such that *x = m2^e*, `e` is an integer and the
-- absolute value of `m` is in the range *[0.5, 1)* (or zero when `x` is zero).
function math.frexp(x) end

---
-- The value `HUGE_VAL`, a value larger than or equal to any other
-- numerical value.
-- function math.huge end
-- * `math.HUGE_VAL`: math.HUGE_VAL

---
-- Returns *m2^e* (`e` should be an integer).
function math.ldexp(m, e) end

---
-- Returns the natural logarithm of `x`.
function math.log(x) end

---
-- Returns the base-10 logarithm of `x`.
function math.log10(x) end

---
-- Returns the maximum value among its arguments.
function math.max(x, ...) end

---
-- Returns the minimum value among its arguments.
function math.min(x, ...) end

---
-- Returns two numbers, the integral part of `x` and the fractional part of
-- `x`.
function math.modf(x) end

---
-- The value of *pi*.
-- function math.pi end
-- * `math.pi`: math.pi

---
-- Returns *x^y*. (You can also use the expression `x^y` to compute this
-- value.)
function math.pow(x, y) end

---
-- Returns the angle `x` (given in degrees) in radians.
function math.rad(x) end

---
-- This function is an interface to the simple pseudo-random generator
-- function `rand` provided by ANSI C. (No guarantees can be given for its
-- statistical properties.)
-- When called without arguments, returns a uniform pseudo-random real
-- number in the range *[0,1)*. When called with an integer number `m`,
-- `math.random` returns a uniform pseudo-random integer in the range *[1,
-- m]*. When called with two integer numbers `m` and `n`, `math.random`
-- returns a uniform pseudo-random integer in the range *[m, n]*.
function math.random(m , n) end

---
-- Sets `x` as the "seed" for the pseudo-random generator: equal seeds
-- produce equal sequences of numbers.
function math.randomseed(x) end

---
-- Returns the sine of `x` (assumed to be in radians).
function math.sin(x) end

---
-- Returns the hyperbolic sine of `x`.
function math.sinh(x) end

---
-- Returns the square root of `x`. (You can also use the expression `x^0.5`
-- to compute this value.)
function math.sqrt(x) end

---
-- Returns the tangent of `x` (assumed to be in radians).
function math.tan(x) end

---
-- Returns the hyperbolic tangent of `x`.
function math.tanh(x) end

return math
