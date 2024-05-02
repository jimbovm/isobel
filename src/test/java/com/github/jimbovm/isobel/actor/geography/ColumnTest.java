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

public class ColumnTest extends ActorTest<Column> {

	public ColumnTest() throws JAXBException {
		super(Column.class);
	}

	@CsvSource({
		"0b0000_0000, 0b0101_0001, 0, 0, 1, BRICK",
		"0b0000_0000, 0b0110_0010, 0, 0, 2, BLOCK",

	})
	@ParameterizedTest void parse(
		@ConvertWith(BinaryIntegerConverter.class) final int lowByte,
		@ConvertWith(BinaryIntegerConverter.class) final int highByte,
		final int expectedX,
		final int expectedY,
		final int expectedExtent,
		final String expectedType) {
	
		Actor actor = NormalCommand.parse(lowByte, highByte, 0);
		
		assertInstanceOf(Column.class, actor);
		
		Column column = (Column) actor;

		Column.Type theExpectedType = Enum.valueOf(Column.Type.class, expectedType);
		assertEquals(column.getX(), expectedX);
		assertEquals(column.getY(), expectedY);
		assertEquals(column.getExtent(), expectedExtent);
		assertEquals(column.getType(), theExpectedType);
	}
	
	@CsvSource({
		"true, 0, 0, 0, BLOCK",
		"true, 0, 0, 0, BRICK",
	})
	@ParameterizedTest void translate(boolean newPage, final int x, final int y, final int extent, String type) throws Exception {

		Column.Type theType = Enum.valueOf(Column.Type.class, type);

		Column column = new Column();
		column.setX(x);
		column.setY(y);
		column.setExtent(extent);
		column.setType(theType);

		testUnparse(column, newPage);
		testMarshal(column);
	}

	void testUnparse(final Column column, final boolean newPage) {

		final byte[] columnBytes = column.unparse(newPage);

		final int expectedNewPageBit = newPage ? 1 : 0;

		assertEquals(expectedNewPageBit, (columnBytes[1] & 0b10000000) >>> 7);
		assertEquals(column.getType().getId(), (columnBytes[1] & 0b01110000) >>> 4);
		assertEquals(column.getExtent(), (columnBytes[1] & 0b00001111));
	}

	void testMarshal(final Column column) throws JAXBException {

		StringWriter writer = new StringWriter();
		marshaller.marshal(column, writer);
		String xml = writer.toString();

		assertTrue(xml.contains("<column"));
		assertTrue(xml.contains(String.format("type=\"%s\"", column.getType())));
		assertTrue(xml.contains(String.format("x=\"%d\"", column.getX())));
		assertTrue(xml.contains(String.format("y=\"%d\"", column.getY())));
		assertTrue(xml.contains(String.format("extent=\"%d\"", column.getExtent())));
	}
}
