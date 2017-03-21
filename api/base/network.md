> Class

# Http

网络请求组件，支持http和https协议，支持GET和POST

---

## 使用


Http(initParams, callback)

* initParams: LuaTable，请求参数
* method: String, 请求方法
* params: LuaTable, 请求业务参数
* callback: LuaFunction, 回调
* 返回参数response为LuaTable
* response.data() 得到 Data数据

| ID   | API        | 参数                                       | 返回值     | 平台      | 备注                |
| ---- | ---------- | ---------------------------------------- | ------- | ------- | ----------------- |
| 1    | url        | v: String                                | v       | Android | 设置请求Url           |
| 2    | method     | v: String, get/post                      | v       | Android | 设置请求方法            |
| 3    | retryTimes | v: Number                                | v       | Android | 重试次数              |
| 4    | timeout    | v: Number                                | v       | Android | 超时时间              |
| 5    | params     | v: LuaTable                              | v       | Android | 请求参数              |
| 6    | callback   | v: LuaFunction                           | Android | 请求回调    |                   |
| 7    | request    | -                                        | -       | Android | 请求                |
| 8    | cancel     | -                                        | -       | -       | 中止                |
| 9    | get        | url: String<br/> params: LuaTable<br/> callback: LuaFunction | -       | -       | GET请求             |
| 10   | post       | url: String<br/> params: LuaTable<br/> callback: LuaFunction | -       | -       | POST请求            |
| 11   | header     |                                          | -       | IOS     | http响应的头信息        |
| 11   | data       |                                          | -       | IOS     | http响应的数据体(数据块类型) |
| 11   | code       |                                          | -       | IOS     | http响应的code       |

## 示例


```
Http({
	"method": "POST",
	"params": {
		"k1": "v1",
		"k2": "v2"
	}
}, function(response)
	print("called success")
end)


http = Http()
http:get("http://luaview.github.io", {
    query = 1
}, function(response)
	print("called success")
end)
```

# Downloader

下载器组件，静态。

**(Deprecated 所有功能可以通过 Http and File 完成)**

---

| ID   | API   | 参数                                       | 返回值  | 平台   | 备注   |
| ---- | ----- | ---------------------------------------- | ---- | ---- | ---- |
| 1    | fetch | url: String<br/> name: String<br/> callback: LuaFunction | -    | -    | TODO |
