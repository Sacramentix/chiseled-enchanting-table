package chiseled_enchanting_table.utils;

import java.util.stream.Stream;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class ChiseledBookshelfLootTable {
    
    public static void fillWithSeededRandomBook(ServerWorld world, BlockPos blockPos) {
        var seed = world.getSeed() + blockPos.getX() * 31L + blockPos.getY() * 37L + blockPos.getZ() * 41L;
        var randomGenerator = Random.create(seed);
        
        // The set nbt method don't seems to work at the moment

        // var nbt = new NbtCompound();
        // for (int i = 0; i < ChiseledBookshelfBlockEntity.MAX_BOOKS; i++) {
        //     final int j = i;
        //     var random = randomGenerator.nextInt(1000);
        //     @SuppressWarnings("unchecked")
        //     var enchantments = (Stream<RegistryEntry<Enchantment>>)(Object) world.getRegistryManager()
        //     .get(RegistryKeys.ENCHANTMENT)
        //     .streamEntries();
        //     if (random < 20) {
        //     var enchanted_book = EnchantmentHelper.enchant(
        //         randomGenerator,
        //         new ItemStack(Items.BOOK),
        //         50,
        //         enchantments
        //     );
        //     nbt.put("Slot" + j, enchanted_book.encode(world.getRegistryManager()));
        //     } else if (random < 70) {
        //     var book = new ItemStack(Items.BOOK);
        //     nbt.put("Slot" + j, book.encode(world.getRegistryManager()));
        //     }
        // }
        // return nbt;

        // Might be a bot slow because it force to add block using main thread :()

        for (int i = 0; i < ChiseledBookshelfBlockEntity.MAX_BOOKS; i++) {
            final int j = i;
            var random = randomGenerator.nextInt(1000);
            @SuppressWarnings("unchecked")
            var enchantments = (Stream<RegistryEntry<Enchantment>>)(Object) world.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT)
                .streamEntries();
            if (random < 20) {
                var enchanted_book = EnchantmentHelper.enchant(
                    randomGenerator,
                    new ItemStack(Items.BOOK),
                    50,
                    enchantments
                );
                world.getServer().execute(() -> {
                    if (!(world.getBlockEntity(blockPos) instanceof ChiseledBookshelfBlockEntity cbsbe)) return;
                    cbsbe.setStack(j, enchanted_book);
                });
                
            } else if (random < 70) {
                world.getServer().execute(() -> {
                    if (!(world.getBlockEntity(blockPos) instanceof ChiseledBookshelfBlockEntity cbsbe)) return;
                    cbsbe.setStack(j, new ItemStack(Items.BOOK));
                });
                
            }
        }
    }
}
