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

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Mixin(ChestBlock.class)
abstract class ChestBlockMixin {

	@Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
	private void getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> ci) {
		if (
			!ClientBaseApi.isBaseActive()
			|| !(world.getBlockEntity(pos) instanceof ChestBlockEntity blockEntity)
		) return;

		NbtCompound nbt = blockEntity.getStack(0).getNbt();

		Optional<String> fakeBlockIdOpt = Optional.of(nbt.getString(FakeBlockRenderer.COMPOUND_ID.toString()));
		Optional<String> blockEntityRendererIdOpt = Optional.of(nbt.getString(BlockEntityRenderer.COMPOUND_ID.toString()));

		Collection<RendererType> renderers = ClientBaseConfig.Deserializer.lastDeserializedRenderers.values();

		if (
			fakeBlockIdOpt.isPresent()
			&& Identifier.isValid(fakeBlockIdOpt.get())
		) {
			Optional<FakeBlockRenderer> fakeBlockRenderer = renderers.stream()
				.filter(renderer -> (
					renderer instanceof FakeBlockRenderer fbr
					&& fbr.model().equals(Identifier.tryParse(fakeBlockIdOpt.get()))
				))
				.map(renderer -> (FakeBlockRenderer) renderer)
				.findFirst();

			if (fakeBlockRenderer.isPresent()) ci.setReturnValue(fakeBlockRenderer.get().shape());
		}

		if (
			blockEntityRendererIdOpt.isPresent()
			&& Identifier.isValid(blockEntityRendererIdOpt.get())
		) {
			Optional<BlockEntityRenderer> blockEntityRenderer = renderers.stream()
				.filter(renderer -> (
					renderer instanceof BlockEntityRenderer ber
					&& ber.rendererId().equals(Identifier.tryParse(blockEntityRendererIdOpt.get()))
				))
				.map(renderer -> (BlockEntityRenderer) renderer)
				.findFirst();

			if (blockEntityRenderer.isPresent()) ci.setReturnValue(blockEntityRenderer.get().shape());
		}
	}

}
