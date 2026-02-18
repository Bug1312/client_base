package com.bug1312.client_base.base.config.interaction;

import java.lang.reflect.Type;

import com.bug1312.client_base.api.config.type.InteractionType;
import com.bug1312.client_base.core.util.DeserializeUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/**
 * {@link InteractionType} that will run an interaction based on a player
 * 	interacting with a specific {@link BlockPos}
 */
public record BlockPosInteraction (
	ResourceLocation interactionId,
	BlockPos blockPos
) implements InteractionType {

	@Override public ResourceLocation getInteractionId() { return interactionId(); }

	public static class Deserializer implements JsonDeserializer<InteractionType> {
		@Override
		public BlockPosInteraction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			var obj = json.getAsJsonObject();

			ResourceLocation interactionId = DeserializeUtil.toIdentifier(obj.get("interaction"));
			BlockPos blockPos = DeserializeUtil.toBlockPos(obj.get("blockpos"));

			return new BlockPosInteraction(interactionId, blockPos);
		}
	}
}
