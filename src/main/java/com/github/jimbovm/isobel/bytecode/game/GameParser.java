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

package com.github.jimbovm.isobel.bytecode.game;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import com.github.jimbovm.isobel.bytecode.geography.GeographyParser;
import com.github.jimbovm.isobel.bytecode.population.PopulationParser;
import com.github.jimbovm.isobel.common.Area;
import com.github.jimbovm.isobel.common.Atlas;
import com.github.jimbovm.isobel.common.Level;
import com.github.jimbovm.isobel.common.Scenario;
import com.github.jimbovm.isobel.common.World;
import lombok.extern.log4j.Log4j2;

/**
 * Encapsulates functionality for extracting data from a binary game
 * image.
 *
 * By default when constructed, this class is designed to work with an
 * exact binary image of the standalone US/Japanese release of the
 * original game cartridge (two 16kB mask-ROM images concatenated,
 * with no header).
 *
 * It is possible in theory to parse a modified game image, and
 * methods are provided for adjusting the behaviour of the parser
 * object. However, this requires careful manual analysis of the
 * non-standard game image, and "garbage in, garbage out" behaviour
 * should be expected.
 * 
 */
@Log4j2
public final class GameParser {

	private int checkpointsStart = 0x11BD;
	private int checkpointsEnd = 0x11CC;

	private int populationEnvironmentTypeStart = 0x1CE0;
	private int geographyEnvironmentTypeStart = 0x1D28;
	
	private byte[] gameData;

	private int populationLsbStart = 0x1CE4;
	private int populationLsbEnd = 0x1D05;

	private int populationMsbStart = 0x1D06;
	private int populationMsbEnd = 0x1D27;

	private int geographyLsbStart = 0x1D2C;
	private int geographyLsbEnd = 0x1D4D;

	private int geographyMsbStart = 0x1D4E;
	private int geographyMsbEnd = 0x1D6F;

	private int underwaterAreas = 3;
	private int overworldAreas = 22;
	private int undergroundAreas = 3;
	private int castleAreas = 6;

	private Map<Integer, Integer> levelsPerWorld;

	private Atlas atlas;

	{
		this.levelsPerWorld = new HashMap<>();
		this.atlas = new Atlas();
	}

	/**
	 * Create a new parser instance to read from a file at the 
	 * supplied location.
	 * 
	 * @param filepath The location of the file to read from.
	 * @return A new <code>GameParser</code> instance.
	 * @throws IOException In the event of a problem with the filepath.
	 */
	public static GameParser create(String filepath) throws IOException {
		return new GameParser(filepath);
	}

	private GameParser(String filepath) throws IOException {
		this.setDefaultAreasPerWorld();
		File file = new File(filepath);
		
		try {
			Path path = Path.of(URLDecoder.decode(file.toPath().toString(), "UTF-8"));
			this.gameData = Files.readAllBytes(path);
		}
		catch (UnsupportedEncodingException e) {
			log.fatal("UTF-8 is not a supported encoding. This should never occur.");
		}
		catch (IOException e) {
			// Don't allow construction if path is invalid
			log.error(e.getMessage());
			throw e;
		}
	}

	private void setDefaultAreasPerWorld() {
		this.levelsPerWorld.put(0, 5);
		this.levelsPerWorld.put(1, 5);
		this.levelsPerWorld.put(2, 4);
		this.levelsPerWorld.put(3, 5);
		this.levelsPerWorld.put(4, 4);
		this.levelsPerWorld.put(5, 4);
		this.levelsPerWorld.put(6, 5);
		this.levelsPerWorld.put(7, 4);
	}

	/**
	 * Set the number of levels per each world of the game.
	 * By default the level counts from the original game are used.
	 * 
	 * @param levelsPerWorld A map of zero-based world numbers to one-based level counts.
	 */
	public void setLevelsPerWorld(Map<Integer, Integer> levelsPerWorld) {
		this.levelsPerWorld = levelsPerWorld;
	}

	private int areaIndexFromComponents(int environmentType, int subindex) {
		return ((environmentType << 5) | subindex);
	}

	/**
	 * Return the total number of levels in the game.
	 * 
	 * @return The sum of all levels in each world. 
	 */
	public int getLevelTotal() {
		return levelsPerWorld.values().stream().reduce(0, Integer::sum);
	}

	private byte[] getCheckpoints() {
		return ArrayUtils.subarray(
			this.gameData,
			checkpointsStart,
			checkpointsEnd + 1);
	}

	private List<Byte> getCleanCheckpoints() {

		final byte[] checkpoints = this.getCheckpoints();
		List<Byte> cleanCheckpoints = new ArrayList<>();

		for (int i = 0; i < 16; i+= 2) {
			byte level1Nybble = (byte) ((checkpoints[i] & 0xF0) >> 4);
			byte level2Nybble = (byte) (checkpoints[i] & 0x0F);
			byte level3Nybble = (byte) ((checkpoints[i+1] & 0xF0) >> 4);
			byte level4Nybble = (byte) (checkpoints[i+1] & 0x0F);
			cleanCheckpoints.addAll(
				List.of(level1Nybble,
				level2Nybble,
				level3Nybble,
				level4Nybble));
		}
		return cleanCheckpoints;
	}

	private byte[] getPopulationLsbs() {
		return ArrayUtils.subarray(
			this.gameData,
			this.populationLsbStart,
			this.populationLsbEnd + 1);
	}

	private byte[] getPopulationMsbs() {
		return ArrayUtils.subarray(
			this.gameData,
			this.populationMsbStart,
			this.populationMsbEnd + 1);
	}

	private byte[] getGeographyLsbs() {
		return ArrayUtils.subarray(
			this.gameData,
			this.geographyLsbStart,
			this.geographyLsbEnd + 1);
	}

	private byte[] getGeographyMsbs() {
		return ArrayUtils.subarray(
			this.gameData,
			geographyMsbStart,
			geographyMsbEnd + 1);
	}

	private int[] getAddresses(byte[] msbs, byte[] lsbs) {
		int[] addresses = new int[msbs.length];
		for (int i = 0; i < msbs.length; i++) {
			// use a ByteBuffer to avoid sign extension messing things up
			byte[] bytes = {0, 0, msbs[i], lsbs[i]};
			addresses[i] = ByteBuffer.wrap(bytes).getInt() & 0b00111111_11111111;
		}
		return addresses;
	}

	private int[] getPopulationAddresses() {
		byte[] msbs = this.getPopulationMsbs();
		byte[] lsbs = this.getPopulationLsbs();

		return this.getAddresses(msbs, lsbs);
	}

	private int[] getGeographyAddresses() {
		byte[] msbs = this.getGeographyMsbs();
		byte[] lsbs = this.getGeographyLsbs();

		return this.getAddresses(msbs, lsbs);
	}

	private byte[] getEnvironmentTypeArray(int offset) {
		return ArrayUtils.subarray(this.gameData, offset, offset + 4);
	}

	private byte[] getGeographyEnvironmentTypeArray() {
		return this.getEnvironmentTypeArray(this.geographyEnvironmentTypeStart);
	}

	private byte[] getPopulationEnvironmentTypeArray() {
		return this.getEnvironmentTypeArray(this.populationEnvironmentTypeStart);
	}

	/**
	 * Retreive a "file" of data from the binary image, defined as all the data
	 * between an offset and a supplied end of file marker, inclusive of both.
	 * 
	 * @param offset The position at which to start reading data.
	 * @param endOfFile The byte value marking where to stop reading.
	 * @return The bytes between <code>offset</code> and <code>endOfFile</code>,
	 * inclusive.
	 */
	public byte[] getFile(int offset, int endOfFile) {
		final int indexOfEof = ArrayUtils.indexOf(this.gameData, (byte) endOfFile, offset);
		return ArrayUtils.subarray(this.gameData, offset, indexOfEof + 1);
	}

	private byte[] getGeographyFile(int offset) {
		return this.getFile(offset, GeographyParser.END_OF_FILE);
	}

	private byte[] getPopulationFile(int offset) {
		return this.getFile(offset, PopulationParser.END_OF_FILE);
	}

	/**
	 * Returns an 8-element array, with each element corresponding
	 * to the minimum number of coins required for hidden 1-ups to
	 * appear in the first level of the subsequent world. The last
	 * value (for world 8) is effectively meaningless.
	 * 
	 * @return An array of 8 bytes. 
	 */
	public byte[] parseHidden1upPrices() {
		return ArrayUtils.subarray(this.gameData, 0x32C2, 0x32C9 + 1);
	}

	/**
	 * Parse the scenario data from the binary image into a
	 * {@link com.github.jimbovm.isobel.common.Scenario} object.
	 * 
	 * @return A scenario object reflective of the data read from the image.
	 */
	public Scenario parseScenario() {

		var worlds = new ArrayList<World>();
		final byte[] worldOffsets = ArrayUtils.subarray(this.gameData, 0x1CB4, 0x1CBB + 1);

		// Keep track of the checkpoint offset relative to the level being considered
		int checkpointPointer = 0;

		for (int worldIndex = 0; worldIndex < 8; worldIndex++) {

			var world = new World();
			final int numberOfLevels = this.levelsPerWorld.get(worldIndex);

			for (int levelIndex = 0; levelIndex < numberOfLevels; levelIndex++) {
				
				final byte areaIndex = this.gameData[0x1CBC + worldOffsets[worldIndex] + levelIndex];
				Area startArea = atlas.get(String.format("Area_%02X", ((int) areaIndex) & 0b01111111));
				
				byte checkpoint;
				// Skip reading the checkpoints array for autowalk areas
				if (startArea.getHeader().isAutowalk()) {
					checkpoint = 0;
				} else {
					checkpoint = this.getCleanCheckpoints().get(checkpointPointer);
					checkpointPointer++;
				}

				Level level = new Level(startArea, checkpoint);
				world.getLevels().add(level);
			}

			worlds.add(world);
		}

		var scenario = new Scenario();
		scenario.setWorlds(worlds);

		return scenario;
	}

	/**
	 * Parse area from the binary image into a
	 * {@link com.github.jimbovm.isobel.common.Area} object.
	 * 
	 * @param environment The environment type of the area to be parsed.
	 * @param geographyAddress The start address of the area's geography data.
	 * @param populationAddress The start address of the area's population data.
	 * @param offset The offset of an area within its environment category.
	 * 
	 * @return An area object reflective of the data read from the image.
	 */
	private void parseArea(
		Area.Environment environment,
		final int geographyAddress,
		final int populationAddress,
		final int offset) 
		{
			
		// Deduce the in-game area index
		final int backFormedAreaIndex = this.areaIndexFromComponents(environment.getId(), offset);
		/* Form the area's immutable name; code that parses
		 * exit pointers forms the name similarly. */
		final String immutableAreaName = String.format("Area_%02X", backFormedAreaIndex);

		// Grab the data based on the addresses and parse into an area object
		byte[] geographyFile = this.getGeographyFile(geographyAddress);
		byte[] populationFile = this.getPopulationFile(populationAddress);
		Area area = Area.parse(environment, geographyFile, populationFile, immutableAreaName);

		// Done; add it to the area atlas keyed to its immutable name
		this.atlas.add(area);
	}

	/**
	 * Parse the area data from the binary image into a
	 * {@link com.github.jimbovm.isobel.common.Atlas} object.
	 * 
	 * @return A atlas object reflective of the data read from the image.
	 */
	public Atlas parseAtlas() {
		// First, retrieve the start offsets for each environment type
		final byte[] geographyEnvironmentOffsets = this.getGeographyEnvironmentTypeArray();
		final byte[] populationEnvironmentOffsets = this.getPopulationEnvironmentTypeArray();

		// Then set convenience constants for these values
		final int underwaterGeographyStart = geographyEnvironmentOffsets[0];
		final int overworldGeographyStart = geographyEnvironmentOffsets[1];
		final int undergroundGeographyStart = geographyEnvironmentOffsets[2];
		final int castleGeographyStart = geographyEnvironmentOffsets[3];
		final int underwaterPopulationStart = populationEnvironmentOffsets[0];
		final int overworldPopulationStart = populationEnvironmentOffsets[1];
		final int undergroundPopulationStart = populationEnvironmentOffsets[2];
		final int castlePopulationStart = populationEnvironmentOffsets[3];

		// Retrieve the actual memory addresses for both kinds of area files
		final int[] geographyAddresses = this.getGeographyAddresses();
		final int[] populationAddresses = this.getPopulationAddresses();

		/* Irrespective of the environment order, we parse
		 * underwater first, then overworld, then underground,
		 * then castle.
		 */
		for (int i = 0; i < underwaterAreas; i++) {
			this.parseArea(
				Area.Environment.UNDERWATER,
				geographyAddresses[underwaterGeographyStart + i],
				populationAddresses[underwaterPopulationStart + i],
				i);
		}

		for (int i = 0; i < overworldAreas; i++) {
			this.parseArea(
				Area.Environment.OVERWORLD,
				geographyAddresses[overworldGeographyStart + i],
				populationAddresses[overworldPopulationStart + i],
				i);
		}

		for (int i = 0; i < undergroundAreas; i++) {
			this.parseArea(
				Area.Environment.UNDERGROUND,
				geographyAddresses[undergroundGeographyStart + i],
				populationAddresses[undergroundPopulationStart + i],
				i);
		}
		
		for (int i = 0; i < castleAreas; i++) {
			this.parseArea(
				Area.Environment.CASTLE,
				geographyAddresses[castleGeographyStart + i],
				populationAddresses[castlePopulationStart + i],
				i);
		}

		return this.atlas;
	}
}
