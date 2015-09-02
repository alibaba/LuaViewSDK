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
package org.luaj.vm2.lib;

import org.luaj.vm2.Globals;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Print;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.Varargs;

/** 
 * Subclass of {@link LibFunction} which implements the lua standard {@code debug} 
 * library. 
 * <p> 
 * The debug library in luaj tries to emulate the behavior of the corresponding C-based lua library.
 * To do this, it must maintain a separate stack of calls to {@link LuaClosure} and {@link LibFunction} 
 * instances.  
 * Especially when lua-to-java bytecode compiling is being used
 * via a {@link LuaCompiler} such as {@link LuaJC}, 
 * this cannot be done in all cases.  
 * <p> 
 * Typically, this library is included as part of a call to either 
 * {@link JsePlatform#debugGlobals()} or {@link JmePlatform#debugGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.debugGlobals();
 * System.out.println( globals.get("debug").get("traceback").call() );
 * } </pre>
 * <p>
 * To instantiate and use it directly, 
 * link it into your globals table via {@link LuaValue#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new DebugLib());
 * System.out.println( globals.get("debug").get("traceback").call() );
 * } </pre>
 * <p>
 * @see LibFunction
 * @see JsePlatform
 * @see JmePlatform
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.10">Lua 5.2 Debug Lib Reference</a>
 */
public class DebugLib extends TwoArgFunction {
	public static boolean CALLS;
	public static boolean TRACE;
	static {
		try { CALLS = (null != System.getProperty("CALLS")); } catch (Exception e) {}
		try { TRACE = (null != System.getProperty("TRACE")); } catch (Exception e) {}
	}
	
	private static final LuaString LUA             = valueOf("Lua");  
	private static final LuaString QMARK           = valueOf("?");  
	private static final LuaString CALL            = valueOf("call");  
	private static final LuaString LINE            = valueOf("line");  
	private static final LuaString COUNT           = valueOf("count");  
	private static final LuaString RETURN          = valueOf("return");
	
	private static final LuaString FUNC            = valueOf("func");  
	private static final LuaString ISTAILCALL      = valueOf("istailcall");  
	private static final LuaString ISVARARG        = valueOf("isvararg");  
	private static final LuaString NUPS            = valueOf("nups");  
	private static final LuaString NPARAMS         = valueOf("nparams");  
	private static final LuaString NAME            = valueOf("name");  
	private static final LuaString NAMEWHAT        = valueOf("namewhat");  
	private static final LuaString WHAT            = valueOf("what");  
	private static final LuaString SOURCE          = valueOf("source");  
	private static final LuaString SHORT_SRC       = valueOf("short_src");  
	private static final LuaString LINEDEFINED     = valueOf("linedefined");  
	private static final LuaString LASTLINEDEFINED = valueOf("lastlinedefined");  
	private static final LuaString CURRENTLINE     = valueOf("currentline");  
	private static final LuaString ACTIVELINES     = valueOf("activelines");  

	Globals globals;
	
	public LuaValue call(LuaValue modname, LuaValue env) {
		globals = env.checkglobals();
		globals.debuglib = this;
		LuaTable debug = new LuaTable();
		debug.set("debug", new debug());
		debug.set("gethook", new gethook());
		debug.set("getinfo", new getinfo());
		debug.set("getlocal", new getlocal());
		debug.set("getmetatable", new getmetatable());
		debug.set("getregistry", new getregistry());
		debug.set("getupvalue", new getupvalue());
		debug.set("getuservalue", new getuservalue());
		debug.set("sethook", new sethook());
		debug.set("setlocal", new setlocal());
		debug.set("setmetatable", new setmetatable());
		debug.set("setupvalue", new setupvalue());
		debug.set("setuservalue", new setuservalue());
		debug.set("traceback", new traceback());
		debug.set("upvalueid", new upvalueid());
		debug.set("upvaluejoin", new upvaluejoin());
		env.set("debug", debug);
		env.get("package").get("loaded").set("debug", debug);
		return debug;
	}

	// debug.debug()
	static final class debug extends ZeroArgFunction { 
		public LuaValue call() {
			return NONE;
		}
	}

	// debug.gethook ([thread])
	final class gethook extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			LuaThread t = args.narg() > 0 ? args.checkthread(1): globals.running;
			return varargsOf(
					t.hookfunc != null? t.hookfunc: NIL,
					valueOf((t.hookcall?"c":"")+(t.hookline?"l":"")+(t.hookrtrn?"r":"")),
					valueOf(t.hookcount));
		}
	}

	//	debug.getinfo ([thread,] f [, what])
	final class getinfo extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			int a=1;
			LuaThread thread = args.isthread(a)? args.checkthread(a++): globals.running; 
			LuaValue func = args.arg(a++);
			String what = args.optjstring(a++, "flnStu");
			DebugLib.CallStack callstack = callstack(thread);

			// find the stack info
			DebugLib.CallFrame frame;
			if ( func.isnumber() ) {
				frame = callstack.getCallFrame(func.toint());
				if (frame == null)
					return NONE;
				func = frame.f;
			} else if ( func.isfunction() ) {
				frame = callstack.findCallFrame(func);
			} else {
				return argerror(a-2, "function or level");
			}

			// start a table
			DebugInfo ar = callstack.auxgetinfo(what, (LuaFunction) func, frame);
			LuaTable info = new LuaTable();
			if (what.indexOf('S') >= 0) {
				info.set(WHAT, LUA);
				info.set(SOURCE, valueOf(ar.source));
				info.set(SHORT_SRC, valueOf(ar.short_src));
				info.set(LINEDEFINED, valueOf(ar.linedefined));
				info.set(LASTLINEDEFINED, valueOf(ar.lastlinedefined));
			}
			if (what.indexOf('l') >= 0) {
				info.set( CURRENTLINE, valueOf(ar.currentline) );
			}
			if (what.indexOf('u') >= 0) {
				info.set(NUPS, valueOf(ar.nups));
				info.set(NPARAMS, valueOf(ar.nparams));
				info.set(ISVARARG, ar.isvararg? ONE: ZERO);
			}
			if (what.indexOf('n') >= 0) {
				info.set(NAME, LuaValue.valueOf(ar.name!=null? ar.name: "?"));
				info.set(NAMEWHAT, LuaValue.valueOf(ar.namewhat));
			}
			if (what.indexOf('t') >= 0) {
				info.set(ISTAILCALL, ZERO);
			}
			if (what.indexOf('L') >= 0) {
				LuaTable lines = new LuaTable();
				info.set(ACTIVELINES, lines);
				DebugLib.CallFrame cf;
				for (int l = 1; (cf=callstack.getCallFrame(l)) != null; ++l)
					if (cf.f == func)
						lines.insert(-1, valueOf(cf.currentline()));
			}
			if (what.indexOf('f') >= 0) {
				if (func != null)
					info.set( FUNC, func );
			}
			return info;
		}
	}

	//	debug.getlocal ([thread,] f, local)
	final class getlocal extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			int a=1;
			LuaThread thread = args.isthread(a)? args.checkthread(a++): globals.running; 
			int level = args.checkint(a++);
			int local = args.checkint(a++);
			CallFrame f = callstack(thread).getCallFrame(level);
			return f != null? f.getLocal(local): NONE;
		}
	}

	//	debug.getmetatable (value)
	final class getmetatable extends LibFunction {
		public LuaValue call(LuaValue v) {
			LuaValue mt = v.getmetatable();
			return mt != null? mt: NIL;
		}
	}

	//	debug.getregistry ()
	final class getregistry extends ZeroArgFunction {
		public LuaValue call() {
			return globals;
		}
	}

	//	debug.getupvalue (f, up)
	static final class getupvalue extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			LuaValue func = args.checkfunction(1);
			int up = args.checkint(2);
			if ( func instanceof LuaClosure ) {
				LuaClosure c = (LuaClosure) func;
				LuaString name = findupvalue(c, up);
				if ( name != null ) {
					return varargsOf(name, c.upValues[up-1].getValue() );
				}
			}
			return NIL;
		}
	}

	//	debug.getuservalue (u)
	static final class getuservalue extends LibFunction { 
		public LuaValue call(LuaValue u) {
			return u.isuserdata()? u: NIL;
		}
	}
	
	
	// debug.sethook ([thread,] hook, mask [, count])
	final class sethook extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			int a=1;
			LuaThread t = args.isthread(a)? args.checkthread(a++): globals.running; 
			LuaValue func    = args.optfunction(a++, null);
			String str       = args.optjstring(a++,"");
			int count        = args.optint(a++,0);
			boolean call=false,line=false,rtrn=false;
			for ( int i=0; i<str.length(); i++ )
				switch ( str.charAt(i) ) {
					case 'c': call=true; break;
					case 'l': line=true; break;
					case 'r': rtrn=true; break;
				}
			t.hookfunc = func;
			t.hookcall = call;
			t.hookline = line;
			t.hookcount = count;
			t.hookrtrn = rtrn;
			return NONE;
		}
	}

	//	debug.setlocal ([thread,] level, local, value)
	final class setlocal extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			int a=1;
			LuaThread thread = args.isthread(a)? args.checkthread(a++): globals.running; 
			int level = args.checkint(a++);
			int local = args.checkint(a++);
			LuaValue value = args.arg(a++);
			CallFrame f = callstack(thread).getCallFrame(level); 
			return f != null? f.setLocal(local, value): NONE;
		}
	}

	//	debug.setmetatable (value, table)
	final class setmetatable extends TwoArgFunction { 
		public LuaValue call(LuaValue value, LuaValue table) {
			LuaValue mt = table.opttable(null);
			switch ( value.type() ) {
				case TNIL:      LuaNil.s_metatable      = mt; break;
				case TNUMBER:   LuaNumber.s_metatable   = mt; break;
				case TBOOLEAN:  LuaBoolean.s_metatable  = mt; break;
				case TSTRING:   LuaString.s_metatable   = mt; break;
				case TFUNCTION: LuaFunction.s_metatable = mt; break;
				case TTHREAD:   LuaThread.s_metatable   = mt; break;
				default: value.setmetatable( mt );
			}
			return value;
		}
	}

	//	debug.setupvalue (f, up, value)
	final class setupvalue extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			LuaValue func = args.checkfunction(1);
			int up = args.checkint(2);
			LuaValue value = args.arg(3);
			if ( func instanceof LuaClosure ) {
				LuaClosure c = (LuaClosure) func;
				LuaString name = findupvalue(c, up);
				if ( name != null ) {
					c.upValues[up-1].setValue(value);
					return name;
				}
			}
			return NIL;
		}
	}

	//	debug.setuservalue (udata, value)
	final class setuservalue extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			Object o = args.checkuserdata(1);
			LuaValue v = args.checkvalue(2);
			LuaUserdata u = (LuaUserdata)args.arg1();
			u.m_instance = v.checkuserdata();
			u.m_metatable = v.getmetatable();
			return NONE;
		}
	}
	
	//	debug.traceback ([thread,] [message [, level]])
	final class traceback extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			int a=1;
			LuaThread thread = args.isthread(a)? args.checkthread(a++): globals.running; 
			String message = args.optjstring(a++, null);
			int level = args.optint(a++,1);
			String tb = callstack(thread).traceback(level);
			return valueOf(message!=null? message+"\n"+tb: tb);
		}
	}
	
	//	debug.upvalueid (f, n)
	final class upvalueid extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			LuaValue func = args.checkfunction(1);
			int up = args.checkint(2);
			if ( func instanceof LuaClosure ) {
				LuaClosure c = (LuaClosure) func;
				if ( c.upValues != null && up > 0 && up <= c.upValues.length ) {
					return valueOf(c.upValues[up-1].hashCode());
				}
			}
			return NIL;
		}
	}

	//	debug.upvaluejoin (f1, n1, f2, n2)
	final class upvaluejoin extends VarArgFunction { 
		public Varargs invoke(Varargs args) {
			LuaClosure f1 = args.checkclosure(1);
			int n1 = args.checkint(2);
			LuaClosure f2 = args.checkclosure(3);
			int n2 = args.checkint(4);
			if (n1 < 1 || n1 > f1.upValues.length)
				argerror("index out of range");
			if (n2 < 1 || n2 > f2.upValues.length)
				argerror("index out of range");
			f1.upValues[n1-1] = f2.upValues[n2-1];
			return NONE;
		}
	}

	public void onCall(LuaFunction f) {
		LuaThread t = globals.running;
		if (t.inhook) return;
		callstack().onCall(f);
		if (t.hookcall && t.hookfunc != null) 
			callHook(CALL, NIL);
	}

	public void onCall(LuaClosure c, Varargs varargs, LuaValue[] stack) {
		LuaThread t = globals.running;
		if (t.inhook) return;
		callstack().onCall(c, varargs, stack);
		if (t.hookcall && t.hookfunc != null) 
			callHook(CALL, NIL);
	}

	public void onInstruction(int pc, Varargs v, int top) {
		LuaThread t = globals.running;
		if (t.inhook) return;
		callstack().onInstruction(pc, v, top);
		if (t.hookfunc == null) return;
		if (t.hookcount > 0)
			if (++t.bytecodes % t.hookcount == 0)
				callHook(COUNT, NIL);
		if (t.hookline) {
			int newline = callstack().currentline();
			if ( newline != t.lastline ) {
				t.lastline = newline;
				callHook(LINE, LuaValue.valueOf(newline));
			}
		}
	}

	public void onReturn() {
		LuaThread t = globals.running;
		if (t.inhook) return;
		callstack().onReturn();
		if (t.hookcall && t.hookfunc != null)
			callHook(RETURN, NIL);
	}

	public String traceback(int level) {
		return callstack().traceback(level);
	}
	
	void callHook(LuaValue type, LuaValue arg) {
		LuaThread t = globals.running;
		t.inhook = true;
		try {
			t.hookfunc.call(type, arg);
		} catch (LuaError e) {
			throw e;
		} catch (RuntimeException e) {
			throw new LuaError(e);
		} finally {
			t.inhook = false;
		}
	}
	
	CallStack callstack() {
		return callstack(globals.running);
	}

	CallStack callstack(LuaThread t) {
		if (t.callstack == null)
			t.callstack = new CallStack();
		return (CallStack) t.callstack;
	}

	static class DebugInfo {
		  String name;	/* (n) */
		  String namewhat;	/* (n) 'global', 'local', 'field', 'method' */
		  String what;	/* (S) 'Lua', 'C', 'main', 'tail' */
		  String source;	/* (S) */
		  int currentline;	/* (l) */
		  int linedefined;	/* (S) */
		  int lastlinedefined;	/* (S) */
		  short nups;	/* (u) number of upvalues */
		  short nparams;/* (u) number of parameters */
		  boolean isvararg;        /* (u) */
		  boolean istailcall;	/* (t) */
		  String short_src; /* (S) */
		  CallFrame cf;  /* active function */

		public void funcinfo(LuaFunction f) {
			if (f.isclosure()) {
				Prototype p = f.checkclosure().p;
				this.source = p.source != null ? p.source.tojstring() : "=?";
				this.linedefined = p.linedefined;
				this.lastlinedefined = p.lastlinedefined;
				this.what = (this.linedefined == 0) ? "main" : "Lua";
				this.short_src = p.shortsource();
			} else {
				this.source = "=[Java]";
				this.linedefined = -1;
				this.lastlinedefined = -1;
				this.what = "Java";
				this.short_src = f.name();
			}
		}
	}
	
	public static class CallStack {
		final static CallFrame[] EMPTY = {};
		CallFrame[] frame = EMPTY;
		int calls  = 0;

		CallStack() {}
		
		int currentline() {
			return calls > 0? frame[calls-1].currentline(): -1;
		}

		private CallFrame pushcall() {
			if (calls >= frame.length) {
				int n = Math.max(4, frame.length * 3 / 2);
				CallFrame[] f = new CallFrame[n];
				System.arraycopy(frame, 0, f, 0, frame.length);
				for (int i = frame.length; i < n; ++i)
					f[i] = new CallFrame();
				frame = f;
				for (int i = 1; i < n; ++i)
					f[i].previous = f[i-1];
			}
			return frame[calls++];
		}
		
		final void onCall(LuaFunction function) {
			pushcall().set(function);
		}

		final void onCall(LuaClosure function, Varargs varargs, LuaValue[] stack) {
			pushcall().set(function, varargs, stack);
		}
		
		final void onReturn() {
			if (calls > 0)
				frame[--calls].reset();
		}
		
		final void onInstruction(int pc, Varargs v, int top) {
			frame[calls-1].instr(pc, v, top);
		}

		/**
		 * Get the traceback starting at a specific level.
		 * @param level
		 * @return String containing the traceback.
		 */
		String traceback(int level) {
			StringBuffer sb = new StringBuffer();
			sb.append( "stack traceback:" );
			for (DebugLib.CallFrame c; (c = getCallFrame(level++)) != null; ) {
				sb.append("\n\t");
				sb.append( c.shortsource() );
				sb.append( ':' );
				if (c.currentline() > 0)
					sb.append( c.currentline()+":" );
				sb.append( " in " );
				DebugInfo ar = auxgetinfo("n", c.f, c);
				if (c.linedefined() == 0)
					sb.append("main chunk");
				else if ( ar.name != null ) {
					sb.append( "function '" );
					sb.append( ar.name );
					sb.append( '\'' );
				} else {
					sb.append( "function <"+c.shortsource()+":"+c.linedefined()+">" );
				}
			}
			sb.append("\n\t[Java]: in ?");
			return sb.toString();
		}

		DebugLib.CallFrame getCallFrame(int level) {
			if (level < 1 || level > calls)
				return null;
			return frame[calls-level];
		}

		DebugLib.CallFrame findCallFrame(LuaValue func) {
			for (int i = 1; i <= calls; ++i)
				if (frame[calls-i].f == func)
					return frame[i];
			return null;
		}	


		DebugInfo auxgetinfo(String what, LuaFunction f, CallFrame ci) {
			DebugInfo ar = new DebugInfo();
			for (int i = 0, n = what.length(); i < n; ++i) {
				switch (what.charAt(i)) {
			      case 'S':
			    	  ar.funcinfo(f);
			    	  break;
			      case 'l':
			    	  ar.currentline = ci != null && ci.f.isclosure()? ci.currentline(): -1;
			    	  break;
			      case 'u':
			    	  if (f != null && f.isclosure()) {
			    		  Prototype p = f.checkclosure().p;
			    		  ar.nups = (short) p.upvalues.length;
			    		  ar.nparams = (short) p.numparams;
			    		  ar.isvararg = p.is_vararg != 0;
			    	  } else {
				    	  ar.nups = 0;
				    	  ar.isvararg = true;
				    	  ar.nparams = 0;
			    	  }
			    	  break;
			      case 't':
			    	  ar.istailcall = false;
			    	  break;
			      case 'n': {
			    	  /* calling function is a known Lua function? */
			    	  if (ci != null && ci.previous != null) {
			    		  if (ci.previous.f.isclosure()) {
			    			  NameWhat nw = getfuncname(ci.previous);
				    		  if (nw != null) {
				    			  ar.name = nw.name;
				    			  ar.namewhat = nw.namewhat;
				    		  }
			    		  }
			    	  }
			    	  if (ar.namewhat == null) {
			    		  ar.namewhat = "";  /* not found */
			    		  ar.name = null;
			    	  }
			    	  break;
			      }
			      case 'L':
			      case 'f':
			    	  break;
			      default:
			    	  // TODO: return bad status.
			    	  break;
				}
			}
			return ar;
		}		

	}

	static class CallFrame {
		LuaFunction f;
		int pc;
		int top;
		Varargs v;
		LuaValue[] stack;
		CallFrame previous;
		void set(LuaClosure function, Varargs varargs, LuaValue[] stack) {
			this.f = function;
			this.v = varargs;
			this.stack = stack;
		}
		public String shortsource() {
			return f.isclosure()? f.checkclosure().p.shortsource(): "[Java]";
		}
		void set(LuaFunction function) {
			this.f = function;
		}
		void reset() {
			this.f = null;
			this.v = null;
			this.stack = null;
		}
		void instr(int pc, Varargs v, int top) {
			this.pc = pc;
			this.v = v;
			this.top = top;
			if (TRACE)
				Print.printState(f.checkclosure(), pc, stack, top, v);
		}
		Varargs getLocal(int i) {
			LuaString name = getlocalname(i);
			if ( name != null )
				return varargsOf( name, stack[i-1] );
			else
				return NIL;
		}
		Varargs setLocal(int i, LuaValue value) {
			LuaString name = getlocalname(i);
			if ( name != null ) {
				stack[i-1] = value;
				return name;
			} else {
				return NIL;
			}
		}
		int currentline() {
			if ( !f.isclosure() ) return -1;
			int[] li = f.checkclosure().p.lineinfo;
			return li==null || pc<0 || pc>=li.length? -1: li[pc]; 
		}
		String sourceline() {
			if ( !f.isclosure() ) return f.tojstring();
			return f.checkclosure().p.shortsource() + ":" + currentline();
		}
		private int linedefined() {
			return f.isclosure()? f.checkclosure().p.linedefined: -1;
		}
		LuaString getlocalname(int index) {
			if ( !f.isclosure() ) return null;
			return f.checkclosure().p.getlocalname(index, pc);
		}
	}

	static LuaString findupvalue(LuaClosure c, int up) {
		if ( c.upValues != null && up > 0 && up <= c.upValues.length ) {
			if ( c.p.upvalues != null && up <= c.p.upvalues.length )
				return c.p.upvalues[up-1].name;
			else
				return LuaString.valueOf( "."+up );
		}
		return null;
	}
	
	static void lua_assert(boolean x) {
		if (!x) throw new RuntimeException("lua_assert failed");
	}	
	
	static class NameWhat {
		final String name;
		final String namewhat;
		NameWhat(String name, String namewhat) {
			this.name = name;
			this.namewhat = namewhat;
		}
	}

	// Return the name info if found, or null if no useful information could be found.
	static NameWhat getfuncname(DebugLib.CallFrame frame) {
		if (!frame.f.isclosure())
			return new NameWhat(frame.f.classnamestub(), "Java");
		Prototype p = frame.f.checkclosure().p;
		int pc = frame.pc;
		int i = p.code[pc]; /* calling instruction */
		LuaString tm;
		switch (Lua.GET_OPCODE(i)) {
			case Lua.OP_CALL:
			case Lua.OP_TAILCALL: /* get function name */
				return getobjname(p, pc, Lua.GETARG_A(i));
			case Lua.OP_TFORCALL: /* for iterator */
		    	return new NameWhat("(for iterator)", "(for iterator");
		    /* all other instructions can call only through metamethods */
		    case Lua.OP_SELF:
		    case Lua.OP_GETTABUP:
		    case Lua.OP_GETTABLE: tm = LuaValue.INDEX; break;
		    case Lua.OP_SETTABUP:
		    case Lua.OP_SETTABLE: tm = LuaValue.NEWINDEX; break;
		    case Lua.OP_EQ: tm = LuaValue.EQ; break;
		    case Lua.OP_ADD: tm = LuaValue.ADD; break;
		    case Lua.OP_SUB: tm = LuaValue.SUB; break;
		    case Lua.OP_MUL: tm = LuaValue.MUL; break;
		    case Lua.OP_DIV: tm = LuaValue.DIV; break;
		    case Lua.OP_MOD: tm = LuaValue.MOD; break;
		    case Lua.OP_POW: tm = LuaValue.POW; break;
		    case Lua.OP_UNM: tm = LuaValue.UNM; break;
		    case Lua.OP_LEN: tm = LuaValue.LEN; break;
		    case Lua.OP_LT: tm = LuaValue.LT; break;
		    case Lua.OP_LE: tm = LuaValue.LE; break;
		    case Lua.OP_CONCAT: tm = LuaValue.CONCAT; break;
		    default:
		      return null;  /* else no useful name can be found */
		}
		return new NameWhat( tm.tojstring(), "metamethod" );
	}
	
	// return NameWhat if found, null if not
	public static NameWhat getobjname(Prototype p, int lastpc, int reg) {
		int pc = lastpc; // currentpc(L, ci);
		LuaString name = p.getlocalname(reg + 1, pc);
		if (name != null) /* is a local? */
			return new NameWhat( name.tojstring(), "local" );

		/* else try symbolic execution */
		pc = findsetreg(p, lastpc, reg);
		if (pc != -1) { /* could find instruction? */
			int i = p.code[pc];
			switch (Lua.GET_OPCODE(i)) {
			case Lua.OP_MOVE: {
				int a = Lua.GETARG_A(i);
				int b = Lua.GETARG_B(i); /* move from `b' to `a' */
				if (b < a)
					return getobjname(p, pc, b); /* get name for `b' */
				break;
			}
			case Lua.OP_GETTABUP:
			case Lua.OP_GETTABLE: {
				int k = Lua.GETARG_C(i); /* key index */
				int t = Lua.GETARG_B(i); /* table index */
		        LuaString vn = (Lua.GET_OPCODE(i) == Lua.OP_GETTABLE)  /* name of indexed variable */
	                    ? p.getlocalname(t + 1, pc)
	                    : (t < p.upvalues.length ? p.upvalues[t].name : QMARK);
				name = kname(p, k);
				return new NameWhat( name.tojstring(), vn != null && vn.eq_b(ENV)? "global": "field" );
			}
			case Lua.OP_GETUPVAL: {
				int u = Lua.GETARG_B(i); /* upvalue index */
				name = u < p.upvalues.length ? p.upvalues[u].name : QMARK;
				return new NameWhat( name.tojstring(), "upvalue" );
			}
		    case Lua.OP_LOADK:
		    case Lua.OP_LOADKX: {
		        int b = (Lua.GET_OPCODE(i) == Lua.OP_LOADK) ? Lua.GETARG_Bx(i)
		                                                    : Lua.GETARG_Ax(p.code[pc + 1]);
		        if (p.k[b].isstring()) {
		          name = p.k[b].strvalue();
		          return new NameWhat( name.tojstring(), "constant" );
		        }
		        break;
		    }
			case Lua.OP_SELF: {
				int k = Lua.GETARG_C(i); /* key index */
				name = kname(p, k);
				return new NameWhat( name.tojstring(), "method" );
			}
			default:
				break;
			}
		}
		return null; /* no useful name found */
	}

	static LuaString kname(Prototype p, int c) {
		if (Lua.ISK(c) && p.k[Lua.INDEXK(c)].isstring())
			return p.k[Lua.INDEXK(c)].strvalue();
		else
			return QMARK;
	}

	/*
	** try to find last instruction before 'lastpc' that modified register 'reg'
	*/
	static int findsetreg (Prototype p, int lastpc, int reg) {
	  int pc;
	  int setreg = -1;  /* keep last instruction that changed 'reg' */
	  for (pc = 0; pc < lastpc; pc++) {
	    int i = p.code[pc];
	    int op = Lua.GET_OPCODE(i);
	    int a = Lua.GETARG_A(i);
	    switch (op) {
	      case Lua.OP_LOADNIL: {
	        int b = Lua.GETARG_B(i);
	        if (a <= reg && reg <= a + b)  /* set registers from 'a' to 'a+b' */
	          setreg = pc;
	        break;
	      }
	      case Lua.OP_TFORCALL: {
	        if (reg >= a + 2) setreg = pc;  /* affect all regs above its base */
	        break;
	      }
	      case Lua.OP_CALL:
	      case Lua.OP_TAILCALL: {
	        if (reg >= a) setreg = pc;  /* affect all registers above base */
	        break;
	      }
	      case Lua.OP_JMP: {
	        int b = Lua.GETARG_sBx(i);
	        int dest = pc + 1 + b;
	        /* jump is forward and do not skip `lastpc'? */
	        if (pc < dest && dest <= lastpc)
	          pc += b;  /* do the jump */
	        break;
	      }
	      case Lua.OP_TEST: {
	        if (reg == a) setreg = pc;  /* jumped code can change 'a' */
	        break;
	      }
	      default:
	        if (Lua.testAMode(op) && reg == a)  /* any instruction that set A */
	          setreg = pc;
	        break;
	    }
	  }
	  return setreg;
	}
}
