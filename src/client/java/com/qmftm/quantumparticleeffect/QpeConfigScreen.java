package com.qmftm.quantumparticleeffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

/** The settings screen, opened from Mod Menu. */
public class QpeConfigScreen extends OptionsSubScreen {
	public QpeConfigScreen(Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, Component.translatable("quantumparticleeffect.config.title"));
	}

	@Override
	protected void addOptions() {
		this.list.addBig(OptionInstance.createBoolean(
			"quantumparticleeffect.config.keep_special_particles",
			value -> Tooltip.create(Component.translatable("quantumparticleeffect.config.keep_special_particles.tooltip")),
			QpeConfig.keepSpecialEffectParticles(),
			value -> {
				QpeConfig.setKeepSpecialEffectParticles(value);
				QpeConfig.save();
			}
		));
	}
}
