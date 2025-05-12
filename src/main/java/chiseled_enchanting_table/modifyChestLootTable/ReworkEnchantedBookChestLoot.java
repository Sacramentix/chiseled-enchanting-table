package chiseled_enchanting_table.modifyChestLootTable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import chiseled_enchanting_table.modifyChestLootTable.abandoned_mineshaft.DepthBasedEfficiency;
import chiseled_enchanting_table.modifyChestLootTable.abandoned_mineshaft.DepthBasedEfficiency.DepthBasedEfficiencyBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.fabric.mixin.registry.sync.RegistriesAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.function.SetLoreLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ReworkEnchantedBookChestLoot {

    public static void init() {

        LootTableEvents.MODIFY.register(
                (key, tableBuilder, source, registries) -> modifyLootTable(key, tableBuilder, source, registries));
    }

    private static void modifyLootTable(
            RegistryKey<LootTable> key, LootTable.Builder tableBuilder,
            LootTableSource source, RegistryWrapper.WrapperLookup registries) {
        if (!source.isBuiltin())
            return;
        var id = key.getValue().toString();
        if (!id.startsWith("minecraft:chests/"))
            return;
        var chestType = id.substring("minecraft:chests/".length());
        
        // remove all vanilla enchanted book from chest loot table
        tableBuilder.apply(RemoveEnchantedBooksFunction.INSTANCE);

        var fromChiseledEnchantingTableLore =
            new SetLoreLootFunction.Builder()
            .lore(
                Text.literal("Chiseled Enchanting Table")
                .styled(style -> style.withColor(0x800080))
            );

        var enchantmentRegistry = registries.getOptionalWrapper(RegistryKeys.ENCHANTMENT).get();

        Function<String, Reference<Enchantment>> enchantEntry = 
            (String s) -> enchantmentRegistry.getOptional(RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(s))).get();

        BiFunction<String, Integer, SetEnchantmentsLootFunction.Builder> enchantWithLevel = 
            (String enchantName, Integer level) -> {
                return new SetEnchantmentsLootFunction.Builder()
                .enchantment(
                    enchantEntry.apply(enchantName),
                    ConstantLootNumberProvider.create(level)
                );
            };
        
        if (chestType.equals("abandoned_mineshaft")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(DepthBasedEfficiencyBuilder.INSTANCE)
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
            tableBuilder.apply(DepthBasedEfficiency.INSTANCE);
        } else if (chestType.equals("igloo_chest")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("frost_walker", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        }
    }

    public static void removeExistingEnchantedBookFromChest(LootTable.Builder tableBuilder) {
        // tableBuilder.
        // tableBuilder.pools.forEach(pool -> {
        // // Create a list to keep track of entries to remove
        // List<LootPoolEntry> entriesToRemove = new ArrayList<>();

        // // Find all enchanted book entries
        // for (LootPoolEntry entry : pool.entries) {
        // // Check if the entry is an ItemEntry for enchanted books
        // // This is a simplified check - in practice you may need more sophisticated
        // detection
        // if (entry instanceof ItemEntry &&
        // ((ItemEntry) entry).getItem() == Items.ENCHANTED_BOOK) {
        // entriesToRemove.add(entry);
        // }
        // }

        // // Remove all enchanted book entries
        // pool.entries.removeAll(entriesToRemove);
        // });
    }
}
