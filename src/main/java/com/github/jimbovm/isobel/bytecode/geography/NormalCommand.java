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

import java.util.HashMap;
import java.util.Map;

import com.github.jimbovm.isobel.actor.geography.Column;
import com.github.jimbovm.isobel.actor.geography.ExtensiblePlatform;
import com.github.jimbovm.isobel.actor.geography.GeographyActor;
import com.github.jimbovm.isobel.actor.geography.Row;
import com.github.jimbovm.isobel.actor.geography.SingletonObject;
import com.github.jimbovm.isobel.actor.geography.UprightPipe;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.github.jimbovm.isobel.bytecode.common.CommandUtils.*;

/**
 * Collects functionality for working with "normal" commands, which
 * are used for spawning actors which can be freely placed in 
 * two-dimensional space.
 * 
 * <table class="plain">
 * <caption style="caption-side: bottom">Normal-type command bytecode</caption>
 * <tr>
 * <td>7</td><td>6</td><td>5</td><td>4</td><td>3</td><td>2</td><td>1</td><td>0</td>
 * <td>7</td><td>6</td><td>5</td><td>4</td><td>3</td><td>2</td><td>1</td><td>0</td>
 * </tr>
 * <tr>
 * <td colspan="4">X</td><td colspan="4">Y</td><td>P</td><td colspan="3">Type</td><td colspan="4">Parameter</td>
 * </tr>
 * </table>
 */
public final class NormalCommand {
		
	private static final int X_MASK = 0xF0; // for lowByte
	private static final int Y_MASK = 0x0F; // for lowByte
	private static final int TYPE_MASK = 0b01110000; // for highByte

	// non-instantiable
	private NormalCommand() {}
	
	/** Actor ID values for "type 0" actors spawned by normal-type commands. */
	@Getter
	@AllArgsConstructor
	public enum PrimeActorId {
		/** A ? Block containing a Super Mushroom or Fire Flower, depending on the player's state. */
		QUESTION_BLOCK_POWERUP (0),
		/** A singleton ? block containing a coin. */
		QUESTION_BLOCK_COIN (1),
		/** An invisible block containing a coin. */
		HIDDEN_BLOCK_COIN (2),
		/** An invisible block containing a 1-Up Mushroom. */
		HIDDEN_BLOCK_1UP (3),
		/** A brick block containing a Super Mushroom or Fire Flower, depending on the player's state. */
		BRICK_POWERUP (4),
		/** A brick block containing a vine. */
		BRICK_VINE (5),
		/** A brick block containing a Starman. */
		BRICK_STARMAN (6),
		/** A multi-coin brick block. */
		BRICK_MULTI_COIN (7),
		/** A brick block containing a 1-Up Mushroom. */
		BRICK_1UP (8),
		/** A short sideways pipe, always enterable. Used as the exit from underwater areas in the original. */
		SIDEWAYS_PIPE (9),
		/** A "punched" ? Block. Often used with fire bars in the original. */
		QUESTION_BLOCK_USED (0xA),
		/** The bouncy trampoline. */
		JUMPING_BOARD (0xB),
		/** @deprecated Internal use only; not intended for spawning directly. */
		@Deprecated TEE_PIPE (0xC),
		/** @deprecated Internal use only; not intended for spawning directly. */
		@Deprecated FLAGPOLE (0xD),
		/** @deprecated Internal use only; not intended for spawning directly. */
		@Deprecated BOWSER_BRIDGE (0xE);

		private final int id;
	}

	/** 
	 * Actor ID values for non-"type 0" actors spawned by normal-type
	 * commands. That is, the type of actor spawned when the Type bits are
	 * something other than 0.
	 */
	@Getter
	@AllArgsConstructor
	public enum AltActorId {
		/** A tree, giant mushroom or Bullet Bill cannon depending on the area header's settings.
		 * @see com.github.jimbovm.isobel.common.AreaHeader.Platform
		 */
		EXTENSIBLE_PLATFORM (1),
		/** A row of brick blocks. */
		ROW_BRICKS (2),
		/** A row of solid blocks. */
		ROW_BLOCKS (3),
		/** A row of coins. */
		ROW_COINS (4),
		/** A column of brick blocks, or coral underwater. */
		COLUMN_BRICKS (5),
		/** A column of solid blocks. */
		COLUMN_BLOCKS (6),
		/** A pipe, mouth facing the sky, which can be enterable or not. */
		UPRIGHT_PIPE (7);

		private final int id;

		private static Map<Integer, AltActorId> map = new HashMap<>();

		static {
			for (AltActorId type : AltActorId.values())
				map.put(type.id, type);
		}

		/**
		 * Return an actor value for a given ID.
		 * 
		 * @param id A numerical actor ID.
		 * @return The actor value associated with the supplied ID.
		 */
		public static AltActorId from(int id) { return map.get(id); }
	}

	private static int getExtent(final int highByte) {
		return highByte & 0x0F;
	}

	private static UprightPipe parseUprightPipe(final int x, final int y, final int highByte) {

		final boolean enterable = ((highByte & 0b00001000) >>> 3) == 1;

		/* Extent works differently for upright pipes; only the 3 LSBs
		are used, the 4th LSB is the enterable flag! */
		final int extent = (highByte & 0b00000111);

		UprightPipe pipe = new UprightPipe();
		pipe.setX(x);
		pipe.setY(y);
		pipe.setExtent(extent);
		pipe.setEnterable(enterable);

		return pipe;
	}

	private static Row parseRow(final int x, final int y, final int highByte) {
		
		final int extent = getExtent(highByte);
		final int typeId = (highByte & 0b01110000) >>> 4;
		final AltActorId actorId = AltActorId.from(typeId);

		Row row = new Row();
		row.setX(x);
		row.setY(y);
		row.setExtent(extent);

		switch (actorId) {
			case ROW_BRICKS: row.setType(Row.Type.BRICK); break;
			case ROW_BLOCKS: row.setType(Row.Type.BLOCK); break;
			case ROW_COINS: row.setType(Row.Type.COIN); break;
			default: throw new IllegalStateException("Attempt to create a row with invalid type code " + actorId);
		}

		return row;
	}

	private static Column parseColumn(final int x, final int y, final int highByte) {
		
		final int extent = getExtent(highByte);
		final int typeId = (highByte & 0b01110000) >>> 4;
		final AltActorId actorId = AltActorId.from(typeId);

		Column column = new Column();
		column.setX(x);
		column.setY(y);
		column.setExtent(extent);

		switch (actorId) {
			case COLUMN_BRICKS: column.setType(Column.Type.BRICK); break;
			case COLUMN_BLOCKS: column.setType(Column.Type.BLOCK); break;
			default: throw new IllegalStateException("Attempt to create a row with invalid type code " + actorId);
		}

		return column;
	}

	private static SingletonObject parseSingletonObject(final int x, final int y, final int highByte) {
		
		final int actorId = (highByte & 0x0F);

		SingletonObject singleton = new SingletonObject();
		singleton.setX(x);
		singleton.setY(y);
		singleton.setType(SingletonObject.Type.from(actorId));
		
		return singleton;
	}

	private static ExtensiblePlatform parseExtensiblePlatform(int x, int y, int highByte) {
		
		final int extent = (highByte & 0x0F);

		ExtensiblePlatform platform = new ExtensiblePlatform();
		platform.setY(y);
		platform.setX(x);
		platform.setExtent(extent);

		return platform;
	}

	private static GeographyActor parseAltActor(final int x, final int y, final int highByte) {
		
		final AltActorId altActor = AltActorId.from((highByte & TYPE_MASK) >>> 4);

		GeographyActor parsed;

		switch (altActor) {
			case EXTENSIBLE_PLATFORM: parsed = parseExtensiblePlatform(x, y, highByte); break;
			case ROW_BLOCKS: case ROW_BRICKS: case ROW_COINS: parsed = parseRow(x, y, highByte); break;
			case COLUMN_BRICKS: case COLUMN_BLOCKS: parsed = parseColumn(x, y, highByte); break;
			case UPRIGHT_PIPE: parsed = parseUprightPipe(x, y, highByte); break;
			default: System.out.println(altActor.toString()); parsed = null;
		}

		return parsed;
	}

	/**
	 * Parse a normal-type geography command and return an actor object.
	 * 
	 * @param lowByte The low byte of the command.
	 * @param highByte The high byte of the command.
	 * @param page The number of 16-block pages from the origin to offset the X and Y coordinates in the command.
	 * @return A {@link com.github.jimbovm.isobel.actor.geography.GeographyActor} parsed from the input.
	 */
	public static GeographyActor parse(final int lowByte, final int highByte, final int page) {

		final int x = ((lowByte & X_MASK) >>> 4) + page * 16;
		final int y = (lowByte & Y_MASK);

		final boolean isSingletonObject = ((highByte & TYPE_MASK) >>> 4) == 0;

		if (isSingletonObject) {
			return parseSingletonObject(x, y, highByte);
		}
		return parseAltActor(x, y, highByte);
	}

	/**
	 * Unparse a {@link UprightPipe} object to bytecode.
	 * 
	 * @param pipe The object to unparse.
	 * @param newPage Whether to set the new page flag in the generated bytecode.
	 * @return A two-byte array of bytecode recognisable by the original game.
	 */
	public static byte[] unparse(UprightPipe pipe, final boolean newPage) {

		byte[] bytecode = new byte[2];

		bytecode[0] = encodeCoordinates(bytecode[0], pipe.getX(), pipe.getY());
		bytecode[1] |= encodeNewPage(bytecode[1], newPage);
		bytecode[1] |= 0b01110000; // constant
		bytecode[1] |= (pipe.isEnterable() ? 0b00001000 : 0);
		bytecode[1] |= pipe.getExtent();

		return bytecode;
	}

	/**
	 * Unparse a {@link SingletonObject} object to bytecode.
	 * 
	 * @param object The object to unparse.
	 * @param newPage Whether to set the new page flag in the generated bytecode.
	 * @return A two-byte array of bytecode recognisable by the original game.
	 */
	public static byte[] unparse(SingletonObject object, boolean newPage) {

		byte[] bytecode = new byte[2];

		bytecode[0] = encodeCoordinates(bytecode[0], object.getX(), object.getY());
		bytecode[1] = encodeNewPage(bytecode[1], newPage);
		bytecode[1] |= object.getType().getId();

		return bytecode;
	}

	/**
	 * Unparse a {@link Row} object to bytecode.
	 * 
	 * @param row The object to unparse.
	 * @param newPage Whether to set the new page flag in the generated bytecode.
	 * @return A two-byte array of bytecode recognisable by the original game.
	 */
	public static byte[] unparse(Row row, boolean newPage) {
		
		byte[] bytecode = new byte[2];

		bytecode[0] = encodeCoordinates(bytecode[0], row.getX(), row.getY());
		bytecode[1] = encodeNewPage(bytecode[1], newPage);
		bytecode[1] |= (row.getType().getId() << 4);
		bytecode[1] |= row.getExtent();
		
		return bytecode;
	}

	/**
	 * Unparse a {@link Column} object to bytecode.
	 * 
	 * @param column The object to unparse.
	 * @param newPage Whether to set the new page flag in the generated bytecode.
	 * @return A two-byte array of bytecode recognisable by the original game.
	 */
	public static byte[] unparse(Column column, boolean newPage) {
		
		byte[] bytecode = new byte[2];

		bytecode[0] = encodeCoordinates(bytecode[0], column.getX(), column.getY());
		bytecode[1] = encodeNewPage(bytecode[1], newPage);
		bytecode[1] |= (column.getType().getId() << 4);
		bytecode[1] |= column.getExtent();
		
		return bytecode;
	}

	/**
	 * Unparse a {@link ExtensiblePlatform} object to bytecode.
	 * 
	 * @param platform The object to unparse.
	 * @param newPage Whether to set the new page flag in the generated bytecode.
	 * @return A two-byte array of bytecode recognisable by the original game.
	 */
	public static byte[] unparse(ExtensiblePlatform platform, boolean newPage) {

		byte[] bytecode = new byte[2];

		bytecode[0] = encodeCoordinates(bytecode[0], platform.getX(), platform.getY());
		bytecode[1] = encodeNewPage(bytecode[1], newPage);
		bytecode[1] |= 0b0001_0000;
		bytecode[1] |= platform.getExtent();

		return bytecode;
	}
}
