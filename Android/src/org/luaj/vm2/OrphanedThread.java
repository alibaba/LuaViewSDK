/*******************************************************************************
 * Copyright (c) 2012 Luaj.org. All rights reserved.
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
 * {@link java.lang.Error} sublcass that indicates a lua thread that is no
 * longer referenced has been detected.
 * <p>
 * The java thread in which this is thrown should correspond to a
 * {@link LuaThread} being used as a coroutine that could not possibly be
 * resumed again because there are no more references to the LuaThread with
 * which it is associated. Rather than locking up resources forever, this error
 * is thrown, and should fall through all the way to the thread's {@link Thread.run}() method.
 * <p>
 * Java code mixed with the luaj vm should not catch this error because it may
 * occur when the coroutine is not running, so any processing done during error
 * handling could break the thread-safety of the application because other lua
 * processing could be going on in a different thread.
 */
public class OrphanedThread extends Error {

	public OrphanedThread() {
		super("orphaned thread");
	}
}
