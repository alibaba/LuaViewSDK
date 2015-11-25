Http:get("http://tmsprepub.alicdn.com/market/3c/quanqiutms.php",

function(ret)
	data = ret.data();
	print("ok: " .. tostring(data) )
end
)

