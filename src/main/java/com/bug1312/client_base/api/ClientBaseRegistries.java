package com.bug1312.client_base.api;

import java.util.function.Consumer;

import com.bug1312.client_base.api.config.type.InteractionType;
import com.bug1312.client_base.api.config.type.RendererType;
import com.bug1312.client_base.core.ClientBaseModCoreInitializer;
import com.google.gson.JsonDeserializer;
import com.mojang.serialization.Lifecycle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public final class ClientBaseRegistries {
	public static final WritableRegistry<JsonDeserializer<InteractionType>> INTERACTION_TYPE = of("config/interaction_type");
	public static final WritableRegistry<JsonDeserializer<RendererType>> RENDERER_TYPE = of("config/renderer_type");

	public static final WritableRegistry<BlockEntityRenderer<BlockEntity>> BLOCK_ENTITY_RENDERER = of("base/block_entity_renderer");
	public static final WritableRegistry<Consumer<Minecraft>> POS_INTERACTION = of("base/block_pos_interaction");

	final static <T> WritableRegistry<T> of(String name) {
		return new MappedRegistry<T>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ClientBaseModCoreInitializer.MOD_ID, name)), Lifecycle.stable(), false);
	}
}
