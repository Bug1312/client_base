package com.bug1312.client_base.api;

import java.util.function.Consumer;

import com.bug1312.client_base.api.config.type.InteractionType;
import com.bug1312.client_base.api.config.type.RendererType;
import com.bug1312.client_base.core.ClientBaseModCoreInitializer;
import com.google.gson.JsonDeserializer;
import com.mojang.serialization.Lifecycle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class ClientBaseRegistries {
	public static final MutableRegistry<JsonDeserializer<InteractionType>> INTERACTION_TYPE = of("config/interaction_type");
	public static final MutableRegistry<JsonDeserializer<RendererType>> RENDERER_TYPE = of("config/renderer_type");

	public static final MutableRegistry<BlockEntityRenderer<BlockEntity>> BLOCK_ENTITY_RENDERER = of("base/block_entity_renderer");
	public static final MutableRegistry<Consumer<MinecraftClient>> POS_INTERACTION = of("base/block_pos_interaction");

	final static <T> MutableRegistry<T> of(String name) {
		return new SimpleRegistry<T>(RegistryKey.ofRegistry(Identifier.of(ClientBaseModCoreInitializer.MOD_ID, name)), Lifecycle.stable(), false);
	}
}
