package com.bug1312.client_base.core.util;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class DesyncUtil {

	public static final ClientPlayConnectionEvents.Disconnect DISCONNECT_HANDLER = (_, client) -> { resync(client); };

	@Nullable()
	static PlayerState previousState;
	static boolean isSynced = true;

	public static boolean isSynced() { return isSynced; }

	public static void desync(Minecraft client) {
		var pos = client.player.position();
		var yaw = client.player.getYRot();
		var pitch = client.player.getXRot();
		var allowModify = client.player.getAbilities().mayBuild;

		previousState = new PlayerState(pos, yaw, pitch, allowModify);
		isSynced = false;
	}

	public static void resync(Minecraft client) {
		if (previousState == null) return;

		client.player.getAbilities().mayBuild = previousState.allowModify();

		Vec3 pos = previousState.position();
		client.player.setPosRaw(pos.x, pos.y, pos.z);
		client.player.setYRot(previousState.yaw());
		client.player.setXRot(previousState.pitch());

		previousState = null;
		isSynced = true;
	}

	record PlayerState(
		Vec3 position,
		float yaw,
		float pitch,
		boolean allowModify
	) { }
}