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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.luaj.vm2.compiler.DumpState;


/**
* Class to undump compiled lua bytecode into a {@link Prototype} instances.
* <p>
* The {@link LoadState} class implements {@link Globals#Undumper}
* which is used to undump a string of bytes that represent a lua binary file
* using either the C-based lua compiler, or luaj's {@link DumpState#dump} function.
* <p>
* The canonical method to load and execute code is done 
* indirectly using the Globals:
* <pre> {@code
* Globals globals = JsePlatform.standardGlobals();
* LuaValue chunk = globasl.load("print('hello, world')", "main.lua");
* chunk.call();
* } </pre>
* This should work regardless of which {@link Globals.Compiler}
* has been installed.
* <p>
* By default, when using {@link JsePlatform} or {@JmePlatform}
* to construct globals, the {@link LoadState} is installed
* as the default {@link Globals#undumper}. 
* <p>
* 
* A lua binary file is created via {@link DumpState#dump}:
* <pre> {@code
* Globals globals = JsePlatform.standardGlobals();
* Prototype p = globals.compilePrototype(new StringReader("print('hello, world')"), "main.lua");
* ByteArrayOutputStream o = new ByteArrayOutputStream();
* DumpState.dump(p, o, false);
* byte[] lua_binary_file_bytes = o.toByteArray();
* } </pre>
* 
* The {@link LoadState} may be used directly to undump these bytes:
* <pre> {@code
* Prototypep = LoadState.instance.undump(new ByteArrayInputStream(lua_binary_file_bytes), "main.lua");
* LuaClosure c = new LuaClosure(p, globals);
* c.call();
* } </pre>
* 
* 
* More commonly, the {@link Globals#undumper} may be used to undump them:
* <pre> {@code
* Prototype p = globals.loadPrototype(new ByteArrayInputStream(lua_binary_file_bytes), "main.lua", "b");
* LuaClosure c = new LuaClosure(p, globals);
* c.call();
* } </pre>
* 
* @see LuaCompiler
* @see LuaClosure
* @see LuaFunction
* @see LoadState#compiler
* @see LoadState#load(InputStream, String, LuaValue)
* @see LuaC
* @see LuaJC
*/
public class LoadState implements Globals.Undumper {

	/** Shared instance of Globals.Undumper to use loading prototypes from binary lua files */
	public static final Globals.Undumper instance = new LoadState();
	
	/** format corresponding to non-number-patched lua, all numbers are floats or doubles */
	public static final int NUMBER_FORMAT_FLOATS_OR_DOUBLES    = 0;

	/** format corresponding to non-number-patched lua, all numbers are ints */
	public static final int NUMBER_FORMAT_INTS_ONLY            = 1;
	
	/** format corresponding to number-patched lua, all numbers are 32-bit (4 byte) ints */
	public static final int NUMBER_FORMAT_NUM_PATCH_INT32      = 4;
	
	// type constants	
	public static final int LUA_TINT            = (-2);
	public static final int LUA_TNONE			= (-1);
	public static final int LUA_TNIL			= 0;
	public static final int LUA_TBOOLEAN		= 1;
	public static final int LUA_TLIGHTUSERDATA	= 2;
	public static final int LUA_TNUMBER			= 3;
	public static final int LUA_TSTRING			= 4;
	public static final int LUA_TTABLE			= 5;
	public static final int LUA_TFUNCTION		= 6;
	public static final int LUA_TUSERDATA		= 7;
	public static final int LUA_TTHREAD			= 8;
	public static final int LUA_TVALUE          = 9;
	
	/** The character encoding to use for file encoding.  Null means the default encoding */
	public static String encoding = null;
	
	/** Signature byte indicating the file is a compiled binary chunk */
	public static final byte[] LUA_SIGNATURE	= { '\033', 'L', 'u', 'a' };

	/** Data to catch conversion errors */
	public static final byte[] LUAC_TAIL = { (byte) 0x19, (byte) 0x93, '\r', '\n', (byte) 0x1a, '\n', };
	

	/** Name for compiled chunks */
	public static final String SOURCE_BINARY_STRING = "binary string";


	/** for header of binary files -- this is Lua 5.2 */
	public static final int LUAC_VERSION		= 0x52;

	/** for header of binary files -- this is the official format */
	public static final int LUAC_FORMAT		= 0;

	/** size of header of binary files */
	public static final int LUAC_HEADERSIZE		= 12;

	// values read from the header
	private int     luacVersion;
	private int     luacFormat;
	private boolean luacLittleEndian;
	private int     luacSizeofInt;
	private int     luacSizeofSizeT;
	private int     luacSizeofInstruction;
	private int     luacSizeofLuaNumber;
	private int 	luacNumberFormat;

	/** input stream from which we are loading */
	public final DataInputStream is;

	/** Name of what is being loaded? */
	String name;

	private static final LuaValue[]     NOVALUES    = {};
	private static final Prototype[] NOPROTOS    = {};
	private static final LocVars[]   NOLOCVARS   = {};
	private static final LuaString[]  NOSTRVALUES = {};
	private static final Upvaldesc[]  NOUPVALDESCS = {};
	private static final int[]       NOINTS      = {};
	
	/** Read buffer */
	private byte[] buf = new byte[512];

	/** Install this class as the standard Globals.Undumper for the supplied Globals */
	public static void install(Globals globals) {
		globals.undumper = instance;
	}
	
	/** Load a 4-byte int value from the input stream
	 * @return the int value laoded.  
	 **/
	int loadInt() throws IOException {
		is.readFully(buf,0,4);
		return luacLittleEndian? 
				(buf[3] << 24) | ((0xff & buf[2]) << 16) | ((0xff & buf[1]) << 8) | (0xff & buf[0]):
				(buf[0] << 24) | ((0xff & buf[1]) << 16) | ((0xff & buf[2]) << 8) | (0xff & buf[3]);
	}
	
	/** Load an array of int values from the input stream
	 * @return the array of int values laoded.  
	 **/
	int[] loadIntArray() throws IOException {
		int n = loadInt();
		if ( n == 0 )
			return NOINTS;
		
		// read all data at once
		int m = n << 2;
		if ( buf.length < m )
			buf = new byte[m];
		is.readFully(buf,0,m);
		int[] array = new int[n];
		for ( int i=0, j=0; i<n; ++i, j+=4 )
			array[i] = luacLittleEndian? 
					(buf[j+3] << 24) | ((0xff & buf[j+2]) << 16) | ((0xff & buf[j+1]) << 8) | (0xff & buf[j+0]):
					(buf[j+0] << 24) | ((0xff & buf[j+1]) << 16) | ((0xff & buf[j+2]) << 8) | (0xff & buf[j+3]);

		return array;
	}
	
	/** Load a long  value from the input stream
	 * @return the long value laoded.  
	 **/
	long loadInt64() throws IOException {
		int a,b;
		if ( this.luacLittleEndian ) {
			a = loadInt();
			b = loadInt();
		} else {
			b = loadInt();
			a = loadInt();
		}
		return (((long)b)<<32) | (((long)a)&0xffffffffL);
	}

	/** Load a lua strin gvalue from the input stream
	 * @return the {@link LuaString} value laoded.  
	 **/
	LuaString loadString() throws IOException {
		int size = this.luacSizeofSizeT == 8? (int) loadInt64(): loadInt();
		if ( size == 0 )
			return null;
		byte[] bytes = new byte[size];
		is.readFully( bytes, 0, size );
		return LuaString.valueOf( bytes, 0, bytes.length - 1 );
	}
	
	/**
	 * Convert bits in a long value to a {@link LuaValue}. 
	 * @param bits long value containing the bits
	 * @return {@link LuaInteger} or {@link LuaDouble} whose value corresponds to the bits provided.
	 */
	public static LuaValue longBitsToLuaNumber( long bits ) {
		if ( ( bits & ( ( 1L << 63 ) - 1 ) ) == 0L ) {
			return LuaValue.ZERO;
		}
		
		int e = (int)((bits >> 52) & 0x7ffL) - 1023;
		
		if ( e >= 0 && e < 31 ) {
			long f = bits & 0xFFFFFFFFFFFFFL;
			int shift = 52 - e;
			long intPrecMask = ( 1L << shift ) - 1;
			if ( ( f & intPrecMask ) == 0 ) {
				int intValue = (int)( f >> shift ) | ( 1 << e );
				return LuaInteger.valueOf( ( ( bits >> 63 ) != 0 ) ? -intValue : intValue );
			}
		}
		
		return LuaValue.valueOf( Double.longBitsToDouble(bits) );
	}
	
	/** 
	 * Load a number from a binary chunk
	 * @return the {@link LuaValue} loaded
	 * @throws IOException if an i/o exception occurs
	 */
	LuaValue loadNumber() throws IOException {
		if ( luacNumberFormat == NUMBER_FORMAT_INTS_ONLY ) {
			return LuaInteger.valueOf( loadInt() );
		} else {
			return longBitsToLuaNumber( loadInt64() );
		}
	}

	/**
	 * Load a list of constants from a binary chunk
	 * @param f the function prototype
	 * @throws IOException if an i/o exception occurs
	 */
	void loadConstants(Prototype f) throws IOException {
		int n = loadInt();
		LuaValue[] values = n>0? new LuaValue[n]: NOVALUES;
		for ( int i=0; i<n; i++ ) {
			switch ( is.readByte() ) {
			case LUA_TNIL:
				values[i] = LuaValue.NIL;
				break;
			case LUA_TBOOLEAN:
				values[i] = (0 != is.readUnsignedByte()? LuaValue.TRUE: LuaValue.FALSE);
				break;
			case LUA_TINT:
				values[i] = LuaInteger.valueOf( loadInt() );
				break;
			case LUA_TNUMBER:
				values[i] = loadNumber();
				break;
			case LUA_TSTRING:
				values[i] = loadString();
				break;
			default:
				throw new IllegalStateException("bad constant");
			}
		}
		f.k = values;
		
		n = loadInt();
		Prototype[] protos = n>0? new Prototype[n]: NOPROTOS;
		for ( int i=0; i<n; i++ )
			protos[i] = loadFunction(f.source);
		f.p = protos;
	}


	void loadUpvalues(Prototype f) throws IOException {
		int n = loadInt();
		f.upvalues = n>0? new Upvaldesc[n]: NOUPVALDESCS;
		for (int i=0; i<n; i++) {
			boolean instack = is.readByte() != 0;
			int idx = ((int) is.readByte()) & 0xff;
			f.upvalues[i] = new Upvaldesc(null, instack, idx);
		}
	}

	/**
	 * Load the debug info for a function prototype
	 * @param f the function Prototype
	 * @throws IOException if there is an i/o exception
	 */
	void loadDebug( Prototype f ) throws IOException {
		f.source = loadString();
		f.lineinfo = loadIntArray();
		int n = loadInt();
		f.locvars = n>0? new LocVars[n]: NOLOCVARS;
		for ( int i=0; i<n; i++ ) {
			LuaString varname = loadString();
			int startpc = loadInt();
			int endpc = loadInt();
			f.locvars[i] = new LocVars(varname, startpc, endpc);
		}
		
		n = loadInt();
		for ( int i=0; i<n; i++ )
			f.upvalues[i].name = loadString();
	}

	/** 
	 * Load a function prototype from the input stream
	 * @param p name of the source
	 * @return {@link Prototype} instance that was loaded
	 * @throws IOException
	 */
	public Prototype loadFunction(LuaString p) throws IOException {
		Prototype f = new Prototype();
////		this.L.push(f);
//		f.source = loadString();
//		if ( f.source == null )
//			f.source = p;
		f.linedefined = loadInt();
		f.lastlinedefined = loadInt();
		f.numparams = is.readUnsignedByte();
		f.is_vararg = is.readUnsignedByte();
		f.maxstacksize = is.readUnsignedByte();
		f.code = loadIntArray();
		loadConstants(f);
		loadUpvalues(f);
		loadDebug(f);
		
		// TODO: add check here, for debugging purposes, I believe
		// see ldebug.c
//		 IF (!luaG_checkcode(f), "bad code");
		
//		 this.L.pop();
		 return f;
	}

	/**
	 * Load the lua chunk header values. 
	 * @throws IOException if an i/o exception occurs. 
	 */
	public void loadHeader() throws IOException {
		luacVersion = is.readByte();
		luacFormat = is.readByte();
		luacLittleEndian = (0 != is.readByte());
		luacSizeofInt = is.readByte();
		luacSizeofSizeT = is.readByte();
		luacSizeofInstruction = is.readByte();
		luacSizeofLuaNumber = is.readByte();
		luacNumberFormat = is.readByte();
		for (int i=0; i < LUAC_TAIL.length; ++i)
			if (is.readByte() != LUAC_TAIL[i])
				throw new LuaError("Unexpeted byte in luac tail of header, index="+i);
	}

	/**
	 * Load input stream as a lua binary chunk if the first 4 bytes are the lua binary signature.
	 * @param stream InputStream to read, after having read the first byte already
	 * @param name Name to apply to the loaded chunk
	 * @return {@link Prototype} that was loaded, or null if the first 4 bytes were not the lua signature.
	 * @throws IOException if an IOException occurs
	 */
	public Prototype undump(InputStream stream, String chunkname) throws IOException {
		// check rest of signature
		if ( stream.read() != LUA_SIGNATURE[0] 
		   || stream.read() != LUA_SIGNATURE[1]
	       || stream.read() != LUA_SIGNATURE[2]
		   || stream.read() != LUA_SIGNATURE[3] )
			return null;
		
		// load file as a compiled chunk
		String sname = getSourceName(chunkname);
		LoadState s = new LoadState( stream, sname );
		s.loadHeader();

		// check format
		switch ( s.luacNumberFormat ) {
		case NUMBER_FORMAT_FLOATS_OR_DOUBLES:
		case NUMBER_FORMAT_INTS_ONLY:
		case NUMBER_FORMAT_NUM_PATCH_INT32:
			break;
		default:
			throw new LuaError("unsupported int size");
		}
		return s.loadFunction( LuaString.valueOf(sname) );
	}
	
	/**
	 * Construct a source name from a supplied chunk name
	 * @param name String name that appears in the chunk
	 * @return source file name
	 */
    public static String getSourceName(String name) {
        String sname = name;
        if ( name.startsWith("@") || name.startsWith("=") )
			sname = name.substring(1);
		else if ( name.startsWith("\033") )
			sname = SOURCE_BINARY_STRING;
        return sname;
    }

	/** Private constructor for create a load state */
	private LoadState( InputStream stream, String name ) {
		this.name = name;
		this.is = new DataInputStream( stream );
	}
	
	private LoadState() {
		this.name = "";
		this.is = null;
	}
}
