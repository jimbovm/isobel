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

package com.github.jimbovm.isobel.actor.geography;

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
