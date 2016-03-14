require "kit/lv_kit"

http = Http()
http.get("http://www.baidu.com", {
    query = 1
}, function(response)
    print(response)
    printTable(response)
    print(response.data())
end)


