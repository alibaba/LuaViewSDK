
http = Http();

http.get(
	"http://tmsprepub.alicdn.com/market/3c/quanqiutms.php",

	function(response)
		print( "responseStatusCode:",response.statusCode() );
		print( "headerFields:",response.headerFields() );
		data = response.data();
		print("ok: " .. tostring(data) )
	end
)

http2 = Http();
http2.post(
	"https://login-test.alibaba-inc.com/authorize/login.do",--url
	{--http post数据
		appcode="123456",
		name="taobao-hz\xicheng.dxc",
		password="dxc123",
		authtype="system"
	},
	function(response)--http 返回的数据
	print(response.data() );
	end
);