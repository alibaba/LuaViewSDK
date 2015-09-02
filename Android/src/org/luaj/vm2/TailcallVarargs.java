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
package org.luaj.vm2;

import org.luaj.vm2.Varargs.ArrayPartVarargs;
import org.luaj.vm2.Varargs.PairVarargs;

/**
 * Subclass of {@link Varargs} that represents a lua tail call 
 * in a Java library function execution environment. 
 * <p>
 * Since Java doesn't have direct support for tail calls, 
 * any lua function whose {@link Prototype} contains the 
 * {@link Lua#OP_TAILCALL} bytecode needs a mechanism 
 * for tail calls when converting lua-bytecode to java-bytecode.
 * <p>
 * The tail call holds the next function and arguments, 
 * and the client a call to {@link #eval()} executes the function
 * repeatedly until the tail calls are completed. 
 * <p>
 * Normally, users of luaj need not concern themselves with the 
 * details of this mechanism, as it is built into the core 
 * execution framework. 
 * @see Prototype 
 * @see LuaJC
 */
public class TailcallVarargs extends Varargs {

	private LuaValue func;
	private Varargs args;
	private Varargs result;
	
	public TailcallVarargs(LuaValue f, Varargs args) {
		this.func = f;
		this.args = args;
	}
	
	public TailcallVarargs(LuaValue object, LuaValue methodname, Varargs args) {
		this.func = object.get(methodname);
		this.args = LuaValue.varargsOf(object, args);
	}
	
	public boolean isTailcall() {
		return true;
	}
	
	public Varargs eval() {
		while ( result == null ) {
			Varargs r = func.onInvoke(args);
			if (r.isTailcall()) {
				TailcallVarargs t = (TailcallVarargs) r;
				func = t.func;
				args = t.args;
			}
			else {
				result = r;			
				func = null;
				args = null;
			}
		}
		return result;
	}
	
	public LuaValue arg( int i ) {
		if ( result == null )
			eval();
		return result.arg(i);
	}
	
	public LuaValue arg1() {
		if (result == null)
			eval();
		return result.arg1();
	}
	
	public int narg() {
		if (result == null)
			eval();
		return result.narg();
	}

	public Varargs subargs(int start) {
		if (result == null)
			eval();
		return result.subargs(start);
	}
}