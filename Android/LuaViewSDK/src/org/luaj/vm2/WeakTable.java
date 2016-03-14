/*******************************************************************************
 * Copyright (c) 2009-2011, 2013 Luaj.org. All rights reserved.
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

import java.lang.ref.WeakReference;

import org.luaj.vm2.LuaTable.Slot;
import org.luaj.vm2.LuaTable.StrongSlot;

/**
 * Subclass of {@link LuaTable} that provides weak key and weak value semantics. 
 * <p> 
 * Normally these are not created directly, but indirectly when changing the mode 
 * of a {@link LuaTable} as lua script executes.  
 * <p>
 * However, calling the constructors directly when weak tables are required from 
 * Java will reduce overhead.  
 */
public class WeakTable implements Metatable {

	private boolean weakkeys, weakvalues;
	private LuaValue backing;

	public static LuaTable make(boolean weakkeys, boolean weakvalues) {
		LuaString mode;
		if ( weakkeys && weakvalues ) {
			mode = LuaString.valueOf("kv");
		} else if ( weakkeys ) {
			mode = LuaString.valueOf("k");
		} else if ( weakvalues ) {
			mode = LuaString.valueOf("v");
		} else {
			return LuaTable.tableOf();
		}
		LuaTable table = LuaTable.tableOf();
		LuaTable mt = LuaTable.tableOf(new LuaValue[] { LuaValue.MODE, mode });
		table.setmetatable(mt);
		return table;
	}

	/**
	 * Construct a table with weak keys, weak values, or both
	 * @param weakkeys true to let the table have weak keys
	 * @param weakvalues true to let the table have weak values
	 */
	public WeakTable(boolean weakkeys, boolean weakvalues, LuaValue backing) {
		this.weakkeys = weakkeys;
		this.weakvalues = weakvalues;
		this.backing = backing;
	}

	public boolean useWeakKeys() {
		return weakkeys;
	}

	public boolean useWeakValues() {
		return weakvalues;
	}

	public LuaValue toLuaValue() {
		return backing;
	}

	public Slot entry(LuaValue key, LuaValue value) {
		value = value.strongvalue();
		if ( value == null )
			return null;
		if ( weakkeys && !( key.isnumber() || key.isstring() || key.isboolean() )) {
			if ( weakvalues && !( value.isnumber() || value.isstring() || value.isboolean() )) {
				return new WeakKeyAndValueSlot( key, value, null );
			} else {
				return new WeakKeySlot( key, value, null );
			}
		}
		if ( weakvalues && ! (value.isnumber() || value.isstring() || value.isboolean() )) {
			return new WeakValueSlot( key, value, null );
		}
		return LuaTable.defaultEntry( key, value );
	}

	public static abstract class WeakSlot implements Slot {

		protected Object key;
		protected Object value;
		protected Slot next;

		protected WeakSlot(Object key, Object value, Slot next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public abstract int keyindex( int hashMask );

		public abstract Slot set(LuaValue value);

		public StrongSlot first() {
			LuaValue key = strongkey();
			LuaValue value = strongvalue();
			if ( key != null && value != null ) {
				return new LuaTable.NormalEntry(key, value);
			} else {
				this.key = null;
				this.value = null;
				return null;
			}
		}

		public StrongSlot find(LuaValue key) {
			StrongSlot first = first();
			return ( first != null ) ? first.find( key ) : null;
		}

		public boolean keyeq(LuaValue key) {
			StrongSlot first = first();
			return ( first != null ) && first.keyeq( key );
		}

		public Slot rest() {
			return next;
		}

		public int arraykey(int max) {
			// Integer keys can never be weak.
			return 0;
		}

		public Slot set(StrongSlot target, LuaValue value) {
			LuaValue key = strongkey();
			if ( key != null && target.find( key ) != null ) {
				return set( value );
			} else if ( key != null ) {
				// Our key is still good.
				next = next.set( target, value );
				return this;
			} else {
				// our key was dropped, remove ourselves from the chain.
				return next.set( target, value );
			}
		}

		public Slot add( Slot entry ) {
			next = ( next != null ) ? next.add( entry ) : entry;
			if ( strongkey() != null && strongvalue() != null ) {
				return this;
			} else {
				return next;
			}
		}

		public Slot remove( StrongSlot target ) {
			LuaValue key = strongkey();
			if ( key == null ) {
				return next.remove( target );
			} else if ( target.keyeq( key ) ) {
				this.value = null;
				return this;
			} else {
				next = next.remove( target );
				return this;
			}
		}

		public Slot relink( Slot rest ) {
			if ( strongkey() != null && strongvalue() != null ) {
				if ( rest == null && this.next == null ) {
					return this;
				} else {
					return copy( rest );
				}
			} else {
				return rest;
			}
		}

		public LuaValue strongkey() {
			return (LuaValue) key;
		}

		public LuaValue strongvalue() {
			return (LuaValue) value;
		}

		protected abstract WeakSlot copy( Slot next );
	}

	static class WeakKeySlot extends WeakSlot {

		private final int keyhash;

		protected WeakKeySlot( LuaValue key, LuaValue value, Slot next ) {
			super(weaken(key), value, next);
			keyhash = key.hashCode();
		}

		protected WeakKeySlot( WeakKeySlot copyFrom, Slot next ) {
			super( copyFrom.key, copyFrom.value, next );
			this.keyhash = copyFrom.keyhash;
		}

		public int keyindex( int mask ) {
			return LuaTable.hashmod( keyhash, mask );
		}

		public Slot set(LuaValue value) {
			this.value = value;
			return this;
		}

		public LuaValue strongkey() {
			return strengthen( key );
		}

		protected WeakSlot copy( Slot rest ) {
			return new WeakKeySlot( this, rest );
		}
	}

	static class WeakValueSlot extends WeakSlot {

		protected WeakValueSlot( LuaValue key, LuaValue value, Slot next ) {
			super( key, weaken(value), next);
		}

		protected WeakValueSlot( WeakValueSlot copyFrom, Slot next ) {
			super( copyFrom.key, copyFrom.value, next );
		}

		public int keyindex( int mask ) {
			return LuaTable.hashSlot( strongkey(), mask );
		}

		public Slot set(LuaValue value) {
			this.value = weaken(value);
			return this;
		}

		public LuaValue strongvalue() {
			return strengthen( value );
		}

		protected WeakSlot copy(Slot next) {
			return new WeakValueSlot( this, next );
		}
	}

	static class WeakKeyAndValueSlot extends WeakSlot {

		private final int keyhash;

		protected WeakKeyAndValueSlot( LuaValue key, LuaValue value, Slot next ) {
			super( weaken(key), weaken(value), next );
			keyhash = key.hashCode();
		}

		protected WeakKeyAndValueSlot(WeakKeyAndValueSlot copyFrom, Slot next) {
			super( copyFrom.key, copyFrom.value, next );
			keyhash = copyFrom.keyhash;
		}

		public int keyindex( int hashMask ) {
			return LuaTable.hashmod( keyhash, hashMask );
		}

		public Slot set(LuaValue value) {
			this.value = weaken(value);
			return this;
		}

		public LuaValue strongkey() {
			return strengthen( key );
		}

		public LuaValue strongvalue() {
			return strengthen( value );
		}

		protected WeakSlot copy( Slot next ) {
			return new WeakKeyAndValueSlot( this, next );
		}
	}

	/**
	 * Self-sent message to convert a value to its weak counterpart
	 * @param value value to convert
	 * @return {@link LuaValue} that is a strong or weak reference, depending on type of {@code value}
	 */
	protected static LuaValue weaken( LuaValue value ) {
		switch ( value.type() ) {
			case LuaValue.TFUNCTION:
			case LuaValue.TTHREAD:
			case LuaValue.TTABLE:
				return new WeakValue(value);
			case LuaValue.TUSERDATA:
				return new WeakUserdata(value);
			default:
				return value;
		}
	}

	/**
	 * Unwrap a LuaValue from a WeakReference and/or WeakUserdata.
	 * @param ref reference to convert
	 * @return LuaValue or null
	 * @see #weaken(LuaValue)
	 */
	protected static LuaValue strengthen(Object ref) {
		if ( ref instanceof WeakReference ) {
			ref = ((WeakReference) ref).get();
		}
		if ( ref instanceof WeakValue ) {
			return ((WeakValue) ref).strongvalue();
		}
		return (LuaValue) ref;
	}

	/** Internal class to implement weak values. 
	 * @see WeakTable
	 */
	static class WeakValue extends LuaValue {
		WeakReference ref;

		protected WeakValue(LuaValue value) {
			ref = new WeakReference(value);
		}

		public int type() {
			illegal("type","weak value");
			return 0;
		}

		public String typename() {
			illegal("typename","weak value");
			return null;
		}

		public String toString() {
			return "weak<"+ref.get()+">";
		}

		public LuaValue strongvalue() {
			Object o = ref.get();
			return (LuaValue)o;
		}

		public boolean raweq(LuaValue rhs) {
			Object o = ref.get();
			return o!=null && rhs.raweq((LuaValue)o);
		}
	}

	/** Internal class to implement weak userdata values. 
	 * @see WeakTable
	 */
	static final class WeakUserdata extends WeakValue {
		private final WeakReference ob;
		private final LuaValue mt;

		private WeakUserdata(LuaValue value) {
			super(value);
			ob = new WeakReference(value.touserdata());
			mt = value.getmetatable();
		}

		public LuaValue strongvalue() {
			Object u = ref.get();
			if ( u != null )
				return (LuaValue) u;
			Object o = ob.get();
			if ( o != null ) {
				LuaValue ud = LuaValue.userdataOf(o,mt);
				ref = new WeakReference(ud);
				return ud;
			} else {
				return null;
			}
		}
	}

	public LuaValue wrap(LuaValue value) {
		return weakvalues ? weaken( value ) : value;
	}

	public LuaValue arrayget(LuaValue[] array, int index) {
		LuaValue value = array[index];
		if (value != null) {
			value = strengthen(value);
			if (value == null) {
				array[index] = null;
			}
		}
		return value;
	}
}
