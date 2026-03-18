/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.geography;

public class GeographyTest {

	static int xOf(byte[] bytecode) {
		return (bytecode[0] & 0xF0) >> 4;
	}

	static int yOf(byte[] bytecode) {
		return bytecode[0] & 0x0F;
	}

	static int pageBitOf(byte[] bytecode) {
		return (bytecode[1] & 0b10000000) >> 7;
	}

	static int typeBitsOf(byte[] bytecode) {
		return (bytecode[1] & 0b01110000) >> 4;
	}

	static int argumentBitsOf(byte[] bytecode) {
		return bytecode[1] & 0b00001111;
	}
}
