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

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.jimbovm.isobel.actor.Actor;
import com.github.jimbovm.isobel.bytecode.geography.FTypeCommand;
import com.github.jimbovm.isobel.test.BinaryIntegerConverter;

public class FullHeightRopeTest {

	@CsvSource({
		"0b0000_1111, 0b0000_0000, 0",
		"0b0001_1111, 0b0000_1010, 1",
		"0b0010_1111, 0b0000_0101, 2",
		"0b1111_1111, 0b0000_1111, 15",

	})
	@ParameterizedTest void parse(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte,
		final int expectedX) {
	
		Actor actor = FTypeCommand.parse(lowByte, highByte, 0);
		
		assertInstanceOf(FullHeightRope.class, actor);
		
		FullHeightRope rope = (FullHeightRope) actor;

		assertEquals(expectedX, rope.getX());
	}

	@CsvSource({
		"0, false, 0b0000_1111, 0b0000_0000",
		"4, false, 0b0100_1111, 0b0000_0000",
		"6, false, 0b0110_1111, 0b0000_0000",
		"16, true, 0b0000_1111, 0b1000_0000",
		"17, false, 0b0000_1111, 0b1000_0000",
	})
	@ParameterizedTest void unparse(
		final int x,
		final boolean newPage,
		@ConvertWith(BinaryIntegerConverter.class) final int expectedLowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int expectedHighByte
	) {
		var rope = FullHeightRope.create(x);

		byte[] bytecode = rope.unparse(newPage);

		assertEquals(0xF, bytecode[0] & 0b0000_1111);

		final int relativeX = (bytecode[0] >>> 4);
		assertEquals(x % 16, relativeX);

		if (newPage) {
			assertEquals((byte) 0b1000_0000, bytecode[1]);
		} else {
			assertEquals(0, bytecode[1]);
		}

	}

	@Test void marshal() throws Exception {
		var writer = new StringWriter();
		var context = JAXBContext.newInstance(FullHeightRope.class);
		var marshaller = context.createMarshaller();
		marshaller.marshal(FullHeightRope.create(8), writer);

		final String xml = writer.toString();
		System.out.println(xml);
		assertTrue(xml.contains("<fullHeightRope "));
		assertTrue(xml.contains("x=\"8\""));
	}

	@Test void unmarshal() throws Exception {
		String xml = "<fullHeightRope x=\"64\" />";

		var context = JAXBContext.newInstance(FullHeightRope.class);
		var unmarshaller = context.createUnmarshaller();
		FullHeightRope rope = (FullHeightRope) unmarshaller.unmarshal(new StringReader(xml));

		assertTrue(rope.getClass() == FullHeightRope.class);
		assertEquals(64, rope.getX());
	}
}
