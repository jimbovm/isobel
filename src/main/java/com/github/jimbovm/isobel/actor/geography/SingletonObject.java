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

package com.github.jimbovm.isobel.actor.geography;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
import com.github.jimbovm.isobel.actor.YPlaceable;
import com.github.jimbovm.isobel.bytecode.geography.NormalCommand;

/** 
 * This bean represents objects that are non-extensible singletons of
 * single-block width and spawned by "type 0" normal-type commands.
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "singletonObject")
public final class SingletonObject extends GeographyActor implements YPlaceable {

	/** The type of object to be spawned by the actor. */
	@XmlType(name = "singletonType")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public enum Type {
		/** A ? Block containing a Super Mushroom or Fire Flower,
		 * depending on the player's state. */
		QUESTION_BLOCK_POWERUP (NormalCommand.PrimeActorId.QUESTION_BLOCK_POWERUP.getId()),
		/** A ? Block containing a coin. */
		QUESTION_BLOCK_COIN (NormalCommand.PrimeActorId.QUESTION_BLOCK_COIN.getId()),
		/** An invisible block containing a coin. */
		HIDDEN_BLOCK_COIN (NormalCommand.PrimeActorId.HIDDEN_BLOCK_COIN.getId()),
		/** An invisible block containing a 1-Up Mushroom. */
		HIDDEN_BLOCK_1UP (NormalCommand.PrimeActorId.HIDDEN_BLOCK_1UP.getId()),
		/** A brick block containing a Super Mushroom or Fire Flower,
		 * depending on the player's state. */
		BRICK_POWERUP (NormalCommand.PrimeActorId.BRICK_POWERUP.getId()),
		/** A brick block containing a vine. */
		BRICK_VINE (NormalCommand.PrimeActorId.BRICK_VINE.getId()),
		/** A brick block containing a Starman. */
		BRICK_STARMAN (NormalCommand.PrimeActorId.BRICK_STARMAN.getId()),
		/** A multi-coin brick block. */
		BRICK_MULTI_COIN (NormalCommand.PrimeActorId.BRICK_MULTI_COIN.getId()),
		/** A brick block containing a 1-Up Mushroom. */
		BRICK_1UP (NormalCommand.PrimeActorId.BRICK_1UP.getId()),
		/** A "used" ? Block (often seen with fire bars in the original). */
		QUESTION_BLOCK_USED (NormalCommand.PrimeActorId.QUESTION_BLOCK_USED.getId()),
		/** A stubby sideways pipe, as seen in underwater areas in the original. */
		SIDEWAYS_PIPE (NormalCommand.PrimeActorId.SIDEWAYS_PIPE.getId()),
		/** The bouncy trampoline. */
		JUMPING_BOARD (NormalCommand.PrimeActorId.JUMPING_BOARD.getId());

		private final int id;
		
		private static Map<Integer, Type> map = new HashMap<>();

		static {
			for (Type type : Type.values())
				map.put(type.id, type);
		}

		/**
		 * Return a type value for a given ID.
		 * 
		 * @param id A numerical type ID.
		 * @return The type value associated with the supplied id.
		 */
		public static Type from(int id) { return map.get(id); }
	}

	@XmlAttribute(name = "y")
	@Min(0) @Max(0xB) private int y;
	
	@XmlAttribute(name = "type")
	@NotNull private SingletonObject.Type type;

	/**
	 * Create a new <code>SingletonObject</code> bean.
	 * 
	 * @param x The X position of the object.
	 * @param y The Y position of the object.
	 * @param type The type of object to spawn.
	 * @return A new singleton object bean with the supplied parameters.
	 */
	public static SingletonObject create(final int x, final int y, final Type type) {
		SingletonObject object = new SingletonObject();
		object.setX(x);
		object.setY(y);
		object.setType(type);
		return object;
	}

	/**
	 * Unparse the bean to its in-game bytecode equivalent.
	 * 
	 * @param newPage Whether to set the new page flag in the generated bytecode. 
	 */
	public byte[] unparse(final boolean newPage) {
		return NormalCommand.unparse(this, newPage);
	}
}
