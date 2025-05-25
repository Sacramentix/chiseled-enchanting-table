package chiseled_enchanting_table.modifyChestLootTable;

import java.util.function.BiFunction;
import java.util.function.Function;

import chiseled_enchanting_table.modifyChestLootTable.abandoned_mineshaft.DepthBasedEfficiency.DepthBasedEfficiencyBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.function.SetLoreLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ReworkEnchantedBookChestLoot {

    public static void init() {
        
        LootTableEvents.MODIFY.register(
            (key, tableBuilder, source, registries) -> modifyLootTable(key, tableBuilder, source, registries)
        );
    }

    private static void modifyLootTable(
        RegistryKey<LootTable> key, LootTable.Builder tableBuilder,
        LootTableSource source, RegistryWrapper.WrapperLookup registries
    ) {
        if (!source.isBuiltin()) return;
        var id = key.getValue().toString();


        


        var fromChiseledEnchantingTableLore =
            new SetLoreLootFunction.Builder()
            .lore(
                Text.literal("Chiseled Enchanting Table")
                .styled(style -> style.withColor(0x800080))
            );

        var enchantmentRegistry = registries.getOrThrow(RegistryKeys.ENCHANTMENT);

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

        if (id.equals("minecraft:archaeology/trail_ruins_rare")) {
            tableBuilder.modifyPools(pool -> {
                pool.with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("silk_touch", 1))
                    .weight(1)
                );
            });

        }
    
        if (!id.startsWith("minecraft:chests/")) return;
        var chestType = id.substring("minecraft:chests/".length());

        // remove all vanilla enchanted book from chest loot table
        tableBuilder.apply(RemoveEnchantedBooksFunction.INSTANCE);
        
        if (chestType.equals("abandoned_mineshaft")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    // .apply(fromChiseledEnchantingTableLore)
                    .apply(DepthBasedEfficiencyBuilder.INSTANCE)
                    .weight(1)
                );
            tableBuilder.pool(lootPool);
            // lootPool.apply(DepthBasedEfficiencyBuilder.INSTANCE);
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
        } else if (chestType.equals("ancient_city_ice_box")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("frost_walker", 2))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("desert_pyramid")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("thorns", 3))
                    .weight(100)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("jungle_temple")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fortune", 3))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("buried_treasure")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("aqua_affinity", 1))
                    .weight(30)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("respiration", 3))
                    .weight(30)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("depth_strider", 3))
                    .weight(30)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 4))
                    .weight(10)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("end_city_treasure")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("mending", 1))
                    .weight(12)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 5))
                    .weight(22)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("power", 5))
                    .weight(22)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("feather_falling", 4))
                    .weight(22)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 4))
                    .weight(22)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("ancient_city")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("swift_sneak", 1))
                    .weight(30)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("swift_sneak", 2))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("swift_sneak", 3))
                    .weight(5)
                ).with(
                    EmptyEntry.builder()
                    .weight(50)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("ruined_portal")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("flame", 1))
                    .weight(7)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_aspect", 1))
                    .weight(7)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fortune", 1))
                    .weight(50)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("efficiency", 2))
                    .weight(18)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_protection", 2))
                    .weight(18)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("shipwreck_map")) {
            var lootPool =
                LootPool.builder();

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("shipwreck_supply")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("lure", 2))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("luck_of_the_sea", 1))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("looting", 1))
                    .weight(5)
                ).with(
                    EmptyEntry.builder()
                    .weight(55)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("shipwreck_treasure")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("respiration", 1))
                    .weight(25)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 3))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fortune", 1))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("knockback", 1))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("depth_strider", 1))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("punch", 1))
                    .weight(5)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("simple_dungeon")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .bonusRolls(UniformLootNumberProvider.create(0, 1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sweeping_edge", 1))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 3))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("smite", 4))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 3))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("bane_of_arthropods", 3))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("looting", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 2))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("power", 3))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("infinity", 1))
                    .weight(5)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("stronghold_library")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 3))
                    .weight(30)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 4))
                    .weight(30)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("power", 4))
                    .weight(30)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("unbreaking", 3))
                    .weight(10)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("stronghold_crossing")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("punch", 2))
                    .weight(25)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("knockback", 2))
                    .weight(25)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sweeping_edge", 3))
                    .weight(25)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("looting", 3))
                    .weight(25)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("stronghold_corridor")) {
            var lootPool_blast_protection =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 2))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 3))
                    .weight(35)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 4))
                    .weight(15)
                ).with(
                    EmptyEntry.builder()
                    .weight(20)
                );
            var lootPool_fire_protection =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_protection", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_protection", 2))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_protection", 3))
                    .weight(35)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_protection", 4))
                    .weight(15)
                ).with(
                    EmptyEntry.builder()
                    .weight(20)
                );
            var lootPool_projectile_protection =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 2))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 3))
                    .weight(35)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 4))
                    .weight(15)
                ).with(
                    EmptyEntry.builder()
                    .weight(20)
                );
            var lootPool_protection =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 2))
                    .weight(35)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 3))
                    .weight(35)
                ).with(
                    EmptyEntry.builder()
                    .weight(20)
                );

            tableBuilder.pool(lootPool_blast_protection);
            tableBuilder.pool(lootPool_fire_protection);
            tableBuilder.pool(lootPool_projectile_protection);
            tableBuilder.pool(lootPool_protection);
        } else if (chestType.equals("woodland_mansion")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 4))
                    .weight(30)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("smite", 5))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("bane_of_arthropods", 5))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("knockback", 2))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sweeping_edge", 3))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("power", 4))
                    .weight(10)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("pillager_outpost")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("multishot", 1))
                    .weight(9)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("piercing", 3))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("piercing", 2))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("piercing", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("quick_charge", 2))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("quick_charge", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("unbreaking", 2))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("unbreaking", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("vanishing_curse", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("bastion_other")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("multishot", 1))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("piercing", 4))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("quick_charge", 3))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 4))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 4))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 3))
                    .weight(8)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("feather_falling", 3))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("unbreaking", 3))
                    .weight(5)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("binding_curse", 1))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("vanishing_curse", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("bastion_hoglin_stable")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("looting", 3))
                    .weight(35)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_aspect", 2))
                    .weight(35)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("knockback", 2))
                    .weight(8)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 4))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("binding_curse", 1))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("vanishing_curse", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("nether_bridge")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("looting", 3))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_aspect", 2))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("flame", 1))
                    .weight(5)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("infinity", 1))
                    .weight(2)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("power", 4))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("punch", 2))
                    .weight(2)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("smite", 5))
                    .weight(5)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("smite", 4))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 4))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sweeping_edge", 3))
                    .weight(4)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fire_protection", 4))
                    .weight(15)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("binding_curse", 1))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("vanishing_curse", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("bastion_treasure")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("fortune", 3))
                    .weight(25)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("silk_touch", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 4))
                    .weight(25)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 3))
                    .weight(25)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("feather_falling", 3))
                    .weight(13)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("binding_curse", 1))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("vanishing_curse", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("bastion_bridge")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 4))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("feather_falling", 2))
                    .weight(8)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 4))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("quick_charge", 3))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("multishot", 1))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("piercing", 4))
                    .weight(20)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("binding_curse", 1))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("vanishing_curse", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("underwater_ruin_big")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("aqua_affinity", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("channeling", 1))
                    .weight(10)
                )
                
                // weight(15)
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 5))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 4))
                    .weight(2)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 3))
                    .weight(3)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 2))
                    .weight(4)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 1))
                    .weight(5)
                )
                
                
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("luck_of_the_sea", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("lure", 1))
                    .weight(10)
                )
                
                // weight(15)
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("loyalty", 3))
                    .weight(2)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("loyalty", 2))
                    .weight(4)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("loyalty", 1))
                    .weight(9)
                )

                // weight(10)
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("depth_strider", 3))
                    .weight(2)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("depth_strider", 2))
                    .weight(3)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)  
                    .apply(enchantWithLevel.apply("depth_strider", 1))
                    .weight(5)
                )
                
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("unbreaking", 2))
                    .weight(8)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("binding_curse", 1))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("vanishing_curse", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("underwater_ruin_small")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("aqua_affinity", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("channeling", 1))
                    .weight(10)
                )
                
                // weight(15)
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 5))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 4))
                    .weight(2)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 3))
                    .weight(3)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 2))
                    .weight(4)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("impaling", 1))
                    .weight(5)
                )
                
                
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("luck_of_the_sea", 1))
                    .weight(10)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("lure", 1))
                    .weight(10)
                )
                
                // weight(15)
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("loyalty", 3))
                    .weight(2)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("loyalty", 2))
                    .weight(4)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("loyalty", 1))
                    .weight(9)
                )

                // weight(10)
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("depth_strider", 3))
                    .weight(2)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("depth_strider", 2))
                    .weight(3)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)  
                    .apply(enchantWithLevel.apply("depth_strider", 1))
                    .weight(5)
                )
                
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("unbreaking", 2))
                    .weight(8)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("binding_curse", 1))
                    .weight(1)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("vanishing_curse", 1))
                    .weight(1)
                );

            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village_armorer")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 1))
                    .weight(60)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("blast_protection", 2))
                    .weight(40)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_butcher")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("looting", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("looting", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_cartographer")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("infinity", 1))
                    .weight(100)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_desert_house")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("thorns", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("thorns", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_fisher")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("luck_of_the_sea", 1))
                    .weight(50)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("lure", 1))
                    .weight(50)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_fletcher")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("power", 1))
                    .weight(60)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("power", 2))
                    .weight(40)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_mason")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("efficiency", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("efficiency", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_plains_house")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_savanna_house")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_shepherd")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("unbreaking", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("unbreaking", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_snowy_house")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("feather_falling", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("feather_falling", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_taiga_house")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("smite", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("smite", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_tannery")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("projectile_protection", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_temple")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("protection", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_toolsmith")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("efficiency", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("efficiency", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        } else if (chestType.equals("village/village_weaponsmith")) {
            var lootPool =
                LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 1))
                    .weight(70)
                ).with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(fromChiseledEnchantingTableLore)
                    .apply(enchantWithLevel.apply("sharpness", 2))
                    .weight(30)
                );
        
            tableBuilder.pool(lootPool);
        }
        
        
    }

}

// ✅ minecraft:chests/abandoned_mineshaft
// minecraft:chests/ancient_city
// ✅ minecraft:chests/ancient_city_ice_box
// minecraft:chests/bastion_bridge
// minecraft:chests/bastion_hoglin_stable
// minecraft:chests/bastion_other
// minecraft:chests/bastion_treasure
// minecraft:chests/buried_treasure
// ✅ minecraft:chests/desert_pyramid
// minecraft:chests/end_city_treasure
// ✅ minecraft:chests/igloo_chest
// ✅ minecraft:chests/jungle_temple
// minecraft:chests/nether_bridge
// minecraft:chests/pillager_outpost
// minecraft:chests/ruined_portal
// minecraft:chests/shipwreck_map
// minecraft:chests/shipwreck_supply
// minecraft:chests/shipwreck_treasure
// minecraft:chests/simple_dungeon
// minecraft:chests/spawn_bonus_chest
// minecraft:chests/stronghold_corridor
// minecraft:chests/stronghold_crossing
// minecraft:chests/stronghold_library
// minecraft:chests/trial_chambers/corridor
// minecraft:chests/trial_chambers/entrance
// minecraft:chests/trial_chambers/intersection
// minecraft:chests/trial_chambers/reward
// minecraft:chests/trial_chambers/reward_common
// minecraft:chests/trial_chambers/reward_rare
// minecraft:chests/trial_chambers/reward_unique
// minecraft:chests/underwater_ruin_big
// minecraft:chests/underwater_ruin_small
// minecraft:chests/village/village_armorer
// minecraft:chests/village/village_butcher
// minecraft:chests/village/village_cartographer
// minecraft:chests/village/village_desert_house
// minecraft:chests/village/village_fisher
// minecraft:chests/village/village_fletcher
// minecraft:chests/village/village_mason
// minecraft:chests/village/village_plains_house
// minecraft:chests/village/village_savanna_house
// minecraft:chests/village/village_shepherd
// minecraft:chests/village/village_snowy_house
// minecraft:chests/village/village_taiga_house
// minecraft:chests/village/village_tannery
// minecraft:chests/village/village_temple
// minecraft:chests/village/village_toolsmith
// minecraft:chests/village/village_weaponsmith
// minecraft:chests/woodland_mansion


// minecraft:aqua_affinity
// minecraft:bane_of_arthropods
// minecraft:binding_curse
// minecraft:blast_protection   1 2 
// minecraft:breach
// minecraft:channeling
// minecraft:density
// minecraft:depth_strider
// minecraft:efficiency
// minecraft:feather_falling   1
// minecraft:fire_aspect
// minecraft:fire_protection   1  2 (3)
// minecraft:flame
// minecraft:fortune           2
// minecraft:frost_walker
// minecraft:impaling
// minecraft:infinity
// minecraft:knockback
// minecraft:looting
// minecraft:loyalty
// minecraft:luck_of_the_sea
// minecraft:lure
// minecraft:mending
// minecraft:multishot
// minecraft:piercing
// minecraft:power                  1 2
// minecraft:projectile_protection  1 2
// minecraft:protection             1 2
// minecraft:punch
// minecraft:quick_charge
// minecraft:respiration            2
// minecraft:riptide                1 2 3
// minecraft:sharpness              1 2 3
// minecraft:silk_touch
// minecraft:smite                  1 2 3
// minecraft:soul_speed
// minecraft:sweeping_edge
// minecraft:swift_sneak
// minecraft:thorns                 1 2
// minecraft:unbreaking             1
// minecraft:vanishing_curse
// minecraft:wind_burst