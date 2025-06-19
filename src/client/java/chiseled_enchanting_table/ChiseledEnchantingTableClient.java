package chiseled_enchanting_table;

import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableBlockEntity;
import chiseled_enchanting_table.gui.ChiseledEnchantingTableScreen;
import chiseled_enchanting_table.registry.BlockRegistry;
import chiseled_enchanting_table.registry.EntityRegistry;
import chiseled_enchanting_table.registry.ScreenHandlerRegistry;
import chiseled_enchanting_table.render.ChiseledEnchantingTableBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantingTableClient implements ClientModInitializer {
	// public static ClientConfig CLIENT_CONFIG;

	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(EntityRegistry.CHISELED_ENCHANTING_TABLE_ENTITY_TYPE, ChiseledEnchantingTableBlockEntityRenderer::new);
		HandledScreens.register(ScreenHandlerRegistry.CHISELED_ENCHANTING_TABLE_SCREEN_HANDLER, ChiseledEnchantingTableScreen::new);
		ColorProviderRegistry.BLOCK.register(
			(state, view, pos, tintIndex) -> 
				view != null && view.getBlockEntityRenderData(pos) instanceof Integer integer ? integer : ChiseledEnchantingTableBlockEntity.DEFAULT_COLOR,
			BlockRegistry.CHISELED_ENCHANTING_TABLE
		);
		BlockRenderLayerMap.putBlock(BlockRegistry.CHISELED_ENCHANTING_TABLE, BlockRenderLayer.CUTOUT_MIPPED);
	}
}