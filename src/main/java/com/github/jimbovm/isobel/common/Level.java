package com.github.jimbovm.isobel.common;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A level within the game's scenario. This is a reference to the area
 * in which the player starts off when the world and level counter are
 * set to some value of W-L.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "level")
public final class Level {
	
	/** The starting area, at the beginning of which the player is initially placed. */
	@XmlIDREF
	@XmlAttribute(name = "startArea")
	private Area startArea;

	/** The page at which the player resumes if they lose a life after passing. */
	@XmlAttribute(name = "checkpoint")
	@PositiveOrZero byte checkpoint;
}
