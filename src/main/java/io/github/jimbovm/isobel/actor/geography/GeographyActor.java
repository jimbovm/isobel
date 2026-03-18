/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.geography;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import io.github.jimbovm.isobel.actor.Actor;

/** Parent class for all geography actors. */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class GeographyActor implements Actor {

	/** The absolute X position of the actor. */
	@XmlAttribute(name = "x")
	@PositiveOrZero
	protected int x;

	/**
	 * Returns the command to spawn the actor in game bytecode format.
	 * 
	 * @param  newPage Whether to set the new page flag in the generated command.
	 * 
	 * @return         A byte array containing area geography bytecode.
	 */
	public abstract byte[] unparse(final boolean newPage);

}
