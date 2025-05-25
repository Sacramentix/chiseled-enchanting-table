package chiseled_enchanting_table.registry;

import java.util.function.Function;

import chiseled_enchanting_table.ChiseledEnchantingTable;
import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class BlockRegistry {

	public static final Block CHISELED_ENCHANTING_TABLE = 
        register(
            "chiseled_enchanting_table",
            ChiseledEnchantingTableBlock::new, 
            Block.Settings.create()
                .mapColor(MapColor.PURPLE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresTool()
                .luminance(state -> 7)
                .strength(5.0F, 1200.0F),
            true
        );


    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
		// Create a registry key for the block
		RegistryKey<Block> blockKey = keyOfBlock(name);
		// Create the block instance
		Block block = blockFactory.apply(settings.registryKey(blockKey));

		// Sometimes, you may not want to register an item for the block.
		// Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
		if (shouldRegisterItem) {
			// Items need to be registered with a different type of registry key, but the ID
			// can be the same.
			RegistryKey<Item> itemKey = keyOfItem(name);

			BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
			Registry.register(Registries.ITEM, itemKey, blockItem);
		}

		return Registry.register(Registries.BLOCK, blockKey, block);
	}

	private static RegistryKey<Block> keyOfBlock(String name) {
		return RegistryKey.of(RegistryKeys.BLOCK, ChiseledEnchantingTable.identifier(name));
	}

	private static RegistryKey<Item> keyOfItem(String name) {
		return RegistryKey.of(RegistryKeys.ITEM, ChiseledEnchantingTable.identifier(name));
	}



	public static void init() {
        
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.add(CHISELED_ENCHANTING_TABLE.asItem());
        });
	}
}