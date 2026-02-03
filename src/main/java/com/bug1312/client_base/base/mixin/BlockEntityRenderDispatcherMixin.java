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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderDispatcher.class)
abstract class BlockEntityRenderDispatcherMixin {

	@Inject(
		method = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/util/math/Vec3d;)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private static <T extends BlockEntity> void client_base$fakeBlockRenderer(BlockEntityRenderer<T> renderer, T blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d cameraPos, CallbackInfo ci) {
		if (
			!ClientBaseApi.isBaseActive()
			|| !(blockEntity instanceof ChestBlockEntity be)
		) return;

		NbtCompound nbt = be.getStack(0).getComponents().getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(new NbtCompound())).copyNbt();

		Optional<String> fakeBlockIdOpt = nbt.getString(FakeBlockRenderer.COMPOUND_ID.toString());
		Optional<String> blockEntityRendererIdOpt = nbt.getString(com.bug1312.client_base.base.config.renderer.BlockEntityRenderer.COMPOUND_ID.toString());

		if (
			fakeBlockIdOpt.isPresent()
			&& fakeBlockIdOpt.get() instanceof String string
			&& Identifier.tryParse(string) instanceof Identifier id
			&& ClientBaseModelLoadingPlugin.MODEL_KEY_MAP.containsKey(id)
		) {
			World world = blockEntity.getWorld();
			int light = (world != null) ? WorldRenderer.getLightmapCoordinates(world, blockEntity.getPos()) : 15728880;

			ExtraModelKey<BlockStateModel> key = ClientBaseModelLoadingPlugin.MODEL_KEY_MAP.get(id);
			BakedModelManager bakedModelManager = MinecraftClient.getInstance().getBakedModelManager();
			BlockStateModel bakedModel = bakedModelManager.getModel(key);
			if (bakedModel != null) {
				matrices.push();

				@SuppressWarnings("deprecation")
				Identifier blockAtlasIdentifier = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
				VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(blockAtlasIdentifier));

				BlockModelRenderer.render(matrices.peek(), vertexConsumer, bakedModel, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);

				matrices.pop();

				ci.cancel();
				return;
			}
		}

		if (
			blockEntityRendererIdOpt.isPresent()
			&& blockEntityRendererIdOpt.get() instanceof String string
			&& Identifier.tryParse(string) instanceof Identifier id
			&& ClientBaseRegistries.BLOCK_ENTITY_RENDERER.containsId(id)
		) {
			BlockEntityRenderer<BlockEntity> newRenderer = ClientBaseRegistries.BLOCK_ENTITY_RENDERER.get(id);
			World world = blockEntity.getWorld();
			int light = (world != null) ? WorldRenderer.getLightmapCoordinates(world, blockEntity.getPos()) : 15728880;

			matrices.push();
			newRenderer.render(blockEntity, tickProgress, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, cameraPos);
			matrices.pop();

			ci.cancel();
			return;
		}
	}

}
