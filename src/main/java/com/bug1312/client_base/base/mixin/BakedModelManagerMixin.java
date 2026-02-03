package com.bug1312.client_base.base.mixin;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.client_base.core.config.ClientBaseConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModelManager;

@Environment(EnvType.CLIENT)
@Mixin(BakedModelManager.class)
abstract class BakedModelManagerMixin {

	@Inject(method = "reload", at = @At("HEAD"))
	private final void client_base$reloadConfigBeforeAssets(CallbackInfoReturnable<CompletableFuture<Void>> ci) {
		ClientBaseConfig.reload();
	}

}
