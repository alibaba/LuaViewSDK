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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;

import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.ResourceFinder;

/**
 * Global environment used by luaj.  Contains global variables referenced by executing lua.
 * <p>
 * 
 * <h3>Constructing and Initializing Instances</h3>
 * Typically, this is constructed indirectly by a call to 
 * {@link JsePlatform.standardGlobasl()} or {@link JmePlatform.standardGlobals()}, 
 * and then used to load lua scripts for execution as in the following example. 
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * globals.load( new StringReader("print 'hello'"), "main.lua" ).call(); 
 * } </pre>
 * The creates a complete global environment with the standard libraries loaded.
 * <p>
 * For specialized circumstances, the Globals may be constructed directly and loaded
 * with only those libraries that are needed, for example.
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load( new BaseLib() ); 
 * } </pre>
 * 
 * <h3>Loading and Executing Lua Code</h3>
 * Globals contains convenience functions to load and execute lua source code given a Reader. 
 * A simple example is:
 * <pre> {@code
 * globals.load( new StringReader("print 'hello'"), "main.lua" ).call(); 
 * } </pre>
 * 
 * <h3>Fine-Grained Control of Compiling and Loading Lua</h3>
 * Executable LuaFunctions are created from lua code in several steps
 * <ul>
 * <li>find the resource using the platform's {@link ResourceFinder}
 * <li>compile lua to lua bytecode using {@link Compiler}
 * <li>load lua bytecode to a {@link LuaPrototpye} using {@link Undumper}
 * <li>construct {@link LuaClosure} from {@link Prototype} with {@link Globals} using {@link Loader}
 * </ul>
 * <p>
 * There are alternate flows when the direct lua-to-Java bytecode compiling {@link LuaJC} is used.
 * <ul>
 * <li>compile lua to lua bytecode using {@link Compiler} or load precompiled code using {@link Undumper}
 * <li>convert lua bytecode to equivalent Java bytecode using {@link LuaJC} that implements {@link Loader} directly
 * </ul>
 * 
 * <h3>Java Field</h3>
 * Certain public fields are provided that contain the current values of important global state:
 * <ul>
 * <li>{@link STDIN} Current value for standard input in the laaded IoLib, if any.
 * <li>{@link STDOUT} Current value for standard output in the loaded IoLib, if any.
 * <li>{@link STDERR} Current value for standard error in the loaded IoLib, if any.
 * <li>{@link finder} Current loaded {@link ResourceFinder}, if any.
 * <li>{@link compiler} Current loaded {@link Compiler}, if any.
 * <li>{@link undumper} Current loaded {@link Undumper}, if any.
 * <li>{@link loader} Current loaded {@link Loader}, if any.
 * </ul>
 * 
 * <h3>Lua Environment Variables</h3>
 * When using {@link JsePlatform} or {@link JmePlatform}, 
 * these environment variables are created within the Globals.
 * <ul>
 * <li>"_G" Pointer to this Globals.
 * <li>"_VERSION" String containing the version of luaj.
 * </ul>
 * 
 * <h3>Use in Multithreaded Environments</h3>
 * In a multi-threaded server environment, each server thread should create one Globals instance, 
 * which will be logically distinct and not interfere with each other, but share certain 
 * static immutable resources such as class data and string data.
 * <p>
 * 
 * @see org.luaj.vm2.lib.jse.JsePlatform
 * @see org.luaj.vm2.lib.jme.JmePlatform
 * @see LuaValue
 * @see Compiler
 * @see Loader
 * @see Undumper
 * @see ResourceFinder
 * @see LuaC
 * @see LuaJC
 */
public class Globals extends LuaTable {

	/** The current default input stream. */
	public InputStream STDIN  = null;

	/** The current default output stream. */
	public PrintStream STDOUT = System.out;

	/** The current default error stream. */
	public PrintStream STDERR = System.err;

	/** The installed ResourceFinder for looking files by name. */
	public ResourceFinder finder;
	
	/** The currently running thread.  Should not be changed by non-library code. */
	public LuaThread running = new LuaThread(this);

	/** The BaseLib instance loaded into this Globals */
	public BaseLib baselib;
	
	/** The PackageLib instance loaded into this Globals */
	public PackageLib package_;
	
	/** The DebugLib instance loaded into this Globals, or null if debugging is not enabled */
	public DebugLib debuglib;

	/** Interface for module that converts a Prototype into a LuaFunction with an environment. */
	public interface Loader {
		/** Convert the prototype into a LuaFunction with the supplied environment. */
		LuaFunction load(Prototype prototype, String chunkname, LuaValue env) throws IOException;
	}

	/** Interface for module that converts lua source text into a prototype. */
	public interface Compiler {
		/** Compile lua source into a Prototype. The InputStream is assumed to be in UTF-8. */
		Prototype compile(InputStream stream, String chunkname) throws IOException;
	}

	/** Interface for module that loads lua binary chunk into a prototype. */
	public interface Undumper {
		/** Load the supplied input stream into a prototype. */
		Prototype undump(InputStream stream, String chunkname) throws IOException;
	}
	
	/** Check that this object is a Globals object, and return it, otherwise throw an error. */
	public Globals checkglobals() {
		return this;
	}
	
	/** The installed loader. 
	 * @see Loader */
	public Loader loader;

	/** The installed compiler.
	 * @see Compiler */
	public Compiler compiler;

	/** The installed undumper.
	 * @see Undumper */
	public Undumper undumper;

	/** Convenience function for loading a file that is either binary lua or lua source.
	 * @param filename Name of the file to load.
	 * @return LuaValue that can be call()'ed or invoke()'ed.
	 * @throws LuaError if the file could not be loaded.
	 */
	public LuaValue loadfile(String filename) {
		try {
			return load(finder.findResource(filename), "@"+filename, "bt", this);
		} catch (Exception e) {
			return error("load "+filename+": "+e);
		}
	}

	/** Convenience function to load a string value as a script.  Must be lua source.
	 * @param script Contents of a lua script, such as "print 'hello, world.'"
	 * @param chunkname Name that will be used within the chunk as the source.
	 * @return LuaValue that may be executed via .call(), .invoke(), or .method() calls.
	 * @throws LuaError if the script could not be compiled.
	 */
	public LuaValue load(String script, String chunkname) {
		return load(new StrReader(script), chunkname);
	}
	
	/** Convenience function to load a string value as a script.  Must be lua source.
	 * @param script Contents of a lua script, such as "print 'hello, world.'"
	 * @return LuaValue that may be executed via .call(), .invoke(), or .method() calls.
	 * @throws LuaError if the script could not be compiled.
	 */
	public LuaValue load(String script) {
		return load(new StrReader(script), script);
	}
	
	/** Load the content form a reader as a text file.  Must be lua source. 
	 * The source is converted to UTF-8, so any characters appearing in quoted literals 
	 * above the range 128 will be converted into multiple bytes.  */
	public LuaValue load(Reader reader, String chunkname) {
		return load(new UTF8Stream(reader), chunkname, "t", this);
	}

	/** Load the content form an input stream as a binary chunk or text file. */
	public LuaValue load(InputStream is, String chunkname, String mode, LuaValue env) {
		try {
			Prototype p = loadPrototype(is, chunkname, mode);
			return loader.load(p, chunkname, env);
		} catch (LuaError l) {
			throw l;
		} catch (Exception e) {
			return error("load "+chunkname+": "+e);
		}
	}

	/** Load lua source or lua binary from an input stream into a Prototype. 
	 * The InputStream is either a binary lua chunk starting with the lua binary chunk signature, 
	 * or a text input file.  If it is a text input file, it is interpreted as a UTF-8 byte sequence.  
	 */
	public Prototype loadPrototype(InputStream is, String chunkname, String mode) throws IOException {
		if (mode.indexOf('b') >= 0) {
			if (undumper == null)
				error("No undumper.");
			if (!is.markSupported())
				is = new BufferedStream(is);
			is.mark(4);
			final Prototype p = undumper.undump(is, chunkname);
			if (p != null)
				return p;
			is.reset();
		}
		if (mode.indexOf('t') >= 0) {
			return compilePrototype(is, chunkname);
		}
		error("Failed to load prototype "+chunkname+" using mode '"+mode+"'");
		return null;
	}
	
	/** Compile lua source from a Reader into a Prototype. The characters in the reader 
	 * are converted to bytes using the UTF-8 encoding, so a string literal containing 
	 * characters with codepoints 128 or above will be converted into multiple bytes. 
	 */
	public Prototype compilePrototype(Reader reader, String chunkname) throws IOException {
		return compilePrototype(new UTF8Stream(reader), chunkname);
	}
	
	/** Compile lua source from an InputStream into a Prototype. 
	 * The input is assumed to be UTf-8, but since bytes in the range 128-255 are passed along as 
	 * literal bytes, any ASCII-compatible encoding such as ISO 8859-1 may also be used.  
	 */
	public Prototype compilePrototype(InputStream stream, String chunkname) throws IOException {
		if (compiler == null)
			error("No compiler.");
		return compiler.compile(stream, chunkname);
	}

	/** Function which yields the current thread. 
	 * @param args  Arguments to supply as return values in the resume function of the resuming thread.
	 * @return Values supplied as arguments to the resume() call that reactivates this thread.
	 */
	public Varargs yield(Varargs args) {
		if (running == null || running.isMainThread())
			throw new LuaError("cannot yield main thread");
		final LuaThread.State s = running.state;
		return s.lua_yield(args);
	}

	/** Reader implementation to read chars from a String in JME or JSE. */
	static class StrReader extends Reader {
		final String s;
		int i = 0;
		final int n;
		StrReader(String s) {
			this.s = s;
			n = s.length();
		}
		public void close() throws IOException {
			i = n;
		}
		public int read() throws IOException {
			return i < n ? s.charAt(i++) : -1;
		}
		public int read(char[] cbuf, int off, int len) throws IOException {
			int j = 0;
			for (; j < len && i < n; ++j, ++i)
				cbuf[off+j] = s.charAt(i);
			return j > 0 || len == 0 ? j : -1;
		}
	}

	/* Abstract base class to provide basic buffered input storage and delivery.
	 * This class may be moved to its own package in the future.
	 */
	abstract static class AbstractBufferedStream extends InputStream {
		protected byte[] b;
		protected int i = 0, j = 0;
		protected AbstractBufferedStream(int buflen) {
			this.b = new byte[buflen];
		}
		abstract protected int avail() throws IOException;
		public int read() throws IOException {
			int a = avail();
			return (a <= 0 ? -1 : 0xff & b[i++]);
		}
		public int read(byte[] b) throws IOException {
			return read(b, 0, b.length);
		}
		public int read(byte[] b, int i0, int n) throws IOException {
			int a = avail();
			if (a <= 0) return -1;
			final int n_read = Math.min(a, n);
			System.arraycopy(this.b,  i,  b,  i0,  n_read);
			i += n_read;
			return n_read;
		}
		public long skip(long n) throws IOException {
			final long k = Math.min(n, j - i);
			i += k;
			return k;
		}		
		public int available() throws IOException {
			return j - i;
		}
	}

	/**  Simple converter from Reader to InputStream using UTF8 encoding that will work
	 * on both JME and JSE.
	 * This class may be moved to its own package in the future.
	 */
	static class UTF8Stream extends AbstractBufferedStream {
		private final char[] c = new char[32];
		private final Reader r;
		UTF8Stream(Reader r) {
			super(96);
			this.r = r;
		}
		protected int avail() throws IOException {
			if (i < j) return j - i;
			int n = r.read(c);
			if (n < 0)
				return -1;
			if (n == 0) {
				int u = r.read();
				if (u < 0)
					return -1;
				c[0] = (char) u;
				n = 1;
			}
			j = LuaString.encodeToUtf8(c, n, b, i = 0);
			return j;
		}
		public void close() throws IOException {
			r.close();
		}
	}
	
	/** Simple buffered InputStream that supports mark.
	 * Used to examine an InputStream for a 4-byte binary lua signature, 
	 * and fall back to text input when the signature is not found,
	 * as well as speed up normal compilation and reading of lua scripts.
	 * This class may be moved to its own package in the future.
	 */
	static class BufferedStream extends AbstractBufferedStream {
		private final InputStream s;
		public BufferedStream(InputStream s) {
			this(128, s);
		}
		BufferedStream(int buflen, InputStream s) {
			super(buflen);
			this.s = s;
		}
		protected int avail() throws IOException {
			if (i < j) return j - i;
			if (j >= b.length) i = j = 0;
			// leave previous bytes in place to implement mark()/reset().
			int n = s.read(b, j, b.length - j);
			if (n < 0)
				return -1;
			if (n == 0) {
				int u = s.read();
				if (u < 0)
					return -1;
				b[j] = (byte) u;
				n = 1;
			}
			j += n;
			return n;
		}
		public void close() throws IOException {
			s.close();
		}
		public synchronized void mark(int n) {
			if (i > 0 || n > b.length) {
				byte[] dest = n > b.length ? new byte[n] : b;
				System.arraycopy(b, i, dest, 0, j - i);
				j -= i;
				i = 0;
				b = dest;
			}
		}
		public boolean markSupported() {
			return true;
		}
		public synchronized void reset() throws IOException {
			i = 0;
		}
	}
}
