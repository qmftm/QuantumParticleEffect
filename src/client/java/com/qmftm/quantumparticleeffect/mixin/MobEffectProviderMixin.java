package com.qmftm.quantumparticleeffect.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.qmftm.quantumparticleeffect.EffectSprites;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ColorParticleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Swaps in the effect's own textures when the particle's color matches a known effect. */
@Mixin(SpellParticle.MobEffectProvider.class)
public abstract class MobEffectProviderMixin {
	@ModifyArg(
		method = "createParticle(Lnet/minecraft/core/particles/ColorParticleOption;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/SpellParticle;<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/client/particle/SpriteSet;)V"
		),
		index = 7
	)
	private SpriteSet quantumparticleeffect$useEffectSprites(SpriteSet original, @Local(argsOnly = true) ColorParticleOption options) {
		EffectSprites.Match match = EffectSprites.forColor(color(options));
		return match != null ? match.sprites() : original;
	}

	@Inject(
		method = "createParticle(Lnet/minecraft/core/particles/ColorParticleOption;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;",
		at = @At("RETURN")
	)
	private void quantumparticleeffect$keepIconColors(CallbackInfoReturnable<Particle> cir, @Local(argsOnly = true) ColorParticleOption options) {
		// Effect icons are already drawn in the effect's colors, so they are not tinted again.
		EffectSprites.Match match = EffectSprites.forColor(color(options));
		if (match != null && !match.tint() && cir.getReturnValue() instanceof SingleQuadParticle particle) {
			particle.setColor(1.0F, 1.0F, 1.0F);
		}
	}

	private static int color(ColorParticleOption options) {
		return ((ColorParticleOptionAccessor) options).quantumparticleeffect$getColor();
	}
}
