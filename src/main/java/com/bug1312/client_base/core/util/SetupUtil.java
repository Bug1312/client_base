package com.bug1312.client_base.core.util;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.bug1312.client_base.core.config.ClientBaseConfig;
import com.bug1312.client_base.core.config.PlayerStartConfig;
import com.bug1312.client_base.core.config.StructureConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class SetupUtil {

	@Nullable
	static BlockPos startPos;

	@Nullable
	public static BlockPos getStartPos() { return startPos; }

	public static void construct(MinecraftClient client) {
		DesyncUtil.desync(client);

		ClientBaseConfig.reload();

		ClientPlayerEntity player = client.player;
		startPos = player.getBlockPos();

		StructureConfig structureConfig = ClientBaseConfig.getInstance().structureConfig();
		Optional<PlayerStartConfig> playerStartConfigOpt = ClientBaseConfig.getInstance().playerStartConfig();

		ClientStructureUtil.place(structureConfig.structure(), startPos.add(structureConfig.offset()), client.world);

		Vec3d newPos = new Vec3d(startPos.getX(), startPos.getY(), startPos.getZ());
		if (playerStartConfigOpt.isPresent()) {
			PlayerStartConfig config = playerStartConfigOpt.get();

			newPos = newPos.add(config.offset());
			player.setPos(newPos.x, newPos.y, newPos.z);

			if (config.pitch().isPresent()) player.setPitch(config.pitch().get());
			if (config.yaw().isPresent()) player.setYaw(config.yaw().get());
		} else {
			newPos = newPos.add(PlayerStartConfig.Deserializer.DEFAULT_OFFSET);
		}

		player.setPos(newPos.x, newPos.y, newPos.z);
		player.setVelocity(Vec3d.ZERO);
		player.getAbilities().allowModifyWorld = false;

	}

	public static void deconstruct(MinecraftClient client) {
		ClientStructureUtil.revert(client);

		startPos = null;

		DesyncUtil.resync(client);
	}

}
