Http:get("http://tmsprepub.alicdn.com/market/3c/quanqiutms.php",

function(ret)
	data = ret.data();
	print("ok: " .. tostring(data) )
end
)


Http:post(
"https://login-test.alibaba-inc.com/authorize/login.do",--url
nil,--http头信息
{--http post数据
appcode="123456",
name="taobao-hz\xicheng.dxc",
password="dxc123",
authtype="system"
},
function(request)--http 返回的数据
print(request.data() );
end
);