package com.bug1312.client_base.api;

import org.jetbrains.annotations.Nullable;

import com.bug1312.client_base.core.config.ClientBaseConfig;
import com.bug1312.client_base.core.util.DesyncUtil;
import com.bug1312.client_base.core.util.SetupUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

@Environment(EnvType.CLIENT)
public final class ClientBaseApi {
	public static boolean isBaseActive() {
		return !DesyncUtil.isSynced();
	}

	public static void activateBase() {
		SetupUtil.construct(MinecraftClient.getInstance());
	}

	public static void deactivateBase() {
		SetupUtil.deconstruct(MinecraftClient.getInstance());
	}

	/**
	 * Used to be added to structure-based {@link BlockPos} for real world {@link BlockPos}.
	 *
	 * @example Knowing an interactions is at (0, 0, 5) to the structure, use this method
	 * 	to get the structure starting position (3435, 67, 420) to be added to.
	 *
	 * @return {@link BlockPos} that is the starting point of the Client Base structure
	 */
	@Nullable
	public static BlockPos getStructurePos() {
		return SetupUtil.getStartPos();
	}

	@Nullable
	public static BlockPos toStructurePos(BlockPos realPos) {
		if (SetupUtil.getStartPos() == null) return null;
		Vec3i offset = SetupUtil.getStartPos().add(ClientBaseConfig.getInstance().structureConfig().offset());
		return realPos.subtract(offset);
	}

}
