package ninja.trek.chiseledenchanting;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantingClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRendererRegistry.register(
				ChiseledEnchanting.CHISELED_ENCHANTING_TABLE_BLOCK_ENTITY,
				ChiseledEnchantmentTableBlockEntityRenderer::new
		);

	}
}