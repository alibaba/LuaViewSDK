t = "123456789";
print( t.byte(2,4) )

print( string:char(51,52,53) )

local s = "just a test";
startPos, endPos = s.find("st")
print(startPos, endPos)

print(string:format("%s is %d", "ten", 10))


s = "just a test"
for w in s.gmatch("%a+") do
	print(w)
end


local s = "just a test";
print( s.gsub("st", "*"))



local s = "num is 1234567890"
print( s.gsub("%d", "*"))



s = "sethook, setlocal, setmetatable, setupvalue, setuservalue"
print( s.gsub("s%a+", "S", 2) )


local s = "just a test"
print(s.match("test"))

s = "today is 2003-5-31"
print(s.match("%d+-%d+-%d+"))

s = "abcdabcd"
print(s.match("a"))

local s = "你好"
print( s.rep(3) )
print( s.rep(3,'!dddd')  )

local s = "reverse";
print(s.reverse())

local s = "abcdefg";
print(s.sub(2, 4))


HELP_MESSAGE = [[
This is the first line of help to show.
And here is the second.
And here is the third.
Let's make sure to include enough text to bore the user.
]];
for line in HELP_MESSAGE.gfind('[^\n]+') do
print(line);
end






