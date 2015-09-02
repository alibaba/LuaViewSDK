/*******************************************************************************
* Copyright (c) 2009 Luaj.org. All rights reserved.
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
package org.luaj.vm2.lib;


import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JavaClass;

/** 
 * Subclass of {@link LibFunction} which implements the features of the luajava package. 
 * @author hanhui.hhh
 */
public class AndroidVarArgFunction extends VarArgFunction {
	
	static final int INIT           = 0;
	static final int NEW	= 1;

	static final String[] NAMES = {
		"new", 
	};
	private String mClassName;
	private String mLibName;
	

	public AndroidVarArgFunction(String libName, String className) {
		mClassName = className;
		mLibName = libName;
	}

	public Varargs invoke(Varargs args) {
		try {
			switch (opcode) {
			case INIT: {
				LuaValue env = args.arg(2);
				LuaTable t = new LuaTable();
				bind( t, this.getClass(), NAMES, NEW );
				env.set(mLibName, t);
				env.get("package").get("loaded").set(mLibName, t);
				return t;
			}
			case NEW: {
				// get constructor
				final Class clazz = classForName(mClassName);
				return JavaClass.forClass(clazz).getConstructor().invoke(args);
			}
				
			default:
				throw new LuaError("not yet supported: "+this);
			}
		} catch (LuaError e) {
			throw e;
		} catch (Exception e) {
			throw new LuaError(e);
		}
	}

	// load classes using app loader to allow luaj to be used as an extension
	protected Class classForName(String name) throws ClassNotFoundException {
		return Class.forName(name, true, ClassLoader.getSystemClassLoader());
	}
	
}