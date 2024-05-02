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

import com.github.jimbovm.isobel.bytecode.geography.ETypeCommand;
import com.github.jimbovm.isobel.common.AreaHeader.Fill;
import com.github.jimbovm.isobel.common.AreaHeader.Scenery;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This bean represents an instruction to modify the terrain fill and/or 
 * scenery of an area on the fly.
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "fillSceneryModifier")
public final class FillSceneryModifier extends GeographyActor {

	/**
	 * Create a new fill/scenery modifier bean. 
	 * 
	 * @param x The X position from which the actor is effective.
	 * @param fill The fill type to be rendered.
	 * @param scenery The scenery type to be rendered.
	 * @return A new instance of <code>FillSceneryModifier</code> with the given parameters.
	 */
	public static FillSceneryModifier create(final int x, Fill fill, Scenery scenery) {
		var modifier = new FillSceneryModifier();
		modifier.setX(x);
		modifier.setFill(fill);
		modifier.setScenery(scenery);
		return modifier;
	}
	
	@XmlAttribute(name = "fill")
	@NotNull private Fill fill = Fill.FILL_2BF_0BC;

	@XmlAttribute(name = "scenery")
	@NotNull private Scenery scenery = Scenery.NONE;

	/**
	 * Unparse the bean to its in-game bytecode equivalent.
	 * 
	 * @param newPage Whether to set the new page flag in the generated bytecode. 
	 */
	public byte[] unparse(final boolean newPage) {
		return ETypeCommand.unparse(this, newPage);
	}
}
