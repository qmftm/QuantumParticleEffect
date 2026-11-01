package com.qmftm.quantumparticleeffect.mixin;

import com.qmftm.quantumparticleeffect.EffectSprites;
import java.util.Map;
import net.minecraft.client.particle.ParticleResources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleResources.class)
public abstract class ParticleResourcesMixin {
	@SuppressWarnings("rawtypes")
	@Shadow
	@Final
	private Map spriteSets;

	@Inject(method = "registerProviders", at = @At("TAIL"))
	private void quantumparticleeffect$registerEffectSprites(CallbackInfo ci) {
		EffectSprites.register(this.spriteSets);
	}

	@Inject(method = "reload", at = @At("HEAD"))
	private void quantumparticleeffect$clearEffectSprites(CallbackInfoReturnable<?> cir) {
		EffectSprites.clear();
	}
}
