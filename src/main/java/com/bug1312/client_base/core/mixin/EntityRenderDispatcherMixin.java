package com.bug1312.client_base.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.client_base.api.ClientBaseApi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
abstract class EntityRenderDispatcherMixin {

	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private <E extends Entity> void client_base$preventEntityRendering(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> ci) {
		if (ClientBaseApi.isBaseActive() && !entity.equals(Minecraft.getInstance().player)) ci.setReturnValue(false);
	}

}
