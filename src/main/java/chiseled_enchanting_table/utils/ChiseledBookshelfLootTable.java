package chiseled_enchanting_table.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import chiseled_enchanting_table.integration.colorful_books.ColorBook;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ChiseledBookshelfLootTable {
    
    public static NbtCompoundWithBlockState fillWithSeededRandomBook(ServerWorld world, BlockPos blockPos) {
        var seed = world.getSeed() + blockPos.getX() * 31L + blockPos.getY() * 37L + blockPos.getZ() * 41L;
        var randomGenerator = new java.util.Random(seed);

        var enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);

        var enchantmentsByMaxLevel = new HashMap<Integer, List<RegistryEntry<Enchantment>>>();

        var enchantmentsByItems = new HashMap<RegistryEntry<Item>, List<RegistryEntry<Enchantment>>>();

        for (int level = 1; level <= 5; level++) {
            enchantmentsByMaxLevel.put(level, new ArrayList<>());
        }
        
        enchantmentRegistry.streamEntries().forEach(enchantEntry -> {
            var enchant = enchantEntry.value();
            var maxLevel = enchant.getMaxLevel();
            
            for (var item : enchant.getApplicableItems()) {
                enchantmentsByItems.putIfAbsent(item, new ArrayList<>());
                enchantmentsByItems.get(item).add(enchantEntry);
            }
            for (var level = 1; level <= 5; level++) {
                if (maxLevel >= level) {
                    enchantmentsByMaxLevel.get(level).add(enchantEntry);
                }
            }
        });

        Function<Integer, ItemStack> randomEnchantFromLevel = 
            (Integer level) -> {
                var book = new ItemStack(Items.ENCHANTED_BOOK);
                var enchantments = enchantmentsByMaxLevel.get(level);
                var randomEnchant = enchantments.stream()
                    .skip(randomGenerator.nextInt(enchantments.size()))
                    .findFirst()
                    .orElseThrow();
                book.addEnchantment(randomEnchant, level);
                ColorBook.enchantedWithConfig(book);
                addLore(book);
                return book;
            };

        Function<RegistryEntry<Item>, ItemStack> enchantSetForItemPool = 
            (RegistryEntry<Item> item) -> {
                var book = new ItemStack(Items.ENCHANTED_BOOK);
                var r1 = randomGenerator.nextInt(1000);
                var numberOfEnchant =
                    r1 > 800 ? 4 :
                    r1 > 500 ? 3 :
                               2 ;
                var enchants = enchantmentsByItems.get(item);
                Collections.shuffle(enchants, randomGenerator);
                
                // var enchantToApply = .collect(Collectors.toList());

                enchants.stream().limit(numberOfEnchant).forEach(enchant->{
                    var r2 = randomGenerator.nextInt(1000);
                    var level =
                        r2 > 850 ? 5 :
                        r2 > 650 ? 4 :
                        r2 > 350 ? 3 :
                                   2 ;
                    var cap = enchant.value().getMaxLevel();
                    var cappedLevel =  cap < level ? cap : level;
                    book.addEnchantment(enchant, cappedLevel);
                });
                ColorBook.enchantedWithConfig(book);
                addLore(book);
                return book;
            };
        Supplier<ItemStack> enchantSetForRandomItemPool = 
            () -> {
                var randomItem = enchantmentsByItems.keySet()
                    .stream()
                    .skip(randomGenerator.nextInt(enchantmentsByItems.size()))
                    .findFirst()
                    .orElseThrow();
                return enchantSetForItemPool.apply(randomItem);
            };
        
        var r = randomGenerator.nextInt(1000);
        var fillingType = 
            r > 800 ? 0 :
            r > 200 ? 1 :
                      2 ;
        
        

        var nbt = new NbtCompound();
        var nbtItems = new NbtList();
        var blockState = Blocks.CHISELED_BOOKSHELF.getDefaultState();
        BooleanProperty[] slots = {
            Properties.SLOT_0_OCCUPIED,
            Properties.SLOT_1_OCCUPIED,
            Properties.SLOT_2_OCCUPIED,
            Properties.SLOT_3_OCCUPIED,
            Properties.SLOT_4_OCCUPIED,
            Properties.SLOT_5_OCCUPIED,
        };
        
        if (fillingType == 0) {
            var i = 0;
            for (var slot : slots) {
                blockState = blockState.with(slot, true);

                var r2 = randomGenerator.nextInt(10_000_000);
                var regitryOps = RegistryOps.of(NbtOps.INSTANCE, world.getRegistryManager());
                // Enchant Set
                if (r2 < 50_000) {
                    var book = enchantSetForRandomItemPool.get();
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 5
                } else if (r2 < 90_000) {
                    var book = randomEnchantFromLevel.apply(5);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 4
                } else if (r2 < 150_000) {
                    var book = randomEnchantFromLevel.apply(4);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 3
                } else if (r2 < 300_000) {
                    var book = randomEnchantFromLevel.apply(3);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 2
                } else if (r2 < 400_000) {
                    var book = randomEnchantFromLevel.apply(2);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 1
                } else if (r2 < 450_000) {
                    var book = randomEnchantFromLevel.apply(1);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                } else {
                    var book = new ItemStack(Items.BOOK);
                    ColorBook.randomly(book, randomGenerator);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                }
                i++;
            }
        } else if (fillingType == 1) {
            var i = 0;
            for (var slot : slots) {

                var r2 = randomGenerator.nextInt(20_000_000);
                if (r2 > 10_000_000) {
                    blockState = blockState.with(slot, false);
                    i++;
                    continue;
                } else {
                    blockState = blockState.with(slot, true);
                }
                var regitryOps = RegistryOps.of(NbtOps.INSTANCE, world.getRegistryManager());
                // Enchant Set
                if (r2 < 50_000) {
                    var book = enchantSetForRandomItemPool.get();
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 5
                } else if (r2 < 90_000) {
                    var book = randomEnchantFromLevel.apply(5);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 4
                } else if (r2 < 150_000) {
                    var book = randomEnchantFromLevel.apply(4);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 3
                } else if (r2 < 300_000) {
                    var book = randomEnchantFromLevel.apply(3);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 2
                } else if (r2 < 400_000) {
                    var book = randomEnchantFromLevel.apply(2);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                // Enchant level 1
                } else if (r2 < 450_000) {
                    var book = randomEnchantFromLevel.apply(1);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                } else {
                    var book = new ItemStack(Items.BOOK);
                    ColorBook.randomly(book, randomGenerator);
                    var nbtBook = ItemStack.CODEC.encodeStart(regitryOps, book).getOrThrow().asCompound().get();
                    nbtBook.putByte("Slot", (byte) i);
                    nbtItems.add(nbtBook);
                }
                i++;
            }
        } else if (fillingType == 2) {
            for (var slot : slots) {
                blockState = blockState.with(slot, false);
            }
        }

        nbt.put("Items", nbtItems);
        return new NbtCompoundWithBlockState(nbt, blockState);
    }

    public static void addLore(ItemStack itemStack) {
        var componentBuilder = ComponentChanges.builder();
		componentBuilder.add(
            DataComponentTypes.LORE,
            new LoreComponent(List.of(
                Text.literal("Chiseled Enchanting Table")
                    .styled(style -> style.withColor(0x800080))
            ))
        );
        itemStack.applyChanges(componentBuilder.build());
    }

    public record NbtCompoundWithBlockState(NbtCompound nbt, BlockState state) {
        public NbtCompoundWithBlockState(NbtCompound nbt, BlockState state) {
            this.nbt = nbt;
            this.state = state; // Ensure immutability
        }
    }
}
