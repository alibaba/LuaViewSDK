package org.luaj.vm2;

import org.luaj.vm2.LuaTable.Slot;

class NonTableMetatable implements Metatable {

	private final LuaValue value;

	public NonTableMetatable(LuaValue value) {
		this.value = value;
	}

	public boolean useWeakKeys() {
		return false;
	}

	public boolean useWeakValues() {
		return false;
	}

	public LuaValue toLuaValue() {
		return value;
	}

	public Slot entry(LuaValue key, LuaValue value) {
		return LuaTable.defaultEntry(key, value);
	}

	public LuaValue wrap(LuaValue value) {
		return value;
	}

	public LuaValue arrayget(LuaValue[] array, int index) {
		return array[index];
	}
}
