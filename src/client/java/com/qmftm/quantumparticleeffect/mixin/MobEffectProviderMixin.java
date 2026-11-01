package com.qmftm.quantumparticleeffect.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.qmftm.quantumparticleeffect.EffectSprites;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ColorParticleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Swaps in the effect's own textures when the particle's color matches a known effect.
 * The game still tints the particle with the effect color, so textures should be white/gray.
 */
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
		SpriteSet custom = EffectSprites.forColor(color(options));
		return custom != null ? custom : original;
	}

	private static int color(ColorParticleOption options) {
		return ((ColorParticleOptionAccessor) options).quantumparticleeffect$getColor();
	}
}
