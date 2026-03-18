/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.common;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the order of levels in the game and the areas which are loaded as
 * a level's starting area.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "scenario")
public final class Scenario {

	{
		this.worlds = new ArrayList<World>();
	}

	/** The 8 worlds that make up the overall scenario. */
	@XmlElement(name = "world")
	private List<World> worlds;
}
