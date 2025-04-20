package chiseled_enchanting_table;

import chiseled_enchanting_table.gui.ChiseledEnchantingTableScreen;
import chiseled_enchanting_table.registry.BlockRegistry;
import chiseled_enchanting_table.registry.EntityRegistry;
import chiseled_enchanting_table.registry.ScreenHandlerRegistry;
import chiseled_enchanting_table.render.ChiseledEnchantingTableBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.component.DataComponentTypes;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantingTableClient implements ClientModInitializer {
	// public static ClientConfig CLIENT_CONFIG;

	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(EntityRegistry.CHISELED_ENCHANTING_TABLE_ENTITY_TYPE, ChiseledEnchantingTableBlockEntityRenderer::new);
		HandledScreens.register(ScreenHandlerRegistry.CHISELED_ENCHANTING_TABLE_SCREEN_HANDLER, ChiseledEnchantingTableScreen::new);
		ColorProviderRegistry.BLOCK.register(
			(state, view, pos, tintIndex) -> 
				view != null && view.getBlockEntityRenderData(pos) instanceof Integer integer ? integer : 0x000000,
			BlockRegistry.CHISELED_ENCHANTING_TABLE
		);
		BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.CHISELED_ENCHANTING_TABLE, RenderLayer.getCutoutMipped());
		ColorProviderRegistry.ITEM.register(
			(stack, tintIndex) -> {
				var dyed_color = stack.get(DataComponentTypes.DYED_COLOR);
				return dyed_color != null ? dyed_color.rgb() : 0xA020F0;
			}, 
			BlockRegistry.CHISELED_ENCHANTING_TABLE
		);

	}
}