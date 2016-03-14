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

import java.util.Hashtable;

import org.luaj.vm2.LocVars;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.Upvaldesc;
import org.luaj.vm2.compiler.LexState.ConsControl;
import org.luaj.vm2.compiler.LexState.expdesc;


public class FuncState extends LuaC {

	static class BlockCnt {
		BlockCnt previous; /* chain */
		short firstlabel; /* index of first label in this block */
		short firstgoto; /* index of first pending goto in this block */
		short nactvar; /* # active locals outside the breakable structure */
		boolean upval; /* true if some variable in the block is an upvalue */
		boolean isloop; /* true if `block' is a loop */
	};
	
	Prototype f;  /* current function header */
	Hashtable h;  /* table to find (and reuse) elements in `k' */
	FuncState prev;  /* enclosing function */
	LexState ls;  /* lexical state */
	LuaC L;  /* compiler being invoked */
	BlockCnt bl;  /* chain of current blocks */
	int pc;  /* next position to code (equivalent to `ncode') */
	int lasttarget;   /* `pc' of last `jump target' */
	IntPtr jpc;  /* list of pending jumps to `pc' */
	int nk;  /* number of elements in `k' */
	int np;  /* number of elements in `p' */
	int firstlocal;  /* index of first local var (in Dyndata array) */
	short nlocvars;  /* number of elements in `locvars' */
	short nactvar;  /* number of active local variables */
	short nups;  /* number of upvalues */
	short freereg;  /* first free register */

	FuncState() {
	}
	
	
	// =============================================================
	// from lcode.h
	// =============================================================

	InstructionPtr getcodePtr(expdesc e) {
		return new InstructionPtr( f.code, e.u.info );
	}

	int getcode(expdesc e) {
		return f.code[e.u.info];
	}

	int codeAsBx(int o, int A, int sBx) {
		return codeABx(o,A,sBx+MAXARG_sBx);
	}

	void setmultret(expdesc e) {
		setreturns(e, LUA_MULTRET);
	}

	
	// =============================================================
	// from lparser.c
	// =============================================================

	/* check for repeated labels on the same block */
	void checkrepeated (LexState.Labeldesc[] ll, int ll_n, LuaString label) {
		int i;
		for (i = bl.firstlabel; i < ll_n; i++) {
			if (label.eq_b(ll[i].name)) {
				String msg = ls.L.pushfstring(
                          "label '" + label + " already defined on line " + ll[i].line);
				ls.semerror(msg);
			}
		}
	}


	void checklimit(int v, int l, String msg) {
		if ( v > l )
			errorlimit( l, msg );
	}
	
	void errorlimit (int limit, String what) {
		// TODO: report message logic.
	  	String msg = (f.linedefined == 0) ?
	  	    L.pushfstring("main function has more than "+limit+" "+what) :
	  	    L.pushfstring("function at line "+f.linedefined+" has more than "+limit+" "+what);
	  	  ls.lexerror(msg, 0);
	}

	LocVars getlocvar(int i) {
		int idx = ls.dyd.actvar[firstlocal + i].idx;
		_assert(idx < nlocvars);
		return f.locvars[idx];
	}

	void removevars (int tolevel) {
	  ls.dyd.n_actvar -= (nactvar - tolevel);
	  while (nactvar > tolevel)
	    getlocvar(--nactvar).endpc = pc;
	}


	int searchupvalue (LuaString name) {
	  int i;
	  Upvaldesc[] up = f.upvalues;
	  for (i = 0; i < nups; i++)
	    if (up[i].name.eq_b(name))
	    	return i;
	  return -1;  /* not found */
	}

	int newupvalue (LuaString name, expdesc v) {
		checklimit(nups + 1, LUAI_MAXUPVAL, "upvalues");
		if (f.upvalues == null || nups + 1 > f.upvalues.length)
			f.upvalues = realloc( f.upvalues, nups > 0 ? nups*2 : 1 );
		f.upvalues[nups] = new Upvaldesc(name, v.k == LexState.VLOCAL, v.u.info);
  		return nups++;
	}
		
	int searchvar(LuaString n) {
		int i;
		for (i = nactvar - 1; i >= 0; i--) {
			if (n.eq_b(getlocvar(i).varname))
				return i;
		}
		return -1; /* not found */
	}
		
	void markupval(int level) {
		BlockCnt bl = this.bl;
		while (bl.nactvar > level)
			bl = bl.previous;
		bl.upval = true;
	}
		
	static int singlevaraux(FuncState fs, LuaString n, expdesc var, int base) {
		if (fs == null)   /* no more levels? */
			return LexState.VVOID;  /* default is global */
		int v = fs.searchvar(n); /* look up at current level */
		if (v >= 0) {
			var.init(LexState.VLOCAL, v);
			if (base == 0)
				fs.markupval(v); /* local will be used as an upval */
			return LexState.VLOCAL;
		} else { /* not found at current level; try upvalues */
		    int idx = fs.searchupvalue(n);  /* try existing upvalues */
		    if (idx < 0) {  /* not found? */
		        if (singlevaraux(fs.prev, n, var, 0) == LexState.VVOID) /* try upper levels */
		          return LexState.VVOID;  /* not found; is a global */
		        /* else was LOCAL or UPVAL */
		        idx  = fs.newupvalue(n, var);  /* will be a new upvalue */
		    }
		    var.init(LexState.VUPVAL, idx);
		    return LexState.VUPVAL;
		}
	}

	/*
	** "export" pending gotos to outer level, to check them against
	** outer labels; if the block being exited has upvalues, and
	** the goto exits the scope of any variable (which can be the
	** upvalue), close those variables being exited.
	*/
	void movegotosout(BlockCnt bl) {
		int i = bl.firstgoto;
		final LexState.Labeldesc[] gl = ls.dyd.gt;
		/* correct pending gotos to current block and try to close it
		   with visible labels */
		while (i < ls.dyd.n_gt) {
			LexState.Labeldesc gt = gl[i];
			if (gt.nactvar > bl.nactvar) {
				if (bl.upval)
					patchclose(gt.pc, bl.nactvar);
				gt.nactvar = bl.nactvar;
			}
			if (!ls.findlabel(i))
				i++; /* move to next one */
		}
	}
	
	void enterblock (BlockCnt bl, boolean isloop) {
	  bl.isloop = isloop;
	  bl.nactvar = nactvar;
	  bl.firstlabel = (short) ls.dyd.n_label;
	  bl.firstgoto = (short) ls.dyd.n_gt;
	  bl.upval = false;
	  bl.previous = this.bl;
	  this.bl = bl;
	  _assert(this.freereg == this.nactvar);
	}

	void leaveblock() {
		BlockCnt bl = this.bl;
		if (bl.previous != null && bl.upval) {
		    /* create a 'jump to here' to close upvalues */
		    int j =  this.jump();
		    this.patchclose(j, bl.nactvar);
		    this.patchtohere(j);
		}
		if (bl.isloop)
		    ls.breaklabel();  /* close pending breaks */
		this.bl = bl.previous;
		this.removevars(bl.nactvar);
		_assert(bl.nactvar == this.nactvar);
		this.freereg = this.nactvar;  /* free registers */
		ls.dyd.n_label = bl.firstlabel;  /* remove local labels */
		if (bl.previous != null)  /* inner block? */
		    this.movegotosout(bl);  /* update pending gotos to outer block */
		else if (bl.firstgoto < ls.dyd.n_gt)  /* pending gotos in outer block? */
		    ls.undefgoto(ls.dyd.gt[bl.firstgoto]);  /* error */
	}

	void closelistfield(ConsControl cc) {
		if (cc.v.k == LexState.VVOID)
			return; /* there is no list item */
		this.exp2nextreg(cc.v);
		cc.v.k = LexState.VVOID;
		if (cc.tostore == LFIELDS_PER_FLUSH) {
			this.setlist(cc.t.u.info, cc.na, cc.tostore); /* flush */
			cc.tostore = 0; /* no more items pending */
		}
	}

	boolean hasmultret(int k) {
		return ((k) == LexState.VCALL || (k) == LexState.VVARARG);
	}

	void lastlistfield (ConsControl cc) {
		if (cc.tostore == 0) return;
		if (hasmultret(cc.v.k)) {
		    this.setmultret(cc.v);
		    this.setlist(cc.t.u.info, cc.na, LUA_MULTRET);
		    cc.na--;  /** do not count last expression (unknown number of elements) */
		}
		else {
		    if (cc.v.k != LexState.VVOID)
		    	this.exp2nextreg(cc.v);
		    this.setlist(cc.t.u.info, cc.na, cc.tostore);
		}
	}
	
	
	
	// =============================================================
	// from lcode.c
	// =============================================================

	void nil(int from, int n) {
		int l = from + n - 1;  /* last register to set nil */
		if (this.pc > this.lasttarget && pc > 0) {  /* no jumps to current position? */
			final int previous_code = f.code[pc - 1];
			if (GET_OPCODE(previous_code) == OP_LOADNIL) {
				int pfrom = GETARG_A(previous_code);
				int pl = pfrom + GETARG_B(previous_code);
				if ((pfrom <= from && from <= pl + 1)
						|| (from <= pfrom && pfrom <= l + 1)) { /* can connect both? */
					if (pfrom < from)
						from = pfrom; /* from = min(from, pfrom) */
					if (pl > l)
						l = pl; /* l = max(l, pl) */
					InstructionPtr previous = new InstructionPtr(this.f.code, this.pc - 1);
					SETARG_A(previous, from);
					SETARG_B(previous, l - from);
					return;
				}
			}  /* else go through */
		}
		this.codeABC(OP_LOADNIL, from, n - 1, 0); 
	}


	int jump() {
		int jpc = this.jpc.i; /* save list of jumps to here */
		this.jpc.i = LexState.NO_JUMP;
		IntPtr j = new IntPtr(this.codeAsBx(OP_JMP, 0, LexState.NO_JUMP));
		this.concat(j, jpc); /* keep them on hold */
		return j.i;
	}

	void ret(int first, int nret) {
		this.codeABC(OP_RETURN, first, nret + 1, 0);
	}

	int condjump(int /* OpCode */op, int A, int B, int C) {
		this.codeABC(op, A, B, C);
		return this.jump();
	}

	void fixjump(int pc, int dest) {
		InstructionPtr jmp = new InstructionPtr(this.f.code, pc);
		int offset = dest - (pc + 1);
		_assert (dest != LexState.NO_JUMP);
		if (Math.abs(offset) > MAXARG_sBx)
			ls.syntaxerror("control structure too long");
		SETARG_sBx(jmp, offset);
	}


	/*
	 * * returns current `pc' and marks it as a jump target (to avoid wrong *
	 * optimizations with consecutive instructions not in the same basic block).
	 */
	int getlabel() {
		this.lasttarget = this.pc;
		return this.pc;
	}


	int getjump(int pc) {
		int offset = GETARG_sBx(this.f.code[pc]);
		/* point to itself represents end of list */
		if (offset == LexState.NO_JUMP)
			/* end of list */
			return LexState.NO_JUMP;
		else
			/* turn offset into absolute position */
			return (pc + 1) + offset;
	}


	InstructionPtr getjumpcontrol(int pc) {
		InstructionPtr pi = new InstructionPtr(this.f.code, pc);
		if (pc >= 1 && testTMode(GET_OPCODE(pi.code[pi.idx - 1])))
			return new InstructionPtr(pi.code, pi.idx - 1);
		else
			return pi;
	}


	/*
	 * * check whether list has any jump that do not produce a value * (or
	 * produce an inverted value)
	 */
	boolean need_value(int list) {
		for (; list != LexState.NO_JUMP; list = this.getjump(list)) {
			int i = this.getjumpcontrol(list).get();
			if (GET_OPCODE(i) != OP_TESTSET)
				return true;
		}
		return false; /* not found */
	}


	boolean patchtestreg(int node, int reg) {
		InstructionPtr i = this.getjumpcontrol(node);
		if (GET_OPCODE(i.get()) != OP_TESTSET)
			/* cannot patch other instructions */
			return false;
		if (reg != NO_REG && reg != GETARG_B(i.get()))
			SETARG_A(i, reg);
		else
			/* no register to put value or register already has the value */
			i.set(CREATE_ABC(OP_TEST, GETARG_B(i.get()), 0, Lua.GETARG_C(i.get())));

		return true;
	}


	void removevalues(int list) {
		for (; list != LexState.NO_JUMP; list = this.getjump(list))
			this.patchtestreg(list, NO_REG);
	}

	void patchlistaux(int list, int vtarget, int reg, int dtarget) {
		while (list != LexState.NO_JUMP) {
			int next = this.getjump(list);
			if (this.patchtestreg(list, reg))
				this.fixjump(list, vtarget);
			else
				this.fixjump(list, dtarget); /* jump to default target */
			list = next;
		}
	}

	void dischargejpc() {
		this.patchlistaux(this.jpc.i, this.pc, NO_REG, this.pc);
		this.jpc.i = LexState.NO_JUMP;
	}

	void patchlist(int list, int target) {
		if (target == this.pc)
			this.patchtohere(list);
		else {
			_assert (target < this.pc);
			this.patchlistaux(list, target, NO_REG, target);
		}
	}

	void patchclose(int list, int level) {
		level++; /* argument is +1 to reserve 0 as non-op */
		while (list != LexState.NO_JUMP) {
			int next = getjump(list);
			_assert(GET_OPCODE(f.code[list]) == OP_JMP
					&& (GETARG_A(f.code[list]) == 0 || GETARG_A(f.code[list]) >= level));
			SETARG_A(f.code, list, level);
			list = next;
		}
	}

	void patchtohere(int list) {
		this.getlabel();
		this.concat(this.jpc, list);
	}

	void concat(IntPtr l1, int l2) {
		if (l2 == LexState.NO_JUMP)
			return;
		if (l1.i == LexState.NO_JUMP)
			l1.i = l2;
		else {
			int list = l1.i;
			int next;
			while ((next = this.getjump(list)) != LexState.NO_JUMP)
				/* find last element */
				list = next;
			this.fixjump(list, l2);
		}
	}

	void checkstack(int n) {
		int newstack = this.freereg + n;
		if (newstack > this.f.maxstacksize) {
			if (newstack >= MAXSTACK)
				ls.syntaxerror("function or expression too complex");
			this.f.maxstacksize = newstack;
		}
	}

	void reserveregs(int n) {
		this.checkstack(n);
		this.freereg += n;
	}

	void freereg(int reg) {
		if (!ISK(reg) && reg >= this.nactvar) {
			this.freereg--;
			_assert (reg == this.freereg);
		}
	}

	void freeexp(expdesc e) {
		if (e.k == LexState.VNONRELOC)
			this.freereg(e.u.info);
	}
	int addk(LuaValue v) {
		if (this.h == null) {
			this.h = new Hashtable();
		} else if (this.h.containsKey(v)) {
			return ((Integer) h.get(v)).intValue();
		} 
		final int idx = this.nk;
		this.h.put(v, new Integer(idx));
		final Prototype f = this.f;
		if (f.k == null || nk + 1 >= f.k.length)
			f.k = realloc( f.k, nk*2 + 1 );
		f.k[this.nk++] = v;
		return idx;
	}

	int stringK(LuaString s) {
		return this.addk(s);
	}

	int numberK(LuaValue r) {
		if ( r instanceof LuaDouble ) {
			double d = r.todouble();
			int i = (int) d;
			if ( d == (double) i ) 
				r = LuaInteger.valueOf(i);
		}
		return this.addk(r);
	}

	int boolK(boolean b) {
		return this.addk((b ? LuaValue.TRUE : LuaValue.FALSE));
	}

	int nilK() {
		return this.addk(LuaValue.NIL);
	}

	void setreturns(expdesc e, int nresults) {
		if (e.k == LexState.VCALL) { /* expression is an open function call? */
			SETARG_C(this.getcodePtr(e), nresults + 1);
		} else if (e.k == LexState.VVARARG) {
			SETARG_B(this.getcodePtr(e), nresults + 1);
			SETARG_A(this.getcodePtr(e), this.freereg);
			this.reserveregs(1);
		}
	}

	void setoneret(expdesc e) {
		if (e.k == LexState.VCALL) { /* expression is an open function call? */
			e.k = LexState.VNONRELOC;
			e.u.info = GETARG_A(this.getcode(e));
		} else if (e.k == LexState.VVARARG) {
			SETARG_B(this.getcodePtr(e), 2);
			e.k = LexState.VRELOCABLE; /* can relocate its simple result */
		}
	}

	void dischargevars(expdesc e) {
		switch (e.k) {
		case LexState.VLOCAL: {
			e.k = LexState.VNONRELOC;
			break;
		}
		case LexState.VUPVAL: {
			e.u.info = this.codeABC(OP_GETUPVAL, 0, e.u.info, 0);
			e.k = LexState.VRELOCABLE;
			break;
		}
		case LexState.VINDEXED: {
			int op = OP_GETTABUP;  /* assume 't' is in an upvalue */
			this.freereg(e.u.ind_idx);
			if (e.u.ind_vt == LexState.VLOCAL) {  /* 't' is in a register? */
				this.freereg(e.u.ind_t);
				op = OP_GETTABLE;
			}
			e.u.info = this.codeABC(op, 0, e.u.ind_t, e.u.ind_idx);
			e.k = LexState.VRELOCABLE;
			break;
		}
		case LexState.VVARARG:
		case LexState.VCALL: {
			this.setoneret(e);
			break;
		}
		default:
			break; /* there is one value available (somewhere) */
		}
	}

	int code_label(int A, int b, int jump) {
		this.getlabel(); /* those instructions may be jump targets */
		return this.codeABC(OP_LOADBOOL, A, b, jump);
	}

	void discharge2reg(expdesc e, int reg) {
		this.dischargevars(e);
		switch (e.k) {
		case LexState.VNIL: {
			this.nil(reg, 1);
			break;
		}
		case LexState.VFALSE:
		case LexState.VTRUE: {
			this.codeABC(OP_LOADBOOL, reg, (e.k == LexState.VTRUE ? 1 : 0),
					0);
			break;
		}
		case LexState.VK: {
			this.codeABx(OP_LOADK, reg, e.u.info);
			break;
		}
		case LexState.VKNUM: {
			this.codeABx(OP_LOADK, reg, this.numberK(e.u.nval()));
			break;
		}
		case LexState.VRELOCABLE: {
			InstructionPtr pc = this.getcodePtr(e);
			SETARG_A(pc, reg);
			break;
		}
		case LexState.VNONRELOC: {
			if (reg != e.u.info)
				this.codeABC(OP_MOVE, reg, e.u.info, 0);
			break;
		}
		default: {
			_assert (e.k == LexState.VVOID || e.k == LexState.VJMP);
			return; /* nothing to do... */
		}
		}
		e.u.info = reg;
		e.k = LexState.VNONRELOC;
	}

	void discharge2anyreg(expdesc e) {
		if (e.k != LexState.VNONRELOC) {
			this.reserveregs(1);
			this.discharge2reg(e, this.freereg - 1);
		}
	}

	void exp2reg(expdesc e, int reg) {
		this.discharge2reg(e, reg);
		if (e.k == LexState.VJMP)
			this.concat(e.t, e.u.info); /* put this jump in `t' list */
		if (e.hasjumps()) {
			int _final; /* position after whole expression */
			int p_f = LexState.NO_JUMP; /* position of an eventual LOAD false */
			int p_t = LexState.NO_JUMP; /* position of an eventual LOAD true */
			if (this.need_value(e.t.i) || this.need_value(e.f.i)) {
				int fj = (e.k == LexState.VJMP) ? LexState.NO_JUMP : this
						.jump();
				p_f = this.code_label(reg, 0, 1);
				p_t = this.code_label(reg, 1, 0);
				this.patchtohere(fj);
			}
			_final = this.getlabel();
			this.patchlistaux(e.f.i, _final, reg, p_f);
			this.patchlistaux(e.t.i, _final, reg, p_t);
		}
		e.f.i = e.t.i = LexState.NO_JUMP;
		e.u.info = reg;
		e.k = LexState.VNONRELOC;
	}

	void exp2nextreg(expdesc e) {
		this.dischargevars(e);
		this.freeexp(e);
		this.reserveregs(1);
		this.exp2reg(e, this.freereg - 1);
	}

	int exp2anyreg(expdesc e) {
		this.dischargevars(e);
		if (e.k == LexState.VNONRELOC) {
			if (!e.hasjumps())
				return e.u.info; /* exp is already in a register */
			if (e.u.info >= this.nactvar) { /* reg. is not a local? */
				this.exp2reg(e, e.u.info); /* put value on it */
				return e.u.info;
			}
		}
		this.exp2nextreg(e); /* default */
		return e.u.info;
	}

	void exp2anyregup (expdesc e) {
		if (e.k != LexState.VUPVAL || e.hasjumps())
			exp2anyreg(e);
	}

	void exp2val(expdesc e) {
		if (e.hasjumps())
			this.exp2anyreg(e);
		else
			this.dischargevars(e);
	}

	int exp2RK(expdesc e) {
		this.exp2val(e);
		switch (e.k) {
		case LexState.VTRUE:
		case LexState.VFALSE:
		case LexState.VNIL: {
			if (this.nk <= MAXINDEXRK) { /* constant fit in RK operand? */
				e.u.info = (e.k == LexState.VNIL) ? this.nilK()
								: this.boolK((e.k == LexState.VTRUE));
				e.k = LexState.VK;
				return RKASK(e.u.info);
			} else
				break;
		}
		case LexState.VKNUM: {
		      e.u.info = this.numberK(e.u.nval());
		      e.k = LexState.VK;
		      /* go through */			
		}
		case LexState.VK: {
			if (e.u.info <= MAXINDEXRK) /* constant fit in argC? */
				return RKASK(e.u.info);
			else
				break;
		}
		default:
			break;
		}
		/* not a constant in the right range: put it in a register */
		return this.exp2anyreg(e);
	}

	void storevar(expdesc var, expdesc ex) {
		switch (var.k) {
		case LexState.VLOCAL: {
			this.freeexp(ex);
			this.exp2reg(ex, var.u.info);
			return;
		}
		case LexState.VUPVAL: {
			int e = this.exp2anyreg(ex);
			this.codeABC(OP_SETUPVAL, e, var.u.info, 0);
			break;
		}
		case LexState.VINDEXED: {
			int op = (var.u.ind_vt == LexState.VLOCAL) ? OP_SETTABLE : OP_SETTABUP;
			int e = this.exp2RK(ex);
		    this.codeABC(op, var.u.ind_t, var.u.ind_idx, e);
			break;
		}
		default: {
			_assert (false); /* invalid var kind to store */
			break;
		}
		}
		this.freeexp(ex);
	}

	void self(expdesc e, expdesc key) {
		int func;
		this.exp2anyreg(e);
		this.freeexp(e);
		func = this.freereg;
		this.reserveregs(2);
		this.codeABC(OP_SELF, func, e.u.info, this.exp2RK(key));
		this.freeexp(key);
		e.u.info = func;
		e.k = LexState.VNONRELOC;
	}

	void invertjump(expdesc e) {
		InstructionPtr pc = this.getjumpcontrol(e.u.info);
		_assert (testTMode(GET_OPCODE(pc.get()))
				&& GET_OPCODE(pc.get()) != OP_TESTSET && Lua
				.GET_OPCODE(pc.get()) != OP_TEST);
		// SETARG_A(pc, !(GETARG_A(pc.get())));
		int a = GETARG_A(pc.get());
		int nota = (a!=0? 0: 1);
		SETARG_A(pc, nota);
	}

	int jumponcond(expdesc e, int cond) {
		if (e.k == LexState.VRELOCABLE) {
			int ie = this.getcode(e);
			if (GET_OPCODE(ie) == OP_NOT) {
				this.pc--; /* remove previous OP_NOT */
				return this.condjump(OP_TEST, GETARG_B(ie), 0, (cond!=0? 0: 1));
			}
			/* else go through */
		}
		this.discharge2anyreg(e);
		this.freeexp(e);
		return this.condjump(OP_TESTSET, NO_REG, e.u.info, cond);
	}

	void goiftrue(expdesc e) {
		int pc; /* pc of last jump */
		this.dischargevars(e);
		switch (e.k) {
		case LexState.VJMP: {
			this.invertjump(e);
			pc = e.u.info;
			break;
		}
		case LexState.VK:
		case LexState.VKNUM:
		case LexState.VTRUE: {
			pc = LexState.NO_JUMP; /* always true; do nothing */
			break;
		}
		default: {
			pc = this.jumponcond(e, 0);
			break;
		}
		}
		this.concat(e.f, pc); /* insert last jump in `f' list */
		this.patchtohere(e.t.i);
		e.t.i = LexState.NO_JUMP;
	}

	void goiffalse(expdesc e) {
		int pc; /* pc of last jump */
		this.dischargevars(e);
		switch (e.k) {
		case LexState.VJMP: {
			pc = e.u.info;
			break;
		}
		case LexState.VNIL:
		case LexState.VFALSE: {
			pc = LexState.NO_JUMP; /* always false; do nothing */
			break;
		}
		default: {
			pc = this.jumponcond(e, 1);
			break;
		}
		}
		this.concat(e.t, pc); /* insert last jump in `t' list */
		this.patchtohere(e.f.i);
		e.f.i = LexState.NO_JUMP;
	}

	void codenot(expdesc e) {
		this.dischargevars(e);
		switch (e.k) {
		case LexState.VNIL:
		case LexState.VFALSE: {
			e.k = LexState.VTRUE;
			break;
		}
		case LexState.VK:
		case LexState.VKNUM:
		case LexState.VTRUE: {
			e.k = LexState.VFALSE;
			break;
		}
		case LexState.VJMP: {
			this.invertjump(e);
			break;
		}
		case LexState.VRELOCABLE:
		case LexState.VNONRELOC: {
			this.discharge2anyreg(e);
			this.freeexp(e);
			e.u.info = this.codeABC(OP_NOT, 0, e.u.info, 0);
			e.k = LexState.VRELOCABLE;
			break;
		}
		default: {
			_assert (false); /* cannot happen */
			break;
		}
		}
		/* interchange true and false lists */
		{
			int temp = e.f.i;
			e.f.i = e.t.i;
			e.t.i = temp;
		}
		this.removevalues(e.f.i);
		this.removevalues(e.t.i);
	}

	static boolean vkisinreg(int k) {
		return ((k) == LexState.VNONRELOC || (k) == LexState.VLOCAL);
	}

	void indexed(expdesc t, expdesc k) {
		t.u.ind_t = (short) t.u.info;
		t.u.ind_idx = (short) this.exp2RK(k);
		LuaC._assert(t.k == LexState.VUPVAL || vkisinreg(t.k));
		t.u.ind_vt = (short) ((t.k == LexState.VUPVAL) ? LexState.VUPVAL : LexState.VLOCAL);
		t.k = LexState.VINDEXED;
	}

	boolean constfolding(int op, expdesc e1, expdesc e2) {
		LuaValue v1, v2, r;
		if (!e1.isnumeral() || !e2.isnumeral())
			return false;
		if ((op == OP_DIV || op == OP_MOD) && e2.u.nval().eq_b(LuaValue.ZERO))
			    return false;  /* do not attempt to divide by 0 */
		v1 = e1.u.nval();
		v2 = e2.u.nval();
		switch (op) {
		case OP_ADD:
			r = v1.add(v2);
			break;
		case OP_SUB:
			r = v1.sub(v2);
			break;
		case OP_MUL:
			r = v1.mul(v2);
			break;
		case OP_DIV:
			r = v1.div(v2);
			break;
		case OP_MOD:
			r = v1.mod(v2);
			break;
		case OP_POW:
			r = v1.pow(v2);
			break;
		case OP_UNM:
			r = v1.neg();
			break;
		case OP_LEN:
			// r = v1.len();
			// break;
			return false; /* no constant folding for 'len' */
		default:
			_assert (false);
			r = null;
			break;
		}
		if ( Double.isNaN(r.todouble()) )
			return false; /* do not attempt to produce NaN */
		e1.u.setNval( r );
		return true;
	}

	void codearith(int op, expdesc e1, expdesc e2, int line) {
		if (constfolding(op, e1, e2))
			return;
		else {
			int o2 = (op != OP_UNM && op != OP_LEN) ? this.exp2RK(e2)
					: 0;
			int o1 = this.exp2RK(e1);
			if (o1 > o2) {
				this.freeexp(e1);
				this.freeexp(e2);
			} else {
				this.freeexp(e2);
				this.freeexp(e1);
			}
			e1.u.info = this.codeABC(op, 0, o1, o2);
			e1.k = LexState.VRELOCABLE;
			fixline(line);
		}
	}

	void codecomp(int /* OpCode */op, int cond, expdesc e1, expdesc e2) {
		int o1 = this.exp2RK(e1);
		int o2 = this.exp2RK(e2);
		this.freeexp(e2);
		this.freeexp(e1);
		if (cond == 0 && op != OP_EQ) {
			int temp; /* exchange args to replace by `<' or `<=' */
			temp = o1;
			o1 = o2;
			o2 = temp; /* o1 <==> o2 */
			cond = 1;
		}
		e1.u.info = this.condjump(op, cond, o1, o2);
		e1.k = LexState.VJMP;
	}

	void prefix(int /* UnOpr */op, expdesc e, int line) {
		expdesc e2 = new expdesc();
		e2.init(LexState.VKNUM, 0);
		switch (op) {
		case LexState.OPR_MINUS: {
			if (e.isnumeral())  /* minus constant? */
				e.u.setNval(e.u.nval().neg());  /* fold it */
		    else {
		       this.exp2anyreg(e);
		       this.codearith(OP_UNM, e, e2, line);
		    }
			break;
		}
		case LexState.OPR_NOT:
			this.codenot(e);
			break;
		case LexState.OPR_LEN: {
			this.exp2anyreg(e); /* cannot operate on constants */
			this.codearith(OP_LEN, e, e2, line);
			break;
		}
		default:
			_assert (false);
		}
	}

	void infix(int /* BinOpr */op, expdesc v) {
		switch (op) {
		case LexState.OPR_AND: {
			this.goiftrue(v);
			break;
		}
		case LexState.OPR_OR: {
			this.goiffalse(v);
			break;
		}
		case LexState.OPR_CONCAT: {
			this.exp2nextreg(v); /* operand must be on the `stack' */
			break;
		}
		case LexState.OPR_ADD:
		case LexState.OPR_SUB:
		case LexState.OPR_MUL:
		case LexState.OPR_DIV:
		case LexState.OPR_MOD:
		case LexState.OPR_POW: {
			if (!v.isnumeral())
				this.exp2RK(v);
			break;
		}
		default: {
			this.exp2RK(v);
			break;
		}
		}
	}


	void posfix(int op, expdesc e1, expdesc e2, int line) {
		switch (op) {
		case LexState.OPR_AND: {
			_assert (e1.t.i == LexState.NO_JUMP); /* list must be closed */
			this.dischargevars(e2);
			this.concat(e2.f, e1.f.i);
			// *e1 = *e2;
			e1.setvalue(e2);
			break;
		}
		case LexState.OPR_OR: {
			_assert (e1.f.i == LexState.NO_JUMP); /* list must be closed */
			this.dischargevars(e2);
			this.concat(e2.t, e1.t.i);
			// *e1 = *e2;
			e1.setvalue(e2);
			break;
		}
		case LexState.OPR_CONCAT: {
			this.exp2val(e2);
			if (e2.k == LexState.VRELOCABLE
					&& GET_OPCODE(this.getcode(e2)) == OP_CONCAT) {
				_assert (e1.u.info == GETARG_B(this.getcode(e2)) - 1);
				this.freeexp(e1);
				SETARG_B(this.getcodePtr(e2), e1.u.info);
				e1.k = LexState.VRELOCABLE;
				e1.u.info = e2.u.info;
			} else {
				this.exp2nextreg(e2); /* operand must be on the 'stack' */
				this.codearith(OP_CONCAT, e1, e2, line);
			}
			break;
		}
		case LexState.OPR_ADD:
			this.codearith(OP_ADD, e1, e2, line);
			break;
		case LexState.OPR_SUB:
			this.codearith(OP_SUB, e1, e2, line);
			break;
		case LexState.OPR_MUL:
			this.codearith(OP_MUL, e1, e2, line);
			break;
		case LexState.OPR_DIV:
			this.codearith(OP_DIV, e1, e2, line);
			break;
		case LexState.OPR_MOD:
			this.codearith(OP_MOD, e1, e2, line);
			break;
		case LexState.OPR_POW:
			this.codearith(OP_POW, e1, e2, line);
			break;
		case LexState.OPR_EQ:
			this.codecomp(OP_EQ, 1, e1, e2);
			break;
		case LexState.OPR_NE:
			this.codecomp(OP_EQ, 0, e1, e2);
			break;
		case LexState.OPR_LT:
			this.codecomp(OP_LT, 1, e1, e2);
			break;
		case LexState.OPR_LE:
			this.codecomp(OP_LE, 1, e1, e2);
			break;
		case LexState.OPR_GT:
			this.codecomp(OP_LT, 0, e1, e2);
			break;
		case LexState.OPR_GE:
			this.codecomp(OP_LE, 0, e1, e2);
			break;
		default:
			_assert (false);
		}
	}


	void fixline(int line) {
		this.f.lineinfo[this.pc - 1] = line;
	}


	int code(int instruction, int line) {
		Prototype f = this.f;
		this.dischargejpc(); /* `pc' will change */
		/* put new instruction in code array */
		if (f.code == null || this.pc + 1 > f.code.length)
			f.code = LuaC.realloc(f.code, this.pc * 2 + 1);
		f.code[this.pc] = instruction;
		/* save corresponding line information */
		if (f.lineinfo == null || this.pc + 1 > f.lineinfo.length)
			f.lineinfo = LuaC.realloc(f.lineinfo,
					this.pc * 2 + 1);
		f.lineinfo[this.pc] = line;
		return this.pc++;
	}


	int codeABC(int o, int a, int b, int c) {
		_assert (getOpMode(o) == iABC);
		_assert (getBMode(o) != OpArgN || b == 0);
		_assert (getCMode(o) != OpArgN || c == 0);
		return this.code(CREATE_ABC(o, a, b, c), this.ls.lastline);
	}


	int codeABx(int o, int a, int bc) {
		_assert (getOpMode(o) == iABx || getOpMode(o) == iAsBx);
		_assert (getCMode(o) == OpArgN);
		_assert (bc >= 0 && bc <= Lua.MAXARG_Bx);
		return this.code(CREATE_ABx(o, a, bc), this.ls.lastline);
	}


	void setlist(int base, int nelems, int tostore) {
		int c = (nelems - 1) / LFIELDS_PER_FLUSH + 1;
		int b = (tostore == LUA_MULTRET) ? 0 : tostore;
		_assert (tostore != 0);
		if (c <= MAXARG_C)
			this.codeABC(OP_SETLIST, base, b, c);
		else {
			this.codeABC(OP_SETLIST, base, b, 0);
			this.code(c, this.ls.lastline);
		}
		this.freereg = (short) (base + 1); /* free registers with list values */
	}
	  
}
