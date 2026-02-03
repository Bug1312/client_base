package com.bug1312.client_base.core.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * Utility class for turning {@link JsonElement} into commonly converted types.
 */
public class DeserializeUtil {

	public static Identifier toIdentifier(JsonElement element) throws JsonParseException {
		String string = element.getAsString();
		Identifier out = Identifier.tryParse(string);

		if (out == null) throw new JsonParseException(string + " is not a valid resource location");

		return out;
	}

	public static BlockPos toBlockPos(JsonElement element) throws JsonParseException {
		JsonArray jsonArray = element.getAsJsonArray();
		if (jsonArray.size() != 3) throw new JsonParseException(element + " is not a valid blockpos");

		return new BlockPos(
			jsonArray.get(0).getAsInt(),
			jsonArray.get(1).getAsInt(),
			jsonArray.get(2).getAsInt()
		);
	}

	public static Vec3d toVec3d(JsonElement element) throws JsonParseException {
		JsonArray jsonArray = element.getAsJsonArray();
		if (jsonArray.size() != 3) throw new JsonParseException(element + " is not a valid position");

		return new Vec3d(
			jsonArray.get(0).getAsDouble(),
			jsonArray.get(1).getAsDouble(),
			jsonArray.get(2).getAsDouble()
		);
	}

	private static List<List<Double>> toDoubleMatrix(JsonElement element) throws JsonParseException {
		List<List<Double>> out = new ArrayList<>();

		JsonArray outerArray = element.getAsJsonArray();
		for (JsonElement rowElement : outerArray) {
			JsonArray innerArray = rowElement.getAsJsonArray();
			List<Double> row = new ArrayList<>(innerArray.size());
			for (JsonElement columnElement : innerArray) row.add(columnElement.getAsDouble());
			out.add(row);
		}

		return out;
	}

	public static VoxelShape toVoxelShape(JsonElement element) throws JsonParseException {
		List<List<Double>> array = toDoubleMatrix(element);

		VoxelShape out = VoxelShapes.empty();

		for (List<Double> shapeArray : array) {
			int size = shapeArray.size();
			if (size < 6) throw new JsonParseException(String.format("Shape array too short: Expected %d, got %d", 6, size));
			if (size > 6) throw new JsonParseException(String.format("Shape array too long: Expected %d, got %d", 6, size));

			out = VoxelShapes.union(out, VoxelShapes.cuboid(
				shapeArray.get(0)/16D,
				shapeArray.get(1)/16D,
				shapeArray.get(2)/16D,
				shapeArray.get(3)/16D,
				shapeArray.get(4)/16D,
				shapeArray.get(5)/16D
			));
		}

		return out;
	}

}
