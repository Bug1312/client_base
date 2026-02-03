package com.bug1312.client_base.core.util;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class DesyncUtil {

	public static final ClientPlayConnectionEvents.Disconnect DISCONNECT_HANDLER = (handler, client) -> { resync(client); };

	@Nullable()
	static PlayerState previousState;
	static boolean isSynced = true;

	public static boolean isSynced() { return isSynced; }

	public static void desync(MinecraftClient client) {
		var pos = client.player.getPos();
		var yaw = client.player.getYaw();
		var pitch = client.player.getPitch();
		var allowModify = client.player.getAbilities().allowModifyWorld;

		previousState = new PlayerState(pos, yaw, pitch, allowModify);
		isSynced = false;
	}

	public static void resync(MinecraftClient client) {
		if (previousState == null) return;

		client.player.getAbilities().allowModifyWorld = previousState.allowModify();

		Vec3d pos = previousState.position();
		client.player.setPos(pos.x, pos.y, pos.z);
		client.player.setYaw(previousState.yaw());
		client.player.setPitch(previousState.pitch());

		previousState = null;
		isSynced = true;
	}

	record PlayerState(
		Vec3d position,
		float yaw,
		float pitch,
		boolean allowModify
	) { }
}