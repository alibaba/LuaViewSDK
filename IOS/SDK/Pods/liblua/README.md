## liblua
* 版本: 5.1
* 来源: wax里的lua, 同时做了增加线程安全的实现
* 源码依赖: 使用 lua.podspec
* lib库依赖: 使用lua.podspec.json

## 编译&打包
* 打开lua.xcodeproj工程， 编译`lua-universal`工程，得到产物
* 将`Build/Products/Release-universal`下的liblua.a放到工程根目录的lib目录里(如果头文件有修改，那么将include的文件也覆盖一下)
* 打开[摩天轮工程](http://mtl3.alibaba-inc.com/project/project_cr_list.htm?projectId=40145)选中`liblua打包`进行打包发布（此发布不会在编译项目，而是使用工程根目录的liblua）