package com.qmftm.quantumparticleeffect.test;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Puts three mobs in front of the player and saves a screenshot.
 * Poison (left), regeneration (middle) and haste (right) should show their inventory icons.
 */
public class EffectParticleClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(100);
			world.getServer().runCommand("time set noon");
			world.getServer().runCommand("gamerule advance_time false");
			world.getServer().runCommand("execute as @a run tp @s ~ ~ ~ 0 10");
			context.waitTicks(10);
			summon(world, "1.5", "minecraft:poison");
			summon(world, "0", "minecraft:regeneration");
			summon(world, "-1.5", "minecraft:haste");
			context.waitTicks(60);
			context.takeScreenshot("effect_particles");
		}
	}

	private static void summon(TestSingleplayerContext world, String left, String effect) {
		world.getServer().runCommand("execute as @a at @s run summon pig ^" + left + " ^ ^4 "
			+ "{NoAI:1b,Silent:1b,Invulnerable:1b,Rotation:[180f,0f],active_effects:[{id:\"" + effect + "\",duration:6000,amplifier:4}]}");
	}
}
