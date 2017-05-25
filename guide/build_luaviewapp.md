# 构建你的第一个LuaView应用
## 1 从GitHub克隆最新版本的LuaViewSDK到本地。
- github网址: https://github.com/alibaba/LuaViewSDK
- https方式：git clone https://github.com/alibaba/LuaViewSDK.git
- ssh方式：git clone git@github.com:alibaba/LuaViewSDK.git

## 2 Android工程搭建
### 2.1 打开Android Studio新建一个Android工程
### 2.2 在工程里添加LuaView的依赖

（1）通过 File -> New -> Import Module，选择克隆到本地的LuaViewSDK目录下的Android/LuaView子目录。

（2）在项目中的build.gradle文件中添加LuaView的依赖

	dependencies {
		.....
	    compile project(':LuaView')
	}

### 2.3 继承自LuaViewBasicActivity
把应用的Launch Activity改成继承自LuaViewBasicActivity。默认Lua业务代码的主入口是LuaView包里的assets/kit/main.lua，默认入口主页面是App.lua，开发者可以更改这两个设置。


```java
public class MyActivity extends LuaViewBasicActivity {
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
      }

      /**
      * 开发者可以在这里更改入口主页面, 默认是App.lua
      * @return
      */
      @Override
      public String getMainPage() {
          return "App";
      }

      /**
      * 开发者可以在这里更改Lua代码的主入口
      * 默认使用kit包下的main.lua
      * 该包提供了一套界面描述和业务逻辑分离的机制
      * @return
      */
      @Override
      public String getLuaViewEntry() {
          return "kit/main.lua";
      }
  }
```

## 3 iOS工程搭建
### 3.1 打开Xcode新建一个iOS工程
### 3.2 在工程里添加相关依赖
新建podfile文件
在podfile文件里面添加

```objectivec
    pod 'LuaViewSDK'
```
执行 `pod install`
### 3.3 添加相关依赖代码
新建XXLuaViewController 作为容器
```objectivec
#import <UIKit/UIKit.h>
#import "LViewController.h"

@interface MyLuaViewController : LViewController

@end
```

在AppDelegate中设MyLuaViewController 为rootViewController,同时载入首页脚本

```objectivec
@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];

    MyLuaViewController* controller = [[MyLuaViewController alloc] initWithPackage:[[NSBundle mainBundle] resourcePath] mainScript:@"kit/main.lua"];
    NSString* entry = @"Playground";
    controller.args = (NSDictionary*)entry;

    self.window.rootViewController = [[UINavigationController alloc]
                                  initWithRootViewController:controller];
    [self.window makeKeyAndVisible];

    return YES;
}
```
添加页面跳转的Bridge接口
```objectivec
- (void)didCreateLuaView:(LView *)view {
    [super didCreateLuaView:view];
    self.lv[@"Bridge"] = self;
}

-(void)require:(NSDictionary*)args {
    JHSLuaViewController* c = [[JHSLuaViewController alloc] initWithPackage:nil mainScript:@"kit/main.lua"];
    c.args = args;
    [self.navigationController pushViewController:c animated:YES];
}
```
## 4 Lua业务代码
### 4.1 关于Lua业务代码的主入口kit/main.lua
kit包提供了一种基于XML的布局机制，分离出界面描述和业务逻辑，使得业务代码可以写得更加优雅。其中，pica.lua是一个负责解析XML标签、创建控件对象、设置控件属性，以及最终布局和渲染视图的单例类。每个虚拟机仅需创建一个这个实例。
### 4.2 如何实现一个页面
在工程的assets目录下创建App.lua文件，Lua页面的写法如下：
```lua
local meta = object:new()

function meta:onCreate()
    self.views = pica:getInstance():render("app.xml")
end

return meta
```
onCreate()方法会在页面初始化的时候被调用。`pica:getInstance():render("app.xml")`，会返回XML文件中所有带有id的UI对象，开发者可以以字典的方式取得自己所关注的对象用于操作视图。

### 4.3 实现app.xml布局文件
同样地在assets目录下，创建app.xml文件，XML文件的写法如下：
```xml
<View frame="0, 0, sys.contW, sys.contH" css="{justify-content='center', align-items='center'}">
    <Text css="{width=sys.contW/2, height=sys.contW/8}" text="Hello World" fontSize="18" textAlign="TextAlign.CENTER"></Text>
</View>
```
在XML中，支持两种布局方式，一种是frame布局，也就是绝对布局。另一种是flex布局，使用Facebook的csslayout布局。到此为止，运行工程就可以看到LuaView的Hello World了。

![Hello World](https://gw.alicdn.com/tfs/TB1_8mWQpXXXXa9aXXXXXXXXXXX-738-1306.png =369x653)

## 5 列表示例
完成了LuaView的Hello World示例，我们再来编写一个展示如何在LuaView中使用列表的例子。这个例子会从豆瓣的开放API获取正在热映的20部电影，并在一个列表中显示出来。我们先只展现一部电影的缩略图、片名、演职员信息以及评分和看过的人数。修改app.xml，如下：
```xml
<View id="root" frame="10, 5, sys.contW-20, sys.contH/3-10" css="{flex-direction='row'}" bg="0xffffff" >
    <View id="left.pannel" css="{flex-direction='column', flex=2}">
        <Image id="profile" css="{flex=1, margin=15}"></Image>
    </View>
    <View id="right.pannel" css="{flex-direction='column', flex=3}">
        <Text id="movieName" css="{flex=1, margin-top=15}" fontSize="18" textColor="0x000000"></Text>
        <Text id="score" css="{height=20}" textColor="0xe61414" fontSize="14"></Text>
        <Text id="character" css="{flex=2}" lineCount="3" textColor="0x666666" fontSize="16"></Text>
        <Text id="number" css="{flex=1, margin-bottom=10}" textColor="0xe61414" fontSize="14"></Text>
    </View>
</View>
```
这个时候我们需要mock数据来填充视图，在App.lua中修改如下：
```lua
local meta = object:new()

function meta:onCreate()
    self.views = pica:getInstance():render("app.xml")

    self.views["profile"]:backgroundColor(0x666666)
    self.views["movieName"]:text("电影名称")
    self.views["score"]:text("评分: 8")
    local director = "导演: 某某某"
    local actors = "主演: 某某A/某某B"
    self.views["character"]:text(director .. "\n" .. actors)
    self.views["number"]:text("1900人看过")
end

return meta
```
在这段代码中，我们根据app.xml中的id可以取出关注的对象进行属性的设置。再次运行程序，可以看到屏幕发生了变化。

![Item](https://gw.alicdn.com/tfs/TB1i7fnQpXXXXXkXFXXXXXXXXXX-734-1300.png =369x653)

接下来我们从豆瓣的开放API抓取真实的数据用来展示：
```lua
local meta = object:new()

function meta:onCreate()
    self.views = pica:getInstance():render("app.xml")

    Http():get("http://api.douban.com/v2/movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a", function(response)
        if (tostring(response:code()) == "200") then
            local _jsonData = Json:toTable(tostring(response:data()))
            self.views["profile"]:image(_jsonData["subjects"][1]["images"]["large"])
            self.views["movieName"]:text(_jsonData["subjects"][1]["title"])
            self.views["score"]:text("评分: " .. _jsonData["subjects"][1]["rating"]["average"])

            local director = "导演: " .. _jsonData["subjects"][1]["directors"][1]["name"]
            local actors = "主演: "
            for _k, _v in pairs(_jsonData["subjects"][1]["casts"]) do
                actors = actors .. _v["name"] .. "/"
            end
            self.views["character"]:text(director .. "\n" .. actors)

            self.views["number"]:text(_jsonData["subjects"][1]["collect_count"] .. "人看过")
        else
            Toast("Request Error")
        end
    end)
end

return meta
```
目前为止，只是展示一部电影的信息，为了渲染更多的条目，我们需要List组件。另外由于网络请求的不稳定性和耗时性，我们需要Load加载组件。在assets目录下新增一个list.xml文件如下：
```xml
<View id="root" frame="0, 0, sys.contW, sys.contH">
    <List id="tableView" frame="0, 0, sys.contW, sys.contH" bg="0xeeeeee"></List>
    <Load id="loading" color="0xF06292" frame="0, sys.contH/2 - sys.contW/8, sys.contW, sys.contW/8"></Load>
</View>
```
相应地，App.lua也要做出更改：
```lua
local meta = object:new()

function meta:onCreate()
    self.views = pica:getInstance():render("list.xml")
    self.loading = self.views["loading"]
    self.list = self.views["tableView"]

    self:handle()
end

function meta:handle()
    self.loading:show()
    Http():get("http://api.douban.com/v2/movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a", function(response)
        self.loading:hide()
        if (tostring(response:code()) == "200") then
            local _jsonData = Json:toTable(tostring(response:data()))
            self.list:initParams({
                Section = {
                    SectionCount = function()
                        return 1
                    end,
                    RowCount = function(section)
                        return _jsonData["count"]
                    end
                },
                Cell = {
                    Id = function(section, row)
                        return "ItemCell"
                    end,
                    ItemCell = {
                        Size = function(section, row)
                            return sys.contW, sys.contH/3
                        end,
                        Init = function(cell, section, row)
                            cell.objs = pica:getInstance():render("app.xml")
                        end,
                        Layout = function(cell, section, row)
                            cell.objs["profile"]:image(_jsonData["subjects"][row]["images"]["large"])
                            cell.objs["movieName"]:text(_jsonData["subjects"][row]["title"])
                            cell.objs["score"]:text("评分: " .. _jsonData["subjects"][row]["rating"]["average"])

                            local director = "导演: " .. _jsonData["subjects"][row]["directors"][1]["name"]
                            local actors = "主演: "
                            for _k, _v in pairs(_jsonData["subjects"][row]["casts"]) do
                                actors = actors .. _v["name"] .. "/"
                            end
                            cell.objs["character"]:text(director .. "\n" .. actors)

                            cell.objs["number"]:text(_jsonData["subjects"][row]["collect_count"] .. "人看过")
                        end
                    }
                }
            })

            self.list:reload()
        else
            Toast("Request Error")
        end
    end)
end

return meta
```
代码的关键是要往List中设置数据源，并且重新reload列表。关于List的更多用法可以查看API文档，<https://alibaba.github.io/LuaViewSDK/apimenu.html>。此时，再次运行程序，可以看到界面生成了一个完整的电影列表。

![List](https://gw.alicdn.com/tfs/TB1GL6vQpXXXXa9XpXXXXXXXXXX-728-1296.png =369x653)


恭喜你完成了第一个LuaView应用，接下来开启你的LuaView之旅吧。
