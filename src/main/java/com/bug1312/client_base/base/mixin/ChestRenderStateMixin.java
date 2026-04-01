package com.bug1312.client_base.base.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.bug1312.client_base.base.duck.ChestRenderStateDuck;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

@Environment(EnvType.CLIENT)
@Mixin(ChestRenderState.class)
public class ChestRenderStateMixin implements ChestRenderStateDuck {
	@Unique() private ChestBlockEntity blockEntity;

	@Override public ChestBlockEntity getBlockEntity() { return blockEntity; }

	@Override public void setBlockEntity(ChestBlockEntity val) { blockEntity = val; }
}
