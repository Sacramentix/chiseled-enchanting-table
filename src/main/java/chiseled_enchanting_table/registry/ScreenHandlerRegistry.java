package chiseled_enchanting_table.registry;

import chiseled_enchanting_table.ChiseledEnchantingTable;
import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ScreenHandlerRegistry {

	public static final ExtendedScreenHandlerType<ChiseledEnchantingTableScreenHandler, ChiseledEnchantingTableScreenHandler.AvailableEnchantmentPayload> 
        CHISELED_ENCHANTING_TABLE_SCREEN_HANDLER = 
            new ExtendedScreenHandlerType<>(
				ChiseledEnchantingTableScreenHandler::new,
				ChiseledEnchantingTableScreenHandler.AvailableEnchantmentPayload.AVAILABLE_CHISELED_ENCHANTMENT_CODEC
			);

	public static void init() {
		Registry.register(Registries.SCREEN_HANDLER, ChiseledEnchantingTable.identifier("chiseled_enchenting_table"), CHISELED_ENCHANTING_TABLE_SCREEN_HANDLER);
		ChiseledEnchantingTableScreenHandler.init();
	}
}
