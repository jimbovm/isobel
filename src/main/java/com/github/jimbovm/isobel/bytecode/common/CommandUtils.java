/* SPDX-License-Identifier: MIT-0

Copyright 2022-2024 Jimbo Brierley.

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal in 
the Software without restriction, including without limitation the rights to 
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
of the Software, and to permit persons to whom the Software is furnished to do 
so.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
SOFTWARE. */

package com.github.jimbovm.isobel.bytecode.common;

/**
 * Miscellaneous utilities for working with commands.
 */
public final class CommandUtils {

	private CommandUtils() {
		// non-instantiable class
	}

	/**
	 * Given the low byte of a command, return it with the passed
	 * absolute X coordinate value encoded as relative (mod 16).
	 * 
	 * @param theByte The byte to which to encode X.
	 * @param x The absolute X coordinate.
	 * 
	 * @return <code>theByte | ((x % 16) &lt; 4)</code>
	 */
	public static byte encodeCoordinates(final byte theByte, final int x) {
		final int relativeX = x % 16;
		return (byte) (theByte | (relativeX << 4));
	}

	/**
	 * Given the low byte of a command, return it with the passed
	 * absolute coordinate values encoded as relative (mod 16).
	 * 
	 * @param theByte The byte to which to encode X and Y.
	 * @param x The absolute X coordinate.
	 * @param y The absolute Y coordinate.
	 * 
	 * @return <code>(theByte | ((x % 16) &lt;&lt; 4)) | (y % 16)</code>
	 */
	public static byte encodeCoordinates(final byte theByte, final int x, final int y) {
		final int relativeY = y % 16;
		return (byte) (encodeCoordinates(theByte, x) | relativeY);
	}

	/**
	 * Given the high or mid byte of a command, return the first argument
	 * with the new page flag set if the second argument is true, or
	 * unchanged otherwise.
	 *
	 * @param theByte The byte to which to encode the new page flag.
	 * @param newPage Value of the new page flag.
	 * 
	 * @return <code>theByte</code> with the most significant bit set if
	 * <code>newPage</code> is true, <code>theByte</code> otherwise.
	 */
	public static byte encodeNewPage(final byte theByte, final boolean newPage) {
		return (byte) (theByte | (newPage ? 0b10000000 : 0));
	}

	/**
	 * Given the high byte of a character command, return the first argument
	 * with the hard mode flag set if the second argument is true, or
	 * unchanged otherwise.
	 *
	 * @param theByte The byte to which to encode the hard mode flag.
	 * @param hardModeOnly Value of the hard mode flag.
	 *
	 * @return <code>theByte</code> with the second most significant bit set
	 * if <code>hardModeOnly</code> is true, <code>theByte</code> otherwise.
	 */
	public static byte encodeHardMode(final byte theByte, final boolean hardModeOnly) {
		return (byte) (theByte | (hardModeOnly ? 0b01000000 : 0));
	}
}
