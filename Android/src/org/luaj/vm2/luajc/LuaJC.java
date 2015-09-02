/*******************************************************************************
* Copyright (c) 2010 Luaj.org. All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
******************************************************************************/
package org.luaj.vm2.luajc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.BaseLib;

/**
 * Implementation of {@link LuaCompiler} which does direct 
 * lua-to-java-bytecode compiling. 
 * <p>
 * By default, when using {@link JsePlatform} or {@JmePlatform}
 * to construct globals, the plain compiler {@link LuaC} is installed and lua code 
 * will only be compiled into lua bytecode and execute as {@link LuaClosure}. 
 * <p>
 * To override the default compiling behavior with {@link LuaJC}
 * lua-to-java bytecode compiler, install it before undumping code, 
 * for example:
 * <pre> {@code
 * LuaValue globals = JsePlatform.standardGlobals();
 * LuaJC.install(globals);
 * LuaValue chunk = globals.load( "print('hello, world'), "main.lua");
 * System.out.println(chunk.isclosure());  // Will be false when LuaJC is working.
 * chunk.call();
 * } </pre>
 * <p>
 * This requires the bcel library to be on the class path to work as expected.  
 * If the library is not found, the default {@link LuaC} lua-to-lua-bytecode 
 * compiler will be used.  
 * 
 * @see LuaCompiler
 * @see LuaC
 * @see BaseLib
 * @see LuaValue
 */
public class LuaJC implements Globals.Loader {
	
	public static final LuaJC instance = new LuaJC();
	
	/** 
	 * Install the compiler as the main Globals.Loader to use in a set of globals. 
	 * Will fall back to the LuaC prototype compiler.
	 */
	public static final void install(Globals G) {
		G.loader = instance; 
	}
	
	protected LuaJC() {}

	public Hashtable compileAll(InputStream script, String chunkname, String filename, Globals globals, boolean genmain) throws IOException {
		final String classname = toStandardJavaClassName( chunkname );
		final Prototype p = globals.loadPrototype(script, classname, "bt");
		return compileProtoAndSubProtos(p, classname, filename, genmain);
	}
	
	public Hashtable compileAll(Reader script, String chunkname, String filename, Globals globals, boolean genmain) throws IOException {
		final String classname = toStandardJavaClassName( chunkname );
		final Prototype p = globals.compilePrototype(script, classname);
		return compileProtoAndSubProtos(p, classname, filename, genmain);
	}
	
	private Hashtable compileProtoAndSubProtos(Prototype p, String classname, String filename, boolean genmain) throws IOException {
		final String luaname = toStandardLuaFileName( filename );
		final Hashtable h = new Hashtable();
		final JavaGen gen = new JavaGen(p, classname, luaname, genmain);
		insert( h, gen );
		return h;
	}
	
	private void insert(Hashtable h, JavaGen gen) {
		h.put(gen.classname, gen.bytecode);
		for ( int i=0, n=gen.inners!=null? gen.inners.length: 0; i<n; i++ )
			insert(h, gen.inners[i]);
	}

	public LuaFunction load(Prototype p, String name, LuaValue globals) throws IOException {
		String luaname = toStandardLuaFileName( name );
		String classname = toStandardJavaClassName( luaname );
		JavaLoader loader = new JavaLoader();
		return loader.load(p, classname, luaname, globals);
	}
	
	private static String toStandardJavaClassName( String luachunkname ) {
		String stub = toStub( luachunkname );
		StringBuffer classname = new StringBuffer();
		for (int i = 0, n = stub.length(); i < n; ++i) {
			final char c = stub.charAt(i);
			classname.append((((i == 0) && Character.isJavaIdentifierStart(c)) || ((i > 0) && Character.isJavaIdentifierPart(c)))? c: '_');
		}
		return classname.toString();
	}
	
	private static String toStandardLuaFileName( String luachunkname ) {
		String stub = toStub( luachunkname );
		String filename = stub.replace('.','/')+".lua";
		return filename.startsWith("@")? filename.substring(1): filename;
	}
	
	private static String toStub( String s ) {
		String stub = s.endsWith(".lua")? s.substring(0,s.length()-4): s;
		return stub;
	}
}
