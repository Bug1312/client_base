package com.bug1312.client_base.core.config;

import java.io.FileReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.bug1312.client_base.api.ClientBaseRegistries;
import com.bug1312.client_base.api.config.type.InteractionType;
import com.bug1312.client_base.api.config.type.RendererType;
import com.bug1312.client_base.core.ClientBaseModCoreInitializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public record ClientBaseConfig(
	StructureConfig structureConfig,
	Optional<PlayerStartConfig> playerStartConfig,
	Map<String, RendererType> renderers,
	List<InteractionType> interactions,
	List<EasyPlaceConfig> easyPlaceList
) implements Serializable {
	static final Path CONFIG_PATH = Path.of("config/client_base.json");
	static final ClientBaseConfig DEFAULT = new ClientBaseConfig(new StructureConfig(StructureConfig.Deserializer.DEFAULT_STRUCTURE, BlockPos.ZERO), Optional.empty(), new HashMap<>(), Collections.emptyList(), Collections.emptyList());

	@Nullable
	static ClientBaseConfig instance;
	@Nullable
	public static ClientBaseConfig getInstance() { return instance; }

	public static void reload() {
		ClientBaseModCoreInitializer.LOGGER.debug("Config (re)loaded");

		if (Files.exists(CONFIG_PATH)) {
			try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
				instance = ClientBaseConfig.GSON.fromJson(reader, ClientBaseConfig.class);
			} catch (Exception err) {
				ClientBaseModCoreInitializer.LOGGER.error("There was an issue parsing Client Base's config", err);
			}
		}

		if (instance == null) instance = DEFAULT;
	}

	static final Gson GSON = new GsonBuilder().registerTypeAdapter(ClientBaseConfig.class, new Deserializer()).create();
	public static class Deserializer implements JsonDeserializer<ClientBaseConfig> {

		static final int VERSION = 1;
		public static Map<String, RendererType> lastDeserializedRenderers;

		@Override
		public ClientBaseConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			if (
				!obj.has("config_format")
				|| !obj.get("config_format").isJsonPrimitive()
				|| obj.get("config_format").getAsInt() != VERSION
			) ClientBaseModCoreInitializer.LOGGER.warn(String.format("Config version is not current (%d). There may be issues.", VERSION));

			StructureConfig structureConfig = StructureConfig.GSON.fromJson(obj.get("structure"), StructureConfig.class);

			Optional<PlayerStartConfig> playerStartConfig = Optional.empty();
			if (obj.has("player_start")) playerStartConfig = Optional.of(PlayerStartConfig.GSON.fromJson(obj.get("player_start"), PlayerStartConfig.class));

			Map<String, RendererType> renderers = new HashMap<>();
			if (obj.has("renderers")) {
				JsonObject renderersObj = obj.get("renderers").getAsJsonObject();
				for (var entry : renderersObj.entrySet()) {
					JsonObject rendererObj = entry.getValue().getAsJsonObject();
					String typeString = rendererObj.get("type").getAsString();
					if (!(ResourceLocation.tryParse(typeString) instanceof ResourceLocation typeId)) break;
					ClientBaseRegistries.RENDERER_TYPE.getOptional(typeId).ifPresent(registryEntry -> {
						Gson gson = new GsonBuilder().registerTypeAdapter(RendererType.class, registryEntry).create();
						renderers.put(entry.getKey(), gson.fromJson(rendererObj, RendererType.class));
					});
				};
			}
			lastDeserializedRenderers = renderers;

			List<InteractionType> interactions = new ArrayList<>();
			if (obj.has("interactions")) {
				JsonArray array = obj.get("interactions").getAsJsonArray();
				for (var entry : array.asList()) {
					JsonObject arrayObj = entry.getAsJsonObject();
					String typeString = arrayObj.get("type").getAsString();
					if (!(ResourceLocation.tryParse(typeString) instanceof ResourceLocation typeId)) break;
					ClientBaseRegistries.INTERACTION_TYPE.getOptional(typeId).ifPresent(registryEntry -> {
						Gson gson = new GsonBuilder().registerTypeAdapter(InteractionType.class, registryEntry).create();
						interactions.add(gson.fromJson(arrayObj, InteractionType.class));
					});
				}
			}

			List<EasyPlaceConfig> easyPlaceList = new ArrayList<>();
			if (obj.has("easy_place")) {
				JsonArray array = obj.get("easy_place").getAsJsonArray();
				for (var arrayObj : array.asList()) {
					EasyPlaceConfig out = EasyPlaceConfig.GSON.fromJson(arrayObj, EasyPlaceConfig.class);
					if (out != null) easyPlaceList.add(out);
				}
			}

			return new ClientBaseConfig(structureConfig, playerStartConfig, renderers, interactions, easyPlaceList);
		}
	}
}
