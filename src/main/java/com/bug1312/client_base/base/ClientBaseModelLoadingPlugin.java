package com.bug1312.client_base.base;

import java.util.HashMap;
import java.util.Map;

import com.bug1312.client_base.base.config.renderer.BlockModelRenderer;
import com.bug1312.client_base.base.config.renderer.FakeBlockRenderer;
import com.bug1312.client_base.core.config.ClientBaseConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientBaseModelLoadingPlugin implements ModelLoadingPlugin {
	public static final Map<Identifier, ExtraModelKey<BlockStateModel>> MODEL_KEY_MAP = new HashMap<>();

	@Override
	public void initialize(Context pluginContext) {
		if (ClientBaseConfig.getInstance() == null) return;

		for (var entry : ClientBaseConfig.getInstance().renderers().entrySet()) {
			if ((entry.getValue() instanceof BlockModelRenderer blockModelRenderer)) {
				ExtraModelKey<BlockStateModel> modelKey = ExtraModelKey.create();
				Identifier model = blockModelRenderer.model();
				MODEL_KEY_MAP.put(model, modelKey);
				pluginContext.addModel(modelKey, SimpleUnbakedExtraModel.blockStateModel(model));
			}

			if ((entry.getValue() instanceof FakeBlockRenderer blockModelRenderer)) {
				ExtraModelKey<BlockStateModel> modelKey = ExtraModelKey.create();
				Identifier model = blockModelRenderer.model();
				MODEL_KEY_MAP.put(model, modelKey);
				pluginContext.addModel(modelKey, SimpleUnbakedExtraModel.blockStateModel(model));
			}
		}
	}

}