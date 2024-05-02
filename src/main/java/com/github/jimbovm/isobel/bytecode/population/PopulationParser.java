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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.github.jimbovm.isobel.actor.population.Character;
import com.github.jimbovm.isobel.actor.population.ExitPointer;
import com.github.jimbovm.isobel.actor.population.PopulationActor;
import com.github.jimbovm.isobel.bytecode.common.BytecodeParser;

/**
 * Parses an input stream of bytes, interpreting them as population commands and
 * outputting a list of actor objects representing what those commands spawn
 * in-game.
 */
public final class PopulationParser extends BytecodeParser<PopulationActor> {
	
	private static final int MASK_X = 0xF0;
	private static final int MASK_Y = 0x0F;

	private static final int OFFSET_X = 4;

	/** The EOF marker for population data. */
	public static final int END_OF_FILE = 0xFF;
	
	/** 
	 * Parse an exit pointer command.
	 * @param lowByte The first byte of the command.
	 * @param midByte The second byte of the command.
	 * @param highByte The third byte of the command.
	 */
	@Override
	protected PopulationActor handleThreeByte(int lowByte, int midByte, int highByte) {
		
		final int x = (lowByte & MASK_X) >>> OFFSET_X;
		final int areaIndex = (midByte & 0b01111111);
		final int worldActive = (highByte & 0b1110000) >>> 4;
		final int startOnPage = (highByte & 0b0001111);

		final int blockOffset = this.page * 16;

		ExitPointer exit = new ExitPointer();
		exit.setX(x + blockOffset);
		exit.setActiveFromWorld(worldActive);
		exit.setStartPage(startOnPage);
		exit.setDestination(String.format("Area_%02X", areaIndex));

		return exit;
	}

	/** 
	 * Parse a character command.
	 * 
	 * @param lowByte The first byte of the command.
	 * @param highByte The second byte of the command.
	 */
	@Override
	protected PopulationActor handleTwoByte(int lowByte, int highByte) {
		
		final int x = (lowByte & MASK_X) >>> OFFSET_X;
		final int y = (lowByte & MASK_Y);
		final boolean hardModeOnly = !((highByte & 0b01000000) == 0);

		final int opcode = highByte & 0b00111111;

		final int blockOffset = this.page * 16;

		Character character = new Character();
		character.setX(x + blockOffset);
		character.setY(y);
		character.setHardModeOnly(hardModeOnly);
		character.setType(Character.Type.from(opcode));
		return character;
	}

	/**
	 * Create a new <code>PopulationParser</code>.
	 * 
	 * @param source The {@link InputStream} from which to read bytecode.
	 */
	public PopulationParser(InputStream source) {
		super(source);
	}

	/**
	 * Parse population actors from the parser's input stream.
	 * 
	 * @return A list of population actors parsed from the 
	 * {@link InputStream} given at construction.
	 * @throws IOException In the event of a problem with the input stream.
	 */
	public List<PopulationActor> parse() throws IOException {
		return super.parse(END_OF_FILE);
	}

	/**
	 * Return whether the command is a three-byte command.
	 * 
	 * @param lowByte The first byte of the command.
	 * 
	 * @return True if the command is three-byte, false otherwise.
	 */
	@Override
	protected boolean isThreeByte(int lowByte) {
		return (lowByte & 0b00001111) == 0b00001110;
	}

	/**
	 * Return whether the command is a page skip.
	 * 
	 * @param lowByte The first byte of the command.
	 * @param highByte The second byte of the command.
	 * 
	 * @return True if the command is a page skip, false otherwise.
	 */
	@Override
	protected boolean isPageSkip(int lowByte, int highByte) {
		return (lowByte & MASK_Y) == 0xF;
	}
}
