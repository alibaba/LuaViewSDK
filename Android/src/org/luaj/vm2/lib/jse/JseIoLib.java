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

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.IoLib;
import org.luaj.vm2.lib.LibFunction;

/** 
 * Subclass of {@link IoLib} and therefore {@link LibFunction} which implements the lua standard {@code io} 
 * library for the JSE platform. 
 * <p> 
 * It uses RandomAccessFile to implement seek on files.  
 * <p>
 * Typically, this library is included as part of a call to 
 * {@link JsePlatform#standardGlobals()}
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * globals.get("io").get("write").call(LuaValue.valueOf("hello, world\n"));
 * } </pre>
 * <p>
 * For special cases where the smallest possible footprint is desired, 
 * a minimal set of libraries could be loaded
 * directly via {@link Globals#load(LuaValue)} using code such as:
 * <pre> {@code
 * Globals globals = new Globals();
 * globals.load(new JseBaseLib());
 * globals.load(new PackageLib());
 * globals.load(new JseIoLib());
 * globals.get("io").get("write").call(LuaValue.valueOf("hello, world\n"));
 * } </pre>
 * <p>However, other libraries such as <em>MathLib</em> are not loaded in this case.
 * <p>
 * This has been implemented to match as closely as possible the behavior in the corresponding library in C.
 * @see LibFunction
 * @see JsePlatform
 * @see org.luaj.vm2.lib.jme.JmePlatform
 * @see IoLib
 * @see JmeIoLib
 * @see <a href="http://www.lua.org/manual/5.2/manual.html#6.8">Lua 5.2 I/O Lib Reference</a>
 */
public class JseIoLib extends IoLib {

	protected File wrapStdin() throws IOException {
		return new StdinFile();
	}
	
	protected File wrapStdout() throws IOException {
		return new StdoutFile(FTYPE_STDOUT);
	}
	
	protected File wrapStderr() throws IOException {
		return new StdoutFile(FTYPE_STDERR);
	}
	
	protected File openFile( String filename, boolean readMode, boolean appendMode, boolean updateMode, boolean binaryMode ) throws IOException {
		RandomAccessFile f = new RandomAccessFile(filename,readMode? "r": "rw");
		if ( appendMode ) {
			f.seek(f.length());
		} else {
			if ( ! readMode )
				f.setLength(0);
		}
		return new FileImpl( f );
	}
	
	protected File openProgram(String prog, String mode) throws IOException {
		final Process p = Runtime.getRuntime().exec(prog);
		return "w".equals(mode)? 
				new FileImpl( p.getOutputStream() ):  
				new FileImpl( p.getInputStream() ); 
	}

	protected File tmpFile() throws IOException {
		java.io.File f = java.io.File.createTempFile(".luaj","bin");
		f.deleteOnExit();
		return new FileImpl( new RandomAccessFile(f,"rw") );
	}
	
	private static void notimplemented() {
		throw new LuaError("not implemented");
	}
	

	private final class FileImpl extends File {
		private final RandomAccessFile file;
		private final InputStream is;
		private final OutputStream os;
		private boolean closed = false;
		private boolean nobuffer = false;
		private FileImpl( RandomAccessFile file, InputStream is, OutputStream os ) {
			this.file = file;
			this.is = is!=null? is.markSupported()? is: new BufferedInputStream(is): null;
			this.os = os;
		}
		private FileImpl( RandomAccessFile f ) {
			this( f, null, null );
		}
		private FileImpl( InputStream i ) {
			this( null, i, null );
		}
		private FileImpl( OutputStream o ) {
			this( null, null, o );
		}
		public String tojstring() {
			return "file ("+this.hashCode()+")";
		}
		public boolean isstdfile() {
			return file == null;
		}
		public void close() throws IOException  {
			closed = true;
			if ( file != null ) {
				file.close();
			}
		}
		public void flush() throws IOException {
			if ( os != null )
				os.flush();
		}
		public void write(LuaString s) throws IOException {
			if ( os != null )
				os.write( s.m_bytes, s.m_offset, s.m_length );
			else if ( file != null )
				file.write( s.m_bytes, s.m_offset, s.m_length );
			else
				notimplemented();
			if ( nobuffer )
				flush();
		}
		public boolean isclosed() {
			return closed;
		}
		public int seek(String option, int pos) throws IOException {
			if ( file != null ) {
				if ( "set".equals(option) ) {
					file.seek(pos);
				} else if ( "end".equals(option) ) {
					file.seek(file.length()+pos);
				} else {
					file.seek(file.getFilePointer()+pos);
				}
				return (int) file.getFilePointer();
			}
			notimplemented();
			return 0;
		}
		public void setvbuf(String mode, int size) {
			nobuffer = "no".equals(mode);
		}

		// get length remaining to read
		public int remaining() throws IOException {
			return file!=null? (int) (file.length()-file.getFilePointer()): -1;
		}
		
		// peek ahead one character
		public int peek() throws IOException {
			if ( is != null ) {
				is.mark(1);
				int c = is.read();
				is.reset();
				return c;
			} else if ( file != null ) {
				long fp = file.getFilePointer();
				int c = file.read();
				file.seek(fp);
				return c;
			}
			notimplemented();
			return 0;
		}		
		
		// return char if read, -1 if eof, throw IOException on other exception 
		public int read() throws IOException {
			if ( is != null ) 
				return is.read();
			else if ( file != null ) {
				return file.read();
			}
			notimplemented();
			return 0;
		}

		// return number of bytes read if positive, -1 if eof, throws IOException
		public int read(byte[] bytes, int offset, int length) throws IOException {
			if (file!=null) {
				return file.read(bytes, offset, length);
			} else if (is!=null) {
				return is.read(bytes, offset, length);
			} else {
				notimplemented();
			}
			return length;
		}
	}

	private final class StdoutFile extends File {
		private final int file_type;

		private StdoutFile(int file_type) {
			this.file_type = file_type;
		}

		public String tojstring() {
			return "file ("+this.hashCode()+")";
		}

		private final PrintStream getPrintStream() {
			return file_type == FTYPE_STDERR?
					globals.STDERR:
					globals.STDOUT;
		}

		public void write(LuaString string) throws IOException {
			getPrintStream().write(string.m_bytes, string.m_offset, string.m_length);
		}

		public void flush() throws IOException {
			getPrintStream().flush();
		}

		public boolean isstdfile() {
			return true;
		}

		public void close() throws IOException {
			// do not close std files.
		}

		public boolean isclosed() {
			return false;
		}

		public int seek(String option, int bytecount) throws IOException {
			return 0;
		}

		public void setvbuf(String mode, int size) {
		}

		public int remaining() throws IOException {
			return 0;
		}

		public int peek() throws IOException, EOFException {
			return 0;
		}

		public int read() throws IOException, EOFException {
			return 0;
		}

		public int read(byte[] bytes, int offset, int length)
				throws IOException {
			return 0;
		}
	}

	private final class StdinFile extends File {
		private StdinFile() {
		}

		public String tojstring() {
			return "file ("+this.hashCode()+")";
		}

		public void write(LuaString string) throws IOException {
		}

		public void flush() throws IOException {
		}

		public boolean isstdfile() {
			return true;
		}

		public void close() throws IOException {
			// do not close std files.
		}

		public boolean isclosed() {
			return false;
		}

		public int seek(String option, int bytecount) throws IOException {
			return 0;
		}

		public void setvbuf(String mode, int size) {
		}

		public int remaining() throws IOException {
			return 0;
		}

		public int peek() throws IOException, EOFException {
			globals.STDIN.mark(1);
			int c = globals.STDIN.read();
			globals.STDIN.reset();
			return c;
		}

		public int read() throws IOException, EOFException {
			return globals.STDIN.read();
		}

		public int read(byte[] bytes, int offset, int length)
				throws IOException {
			return globals.STDIN.read(bytes, offset, length);
		}
	}
}
