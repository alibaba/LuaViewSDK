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
 * Prototype representing compiled lua code. 
 * 
 * <p>
 * This is both a straight translation of the corresponding C type, 
 * and the main data structure for execution of compiled lua bytecode. 
 * 
 * <p>
 * Generally, the {@link Protoytpe} is not constructed directly is an intermediate result
 * as lua code is loaded using {@link Globals.load}:
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * globals.load( new StringReader("print 'hello'"), "main.lua" ).call(); 
 * } </pre>
 * 
 * <p>
 * To create a {@link Prototype} directly, a compiler such as {@link LuaC} may be used:
 * <pre> {@code
 * InputStream is = new ByteArrayInputStream("print('hello,world')".getBytes());
 * Prototype p = LuaC.instance.compile(is, "script");
 * }</pre> 
 * 
 * To simplify loading, the {@link Globals#compilePrototype} method may be used: 
 * <pre> {@code
 * Prototype p = globals.compileProtoytpe(is, "script");
 * }</pre>
 * 
 * It may also be loaded from a {@link java.io.Reader} : 
 * <pre> {@code
 * Prototype p = globals.compileProtoytpe(new StringReader(script), "script");
 * }</pre>
 * 
 * To un-dump a binary file known to be a binary lua file that has been dumped to a string,
 * the {@link Globals#Undumper} interface may be used: 
 * <pre> {@code
 * FileInputStream lua_binary_file = new FileInputStream("foo.lc");  // Known to be compiled lua.
 * Prototype p = globals.undumper.undump(lua_binary_file, "foo.lua");
 * }</pre>
 * 
 * To execute the code represented by the {@link Prototype} it must be supplied to 
 * the constructor of a {@link LuaClosure}:
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * LuaClosure f = new LuaClosure(p, globals);
 * f.call();
 * }</pre> 
 * 
 * To simplify the debugging of prototype values, the contents may be printed using {@link Print#print}:
 * <pre> {@code
 * Print.print(p);
 * }</pre>
 * <p>
 *  
 * @see LuaClosure
 * @see Globals
 * @see Globals#Undumper
 * @see Globasl#Compiler
 * @see Print#print
 */

public class Prototype {
	/* constants used by the function */
	public LuaValue[] k; 
	public int[] code;
	/* functions defined inside the function */
	public Prototype[] p;
	/* map from opcodes to source lines */
	public int[] lineinfo;
	/* information about local variables */
	public LocVars[] locvars;
	/* upvalue information */
	public Upvaldesc[] upvalues;
	public LuaString  source;
	public int linedefined;
	public int lastlinedefined;
	public int numparams;
	public int is_vararg;
	public int maxstacksize;


	public Prototype() {}
	
	public Prototype(int n_upvalues) {
		upvalues = new Upvaldesc[n_upvalues];
	}
	
	public String toString() {
		return source + ":" + linedefined+"-"+lastlinedefined;
	}
	
	/** Get the name of a local variable.
	 * 
	 * @param number the local variable number to look up
	 * @param pc the program counter
	 * @return the name, or null if not found
	 */
	public LuaString getlocalname(int number, int pc) {
	  int i;
	  for (i = 0; i<locvars.length && locvars[i].startpc <= pc; i++) {
	    if (pc < locvars[i].endpc) {  /* is variable active? */
	    	number--;
	      if (number == 0)
	        return locvars[i].varname;
	    }
	  }
	  return null;  /* not found */
	}
	
	public String shortsource() {
		String name = source.tojstring();
        if ( name.startsWith("@") || name.startsWith("=") )
			name = name.substring(1);
		else if ( name.startsWith("\033") )
			name = "binary string";
        return name;
	}
}
