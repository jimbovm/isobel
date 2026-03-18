/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.bytecode.geography;

import lombok.AllArgsConstructor;
import lombok.Getter;

import io.github.jimbovm.isobel.actor.geography.FixedExtensible;
import io.github.jimbovm.isobel.bytecode.common.CommandUtils;

/**
 * Collects functionality for working with C-type commands, which spawn
 * miscellaneous level geography. C-type commands are so named because they have
 * a fixed Y coordinate value of 0xC.
 *
 * <table class="plain">
 * <caption style="caption-side: bottom">C-type command bytecode</caption>
 * <tr>
 * <td>7</td>
 * <td>6</td>
 * <td>5</td>
 * <td>4</td>
 * <td>3</td>
 * <td>2</td>
 * <td>1</td>
 * <td>0</td>
 * <td>7</td>
 * <td>6</td>
 * <td>5</td>
 * <td>4</td>
 * <td>3</td>
 * <td>2</td>
 * <td>1</td>
 * <td>0</td>
 * </tr>
 * <tr>
 * <td colspan="4">X</td>
 * <td colspan="4">0xC (constant)</td>
 * <td>P</td>
 * <td colspan="3">Actor ID</td>
 * <td colspan="4">Extent</td>
 * </tr>
 * </table>
 */
public final class CTypeCommand {

	// non-instantiable
	private CTypeCommand() {}

	/** The types of actor spawnable by a C-type geography command. */
	@Getter
	@AllArgsConstructor
	public enum ActorId {

		/**
		 * Removes the bottom two floor blocks, irrespective
		 * of terrain fill.
		 */
		PIT(0),
		/** The horizontal part of the ropes of a scale lift. */
		HORIZONTAL_SCALE_ROPE(1),
		/** A bridge at Y position 7. */
		BRIDGE_Y7(2),
		/** A bridge at Y position 8. */
		BRIDGE_Y8(3),
		/** A bridge at Y position 10. */
		BRIDGE_Y10(4),
		/**
		 * Removes the bottom two floor blocks and fills the
		 * space with water or lava, irrespective of terrain
		 * fill.
		 */
		POOL(5),
		/**
		 * A row of question blocks containing coins at Y
		 * position 3.
		 */
		QUESTION_BLOCK_RUN_Y3(6),
		/**
		 * A row of question blocks containing coins at Y
		 * position 7.
		 */
		QUESTION_BLOCK_RUN_Y7(7);

		private final int id;
	}

	/**
	 * Parse a C-type geography command and return an actor object.
	 * 
	 * @param  lowByte  The low byte of the command.
	 * @param  highByte The high byte of the command.
	 * @param  page     The number of 16-block pages from the origin to offset the X
	 *                  and Y coordinates in the command.
	 * 
	 * @return          A
	 *                  {@link io.github.jimbovm.isobel.actor.geography.GeographyActor}
	 *                  parsed from the input.
	 */
	public static FixedExtensible parse(final int lowByte, final int highByte, final int page) {

		final int x = ((lowByte & 0xF0) >>> 4) + page * 16;
		final int extent = (highByte & 0x0F);
		final FixedExtensible.Type type = FixedExtensible.Type.of((highByte & 0b01110000) >>> 4);

		FixedExtensible parsed = new FixedExtensible();
		parsed.setX(x);
		parsed.setExtent(extent);
		parsed.setType(type);

		return parsed;
	}

	/**
	 * Unparse a {@link FixedExtensible} object to geography bytecode.
	 * 
	 * @param  fixedExtensible The object to unparse.
	 * @param  newPage         Whether to set the new page flag in the generated
	 *                         bytecode.
	 * 
	 * @return                 A two-byte array of bytecode recognisable by the
	 *                         original game.
	 */
	public static byte[] unparse(FixedExtensible fixedExtensible, boolean newPage) {

		byte[] bytecode = new byte[2];

		bytecode[0] = 0xC;
		bytecode[0] = CommandUtils.encodeCoordinates(bytecode[0], fixedExtensible.getX());
		bytecode[1] = CommandUtils.encodeNewPage(bytecode[1], newPage);
		bytecode[1] |= (fixedExtensible.getType().getId() << 4);
		bytecode[1] |= fixedExtensible.getExtent();

		return bytecode;
	}
}
