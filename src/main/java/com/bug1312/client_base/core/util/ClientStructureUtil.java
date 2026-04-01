package com.bug1312.client_base.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.bug1312.client_base.core.config.ClientBaseConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

@Environment(EnvType.CLIENT)
public class ClientStructureUtil {

	private static final Map<BlockPos, BlockState> ORIGINAL_BLOCKS = new HashMap<>();
	private static final Map<BlockPos, BlockEntity> NEW_BLOCK_ENTITIES = new HashMap<>();

	public static void place(CompoundTag nbt, BlockPos pos, ClientLevel world) {
		ListTag sizeList = nbt.getList("size").get();
		if (sizeList.size() != 3) return;

		ListTag paletteList = nbt.getList("palette").get();
		BlockState[] palette = new BlockState[paletteList.size()];

		for (int i = 0; i < paletteList.size(); i++) {
			CompoundTag stateNbt = paletteList.getCompound(i).get();
			palette[i] = readBlockState(stateNbt);
		}

		ListTag blocksList = nbt.getList("blocks").get();
		for (int i = 0; i < blocksList.size(); i++) {
			CompoundTag blockNbt = blocksList.getCompound(i).get();

			BlockPos blockPos = blockNbt.read("pos", BlockPos.CODEC).get();

			int state = blockNbt.getInt("state").get();
			if (state < 0 || state >= palette.length) continue;

			BlockState blockState = palette[state];
			BlockPos targetPos = pos.offset(blockPos);

			setBlockState(world, targetPos, blockState);

			if (blockNbt.contains("nbt")) {
				CompoundTag blockEntityNbt = blockNbt.getCompound("nbt").get();
				addBlockEntity(world, targetPos, blockState, blockEntityNbt);
			}
		}

		for (var record : ClientBaseConfig.getInstance().easyPlaceList()) {
			BlockPos newPos = pos.offset(record.blockPos());

			ORIGINAL_BLOCKS.put(pos.immutable(), world.getBlockState(newPos));

			record.placeable().place(world, newPos);

			if (world.getBlockEntity(newPos) instanceof BlockEntity be) NEW_BLOCK_ENTITIES.put(newPos, be);
		}
	}

	public static void revert(Minecraft client) {
		ClientLevel world = client.level;

		for (Map.Entry<BlockPos, BlockEntity> entry : NEW_BLOCK_ENTITIES.entrySet()) {
			world.removeBlockEntity(entry.getKey());
		}

		for (Map.Entry<BlockPos, BlockState> entry : ORIGINAL_BLOCKS.entrySet()) {
			world.setBlock(entry.getKey(), entry.getValue(), Block.UPDATE_KNOWN_SHAPE);
		}

		NEW_BLOCK_ENTITIES.clear();
		ORIGINAL_BLOCKS.clear();

		client.levelRenderer.allChanged();
	}

	private static BlockState readBlockState(CompoundTag nbt) {
		String name = nbt.getString("Name").get();

		if (!(Identifier.tryParse(name) instanceof Identifier id)) return Blocks.AIR.defaultBlockState();

		BlockState state = BuiltInRegistries.BLOCK.getOptional(id).orElse(Blocks.AIR).defaultBlockState();

		if (nbt.contains("Properties")) {
			CompoundTag properties = nbt.getCompound("Properties").get();
			state = applyProperties(state, properties);
		}

		return state;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static BlockState applyProperties(BlockState state, CompoundTag properties) {
		for (String key : properties.keySet()) {
			Property<?> property = state.getBlock().getStateDefinition().getProperty(key);
			if (property != null) {
				String value = properties.getString(key).get();
				Optional<?> optional = property.getValue(value);
				if (optional.isPresent()) state = state.setValue((Property) property, (Comparable) optional.get());
			}
		}
		return state;
	}

	private static void setBlockState(Level world, BlockPos pos, BlockState state) {
		BlockState original = world.getBlockState(pos);
		ORIGINAL_BLOCKS.put(pos.immutable(), original);

		world.setBlock(pos, state, Block.UPDATE_KNOWN_SHAPE);
	}

	private static void addBlockEntity(Level world, BlockPos pos, BlockState state, CompoundTag nbt) {
		if (state.hasBlockEntity()) {
			BlockEntity blockEntity = BlockEntity.loadStatic(pos, state, nbt, world.registryAccess());
			NEW_BLOCK_ENTITIES.put(pos.immutable(), blockEntity);

			world.setBlockEntity(blockEntity);
		}
	}

}