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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class NameScope {

	private static final Set<String> LUA_KEYWORDS = new HashSet<String>();
	
	static { 
		String[] k = new String[] { 
	        "and", "break", "do", "else", "elseif", "end", 
	        "false", "for", "function", "if", "in", "local",
	        "nil", "not", "or", "repeat", "return", 
	        "then", "true", "until", "while" };
		for ( int i=0; i<k.length; i++ )
			LUA_KEYWORDS.add( k[i] );
	}
	
	public final Map<String,Variable> namedVariables = new HashMap<String,Variable>();

	public final NameScope outerScope;

	public int functionNestingCount;
	
	/** Construct default names scope */
	public NameScope() {
		this.outerScope = null;
		this.functionNestingCount = 0;
	}
	
	/** Construct name scope within another scope*/
	public NameScope(NameScope outerScope) {
		this.outerScope = outerScope;
		this.functionNestingCount = outerScope!=null? outerScope.functionNestingCount: 0;
	}
	
	/** Look up a name.  If it is a global name, then throw IllegalArgumentException. */
	public Variable find( String name ) throws IllegalArgumentException {
		validateIsNotKeyword(name);
		for ( NameScope n = this; n!=null; n=n.outerScope )
			if ( n.namedVariables.containsKey(name) )
				return (Variable)n.namedVariables.get(name);
		Variable value = new Variable(name);
		this.namedVariables.put(name, value);
		return value;
	}
	
	/** Define a name in this scope.  If it is a global name, then throw IllegalArgumentException. */
	public Variable define( String name ) throws IllegalStateException, IllegalArgumentException {
		validateIsNotKeyword(name);
		Variable value = new Variable(name, this);
		this.namedVariables.put(name, value);
		return value;
	}
	
	private void validateIsNotKeyword(String name) {
		if ( LUA_KEYWORDS.contains(name) )
			throw new IllegalArgumentException("name is a keyword: '"+name+"'");
	}
}
