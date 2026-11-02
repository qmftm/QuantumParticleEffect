package com.qmftm.quantumparticleeffect.mixin;

import com.qmftm.quantumparticleeffect.EffectSprites;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	/** Lets effects with their own particle use the icon particle when the setting says so. */
	@ModifyArg(
		method = "tickEffects",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
		index = 0
	)
	private ParticleOptions quantumparticleeffect$replaceOwnParticle(ParticleOptions original) {
		return EffectSprites.replaceOwnParticle(original);
	}
}
