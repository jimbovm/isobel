package com.github.jimbovm.isobel.common;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a world within the game's scenario, consisting of four 
 * levels with the fourth having an axe and a Mushroom Retainer/Princess 
 * object at its end.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "world")
public final class World {

	/**
	 * The levels within this world.
	 */
	@XmlElement(name = "level")
	private List<Level> levels;

	/** The minimum number of coins a player must have collected
	 *  in the third level of the previous world for hidden blocks 
	 *  containing 1-Ups to be spawned in the first level of the 
	 *  current world (if arriving there by means other than the 
	 *  title screen or a warp zone).
	 */
	@XmlAttribute
	@PositiveOrZero
	private byte hidden1upCost;

	{
		this.levels = new ArrayList<Level>();
		this.hidden1upCost = 0;
	}
}
