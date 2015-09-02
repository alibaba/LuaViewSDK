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
package org.luaj.vm2.lib.jse;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/** 
 * Subclass of {@link LibFunction} which implements the lua standard {@code math} 
 * library. 
 * <p> 
 * It contains all lua math functions, including those not available on the JME platform.  
 * See {@link org.luaj.lib.MathLib} for the exception list.  
 * <p>
 * Typically, this library is included as part of a call to 
 * {@link JsePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * <p>
 * For special cases where the smallest possible footprint is desired, 
 * a minimal set of libraries could be loaded
 * directly via {@link Globals#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new JseMathLib());
 * System.out.println( globals.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * <p>However, other libraries such as <em>CoroutineLib</em> are not loaded in this case.
 * <p>
 * This has been implemented to match as closely as possible the behavior in the corresponding library in C.
 * @see LibFunction
 * @see JsePlatform
 * @see org.luaj.vm2.lib.jme.JmePlatform
 * @see JseMathLib
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.6">Lua 5.2 Math Lib Reference</a>
 */
public class JseMathLib extends org.luaj.vm2.lib.MathLib {
	
	public JseMathLib() {}

	public LuaValue call(LuaValue modname, LuaValue env) {
		super.call(modname, env);
		LuaValue math = env.get("math");
		math.set("acos", new acos());
		math.set("asin", new asin());
		math.set("atan", new atan());
		math.set("atan2", new atan2());
		math.set("cosh", new cosh());
		math.set("exp", new exp());
		math.set("log", new log());
		math.set("pow", new pow());
		math.set("sinh", new sinh());
		math.set("tanh", new tanh());
		return math;
	}

	static final class acos extends UnaryOp { protected double call(double d) { return Math.acos(d); } }
	static final class asin extends UnaryOp { protected double call(double d) { return Math.asin(d); } }
	static final class atan extends UnaryOp { protected double call(double d) { return Math.atan(d); } }
	static final class atan2 extends BinaryOp { protected double call(double y, double x) { return Math.atan2(y, x); } }
	static final class cosh extends UnaryOp { protected double call(double d) { return Math.cosh(d); } }
	static final class exp extends UnaryOp { protected double call(double d) { return Math.exp(d); } }
	static final class log extends UnaryOp { protected double call(double d) { return Math.log(d); } }
	static final class pow extends BinaryOp { protected double call(double x, double y) { return Math.pow(x, y); } }
	static final class sinh extends UnaryOp { protected double call(double d) { return Math.sinh(d); } }
	static final class tanh extends UnaryOp { protected double call(double d) { return Math.tanh(d); } }

	/** Faster, better version of pow() used by arithmetic operator ^ */
	public double dpow_lib(double a, double b) {
		return Math.pow(a, b);
	}
	
	
}
