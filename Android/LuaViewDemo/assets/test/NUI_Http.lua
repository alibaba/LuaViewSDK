require "kit/lv_kit"

local label = Label()
label.text("请看日志输出")
label.frame(0, 0, System.screenSize())

http = Http()
http.get("http://www.baidu.com", {
    query = 1
}, function(response)
    print(response)
    printTable(response)
    print(response.data())
end)


