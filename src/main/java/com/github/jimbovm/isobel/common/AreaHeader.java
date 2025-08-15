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

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * Represents an area header, which sets default values for how the area should 
 * be rendered. This also provides static utility methods used to interpret the 
 * opcodes for controlling the background, scenery and terrain fill used 
 * dynamically via header modifiers used in geography data.
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
@Log4j2
@ToString
@XmlType(name = "header")
@XmlRootElement(name = "header")
public final class AreaHeader {

	/** Strings for header bytecode */
	private static final ResourceBundle headerStrings = ResourceBundle.getBundle("Header");

	private static final int MASK_TIMER = 0b1100000000000000;
	private static final int MASK_AUTOWALK = 0b0010000000000000;
	private static final int MASK_START = 0b0001100000000000;
	private static final int MASK_BACKGROUND = 0b0000011100000000;
	private static final int MASK_PLATFORM_TYPE = 0b0000000011000000;
	private static final int MASK_SCENERY = 0b0000000000110000;
	private static final int MASK_GROUND = 0b0000000000001111;
	
	private static final int MASK_TIMER_OFFSET = 14;
	private static final int MASK_AUTOWALK_OFFSET = 13;
	private static final int MASK_START_OFFSET = 11;
	private static final int MASK_BACKGROUND_OFFSET = 8;
	private static final int MASK_PLATFORM_OFFSET = 6;
	private static final int MASK_SCENERY_OFFSET = 4;

	private static final int TIMER_0 = 0b00;
	private static final int TIMER_400 = 0b01;
	private static final int TIMER_300 = 0b10;
	private static final int TIMER_200 = 0b11;

	/**
	 * Default fill style for the area, determining the way in which layers of 
	 * blocks are generated to fill the playable part of the screen.
	 */
	@XmlType(name = "fill")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public static enum Fill {

		/** No fill; empty. */
		FILL_NONE (0),
		/** Two floor blocks. */
		FILL_2BF_0BC (1),
		/** Two floor blocks and one ceiling block. */
		FILL_2BF_1BC (2),
		/** Two floor blocks and three ceiling blocks. */
		FILL_2BF_3BC (3),
		/** Two floor blocks and four ceiling blocks. */
		FILL_2BF_4BC (4),
		/** Two floor blocks and eight ceiling blocks. */
		FILL_2BF_8BC (5),
		/** Five floor blocks and one ceiling block. */
		FILL_5BF_1BC (6),
		/** Five floor blocks and three ceiling blocks. */
		FILL_5BF_3BC (7),
		/** Five floor blocks and four ceiling blocks. */
		FILL_5BF_4BC (8),
		/** Six floor blocks and one ceiling block. */
		FILL_6BF_1BC (9),
		/** One ceiling block (no floor). */
		FILL_0BF_1BC (10),
		/** Six floor blocks and four ceiling blocks. */
		FILL_6BF_4BC (11),
		/** Nine floor blocks and one ceiling block. */
		FILL_9BF_1BC (12),
		/** Two floor blocks, a three-block gap, a layer of five blocks, a two-block gap, and one ceiling block. */
		FILL_2BF_3BG_5BL_2BG_1BC (13),
		/** Two floor blocks, a three-block gap, a four-block layer, a three-block gap, and one ceiling block. */
		FILL_2BF_3BG_4BL_3BG_1BC (14),
		/** Total fill; the whole screen is filled. */
		FILL_ALL (15);

		private final int opcode;

		private static Map<Integer, Fill> map = new HashMap<Integer, Fill>();

		static {
			for (Fill type : Fill.values())
				map.put(type.opcode, type);
		}
		
		/**
		 * Return a fill value for a given ID.
		 * 
		 * @param opcode A numerical fill ID.
		 * @return The fill value associated with the supplied ID.
		 */
		public static final Fill from(int opcode) {
			return map.get(opcode);	
		}

		@Override
		public String toString() {
			return headerStrings.getString(this.name());
		}
	};

	private static final int[] TIMER_VALUES = {
		0,
		400,
		300,
		200
	};

	/** The position at which the player is spawned on entering the area. */
	@XmlType(name = "startPosition")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public static enum StartPosition {
		/** @deprecated Used by the game engine only. */
		@Deprecated
		FALL_INTERNAL (0),
		/** Player falls from above the top of the screen. */
		FALL (1),
		/** PLayer starts two blocks from the bottom of the screen. */
		BOTTOM (2),
		/** Player starts in the middle of the screen. */
		MIDDLE (3),
		/** @deprecated Redundant. */
		@Deprecated
		FALL_4 (4),
		/** @deprecated Redundant. */
		@Deprecated
		FALL_5 (5),
		/** Player starts two blocks from the bottom of the screen, autowalking. */
		BOTTOM_AUTOWALK (6),
		/** @deprecated Redundant. */
		@Deprecated
		BOTTOM_AUTOWALK_7 (7);

		private final int opcode;

		private static Map<Integer, StartPosition> map = new HashMap<Integer, StartPosition>();

		static {
			for (StartPosition type : StartPosition.values())
			map.put(type.opcode, type);
		}

		/**
		 * Return a start position value for a given ID.
		 * 
		 * @param opcode A numerical start position ID.
		 * @return The start position value associated with the supplied ID.
		 */
		public static final StartPosition from(int opcode) {
			return map.get(opcode);
		}
	};

	/**
	 * Style of non-interactive scenery in the background layer.
	 */
	@XmlType(name = "scenery")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public static enum Scenery {
		/** No scenery. */
		NONE (0b00),
		/** Clouds only. */
		CLOUDS (0b01),
		/** Clouds, hills and trees. */
		HILLS (0b10),
		/** Clouds, fences and trees. */
		FENCES (0b11);
		
		private final int opcode;

		private static Map<Integer, Scenery> map = new HashMap<>();

		static {
			for (Scenery type : Scenery.values())
			map.put(type.opcode, type);
		}

		/**
		 * Return a scenery type value for a given ID.
		 * 
		 * @param opcode A numerical scenery type ID.
		 * @return The scenery type value associated with the supplied ID.
		 */
		public static final Scenery from(int opcode) {
			return map.get(opcode);
		}
		
		@Override
		public String toString() {
			if (this == Scenery.NONE) {
				return headerStrings.getString("SCENERY_NONE");
			}
			return headerStrings.getString(this.name());
		}
	};
	
	/** 
	 * Style in which extensible platforms (generated by geography
	 * commands) are rendered. 
	 */
	@XmlType(name = "platform")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public static enum Platform {
		/** Extensible platforms are trees. */
		@XmlEnumValue("TREE")
		TREE (0b00),
		/** Extensible platforms are giant mushrooms. */
		@XmlEnumValue("MUSHROOM")
		MUSHROOM (0b01),
		/** Extensible platforms are Bullet Bill cannons. */
		@XmlEnumValue("CANNON")
		CANNON (0b10),
		/** 
		 * Cloud area; extensible platforms are trees, and the
		 * player falling out of the sky is an exit event, not a death.
		 */
		@XmlEnumValue("CLOUD")
		CLOUD (0b11);
		
		private final int opcode;

		private static Map<Integer, Platform> map = new HashMap<>();

		static {
			for (Platform type : Platform.values())
			map.put(type.opcode, type);
		}

		/**
		 * Return a platform type value for a given ID.
		 * 
		 * @param opcode A numerical platform type ID.
		 * @return The platform type value associated with the supplied ID.
		 */
		public static final Platform from(int opcode) {
			return map.get(opcode);
		}

		@Override
		public String toString() {
			return headerStrings.getString(this.name());
		}
	};
	
	/** 
	 * Background style, influencing the background colour and the palette used 
	 * for scenery.
	 */
	@XmlType(name = "background")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public static enum Background {

		/** No background. */
		@XmlEnumValue("NONE") NONE (0b000),
		/** The sea fills the background. */
		@XmlEnumValue("UNDERWATER") UNDERWATER (0b001),
		/** The background is castle walls. */
		@XmlEnumValue("CASTLE_WALL") CASTLE_WALL (0b010),
		/** Water or lava shows in the background where there is no floor. */
		@XmlEnumValue("OVER_WATER") OVER_WATER (0b011),
		/** Nighttime palette. */
		@XmlEnumValue("NIGHT") NIGHT (0b100),
		/** Daytime with a winter palette. */
		@XmlEnumValue("DAY_SNOW") DAY_SNOW (0b101),
		/** Nighttime with a winter palette. */
		@XmlEnumValue("NIGHT_SNOW") NIGHT_SNOW (0b110),
		/** Colour drained from the world (as in 6-3 in the original). */
		@XmlEnumValue("MONOCHROME") MONOCHROME (0b111);
		
		private final int opcode;
		private static Map<Integer, Background> map = new HashMap<>();

		static {
			for (Background type : Background.values()) {
				map.put(type.opcode, type);
			}
		}

		/**
		 * Return a background type value for a given ID.
		 * 
		 * @param opcode A numerical background type ID.
		 * @return The type value associated with the supplied ID.
		 */
		public static final Background from(int opcode) {
			return map.get(opcode);
		}

		@Override
		public String toString() {
			if (this == Background.NONE) {
				return headerStrings.getString("BACKGROUND_NONE");
			}
			return headerStrings.getString(this.name());
		}
	};
	
	private Fill fill;

	private boolean autowalk;

	private int ticks;

	private StartPosition startPosition;

	private Background background;

	private Scenery scenery;

	private Platform platform;

	/**
	 * Create an <code>AreaHeader</code> object from an in-game header. 
	 * 
	 * @param lowByte The first byte of the header.
	 * @param highByte The second byte of the header.
	 * @return An <code>AreaHeader</code> object parsed from the supplied bytes.
	 */
	public static AreaHeader parse(int lowByte, int highByte) {
		int headerWord = (lowByte << 8) | highByte;
		return AreaHeader.parse(headerWord);
	}

	private static AreaHeader parse(int headerWord) {

		AreaHeader endProduct = new AreaHeader();
	
		int timerIndex = parseValue(
			headerWord,
			MASK_TIMER,
			MASK_TIMER_OFFSET);
		endProduct.ticks = TIMER_VALUES[timerIndex];
		
		int autoWalkValue = parseValue(
			headerWord,
			MASK_AUTOWALK,
			MASK_AUTOWALK_OFFSET);
		endProduct.autowalk = (autoWalkValue == 1);
		
		int backgroundValue = (headerWord & MASK_BACKGROUND) >>> MASK_BACKGROUND_OFFSET;
		endProduct.background = AreaHeader.Background.from(backgroundValue);

		int sceneryValue = parseValue(
			headerWord,
			MASK_SCENERY,
			MASK_SCENERY_OFFSET);
		endProduct.scenery = AreaHeader.Scenery.from(sceneryValue);

		int platformTypeValue = parseValue(
			headerWord,
			MASK_PLATFORM_TYPE,
			MASK_PLATFORM_OFFSET);
		endProduct.platform = AreaHeader.Platform.from(platformTypeValue);
		
		endProduct.fill = AreaHeader.Fill.from((headerWord) & MASK_GROUND);

		int startValue = parseValue(headerWord, MASK_START, MASK_START_OFFSET);
		endProduct.startPosition = AreaHeader.StartPosition.from((startValue));

		log.info(String.format(
			"Created AreaHeader from header word %x.",
			headerWord
		));
		
		return endProduct;
	}
	
	private static int parseValue(final int headerBytes, final int mask, final int offset) {
		return (headerBytes & mask) >>> offset;
	}

	/**
	 * Set the initial number of ticks the timer will count down from.
	 * 
	 * @param ticks 0, 400, 300 or 200. 
	 */
	public void setTicks(int ticks) {

		final Set<Integer> VALID_VALUES = Set.of(0, 400, 300, 200);

		if (VALID_VALUES.contains(ticks) == false) {
			throw new IllegalArgumentException(
				String.format("Illegal ticks value %d. Values allowed are %s",
				ticks, VALID_VALUES.toString())
			);
		}
		
		this.ticks = ticks; 
	}

	/**
	 * Return a binary representation of the header recognisable by the 
	 * original game.
	 * 
	 * @return A two-byte array encoding the header 
	 */
	public byte[] unparse() {

		byte[] headerBytes = new byte[2];

		int ticksBits;
		switch (this.ticks) {
			case 0:
				ticksBits = TIMER_0; break;
			case 300:
				ticksBits = TIMER_300; break;
			case 200:
				ticksBits = TIMER_200; break;
			default:
				ticksBits = TIMER_400;
		}

		// low byte
		headerBytes[0] |= ticksBits << (MASK_TIMER_OFFSET - 8);
		headerBytes[0] |= (this.autowalk ? 1 : 0) << (MASK_AUTOWALK_OFFSET - 8); 
		headerBytes[0] |= this.startPosition.opcode << (MASK_START_OFFSET - 8); 
		headerBytes[0] |= this.background.opcode;

		// high byte
		headerBytes[1] |= this.platform.opcode << MASK_PLATFORM_OFFSET;
		headerBytes[1] |= this.scenery.opcode << MASK_SCENERY_OFFSET;
		headerBytes[1] |= this.fill.opcode;

		return headerBytes;
	}

	/**
	 * Creates a new AreaHeader object with default values of 400 timer
	 * ticks, autowalk off, no background, tree platforms, hills and clouds
	 * scenery and a two-block floor, no ceiling terrain fill.
	 */
	public AreaHeader() {
		this.ticks = 400;
		this.autowalk = false;
		this.startPosition = AreaHeader.StartPosition.BOTTOM;
		this.background = AreaHeader.Background.NONE;
		this.platform = AreaHeader.Platform.TREE;
		this.scenery = AreaHeader.Scenery.HILLS;
		this.fill = AreaHeader.Fill.FILL_2BF_0BC;
	}
}
