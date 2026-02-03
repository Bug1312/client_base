package com.bug1312.client_base.base.config.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

/**
 * Helper class for helping compare and create blockstates from strings,
 * 	attempting to simulate inputs from datapack & resourcepack JSON files
 */
public class BlockStateMatcher {
	private final Block block;
	private final Map<String, String> properties;

	private BlockStateMatcher(Block block, Map<String, String> properties) {
		this.block = block;
		this.properties = properties;
	}

	private static Map<String, String> parseStateString(String stateString) {
		Map<String, String> properties = new HashMap<>();

		if (stateString == null || stateString.trim().isEmpty()) return properties;

		String[] pairs = stateString.split(",");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=", 2);
			if (keyValue.length == 2) properties.put(keyValue[0].trim(), keyValue[1].trim());
		}

		return properties;
	}

	/**
	 * Used to parse strings familiar with the blockstate JSON variant models into
	 * 	a class for matching blockstates with desired input.
	 *
	 * @param blockId Block {@link Identifier}
	 * @param stateString Blockstate variant string. Does not need to be single blockstate with all properties
	 *
	 * @apiNote A highly adaptable blockstate that has no use in the Client Base is
	 * 	a noteblock.
	 *
	 * @example
	 * 	<code>{@link BlockStateMatcher#of}({@link Identifier#ofVanilla}("noteblock"), "instrument=harp,note=1");</code>
	 */
	@Nullable
	public static BlockStateMatcher of(Identifier blockId, String stateString) {
		if (!Registries.BLOCK.containsId(blockId)) return null;

		Block block = Registries.BLOCK.get(blockId);
		Map<String, String> properties = parseStateString(stateString);

		return new BlockStateMatcher(block, properties);
	}

	private Optional<Property<?>> findProperty(BlockState state, String name) {
		return state.getProperties().stream().filter(p -> p.getName().equals(name)).findFirst();
	}

	private <T extends Comparable<T>> String getPropertyValue(BlockState state, Property<T> property) {
		T value = state.get(property);
		return property.name(value);
	}

	public boolean matches(BlockState state) {
		if (!state.isOf(block)) return false;

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			String propertyName = entry.getKey();
			String expectedValue = entry.getValue();

			Optional<Property<?>> property = findProperty(state, propertyName);
			if (property.isEmpty()) return false;

			String actualValue = getPropertyValue(state, property.get());
			if (!expectedValue.equals(actualValue)) return false;
		}

		return true;
	}

	@Nullable
	private <T extends Comparable<T>> BlockState setPropertyValue(BlockState state, Property<T> property, String value) {
		Optional<T> parsed = property.parse(value);
		if (parsed.isEmpty()) return null;
		return state.with(property, parsed.get());
	}

	@Nullable
	public BlockState toBlockState(ClientWorld world) {
		BlockState state = block.getDefaultState();

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			String propertyName = entry.getKey();
			String value = entry.getValue();

			Optional<Property<?>> property = findProperty(state, propertyName);
			if (property.isEmpty()) return null;

			state = setPropertyValue(state, property.get(), value);
			if (state == null) return null;
		}

		return state;
	}
}