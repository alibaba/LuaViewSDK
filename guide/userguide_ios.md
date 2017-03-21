# SDK使用说明 - iOS

## 如何接入

1. **Pods引入（推荐）**

   在目标工程Podfile中加入如下命令，可以自动将最新的LuaViewSDK和对应的Lua虚拟机代码同时引入：

   `pod 'LuaViewSDK', :git => 'git@gitlab.alibaba-inc.com:luaview/LuaViewSDK.git'`

2. **源码引入**

   在Github上clone或者下载最新版本源代码：[https://github.com/alibaba/LuaViewSDK](https://github.com/alibaba/LuaViewSDK)，将LuaViewSDK和Lua虚拟机的源代码拷贝到目标工程中。如下图所示，红色表示的是LuaViewSDK源代码，绿色表示的是Lua虚拟机源代码

   ![](https://gw.alicdn.com/tfs/TB1YBlzQXXXXXbhapXXXXXXXXXX-560-788.png)

## 基本用法

LuaViewSDK提供了两种方法进行LuaView脚本的动态渲染，一种使用LuaView对象进行脚本的加载和渲染，两一种是使用LuaViewCore虚拟机对象进行脚本加载，并对任意的View进行动态渲染

1. **使用LuaView对象加载脚本和渲染：**

   - *创建LuaView对象*

   ```objectivec
   LuaView *luaview = [[LuaView alloc] initWithFrame:rect];
   ```

   - *创建Bundle对象，用于文件搜索；如果只使用本地文件，则可跳过第2步*

   ```objectivec
   LVBundle *bundle = [[LVBundle alloc] init];
   luaview.bundle = bundle;
   //2.1 设置脚本的搜索路径，可添加多个路径；如果只使用工程本地脚本，则无需设置
   [luaview.bundle addScriptPath:packagePath];  
   //2.2 设置资源文件的搜索路径；如果只使用工程本地资源，则无需设置
   [luaview.bundle addResourcePath:packagePath];
   ```

   - *加载并运行脚本*

   ```objectivec
   //3.1 加载明文脚本
   [luaview runFile:scriptFileName];
   //3.2 加载加密脚本
   [luaview runSignFile:scriptFileName];
   ```

   - *使用完毕清理内存*

   ```objectivec
   //LuaView对象被回收之前必须清理内存
   [luaview releaseLuaView];
   ```

   这样生成的View可以作为单个view嵌入到任意view中使用，也可以作为独立页面添加到ViewController中使用。如果需要使用独立的VC页面，推荐使用SDK中 `LViewController`，已经做好了各种生命周期和内存管理的处理

   - *直接使用LViewController*

   ```objectivec
   //1. 创建LuaView VC
   LViewController *luaVC = [[LViewController alloc] init];

   //2. 加载并运行脚本
   [luaVC.lv runFile:scriptFileName];
   ```

   **进阶：**

   开发者也可以使用完全自定义的View Controller，将LuaView作为一个普通的view加入到view层级中，处理好view的事件，并做好内存回收即可，例：

   ```objectivec
   //创建LuaView
   -(void)viewDidLoad{
     	self.luaview = [[LuaView alloc] init];
     	self.luaview.viewController = self;
     	
     	[self.view addSubview:self.luaview];
   }

   //事件管理
   - (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
       [self.luaview motionBegan:motion withEvent:event];
   }

   - (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
       [self.luaview motionCancelled:motion withEvent:event];
   }

   - (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
       [self.luaview motionEnded:motion withEvent:event];
   }

   -(void) viewWillAppear:(BOOL)animated{
       [super viewWillAppear:animated];
       [self.luaview viewWillAppear];
   }

   -(void) viewDidAppear:(BOOL)animated{
       [super viewDidAppear:animated];
       [self.luaview viewDidAppear];
   }

   -(void) viewWillDisappear:(BOOL)animated{
       [super viewWillDisappear:animated];
       [self.luaview viewWillDisAppear];
   }

   -(void) viewDidDisappear:(BOOL)animated{
       [super viewDidDisappear:animated];
       [self.luaview viewDidDisAppear];
   }

   //清理内存
   -(void)dealloc{
     	[self.luaview releaseLuaView];
   }
   ```

2. **进阶：使用LuaViewCore加载脚本进行共享虚拟机渲染 - 一个虚拟机渲染多个页面**

   由于LuaView可以作为一个标准的View放到任何页面使用，虽然LuaViewSDK性能相对较高，但是当一个页面中有过多个LuaView时，依然可能会造成性能下降；在这种情况下，为了提高性能和渲染效率，我们建议使用LuaViewCore作为共享虚拟机进行渲染。如下图所示，聚划算客户端的首页是Native结构，但是其中所有的商品坑位都是使用同一个LuaViewCore进行共享渲染

   <img src="https://img.alicdn.com/tfs/TB1WC45QXXXXXabXVXXXXXXXXXX-1242-2208.jpg" width='50%' height='50%'/>

   **如何使用共享LuaViewCore进行渲染？**

   a. 创建一个LuaViewCore

   ```objectivec
   LuaViewCore *lvCore = [[LuaViewCore alloc] init];
   ```

   b. 设置脚本和资源搜索路径（可选）

   ```objectivec
   LVBundle *bundle = [[LVBundle alloc] init];
   lvCore.bundle = bundle;
   //2.1 设置脚本的搜索路径，可添加多个路径；如果只使用工程本地脚本，则无需设置
   [luaview.bundle addScriptPath:packagePath];  
   //2.2 设置资源文件的搜索路径；如果只使用工程本地资源，则无需设置
   [luaview.bundle addResourcePath:packagePath];
   ```

   c. 加载脚本文件

   ```objectivec
   [lvCore loadFile:scriptName];
   ```

   d. 渲染指定View（可以是任意view）

   ```objectivec
   //targetView可以是用户指定的任意一个view，不必是LuaView对象
   //luaFunctionName是加载的LuaView脚本里的函数
   NSString *functionName = @"luaFuntionName";
   [lvCore callLua:functionName environment:targetView args:nil];
   ```

3. **如何使用加密脚本**

   LuaViewSDK支持RSA2048非对称加密。如果脚本使用了RSA加密，那么在加载脚本之前需要加载公钥证书。

   ```objectivec
   LuaView *luaview = [[LuaView alloc] init];

   NSString filePath = @"CertificatePath"; //公钥证书路径
   [luaview.rsa setPublicKeyFilePath:filePath];
   ```

   或

   ```objectivec
   LuaViewCore *luaviewCore = [[LuaViewCore alloc] init];
   NSString filePath = @"CertificatePath"; //公钥证书路径
   [luaviewCore.rsa setPublicKeyFilePath:filePath];
   ```

   然后使用加密接口运行脚本

   `[luaview runSignFile:luaviewScript];`

## SDK接口

### *LuaView对象方法*

| API               | 参数                       | 返回值      | 备注                       |
| ----------------- | ------------------------ | -------- | ------------------------ |
| bundle            | -                        | Bundle对象 | LuaView的bundle属性用于脚本目录管理 |
| loadFile          | url: NSString            | 错误信息     | 加载指定脚本                   |
| loadSignFile      | url: NSString            | 错误信息     | 加载指定加密脚本，需要预先加载加密证书      |
| runFile           | url: NSString            | 错误信息     | 运行指定脚本                   |
| runSignFile       | url: NSString            | 错误信息     | 运行指定加密脚本，需要预先加载加密证书      |
| runData           | data: 数据块, fileName:调试信息 | 错误信息     | 加载指定数据块                  |
| viewWillAppear    | -                        | -        | viewWillAppear           |
| viewDidAppear     | -                        | -        | viewDidAppear            |
| viewWillDisAppear | -                        | -        | viewWillDisAppear        |
| viewDidDisAppear  | -                        | -        | viewDidDisAppear         |
| motionBegan       | -                        | -        | motionBegan              |
| motionEnded       | -                        | -        | motionEnded              |

### *LuaViewCore对象方法*

| API          | 参数                       | 返回值        | 备注                              |
| ------------ | ------------------------ | ---------- | ------------------------------- |
| bundle       | -                        | LVBundle对象 | LuaView的bundle属性，用于脚本和资源的搜索路径管理 |
| ras          |                          | LVRSA对象    | LVRSA对象，用于脚本加密证书管理              |
| loadFile     | url: NSString            | 错误信息       | 加载指定脚本                          |
| loadSignFile | url: NSString            | 错误信息       | 加载指定加密脚本，需要预先加载加密证书             |
| runFile      | url: NSString            | 错误信息       | 加载指定脚本                          |
| runSignFile  | url: NSString            | 错误信息       | 加载指定加密脚本，需要预先加载加密证书             |
| runData      | data: 数据块, fileName:调试信息 | 错误信息       | 加载指定数据块                         |

### *LVBundle对象方法*

| API                  | 参数            | 返回值  | 备注       |
| -------------------- | ------------- | ---- | -------- |
| addResourcePath      | path:NSString | -    | 资源搜索路径   |
| removeResourcePath   | path:NSString | -    | 去除资源搜索路径 |
| addScriptPath        | path:NSString | -    | 脚本搜索路径   |
| removeScriptPath     | path:NSString | -    | 去除脚本搜索路径 |
| resourcePathWithName | name:NSString | -    | 资源       |
| resourceWithName     | name:NSString | -    | 获取资源     |
| imageWithName        | name:NSString | -    | 获取图片     |
| scriptPathWithName   | name:NSString | -    | 获取脚本路径   |
| scriptWithName       | name:NSString | -    | 获取脚本     |
| signedScriptWithName | name:NSString | -    | 获取签名脚本   |

### *LVRSA对象方法*

| API                       | 参数                          | 返回值    | 备注                   |
| ------------------------- | --------------------------- | ------ | -------------------- |
| verifyData:withSignedData | data:NSData<br/>sign:NSData | BOOL   | 验证数据的签名是否正确          |
| aesKeyBytes               | -                           | NSData | 获取证书的NSData格式内容      |
| setPublicKeyFilePath:     | filePath:String             | -      | 设置RSA公钥证书路径          |
| lv_SHA256HashBytes        | fileData                    |        | 静态方法，获取文件数据的SHA256签名 |

