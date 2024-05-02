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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.jimbovm.isobel.actor.Actor;
import com.github.jimbovm.isobel.bytecode.geography.FTypeCommand;
import com.github.jimbovm.isobel.test.BinaryIntegerConverter;

public class StaircaseTest {

	@CsvSource({
		"0b0000_1111, 0b0011_0001, 0, 1, 0",
		"0b0000_1111, 0b0011_0001, 0, 1, 1",
		"0b0100_1111, 0b0011_0100, 4, 4, 2",
		"0b0100_1111, 0b0011_1000, 4, 8, 3",

	})
	@ParameterizedTest void parse(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte,
		final int expectedX,
		final int expectedExtent,
		final int page) {
	
		Actor actor = FTypeCommand.parse(lowByte, highByte, page);
		
		assertInstanceOf(Staircase.class, actor);
		
		Staircase staircase = (Staircase) actor;

		assertEquals(expectedX + page * 16, staircase.getX());
		assertEquals(expectedExtent, staircase.getExtent());
	}
}
