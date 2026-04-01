package com.bug1312.client_base.base.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.client_base.base.duck.ChestRenderStateDuck;

import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.phys.Vec3;

@Mixin(ChestRenderer.class)
public class ChestRendererMixin<T extends BlockEntity & LidBlockEntity> {

	@Inject(method = "extractRenderState", at = @At("TAIL"), cancellable = true)
	private void client_base$applyDuckValues(
		final T blockEntity,
		final ChestRenderState state,
		final float partialTicks,
		final Vec3 cameraPosition,
		final ModelFeatureRenderer.CrumblingOverlay breakProgress,
		CallbackInfo ci
	) {
		if (!(blockEntity instanceof ChestBlockEntity be)) return;
		if (!((Object) state instanceof ChestRenderStateDuck s)) return;

		s.setBlockEntity(be);
	}

}
