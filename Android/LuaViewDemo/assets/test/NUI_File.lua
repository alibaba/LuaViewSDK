require "kit/lv_kit"

local label = Label()
label.text("请看日志输出")
label.frame(0, 0, System.screenSize())

http = Http()
http.get("http://www.baidu.com", {
    query = 1
}, function(response)
    local data = response:data()

    test1(data)
    test2(data)
end)


function test1(data)
    print("-------------------------test1---------------------")
    File.save(data, "test.html")
    print("exists", File.exists("test.html"))
    print("read", File.read("test.html"))
    print("path", File.path("test.html"))
    print("exists", File.exists("test.html"))
    print("--------------------------test1---------------------")
end

function test2(data)
    print("-------------------------test2---------------------")

    File.save(data, "test1.html", function()
        print("file saved", File.exists("test1.html"))
        print("read1", File.read("test1.html"), "xxx")


        print("path2", File.path("test1.html"))
        print("exists2", File.exists("test2.html"))

        File.read("test1.html", function(data)
            print("read2", data)
            print("-------------------------test2---------------------")
        end)
    end)


end


