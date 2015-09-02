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

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/** 
 * Subclass of {@link LibFunction} which implements the lua standard {@code table} 
 * library. 
 * 
 * <p>
 * Typically, this library is included as part of a call to either 
 * {@link JsePlatform#standardGlobals()} or {@link JmePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("table").get("length").call( LuaValue.tableOf() ) );
 * } </pre>
 * <p>
 * To instantiate and use it directly, 
 * link it into your globals table via {@link LuaValue#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new TableLib());
 * System.out.println( globals.get("table").get("length").call( LuaValue.tableOf() ) );
 * } </pre>
 * <p>
 * This has been implemented to match as closely as possible the behavior in the corresponding library in C.
 * @see LibFunction
 * @see JsePlatform
 * @see JmePlatform
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.5">Lua 5.2 Table Lib Reference</a>
 */
public class TableLib extends TwoArgFunction {

	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaTable table = new LuaTable();
		table.set("concat", new concat());
		table.set("insert", new insert());
		table.set("pack", new pack());
		table.set("remove", new remove());
		table.set("sort", new sort());
		table.set("unpack", new unpack());
		env.set("table", table);
		env.get("package").get("loaded").set("table", table);
		return NIL;
	}

	static class TableLibFunction extends LibFunction {
		public LuaValue call() {
			return argerror(1, "table expected, got no value");
		}
	}
	
	// "concat" (table [, sep [, i [, j]]]) -> string
	static class concat extends TableLibFunction {
		public LuaValue call(LuaValue list) {
			return list.checktable().concat(EMPTYSTRING,1,list.length());
		}
		public LuaValue call(LuaValue list, LuaValue sep) {
			return list.checktable().concat(sep.checkstring(),1,list.length());
		}
		public LuaValue call(LuaValue list, LuaValue sep, LuaValue i) {
			return list.checktable().concat(sep.checkstring(),i.checkint(),list.length());
		}
		public LuaValue call(LuaValue list, LuaValue sep, LuaValue i, LuaValue j) {
			return list.checktable().concat(sep.checkstring(),i.checkint(),j.checkint());
		}
	}

	// "insert" (table, [pos,] value) -> prev-ele
	static class insert extends TableLibFunction {
		public LuaValue call(LuaValue list) {
			return argerror(2, "value expected");
		}
		public LuaValue call(LuaValue table, LuaValue value) {
			table.checktable().insert(table.length()+1,value);
			return NONE;
		}
		public LuaValue call(LuaValue table, LuaValue pos, LuaValue value) {
			table.checktable().insert(pos.checkint(),value);
			return NONE;
		}
	}
	
	// "pack" (...) -> table
	static class pack extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			LuaValue t = tableOf(args, 1);
			t.set("n", args.narg());
			return t;
		}
	}

	// "remove" (table [, pos]) -> removed-ele
	static class remove extends TableLibFunction {
		public LuaValue call(LuaValue list) {
			return list.checktable().remove(0);
		}
		public LuaValue call(LuaValue list, LuaValue pos) {
			return list.checktable().remove(pos.checkint());
		}
	}

	// "sort" (table [, comp])
	static class sort extends TwoArgFunction {
		public LuaValue call(LuaValue table, LuaValue compare) {
			table.checktable().sort(compare.isnil()? NIL: compare.checkfunction());
			return NONE;
		}
	}

	// "unpack", // (list [,i [,j]]) -> result1, ...
	static class unpack extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			LuaTable t = args.checktable(1);
			switch (args.narg()) {
			case 1: return t.unpack();
			case 2: return t.unpack(args.checkint(2));
			default: return t.unpack(args.checkint(2), args.checkint(3));
			}
		}
	}
}
