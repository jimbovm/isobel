/*
SPDX-License-Identifier: MIT-0

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
SOFTWARE.
*/

package com.github.jimbovm.isobel.bytecode.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.jimbovm.isobel.test.TestSuite;

import com.github.jimbovm.isobel.bytecode.geography.GeographyParser;
import com.github.jimbovm.isobel.common.Atlas;
import com.github.jimbovm.isobel.common.Scenario;

@DisabledIf("gameImageNotPresent")
public class GameParserTest extends TestSuite {

	private GameParser parser;

	public GameParserTest() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException {

		this.parser = GameParser.create(getFileFromResources("smb.bin").toString());
	}

	static boolean gameImageNotPresent() {
		return Objects.isNull(GameParserTest.class.getClassLoader().getResource("smb.bin"));
	}

	@Test void testGeographyEnvOffsetArray() throws Exception {

		final byte[] expected = {0x00, 0x03, 0x19, 0x1C};

		byte[] environmentTypeArray = (byte[]) MethodUtils.invokeMethod(
			parser,
			true,
			"getGeographyEnvironmentTypeArray");

		for (int i = 0; i <= 3; i++) {
			assertEquals(expected[i], environmentTypeArray[i]);
		}
	}

	@Test void testPopulationEnvOffsetArray() throws Exception {

		final byte[] expected = {0x1F, 0x06, 0x1C, 0x00};

		byte[] environmentTypeArray = (byte[]) MethodUtils.invokeMethod(
			parser,
			true,
			"getPopulationEnvironmentTypeArray");

		for (int i = 0; i <= 3; i++) {
			assertEquals(expected[i], environmentTypeArray[i]);
		}
	}

	@CsvSource({
		"0, 0x2E06", // bonus water area
		"1, 0x2E45", // 2-1/7-1
		"2, 0x2EC0" // final castle water area
	})
	@ParameterizedTest void testGeographyAddresses(final int index, final int address) throws Exception {
		int[] addresses = (int[]) MethodUtils.invokeMethod(parser, true, "getGeographyAddresses");
		assertEquals(address, addresses[index]); // bonus water area
	}

	@CsvSource({
		"0, 0x41, 0x01", // bonus water area
		"1, 0x41, 0x01", // 2-1/7-1
		"2, 0x49, 0x0F" // final castle water area
	})
	@ParameterizedTest void testGetGeographyFile(final int address, final int lowByte, final int highByte) throws Exception {
		int[] addresses = (int[]) MethodUtils.invokeMethod(parser, true, "getGeographyAddresses");
		byte[] file = (byte[]) MethodUtils.invokeMethod(parser, true, "getGeographyFile", addresses[address]);

		// file should end with an EOF marker
		assertEquals((byte) GeographyParser.END_OF_FILE, file[file.length - 1]);
		// check header bytes
		assertEquals(lowByte, file[0]);
		assertEquals(highByte, file[1]);
	}
	@CsvSource({
		"0, 0, 'Area_25'",
		"0, 1, 'Area_29'",
		"0, 2, 'Area_40'",
		"0, 3, 'Area_26'",
		"0, 4, 'Area_60'",
	})
	@ParameterizedTest void testGetScenario(int world, int level, String area) throws Exception {
		parser.parseAtlas();
		Scenario scenario = parser.parseScenario();
		assertEquals(area, scenario.getWorlds().
			get(world).
			getLevels().
			get(level).
			getStartArea().
			getId());
	}

	@Test void testParseAreas() throws Exception {
		Atlas atlas = parser.parseAtlas();

		assertTrue(atlas.getAreasById().containsKey("Area_00"));
	}
	
	@CsvSource({
		"0, 0, 5", // 1-1
		"0, 2, 6", // 1-2
		"1, 0, 6", // 2-1
		"7, 0, 0", // 8-1
	})
	@ParameterizedTest void testParseCheckpoints(int world, int level, byte checkpoint) throws Exception {

		parser.parseAtlas();
		Scenario scenario = parser.parseScenario();

		assertEquals(checkpoint, scenario.getWorlds()
		.get(world)
		.getLevels()
		.get(level)
		.getCheckpoint());
	}
}
