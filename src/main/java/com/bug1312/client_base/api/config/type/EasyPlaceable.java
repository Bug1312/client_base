package com.bug1312.client_base.api.config.type;

import com.bug1312.client_base.core.config.EasyPlaceConfig;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Interface to attach to {@link RendererType RendererTypes} for
 * 	{@link EasyPlaceConfig} to interact with.
 */
public interface EasyPlaceable {
	public void place(ClientWorld world, BlockPos pos);
}
