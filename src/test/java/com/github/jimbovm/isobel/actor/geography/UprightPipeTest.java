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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UprightPipeTest extends GeographyTest {

	@ParameterizedTest
	@CsvSource({
		"0, 0, 0, false",
		"0, 0, 1, false",
		"0, 10, 4, true",
		"0, 10, 4, false",
	})
	void unparse(final int x, final int y, final int extent, boolean enterable) {
		
		var pipe = UprightPipe.create(x, y, extent, enterable);

		byte[] bytecode = pipe.unparse(false);

		assertEquals(x % 16, xOf(bytecode));
		assertEquals(y, yOf(bytecode));
		assertEquals(0b111, typeBitsOf(bytecode));

		assertEquals(enterable ? 1 : 0, (bytecode[1] & 0b00001000) >> 3);
		assertEquals(extent, bytecode[1] & 0b00000111);
	}
	
}
