/*
SPDX-License-Identifier: MIT-0

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
SOFTWARE.
*/

package com.github.jimbovm.isobel.actor.geography;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.PositiveOrZero;

import com.github.jimbovm.isobel.actor.Actor;

/** Parent class for all geography actors. */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class GeographyActor implements Actor {
	
	/** The absolute X position of the actor. */
	@XmlAttribute(name = "x")
	@PositiveOrZero protected int x;

	/**
	 * Returns the command to spawn the actor in game bytecode format.
	 * 
	 * @param newPage Whether to set the new page flag in the generated command.
	 * 
	 * @return A byte array containing area geography bytecode.
	 */
	public abstract byte[] unparse(final boolean newPage);
	
}
