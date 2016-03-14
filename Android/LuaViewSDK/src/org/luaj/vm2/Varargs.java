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

/**
 * Class to encapsulate varargs values, either as part of a variable argument list, or multiple return values.
 * <p>
 * To construct varargs, use one of the static methods such as 
 * {@code LuaValue.varargsOf(LuaValue,LuaValue)}
 * <p>
 * <p>
 * Any LuaValue can be used as a stand-in for Varargs, for both calls and return values. 
 * When doing so, nargs() will return 1 and arg1() or arg(1) will return this.  
 * This simplifies the case when calling or implementing varargs functions with only 
 * 1 argument or 1 return value.  
 * <p>
 * Varargs can also be derived from other varargs by appending to the front with a call 
 * such as  {@code LuaValue.varargsOf(LuaValue,Varargs)}
 * or by taking a portion of the args using {@code Varargs.subargs(int start)}
 * <p>
 * @see LuaValue#varargsOf(LuaValue[])
 * @see LuaValue#varargsOf(LuaValue, Varargs)
 * @see LuaValue#varargsOf(LuaValue[], Varargs)
 * @see LuaValue#varargsOf(LuaValue, LuaValue, Varargs)
 * @see LuaValue#varargsOf(LuaValue[], int, int)
 * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
 * @see LuaValue#subargs(int)
 */
public abstract class Varargs {

	/**
	 * Get the n-th argument value (1-based).
	 * @param i the index of the argument to get, 1 is the first argument
	 * @return Value at position i, or LuaValue.NIL if there is none.
	 * @see Varargs#arg1()
	 * @see LuaValue#NIL
	 */
	abstract public LuaValue arg( int i );
	
	/**
	 * Get the number of arguments, or 0 if there are none. 
	 * @return number of arguments. 
	 */
	abstract public int narg();
	
	/**
	 * Get the first argument in the list. 
	 * @return LuaValue which is first in the list, or LuaValue.NIL if there are no values.
	 * @see Varargs#arg(int)
	 * @see LuaValue#NIL
	 */
	abstract public LuaValue arg1();

	/** 
	 * Evaluate any pending tail call and return result.
	 * @return the evaluated tail call result 
	 */
	public Varargs eval() { return this; }
	
	/**
	 * Return true if this is a TailcallVarargs
	 * @return true if a tail call, false otherwise
	 */
	public boolean isTailcall() {
		return false;
	}
	
	// -----------------------------------------------------------------------
	// utilities to get specific arguments and type-check them.
	// -----------------------------------------------------------------------
	
	/** Gets the type of argument {@code i} 
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return int value corresponding to one of the LuaValue integer type values
	 * @see LuaValue.TNIL
	 * @see LuaValue.TBOOLEAN
	 * @see LuaValue.TNUMBER
	 * @see LuaValue.TSTRING
	 * @see LuaValue.TTABLE
	 * @see LuaValue.TFUNCTION
	 * @see LuaValue.TUSERDATA
	 * @see LuaValue.TTHREAD
	 * */
	public int type(int i)             { return arg(i).type(); }
	
	/** Tests if argument i is nil.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if the argument is nil or does not exist, false otherwise
	 * @see LuaValue.TNIL
	 * */
	public boolean isnil(int i)        { return arg(i).isnil(); }

	/** Tests if argument i is a function.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if the argument exists and is a function or closure, false otherwise
	 * @see LuaValue.TFUNCTION
	 * */
	public boolean isfunction(int i)   { return arg(i).isfunction(); }

	/** Tests if argument i is a number.
	 * Since anywhere a number is required, a string can be used that 
	 * is a number, this will return true for both numbers and 
	 * strings that can be interpreted as numbers.  
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if the argument exists and is a number or 
	 * string that can be interpreted as a number, false otherwise
	 * @see LuaValue.TNUMBER
	 * @see LuaValue.TSTRING
	 * */
	public boolean isnumber(int i)     { return arg(i).isnumber(); }

	/** Tests if argument i is a string.  
	 * Since all lua numbers can be used where strings are used, 
	 * this will return true for both strings and numbers.  
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if the argument exists and is a string or number, false otherwise
	 * @see LuaValue.TNUMBER
	 * @see LuaValue.TSTRING
	 * */
	public boolean isstring(int i)     { return arg(i).isstring(); }

	/** Tests if argument i is a table.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if the argument exists and is a lua table, false otherwise
	 * @see LuaValue.TTABLE
	 * */
	public boolean istable(int i)      { return arg(i).istable(); }

	/** Tests if argument i is a thread.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if the argument exists and is a lua thread, false otherwise
	 * @see LuaValue.TTHREAD
	 * */
	public boolean isthread(int i)     { return arg(i).isthread(); }

	/** Tests if argument i is a userdata.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if the argument exists and is a userdata, false otherwise
	 * @see LuaValue.TUSERDATA
	 * */
	public boolean isuserdata(int i)   { return arg(i).isuserdata(); }

	/** Tests if a value exists at argument i.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if the argument exists, false otherwise
	 * */
	public boolean isvalue(int i)      { return i>0 && i<=narg(); }
	
	/** Return argument i as a boolean value, {@code defval} if nil, or throw a LuaError if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if argument i is boolean true, false if it is false, or defval if not supplied or nil 
	 * @exception LuaError if the argument is not a lua boolean
	 * */
	public boolean      optboolean(int i, boolean defval)          { return arg(i).optboolean(defval); }

	/** Return argument i as a closure, {@code defval} if nil, or throw a LuaError if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaClosure if argument i is a closure, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a lua closure
	 * */
	public LuaClosure   optclosure(int i, LuaClosure defval)       { return arg(i).optclosure(defval); }

	/** Return argument i as a double, {@code defval} if nil, or throw a LuaError if it cannot be converted to one.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return java double value if argument i is a number or string that converts to a number, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a number
	 * */
	public double       optdouble(int i, double defval)            { return arg(i).optdouble(defval); }

	/** Return argument i as a function, {@code defval} if nil, or throw a LuaError  if an incompatible type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaValue that can be called if argument i is lua function or closure, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a lua function or closure
	 * */
	public LuaFunction  optfunction(int i, LuaFunction defval)     { return arg(i).optfunction(defval); }

	/** Return argument i as a java int value, discarding any fractional part, {@code defval} if nil, or throw a LuaError  if not a number.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return int value with fraction discarded and truncated if necessary if argument i is number, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a number
	 * */
	public int          optint(int i, int defval)                  { return arg(i).optint(defval); }

	/** Return argument i as a java int value, {@code defval} if nil, or throw a LuaError  if not a number or is not representable by a java int.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaInteger value that fits in a java int without rounding, or defval if not supplied or nil
	 * @exception LuaError if the argument cannot be represented by a java int value
	 * */
	public LuaInteger   optinteger(int i, LuaInteger defval)       { return arg(i).optinteger(defval); }

	/** Return argument i as a java long value, discarding any fractional part, {@code defval} if nil, or throw a LuaError  if not a number.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return long value with fraction discarded and truncated if necessary if argument i is number, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a number
	 * */
	public long         optlong(int i, long defval)                { return arg(i).optlong(defval); }

	/** Return argument i as a LuaNumber, {@code defval} if nil, or throw a LuaError  if not a number or string that can be converted to a number.
	 * @param i the index of the argument to test, 1 is the first argument, or defval if not supplied or nil
	 * @return LuaNumber if argument i is number or can be converted to a number
	 * @exception LuaError if the argument is not a number
	 * */
	public LuaNumber    optnumber(int i, LuaNumber defval)         { return arg(i).optnumber(defval); }

	/** Return argument i as a java String if a string or number, {@code defval} if nil, or throw a LuaError  if any other type
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return String value if argument i is a string or number, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a string or number
	 * */
	public String       optjstring(int i, String defval)           { return arg(i).optjstring(defval); }

	/** Return argument i as a LuaString if a string or number, {@code defval} if nil, or throw a LuaError  if any other type
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaString value if argument i is a string or number, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a string or number
	 * */
	public LuaString    optstring(int i, LuaString defval)         { return arg(i).optstring(defval); }

	/** Return argument i as a LuaTable if a lua table, {@code defval} if nil, or throw a LuaError  if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaTable value if a table, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a lua table
	 * */
	public LuaTable     opttable(int i, LuaTable defval)           { return arg(i).opttable(defval); }

	/** Return argument i as a LuaThread if a lua thread, {@code defval} if nil, or throw a LuaError  if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaThread value if a thread, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a lua thread
	 * */
	public LuaThread    optthread(int i, LuaThread defval)         { return arg(i).optthread(defval); }

	/** Return argument i as a java Object if a userdata, {@code defval} if nil, or throw a LuaError  if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return java Object value if argument i is a userdata, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a userdata
	 * */
	public Object       optuserdata(int i, Object defval)          { return arg(i).optuserdata(defval); }

	/** Return argument i as a java Object if it is a userdata whose instance Class c or a subclass, 
	 * {@code defval} if nil, or throw a LuaError  if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @param c the class to which the userdata instance must be assignable
	 * @return java Object value if argument i is a userdata whose instance Class c or a subclass, or defval if not supplied or nil
	 * @exception LuaError if the argument is not a userdata or from whose instance c is not assignable
	 * */
	public Object       optuserdata(int i, Class c, Object defval) { return arg(i).optuserdata(c,defval); }

	/** Return argument i as a LuaValue if it exists, or {@code defval}.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaValue value if the argument exists, defval if not
	 * @exception LuaError if the argument does not exist.
	 * */
	public LuaValue     optvalue(int i, LuaValue defval)           { return i>0 && i<=narg()? arg(i): defval; }

	/** Return argument i as a boolean value, or throw an error if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if argument i is boolean true, false if it is false
	 * @exception LuaError if the argument is not a lua boolean
	 * */
	public boolean      checkboolean(int i)          { return arg(i).checkboolean(); }

	/** Return argument i as a closure, or throw an error if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaClosure if argument i is a closure.
	 * @exception LuaError if the argument is not a lua closure
	 * */
	public LuaClosure   checkclosure(int i)          { return arg(i).checkclosure(); }

	/** Return argument i as a double, or throw an error if it cannot be converted to one.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return java double value if argument i is a number or string that converts to a number
	 * @exception LuaError if the argument is not a number
	 * */
	public double       checkdouble(int i)           { return arg(i).checknumber().todouble(); }

	/** Return argument i as a function, or throw an error if an incompatible type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaValue that can be called if argument i is lua function or closure
	 * @exception LuaError if the argument is not a lua function or closure
	 * */
	public LuaFunction    checkfunction(int i)         { return arg(i).checkfunction(); }

	/** Return argument i as a java int value, discarding any fractional part, or throw an error if not a number.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return int value with fraction discarded and truncated if necessary if argument i is number
	 * @exception LuaError if the argument is not a number
	 * */
	public int          checkint(int i)              { return arg(i).checknumber().toint(); }

	/** Return argument i as a java int value, or throw an error if not a number or is not representable by a java int.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaInteger value that fits in a java int without rounding
	 * @exception LuaError if the argument cannot be represented by a java int value
	 * */
	public LuaInteger   checkinteger(int i)          { return arg(i).checkinteger(); }

	/** Return argument i as a java long value, discarding any fractional part, or throw an error if not a number.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return long value with fraction discarded and truncated if necessary if argument i is number
	 * @exception LuaError if the argument is not a number
	 * */
	public long         checklong(int i)             { return arg(i).checknumber().tolong(); }

	/** Return argument i as a LuaNumber, or throw an error if not a number or string that can be converted to a number.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaNumber if argument i is number or can be converted to a number
	 * @exception LuaError if the argument is not a number
	 * */
	public LuaNumber    checknumber(int i)           { return arg(i).checknumber(); }

	/** Return argument i as a java String if a string or number, or throw an error if any other type
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return String value if argument i is a string or number
	 * @exception LuaError if the argument is not a string or number
	 * */
	public String       checkjstring(int i)          { return arg(i).checkjstring(); }

	/** Return argument i as a LuaString if a string or number, or throw an error if any other type
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaString value if argument i is a string or number
	 * @exception LuaError if the argument is not a string or number
	 * */
	public LuaString    checkstring(int i)           { return arg(i).checkstring(); }

	/** Return argument i as a LuaTable if a lua table, or throw an error if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaTable value if a table
	 * @exception LuaError if the argument is not a lua table
	 * */
	public LuaTable     checktable(int i)            { return arg(i).checktable(); }

	/** Return argument i as a LuaThread if a lua thread, or throw an error if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaThread value if a thread
	 * @exception LuaError if the argument is not a lua thread
	 * */
	public LuaThread    checkthread(int i)           { return arg(i).checkthread(); }

	/** Return argument i as a java Object if a userdata, or throw an error if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return java Object value if argument i is a userdata
	 * @exception LuaError if the argument is not a userdata
	 * */
	public Object       checkuserdata(int i)         { return arg(i).checkuserdata(); }

	/** Return argument i as a java Object if it is a userdata whose instance Class c or a subclass, 
	 * or throw an error if any other type.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @param c the class to which the userdata instance must be assignable
	 * @return java Object value if argument i is a userdata whose instance Class c or a subclass
	 * @exception LuaError if the argument is not a userdata or from whose instance c is not assignable
	 * */
	public Object       checkuserdata(int i,Class c) { return arg(i).checkuserdata(c); }

	/** Return argument i as a LuaValue if it exists, or throw an error.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaValue value if the argument exists
	 * @exception LuaError if the argument does not exist.
	 * */
	public LuaValue     checkvalue(int i)            { return i<=narg()? arg(i): LuaValue.argerror(i,"value expected"); }

	/** Return argument i as a LuaValue if it is not nil, or throw an error if it is nil.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return LuaValue value if the argument is not nil
	 * @exception LuaError if the argument doesn't exist or evaluates to nil.
	 * */
	public LuaValue     checknotnil(int i)           { return arg(i).checknotnil(); }
	
	/** Return argument i as a LuaValue when a user-supplied assertion passes, or throw an error.
	 * @param test user supplied assertion to test against
	 * @param i the index to report in any error message
	 * @param msg the error message to use when the test fails
	 * @return LuaValue value if the value of {@code test} is {@code true}
	 * @exception LuaError if the the value of {@code test} is {@code false}
	 * */
	public void         argcheck(boolean test, int i, String msg) { if (!test) LuaValue.argerror(i,msg); }
	
	/** Return true if there is no argument or nil at argument i.
	 * @param i the index of the argument to test, 1 is the first argument
	 * @return true if argument i contains either no argument or nil
	 * */
	public boolean isnoneornil(int i) {
		return i>narg() || arg(i).isnil();
	}
	
	/** Convert argument {@code i} to java boolean based on lua rules for boolean evaluation. 
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return {@code false} if argument i is nil or false, otherwise {@code true}
	 * */
	public boolean toboolean(int i)           { return arg(i).toboolean(); }

	/** Return argument i as a java byte value, discarding any fractional part and truncating, 
	 * or 0 if not a number.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return byte value with fraction discarded and truncated if necessary if argument i is number, otherwise 0
	 * */
	public byte    tobyte(int i)              { return arg(i).tobyte(); }
	
	/** Return argument i as a java char value, discarding any fractional part and truncating, 
	 * or 0 if not a number.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return char value with fraction discarded and truncated if necessary if argument i is number, otherwise 0
	 * */
	public char    tochar(int i)              { return arg(i).tochar(); }

	/** Return argument i as a java double value or 0 if not a number.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return double value if argument i is number, otherwise 0
	 * */
	public double  todouble(int i)            { return arg(i).todouble(); }

	/** Return argument i as a java float value, discarding excess fractional part and truncating, 
	 * or 0 if not a number.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return float value with excess fraction discarded and truncated if necessary if argument i is number, otherwise 0
	 * */
	public float   tofloat(int i)             { return arg(i).tofloat(); }
	
	/** Return argument i as a java int value, discarding any fractional part and truncating, 
	 * or 0 if not a number.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return int value with fraction discarded and truncated if necessary if argument i is number, otherwise 0
	 * */
	public int     toint(int i)               { return arg(i).toint(); }

	/** Return argument i as a java long value, discarding any fractional part and truncating, 
	 * or 0 if not a number.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return long value with fraction discarded and truncated if necessary if argument i is number, otherwise 0
	 * */
	public long    tolong(int i)              { return arg(i).tolong(); }

	/** Return argument i as a java String based on the type of the argument.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return String value representing the type
	 * */
	public String  tojstring(int i)           { return arg(i).tojstring(); }
	
	/** Return argument i as a java short value, discarding any fractional part and truncating, 
	 * or 0 if not a number.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return short value with fraction discarded and truncated if necessary if argument i is number, otherwise 0
	 * */
	public short   toshort(int i)             { return arg(i).toshort(); }

	/** Return argument i as a java Object if a userdata, or null.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @return java Object value if argument i is a userdata, otherwise null
	 * */
	public Object  touserdata(int i)          { return arg(i).touserdata(); }

	/** Return argument i as a java Object if it is a userdata whose instance Class c or a subclass, or null.
	 * @param i the index of the argument to convert, 1 is the first argument
	 * @param c the class to which the userdata instance must be assignable
	 * @return java Object value if argument i is a userdata whose instance Class c or a subclass, otherwise null
	 * */
	public Object  touserdata(int i,Class c)  { return arg(i).touserdata(c); }
	
	/** Convert the list of varargs values to a human readable java String. 
	 * @return String value in human readable form such as {1,2}. 
	 */
	public String tojstring() {
		Buffer sb = new Buffer();
		sb.append( "(" );
		for ( int i=1,n=narg(); i<=n; i++ ) {
			if (i>1) sb.append( "," );
			sb.append( arg(i).tojstring() );
		}
		sb.append( ")" );
		return sb.tojstring();
	}
	
	/** Convert the value or values to a java String using Varargs.tojstring() 
	 * @return String value in human readable form. 
	 * @see Varargs#tojstring()
	 */
	public String toString() { return tojstring(); }

	/**
	 * Create a {@code Varargs} instance containing arguments starting at index {@code start}
	 * @param start the index from which to include arguments, where 1 is the first argument.
	 * @return Varargs containing argument { start, start+1,  ... , narg-start-1 }
	 */
	abstract public Varargs subargs(final int start);

	/**
	 * Implementation of Varargs for use in the Varargs.subargs() function.
	 * @see Varargs#subargs(int)
	 */
	static class SubVarargs extends Varargs {
		private final Varargs v;
		private final int start;
		private final int end;
		public SubVarargs(Varargs varargs, int start, int end) {
			this.v = varargs;
			this.start = start;
			this.end = end;
		}
		public LuaValue arg(int i) {
			i += start-1;
			return i>=start && i<=end? v.arg(i): LuaValue.NIL;
		}
		public LuaValue arg1() {
			return v.arg(start);
		}
		public int narg() {
			return end+1-start;
		}
		public Varargs subargs(final int start) {
			if (start == 1)
				return this;
			final int newstart = this.start + start - 1;
			if (start > 0) {
				if (newstart >= this.end)
					return LuaValue.NONE;
				if (newstart == this.end)
					return v.arg(this.end);
				if (newstart == this.end-1)
					return new Varargs.PairVarargs(v.arg(this.end-1), v.arg(this.end));
				return new SubVarargs(v, newstart, this.end);
			}
			return new SubVarargs(v, newstart, this.end);
		}
	}

	/** Varargs implemenation backed by two values.  
	 * <p>
	 * This is an internal class not intended to be used directly.
	 * Instead use the corresponding static method on LuaValue. 
	 *  
	 * @see LuaValue#varargsOf(LuaValue, Varargs)
	 */
	static final class PairVarargs extends Varargs {
		private final LuaValue v1;
		private final Varargs v2;
		/** Construct a Varargs from an two LuaValue. 
		 * <p>
		 * This is an internal class not intended to be used directly.
		 * Instead use the corresponding static method on LuaValue. 
		 *  
		 * @see LuaValue#varargsOf(LuaValue, Varargs)
		 */
		PairVarargs(LuaValue v1, Varargs v2) {
			this.v1 = v1;
			this.v2 = v2;
		}
		public LuaValue arg(int i) {
			return i==1? v1: v2.arg(i-1);
		}
		public int narg() {
			return 1+v2.narg();
		}
		public LuaValue arg1() { 
			return v1; 
		}
		public Varargs subargs(final int start) {
			if (start == 1)
				return this;
			if (start == 2)
				return v2;
			if (start > 2)
				return v2.subargs(start - 1);
			return LuaValue.argerror(1, "start must be > 0");
		}
	}

	/** Varargs implemenation backed by an array of LuaValues 
	 * <p>
	 * This is an internal class not intended to be used directly.
	 * Instead use the corresponding static methods on LuaValue. 
	 *  
	 * @see LuaValue#varargsOf(LuaValue[])
	 * @see LuaValue#varargsOf(LuaValue[], Varargs)
	 */
	static final class ArrayVarargs extends Varargs {
		private final LuaValue[] v;
		private final Varargs r;
		/** Construct a Varargs from an array of LuaValue. 
		 * <p>
		 * This is an internal class not intended to be used directly.
		 * Instead use the corresponding static methods on LuaValue. 
		 *  
		 * @see LuaValue#varargsOf(LuaValue[])
		 * @see LuaValue#varargsOf(LuaValue[], Varargs)
		 */
		ArrayVarargs(LuaValue[] v, Varargs r) {
			this.v = v;
			this.r = r ;
			for (int i = 0; i < v.length; ++i)
				if (v[i] == null)
					throw new IllegalArgumentException("nulls in array");
		}
		public LuaValue arg(int i) {
			return i < 1 ? LuaValue.NIL: i <= v.length? v[i - 1]: r.arg(i-v.length);
		}
		public int narg() {
			return v.length+r.narg();
		}
		public LuaValue arg1() { return v.length>0? v[0]: r.arg1(); }
		public Varargs subargs(int start) {
			if (start <= 0)
				LuaValue.argerror(1, "start must be > 0");
			if (start == 1)
				return this;
			if (start > v.length)
				return r.subargs(start - v.length);
			return LuaValue.varargsOf(v, start - 1, v.length - (start - 1), r);
		}
	}

	/** Varargs implemenation backed by an array of LuaValues 
	 * <p>
	 * This is an internal class not intended to be used directly.
	 * Instead use the corresponding static methods on LuaValue. 
	 *  
	 * @see LuaValue#varargsOf(LuaValue[], int, int)
	 * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
	 */
	static final class ArrayPartVarargs extends Varargs {
		private final int offset;
		private final LuaValue[] v;
		private final int length;
		private final Varargs more;
		/** Construct a Varargs from an array of LuaValue. 
		 * <p>
		 * This is an internal class not intended to be used directly.
		 * Instead use the corresponding static methods on LuaValue. 
		 *  
		 * @see LuaValue#varargsOf(LuaValue[], int, int)
		 */
		ArrayPartVarargs(LuaValue[] v, int offset, int length) {
			this.v = v;
			this.offset = offset;
			this.length = length;
			this.more = LuaValue.NONE;
		}
		/** Construct a Varargs from an array of LuaValue and additional arguments. 
		 * <p>
		 * This is an internal class not intended to be used directly.
		 * Instead use the corresponding static method on LuaValue. 
		 *  
		 * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
		 */
		public ArrayPartVarargs(LuaValue[] v, int offset, int length, Varargs more) {
			this.v = v;
			this.offset = offset;
			this.length = length;
			this.more = more;
		}
		public LuaValue arg(final int i) {
			return i < 1? LuaValue.NIL: i <= length? v[offset+i-1]: more.arg(i-length);
		}
		public int narg() {
			return length + more.narg();
		}
		public LuaValue arg1() { 
			return length>0? v[offset]: more.arg1(); 
		}
		public Varargs subargs(int start) {
			if (start <= 0)
				LuaValue.argerror(1, "start must be > 0");
			if (start == 1)
				return this;
			if (start > length)
				return more.subargs(start - length);
			return LuaValue.varargsOf(v, offset + start - 1, length - (start - 1), more);
		}
	}
}
