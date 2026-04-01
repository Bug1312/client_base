package com.bug1312.client_base.base.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.client_base.api.ClientBaseApi;
import com.bug1312.client_base.api.ClientBaseRegistries;
import com.bug1312.client_base.base.ClientBaseModelLoadingPlugin;
import com.bug1312.client_base.base.config.renderer.FakeBlockRenderer;
import com.bug1312.client_base.base.duck.ChestRenderStateDuck;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderDispatcher.class)
abstract class BlockEntityRenderDispatcherMixin {

	private static final RandomSource random = RandomSource.createThreadLocalInstance(0L);

	@Inject(
		method = "submit",
		at = @At("HEAD"),
		cancellable = true
	)
	private static <S extends BlockEntityRenderState, T extends BlockEntityRenderState> void client_base$fakeBlockRenderer(final S state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera, CallbackInfo ci) {
		if (
			!ClientBaseApi.isBaseActive()
			|| !(state instanceof ChestRenderState s)
		) return;

		ChestBlockEntity be = ((ChestRenderStateDuck) (Object) s).getBlockEntity();

		ItemStack stack = be.getItem(0);
		if (stack == null) return;
		CompoundTag nbt = stack.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag())).copyTag();
		if (nbt == null) return;

		Optional<String> fakeBlockIdOpt = nbt.getString(FakeBlockRenderer.COMPOUND_ID.toString());
		Optional<String> blockEntityRendererIdOpt = nbt.getString(com.bug1312.client_base.base.config.renderer.BlockEntityRenderer.COMPOUND_ID.toString());

		if (
			fakeBlockIdOpt.isPresent()
			&& fakeBlockIdOpt.get() instanceof String string
			&& Identifier.tryParse(string) instanceof Identifier id
			&& ClientBaseModelLoadingPlugin.MODEL_KEY_MAP.containsKey(id)
		) {
			int light = s.lightCoords;

			ExtraModelKey<BlockStateModel> key = ClientBaseModelLoadingPlugin.MODEL_KEY_MAP.get(id);
			ModelManager bakedModelManager = Minecraft.getInstance().getModelManager();
			BlockStateModel bakedModel = bakedModelManager.getModel(key);
			if (bakedModel != null) {
				poseStack.pushPose();

				List<BlockStateModelPart> parts = new ArrayList<>();
				bakedModel.collectParts(random, parts);

				submitNodeCollector.submitBlockModel(
					poseStack,
					Sheets.translucentBlockSheet(),
					parts,
					BlockModelRenderState.EMPTY_TINTS,
					light,
					OverlayTexture.NO_OVERLAY,
					0
				);
				poseStack.popPose();

				ci.cancel();
				return;
			}
		}

		if (
			blockEntityRendererIdOpt.isPresent()
			&& blockEntityRendererIdOpt.get() instanceof String string
			&& Identifier.tryParse(string) instanceof Identifier id
			&& ClientBaseRegistries.BLOCK_ENTITY_RENDERER.containsKey(id)
		) {
			@SuppressWarnings("unchecked")
			var newRenderer = (BlockEntityRenderer<BlockEntity, T>) ClientBaseRegistries.BLOCK_ENTITY_RENDERER.getValue(id);
			T st = newRenderer.createRenderState();
			newRenderer.extractRenderState(be, st, 0, camera.pos, null);

			poseStack.pushPose();
			newRenderer.submit(st, poseStack, submitNodeCollector, camera);
			poseStack.popPose();

			ci.cancel();
			return;
		}
	}

}
