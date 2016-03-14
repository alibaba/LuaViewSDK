/*******************************************************************************
 * Copyright (c) 2013 Luaj.org. All rights reserved.
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

import org.luaj.vm2.LuaTable.Slot;

/**
 * Provides operations that depend on the __mode key of the metatable.
 */
interface Metatable {

	/** Return whether or not this table's keys are weak. */
	public boolean useWeakKeys();

	/** Return whether or not this table's values are weak. */
	public boolean useWeakValues();

	/** Return this metatable as a LuaValue. */
	public LuaValue toLuaValue();

	/** Return an instance of Slot appropriate for the given key and value. */
	public Slot entry( LuaValue key, LuaValue value );

	/** Returns the given value wrapped in a weak reference if appropriate. */
	public LuaValue wrap( LuaValue value );

	/**
	 * Returns the value at the given index in the array, or null if it is a weak reference that
	 * has been dropped.
	 */
	public LuaValue arrayget(LuaValue[] array, int index);
}
