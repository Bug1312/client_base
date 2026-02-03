package com.bug1312.client_base.core.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Optional;

import com.bug1312.client_base.core.ClientBaseModCoreInitializer;
import com.bug1312.client_base.core.util.DeserializeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record StructureConfig(
	NbtCompound structure,
	BlockPos offset
) implements Serializable {
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(StructureConfig.class, new Deserializer()).create();
	public static class Deserializer implements JsonDeserializer<StructureConfig> {
		private static final ResourceFinder FINDER = new ResourceFinder("client_base/structures", ".nbt");
		public static final NbtCompound DEFAULT_STRUCTURE = new StructureTemplate().writeNbt(new NbtCompound());

		@Override
		public StructureConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			NbtCompound structure = DEFAULT_STRUCTURE;

			String file = obj.get("structure").getAsString();
			if (file.contains(".nbt")) {
				try {
					InputStream stream = new FileInputStream(file);
					structure = NbtIo.readCompressed(stream, NbtSizeTracker.ofUnlimitedBytes());
				} catch (IOException err) {
					ClientBaseModCoreInitializer.LOGGER.error(String.format("Structure file could not be found at path \"%s\"", file), err);
				}
			} else {
				if (Identifier.tryParse(file) instanceof Identifier id) {
					Optional<Resource> resourceOpt = MinecraftClient.getInstance().getResourceManager().getResource(FINDER.toResourceId(id));
					if (resourceOpt.isPresent()) {
						Resource resource = resourceOpt.get();
						try {
							InputStream stream = resource.getInputStream();
							structure = NbtIo.readCompressed(stream, NbtSizeTracker.ofUnlimitedBytes());
						} catch (IOException err) {
							ClientBaseModCoreInitializer.LOGGER.error(String.format("Structure resource could not be found at \"%s\"", id), err);
						}
					}
				}
			}

			BlockPos offset = obj.has("offset") ? DeserializeUtil.toBlockPos(obj.get("offset")) : BlockPos.ORIGIN;

			return new StructureConfig(structure, offset);
		}
	}
}