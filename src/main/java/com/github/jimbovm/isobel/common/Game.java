package com.github.jimbovm.isobel.common;

import java.io.IOException;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.github.jimbovm.isobel.bytecode.game.GameParser;

/**
 * Encapsulates the complete mutable data of a SMB game in high-level form.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "game")
public final class Game {

	@XmlID
	@XmlAttribute(name = "id")
	private String id;
	
	/**
	 * The complete set of areas in the game, including starting,
	 * sub and interlude areas.
	 */
	@XmlElement(name = "atlas")
	private Atlas atlas;

	/**
	 * Definitions of the areas that will load in as each level W-L for a
	 * given W and L.
	 */
	@XmlElement(name = "scenario")
	private Scenario scenario;

	{
		this.scenario = new Scenario();
		this.atlas = new Atlas();
	}

	/**
	 * Parse a <code>Game</code> from a file. This file should be an
	 * exact binary image of the two game ROM chips, concatenated, 
	 * with no header.
	 * 
	 * @param path A filename from which to read.
	 * @return A <code>Game</code> parsed from the supplied file.
	 * @throws IOException In the event of a problem reading from the file.
	 */
	public static Game parse(String path) throws IOException {

		Game game = new Game();

		GameParser parser = GameParser.create(path);
		
		game.atlas = parser.parseAtlas();
		game.scenario = parser.parseScenario();

		return game;
	}
}
