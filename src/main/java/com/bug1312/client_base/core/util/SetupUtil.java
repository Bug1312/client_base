package com.bug1312.client_base.core.util;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.bug1312.client_base.core.config.ClientBaseConfig;
import com.bug1312.client_base.core.config.PlayerStartConfig;
import com.bug1312.client_base.core.config.StructureConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class SetupUtil {

	@Nullable
	static BlockPos startPos;

	@Nullable
	public static BlockPos getStartPos() { return startPos; }

	public static void construct(Minecraft client) {
		DesyncUtil.desync(client);

		ClientBaseConfig.reload();

		LocalPlayer player = client.player;
		startPos = player.blockPosition();

		StructureConfig structureConfig = ClientBaseConfig.getInstance().structureConfig();
		Optional<PlayerStartConfig> playerStartConfigOpt = ClientBaseConfig.getInstance().playerStartConfig();

		ClientStructureUtil.place(structureConfig.structure(), startPos.offset(structureConfig.offset()), client.level);

		Vec3 newPos = new Vec3(startPos);
		if (playerStartConfigOpt.isPresent()) {
			PlayerStartConfig config = playerStartConfigOpt.get();

			newPos = newPos.add(config.offset());
			player.setPosRaw(newPos.x, newPos.y, newPos.z);

			if (config.pitch().isPresent()) player.setXRot(config.pitch().get());
			if (config.yaw().isPresent()) player.setYRot(config.yaw().get());
		} else {
			newPos = newPos.add(PlayerStartConfig.Deserializer.DEFAULT_OFFSET);
		}

		player.setPosRaw(newPos.x, newPos.y, newPos.z);
		player.setDeltaMovement(Vec3.ZERO);
		player.getAbilities().mayBuild = false;

	}

	public static void deconstruct(Minecraft client) {
		ClientStructureUtil.revert(client);

		startPos = null;

		DesyncUtil.resync(client);
	}

}
