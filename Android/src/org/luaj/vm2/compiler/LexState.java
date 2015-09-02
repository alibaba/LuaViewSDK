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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import org.luaj.vm2.LocVars;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.compiler.FuncState.BlockCnt;
import org.luaj.vm2.lib.MathLib;


public class LexState {
	
	protected static final String RESERVED_LOCAL_VAR_FOR_CONTROL = "(for control)";
    protected static final String RESERVED_LOCAL_VAR_FOR_STATE = "(for state)";
    protected static final String RESERVED_LOCAL_VAR_FOR_GENERATOR = "(for generator)";
    protected static final String RESERVED_LOCAL_VAR_FOR_STEP = "(for step)";
    protected static final String RESERVED_LOCAL_VAR_FOR_LIMIT = "(for limit)";
    protected static final String RESERVED_LOCAL_VAR_FOR_INDEX = "(for index)";
    
    // keywords array
    protected static final String[] RESERVED_LOCAL_VAR_KEYWORDS = new String[] {
        RESERVED_LOCAL_VAR_FOR_CONTROL,
        RESERVED_LOCAL_VAR_FOR_GENERATOR,
        RESERVED_LOCAL_VAR_FOR_INDEX,
        RESERVED_LOCAL_VAR_FOR_LIMIT,
        RESERVED_LOCAL_VAR_FOR_STATE,
        RESERVED_LOCAL_VAR_FOR_STEP
    };
    private static final Hashtable RESERVED_LOCAL_VAR_KEYWORDS_TABLE = new Hashtable();
    static {
    	for ( int i=0; i<RESERVED_LOCAL_VAR_KEYWORDS.length; i++ )
        	RESERVED_LOCAL_VAR_KEYWORDS_TABLE.put( RESERVED_LOCAL_VAR_KEYWORDS[i], Boolean.TRUE );
    }
                               
    private static final int EOZ    = (-1);
	private static final int MAX_INT = Integer.MAX_VALUE-2;
	private static final int UCHAR_MAX = 255; // TODO, convert to unicode CHAR_MAX? 
	private static final int LUAI_MAXCCALLS = 200;
	
	private static final String LUA_QS(String s) { return "'"+s+"'"; }
	private static final String LUA_QL(Object o) { return LUA_QS(String.valueOf(o)); }
	
	private static final int     LUA_COMPAT_LSTR   =    1; // 1 for compatibility, 2 for old behavior
	private static final boolean LUA_COMPAT_VARARG = true;	
    
    public static boolean isReservedKeyword(String varName) {
    	return RESERVED_LOCAL_VAR_KEYWORDS_TABLE.containsKey(varName);
    }
    
	/*
	** Marks the end of a patch list. It is an invalid value both as an absolute
	** address, and as a list link (would link an element to itself).
	*/
	static final int NO_JUMP = (-1);

	/*
	** grep "ORDER OPR" if you change these enums
	*/
	static final int 
	  OPR_ADD=0, OPR_SUB=1, OPR_MUL=2, OPR_DIV=3, OPR_MOD=4, OPR_POW=5,
	  OPR_CONCAT=6,
	  OPR_NE=7, OPR_EQ=8,
	  OPR_LT=9, OPR_LE=10, OPR_GT=11, OPR_GE=12,
	  OPR_AND=13, OPR_OR=14,
	  OPR_NOBINOPR=15;

	static final int 
		OPR_MINUS=0, OPR_NOT=1, OPR_LEN=2, OPR_NOUNOPR=3;

	/* exp kind */
	static final int 	  
	  VVOID = 0,	/* no value */
	  VNIL = 1,
	  VTRUE = 2,
	  VFALSE = 3,
	  VK = 4,		/* info = index of constant in `k' */
	  VKNUM = 5,	/* nval = numerical value */
	  VNONRELOC = 6,	/* info = result register */
	  VLOCAL = 7,	/* info = local register */
	  VUPVAL = 8,       /* info = index of upvalue in `upvalues' */
	  VINDEXED = 9,	/* info = table register, aux = index register (or `k') */
	  VJMP = 10,		/* info = instruction pc */
	  VRELOCABLE = 11,	/* info = instruction pc */
	  VCALL = 12,	/* info = instruction pc */
	  VVARARG = 13;	/* info = instruction pc */
	
	/* semantics information */
	private static class SemInfo {
		LuaValue r;
		LuaString ts;
	};

	private static class Token {
		int token;
		final SemInfo seminfo = new SemInfo();
		public void set(Token other) {
			this.token = other.token;
			this.seminfo.r = other.seminfo.r;
			this.seminfo.ts = other.seminfo.ts;
		}
	};
	
	int current;  /* current character (charint) */
	int linenumber;  /* input line counter */
	int lastline;  /* line of last token `consumed' */
	final Token t = new Token();  /* current token */
	final Token lookahead = new Token();  /* look ahead token */
	FuncState fs;  /* `FuncState' is private to the parser */
	LuaC L;
	InputStream z;  /* input stream */
	char[] buff;  /* buffer for tokens */
	int nbuff; /* length of buffer */
	Dyndata dyd = new Dyndata();  /* dynamic structures used by the parser */
	LuaString source;  /* current source name */
	LuaString envn;  /* environment variable name */
	byte decpoint;  /* locale decimal point */

	/* ORDER RESERVED */
	final static String luaX_tokens [] = {
	    "and", "break", "do", "else", "elseif",
	    "end", "false", "for", "function", "goto", "if",
	    "in", "local", "nil", "not", "or", "repeat",
	    "return", "then", "true", "until", "while",
	    "..", "...", "==", ">=", "<=", "~=",
	    "::", "<eos>", "<number>", "<name>", "<string>", "<eof>",
	};

	final static int 
		/* terminal symbols denoted by reserved words */
		TK_AND=257,  TK_BREAK=258, TK_DO=259, TK_ELSE=260, TK_ELSEIF=261, 
		TK_END=262, TK_FALSE=263, TK_FOR=264, TK_FUNCTION=265, TK_GOTO=266, TK_IF=267, 
		TK_IN=268, TK_LOCAL=269, TK_NIL=270, TK_NOT=271, TK_OR=272, TK_REPEAT=273,
		TK_RETURN=274, TK_THEN=275, TK_TRUE=276, TK_UNTIL=277, TK_WHILE=278,
		/* other terminal symbols */
		TK_CONCAT=279, TK_DOTS=280, TK_EQ=281, TK_GE=282, TK_LE=283, TK_NE=284, 
		TK_DBCOLON=285, TK_EOS=286, TK_NUMBER=287, TK_NAME=288, TK_STRING=289;
	  
	final static int FIRST_RESERVED = TK_AND;
	final static int NUM_RESERVED = TK_WHILE+1-FIRST_RESERVED;
	
	final static Hashtable RESERVED = new Hashtable();
	static {
		for ( int i=0; i<NUM_RESERVED; i++ ) {
			LuaString ts = (LuaString) LuaValue.valueOf( luaX_tokens[i] );
			RESERVED.put(ts, new Integer(FIRST_RESERVED+i));
		}
	}

	private boolean isalnum(int c) {
		return (c >= '0' && c <= '9') 
			|| (c >= 'a' && c <= 'z')
			|| (c >= 'A' && c <= 'Z')
			|| (c == '_');
		// return Character.isLetterOrDigit(c);
	}
	
	private boolean isalpha(int c) {
		return (c >= 'a' && c <= 'z')
			|| (c >= 'A' && c <= 'Z');
	}
	
	private boolean isdigit(int c) {
		return (c >= '0' && c <= '9'); 
	}
	
	private boolean isxdigit(int c) {
		return (c >= '0' && c <= '9')
				|| (c >= 'a' && c <= 'f')
				|| (c >= 'A' && c <= 'F'); 
	}
	
	private boolean isspace(int c) {
		return (c <= ' ');
	}
	
	
	public LexState(LuaC state, InputStream stream) {
		this.z = stream;
		this.buff = new char[32];
		this.L = state;
	}

	void nextChar() {
		try {
 			current = z.read();
		} catch ( IOException e ) {
			e.printStackTrace();
			current = EOZ;
		}
	}

	boolean currIsNewline() {
		return current == '\n' || current == '\r';
	}

	void save_and_next() {
		save( current );
		nextChar();
	}

	void save(int c) {
		if ( buff == null || nbuff + 1 > buff.length )
			buff = LuaC.realloc( buff, nbuff*2+1 );
		buff[nbuff++] = (char) c;
	}


	String token2str( int token ) {
		if ( token < FIRST_RESERVED ) {
			return iscntrl(token)? 
					L.pushfstring( "char("+((int)token)+")" ):
					L.pushfstring( String.valueOf( (char) token ) );
		} else {
			return luaX_tokens[token-FIRST_RESERVED];
		}
	}

	private static boolean iscntrl(int token) {
		return token < ' ';
	}

	String txtToken(int token) {
		switch ( token ) {
		case TK_NAME:
		case TK_STRING:
		case TK_NUMBER:
			return new String( buff, 0, nbuff );
		default:
			return token2str( token );
		}
	}

	void lexerror( String msg, int token ) {
		String cid = Lua.chunkid( source.tojstring() );
		L.pushfstring( cid+":"+linenumber+": "+msg );
		if ( token != 0 )
			L.pushfstring( "syntax error: "+msg+" near "+txtToken(token) );
		throw new LuaError(cid+":"+linenumber+": "+msg);
	}

	void syntaxerror( String msg ) {
		lexerror( msg, t.token );
	}

	// only called by new_localvarliteral() for var names.
	LuaString newstring( String s ) {
		return L.newTString(s);
	}

	LuaString newstring( char[] chars, int offset, int len ) {
		return L.newTString(new String(chars, offset, len));
	}

	void inclinenumber() {
		int old = current;
		LuaC._assert( currIsNewline() );
		nextChar(); /* skip '\n' or '\r' */
		if ( currIsNewline() && current != old )
			nextChar(); /* skip '\n\r' or '\r\n' */
		if ( ++linenumber >= MAX_INT )
			syntaxerror("chunk has too many lines");
	}

	void setinput( LuaC L, int firstByte, InputStream z, LuaString source ) {
		this.decpoint = '.';
		this.L = L;
		this.lookahead.token = TK_EOS; /* no look-ahead token */
		this.z = z;
		this.fs = null;
		this.linenumber = 1;
		this.lastline = 1;
		this.source = source;
		this.envn = LuaValue.ENV;  /* environment variable name */
		this.nbuff = 0;   /* initialize buffer */
		this.current = firstByte; /* read first char */
		this.skipShebang();
	}
	
	private void skipShebang() {
		if ( current == '#' )
			while (!currIsNewline() && current != EOZ)
				nextChar();
	}
	


	/*
	** =======================================================
	** LEXICAL ANALYZER
	** =======================================================
	*/


	boolean check_next(String set) {
		if (set.indexOf(current) < 0)
			return false;
		save_and_next();
		return true;
	}

	void buffreplace(char from, char to) {
		int n = nbuff;
		char[] p = buff;
		while ((--n) >= 0)
			if (p[n] == from)
				p[n] = to;
	}

	LuaValue strx2number(String str, SemInfo seminfo) {
		char[] c = str.toCharArray();
		int s = 0;
		while ( s < c.length && isspace(c[s]))
			++s;
		// Check for negative sign
		double sgn = 1.0;
		if (s < c.length && c[s] == '-') {
			sgn = -1.0;
			++s;
		}
		/* Check for "0x" */
		if (s + 2 >= c.length )
			return LuaValue.ZERO;
		if (c[s++] != '0')
			return LuaValue.ZERO;
		if (c[s] != 'x' && c[s] != 'X')
			return LuaValue.ZERO;
		++s;

		// read integer part.
		double m = 0;
		int e = 0;
		while (s < c.length && isxdigit(c[s]))
			m = (m * 16) + hexvalue(c[s++]);
		if (s < c.length && c[s] == '.') {
			++s;  // skip dot
			while (s < c.length && isxdigit(c[s])) {
				m = (m * 16) + hexvalue(c[s++]);
				e -= 4;  // Each fractional part shifts right by 2^4
			}
		}
		if (s < c.length && (c[s] == 'p' || c[s] == 'P')) {
			++s;
			int exp1 = 0;
			boolean neg1 = false;
			if (s < c.length && c[s] == '-') {
				neg1 = true;
				++s;
			}
			while (s < c.length && isdigit(c[s]))
				exp1 = exp1 * 10 + c[s++] - '0';
			if (neg1)
				exp1 = -exp1;
			e += exp1;
		}
		return LuaValue.valueOf(sgn * m * MathLib.dpow_d(2.0, e));
	}
	
	boolean str2d(String str, SemInfo seminfo) {
		if (str.indexOf('n')>=0 || str.indexOf('N')>=0)
			seminfo.r = LuaValue.ZERO;
		else if (str.indexOf('x')>=0 || str.indexOf('X')>=0)
			seminfo.r = strx2number(str, seminfo);
		else
			seminfo.r = LuaValue.valueOf(Double.parseDouble(str.trim()));
		return true;
	}

	void read_numeral(SemInfo seminfo) {
		String expo = "Ee";
		int first = current;
		LuaC._assert (isdigit(current));
		save_and_next();
		if (first == '0' && check_next("Xx"))
			expo = "Pp";
		while (true) {
			if (check_next(expo))
				check_next("+-");
			if(isxdigit(current) || current == '.')
				save_and_next();
			else
				break;
		}
		save('\0');
		String str = new String(buff, 0, nbuff);
		str2d(str, seminfo);
	}

	int skip_sep() {
		int count = 0;
		int s = current;
		LuaC._assert (s == '[' || s == ']');
		save_and_next();
		while (current == '=') {
			save_and_next();
			count++;
		}
		return (current == s) ? count : (-count) - 1;
	}

	void read_long_string(SemInfo seminfo, int sep) {
		int cont = 0;
		save_and_next(); /* skip 2nd `[' */
		if (currIsNewline()) /* string starts with a newline? */
			inclinenumber(); /* skip it */
		for (boolean endloop = false; !endloop;) {
			switch (current) {
			case EOZ:
				lexerror((seminfo != null) ? "unfinished long string"
						: "unfinished long comment", TK_EOS);
				break; /* to avoid warnings */
			case '[': {
				if (skip_sep() == sep) {
					save_and_next(); /* skip 2nd `[' */
					cont++;
					if (LUA_COMPAT_LSTR == 1) {
						if (sep == 0)
							lexerror("nesting of [[...]] is deprecated", '[');
					}
				}
				break;
			}
			case ']': {
				if (skip_sep() == sep) {
					save_and_next(); /* skip 2nd `]' */
					if (LUA_COMPAT_LSTR == 2) {
						cont--;
						if (sep == 0 && cont >= 0)
							break;
					}
					endloop = true;
				}
				break;
			}
			case '\n':
			case '\r': {
				save('\n');
				inclinenumber();
				if (seminfo == null)
					nbuff = 0; /* avoid wasting space */
				break;
			}
			default: {
				if (seminfo != null)
					save_and_next();
				else
					nextChar();
			}
			}
		}
		if (seminfo != null)
			seminfo.ts =  L.newTString(LuaString.valueOf(buff, 2 + sep, nbuff - 2 * (2 + sep)));
	}

	int hexvalue(int c) {
		return c <= '9'? c - '0': c <= 'F'? c + 10 - 'A': c + 10 - 'a';
	}

	int readhexaesc() {
		nextChar();
		int c1 = current;
		nextChar();
		int c2 = current;
		if (!isxdigit(c1) || !isxdigit(c2))
			lexerror("hexadecimal digit expected 'x"+((char)c1)+((char)c2), TK_STRING);
		return (hexvalue(c1) << 4) + hexvalue(c2);
	}

	void read_string(int del, SemInfo seminfo) {
		save_and_next();
		while (current != del) {
			switch (current) {
			case EOZ:
				lexerror("unfinished string", TK_EOS);
				continue; /* to avoid warnings */
			case '\n':
			case '\r':
				lexerror("unfinished string", TK_STRING);
				continue; /* to avoid warnings */
			case '\\': {
				int c;
				nextChar(); /* do not save the `\' */
				switch (current) {
				case 'a': /* bell */
					c = '\u0007';
					break;
				case 'b': /* backspace */
					c = '\b';
					break;
				case 'f': /* form feed */
					c = '\f';
					break;
				case 'n': /* newline */
					c = '\n';
					break;
				case 'r': /* carriage return */
					c = '\r';
					break;
				case 't': /* tab */
					c = '\t';
					break;
				case 'v': /* vertical tab */
					c = '\u000B';
					break;
				case 'x':
					c = readhexaesc();
					break;
				case '\n': /* go through */
				case '\r':
					save('\n');
					inclinenumber();
					continue;
				case EOZ:
					continue; /* will raise an error next loop */
		        case 'z': {  /* zap following span of spaces */
		              nextChar();  /* skip the 'z' */
		              while (isspace(current)) {
		            	  if (currIsNewline()) inclinenumber();
		            	  else nextChar();
		              }
		              continue;
		        }
				default: {
					if (!isdigit(current))
						save_and_next(); /* handles \\, \", \', and \? */
					else { /* \xxx */
						int i = 0;
						c = 0;
						do {
							c = 10 * c + (current - '0');
							nextChar();
						} while (++i < 3 && isdigit(current));
						if (c > UCHAR_MAX)
							lexerror("escape sequence too large", TK_STRING);
						save(c);
					}
					continue;
				}
				}
				save(c);
				nextChar();
				continue;
			}
			default:
				save_and_next();
			}
		}
		save_and_next(); /* skip delimiter */
		seminfo.ts = L.newTString(LuaString.valueOf(buff, 1, nbuff-2));
	}

	int llex(SemInfo seminfo) {
		nbuff = 0;
		while (true) {
			switch (current) {
			case '\n':
			case '\r': {
				inclinenumber();
				continue;
			}
			case '-': {
				nextChar();
				if (current != '-')
					return '-';
				/* else is a comment */
				nextChar();
				if (current == '[') {
					int sep = skip_sep();
					nbuff = 0; /* `skip_sep' may dirty the buffer */
					if (sep >= 0) {
						read_long_string(null, sep); /* long comment */
						nbuff = 0;
						continue;
					}
				}
				/* else short comment */
				while (!currIsNewline() && current != EOZ)
					nextChar();
				continue;
			}
			case '[': {
				int sep = skip_sep();
				if (sep >= 0) {
					read_long_string(seminfo, sep);
					return TK_STRING;
				} else if (sep == -1)
					return '[';
				else
					lexerror("invalid long string delimiter", TK_STRING);
			}
			case '=': {
				nextChar();
				if (current != '=')
					return '=';
				else {
					nextChar();
					return TK_EQ;
				}
			}
			case '<': {
				nextChar();
				if (current != '=')
					return '<';
				else {
					nextChar();
					return TK_LE;
				}
			}
			case '>': {
				nextChar();
				if (current != '=')
					return '>';
				else {
					nextChar();
					return TK_GE;
				}
			}
			case '~': {
				nextChar();
				if (current != '=')
					return '~';
				else {
					nextChar();
					return TK_NE;
				}
			}
			case ':': {
				nextChar();
				if (current != ':')
					return ':';
				else {
					nextChar();
					return TK_DBCOLON;
				}
			}
			case '"':
			case '\'': {
				read_string(current, seminfo);
				return TK_STRING;
			}
			case '.': {
				save_and_next();
				if (check_next(".")) {
					if (check_next("."))
						return TK_DOTS; /* ... */
					else
						return TK_CONCAT; /* .. */
				} else if (!isdigit(current))
					return '.';
				else {
					read_numeral(seminfo);
					return TK_NUMBER;
				}
			}
		    case '0': case '1': case '2': case '3': case '4':
		    case '5': case '6': case '7': case '8': case '9': {
		        read_numeral(seminfo);
		        return TK_NUMBER;
		    }
		 	case EOZ: {
				return TK_EOS;
			}
			default: {
				if (isspace(current)) {
					LuaC._assert (!currIsNewline());
					nextChar();
					continue;
				} else if (isdigit(current)) {
					read_numeral(seminfo);
					return TK_NUMBER;
				} else if (isalpha(current) || current == '_') {
					/* identifier or reserved word */
					LuaString ts;
					do {
						save_and_next();
					} while (isalnum(current) || current == '_');
					ts = newstring(buff, 0, nbuff);
					if ( RESERVED.containsKey(ts) )
						return ((Integer)RESERVED.get(ts)).intValue();
					else {
						seminfo.ts = ts;
						return TK_NAME;
					}
				} else {
					int c = current;
					nextChar();
					return c; /* single-char tokens (+ - / ...) */
				}
			}
			}
		}
	}

	void next() {
		lastline = linenumber;
		if (lookahead.token != TK_EOS) { /* is there a look-ahead token? */
			t.set( lookahead ); /* use this one */
			lookahead.token = TK_EOS; /* and discharge it */
		} else
			t.token = llex(t.seminfo); /* read next token */
	}

	void lookahead() {
		LuaC._assert (lookahead.token == TK_EOS);
		lookahead.token = llex(lookahead.seminfo);
	}

	// =============================================================
	// from lcode.h
	// =============================================================
	
	
	// =============================================================
	// from lparser.c
	// =============================================================

	static final boolean vkisvar(final int k) {
		return (VLOCAL <= (k) && (k) <= VINDEXED);
	}

	static final boolean vkisinreg(final int k) {
		return ((k) == VNONRELOC || (k) == VLOCAL);
	}

	static class expdesc {
		int k; // expkind, from enumerated list, above
		static class U { // originally a union
			short ind_idx; // index (R/K)
			short ind_t; // table(register or upvalue)
			short ind_vt; // whether 't' is register (VLOCAL) or (UPVALUE)
			private LuaValue _nval;
			int info;
			public void setNval(LuaValue r) {
				_nval = r;
			}
			public LuaValue nval() {
				return (_nval == null? LuaInteger.valueOf(info): _nval);
			}
		};
		final U u = new U();
		final IntPtr t = new IntPtr(); /* patch list of `exit when true' */
		final IntPtr f = new IntPtr(); /* patch list of `exit when false' */
		void init( int k, int i ) {
			this.f.i = NO_JUMP;
			this.t.i = NO_JUMP;
			this.k = k;
			this.u.info = i;
		}

		boolean hasjumps() {
			return (t.i != f.i);
		}

		boolean isnumeral() {
			return (k == VKNUM && t.i == NO_JUMP && f.i == NO_JUMP);
		}

		public void setvalue(expdesc other) {
			this.f.i = other.f.i;
			this.k = other.k;
			this.t.i = other.t.i;
			this.u._nval = other.u._nval;
			this.u.ind_idx = other.u.ind_idx;
			this.u.ind_t = other.u.ind_t;
			this.u.ind_vt = other.u.ind_vt;
			this.u.info = other.u.info;
		}
	}


	/* description of active local variable */
	static class Vardesc {
		final short idx;  /* variable index in stack */
		Vardesc(int idx) {
			this.idx = (short) idx;
		}
	};


	/* description of pending goto statements and label statements */
	static class Labeldesc {
		LuaString name;  /* label identifier */
		int pc;  /* position in code */
		int line;  /* line where it appeared */
		short nactvar;  /* local level where it appears in current block */
		public Labeldesc(LuaString name, int pc, int line, short nactvar) {
			this.name = name;
			this.pc = pc;
			this.line = line;
			this.nactvar = nactvar;
		}
	};


	/* dynamic structures used by the parser */
	static class Dyndata {
		Vardesc[] actvar;  /* list of active local variables */ 
		int n_actvar = 0;
		Labeldesc[] gt;  /* list of pending gotos */
		int n_gt = 0;
		Labeldesc[] label;   /* list of active labels */
		int n_label = 0;
	};
	
	
	boolean hasmultret(int k) {
		return ((k) == VCALL || (k) == VVARARG);
	}

	/*----------------------------------------------------------------------
	name		args	description
	------------------------------------------------------------------------*/
	
	void anchor_token () {
		/* last token from outer function must be EOS */
		LuaC._assert(fs != null || t.token == TK_EOS);
		if (t.token == TK_NAME || t.token == TK_STRING) {
			LuaString ts = t.seminfo.ts;
			// TODO: is this necessary?
			L.cachedLuaString(t.seminfo.ts);
		}
	}

	/* semantic error */
	void semerror (String msg) {
		t.token = 0;  /* remove 'near to' from final message */
		syntaxerror(msg);
	}

	void error_expected(int token) {
		syntaxerror(L.pushfstring(LUA_QS(token2str(token)) + " expected"));
	}

	boolean testnext(int c) {
		if (t.token == c) {
			next();
			return true;
		} else
			return false;
	}

	void check(int c) {
		if (t.token != c)
			error_expected(c);
	}

	void checknext (int c) {
	  check(c);
	  next();
	}

	void check_condition(boolean c, String msg) {
		if (!(c))
			syntaxerror(msg);
	}


	void check_match(int what, int who, int where) {
		if (!testnext(what)) {
			if (where == linenumber)
				error_expected(what);
			else {
				syntaxerror(L.pushfstring(LUA_QS(token2str(what))
						+ " expected " + "(to close " + LUA_QS(token2str(who))
						+ " at line " + where + ")"));
			}
		}
	}

	LuaString str_checkname() {
		LuaString ts;
		check(TK_NAME);
		ts = t.seminfo.ts;
		next();
		return ts;
	}
	
	void codestring(expdesc e, LuaString s) {
		e.init(VK, fs.stringK(s));
	}

	void checkname(expdesc e) {
		codestring(e, str_checkname());
	}

	
	int registerlocalvar(LuaString varname) {
		FuncState fs = this.fs;
		Prototype f = fs.f;
		if (f.locvars == null || fs.nlocvars + 1 > f.locvars.length)
			f.locvars = LuaC.realloc( f.locvars, fs.nlocvars*2+1 );
		f.locvars[fs.nlocvars] = new LocVars(varname,0,0);
		return fs.nlocvars++;
	}
	
	void new_localvar(LuaString name) {
		int reg = registerlocalvar(name);
		fs.checklimit(dyd.n_actvar + 1, FuncState.LUAI_MAXVARS, "local variables");
		if (dyd.actvar == null || dyd.n_actvar + 1 > dyd.actvar.length)
			dyd.actvar = LuaC.realloc(dyd.actvar, Math.max(1, dyd.n_actvar * 2));
		dyd.actvar[dyd.n_actvar++] = new Vardesc(reg);
	}

	void new_localvarliteral(String v) {
		LuaString ts = newstring(v);
		new_localvar(ts);
	}

	void adjustlocalvars(int nvars) {
		FuncState fs = this.fs;
		fs.nactvar = (short) (fs.nactvar + nvars);
		for (; nvars > 0; nvars--) {
			fs.getlocvar(fs.nactvar - nvars).startpc = fs.pc;
		}
	}

	void removevars(int tolevel) {
		FuncState fs = this.fs;
		while (fs.nactvar > tolevel)
			fs.getlocvar(--fs.nactvar).endpc = fs.pc;
	}
	
	void singlevar(expdesc var) {
		LuaString varname = this.str_checkname();
		FuncState fs = this.fs;
		if (FuncState.singlevaraux(fs, varname, var, 1) == VVOID) { /* global name? */
			expdesc key = new expdesc();
		    FuncState.singlevaraux(fs, this.envn, var, 1);  /* get environment variable */
		    LuaC._assert(var.k == VLOCAL || var.k == VUPVAL);
		    this.codestring(key, varname);  /* key is variable name */
		    fs.indexed(var, key);  /* env[varname] */
		}
	}
	
	void adjust_assign(int nvars, int nexps, expdesc e) {
		FuncState fs = this.fs;
		int extra = nvars - nexps;
		if (hasmultret(e.k)) {
			/* includes call itself */
			extra++;
			if (extra < 0)
				extra = 0;
			/* last exp. provides the difference */
			fs.setreturns(e, extra);
			if (extra > 1)
				fs.reserveregs(extra - 1);
		} else {
			/* close last expression */
			if (e.k != VVOID)
				fs.exp2nextreg(e);
			if (extra > 0) {
				int reg = fs.freereg;
				fs.reserveregs(extra);
				fs.nil(reg, extra);
			}
		}
	}
	
	void enterlevel() {
		if (++L.nCcalls > LUAI_MAXCCALLS)
			lexerror("chunk has too many syntax levels", 0);
	}
	
	void leavelevel() {
		L.nCcalls--;
	}

	void closegoto(int g, Labeldesc label) {
		FuncState fs = this.fs;
		Labeldesc[] gl = this.dyd.gt;
		Labeldesc gt = gl[g];
		LuaC._assert(gt.name.eq_b(label.name));
		if (gt.nactvar < label.nactvar) {
			LuaString vname = fs.getlocvar(gt.nactvar).varname;
			String msg = L.pushfstring("<goto " + gt.name + "> at line "
					+ gt.line + " jumps into the scope of local '"
					+ vname.tojstring() + "'");
			semerror(msg);
		}
		fs.patchlist(gt.pc, label.pc);
		/* remove goto from pending list */
		System.arraycopy(gl, g + 1, gl, g, this.dyd.n_gt - g - 1);
		gl[--this.dyd.n_gt] = null;
	}

	/*
	 ** try to close a goto with existing labels; this solves backward jumps
	 */
	boolean findlabel (int g) {
		int i;
		BlockCnt bl = fs.bl;
		Dyndata dyd = this.dyd;
		Labeldesc gt = dyd.gt[g];
		/* check labels in current block for a match */
		for (i = bl.firstlabel; i < dyd.n_label; i++) {
			Labeldesc lb = dyd.label[i];
			if (lb.name.eq_b(gt.name)) {  /* correct label? */
				if (gt.nactvar > lb.nactvar &&
						(bl.upval || dyd.n_label > bl.firstlabel))
					fs.patchclose(gt.pc, lb.nactvar);
				closegoto(g, lb);  /* close it */
				return true;
			}
		}
		return false;  /* label not found; cannot close goto */
	}

	/* Caller must LuaC.grow() the vector before calling this. */
	int newlabelentry(Labeldesc[] l, int index, LuaString name, int line, int pc) {
		l[index] = new Labeldesc(name, pc, line, fs.nactvar);
		return index;
	}

	/*
	 ** check whether new label 'lb' matches any pending gotos in current
	 ** block; solves forward jumps
	 */
	void findgotos (Labeldesc lb) {
		Labeldesc[] gl = dyd.gt;
		int i = fs.bl.firstgoto;
		while (i < dyd.n_gt) {
			if (gl[i].name.eq_b(lb.name))
				closegoto(i, lb);
			else
				i++;
		}
	}
	

	/*
	** create a label named "break" to resolve break statements
	*/
	void breaklabel () {
		LuaString n = LuaString.valueOf("break");
		int l = newlabelentry(dyd.label=LuaC.grow(dyd.label, dyd.n_label+1), dyd.n_label++, n, 0, fs.pc);
		findgotos(dyd.label[l]);
	}

	/*
	** generates an error for an undefined 'goto'; choose appropriate
	** message when label name is a reserved word (which can only be 'break')
	*/
	void undefgoto (Labeldesc gt) {
	  String msg = L.pushfstring(isReservedKeyword(gt.name.tojstring())
	                    ? "<"+gt.name+"> at line "+gt.line+" not inside a loop"
	                    : "no visible label '"+gt.name+"' for <goto> at line "+gt.line);
	  semerror(msg);
	}

	Prototype addprototype () {
	  Prototype clp;
	  Prototype f = fs.f;  /* prototype of current function */
	  if (f.p == null || fs.np >= f.p.length) {
	    f.p = LuaC.realloc(f.p, Math.max(1, fs.np * 2));
	  }
	  f.p[fs.np++] = clp = new Prototype();
	  return clp;
	}

	void codeclosure (expdesc v) {
	  FuncState fs = this.fs.prev;
	  v.init(VRELOCABLE, fs.codeABx(LuaC.OP_CLOSURE, 0, fs.np - 1));
	  fs.exp2nextreg(v);  /* fix it at stack top (for GC) */
	}

	void open_func (FuncState fs, BlockCnt bl) {
		  fs.prev = this.fs;  /* linked list of funcstates */
		  fs.ls = this;
		  this.fs = fs;
		  fs.pc = 0;
		  fs.lasttarget = -1;
		  fs.jpc = new IntPtr( NO_JUMP );
		  fs.freereg = 0;
		  fs.nk = 0;
		  fs.np = 0;
		  fs.nups = 0;
		  fs.nlocvars = 0;
		  fs.nactvar = 0;
		  fs.firstlocal = dyd.n_actvar;
		  fs.bl = null;
		  fs.f.source = this.source;
		  fs.f.maxstacksize = 2;  /* registers 0/1 are always valid */
		  fs.enterblock(bl,  false);
	}

	void close_func() {
		FuncState fs = this.fs;
		Prototype f = fs.f;
		fs.ret(0, 0); /* final return */
		fs.leaveblock();
		f.code = LuaC.realloc(f.code, fs.pc);
		f.lineinfo = LuaC.realloc(f.lineinfo, fs.pc);
		f.k = LuaC.realloc(f.k, fs.nk);
		f.p = LuaC.realloc(f.p, fs.np);
		f.locvars = LuaC.realloc(f.locvars, fs.nlocvars);
		f.upvalues = LuaC.realloc(f.upvalues, fs.nups);
		LuaC._assert (fs.bl == null);
		this.fs = fs.prev;
		// last token read was anchored in defunct function; must reanchor it
		// ls.anchor_token();
	}

	/*============================================================*/
	/* GRAMMAR RULES */
	/*============================================================*/

	void fieldsel(expdesc v) {
		/* fieldsel -> ['.' | ':'] NAME */
		FuncState fs = this.fs;
		expdesc key = new expdesc();
		fs.exp2anyregup(v);
		this.next(); /* skip the dot or colon */
		this.checkname(key);
		fs.indexed(v, key);
	}
	
	void yindex(expdesc v) {
		/* index -> '[' expr ']' */
		this.next(); /* skip the '[' */
		this.expr(v);
		this.fs.exp2val(v);
		this.checknext(']');
	}


 /*
	** {======================================================================
	** Rules for Constructors
	** =======================================================================
	*/


	static class ConsControl {
		expdesc v = new expdesc(); /* last list item read */
		expdesc t; /* table descriptor */
		int nh; /* total number of `record' elements */
		int na; /* total number of array elements */
		int tostore; /* number of array elements pending to be stored */
	};


	void recfield(ConsControl cc) {
		/* recfield -> (NAME | `['exp1`]') = exp1 */
		FuncState fs = this.fs;
		int reg = this.fs.freereg;
		expdesc key = new expdesc();
		expdesc val = new expdesc();
		int rkkey;
		if (this.t.token == TK_NAME) {
			fs.checklimit(cc.nh, MAX_INT, "items in a constructor");
			this.checkname(key);
		} else
			/* this.t.token == '[' */
			this.yindex(key);
		cc.nh++;
		this.checknext('=');
		rkkey = fs.exp2RK(key);
		this.expr(val);
		fs.codeABC(Lua.OP_SETTABLE, cc.t.u.info, rkkey, fs.exp2RK(val));
		fs.freereg = (short)reg; /* free registers */
	}

	void listfield (ConsControl cc) {
	  this.expr(cc.v);
	  fs.checklimit(cc.na, MAX_INT, "items in a constructor");
	  cc.na++;
	  cc.tostore++;
	}


	void constructor(expdesc t) {
		/* constructor -> ?? */
		FuncState fs = this.fs;
		int line = this.linenumber;
		int pc = fs.codeABC(Lua.OP_NEWTABLE, 0, 0, 0);
		ConsControl cc = new ConsControl();
		cc.na = cc.nh = cc.tostore = 0;
		cc.t = t;
		t.init(VRELOCABLE, pc);
		cc.v.init(VVOID, 0); /* no value (yet) */
		fs.exp2nextreg(t); /* fix it at stack top (for gc) */
		this.checknext('{');
		do {
			LuaC._assert (cc.v.k == VVOID || cc.tostore > 0);
			if (this.t.token == '}')
				break;
			fs.closelistfield(cc);
			switch (this.t.token) {
			case TK_NAME: { /* may be listfields or recfields */
				this.lookahead();
				if (this.lookahead.token != '=') /* expression? */
					this.listfield(cc);
				else
					this.recfield(cc);
				break;
			}
			case '[': { /* constructor_item -> recfield */
				this.recfield(cc);
				break;
			}
			default: { /* constructor_part -> listfield */
				this.listfield(cc);
				break;
			}
			}
		} while (this.testnext(',') || this.testnext(';'));
		this.check_match('}', '{', line);
		fs.lastlistfield(cc);
		InstructionPtr i = new InstructionPtr(fs.f.code, pc);
		LuaC.SETARG_B(i, luaO_int2fb(cc.na)); /* set initial array size */
		LuaC.SETARG_C(i, luaO_int2fb(cc.nh));  /* set initial table size */
	}
	
	/*
	** converts an integer to a "floating point byte", represented as
	** (eeeeexxx), where the real value is (1xxx) * 2^(eeeee - 1) if
	** eeeee != 0 and (xxx) otherwise.
	*/
	static int luaO_int2fb (int x) {
	  int e = 0;  /* expoent */
	  while (x >= 16) {
	    x = (x+1) >> 1;
	    e++;
	  }
	  if (x < 8) return x;
	  else return ((e+1) << 3) | (((int)x) - 8);
	}


	/* }====================================================================== */

	void parlist () {
	  /* parlist -> [ param { `,' param } ] */
	  FuncState fs = this.fs;
	  Prototype f = fs.f;
	  int nparams = 0;
	  f.is_vararg = 0;
	  if (this.t.token != ')') {  /* is `parlist' not empty? */
	    do {
	      switch (this.t.token) {
	        case TK_NAME: {  /* param . NAME */
	          this.new_localvar(this.str_checkname());
	          ++nparams;
	          break;
	        }
	        case TK_DOTS: {  /* param . `...' */
	          this.next();
	          f.is_vararg = 1;
	          break;
	        }
	        default: this.syntaxerror("<name> or " + LUA_QL("...") + " expected");
	      }
	    } while ((f.is_vararg==0) && this.testnext(','));
	  }
	  this.adjustlocalvars(nparams);
	  f.numparams = fs.nactvar;
	  fs.reserveregs(fs.nactvar);  /* reserve register for parameters */
	}


	void body(expdesc e, boolean needself, int line) {
		/* body -> `(' parlist `)' chunk END */
		FuncState new_fs = new FuncState();
		BlockCnt bl = new BlockCnt();
		new_fs.f = addprototype();
		new_fs.f.linedefined = line;
		open_func(new_fs, bl);
		this.checknext('(');
		if (needself) {
			new_localvarliteral("self");
			adjustlocalvars(1);
		}
		this.parlist();
		this.checknext(')');
		this.statlist();
		new_fs.f.lastlinedefined = this.linenumber;
		this.check_match(TK_END, TK_FUNCTION, line);
		this.codeclosure(e);
		this.close_func();
	}
	
	int explist(expdesc v) {
		/* explist1 -> expr { `,' expr } */
		int n = 1; /* at least one expression */
		this.expr(v);
		while (this.testnext(',')) {
			fs.exp2nextreg(v);
			this.expr(v);
			n++;
		}
		return n;
	}


	void funcargs(expdesc f, int line) {
		FuncState fs = this.fs;
		expdesc args = new expdesc();
		int base, nparams;
		switch (this.t.token) {
		case '(': { /* funcargs -> `(' [ explist1 ] `)' */
			this.next();
			if (this.t.token == ')') /* arg list is empty? */
				args.k = VVOID;
			else {
				this.explist(args);
				fs.setmultret(args);
			}
			this.check_match(')', '(', line);
			break;
		}
		case '{': { /* funcargs -> constructor */
			this.constructor(args);
			break;
		}
		case TK_STRING: { /* funcargs -> STRING */
			this.codestring(args, this.t.seminfo.ts);
			this.next(); /* must use `seminfo' before `next' */
			break;
		}
		default: {
			this.syntaxerror("function arguments expected");
			return;
		}
		}
		LuaC._assert (f.k == VNONRELOC);
		base = f.u.info; /* base register for call */
		if (hasmultret(args.k))
			nparams = Lua.LUA_MULTRET; /* open call */
		else {
			if (args.k != VVOID)
				fs.exp2nextreg(args); /* close last argument */
			nparams = fs.freereg - (base + 1);
		}
		f.init(VCALL, fs.codeABC(Lua.OP_CALL, base, nparams + 1, 2));
		fs.fixline(line);
		fs.freereg = (short)(base+1);  /* call remove function and arguments and leaves
							 * (unless changed) one result */
	}


	/*
	** {======================================================================
	** Expression parsing
	** =======================================================================
	*/

	void primaryexp (expdesc v) {
		/* primaryexp -> NAME | '(' expr ')' */
		switch (t.token) {
		case '(': {
			int line = linenumber;
			this.next();
			this.expr(v);
			this.check_match(')', '(', line);
			fs.dischargevars(v);
			return;
		}
		case TK_NAME: {
			singlevar(v);
			return;
		}
		default: {
			this.syntaxerror("unexpected symbol " + t.token + " (" + ((char) t.token) + ")");
			return;
		}
		}
	}


	void suffixedexp (expdesc v) {
		/* suffixedexp ->
       	primaryexp { '.' NAME | '[' exp ']' | ':' NAME funcargs | funcargs } */
		int line = linenumber;
		primaryexp(v);
		for (;;) {
			switch (t.token) {
			case '.': { /* fieldsel */
				this.fieldsel(v);
				break;
			}
			case '[': { /* `[' exp1 `]' */
				expdesc key = new expdesc();
				fs.exp2anyregup(v);
				this.yindex(key);
				fs.indexed(v, key);
				break;
			}
			case ':': { /* `:' NAME funcargs */
				expdesc key = new expdesc();
				this.next();
				this.checkname(key);
				fs.self(v, key);
				this.funcargs(v, line);
				break;
			}
			case '(':
			case TK_STRING:
			case '{': { /* funcargs */
				fs.exp2nextreg(v);
				this.funcargs(v, line);
				break;
			}
			default:
				return;
			}
		}
    }


	void simpleexp(expdesc v) {
		/*
		 * simpleexp -> NUMBER | STRING | NIL | true | false | ... | constructor |
		 * FUNCTION body | primaryexp
		 */
		switch (this.t.token) {
		case TK_NUMBER: {
			v.init(VKNUM, 0);
			v.u.setNval(this.t.seminfo.r);
			break;
		}
		case TK_STRING: {
			this.codestring(v, this.t.seminfo.ts);
			break;
		}
		case TK_NIL: {
			v.init(VNIL, 0);
			break;
		}
		case TK_TRUE: {
			v.init(VTRUE, 0);
			break;
		}
		case TK_FALSE: {
			v.init(VFALSE, 0);
			break;
		}
		case TK_DOTS: { /* vararg */
			FuncState fs = this.fs;
			this.check_condition(fs.f.is_vararg!=0, "cannot use " + LUA_QL("...")
					+ " outside a vararg function");
			v.init(VVARARG, fs.codeABC(Lua.OP_VARARG, 0, 1, 0));
			break;
		}
		case '{': { /* constructor */
			this.constructor(v);
			return;
		}
		case TK_FUNCTION: {
			this.next();
			this.body(v, false, this.linenumber);
			return;
		}
		default: {
			this.suffixedexp(v);
			return;
		}
		}
		this.next();
	}


	int getunopr(int op) {
		switch (op) {
		case TK_NOT:
			return OPR_NOT;
		case '-':
			return OPR_MINUS;
		case '#':
			return OPR_LEN;
		default:
			return OPR_NOUNOPR;
		}
	}


	int getbinopr(int op) {
		switch (op) {
		case '+':
			return OPR_ADD;
		case '-':
			return OPR_SUB;
		case '*':
			return OPR_MUL;
		case '/':
			return OPR_DIV;
		case '%':
			return OPR_MOD;
		case '^':
			return OPR_POW;
		case TK_CONCAT:
			return OPR_CONCAT;
		case TK_NE:
			return OPR_NE;
		case TK_EQ:
			return OPR_EQ;
		case '<':
			return OPR_LT;
		case TK_LE:
			return OPR_LE;
		case '>':
			return OPR_GT;
		case TK_GE:
			return OPR_GE;
		case TK_AND:
			return OPR_AND;
		case TK_OR:
			return OPR_OR;
		default:
			return OPR_NOBINOPR;
		}
	}

	static class Priority {
		final byte left; /* left priority for each binary operator */

		final byte right; /* right priority */

		public Priority(int i, int j) {
			left = (byte) i;
			right = (byte) j;
		}
	};
	
	static Priority[] priority = {  /* ORDER OPR */
	   new Priority(6, 6), new Priority(6, 6), new Priority(7, 7), new Priority(7, 7), new Priority(7, 7),  /* `+' `-' `/' `%' */
	   new Priority(10, 9), new Priority(5, 4),                 /* power and concat (right associative) */
	   new Priority(3, 3), new Priority(3, 3),                  /* equality and inequality */
	   new Priority(3, 3), new Priority(3, 3), new Priority(3, 3), new Priority(3, 3),  /* order */
	   new Priority(2, 2), new Priority(1, 1)                   /* logical (and/or) */
	};

	static final int UNARY_PRIORITY	= 8;  /* priority for unary operators */


	/*
	** subexpr -> (simpleexp | unop subexpr) { binop subexpr }
	** where `binop' is any binary operator with a priority higher than `limit'
	*/
	int subexpr(expdesc v, int limit) {
		int op;
		int uop;
		this.enterlevel();
		uop = getunopr(this.t.token);
		if (uop != OPR_NOUNOPR) {
		    int line = linenumber;
			this.next();
			this.subexpr(v, UNARY_PRIORITY);
			fs.prefix(uop, v, line);
		} else
			this.simpleexp(v);
		/* expand while operators have priorities higher than `limit' */
		op = getbinopr(this.t.token);
		while (op != OPR_NOBINOPR && priority[op].left > limit) {
			expdesc v2 = new expdesc();
			int line = linenumber;
			this.next();
			fs.infix(op, v);
			/* read sub-expression with higher priority */
			int nextop = this.subexpr(v2, priority[op].right);
			fs.posfix(op, v, v2, line);
			op = nextop;
		}
		this.leavelevel();
		return op; /* return first untreated operator */
	}

	void expr(expdesc v) {
		this.subexpr(v, 0);
	}

	/* }==================================================================== */



	/*
	** {======================================================================
	** Rules for Statements
	** =======================================================================
	*/


	boolean block_follow (boolean withuntil) {
		switch (t.token) {
		    case TK_ELSE: case TK_ELSEIF: case TK_END: case TK_EOS:
		    	return true;
			case TK_UNTIL: 
		    	return withuntil;
		    default: return false;
		}
	}


	void block () {
	  /* block -> chunk */
	  FuncState fs = this.fs;
	  BlockCnt bl = new BlockCnt();
	  fs.enterblock(bl, false);
	  this.statlist();
	  fs.leaveblock();
	}


	/*
	** structure to chain all variables in the left-hand side of an
	** assignment
	*/
	static class LHS_assign {
		LHS_assign prev;
		/* variable (global, local, upvalue, or indexed) */
		expdesc v = new expdesc(); 
	};


	/*
	** check whether, in an assignment to a local variable, the local variable
	** is needed in a previous assignment (to a table). If so, save original
	** local value in a safe place and use this safe copy in the previous
	** assignment.
	*/
	void check_conflict (LHS_assign lh, expdesc v) {
		FuncState fs = this.fs;
		short extra = (short) fs.freereg;  /* eventual position to save local variable */
		boolean conflict = false;
		for (; lh!=null; lh = lh.prev) {
			if (lh.v.k == VINDEXED) {
				/* table is the upvalue/local being assigned now? */
				if (lh.v.u.ind_vt == v.k && lh.v.u.ind_t == v.u.info) {
					conflict = true;
					lh.v.u.ind_vt = VLOCAL;
					lh.v.u.ind_t = extra;  /* previous assignment will use safe copy */
				}
				/* index is the local being assigned? (index cannot be upvalue) */
				if (v.k == VLOCAL && lh.v.u.ind_idx == v.u.info) {
					conflict = true;
					lh.v.u.ind_idx = extra;  /* previous assignment will use safe copy */
				}
			}
		}
		if (conflict) {
		    /* copy upvalue/local value to a temporary (in position 'extra') */
		    int op = (v.k == VLOCAL) ? Lua.OP_MOVE : Lua.OP_GETUPVAL;
		    fs.codeABC(op, extra, v.u.info, 0);
		    fs.reserveregs(1);
		}
	}


	void assignment (LHS_assign lh, int nvars) {
		expdesc e = new expdesc();
		this.check_condition(VLOCAL <= lh.v.k && lh.v.k <= VINDEXED,
	                      "syntax error");
		if (this.testnext(',')) {  /* assignment -> `,' primaryexp assignment */
		    LHS_assign nv = new LHS_assign();
		    nv.prev = lh;
		    this.suffixedexp(nv.v);
		    if (nv.v.k != VINDEXED)
		      this.check_conflict(lh, nv.v);
		    this.assignment(nv, nvars+1);
		}
		else {  /* assignment . `=' explist1 */
		    int nexps;
		    this.checknext('=');
		    nexps = this.explist(e);
		    if (nexps != nvars) {
		      this.adjust_assign(nvars, nexps, e);
		      if (nexps > nvars)
		        this.fs.freereg -= nexps - nvars;  /* remove extra values */
	    }
	    else {
	    	fs.setoneret(e);  /* close last expression */
	    	fs.storevar(lh.v, e);
	    	return;  /* avoid default */
	    }
	  }
	  e.init(VNONRELOC, this.fs.freereg-1);  /* default assignment */
	  fs.storevar(lh.v, e);
	}


	int cond() {
		/* cond -> exp */
		expdesc v = new expdesc();
		/* read condition */
		this.expr(v);
		/* `falses' are all equal here */
		if (v.k == VNIL)
			v.k = VFALSE;
		fs.goiftrue(v);
		return v.f.i;
	}

	void gotostat(int pc) {
		int line = linenumber;
		LuaString label;
		int g;
		if (testnext(TK_GOTO))
			label = str_checkname();
		else {
			next();  /* skip break */
			label = LuaString.valueOf("break");
		}
		g = newlabelentry(dyd.gt =LuaC.grow(dyd.gt, dyd.n_gt+1), dyd.n_gt++, label, line, pc);
		findlabel(g);  /* close it if label already defined */
	}


	/* skip no-op statements */
	void skipnoopstat () {
		while (t.token == ';' || t.token == TK_DBCOLON)
			statement();
	}


	void labelstat (LuaString label, int line) {
		/* label -> '::' NAME '::' */
		int l;  /* index of new label being created */
		fs.checkrepeated(dyd.label, dyd.n_label, label);  /* check for repeated labels */
		checknext(TK_DBCOLON);  /* skip double colon */
		/* create new entry for this label */
		l = newlabelentry(dyd.label=LuaC.grow(dyd.label, dyd.n_label+1), dyd.n_label++, label, line, fs.pc);
		skipnoopstat();  /* skip other no-op statements */
		if (block_follow(false)) {  /* label is last no-op statement in the block? */
			/* assume that locals are already out of scope */
			dyd.label[l].nactvar = fs.bl.nactvar;
		}
		findgotos(dyd.label[l]);
}

	
	void whilestat (int line) {
		/* whilestat -> WHILE cond DO block END */
		FuncState fs = this.fs;
		int whileinit;
		int condexit;
		BlockCnt bl = new BlockCnt();
		this.next();  /* skip WHILE */
		whileinit = fs.getlabel();
		condexit = this.cond();
		fs.enterblock(bl, true);
		this.checknext(TK_DO);
		this.block();
		fs.patchlist(fs.jump(), whileinit);
		this.check_match(TK_END, TK_WHILE, line);
		fs.leaveblock();
		fs.patchtohere(condexit);  /* false conditions finish the loop */
	}

	void repeatstat(int line) {
		/* repeatstat -> REPEAT block UNTIL cond */
		int condexit;
		FuncState fs = this.fs;
		int repeat_init = fs.getlabel();
		BlockCnt bl1 = new BlockCnt();
		BlockCnt bl2 = new BlockCnt();
		fs.enterblock(bl1, true); /* loop block */
		fs.enterblock(bl2, false); /* scope block */
		this.next(); /* skip REPEAT */
		this.statlist();
		this.check_match(TK_UNTIL, TK_REPEAT, line);
		condexit = this.cond(); /* read condition (inside scope block) */
		if (bl2.upval) { /* upvalues? */
		    fs.patchclose(condexit, bl2.nactvar);
		}
		fs.leaveblock(); /* finish scope */
		fs.patchlist(condexit, repeat_init); /* close the loop */
		fs.leaveblock(); /* finish loop */
	}


	int exp1() {
		expdesc e = new expdesc();
		int k;
		this.expr(e);
		k = e.k;
		fs.exp2nextreg(e);
		return k;
	}


	void forbody(int base, int line, int nvars, boolean isnum) {
		/* forbody -> DO block */
		BlockCnt bl = new BlockCnt();
		FuncState fs = this.fs;
		int prep, endfor;
		this.adjustlocalvars(3); /* control variables */
		this.checknext(TK_DO);
		prep = isnum ? fs.codeAsBx(Lua.OP_FORPREP, base, NO_JUMP) : fs.jump();
		fs.enterblock(bl, false); /* scope for declared variables */
		this.adjustlocalvars(nvars);
		fs.reserveregs(nvars);
		this.block();
		fs.leaveblock(); /* end of scope for declared variables */
		fs.patchtohere(prep);
		if (isnum)  /* numeric for? */
			endfor = fs.codeAsBx(Lua.OP_FORLOOP, base, NO_JUMP);
		else {  /* generic for */
			fs.codeABC(Lua.OP_TFORCALL, base, 0, nvars);
			fs.fixline(line);
			endfor = fs.codeAsBx(Lua.OP_TFORLOOP, base + 2, NO_JUMP);
		}
		fs.patchlist(endfor, prep + 1);
		fs.fixline(line);
	}


	void fornum(LuaString varname, int line) {
		/* fornum -> NAME = exp1,exp1[,exp1] forbody */
		FuncState fs = this.fs;
		int base = fs.freereg;
		this.new_localvarliteral(RESERVED_LOCAL_VAR_FOR_INDEX);
		this.new_localvarliteral(RESERVED_LOCAL_VAR_FOR_LIMIT);
		this.new_localvarliteral(RESERVED_LOCAL_VAR_FOR_STEP);
		this.new_localvar(varname);
		this.checknext('=');
		this.exp1(); /* initial value */
		this.checknext(',');
		this.exp1(); /* limit */
		if (this.testnext(','))
			this.exp1(); /* optional step */
		else { /* default step = 1 */
			fs.codeABx(Lua.OP_LOADK, fs.freereg, fs.numberK(LuaInteger.valueOf(1)));
			fs.reserveregs(1);
		}
		this.forbody(base, line, 1, true);
	}


	void forlist(LuaString indexname) {
		/* forlist -> NAME {,NAME} IN explist1 forbody */
		FuncState fs = this.fs;
		expdesc e = new expdesc();
		int nvars = 4;   /* gen, state, control, plus at least one declared var */
		int line;
		int base = fs.freereg;
		/* create control variables */
		this.new_localvarliteral(RESERVED_LOCAL_VAR_FOR_GENERATOR);
		this.new_localvarliteral(RESERVED_LOCAL_VAR_FOR_STATE);
		this.new_localvarliteral(RESERVED_LOCAL_VAR_FOR_CONTROL);
		/* create declared variables */
		this.new_localvar(indexname);
		while (this.testnext(',')) {
			this.new_localvar(this.str_checkname());
			++nvars;
		}
		this.checknext(TK_IN);
		line = this.linenumber;
		this.adjust_assign(3, this.explist(e), e);
		fs.checkstack(3); /* extra space to call generator */
		this.forbody(base, line, nvars - 3, false);
	}


	void forstat(int line) {
		/* forstat -> FOR (fornum | forlist) END */
		FuncState fs = this.fs;
		LuaString varname;
		BlockCnt bl = new BlockCnt();
		fs.enterblock(bl, true); /* scope for loop and control variables */
		this.next(); /* skip `for' */
		varname = this.str_checkname(); /* first variable name */
		switch (this.t.token) {
		case '=':
			this.fornum(varname, line);
			break;
		case ',':
		case TK_IN:
			this.forlist(varname);
			break;
		default:
			this.syntaxerror(LUA_QL("=") + " or " + LUA_QL("in") + " expected");
		}
		this.check_match(TK_END, TK_FOR, line);
		fs.leaveblock(); /* loop scope (`break' jumps to this point) */
	}


	void test_then_block(IntPtr escapelist) {
		/* test_then_block -> [IF | ELSEIF] cond THEN block */
		expdesc v = new expdesc();
		BlockCnt bl = new BlockCnt();
		int jf;  /* instruction to skip 'then' code (if condition is false) */
		this.next(); /* skip IF or ELSEIF */
		expr(v);  /* read expression */
		this.checknext(TK_THEN);
		if (t.token == TK_GOTO || t.token == TK_BREAK) {
			fs.goiffalse(v); /* will jump to label if condition is true */
			fs.enterblock(bl, false); /* must enter block before 'goto' */
			gotostat(v.t.i); /* handle goto/break */
			skipnoopstat(); /* skip other no-op statements */
			if (block_follow(false)) { /* 'goto' is the entire block? */
				fs.leaveblock();
				return; /* and that is it */
			} else
				/* must skip over 'then' part if condition is false */
				jf = fs.jump();
		} else { /* regular case (not goto/break) */
			fs.goiftrue(v); /* skip over block if condition is false */
			fs.enterblock(bl, false);
			jf = v.f.i;
		}
		statlist(); /* `then' part */
		fs.leaveblock();
		if (t.token == TK_ELSE || t.token == TK_ELSEIF)
			fs.concat(escapelist, fs.jump()); /* must jump over it */
		fs.patchtohere(jf);
	}


	void ifstat(int line) {
		IntPtr escapelist = new IntPtr(NO_JUMP);  /* exit list for finished parts */
		test_then_block(escapelist);  /* IF cond THEN block */
		while (t.token == TK_ELSEIF)
		    test_then_block(escapelist);  /* ELSEIF cond THEN block */
		if (testnext(TK_ELSE))
		    block();  /* `else' part */
		check_match(TK_END, TK_IF, line);
		fs.patchtohere(escapelist.i);  /* patch escape list to 'if' end */
	}

	void localfunc() {
		expdesc b = new expdesc();
		FuncState fs = this.fs;
		this.new_localvar(this.str_checkname());
		this.adjustlocalvars(1);
		this.body(b, false, this.linenumber);
		/* debug information will only see the variable after this point! */
		fs.getlocvar(fs.nactvar - 1).startpc = fs.pc;
	}


	void localstat() {
		/* stat -> LOCAL NAME {`,' NAME} [`=' explist1] */
		int nvars = 0;
		int nexps;
		expdesc e = new expdesc();
		do {
			this.new_localvar(this.str_checkname());
			++nvars;
		} while (this.testnext(','));
		if (this.testnext('='))
			nexps = this.explist(e);
		else {
			e.k = VVOID;
			nexps = 0;
		}
		this.adjust_assign(nvars, nexps, e);
		this.adjustlocalvars(nvars);
	}


	boolean funcname(expdesc v) {
		/* funcname -> NAME {field} [`:' NAME] */
		boolean ismethod = false;
		this.singlevar(v);
		while (this.t.token == '.')
			this.fieldsel(v);
		if (this.t.token == ':') {
			ismethod = true;
			this.fieldsel(v);
		}
		return ismethod;
	}


	void funcstat(int line) {
		/* funcstat -> FUNCTION funcname body */
		boolean needself;
		expdesc v = new expdesc();
		expdesc b = new expdesc();
		this.next(); /* skip FUNCTION */
		needself = this.funcname(v);
		this.body(b, needself, line);
		fs.storevar(v, b);
		fs.fixline(line); /* definition `happens' in the first line */
	}


	void exprstat() {
		/* stat -> func | assignment */
		FuncState fs = this.fs;
		LHS_assign v = new LHS_assign();
		this.suffixedexp(v.v);
		if (t.token == '=' || t.token == ',') { /* stat -> assignment ? */
			v.prev = null;
			assignment(v, 1);
		}
		else {  /* stat -> func */
			check_condition(v.v.k == VCALL, "syntax error");
			LuaC.SETARG_C(fs.getcodePtr(v.v), 1);  /* call statement uses no results */
		}
	}

	void retstat() {
		/* stat -> RETURN explist */
		FuncState fs = this.fs;
		expdesc e = new expdesc();
		int first, nret; /* registers with returned values */
		if (block_follow(true) || this.t.token == ';')
			first = nret = 0; /* return no values */
		else {
			nret = this.explist(e); /* optional return values */
			if (hasmultret(e.k)) {
				fs.setmultret(e);
				if (e.k == VCALL && nret == 1) { /* tail call? */
					LuaC.SET_OPCODE(fs.getcodePtr(e), Lua.OP_TAILCALL);
					LuaC._assert (Lua.GETARG_A(fs.getcode(e)) == fs.nactvar);
				}
				first = fs.nactvar;
				nret = Lua.LUA_MULTRET; /* return all values */
			} else {
				if (nret == 1) /* only one single value? */
					first = fs.exp2anyreg(e);
				else {
					fs.exp2nextreg(e); /* values must go to the `stack' */
					first = fs.nactvar; /* return all `active' values */
					LuaC._assert (nret == fs.freereg - first);
				}
			}
		}
		fs.ret(first, nret);
		testnext(';');  /* skip optional semicolon */
	}

	void statement() {
		int line = this.linenumber; /* may be needed for error messages */
		enterlevel();
		switch (this.t.token) {
		case ';': { /* stat -> ';' (empty statement) */
			next(); /* skip ';' */
			break;
		}
		case TK_IF: { /* stat -> ifstat */
			this.ifstat(line);
			break;
		}
		case TK_WHILE: { /* stat -> whilestat */
			this.whilestat(line);
			break;
		}
		case TK_DO: { /* stat -> DO block END */
			this.next(); /* skip DO */
			this.block();
			this.check_match(TK_END, TK_DO, line);
			break;
		}
		case TK_FOR: { /* stat -> forstat */
			this.forstat(line);
			break;
		}
		case TK_REPEAT: { /* stat -> repeatstat */
			this.repeatstat(line);
			break;
		}
		case TK_FUNCTION: {
			this.funcstat(line); /* stat -> funcstat */
			break;
		}
		case TK_LOCAL: { /* stat -> localstat */
			this.next(); /* skip LOCAL */
			if (this.testnext(TK_FUNCTION)) /* local function? */
				this.localfunc();
			else
				this.localstat();
			break;
		}
		case TK_DBCOLON: { /* stat -> label */
			next(); /* skip double colon */
			labelstat(str_checkname(), line);
			break;
		}
		case TK_RETURN: { /* stat -> retstat */
		    next();  /* skip RETURN */
			this.retstat();
			break;
		}
		case TK_BREAK:
		case TK_GOTO: { /* stat -> breakstat */
			this.gotostat(fs.jump());
			break;
		}
		default: {
			this.exprstat();
			break;
		}
		}
		LuaC._assert(fs.f.maxstacksize >= fs.freereg
				&& fs.freereg >= fs.nactvar);
		fs.freereg = fs.nactvar; /* free registers */
		leavelevel();
	}

	void statlist() {
		/* statlist -> { stat [`;'] } */
		while (!block_follow(true)) {
			if (t.token == TK_RETURN) {
				statement();
				return; /* 'return' must be last statement */
			}
			statement();
		}
	}

	/*
	** compiles the main function, which is a regular vararg function with an
	** upvalue named LUA_ENV
	*/
	public void mainfunc(FuncState funcstate) {
		  BlockCnt bl = new BlockCnt();
		  open_func(funcstate, bl);
		  fs.f.is_vararg = 1;  /* main function is always vararg */
		  expdesc v = new expdesc();
		  v.init(VLOCAL, 0);  /* create and... */
		  fs.newupvalue(envn, v);  /* ...set environment upvalue */
		  next();  /* read first token */
		  statlist();  /* parse main body */
		  check(TK_EOS);
		  close_func();
	}
	
	/* }====================================================================== */
		
}
