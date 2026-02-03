package com.bug1312.client_base.base.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.bug1312.client_base.api.PassthroughPacket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.SlotChangedStateC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;

@Environment(EnvType.CLIENT)
@Mixin({
	AdvancementTabC2SPacket.class,
	BookUpdateC2SPacket.class,
	ChatCommandSignedC2SPacket.class,
	ChatMessageC2SPacket.class,
	ClientOptionsC2SPacket.class,
	CommandExecutionC2SPacket.class,
	KeepAliveC2SPacket.class,
	PlayerActionC2SPacket.class,
	SlotChangedStateC2SPacket.class,
	UpdateDifficultyC2SPacket.class,
	UpdateDifficultyLockC2SPacket.class,
})
class PassThrough implements PassthroughPacket { }