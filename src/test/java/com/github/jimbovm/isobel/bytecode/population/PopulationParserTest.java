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

package com.github.jimbovm.isobel.bytecode.population;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.jimbovm.isobel.test.BinaryIntegerConverter;

public class PopulationParserTest {

	@CsvSource({
		"0b0000_0000, 0b1111_1111"
	})
	@ParameterizedTest void twoByteFailOnPrematureEof(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte
		) {

		byte[] badBytes = {(byte) lowByte, (byte) highByte};

		assertThrows(IOException.class, () -> {
			PopulationParser parser = new PopulationParser(new ByteArrayInputStream(badBytes));
			parser.parse();
		});
	}

	@CsvSource({
		"0b0000_1110, 0b1111_1111, 0b0000_0000",
		"0b0000_1110, 0b0000_0000, 0b1111_1111"
	})
	@ParameterizedTest void threeByteFailOnPrematureEof(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int midByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte
		) {

		byte[] badBytes = {(byte) lowByte, (byte) midByte, (byte) highByte};

		assertThrows(IOException.class, () -> {
			PopulationParser parser = new PopulationParser(new ByteArrayInputStream(badBytes));
			parser.parse();
		});
	}
}
