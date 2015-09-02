/*******************************************************************************
* Copyright (c) 2010-2011 Luaj.org. All rights reserved.
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

import java.io.InputStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/** 
 * Subclass of {@link LibFunction} which implements the lua standard package and module 
 * library functions. 
 * 
 * <h3>Lua Environment Variables</h3>
 * The following variables are available to lua scrips when this library has been loaded:
 * <ul>
 * <li><code>"package.loaded"</code> Lua table of loaded modules.
 * <li><code>"package.path"</code> Search path for lua scripts.
 * <li><code>"package.preload"</code> Lua table of uninitialized preload functions.
 * <li><code>"package.searchers"</code> Lua table of functions that search for object to load.
 * </ul>
 * 
 * <h3>Java Environment Variables</h3>
 * These Java environment variables affect the library behavior:
 * <ul>
 * <li><code>"luaj.package.path"</code> Initial value for <code>"package.path"</code>.  Default value is <code>"?.lua"</code>
 * </ul>
 * 
 * <h3>Loading</h3>
 * Typically, this library is included as part of a call to either 
 * {@link JsePlatform#standardGlobals()} or {@link JmePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("require").call"foo") );
 * } </pre>
 * <p>
 * To instantiate and use it directly, 
 * link it into your globals table via {@link LuaValue#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * System.out.println( globals.get("require").call("foo") );
 * } </pre>
 * <h3>Limitations</h3>
 * This library has been implemented to match as closely as possible the behavior in the corresponding library in C.
 * However, the default filesystem search semantics are different and delegated to the bas library 
 * as outlined in the {@link BaseLib} and {@link JseBaseLib} documentation. 
 * <p>
 * @see LibFunction
 * @see BaseLib
 * @see JseBaseLib
 * @see JsePlatform
 * @see JmePlatform
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.3">Lua 5.2 Package Lib Reference</a>
 */
public class PackageLib extends TwoArgFunction {

	/** The default value to use for package.path.  This can be set with the system property
	 * <code>"luaj.package.path"</code>, and is <code>"?.lua"</code> by default. */
	public static String DEFAULT_LUA_PATH;
	static {
		try {
			DEFAULT_LUA_PATH = System.getProperty("luaj.package.path");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		if (DEFAULT_LUA_PATH == null)
			DEFAULT_LUA_PATH = "?.lua";
	}

	private static final LuaString _LOADED      = valueOf("loaded");
	private static final LuaString _LOADLIB     = valueOf("loadlib");
	private static final LuaString _PRELOAD     = valueOf("preload");
	private static final LuaString _PATH        = valueOf("path");
	private static final LuaString _SEARCHPATH  = valueOf("searchpath");
	private static final LuaString _SEARCHERS   = valueOf("searchers");
	
	/** The globals that were used to load this library. */
	Globals globals;

	/** The table for this package. */
	LuaTable package_;
	
	/** Loader that loads from {@link preload} table if found there */
	public preload_searcher preload_searcher;
	
	/** Loader that loads as a lua script using the lua path currently in {@link path} */
	public lua_searcher lua_searcher;
	
	/** Loader that loads as a Java class.  Class must have public constructor and be a LuaValue. */
	public java_searcher java_searcher;

	private static final LuaString _SENTINEL   = valueOf("\u0001");
	
	private static final String FILE_SEP = System.getProperty("file.separator");

	public PackageLib() {}

	public LuaValue call(LuaValue modname, LuaValue env) {
		globals = env.checkglobals();
		globals.set("require", new require());
		package_ = new LuaTable();
		package_.set(_LOADED, new LuaTable());
		package_.set(_PRELOAD, new LuaTable());
		package_.set(_PATH, LuaValue.valueOf(DEFAULT_LUA_PATH));
		package_.set(_LOADLIB, new loadlib());
		package_.set(_SEARCHPATH, new searchpath());
		LuaTable searchers = new LuaTable();
		searchers.set(1, preload_searcher = new preload_searcher());
		searchers.set(2, lua_searcher     = new lua_searcher());
		searchers.set(3, java_searcher    = new java_searcher());
		package_.set(_SEARCHERS, searchers);
		package_.get(_LOADED).set("package", package_);
		env.set("package", package_);
		globals.package_ = this;
		return env;
	}
	
	/** Allow packages to mark themselves as loaded */
	public void setIsLoaded(String name, LuaTable value) {
		package_.get(_LOADED).set(name, value);
	}


	/** Set the lua path used by this library instance to a new value.  
	 * Merely sets the value of {@link path} to be used in subsequent searches. */
	public void setLuaPath( String newLuaPath ) {
		package_.set(_PATH, LuaValue.valueOf(newLuaPath));
	}
	
	public String tojstring() {
		return "package";
	}
	
	// ======================== Package loading =============================

	/** 
	 * require (modname)
	 * 
	 * Loads the given module. The function starts by looking into the package.loaded table 
	 * to determine whether modname is already loaded. If it is, then require returns the value 
	 * stored at package.loaded[modname]. Otherwise, it tries to find a loader for the module.
	 * 
	 * To find a loader, require is guided by the package.searchers sequence. 
	 * By changing this sequence, we can change how require looks for a module. 
	 * The following explanation is based on the default configuration for package.searchers.
	 * 
	 * First require queries package.preload[modname]. If it has a value, this value 
	 * (which should be a function) is the loader. Otherwise require searches for a Lua loader using 
	 * the path stored in package.path. If that also fails, it searches for a Java loader using 
	 * the classpath, using the public default constructor, and casting the instance to LuaFunction.
	 * 
	 * Once a loader is found, require calls the loader with two arguments: modname and an extra value 
	 * dependent on how it got the loader. If the loader came from a file, this extra value is the file name.
	 * If the loader is a Java instance of LuaFunction, this extra value is the environment.  
	 * If the loader returns any non-nil value, require assigns the returned value to package.loaded[modname]. 
	 * If the loader does not return a non-nil value and has not assigned any value to package.loaded[modname], 
	 * then require assigns true to this entry.
	 * In any case, require returns the final value of package.loaded[modname].
	 *  
	 * If there is any error loading or running the module, or if it cannot find any loader for the module,
	 * then require raises an error.
	 */	
	public class require extends OneArgFunction {
		public LuaValue call( LuaValue arg ) {
			LuaString name = arg.checkstring();
			LuaValue loaded = package_.get(_LOADED);
			LuaValue result = loaded.get(name);
			if ( result.toboolean() ) {
				if ( result == _SENTINEL )
					error("loop or previous error loading module '"+name+"'");
				return result;
			}
	
			/* else must load it; iterate over available loaders */
			LuaTable tbl = package_.get(_SEARCHERS).checktable();
			StringBuffer sb = new StringBuffer();
			Varargs loader = null;
			for ( int i=1; true; i++ ) {
				LuaValue searcher = tbl.get(i);
				if ( searcher.isnil() ) {
					error( "module '"+name+"' not found: "+name+sb );				
			    }
							
			    /* call loader with module name as argument */
				loader = searcher.invoke(name);
				if ( loader.isfunction(1) )
					break;
				if ( loader.isstring(1) )
					sb.append( loader.tojstring(1) );
			}
	
			// load the module using the loader
			loaded.set(name, _SENTINEL);
			result = loader.arg1().call(name, loader.arg(2));
			if ( ! result.isnil() )
				loaded.set( name, result );
			else if ( (result = loaded.get(name)) == _SENTINEL ) 
				loaded.set( name, result = LuaValue.TRUE );
			return result;
		}
	}

	public static class loadlib extends VarArgFunction {
		public Varargs loadlib( Varargs args ) {
			args.checkstring(1);
			return varargsOf(NIL, valueOf("dynamic libraries not enabled"), valueOf("absent"));
		}
	}

	public class preload_searcher extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			LuaString name = args.checkstring(1);
			LuaValue val = package_.get(_PRELOAD).get(name);
			return val.isnil()? 
				valueOf("\n\tno field package.preload['"+name+"']"):
				val;
		}
	}

	public class lua_searcher extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			LuaString name = args.checkstring(1);
			InputStream is = null;
					
			// get package path
			LuaValue path = package_.get(_PATH);
			if ( ! path.isstring() ) 
				return valueOf("package.path is not a string");
		
			// get the searchpath function.
			Varargs v = package_.get(_SEARCHPATH).invoke(varargsOf(name, path));
			
			// Did we get a result?
			if (!v.isstring(1))
				return v.arg(2).tostring();
			LuaString filename = v.arg1().strvalue();
		
			// Try to load the file.
			v = globals.loadfile(filename.tojstring()); 
			if ( v.arg1().isfunction() )
				return LuaValue.varargsOf(v.arg1(), filename);
			
			// report error
			return varargsOf(NIL, valueOf("'"+filename+"': "+v.arg(2).tojstring()));
		}
	}

	public class searchpath extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			String name = args.checkjstring(1);
			String path = args.checkjstring(2);
			String sep = args.optjstring(3, ".");
			String rep = args.optjstring(4, FILE_SEP);
			
			// check the path elements
			int e = -1;
			int n = path.length();
			StringBuffer sb = null;
			name = name.replace(sep.charAt(0), rep.charAt(0));
			while ( e < n ) {
				
				// find next template
				int b = e+1;
				e = path.indexOf(';',b);
				if ( e < 0 )
					e = path.length();
				String template = path.substring(b,e);
	
				// create filename
				int q = template.indexOf('?');
				String filename = template;
				if ( q >= 0 ) {
					filename = template.substring(0,q) + name + template.substring(q+1);
				}
				
				// try opening the file
				InputStream is = globals.finder.findResource(filename);
				if (is != null) {
					try { is.close(); } catch ( java.io.IOException ioe ) {}
					return valueOf(filename);
				}
				
				// report error
				if ( sb == null )
					sb = new StringBuffer();
				sb.append( "\n\t"+filename );
			}
			return varargsOf(NIL, valueOf(sb.toString()));
		}
	}
	
	public class java_searcher extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			String name = args.checkjstring(1);
			String classname = toClassname( name );
			Class c = null;
			LuaValue v = null;
			try {
				c = Class.forName(classname);
				v = (LuaValue) c.newInstance();
				if (v.isfunction())
					((LuaFunction)v).initupvalue1(globals);
				return varargsOf(v, globals);
			} catch ( ClassNotFoundException  cnfe ) {
				return valueOf("\n\tno class '"+classname+"'" );
			} catch ( Exception e ) {
				return valueOf("\n\tjava load failed on '"+classname+"', "+e );
			}
		}
	}
	
	/** Convert lua filename to valid class name */
	public static final String toClassname( String filename ) {
		int n=filename.length();
		int j=n;
		if ( filename.endsWith(".lua") )
			j -= 4;
		for ( int k=0; k<j; k++ ) {
			char c = filename.charAt(k);
			if ( (!isClassnamePart(c)) || (c=='/') || (c=='\\') ) {
				StringBuffer sb = new StringBuffer(j);
				for ( int i=0; i<j; i++ ) {
					c = filename.charAt(i);
					sb.append( 
							 (isClassnamePart(c))? c:
							 ((c=='/') || (c=='\\'))? '.': '_' ); 
				}
				return sb.toString();
			}
		}
		return n==j? filename: filename.substring(0,j);
	}
	
	private static final boolean isClassnamePart(char c) {
		if ( (c>='a'&&c<='z') || (c>='A'&&c<='Z') || (c>='0'&&c<='9') )
			return true;
		switch ( c ) {
		case '.':
		case '$':
		case '_':
			return true;
		default:
			return false;
		}
	}	
}
