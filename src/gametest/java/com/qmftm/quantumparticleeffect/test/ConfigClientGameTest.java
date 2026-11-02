package com.qmftm.quantumparticleeffect.test;

import com.qmftm.quantumparticleeffect.QpeConfig;
import com.qmftm.quantumparticleeffect.QpeConfigScreen;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Checks the "keep special effect particles" setting with the six effects that have their own particle:
 * once with the setting on (the default), once with it off, and takes a screenshot of the settings screen.
 */
public class ConfigClientGameTest implements FabricClientGameTest {
	private static final List<String> SPECIAL = List.of("trial_omen", "raid_omen", "wind_charged", "oozing", "weaving", "infested");

	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(100);
			world.getServer().runCommand("time set noon");
			world.getServer().runCommand("gamerule advance_time false");
			world.getServer().runCommand("execute as @a run tp @s ~ ~ ~ 0 5");
			context.waitTicks(10);

			for (int i = 0; i < SPECIAL.size(); i++) {
				double left = 3.0 - i * 1.2;
				world.getServer().runCommand("execute as @a at @s run summon mannequin ^" + left + " ^ ^5 "
					+ "{immovable:1b,hide_description:1b,Invulnerable:1b,Rotation:[180f,0f],"
					+ "CustomName:\"" + SPECIAL.get(i) + "\",CustomNameVisible:1b,"
					+ "active_effects:[{id:\"minecraft:" + SPECIAL.get(i) + "\",duration:6000,amplifier:4}]}");
			}

			context.runOnClient(client -> QpeConfig.setKeepSpecialEffectParticles(true));
			context.waitTicks(120);
			context.takeScreenshot("config_keep_on");

			context.runOnClient(client -> QpeConfig.setKeepSpecialEffectParticles(false));
			context.waitTicks(120);
			context.takeScreenshot("config_keep_off");

			context.runOnClient(client -> client.gui.setScreen(new QpeConfigScreen(null)));
			context.waitTicks(10);
			context.takeScreenshot("config_screen");
			context.runOnClient(client -> client.gui.setScreen(null));
			context.runOnClient(client -> QpeConfig.setKeepSpecialEffectParticles(true));
		}
	}
}
