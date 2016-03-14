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

public class TableField extends SyntaxElement {

	public final Exp index;
	public final String name;
	public final Exp rhs;
	
	public TableField(Exp index, String name, Exp rhs) {
		this.index = index;
		this.name = name;
		this.rhs = rhs;
	}
	
	public static TableField keyedField(Exp index, Exp rhs) {
		return new TableField(index, null, rhs);
	}

	public static TableField namedField(String name, Exp rhs) {
		return new TableField(null, name, rhs);
	}

	public static TableField listField(Exp rhs) {
		return new TableField(null, null, rhs);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
