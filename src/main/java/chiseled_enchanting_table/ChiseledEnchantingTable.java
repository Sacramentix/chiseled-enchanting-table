package chiseled_enchanting_table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chiseled_enchanting_table.modifyChestLootTable.ReworkEnchantedBookChestLoot;
import chiseled_enchanting_table.modifyChestLootTable.abandoned_mineshaft.DepthBasedEfficiency;
import chiseled_enchanting_table.modifyChestLootTable.RemoveEnchantedBooksFunction;
import chiseled_enchanting_table.registry.BlockRegistry;
import chiseled_enchanting_table.registry.EntityRegistry;
import chiseled_enchanting_table.registry.ScreenHandlerRegistry;
import chiseled_enchanting_table.registry.StructureProcessorRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.SaveLoading.ServerConfig;
import net.minecraft.util.Identifier;

public class ChiseledEnchantingTable implements ModInitializer {
	public static final String MOD_ID = "chiseled_enchanting_table";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ServerConfig SERVER_CONFIG;


	@Override
	public void onInitialize() {
		LOGGER.info("Chiseling the enchant system!");
		// SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.BOTH);
		BlockRegistry.init();
		EntityRegistry.init();
		ScreenHandlerRegistry.init();
		StructureProcessorRegistry.init();
		RemoveEnchantedBooksFunction.register();
		DepthBasedEfficiency.register();
		ReworkEnchantedBookChestLoot.init();
	}

	

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}