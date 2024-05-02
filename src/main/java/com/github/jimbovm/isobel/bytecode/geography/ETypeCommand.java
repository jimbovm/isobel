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

package com.github.jimbovm.isobel.bytecode.geography;

import com.github.jimbovm.isobel.actor.geography.BackgroundModifier;
import com.github.jimbovm.isobel.actor.geography.FillSceneryModifier;
import com.github.jimbovm.isobel.actor.geography.GeographyActor;
import com.github.jimbovm.isobel.bytecode.common.CommandUtils;
import com.github.jimbovm.isobel.common.AreaHeader;
import com.github.jimbovm.isobel.common.AreaHeader.Background;
import com.github.jimbovm.isobel.common.AreaHeader.Fill;
import com.github.jimbovm.isobel.common.AreaHeader.Scenery;

/**
 * Collects functionality for working with E-type commands, which are
 * used for changing the non-interactive background and scenery of
 * areas on the fly.
 */
public final class ETypeCommand {

	// non-instantiable
	private ETypeCommand() {}

	private static final int MASK_BACKGROUND = 0b00000111;
	private static final int MASK_SCENERY = 0b00110000;
	private static final int MASK_FILL = 0b00001111;

	private static BackgroundModifier parseBackgroundModifier(final int x, final int highByte) {

		int backgroundOpcode = (highByte & MASK_BACKGROUND);
		Background background = AreaHeader.Background.from(backgroundOpcode);
		BackgroundModifier modifier = new BackgroundModifier();
		modifier.setX(x);
		modifier.setBackground(background);

		return modifier;
	}

	private static FillSceneryModifier parseFillSceneryModifier(final int x, final int highByte) {
		final int fillOpcode = highByte & MASK_FILL;
		final int sceneryOpcode = (highByte & MASK_SCENERY) >>> 6; 
		Fill fill = Fill.from(fillOpcode);
		Scenery scenery = Scenery.from(sceneryOpcode);

		FillSceneryModifier modifier = new FillSceneryModifier();
		modifier.setX(x);
		modifier.setFill(fill);
		modifier.setScenery(scenery);
		
		return modifier;
	}

	/**
	 * Parse an E-type geography command and return an actor object.
	 * 
	 * @param lowByte The low byte of the command.
	 * @param highByte The high byte of the command.
	 * @param page The number of 16-block pages from the origin to offset the X and Y coordinates in the command.
	 * @return A {@link com.github.jimbovm.isobel.actor.geography.GeographyActor} parsed from the input.
	 */
	public static GeographyActor parse(final int lowByte, final int highByte, final int page) {

		final boolean isBackgroundModifier = ((highByte & 0b01000000) >> 6) == 1;
		
		final int x = ((lowByte & 0xF0) >>> 4) + page * 16;
		if (isBackgroundModifier) {
			return parseBackgroundModifier(x, highByte);
		}
		// If we're here, it's a fill/scenery modifier
		return parseFillSceneryModifier(x, highByte);
	}

	/**
	 * Return a background modifier bean in game bytecode format.
	 * 
	 * @param modifier A background modifier bean.
	 * @param newPage Whether the command is the first on a new page.
	 * @return Bytecode to spawn the actor represented by the first argument.
	 */
	public static byte[] unparse(BackgroundModifier modifier, final boolean newPage) {
		
		byte[] bytecode = new byte[2];

		bytecode[0] = 0xE;
		bytecode[0] = CommandUtils.encodeCoordinates(bytecode[0], modifier.getX());
		bytecode[1] = CommandUtils.encodeNewPage(bytecode[1], newPage);

		bytecode[1] |= 0b0100_0000; // constant for background modifier spawners
		bytecode[1] |= modifier.getBackground().getOpcode();

		return bytecode;
	}

	/**
	 * Return a fill/scenery modifier bean in game bytecode format.
	 * 
	 * @param modifier A fill/scenery modifier bean.
	 * @param newPage Whether the command is the first on a new page.
	 * @return Bytecode to spawn the actor represented by the first argument.
	 */
	public static byte[] unparse(FillSceneryModifier modifier, final boolean newPage) {

		byte[] bytecode = new byte[2];

		bytecode[0] = 0xE;
		bytecode[0] = CommandUtils.encodeCoordinates(bytecode[0], modifier.getX());
		bytecode[1] = CommandUtils.encodeNewPage(bytecode[1], newPage);

		bytecode[1] |= (modifier.getScenery().getOpcode() << 4);
		bytecode[1] |= modifier.getFill().getOpcode();

		return bytecode;
	}
}
