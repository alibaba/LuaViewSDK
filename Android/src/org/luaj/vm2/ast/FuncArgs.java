/*******************************************************************************
* Copyright (c) 2010 Luaj.org. All rights reserved.
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
package org.luaj.vm2.ast;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaString;

public class FuncArgs extends SyntaxElement {

	public final List<Exp> exps;
	
	/** exp1,exp2... */
	public static FuncArgs explist(List<Exp> explist) {
		return new FuncArgs(explist);
	}

	/** {...} */
	public static FuncArgs tableconstructor(TableConstructor table) {
		return new FuncArgs(table);
	}

	/** "mylib" */
	public static FuncArgs string(LuaString string) {
		return new FuncArgs(string);
	}

	public FuncArgs(List<Exp> exps) {
		this.exps = exps;
	}

	public FuncArgs(LuaString string) {
		this.exps = new ArrayList<Exp>();
		this.exps.add( Exp.constant(string) );
	}

	public FuncArgs(TableConstructor table) {
		this.exps = new ArrayList<Exp>();
		this.exps.add( table );
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
