package com.qmftm.quantumparticleeffect.test;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Puts four mannequins in a row in front of the player and saves a screenshot. From left to right:
 * trial omen (vanilla, for comparison), poison, regeneration and haste.
 * The last three should show silhouettes of their icons in the effect color.
 */
public class EffectParticleClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(100);
			world.getServer().runCommand("time set noon");
			world.getServer().runCommand("gamerule advance_time false");
			world.getServer().runCommand("execute as @a run tp @s ~ ~ ~ 0 5");
			context.waitTicks(10);
			summon(world, "2.25", "minecraft:trial_omen");
			summon(world, "0.75", "minecraft:poison");
			summon(world, "-0.75", "minecraft:regeneration");
			summon(world, "-2.25", "minecraft:haste");
			context.waitTicks(60);
			context.takeScreenshot("effect_particles");
		}
	}

	private static void summon(TestSingleplayerContext world, String left, String effect) {
		world.getServer().runCommand("execute as @a at @s run summon mannequin ^" + left + " ^ ^4 "
			+ "{immovable:1b,hide_description:1b,Invulnerable:1b,Rotation:[180f,0f],active_effects:[{id:\"" + effect + "\",duration:6000,amplifier:4}]}");
	}
}
