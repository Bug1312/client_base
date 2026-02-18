package com.bug1312.client_base.api.config.type;

import java.io.Serializable;

import net.minecraft.resources.ResourceLocation;

public interface InteractionType extends Serializable {
	public ResourceLocation getInteractionId();
}
