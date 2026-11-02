package com.qmftm.quantumparticleeffect.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.qmftm.quantumparticleeffect.EffectSprites;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Swaps in the effect's own textures when the particle's color matches a known effect. */
@Mixin(SpellParticle.MobEffectProvider.class)
public abstract class MobEffectProviderMixin {
	/** Each particle is drawn between 1x and 2x of the size the game gives it, picked at random. */
	private static final float MIN_SIZE_MULTIPLIER = 1.0F;
	private static final float MAX_SIZE_MULTIPLIER = 2.0F;

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

	@Inject(
		method = "createParticle(Lnet/minecraft/core/particles/ColorParticleOption;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;",
		at = @At("RETURN")
	)
	private void quantumparticleeffect$enlarge(
		CallbackInfoReturnable<Particle> cir,
		@Local(argsOnly = true) ColorParticleOption options,
		@Local(argsOnly = true) RandomSource random
	) {
		// The effect textures are 18x18, twice the size of the vanilla 9x9 effect particle, so draw them bigger.
		if (EffectSprites.forColor(color(options)) != null) {
			cir.getReturnValue().scale(MIN_SIZE_MULTIPLIER + random.nextFloat() * (MAX_SIZE_MULTIPLIER - MIN_SIZE_MULTIPLIER));
		}
	}

	private static int color(ColorParticleOption options) {
		return ((ColorParticleOptionAccessor) options).quantumparticleeffect$getColor();
	}
}
