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
package org.luaj.vm2.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LocVars;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.Upvaldesc;
import org.luaj.vm2.lib.BaseLib;

/**
 * Compiler for Lua.
 * 
 * <p>
 * Compiles lua source files into lua bytecode within a {@link Prototype}, 
 * loads lua binary files directly into a{@link Prototype}, 
 * and optionaly instantiates a {@link LuaClosure} around the result 
 * using a user-supplied environment.  
 * 
 * <p>
 * Implements the {@link Globals.Compiler} interface for loading 
 * initialized chunks, which is an interface common to 
 * lua bytecode compiling and java bytecode compiling.
 *  
 * <p> 
 * The {@link LuaC} compiler is installed by default by both the 
 * {@link JsePlatform} and {@link JmePlatform} classes, 
 * so in the following example, the default {@link LuaC} compiler 
 * will be used:
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * globals.load(new StringReader("print 'hello'"), "main.lua" ).call();
 * } </pre>
 * 
 * To load the LuaC compiler manually, use the install method:
 * <pre> {@code
 * LuaC.install(globals);
 * } </pre>
 * 
 * @see LuaC#install(Globals)
 * @see Globals#Compiler
 * @see Globals#Loader
 * @see LuaJC
 * @see JsePlatform
 * @see JmePlatform
 * @see BaseLib
 * @see LuaValue
 * @see LuaCompiler
 * @see Prototype
 */
public class LuaC extends Lua implements Globals.Compiler, Globals.Loader {

	/** A sharable instance of the LuaC compiler. */
	public static final LuaC instance = new LuaC();
	
	/** Install the compiler so that LoadState will first 
	 * try to use it when handed bytes that are 
	 * not already a compiled lua chunk.
	 * @param globals the Globals into which this is to be installed.
	 */
	public static void install(Globals globals) {
		globals.compiler = instance;
		globals.loader = instance;
	}

	protected static void _assert(boolean b) {		
		if (!b)
			throw new LuaError("compiler assert failed");
	}
	
	public static final int MAXSTACK = 250;
	static final int LUAI_MAXUPVAL = 0xff;
	static final int LUAI_MAXVARS = 200;
	static final int NO_REG		 = MAXARG_A;
	

	/* OpMode - basic instruction format */
	static final int 
		iABC = 0,
		iABx = 1,
		iAsBx = 2;

	/* OpArgMask */
	static final int 
	  OpArgN = 0,  /* argument is not used */
	  OpArgU = 1,  /* argument is used */
	  OpArgR = 2,  /* argument is a register or a jump offset */
	  OpArgK = 3;   /* argument is a constant or register/constant */


	static void SET_OPCODE(InstructionPtr i,int o) {
		i.set( ( i.get() & (MASK_NOT_OP)) | ((o << POS_OP) & MASK_OP) );
	}
	
	static void SETARG_A(int[] code, int index, int u) {
		code[index] = (code[index] & (MASK_NOT_A)) | ((u << POS_A) & MASK_A);
	}

	static void SETARG_A(InstructionPtr i,int u) {
		i.set( ( i.get() & (MASK_NOT_A)) | ((u << POS_A) & MASK_A) );
	}

	static void SETARG_B(InstructionPtr i,int u) {
		i.set( ( i.get() & (MASK_NOT_B)) | ((u << POS_B) & MASK_B) );
	}

	static void SETARG_C(InstructionPtr i,int u) {
		i.set( ( i.get() & (MASK_NOT_C)) | ((u << POS_C) & MASK_C) );
	}
	
	static void SETARG_Bx(InstructionPtr i,int u) {
		i.set( ( i.get() & (MASK_NOT_Bx)) | ((u << POS_Bx) & MASK_Bx) );
	}
	
	static void SETARG_sBx(InstructionPtr i,int u) {
		SETARG_Bx( i, u + MAXARG_sBx );
	}

	static int CREATE_ABC(int o, int a, int b, int c) {
		return ((o << POS_OP) & MASK_OP) |
				((a << POS_A) & MASK_A) |
				((b << POS_B) & MASK_B) |
				((c << POS_C) & MASK_C) ;
	}
	
	static int CREATE_ABx(int o, int a, int bc) {
		return ((o << POS_OP) & MASK_OP) |
				((a << POS_A) & MASK_A) |
				((bc << POS_Bx) & MASK_Bx) ;
 	}

	// vector reallocation
	
	static LuaValue[] realloc(LuaValue[] v, int n) {
		LuaValue[] a = new LuaValue[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static Prototype[] realloc(Prototype[] v, int n) {
		Prototype[] a = new Prototype[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static LuaString[] realloc(LuaString[] v, int n) {
		LuaString[] a = new LuaString[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static LocVars[] realloc(LocVars[] v, int n) {
		LocVars[] a = new LocVars[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static Upvaldesc[] realloc(Upvaldesc[] v, int n) {
		Upvaldesc[] a = new Upvaldesc[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static LexState.Vardesc[] realloc(LexState.Vardesc[] v, int n) {
		LexState.Vardesc[] a = new LexState.Vardesc[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static LexState.Labeldesc[] grow(LexState.Labeldesc[] v, int min_n) {
		return v == null ? new LexState.Labeldesc[2] : v.length < min_n ? realloc(v, v.length*2) : v; 
	}
	
	static LexState.Labeldesc[] realloc(LexState.Labeldesc[] v, int n) {
		LexState.Labeldesc[] a = new LexState.Labeldesc[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static int[] realloc(int[] v, int n) {
		int[] a = new int[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static byte[] realloc(byte[] v, int n) {
		byte[] a = new byte[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static char[] realloc(char[] v, int n) {
		char[] a = new char[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	public int nCcalls;
	Hashtable strings;

	protected LuaC() {}
	
	private LuaC(Hashtable strings) {
		 this.strings = strings;
	}

	/** Compile lua source into a Prototype.
	 * @param stream InputStream representing the text source conforming to lua source syntax.
	 * @param chunkname String name of the chunk to use.
	 * @return Prototype representing the lua chunk for this source.
	 * @throws IOException
	 */
	public Prototype compile(InputStream stream, String chunkname) throws IOException {
		return (new LuaC(new Hashtable())).luaY_parser(stream, chunkname);
	}

	/** @deprecated
	 * Use Globals.load(InputString, String, String) instead, 
	 * or LuaC.compil(InputStream, String) and construct LuaClosure directly.
	 */
	public LuaValue load(InputStream stream, String chunkname, Globals globals) throws IOException {
		return new LuaClosure(compile(stream, chunkname), globals);
	}


	/** Parse the input */
	private Prototype luaY_parser(InputStream z, String name) throws IOException{
		LexState lexstate = new LexState(this, z);
		FuncState funcstate = new FuncState();
		// lexstate.buff = buff;
		lexstate.fs = funcstate;
		lexstate.setinput( this, z.read(), z, (LuaString) LuaValue.valueOf(name) );
		/* main func. is always vararg */
		funcstate.f = new Prototype();
		funcstate.f.source = (LuaString) LuaValue.valueOf(name);
		lexstate.mainfunc(funcstate);
		LuaC._assert (funcstate.prev == null);
		/* all scopes should be correctly finished */
		LuaC._assert (lexstate.dyd == null 
				|| (lexstate.dyd.n_actvar == 0 && lexstate.dyd.n_gt == 0 && lexstate.dyd.n_label == 0));
		return funcstate.f;
	}

	// look up and keep at most one copy of each string
	public LuaString newTString(String s) {
		return cachedLuaString(LuaString.valueOf(s));
	}

	// look up and keep at most one copy of each string
	public LuaString newTString(LuaString s) {
		return cachedLuaString(s);
	}

	public LuaString cachedLuaString(LuaString s) {
		LuaString c = (LuaString) strings.get(s);
		if (c != null) 
			return c;
		strings.put(s, s);
		return s;
	}

	public String pushfstring(String string) {
		return string;
	}

	public LuaFunction load(Prototype prototype, String chunkname, LuaValue env) throws IOException {
		return new LuaClosure(prototype, env);
	}
}
