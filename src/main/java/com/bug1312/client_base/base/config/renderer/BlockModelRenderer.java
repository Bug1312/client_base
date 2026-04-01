package com.bug1312.client_base.base.config.renderer;

import java.lang.reflect.Type;

import com.bug1312.client_base.api.config.type.EasyPlaceable;
import com.bug1312.client_base.api.config.type.RendererType;
import com.bug1312.client_base.core.util.DeserializeUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {@link RendererType Renderer} that will replace the model used by a blockstate.
 * 	This variant may be preferred over {@link FakeBlockRenderer} due to more accurate
 * 	lighting.
 */
public record BlockModelRenderer(
	BlockStateMatcher matcher,
	Identifier model
) implements RendererType, EasyPlaceable {

	@Override
	public void place(ClientLevel world, BlockPos pos) {
		BlockState state = matcher().toBlockState(world);
		if (state != null) world.setBlockAndUpdate(pos, state);
	}

	public static class Deserializer implements JsonDeserializer<RendererType> {
		@Override
		public RendererType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			Identifier model = DeserializeUtil.toIdentifier(obj.get("model"));

			JsonObject replaceStateObj = obj.get("replace_state").getAsJsonObject();
			Identifier blockId = DeserializeUtil.toIdentifier(replaceStateObj.get("block"));
			BlockStateMatcher matcher = BlockStateMatcher.of(blockId, replaceStateObj.get("state").getAsString());
			if (matcher == null) return null;

			return new BlockModelRenderer(matcher, model);
		}
	}

}