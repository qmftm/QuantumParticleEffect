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
 * One sprite set per mob effect.
 *
 * For an effect like {@code minecraft:poison} we register the particle id
 * {@code minecraft:effect/poison}. The game then loads its textures from
 * {@code assets/minecraft/particles/effect/poison.json}, the same way it loads
 * vanilla particles. Effects without such a file keep the vanilla look.
 *
 * Only the effect's color is sent to clients, so the effect is found by color.
 */
public final class EffectSprites {
	private static final String MUTABLE_SPRITE_SET = "net.minecraft.client.particle.ParticleResources$MutableSpriteSet";

	/** Effect color (RGB) -> sprite set. */
	private static final Int2ObjectMap<SpriteSet> BY_COLOR = new Int2ObjectOpenHashMap<>();
	private static final Map<Identifier, SpriteSet> BY_PARTICLE_ID = new HashMap<>();

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
			BY_COLOR.putIfAbsent(effect.getColor() & 0xFFFFFF, set);
		}
	}

	/** Called before a resource reload so effects whose texture file was removed go back to vanilla. */
	public static void clear() {
		for (SpriteSet set : BY_PARTICLE_ID.values()) {
			((MutableSpriteSetAccessor) set).quantumparticleeffect$setSprites(null);
		}
	}

	/** Returns the custom sprite set for this particle color, or null to use the vanilla one. */
	public static @Nullable SpriteSet forColor(int argb) {
		SpriteSet set = BY_COLOR.get(argb & 0xFFFFFF);
		if (set == null || ((MutableSpriteSetAccessor) set).quantumparticleeffect$getSprites() == null) {
			return null;
		}
		return set;
	}
}
