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
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
abstract class ClientPlayerInteractionManagerMixin {

	@Inject(method = { "destroyBlock", "startDestroyBlock", "continueDestroyBlock" }, at = @At("HEAD"), cancellable = true)
	private void client_base$disableClientBaseBlockBreaking(CallbackInfoReturnable<Boolean> ci) {
		if (ClientBaseApi.isBaseActive()) ci.setReturnValue(false);
	}

	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	private void client_base$clientBaseInteractions(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> ci) {
		if (ClientBaseApi.isBaseActive()) {
			BlockHitResult newHitResult = hitResult.withPosition(ClientBaseApi.toStructurePos(hitResult.getBlockPos()));

			for (var interaction : ClientBaseConfig.getInstance().interactions()) {
				if (!(ClientBaseRegistries.POS_INTERACTION.containsKey(interaction.getInteractionId()))) continue;
				ClientBaseEvents.INTERACT.invoker().onInteraction(interaction, player, hand, newHitResult);
			}
		}
	}

}
