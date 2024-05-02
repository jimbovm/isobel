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

import com.github.jimbovm.isobel.bytecode.geography.DTypeCommand;
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

/**
 * This bean represents the very miscellaneous set of immovable actors of fixed
 * extent and fixed Y position spawned by D-type commands.
  */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder={"x", "type"})
@XmlRootElement(name = "fixedStatic")
public final class FixedStatic extends GeographyActor {
	
	/**
	 * The types of object spawnable by the actor.
	 */
	@XmlType(name = "fixedStaticType")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public enum Type {
		/** The three-way pipe seen in the interlude area before 1-2 in
		 * the original. Increments the level counter internally without
		 * changing the value seen by the player when entered. */
		TEE_PIPE (DTypeCommand.ActorId.TEE_PIPE.getId()),
		/** The end-of-level flagpole. Increments the level counter. */
		FLAGPOLE (DTypeCommand.ActorId.FLAGPOLE.getId()),
		/** The axe seen in castle levels. Increments the world counter. */
		AXE (DTypeCommand.ActorId.AXE.getId()),
		/** The chain seen next to the axe and bridge in castle levels. */
		CHAIN (DTypeCommand.ActorId.CHAIN.getId()),
		/** The bridge on which all Bowser fights take place in the
		 * original. */
		BOWSER_BRIDGE (DTypeCommand.ActorId.BOWSER_BRIDGE.getId()),
		/** The scroll lock actor used with warp zones. */
		WARP_SCROLL_LOCK (DTypeCommand.ActorId.WARP_SCROLL_LOCK.getId()),
		/** The generic scroll lock actor, used in underground bonus
		 * rooms in the original. */
		SCROLL_LOCK (DTypeCommand.ActorId.SCROLL_LOCK.getId()),
		/** Spawns infinite flying red Cheep-Cheeps. */
		INFINITE_FLYING_CHEEP_GENERATOR (DTypeCommand.ActorId.INFINITE_FLYING_CHEEP_GENERATOR.getId()),
		/** Spawns infinite swimming Cheep-Cheeps underwater, and infinite Bullet Bills elsewhere. */
		INFINITE_BULLET_BILL_GENERATOR (DTypeCommand.ActorId.INFINITE_BULLET_BILL_GENERATOR.getId()),
		/** "Off switch" for infinite enemy generators, plus "back off" command for Lakitu. */
		STOP_INFINITE_GENERATOR (DTypeCommand.ActorId.STOP_INFINITE_GENERATOR.getId()),
		/** Loop the player back four pages, unless the game engine determines otherwise. */
		LOOP (DTypeCommand.ActorId.LOOP.getId());

		private final int id;

		private static Map<Integer, Type> map = new HashMap<>();

		static {
			for (Type type : Type.values())
				map.put(type.id, type);
		}
		
		/**
		 * Return an actor value for a given ID.
		 * 
		 * @param id A numerical actor ID.
		 * @return The actor value associated with the supplied ID.
		 */
		public static Type of(int id) { return map.get(id); }
	}

	/** The type of actor to spawn. */
	@XmlAttribute
	@NotNull private Type type;

	/**
	 * Create a new <code>FixedStatic</code> instance. 
	 * 
	 * @param x The X position of the actor.
	 * @param type The type of actor.
	 * @return A new instance of <code>FixedStatic</code> with the given parameters.
	 */
	public static FixedStatic create(int x, Type type) {
		var actor = new FixedStatic();
		actor.setX(x);
		actor.setType(type);
		return actor;
	}

	/**
	 * Create a new tee pipe (seen in the interlude area in the original).
	 * This actor increments the level counter internally without changing
	 * the level value shown to the player.
	 * 
	 * @param x The X position of the tee pipe.
	 * @return A new tee pipe instance. 
	 */
	public static FixedStatic createTeePipe(int x) {
		return FixedStatic.create(x, Type.TEE_PIPE);
	}

	/**
	 * Create a new flagpole.
	 * 
	 * @param x The X position of the flagpole.
	 * @return A new flagpole instance.
	 */
	public static FixedStatic createFlagpole(int x) {
		return FixedStatic.create(x, Type.FLAGPOLE);
	}

	/**
	 * Create a new axe.
	 * 
	 * @param x The X position of the axe.
	 * @return A new axe instance. 
	 */
	public static FixedStatic createAxe(int x) {
		return FixedStatic.create(x, Type.AXE);
	}

	/**
	 * Create a new chain.
	 * 
	 * @param x The X position of the chain.
	 * @return A new chain instance. 
	 */
	public static FixedStatic createChain(int x) {
		return FixedStatic.create(x, Type.CHAIN);
	}

	/**
	 * Create a new bridge on which Bowser fights normally take place.
	 * 
	 * @param x The X position of the bridge.
	 * @return A new bridge instance. 
	 */
	public static FixedStatic createBowserBridge(int x) {
		return FixedStatic.create(x, Type.BOWSER_BRIDGE);
	}

	/**
	 * Create a new instance of the warp zone scroll lock actor.
	 * 
	 * @param x The X position of the scroll lock.
	 * @return A new warp scroll lock instance. 
	 */
	public static FixedStatic createWarpScrollLock(int x) {
		return FixedStatic.create(x, Type.WARP_SCROLL_LOCK);
	}

	/**
	 * Create a new instance of the scroll lock actor.
	 * 
	 * @param x The X position of the scroll lock.
	 * @return A new scroll lock instance. 
	 */
	public static FixedStatic createScrollLock(int x) {
		return FixedStatic.create(x, Type.SCROLL_LOCK);
	}

	/**
	 * Create a new infinite flying Cheep-Cheep generator.
	 * 
	 * @param x The X position where the bombardment begins.
	 * @return A new generator instance. 
	 */
	public static FixedStatic createInfiniteFlyingCheepGenerator(int x) {
		return FixedStatic.create(x, Type.INFINITE_FLYING_CHEEP_GENERATOR);
	}

	/**
	 * Create a new infinite Bullet Bill generator, which works as an 
	 * infinite swimming Cheep-Cheep generator in underwater environments.
	 * 
	 * @param x The X position where the bombardment begins.
	 * @return A new generator instance.
	 */
	public static FixedStatic createInfiniteBulletBillGenerator(int x) {
		return FixedStatic.create(x, Type.INFINITE_BULLET_BILL_GENERATOR);
	}

	/**
	 * Create a new "stop generator" actor, which ends both infinite enemy
	 * generators and Lakitu's pursuit.
	 * 
	 * @param x The X position of the stop actor.
	 * @return A new "stop generator" instance. 
	 */
	public static FixedStatic createGeneratorStop(int x) {
		return FixedStatic.create(x, Type.STOP_INFINITE_GENERATOR);
	}

	/**
	 * Create a new loop actor. Unless cancelled by the player walking 
	 * on set blocks, sends the player back four pages with the level 
	 * rendering repeating from that point.
	 * 
	 * Please note that Isobel does not currently support user-defined 
	 * loop cancel switches. 
	 * 
	 * @param x The X position of the loop.
	 * @return A new loop actor instance.
	 */
	public static FixedStatic createLoop(int x) {
		return FixedStatic.create(x, Type.LOOP);
	}

	/**
	 * Unparse the bean to its in-game bytecode equivalent.
	 * 
	 * @param newPage Whether to set the new page flag in the generated bytecode. 
	 */
	@Override
	public byte[] unparse(boolean newPage) {
		return DTypeCommand.unparse(this, newPage);
	}
}
