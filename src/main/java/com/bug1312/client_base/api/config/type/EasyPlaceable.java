package com.bug1312.client_base.api.config.type;

import com.bug1312.client_base.core.config.EasyPlaceConfig;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

/**
 * Interface to attach to {@link RendererType RendererTypes} for
 * 	{@link EasyPlaceConfig} to interact with.
 */
public interface EasyPlaceable {
	public void place(ClientLevel world, BlockPos pos);
}
