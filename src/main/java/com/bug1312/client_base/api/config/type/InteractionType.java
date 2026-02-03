package com.bug1312.client_base.api.config.type;

import java.io.Serializable;

import net.minecraft.util.Identifier;

public interface InteractionType extends Serializable {
	public Identifier getInteractionId();
}
