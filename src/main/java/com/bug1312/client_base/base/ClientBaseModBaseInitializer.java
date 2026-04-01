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
import com.bug1312.client_base.core.ClientBaseModCoreInitializer;
import com.bug1312.client_base.core.util.SetupUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ClientBaseModBaseInitializer implements ClientModInitializer {

	public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(ClientBaseModCoreInitializer.MOD_ID, "keybinds"));
	public static final KeyMapping KEYBIND = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.client_base.toggle", GLFW.GLFW_KEY_HOME, CATEGORY));

	@Override
	public void onInitializeClient() {
		ModelLoadingPlugin.register(new ClientBaseModelLoadingPlugin());

		Registry.register(ClientBaseRegistries.RENDERER_TYPE, Identifier.parse("block_entity"), new BlockEntityRenderer.Deserializer());
		Registry.register(ClientBaseRegistries.RENDERER_TYPE, Identifier.parse("fake_block"), new FakeBlockRenderer.Deserializer());
		Registry.register(ClientBaseRegistries.RENDERER_TYPE, Identifier.parse("replace_state"), new BlockModelRenderer.Deserializer());

		Registry.register(ClientBaseRegistries.INTERACTION_TYPE, Identifier.parse("blockpos"), new BlockPosInteraction.Deserializer());
		ClientBaseEvents.INTERACT.register((interaction, _, _, hitResult) -> {
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
		registerBaseInteraction("exit", (_) -> { if (ClientBaseApi.isBaseActive()) ClientBaseApi.deactivateBase(); } );

		registerBaseInteraction("inventory", (client) -> client.setScreen(new InventoryScreen(client.player)));
		registerBaseInteraction("advancements", (client) -> client.setScreen(new AdvancementsScreen(client.player.connection.getAdvancements())));
		registerBaseInteraction("options", (client) -> client.setScreen(new OptionsScreen(null, client.options, true)));
		registerBaseInteraction("video_options", (client) -> client.setScreen(new VideoSettingsScreen(null, client, client.options)));
	}

	static void registerBaseInteraction(String name, Consumer<Minecraft> action) {
		Registry.register(ClientBaseRegistries.POS_INTERACTION, Identifier.parse(name), action);
	}

}
