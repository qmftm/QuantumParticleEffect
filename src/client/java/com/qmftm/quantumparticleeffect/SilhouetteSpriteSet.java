package com.qmftm.quantumparticleeffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

/** The single silhouette sprite made from an effect's icon by {@link EffectSilhouetteSource}. */
final class SilhouetteSpriteSet implements SpriteSet {
	private final Identifier spriteId;

	SilhouetteSpriteSet(Identifier effectId) {
		this.spriteId = effectId.withPrefix(EffectSilhouetteSource.PREFIX);
	}

	/** False when the effect has no icon (the atlas would return its missing texture). */
	boolean exists() {
		TextureAtlas atlas = atlas();
		return atlas.getSprite(this.spriteId) != atlas.missingSprite();
	}

	@Override
	public TextureAtlasSprite get(int index, int max) {
		return first();
	}

	@Override
	public TextureAtlasSprite get(RandomSource random) {
		return first();
	}

	@Override
	public TextureAtlasSprite first() {
		return atlas().getSprite(this.spriteId);
	}

	private static TextureAtlas atlas() {
		return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.PARTICLES);
	}
}
