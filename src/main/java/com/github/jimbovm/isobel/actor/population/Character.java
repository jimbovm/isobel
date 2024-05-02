/* SPDX-License-Identifier: MIT-0

Copyright 2022 Jimbo Brierley.

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

package com.github.jimbovm.isobel.actor.population;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.xml.bind.annotation.XmlEnumValue;

import com.github.jimbovm.isobel.actor.YPlaceable;
import com.github.jimbovm.isobel.bytecode.population.PopulationCommand;
import com.github.jimbovm.isobel.bytecode.population.PopulationCommand.CharacterId;
import com.github.jimbovm.isobel.common.Atlas;

/**
 * This bean represents all actors represented by population commands in the 
 * original game with the exception of page manipulators. All of these can 
 * be placed freely in the X and Y axes, and have fixed extent.
 * 
 * The overwhelming majority of these are objects, mostly moving or movable,
 * which the player character can interact with or change the state of in some 
 * way. Some of these, such as lifts and fire bars, can only very tenuously be
 * described as "characters", and a few (e.g. the warp zone command) can't even 
 * remotely be considered that way, but these oddities are arguably too small to
 * justify higher levels of abstraction.
 */
@ToString
@Getter
@Setter
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder={"x", "y", "type", "hardModeOnly"})
@XmlRootElement(name = "character")
public final class Character extends PopulationActor implements YPlaceable {

	/** Representations of character types. */
	@XmlType(name = "characterType")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public enum Type {
		
		/** The ubiquitous turtle; walks off edges. */
		@XmlEnumValue("GREEN_TROOPA")
		GREEN_TROOPA (CharacterId.GREEN_TROOPA_WALKOFF.getOpcode()),
		/** A Buzzy Beetle. */
		@XmlEnumValue("BUZZY_BEETLE")
		BUZZY_BEETLE (CharacterId.BUZZY_BEETLE.getOpcode()),
		/** The ubiquitous turtle; turns around at edges. */
		@XmlEnumValue("RED_TROOPA")
		RED_TROOPA (CharacterId.RED_TROOPA_STICKY.getOpcode()),
		/** One half of the hammer-slinging duo. */
		@XmlEnumValue("HAMMER_BRO")
		HAMMER_BRO (CharacterId.HAMMER_BRO.getOpcode()),
		/** The always stompable mushroom traitor. */
		@XmlEnumValue("GOOMBA")
		GOOMBA (CharacterId.GOOMBA.getOpcode()),
		/** The squid. */
		@XmlEnumValue("BLOOBER")
		BLOOBER (CharacterId.BLOOBER.getOpcode()),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		@XmlEnumValue("BULLET_BILL") 
		BULLET_BILL (CharacterId.BULLET_BILL.getOpcode()),
		/** @deprecated Beta enemy, not intended for use. */
		@Deprecated
		@XmlEnumValue("GREEN_PARATROOPA_FIXED")
		GREEN_PARATROOPA_FIXED (CharacterId.GREEN_PARATROOPA_FIXED.getOpcode()),
		/** The straighter swimming Cheep-Cheep. */
		@XmlEnumValue("GREEN_CHEEP")
		GREEN_CHEEP (CharacterId.GREEN_CHEEP.getOpcode()),
		/** The ubiquitous fish, in its swimming guise. */
		@XmlEnumValue("RED_CHEEP")
		RED_CHEEP (CharacterId.RED_CHEEP.getOpcode()),
		/** The big ball of lava. Its X position sets its jump height. */
		@XmlEnumValue("PODOBOO")
		PODOBOO (CharacterId.PODOBOO.getOpcode()),
		/** @deprecated In the original game, Piranha Plants are spawned automatically in all pipes when the level is anything other than 1-1. */
		@Deprecated
		@XmlEnumValue("PIRANHA_PLANT")
		PIRANHA_PLANT (CharacterId.PIRANHA_PLANT.getOpcode()),
		/** A flying green KT that hops. */
		@XmlEnumValue("GREEN_PARATROOPA_HOP")
		GREEN_PARATROOPA_HOP (CharacterId.GREEN_PARATROOPA_HOP.getOpcode()),
		/** A flying red KT that flaps up and down. */
		@XmlEnumValue("RED_PARATROOPA")
		RED_PARATROOPA (CharacterId.RED_PARATROOPA.getOpcode()),
		/** A flying green KT that flaps back and forth in midair. */
		@XmlEnumValue("GREEN_PARATROOPA_HOVER")
		GREEN_PARATROOPA_HOVER (CharacterId.GREEN_PARATROOPA_HOVER.getOpcode()),
		/** The master of clouds and thrower of eggs. */
		@XmlEnumValue("LAKITU")
		LAKITU (CharacterId.LAKITU.getOpcode()),
		/** A pre-hatched Lakitu pet. */
		@XmlEnumValue("SPINY")
		SPINY (CharacterId.SPINY.getOpcode()),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		@XmlEnumValue("FLYING_CHEEP_GENERATOR")
		FLYING_CHEEP_GENERATOR (CharacterId.FLYING_CHEEP_GENERATOR.getOpcode()),
		/** Bowser's bursts of fiery breath. */
		@XmlEnumValue("BOWSER_BREATH_GENERATOR")
		BOWSER_BREATH_GENERATOR (CharacterId.BOWSER_BREATH_GENERATOR.getOpcode()),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		@XmlEnumValue("FIREWORKS")
		FIREWORKS (CharacterId.FIREWORKS.getOpcode()),
		/** @deprecated Spawned only by the game engine itself. */
		@Deprecated
		@XmlEnumValue("BILL_GENERATOR")
		BILL_GENERATOR (CharacterId.BILL_GENERATOR.getOpcode()),
		/** A short fire bar which moves slowly clockwise. */
		@XmlEnumValue("SLOW_FIRE_BAR_CLOCKWISE")
		SLOW_FIRE_BAR_CLOCKWISE (CharacterId.SLOW_FIRE_BAR_CLOCKWISE.getOpcode()),
		/** A short fire bar which moves quickly clockwise. */
		@XmlEnumValue("FAST_FIRE_BAR_CLOCKWISE")
		FAST_FIRE_BAR_CLOCKWISE (CharacterId.FAST_FIRE_BAR_CLOCKWISE.getOpcode()),
		/** A short fire bar which moves slowly anticlockwise. */
		@XmlEnumValue("SLOW_FIRE_BAR_ANTICLOCKWISE")
		SLOW_FIRE_BAR_ANTICLOCKWISE (CharacterId.SLOW_FIRE_BAR_ANTICLOCKWISE.getOpcode()),
		/** A short fire bar which moves quickly anticlockwise. */
		@XmlEnumValue("FAST_FIRE_BAR_ANTICLOCKWISE")
		FAST_FIRE_BAR_ANTICLOCKWISE (CharacterId.FAST_FIRE_BAR_ANTICLOCKWISE.getOpcode()),
		/** A long fire bar which moves clockwise. */
		@XmlEnumValue("LONG_FIRE_BAR_CLOCKWISE")
		LONG_FIRE_BAR_CLOCKWISE (CharacterId.LONG_FIRE_BAR_CLOCKWISE.getOpcode()),
		/**
		 * A lift consisting of two platforms that move like scales. The ropes are spawned separately.
		 * @see com.github.jimbovm.isobel.actor.geography.FixedExtensible
		 * @see com.github.jimbovm.isobel.actor.geography.ScaleRopeVertical
		 */
		@XmlEnumValue("SCALE_LIFT")
		SCALE_LIFT (CharacterId.SCALE_LIFT.getOpcode()),
		/** A lift which moves up and down. */
		@XmlEnumValue("LIFT_UP_AND_DOWN")
		LIFT_UP_AND_DOWN (CharacterId.LIFT_UP_AND_DOWN.getOpcode()),
		/** A lift which moves continuously up. */
		@XmlEnumValue("LIFT_UP")
		LIFT_UP (CharacterId.LIFT_UP.getOpcode()),
		/** A lift which moves continuously down. */
		@XmlEnumValue("LIFT_DOWN")
		LIFT_DOWN (CharacterId.LIFT_DOWN.getOpcode()),
		/** A lift which moves from side to side. */
		@XmlEnumValue("LIFT_SIDE_TO_SIDE")
		LIFT_SIDE_TO_SIDE (CharacterId.LIFT_SIDE_TO_SIDE.getOpcode()),
		/** A lift which falls when stepped on. */
		@XmlEnumValue("LIFT_FALL")
		LIFT_FALL (CharacterId.LIFT_FALL.getOpcode()),
		/** A lift which moves continuously right. */
		@XmlEnumValue("LIFT_RIGHT")
		LIFT_RIGHT (CharacterId.LIFT_RIGHT.getOpcode()),
		/** A shorter lift which moves continuously up. */
		@XmlEnumValue("SHORT_LIFT_UP")
		SHORT_LIFT_UP (CharacterId.SHORT_LIFT_UP.getOpcode()),
		/** A shorter lift which moves continuously down. */
		@XmlEnumValue("SHORT_LIFT_DOWN")
		SHORT_LIFT_DOWN (CharacterId.SHORT_LIFT_DOWN.getOpcode()),
		/** The king of evil himself. */
		@XmlEnumValue("BOWSER")
		BOWSER (CharacterId.BOWSER.getOpcode()),
		/** The actor which triggers warp zones. */
		@XmlEnumValue("WARP_ZONE")
		WARP_ZONE (CharacterId.WARP_ZONE.getOpcode()),
		/** A Mushroom Retainer or the Princess, depending on world. */
		@XmlEnumValue("TOAD_PEACH")
		TOAD_PEACH (CharacterId.TOAD_PEACH.getOpcode()),
		/** A squad of two Goombas at Y position 10. */
		@XmlEnumValue("GOOMBA_SQUAD_2_Y10")
		GOOMBA_SQUAD_2_Y10 (CharacterId.GOOMBA_SQUAD_2_Y10.getOpcode()),
		/** A squad of three Goombas at Y position 10. */
		@XmlEnumValue("GOOMBA_SQUAD_3_Y10")
		GOOMBA_SQUAD_3_Y10 (CharacterId.GOOMBA_SQUAD_3_Y10.getOpcode()),
		/** A squad of two Goombas at Y position 6. */
		@XmlEnumValue("GOOMBA_SQUAD_2_Y6")
		GOOMBA_SQUAD_2_Y6 (CharacterId.GOOMBA_SQUAD_2_Y6.getOpcode()),
		/** A squad of three Goombas at Y position 6. */
		@XmlEnumValue("GOOMBA_SQUAD_3_Y6")
		GOOMBA_SQUAD_3_Y6 (CharacterId.GOOMBA_SQUAD_3_Y6.getOpcode()),
		/** A squad of two Green Koopa Troopas at Y position 10. */
		@XmlEnumValue("TROOPA_SQUAD_2_Y10")
		TROOPA_SQUAD_2_Y10 (CharacterId.TROOPA_SQUAD_2_Y10.getOpcode()),
		/** A squad of three Green Koopa Troopas at Y position 10. */
		@XmlEnumValue("TROOPA_SQUAD_3_Y10")
		TROOPA_SQUAD_3_Y10 (CharacterId.TROOPA_SQUAD_3_Y10.getOpcode()),
		/** A squad of two Green Koopa Troopas at Y position 6. */
		@XmlEnumValue("TROOPA_SQUAD_2_Y6")
		TROOPA_SQUAD_2_Y6 (CharacterId.TROOPA_SQUAD_2_Y6.getOpcode()),
		/** A squad of three Green Koopa Troopas at Y position 10. */
		@XmlEnumValue("TROOPA_SQUAD_3_Y6")
		TROOPA_SQUAD_3_Y6 (CharacterId.TROOPA_SQUAD_3_Y6.getOpcode());

		private final int id;

		private static Map<Integer, Type> map = new HashMap<>();

		static {
			for (Type type : Type.values()) {
				map.put(type.id, type);
			}
		}
		
		/**
		 * Return an character value for a given ID.
		 * 
		 * @param opcode A numerical character ID.
		 * @return The character value associated with the supplied ID.
		 */
		public static Type from(int opcode) {
			return map.get(opcode);
		}
	}
	
	/** The Y position at which the character is spawned. */
	@XmlAttribute(name = "y")
	@Min(0) @Max(11) private int y;

	/** The type of character represented. */
	@XmlAttribute(name = "type")
	private Type type;

	/** Whether the character appears only when the game sets the hard mode flag.
	 *  If false, spawn the character in easy and hard mode. If true, spawn only 
	 *  in hard mode.
	 */
	@XmlAttribute(name = "hardModeOnly")
	private boolean hardModeOnly;

	/**
	 * Create a new <code>Character</code>bean. 
	 * 
	 * @param x The X position of the character.
	 * @param y The Y position of the character.
	 * @param type The type of character to spawn.
	 * @param hardModeOnly Whether to spawn the enemy only in hard mode.
	 * @return A new instance of <code>Character</code> with the given parameters.
	 */
	public static Character create(int x, int y, Type type, boolean hardModeOnly) {
		var character = new Character();
		character.setX(x);
		character.setY(y);
		character.setType(type);
		character.setHardModeOnly(hardModeOnly);
		return character;
	}

	/**
	 * Unparse the bean to its in-game bytecode equivalent.
	 * 
	 * @param newPage Whether to set the new page flag in the generated bytecode. 
	 */
	@Override
	public byte[] unparse(final boolean newPage, final Atlas atlas) {
		return PopulationCommand.unparse(this, newPage);
	}
}
