
local t = {"alpha", "beta", "gamma"}
print(table:concat(t,":"))

print(table:concat(t, nil, 1, 2))

print(table:concat(t, "\n", 2, 3))

print(#t);