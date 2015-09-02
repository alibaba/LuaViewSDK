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
package org.luaj.vm2;

import org.luaj.vm2.lib.MathLib;

/**
 * Extension of {@link LuaNumber} which can hold a Java int as its value. 
 * <p>
 * These instance are not instantiated directly by clients, but indirectly 
 * via the static functions {@link LuaValue#valueOf(int)} or {@link LuaValue#valueOf(double)}
 * functions.  This ensures that policies regarding pooling of instances are 
 * encapsulated.  
 * <p>
 * There are no API's specific to LuaInteger that are useful beyond what is already 
 * exposed in {@link LuaValue}.
 * 
 * @see LuaValue
 * @see LuaNumber
 * @see LuaDouble
 * @see LuaValue#valueOf(int)
 * @see LuaValue#valueOf(double)
 */
public class LuaInteger extends LuaNumber {

	private static final LuaInteger[] intValues = new LuaInteger[512];
	static {
		for ( int i=0; i<512; i++ )
			intValues[i] = new LuaInteger(i-256);
	}

	public static LuaInteger valueOf(int i) {
		return i<=255 && i>=-256? intValues[i+256]: new LuaInteger(i);
	};
	
	 // TODO consider moving this to LuaValue
	/** Return a LuaNumber that represents the value provided
	 * @param l long value to represent.
	 * @return LuaNumber that is eithe LuaInteger or LuaDouble representing l
	 * @see LuaValue#valueOf(int)
	 * @see LuaValue#valueOf(double)
	 */
	public static LuaNumber valueOf(long l) {
		int i = (int) l;
		return l==i? (i<=255 && i>=-256? intValues[i+256]: 
			(LuaNumber) new LuaInteger(i)): 
			(LuaNumber) LuaDouble.valueOf(l);
	}
	
	/** The value being held by this instance. */
	public final int v;
	
	/** 
	 * Package protected constructor. 
	 * @see LuaValue#valueOf(int)
	 **/
	LuaInteger(int i) {
		this.v = i;
	}
	
	public boolean isint() {		return true;	}
	public boolean isinttype() {	return true;	}
	public boolean islong() {		return true;	}
	
	public byte    tobyte()        { return (byte) v; }
	public char    tochar()        { return (char) v; }
	public double  todouble()      { return v; }
	public float   tofloat()       { return v; }
	public int     toint()         { return v; }
	public long    tolong()        { return v; }
	public short   toshort()       { return (short) v; }

	public double      optdouble(double defval)            { return v; }
	public int         optint(int defval)                  { return v;  }
	public LuaInteger  optinteger(LuaInteger defval)       { return this; }
	public long        optlong(long defval)                { return v; }

	public String tojstring() {
		return Integer.toString(v);
	}

	public LuaString strvalue() {
		return LuaString.valueOf(Integer.toString(v));
	}
		
	public LuaString optstring(LuaString defval) {
		return LuaString.valueOf(Integer.toString(v)); 
	}
	
	public LuaValue tostring() {
		return LuaString.valueOf(Integer.toString(v)); 
	}
		
	public String optjstring(String defval) { 
		return Integer.toString(v); 
	}
	
	public LuaInteger checkinteger() {
		return this;
	}
	
	public boolean isstring() {
		return true;
	}
	
	public int hashCode() {
		return v;
	}

	public static int hashCode(int x) {
		return x;
	}

	// unary operators
	public LuaValue neg() { return valueOf(-(long)v); }
	
	// object equality, used for key comparison
	public boolean equals(Object o) { return o instanceof LuaInteger? ((LuaInteger)o).v == v: false; }
	
	// equality w/ metatable processing
	public LuaValue eq( LuaValue val )    { return val.raweq(v)? TRUE: FALSE; }
	public boolean eq_b( LuaValue val )   { return val.raweq(v); }
	
	// equality w/o metatable processing
	public boolean raweq( LuaValue val )  { return val.raweq(v); }
	public boolean raweq( double val )    { return v == val; }
	public boolean raweq( int val )       { return v == val; }
	
	// arithmetic operators
	public LuaValue   add( LuaValue rhs )        { return rhs.add(v); }
	public LuaValue   add( double lhs )     { return LuaDouble.valueOf(lhs + v); }
	public LuaValue   add( int lhs )        { return LuaInteger.valueOf(lhs + (long)v); }
	public LuaValue   sub( LuaValue rhs )        { return rhs.subFrom(v); }
	public LuaValue   sub( double rhs )        { return LuaDouble.valueOf(v - rhs); }
	public LuaValue   sub( int rhs )        { return LuaDouble.valueOf(v - rhs); }
	public LuaValue   subFrom( double lhs )   { return LuaDouble.valueOf(lhs - v); }
	public LuaValue   subFrom( int lhs )      { return LuaInteger.valueOf(lhs - (long)v); }
	public LuaValue   mul( LuaValue rhs )        { return rhs.mul(v); }
	public LuaValue   mul( double lhs )   { return LuaDouble.valueOf(lhs * v); }
	public LuaValue   mul( int lhs )      { return LuaInteger.valueOf(lhs * (long)v); }
	public LuaValue   pow( LuaValue rhs )        { return rhs.powWith(v); }
	public LuaValue   pow( double rhs )        { return MathLib.dpow(v,rhs); }
	public LuaValue   pow( int rhs )        { return MathLib.dpow(v,rhs); }
	public LuaValue   powWith( double lhs )   { return MathLib.dpow(lhs,v); }
	public LuaValue   powWith( int lhs )      { return MathLib.dpow(lhs,v); }
	public LuaValue   div( LuaValue rhs )        { return rhs.divInto(v); }
	public LuaValue   div( double rhs )        { return LuaDouble.ddiv(v,rhs); }
	public LuaValue   div( int rhs )        { return LuaDouble.ddiv(v,rhs); }
	public LuaValue   divInto( double lhs )   { return LuaDouble.ddiv(lhs,v); }
	public LuaValue   mod( LuaValue rhs )        { return rhs.modFrom(v); }
	public LuaValue   mod( double rhs )        { return LuaDouble.dmod(v,rhs); }
	public LuaValue   mod( int rhs )        { return LuaDouble.dmod(v,rhs); }
	public LuaValue   modFrom( double lhs )   { return LuaDouble.dmod(lhs,v); }
	
	// relational operators
	public LuaValue   lt( LuaValue rhs )         { return rhs.gt_b(v)? TRUE: FALSE; }	
	public LuaValue   lt( double rhs )      { return v < rhs? TRUE: FALSE; }
	public LuaValue   lt( int rhs )         { return v < rhs? TRUE: FALSE; }
	public boolean lt_b( LuaValue rhs )       { return rhs.gt_b(v); }
	public boolean lt_b( int rhs )         { return v < rhs; }
	public boolean lt_b( double rhs )      { return v < rhs; }
	public LuaValue   lteq( LuaValue rhs )       { return rhs.gteq_b(v)? TRUE: FALSE; }
	public LuaValue   lteq( double rhs )    { return v <= rhs? TRUE: FALSE; }
	public LuaValue   lteq( int rhs )       { return v <= rhs? TRUE: FALSE; }
	public boolean lteq_b( LuaValue rhs )     { return rhs.gteq_b(v); }
	public boolean lteq_b( int rhs )       { return v <= rhs; }
	public boolean lteq_b( double rhs )    { return v <= rhs; }
	public LuaValue   gt( LuaValue rhs )         { return rhs.lt_b(v)? TRUE: FALSE; }
	public LuaValue   gt( double rhs )      { return v > rhs? TRUE: FALSE; }
	public LuaValue   gt( int rhs )         { return v > rhs? TRUE: FALSE; }
	public boolean gt_b( LuaValue rhs )       { return rhs.lt_b(v); }
	public boolean gt_b( int rhs )         { return v > rhs; }
	public boolean gt_b( double rhs )      { return v > rhs; }
	public LuaValue   gteq( LuaValue rhs )       { return rhs.lteq_b(v)? TRUE: FALSE; }
	public LuaValue   gteq( double rhs )    { return v >= rhs? TRUE: FALSE; }
	public LuaValue   gteq( int rhs )       { return v >= rhs? TRUE: FALSE; }
	public boolean gteq_b( LuaValue rhs )     { return rhs.lteq_b(v); }
	public boolean gteq_b( int rhs )       { return v >= rhs; }
	public boolean gteq_b( double rhs )    { return v >= rhs; }
	
	// string comparison
	public int strcmp( LuaString rhs )      { typerror("attempt to compare number with string"); return 0; }
	
	public int checkint() { 
		return v; 
	}
	public long checklong() {
		return v; 
	}
	public double checkdouble() {
		return v;
	}
	public String checkjstring() { 
		return String.valueOf(v); 
	}
	public LuaString checkstring() { 
		return valueOf( String.valueOf(v) ); 
	}

}
