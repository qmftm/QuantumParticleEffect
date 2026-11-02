package com.qmftm.quantumparticleeffect;

import com.qmftm.quantumparticleeffect.mixin.MutableSpriteSetAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jspecify.annotations.Nullable;

/**
 * Picks the particle textures for each mob effect.
 *
 * 1. If a resource pack has {@code assets/<ns>/particles/effect/<name>.json}, its textures are used.
 * 2. Otherwise a small silhouette made from the effect's inventory icon is used
 *    (see {@link EffectSilhouetteSource}). Effects from other mods work too, as long as they ship an icon.
 * 3. Otherwise the vanilla particle is used.
 *
 * Effects that already have their own particle (trial omen, raid omen, ...) keep it unless the
 * "keep special particles" setting is turned off, see {@link QpeConfig}.
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

	/** Same as {@link #BY_COLOR}, for effects that have their own particle (trial omen, raid omen, ...). */
	private static final Int2ObjectMap<Entry> SPECIAL_BY_COLOR = new Int2ObjectOpenHashMap<>();
	/** Own particle of those effects -> effect color, so it can be swapped for the icon particle. */
	private static final Map<ParticleOptions, Integer> SPECIAL_PARTICLES = new HashMap<>();

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
			Entry entry = new Entry(set, new SilhouetteSpriteSet(effectId));
			int color = effect.getColor() & 0xFFFFFF;
			ParticleOptions own = ownParticle(effect);
			if (own != null) {
				SPECIAL_BY_COLOR.putIfAbsent(color, entry);
				SPECIAL_PARTICLES.putIfAbsent(own, color);
			} else {
				// Two effects can share a color (e.g. instant_health and saturation); the first one wins.
				BY_COLOR.putIfAbsent(color, entry);
			}
		}
	}

	/** The effect's own particle, or null when it uses the normal tinted effect particle. */
	private static @Nullable ParticleOptions ownParticle(MobEffect effect) {
		try {
			ParticleOptions options = effect.createParticleOptions(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)));
			boolean normal = options instanceof ColorParticleOption color && color.getType() == ParticleTypes.ENTITY_EFFECT;
			return normal ? null : options;
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * When the "keep special particles" setting is off, returns the normal effect particle (in the effect's color)
	 * to show instead of {@code original}. Otherwise returns {@code original}.
	 */
	public static ParticleOptions replaceOwnParticle(ParticleOptions original) {
		if (QpeConfig.keepSpecialEffectParticles()) {
			return original;
		}
		Integer color = SPECIAL_PARTICLES.get(original);
		return color == null ? original : ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.color(255, color));
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
		if (entry == null && !QpeConfig.keepSpecialEffectParticles()) {
			entry = SPECIAL_BY_COLOR.get(argb & 0xFFFFFF);
		}
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
