/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.geography;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.jimbovm.isobel.actor.Actor;
import io.github.jimbovm.isobel.bytecode.geography.FTypeCommand;
import io.github.jimbovm.isobel.test.BinaryIntegerConverter;

public class CastleTest {

	@CsvSource({
		"0b0000_1111, 0b0010_0000, 0, LARGE",
		"0b0001_1111, 0b0010_0110, 1, SMALL",
		"0b0010_1111, 0b0010_0000, 2, LARGE",
		"0b1111_1111, 0b0010_0110, 15, SMALL",
		"0b0000_1111, 0b0010_0111, 0, SMALL",

	})
	@ParameterizedTest
	void parse(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte, final int expectedX,
		final String expectedSize) {

		Actor actor = FTypeCommand.parse(lowByte, highByte, 0);

		assertInstanceOf(Castle.class, actor);

		var castle = (Castle) actor;

		assertEquals(expectedX, castle.getX());
		assertEquals(castle.getSize(), Castle.Size.valueOf(expectedSize));

	}

	@CsvSource({
		"0, LARGE, false, 0b0000_1111, 0b0010_0000",
		"0, SMALL, false, 0b0000_1111, 0b0010_0110",
		"16, LARGE, false, 0b0000_1111, 0b0010_0000",
		"16, SMALL, false, 0b0000_1111, 0b0010_0110",
		"0, SMALL, true, 0b0000_1111, 0b1010_0110"

	})
	@ParameterizedTest
	void unparse(
		final int x, final String size, final boolean newPage,
		@ConvertWith(BinaryIntegerConverter.class) final int expectedLowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int expectedHighByte) {
		var castle = Castle.create(x, Castle.Size.valueOf(size));
		byte[] bytecode = castle.unparse(newPage);

		assertEquals((byte) expectedLowByte, bytecode[0]);
		assertEquals((byte) expectedHighByte, bytecode[1]);
	}
}
