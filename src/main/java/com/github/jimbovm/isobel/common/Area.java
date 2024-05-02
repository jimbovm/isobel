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

package com.github.jimbovm.isobel.common;

import com.github.jimbovm.isobel.actor.geography.AnglePipe;
import com.github.jimbovm.isobel.actor.geography.BackgroundModifier;
import com.github.jimbovm.isobel.actor.geography.Castle;
import com.github.jimbovm.isobel.actor.geography.Column;
import com.github.jimbovm.isobel.actor.geography.ExtensiblePlatform;
import com.github.jimbovm.isobel.actor.geography.FillSceneryModifier;
import com.github.jimbovm.isobel.actor.geography.FixedExtensible;
import com.github.jimbovm.isobel.actor.geography.FixedStatic;
import com.github.jimbovm.isobel.actor.geography.FullHeightRope;
import com.github.jimbovm.isobel.actor.geography.GeographyActor;
import com.github.jimbovm.isobel.actor.geography.Row;
import com.github.jimbovm.isobel.actor.geography.ScaleRopeVertical;
import com.github.jimbovm.isobel.actor.geography.SingletonObject;
import com.github.jimbovm.isobel.actor.geography.Staircase;
import com.github.jimbovm.isobel.actor.geography.UprightPipe;
import com.github.jimbovm.isobel.actor.population.Character;
import com.github.jimbovm.isobel.actor.population.ExitPointer;
import com.github.jimbovm.isobel.actor.population.PopulationActor;
import com.github.jimbovm.isobel.bytecode.geography.GeographyParser;
import com.github.jimbovm.isobel.bytecode.geography.GeographyUnparser;
import com.github.jimbovm.isobel.bytecode.population.PopulationParser;
import com.github.jimbovm.isobel.bytecode.population.PopulationUnparser;
import com.github.jimbovm.isobel.common.AreaHeader.Background;
import com.github.jimbovm.isobel.common.AreaHeader.Fill;
import com.github.jimbovm.isobel.common.AreaHeader.Platform;
import com.github.jimbovm.isobel.common.AreaHeader.Scenery;
import com.github.jimbovm.isobel.common.AreaHeader.StartPosition;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 
 * Representation of a discrete playable area within a game, be it a level's
 * main area, a bonus area or otherwise. For the game to progress, an area must
 * end with a flagpole or axe, or have a functioning exit to another area.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "area")
@XmlType(propOrder={"header", "geography", "population"})
public final class Area implements Comparable<Area> {

	/**
	 * The environment type of an area. Changing the environment
	 * changes the appearance, music and behaviour of some actors.
	 */
	@XmlType(name = "environment")
	@XmlEnum
	@Getter
	@AllArgsConstructor
	public enum Environment {
		/** Underwater, as e.g. world 2-2 in the original. */
		UNDERWATER (0),
		/** Outside, as e.g. 1-1 in the original.  */
		OVERWORLD (1),
		/** Underground, as e.g. 1-2 in the original. */
		UNDERGROUND (2),
		/** Inside a castle, as every W-4 level in the original. */
		CASTLE (3);

		private final int id;
	}
	
	/** The environment type of the area. */
	@XmlAttribute(name = "environment")
	private Environment environment;

	/** The area's geography actors. */
	@XmlElementWrapper(name = "geography")
	@XmlElements({
		@XmlElement(name = "anglePipe", type=AnglePipe.class),
		@XmlElement(name = "backgroundModifier", type=BackgroundModifier.class),
		@XmlElement(name = "castle", type=Castle.class),
		@XmlElement(name = "column", type=Column.class),
		@XmlElement(name = "extensiblePlatform", type=ExtensiblePlatform.class),
		@XmlElement(name = "fillSceneryModifier", type=FillSceneryModifier.class),
		@XmlElement(name = "fixedExtensible", type=FixedExtensible.class),
		@XmlElement(name = "fixedStatic", type=FixedStatic.class),
		@XmlElement(name = "fullHeightRope", type=FullHeightRope.class),
		@XmlElement(name = "row", type=Row.class),
		@XmlElement(name = "scaleRopeVertical", type=ScaleRopeVertical.class),
		@XmlElement(name = "singletonObject", type=SingletonObject.class),
		@XmlElement(name = "staircase", type=Staircase.class),
		@XmlElement(name = "uprightPipe", type=UprightPipe.class)
	})
	private List<GeographyActor> geography;

	/** The area's population actors. */
	@XmlElementWrapper(name = "population")
	@XmlElements({
		@XmlElement(name = "character", type=Character.class),
		@XmlElement(name = "exitPointer", type=ExitPointer.class)
	})
	private List<PopulationActor> population;

	/** The immutable internal name of the area. */
	@XmlID
	@XmlAttribute(name = "id")
	private String id;

	/** The human-readable name of the area. */
	@XmlAttribute(name = "familiarName")
	private String familiarName;

	/** Representation of the area header, which specifies "default settings." */
	@XmlElement(name = "header")
	private AreaHeader header;

	{
		this.geography = new ArrayList<>();
		this.population = new ArrayList<>();
	}
	
	/**
	 * Create a new Area object with default settings: overworld environment, 
	 * no autowalk, no background, two-block floor and no ceiling terrain fill,
	 * no scenery, extensible platforms as trees, 400 ticks on the timer, bottom 
	 * start position and geography of a basic staircase and flagpole "end zone".
	 */
	public Area() {

		this.setEnvironment(Environment.OVERWORLD);

		this.familiarName = "New Area " + LocalDateTime.now().toString();

		this.id = UUID.randomUUID().toString().replace('-', '_');

		var header = AreaHeader.builder()
			.autowalk(false)
			.background(Background.NONE)
			.fill(Fill.FILL_2BF_0BC)
			.platform(Platform.TREE)
			.scenery(Scenery.NONE)
			.startPosition(StartPosition.BOTTOM)
			.ticks(400)
			.build();
			
		this.setHeader(header);

		this.setGeography(new ArrayList<>(List.of(
			UprightPipe.create(3, 9, 1, false),
			Staircase.create(5, 8),
			FixedStatic.createFlagpole(21),
			Castle.create(25, Castle.Size.SMALL))));
	}

	/**
	 * Parse a new Area object from bytecode.
	 * 
	 * @param environment The environment type of the area returned.
	 * @param geography An array of geograpy bytecode.
	 * @param population An array of population bytecode.
	 * @param immutableName The immutable name to give the area returned.
	 * @return An area constructed from the supplied bytecode.
	 */
	public static Area parse(Environment environment, byte[] geography, byte[] population, String immutableName) {
		Area area = new Area();
		area.setId(immutableName);
		area.setFamiliarName(immutableName);

		var areaHeader = AreaHeader.parse(geography[0], geography[1]);
		area.setHeader(areaHeader);

		area.setEnvironment(environment);

		GeographyParser geographyParser = new GeographyParser(new ByteArrayInputStream(geography));
		PopulationParser populationParser = new PopulationParser(new ByteArrayInputStream(population));
		
		try {
			area.setGeography(geographyParser.parse());
			area.setPopulation(populationParser.parse());
		}
		catch (IOException e) {
			// This is deeply unlikely given the type of stream we are reading
			e.printStackTrace();
		}

		return area;
	}

	/**
	 * Unparse the area's list of geography beans to bytecode.
	 * 
	 * @return An array of geography bytecode.
	 */
	public byte[] unparseGeography() {
		GeographyUnparser geographyUnparser = new GeographyUnparser(this.header);
		return geographyUnparser.unparse(this.getGeography());
	}

	/**
	 * Unparse the area's list of population beans to bytecode.
	 * 
	 * @param atlas An {@link Atlas} of all areas in a game, required to resolve
	 * destinations of exit pointers. 
	 * @return An array of population bytecode.
	 */
	public byte[] unparsePopulation(Atlas atlas) {
		PopulationUnparser populationUnparser = new PopulationUnparser(atlas);
		return populationUnparser.unparse(this.getPopulation());
	}

	/**
	 * Compare an area to another area for ordering purposes, based on how area is
	 * ordered in the original game, that is, by environment type.
	 * 
	 * @param other Another <code>Area</code>.
	 * @return 0 if the two areas have the same environment, -1 if the environment ID
	 * of this object is less than the environment ID of the other area, and 1 otherwise.
	 * 
	 * @see com.github.jimbovm.isobel.common.Area.Environment
	 */
	@Override
	public int compareTo(Area other) {
		if (this.environment == other.getEnvironment()) {
			return 0;
		}
		else if (this.environment.getId() < other.getEnvironment().getId()) {
			return -1;
		}
		else {
			return 1;
		}
	}
}
