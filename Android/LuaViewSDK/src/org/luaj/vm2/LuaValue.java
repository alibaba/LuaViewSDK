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
 * Base class for all concrete lua type values.  
 * <p>
 * Establishes base implementations for all the operations on lua types.
 * This allows Java clients to deal essentially with one type for all Java values, namely {@link LuaValue}.
 * <p>
 * Constructors are provided as static methods for common Java types, such as 
 * {@link LuaValue#valueOf(int)} or {@link LuaValue#valueOf(String)} 
 * to allow for instance pooling. 
 * <p> 
 * Constants are defined for the lua values 
 * {@link #NIL}, {@link #TRUE}, and {@link #FALSE}. 
 * A constant {@link #NONE} is defined which is a {@link Varargs} list having no values.
 * <p> 
 * Operations are performed on values directly via their Java methods.  
 * For example, the following code divides two numbers:
 * <pre> {@code
 * LuaValue a = LuaValue.valueOf( 5 );
 * LuaValue b = LuaValue.valueOf( 4 );
 * LuaValue c = a.div(b);
 * } </pre>
 * Note that in this example, c will be a {@link LuaDouble}, but would be a {@link LuaInteger} 
 * if the value of a were changed to 8, say. 
 * In general the value of c in practice will vary depending on both the types and values of a and b
 * as well as any metatable/metatag processing that occurs.  
 * <p>
 * Field access and function calls are similar, with common overloads to simplify Java usage:
 * <pre> {@code
 * LuaValue globals = JsePlatform.standardGlobals();
 * LuaValue sqrt = globals.get("math").get("sqrt");
 * LuaValue print = globals.get("print");
 * LuaValue d = sqrt.call( a );
 * print.call( LuaValue.valueOf("sqrt(5):"), a );
 * } </pre>
 * <p>
 * To supply variable arguments or get multiple return values, use 
 * {@link invoke(Varargs)} or {@link invokemethod(LuaValue, Varargs)} methods:
 * <pre> {@code
 * LuaValue modf = globals.get("math").get("modf");
 * Varargs r = modf.invoke( d );
 * print.call( r.arg(1), r.arg(2) );
 * } </pre>
 * <p>
 * To load and run a script, {@link LoadState} is used:
 * <pre> {@code
 * LoadState.load( new FileInputStream("main.lua"), "main.lua", globals ).call();
 * } </pre>
 * <p>
 * although {@code require} could also be used: 
 * <pre> {@code
 * globals.get("require").call(LuaValue.valueOf("main"));
 * } </pre>
 * For this to work the file must be in the current directory, or in the class path, 
 * dependening on the platform.  
 * See {@link JsePlatform} and {@link JmePlatform} for details. 
 * <p>
 * In general a {@link LuaError} may be thrown on any operation when the  
 * types supplied to any operation are illegal from a lua perspective. 
 * Examples could be attempting to concatenate a NIL value, or attempting arithmetic 
 * on values that are not number.  
 * <p>
 * There are several methods for preinitializing tables, such as:
 * <ul>
 * <li>{@link #listOf(LuaValue[])} for unnamed elements</li>
 * <li>{@link #tableOf(LuaValue[])} for named elements</li>
 * <li>{@link #tableOf(LuaValue[], LuaValue[], Varargs)} for mixtures</li>
 * </ul>
 * <p>  
 * Predefined constants exist for the standard lua type constants 
 * {@link TNIL}, {@link TBOOLEAN}, {@link TLIGHTUSERDATA}, {@link TNUMBER}, {@link TSTRING}, 
 * {@link TTABLE}, {@link TFUNCTION}, {@link TUSERDATA}, {@link TTHREAD},
 * and extended lua type constants 
 * {@link TINT}, {@link TNONE}, {@link TVALUE}
 * <p> 
 * Predefined constants exist for all strings used as metatags:  
 * {@link INDEX}, {@link NEWINDEX}, {@link CALL}, {@link MODE}, {@link METATABLE},   
 * {@link ADD}, {@link SUB}, {@link DIV}, {@link MUL}, {@link POW},   
 * {@link MOD}, {@link UNM}, {@link LEN}, {@link EQ}, {@link LT},   
 * {@link LE}, {@link TOSTRING}, and {@link CONCAT}.
 * 
 * @see JsePlatform
 * @see JmePlatform
 * @see LoadState 
 * @see Varargs
 */
abstract
public class LuaValue extends Varargs {
	
	/** Type enumeration constant for lua numbers that are ints, for compatibility with lua 5.1 number patch only */
	public static final int TINT            = (-2);
	
	/** Type enumeration constant for lua values that have no type, for example weak table entries */
	public static final int TNONE			= (-1);
	
	/** Type enumeration constant for lua nil */
	public static final int TNIL			= 0;
	
	/** Type enumeration constant for lua booleans */
	public static final int TBOOLEAN		= 1;
	
	/** Type enumeration constant for lua light userdata, for compatibility with C-based lua only */
	public static final int TLIGHTUSERDATA	= 2;
	
	/** Type enumeration constant for lua numbers */
	public static final int TNUMBER			= 3;
	
	/** Type enumeration constant for lua strings */
	public static final int TSTRING			= 4;
	
	/** Type enumeration constant for lua tables */
	public static final int TTABLE			= 5;
	
	/** Type enumeration constant for lua functions */
	public static final int TFUNCTION		= 6;
	
	/** Type enumeration constant for lua userdatas */
	public static final int TUSERDATA		= 7;
	
	/** Type enumeration constant for lua threads */
	public static final int TTHREAD			= 8;
	
	/** Type enumeration constant for unknown values, for compatibility with C-based lua only */
	public static final int TVALUE          = 9;

	/** String array constant containing names of each of the lua value types
	 * @see #type()
	 * @see #typename() 
	 */
	public static final String[] TYPE_NAMES = {
		"nil", 
		"boolean",
		"lightuserdata",
		"number",
		"string",
		"table",
		"function",
		"userdata",
		"thread",
		"value",
	};
	
	/** LuaValue constant corresponding to lua {@code nil} */
	public static final LuaValue   NIL       = LuaNil._NIL;
	
	/** LuaBoolean constant corresponding to lua {@code true} */
	public static final LuaBoolean TRUE      = LuaBoolean._TRUE;

	/** LuaBoolean constant corresponding to lua {@code false} */
	public static final LuaBoolean FALSE     = LuaBoolean._FALSE;

	/** LuaValue constant corresponding to a {@link Varargs} list of no values */
	public static final LuaValue   NONE      = None._NONE;
	
	/** LuaValue number constant equal to 0 */
	public static final LuaNumber  ZERO      = LuaInteger.valueOf(0);
	
	/** LuaValue number constant equal to 1 */
	public static final LuaNumber  ONE       = LuaInteger.valueOf(1);

	/** LuaValue number constant equal to -1 */
	public static final LuaNumber  MINUSONE  = LuaInteger.valueOf(-1);
	
	/** LuaValue array constant with no values */
	public static final LuaValue[] NOVALS    = {};

	/** The variable name of the environment. */
	public static LuaString ENV               = valueOf("_ENV");

	/** LuaString constant with value "__index" for use as metatag */
	public static final LuaString INDEX       = valueOf("__index");

	/** LuaString constant with value "__newindex" for use as metatag */
	public static final LuaString NEWINDEX    = valueOf("__newindex");

	/** LuaString constant with value "__call" for use as metatag */
	public static final LuaString CALL        = valueOf("__call");

	/** LuaString constant with value "__mode" for use as metatag */
	public static final LuaString MODE        = valueOf("__mode");

	/** LuaString constant with value "__metatable" for use as metatag */
	public static final LuaString METATABLE   = valueOf("__metatable");

	/** LuaString constant with value "__add" for use as metatag */
	public static final LuaString ADD         = valueOf("__add");

	/** LuaString constant with value "__sub" for use as metatag */
	public static final LuaString SUB         = valueOf("__sub");

	/** LuaString constant with value "__div" for use as metatag */
	public static final LuaString DIV         = valueOf("__div");

	/** LuaString constant with value "__mul" for use as metatag */
	public static final LuaString MUL         = valueOf("__mul");

	/** LuaString constant with value "__pow" for use as metatag */
	public static final LuaString POW         = valueOf("__pow");

	/** LuaString constant with value "__mod" for use as metatag */
	public static final LuaString MOD         = valueOf("__mod");

	/** LuaString constant with value "__unm" for use as metatag */
	public static final LuaString UNM         = valueOf("__unm");

	/** LuaString constant with value "__len" for use as metatag */
	public static final LuaString LEN         = valueOf("__len");

	/** LuaString constant with value "__eq" for use as metatag */
	public static final LuaString EQ          = valueOf("__eq");

	/** LuaString constant with value "__lt" for use as metatag */
	public static final LuaString LT          = valueOf("__lt");

	/** LuaString constant with value "__le" for use as metatag */
	public static final LuaString LE          = valueOf("__le");

	/** LuaString constant with value "__tostring" for use as metatag */
	public static final LuaString TOSTRING    = valueOf("__tostring");

	/** LuaString constant with value "__concat" for use as metatag */
	public static final LuaString CONCAT      = valueOf("__concat");
	
	/** LuaString constant with value "" */
	public static final LuaString EMPTYSTRING = valueOf("");

	/** Limit on lua stack size */
	private static int MAXSTACK = 250;
	
	/** Array of {@link NIL} values to optimize filling stacks using System.arraycopy(). 
	 * Must not be modified. 
	 */
	public static final LuaValue[] NILS = new LuaValue[MAXSTACK];	
	static {
		for ( int i=0; i<MAXSTACK; i++ )
			NILS[i] = NIL;
	}
	
	// type
	/** Get the enumeration value for the type of this value.
	 * @return value for this type, one of 
	 * {@link TNIL}, 
	 * {@link TBOOLEAN}, 
	 * {@link TNUMBER}, 
	 * {@link TSTRING}, 
	 * {@link TTABLE}, 
	 * {@link TFUNCTION}, 
	 * {@link TUSERDATA}, 
	 * {@link TTHREAD} 
	 * @see #typename()
	 */
	abstract public int type();
	
	/** Get the String name of the type of this value. 
	 * <p>
	 * 
	 * @return name from type name list {@link #TYPE_NAMES}
	 * corresponding to the type of this value: 		
	 * "nil", "boolean", "number", "string",
	 * "table", "function", "userdata", "thread"  
	 * @see #type()
	 */
	abstract public String  typename();

	/** Check if {@code this} is a {@code boolean} 
	 * @return true if this is a {@code boolean}, otherwise false
	 * @see #isboolean()
	 * @see #toboolean()
	 * @see #checkboolean()
	 * @see #optboolean(boolean)
	 * @see #TOBOLEAN
	 */
	public boolean isboolean()           { return false; }

	/** Check if {@code this} is a {@code function} that is a closure, 
	 * meaning interprets lua bytecode for its execution
	 * @return true if this is a {@code closure}, otherwise false
	 * @see #isfunction()
	 * @see #checkclosure()
	 * @see #optclosure(LuaClosure)
	 * @see #TFUNCTION
	 */
	public boolean isclosure()           { return false; }

	/** Check if {@code this} is a {@code function}
	 * @return true if this is a {@code function}, otherwise false
	 * @see #isclosure()
	 * @see #checkfunction()
	 * @see #optfunciton(LuaFunction)
	 * @see #TFUNCTION
	 */
	public boolean isfunction()          { return false; }
	
	/** Check if {@code this} is a {@code number} and is representable by java int 
	 * without rounding or truncation
	 * @return true if this is a {@code number} 
	 * meaning derives from {@link LuaNumber} 
	 * or derives from {@link LuaString} and is convertible to a number,
	 * and can be represented by int, 
	 * otherwise false
	 * @see #isinttype()
	 * @see #islong()
	 * @see #tonumber()
	 * @see #checkint()
	 * @see #optint(int)
	 * @see #TNUMBER
	 */
	public boolean isint()               { return false; }

	/** Check if {@code this} is a {@link LuaInteger}
	 * <p>
	 * No attempt to convert from string will be made by this call. 
	 * @return true if this is a {@code LuaInteger}, 
	 * otherwise false
	 * @see #isint()
	 * @see #isnumber()
	 * @see #tonumber()
	 * @see #TNUMBER
	 */
	public boolean isinttype()           { return false; }
	
	/** Check if {@code this} is a {@code number} and is representable by java long 
	 * without rounding or truncation
	 * @return true if this is a {@code number} 
	 * meaning derives from {@link LuaNumber} 
	 * or derives from {@link LuaString} and is convertible to a number,
	 * and can be represented by long, 
	 * otherwise false
	 * @see #tonumber()
	 * @see #checklong()
	 * @see #optlong(long)
	 * @see #TNUMBER
	 */
	public boolean islong()              { return false; }
	
	/** Check if {@code this} is {@code nil}
	 * @return true if this is {@code nil}, otherwise false
	 * @see #NIL
	 * @see #NONE
	 * @see #checknotnil()
	 * @see #optvalue(LuaValue)
	 * @see Varargs#isnoneornil(int)
	 * @see #TNIL
	 * @see #TNONE
	 */
	public boolean isnil()               { return false; }
	
	/** Check if {@code this} is a {@code number}
	 * @return true if this is a {@code number}, 
	 * meaning derives from {@link LuaNumber} 
	 * or derives from {@link LuaString} and is convertible to a number, 
	 * otherwise false
	 * @see #tonumber()
	 * @see #checknumber()
	 * @see #optnumber(LuaNumber)
	 * @see #TNUMBER
	 */
	public boolean isnumber()            { return false; } // may convert from string
	
	/** Check if {@code this} is a {@code string}
	 * @return true if this is a {@code string}, 
	 * meaning derives from {@link LuaString} or {@link LuaNumber}, 
	 * otherwise false
	 * @see #tostring()
	 * @see #checkstring()
	 * @see #optstring(LuaString)
	 * @see #TSTRING
	 */
	public boolean isstring()            { return false; }
	
	/** Check if {@code this} is a {@code thread}
	 * @return true if this is a {@code thread}, otherwise false
	 * @see #checkthread()
	 * @see #optthread(LuaThread)
	 * @see #TTHREAD
	 */
	public boolean isthread()            { return false; }
	
	/** Check if {@code this} is a {@code table}
	 * @return true if this is a {@code table}, otherwise false
	 * @see #checktable()
	 * @see #opttable(LuaTable)
	 * @see #TTABLE
	 */
	public boolean istable()             { return false; }
	
	/** Check if {@code this} is a {@code userdata}
	 * @return true if this is a {@code userdata}, otherwise false
	 * @see #isuserdata(Class)
	 * @see #touserdata()
	 * @see #checkuserdata()
	 * @see #optuserdata(Object)
	 * @see #TUSERDATA
	 */
	public boolean isuserdata()          { return false; }
	
	/** Check if {@code this} is a {@code userdata} of type {@code c}
	 * @param c Class to test instance against
	 * @return true if this is a {@code userdata} 
	 * and the instance is assignable to {@code c}, 
	 * otherwise false
	 * @see #isuserdata()
	 * @see #touserdata(Class)
	 * @see #checkuserdata(Class)
	 * @see #optuserdata(Object,Class)
	 * @see #TUSERDATA
	 */
	public boolean isuserdata(Class c)   { return false; }
	
	/** Convert to boolean false if {@link #NIL} or {@link #FALSE}, true if anything else
	 * @return Value cast to byte if number or string convertible to number, otherwise 0
	 * @see #optboolean(boolean)
	 * @see #checkboolean() 
	 * @see #isboolean()
	 * @see TBOOLEAN
	 */
	public boolean toboolean()           { return true; }
	
	/** Convert to byte if numeric, or 0 if not.
	 * @return Value cast to byte if number or string convertible to number, otherwise 0 
	 * @see #toint()
	 * @see #todouble()
	 * @see #optbyte(byte)
	 * @see #checknumber()
	 * @see #isnumber()
	 * @see TNUMBER
	 */
	public byte    tobyte()              { return 0; }
	
	/** Convert to char if numeric, or 0 if not.
	 * @return Value cast to char if number or string convertible to number, otherwise 0 
	 * @see #toint()
	 * @see #todouble()
	 * @see #optchar(char)
	 * @see #checknumber()
	 * @see #isnumber()
	 * @see TNUMBER
	 */
	public char    tochar()              { return 0; }
	
	/** Convert to double if numeric, or 0 if not.
	 * @return Value cast to double if number or string convertible to number, otherwise 0 
	 * @see #toint()
	 * @see #tobyte()
	 * @see #tochar()
	 * @see #toshort()
	 * @see #tolong()
	 * @see #tofloat()
	 * @see #optdouble(double)
	 * @see #checknumber()
	 * @see #isnumber()
	 * @see TNUMBER
	 */
	public double  todouble()            { return 0; }
	
	/** Convert to float if numeric, or 0 if not.
	 * @return Value cast to float if number or string convertible to number, otherwise 0 
	 * @see #toint()
	 * @see #todouble()
	 * @see #optfloat(float)
	 * @see #checknumber()
	 * @see #isnumber()
	 * @see TNUMBER
	 */
	public float   tofloat()             { return 0; }
	
	/** Convert to int if numeric, or 0 if not.
	 * @return Value cast to int if number or string convertible to number, otherwise 0 
	 * @see #tobyte()
	 * @see #tochar()
	 * @see #toshort()
	 * @see #tolong()
	 * @see #tofloat()
	 * @see #todouble()
	 * @see #optint(int)
	 * @see #checknumber()
	 * @see #isnumber()
	 * @see TNUMBER
	 */
	public int     toint()               { return 0; }
	
	/** Convert to long if numeric, or 0 if not.
	 * @return Value cast to long if number or string convertible to number, otherwise 0 
	 * @see #isint()
	 * @see #isinttype()
	 * @see #toint()
	 * @see #todouble()
	 * @see #optlong(long)
	 * @see #checknumber()
	 * @see #isnumber()
	 * @see TNUMBER
	 */
	public long    tolong()              { return 0; }
	
	/** Convert to short if numeric, or 0 if not.
	 * @return Value cast to short if number or string convertible to number, otherwise 0 
	 * @see #toint()
	 * @see #todouble()
	 * @see #optshort(short)
	 * @see #checknumber()
	 * @see #isnumber()
	 * @see TNUMBER
	 */
	public short   toshort()             { return 0; }
	
	/** Convert to human readable String for any type.
	 * @return String for use by human readers based on type. 
	 * @see #tostring()
	 * @see #optjstring(String)
	 * @see #checkjstring()
	 * @see #isstring()
	 * @see TSTRING
	 */
	public String  tojstring()           { return typename() + ": " + Integer.toHexString(hashCode()); }
	
	/** Convert to userdata instance, or null.
	 * @return userdata instance if userdata, or null if not {@link LuaUserdata}
	 * @see #optuserdata(Object)
	 * @see #checkuserdata()
	 * @see #isuserdata()
	 * @see #TUSERDATA
	 */
	public Object  touserdata()          { return null; }
	
	/** Convert to userdata instance if specific type, or null.
	 * @return userdata instance if is a userdata whose instance derives from {@code c}, 
	 * or null if not {@link LuaUserdata}
	 * @see #optuserdata(Class,Object)
	 * @see #checkuserdata(Class)
	 * @see #isuserdata(Class)
	 * @see #TUSERDATA
	 */
	public Object  touserdata(Class c)   { return null; }

	/** 
	 * Convert the value to a human readable string using {@link #tojstring()}
	 * @return String value intended to be human readible.
	 * @see #tostring()
	 * @see #tojstring()
	 * @see #optstring(LuaString)
	 * @see #checkstring()
	 * @see #toString() 
	 */
	public String toString() { return tojstring(); }
	
	/** Conditionally convert to lua number without throwing errors.
	 * <p> 
	 * In lua all numbers are strings, but not all strings are numbers. 
	 * This function will return 
	 * the {@link LuaValue} {@code this} if it is a number 
	 * or a string convertible to a number, 
	 * and {@link NIL} for all other cases.  
	 * <p>
	 * This allows values to be tested for their "numeric-ness" without
	 * the penalty of throwing exceptions, 
	 * nor the cost of converting the type and creating storage for it.    
	 * @return {@code this} if it is a {@link LuaNumber}
	 * or {@link LuaString} that can be converted to a number, 
	 * otherwise {@link #NIL}
	 * @see #tostring()
	 * @see #optnumber(LuaNumber)
	 * @see #checknumber()
	 * @see #toint()
	 * @see #todouble()
	 */ 
	public LuaValue    tonumber()     { return NIL; }
	
	/** Conditionally convert to lua string without throwing errors.
	 * <p> 
	 * In lua all numbers are strings, so this function will return 
	 * the {@link LuaValue} {@code this} if it is a string or number, 
	 * and {@link NIL} for all other cases.  
	 * <p>
	 * This allows values to be tested for their "string-ness" without
	 * the penalty of throwing exceptions.    
	 * @return {@code this} if it is a {@link LuaString} or {@link LuaNumber}, 
	 * otherwise {@link NIL}  
	 * @see #tonumber()
	 * @see #tojstring()
	 * @see #optstring(LuaString)
	 * @see #checkstring()
	 * @see #toString() 
	 */ 
	public LuaValue    tostring()     { return NIL; }

	/** Check that optional argument is a boolean and return its boolean value
	 * @param defval boolean value to return if {@code this} is nil or none
	 * @return {@code this} cast to boolean if a {@LuaBoolean}, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} otherwise
	 * @throws LuaError if was not a boolean or nil or none.
	 * @see #checkboolean()
	 * @see #isboolean()
	 * @see #TBOOLEAN
	 */
	public boolean     optboolean(boolean defval)          { argerror("boolean");   return false; }

	/** Check that optional argument is a closure and return as {@link LuaClosure}
	 * <p>
	 * A {@link LuaClosure} is a {@LuaFunction} that executes lua byteccode.  
	 * @param defval {@link LuaClosure} to return if {@code this} is nil or none
	 * @return {@code this} cast to {@link LuaClosure} if a function, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} otherwise
	 * @throws LuaError if was not a closure or nil or none.
	 * @see #checkclosure()
	 * @see #isclosure()
	 * @see #TFUNCTION
	 */
	public LuaClosure  optclosure(LuaClosure defval)       { argerror("closure");   return null;  }

	/** Check that optional argument is a number or string convertible to number and return as double
	 * @param defval double to return if {@code this} is nil or none
	 * @return {@code this} cast to double if numeric, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} otherwise
	 * @throws LuaError if was not numeric or nil or none.
	 * @see #optint(int)
	 * @see #optinteger(LuaInteger)
	 * @see #checkdouble()
	 * @see #todouble()
	 * @see #tonumber()
	 * @see #isnumber()
	 * @see #TNUMBER
	 */
	public double      optdouble(double defval)            { argerror("double");    return 0;     }

	/** Check that optional argument is a function and return as {@link LuaFunction}
	 * <p>
	 * A {@link LuaFunction} may either be a Java function that implements 
	 * functionality directly in Java,  or a {@link LuaClosure} 
	 * which is a {@link LuaFunction} that executes lua bytecode.  
	 * @param defval {@link LuaFunction} to return if {@code this} is nil or none
	 * @return {@code this} cast to {@link LuaFunction} if a function, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} otherwise
	 * @throws LuaError if was not a function or nil or none.
	 * @see #checkfunction()
	 * @see #isfunction()
	 * @see #TFUNCTION
	 */
	public LuaFunction optfunction(LuaFunction defval)     { argerror("function");  return null;  }

	/** Check that optional argument is a number or string convertible to number and return as int
	 * @param defval int to return if {@code this} is nil or none
	 * @return {@code this} cast to int if numeric, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} otherwise
	 * @throws LuaError if was not numeric or nil or none.
	 * @see #optdouble(double)
	 * @see #optlong(long)
	 * @see #optinteger(LuaInteger)
	 * @see #checkint()
	 * @see #toint()
	 * @see #tonumber()
	 * @see #isnumber()
	 * @see #TNUMBER
	 */
	public int         optint(int defval)                  { argerror("int");       return 0;     }

	/** Check that optional argument is a number or string convertible to number and return as {@link LuaInteger}
	 * @param defval {@link LuaInteger} to return if {@code this} is nil or none
	 * @return {@code this} converted and wrapped in {@link LuaInteger} if numeric, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} otherwise
	 * @throws LuaError if was not numeric or nil or none.
	 * @see #optdouble(double)
	 * @see #optint(int)
	 * @see #checkint()
	 * @see #toint()
	 * @see #tonumber()
	 * @see #isnumber()
	 * @see #TNUMBER
	 */
	public LuaInteger  optinteger(LuaInteger defval)       { argerror("integer");   return null;  }

	/** Check that optional argument is a number or string convertible to number and return as long
	 * @param defval long to return if {@code this} is nil or none
	 * @return {@code this} cast to long if numeric, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} otherwise
	 * @throws LuaError if was not numeric or nil or none.
	 * @see #optdouble(double)
	 * @see #optint(int)
	 * @see #checkint()
	 * @see #toint()
	 * @see #tonumber()
	 * @see #isnumber()
	 * @see #TNUMBER
	 */
	public long        optlong(long defval)                { argerror("long");      return 0;     }

	/** Check that optional argument is a number or string convertible to number and return as {@link LuaNumber}
	 * @param defval {@link LuaNumber} to return if {@code this} is nil or none
	 * @return {@code this} cast to {@link LuaNumber} if numeric, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} otherwise
	 * @throws LuaError if was not numeric or nil or none.
	 * @see #optdouble(double)
	 * @see #optlong(long)
	 * @see #optint(int)
	 * @see #checkint()
	 * @see #toint()
	 * @see #tonumber()
	 * @see #isnumber()
	 * @see #TNUMBER
	 */
	public LuaNumber   optnumber(LuaNumber defval)         { argerror("number");    return null;  }

	/** Check that optional argument is a string or number and return as Java String
	 * @param defval {@link LuaString} to return if {@code this} is nil or none
	 * @return {@code this} converted to String if a string or number, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} if some other type
	 * @throws LuaError if was not a string or number or nil or none.
	 * @see #tojstring()
	 * @see #optstring(LuaString)
	 * @see #checkjstring()
	 * @see #toString() 
	 * @see #TSTRING
	 */
	public String      optjstring(String defval)           { argerror("String");    return null;  }

	/** Check that optional argument is a string or number and return as {@link LuaString}
	 * @param defval {@link LuaString} to return if {@code this} is nil or none
	 * @return {@code this} converted to {@link LuaString} if a string or number, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} if some other type
	 * @throws LuaError if was not a string or number or nil or none.
	 * @see #tojstring()
	 * @see #optjstring(String)
	 * @see #checkstring()
	 * @see #toString() 
	 * @see #TSTRING
	 */
	public LuaString   optstring(LuaString defval)         { argerror("string");    return null;  }

	/** Check that optional argument is a table and return as {@link LuaTable}
	 * @param defval {@link LuaTable} to return if {@code this} is nil or none
	 * @return {@code this} cast to {@link LuaTable} if a table, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} if some other type
	 * @throws LuaError if was not a table or nil or none.
	 * @see #checktable()
	 * @see #istable()
	 * @see #TTABLE
	 */
	public LuaTable    opttable(LuaTable defval)           { argerror("table");     return null;  }

	/** Check that optional argument is a thread and return as {@link LuaThread}
	 * @param defval {@link LuaThread} to return if {@code this} is nil or none
	 * @return {@code this} cast to {@link LuaTable} if a thread, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} if some other type
	 * @throws LuaError if was not a thread or nil or none.
	 * @see #checkthread()
	 * @see #isthread()
	 * @see #TTHREAD
	 */
	public LuaThread   optthread(LuaThread defval)         { argerror("thread");    return null;  }

	/** Check that optional argument is a userdata and return the Object instance
	 * @param defval Object to return if {@code this} is nil or none
	 * @return Object instance of the userdata if a {@link LuaUserdata}, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} if some other type
	 * @throws LuaError if was not a userdata or nil or none.
	 * @see #checkuserdata()
	 * @see #isuserdata()
	 * @see #optuserdata(Class, Object)
	 * @see #TUSERDATA
	 */
	public Object      optuserdata(Object defval)          { argerror("object");    return null;  }

	/** Check that optional argument is a userdata whose instance is of a type
	 * and return the Object instance
	 * @param c Class to test userdata instance against
	 * @param defval Object to return if {@code this} is nil or none
	 * @return Object instance of the userdata if a {@link LuaUserdata} and instance is assignable to {@code c}, 
	 * {@code defval} if nil or none, 
	 * throws {@link LuaError} if some other type
	 * @throws LuaError if was not a userdata whose instance is assignable to {@code c} or nil or none.
	 * @see #checkuserdata(Class)
	 * @see #isuserdata(Class)
	 * @see #optuserdata(Object)
	 * @see #TUSERDATA
	 */
	public Object      optuserdata(Class c, Object defval) { argerror(c.getName()); return null;  }

	/** Perform argument check that this is not nil or none.
	 * @param defval {@link LuaValue} to return if {@code this} is nil or none
	 * @return {@code this} if not nil or none, else {@code defval}
	 * @see #NIL
	 * @see #NONE
	 * @see #isnil()
	 * @see Varargs#isnoneornil(int)
	 * @see #TNIL
	 * @see #TNONE
	 */
	public LuaValue    optvalue(LuaValue defval)           { return this; }

	
	/** Check that the value is a {@link LuaBoolean}, 
	 * or throw {@link LuaError} if not
	 * @return boolean value for {@code this} if it is a {@link LuaBoolean} 
	 * @throws LuaError if not a {@link LuaBoolean}
	 * @see #optboolean(boolean)
	 * @see #TBOOLEAN
	 */
	public boolean     checkboolean()          { argerror("boolean");   return false; }
	
	/** Check that the value is a {@link LuaClosure} , 
	 * or throw {@link LuaError} if not
	 * <p>
	 * {@link LuaClosure} is a subclass of {@LuaFunction} that interprets lua bytecode. 
	 * @return {@code this} cast as {@link LuaClosure} 
	 * @throws LuaError if not a {@link LuaClosure}
	 * @see #checkfunction()
	 * @see #optclosure(LuaClosure)
	 * @see #isclosure()
	 * @see #TFUNCTION
	 */
	public LuaClosure  checkclosure()          { argerror("closure");   return null;  }
	
	/** Check that the value is numeric and return the value as a double, 
	 * or throw {@link LuaError} if not numeric
	 * <p>
	 * Values that are {@link LuaNumber} and values that are {@link LuaString} 
	 * that can be converted to a number will be converted to double. 
	 * @return value cast to a double if numeric
	 * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString} that can't be converted to number
	 * @see #checkint()
	 * @see #checkinteger()
	 * @see #checklong()
	 * @see #optdouble(double)
	 * @see #TNUMBER
	 */
	public double      checkdouble()           { argerror("double");    return 0; }
	
	/** Check that the value is a function , or throw {@link LuaError} if not 
	 * <p>
	 * A {@link LuaFunction} may either be a Java function that implements 
	 * functionality directly in Java,  or a {@link LuaClosure} 
	 * which is a {@link LuaFunction} that executes lua bytecode.  
	 * @return {@code this} if it is a lua function or closure
	 * @throws LuaError if not a function 
	 * @see #checkclosure()
	 */
	public LuaFunction    checkfunction()         { argerror("function");  return null; }	


	/** Check that the value is a Globals instance, or throw {@link LuaError} if not 
	 * <p>
	 * {@link Globals} are a special {@link LuaTable} that establish the default global environment.  
	 * @return {@code this} if if an instance fof {@Globals}
	 * @throws LuaError if not a {@link Globals} instance. 
	 */
	public Globals checkglobals() { argerror("globals");  return null; }

	/** Check that the value is numeric, and convert and cast value to int, or throw {@link LuaError} if not numeric 
	 * <p>
	 * Values that are {@link LuaNumber} will be cast to int and may lose precision.
	 * Values that are {@link LuaString} that can be converted to a number will be converted, 
	 * then cast to int, so may also lose precision.
	 * @return value cast to a int if numeric
	 * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString} that can't be converted to number
	 * @see #checkinteger()
	 * @see #checklong()
	 * @see #checkdouble()
	 * @see #optint(int)
	 * @see #TNUMBER
	 */
	public int         checkint()              { argerror("int");       return 0; }

	/** Check that the value is numeric, and convert and cast value to int, or throw {@link LuaError} if not numeric 
	 * <p>
	 * Values that are {@link LuaNumber} will be cast to int and may lose precision.
	 * Values that are {@link LuaString} that can be converted to a number will be converted, 
	 * then cast to int, so may also lose precision.
	 * @return value cast to a int and wrapped in {@link LuaInteger} if numeric
	 * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString} that can't be converted to number
	 * @see #checkint()
	 * @see #checklong()
	 * @see #checkdouble()
	 * @see #optinteger(LuaInteger)
	 * @see #TNUMBER
	 */
	public LuaInteger  checkinteger()          { argerror("integer");   return null; }
	
	/** Check that the value is numeric, and convert and cast value to long, or throw {@link LuaError} if not numeric 
	 * <p>
	 * Values that are {@link LuaNumber} will be cast to long and may lose precision.
	 * Values that are {@link LuaString} that can be converted to a number will be converted, 
	 * then cast to long, so may also lose precision.
	 * @return value cast to a long if numeric
	 * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString} that can't be converted to number
	 * @see #checkint()
	 * @see #checkinteger()
	 * @see #checkdouble()
	 * @see #optlong(long)
	 * @see #TNUMBER
	 */
	public long        checklong()             { argerror("long");      return 0; }
	
	/** Check that the value is numeric, and return as a LuaNumber if so, or throw {@link LuaError} 
	 * <p>
	 * Values that are {@link LuaString} that can be converted to a number will be converted and returned. 
	 * @return value as a {@link LuaNumber} if numeric
	 * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString} that can't be converted to number
	 * @see #checkint()
	 * @see #checkinteger()
	 * @see #checkdouble()
	 * @see #checklong()
	 * @see #optnumber(LuaNumber)
	 * @see #TNUMBER
	 */
	public LuaNumber   checknumber()           { argerror("number");    return null; }
	
	/** Check that the value is numeric, and return as a LuaNumber if so, or throw {@link LuaError} 
	 * <p>
	 * Values that are {@link LuaString} that can be converted to a number will be converted and returned. 
	 * @param msg String message to supply if conversion fails
	 * @return value as a {@link LuaNumber} if numeric
	 * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString} that can't be converted to number
	 * @see #checkint()
	 * @see #checkinteger()
	 * @see #checkdouble()
	 * @see #checklong()
	 * @see #optnumber(LuaNumber)
	 * @see #TNUMBER
	 */
	public LuaNumber   checknumber(String msg) { throw new LuaError(msg); }
	
	/** Convert this value to a Java String.
	 * <p>
	 * The string representations here will roughly match what is produced by the 
	 * C lua distribution, however hash codes have no relationship, 
	 * and there may be differences in number formatting.  
	 * @return String representation of the value
	 * @see #checkstring()
	 * @see #optjstring(String)
	 * @see #tojstring()
	 * @see #isstring
	 * @see #TSTRING
	 */
	public String      checkjstring()          { argerror("string");    return null; }
	
	/** Check that this is a lua string, or throw {@link LuaError} if it is not.
	 * <p>
	 * In lua all numbers are strings, so this will succeed for 
	 * anything that derives from {@link LuaString} or {@link LuaNumber}.  
	 * Numbers will be converted to {@link LuaString}.  
	 * 
	 * @return {@link LuaString} representation of the value if it is a {@link LuaString} or {@link LuaNumber}
	 * @throws LuaError if {@code this} is not a {@link LuaTable}
	 * @see #checkjstring()
	 * @see #optstring(LuaString)
	 * @see #tostring()
	 * @see #isstring()
	 * @see #TSTRING
	 */
	public LuaString   checkstring()           { argerror("string");    return null; }
	
	/** Check that this is a {@link LuaTable}, or throw {@link LuaError} if it is not
	 * @return {@code this} if it is a {@link LuaTable}
	 * @throws LuaError if {@code this} is not a {@link LuaTable}
	 * @see #istable()
	 * @see #opttable(LuaTable)
	 * @see #TTABLE
	 */
	public LuaTable    checktable()            { argerror("table");     return null; }	
	
	/** Check that this is a {@link LuaThread}, or throw {@link LuaError} if it is not
	 * @return {@code this} if it is a {@link LuaThread}
	 * @throws LuaError if {@code this} is not a {@link LuaThread}
	 * @see #isthread()
	 * @see #optthread(LuaThread)
	 * @see #TTHREAD
	 */
	public LuaThread   checkthread()           { argerror("thread");    return null; }
	
	/** Check that this is a {@link LuaUserdata}, or throw {@link LuaError} if it is not
	 * @return {@code this} if it is a {@link LuaUserdata}
	 * @throws LuaError if {@code this} is not a {@link LuaUserdata}
	 * @see #isuserdata()
	 * @see #optuserdata(Object)
	 * @see #checkuserdata(Class)
	 * @see #TUSERDATA
	 */
	public Object      checkuserdata()         { argerror("userdata");  return null; }
	
	/** Check that this is a {@link LuaUserdata}, or throw {@link LuaError} if it is not
	 * @return {@code this} if it is a {@link LuaUserdata}
	 * @throws LuaError if {@code this} is not a {@link LuaUserdata}
	 * @see #isuserdata(Class)
	 * @see #optuserdata(Class, Object)
	 * @see #checkuserdata()
	 * @see #TUSERDATA
	 */
	public Object      checkuserdata(Class c)  { argerror("userdata");  return null; }
	
	/** Check that this is not the value {@link NIL}, or throw {@link LuaError} if it is
	 * @return {@code this} if it is not {@link NIL}
	 * @throws LuaError if {@code this} is {@link NIL}
	 * @see #optvalue(LuaValue)
	 */
	public LuaValue    checknotnil()           { return this; }
	
	/** Return true if this is a valid key in a table index operation.
	 * @return true if valid as a table key, otherwise false
	 * @see #isnil()
	 * @see #isinttype()
	 */
	public boolean isvalidkey()         { return true; }
	
	/** 
	 * Throw a {@link LuaError} with a particular message
	 * @param message String providing message details
	 * @throws LuaError in all cases
	 */
	public static LuaValue error(String message) { throw new LuaError(message); }

	/** 
	 * Assert a condition is true, or throw a {@link LuaError} if not
	 * @param b condition to test
	 * @return returns no value when b is true, throws error not return if b is false
	 * @throws LuaError if b is not true
	 */
	public static void assert_(boolean b,String msg) { if(!b) throw new LuaError(msg); }
	
	/** 
	 * Throw a {@link LuaError} indicating an invalid argument was supplied to a function
	 * @param expected String naming the type that was expected
	 * @throws LuaError in all cases
	 */
	protected LuaValue argerror(String expected) { throw new LuaError("bad argument: "+expected+" expected, got "+typename()); }
	
	/** 
	 * Throw a {@link LuaError} indicating an invalid argument was supplied to a function
	 * @param iarg index of the argument that was invalid, first index is 1
	 * @param msg String providing information about the invalid argument
	 * @throws LuaError in all cases
	 */
	public static LuaValue argerror(int iarg,String msg) { throw new LuaError("bad argument #"+iarg+": "+msg); }
	
	/** 
	 * Throw a {@link LuaError} indicating an invalid type was supplied to a function
	 * @param expected String naming the type that was expected
	 * @throws LuaError in all cases
	 */
	protected LuaValue typerror(String expected) { throw new LuaError(expected+" expected, got "+typename()); }
	
	/** 
	 * Throw a {@link LuaError} indicating an operation is not implemented 
	 * @throws LuaError in all cases
	 */
	protected LuaValue unimplemented(String fun) { throw new LuaError("'"+fun+"' not implemented for "+typename()); }
	
	/** 
	 * Throw a {@link LuaError} indicating an illegal operation occurred, 
	 * typically involved in managing weak references
	 * @throws LuaError in all cases
	 */
	protected LuaValue illegal(String op,String typename) { throw new LuaError("illegal operation '"+op+"' for "+typename); }
	
	/** 
	 * Throw a {@link LuaError} based on the len operator, 
	 * typically due to an invalid operand type
	 * @throws LuaError in all cases
	 */
	protected LuaValue lenerror() { throw new LuaError("attempt to get length of "+typename()); }
	
	/** 
	 * Throw a {@link LuaError} based on an arithmetic error such as add, or pow, 
	 * typically due to an invalid operand type
	 * @throws LuaError in all cases
	 */
	protected LuaValue aritherror() { throw new LuaError("attempt to perform arithmetic on "+typename()); }
	
	/** 
	 * Throw a {@link LuaError} based on an arithmetic error such as add, or pow,
	 * typically due to an invalid operand type
	 * @param fun String description of the function that was attempted
	 * @throws LuaError in all cases
	 */
	protected LuaValue aritherror(String fun) { throw new LuaError("attempt to perform arithmetic '"+fun+"' on "+typename()); }
	
	/** 
	 * Throw a {@link LuaError} based on a comparison error such as greater-than or less-than,
	 * typically due to an invalid operand type
	 * @param rhs String description of what was on the right-hand-side of the comparison that resulted in the error.
	 * @throws LuaError in all cases
	 */
	protected LuaValue compareerror(String rhs) { throw new LuaError("attempt to compare "+typename()+" with "+rhs); }
	
	/** 
	 * Throw a {@link LuaError} based on a comparison error such as greater-than or less-than,
	 * typically due to an invalid operand type
	 * @param rhs Right-hand-side of the comparison that resulted in the error.
	 * @throws LuaError in all cases
	 */
	protected LuaValue compareerror(LuaValue rhs) { throw new LuaError("attempt to compare "+typename()+" with "+rhs.typename()); }
	
	/** Get a value in a table including metatag processing using {@link INDEX}.
	 * @param key the key to look up, must not be {@link NIL} or null
	 * @return {@link LuaValue} for that key, or {@link NIL} if not found and no metatag
	 * @throws LuaError if {@code this} is not a table,
	 * or there is no {@link INDEX} metatag,  
	 * or key is {@link NIL} 
	 * @see #get(int)
	 * @see #get(String)
	 * @see #rawget(LuaValue)
	 */
	public LuaValue get( LuaValue key ) { return gettable(this,key); }
	
	/** Get a value in a table including metatag processing using {@link INDEX}.
	 * @param key the key to look up
	 * @return {@link LuaValue} for that key, or {@link NIL} if not found
	 * @throws LuaError if {@code this} is not a table,
	 * or there is no {@link INDEX} metatag  
	 * @see #get(LuaValue)
	 * @see #rawget(int)
	 */
	public LuaValue get( int key ) { return get(LuaInteger.valueOf(key)); }

	/** Get a value in a table including metatag processing using {@link INDEX}.
	 * @param key the key to look up, must not be null
	 * @return {@link LuaValue} for that key, or {@link NIL} if not found
	 * @throws LuaError if {@code this} is not a table,
	 * or there is no {@link INDEX} metatag  
	 * @see #get(LuaValue)
	 * @see #rawget(String)
	 */
	public LuaValue get( String key ) { return get(valueOf(key)); }
	
	/** Set a value in a table without metatag processing using {@link NEWINDEX}.
	 * @param key the key to use, must not be {@link NIL} or null
	 * @param value the value to use, can be {@link NIL}, must not be null
	 * @throws LuaError if {@code this} is not a table, 
	 * or key is {@link NIL},
	 * or there is no {@link NEWINDEX} metatag  
	 */
	public void set( LuaValue key, LuaValue value ) { settable(this, key, value); }
	
	/** Set a value in a table without metatag processing using {@link NEWINDEX}.
	 * @param key the key to use
	 * @param value the value to use, can be {@link NIL}, must not be null
	 * @throws LuaError if {@code this} is not a table, 
	 * or there is no {@link NEWINDEX} metatag  
	 */
	public void set( int key, LuaValue value ) { set(LuaInteger.valueOf(key), value ); }
	
	/** Set a value in a table without metatag processing using {@link NEWINDEX}.
	 * @param key the key to use
	 * @param value the value to use, must not be null
	 * @throws LuaError if {@code this} is not a table, 
	 * or there is no {@link NEWINDEX} metatag  
	 */
	public void set( int key, String value ) { set(key, valueOf(value) ); }
	
	/** Set a value in a table without metatag processing using {@link NEWINDEX}.
	 * @param key the key to use, must not be {@link NIL} or null
	 * @param value the value to use, can be {@link NIL}, must not be null
	 * @throws LuaError if {@code this} is not a table, 
	 * or there is no {@link NEWINDEX} metatag  
	 */
	public void set( String key, LuaValue value ) { set(valueOf(key), value ); }
	
	/** Set a value in a table without metatag processing using {@link NEWINDEX}.
	 * @param key the key to use, must not be null
	 * @param value the value to use
	 * @throws LuaError if {@code this} is not a table, 
	 * or there is no {@link NEWINDEX} metatag  
	 */
	public void set( String key, double value ) { set(valueOf(key), valueOf(value) ); }
	
	/** Set a value in a table without metatag processing using {@link NEWINDEX}.
	 * @param key the key to use, must not be null
	 * @param value the value to use
	 * @throws LuaError if {@code this} is not a table, 
	 * or there is no {@link NEWINDEX} metatag  
	 */
	public void set( String key, int value ) { set(valueOf(key), valueOf(value) ); }
	
	/** Set a value in a table without metatag processing using {@link NEWINDEX}.
	 * @param key the key to use, must not be null
	 * @param value the value to use, must not be null
	 * @throws LuaError if {@code this} is not a table, 
	 * or there is no {@link NEWINDEX} metatag  
	 */
	public void set( String key, String value ) { set(valueOf(key), valueOf(value) ); }

	/** Get a value in a table without metatag processing.
	 * @param key the key to look up, must not be {@link NIL} or null
	 * @return {@link LuaValue} for that key, or {@link NIL} if not found
	 * @throws LuaError if {@code this} is not a table, or key is {@link NIL}
	 */
	public LuaValue rawget( LuaValue key ) { return unimplemented("rawget"); }

	/** Get a value in a table without metatag processing.
	 * @param key the key to look up
	 * @return {@link LuaValue} for that key, or {@link NIL} if not found
	 * @throws LuaError if {@code this} is not a table
	 */
	public LuaValue rawget( int key ) { return rawget(valueOf(key)); }

	/** Get a value in a table without metatag processing.
	 * @param key the key to look up, must not be null
	 * @return {@link LuaValue} for that key, or {@link NIL} if not found
	 * @throws LuaError if {@code this} is not a table
	 */
	public LuaValue rawget( String key ) { return rawget(valueOf(key)); }
	
	/** Set a value in a table without metatag processing.
	 * @param key the key to use, must not be {@link NIL} or null
	 * @param value the value to use, can be {@link NIL}, must not be null
	 * @throws LuaError if {@code this} is not a table, or key is {@link NIL}
	 */
	public void rawset( LuaValue key, LuaValue value ) { unimplemented("rawset"); }
	
	/** Set a value in a table without metatag processing.
	 * @param key the key to use
	 * @param value the value to use, can be {@link NIL}, must not be null
	 * @throws LuaError if {@code this} is not a table
	 */
	public void rawset( int key, LuaValue value ) { rawset(valueOf(key),value); }
	
	/** Set a value in a table without metatag processing.
	 * @param key the key to use
	 * @param value the value to use, can be {@link NIL}, must not be null
	 * @throws LuaError if {@code this} is not a table
	 */
	public void rawset( int key, String value ) { rawset(key,valueOf(value)); }
	
	/** Set a value in a table without metatag processing.
	 * @param key the key to use, must not be null
	 * @param value the value to use, can be {@link NIL}, must not be null
	 * @throws LuaError if {@code this} is not a table
	 */
	public void rawset( String key, LuaValue value ) { rawset(valueOf(key),value); }
	
	/** Set a value in a table without metatag processing.
	 * @param key the key to use, must not be null
	 * @param value the value to use
	 * @throws LuaError if {@code this} is not a table
	 */
	public void rawset( String key, double value ) { rawset(valueOf(key),valueOf(value)); }
	
	/** Set a value in a table without metatag processing.
	 * @param key the key to use, must not be null
	 * @param value the value to use
	 * @throws LuaError if {@code this} is not a table
	 */
	public void rawset( String key, int value ) { rawset(valueOf(key),valueOf(value)); }
	
	/** Set a value in a table without metatag processing.
	 * @param key the key to use, must not be null
	 * @param value the value to use, must not be null
	 * @throws LuaError if {@code this} is not a table
	 */
	public void rawset( String key, String value ) { rawset(valueOf(key),valueOf(value)); }

	/** Set list values in a table without invoking metatag processing  
	 * <p>
	 * Primarily used internally in response to a SETLIST bytecode. 
	 * @param key0 the first key to set in the table
	 * @param values the list of values to set
	 * @throws LuaError if this is not a table. 
	 */
	public void rawsetlist( int key0, Varargs values ) { for ( int i=0, n=values.narg(); i<n; i++ ) rawset(key0+i,values.arg(i+1)); }
	
	/** Preallocate the array part of a table to be a certain size, 
	 * <p>
	 * Primarily used internally in response to a SETLIST bytecode. 
	 * @param i the number of array slots to preallocate in the table.
	 * @throws LuaError if this is not a table. 
	 */
	public void presize( int i) { typerror("table"); }
	
	/** Find the next key,value pair if {@code this} is a table, 
	 * return {@link NIL} if there are no more, or throw a {@link LuaError} if not a table.
	 * <p>
	 * To iterate over all key-value pairs in a table you can use
	 * <pre> {@code
	 * LuaValue k = LuaValue.NIL;
	 * while ( true ) {
	 *    Varargs n = table.next(k);
	 *    if ( (k = n.arg1()).isnil() )
	 *       break;
	 *    LuaValue v = n.arg(2)
	 *    process( k, v )
	 * }}</pre>
	 * @param index {@link LuaInteger} value identifying a key to start from, 
	 * or {@link NIL} to start at the beginning
	 * @return {@link Varargs} containing {key,value} for the next entry, 
	 * or {@link NIL} if there are no more.
	 * @throws LuaError if {@code this} is not a table, or the supplied key is invalid.
	 * @see LuaTable
	 * @see #inext()
	 * @see #valueOf(int)
	 * @see Varargs#arg1()
	 * @see Varargs#arg(int)
	 * @see #isnil()
	 */
	public Varargs next(LuaValue index) { return typerror("table"); }
	
	/** Find the next integer-key,value pair if {@code this} is a table, 
	 * return {@link NIL} if there are no more, or throw a {@link LuaError} if not a table.
	 * <p>
	 * To iterate over integer keys in a table you can use
	 * <pre> {@code
	 *   LuaValue k = LuaValue.NIL;
	 *   while ( true ) {
	 *      Varargs n = table.inext(k);
	 *      if ( (k = n.arg1()).isnil() )
	 *         break;
	 *      LuaValue v = n.arg(2)
	 *      process( k, v )
	 *   }
	 * } </pre>
	 * @param index {@link LuaInteger} value identifying a key to start from, 
	 * or {@link NIL} to start at the beginning
	 * @return {@link Varargs} containing {@code (key,value)} for the next entry, 
	 * or {@link NONE} if there are no more.
	 * @throws LuaError if {@code this} is not a table, or the supplied key is invalid.
	 * @see LuaTable
	 * @see #next()
	 * @see #valueOf(int)
	 * @see Varargs#arg1()
	 * @see Varargs#arg(int)
	 * @see #isnil()
	 */
	public Varargs inext(LuaValue index) { return typerror("table"); }
	
	/** 
	 * Load a library instance by calling it with and empty string as the modname, 
	 * and this Globals as the environment. This is normally used to iniitalize the 
	 * library instance and which may install itself into these globals.   
	 * @param library The callable {@link LuaValue} to load into {@code this}
	 * @param string 
	 * @return {@link LuaValue} returned by the initialization call.
	 */
	public LuaValue load(LuaValue library) { return library.call(EMPTYSTRING, this); }

	// varargs references
	public LuaValue arg(int index) { return index==1? this: NIL; }
	public int narg() { return 1; };
	public LuaValue arg1() { return this; }
	
	/** 
	 * Get the metatable for this {@link LuaValue}
	 * <p>
	 * For {@link LuaTable} and {@link LuaUserdata} instances, 
	 * the metatable returned is this instance metatable. 
	 * For all other types, the class metatable value will be returned.   
	 * @return metatable, or null if it there is none
	 * @see LuaBoolean#s_metatable
	 * @see LuaNumber#s_metatable
	 * @see LuaNil#s_metatable
	 * @see LuaFunction#s_metatable
	 * @see LuaThread#s_metatable
	 */
	public LuaValue getmetatable() { return null; }
	
	/** 
	 * Set the metatable for this {@link LuaValue}
	 * <p>
	 * For {@link LuaTable} and {@link LuaUserdata} instances, the metatable is per instance. 
	 * For all other types, there is one metatable per type that can be set directly from java 
	 * @param metatable {@link LuaValue} instance to serve as the metatable, or null to reset it.
	 * @return {@code this} to allow chaining of Java function calls
	 * @see LuaBoolean#s_metatable
	 * @see LuaNumber#s_metatable
	 * @see LuaNil#s_metatable
	 * @see LuaFunction#s_metatable
	 * @see LuaThread#s_metatable
	 */
	public LuaValue setmetatable(LuaValue metatable) { return argerror("table"); }
			
	/** Call {@link this} with 0 arguments, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a method call, use {@link #method(LuaValue)} instead.
	 *  
	 * @return First return value {@code (this())}, or {@link NIL} if there were none.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call(LuaValue)
	 * @see #call(LuaValue,LuaValue)
	 * @see #call(LuaValue, LuaValue, LuaValue)
	 * @see #invoke()
	 * @see #method(String)
	 * @see #method(LuaValue)
	 */
	public LuaValue call() { return callmt().call(this); }

	/** Call {@link this} with 1 argument, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a method call, use {@link #method(LuaValue)} instead.
	 *  
	 * @param arg First argument to supply to the called function
	 * @return First return value {@code (this(arg))}, or {@link NIL} if there were none.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #call(LuaValue,LuaValue)
	 * @see #call(LuaValue, LuaValue, LuaValue)
	 * @see #invoke(LuaValue)
	 * @see #method(String,LuaValue)
	 * @see #method(LuaValue,LuaValue)
	 */
	public LuaValue call(LuaValue arg) { return callmt().call(this,arg); }

	/** Convenience function which calls a luavalue with a single, string argument. 
	 * @param arg String argument to the function.  This will be converted to a LuaString.
	 * @return return value of the invocation. 
	 * @see #call(LuaValue)
	 */
	public LuaValue call(String arg) { return call(valueOf(arg)); }
	
	/** Call {@link this} with 2 arguments, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a method call, use {@link #method(LuaValue)} instead.
	 *  
	 * @param arg1 First argument to supply to the called function
	 * @param arg2 Second argument to supply to the called function
	 * @return First return value {@code (this(arg1,arg2))}, or {@link NIL} if there were none.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #call(LuaValue)
	 * @see #call(LuaValue, LuaValue, LuaValue)
	 * @see #invoke(LuaValue,LuaValue)
	 * @see #method(String,LuaValue,LuaValue)
	 * @see #method(LuaValue,LuaValue,LuaValue)
	 */
	public LuaValue call(LuaValue arg1, LuaValue arg2) { return callmt().call(this,arg1,arg2); }

	/** Call {@link this} with 3 arguments, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a method call, use {@link #method(LuaValue)} instead.
	 *  
	 * @param arg1 First argument to supply to the called function
	 * @param arg2 Second argument to supply to the called function
	 * @param arg3 Second argument to supply to the called function
	 * @return First return value {@code (this(arg1,arg2,arg3))}, or {@link NIL} if there were none.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #call(LuaValue)
	 * @see #call(LuaValue, LuaValue)
	 * @see #invoke(LuaValue,LuaValue, LuaValue)
	 * @see #invokemethod(String,Varargs)
	 * @see #invokemethod(LuaValue,Varargs)
	 */
	public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) { return callmt().invoke(new LuaValue[]{this,arg1,arg2,arg3}).arg1(); }
	
	/** Call named method on {@link this} with 0 arguments, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument. 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #call()} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @return All values returned from {@code this:name()} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke()
	 * @see #method(LuaValue)
	 * @see #method(String,LuaValue)
	 * @see #method(String,LuaValue,LuaValue)
	 */
	public LuaValue method(String name) { return this.get(name).call(this); }

	/** Call named method on {@link this} with 0 arguments, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #call()} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @return All values returned from {@code this:name()} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke()
	 * @see #method(String)
	 * @see #method(LuaValue,LuaValue)
	 * @see #method(LuaValue,LuaValue,LuaValue)
	 */
	public LuaValue method(LuaValue name) { return this.get(name).call(this); }
	
	/** Call named method on {@link this} with 1 argument, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #call(LuaValue)} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @param arg Argument to supply to the method
	 * @return All values returned from {@code this:name(arg)} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call(LuaValue)
	 * @see #invoke(LuaValue)
	 * @see #method(LuaValue,LuaValue)
	 * @see #method(String)
	 * @see #method(String,LuaValue,LuaValue)
	 */
	public LuaValue method(String name, LuaValue arg) { return this.get(name).call(this,arg); }
	
	/** Call named method on {@link this} with 1 argument, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #call(LuaValue)} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @param arg Argument to supply to the method
	 * @return All values returned from {@code this:name(arg)} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call(LuaValue)
	 * @see #invoke(LuaValue)
	 * @see #method(String,LuaValue)
	 * @see #method(LuaValue)
	 * @see #method(LuaValue,LuaValue,LuaValue)
	 */
	public LuaValue method(LuaValue name, LuaValue arg) { return this.get(name).call(this,arg); }

	/** Call named method on {@link this} with 2 arguments, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #call(LuaValue,LuaValue)} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @param arg1 First argument to supply to the method
	 * @param arg2 Second argument to supply to the method
	 * @return All values returned from {@code this:name(arg1,arg2)} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call(LuaValue,LuaValue)
	 * @see #invoke(LuaValue,Varargs)
	 * @see #method(String,LuaValue)
	 * @see #method(LuaValue,LuaValue,LuaValue)
	 */
	public LuaValue method(String name, LuaValue arg1, LuaValue arg2) { return this.get(name).call(this,arg1,arg2); }

	/** Call named method on {@link this} with 2 arguments, including metatag processing, 
	 * and return only the first return value.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return only its first return value, dropping any others.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * If the return value is a {@link Varargs}, only the 1st value will be returned. 
	 * To get multiple values, use {@link #invoke()} instead. 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #call(LuaValue,LuaValue)} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @param arg1 First argument to supply to the method
	 * @param arg2 Second argument to supply to the method
	 * @return All values returned from {@code this:name(arg1,arg2)} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call(LuaValue,LuaValue)
	 * @see #invoke(LuaValue,Varargs)
	 * @see #method(LuaValue,LuaValue)
	 * @see #method(String,LuaValue,LuaValue)
	 */
	public LuaValue method(LuaValue name, LuaValue arg1, LuaValue arg2) { return this.get(name).call(this,arg1,arg2); }
	
	/** Call {@link this} with 0 arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, and return all values.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a method call, use {@link #invokemethod(LuaValue)} instead.
	 *  
	 * @return All return values as a {@link Varargs} instance.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke(Varargs)
	 * @see #invokemethod(String)
	 * @see #invokemethod(LuaValue)
	 */
	public Varargs invoke() { return invoke(NONE); }

	/** Call {@link this} with variable arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, and return all values.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a method call, use {@link #invokemethod(LuaValue)} instead.
	 *  
	 * @param args Varargs containing the arguments to supply to the called function
	 * @return All return values as a {@link Varargs} instance.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #varargsOf(LuaValue[])
	 * @see #call(LuaValue)
	 * @see #invoke()
	 * @see #invoke(LuaValue,Varargs)
	 * @see #invokemethod(String,Varargs)
	 * @see #invokemethod(LuaValue,Varargs)
	 */
	public Varargs invoke(Varargs args) { return callmt().invoke(this,args); }

	/** Call {@link this} with variable arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, and return all values.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a method call, use {@link #invokemethod(LuaValue,Varargs)} instead.
	 *  
	 * @param arg The first argument to supply to the called function
	 * @param varargs Varargs containing the remaining arguments to supply to the called function
	 * @return All return values as a {@link Varargs} instance.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #varargsOf(LuaValue[])
	 * @see #call(LuaValue,LuaValue)
	 * @see #invoke(LuaValue,Varargs)
	 * @see #invokemethod(String,Varargs)
	 * @see #invokemethod(LuaValue,Varargs)
	 */
	public Varargs invoke(LuaValue arg,Varargs varargs) { return invoke(varargsOf(arg,varargs)); }

	/** Call {@link this} with variable arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, and return all values.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a method call, use {@link #invokemethod(LuaValue,Varargs)} instead.
	 *  
	 * @param arg1 The first argument to supply to the called function
	 * @param arg2 The second argument to supply to the called function
	 * @param varargs Varargs containing the remaining arguments to supply to the called function
	 * @return All return values as a {@link Varargs} instance.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #varargsOf(LuaValue[])
	 * @see #call(LuaValue,LuaValue,LuaValue)
	 * @see #invoke(LuaValue,LuaValue,Varargs)
	 * @see #invokemethod(String,Varargs)
	 * @see #invokemethod(LuaValue,Varargs)
	 */
	public Varargs invoke(LuaValue arg1,LuaValue arg2,Varargs varargs) { return invoke(varargsOf(arg1,arg2,varargs)); }

	/** Call {@link this} with variable arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, and return all values.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a method call, use {@link #invokemethod(LuaValue,Varargs)} instead.
	 *  
	 * @param args Array of arguments to supply to the called function
	 * @return All return values as a {@link Varargs} instance.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #varargsOf(LuaValue[])
	 * @see #call(LuaValue,LuaValue,LuaValue)
	 * @see #invoke(LuaValue,LuaValue,Varargs)
	 * @see #invokemethod(String,LuaValue[])
	 * @see #invokemethod(LuaValue,LuaValue[])
	 */
	public Varargs invoke(LuaValue[] args) { return invoke(varargsOf(args)); }

	/** Call {@link this} with variable arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * If {@code this} is a {@link LuaFunction}, call it, and return all values.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a method call, use {@link #invokemethod(LuaValue,Varargs)} instead.
	 *  
	 * @param args Array of arguments to supply to the called function
	 * @param varargs Varargs containing additional arguments to supply to the called function
	 * @return All return values as a {@link Varargs} instance.
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #varargsOf(LuaValue[])
	 * @see #call(LuaValue,LuaValue,LuaValue)
	 * @see #invoke(LuaValue,LuaValue,Varargs)
	 * @see #invokemethod(String,LuaValue[])
	 * @see #invokemethod(LuaValue,LuaValue[])
	 * @see #invokemethod(String,Varargs)
	 * @see #invokemethod(LuaValue,Varargs)
	 */
	public Varargs invoke(LuaValue[] args,Varargs varargs) { return invoke(varargsOf(args,varargs)); }
	
	/** Call named method on {@link this} with 0 arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return all return values as a {@link Varargs} instance.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #invoke()} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @return All values returned from {@code this:name()} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke()
	 * @see #method(String)
	 * @see #invokemethod(LuaValue)
	 * @see #invokemethod(String,LuaValue)
	 */
	public Varargs invokemethod(String name) { return get(name).invoke(this); }
	
	/** Call named method on {@link this} with 0 arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return all return values as a {@link Varargs} instance.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #invoke()} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @return All values returned from {@code this:name()} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke()
	 * @see #method(LuaValue)
	 * @see #invokemethod(String)
	 * @see #invokemethod(LuaValue,LuaValue)
	 */
	public Varargs invokemethod(LuaValue name) { return get(name).invoke(this); }
	
	/** Call named method on {@link this} with 1 argument, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return all return values as a {@link Varargs} instance.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #invoke(Varargs)} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @param args {@link Varargs} containing arguments to supply to the called function after {@code this} 
	 * @return All values returned from {@code this:name(args)} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke(Varargs)
	 * @see #method(String)
	 * @see #invokemethod(LuaValue,Varargs)
	 * @see #invokemethod(String,LuaValue[])
	 */
	public Varargs invokemethod(String name, Varargs args) { return get(name).invoke(varargsOf(this,args)); }
	
	/** Call named method on {@link this} with variable arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return all return values as a {@link Varargs} instance.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #invoke(Varargs)} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @param args {@link Varargs} containing arguments to supply to the called function after {@code this} 
	 * @return All values returned from {@code this:name(args)} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke(Varargs)
	 * @see #method(String)
	 * @see #invokemethod(String,Varargs)
	 * @see #invokemethod(LuaValue,LuaValue[])
	 */
	public Varargs invokemethod(LuaValue name, Varargs args) { return get(name).invoke(varargsOf(this,args)); }
	
	/** Call named method on {@link this} with 1 argument, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return all return values as a {@link Varargs} instance.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #invoke(Varargs)} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @param args Array of {@link LuaValue} containing arguments to supply to the called function after {@code this} 
	 * @return All values returned from {@code this:name(args)} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke(Varargs)
	 * @see #method(String)
	 * @see #invokemethod(LuaValue,LuaValue[])
	 * @see #invokemethod(String,Varargs)
	 * @see LuaValue#varargsOf(LuaValue[])
	 */
	public Varargs invokemethod(String name, LuaValue[] args) { return get(name).invoke(varargsOf(this,varargsOf(args))); }
	
	/** Call named method on {@link this} with variable arguments, including metatag processing, 
	 * and retain all return values in a {@link Varargs}.
	 * <p>
	 * Look up {@code this[name]} and if it is a {@link LuaFunction}, 
	 * call it inserting {@link this} as an additional first argument, 
	 * and return all return values as a {@link Varargs} instance.  
	 * Otherwise, look for the {@link CALL} metatag and call that. 
	 * <p>
	 * To get a particular return value, us {@link Varargs#arg(int)} 
	 * <p>
	 * To call {@link this} as a plain call, use {@link #invoke(Varargs)} instead.
	 *  
	 * @param name Name of the method to look up for invocation
	 * @param args Array of {@link LuaValue} containing arguments to supply to the called function after {@code this} 
	 * @return All values returned from {@code this:name(args)} as a {@link Varargs} instance
	 * @throws LuaError if not a function and {@link CALL} is not defined, 
	 * or the invoked function throws a {@link LuaError} 
	 * or the invoked closure throw a lua {@code error}
	 * @see #call()
	 * @see #invoke(Varargs)
	 * @see #method(String)
	 * @see #invokemethod(String,LuaValue[])
	 * @see #invokemethod(LuaValue,Varargs)
	 * @see LuaValue#varargsOf(LuaValue[])
	 */
	public Varargs invokemethod(LuaValue name, LuaValue[] args) { return get(name).invoke(varargsOf(this,varargsOf(args))); }
	
	/**
	 * Get the metatag value for the {@link CALL} metatag, if it exists.
	 * @return {@link LuaValue} value if metatag is defined
	 * @throws LuaError if {@link CALL} metatag is not defined. 
	 */
	protected LuaValue callmt() {
		return checkmetatag(CALL, "attempt to call ");
	}
	
	/** Unary not: return inverse boolean value {@code (~this)} as defined by lua not operator  
	 * @return {@link TRUE} if {@link NIL} or {@link FALSE}, otherwise {@link FALSE} 
	 */
	public LuaValue not()  { return FALSE;  }
	
	/** Unary minus: return negative value {@code (-this)} as defined by lua unary minus operator  
	 * @return boolean inverse as {@link LuaBoolean} if boolean or nil, 
	 * numeric inverse as {@LuaNumber} if numeric,  
	 * or metatag processing result if {@link UNM} metatag is defined
	 * @throws LuaError if  {@code this} is not a table or string, and has no {@link UNM} metatag
	 */
	public LuaValue neg()  { return checkmetatag(UNM, "attempt to perform arithmetic on ").call(this);  }
	
	/** Length operator: return lua length of object {@code (#this)} including metatag processing as java int  
	 * @return length as defined by the lua # operator
	 * or metatag processing result
	 * @throws LuaError if  {@code this} is not a table or string, and has no {@link LEN} metatag
	 */
	public LuaValue len()  { return checkmetatag(LEN, "attempt to get length of ").call(this);  }

	/** Length operator: return lua length of object {@code (#this)} including metatag processing as java int  
	 * @return length as defined by the lua # operator
	 * or metatag processing result converted to java int using {@link #toint()}
	 * @throws LuaError if  {@code this} is not a table or string, and has no {@link LEN} metatag
	 */
	public int length()    { return len().toint(); }
	
	/** Get raw length of table or string without metatag processing.
	 * @return the length of the table or string.
	 * @throws LuaError if {@code this} is not a table or string.
	 */
	public int rawlen() { typerror("table or string"); return 0; }
	
	// object equality, used for key comparison
	public boolean equals(Object obj)         { return this == obj; } 
	
	/** Equals: Perform equality comparison with another value 
	 * including metatag processing using {@link EQ}.
	 * @param val The value to compare with.
	 * @return  {@link TRUE} if values are comparable and {@code (this == rhs)}, 
	 * {@link FALSE} if comparable but not equal, 
	 * {@link LuaValue} if metatag processing occurs.  
	 * @see #eq_b(LuaValue)
	 * @see #raweq(LuaValue)
	 * @see #neq(LuaValue)
	 * @see #eqmtcall(LuaValue, LuaValue, LuaValue, LuaValue)
	 * @see #EQ
	 */
	public LuaValue   eq( LuaValue val )      { return this == val? TRUE: FALSE; }
	
	/** Equals: Perform equality comparison with another value 
	 * including metatag processing using {@link EQ}, 
	 * and return java boolean
	 * @param val The value to compare with.
	 * @return  true if values are comparable and {@code (this == rhs)}, 
	 * false if comparable but not equal, 
	 * result converted to java boolean if metatag processing occurs.  
	 * @see #eq(LuaValue)
	 * @see #raweq(LuaValue)
	 * @see #neq_b(LuaValue)
	 * @see #eqmtcall(LuaValue, LuaValue, LuaValue, LuaValue)
	 * @see #EQ
	 */
	public boolean  eq_b( LuaValue val )      { return this == val; } 

	/** Notquals: Perform inequality comparison with another value 
	 * including metatag processing using {@link EQ}.
	 * @param val The value to compare with.
	 * @return  {@link TRUE} if values are comparable and {@code (this != rhs)}, 
	 * {@link FALSE} if comparable but equal, 
	 * inverse of {@link LuaValue} converted to {@link LuaBoolean} if metatag processing occurs.  
	 * @see #eq(LuaValue)
	 * @see #raweq(LuaValue)
	 * @see #eqmtcall(LuaValue, LuaValue, LuaValue, LuaValue)
	 * @see #EQ
	 */
	public LuaValue  neq( LuaValue val )      { return  eq_b(val)? FALSE: TRUE;  }

	/** Notquals: Perform inequality comparison with another value 
	 * including metatag processing using {@link EQ}.
	 * @param val The value to compare with.
	 * @return  true if values are comparable and {@code (this != rhs)}, 
	 * false if comparable but equal, 
	 * inverse of result converted to boolean if metatag processing occurs.  
	 * @see #eq_b(LuaValue)
	 * @see #raweq(LuaValue)
	 * @see #eqmtcall(LuaValue, LuaValue, LuaValue, LuaValue)
	 * @see #EQ
	 */
	public boolean neq_b( LuaValue val )      { return !eq_b(val); }

	/** Equals: Perform direct equality comparison with another value 
	 * without metatag processing.
	 * @param val The value to compare with.
	 * @return  true if {@code (this == rhs)}, false otherwise  
	 * @see #eq(LuaValue)
	 * @see #raweq(LuaUserdata)
	 * @see #raweq(LuaString)
	 * @see #raweq(double)
	 * @see #raweq(int)
	 * @see #EQ
	 */
	public boolean raweq( LuaValue val )      { return this == val; }
	
	/** Equals: Perform direct equality comparison with a {@link LuaUserdata} value 
	 * without metatag processing.
	 * @param val The {@link LuaUserdata} to compare with.
	 * @return  true if {@code this} is userdata 
	 * and their metatables are the same using ==  
	 * and their instances are equal using {@link #equals(Object)},
	 * otherwise false  
	 * @see #eq(LuaValue)
	 * @see #raweq(LuaValue)
	 */
	public boolean raweq( LuaUserdata val )   { return false; }

	/** Equals: Perform direct equality comparison with a {@link LuaString} value 
	 * without metatag processing.
	 * @param val The {@link LuaString} to compare with.
	 * @return  true if {@code this} is a {@link LuaString} 
	 * and their byte sequences match,
	 * otherwise false  
	 */
	public boolean raweq( LuaString val )     { return false; }

	/** Equals: Perform direct equality comparison with a double value 
	 * without metatag processing.
	 * @param val The double value to compare with.
	 * @return  true if {@code this} is a {@link LuaNumber} 
	 * whose value equals val,
	 * otherwise false  
	 */
	public boolean raweq( double val )        { return false; }

	/** Equals: Perform direct equality comparison with a int value 
	 * without metatag processing.
	 * @param val The double value to compare with.
	 * @return  true if {@code this} is a {@link LuaNumber} 
	 * whose value equals val,
	 * otherwise false  
	 */
	public boolean raweq( int val )           { return false; }

	/** Perform equality testing metatag processing 
	 * @param lhs left-hand-side of equality expression
	 * @param lhsmt metatag value for left-hand-side
	 * @param rhs right-hand-side of equality expression 
	 * @param rhsmt metatag value for right-hand-side
	 * @return true if metatag processing result is not {@link NIL} or {@link FALSE}
	 * @throws LuaError if metatag was not defined for either operand 
	 * @see #equals(Object)
	 * @see #eq(LuaValue)
	 * @see #raweq(LuaValue)
	 * @see #EQ
	 */
	public static final boolean eqmtcall(LuaValue lhs, LuaValue lhsmt, LuaValue rhs, LuaValue rhsmt) {
		LuaValue h = lhsmt.rawget(EQ);
		return h.isnil() || h!=rhsmt.rawget(EQ)? false: h.call(lhs,rhs).toboolean();
	}
	
	/** Add: Perform numeric add operation with another value 
	 * including metatag processing.
	 * <p>
	 * Each operand must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the add with
	 * @return  value of {@code (this + rhs)} if both are numeric,  
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either operand is not a number or string convertible to number,  
	 * and neither has the {@link ADD} metatag defined 
	 * @see #arithmt(LuaValue, LuaValue)
	 */
	public LuaValue   add( LuaValue rhs )        { return arithmt(ADD,rhs); }
	
	/** Add: Perform numeric add operation with another value 
	 * of double type with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the add with
	 * @return  value of {@code (this + rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #add(LuaValue)
	 */
	public LuaValue   add(double rhs)         { return arithmtwith(ADD,rhs); }
	
	/** Add: Perform numeric add operation with another value 
	 * of int type with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the add with
	 * @return  value of {@code (this + rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #add(LuaValue)
	 */
	public LuaValue   add(int rhs)            { return add((double)rhs); }
	
	/** Subtract: Perform numeric subtract operation with another value 
	 * of unknown type, 
	 * including metatag processing.
	 * <p>
	 * Each operand must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the subtract with
	 * @return  value of {@code (this - rhs)} if both are numeric,  
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either operand is not a number or string convertible to number,  
	 * and neither has the {@link SUB} metatag defined 
	 * @see #arithmt(LuaValue, LuaValue)
	 */
	public LuaValue   sub( LuaValue rhs )        { return arithmt(SUB,rhs); }
	
	/** Subtract: Perform numeric subtract operation with another value 
	 * of double type with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the subtract with
	 * @return  value of {@code (this - rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #sub(LuaValue)
	 */
	public LuaValue   sub( double rhs )        { return aritherror("sub"); }
	
	/** Subtract: Perform numeric subtract operation with another value 
	 * of int type with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the subtract with
	 * @return  value of {@code (this - rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #sub(LuaValue)
	 */
	public LuaValue   sub( int rhs )        { return aritherror("sub"); }
	
	/** Reverse-subtract: Perform numeric subtract operation from an int value 
	 * with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param lhs The left-hand-side value from which to perform the subtraction
	 * @return  value of {@code (lhs - this)} if this is numeric 
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #sub(LuaValue)
	 * @see #sub(double)
	 * @see #sub(int)
	 */
	public LuaValue   subFrom(double lhs)     { return arithmtwith(SUB,lhs); }
	
	/** Reverse-subtract: Perform numeric subtract operation from a double value 
	 * without metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * <p>
	 * For metatag processing {@link #sub(LuaValue)} must be used
	 * 
	 * @param lhs The left-hand-side value from which to perform the subtraction
	 * @return  value of {@code (lhs - this)} if this is numeric 
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #sub(LuaValue)
	 * @see #sub(double)
	 * @see #sub(int)
	 */
	public LuaValue   subFrom(int lhs)        { return subFrom((double)lhs); }
	
	/** Multiply: Perform numeric multiply operation with another value 
	 * of unknown type, 
	 * including metatag processing.
	 * <p>
	 * Each operand must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the multiply with
	 * @return  value of {@code (this * rhs)} if both are numeric,  
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either operand is not a number or string convertible to number,  
	 * and neither has the {@link MUL} metatag defined 
	 * @see #arithmt(LuaValue, LuaValue)
	 */
	public LuaValue   mul( LuaValue rhs )        { return arithmt(MUL,rhs); }
	
	/** Multiply: Perform numeric multiply operation with another value 
	 * of double type with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the multiply with
	 * @return  value of {@code (this * rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #mul(LuaValue)
	 */
	public LuaValue   mul(double rhs)         { return arithmtwith(MUL,rhs); }
	
	/** Multiply: Perform numeric multiply operation with another value 
	 * of int type with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the multiply with
	 * @return  value of {@code (this * rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #mul(LuaValue)
	 */
	public LuaValue   mul(int rhs)            { return mul((double)rhs); }
	
	/** Raise to power: Raise this value to a power  
	 * including metatag processing.
	 * <p>
	 * Each operand must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The power to raise this value to
	 * @return  value of {@code (this ^ rhs)} if both are numeric,  
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either operand is not a number or string convertible to number,  
	 * and neither has the {@link POW} metatag defined 
	 * @see #arithmt(LuaValue, LuaValue)
	 */
	public LuaValue   pow( LuaValue rhs )        { return arithmt(POW,rhs); }
	
	/** Raise to power: Raise this value to a power 
	 * of double type with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The power to raise this value to
	 * @return  value of {@code (this ^ rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #pow(LuaValue)
	 */
	public LuaValue   pow( double rhs )        { return aritherror("pow"); }
	
	/** Raise to power: Raise this value to a power 
	 * of int type with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The power to raise this value to
	 * @return  value of {@code (this ^ rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #pow(LuaValue)
	 */
	public LuaValue   pow( int rhs )        { return aritherror("pow"); }
	
	/** Reverse-raise to power: Raise another value of double type to this power 
	 * with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param lhs The left-hand-side value which will be raised to this power
	 * @return  value of {@code (lhs ^ this)} if this is numeric 
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #pow(LuaValue)
	 * @see #pow(double)
	 * @see #pow(int)
	 */
	public LuaValue   powWith(double lhs)     { return arithmtwith(POW,lhs); }
	
	/** Reverse-raise to power: Raise another value of double type to this power 
	 * with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param lhs The left-hand-side value which will be raised to this power
	 * @return  value of {@code (lhs ^ this)} if this is numeric 
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #pow(LuaValue)
	 * @see #pow(double)
	 * @see #pow(int)
	 */
	public LuaValue   powWith(int lhs)        { return powWith((double)lhs); }
	
	/** Divide: Perform numeric divide operation by another value 
	 * of unknown type, 
	 * including metatag processing.
	 * <p>
	 * Each operand must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the divulo with
	 * @return  value of {@code (this / rhs)} if both are numeric,  
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either operand is not a number or string convertible to number,  
	 * and neither has the {@link DIV} metatag defined 
	 * @see #arithmt(LuaValue, LuaValue)
	 */
	public LuaValue   div( LuaValue rhs )        { return arithmt(DIV,rhs); }
	
	/** Divide: Perform numeric divide operation by another value 
	 * of double type without metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * <p>
	 * For metatag processing {@link #div(LuaValue)} must be used
	 * 
	 * @param rhs The right-hand-side value to perform the divulo with
	 * @return  value of {@code (this / rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #div(LuaValue)
	 */
	public LuaValue   div( double rhs )        { return aritherror("div"); }
	
	/** Divide: Perform numeric divide operation by another value 
	 * of int type without metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * <p>
	 * For metatag processing {@link #div(LuaValue)} must be used
	 * 
	 * @param rhs The right-hand-side value to perform the divulo with
	 * @return  value of {@code (this / rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #div(LuaValue)
	 */
	public LuaValue   div( int rhs )        { return aritherror("div"); }
	
	/** Reverse-divide: Perform numeric divide operation into another value 
	 * with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param lhs The left-hand-side value which will be divided by this
	 * @return  value of {@code (lhs / this)} if this is numeric 
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #div(LuaValue)
	 * @see #div(double)
	 * @see #div(int)
	 */
	public LuaValue   divInto(double lhs)     { return arithmtwith(DIV,lhs); }
	
	/** Modulo: Perform numeric modulo operation with another value 
	 * of unknown type, 
	 * including metatag processing.
	 * <p>
	 * Each operand must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param rhs The right-hand-side value to perform the modulo with
	 * @return  value of {@code (this % rhs)} if both are numeric,  
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either operand is not a number or string convertible to number,  
	 * and neither has the {@link MOD} metatag defined 
	 * @see #arithmt(LuaValue, LuaValue)
	 */
	public LuaValue   mod( LuaValue rhs )        { return arithmt(MOD,rhs); }
	
	/** Modulo: Perform numeric modulo operation with another value 
	 * of double type without metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * <p>
	 * For metatag processing {@link #mod(LuaValue)} must be used
	 * 
	 * @param rhs The right-hand-side value to perform the modulo with
	 * @return  value of {@code (this % rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #mod(LuaValue)
	 */
	public LuaValue   mod( double rhs )        { return aritherror("mod"); }
	
	/** Modulo: Perform numeric modulo operation with another value 
	 * of int type without metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * <p>
	 * For metatag processing {@link #mod(LuaValue)} must be used
	 * 
	 * @param rhs The right-hand-side value to perform the modulo with
	 * @return  value of {@code (this % rhs)} if this is numeric  
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #mod(LuaValue)
	 */
	public LuaValue   mod( int rhs )        { return aritherror("mod"); }
	
	/** Reverse-modulo: Perform numeric modulo operation from another value 
	 * with metatag processing
	 * <p>
	 * {@code this} must derive from {@link LuaNumber}
	 * or derive from {@link LuaString} and be convertible to a number
	 * 
	 * @param lhs The left-hand-side value which will be modulo'ed by this
	 * @return  value of {@code (lhs % this)} if this is numeric 
	 * @throws LuaError if {@code this} is not a number or string convertible to number  
	 * @see #mod(LuaValue)
	 * @see #mod(double)
	 * @see #mod(int)
	 */
	public LuaValue   modFrom(double lhs)     { return arithmtwith(MOD,lhs); }
	
	/** Perform metatag processing for arithmetic operations. 
	 * <p>
	 * Finds the supplied metatag value for {@code this} or {@code op2} and invokes it, 
	 * or throws {@link LuaError} if neither is defined. 
	 * @param tag The metatag to look up
	 * @param op2 The other operand value to perform the operation with
	 * @return {@link LuaValue} resulting from metatag processing
	 * @throws LuaError if metatag was not defined for either operand 
	 * @see #add(LuaValue)
	 * @see #sub(LuaValue)
	 * @see #mul(LuaValue)
	 * @see #pow(LuaValue)
	 * @see #div(LuaValue)
	 * @see #mod(LuaValue)
	 * @see #ADD
	 * @see #SUB
	 * @see #MUL
	 * @see #POW
	 * @see #DIV
	 * @see #MOD
	 */
	protected LuaValue arithmt(LuaValue tag, LuaValue op2) {
		LuaValue h = this.metatag(tag);
		if ( h.isnil() ) {
			h = op2.metatag(tag);
			if ( h.isnil() )
				error( "attempt to perform arithmetic "+tag+" on "+typename()+" and "+op2.typename() );
		}
		return h.call( this, op2 );
	}
	
	/** Perform metatag processing for arithmetic operations when the left-hand-side is a number. 
	 * <p>
	 * Finds the supplied metatag value for {@code this} and invokes it, 
	 * or throws {@link LuaError} if neither is defined. 
	 * @param tag The metatag to look up
	 * @param op1 The value of the left-hand-side to perform the operation with
	 * @return {@link LuaValue} resulting from metatag processing
	 * @throws LuaError if metatag was not defined for either operand 
	 * @see #add(LuaValue)
	 * @see #sub(LuaValue)
	 * @see #mul(LuaValue)
	 * @see #pow(LuaValue)
	 * @see #div(LuaValue)
	 * @see #mod(LuaValue)
	 * @see #ADD
	 * @see #SUB
	 * @see #MUL
	 * @see #POW
	 * @see #DIV
	 * @see #MOD
	 */
	protected LuaValue arithmtwith(LuaValue tag, double op1) {
		LuaValue h = metatag(tag);
		if ( h.isnil() )
			error( "attempt to perform arithmetic "+tag+" on number and "+typename() );
		return h.call( LuaValue.valueOf(op1), this );
	}
	
	/** Less than: Perform numeric or string comparison with another value 
	 * of unknown type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this < rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LT} metatag is defined.
	 * @see #gteq_b(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   lt( LuaValue rhs )         { return comparemt(LT,rhs); }

	/** Less than: Perform numeric comparison with another value 
	 * of double type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this < rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if this is not a number
	 * and no {@link LT} metatag is defined.
	 * @see #gteq_b(double)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   lt( double rhs )         { return compareerror("number"); }

	/** Less than: Perform numeric comparison with another value 
	 * of int type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this < rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if this is not a number
	 * and no {@link LT} metatag is defined.
	 * @see #gteq_b(int)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   lt( int rhs )         { return compareerror("number"); }

	/** Less than: Perform numeric or string comparison with another value 
	 * of unknown type, including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this < rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LT} metatag is defined.
	 * @see #gteq(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean lt_b( LuaValue rhs )     { return comparemt(LT,rhs).toboolean(); }

	/** Less than: Perform numeric comparison with another value 
	 * of int type, 
	 * including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this < rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if this is not a number
	 * and no {@link LT} metatag is defined.
	 * @see #gteq(int)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean lt_b( int rhs )         { compareerror("number"); return false; }

	/** Less than: Perform numeric or string comparison with another value 
	 * of unknown type, including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this < rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LT} metatag is defined.
	 * @see #gteq(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean lt_b( double rhs )      { compareerror("number"); return false; }

	/** Less than or equals: Perform numeric or string comparison with another value 
	 * of unknown type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this <= rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LE} metatag is defined.
	 * @see #gteq_b(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   lteq( LuaValue rhs )       { return comparemt(LE,rhs); }

	/** Less than or equals: Perform numeric comparison with another value 
	 * of double type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this <= rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if this is not a number
	 * and no {@link LE} metatag is defined.
	 * @see #gteq_b(double)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   lteq( double rhs )       { return compareerror("number"); }

	/** Less than or equals: Perform numeric comparison with another value 
	 * of int type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this <= rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if this is not a number
	 * and no {@link LE} metatag is defined.
	 * @see #gteq_b(int)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   lteq( int rhs )       { return compareerror("number"); }

	/** Less than or equals: Perform numeric or string comparison with another value 
	 * of unknown type, including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this <= rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LE} metatag is defined.
	 * @see #gteq(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean lteq_b( LuaValue rhs )     { return comparemt(LE,rhs).toboolean(); }

	/** Less than or equals: Perform numeric comparison with another value 
	 * of int type, 
	 * including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this <= rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if this is not a number
	 * and no {@link LE} metatag is defined.
	 * @see #gteq(int)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean lteq_b( int rhs )       { compareerror("number"); return false; }

	/** Less than or equals: Perform numeric comparison with another value 
	 * of double type, 
	 * including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this <= rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if this is not a number
	 * and no {@link LE} metatag is defined.
	 * @see #gteq(double)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean lteq_b( double rhs )    { compareerror("number"); return false; }

	/** Greater than: Perform numeric or string comparison with another value 
	 * of unknown type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this > rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LE} metatag is defined.
	 * @see #gteq_b(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   gt( LuaValue rhs )         { return rhs.comparemt(LE,this); }

	/** Greater than: Perform numeric comparison with another value 
	 * of double type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this > rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if this is not a number
	 * and no {@link LE} metatag is defined.
	 * @see #gteq_b(double)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   gt( double rhs )         { return compareerror("number"); }

	/** Greater than: Perform numeric comparison with another value 
	 * of int type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this > rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if this is not a number
	 * and no {@link LE} metatag is defined.
	 * @see #gteq_b(int)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   gt( int rhs )         { return compareerror("number"); }

	/** Greater than: Perform numeric or string comparison with another value 
	 * of unknown type, including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this > rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LE} metatag is defined.
	 * @see #gteq(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean gt_b( LuaValue rhs )       { return rhs.comparemt(LE,this).toboolean(); }

	/** Greater than: Perform numeric comparison with another value 
	 * of int type, 
	 * including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this > rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if this is not a number
	 * and no {@link LE} metatag is defined.
	 * @see #gteq(int)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean gt_b( int rhs )         { compareerror("number"); return false; }

	/** Greater than: Perform numeric or string comparison with another value 
	 * of unknown type, including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this > rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LE} metatag is defined.
	 * @see #gteq(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean gt_b( double rhs )      { compareerror("number"); return false; }

	/** Greater than or equals: Perform numeric or string comparison with another value 
	 * of unknown type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this >= rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LT} metatag is defined.
	 * @see #gteq_b(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   gteq( LuaValue rhs )       { return rhs.comparemt(LT,this); }

	/** Greater than or equals: Perform numeric comparison with another value 
	 * of double type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this >= rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if this is not a number
	 * and no {@link LT} metatag is defined.
	 * @see #gteq_b(double)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   gteq( double rhs )         { return compareerror("number"); }

	/** Greater than or equals: Perform numeric comparison with another value 
	 * of int type, 
	 * including metatag processing, and returning {@link LuaValue}.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  {@link TRUE} if {@code (this >= rhs)}, {@link FALSE} if not, 
	 * or {@link LuaValue} if metatag processing occurs  
	 * @throws LuaError if this is not a number
	 * and no {@link LT} metatag is defined.
	 * @see #gteq_b(int)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public LuaValue   gteq( int rhs )         { return valueOf(todouble() >= rhs); }

	/** Greater than or equals: Perform numeric or string comparison with another value 
	 * of unknown type, including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, both operands must derive from {@link LuaString}
	 * or both must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this >= rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if either both operands are not a strings or both are not numbers
	 * and no {@link LT} metatag is defined.
	 * @see #gteq(LuaValue)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean gteq_b( LuaValue rhs )     { return rhs.comparemt(LT,this).toboolean(); }

	/** Greater than or equals: Perform numeric comparison with another value 
	 * of int type, 
	 * including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this >= rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if this is not a number
	 * and no {@link LT} metatag is defined.
	 * @see #gteq(int)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean gteq_b( int rhs )       { compareerror("number"); return false; }

	/** Greater than or equals: Perform numeric comparison with another value 
	 * of double type, 
	 * including metatag processing, 
	 * and returning java boolean.
	 * <p>
	 * To be comparable, this must derive from {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @return  true if {@code (this >= rhs)}, false if not, 
	 * and boolean interpreation of result if metatag processing occurs.
	 * @throws LuaError if this is not a number
	 * and no {@link LT} metatag is defined.
	 * @see #gteq(double)
	 * @see #comparemt(LuaValue, LuaValue)
	 */
	public boolean gteq_b( double rhs )    { compareerror("number"); return false; }
	
	/** Perform metatag processing for comparison operations. 
	 * <p>
	 * Finds the supplied metatag value and invokes it, 
	 * or throws {@link LuaError} if none applies. 
	 * @param tag The metatag to look up
	 * @param rhs The right-hand-side value to perform the operation with
	 * @return {@link LuaValue} resulting from metatag processing
	 * @throws LuaError if metatag was not defined for either operand, 
	 * or if the operands are not the same type, 
	 * or the metatag values for the two operands are different. 
	 * @see #gt(LuaValue)
	 * @see #gteq(LuaValue)
	 * @see #lt(LuaValue)
	 * @see #lteq(LuaValue)
	 */
	public LuaValue comparemt( LuaValue tag, LuaValue op1 ) { 
		LuaValue h = metatag(tag);
		if ( !h.isnil() )
			return h.call(this, op1);
		h = op1.metatag(tag);
		if ( !h.isnil() )
			return h.call(this, op1);
		return error("attempt to compare "+tag+" on "+typename()+" and "+op1.typename());
	}
	
	/** Perform string comparison with another value 
	 * of any type 
	 * using string comparison based on byte values.
	 * <p>
	 * Only strings can be compared, meaning 
	 * each operand must derive from {@link LuaString}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @returns  int < 0 for {@code (this < rhs)}, int > 0 for {@code (this > rhs)}, or 0 when same string.  
	 * @throws LuaError if either operand is not a string
	 */
	public int strcmp( LuaValue rhs )         { error("attempt to compare "+typename()); return 0; }

	/** Perform string comparison with another value 
	 * known to be a {@link LuaString} 
	 * using string comparison based on byte values.
	 * <p>
	 * Only strings can be compared, meaning 
	 * each operand must derive from {@link LuaString}. 
	 * 
	 * @param rhs The right-hand-side value to perform the comparison with
	 * @returns  int < 0 for {@code (this < rhs)}, int > 0 for {@code (this > rhs)}, or 0 when same string.  
	 * @throws LuaError if this is not a string
	 */
	public int strcmp( LuaString rhs )      { error("attempt to compare "+typename()); return 0; }

	/** Concatenate another value onto this value and return the result 
	 * using rules of lua string concatenation including metatag processing.
	 * <p>
	 * Only strings and numbers as represented can be concatenated, meaning 
	 * each operand must derive from {@link LuaString} or {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side value to perform the operation with
	 * @returns  {@link LuaValue} resulting from concatenation of {@code (this .. rhs)}
	 * @throws LuaError if either operand is not of an appropriate type, 
	 * such as nil or a table
	 */
	public LuaValue concat(LuaValue rhs)      { return this.concatmt(rhs); }

	/** Reverse-concatenation: concatenate this value onto another value 
	 * whose type is unknwon 
	 * and return the result using rules of lua string concatenation including 
	 * metatag processing.  
	 * <p>
	 * Only strings and numbers as represented can be concatenated, meaning 
	 * each operand must derive from {@link LuaString} or {@link LuaNumber}. 
	 * 
	 * @param lhs The left-hand-side value onto which this will be concatenated
	 * @returns  {@link LuaValue} resulting from concatenation of {@code (lhs .. this)}
	 * @throws LuaError if either operand is not of an appropriate type, 
	 * such as nil or a table
	 * @see #concat(LuaValue)
	 */
	public LuaValue concatTo(LuaValue lhs)    { return lhs.concatmt(this); }

	/** Reverse-concatenation: concatenate this value onto another value 
	 * known to be a {@link  LuaNumber} 
	 * and return the result using rules of lua string concatenation including 
	 * metatag processing.  
	 * <p>
	 * Only strings and numbers as represented can be concatenated, meaning 
	 * each operand must derive from {@link LuaString} or {@link LuaNumber}. 
	 * 
	 * @param lhs The left-hand-side value onto which this will be concatenated
	 * @returns  {@link LuaValue} resulting from concatenation of {@code (lhs .. this)}
	 * @throws LuaError if either operand is not of an appropriate type, 
	 * such as nil or a table
	 * @see #concat(LuaValue)
	 */
	public LuaValue concatTo(LuaNumber lhs)   { return lhs.concatmt(this); }
	
	/** Reverse-concatenation: concatenate this value onto another value 
	 * known to be a {@link  LuaString} 
	 * and return the result using rules of lua string concatenation including 
	 * metatag processing.  
	 * <p>
	 * Only strings and numbers as represented can be concatenated, meaning 
	 * each operand must derive from {@link LuaString} or {@link LuaNumber}. 
	 * 
	 * @param lhs The left-hand-side value onto which this will be concatenated
	 * @returns  {@link LuaValue} resulting from concatenation of {@code (lhs .. this)}
	 * @throws LuaError if either operand is not of an appropriate type, 
	 * such as nil or a table
	 * @see #concat(LuaValue)
	 */
	public LuaValue concatTo(LuaString lhs)   { return lhs.concatmt(this); }

	/** Convert the value to a {@link Buffer} for more efficient concatenation of 
	 * multiple strings. 
	 * @return Buffer instance containing the string or number
	 */
	public Buffer   buffer()                  { return new Buffer(this); }
	
	/** Concatenate a {@link Buffer} onto this value and return the result 
	 * using rules of lua string concatenation including metatag processing.
	 * <p>
	 * Only strings and numbers as represented can be concatenated, meaning 
	 * each operand must derive from {@link LuaString} or {@link LuaNumber}. 
	 * 
	 * @param rhs The right-hand-side {@link Buffer} to perform the operation with
	 * @return LuaString resulting from concatenation of {@code (this .. rhs)}
	 * @throws LuaError if either operand is not of an appropriate type, 
	 * such as nil or a table
	 */
	public Buffer   concat(Buffer rhs)        { return rhs.concatTo(this); }
	
	/** Perform metatag processing for concatenation operations. 
	 * <p>
	 * Finds the {@link CONCAT} metatag value and invokes it, 
	 * or throws {@link LuaError} if it doesn't exist. 
	 * @param rhs The right-hand-side value to perform the operation with
	 * @return {@link LuaValue} resulting from metatag processing for {@link CONCAT} metatag.
	 * @throws LuaError if metatag was not defined for either operand
	 */
	public LuaValue concatmt(LuaValue rhs) {
		LuaValue h=metatag(CONCAT);
		if ( h.isnil() && (h=rhs.metatag(CONCAT)).isnil())
			error("attempt to concatenate "+typename()+" and "+rhs.typename());
		return h.call(this,rhs);
	}
	
	/** Perform boolean {@code and} with another operand, based on lua rules for boolean evaluation.
	 * This returns either {@code this} or {@code rhs} depending on the boolean value for {@code this}.
	 * 
	 * @param rhs The right-hand-side value to perform the operation with
	 * @return {@code this} if {@code this.toboolean()} is false, {@code rhs} otherwise.
	 */
	public LuaValue   and( LuaValue rhs )      { return this.toboolean()? rhs: this; }
	
	/** Perform boolean {@code or} with another operand, based on lua rules for boolean evaluation.
	 * This returns either {@code this} or {@code rhs} depending on the boolean value for {@code this}.
	 * 
	 * @param rhs The right-hand-side value to perform the operation with
	 * @return {@code this} if {@code this.toboolean()} is true, {@code rhs} otherwise. 
	 */
	public LuaValue   or( LuaValue rhs )       { return this.toboolean()? this: rhs; }
	
	/** Perform end-condition test in for-loop processing. 
	 * <p>
	 * Used in lua-bytecode to Java-bytecode conversion.
	 * 
	 * @param limit the numerical limit to complete the for loop
	 * @param step the numberical step size to use. 
	 * @return true if limit has not been reached, false otherwise.
	 */
	public boolean testfor_b(LuaValue limit, LuaValue step) { return step.gt_b(0)? lteq_b(limit): gteq_b(limit); }
	
	/**
	 * Convert this value to a string if it is a {@link LuaString} or {@link LuaNumber}, 
	 * or throw a {@link LuaError} if it is not
	 * @return {@link LuaString} corresponding to the value if a string or number
	 * @throws LuaError if not a string or number
	 */
	public LuaString strvalue()     { typerror("strValue"); return null; }

	/** Return this value as a strong reference, or null if it was weak and is no longer referenced.
	 * @return {@link LuaValue} referred to, or null if it was weak and is no longer referenced.
	 * @see WeakTable
	 */
	public LuaValue  strongvalue()  { return this; }

	/** Convert java boolean to a {@link LuaValue}.
	 * 
	 * @param b boolean value to convert
	 * @return {@link TRUE} if not  or {@link FALSE} if false
	 */
	public static LuaBoolean  valueOf(boolean b)    { return b? LuaValue.TRUE: FALSE; };

	/** Convert java int to a {@link LuaValue}.
	 * 
	 * @param i int value to convert
	 * @return {@link LuaInteger} instance, possibly pooled, whose value is i
	 */
	public static LuaInteger  valueOf(int i)        { return LuaInteger.valueOf(i); }
	
	/** Convert java double to a {@link LuaValue}.  
	 * This may return a {@link LuaInteger} or {@link LuaDouble} depending 
	 * on the value supplied.  
	 * 
	 * @param d double value to convert
	 * @return {@link LuaNumber} instance, possibly pooled, whose value is d
	 */
	public static LuaNumber   valueOf(double d)     { return LuaDouble.valueOf(d); };
	
	/** Convert java string to a {@link LuaValue}.
	 * 
	 * @param s String value to convert
	 * @return {@link LuaString} instance, possibly pooled, whose value is s
	 */
	public static LuaString valueOf(String s)     { return LuaString.valueOf(s); }

	/** Convert bytes in an array to a {@link LuaValue}.
	 * 
	 * @param bytes byte array to convert
	 * @return {@link LuaString} instance, possibly pooled, whose bytes are those in the supplied array
	 */
	public static LuaString valueOf(byte[] bytes) { return LuaString.valueOf(bytes); }
	
	/** Convert bytes in an array to a {@link LuaValue}.
	 * 
	 * @param bytes byte array to convert
	 * @param off offset into the byte array, starting at 0
	 * @param len number of bytes to include in the {@link LuaString}
	 * @return {@link LuaString} instance, possibly pooled, whose bytes are those in the supplied array
	 */
	public static LuaString valueOf(byte[] bytes, int off, int len) { 
		return LuaString.valueOf(bytes,off,len); 
	}
	
	/** Construct an empty {@link LuaTable}. 
	 * @return new {@link LuaTable} instance with no values and no metatable. 
	 */
	public static LuaTable tableOf() { return new LuaTable(); }

	/** Construct a {@link LuaTable} initialized with supplied array values. 
	 * @param varargs {@link Varargs} containing the values to use in initialization
	 * @param firstarg the index of the first argument to use from the varargs, 1 being the first.
	 * @return new {@link LuaTable} instance with sequential elements coming from the varargs. 
	 */
	public static LuaTable tableOf(Varargs varargs, int firstarg) { return new LuaTable(varargs,firstarg); }
	
	/** Construct an empty {@link LuaTable} preallocated to hold array and hashed elements 
	 * @param narray Number of array elements to preallocate
	 * @param nhash Number of hash elements to preallocate
	 * @return new {@link LuaTable} instance with no values and no metatable, but preallocated for array and hashed elements.
	 */
	public static LuaTable tableOf(int narray, int nhash) { return new LuaTable(narray, nhash); }	
	
	/** Construct a {@link LuaTable} initialized with supplied array values. 
	 * @param unnamedValues array of {@link LuaValue} containing the values to use in initialization
	 * @return new {@link LuaTable} instance with sequential elements coming from the array. 
	 */
	public static LuaTable listOf(LuaValue[] unnamedValues) { return new LuaTable(null,unnamedValues,null); }
	
	/** Construct a {@link LuaTable} initialized with supplied array values. 
	 * @param unnamedValues array of {@link LuaValue} containing the first values to use in initialization
	 * @param lastarg {@link Varargs} containing additional values to use in initialization
	 * to be put after the last unnamedValues element
	 * @return new {@link LuaTable} instance with sequential elements coming from the array and varargs. 
	 */
	public static LuaTable listOf(LuaValue[] unnamedValues,Varargs lastarg) { return new LuaTable(null,unnamedValues,lastarg); }

	/** Construct a {@link LuaTable} initialized with supplied named values. 
	 * @param namedValues array of {@link LuaValue} containing the keys and values to use in initialization
	 * in order {@code {key-a, value-a, key-b, value-b, ...} }
	 * @return new {@link LuaTable} instance with non-sequential keys coming from the supplied array. 
	 */
	public static LuaTable tableOf(LuaValue[] namedValues) { return new LuaTable(namedValues,null,null); }	

	/** Construct a {@link LuaTable} initialized with supplied named values and sequential elements.
	 * The named values will be assigned first, and the sequential elements will be assigned later,
	 * possibly overwriting named values at the same slot if there are conflicts.  
	 * @param namedValues array of {@link LuaValue} containing the keys and values to use in initialization
	 * in order {@code {key-a, value-a, key-b, value-b, ...} }
	 * @param unnamedValues array of {@link LuaValue} containing the sequenctial elements to use in initialization
	 * in order {@code {value-1, value-2, ...} }, or null if there are none
	 * @return new {@link LuaTable} instance with named and sequential values supplied. 
	 */
	public static LuaTable tableOf(LuaValue[] namedValues, LuaValue[] unnamedValues) {return new LuaTable(namedValues,unnamedValues,null); }	

	/** Construct a {@link LuaTable} initialized with supplied named values and sequential elements in an array part and as varargs.
	 * The named values will be assigned first, and the sequential elements will be assigned later,
	 * possibly overwriting named values at the same slot if there are conflicts.  
	 * @param namedValues array of {@link LuaValue} containing the keys and values to use in initialization
	 * in order {@code {key-a, value-a, key-b, value-b, ...} }
	 * @param unnamedValues array of {@link LuaValue} containing the first sequenctial elements to use in initialization
	 * in order {@code {value-1, value-2, ...} }, or null if there are none
	 * @param lastarg {@link Varargs} containing additional values to use in the sequential part of the initialization, 
	 * to be put after the last unnamedValues element
	 * @return new {@link LuaTable} instance with named and sequential values supplied. 
	 */
	public static LuaTable tableOf(LuaValue[] namedValues, LuaValue[] unnamedValues, Varargs lastarg) {return new LuaTable(namedValues,unnamedValues,lastarg); }	
	
	/** Construct a LuaUserdata for an object. 
	 * 
	 * @param o The java instance to be wrapped as userdata
	 * @return {@link LuaUserdata} value wrapping the java instance. 
	 */
	public static LuaUserdata userdataOf(Object o) { return new LuaUserdata(o); }
	
	/** Construct a LuaUserdata for an object with a user supplied metatable. 
	 * 
	 * @param o The java instance to be wrapped as userdata
	 * @param metatable The metatble to associate with the userdata instance. 
	 * @return {@link LuaUserdata} value wrapping the java instance. 
	 */
	public static LuaUserdata userdataOf(Object o,LuaValue metatable) { return new LuaUserdata(o,metatable); }

	/** Constant limiting metatag loop processing */
	private static final int      MAXTAGLOOP = 100;
	
	/**
	 * Return value for field reference including metatag processing, or {@link LuaValue#NIL} if it doesn't exist.
	 * @param t {@link LuaValue} on which field is being referenced, typically a table or something with the metatag {@link LuaValue#INDEX} defined
	 * @param key {@link LuaValue} naming the field to reference
	 * @return {@link LuaValue} for the {@code key} if it exists, or {@link LuaValue#NIL}
	 * @throws LuaError if there is a loop in metatag processing
	 */
	/** get value from metatable operations, or NIL if not defined by metatables */
	protected static LuaValue gettable(LuaValue t, LuaValue key) {
		LuaValue tm;
		int loop = 0;
		do { 
			if (t.istable()) {
				LuaValue res = t.rawget(key);
				if ((!res.isnil()) || (tm = t.metatag(INDEX)).isnil())
					return res;
			} else if ((tm = t.metatag(INDEX)).isnil())
				t.indexerror();
			if (tm.isfunction())
				return tm.call(t, key);
			t = tm;
		}
		while ( ++loop < MAXTAGLOOP );
		error("loop in gettable");
		return NIL;
	}
	
	/**
	 * Perform field assignment including metatag processing.
	 * @param t {@link LuaValue} on which value is being set, typically a table or something with the metatag {@link LuaValue#NEWINDEX} defined
	 * @param key {@link LuaValue} naming the field to assign
	 * @param value {@link LuaValue} the new value to assign to {@code key} 
	 * @throws LuaError if there is a loop in metatag processing
	 * @return true if assignment or metatag processing succeeded, false otherwise  
	 */
	protected static boolean settable(LuaValue t, LuaValue key, LuaValue value) {
		LuaValue tm;
		int loop = 0;
		do { 
			if (t.istable()) {
				if ((!t.rawget(key).isnil()) || (tm = t.metatag(NEWINDEX)).isnil()) {
					t.rawset(key, value);
					return true;
				}
			} else if ((tm = t.metatag(NEWINDEX)).isnil())
				t.typerror("index");
			if (tm.isfunction()) {
				tm.call(t, key, value);
				return true;
			}
			t = tm;
		}
		while ( ++loop < MAXTAGLOOP );
		error("loop in settable");
		return false;
	}
	
    /**
     * Get particular metatag, or return {@link LuaValue#NIL} if it doesn't exist 
     * @param tag Metatag name to look up, typically a string such as 
     * {@link LuaValue#INDEX} or {@link LuaValue#NEWINDEX} 
     * @param reason Description of error when tag lookup fails.
     * @return {@link LuaValue} for tag {@code reason}, or  {@link LuaValue#NIL}
     */
    public LuaValue metatag(LuaValue tag) {
    	LuaValue mt = getmetatable();
    	if ( mt == null )
    		return NIL;
    	return mt.rawget(tag);
    }

    /**
     * Get particular metatag, or throw {@link LuaError} if it doesn't exist 
     * @param tag Metatag name to look up, typically a string such as 
     * {@link LuaValue#INDEX} or {@link LuaValue#NEWINDEX} 
     * @param reason Description of error when tag lookup fails.
     * @return {@link LuaValue} that can be called
     * @throws LuaError when the lookup fails.
     */
	protected LuaValue checkmetatag(LuaValue tag, String reason) {
		LuaValue h = this.metatag(tag);
		if ( h.isnil() )
			throw new LuaError(reason+typename());
		return h;
	}

	/** Construct a Metatable instance from the given LuaValue */
	protected static Metatable metatableOf(LuaValue mt) {
		if ( mt != null && mt.istable() ) {
			LuaValue mode = mt.rawget(MODE);
			if ( mode.isstring() ) {
				String m = mode.tojstring();
				boolean weakkeys = m.indexOf('k') >= 0;
				boolean weakvalues = m.indexOf('v') >= 0;
				if ( weakkeys || weakvalues ) {
					return new WeakTable(weakkeys, weakvalues, mt);
				}
			}
			return (LuaTable)mt;
		} else if ( mt != null ) {
			return new NonTableMetatable( mt );
		} else {
			return null;
		}
	}

	/** Throw {@link LuaError} indicating index was attempted on illegal type 
	 * @throws LuaError when called.  
	 */
    private void indexerror() {
		error( "attempt to index ? (a "+typename()+" value)" );
	}
 	
	/** Construct a {@link Varargs} around an array of {@link LuaValue}s.
	 * 
	 * @param v The array of {@link LuaValue}s
	 * @param more {@link Varargs} contain values to include at the end
	 * @return {@link Varargs} wrapping the supplied values. 
	 * @see LuaValue#varargsOf(LuaValue, Varargs)
	 * @see LuaValue#varargsOf(LuaValue[], int, int)
	 */
	public static Varargs varargsOf(final LuaValue[] v) {
		switch ( v.length ) {
		case 0: return NONE;
		case 1: return v[0];
		case 2: return new Varargs.PairVarargs(v[0],v[1]); 
		default: return new Varargs.ArrayVarargs(v,NONE);
		}
	}
	
	/** Construct a {@link Varargs} around an array of {@link LuaValue}s.
	 * 
	 * @param v The array of {@link LuaValue}s
	 * @param more {@link Varargs} contain values to include at the end
	 * @return {@link Varargs} wrapping the supplied values.
	 * @see LuaValue#varargsOf(LuaValue[])
	 * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs) 
	 */
	public static Varargs varargsOf(final LuaValue[] v,Varargs r) { 
		switch ( v.length ) {
		case 0: return r;
		case 1: return new Varargs.PairVarargs(v[0],r);
		default: return new Varargs.ArrayVarargs(v,r);
		}
	}

	/** Construct a {@link Varargs} around an array of {@link LuaValue}s.
	 * 
	 * @param v The array of {@link LuaValue}s
	 * @param offset number of initial values to skip in the array
	 * @param length number of values to include from the array
	 * @return {@link Varargs} wrapping the supplied values.
	 * @see LuaValue#varargsOf(LuaValue[])
	 * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
	 */
	public static Varargs varargsOf(final LuaValue[] v, final int offset, final int length) {
		switch ( length ) {
		case 0: return NONE;
		case 1: return v[offset];
		case 2: return new Varargs.PairVarargs(v[offset+0],v[offset+1]);
		default: return new Varargs.ArrayPartVarargs(v,offset,length);
		}
	}

	/** Construct a {@link Varargs} around an array of {@link LuaValue}s.
	 * 
	 * @param v The array of {@link LuaValue}s
	 * @param offset number of initial values to skip in the array
	 * @param length number of values to include from the array
	 * @param more {@link Varargs} contain values to include at the end
	 * @return {@link Varargs} wrapping the supplied values. 
	 * @see LuaValue#varargsOf(LuaValue[], Varargs)
	 * @see LuaValue#varargsOf(LuaValue[], int, int)
	 */
	public static Varargs varargsOf(final LuaValue[] v, final int offset, final int length,Varargs more) {
		switch ( length ) {
		case 0: return more;
		case 1: return new Varargs.PairVarargs(v[offset],more);
		default: return new Varargs.ArrayPartVarargs(v,offset,length,more);
		}
	}

	/** Construct a {@link Varargs} around a set of 2 or more {@link LuaValue}s.
	 * <p>
	 * This can be used to wrap exactly 2 values, or a list consisting of 1 initial value
	 * followed by another variable list of remaining values.  
	 * 
	 * @param v1 First {@link LuaValue} in the {@link Varargs}
	 * @param v2 {@link LuaValue} supplying the 2rd value, 
	 * or {@link Varargs}s supplying all values beyond the first
	 * @return {@link Varargs} wrapping the supplied values. 
	 */
	public static Varargs varargsOf(LuaValue v, Varargs r) {
		switch ( r.narg() ) {
		case 0: return v;
		default: return new Varargs.PairVarargs(v,r);
		}
	}
	
	/** Construct a {@link Varargs} around a set of 3 or more {@link LuaValue}s.
	 * <p>
	 * This can be used to wrap exactly 3 values, or a list consisting of 2 initial values
	 * followed by another variable list of remaining values.  
	 * 
	 * @param v1 First {@link LuaValue} in the {@link Varargs}
	 * @param v2 Second {@link LuaValue} in the {@link Varargs}
	 * @param v3 {@link LuaValue} supplying the 3rd value, 
	 * or {@link Varargs}s supplying all values beyond the second
	 * @return {@link Varargs} wrapping the supplied values. 
	 */
	public static Varargs varargsOf(LuaValue v1,LuaValue v2,Varargs v3) { 
		switch ( v3.narg() ) {
		case 0: return new Varargs.PairVarargs(v1,v2);
		default: return new Varargs.ArrayVarargs(new LuaValue[] {v1,v2},v3); 
		}
	}
	
	/** Construct a {@link TailcallVarargs} around a function and arguments.
	 * <p>
	 * The tail call is not yet called or processing until the client invokes
	 * {@link TailcallVarargs#eval()} which performs the tail call processing.
	 * <p> 
	 * This method is typically not used directly by client code.  
	 * Instead use one of the function invocation methods.   
	 * 
	 * @param func {@link LuaValue} to be called as a tail call
	 * @param args {@link Varargs} containing the arguments to the call
	 * @return {@link TailcallVarargs} to be used in tailcall oprocessing.
	 * @see LuaValue#call()
	 * @see LuaValue#invoke()
	 * @see LuaValue#method(LuaValue)
	 * @see LuaValue#invokemethod(LuaValue)
	 */
	public static Varargs tailcallOf(LuaValue func, Varargs args) { 
		return new TailcallVarargs(func, args);
	}
	
	/**
	 * Callback used during tail call processing to invoke the function once.
	 * <p>
	 * This may return a {@link TailcallVarargs} to be evaluated by the client. 
	 * <p>
	 * This should not be called directly, instead use one of the call invocation functions.
	 * 
	 * @param args the arguments to the call invocation.
	 * @return Varargs the return values, possible a TailcallVarargs.
	 * @see LuaValue#call()
	 * @see LuaValue#invoke()
	 * @see LuaValue#method(LuaValue)
	 * @see LuaValue#invokemethod(LuaValue)
	 */
	public Varargs onInvoke(Varargs args) {
		return invoke(args);
	}

	/** Hook for implementations such as LuaJC to load the environment of the main chunk 
	 * into the first upvalue location.  If the function has no upvalues or is not a main chunk, 
	 * calling this will be no effect.
	 * @param env  The environment to load into the first upvalue, if there is one.
	 */
	public void initupvalue1(LuaValue env) {}

	/** Varargs implemenation with no values.  
	 * <p>
	 * This is an internal class not intended to be used directly.
	 * Instead use the predefined constant {@link LuaValue#NONE} 
	 *  
	 * @see LuaValue#NONE
	 */
	private static final class None extends LuaNil {
		static None _NONE = new None();
		public LuaValue arg(int i) { return NIL; }
		public int narg() { return 0; }
		public LuaValue arg1() { return NIL; }
		public String tojstring() { return "none"; }
		public Varargs subargs(final int start) { return start > 0? this: argerror(1, "start must be > 0"); }

	}
	
	/**
	 * Create a {@code Varargs} instance containing arguments starting at index {@code start}
	 * @param start the index from which to include arguments, where 1 is the first argument.
	 * @return Varargs containing argument { start, start+1,  ... , narg-start-1 }
	 */
	public Varargs subargs(final int start) {
		if (start == 1)
			return this;
		if (start > 1)
			return NONE;
		return argerror(1, "start must be > 0");
	}

}
