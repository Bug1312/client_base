package com.bug1312.client_base.core.config;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.bug1312.client_base.api.config.type.EasyPlaceable;
import com.bug1312.client_base.api.config.type.RendererType;
import com.bug1312.client_base.base.config.renderer.BlockEntityRenderer;
import com.bug1312.client_base.base.config.renderer.FakeBlockRenderer;
import com.bug1312.client_base.core.util.DeserializeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Configuration that allows for Client Base's config to place blocks.
 * 	Often used for difficult to save blocks, like {@link FakeBlockRenderer} and {@link BlockEntityRenderer}.
 */
public record EasyPlaceConfig(
	BlockPos blockPos,
	EasyPlaceable placeable
) implements Serializable {

	public void place(ClientWorld world, BlockPos pos) {
		placeable.place(world, pos);
	}

	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(EasyPlaceConfig.class, new Deserializer()).create();
	public static class Deserializer implements JsonDeserializer<EasyPlaceConfig> {
		@Override
		public EasyPlaceConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			BlockPos blockPos = DeserializeUtil.toBlockPos(obj.get("blockpos"));

			String rendererKey = obj.get("renderer").getAsString();
			if (!ClientBaseConfig.Deserializer.lastDeserializedRenderers.containsKey(rendererKey)) return null;
			RendererType renderer = ClientBaseConfig.Deserializer.lastDeserializedRenderers.get(rendererKey);
			if (!(renderer instanceof EasyPlaceable placeable)) return null;

			return new EasyPlaceConfig(blockPos, placeable);
		}
	}
}