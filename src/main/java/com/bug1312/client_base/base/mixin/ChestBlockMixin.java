package com.bug1312.client_base.base.mixin;

import java.util.Collection;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.client_base.api.ClientBaseApi;
import com.bug1312.client_base.api.config.type.RendererType;
import com.bug1312.client_base.base.config.renderer.BlockEntityRenderer;
import com.bug1312.client_base.base.config.renderer.FakeBlockRenderer;
import com.bug1312.client_base.core.config.ClientBaseConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(ChestBlock.class)
abstract class ChestBlockMixin {

	@Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
	private void getOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> ci) {
		if (
			!ClientBaseApi.isBaseActive()
			|| !(world.getBlockEntity(pos) instanceof ChestBlockEntity blockEntity)
		) return;

		ItemStack stack = blockEntity.getItem(0);
		if (stack == null) return;
		CompoundTag nbt = stack.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag())).copyTag();
		if (nbt == null) return;

		Optional<String> fakeBlockIdOpt = nbt.getString(FakeBlockRenderer.COMPOUND_ID.toString());
		Optional<String> blockEntityRendererIdOpt = nbt.getString(BlockEntityRenderer.COMPOUND_ID.toString());

		Collection<RendererType> renderers = ClientBaseConfig.Deserializer.lastDeserializedRenderers.values();

		if (
			fakeBlockIdOpt.isPresent()
			&& fakeBlockIdOpt.get() instanceof String string
			&& ResourceLocation.tryParse(string) instanceof ResourceLocation id
		) {
			Optional<FakeBlockRenderer> fakeBlockRenderer = renderers.stream()
				.filter(renderer -> (
					renderer instanceof FakeBlockRenderer fbr
					&& fbr.model().equals(id)
				))
				.map(renderer -> (FakeBlockRenderer) renderer)
				.findFirst();

			if (fakeBlockRenderer.isPresent()) ci.setReturnValue(fakeBlockRenderer.get().shape());
		}

		if (
			blockEntityRendererIdOpt.isPresent()
			&& blockEntityRendererIdOpt.get() instanceof String string
			&& ResourceLocation.tryParse(string) instanceof ResourceLocation id
		) {
			Optional<BlockEntityRenderer> blockEntityRenderer = renderers.stream()
				.filter(renderer -> (
					renderer instanceof BlockEntityRenderer ber
					&& ber.rendererId().equals(id)
				))
				.map(renderer -> (BlockEntityRenderer) renderer)
				.findFirst();

			if (blockEntityRenderer.isPresent()) ci.setReturnValue(blockEntityRenderer.get().shape());
		}
	}
}
