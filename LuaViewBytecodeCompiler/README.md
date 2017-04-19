#LuaViewBytecodeCompiler

> 该工具用于将LuaView的源码预编译成Lua bytecode。用于加速LuaView的执行。

### 注意
* 该工具编译出来的bytecode代码只能用于Android端，暂时不能用于iOS端


### 使用
1. 创建项目普通Java项目
2. 引入jar包
3. 使用jar包提供的函数对lua代码进行打包(LuaViewBytecodeCompiler类)
	* A：public static byte[] compile(byte[] source, String filename) throws Exception
	* B：public static void compile(String filePath) throws Exception
4. 	A第一个方法将一个源码的二进制流编译成lua bytecode二进制流
5. 	B方法将给定代码地址的代码编译成lua bytecode并保存在同目录下，文件名为*.luap
6. Android项目直接加载二进制代码
	* luaView.loadPrototype(final InputStream inputStream, final String name, final LuaScriptLoader.ScriptExecuteCallback callback)

