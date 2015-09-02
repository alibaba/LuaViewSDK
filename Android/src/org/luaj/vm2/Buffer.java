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
package org.luaj.vm2;


/**
 * String buffer for use in string library methods, optimized for production 
 * of StrValue instances.
 * <p>
 * The buffer can begin initially as a wrapped {@link LuaValue} 
 * and only when concatenation actually occurs are the bytes first copied.
 * <p> 
 * To convert back to a {@link LuaValue} again, 
 * the function {@link Buffer#value()} is used.
 * @see LuaValue
 * @see LuaValue#buffer()
 * @see LuaString
 */
public final class Buffer {
	
	/** Default capacity for a buffer: 64 */
	private static final int DEFAULT_CAPACITY = 64;
	
	/** Shared static array with no bytes */
	private static final byte[] NOBYTES = {};

	/** Bytes in this buffer */
	private byte[] bytes;
	
	/** Length of this buffer */
	private int length;
	
	/** Offset into the byte array */
	private int offset;
	
	/** Value of this buffer, when not represented in bytes */
	private LuaValue value;
	
	/**
	 * Create buffer with default capacity
	 * @see #DEFAULT_CAPACITY
	 */
	public Buffer() {
		this(DEFAULT_CAPACITY);
	}
	
	/**
	 * Create buffer with specified initial capacity
	 * @param initialCapacity the initial capacity
	 */
	public Buffer( int initialCapacity ) {
		bytes = new byte[ initialCapacity ];
		length = 0;
		offset = 0;
		value = null;
	}
	
	/**
	 * Create buffer with specified initial value
	 * @param value the initial value
	 */
	public Buffer(LuaValue value) {
		bytes = NOBYTES;
		length = offset = 0;
		this.value = value;
	}

	/** 
	 * Get buffer contents as a {@link LuaValue}
	 * @return value as a {@link LuaValue}, converting as necessary
	 */
	public LuaValue value() {
		return value != null? value: this.tostring();
	}

	/** 
	 * Set buffer contents as a {@link LuaValue}
	 * @param value value to set
	 */
	public Buffer setvalue(LuaValue value) {
		bytes = NOBYTES;
		offset = length = 0;
		this.value = value;
		return this;
	}
	
	/** 
	 * Convert the buffer to a {@link LuaString}
	 * @return the value as a {@link LuaString}
	 */
	public final LuaString tostring() {
		realloc( length, 0 );
		return LuaString.valueOf( bytes, offset, length );
	}
	
	/** 
	 * Convert the buffer to a Java String
	 * @return the value as a Java String
	 */
	public String tojstring() {
		return value().tojstring();
	}
	
	/** 
	 * Convert the buffer to a Java String
	 * @return the value as a Java String
	 */
	public String toString() {
		return tojstring();
	}

	/** 
	 * Append a single byte to the buffer.
	 * @return {@code this} to allow call chaining
	 */
	public final Buffer append( byte b ) {
		makeroom( 0, 1 );
		bytes[ offset + length++ ] = b;
		return this;
	}

	/** 
	 * Append a {@link LuaValue} to the buffer.
	 * @return {@code this} to allow call chaining
	 */
	public final Buffer append( LuaValue val ) {
		append( val.strvalue() );
		return this;
	}
	
	/** 
	 * Append a {@link LuaString} to the buffer.
	 * @return {@code this} to allow call chaining
	 */
	public final Buffer append( LuaString str ) {
		final int n = str.m_length;
		makeroom( 0, n );
		str.copyInto( 0, bytes, offset + length, n );
		length += n;
		return this;
	}
	
	/** 
	 * Append a Java String to the buffer.
	 * The Java string will be converted to bytes using the UTF8 encoding. 
	 * @return {@code this} to allow call chaining
	 * @see LuaString#encodeToUtf8(char[], int, byte[], int)
	 */
	public final Buffer append( String str ) {
		char[] c = str.toCharArray();
		final int n = LuaString.lengthAsUtf8( c );
		makeroom( 0, n );
		LuaString.encodeToUtf8( c, c.length, bytes, offset + length );
		length += n;
		return this;
	}

	/** Concatenate this buffer onto a {@link LuaValue}
	 * @param lhs the left-hand-side value onto which we are concatenating {@code this} 
	 * @return {@link Buffer} for use in call chaining.
	 */
	public Buffer concatTo(LuaValue lhs) {
		return setvalue(lhs.concat(value()));
	}

	/** Concatenate this buffer onto a {@link LuaString}
	 * @param lhs the left-hand-side value onto which we are concatenating {@code this} 
	 * @return {@link Buffer} for use in call chaining.
	 */
	public Buffer concatTo(LuaString lhs) {
		return value!=null&&!value.isstring()? setvalue(lhs.concat(value)): prepend(lhs);
	}

	/** Concatenate this buffer onto a {@link LuaNumber}
	 * <p>
	 * The {@link LuaNumber} will be converted to a string before concatenating. 
	 * @param lhs the left-hand-side value onto which we are concatenating {@code this} 
	 * @return {@link Buffer} for use in call chaining.
	 */
	public Buffer concatTo(LuaNumber lhs) {
		return value!=null&&!value.isstring()? setvalue(lhs.concat(value)): prepend(lhs.strvalue());
	}

	/** Concatenate bytes from a {@link LuaString} onto the front of this buffer
	 * @param s the left-hand-side value which we will concatenate onto the front of {@code this} 
	 * @return {@link Buffer} for use in call chaining.
	 */
	public Buffer prepend(LuaString s) {
		int n = s.m_length;
		makeroom( n, 0 );
		System.arraycopy( s.m_bytes, s.m_offset, bytes, offset-n, n );
		offset -= n;
		length += n;
		value = null;
		return this;
	}

	/** Ensure there is enough room before and after the bytes.
	 * @param nbefore number of unused bytes which must precede the data after this completes 
	 * @param nafter number of unused bytes which must follow the data after this completes 
	 */
	public final void makeroom( int nbefore, int nafter ) {
		if ( value != null ) {
			LuaString s = value.strvalue();
			value = null;
			length = s.m_length;
			offset = nbefore;
			bytes = new byte[nbefore+length+nafter];
			System.arraycopy(s.m_bytes, s.m_offset, bytes, offset, length);
		} else if ( offset+length+nafter > bytes.length || offset<nbefore ) {
			int n = nbefore+length+nafter;
			int m = n<32? 32: n<length*2? length*2: n;
			realloc( m, nbefore==0? 0: m-length-nafter );
		}
	}
	
	/** Reallocate the internal storage for the buffer
	 * @param newSize the size of the buffer to use 
	 * @param newOffset the offset to use 
	 */
	private final void realloc( int newSize, int newOffset ) {
		if ( newSize != bytes.length ) {
			byte[] newBytes = new byte[ newSize ];
			System.arraycopy( bytes, offset, newBytes, newOffset, length );
			bytes = newBytes;
			offset = newOffset;
		}
	}

}
