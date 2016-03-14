/*******************************************************************************
* Copyright (c) 2009-2011 Luaj.org. All rights reserved.
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
package org.luaj.vm2;

/**
 * Class to encapsulate behavior of the singleton instance {@code nil} 
 * <p>
 * There will be one instance of this class, {@link LuaValue#NIL}, 
 * per Java virtual machine.  
 * However, the {@link Varargs} instance {@link LuaValue#NONE}
 * which is the empty list, 
 * is also considered treated as a nil value by default.  
 * <p>
 * Although it is possible to test for nil using Java == operator, 
 * the recommended approach is to use the method {@link LuaValue#isnil()} 
 * instead.  By using that any ambiguities between 
 * {@link LuaValue#NIL} and {@link LuaValue#NONE} are avoided.
 * @see LuaValue
 * @see LuaValue#NIL
 */
public class LuaNil extends LuaValue {
	
	static final LuaNil _NIL = new LuaNil();
	
	public static LuaValue s_metatable;
	
	LuaNil() {}

	public int type() {
		return LuaValue.TNIL;
	}

	public String toString() {
		return "nil";		
	}
	
	public String typename() {
		return "nil";
	}
	
	public String tojstring() {
		return "nil";
	}

	public LuaValue not()  { 
		return LuaValue.TRUE;  
	}
	
	public boolean toboolean() { 
		return false; 
	}
	
	public boolean isnil() {
		return true;
	}
		
	public LuaValue getmetatable() { 
		return s_metatable; 
	}
	
	public boolean equals(Object o) {
		return o instanceof LuaNil;
	}

	public LuaValue checknotnil() { 
		return argerror("value");
	}
	
	public boolean isvalidkey() {
		return false;
	}

	// optional argument conversions - nil alwas falls badk to default value
	public boolean     optboolean(boolean defval)          { return defval; }
	public LuaClosure  optclosure(LuaClosure defval)       { return defval; }
	public double      optdouble(double defval)               { return defval; }
	public LuaFunction optfunction(LuaFunction defval)     { return defval; }
	public int         optint(int defval)                  { return defval; }
	public LuaInteger  optinteger(LuaInteger defval)       { return defval; }
	public long        optlong(long defval)                { return defval; }
	public LuaNumber   optnumber(LuaNumber defval)         { return defval; }
	public LuaTable    opttable(LuaTable defval)           { return defval; }
	public LuaThread   optthread(LuaThread defval)         { return defval; }
	public String      optjstring(String defval)            { return defval; }
	public LuaString   optstring(LuaString defval)         { return defval; }
	public Object      optuserdata(Object defval)          { return defval; }
	public Object      optuserdata(Class c, Object defval) { return defval; }
	public LuaValue    optvalue(LuaValue defval)           { return defval; }
}
