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

import java.io.StringWriter;

import jakarta.xml.bind.JAXBException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.jimbovm.isobel.actor.Actor;
import com.github.jimbovm.isobel.actor.ActorTest;
import com.github.jimbovm.isobel.bytecode.geography.NormalCommand;
import com.github.jimbovm.isobel.test.BinaryIntegerConverter;

public class RowTest extends ActorTest<Row> {

	public RowTest() throws JAXBException {
		super(Row.class);
	}

	@CsvSource({
		"0b0000_0000, 0b0010_0001, 0, 0, 1, BRICK",
		"0b0000_0000, 0b0011_0010, 0, 0, 2, BLOCK",
		"0b0000_0000, 0b0100_0100, 0, 0, 4, COIN",
	})
	@ParameterizedTest void parse(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte,
		final int expectedX,
		final int expectedY,
		final int expectedExtent,
		final String expectedType) {
	
		Actor row = NormalCommand.parse(lowByte, highByte, 0);
		
		assertInstanceOf(Row.class, row);
		
		Row theRow = (Row) row;

		Row.Type theExpectedType = Enum.valueOf(Row.Type.class, expectedType);
		assertEquals(theRow.getX(), expectedX);
		assertEquals(theRow.getY(), expectedY);
		assertEquals(theRow.getExtent(), expectedExtent);
		assertEquals(theRow.getType(), theExpectedType);
	}
	
	@CsvSource({
		"true, 0, 0, 0, BLOCK",
		"true, 0, 0, 0, BRICK",
		"false, 4, 5, 3, COIN",
		"false, 6, 7, 3, COIN"
	})
	@ParameterizedTest void translate(boolean newPage, final int x, final int y, final int extent, String type) throws Exception {

		Row.Type theType = Enum.valueOf(Row.Type.class, type);

		Row row = new Row();
		row.setX(x);
		row.setY(y);
		row.setExtent(extent);
		row.setType(theType);

		testUnparse(row, newPage);
		testMarshal(row);
	}

	void testUnparse(final Row row, final boolean newPage) {

		final byte[] rowBytes = row.unparse(newPage);

		final int expectedNewPageBit = newPage ? 1 : 0;

		assertEquals(expectedNewPageBit, (rowBytes[1] & 0b10000000) >>> 7);
		assertEquals(row.getType().getId(), (rowBytes[1] & 0b01110000) >>> 4);
		assertEquals(row.getExtent(), (rowBytes[1] & 0b00001111));
	}

	void testMarshal(final Row row) throws JAXBException {

		StringWriter writer = new StringWriter();
		marshaller.marshal(row, writer);
		String xml = writer.toString();

		assertTrue(xml.contains("<row"));
		assertTrue(xml.contains(String.format("type=\"%s\"", row.getType())));
		assertTrue(xml.contains(String.format("x=\"%d\"", row.getX())));
		assertTrue(xml.contains(String.format("y=\"%d\"", row.getY())));
		assertTrue(xml.contains(String.format("extent=\"%d\"", row.getExtent())));
	}
}
