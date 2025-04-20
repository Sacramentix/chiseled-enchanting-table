package chiseled_enchanting_table.registry;

import chiseled_enchanting_table.ChiseledEnchantingTable;
import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntityRegistry {

	public static final BlockEntityType<ChiseledEnchantingTableBlockEntity> CHISELED_ENCHANTING_TABLE_ENTITY_TYPE = 
        Registry.register(Registries.BLOCK_ENTITY_TYPE,
			ChiseledEnchantingTable.identifier("chiseled_enchanting_table"),
            BlockEntityType.Builder.create(
                ChiseledEnchantingTableBlockEntity::new,
                BlockRegistry.CHISELED_ENCHANTING_TABLE
            ).build()
        );

	public static void init() {
	}
}