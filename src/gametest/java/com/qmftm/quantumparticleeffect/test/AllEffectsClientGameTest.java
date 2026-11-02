package com.qmftm.quantumparticleeffect.test;

import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Shows the particles of (almost) every vanilla effect, eight mannequins per screenshot.
 * Instant effects are left out because they leave no particles on the mob.
 */
public class AllEffectsClientGameTest implements FabricClientGameTest {
	private static final List<List<String>> BATCHES = List.of(
		List.of("speed", "slowness", "haste", "mining_fatigue", "strength", "jump_boost", "nausea", "regeneration"),
		List.of("resistance", "fire_resistance", "water_breathing", "invisibility", "blindness", "night_vision", "hunger", "weakness"),
		List.of("poison", "wither", "health_boost", "absorption", "glowing", "levitation", "luck", "unluck"),
		List.of("slow_falling", "conduit_power", "dolphins_grace", "bad_omen", "hero_of_the_village", "darkness", "breath_of_the_nautilus", "trial_omen"),
		List.of("raid_omen", "wind_charged", "weaving", "oozing", "infested")
	);

	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(100);
			world.getServer().runCommand("time set noon");
			world.getServer().runCommand("gamerule advance_time false");
			world.getServer().runCommand("execute as @a run tp @s ~ ~ ~ 0 5");
			context.waitTicks(10);

			for (int batch = 0; batch < BATCHES.size(); batch++) {
				world.getServer().runCommand("kill @e[type=minecraft:mannequin]");
				List<String> effects = BATCHES.get(batch);
				for (int i = 0; i < effects.size(); i++) {
					double left = 4.2 - i * 1.2;
					world.getServer().runCommand("execute as @a at @s run summon mannequin ^" + left + " ^ ^5.5 "
						+ "{immovable:1b,hide_description:1b,Invulnerable:1b,Rotation:[180f,0f],"
						+ "CustomName:\"" + effects.get(i) + "\",CustomNameVisible:1b,"
						+ "active_effects:[{id:\"minecraft:" + effects.get(i) + "\",duration:6000,amplifier:4}]}");
				}
				context.waitTicks(100);
				context.takeScreenshot("all_effects_" + (batch + 1));
			}
		}
	}
}
