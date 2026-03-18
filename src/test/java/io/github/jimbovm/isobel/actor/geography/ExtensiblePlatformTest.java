/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.geography;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ExtensiblePlatformTest extends GeographyTest {

	@CsvSource({
		"0, 6, 3" })
	@ParameterizedTest
	void unparse(final int x, final int y, final int extent) {

		var platform = ExtensiblePlatform.create(x, y, extent);

		final byte[] bytecode = platform.unparse(false);

		assertEquals(x, xOf(bytecode));
		assertEquals(y, yOf(bytecode));
		assertEquals(001, typeBitsOf(bytecode));
	}
}
