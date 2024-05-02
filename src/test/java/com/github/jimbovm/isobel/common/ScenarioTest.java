package com.github.jimbovm.isobel.common;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.github.jimbovm.isobel.asm.AssemblyFormat;
import com.github.jimbovm.isobel.common.Area.Environment;

@TestInstance(Lifecycle.PER_CLASS)
public class ScenarioTest {
	
	Scenario scenario;
	Atlas atlas;

	Area overworld1;
	Area underground1;
	Area overworld2;
	Area castle1;

	@BeforeAll void setup() {
		overworld1 = new Area();
		overworld2 = new Area();
		underground1 = new Area();
		castle1 = new Area();
		castle1.setEnvironment(Environment.CASTLE);
		underground1.setEnvironment(Environment.UNDERGROUND);

		atlas = new Atlas();
		atlas.add(overworld1);
		atlas.add(overworld2);
		atlas.add(underground1);
		atlas.add(castle1);

		scenario = new Scenario();

		byte[] prices = {10, 20, 30, 40, 50, 60, 70, 80};

		for (int i = 0; i < 8; i++) {
			World world = new World();
			Level level1 = new Level(overworld1, (byte) 5);
			Level level2 = new Level(underground1, (byte) 6);
			Level level3 = new Level(overworld2, (byte) 7);
			Level level4 = new Level(castle1, (byte) 8);
			world.getLevels().add(level1);
			world.getLevels().add(level2);
			world.getLevels().add(level3);
			world.getLevels().add(level4);
			world.setHidden1upCost(prices[i]);
			scenario.getWorlds().add(i, world);
		}
	}

	@Test void checkAssembly() {
		Map<String,String> asm = AssemblyFormat.toAssembly(scenario, atlas);

		System.out.println(asm);

		assertTrue(asm.get("checkpoints").contains(".byte $56, $78, $56, $78,"));
		assertTrue(asm.get("costs").contains(".byte $0a, $14, $1e, $28, $32, $3c, $46, $50"));
		assertTrue(asm.get("scenario").contains("World1Areas:\n    .byte $20, $40, $21, $60"));
	}
}
