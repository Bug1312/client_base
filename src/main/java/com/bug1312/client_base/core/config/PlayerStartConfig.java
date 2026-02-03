package com.bug1312.client_base.core.config;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Optional;

import com.bug1312.client_base.core.util.DeserializeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.math.Vec3d;

public record PlayerStartConfig(
	Vec3d offset,
	Optional<Float> pitch,
	Optional<Float> yaw
) implements Serializable {
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(PlayerStartConfig.class, new Deserializer()).create();
	public static class Deserializer implements JsonDeserializer<PlayerStartConfig> {
		public static final Vec3d DEFAULT_OFFSET = new Vec3d(0.5, 0.0, 0.5);

		@Override
		public PlayerStartConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			Vec3d offset = obj.has("offset") ? DeserializeUtil.toVec3d(obj.get("offset")) : DEFAULT_OFFSET;
			Optional<Float> pitch = (obj.has("pitch")) ? Optional.of(obj.get("pitch").getAsFloat()) : Optional.empty();
			Optional<Float> yaw = (obj.has("yaw")) ? Optional.of(obj.get("yaw").getAsFloat()) : Optional.empty();

			return new PlayerStartConfig(offset, pitch, yaw);
		}
	}
}