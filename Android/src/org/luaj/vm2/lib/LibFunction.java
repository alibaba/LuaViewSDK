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
package org.luaj.vm2.lib;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Subclass of {@link LuaFunction} common to Java functions exposed to lua. 
 * <p>
 * To provide for common implementations in JME and JSE, 
 * library functions are typically grouped on one or more library classes
 * and an opcode per library function is defined and used to key the switch
 * to the correct function within the library.  
 * <p>
 * Since lua functions can be called with too few or too many arguments, 
 * and there are overloaded {@link LuaValue#call()} functions with varying 
 * number of arguments, a Java function exposed in lua needs to handle  the
 * argument fixup when a function is called with a number of arguments 
 * differs from that expected.  
 * <p>
 * To simplify the creation of library functions, 
 * there are 5 direct subclasses to handle common cases based on number of 
 * argument values and number of return return values.
 * <ul>
 * <li>{@link ZeroArgFunction}</li>
 * <li>{@link OneArgFunction}</li>
 * <li>{@link TwoArgFunction}</li>
 * <li>{@link ThreeArgFunction}</li>
 * <li>{@link VarArgFunction}</li>
 * </ul>
 * <p>
 * To be a Java library that can be loaded via {@code require}, it should have 
 * a public constructor that returns a {@link LuaValue} that, when executed, 
 * initializes the library. 
 * <p> 
 * For example, the following code will implement a library called "hyperbolic" 
 * with two functions, "sinh", and "cosh":
 <pre> {@code 
 * import org.luaj.vm2.LuaValue;
 * import org.luaj.vm2.lib.*;
 * 
 * public class hyperbolic extends TwoArgFunction {
 *
 *	public hyperbolic() {}
 *
 *	public LuaValue call(LuaValue modname, LuaValue env) {
 *		LuaValue library = tableOf();
 *		library.set( "sinh", new sinh() );
 *		library.set( "cosh", new cosh() );
 *		env.set( "hyperbolic", library );
 *		return library;
 *	}
 *
 *	static class sinh extends OneArgFunction {
 *		public LuaValue call(LuaValue x) {
 *			return LuaValue.valueOf(Math.sinh(x.checkdouble()));
 *		}
 *	}
 *	
 *	static class cosh extends OneArgFunction {
 *		public LuaValue call(LuaValue x) {
 *			return LuaValue.valueOf(Math.cosh(x.checkdouble()));
 *		}
 *	}
 *}
 *}</pre>
 * The default constructor is used to instantiate the library 
 * in response to {@code require 'hyperbolic'} statement,
 * provided it is on Java&quot;s class path. 
 * This instance is then invoked with 2 arguments: the name supplied to require(), 
 * and the environment for this function.  The library may ignore these, or use 
 * them to leave side effects in the global environment, for example. 
 * In the previous example, two functions are created, 'sinh', and 'cosh', and placed 
 * into a global table called 'hyperbolic' using the supplied 'env' argument.
 * <p>
 * To test it, a script such as this can be used:
 * <pre> {@code
 * local t = require('hyperbolic')
 * print( 't', t )
 * print( 'hyperbolic', hyperbolic )
 * for k,v in pairs(t) do
 * 	print( 'k,v', k,v )
 * end
 * print( 'sinh(.5)', hyperbolic.sinh(.5) )
 * print( 'cosh(.5)', hyperbolic.cosh(.5) )
 * }</pre> 
 * <p>
 * It should produce something like:
 * <pre> {@code
 * t	table: 3dbbd23f
 * hyperbolic	table: 3dbbd23f
 * k,v	cosh	function: 3dbbd128
 * k,v	sinh	function: 3dbbd242
 * sinh(.5)	0.5210953
 * cosh(.5)	1.127626
 * }</pre> 
 * <p> 
 * See the source code in any of the library functions 
 * such as {@link BaseLib} or {@link TableLib} for other examples.  
 */
abstract public class LibFunction extends LuaFunction {
	
	/** User-defined opcode to differentiate between instances of the library function class. 
	 * <p>
	 * Subclass will typicall switch on this value to provide the specific behavior for each function. 
	 */
	protected int opcode;
	
	/** The common name for this function, useful for debugging.
	 * <p>
	 * Binding functions initialize this to the name to which it is bound.
	 */
	protected String name;
	
	/** Default constructor for use by subclasses */
	protected LibFunction() {		
	}
	
	public String tojstring() {
		return name != null? name: super.tojstring();
	}
	
	/** 
	 * Bind a set of library functions.  
	 * <p>
	 * An array of names is provided, and the first name is bound 
	 * with opcode = 0, second with 1, etc. 
	 * @param env The environment to apply to each bound function 
	 * @param factory the Class to instantiate for each bound function
	 * @param names array of String names, one for each function.
	 * @see #bind(LuaValue, Class, String[], int)  
	 */
	protected void bind(LuaValue env, Class factory,  String[] names ) {
		bind( env, factory, names, 0 );
	}
	
	/** 
	 * Bind a set of library functions, with an offset  
	 * <p>
	 * An array of names is provided, and the first name is bound 
	 * with opcode = {@code firstopcode}, second with {@code firstopcode+1}, etc. 
	 * @param env The environment to apply to each bound function 
	 * @param factory the Class to instantiate for each bound function
	 * @param names array of String names, one for each function.
	 * @param firstopcode the first opcode to use  
	 * @see #bind(LuaValue, Class, String[])  
	 */
	protected void bind(LuaValue env, Class factory,  String[] names, int firstopcode ) {
		try {
			for ( int i=0, n=names.length; i<n; i++ ) {
				LibFunction f = (LibFunction) factory.newInstance();
				f.opcode = firstopcode + i;
				f.name = names[i];
				env.set(f.name, f);
			}
		} catch ( Exception e ) {
			throw new LuaError( "bind failed: "+e );
		}
	}	

	/** Java code generation utility to allocate storage for upvalue, leave it empty */
	protected static LuaValue[] newupe() {
		return new LuaValue[1];
	}

	/** Java code generation utility to allocate storage for upvalue, initialize with nil */
	protected static LuaValue[] newupn() {
		return new LuaValue[] { NIL };
	}
	
	/** Java code generation utility to allocate storage for upvalue, initialize with value */
	protected static LuaValue[] newupl(LuaValue v) {
		return new LuaValue[] { v };
	}

	public LuaValue call() {
		return argerror(1,"value");
	}
	public LuaValue call(LuaValue a) {
		return call();
	}
	public LuaValue call(LuaValue a, LuaValue b) {
		return call(a);
	}
	public LuaValue call(LuaValue a, LuaValue b, LuaValue c) {
		return call(a,b);
	}
	public LuaValue call(LuaValue a, LuaValue b, LuaValue c, LuaValue d) {
		return call(a,b,c);
	}
	public Varargs invoke(Varargs args) {
		switch(args.narg()) {
		case 0: return call();
		case 1: return call(args.arg1());
		case 2: return call(args.arg1(),args.arg(2));
		case 3: return call(args.arg1(),args.arg(2),args.arg(3));
		default: return call(args.arg1(),args.arg(2),args.arg(3),args.arg(4));
		}
	}
} 
