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
 * Constants for lua limits and opcodes. 
 * <p>
 * This is a direct translation of C lua distribution header file constants
 * for bytecode creation and processing.  
 */
public class Lua {
	/** version is supplied by ant build task */
	public static final String _VERSION = "Luaj 0.0";
	
	/** use return values from previous op */
	public static final int LUA_MULTRET = -1;

	// from lopcodes.h

	/*===========================================================================
	  We assume that instructions are unsigned numbers.
	  All instructions have an opcode in the first 6 bits.
	  Instructions can have the following fields:
		`A' : 8 bits
		`B' : 9 bits
		`C' : 9 bits
		`Bx' : 18 bits (`B' and `C' together)
		`sBx' : signed Bx

	  A signed argument is represented in excess K; that is, the number
	  value is the unsigned value minus K. K is exactly the maximum value
	  for that argument (so that -max is represented by 0, and +max is
	  represented by 2*max), which is half the maximum for the corresponding
	  unsigned argument.
	===========================================================================*/


	/* basic instruction format */
	public static final int	iABC = 0;
	public static final int	iABx = 1;
	public static final int	iAsBx = 2;
	public static final int	iAx = 3;


	/*
	** size and position of opcode arguments.
	*/
	public static final int SIZE_C		= 9;
	public static final int SIZE_B		= 9;
	public static final int SIZE_Bx		= (SIZE_C + SIZE_B);
	public static final int SIZE_A		= 8;
	public static final int SIZE_Ax		= (SIZE_C + SIZE_B + SIZE_A);

	public static final int SIZE_OP		= 6;

	public static final int POS_OP		= 0;
	public static final int POS_A		= (POS_OP + SIZE_OP);
	public static final int POS_C		= (POS_A + SIZE_A);
	public static final int POS_B		= (POS_C + SIZE_C);
	public static final int POS_Bx		= POS_C;
	public static final int POS_Ax		= POS_A;


	public static final int MAX_OP          = ((1<<SIZE_OP)-1);
	public static final int MAXARG_A        = ((1<<SIZE_A)-1);
	public static final int MAXARG_B        = ((1<<SIZE_B)-1);
	public static final int MAXARG_C        = ((1<<SIZE_C)-1);
	public static final int MAXARG_Bx       = ((1<<SIZE_Bx)-1);
	public static final int MAXARG_sBx      = (MAXARG_Bx>>1);     	/* `sBx' is signed */
	public static final int MAXARG_Ax       = ((1<<SIZE_Ax)-1);

	public static final int MASK_OP = ((1<<SIZE_OP)-1)<<POS_OP; 
	public static final int MASK_A  = ((1<<SIZE_A)-1)<<POS_A; 
	public static final int MASK_B  = ((1<<SIZE_B)-1)<<POS_B; 
	public static final int MASK_C  = ((1<<SIZE_C)-1)<<POS_C; 
	public static final int MASK_Bx = ((1<<SIZE_Bx)-1)<<POS_Bx; 

	public static final int MASK_NOT_OP = ~MASK_OP; 
	public static final int MASK_NOT_A  = ~MASK_A; 
	public static final int MASK_NOT_B  = ~MASK_B; 
	public static final int MASK_NOT_C  = ~MASK_C; 
	public static final int MASK_NOT_Bx = ~MASK_Bx; 

	/*
	** the following macros help to manipulate instructions
	*/
	public static int GET_OPCODE(int i) {
		return (i >> POS_OP) & MAX_OP;
	}

	public static int GETARG_A(int i) {
		return (i >> POS_A) & MAXARG_A;
	}

	public static int GETARG_Ax(int i) {
		return (i >> POS_Ax) & MAXARG_Ax;
	}

	public static int GETARG_B(int i) {
		return (i >> POS_B) & MAXARG_B;
	}

	public static int GETARG_C(int i) {
		return (i >> POS_C) & MAXARG_C;
	}

	public static int GETARG_Bx(int i) {
		return (i >> POS_Bx) & MAXARG_Bx;
	}

	public static int GETARG_sBx(int i) {
		return ((i >> POS_Bx) & MAXARG_Bx) - MAXARG_sBx;
	}


	/*
	** Macros to operate RK indices
	*/

	/** this bit 1 means constant (0 means register) */
	public static final int BITRK		= (1 << (SIZE_B - 1));

	/** test whether value is a constant */
	public static boolean ISK(int x) {
		return 0 != ((x) & BITRK);
	}

	/** gets the index of the constant */
	public static int INDEXK(int r) {
		return ((int)(r) & ~BITRK);
	}

	public static final int MAXINDEXRK	= (BITRK - 1);

	/** code a constant index as a RK value */
	public static int RKASK(int x) {
		return ((x) | BITRK);
	}


	/**
	** invalid register that fits in 8 bits
	*/
	public static final int  NO_REG		= MAXARG_A;


	/*
	** R(x) - register
	** Kst(x) - constant (in constant table)
	** RK(x) == if ISK(x) then Kst(INDEXK(x)) else R(x)
	*/


	/*
	** grep "ORDER OP" if you change these enums
	*/

	/*----------------------------------------------------------------------
	name		args	description
	------------------------------------------------------------------------*/
	public static final int OP_MOVE = 0;/*	A B	R(A) := R(B)					*/
	public static final int OP_LOADK = 1;/*	A Bx	R(A) := Kst(Bx)					*/
	public static final int OP_LOADKX = 2;/*	A 	R(A) := Kst(extra arg)					*/
	public static final int OP_LOADBOOL = 3;/*	A B C	R(A) := (Bool)B; if (C) pc++			*/
	public static final int OP_LOADNIL = 4; /*	A B	R(A) := ... := R(A+B) := nil			*/
	public static final int OP_GETUPVAL = 5; /*	A B	R(A) := UpValue[B]				*/

	public static final int OP_GETTABUP = 6; /*	A B C	R(A) := UpValue[B][RK(C)]			*/
	public static final int OP_GETTABLE = 7; /*	A B C	R(A) := R(B)[RK(C)]				*/

	public static final int OP_SETTABUP = 8; /*	A B C	UpValue[A][RK(B)] := RK(C)			*/
	public static final int OP_SETUPVAL = 9; /*	A B	UpValue[B] := R(A)				*/
	public static final int OP_SETTABLE = 10; /*	A B C	R(A)[RK(B)] := RK(C)				*/

	public static final int OP_NEWTABLE = 11; /*	A B C	R(A) := {} (size = B,C)				*/

	public static final int OP_SELF = 12; /*	A B C	R(A+1) := R(B); R(A) := R(B)[RK(C)]		*/

	public static final int OP_ADD = 13; /*	A B C	R(A) := RK(B) + RK(C)				*/
	public static final int OP_SUB = 14; /*	A B C	R(A) := RK(B) - RK(C)				*/
	public static final int OP_MUL = 15; /*	A B C	R(A) := RK(B) * RK(C)				*/
	public static final int OP_DIV = 16; /*	A B C	R(A) := RK(B) / RK(C)				*/
	public static final int OP_MOD = 17; /*	A B C	R(A) := RK(B) % RK(C)				*/
	public static final int OP_POW = 18; /*	A B C	R(A) := RK(B) ^ RK(C)				*/
	public static final int OP_UNM = 19; /*	A B	R(A) := -R(B)					*/
	public static final int OP_NOT = 20; /*	A B	R(A) := not R(B)				*/
	public static final int OP_LEN = 21; /*	A B	R(A) := length of R(B)				*/

	public static final int OP_CONCAT = 22; /*	A B C	R(A) := R(B).. ... ..R(C)			*/

	public static final int OP_JMP = 23; /*	sBx	pc+=sBx					*/
	public static final int OP_EQ = 24; /*	A B C	if ((RK(B) == RK(C)) ~= A) then pc++		*/
	public static final int OP_LT = 25; /*	A B C	if ((RK(B) <  RK(C)) ~= A) then pc++  		*/
	public static final int OP_LE = 26; /*	A B C	if ((RK(B) <= RK(C)) ~= A) then pc++  		*/

	public static final int OP_TEST = 27; /*	A C	if not (R(A) <=> C) then pc++			*/ 
	public static final int OP_TESTSET = 28; /*	A B C	if (R(B) <=> C) then R(A) := R(B) else pc++	*/ 

	public static final int OP_CALL = 29; /*	A B C	R(A), ... ,R(A+C-2) := R(A)(R(A+1), ... ,R(A+B-1)) */
	public static final int OP_TAILCALL = 30; /*	A B C	return R(A)(R(A+1), ... ,R(A+B-1))		*/
	public static final int OP_RETURN = 31; /*	A B	return R(A), ... ,R(A+B-2)	(see note)	*/

	public static final int OP_FORLOOP = 32; /*	A sBx	R(A)+=R(A+2);
				if R(A) <?= R(A+1) then { pc+=sBx; R(A+3)=R(A) }*/
	public static final int OP_FORPREP = 33; /*	A sBx	R(A)-=R(A+2); pc+=sBx				*/

	public static final int OP_TFORCALL = 34; /* A C	R(A+3), ... ,R(A+2+C) := R(A)(R(A+1), R(A+2));	*/
	public static final int OP_TFORLOOP = 35; /* A sBx   if R(A+1) ~= nil then { R(A)=R(A+1); pc += sBx } */ 
	public static final int OP_SETLIST = 36; /*	A B C	R(A)[(C-1)*FPF+i] := R(A+i), 1 <= i <= B	*/

	public static final int OP_CLOSURE = 37; /*	A Bx	R(A) := closure(KPROTO[Bx], R(A), ... ,R(A+n))	*/

	public static final int OP_VARARG = 38; /*	A B	R(A), R(A+1), ..., R(A+B-1) = vararg		*/

	public static final int OP_EXTRAARG = 39; /* Ax	extra (larger) argument for previous opcode	*/

	public static final int NUM_OPCODES	= OP_EXTRAARG + 1;

	/* pseudo-opcodes used in parsing only.  */
	public static final int OP_GT  = 63; // > 
	public static final int OP_GE  = 62; // >=
	public static final int OP_NEQ = 61; // ~= 
	public static final int OP_AND = 60; // and 
	public static final int OP_OR  = 59; // or 
	
	/*===========================================================================
	  Notes:
	  (*) In OP_CALL, if (B == 0) then B = top. C is the number of returns - 1,
	      and can be 0: OP_CALL then sets `top' to last_result+1, so
	      next open instruction (OP_CALL, OP_RETURN, OP_SETLIST) may use `top'.

	  (*) In OP_VARARG, if (B == 0) then use actual number of varargs and
	      set top (like in OP_CALL with C == 0).

	  (*) In OP_RETURN, if (B == 0) then return up to `top'

	  (*) In OP_SETLIST, if (B == 0) then B = `top';
	      if (C == 0) then next `instruction' is real C

	  (*) For comparisons, A specifies what condition the test should accept
	      (true or false).

	  (*) All `skips' (pc++) assume that next instruction is a jump
	===========================================================================*/


	/*
	** masks for instruction properties. The format is:
	** bits 0-1: op mode
	** bits 2-3: C arg mode
	** bits 4-5: B arg mode
	** bit 6: instruction set register A
	** bit 7: operator is a test
	*/  

	  public static final int OpArgN = 0;  /* argument is not used */
	  public static final int OpArgU = 1;  /* argument is used */
	  public static final int OpArgR = 2;  /* argument is a register or a jump offset */
	  public static final int OpArgK = 3;  /* argument is a constant or register/constant */

	  public static final int[] luaP_opmodes = {
	  /*   T        A           B             C          mode		   opcode	*/
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_MOVE */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgN<<2) | (iABx),		/* OP_LOADK */
		 (0<<7) | (1<<6) | (OpArgN<<4) | (OpArgN<<2) | (iABx),		/* OP_LOADKX */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_LOADBOOL */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_LOADNIL */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_GETUPVAL */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgK<<2) | (iABC),		/* OP_GETTABUP */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgK<<2) | (iABC),		/* OP_GETTABLE */
		 (0<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_SETTABUP */
		 (0<<7) | (0<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_SETUPVAL */
		 (0<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_SETTABLE */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_NEWTABLE */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgK<<2) | (iABC),		/* OP_SELF */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_ADD */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_SUB */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_MUL */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_DIV */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_MOD */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_POW */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_UNM */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_NOT */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_LEN */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgR<<2) | (iABC),		/* OP_CONCAT */
		 (0<<7) | (0<<6) | (OpArgR<<4) | (OpArgN<<2) | (iAsBx),		/* OP_JMP */
		 (1<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_EQ */
		 (1<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_LT */
		 (1<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_LE */
		 (1<<7) | (0<<6) | (OpArgN<<4) | (OpArgU<<2) | (iABC),		/* OP_TEST */
		 (1<<7) | (1<<6) | (OpArgR<<4) | (OpArgU<<2) | (iABC),		/* OP_TESTSET */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_CALL */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_TAILCALL */
		 (0<<7) | (0<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_RETURN */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iAsBx),		/* OP_FORLOOP */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iAsBx),		/* OP_FORPREP */
		 (0<<7) | (0<<6) | (OpArgN<<4) | (OpArgU<<2) | (iABC),		/* OP_TFORCALL */
		 (1<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iAsBx),		/* OP_TFORLOOP */
		 (0<<7) | (0<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_SETLIST */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABx),		/* OP_CLOSURE */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_VARARG */
		 (0<<7) | (0<<6) | (OpArgU<<4) | (OpArgU<<2) | (iAx),		/* OP_EXTRAARG */
	  };

	public static int getOpMode(int m) {
		return luaP_opmodes[m] & 3;
	}
	public static int getBMode(int m) {
		return (luaP_opmodes[m] >> 4) & 3;
	}
	public static int getCMode(int m) {
		return (luaP_opmodes[m] >> 2) & 3;
	}
	public static boolean testAMode(int m) {
		return 0 != (luaP_opmodes[m] & (1 << 6));
	}
	public static boolean testTMode(int m) {
		return 0 != (luaP_opmodes[m] & (1 << 7));
	}

	/* number of list items to accumulate before a SETLIST instruction */
	public static final int LFIELDS_PER_FLUSH = 50;

	private static final int MAXSRC = 80;

	public static String chunkid( String source ) {
		 if ( source.startsWith("=") )
			 return source.substring(1);
		 String end = "";
		 if ( source.startsWith("@") ) {
			 source = source.substring(1);
		 } else {
			 source = "[string \""+source;
			 end = "\"]";
		 }
		 int n = source.length() + end.length(); 
		 if ( n > MAXSRC )
			 source = source.substring(0,MAXSRC-end.length()-3) + "...";
		 return source + end;
	}
}
