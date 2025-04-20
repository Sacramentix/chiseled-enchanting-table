package chiseled_enchanting_table.utils;

import java.util.stream.Stream;

import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;

import net.minecraft.world.World;

public class EnchantmentFinder {

    /**
     * A function to get all enchantement contained in a chiseled bookshelf
     * @param cbsbe the chiseledBookshelf block entity
     * @return a Stream of map entry where the key is the enchantment and the value is the level
     */
    public static 
    Stream<EnchantmentWithLevel> 
    streamAllEnchantements(ChiseledBookshelfBlockEntity cbsbe, World world) {
        return Stream.iterate(0, i -> i + 1)
            .limit(cbsbe.size())
            .map(cbsbe::getStack)
            .flatMap(stack -> {
                ItemEnchantmentsComponent itemEnchantmentsComponent = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
                if (itemEnchantmentsComponent == null) return Stream.empty();
                return itemEnchantmentsComponent
                    .getEnchantmentEntries()
                    .stream()
                    .map((e)->
                        // The enchantment entry consist of a RegistryEntry<Enchantment> as a key
                        // and a int as value that correspond to the enchant level
                        new EnchantmentWithLevel(
                            EnchantmentWithLevel.EnchantmentToIdentifier(e.getKey().value(), world),
                            e.getIntValue()
                        )
                    );
            });
    }
}