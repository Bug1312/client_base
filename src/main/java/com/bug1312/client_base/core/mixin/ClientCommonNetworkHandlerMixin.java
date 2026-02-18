package com.bug1312.client_base.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.client_base.api.PassthroughPacket;
import com.bug1312.client_base.core.util.DesyncUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;

@Environment(EnvType.CLIENT)
@Mixin(ClientCommonPacketListenerImpl.class)
abstract class ClientCommonNetworkHandlerMixin {

	@Inject(method = "send", at = @At("HEAD"), cancellable = true)
	private void client_base$blockPacketsWhenDesynced(Packet<?> packet, CallbackInfo ci) {
		if (!DesyncUtil.isSynced() && !(packet instanceof PassthroughPacket)) ci.cancel();
	}

}
