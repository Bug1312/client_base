package com.bug1312.client_base.base.config.renderer;

import java.lang.reflect.Type;
import java.util.function.UnaryOperator;

import com.bug1312.client_base.api.config.type.EasyPlaceable;
import com.bug1312.client_base.api.config.type.RendererType;
import com.bug1312.client_base.core.ClientBaseModCoreInitializer;
import com.bug1312.client_base.core.util.DeserializeUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * {@link RendererType Renderer} that will create a custom block using a JSON block model
 * 	by passing it through a {@link BlockEntityRenderer} utilizing {@link ModelBlockRenderer#renderModel}.
 *
 * This will allow for endless custom blocks without the need to create a new Java integration.
 */
public record FakeBlockRenderer(
	ResourceLocation model,
	VoxelShape shape
) implements RendererType, EasyPlaceable {

	public static final ResourceLocation COMPOUND_ID = ResourceLocation.fromNamespaceAndPath(ClientBaseModCoreInitializer.MOD_ID, "fake_block_renderer");

	@Override
	public void place(ClientLevel world, BlockPos pos) {
		BlockState state = Blocks.CHEST.defaultBlockState();
		world.setBlockAndUpdate(pos, state);
		CompoundTag nbt = new CompoundTag();
		CompoundTag customCompound = new CompoundTag();
		customCompound.putString(COMPOUND_ID.toString(), model.toString());
		ItemStack stack = new ItemStack(Blocks.STONE);
		stack.update(DataComponents.CUSTOM_DATA, CustomData.of(customCompound), UnaryOperator.identity());
		nbt.putString("id", "minecraft:chest");
		BlockEntity blockEntity = BlockEntity.loadStatic(pos, state, nbt, world.registryAccess());
		if (blockEntity instanceof ChestBlockEntity chest) chest.setItem(0, stack);
		if (blockEntity != null) world.setBlockEntity(blockEntity);
	}

	public static class Deserializer implements JsonDeserializer<RendererType> {
		@Override
		public RendererType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			ResourceLocation model = DeserializeUtil.toIdentifier(obj.get("model"));

			VoxelShape shape = (obj.has("hitbox")) ? DeserializeUtil.toVoxelShape(obj.get("hitbox")) : Shapes.block();

			return new FakeBlockRenderer(model, shape);
		}
	}

}