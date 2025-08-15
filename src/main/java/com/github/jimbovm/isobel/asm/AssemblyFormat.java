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

package com.github.jimbovm.isobel.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.jimbovm.isobel.common.Area;
import com.github.jimbovm.isobel.common.Atlas;
import com.github.jimbovm.isobel.common.Game;
import com.github.jimbovm.isobel.common.Level;
import com.github.jimbovm.isobel.common.Scenario;
import com.github.jimbovm.isobel.common.World;
import com.github.jimbovm.isobel.common.Area.Environment;

/**
 * Functionality for formatting byte arrays into a format suitable for 
 * assembly.
 */
public final class AssemblyFormat {

	/**
	 * Format a byte array as assembly, outputting comma-separated lists of
	 * bytes in the form <code>$0f</code>, and labelled at the beginning 
	 * with the supplied label.
	 * 
	 * @param bytes A byte array to format
	 * @param label The label with which to label the generated assembly
	 * @return The generated assembly as a string
	 */
	public static String formatAsAssembly(byte[] bytes, String label) {

		if (bytes.length == 0) {
			return "";
		}

		StringBuffer buffer = new StringBuffer(String.format("%s:\n    ", label));
		buffer.append(".byte ");

		for (int i = 0; i < bytes.length; i++) {

			String following = ", ";
			if (i == (bytes.length - 1)) {
				following = "\n";
			}

			buffer.append(String.format("$%02x%s", bytes[i], following));
			
		}
		return buffer.toString();
	}

	/**
	 * Return a given world as assembly, labelling its areas with the number of the world.
	 *
	 * @param world The world object to be formatted as assembly
	 * @param worldNumber The number of the world (1 to 8)
	 * @param atlas An atlas object containing all the game's areas
	 * @return The generated assembly as a string
	 */
	public static String toAssembly(final World world, final int worldNumber, final Atlas atlas) {

		List<Level> levels = world.getLevels();

		String worldLabel = "World" + (worldNumber+1) + "Areas";
		byte[] indexesForWorld = new byte[levels.size()];
		
		for (int i = 0; i < levels.size(); i++) {
			final Area startArea = levels.get(i).getStartArea();
			final byte indexNumber = (byte) ((int) atlas.getIndexByArea().get(startArea));
			indexesForWorld[i] = indexNumber;
		}

		return formatAsAssembly(indexesForWorld, worldLabel);
	}

	/**
	 * Return a map to the key data represented by the Scenario object and its fields
	 * (the scenario itself, the hidden 1-up costs and the checkpoints) as assembly.
	 * 
	 * @param scenario A scenario object specifying a game's scenario data
	 * @param atlas	An atlas containing a game's complete area data
	 * @return A {@link Map} of Strings to Strings holding the generated assembly
	 */
	public static Map<String, String> toAssembly(Scenario scenario, Atlas atlas) {

		Map<String, String> assembly = new HashMap<>();

		StringBuilder scenarioBuilder = new StringBuilder("AreaAddrOffsets:\n");
		
		byte[] hidden1upCosts = new byte[8];
		List<Byte> checkpointNybbles = new ArrayList<>();

		for (int worldIndex = 0; worldIndex < 8; worldIndex++) {

			World thisWorld = scenario.getWorlds().get(worldIndex);

			scenarioBuilder.append(toAssembly(thisWorld, worldIndex, atlas) + "\n");
			hidden1upCosts[worldIndex] = thisWorld.getHidden1upCost();

			// limit to the first 4 levels for retrieving checkpoints
			int worldFinalLevel = 4;
			for (int levelIndex = 0; levelIndex < worldFinalLevel; levelIndex++) {
				
				Level thisLevel = thisWorld.getLevels().get(levelIndex);
				
				// ignore autowalk areas
				if (thisLevel.getStartArea().getHeader().isAutowalk() == true) {
					worldFinalLevel++;
					continue;
				}
				
				// try to be resilient against unsupported worlds < 4 levels
				if (levelIndex < thisWorld.getLevels().size()) {
					checkpointNybbles.add(thisLevel.getCheckpoint());
				} else {
					checkpointNybbles.add((byte) 0);
				}
			}
		}

		byte[] checkpoints = new byte[16];

		for (int i = 0; i < checkpointNybbles.size(); i += 4) {
			for (int j = 0; j < 16; j += 2) {
				checkpoints[j] = (byte) ((checkpointNybbles.get(i) & 0xF) << 4);
				checkpoints[j] |= checkpointNybbles.get(i+1) & 0xF;
				checkpoints[j+1] = (byte) ((checkpointNybbles.get(i+2) & 0xF) << 4);
				checkpoints[j+1] |= checkpointNybbles.get(i+3) & 0xF;
			}
		}

		assembly.put("scenario", scenarioBuilder.toString());
		assembly.put("costs", formatAsAssembly(hidden1upCosts, "Hidden1UpCoinAmts"));
		assembly.put("checkpoints", formatAsAssembly(checkpoints, "HalfwayPageNybbles"));
		return assembly;
	}

	/**
	 * Return a map to a game's raw area data formatted as assembly.
	 * 
	 * @param atlas An atlas containing a game's complete area data
	 * @return A {@link Map} of Strings to Strings holding the generated assembly
	 */
	public static Map<String, String> toAssembly(Atlas atlas) {

		StringBuilder geographyBuilder = new StringBuilder();
		StringBuilder populationBuilder = new StringBuilder();

		for (Area area : atlas.getAreas()) {
			geographyBuilder.append(formatAsAssembly(area.unparseGeography(), "G_" + area.getId()));
		}

		for (Area area : atlas.getAreas()) {
			populationBuilder.append(formatAsAssembly(area.unparsePopulation(atlas), "P_" + area.getId()));
		}

		HashMap<String, String> output = new HashMap<>();
		output.put("geography", geographyBuilder.toString());
		output.put("population", populationBuilder.toString());

		final Map<Environment, Integer> counts = atlas.getAreaCounts();
		final int totalAreas = counts.values().stream().reduce(0, Integer::sum);
		byte[] offsets = new byte[4];

		offsets[3] = (byte) (totalAreas - counts.getOrDefault(Environment.CASTLE, 0));
		offsets[2] = (byte) (offsets[3] - counts.getOrDefault(Environment.UNDERGROUND, 0));
		offsets[1] = (byte) (offsets[2] - counts.getOrDefault(Environment.OVERWORLD, 0));
		offsets[0] = (byte) (offsets[1] - counts.getOrDefault(Environment.UNDERWATER, 0));

		output.put("geography-environment-offsets", formatAsAssembly(offsets, "AreaDataHOffsets"));
		output.put("population-environment-offsets", formatAsAssembly(offsets, "EnemyAddrHOffsets"));

		StringBuilder populationAddressesBuilder = new StringBuilder();
		populationAddressesBuilder.append(".define EnemyDataAddr ");
		StringBuilder geographyAddressesBuilder = new StringBuilder();
		geographyAddressesBuilder.append(".define AreaDataAddr ");

		List<Area> areas = atlas.getAreas(); // they are already organised by environment
		if (areas.size() > 0) {
			for (int i = 0; i < (areas.size() - 1); i++) {
				populationAddressesBuilder.append("P_" + areas.get(i).getId() + ", ");
				geographyAddressesBuilder.append("G_" + areas.get(i).getId() + ", ");
			}
			populationAddressesBuilder.append("P_" + areas.get(areas.size() - 1).getId() + "\n");
			geographyAddressesBuilder.append("G_" + areas.get(areas.size() - 1).getId() + "\n");
		}

		populationAddressesBuilder.append("EnemyDataAddrLow: .lobytes EnemyDataAddr\n");
		populationAddressesBuilder.append("EnemyDataAddrHigh: .hibytes EnemyDataAddr\n");
		geographyAddressesBuilder.append("AreaDataAddrLow: .lobytes AreaDataAddr\n");
		geographyAddressesBuilder.append("AreaDataAddrHigh: .hibytes AreaDataAddr\n");

		output.put("population-addresses", populationAddressesBuilder.toString());
		output.put("geography-addresses", geographyAddressesBuilder.toString());

		return output;
	}


	/**
	 * Return a full game in assemblable format.
	 * 
	 * @param game The <code>Game</code> to format as assembly.
	 * @return A map of Strings to Strings reflective of output filenames
	 * and their contents.
	 */
	public static Map<String, String> toAssembly(Game game) {

		HashMap<String, String> output = new HashMap<>();

		output.putAll(toAssembly(game.getAtlas()));
		output.putAll(toAssembly(game.getScenario(), game.getAtlas()));
		output.putAll(toAssembly(game.getAtlas()));

		return output;
	}
}
