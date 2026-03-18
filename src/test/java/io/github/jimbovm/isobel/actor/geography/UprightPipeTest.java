/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.geography;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UprightPipeTest extends GeographyTest {

	@ParameterizedTest
	@CsvSource({
		"0, 0, 0, false",
		"0, 0, 1, false",
		"0, 10, 4, true",
		"0, 10, 4, false", })
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
