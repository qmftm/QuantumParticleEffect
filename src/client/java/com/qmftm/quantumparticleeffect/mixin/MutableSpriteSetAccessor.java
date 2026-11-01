package com.qmftm.quantumparticleeffect.mixin;

import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.particle.ParticleResources$MutableSpriteSet")
public interface MutableSpriteSetAccessor {
	@Accessor("sprites")
	List<TextureAtlasSprite> quantumparticleeffect$getSprites();

	@Accessor("sprites")
	void quantumparticleeffect$setSprites(List<TextureAtlasSprite> sprites);
}
