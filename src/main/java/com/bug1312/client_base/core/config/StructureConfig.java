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

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public record StructureConfig(
	CompoundTag structure,
	BlockPos offset
) implements Serializable {
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(StructureConfig.class, new Deserializer()).create();
	public static class Deserializer implements JsonDeserializer<StructureConfig> {
		private static final FileToIdConverter FINDER = new FileToIdConverter("client_base/structures", ".nbt");
		public static final CompoundTag DEFAULT_STRUCTURE = new StructureTemplate().save(new CompoundTag());

		@Override
		public StructureConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			CompoundTag structure = DEFAULT_STRUCTURE;

			String file = obj.get("structure").getAsString();
			if (file.contains(".nbt")) {
				try {
					InputStream stream = new FileInputStream(file);
					structure = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
				} catch (IOException err) {
					ClientBaseModCoreInitializer.LOGGER.error(String.format("Structure file could not be found at path \"%s\"", file), err);
				}
			} else {
				if (ResourceLocation.tryParse(file) instanceof ResourceLocation id) {
					Optional<Resource> resourceOpt = Minecraft.getInstance().getResourceManager().getResource(FINDER.fileToId(id));
					if (resourceOpt.isPresent()) {
						Resource resource = resourceOpt.get();
						try {
							InputStream stream = resource.open();
							structure = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
						} catch (IOException err) {
							ClientBaseModCoreInitializer.LOGGER.error(String.format("Structure resource could not be found at \"%s\"", id), err);
						}
					}
				}
			}

			BlockPos offset = obj.has("offset") ? DeserializeUtil.toBlockPos(obj.get("offset")) : BlockPos.ZERO;

			return new StructureConfig(structure, offset);
		}
	}
}