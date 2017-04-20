scrW,scrH = System:screenSize();
label = Label();
label:frame(0,0,scrW,scrH-64);
label:lineCount(100);

function myprint( s )
	print(s);
	local text = label:text();
	if( text ) then 
		label:text( text .. "\n" .. s);
	else
		label:text( s);
	end
end


t = "123456789";
myprint( t:byte(2,4) )

myprint( string.char(51,52,53) )

local s = "just a test";
startPos, endPos = s:find("st")
myprint(startPos, endPos)

myprint(string.format("%s is %d", "ten", 10))


s = "just a test"
for w in s:gmatch("%a+") do
	myprint(w)
end


local s = "just a test";
myprint( s:gsub("st", "*"))



local s = "num is 1234567890"
myprint( s:gsub("%d", "*"))



s = "sethook, setlocal, setmetatable, setupvalue, setuservalue"
myprint( s:gsub("s%a+", "S", 2) )


local s = "just a test"
myprint(s:match("test"))

s = "today is 2003-5-31"
myprint(s:match("%d+-%d+-%d+"))

s = "abcdabcd"
myprint(s:match("a"))

local s = "你好"
myprint( s:rep(3) )
myprint( s:rep(3,'!dddd')  )

local s = "reverse";
myprint(s:reverse())

local s = "abcdefg";
myprint(s:sub(2, 4))


HELP_MESSAGE = [[
This is the first line of help to show.
And here is the second.
And here is the third.
Let's make sure to include enough text to bore the user.
]];
for line in HELP_MESSAGE:gfind('[^\n]+') do
myprint(line);
end

myprint("--end---");





