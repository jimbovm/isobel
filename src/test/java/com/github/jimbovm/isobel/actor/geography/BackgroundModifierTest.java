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
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.jimbovm.isobel.common.AreaHeader.Background;
import com.github.jimbovm.isobel.test.BinaryIntegerConverter;

public class BackgroundModifierTest extends GeographyTest {
	
	@CsvSource({
		"NONE, 0, false, 0b0100_0000",
		"UNDERWATER, 5, false, 0b0100_0001",
		"CASTLE_WALL, 6, false, 0b0100_0010",
		"OVER_WATER, 0, false, 0b0100_0011",
		"NIGHT, 0, false, 0b0100_0100",
		"DAY_SNOW, 0, false, 0b0100_0101",
		"NIGHT_SNOW, 0, false, 0b0100_0110",
		"MONOCHROME, 15, false, 0b0100_0111",
		"NONE, 0, true, 0b1100_0000",
		"UNDERWATER, 5, true, 0b1100_0001",
		"CASTLE_WALL, 6, true, 0b1100_0010",
		"OVER_WATER, 0, true, 0b1100_0011",
		"NIGHT, 0, true, 0b1100_0100",
		"DAY_SNOW, 0, true, 0b1100_0101",
		"NIGHT_SNOW, 0, true, 0b1100_0110",
		"MONOCHROME, 15, true, 0b1100_0111"
	})
	@ParameterizedTest void unparse(
		final String background,
		final int x,
		final boolean newPage,
		@ConvertWith(BinaryIntegerConverter.class) final int expectedHighByte) {

		BackgroundModifier modifier = new BackgroundModifier();
		modifier.setX(x);
		modifier.setBackground(Background.valueOf(background));

		byte[] bytecode = modifier.unparse(newPage);

		assertEquals(0xE, yOf(bytecode));
		assertEquals((byte) expectedHighByte, bytecode[1]);
	}
}
