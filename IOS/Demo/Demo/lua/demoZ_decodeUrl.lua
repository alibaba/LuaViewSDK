


function decodeURI(s)
    s = string:gsub(s, '%%(%x%x)', function(h) return string:char(tonumber(h, 16)) end)
    return s
end

function encodeURI(s)
    s = string:gsub(s, "([^%w%.%- ])", function(c) return string:format("%%%02X", string:byte(c)) end)
    return string:gsub(s, " ", "+")
end

local url = "https://ju.taobao.com/json/tg/ajaxGetItemsV2.json?type=0&actSignIds=13250636%2C13653028%2C13730782%2C13658145%2C13433986%2C13261452%2C13740795%2C13653026%2C13654245%2C13555254%2C13260079%2C13508104%2C13406908%2C13567951&stype=dist%2CsoldCount&reverse=down%2Cdown&dist=activitySignId%3A3&page=1&needPt=false&psize=42&_=1451888647101&callback=_brand_items0";
local params = "type=0&actSignIds=13250636,13653028,13730782,13658145,13433986,13261452,13740795,13653026,13654245,13555254,13260079,13508104,13406908,13567951&stype=dist,soldCount&reverse=down,down&dist=activitySignId:3&page=1&needPt=false&psize=42&_=1451888647101&callback=_brand_items0";

print( "params" , params )
params = encodeURI(params);
print( "params" , params )

print(decodeURI(url));


local t = os.date( "*t" );
t.hour = 10;
t.min = 0;
t.sec = 0;
local ostime = os.time(t);
print(ostime)
