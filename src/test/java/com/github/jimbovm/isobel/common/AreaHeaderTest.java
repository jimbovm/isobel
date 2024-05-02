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
 
package com.github.jimbovm.isobel.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.io.StringWriter;

import com.github.jimbovm.isobel.test.TestSuite;

class AreaHeaderTest extends TestSuite {

	void checkHeader(
		AreaHeader header,
		int ticks,
		boolean autowalk,
		AreaHeader.StartPosition startPosition,
		AreaHeader.Background background,
		AreaHeader.Platform platform,
		AreaHeader.Scenery scenery,
		AreaHeader.Fill fill
	) {
		assertEquals(background, header.getBackground());
		assertEquals(scenery, header.getScenery());
		assertEquals(platform, header.getPlatform());
		assertEquals(autowalk, header.isAutowalk());
		assertEquals(ticks, header.getTicks());
		assertEquals(fill, header.getFill());
		assertEquals(startPosition, header.getStartPosition());
	}

	@ParameterizedTest
	@CsvSource({
		"0,NONE",
		"1,CLOUDS",
		"2,HILLS",
		"3,FENCES"
	})
	void testScenery(final int bits, final String type) {

		AreaHeader header = new AreaHeader();
		AreaHeader.Scenery scenery = AreaHeader.Scenery.valueOf(type);
		
		header.setScenery(scenery);
		assertEquals(type, header.getScenery().name());

		final byte[] headerBytes = header.unparse();
		final int sceneryFromBytes = (headerBytes[1] & 0b00110000) >>> 4;
		assertEquals(sceneryFromBytes, bits);
	}

	@Test void world1_1HeaderParse() throws Exception {

		this.checkHeader(
			AreaHeader.parse(0x50, 0x21),
			400,
			false,
			AreaHeader.StartPosition.BOTTOM,
			AreaHeader.Background.NONE,
			AreaHeader.Platform.TREE,
			AreaHeader.Scenery.HILLS,
			AreaHeader.Fill.FILL_2BF_0BC);
	}

	@Test void world1_2HeaderParse() throws Exception {

		this.checkHeader(
			AreaHeader.parse(0x48, 0x0f),
			400,
			false,
			AreaHeader.StartPosition.FALL,
			AreaHeader.Background.NONE,
			AreaHeader.Platform.TREE,
			AreaHeader.Scenery.NONE,
			AreaHeader.Fill.FILL_ALL);
	}

	@Test void world1_3HeaderParse() throws Exception {

		this.checkHeader(
			AreaHeader.parse(0x90, 0x11),
			300,
			false,
			AreaHeader.StartPosition.BOTTOM,
			AreaHeader.Background.NONE,
			AreaHeader.Platform.TREE,
			AreaHeader.Scenery.CLOUDS,
			AreaHeader.Fill.FILL_2BF_0BC);
	}

	@Test void world1_4HeaderParse() throws Exception {

		this.checkHeader(
			AreaHeader.parse(0x9b, 0x07),
			300,
			false,
			AreaHeader.StartPosition.MIDDLE,
			AreaHeader.Background.OVER_WATER,
			AreaHeader.Platform.TREE,
			AreaHeader.Scenery.NONE,
			AreaHeader.Fill.FILL_5BF_3BC);
	}

	@CsvSource({
		"0x50, 0x21", // 1-1
		"0x48, 0x0f", // 1-2
		"0x90, 0x11", // 1-3
		"0x9b, 0x07", // 1-4
		"0x52, 0x31", // 2-1
		"0x41, 0x01", // 2-2
		"0x90, 0x11", // 2-3
		"0x52, 0x31", // 3-1
		"0x96, 0x31", // 3-2
		"0x94, 0x11", // 3-3
		"0x52, 0x21", // 4-1
		"0x10, 0x51", // mushroom warp zone
		"0x90, 0x51", // 4-3
		"0x5b, 0x07", // 4-4
		"0x38, 0x11", // pipe transition scene 
	})
	@ParameterizedTest void headerParseEquality(int lowByte, int highByte) throws Exception {

		var header = AreaHeader.parse((byte) lowByte, (byte) highByte);
		byte[] headerBytes = header.unparse();

		assertEquals(headerBytes[0], (byte) lowByte);
		assertEquals(headerBytes[1], (byte) highByte);
	}

	@Test void marshal() throws Exception {
		var header = new AreaHeader();

		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(AreaHeader.class);
		Marshaller m = context.createMarshaller();
		m.marshal(header, writer);

		String xml = writer.toString();

		assertFalse(xml.length() == 0);
		assertTrue(xml.contains("<is:autowalk>false</is:autowalk>"));
		assertTrue(xml.contains("<is:background>NONE</is:background>"));
		assertTrue(xml.contains("<is:platform>TREE</is:platform>"));
		assertTrue(xml.contains("<is:startPosition>BOTTOM</is:startPosition>"));
	}

	@Test void unmarshal() throws Exception {

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><is:header xmlns:is='http://github.com/jimbovm/isobel'><is:autowalk>false</is:autowalk><is:background>NONE</is:background><is:fill>FILL_2BF_0BC</is:fill><is:platform>TREE</is:platform><is:scenery>HILLS</is:scenery><is:startPosition>BOTTOM</is:startPosition><is:ticks>400</is:ticks></is:header>";

		JAXBContext context = JAXBContext.newInstance(AreaHeader.class);
		Unmarshaller m = context.createUnmarshaller();
		AreaHeader header = (AreaHeader) m.unmarshal(new StringReader(xml));

		assertNotNull(header);
		assertEquals(400, header.getTicks());
		assertFalse(header.isAutowalk());
		assertEquals(AreaHeader.Scenery.HILLS, header.getScenery());
		assertEquals(AreaHeader.Fill.FILL_2BF_0BC, header.getFill());
		assertEquals(AreaHeader.Platform.TREE, header.getPlatform());
	}
}
