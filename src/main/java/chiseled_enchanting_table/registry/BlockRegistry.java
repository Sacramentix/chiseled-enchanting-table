package chiseled_enchanting_table.registry;

import java.util.function.Function;

import chiseled_enchanting_table.ChiseledEnchantingTable;
import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class BlockRegistry {

	public static final Block CHISELED_ENCHANTING_TABLE = 
        register(
            "chiseled_enchanting_table",
            ChiseledEnchantingTableBlock::new, 
            Block.Settings.create()
                .mapColor(MapColor.RED)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresTool()
                .luminance(state -> 7)
                .strength(5.0F, 1200.0F)
        );

    private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = ChiseledEnchantingTable.identifier(id);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);
    
        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);
        return block;
    }

	public static void init() {
        
	}
}