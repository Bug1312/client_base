package com.bug1312.client_base.base.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.client_base.api.ClientBaseApi;
import com.bug1312.client_base.api.ClientBaseRegistries;
import com.bug1312.client_base.base.ClientBaseModelLoadingPlugin;
import com.bug1312.client_base.base.config.renderer.FakeBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderDispatcher.class)
abstract class BlockEntityRenderDispatcherMixin {

	@Inject(
		method = "setupAndRender",
		at = @At("HEAD"),
		cancellable = true
	)
	private static <T extends BlockEntity> void client_base$fakeBlockRenderer(BlockEntityRenderer<T> renderer, T blockEntity, float tickProgress, PoseStack matrices, MultiBufferSource vertexConsumers, Vec3 cameraPos, CallbackInfo ci) {
		if (
			!ClientBaseApi.isBaseActive()
			|| !(blockEntity instanceof ChestBlockEntity be)
		) return;

		ItemStack stack = be.getItem(0);
		if (stack == null) return;
		CompoundTag nbt = stack.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag())).copyTag();
		if (nbt == null) return;

		Optional<String> fakeBlockIdOpt = nbt.getString(FakeBlockRenderer.COMPOUND_ID.toString());
		Optional<String> blockEntityRendererIdOpt = nbt.getString(com.bug1312.client_base.base.config.renderer.BlockEntityRenderer.COMPOUND_ID.toString());

		if (
			fakeBlockIdOpt.isPresent()
			&& fakeBlockIdOpt.get() instanceof String string
			&& ResourceLocation.tryParse(string) instanceof ResourceLocation id
			&& ClientBaseModelLoadingPlugin.MODEL_KEY_MAP.containsKey(id)
		) {
			Level world = blockEntity.getLevel();
			int light = (world != null) ? LevelRenderer.getLightColor(world, blockEntity.getBlockPos()) : 15728880;

			ExtraModelKey<BlockStateModel> key = ClientBaseModelLoadingPlugin.MODEL_KEY_MAP.get(id);
			ModelManager bakedModelManager = Minecraft.getInstance().getModelManager();
			BlockStateModel bakedModel = bakedModelManager.getModel(key);
			if (bakedModel != null) {
				matrices.pushPose();

				@SuppressWarnings("deprecation")
				ResourceLocation blockAtlasIdentifier = TextureAtlas.LOCATION_BLOCKS;
				VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityTranslucent(blockAtlasIdentifier));

				ModelBlockRenderer.renderModel(matrices.last(), vertexConsumer, bakedModel, 1, 1, 1, light, OverlayTexture.NO_OVERLAY);

				matrices.popPose();

				ci.cancel();
				return;
			}
		}

		if (
			blockEntityRendererIdOpt.isPresent()
			&& blockEntityRendererIdOpt.get() instanceof String string
			&& ResourceLocation.tryParse(string) instanceof ResourceLocation id
			&& ClientBaseRegistries.BLOCK_ENTITY_RENDERER.containsKey(id)
		) {
			BlockEntityRenderer<BlockEntity> newRenderer = ClientBaseRegistries.BLOCK_ENTITY_RENDERER.getValue(id);
			Level world = blockEntity.getLevel();
			int light = (world != null) ? LevelRenderer.getLightColor(world, blockEntity.getBlockPos()) : 15728880;

			matrices.pushPose();
			newRenderer.render(blockEntity, tickProgress, matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY, cameraPos);
			matrices.popPose();

			ci.cancel();
			return;
		}
	}

}
