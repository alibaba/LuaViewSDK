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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Debug helper class to pretty-print lua bytecodes. 
 * @see Prototype
 * @see LuaClosure 
 */
public class Print extends Lua {

	/** opcode names */
	private static final String STRING_FOR_NULL = "null";
	public static PrintStream ps = System.out;

	/** String names for each lua opcode value. */
	public static final String[] OPNAMES = {
		  "MOVE",
		  "LOADK",
		  "LOADKX",
		  "LOADBOOL",
		  "LOADNIL",
		  "GETUPVAL",
		  "GETTABUP",
		  "GETTABLE",
		  "SETTABUP",
		  "SETUPVAL",
		  "SETTABLE",
		  "NEWTABLE",
		  "SELF",
		  "ADD",
		  "SUB",
		  "MUL",
		  "DIV",
		  "MOD",
		  "POW",
		  "UNM",
		  "NOT",
		  "LEN",
		  "CONCAT",
		  "JMP",
		  "EQ",
		  "LT",
		  "LE",
		  "TEST",
		  "TESTSET",
		  "CALL",
		  "TAILCALL",
		  "RETURN",
		  "FORLOOP",
		  "FORPREP",
		  "TFORCALL",
		  "TFORLOOP",
		  "SETLIST",
		  "CLOSURE",
		  "VARARG",
		  "EXTRAARG",
		  null,
	};


	static void printString(PrintStream ps, final LuaString s) {
		
		ps.print('"');
		for (int i = 0, n = s.m_length; i < n; i++) {
			int c = s.m_bytes[s.m_offset+i];
			if ( c >= ' ' && c <= '~' && c != '\"' && c != '\\' )
				ps.print((char) c);
			else {
				switch (c) {
					case '"':
						ps.print("\\\"");
						break;
					case '\\':
						ps.print("\\\\");
						break;
					case 0x0007: /* bell */
						ps.print("\\a");
						break;
					case '\b': /* backspace */
						ps.print("\\b");
						break;
					case '\f':  /* form feed */
						ps.print("\\f");
						break;
					case '\t':  /* tab */
						ps.print("\\t");
						break;
					case '\r': /* carriage return */
						ps.print("\\r");
						break;
					case '\n': /* newline */
						ps.print("\\n");
						break;
					case 0x000B: /* vertical tab */
						ps.print("\\v");
						break;
					default:
						ps.print('\\');
						ps.print(Integer.toString(1000 + 0xff&c).substring(1));
						break;
				}
			}
		}
		ps.print('"');
	}

	static void printValue( PrintStream ps, LuaValue v ) {
		switch ( v.type() ) {
		case LuaValue.TSTRING: printString( ps, (LuaString) v ); break;
		default: ps.print( v.tojstring() );
		
		}
	}
	
	static void printConstant(PrintStream ps, Prototype f, int i) {
		printValue( ps, f.k[i] );
	}

	static void printUpvalue(PrintStream ps, Upvaldesc u) {
		ps.print( u.idx + " " );
		printValue( ps, u.name );
	}

	/** 
	 * Print the code in a prototype
	 * @param f the {@link Prototype}
	 */
	public static void printCode(Prototype f) {
		int[] code = f.code;
		int pc, n = code.length;
		for (pc = 0; pc < n; pc++) {
			printOpCode(f, pc);
			ps.println();
		}
	}

	/** 
	 * Print an opcode in a prototype
	 * @param f the {@link Prototype}
	 * @param pc the program counter to look up and print
	 */
	public static void printOpCode(Prototype f, int pc) {
		printOpCode(ps,f,pc);
	}
	
	/** 
	 * Print an opcode in a prototype
	 * @param ps the {@link PrintStream} to print to
	 * @param f the {@link Prototype}
	 * @param pc the program counter to look up and print
	 */
	public static void printOpCode(PrintStream ps, Prototype f, int pc) {
		int[] code = f.code;
		int i = code[pc];
		int o = GET_OPCODE(i);
		int a = GETARG_A(i);
		int b = GETARG_B(i);
		int c = GETARG_C(i);
		int bx = GETARG_Bx(i);
		int sbx = GETARG_sBx(i);
		int line = getline(f, pc);
		ps.print("  " + (pc + 1) + "  ");
		if (line > 0)
			ps.print("[" + line + "]  ");
		else
			ps.print("[-]  ");
		ps.print(OPNAMES[o] + "  ");
		switch (getOpMode(o)) {
		case iABC:
			ps.print( a );
			if (getBMode(o) != OpArgN)
				ps.print(" "+(ISK(b) ? (-1 - INDEXK(b)) : b));
			if (getCMode(o) != OpArgN)
				ps.print(" "+(ISK(c) ? (-1 - INDEXK(c)) : c));
			break;
		case iABx:
			if (getBMode(o) == OpArgK) {
				ps.print(a + " " + (-1 - bx));
			} else {
				ps.print(a + " " + (bx));
			}
			break;
		case iAsBx:
			if (o == OP_JMP)
				ps.print( sbx );
			else
				ps.print(a + " " + sbx);
			break;
		}
		switch (o) {
		case OP_LOADK:
			ps.print("  ; ");
			printConstant(ps, f, bx);
			break;
		case OP_GETUPVAL:
		case OP_SETUPVAL:
			ps.print("  ; ");
			printUpvalue(ps, f.upvalues[b]);
			break;
		case OP_GETTABUP:
			ps.print("  ; ");
			printUpvalue(ps, f.upvalues[b]);
			ps.print(" ");
			if (ISK(c))
				printConstant(ps, f, INDEXK(c));
			else
				ps.print("-");
			break;
		case OP_SETTABUP:
			ps.print("  ; ");
			printUpvalue(ps, f.upvalues[a]);
			ps.print(" ");
			if (ISK(b))
				printConstant(ps, f, INDEXK(b));
			else
				ps.print("-");
			ps.print(" ");
			if (ISK(c))
				printConstant(ps, f, INDEXK(c));
			else
				ps.print("-");
			break;
		case OP_GETTABLE:
		case OP_SELF:
			if (ISK(c)) {
				ps.print("  ; ");
				printConstant(ps, f, INDEXK(c));
			}
			break;
		case OP_SETTABLE:
		case OP_ADD:
		case OP_SUB:
		case OP_MUL:
		case OP_DIV:
		case OP_POW:
		case OP_EQ:
		case OP_LT:
		case OP_LE:
			if (ISK(b) || ISK(c)) {
				ps.print("  ; ");
				if (ISK(b))
					printConstant(ps, f, INDEXK(b));
				else
					ps.print("-");
				ps.print(" ");
				if (ISK(c))
					printConstant(ps, f, INDEXK(c));
				else
					ps.print("-");
			}
			break;
		case OP_JMP:
		case OP_FORLOOP:
		case OP_FORPREP:
			ps.print("  ; to " + (sbx + pc + 2));
			break;
		case OP_CLOSURE:
			ps.print("  ; " + f.p[bx].getClass().getName());
			break;
		case OP_SETLIST:
			if (c == 0)
				ps.print("  ; " + ((int) code[++pc]));
			else
				ps.print("  ; " + ((int) c));
			break;
		case OP_VARARG:
			ps.print( "  ; is_vararg="+ f.is_vararg );
			break;			
		default:
			break;
		}
	}

	private static int getline(Prototype f, int pc) {
		return pc>0 && f.lineinfo!=null && pc<f.lineinfo.length? f.lineinfo[pc]: -1;
	}

	static void printHeader(Prototype f) {
		String s = String.valueOf(f.source);
		if (s.startsWith("@") || s.startsWith("="))
			s = s.substring(1);
		else if ("\033Lua".equals(s))
			s = "(bstring)";
		else
			s = "(string)";
		String a = (f.linedefined == 0) ? "main" : "function";
		ps.print("\n%" + a + " <" + s + ":" + f.linedefined + ","
				+ f.lastlinedefined + "> (" + f.code.length + " instructions, "
				+ f.code.length * 4 + " bytes at " + id(f) + ")\n");
		ps.print(f.numparams + " param, " + f.maxstacksize + " slot, "
				+ f.upvalues.length + " upvalue, ");
		ps.print(f.locvars.length + " local, " + f.k.length
				+ " constant, " + f.p.length + " function\n");
	}

	static void printConstants(Prototype f) {
		int i, n = f.k.length;
		ps.print("constants (" + n + ") for " + id(f) + ":\n");
		for (i = 0; i < n; i++) {
			ps.print("  " + (i + 1) + "  ");
			printValue( ps, f.k[i] );
			ps.print( "\n");
		}
	}

	static void printLocals(Prototype f) {
		int i, n = f.locvars.length;
		ps.print("locals (" + n + ") for " + id(f) + ":\n");
		for (i = 0; i < n; i++) {
			ps.println("  "+i+"  "+f.locvars[i].varname+" "+(f.locvars[i].startpc+1)+" "+(f.locvars[i].endpc+1));
		}
	}

	static void printUpValues(Prototype f) {
		int i, n = f.upvalues.length;
		ps.print("upvalues (" + n + ") for " + id(f) + ":\n");
		for (i = 0; i < n; i++) {
			ps.print("  " + i + "  " + f.upvalues[i] + "\n");
		}
	}

	/** Pretty-prints contents of a Prototype.
	 * 
	 * @param prototype Prototype to print.
	 */
	public static void print(Prototype prototype) {
		printFunction(prototype, true);
	}
	
	/** Pretty-prints contents of a Prototype in short or long form.
	 * 
	 * @param prototype Prototype to print.
	 * @param full true to print all fields, false to print short form.
	 */
	public static void printFunction(Prototype prototype, boolean full) {
		int i, n = prototype.p.length;
		printHeader(prototype);
		printCode(prototype);
		if (full) {
			printConstants(prototype);
			printLocals(prototype);
			printUpValues(prototype);
		}
		for (i = 0; i < n; i++)
			printFunction(prototype.p[i], full);
	}

	private static void format( String s, int maxcols ) {
		int n = s.length();
		if ( n > maxcols )
			ps.print( s.substring(0,maxcols) );
		else {
			ps.print( s );
			for ( int i=maxcols-n; --i>=0; )
				ps.print( ' ' );
		}
	}

	private static String id(Prototype f) {
		return "Proto";
	}
	private void _assert(boolean b) {
		if ( !b ) 
			throw new NullPointerException("_assert failed");
	}

	/**
	 * Print the state of a {@link LuaClosure} that is being executed
	 * @param cl the {@link LuaClosure} 
	 * @param pc the program counter
	 * @param stack the stack of {@link LuaValue}
	 * @param top the top of the stack
	 * @param varargs any {@link Varargs} value that may apply
	 */
	public static void printState(LuaClosure cl, int pc, LuaValue[] stack, int top, Varargs varargs) {
		// print opcode into buffer
		PrintStream previous = ps;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ps = new PrintStream( baos );
		printOpCode( cl.p, pc );
		ps.flush();
		ps.close();
		ps = previous;
		format( baos.toString(), 50 );

		// print stack
		ps.print('[');
		for ( int i=0; i<stack.length; i++ ) {
			LuaValue v = stack[i];
			if ( v == null ) 
				ps.print(STRING_FOR_NULL);
			else switch ( v.type() ) {
			case LuaValue.TSTRING: 
				LuaString s = v.checkstring();
				ps.print( s.length() < 48?
						s.tojstring():
						s.substring(0, 32).tojstring()+"...+"+(s.length()-32)+"b");					
				break;
			case LuaValue.TFUNCTION:
				ps.print( v.tojstring() );
				break;
			case LuaValue.TUSERDATA:
				Object o = v.touserdata();
				if ( o != null ) {
					String n = o.getClass().getName();
					n = n.substring(n.lastIndexOf('.')+1);
					ps.print( n+": "+Integer.toHexString(o.hashCode()) );
				} else {
					ps.print( v.toString() );
				}
				break;
			default:
				ps.print(v.tojstring());
			}
			if ( i+1 == top )
				ps.print(']');
			ps.print( " | " );
		}
		ps.print(varargs);
		ps.println();
	}

	

}
