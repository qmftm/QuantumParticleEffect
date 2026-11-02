package com.qmftm.quantumparticleeffect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

/** Settings, saved in {@code config/quantumparticleeffect.json}. Can be changed in Mod Menu. */
public final class QpeConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String KEEP_SPECIAL = "keep_special_effect_particles";

	/**
	 * When true (the default), effects that already have their own particle, like the trial and raid omens,
	 * keep it. When false they get the icon particle like every other effect.
	 */
	private static volatile boolean keepSpecialEffectParticles = true;

	private QpeConfig() {
	}

	public static boolean keepSpecialEffectParticles() {
		return keepSpecialEffectParticles;
	}

	public static void setKeepSpecialEffectParticles(boolean value) {
		keepSpecialEffectParticles = value;
	}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve(QuantumParticleEffectClient.MOD_ID + ".json");
	}

	public static void load() {
		Path file = file();
		if (!Files.exists(file)) {
			save();
			return;
		}
		try (Reader reader = Files.newBufferedReader(file)) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class);
			if (json != null && json.has(KEEP_SPECIAL)) {
				keepSpecialEffectParticles = json.get(KEEP_SPECIAL).getAsBoolean();
			}
		} catch (IOException | RuntimeException e) {
			LOGGER.warn("Could not read {}, using defaults", file, e);
		}
	}

	public static void save() {
		JsonObject json = new JsonObject();
		json.addProperty(KEEP_SPECIAL, keepSpecialEffectParticles);
		try (Writer writer = Files.newBufferedWriter(file())) {
			GSON.toJson(json, writer);
		} catch (IOException e) {
			LOGGER.warn("Could not save {}", file(), e);
		}
	}
}
