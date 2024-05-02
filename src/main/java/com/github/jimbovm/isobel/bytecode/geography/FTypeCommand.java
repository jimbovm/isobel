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

import org.apache.commons.lang3.StringUtils;
import com.github.jimbovm.isobel.actor.geography.AnglePipe;
import com.github.jimbovm.isobel.actor.geography.Castle;
import com.github.jimbovm.isobel.actor.geography.FullHeightRope;
import com.github.jimbovm.isobel.actor.geography.GeographyActor;
import com.github.jimbovm.isobel.actor.geography.ScaleRopeVertical;
import com.github.jimbovm.isobel.actor.geography.Staircase;
import com.github.jimbovm.isobel.bytecode.common.CommandUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Collects functionality for working with F-type geography commands,
 * which are used to spawn a variety of miscellaneous actors.
 *
 * F-type commands work in a dissimilar way to most other commands,
 * encoding an actor ID and a parameter in the 7 least significant
 * bits of their high bytes.
 *
 * @see com.github.jimbovm.isobel.actor.geography.AnglePipe
 * @see com.github.jimbovm.isobel.actor.geography.Castle
 * @see com.github.jimbovm.isobel.actor.geography.FullHeightRope
 * @see com.github.jimbovm.isobel.actor.geography.ScaleRopeVertical
 * @see com.github.jimbovm.isobel.actor.geography.Staircase
 */
@Log4j2
public final class FTypeCommand {

	// non-instantiable
	private FTypeCommand() {}

	@Getter
	@AllArgsConstructor
	private static enum Mask {

		/** Bitmask for the X coordinate bits in the low byte */
		X (0b11110000),
		/** Bitmask for the actor ID plus its parameter in the high byte */
		ACTOR_WITH_PARAMETER (0b01111111),
		/** Bitmask for the actor ID in the high byte */
		ACTOR (0b01110000),
		/** Bitmask for the actor parameter in the high byte */
		PARAMETER (0b00001111);

		private final int mask;
	}

	/** The types of actor spawnable by an F-type geography command. */
	@Getter
	@AllArgsConstructor
	public static enum ActorId {
		/** Actor ID for parsing a command for a full height rope.
		 * @see com.github.jimbovm.isobel.actor.geography.FullHeightRope
		 */
		FULL_HEIGHT_ROPE (0),
		/** Actor ID for parsing a command for a vertical scale lift rope.
		 * @see com.github.jimbovm.isobel.actor.geography.ScaleRopeVertical
		 */
		SCALE_ROPE_VERTICAL (1),
		/** Actor ID for parsing a command for a castle.
		 * @see com.github.jimbovm.isobel.actor.geography.Castle
		 */
		CASTLE (2),
		/** Actor ID for parsing a command for a staircase.
		 * @see com.github.jimbovm.isobel.actor.geography.Staircase
		 */
		STAIRCASE (3),
		/** Actor ID for parsing a command for an angle pipe.
		 * @see com.github.jimbovm.isobel.actor.geography.AnglePipe
		 */
		ANGLE_PIPE (4);

		/**
		 * The unsigned integer representing the actor that the command spawns.
		 */
		private final int id;

		/**
		 * Retrieve an actor ID enum value from an ID.
		 * 
		 * @param opcode An actor ID.
		 * @return The enum value referred to by the ID.
		 */
		public static ActorId from(int opcode) {

			switch (opcode) {
				case 0: return FULL_HEIGHT_ROPE;
				case 1: return SCALE_ROPE_VERTICAL;
				case 2: return CASTLE;
				case 3: return STAIRCASE;
				case 4: return ANGLE_PIPE;
				default:
					log.error("Could not parse F-type command with invalid opcode " + opcode);
					return null;
			}
		}
	};

	private static int parseParameter(final int actorId) {
		return actorId & Mask.PARAMETER.getMask();
	}

	private static Castle parseCastle(int x, int actorId) {

		int sizeParameter = parseParameter(actorId);
		Castle castle = new Castle();
		Castle.Size size = (sizeParameter == 0) ? Castle.Size.LARGE : Castle.Size.SMALL;
		castle.setX(x);
		castle.setSize(size);
		return castle;
	}

	/**
	 * Parse an F-type geography command and return an actor object.
	 * 
	 * @param lowByte The low byte of the command.
	 * @param highByte The high byte of the command.
	 * @param page The number of 16-block pages from the origin to offset the X and Y coordinates in the command.
	 * @return A {@link com.github.jimbovm.isobel.actor.geography.GeographyActor} parsed from the input.
	 */
	public static GeographyActor parse(final int lowByte, final int highByte, final int page) {

		final int x = ((lowByte & Mask.X.getMask()) >>> 4) + page * 16;
		// Y position is ignored as it's always 0x0F
		final int actorId = (highByte & Mask.ACTOR_WITH_PARAMETER.getMask());

		GeographyActor parsed = null;
		
		// Various F-type commands encode their parameters in weird ways.
		final ActorId actor = ActorId.from((actorId & Mask.ACTOR.getMask()) >>> 4);

		log.info(String.format("Parsed %s at x=%d", actor.toString(), x));

		switch (actor) {
			case FULL_HEIGHT_ROPE:
				FullHeightRope rope = new FullHeightRope();
				rope.setX(x);
				parsed = rope;
				break;
			case SCALE_ROPE_VERTICAL:
				ScaleRopeVertical scaleRope = new ScaleRopeVertical();
				scaleRope.setX(x);
				scaleRope.setExtent(parseParameter(actorId));
				parsed = scaleRope;
				break;
			case CASTLE: return parseCastle(x, actorId);
			case STAIRCASE: // staircases
				Staircase staircase = new Staircase();
				staircase.setExtent(parseParameter(actorId));
				staircase.setX(x);
				parsed = staircase;
				break;
			case ANGLE_PIPE: // angle pipe
				AnglePipe anglePipe = new AnglePipe();
				// Y position is encoded in the same way as other objects encode extent
				anglePipe.setY(parseParameter(actorId));
				anglePipe.setX(x);
				parsed = anglePipe;
				break;
			default: // everything else
				log.error(String.format(
					"Could not parse actor from bytes 0x%02X%02X (%s %s)", 
					lowByte,
					highByte,
					StringUtils.leftPad(Integer.toBinaryString(lowByte), 8, '0'),
					StringUtils.leftPad(Integer.toBinaryString(highByte), 8, '0')));
		}

		return parsed;
	}

	/**
	 * Return the game bytecode for a generic F-type command.
	 * 
	 * @param actorId The actor ID to encode.
	 * @param The absolute X position of the command.
	 * @param newPage Whether the command is the first on a new page.
	 * @param The parameter to encode within the command.
	 * @return Bytecode to spawn the actor described by the arguments.
	 */
	private static byte[] unparse(
		final ActorId actorId,
		final int x,
		final int parameter,
		final boolean newPage
	) {
		byte[] bytecode = new byte[2];

		bytecode[0] |= 0x0F; // constant
		bytecode[0] = CommandUtils.encodeCoordinates(bytecode[0], x);

		bytecode[1] = CommandUtils.encodeNewPage(bytecode[1], newPage);
		bytecode[1] |= (actorId.getId() << 4);
		bytecode[1] |= parameter;

		return bytecode;
	}

	/**
	 * Return a staircase bean in game bytecode format.
	 * 
	 * @param staircase A staircase bean.
	 * @param newPage Whether the command is the first on a new page.
	 * @return Bytecode to spawn the actor represented by the first argument.
	 */
	public static byte[] unparse(Staircase staircase, final boolean newPage) {
		return unparse(
			ActorId.STAIRCASE,
			staircase.getX(),
			staircase.getExtent(),
			newPage);
	}

	/**
	 * Return a vertical scale rope bean in game bytecode format.
	 * 
	 * @param rope A vertical scale rope bean.
	 * @param newPage Whether the command is the first on a new page.
	 * @return Bytecode to spawn the actor represented by the first argument.
	 */
	public static byte[] unparse(ScaleRopeVertical rope, final boolean newPage) {
		return unparse(
			ActorId.SCALE_ROPE_VERTICAL, 
			rope.getX(),
			rope.getExtent(),
			newPage);
	}

	/**
	 * Return a full height rope bean in game bytecode format.
	 * 
	 * @param rope A full height rope bean.
	 * @param newPage Whether the command is the first on a new page.
	 * @return Bytecode to spawn the actor represented by the first argument.
	 */
	public static byte[] unparse(FullHeightRope rope, final boolean newPage) {
		return unparse(
			ActorId.FULL_HEIGHT_ROPE,
			rope.getX(),
			0, // this actor doesn't take a parameter
			newPage);
	}

	/**
	 * Return a castle bean in game bytecode format.
	 * 
	 * @param castle A castle bean.
	 * @param newPage Whether the command is the first on a new page.
	 * @return Bytecode to spawn the actor represented by the first argument.
	 */
	public static byte[] unparse(Castle castle, final boolean newPage) {
		return unparse(
			ActorId.CASTLE,
			castle.getX(),
			castle.getSize().getId(),
			newPage);
	}

	/**
	 * Return an angle pipe bean in game bytecode format.
	 * 
	 * @param pipe An angle pipe bean.
	 * @param newPage Whether the command is the first on a new page.
	 * @return Bytecode to spawn the actor represented by the first argument.
	 */
	public static byte[] unparse(AnglePipe pipe, final boolean newPage) {
		return unparse(
			ActorId.ANGLE_PIPE,
			pipe.getX(),
			pipe.getY(),
			newPage);
	}
}
