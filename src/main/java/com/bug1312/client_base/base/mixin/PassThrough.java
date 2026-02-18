package com.bug1312.client_base.base.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.bug1312.client_base.api.PassthroughPacket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChangeGameModePacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectBundleItemPacket;

@Environment(EnvType.CLIENT)
@Mixin({
	ServerboundSeenAdvancementsPacket.class,
	ServerboundEditBookPacket.class,
	ServerboundChatCommandSignedPacket.class,
	ServerboundChatPacket.class,
	ServerboundClientInformationPacket.class,
	ServerboundChatCommandPacket.class,
	ServerboundKeepAlivePacket.class,
	ServerboundPlayerActionPacket.class,
	ServerboundContainerSlotStateChangedPacket.class,
	ServerboundChangeDifficultyPacket.class,
	ServerboundLockDifficultyPacket.class,
	ServerboundChangeGameModePacket.class,
	ServerboundSelectBundleItemPacket.class,
})
class PassThrough implements PassthroughPacket { }