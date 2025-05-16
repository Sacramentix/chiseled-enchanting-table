package chiseled_enchanting_table.registry;

import java.util.function.Function;

import chiseled_enchanting_table.ChiseledEnchantingTable;
import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

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
        // registerBlock(
        //     "chiseled_enchanting_table",
        //     new ChiseledEnchantingTableBlock(
        //         AbstractBlock.Settings.create()

        //     ),
        //     ItemGroups.FUNCTIONAL
        // );

    private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = ChiseledEnchantingTable.identifier(id);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);
    
        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);
        return block;
    }


	// private static Block registerBlock(String name, Block block, RegistryKey<ItemGroup> itemGroup) {
	// 	Registry.register(
    //         Registries.ITEM,
    //         ChiseledEnchantingTable.identifier(name),
    //         new BlockItem(block, new Item.Settings())
    //     );
	// 	ItemGroupEvents.modifyEntriesEvent(itemGroup)
    //         .register(content -> content.add(block));
	// 	return Registry.register(
    //         Registries.BLOCK,
    //         ChiseledEnchantingTable.identifier(name),
    //         block
    //     );
	// }

	public static void init() {
        
	}
}