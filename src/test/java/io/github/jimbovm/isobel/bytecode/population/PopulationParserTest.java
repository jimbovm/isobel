/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.bytecode.population;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.jimbovm.isobel.test.BinaryIntegerConverter;

public class PopulationParserTest {

	@CsvSource({
		"0b0000_0000, 0b1111_1111" })
	@ParameterizedTest
	void twoByteFailOnPrematureEof(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte) {

		byte[] badBytes =
			{
				(byte) lowByte,
				(byte) highByte };

		assertThrows(IOException.class, () -> {
			PopulationParser parser = new PopulationParser(new ByteArrayInputStream(badBytes));
			parser.parse();
		});
	}

	@CsvSource({
		"0b0000_1110, 0b1111_1111, 0b0000_0000",
		"0b0000_1110, 0b0000_0000, 0b1111_1111" })
	@ParameterizedTest
	void threeByteFailOnPrematureEof(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int midByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte) {

		byte[] badBytes =
			{
				(byte) lowByte,
				(byte) midByte,
				(byte) highByte };

		assertThrows(IOException.class, () -> {
			PopulationParser parser = new PopulationParser(new ByteArrayInputStream(badBytes));
			parser.parse();
		});
	}
}
