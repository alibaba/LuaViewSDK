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
package org.luaj.vm2.lib.jse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.ResourceFinder;

/** 
 * Subclass of {@link BaseLib} and {@link LibFunction} which implements the lua basic library functions
 * and provides a directory based {@link ResourceFinder} as the {@link #finder}. 
 * <p>
 * Since JME has no file system by default, {@link BaseLib} implements 
 * {@link ResourceFinder} using {@link Class#getResource(String)}. 
 * The {@link JseBaseLib} implements {@link finder} by scanning the current directory
 * first, then falling back to   {@link Class#getResource(String)} if that fails.
 * Otherwise, the behavior is the same as that of {@link BaseLib}.  
 * <p>  
 * Typically, this library is included as part of a call to 
 * {@link JsePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * globals.get("print").call(LuaValue.valueOf("hello, world"));
 * } </pre>
 * <p>
 * For special cases where the smallest possible footprint is desired, 
 * a minimal set of libraries could be loaded
 * directly via {@link Globals#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.get("print").call(LuaValue.valueOf("hello, world"));
 * } </pre>
 * <p>However, other libraries such as <em>PackageLib</em> are not loaded in this case.
 * <p>
 * This is a direct port of the corresponding library in C.
 * @see Globals
 * @see BaseLib
 * @see ResourceFinder
 * @see {@link Globals.finder}
 * @see LibFunction
 * @see JsePlatform
 * @see org.luaj.vm2.lib.jme.JmePlatform
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.1">Lua 5.2 Base Lib Reference</a>
 */

public class JseBaseLib extends org.luaj.vm2.lib.BaseLib {

	/** Extend the library loading to set the default value for {@link Globals.STDIN} */
	public LuaValue call(LuaValue modname, LuaValue env) {
		super.call(modname, env);
		env.checkglobals().STDIN = System.in;
		return env;
	}


	/** 
	 * Try to open a file in the current working directory, 
	 * or fall back to base opener if not found.
	 * 
	 * This implementation attempts to open the file using new File(filename).  
	 * It falls back to the base implementation that looks it up as a resource
	 * in the class path if not found as a plain file. 
	 *  
	 * @see org.luaj.vm2.lib.BaseLib
	 * @see org.luaj.vm2.lib.ResourceFinder
	 * 
	 * @param filename
	 * @return InputStream, or null if not found. 
	 */
	public InputStream findResource(String filename) {
		File f = new File(filename);
		if ( ! f.exists() )
			return super.findResource(filename);
		try {
			return new FileInputStream(f);
		} catch ( IOException ioe ) {
			return null;
		}
	}
}
