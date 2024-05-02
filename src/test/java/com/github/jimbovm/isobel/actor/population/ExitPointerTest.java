package com.github.jimbovm.isobel.actor.population;

import org.junit.jupiter.api.Test;

import com.github.jimbovm.isobel.common.Area;
import com.github.jimbovm.isobel.common.Atlas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class ExitPointerTest {

	@Test void testExitPointer() {

		Atlas atlas = new Atlas();
		
		Area underwater1 = new Area();
		underwater1.setEnvironment(Area.Environment.UNDERWATER);
		underwater1.setId("Underwater_1");
		Area underwater2 = new Area();
		underwater2.setId("Underwater_2");
		underwater2.setEnvironment(Area.Environment.UNDERWATER);
		Area overworld1 = new Area();
		overworld1.setEnvironment(Area.Environment.OVERWORLD);
		overworld1.setId("Overworld_1");

		atlas.addAll(List.of(underwater1, underwater2, overworld1));

		ExitPointer pointer = new ExitPointer();
		pointer.setX(0);
		pointer.setStartPage(4);
		pointer.setActiveFromWorld(4);
		pointer.setDestination("Underwater_2");

		System.out.println("Areas: " + atlas.getAreas());

		byte[] bytecode = pointer.unparse(false, atlas);

		assertEquals((byte) 0b0000_1110, bytecode[0]);
		assertEquals((byte) 0b0000_0001, bytecode[1]);
		assertEquals((byte) 0b1000_0100, bytecode[2]);
	}
}
