package chiseled_enchanting_table.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class ChiseledBookshelfLootTable {
    
    public static NbtCompoundWithOccupiedSlots fillWithSeededRandomBook(ServerWorld world, BlockPos blockPos) {
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
        var nbt = new NbtCompound();
        var nbtItems = new NbtList();
        var occupiedSlots = new HashSet<Integer>();
        
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
                var nbtBook = (NbtCompound) enchanted_book.encode(world.getRegistryManager());
                nbtBook.putByte("Slot", (byte) j);
                nbtItems.add(nbtBook);
                occupiedSlots.add(i);
                
            } else if (random < 70) {
                var book = new ItemStack(Items.BOOK);
                var nbtBook = (NbtCompound) book.encode(world.getRegistryManager());
                nbtBook.putByte("Slot", (byte) j);
                nbtItems.add(nbtBook);
                
            }
            
        }
        nbt.put("Items", nbtItems);
        return new NbtCompoundWithOccupiedSlots(nbt, occupiedSlots);
    }

    public record NbtCompoundWithOccupiedSlots(NbtCompound nbt, Set<Integer> slots) {
        public NbtCompoundWithOccupiedSlots(NbtCompound nbt, Set<Integer> slots) {
            this.nbt = nbt;
            this.slots = Set.copyOf(slots); // Ensure immutability
        }
    }
}
