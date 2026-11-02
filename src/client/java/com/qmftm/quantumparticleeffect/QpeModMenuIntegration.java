package com.qmftm.quantumparticleeffect;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Adds the settings button to this mod's page in Mod Menu. Only used when Mod Menu is installed. */
public class QpeModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return QpeConfigScreen::new;
	}
}
