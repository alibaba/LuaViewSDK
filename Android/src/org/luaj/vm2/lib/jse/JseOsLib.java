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
package org.luaj.vm2.lib.jse;

import java.io.File;
import java.io.IOException;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * Subclass of {@link LibFunction} which implements the standard lua {@code os} library.
 * <p>
 * This contains more complete implementations of the following functions 
 * using features that are specific to JSE:   
 * <ul>
 * <li>{@code execute()}</li>
 * <li>{@code remove()}</li>
 * <li>{@code rename()}</li>
 * <li>{@code tmpname()}</li>
 * </ul>
 * <p>
 * Because the nature of the {@code os} library is to encapsulate 
 * os-specific features, the behavior of these functions varies considerably 
 * from their counterparts in the C platform.  
 * <p>
 * Typically, this library is included as part of a call to 
 * {@link JsePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * System.out.println( globals.get("os").get("time").call() );
 * } </pre>
 * <p>
 * For special cases where the smallest possible footprint is desired, 
 * a minimal set of libraries could be loaded
 * directly via {@link Globals#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new JseOsLib());
 * System.out.println( globals.get("os").get("time").call() );
 * } </pre>
 * <p>However, other libraries such as <em>MathLib</em> are not loaded in this case.
 * <p>
 * @see LibFunction
 * @see OsLib
 * @see JsePlatform
 * @see org.luaj.vm2.lib.jme.JmePlatform
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.9">Lua 5.2 OS Lib Reference</a>
 */
public class JseOsLib extends org.luaj.vm2.lib.OsLib {
	
	/** return code indicating the execute() threw an I/O exception */
	public static int EXEC_IOEXCEPTION =  1;
	
	/** return code indicating the execute() was interrupted */
	public static int EXEC_INTERRUPTED = -2;
	
	/** return code indicating the execute() threw an unknown exception */
	public static int EXEC_ERROR       = -3;
	
	/** public constructor */
	public JseOsLib() {
	}

	protected Varargs execute(String command) {
		int exitValue;
		try {
			exitValue = new JseProcess(command, null, globals.STDOUT, globals.STDERR).waitFor();
		} catch (IOException ioe) {
			exitValue = EXEC_IOEXCEPTION;
		} catch (InterruptedException e) {
			exitValue = EXEC_INTERRUPTED;
		} catch (Throwable t) {
			exitValue = EXEC_ERROR;
		}
		if (exitValue == 0)
			return varargsOf(TRUE, valueOf("exit"), ZERO);
		return varargsOf(NIL, valueOf("signal"), valueOf(exitValue));
	}

	protected void remove(String filename) throws IOException {
		File f = new File(filename);
		if ( ! f.exists() )
			throw new IOException("No such file or directory");
		if ( ! f.delete() )
			throw new IOException("Failed to delete");
	}

	protected void rename(String oldname, String newname) throws IOException {
		File f = new File(oldname);
		if ( ! f.exists() )
			throw new IOException("No such file or directory");
		if ( ! f.renameTo(new File(newname)) )
			throw new IOException("Failed to delete");
	}

	protected String tmpname() {
		try {
			java.io.File f = java.io.File.createTempFile(TMP_PREFIX ,TMP_SUFFIX);
			return f.getName();
		} catch ( IOException ioe ) {
			return super.tmpname();
		}
	}
	
}
