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

import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

/**
 * Helper class to coerce values from Java to lua within the luajava library. 
 * <p>
 * This class is primarily used by the {@link LuajavaLib}, 
 * but can also be used directly when working with Java/lua bindings. 
 * <p>
 * To coerce scalar types, the various, generally the {@code valueOf(type)} methods 
 * on {@link LuaValue} may be used:
 * <ul>
 * <li>{@link LuaValue#valueOf(boolean)}</li>
 * <li>{@link LuaValue#valueOf(byte[])}</li>
 * <li>{@link LuaValue#valueOf(double)}</li>
 * <li>{@link LuaValue#valueOf(int)}</li>
 * <li>{@link LuaValue#valueOf(String)}</li>
 * </ul>
 * <p>
 * To coerce arrays of objects and lists, the {@code listOf(..)} and {@code tableOf(...)} methods 
 * on {@link LuaValue} may be used:
 * <ul>
 * <li>{@link LuaValue#listOf(LuaValue[])}</li>
 * <li>{@link LuaValue#listOf(LuaValue[], org.luaj.vm2.Varargs)}</li>
 * <li>{@link LuaValue#tableOf(LuaValue[])}</li>
 * <li>{@link LuaValue#tableOf(LuaValue[], LuaValue[], org.luaj.vm2.Varargs)}</li>
 * </ul>
 * The method {@link CoerceJavaToLua#coerce(Object)} looks as the type and dimesioning 
 * of the argument and tries to guess the best fit for corrsponding lua scalar, 
 * table, or table of tables. 
 * 
 * @see CoerceJavaToLua#coerce(Object)
 * @see LuajavaLib
 */
public class CoerceJavaToLua {
	
	static interface Coercion { 
		public LuaValue coerce( Object javaValue );
	};
	
	static final Map COERCIONS = new HashMap();
	
	static {
		Coercion boolCoercion = new Coercion() {
			public LuaValue coerce( Object javaValue ) {
				Boolean b = (Boolean) javaValue;
				return b.booleanValue()? LuaValue.TRUE: LuaValue.FALSE;
			} 
		} ;
		Coercion intCoercion = new Coercion() {
			public LuaValue coerce( Object javaValue ) {
				Number n = (Number) javaValue;
				return LuaInteger.valueOf( n.intValue() );
			} 
		} ;
		Coercion charCoercion = new Coercion() {
			public LuaValue coerce( Object javaValue ) {
				Character c = (Character) javaValue;
				return LuaInteger.valueOf( c.charValue() );
			} 
		} ;
		Coercion doubleCoercion = new Coercion() {
			public LuaValue coerce( Object javaValue ) {
				Number n = (Number) javaValue;
				return LuaDouble.valueOf( n.doubleValue() );
			} 
		} ;
		Coercion stringCoercion = new Coercion() {
			public LuaValue coerce( Object javaValue ) {
				return LuaString.valueOf( javaValue.toString() );
			} 
		} ;
		Coercion bytesCoercion = new Coercion() {
			public LuaValue coerce( Object javaValue ) {
				return LuaValue.valueOf((byte[]) javaValue);
			} 
		} ;
		Coercion classCoercion = new Coercion() {
			public LuaValue coerce( Object javaValue ) {
				return JavaClass.forClass((Class) javaValue);
			} 
		} ;
		COERCIONS.put( Boolean.class, boolCoercion );
		COERCIONS.put( Byte.class, intCoercion );
		COERCIONS.put( Character.class, charCoercion );
		COERCIONS.put( Short.class, intCoercion );
		COERCIONS.put( Integer.class, intCoercion );
		COERCIONS.put( Long.class, doubleCoercion );
		COERCIONS.put( Float.class, doubleCoercion );
		COERCIONS.put( Double.class, doubleCoercion );
		COERCIONS.put( String.class, stringCoercion );
		COERCIONS.put( byte[].class, bytesCoercion );
		COERCIONS.put( Class.class, classCoercion );
	}

	/**
	 * Coerse a Java object to a corresponding lua value. 
	 * <p>
	 * Integral types {@code boolean}, {@code byte},  {@code char}, and {@code int} 
	 * will become {@link LuaInteger};
	 * {@code long}, {@code float}, and {@code double} will become {@link LuaDouble};
	 * {@code String} and {@code byte[]} will become {@link LuaString}; 
	 * types inheriting from {@link LuaValue} will be returned without coercion;
	 * other types will become {@link LuaUserdata}.
	 * @param o Java object needing conversion
	 * @return {@link LuaValue} corresponding to the supplied Java value. 
	 * @see LuaValue
	 * @see LuaInteger
	 * @see LuaDouble
	 * @see LuaString
	 * @see LuaUserdata
	 */
	public static LuaValue coerce(Object o) {
		if ( o == null )
			return LuaValue.NIL;
		Class clazz = o.getClass();
		Coercion c = (Coercion) COERCIONS.get( clazz );
		if ( c == null ) {
			c = clazz.isArray()? arrayCoercion:
				o instanceof LuaValue ? luaCoercion:
					instanceCoercion;
			COERCIONS.put( clazz, c );
		}
		return c.coerce(o);
	}

	static final Coercion instanceCoercion = new Coercion() {
		public LuaValue coerce(Object javaValue) {
			return new JavaInstance(javaValue);
		}
	};
	
	// should be userdata? 
	static final Coercion arrayCoercion = new Coercion() {
		public LuaValue coerce(Object javaValue) {
			return new JavaArray(javaValue);
		}
	};	

	static final Coercion luaCoercion = new Coercion() {
		public LuaValue coerce( Object javaValue ) {
			return (LuaValue) javaValue;
		} 
	} ;
}
