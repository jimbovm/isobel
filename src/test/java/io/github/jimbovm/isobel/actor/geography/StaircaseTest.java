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

public class StaircaseTest {

	@CsvSource({
		"0b0000_1111, 0b0011_0001, 0, 1, 0",
		"0b0000_1111, 0b0011_0001, 0, 1, 1",
		"0b0100_1111, 0b0011_0100, 4, 4, 2",
		"0b0100_1111, 0b0011_1000, 4, 8, 3",

	})
	@ParameterizedTest
	void parse(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte, final int expectedX,
		final int expectedExtent, final int page) {

		Actor actor = FTypeCommand.parse(lowByte, highByte, page);

		assertInstanceOf(Staircase.class, actor);

		Staircase staircase = (Staircase) actor;

		assertEquals(expectedX + page * 16, staircase.getX());
		assertEquals(expectedExtent, staircase.getExtent());
	}
}
