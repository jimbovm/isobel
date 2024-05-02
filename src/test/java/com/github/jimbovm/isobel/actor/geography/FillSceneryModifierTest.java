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

import com.github.jimbovm.isobel.common.AreaHeader.Fill;
import com.github.jimbovm.isobel.common.AreaHeader.Scenery;

public class FillSceneryModifierTest extends GeographyTest {

	@CsvSource({
		"1, FILL_NONE, NONE",
		"24, FILL_2BF_0BC, HILLS"
	})
	@ParameterizedTest void unparse(final int x, final String fill, final String scenery) {

		var fillObject = Fill.valueOf(fill);
		var sceneryObject = Scenery.valueOf(scenery);
		var modifier = FillSceneryModifier.create(x, fillObject, sceneryObject);

		byte[] bytecode = modifier.unparse(false);

		// Flag for fill/scenery modifier, not background
		assertEquals(0, (bytecode[1] & 0b01000000) >> 6);

		// No Y coordinate, always 0xE
		assertEquals(x % 16, xOf(bytecode));
		assertEquals(0xE, yOf(bytecode));

		final int sceneryBits = (bytecode[1] & 0b00110000) >> 4;
		final int fillBits = bytecode[1] & 0b00001111;

		assertEquals(fillObject.getOpcode(), fillBits);
		assertEquals(sceneryObject.getOpcode(), sceneryBits);
	}
}
