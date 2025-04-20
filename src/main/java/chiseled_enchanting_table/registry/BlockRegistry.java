package chiseled_enchanting_table.registry;

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

public class BlockRegistry {

	public static final Block CHISELED_ENCHANTING_TABLE = 
        registerBlock(
            "chiseled_enchanting_table",
            new ChiseledEnchantingTableBlock(
                AbstractBlock.Settings.create()
                    .mapColor(MapColor.RED)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .luminance(state -> 7)
                    .strength(5.0F, 1200.0F)
            ),
            ItemGroups.FUNCTIONAL
        );

	private static Block registerBlock(String name, Block block, RegistryKey<ItemGroup> itemGroup) {
		Registry.register(
            Registries.ITEM,
            ChiseledEnchantingTable.identifier(name),
            new BlockItem(block, new Item.Settings())
        );
		ItemGroupEvents.modifyEntriesEvent(itemGroup)
            .register(content -> content.add(block));
		return Registry.register(
            Registries.BLOCK,
            ChiseledEnchantingTable.identifier(name),
            block
        );
	}

	public static void init() {
        
	}
}