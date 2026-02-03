package com.bug1312.client_base.base;

import java.util.ArrayList;
import java.util.List;

import com.bug1312.client_base.base.config.renderer.BlockModelRenderer;
import com.bug1312.client_base.base.config.renderer.FakeBlockRenderer;
import com.bug1312.client_base.core.config.ClientBaseConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientBaseModelLoadingPlugin implements ModelLoadingPlugin {
	public static final List<Identifier> MODEL_LIST = new ArrayList<>();

	@Override
	public void onInitializeModelLoader(Context pluginContext) {
		if (ClientBaseConfig.getInstance() == null) return;

		for (var entry : ClientBaseConfig.getInstance().renderers().entrySet()) {
			if ((entry.getValue() instanceof BlockModelRenderer blockModelRenderer)) {
				Identifier model = blockModelRenderer.model();
				pluginContext.addModels(model);
				MODEL_LIST.add(model);
			}

			if ((entry.getValue() instanceof FakeBlockRenderer blockModelRenderer)) {
				Identifier model = blockModelRenderer.model();
				pluginContext.addModels(model);
				MODEL_LIST.add(model);
			}
		}
	}

}