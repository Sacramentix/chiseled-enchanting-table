package chiseled_enchanting_table.utils;

import chiseled_enchanting_table.ChiseledEnchantingTable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class Advancement {
    
    public static void give(PlayerEntity player, String name) {
        if (!(player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer)) return;
        var advancement = serverPlayer.getServer().getAdvancementLoader().get(ChiseledEnchantingTable.identifier(name));
        if (advancement == null) return;
        var progress = serverPlayer.getAdvancementTracker().getProgress(advancement);
        if (progress.isDone()) return;
        for (var criterion : progress.getUnobtainedCriteria()) {
            serverPlayer.getAdvancementTracker().grantCriterion(advancement, criterion);
        }
    }

    public static void checkAllEnchantBook(PlayerEntity player, World world, ItemStack enchantable_item) {
        if (!enchantable_item.isOf(Items.ENCHANTED_BOOK)) return;
        var numberOfEnchantExisting = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntrySet().size();
        var numberOfEnchantOnBook = EnchantmentHelper.getEnchantments(enchantable_item).getEnchantments().size();
        if (numberOfEnchantOnBook >= numberOfEnchantExisting) {
            Advancement.give(player, "all_enchant_book");
        }
    }


}
