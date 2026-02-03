package com.bug1312.client_base.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.bug1312.client_base.core.config.ClientBaseConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ClientStructureUtil {

	private static final Map<BlockPos, BlockState> ORIGINAL_BLOCKS = new HashMap<>();
	private static final Map<BlockPos, BlockEntity> NEW_BLOCK_ENTITIES = new HashMap<>();

	public static void place(NbtCompound nbt, BlockPos pos, ClientWorld world) {
		NbtList sizeList = nbt.getList("size", NbtElement.INT_TYPE);
		if (sizeList.size() != 3) return;

		NbtList paletteList = nbt.getList("palette", NbtElement.COMPOUND_TYPE);
		BlockState[] palette = new BlockState[paletteList.size()];

		for (int i = 0; i < paletteList.size(); i++) {
			NbtCompound stateNbt = paletteList.getCompound(i);
			palette[i] = readBlockState(stateNbt);
		}

		NbtList blocksList = nbt.getList("blocks", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < blocksList.size(); i++) {
			NbtCompound blockNbt = blocksList.getCompound(i);

			NbtList _blockPosNbtList = blockNbt.getList("pos", NbtElement.INT_TYPE);
			BlockPos blockPos = new BlockPos(_blockPosNbtList.getInt(0), _blockPosNbtList.getInt(1), _blockPosNbtList.getInt(2));

			int state = blockNbt.getInt("state");
			if (state < 0 || state >= palette.length) continue;

			if (state == 7) continue;
			BlockState blockState = palette[state];
			BlockPos targetPos = pos.add(blockPos);

			setBlockState(world, targetPos, blockState);

			if (blockNbt.contains("nbt")) {
				NbtCompound blockEntityNbt = blockNbt.getCompound("nbt");
				addBlockEntity(world, targetPos, blockState, blockEntityNbt);
			}
		}

		for (var record : ClientBaseConfig.getInstance().easyPlaceList()) {
			BlockPos newPos = pos.add(record.blockPos());

			ORIGINAL_BLOCKS.put(pos.toImmutable(), world.getBlockState(newPos));

			record.placeable().place(world, newPos);

			BlockEntity be = world.getBlockEntity(newPos);
			if (be != null) NEW_BLOCK_ENTITIES.put(newPos, be);
		}
	}

	public static void revert(MinecraftClient client) {
		ClientWorld world = client.world;

		for (Map.Entry<BlockPos, BlockEntity> entry : NEW_BLOCK_ENTITIES.entrySet()) {
			world.removeBlockEntity(entry.getKey());
		}

		for (Map.Entry<BlockPos, BlockState> entry : ORIGINAL_BLOCKS.entrySet()) {
			world.setBlockState(entry.getKey(), entry.getValue(), 0);
		}

		NEW_BLOCK_ENTITIES.clear();
		ORIGINAL_BLOCKS.clear();

		client.worldRenderer.reload();
	}

	private static BlockState readBlockState(NbtCompound nbt) {
		String name = nbt.getString("Name");

		Identifier id = Identifier.tryParse(name);
		if (id == null) return Blocks.AIR.getDefaultState();

		BlockState state = Optional.of(Registries.BLOCK.get(id)).orElse(Blocks.AIR).getDefaultState();

		if (nbt.contains("Properties")) {
			NbtCompound properties = nbt.getCompound("Properties");
			state = applyProperties(state, properties);
		}

		return state;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static BlockState applyProperties(BlockState state, NbtCompound properties) {
		for (String key : properties.getKeys()) {
			Property<?> property = state.getBlock().getStateManager().getProperty(key);
			if (property != null) {
				String value = properties.getString(key);
				Optional<?> optional = property.parse(value);
				if (optional.isPresent()) state = state.with((Property) property, (Comparable) optional.get());
			}
		}
		return state;
	}

	private static void setBlockState(World world, final BlockPos pos, BlockState state) {
		BlockState original = world.getBlockState(pos);
		ORIGINAL_BLOCKS.put(pos.toImmutable(), original);

		world.setBlockState(pos, state);
	}

	private static void addBlockEntity(World world, BlockPos pos, BlockState state, NbtCompound nbt) {
		if (state.hasBlockEntity()) {
			BlockEntity blockEntity = BlockEntity.createFromNbt(pos, state, nbt);
			NEW_BLOCK_ENTITIES.put(pos.toImmutable(), blockEntity);

			world.addBlockEntity(blockEntity);
		}
	}

}