package com.bug1312.client_base.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.client_base.api.ClientBaseApi;
import com.bug1312.client_base.api.ClientBaseEvents;
import com.bug1312.client_base.api.ClientBaseRegistries;
import com.bug1312.client_base.core.config.ClientBaseConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
abstract class ClientPlayerInteractionManagerMixin {

	@Inject(method = { "breakBlock", "attackBlock", "updateBlockBreakingProgress" }, at = @At("HEAD"), cancellable = true)
	private void client_base$disableClientBaseBlockBreaking(CallbackInfoReturnable<Boolean> ci) {
		if (ClientBaseApi.isBaseActive()) ci.setReturnValue(false);
	}

	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	private void client_base$clientBaseInteractions(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> ci) {
		if (ClientBaseApi.isBaseActive()) {
			BlockHitResult newHitResult = hitResult.withBlockPos(ClientBaseApi.toStructurePos(hitResult.getBlockPos()));

			for (var interaction : ClientBaseConfig.getInstance().interactions()) {
				if (!(ClientBaseRegistries.POS_INTERACTION.containsId(interaction.getInteractionId()))) continue;
				ClientBaseEvents.INTERACT.invoker().onInteraction(interaction, player, hand, newHitResult);
			}
		}
	}

}
