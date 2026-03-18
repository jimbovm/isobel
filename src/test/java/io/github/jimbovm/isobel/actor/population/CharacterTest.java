/*
 * SPDX-License-Identifier: MIT-0
 *
 * This file is part of Isobel (https://github.com/jimbovm/isobel).
 */

package io.github.jimbovm.isobel.actor.population;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.github.jimbovm.isobel.test.TestSuite;

public class CharacterTest extends TestSuite {

	@EnumSource(Character.Type.class)
	@ParameterizedTest
	void marshal(Character.Type type) throws JAXBException {

		Character character = new Character();
		character.setType(type);
		character.setX(64);
		character.setY(8);

		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(Character.class);
		Marshaller m = context.createMarshaller();
		m.marshal(character, writer);

		String xml = writer.toString();

		assertTrue(xml.contains("<character"));
		assertTrue(xml.contains("x=\"64\""));
		assertTrue(xml.contains("y=\"8\""));
		assertTrue(xml.contains(String.format("type=\"%s\"", type)));
	}

}
