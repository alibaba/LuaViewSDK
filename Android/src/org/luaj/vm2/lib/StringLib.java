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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.Buffer;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.DumpState;

/** 
 * Subclass of {@link LibFunction} which implements the lua standard {@code string} 
 * library. 
 * <p>
 * Typically, this library is included as part of a call to either 
 * {@link JsePlatform#standardGlobals()} or {@link JmePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("string").get("upper").call( LuaValue.valueOf("abcde") ) );
 * } </pre>
 * <p>
 * To instantiate and use it directly, 
 * link it into your globals table via {@link LuaValue#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new StringLib());
 * System.out.println( globals.get("string").get("upper").call( LuaValue.valueOf("abcde") ) );
 * } </pre>
 * <p>
 * This is a direct port of the corresponding library in C.
 * @see LibFunction
 * @see JsePlatform
 * @see JmePlatform
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.4">Lua 5.2 String Lib Reference</a>
 */
public class StringLib extends TwoArgFunction {

	public static LuaTable instance;
	
	public StringLib() {
	}

	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaTable t = new LuaTable();
		bind(t, StringLib1.class, new String[] {
			"dump", "len", "lower", "reverse", "upper", } );
		bind(t, StringLibV.class, new String[] {
			"byte", "char", "find", "format", 
			"gmatch", "gsub", "match", "rep", 
			"sub"} );
		env.set("string", t);
		instance = t;
		if ( LuaString.s_metatable == null )
			LuaString.s_metatable = tableOf( new LuaValue[] { INDEX, t } );
		env.get("package").get("loaded").set("string", t);
		return t;
	}
	
	static final class StringLib1 extends OneArgFunction {
		public LuaValue call(LuaValue arg) {
			switch ( opcode ) { 
			case 0: return dump(arg); // dump (function)
			case 1: return StringLib.len(arg); // len (function)
			case 2: return lower(arg); // lower (function)
			case 3: return reverse(arg); // reverse (function)
			case 4: return upper(arg); // upper (function)
			}
			return NIL;
		}
	}

	static final class StringLibV extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			switch ( opcode ) {
			case 0: return StringLib.byte_( args );
			case 1: return StringLib.char_( args );
			case 2: return StringLib.find( args );
			case 3: return StringLib.format( args );
			case 4: return StringLib.gmatch( args );
			case 5: return StringLib.gsub( args );
			case 6: return StringLib.match( args );
			case 7: return StringLib.rep( args );
			case 8: return StringLib.sub( args );
			}
			return NONE;
		}
	}

	/**
	 * string.byte (s [, i [, j]]) 
	 * 
	 * Returns the internal numerical codes of the
	 * characters s[i], s[i+1], ..., s[j]. The default value for i is 1; the
	 * default value for j is i.
	 * 
	 * Note that numerical codes are not necessarily portable across platforms.
	 * 
	 * @param args the calling args
	 */
	static Varargs byte_( Varargs args ) {
		LuaString s = args.checkstring(1);
		int l = s.m_length;
		int posi = posrelat( args.optint(2,1), l );
		int pose = posrelat( args.optint(3,posi), l );
		int n,i;
		if (posi <= 0) posi = 1;
		if (pose > l) pose = l;
		if (posi > pose) return NONE;  /* empty interval; return no values */
		n = (int)(pose -  posi + 1);
		if (posi + n <= pose)  /* overflow? */
		    error("string slice too long");
		LuaValue[] v = new LuaValue[n];
		for (i=0; i<n; i++)
			v[i] = valueOf(s.luaByte(posi+i-1));
		return varargsOf(v);
	}

	/** 
	 * string.char (...)
	 * 
	 * Receives zero or more integers. Returns a string with length equal 
	 * to the number of arguments, in which each character has the internal 
	 * numerical code equal to its corresponding argument. 
	 * 
	 * Note that numerical codes are not necessarily portable across platforms.
	 * 
	 * @param args the calling VM
	 */
	public static Varargs char_( Varargs args) {
		int n = args.narg();
		byte[] bytes = new byte[n];
		for ( int i=0, a=1; i<n; i++, a++ ) {
			int c = args.checkint(a);
			if (c<0 || c>=256) argerror(a, "invalid value");
			bytes[i] = (byte) c;
		}
		return LuaString.valueOf( bytes );
	}
		
	/** 
	 * string.dump (function)
	 * 
	 * Returns a string containing a binary representation of the given function, 
	 * so that a later loadstring on this string returns a copy of the function. 
	 * function must be a Lua function without upvalues.
	 *  
	 * TODO: port dumping code as optional add-on
	 */
	static LuaValue dump( LuaValue arg ) {
		LuaValue f = arg.checkfunction();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DumpState.dump( ((LuaClosure)f).p, baos, true );
			return LuaString.valueOf(baos.toByteArray());
		} catch (IOException e) {
			return error( e.getMessage() );
		}
	}

	/** 
	 * string.find (s, pattern [, init [, plain]])
	 * 
	 * Looks for the first match of pattern in the string s. 
	 * If it finds a match, then find returns the indices of s 
	 * where this occurrence starts and ends; otherwise, it returns nil. 
	 * A third, optional numerical argument init specifies where to start the search; 
	 * its default value is 1 and may be negative. A value of true as a fourth, 
	 * optional argument plain turns off the pattern matching facilities, 
	 * so the function does a plain "find substring" operation, 
	 * with no characters in pattern being considered "magic". 
	 * Note that if plain is given, then init must be given as well.
	 * 
	 * If the pattern has captures, then in a successful match the captured values 
	 * are also returned, after the two indices.
	 */
	static Varargs find( Varargs args ) {
		return str_find_aux( args, true );
	}

	/** 
	 * string.format (formatstring, ...)
	 * 
	 * Returns a formatted version of its variable number of arguments following 
	 * the description given in its first argument (which must be a string). 
	 * The format string follows the same rules as the printf family of standard C functions. 
	 * The only differences are that the options/modifiers *, l, L, n, p, and h are not supported 
	 * and that there is an extra option, q. The q option formats a string in a form suitable 
	 * to be safely read back by the Lua interpreter: the string is written between double quotes, 
	 * and all double quotes, newlines, embedded zeros, and backslashes in the string are correctly 
	 * escaped when written. For instance, the call
	 *   string.format('%q', 'a string with "quotes" and \n new line')
	 *
	 * will produce the string:
	 *    "a string with \"quotes\" and \
	 *    new line"
	 *    
	 * The options c, d, E, e, f, g, G, i, o, u, X, and x all expect a number as argument, 
	 * whereas q and s expect a string. 
	 * 
	 * This function does not accept string values containing embedded zeros, 
	 * except as arguments to the q option. 
	 */
	static Varargs format( Varargs args ) {
		LuaString fmt = args.checkstring( 1 );
		final int n = fmt.length();
		Buffer result = new Buffer(n);
		int arg = 1;
		int c;
		
		for ( int i = 0; i < n; ) {
			switch ( c = fmt.luaByte( i++ ) ) {
			case '\n':
				result.append( "\n" );
				break;
			default:
				result.append( (byte) c );
				break;
			case L_ESC:
				if ( i < n ) {
					if ( ( c = fmt.luaByte( i ) ) == L_ESC ) {
						++i;
						result.append( (byte)L_ESC );
					} else {
						arg++;
						FormatDesc fdsc = new FormatDesc(args, fmt, i );
						i += fdsc.length;
						switch ( fdsc.conversion ) {
						case 'c':
							fdsc.format( result, (byte)args.checkint( arg ) );
							break;
						case 'i':
						case 'd':
							fdsc.format( result, args.checkint( arg ) );
							break;
						case 'o':
						case 'u':
						case 'x':
						case 'X':
							fdsc.format( result, args.checklong( arg ) );
							break;
						case 'e':
						case 'E':
						case 'f':
						case 'g':
						case 'G':
							fdsc.format( result, args.checkdouble( arg ) );
							break;
						case 'q':
							addquoted( result, args.checkstring( arg ) );
							break;
						case 's': {
							LuaString s = args.checkstring( arg );
							if ( fdsc.precision == -1 && s.length() >= 100 ) {
								result.append( s );
							} else {
								fdsc.format( result, s );
							}
						}	break;
						default:
							error("invalid option '%"+(char)fdsc.conversion+"' to 'format'");
							break;
						}
					}
				}
			}
		}
		
		return result.tostring();
	}
	
	private static void addquoted(Buffer buf, LuaString s) {
		int c;
		buf.append( (byte) '"' );
		for ( int i = 0, n = s.length(); i < n; i++ ) {
			switch ( c = s.luaByte( i ) ) {
			case '"': case '\\':  case '\n':
				buf.append( (byte)'\\' );
				buf.append( (byte)c );
				break;
			default:
				if (c <= 0x1F || c == 0x7F) {
					buf.append( (byte) '\\' );
					if (i+1 == n || s.luaByte(i+1) < '0' || s.luaByte(i+1) > '9') {
						buf.append(Integer.toString(c));
					} else {
						buf.append( (byte) '0' );
						buf.append( (byte) (char) ('0' + c / 10) );
						buf.append( (byte) (char) ('0' + c % 10) );
					}
				} else {
					buf.append((byte) c);
				}
				break;
			}
		}
		buf.append( (byte) '"' );
	}
	
	private static final String FLAGS = "-+ #0";
	
	static class FormatDesc {
		
		private boolean leftAdjust;
		private boolean zeroPad;
		private boolean explicitPlus;
		private boolean space;
		private boolean alternateForm;
		private static final int MAX_FLAGS = 5;
		
		private int width;
		private int precision;
		
		public final int conversion;
		public final int length;
		
		public FormatDesc(Varargs args, LuaString strfrmt, final int start) {
			int p = start, n = strfrmt.length();
			int c = 0;
			
			boolean moreFlags = true;
			while ( moreFlags ) {
				switch ( c = ( (p < n) ? strfrmt.luaByte( p++ ) : 0 ) ) {
				case '-': leftAdjust = true; break;
				case '+': explicitPlus = true; break;
				case ' ': space = true; break;
				case '#': alternateForm = true; break;
				case '0': zeroPad = true; break;
				default: moreFlags = false; break;
				}
			}
			if ( p - start > MAX_FLAGS )
				error("invalid format (repeated flags)");
			
			width = -1;
			if ( Character.isDigit( (char)c ) ) {
				width = c - '0';
				c = ( (p < n) ? strfrmt.luaByte( p++ ) : 0 );
				if ( Character.isDigit( (char) c ) ) {
					width = width * 10 + (c - '0');
					c = ( (p < n) ? strfrmt.luaByte( p++ ) : 0 );
				}
			}
			
			precision = -1;
			if ( c == '.' ) {
				c = ( (p < n) ? strfrmt.luaByte( p++ ) : 0 );
				if ( Character.isDigit( (char) c ) ) {
					precision = c - '0';
					c = ( (p < n) ? strfrmt.luaByte( p++ ) : 0 );
					if ( Character.isDigit( (char) c ) ) {
						precision = precision * 10 + (c - '0');
						c = ( (p < n) ? strfrmt.luaByte( p++ ) : 0 );
					}
				}
			}
			
			if ( Character.isDigit( (char) c ) )
				error("invalid format (width or precision too long)");
			
			zeroPad &= !leftAdjust; // '-' overrides '0'
			conversion = c;
			length = p - start;
		}
		
		public void format(Buffer buf, byte c) {
			// TODO: not clear that any of width, precision, or flags apply here.
			buf.append(c);
		}
		
		public void format(Buffer buf, long number) {
			String digits;
			
			if ( number == 0 && precision == 0 ) {
				digits = "";
			} else {
				int radix;
				switch ( conversion ) {
				case 'x':
				case 'X':
					radix = 16;
					break;
				case 'o':
					radix = 8;
					break;
				default:
					radix = 10;
					break;
				}
				digits = Long.toString( number, radix );
				if ( conversion == 'X' )
					digits = digits.toUpperCase();
			}
			
			int minwidth = digits.length();
			int ndigits = minwidth;
			int nzeros;
			
			if ( number < 0 ) {
				ndigits--;
			} else if ( explicitPlus || space ) {
				minwidth++;
			}
			
			if ( precision > ndigits )
				nzeros = precision - ndigits;
			else if ( precision == -1 && zeroPad && width > minwidth )
				nzeros = width - minwidth;
			else
				nzeros = 0;
			
			minwidth += nzeros;
			int nspaces = width > minwidth ? width - minwidth : 0;
			
			if ( !leftAdjust )
				pad( buf, ' ', nspaces );
			
			if ( number < 0 ) {
				if ( nzeros > 0 ) {
					buf.append( (byte)'-' );
					digits = digits.substring( 1 );
				}
			} else if ( explicitPlus ) {
				buf.append( (byte)'+' );
			} else if ( space ) {
				buf.append( (byte)' ' );
			}
			
			if ( nzeros > 0 )
				pad( buf, '0', nzeros );
			
			buf.append( digits );
			
			if ( leftAdjust )
				pad( buf, ' ', nspaces );
		}
		
		public void format(Buffer buf, double x) {
			// TODO
			buf.append( String.valueOf( x ) );
		}
		
		public void format(Buffer buf, LuaString s) {
			int nullindex = s.indexOf( (byte)'\0', 0 );
			if ( nullindex != -1 )
				s = s.substring( 0, nullindex );
			buf.append(s);
		}
		
		public static final void pad(Buffer buf, char c, int n) {
			byte b = (byte)c;
			while ( n-- > 0 )
				buf.append(b);
		}
	}
	
	/** 
	 * string.gmatch (s, pattern)
	 * 
	 * Returns an iterator function that, each time it is called, returns the next captures 
	 * from pattern over string s. If pattern specifies no captures, then the 
	 * whole match is produced in each call. 
	 * 
	 * As an example, the following loop
	 *   s = "hello world from Lua"
	 *   for w in string.gmatch(s, "%a+") do
	 *      print(w)
	 *   end
	 *   
	 * will iterate over all the words from string s, printing one per line. 
	 * The next example collects all pairs key=value from the given string into a table:
	 *   t = {}
	 *   s = "from=world, to=Lua"
	 *   for k, v in string.gmatch(s, "(%w+)=(%w+)") do
	 *     t[k] = v
	 *   end
	 *
	 * For this function, a '^' at the start of a pattern does not work as an anchor, 
	 * as this would prevent the iteration.
	 */
	static Varargs gmatch( Varargs args ) {
		LuaString src = args.checkstring( 1 );
		LuaString pat = args.checkstring( 2 );
		return new GMatchAux(args, src, pat);
	}

	static class GMatchAux extends VarArgFunction {
		private final int srclen;
		private final MatchState ms;
		private int soffset;
		public GMatchAux(Varargs args, LuaString src, LuaString pat) {
			this.srclen = src.length();
			this.ms = new MatchState(args, src, pat);
			this.soffset = 0;
		}
		public Varargs invoke(Varargs args) {
			for ( ; soffset<srclen; soffset++ ) {
				ms.reset();
				int res = ms.match(soffset, 0);
				if ( res >=0 ) {
					int soff = soffset;
					soffset = res;
					return ms.push_captures( true, soff, res );
				}
			}
			return NIL;
		}
	}

	
	/** 
	 * string.gsub (s, pattern, repl [, n])
	 * Returns a copy of s in which all (or the first n, if given) occurrences of the 
	 * pattern have been replaced by a replacement string specified by repl, which 
	 * may be a string, a table, or a function. gsub also returns, as its second value, 
	 * the total number of matches that occurred.
	 * 
	 * If repl is a string, then its value is used for replacement. 
	 * The character % works as an escape character: any sequence in repl of the form %n, 
	 * with n between 1 and 9, stands for the value of the n-th captured substring (see below). 
	 * The sequence %0 stands for the whole match. The sequence %% stands for a single %. 
	 *  
	 * If repl is a table, then the table is queried for every match, using the first capture 
	 * as the key; if the pattern specifies no captures, then the whole match is used as the key. 
	 * 
	 * If repl is a function, then this function is called every time a match occurs, 
	 * with all captured substrings passed as arguments, in order; if the pattern specifies 
	 * no captures, then the whole match is passed as a sole argument. 
	 * 
	 * If the value returned by the table query or by the function call is a string or a number, 
	 * then it is used as the replacement string; otherwise, if it is false or nil, 
	 * then there is no replacement (that is, the original match is kept in the string). 
	 * 
	 * Here are some examples:
	 * 	     x = string.gsub("hello world", "(%w+)", "%1 %1")
	 * 	     --> x="hello hello world world"
	 * 
	 *	     x = string.gsub("hello world", "%w+", "%0 %0", 1)
	 *	     --> x="hello hello world"
	 *
	 *	     x = string.gsub("hello world from Lua", "(%w+)%s*(%w+)", "%2 %1")
	 *	     --> x="world hello Lua from"
	 *
	 *	     x = string.gsub("home = $HOME, user = $USER", "%$(%w+)", os.getenv)
	 *	     --> x="home = /home/roberto, user = roberto"
	 *
	 *	     x = string.gsub("4+5 = $return 4+5$", "%$(.-)%$", function (s)
	 *	           return loadstring(s)()
	 *       end)
	 *	     --> x="4+5 = 9"
	 *
	 *	     local t = {name="lua", version="5.1"}
	 *	     x = string.gsub("$name-$version.tar.gz", "%$(%w+)", t)
	 *	     --> x="lua-5.1.tar.gz"
	 */
	static Varargs gsub( Varargs args ) {
		LuaString src = args.checkstring( 1 );
		final int srclen = src.length();
		LuaString p = args.checkstring( 2 );
		LuaValue repl = args.arg( 3 );
		int max_s = args.optint( 4, srclen + 1 );
		final boolean anchor = p.length() > 0 && p.charAt( 0 ) == '^';
		
		Buffer lbuf = new Buffer( srclen );
		MatchState ms = new MatchState( args, src, p );
		
		int soffset = 0;
		int n = 0;
		while ( n < max_s ) {
			ms.reset();
			int res = ms.match( soffset, anchor ? 1 : 0 );
			if ( res != -1 ) {
				n++;
				ms.add_value( lbuf, soffset, res, repl );
			}
			if ( res != -1 && res > soffset )
				soffset = res;
			else if ( soffset < srclen )
				lbuf.append( (byte) src.luaByte( soffset++ ) );
			else
				break;
			if ( anchor )
				break;
		}
		lbuf.append( src.substring( soffset, srclen ) );
		return varargsOf(lbuf.tostring(), valueOf(n));
	}
	
	/** 
	 * string.len (s)
	 * 
	 * Receives a string and returns its length. The empty string "" has length 0. 
	 * Embedded zeros are counted, so "a\000bc\000" has length 5. 
	 */
	static LuaValue len( LuaValue arg ) {
		return arg.checkstring().len();
	}

	/** 
	 * string.lower (s)
	 * 
	 * Receives a string and returns a copy of this string with all uppercase letters 
	 * changed to lowercase. All other characters are left unchanged. 
	 * The definition of what an uppercase letter is depends on the current locale.
	 */
	static LuaValue lower( LuaValue arg ) {
		return valueOf( arg.checkjstring().toLowerCase() );
	}

	/**
	 * string.match (s, pattern [, init])
	 * 
	 * Looks for the first match of pattern in the string s. If it finds one,
	 * then match returns the captures from the pattern; otherwise it returns
	 * nil. If pattern specifies no captures, then the whole match is returned.
	 * A third, optional numerical argument init specifies where to start the
	 * search; its default value is 1 and may be negative.
	 */
	static Varargs match( Varargs args ) {
		return str_find_aux( args, false );
	}
	
	/**
	 * string.rep (s, n)
	 * 
	 * Returns a string that is the concatenation of n copies of the string s. 
	 */
	static Varargs rep( Varargs args ) {
		LuaString s = args.checkstring( 1 );
		int n = args.checkint( 2 );
		final byte[] bytes = new byte[ s.length() * n ];
		int len = s.length();
		for ( int offset = 0; offset < bytes.length; offset += len ) {
			s.copyInto( 0, bytes, offset, len );
		}
		return LuaString.valueOf( bytes );
	}

	/** 
	 * string.reverse (s)
	 * 
	 * Returns a string that is the string s reversed. 
	 */
	static LuaValue reverse( LuaValue arg ) {		
		LuaString s = arg.checkstring();
		int n = s.length();
		byte[] b = new byte[n];
		for ( int i=0, j=n-1; i<n; i++, j-- )
			b[j] = (byte) s.luaByte(i);
		return LuaString.valueOf( b );
	}

	/** 
	 * string.sub (s, i [, j])
	 * 
	 * Returns the substring of s that starts at i and continues until j; 
	 * i and j may be negative. If j is absent, then it is assumed to be equal to -1 
	 * (which is the same as the string length). In particular, the call 
	 *    string.sub(s,1,j) 
	 * returns a prefix of s with length j, and 
	 *   string.sub(s, -i) 
	 * returns a suffix of s with length i.
	 */
	static Varargs sub( Varargs args ) {
		final LuaString s = args.checkstring( 1 );
		final int l = s.length();
		
		int start = posrelat( args.checkint( 2 ), l );
		int end = posrelat( args.optint( 3, -1 ), l );
		
		if ( start < 1 )
			start = 1;
		if ( end > l )
			end = l;
		
		if ( start <= end ) {
			return s.substring( start-1 , end );
		} else {
			return EMPTYSTRING;
		}
	}
	
	/** 
	 * string.upper (s)
	 * 
	 * Receives a string and returns a copy of this string with all lowercase letters 
	 * changed to uppercase. All other characters are left unchanged. 
	 * The definition of what a lowercase letter is depends on the current locale.	
	 */
	static LuaValue upper( LuaValue arg ) {
		return valueOf(arg.checkjstring().toUpperCase());
	}
	
	/**
	 * This utility method implements both string.find and string.match.
	 */
	static Varargs str_find_aux( Varargs args, boolean find ) {
		LuaString s = args.checkstring( 1 );
		LuaString pat = args.checkstring( 2 );
		int init = args.optint( 3, 1 );
		
		if ( init > 0 ) {
			init = Math.min( init - 1, s.length() );
		} else if ( init < 0 ) {
			init = Math.max( 0, s.length() + init );
		}
		
		boolean fastMatch = find && ( args.arg(4).toboolean() || pat.indexOfAny( SPECIALS ) == -1 );
		
		if ( fastMatch ) {
			int result = s.indexOf( pat, init );
			if ( result != -1 ) {
				return varargsOf( valueOf(result+1), valueOf(result+pat.length()) );
			}
		} else {
			MatchState ms = new MatchState( args, s, pat );
			
			boolean anchor = false;
			int poff = 0;
			if ( pat.luaByte( 0 ) == '^' ) {
				anchor = true;
				poff = 1;
			}
			
			int soff = init;
			do {
				int res;
				ms.reset();
				if ( ( res = ms.match( soff, poff ) ) != -1 ) {
					if ( find ) {
						return varargsOf( valueOf(soff+1), valueOf(res), ms.push_captures( false, soff, res ));
					} else {
						return ms.push_captures( true, soff, res );
					}
				}
			} while ( soff++ < s.length() && !anchor );
		}
		return NIL;
	}
	
	private static int posrelat( int pos, int len ) {
		return ( pos >= 0 ) ? pos : len + pos + 1;
	}
	
	// Pattern matching implementation
	
	private static final int L_ESC = '%';
	private static final LuaString SPECIALS = valueOf("^$*+?.([%-");
	private static final int MAX_CAPTURES = 32;
	
	private static final int CAP_UNFINISHED = -1;
	private static final int CAP_POSITION = -2;
	
	private static final byte MASK_ALPHA		= 0x01;
	private static final byte MASK_LOWERCASE	= 0x02;
	private static final byte MASK_UPPERCASE	= 0x04;
	private static final byte MASK_DIGIT		= 0x08;
	private static final byte MASK_PUNCT		= 0x10;
	private static final byte MASK_SPACE		= 0x20;
	private static final byte MASK_CONTROL		= 0x40;
	private static final byte MASK_HEXDIGIT		= (byte)0x80;
	
	private static final byte[] CHAR_TABLE;
	
	static {
		CHAR_TABLE = new byte[256];
		
		for ( int i = 0; i < 256; ++i ) {
			final char c = (char) i;
			CHAR_TABLE[i] = (byte)( ( Character.isDigit( c ) ? MASK_DIGIT : 0 ) |
							( Character.isLowerCase( c ) ? MASK_LOWERCASE : 0 ) |
							( Character.isUpperCase( c ) ? MASK_UPPERCASE : 0 ) |
							( ( c < ' ' || c == 0x7F ) ? MASK_CONTROL : 0 ) );
			if ( ( c >= 'a' && c <= 'f' ) || ( c >= 'A' && c <= 'F' ) || ( c >= '0' && c <= '9' ) ) {
				CHAR_TABLE[i] |= MASK_HEXDIGIT;
			}
			if ( ( c >= '!' && c <= '/' ) || ( c >= ':' && c <= '@' ) ) {
				CHAR_TABLE[i] |= MASK_PUNCT;
			}
			if ( ( CHAR_TABLE[i] & ( MASK_LOWERCASE | MASK_UPPERCASE ) ) != 0 ) {
				CHAR_TABLE[i] |= MASK_ALPHA;
			}
		}
		
		CHAR_TABLE[' '] = MASK_SPACE;
		CHAR_TABLE['\r'] |= MASK_SPACE;
		CHAR_TABLE['\n'] |= MASK_SPACE;
		CHAR_TABLE['\t'] |= MASK_SPACE;
		CHAR_TABLE[0x0C /* '\v' */ ] |= MASK_SPACE;
		CHAR_TABLE['\f'] |= MASK_SPACE;
	};
	
	static class MatchState {
		final LuaString s;
		final LuaString p;
		final Varargs args;
		int level;
		int[] cinit;
		int[] clen;
		
		MatchState( Varargs args, LuaString s, LuaString pattern ) {
			this.s = s;
			this.p = pattern;
			this.args = args;
			this.level = 0;
			this.cinit = new int[ MAX_CAPTURES ];
			this.clen = new int[ MAX_CAPTURES ];
		}
		
		void reset() {
			level = 0;
		}
		
		private void add_s( Buffer lbuf, LuaString news, int soff, int e ) {
			int l = news.length();
			for ( int i = 0; i < l; ++i ) {
				byte b = (byte) news.luaByte( i );
				if ( b != L_ESC ) {
					lbuf.append( (byte) b );
				} else {
					++i; // skip ESC
					b = (byte) news.luaByte( i );
					if ( !Character.isDigit( (char) b ) ) {
						lbuf.append( b );
					} else if ( b == '0' ) {
						lbuf.append( s.substring( soff, e ) );
					} else {
						lbuf.append( push_onecapture( b - '1', soff, e ).strvalue() );
					}
				}
			}
		}
		
		public void add_value( Buffer lbuf, int soffset, int end, LuaValue repl ) {
			switch ( repl.type() ) {
			case LuaValue.TSTRING:
			case LuaValue.TNUMBER:
				add_s( lbuf, repl.strvalue(), soffset, end );
				return;
				
			case LuaValue.TFUNCTION:
				repl = repl.invoke( push_captures( true, soffset, end ) ).arg1();
				break;
				
			case LuaValue.TTABLE:
				// Need to call push_onecapture here for the error checking
				repl = repl.get( push_onecapture( 0, soffset, end ) );
				break;
				
			default:
				error( "bad argument: string/function/table expected" );
				return;
			}
			
			if ( !repl.toboolean() ) {
				repl = s.substring( soffset, end );
			} else if ( ! repl.isstring() ) {
				error( "invalid replacement value (a "+repl.typename()+")" );
			}
			lbuf.append( repl.strvalue() );
		}
		
		Varargs push_captures( boolean wholeMatch, int soff, int end ) {
			int nlevels = ( this.level == 0 && wholeMatch ) ? 1 : this.level;
			switch ( nlevels ) {
			case 0: return NONE;
			case 1: return push_onecapture( 0, soff, end );
			}
			LuaValue[] v = new LuaValue[nlevels];
			for ( int i = 0; i < nlevels; ++i )
				v[i] = push_onecapture( i, soff, end );
			return varargsOf(v);
		}
		
		private LuaValue push_onecapture( int i, int soff, int end ) {
			if ( i >= this.level ) {
				if ( i == 0 ) {
					return s.substring( soff, end );
				} else {
					return error( "invalid capture index" );
				}
			} else {
				int l = clen[i];
				if ( l == CAP_UNFINISHED ) {
					return error( "unfinished capture" );
				}
				if ( l == CAP_POSITION ) {
					return valueOf( cinit[i] + 1 );
				} else {
					int begin = cinit[i];
					return s.substring( begin, begin + l );
				}
			}
		}
		
		private int check_capture( int l ) {
			l -= '1';
			if ( l < 0 || l >= level || this.clen[l] == CAP_UNFINISHED ) {
				error("invalid capture index");
			}
			return l;
		}
		
		private int capture_to_close() {
			int level = this.level;
			for ( level--; level >= 0; level-- )
				if ( clen[level] == CAP_UNFINISHED )
					return level;
			error("invalid pattern capture");
			return 0;
		}
		
		int classend( int poffset ) {
			switch ( p.luaByte( poffset++ ) ) {
			case L_ESC:
				if ( poffset == p.length() ) {
					error( "malformed pattern (ends with %)" );
				}
				return poffset + 1;
				
			case '[':
				if ( p.luaByte( poffset ) == '^' ) poffset++;
				do {
					if ( poffset == p.length() ) {
						error( "malformed pattern (missing ])" );
					}
					if ( p.luaByte( poffset++ ) == L_ESC && poffset != p.length() )
						poffset++;
				} while ( p.luaByte( poffset ) != ']' );
				return poffset + 1;
			default:
				return poffset;
			}
		}
		
		static boolean match_class( int c, int cl ) {
			final char lcl = Character.toLowerCase( (char) cl );
			int cdata = CHAR_TABLE[c];
			
			boolean res;
			switch ( lcl ) {
			case 'a': res = ( cdata & MASK_ALPHA ) != 0; break;
			case 'd': res = ( cdata & MASK_DIGIT ) != 0; break;
			case 'l': res = ( cdata & MASK_LOWERCASE ) != 0; break;
			case 'u': res = ( cdata & MASK_UPPERCASE ) != 0; break;
			case 'c': res = ( cdata & MASK_CONTROL ) != 0; break;
			case 'p': res = ( cdata & MASK_PUNCT ) != 0; break;
			case 's': res = ( cdata & MASK_SPACE ) != 0; break;
			case 'w': res = ( cdata & ( MASK_ALPHA | MASK_DIGIT ) ) != 0; break;
			case 'x': res = ( cdata & MASK_HEXDIGIT ) != 0; break;
			case 'z': res = ( c == 0 ); break;
			default: return cl == c;
			}
			return ( lcl == cl ) ? res : !res;
		}
		
		boolean matchbracketclass( int c, int poff, int ec ) {
			boolean sig = true;
			if ( p.luaByte( poff + 1 ) == '^' ) {
				sig = false;
				poff++;
			}
			while ( ++poff < ec ) {
				if ( p.luaByte( poff ) == L_ESC ) {
					poff++;
					if ( match_class( c, p.luaByte( poff ) ) )
						return sig;
				}
				else if ( ( p.luaByte( poff + 1 ) == '-' ) && ( poff + 2 < ec ) ) {
					poff += 2;
					if ( p.luaByte( poff - 2 ) <= c && c <= p.luaByte( poff ) )
						return sig;
				}
				else if ( p.luaByte( poff ) == c ) return sig;
			}
			return !sig;
		}
		
		boolean singlematch( int c, int poff, int ep ) {
			switch ( p.luaByte( poff ) ) {
			case '.': return true;
			case L_ESC: return match_class( c, p.luaByte( poff + 1 ) );
			case '[': return matchbracketclass( c, poff, ep - 1 );
			default: return p.luaByte( poff ) == c;
			}
		}
		
		/**
		 * Perform pattern matching. If there is a match, returns offset into s
		 * where match ends, otherwise returns -1.
		 */
		int match( int soffset, int poffset ) {
			while ( true ) {
				// Check if we are at the end of the pattern - 
				// equivalent to the '\0' case in the C version, but our pattern
				// string is not NUL-terminated.
				if ( poffset == p.length() )
					return soffset;
				switch ( p.luaByte( poffset ) ) {
				case '(':
					if ( ++poffset < p.length() && p.luaByte( poffset ) == ')' )
						return start_capture( soffset, poffset + 1, CAP_POSITION );
					else
						return start_capture( soffset, poffset, CAP_UNFINISHED );
				case ')':
					return end_capture( soffset, poffset + 1 );
				case L_ESC:
					if ( poffset + 1 == p.length() )
						error("malformed pattern (ends with '%')");
					switch ( p.luaByte( poffset + 1 ) ) {
					case 'b':
						soffset = matchbalance( soffset, poffset + 2 );
						if ( soffset == -1 ) return -1;
						poffset += 4;
						continue;
					case 'f': {
						poffset += 2;
						if ( p.luaByte( poffset ) != '[' ) {
							error("Missing [ after %f in pattern");
						}
						int ep = classend( poffset );
						int previous = ( soffset == 0 ) ? -1 : s.luaByte( soffset - 1 );
						if ( matchbracketclass( previous, poffset, ep - 1 ) ||
							 matchbracketclass( s.luaByte( soffset ), poffset, ep - 1 ) )
							return -1;
						poffset = ep;
						continue;
					}
					default: {
						int c = p.luaByte( poffset + 1 );
						if ( Character.isDigit( (char) c ) ) {
							soffset = match_capture( soffset, c );
							if ( soffset == -1 )
								return -1;
							return match( soffset, poffset + 2 );
						}
					}
					}
				case '$':
					if ( poffset + 1 == p.length() )
						return ( soffset == s.length() ) ? soffset : -1;
				}
				int ep = classend( poffset );
				boolean m = soffset < s.length() && singlematch( s.luaByte( soffset ), poffset, ep );
				int pc = ( ep < p.length() ) ? p.luaByte( ep ) : '\0';
				
				switch ( pc ) {
				case '?':
					int res;
					if ( m && ( ( res = match( soffset + 1, ep + 1 ) ) != -1 ) )
						return res;
					poffset = ep + 1;
					continue;
				case '*':
					return max_expand( soffset, poffset, ep );
				case '+':
					return ( m ? max_expand( soffset + 1, poffset, ep ) : -1 );
				case '-':
					return min_expand( soffset, poffset, ep );
				default:
					if ( !m )
						return -1;
					soffset++;
					poffset = ep;
					continue;
				}
			}
		}
		
		int max_expand( int soff, int poff, int ep ) {
			int i = 0;
			while ( soff + i < s.length() &&
					singlematch( s.luaByte( soff + i ), poff, ep ) )
				i++;
			while ( i >= 0 ) {
				int res = match( soff + i, ep + 1 );
				if ( res != -1 )
					return res;
				i--;
			}
			return -1;
		}
		
		int min_expand( int soff, int poff, int ep ) {
			for ( ;; ) {
				int res = match( soff, ep + 1 );
				if ( res != -1 )
					return res;
				else if ( soff < s.length() && singlematch( s.luaByte( soff ), poff, ep ) )
					soff++;
				else return -1;
			}
		}
		
		int start_capture( int soff, int poff, int what ) {
			int res;
			int level = this.level;
			if ( level >= MAX_CAPTURES ) {
				error( "too many captures" );
			}
			cinit[ level ] = soff;
			clen[ level ] = what;
			this.level = level + 1;
			if ( ( res = match( soff, poff ) ) == -1 )
				this.level--;
			return res;
		}
		
		int end_capture( int soff, int poff ) {
			int l = capture_to_close();
			int res;
			clen[l] = soff - cinit[l];
			if ( ( res = match( soff, poff ) ) == -1 )
				clen[l] = CAP_UNFINISHED;
			return res;
		}
		
		int match_capture( int soff, int l ) {
			l = check_capture( l );
			int len = clen[ l ];
			if ( ( s.length() - soff ) >= len &&
				 LuaString.equals( s, cinit[l], s, soff, len ) )
				return soff + len;
			else
				return -1;
		}
		
		int matchbalance( int soff, int poff ) {
			final int plen = p.length();
			if ( poff == plen || poff + 1 == plen ) {
				error( "unbalanced pattern" );
			}
			final int slen = s.length();
			if ( soff >= slen )
				return -1;
			final int b = p.luaByte( poff );
			if ( s.luaByte( soff ) != b )
				return -1;
			final int e = p.luaByte( poff + 1 );
			int cont = 1;
			while ( ++soff < slen ) {
				if ( s.luaByte( soff ) == e ) {
					if ( --cont == 0 ) return soff + 1;
				}
				else if ( s.luaByte( soff ) == b ) cont++;
			}
			return -1;
		}
	}
}
