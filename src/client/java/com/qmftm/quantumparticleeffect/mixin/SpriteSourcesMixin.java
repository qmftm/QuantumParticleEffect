package com.qmftm.quantumparticleeffect.mixin;

import com.mojang.serialization.MapCodec;
import com.qmftm.quantumparticleeffect.EffectSilhouetteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Registers the {@code quantumparticleeffect:effect_silhouettes} atlas source type. */
@Mixin(SpriteSources.class)
public abstract class SpriteSourcesMixin {
	@Shadow
	@Final
	private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends SpriteSource>> ID_MAPPER;

	@Inject(method = "bootstrap", at = @At("TAIL"))
	private static void quantumparticleeffect$registerSource(CallbackInfo ci) {
		ID_MAPPER.put(EffectSilhouetteSource.TYPE, EffectSilhouetteSource.MAP_CODEC);
	}
}
