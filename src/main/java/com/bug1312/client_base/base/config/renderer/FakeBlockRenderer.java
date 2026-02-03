package com.bug1312.client_base.base.config.renderer;

import java.lang.reflect.Type;

import com.bug1312.client_base.api.config.type.EasyPlaceable;
import com.bug1312.client_base.api.config.type.RendererType;
import com.bug1312.client_base.core.ClientBaseModCoreInitializer;
import com.bug1312.client_base.core.util.DeserializeUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * {@link RendererType Renderer} that will create a custom block using a JSON block model
 * 	by passing it through a {@link BlockEntityRenderer} utilizing {@link BlockModelRenderer#render}.
 *
 * This will allow for endless custom blocks without the need to create a new Java integration.
 */
public record FakeBlockRenderer(
	Identifier model,
	VoxelShape shape
) implements RendererType, EasyPlaceable {

	public static final Identifier COMPOUND_ID = Identifier.of(ClientBaseModCoreInitializer.MOD_ID, "fake_block_renderer");

	@Override
	public void place(ClientWorld world, BlockPos pos) {
		BlockState state = Blocks.CHEST.getDefaultState();
		world.setBlockState(pos, state);
		NbtCompound nbt = new NbtCompound();
		NbtCompound customCompount = new NbtCompound();
		customCompount.putString(COMPOUND_ID.toString(), model.toString());
		ItemStack stack = new ItemStack(Blocks.STONE);
		stack.setNbt(customCompount);
		nbt.putString("id", "minecraft:chest");
		BlockEntity blockEntity = BlockEntity.createFromNbt(pos, state, nbt);
		if (blockEntity instanceof ChestBlockEntity chest) chest.setStack(0, stack);
		if (blockEntity != null) world.addBlockEntity(blockEntity);
	}

	public static class Deserializer implements JsonDeserializer<RendererType> {
		@Override
		public RendererType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			Identifier model = DeserializeUtil.toIdentifier(obj.get("model"));

			VoxelShape shape = (obj.has("hitbox")) ? DeserializeUtil.toVoxelShape(obj.get("hitbox")) : VoxelShapes.fullCube();

			return new FakeBlockRenderer(model, shape);
		}
	}

}