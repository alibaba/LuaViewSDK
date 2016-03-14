/*******************************************************************************
* Copyright (c) 2007-2011 LuaJ. All rights reserved.
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

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/** 
 * Subclass of {@link LibFunction} which implements the lua standard {@code coroutine} 
 * library. 
 * <p> 
 * The coroutine library in luaj has the same behavior as the
 * coroutine library in C, but is implemented using Java Threads to maintain 
 * the call state between invocations.  Therefore it can be yielded from anywhere, 
 * similar to the "Coco" yield-from-anywhere patch available for C-based lua.
 * However, coroutines that are yielded but never resumed to complete their execution
 * may not be collected by the garbage collector. 
 * <p> 
 * Typically, this library is included as part of a call to either 
 * {@link JsePlatform#standardGlobals()} or {@link JmePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("coroutine").get("running").call() );
 * } </pre>
 * <p>
 * To instantiate and use it directly, 
 * link it into your globals table via {@link LuaValue#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new CoroutineLib());
 * System.out.println( globals.get("coroutine").get("running").call() );
 * } </pre>
 * <p>
 * @see LibFunction
 * @see JsePlatform
 * @see JmePlatform
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.2">Lua 5.2 Coroutine Lib Reference</a>
 */
public class CoroutineLib extends TwoArgFunction {

	static long thread_orphan_check_interval = 30000;

	static int coroutine_count = 0;

	Globals globals;
	
	public LuaValue call(LuaValue modname, LuaValue env) {
		globals = env.checkglobals();
		LuaTable coroutine = new LuaTable();
		coroutine.set("create", new create());
		coroutine.set("resume", new resume());
		coroutine.set("running", new running());
		coroutine.set("status", new status());
		coroutine.set("yield", new yield());
		coroutine.set("wrap", new wrap());
		env.set("coroutine", coroutine);
		env.get("package").get("loaded").set("coroutine", coroutine);
		return coroutine;
	}

	final class create extends LibFunction {
		public LuaValue call(LuaValue f) {
			return new LuaThread(globals, f.checkfunction());
		}
	}

	final class resume extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			final LuaThread t = args.checkthread(1);
			return t.resume( args.subargs(2) );
		}
	}

	final class running extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			final LuaThread r = globals.running;
			return varargsOf(r, valueOf(r.isMainThread()));
		}
	}

	static final class status extends LibFunction {
		public LuaValue call(LuaValue t) {
			LuaThread lt = t.checkthread();
			return valueOf( lt.getStatus() );
		}
	}
	
	final class yield extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			return globals.yield( args );
		}
	}

	final class wrap extends LibFunction {
		public LuaValue call(LuaValue f) {
			final LuaValue func = f.checkfunction();
			final LuaThread thread = new LuaThread(globals, func);
			return new wrapper(thread);
		}
	}

	final class wrapper extends VarArgFunction {
		final LuaThread luathread;
		wrapper(LuaThread luathread) {
			this.luathread = luathread;
		}
		public Varargs invoke(Varargs args) {
			final Varargs result = luathread.resume(args);
			if ( result.arg1().toboolean() ) {
				return result.subargs(2);
			} else {
				return error( result.arg(2).tojstring() );
			}
		}
	}
}
