package com.github.jimbovm.isobel.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.github.jimbovm.isobel.test.TestSuite;

import com.github.jimbovm.isobel.actor.geography.GeographyActor;
import com.github.jimbovm.isobel.actor.geography.SingletonObject;
import com.github.jimbovm.isobel.bytecode.geography.GeographyUnparser;

public class AreaTest extends TestSuite {

	@Test void skipOnePage() {

		final int Y = 10;

		var area = new Area();

		var geography = new ArrayList<GeographyActor>();

		// page 0
		geography.add(SingletonObject.create(0, Y, SingletonObject.Type.BRICK_POWERUP));
		geography.add(SingletonObject.create(1, Y, SingletonObject.Type.BRICK_POWERUP));
		// page 1
		geography.add(SingletonObject.create(16, Y, SingletonObject.Type.BRICK_POWERUP));
		// page 2
		geography.add(SingletonObject.create(32, Y, SingletonObject.Type.BRICK_POWERUP));

		area.setGeography(geography);

		GeographyUnparser unparser = new GeographyUnparser(area.getHeader());
		byte[] bytecode = unparser.unparse(area.getGeography());

		System.out.println("skipOnePage():");
		for (byte b : bytecode) {
			System.out.println(StringUtils.leftPad(Integer.toBinaryString(b & 0xFF), 8, '0'));
		}

		// header, four pairs of bytes, one EOF marker
		assertEquals(2 + 8 + 1, bytecode.length);

		// coordinates should all be 0
		assertEquals(0, bytecode[2 + 0] & 0b11110000);
		assertEquals(Y, bytecode[2 + 0] & 0b00001111);

		// new page flag should not be set for 2nd command
		assertEquals((byte) 0, bytecode[2 + 3] & (byte) 0b10000000);

		// new page flags should be set for 3rd and 4th commands
		assertEquals((byte) 0b10000000, bytecode[2 + 5] & (byte) 0b10000000);
		assertEquals((byte) 0b10000000, bytecode[2 + 7] & (byte) 0b10000000);

		// final byte should be EOF
		assertEquals((byte) 0xFD, bytecode[2 + 8]);
	}

	@Test void skipTwoPages() {

		final int Y = 10;

		var area = new Area();

		var geography = new ArrayList<GeographyActor>();

		// page 0
		geography.add(SingletonObject.create(0, Y, SingletonObject.Type.BRICK_POWERUP));
		geography.add(SingletonObject.create(1, Y, SingletonObject.Type.BRICK_POWERUP));
		// pages 1 and 2 are empty
		// page 3
		geography.add(SingletonObject.create(49, Y, SingletonObject.Type.BRICK_POWERUP));

		area.setGeography(geography);

		GeographyUnparser unparser = new GeographyUnparser(area.getHeader());
		byte[] bytecode = unparser.unparse(area.getGeography());

		System.out.println("skipTwoPages():");
		for (byte b : bytecode) {
			System.out.println(StringUtils.leftPad(Integer.toBinaryString(b & 0xFF), 8, '0'));
		}

		// three actors (6 bytes) one page skip (2 bytes), one EOF (1 byte), with header
		assertEquals(2 + 6 + 2 + 1, bytecode.length);

		// bytecode[6, 7] should be the D-type page skip command
		assertEquals(0xD, bytecode[2 + 4] & 0b00001111);

		// the low byte of the D-type command should point to page 3
		assertEquals(3, bytecode[2 + 5] & 0b00011111);
		
		// final byte should be EOF
		assertEquals((byte) 0xFD, bytecode[2 + 8]);
	}
}
