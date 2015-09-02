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

import org.luaj.vm2.LuaValue;

/** Variable is created lua name scopes, and is a named, lua variable that
 * either refers to a lua local, global, or upvalue storage location.  
 */
public class Variable {
	
	/** The name as it appears in lua source code */
	public final String name;
	
	/** The lua scope in which this variable is defined. */ 
	public final NameScope definingScope;
	
	/** true if this variable is an upvalue */
	public boolean isupvalue;
	
	/** true if there are assignments made to this variable */
	public boolean hasassignments;

	/** When hasassignments == false, and the initial value is a constant, this is the initial value */
	public LuaValue initialValue;
	
	/** Global is named variable not associated with a defining scope */
	public Variable(String name) {
		this.name = name;
		this.definingScope = null;
	}
	public Variable(String name, NameScope definingScope) {
	/** Local variable is defined in a particular scope.  */
		this.name = name;
		this.definingScope = definingScope;
	}
	public boolean isLocal() {
		return this.definingScope != null;
	}
	public boolean isConstant() {
		return ! hasassignments && initialValue != null;
	}
}