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

import static com.github.jimbovm.isobel.bytecode.common.CommandUtils.encodeCoordinates;
import static com.github.jimbovm.isobel.bytecode.common.CommandUtils.encodeNewPage;
import static com.github.jimbovm.isobel.bytecode.common.CommandUtils.encodeHardMode;

import com.github.jimbovm.isobel.actor.PageSkip;
import com.github.jimbovm.isobel.actor.population.Character;
import com.github.jimbovm.isobel.actor.population.ExitPointer;
import com.github.jimbovm.isobel.common.Area;
import com.github.jimbovm.isobel.common.Atlas;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Encapulates functionality for working with population commands. Typically,
 * these are character commands, "characters" being moving objects which the 
 * player can interact with in some way, such as enemies, lifts and moving platforms.
 * Population commands also include page skip commands.
 */
public interface PopulationCommand {

	/**
	 * The character to be spawned by a character command.
	 */
	@Getter
	@AllArgsConstructor
	public enum CharacterId {

		/** The ubiquitous turtle; walks off edges. */
		GREEN_TROOPA_WALKOFF(0x00),
		/** @deprecated A Red KT that behaves like a Green KT. Beta, not intended for use. */
		@Deprecated
		RED_TROOPA_WALKOFF(0x01),
		/** The fireproof hardhead. */
		BUZZY_BEETLE(0x02),
		/** The ubiquitous turtle; turns around at edges. */
		RED_TROOPA_STICKY(0x03),
		/** @deprecated A Green KT that behaves like a Red KT. What Red Paratroopas become when stomped. */
		@Deprecated
		GREEN_TROOPA_STICKY(0x04),
		/** One half of the hammer-slinging duo. */
		HAMMER_BRO(0x05),
		/** The always stompable mushroom that betrayed their kingdom. */
		GOOMBA(0x06),
		/** The squid. */
		BLOOBER(0x07),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		BULLET_BILL(0x08),
		/** @deprecated Beta enemy, not intended for use. */
		@Deprecated
		GREEN_PARATROOPA_FIXED(0x09),
		/** The straighter swimming Cheep-Cheep. */
		GREEN_CHEEP(0x0A),
		/** The ubiquitous fish, in its swimming guise. */
		RED_CHEEP(0x0B),
		/** The big ball of lava. Its X position sets its jump height. */
		PODOBOO(0x0C),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		PIRANHA_PLANT(0x0D),
		/** A flying green KT that hops. */
		GREEN_PARATROOPA_HOP(0x0E),
		/** A flying red KT that flaps up and down. */
		RED_PARATROOPA(0x0F),
		/** A flying green KT that flaps back and forth in midair. */
		GREEN_PARATROOPA_HOVER(0x10),
		/** The master of clouds and thrower of eggs. */
		LAKITU(0x11),
		/** A pre-hatched Lakitu pet. */
		SPINY(0x12),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		FLYING_CHEEP_GENERATOR(0x14),
		/** Bowser's bursts of fiery breath. */
		BOWSER_BREATH_GENERATOR(0x15),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		FIREWORKS(0x16),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		BILL_GENERATOR(0x17),
		/** A short fire bar which moves slowly clockwise. */
		SLOW_FIRE_BAR_CLOCKWISE(0x1B),
		/** A short fire bar which moves quickly clockwise. */
		FAST_FIRE_BAR_CLOCKWISE(0x1C),
		/** A short fire bar which moves slowly anticlockwise. */
		SLOW_FIRE_BAR_ANTICLOCKWISE(0x1D),
		/** A short fire bar which moves quickly anticlockwise. */
		FAST_FIRE_BAR_ANTICLOCKWISE(0x1E),
		/** A long fire bar which moves clockwise. */
		LONG_FIRE_BAR_CLOCKWISE(0x1F),
		/** A double lift that behaves as a scale. */
		SCALE_LIFT(0x24),
		/** A lift that moves up and down. */
		LIFT_UP_AND_DOWN(0x25),
		/** A lift that moves up and off the top of the screen. */
		LIFT_UP(0x26),
		/** A lift that moves down and off the bottom of the screen. */
		LIFT_DOWN(0x27),
		/** A lift that moves side to side. */
		LIFT_SIDE_TO_SIDE(0x28),
		/** A lift that falls when walked on. */
		LIFT_FALL(0x29),
		/** A lift that moves right forever. */
		LIFT_RIGHT(0x2A),
		/** A short lift that moves up and off the top of the screen. */
		SHORT_LIFT_UP(0x2B),
		/** A short lift that moves down and off the bottom of the screen. */
		SHORT_LIFT_DOWN(0x2C),
		/** The king of evil himself. */
		BOWSER(0x2D),
		/** The actor which triggers warp zones. */
		WARP_ZONE(0x34),
		/** A Mushroom Retainer or the Princess, depending on world. */
		TOAD_PEACH(0x35),
		/** A squad of two Goombas at Y position 10. */
		GOOMBA_SQUAD_2_Y10(0x37),
		/** A squad of three Goombas at Y position 10. */
		GOOMBA_SQUAD_3_Y10(0x38),
		/** A squad of two Goombas at Y position 6. */
		GOOMBA_SQUAD_2_Y6(0x39),
		/** A squad of three Goombas at Y position 6. */
		GOOMBA_SQUAD_3_Y6(0x3A),
		/** A squad of two Green Koopa Troopas at Y position 10. */
		TROOPA_SQUAD_2_Y10(0x3B),
		/** A squad of three Green Koopa Troopas at Y position 10. */
		TROOPA_SQUAD_3_Y10(0x3C),
		/** A squad of two Green Koopa Troopas at Y position 6. */
		TROOPA_SQUAD_2_Y6(0x3D),
		/** A squad of three Green Koopa Troopas at Y position 6. */
		TROOPA_SQUAD_3_Y6(0x3E);

		private final int opcode;
	}

	/**
	 * Unparse an ExitPointer to game bytecode.
	 *
	 * @param exitPointer The ExitPointer to unparse.
	 * @param newPage Whether to set the new page flag in the output.
	 * @param atlas The Atlas to use to determine area index data.
	 *
	 * @return The bytecode to spawn the exit pointer represented by the
	 * first argument.
	 */
	public static byte[] unparse(ExitPointer exitPointer, final boolean newPage, final Atlas atlas) {

		byte[] bytecode = new byte[3];
		bytecode[0] = encodeCoordinates(bytecode[0], exitPointer.getX());
		bytecode[0] |= 0b1110;
		bytecode[1] = encodeNewPage(bytecode[1], newPage);

		final Area destination = atlas.get(exitPointer.getDestination());

		bytecode[1] |= atlas.getIndex(destination);

		bytecode[2] |= exitPointer.getStartPage() | (exitPointer.getActiveFromWorld() << 5);
		bytecode[2] &= 0xFF;

		return bytecode;

	}

	/**
	 * Unparse a Character to game bytecode.
	 *
	 * @param character The Character to unparse.
	 * @param newPage Whether to set the new page flag in the output.
	 *
	 * @return The bytecode to spawn the character represented by the first
	 * argument.
	 */
	public static byte[] unparse(Character character, final boolean newPage) {

		byte[] bytecode = new byte[2];

		bytecode[0] = encodeCoordinates(bytecode[0], character.getX(), character.getY());
		bytecode[1] = encodeNewPage(bytecode[1], newPage);
		bytecode[1] = encodeHardMode(bytecode[1], character.isHardModeOnly());

		bytecode[1] |= character.getType().getId();

		return bytecode;
	}
	/**
	 * Unparse a PageSkip to game bytecode.
	 *
	 * @param skip The PageSkip to unparse.
	 * @param newPage Whether to set the new page flag in the output.
	 *
	 * @return The bytecode to spawn the page skip represented by the first
	 * argument.
	 */
	public static byte[] unparse(PageSkip skip, final boolean newPage) {

		byte[] bytecode = new byte[2];

		// set sentinel for a page skip command
		bytecode[0] |= 0xF;
		bytecode[0] = encodeCoordinates(bytecode[0], skip.getX());

		encodeNewPage(bytecode[1], newPage);
		bytecode[1] |= (skip.getTarget() & 0b00111111);

		return bytecode;
	}
}
