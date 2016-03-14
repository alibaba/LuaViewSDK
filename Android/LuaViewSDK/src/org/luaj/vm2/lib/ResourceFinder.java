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

import java.io.InputStream;

/** 
 * Interface for opening application resource files such as scripts sources.  
 * <p>
 * This is used by required to load files that are part of 
 * the application, and implemented by BaseLib
 * for both the Jme and Jse platforms. 
 * <p>
 * The Jme version of base lib {@link BaseLib} 
 * implements {@link Globals#finder} via {@link Class#getResourceAsStream(String)}, 
 * while the Jse version {@link JseBaseLib} implements it using {@link java.io.File#File(String)}.
 * <p>
 * The io library does not use this API for file manipulation.
 * <p>
 * @see BaseLib
 * @see Globals#finder
 * @see JseBaseLib
 * @see JmePlatform
 * @see JsePlatform 
 */
public interface ResourceFinder {
	
	/** 
	 * Try to open a file, or return null if not found.
	 * 
	 * @see org.luaj.vm2.lib.BaseLib
	 * @see org.luaj.vm2.lib.jse.JseBaseLib
	 * 
	 * @param filename
	 * @return InputStream, or null if not found. 
	 */
	public InputStream findResource( String filename );
}