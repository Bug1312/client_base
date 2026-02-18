package com.bug1312.client_base.api;

import com.bug1312.client_base.api.config.type.InteractionType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

@Environment(EnvType.CLIENT)
public class ClientBaseEvents {

	/**
	 * An event that is called when a player makes a block interaction inside a Client Base.
	 */
	public static final Event<Interact> INTERACT = EventFactory.createArrayBacked(Interact.class, callbacks -> (interaction, player, hand, hitResult) -> {
		for (Interact callback : callbacks) {
			callback.onInteraction(interaction, player, hand, hitResult);
		}
	});

	@FunctionalInterface
	public interface Interact {
		void onInteraction(InteractionType interaction, LocalPlayer player, InteractionHand hand, BlockHitResult hitResult);
	}
}
