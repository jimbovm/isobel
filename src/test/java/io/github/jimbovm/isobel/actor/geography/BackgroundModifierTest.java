/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.geography;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.jimbovm.isobel.common.AreaHeader.Background;
import io.github.jimbovm.isobel.test.BinaryIntegerConverter;

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
		"MONOCHROME, 15, true, 0b1100_0111" })
	@ParameterizedTest
	void unparse(
		final String background, final int x, final boolean newPage,
		@ConvertWith(BinaryIntegerConverter.class) final int expectedHighByte) {

		BackgroundModifier modifier = new BackgroundModifier();
		modifier.setX(x);
		modifier.setBackground(Background.valueOf(background));

		byte[] bytecode = modifier.unparse(newPage);

		assertEquals(0xE, yOf(bytecode));
		assertEquals((byte) expectedHighByte, bytecode[1]);
	}
}
