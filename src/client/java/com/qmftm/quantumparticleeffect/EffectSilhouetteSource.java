package com.qmftm.quantumparticleeffect;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/**
 * Atlas sprite source that turns every effect inventory icon ({@code textures/mob_effect/<name>.png},
 * from vanilla or any mod) into a pixel silhouette at the icon's own resolution, like the trial/raid omen particles.
 *
 * The result uses four shades of gray (from the icon's brightness) so the game can tint it with the effect color.
 * Sprite id: {@code <ns>:effect_silhouette/<name>}.
 */
public final class EffectSilhouetteSource implements SpriteSource {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Identifier TYPE = Identifier.fromNamespaceAndPath(QuantumParticleEffectClient.MOD_ID, "effect_silhouettes");
	public static final MapCodec<EffectSilhouetteSource> MAP_CODEC = MapCodec.unit(EffectSilhouetteSource::new);
	public static final String PREFIX = "effect_silhouette/";

	/** Gray shades from darkest to brightest. Each gets about a quarter of the filled pixels. */
	private static final int[] SHADES = {
		ARGB.color(255, 150, 150, 150),
		ARGB.color(255, 190, 190, 190),
		ARGB.color(255, 225, 225, 225),
		ARGB.color(255, 255, 255, 255)
	};

	private static final FileToIdConverter ICONS = new FileToIdConverter("textures/mob_effect", ".png");

	@Override
	public void run(ResourceManager resourceManager, SpriteSource.Output output) {
		ICONS.listMatchingResources(resourceManager).forEach((file, resource) -> {
			Identifier id = ICONS.fileToId(file).withPrefix(PREFIX);
			output.add(id, loader -> load(id, resource));
		});
	}

	@Override
	public MapCodec<EffectSilhouetteSource> codec() {
		return MAP_CODEC;
	}

	private static @Nullable SpriteContents load(Identifier id, Resource resource) {
		try (InputStream in = resource.open(); NativeImage icon = NativeImage.read(in)) {
			NativeImage out = silhouette(icon);
			return new SpriteContents(id, new FrameSize(out.getWidth(), out.getHeight()), out);
		} catch (IOException e) {
			LOGGER.warn("Could not make effect particle from {}", id, e);
			return null;
		}
	}

	static NativeImage silhouette(NativeImage icon) {
		int width = icon.getWidth();
		// Animated icons are a vertical strip of square frames; use the first one.
		int height = Math.min(icon.getHeight(), width);

		NativeImage out = new NativeImage(width, height, true);
		float[] brightness = new float[width * height];
		Arrays.fill(brightness, -1.0F);
		List<Float> filled = new ArrayList<>();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int c = icon.getPixel(x, y);
				if (ARGB.alpha(c) > 127) {
					float value = 0.299F * ARGB.red(c) + 0.587F * ARGB.green(c) + 0.114F * ARGB.blue(c);
					brightness[x + y * width] = value;
					filled.add(value);
				}
			}
		}

		if (filled.isEmpty()) {
			return out;
		}
		filled.sort(null);
		float[] cuts = new float[SHADES.length - 1];
		for (int k = 0; k < cuts.length; k++) {
			cuts[k] = filled.get(filled.size() * (k + 1) / SHADES.length);
		}
		for (int i = 0; i < brightness.length; i++) {
			if (brightness[i] >= 0) {
				int shade = 0;
				for (float cut : cuts) {
					if (brightness[i] >= cut) {
						shade++;
					}
				}
				out.setPixel(i % width, i / width, SHADES[shade]);
			}
		}
		return out;
	}

}
