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
package org.luaj.vm2.lib.jse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Analog of Process that pipes input and output to client-specified streams.
 */
public class JseProcess {

	final Process process;
	final Thread input,output,error;

	/** Construct a process around a command, with specified streams to redirect input and output to.
	 * 
	 * @param cmd The command to execute, including arguments, if any
	 * @param stdin Optional InputStream to read from as process input, or null if input is not needed.
	 * @param stdout Optional OutputStream to copy process output to, or null if output is ignored.
	 * @param stderr Optinoal OutputStream to copy process stderr output to, or null if output is ignored.
	 * @throws IOException If the system process could not be created.
	 * @see Process
	 */
	public JseProcess(String[] cmd, InputStream stdin, OutputStream stdout, OutputStream stderr) throws IOException {
		this(Runtime.getRuntime().exec(cmd), stdin, stdout, stderr);	
	}

	/** Construct a process around a command, with specified streams to redirect input and output to.
	 * 
	 * @param cmd The command to execute, including arguments, if any
	 * @param stdin Optional InputStream to read from as process input, or null if input is not needed.
	 * @param stdout Optional OutputStream to copy process output to, or null if output is ignored.
	 * @param stderr Optinoal OutputStream to copy process stderr output to, or null if output is ignored.
	 * @throws IOException If the system process could not be created.
	 * @see Process
	 */
	public JseProcess(String cmd, InputStream stdin, OutputStream stdout, OutputStream stderr) throws IOException {
		this(Runtime.getRuntime().exec(cmd), stdin, stdout, stderr);	
	}

	private JseProcess(Process process, InputStream stdin, OutputStream stdout, OutputStream stderr) {
		this.process = process;
		input = stdin == null? null: copyBytes(stdin, process.getOutputStream(), null, process.getOutputStream());
		output = stdout == null? null: copyBytes(process.getInputStream(), stdout, process.getInputStream(), null);
		error = stderr == null? null: copyBytes(process.getErrorStream(), stderr, process.getErrorStream(), null);
	}

	/** Get the exit value of the process. */
	public int exitValue() {
		return process.exitValue();
	}

	/** Wait for the process to complete, and all pending output to finish.
	 * @return The exit status.
	 * @throws InterruptedException
	 */
	public int waitFor() throws InterruptedException {
		int r = process.waitFor();
		if (input != null)
			input.join();
		if (output != null)
			output.join();
		if (error != null)
			error.join();
		process.destroy();
		return r;
	}

	/** Create a thread to copy bytes from input to output. */
	private Thread copyBytes(final InputStream input,
			final OutputStream output, final InputStream ownedInput,
			final OutputStream ownedOutput) {
		Thread t = (new Thread() {
			public void run() {
				try {
					byte[] buf = new byte[1024];
					int r;
					try {
						while ((r = input.read(buf)) >= 0) {
							output.write(buf, 0, r);
						}
					} finally {
						if (ownedInput != null)
							ownedInput.close();
						if (ownedOutput != null)
							ownedOutput.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		return t;
	}
}
