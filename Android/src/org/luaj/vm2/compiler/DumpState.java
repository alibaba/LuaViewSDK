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
package org.luaj.vm2.compiler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LocVars;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;


/** Class to dump a {@link Prototype} into an output stream, as part of compiling.
 * <p>
 * Generally, this class is not used directly, but rather indirectly via a command 
 * line interface tool such as {@link luac}.
 * <p>
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
 * @see luac
 * @see LoadState
 * @see Globals
 * @see Prototype
 */
public class DumpState {

	/** set true to allow integer compilation */
	public static boolean ALLOW_INTEGER_CASTING = false;
	
	/** format corresponding to non-number-patched lua, all numbers are floats or doubles */
	public static final int NUMBER_FORMAT_FLOATS_OR_DOUBLES    = 0;

	/** format corresponding to non-number-patched lua, all numbers are ints */
	public static final int NUMBER_FORMAT_INTS_ONLY            = 1;
	
	/** format corresponding to number-patched lua, all numbers are 32-bit (4 byte) ints */
	public static final int NUMBER_FORMAT_NUM_PATCH_INT32      = 4;
	
	/** default number format */
	public static final int NUMBER_FORMAT_DEFAULT = NUMBER_FORMAT_FLOATS_OR_DOUBLES;

	// header fields
	private boolean IS_LITTLE_ENDIAN = false;
	private int NUMBER_FORMAT = NUMBER_FORMAT_DEFAULT;
	private int SIZEOF_LUA_NUMBER = 8;
	private static final int SIZEOF_INT = 4;
	private static final int SIZEOF_SIZET = 4;
	private static final int SIZEOF_INSTRUCTION = 4;

	DataOutputStream writer;
	boolean strip;
	int status;

	public DumpState(OutputStream w, boolean strip) {
		this.writer = new DataOutputStream( w );
		this.strip = strip;
		this.status = 0;
	}

	void dumpBlock(final byte[] b, int size) throws IOException {
		writer.write(b, 0, size);
	}

	void dumpChar(int b) throws IOException {
		writer.write( b );
	}

	void dumpInt(int x) throws IOException {
		if ( IS_LITTLE_ENDIAN ) {
			writer.writeByte(x&0xff);
			writer.writeByte((x>>8)&0xff);
			writer.writeByte((x>>16)&0xff);
			writer.writeByte((x>>24)&0xff);
		} else {
			writer.writeInt(x);
		}
	}
	
	void dumpString(LuaString s) throws IOException {
		final int len = s.len().toint();
		dumpInt( len+1 );
		s.write( writer, 0, len );
		writer.write( 0 );
	}
	
	void dumpDouble(double d) throws IOException {
		long l = Double.doubleToLongBits(d);
		if ( IS_LITTLE_ENDIAN ) {
			dumpInt( (int) l );
			dumpInt( (int) (l>>32) );
		} else {
			writer.writeLong(l);
		}
	}

	void dumpCode( final Prototype f ) throws IOException {
		final int[] code = f.code;
		int n = code.length;
		dumpInt( n );
		for ( int i=0; i<n; i++ )
			dumpInt( code[i] );
	}
	
	void dumpConstants(final Prototype f) throws IOException {
		final LuaValue[] k = f.k;
		int i, n = k.length;
		dumpInt(n);
		for (i = 0; i < n; i++) {
			final LuaValue o = k[i];
			switch ( o.type() ) {
			case LuaValue.TNIL:
				writer.write(LuaValue.TNIL);
				break;
			case LuaValue.TBOOLEAN:
				writer.write(LuaValue.TBOOLEAN);
				dumpChar(o.toboolean() ? 1 : 0);
				break;
			case LuaValue.TNUMBER:
				switch (NUMBER_FORMAT) {
				case NUMBER_FORMAT_FLOATS_OR_DOUBLES:
					writer.write(LuaValue.TNUMBER);
					dumpDouble(o.todouble());
					break;
				case NUMBER_FORMAT_INTS_ONLY:
					if ( ! ALLOW_INTEGER_CASTING && ! o.isint() )
						throw new java.lang.IllegalArgumentException("not an integer: "+o);
					writer.write(LuaValue.TNUMBER);
					dumpInt(o.toint());
					break;
				case NUMBER_FORMAT_NUM_PATCH_INT32:
					if ( o.isint() ) {
						writer.write(LuaValue.TINT);
						dumpInt(o.toint());
					} else {
						writer.write(LuaValue.TNUMBER);
						dumpDouble(o.todouble());
					}
					break;
				default:
					throw new IllegalArgumentException("number format not supported: "+NUMBER_FORMAT);
				}
				break;
			case LuaValue.TSTRING:
				writer.write(LuaValue.TSTRING);
				dumpString((LuaString)o);
				break;
			default:
				throw new IllegalArgumentException("bad type for " + o);			
			}
		}
		n = f.p.length;
		dumpInt(n);
		for (i = 0; i < n; i++)
			dumpFunction(f.p[i]);
	}

	void dumpUpvalues(final Prototype f) throws IOException {
		int n = f.upvalues.length;
		dumpInt(n);
		for (int i = 0; i < n; i++) {
			writer.writeByte(f.upvalues[i].instack ? 1 : 0);
			writer.writeByte(f.upvalues[i].idx);
		}
	}

	void dumpDebug(final Prototype f) throws IOException {
		int i, n;
		if (strip)
			dumpInt(0);
		else
			dumpString(f.source);
		n = strip ? 0 : f.lineinfo.length;
		dumpInt(n);
		for (i = 0; i < n; i++)
			dumpInt(f.lineinfo[i]);
		n = strip ? 0 : f.locvars.length;
		dumpInt(n);
		for (i = 0; i < n; i++) {
			LocVars lvi = f.locvars[i];
			dumpString(lvi.varname);
			dumpInt(lvi.startpc);
			dumpInt(lvi.endpc);
		}
		n = strip ? 0 : f.upvalues.length;
		dumpInt(n);
		for (i = 0; i < n; i++)
			dumpString(f.upvalues[i].name);
	}
	
	void dumpFunction(final Prototype f) throws IOException {
		dumpInt(f.linedefined);
		dumpInt(f.lastlinedefined);
		dumpChar(f.numparams);
		dumpChar(f.is_vararg);
		dumpChar(f.maxstacksize);
		dumpCode(f);
		dumpConstants(f);
		dumpUpvalues(f);
		dumpDebug(f);
	}

	void dumpHeader() throws IOException {
		writer.write( LoadState.LUA_SIGNATURE );
		writer.write( LoadState.LUAC_VERSION );
		writer.write( LoadState.LUAC_FORMAT );
		writer.write( IS_LITTLE_ENDIAN? 1: 0 );
		writer.write( SIZEOF_INT );
		writer.write( SIZEOF_SIZET );
		writer.write( SIZEOF_INSTRUCTION );
		writer.write( SIZEOF_LUA_NUMBER );
		writer.write( NUMBER_FORMAT );
		writer.write( LoadState.LUAC_TAIL );
	}

	/*
	** dump Lua function as precompiled chunk
	*/
	public static int dump( Prototype f, OutputStream w, boolean strip ) throws IOException {
		DumpState D = new DumpState(w,strip);
		D.dumpHeader();
		D.dumpFunction(f);
		return D.status;
	}

	/**
	 * 
	 * @param f the function to dump
	 * @param w the output stream to dump to
	 * @param stripDebug true to strip debugging info, false otherwise
	 * @param numberFormat one of NUMBER_FORMAT_FLOATS_OR_DOUBLES, NUMBER_FORMAT_INTS_ONLY, NUMBER_FORMAT_NUM_PATCH_INT32
	 * @param littleendian true to use little endian for numbers, false for big endian
	 * @return 0 if dump succeeds
	 * @throws IOException
	 * @throws IllegalArgumentException if the number format it not supported
	 */
	public static int dump(Prototype f, OutputStream w, boolean stripDebug, int numberFormat, boolean littleendian) throws IOException {
		switch ( numberFormat ) {
		case NUMBER_FORMAT_FLOATS_OR_DOUBLES:
		case NUMBER_FORMAT_INTS_ONLY:
		case NUMBER_FORMAT_NUM_PATCH_INT32:
			break;
		default:
			throw new IllegalArgumentException("number format not supported: "+numberFormat);
		}
		DumpState D = new DumpState(w,stripDebug);
		D.IS_LITTLE_ENDIAN = littleendian;
		D.NUMBER_FORMAT = numberFormat;
		D.SIZEOF_LUA_NUMBER = (numberFormat==NUMBER_FORMAT_INTS_ONLY? 4: 8);
		D.dumpHeader();
		D.dumpFunction(f);
		return D.status;
	}
}
