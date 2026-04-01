package com.bug1312.client_base.base.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.client_base.api.ClientBaseApi;
import com.bug1312.client_base.base.ClientBaseModelLoadingPlugin;
import com.bug1312.client_base.base.config.renderer.BlockModelRenderer;
import com.bug1312.client_base.core.config.ClientBaseConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
@Mixin(BlockStateModelSet.class)
abstract class BlockStateModelSetMixin {

	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	private void client_base$blockModelRendererReplace(BlockState state, CallbackInfoReturnable<BlockStateModel> ci) {
		if (!ClientBaseApi.isBaseActive()) return;

		for (var entry : ClientBaseConfig.getInstance().renderers().entrySet()) {
			if (!(entry.getValue() instanceof BlockModelRenderer blockModelRenderer)) continue;
			if (!blockModelRenderer.matcher().matches(state)) continue;
			ModelManager bakedModelManager = Minecraft.getInstance().getModelManager();
			ExtraModelKey<BlockStateModel> key = ClientBaseModelLoadingPlugin.MODEL_KEY_MAP.get(blockModelRenderer.model());
			if (key == null) continue;

			ci.setReturnValue(bakedModelManager.getModel(key));
			return;
		}
	}

}
