/**
 * 
 */
package org.luaj.vm2.luajc;

import org.luaj.vm2.Lua;

public class UpvalInfo {
	ProtoInfo pi;    // where defined
	int slot;       // where defined
	int nvars;		// number of vars involved
	VarInfo var[];	// list of vars
	boolean rw;     // read-write

	// Upval info representing the implied context containing only the environment.
	public UpvalInfo(ProtoInfo pi) {
		this.pi = pi;
		this.slot = 0;
		this.nvars = 1;
		this.var = new VarInfo[] { VarInfo.PARAM(0) };
		this.rw = false;
	}

	public UpvalInfo(ProtoInfo pi, int pc, int slot) {
		this.pi = pi;
		this.slot = slot;
		this.nvars = 0;
		this.var = null;
		includeVarAndPosteriorVars( pi.vars[slot][pc] );
		for ( int i=0; i<nvars; i++ )
			var[i].allocupvalue = testIsAllocUpvalue( var[i] );
		this.rw = nvars > 1;		
	}

	private boolean includeVarAndPosteriorVars( VarInfo var ) {
		if ( var == null || var == VarInfo.INVALID )
			return false;
		if ( var.upvalue == this )
			return true;
		var.upvalue = this;
		appendVar( var );
		if ( isLoopVariable( var ) )
			return false;
		boolean loopDetected = includePosteriorVarsCheckLoops( var );
		if ( loopDetected )
			includePriorVarsIgnoreLoops( var );
		return loopDetected;
	}
	
	private boolean isLoopVariable(VarInfo var) {
		if ( var.pc >= 0 ) {
			switch ( Lua.GET_OPCODE(pi.prototype.code[var.pc]) ) {
			case Lua.OP_TFORLOOP:
			case Lua.OP_FORLOOP:
				return true;
			}
		}
		return false;
	}

	private boolean includePosteriorVarsCheckLoops( VarInfo prior ) {
		boolean loopDetected = false;
		for ( int i=0, n=pi.blocklist.length; i<n; i++ ) {
			BasicBlock b = pi.blocklist[i];
			VarInfo v = pi.vars[slot][b.pc1];
			if ( v == prior ) {
				for ( int j=0, m=b.next!=null? b.next.length: 0; j<m; j++ ) {
					BasicBlock b1 = b.next[j];
					VarInfo v1 = pi.vars[slot][b1.pc0];
					if ( v1 != prior ) {
						loopDetected |= includeVarAndPosteriorVars( v1 );
						if ( v1.isPhiVar() )
							includePriorVarsIgnoreLoops( v1 );
					}
				}
			} else {
				for ( int pc=b.pc1-1; pc>=b.pc0; pc-- ) {
					if ( pi.vars[slot][pc] == prior ) {
						loopDetected |= includeVarAndPosteriorVars( pi.vars[slot][pc+1] );
						break;
					}
				}
			}
		}
		return loopDetected;
	}
	
	private void includePriorVarsIgnoreLoops(VarInfo poster) {
		for ( int i=0, n=pi.blocklist.length; i<n; i++ ) {
			BasicBlock b = pi.blocklist[i];
			VarInfo v = pi.vars[slot][b.pc0];
			if ( v == poster ) {
				for ( int j=0, m=b.prev!=null? b.prev.length: 0; j<m; j++ ) {
					BasicBlock b0 = b.prev[j];
					VarInfo v0 = pi.vars[slot][b0.pc1];
					if ( v0 != poster )
						includeVarAndPosteriorVars( v0 );
				}
			} else {
				for ( int pc=b.pc0+1; pc<=b.pc1; pc++ ) {
					if (  pi.vars[slot][pc] == poster ) {
						includeVarAndPosteriorVars( pi.vars[slot][pc-1] );
						break;
					}
				}
			}
		}
	}

	private void appendVar(VarInfo v) {
		if ( nvars == 0 ) {
			var = new VarInfo[1];
		} else if ( nvars+1 >= var.length ) {
			VarInfo[] s = var;
			var = new VarInfo[nvars*2+1];
			System.arraycopy(s, 0, var, 0, nvars);				
		}
		var[nvars++] = v;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append( pi.name );
		for ( int i=0; i<nvars; i++ ) {
			sb.append( i>0? ",": " " );
			sb.append( String.valueOf(var[i]));
		}
		if ( rw )
			sb.append( "(rw)" );
		return sb.toString();
	}
	
	private boolean testIsAllocUpvalue(VarInfo v) {
		if ( v.pc < 0 )
			return true;
		BasicBlock b = pi.blocks[v.pc];
		if ( v.pc > b.pc0 )
			return pi.vars[slot][v.pc-1].upvalue != this;
		if ( b.prev == null ) {
			v = pi.params[slot];
			if ( v != null && v.upvalue != this )
				return true;
		} else {
			for ( int i=0, n=b.prev.length; i<n; i++ ) {
				v = pi.vars[slot][b.prev[i].pc1];
				if ( v != null && v.upvalue != this )
					return true;
			}
		}
		return false;
	}

}