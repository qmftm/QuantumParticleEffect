package com.qmftm.quantumparticleeffect;

import net.fabricmc.api.ClientModInitializer;

public class QuantumParticleEffectClient implements ClientModInitializer {
	public static final String MOD_ID = "quantumparticleeffect";

	@Override
	public void onInitializeClient() {
		QpeConfig.load();
		// The rest happens in mixins; see EffectSprites.
	}
}
