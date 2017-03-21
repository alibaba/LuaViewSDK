> Static Class

# File

文件操作组件，静态类，无法实例化，通过静态方法直接调用

| ID   | API    | 参数                                       | 返回值                      | 平台   | 备注                                       |
| ---- | ------ | ---------------------------------------- | ------------------------ | ---- | ---------------------------------------- |
| 1    | save   | path: String<br/> data: <a href="#data">Data<a/> <br/> callback: LuaFunction or <br/> data: <a href="#data">Data<a/> <br/> path: String<br/> callback: LuaFunction | -                        | -    | 保存data内数据到path指定的文件内，文件名支持子目录或者上级目录      |
| 2    | read   | path: String<br/> callback: LuaFunction  | <a href="#data">Data</a> | -    | 读取给定path的文件，并返回Data数据，支持异步读取，异步返回数据通过callback返回 |
| 3    | exists | path: String                             | -                        | -    | 文件是否存在                                   |
| 4    | path   | name: filename                           | path:String              | -    | 获取给定文件名的绝对存储路径                           |

## 示例

```
http = Http()
http:get("http://luaview.github.io", {
    query = 1
}, function(response)
  local data = response:data()

	-- 保存
	File.save(data, "test.html") -- 同步存
	File.save(data, "test.html", function(status)
		-- 异步存
	end)

	-- 读取
	data = File.read("test.html") -- 同步读
	File.read("test.html", function(data)
		-- 异步读取
	end)

	-- 存在判断
	print(File.exists("test.html"))

	-- 文件路径
	print(File.path("test.html"))
end)
```
