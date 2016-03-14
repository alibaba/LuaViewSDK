/*******************************************************************************
* Copyright (c) 2011 Luaj.org. All rights reserved.
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

import java.lang.reflect.Field;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

/**
 * LuaValue that represents a Java instance.
 * <p>
 * Will respond to get() and set() by returning field values or methods. 
 * <p>
 * This class is not used directly.  
 * It is returned by calls to {@link CoerceJavaToLua#coerce(Object)} 
 * when a subclass of Object is supplied.
 * @see CoerceJavaToLua
 * @see CoerceLuaToJava
 */
class JavaInstance extends LuaUserdata {

	JavaClass jclass;
	
	JavaInstance(Object instance) {
		super(instance);
	}

	public LuaValue get(LuaValue key) {
		if ( jclass == null )
			jclass = JavaClass.forClass(m_instance.getClass());
		Field f = jclass.getField(key);
		if ( f != null )
			try {
				return CoerceJavaToLua.coerce(f.get(m_instance));
			} catch (Exception e) {
				throw new LuaError(e);
			}
		LuaValue m = jclass.getMethod(key);
		if ( m != null )
			return m;
		return super.get(key);
	}

	public void set(LuaValue key, LuaValue value) {
		if ( jclass == null )
			jclass = JavaClass.forClass(m_instance.getClass());
		Field f = jclass.getField(key);
		if ( f != null )
			try {
				f.set(m_instance, CoerceLuaToJava.coerce(value, f.getType()));
				return;
			} catch (Exception e) {
				throw new LuaError(e);
			}
		super.set(key, value);
	} 	
	
}
