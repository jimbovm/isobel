package com.github.jimbovm.isobel.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.jimbovm.isobel.actor.geography.ExtensiblePlatform;
import com.github.jimbovm.isobel.asm.AssemblyFormat;

public class AtlasTest {

	Area underwater1;
	Area castle1;
	Area underwater2;
	Area overworld1;
	Area overworld2;
	Area underground1;

	Atlas atlas = new Atlas();

	@BeforeEach
	void setUpAreas() {
		underwater1 = new Area();
		underwater1.setId("Underwater_1");
		underwater1.setEnvironment(Area.Environment.UNDERWATER);
		castle1 = new Area();
		castle1.setId("Castle_1");
		castle1.setEnvironment(Area.Environment.CASTLE);
		underwater2 = new Area();
		underwater2.setId("Underwater_2");
		underwater2.setEnvironment(Area.Environment.UNDERWATER);
		overworld1 = new Area();
		overworld1.setId("Overworld_1");
		overworld1.setEnvironment(Area.Environment.OVERWORLD);
		overworld2 = new Area();
		overworld2.setId("Overworld_2_Mushroomy");
		overworld2.setEnvironment(Area.Environment.OVERWORLD);
		AreaHeader overworld2Header = overworld2.getHeader();
		overworld2Header.setBackground(AreaHeader.Background.MONOCHROME);
		overworld2Header.setScenery(AreaHeader.Scenery.HILLS);
		overworld2Header.setPlatform(AreaHeader.Platform.MUSHROOM);
		overworld2.getGeography().add(ExtensiblePlatform.create(8, 8, 6));
		underground1 = new Area();
		underground1.setId("Underground_1");
		underground1.setEnvironment(Area.Environment.UNDERGROUND);

		atlas.addAll(List.of(underwater1, underwater2, castle1, overworld1, overworld2, underground1));
	}

	@Test void testIndexNumbers() {

		assertEquals(0x00, atlas.getIndexByArea().get(underwater1));
		assertEquals(0x01, atlas.getIndexByArea().get(underwater2));
		assertEquals(0x20, atlas.getIndexByArea().get(overworld1));
		assertEquals(0x21, atlas.getIndexByArea().get(overworld2));
		assertEquals(0x40, atlas.getIndexByArea().get(underground1));
		assertEquals(0x60, atlas.getIndexByArea().get(castle1));
	}

	@Test void testCounts() {
		assertEquals(2, atlas.getAreaCounts().get(Area.Environment.UNDERWATER));
		assertEquals(2, atlas.getAreaCounts().get(Area.Environment.OVERWORLD));
		assertEquals(1, atlas.getAreaCounts().get(Area.Environment.UNDERGROUND));
		assertEquals(1, atlas.getAreaCounts().get(Area.Environment.CASTLE));
	}
	
	@Test void testAdd() {

		System.out.println(atlas.getIndexByArea());

		atlas.remove(overworld1);

		System.out.println(atlas.getIndexByArea());
	}

	@Test void testAssembly() {

		for (Area area : atlas.getAreas()) {
			System.out.println(area.getEnvironment());
		}

		System.out.println(AssemblyFormat.toAssembly(atlas));
	}
}
