package com.bug1312.client_base.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bug1312.client_base.core.util.DesyncUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public class ClientBaseModCoreInitializer implements ClientModInitializer {

	public static final String MOD_ID = "client_base";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.DISCONNECT.register(DesyncUtil.DISCONNECT_HANDLER);
	}

}