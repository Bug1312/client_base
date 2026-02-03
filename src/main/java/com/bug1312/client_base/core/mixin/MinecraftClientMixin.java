package com.bug1312.client_base.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.client_base.core.util.DesyncUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {

	@Inject(method = "isPaused", at = @At("RETURN"), cancellable = true)
	private void client_base$pauseGameOnActive(CallbackInfoReturnable<Boolean> ci) {
		if (!DesyncUtil.isSynced()) ci.setReturnValue(true);
	}

}
