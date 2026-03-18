/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.population;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import io.github.jimbovm.isobel.actor.Actor;
import io.github.jimbovm.isobel.common.Atlas;

/**
 * Parent class for all population actors.
 */
@Getter
@Setter
@ToString
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public abstract class PopulationActor implements Actor {

	/** The absolute X position of the actor. */
	@XmlAttribute(name = "x")
	@PositiveOrZero
	protected int x;

	/**
	 * Returns the command to spawn the actor in game bytecode format.
	 * 
	 * @param  newPage Whether to set the new page flag in the generated command.
	 * @param  atlas   An Atlas from which to look up area index numbers.
	 * 
	 * @return         A byte array containing area population bytecode.
	 */
	public abstract byte[] unparse(final boolean newPage, final Atlas atlas);
}
