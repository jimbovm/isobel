/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.geography;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SingletonObjectTest extends GeographyTest {

	@ParameterizedTest
	@CsvSource({
		"0, 0, QUESTION_BLOCK_POWERUP",
		"0, 0, QUESTION_BLOCK_COIN",
		"8, 8, BRICK_VINE",
		"15, 4, SIDEWAYS_PIPE" })
	void unparse(final int x, final int y, final String type) {

		var objectType = SingletonObject.Type.valueOf(type);

		var object = SingletonObject.create(x, y, objectType);

		byte[] bytecode = object.unparse(false);

		assertEquals(x % 16, xOf(bytecode));
		assertEquals(y, yOf(bytecode));
		assertEquals(objectType.getId(), argumentBitsOf(bytecode));
		assertEquals(0, typeBitsOf(bytecode));
	}

}
