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

package com.github.jimbovm.isobel.actor.population;

import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jimbovm.isobel.test.TestSuite;

public class CharacterTest extends TestSuite {
	
	@EnumSource(Character.Type.class)
	@ParameterizedTest void marshal(Character.Type type) throws JAXBException {

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
