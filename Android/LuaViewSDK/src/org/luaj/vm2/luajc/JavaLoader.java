package org.luaj.vm2.luajc;

import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;

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
public class JavaLoader extends ClassLoader {

	private Map<String,byte[]> unloaded = new HashMap<String,byte[]>();
	
	public JavaLoader() {
	}

	public LuaFunction load( Prototype p, String classname, String filename, LuaValue env ) {
		JavaGen jg = new JavaGen( p, classname, filename, false );
		return load( jg, env );
	}
	
	public LuaFunction load( JavaGen jg, LuaValue env ) {
		include( jg );
		return load( jg.classname, env );
	}
	
	public LuaFunction load(String classname, LuaValue env) {
		try {
			Class c = loadClass( classname );
			LuaFunction v = (LuaFunction) c.newInstance();
			v.initupvalue1(env);
			return v;
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new IllegalStateException("bad class gen: "+e);
		}
	}

	public void include( JavaGen jg ) {
		unloaded.put( jg.classname, jg.bytecode );
		for ( int i=0, n=jg.inners!=null? jg.inners.length: 0; i<n; i++ )
			include( jg.inners[i] );
	}

	public Class findClass(String classname) throws ClassNotFoundException {
		byte[] bytes = (byte[]) unloaded.get(classname);
		if ( bytes != null )
			return defineClass(classname, bytes, 0, bytes.length);
		return super.findClass(classname);
	}

}
