-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

require "kit/lv_kit"

local label = Label()
label.text("请看日志输出")
label.frame(0, 0, System.screenSize())

http = Http()
http.get("http://www.baidu.com", {
    query = 1
}, function(response)
    local data = response:data()

    print(data)
    test1(data)
    test2(data)
    test3(data)
end)


function test1(data)
    print("-------------------------test1---------------------")
    print("save", File.save(data, "test.html"))
    print("read", File.read("test.html"))
    print("path", File.path("test.html"))
    print("exists", File.exists("test.html"))
    print("--------------------------test1---------------------")
end

function test2(data)
    print("-------------------------test2---------------------")
    print("save", File.save(data, "demo/test.html"))
    print("read", File.read("demo/test.html"))
    print("path", File.path("demo/test.html"))
    print("exists", File.exists("demo/test.html"))
    print("--------------------------test2---------------------")
end

function test3(data)
    print("-------------------------test3---------------------")
    print("save", File.save(data, "../../../common/test.html"))
    print("read", File.read("../../../common/test.html"))
    print("path", File.path("../../../common/test.html"))
    print("exists", File.exists("../../../common/test.html"))
    print("--------------------------test3---------------------")
end

function test4(data)
    print("-------------------------test4---------------------")

    File.save(data, "test1.html", function(status)
        print("file saved", status, File.exists("test1.html"))
        print("read1", File.read("test1.html"), "xxx")


        print("path2", File.path("test1.html"))
        print("exists1", File.exists("test1.html"))
        print("exists3", File.exists("test2.html"))

        File.read("test1.html", function(data)
            print("read2", data)
            print("-------------------------test4---------------------")
        end)
    end)


end




http = Http()
http.get("https://gju1.alicdn.com/bao/uploaded/i4/100000120700895002/TB2Qu0_fXXXXXb9XpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg", function(response)
    local data = response:data()

    File.save(data, "../common/test.png")

    File.read("../common/test.png", function(data)
        local img = Image()
        img.frame(0, 0, 100, 100)
        img.image("../common/test.png")

        local img2 = Image()
        img2.frame(101, 0, 100, 100)
        img2.image(data)
    end)
end)