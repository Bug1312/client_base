package com.bug1312.client_base.base;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import com.bug1312.client_base.api.ClientBaseApi;
import com.bug1312.client_base.api.ClientBaseEvents;
import com.bug1312.client_base.api.ClientBaseRegistries;
import com.bug1312.client_base.base.config.interaction.BlockPosInteraction;
import com.bug1312.client_base.base.config.renderer.BlockEntityRenderer;
import com.bug1312.client_base.base.config.renderer.BlockModelRenderer;
import com.bug1312.client_base.base.config.renderer.FakeBlockRenderer;
import com.bug1312.client_base.core.util.SetupUtil;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ClientBaseModBaseInitializer implements ClientModInitializer {

	public static final KeyMapping KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.client_base.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_HOME, "key.categories.client_base.keybinds"));

	@Override
	public void onInitializeClient() {
		ModelLoadingPlugin.register(new ClientBaseModelLoadingPlugin());

		Registry.register(ClientBaseRegistries.RENDERER_TYPE, ResourceLocation.parse("block_entity"), new BlockEntityRenderer.Deserializer());
		Registry.register(ClientBaseRegistries.RENDERER_TYPE, ResourceLocation.parse("fake_block"), new FakeBlockRenderer.Deserializer());
		Registry.register(ClientBaseRegistries.RENDERER_TYPE, ResourceLocation.parse("replace_state"), new BlockModelRenderer.Deserializer());

		Registry.register(ClientBaseRegistries.INTERACTION_TYPE, ResourceLocation.parse("blockpos"), new BlockPosInteraction.Deserializer());
		ClientBaseEvents.INTERACT.register((interaction, player, hand, hitResult) -> {
			if (interaction instanceof BlockPosInteraction blockPosMenu) {
				if (!blockPosMenu.blockPos().equals(hitResult.getBlockPos())) return;
				ClientBaseRegistries.POS_INTERACTION.getValue(interaction.getInteractionId()).accept(Minecraft.getInstance());
			}
		});

		registerBaseInteractions();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (KEYBIND.consumeClick()) {
				if (ClientBaseApi.isBaseActive()) SetupUtil.deconstruct(client);
				else SetupUtil.construct(client);
			}
		});
	}

	void registerBaseInteractions() {
		registerBaseInteraction("exit", (client) -> { if (ClientBaseApi.isBaseActive()) ClientBaseApi.deactivateBase(); } );

		registerBaseInteraction("inventory", (client) -> client.setScreen(new InventoryScreen(client.player)));
		registerBaseInteraction("advancements", (client) -> client.setScreen(new AdvancementsScreen(client.player.connection.getAdvancements())));
		registerBaseInteraction("options", (client) -> client.setScreen(new OptionsScreen(null, client.options)));
		registerBaseInteraction("video_options", (client) -> client.setScreen(new VideoSettingsScreen(null, client, client.options)));
	}

	static void registerBaseInteraction(String name, Consumer<Minecraft> action) {
		Registry.register(ClientBaseRegistries.POS_INTERACTION, ResourceLocation.parse(name), action);
	}

}
