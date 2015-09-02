/**
 * 
 */
package org.luaj.vm2.luajc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class VarInfo {

	public static VarInfo INVALID = new VarInfo(-1, -1);

	public static VarInfo PARAM(int slot) {
		return new VarInfo(slot, -1) {
			public String toString() {
				return slot + ".p";
			}
		};
	}

	public static VarInfo NIL(final int slot) {
		return new VarInfo(slot, -1) {
			public String toString() {
				return "nil";
			}
		};
	}

	public static VarInfo PHI(final ProtoInfo pi, final int slot, final int pc) {
		return new PhiVarInfo(pi, slot, pc);
	}

	public final int slot; // where assigned
	public final int pc; // where assigned, or -1 if for block inputs

	public UpvalInfo upvalue; // not null if this var is an upvalue
	public boolean allocupvalue; // true if this variable allocates r/w upvalue
									// storage
	public boolean isreferenced; // true if this variable is refenced by some
									// opcode

	public VarInfo(int slot, int pc) {
		this.slot = slot;
		this.pc = pc;
	}

	public String toString() {
		return slot < 0 ? "x.x" : (slot + "." + pc);
	}

	/** Return replacement variable if there is exactly one value possible, 
	 * otherwise compute entire collection of variables and return null. 
	 * Computes the list of aall variable values, and saves it for the future. 
	 * 
	 * @return new Variable to replace with if there is only one value, or null to leave alone. 
	 */
	public VarInfo resolvePhiVariableValues() {
		return null;
	}

	protected void collectUniqueValues(Set visitedBlocks, Set vars) {
		vars.add(this);
	}

	public boolean isPhiVar() {
		return false;
	}

	private static final class PhiVarInfo extends VarInfo {
		private final ProtoInfo pi;
		VarInfo[] values;

		private PhiVarInfo(ProtoInfo pi, int slot, int pc) {
			super(slot, pc);
			this.pi = pi;
		}

		public boolean isPhiVar() {
			return true;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append( super.toString() );
			sb.append("={");
			for (int i=0, n=(values!=null? values.length : 0); i<n; i++) {
				if ( i>0 ) 
					sb.append( "," );
				sb.append(String.valueOf(values[i]));
			}
			sb.append("}");
			return sb.toString();
		}

		public VarInfo resolvePhiVariableValues() {
			Set visitedBlocks = new HashSet();
			Set vars = new HashSet();
			this.collectUniqueValues(visitedBlocks, vars);
			if (vars.contains(INVALID))
				return INVALID;
			int n = vars.size();
			Iterator it = vars.iterator();
			if (n == 1) {
				VarInfo v = (VarInfo) it.next();
				v.isreferenced |= this.isreferenced;
				return v;
			}
			this.values = new VarInfo[n];
			for ( int i=0; i<n; i++ ) {
				this.values[i] = (VarInfo) it.next();
				this.values[i].isreferenced |= this.isreferenced;
			}
			return null;
		}

		protected void collectUniqueValues(Set visitedBlocks, Set vars) {
			BasicBlock b = pi.blocks[pc];
			if ( pc == 0 )
				vars.add(pi.params[slot]);
			for (int i = 0, n = b.prev != null ? b.prev.length : 0; i < n; i++) {
				BasicBlock bp = b.prev[i];
				if (!visitedBlocks.contains(bp)) {
					visitedBlocks.add(bp);
					VarInfo v = pi.vars[slot][bp.pc1];
					if ( v != null )
						v.collectUniqueValues(visitedBlocks, vars);
				}
			}
		}
	}
}