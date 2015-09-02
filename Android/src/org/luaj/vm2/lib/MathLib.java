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

import java.util.Random;

import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/** 
 * Subclass of {@link LibFunction} which implements the lua standard {@code math} 
 * library. 
 * <p> 
 * It contains only the math library support that is possible on JME.  
 * For a more complete implementation based on math functions specific to JSE 
 * use {@link org.luaj.vm2.lib.jse.JseMathLib}. 
 * In Particular the following math functions are <b>not</b> implemented by this library:
 * <ul>
 * <li>acos</li>
 * <li>asin</li>
 * <li>atan</li>
 * <li>cosh</li>
 * <li>log</li>
 * <li>sinh</li>
 * <li>tanh</li>
 * <li>atan2</li>
 * </ul>
 * <p>
 * The implementations of {@code exp()} and {@code pow()} are constructed by 
 * hand for JME, so will be slower and less accurate than when executed on the JSE platform.
 * <p> 
 * Typically, this library is included as part of a call to either 
 * {@link JsePlatform#standardGlobals()} or {@link JmePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * When using {@link JsePlaform} as in this example, the subclass {@link JseMathLib} will 
 * be included, which also includes this base functionality.
 * <p>
 * To instantiate and use it directly, 
 * link it into your globals table via {@link LuaValue#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new MathLib());
 * System.out.println( globals.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * Doing so will ensure the library is properly initialized 
 * and loaded into the globals table. 
 * <p>
 * This has been implemented to match as closely as possible the behavior in the corresponding library in C.
 * @see LibFunction
 * @see JsePlatform
 * @see JmePlatform
 * @see JseMathLib
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.6">Lua 5.2 Math Lib Reference</a>
 */
public class MathLib extends TwoArgFunction {
	
	public static MathLib MATHLIB = null;

	public MathLib() {
		MATHLIB = this;
	}

	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaTable math = new LuaTable(0,30);
		math.set("abs", new abs());
		math.set("ceil", new ceil());
		math.set("cos", new cos());
		math.set("deg", new deg());
		math.set("exp", new exp(this));
		math.set("floor", new floor());
		math.set("fmod", new fmod());
		math.set("frexp", new frexp());
		math.set("huge", LuaDouble.POSINF );
		math.set("ldexp", new ldexp());
		math.set("max", new max());
		math.set("min", new min());
		math.set("modf", new modf());
		math.set("pi", Math.PI );
		math.set("pow", new pow());
		random r;
		math.set("random", r = new random());
		math.set("randomseed", new randomseed(r));
		math.set("rad", new rad());
		math.set("sin", new sin());
		math.set("sqrt", new sqrt());
		math.set("tan", new tan());
		env.set("math", math);
		env.get("package").get("loaded").set("math", math);
		return math;
	}
	
	abstract protected static class UnaryOp extends OneArgFunction {
		public LuaValue call(LuaValue arg) {
			return valueOf(call(arg.checkdouble()));
		}
		abstract protected double call(double d);
	}

	abstract protected static class BinaryOp extends TwoArgFunction {
		public LuaValue call(LuaValue x, LuaValue y) {
			return valueOf(call(x.checkdouble(), y.checkdouble()));
		}
		abstract protected double call(double x, double y);
	}

	static final class abs extends UnaryOp { protected double call(double d) { return Math.abs(d); } }
	static final class ceil extends UnaryOp { protected double call(double d) { return Math.ceil(d); } }
	static final class cos extends UnaryOp { protected double call(double d) { return Math.cos(d); } }
	static final class deg extends UnaryOp { protected double call(double d) { return Math.toDegrees(d); } }
	static final class floor extends UnaryOp { protected double call(double d) { return Math.floor(d); } }
	static final class rad extends UnaryOp { protected double call(double d) { return Math.toRadians(d); } }
	static final class sin extends UnaryOp { protected double call(double d) { return Math.sin(d); } }
	static final class sqrt extends UnaryOp { protected double call(double d) { return Math.sqrt(d); } }
	static final class tan extends UnaryOp { protected double call(double d) { return Math.tan(d); } }

	static final class exp extends UnaryOp {
		final MathLib mathlib;
		exp(MathLib mathlib) {
			this.mathlib = mathlib;
		}
		protected double call(double d) { 
			return mathlib.dpow_lib(Math.E,d); 
		} 
	}
	
	static final class fmod extends BinaryOp {
		protected double call(double x, double y) {
			double q = x/y;
			return x - y * (q>=0? Math.floor(q): Math.ceil(q));
		}
	}
	static final class ldexp extends BinaryOp {
		protected double call(double x, double y) {
			// This is the behavior on os-x, windows differs in rounding behavior.
			return x * Double.longBitsToDouble((((long) y) + 1023) << 52);
		}
	}
	static final class pow extends BinaryOp {
		protected double call(double x, double y) {
			return MathLib.dpow_default(x, y);
		}
	}

	static class frexp extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			double x = args.checkdouble(1);
			if ( x == 0 ) return varargsOf(ZERO,ZERO);
			long bits = Double.doubleToLongBits( x );
			double m = ((bits & (~(-1L<<52))) + (1L<<52)) * ((bits >= 0)? (.5 / (1L<<52)): (-.5 / (1L<<52)));
			double e = (((int) (bits >> 52)) & 0x7ff) - 1022;
			return varargsOf( valueOf(m), valueOf(e) );
		}
	}

	static class max extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			double m = args.checkdouble(1);
			for ( int i=2,n=args.narg(); i<=n; ++i )
				m = Math.max(m,args.checkdouble(i));
			return valueOf(m);
		}
	}
	
	static class min extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			double m = args.checkdouble(1);
			for ( int i=2,n=args.narg(); i<=n; ++i )
				m = Math.min(m,args.checkdouble(i));
			return valueOf(m);
		}
	}
	
	static class modf extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			double x = args.checkdouble(1);
			double intPart = ( x > 0 ) ? Math.floor( x ) : Math.ceil( x );
			double fracPart = x - intPart;
			return varargsOf( valueOf(intPart), valueOf(fracPart) );
		}
	}
	
	static class random extends LibFunction {
		Random random = new Random();
		public LuaValue call() {
			return valueOf( random.nextDouble() );
		}
		public LuaValue call(LuaValue a) {
			int m = a.checkint();
			if (m<1) argerror(1, "interval is empty");
			return valueOf( 1 + random.nextInt(m) );
		}
		public LuaValue call(LuaValue a, LuaValue b) {
			int m = a.checkint();
			int n = b.checkint();
			if (n<m) argerror(2, "interval is empty");
			return valueOf( m + random.nextInt(n+1-m) );
		}
		
	}

	static class randomseed extends OneArgFunction {
		final random random;
		randomseed(random random) {
			this.random = random;
		}
		public LuaValue call(LuaValue arg) {
			long seed = arg.checklong();
			random.random = new Random(seed);
			return NONE;
		}
	}
	
	/** compute power using installed math library, or default if there is no math library installed */
	public static LuaValue dpow(double a, double b) {
		return LuaDouble.valueOf( 
				MATHLIB!=null?
				MATHLIB.dpow_lib(a,b):
				dpow_default(a,b) );
	}
	public static double dpow_d(double a, double b) {
		return MATHLIB!=null? 
				MATHLIB.dpow_lib(a,b): 
				dpow_default(a,b);
	}
	
	/** 
	 * Hook to override default dpow behavior with faster implementation.  
	 */
	public double dpow_lib(double a, double b) {
		return dpow_default(a,b);
	}

	/** 
	 * Default JME version computes using longhand heuristics. 
	 */
	protected static double dpow_default(double a, double b) {
		if ( b < 0 )
			return 1 / dpow_default( a, -b );
		double p = 1;
		int whole = (int) b;
		for ( double v=a; whole > 0; whole>>=1, v*=v )
			if ( (whole & 1) != 0 )
				p *= v;
		if ( (b -= whole) > 0 ) {
			int frac = (int) (0x10000 * b);
			for ( ; (frac&0xffff)!=0; frac<<=1 ) {
				a = Math.sqrt(a);
				if ( (frac & 0x8000) != 0 )
					p *= a;
			}
		}
		return p;
	}

}
