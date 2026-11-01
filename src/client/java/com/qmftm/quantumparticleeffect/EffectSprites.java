package com.qmftm.quantumparticleeffect;

import com.qmftm.quantumparticleeffect.mixin.MutableSpriteSetAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import org.jspecify.annotations.Nullable;

/**
 * Picks the particle textures for each mob effect.
 *
 * 1. If a resource pack has {@code assets/<ns>/particles/effect/<name>.json}, its textures are used.
 * 2. Otherwise a small silhouette made from the effect's inventory icon is used
 *    (see {@link EffectSilhouetteSource}). Effects from other mods work too, as long as they ship an icon.
 * 3. Otherwise the vanilla particle is used.
 *
 * Both are tinted with the effect color, like the trial/raid omen particles.
 *
 * Only the effect's color is sent to clients, so the effect is found by color.
 */
public final class EffectSprites {
	private static final String MUTABLE_SPRITE_SET = "net.minecraft.client.particle.ParticleResources$MutableSpriteSet";

	/** Effect color (RGB) -> textures for that effect. */
	private static final Int2ObjectMap<Entry> BY_COLOR = new Int2ObjectOpenHashMap<>();
	private static final Map<Identifier, SpriteSet> BY_PARTICLE_ID = new HashMap<>();

	private record Entry(SpriteSet custom, SilhouetteSpriteSet silhouette) {
	}

	private EffectSprites() {
	}

	/** Adds a sprite set for every registered effect to the game's sprite set map. */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void register(Map spriteSets) {
		Constructor<?> constructor;
		try {
			constructor = Class.forName(MUTABLE_SPRITE_SET).getDeclaredConstructor();
			constructor.setAccessible(true);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Could not find " + MUTABLE_SPRITE_SET, e);
		}

		for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
			Identifier effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);
			if (effectId == null) {
				continue;
			}
			Identifier particleId = effectId.withPrefix("effect/");
			if (spriteSets.containsKey(particleId)) {
				continue;
			}

			SpriteSet set;
			try {
				set = (SpriteSet) constructor.newInstance();
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException("Could not create sprite set for " + effectId, e);
			}
			spriteSets.put(particleId, set);
			BY_PARTICLE_ID.put(particleId, set);
			// Two effects can share a color (e.g. instant_health and saturation); the first one wins.
			BY_COLOR.putIfAbsent(effect.getColor() & 0xFFFFFF, new Entry(set, new SilhouetteSpriteSet(effectId)));
		}
	}

	/** Called before a resource reload so effects whose texture file was removed go back to vanilla. */
	public static void clear() {
		for (SpriteSet set : BY_PARTICLE_ID.values()) {
			((MutableSpriteSetAccessor) set).quantumparticleeffect$setSprites(null);
		}
	}

	/** Returns the textures for this particle color, or null to use the vanilla particle. */
	public static @Nullable SpriteSet forColor(int argb) {
		Entry entry = BY_COLOR.get(argb & 0xFFFFFF);
		if (entry == null) {
			return null;
		}
		if (((MutableSpriteSetAccessor) entry.custom()).quantumparticleeffect$getSprites() != null) {
			return entry.custom();
		}
		if (entry.silhouette().exists()) {
			return entry.silhouette();
		}
		return null;
	}
}
