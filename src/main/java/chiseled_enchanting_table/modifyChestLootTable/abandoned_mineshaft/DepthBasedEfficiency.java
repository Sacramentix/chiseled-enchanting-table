package chiseled_enchanting_table.modifyChestLootTable.abandoned_mineshaft;

import java.util.List;

import com.mojang.serialization.MapCodec;
import chiseled_enchanting_table.ChiseledEnchantingTable;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.MineshaftStructure;
import net.minecraft.registry.Registries;

public class DepthBasedEfficiency implements LootFunction {
    // Singleton instance since this is a static function with no parameters
    public static final DepthBasedEfficiency INSTANCE = new DepthBasedEfficiency();

    // The function type - will be initialized in the register method
    public static LootFunctionType<DepthBasedEfficiency> TYPE;

    private DepthBasedEfficiency() {
        // Private constructor to enforce singleton
    }

    @Override
    public LootFunctionType<DepthBasedEfficiency> getType() {
        return TYPE;
    }

    // Mineshaft can go between Y Sea level - 10 to world bottom

    // We want toto ince

    @Override
    public ItemStack apply(ItemStack stack, LootContext ctx) {
        if (!stack.isOf(Items.ENCHANTED_BOOK)) return stack;
        var world = ctx.getWorld();
        var efficiency = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Identifier.ofVanilla("efficiency")).get();
        var mineshaftTopY = world.getSeaLevel();
        var mineshaftBottomY = world.getBottomY();
        var currentY = ctx.get(LootContextParameters.ORIGIN).y;
        // Calculate a rounded value between 2 and 5 based on currentY's proximity to mineshaftTopY and mineshaftBottomY
        var roundedValue = Math.round(2 + (float)(mineshaftTopY - currentY) / (mineshaftTopY - mineshaftBottomY) * 3);
        var v2_5 = Math.max(2, Math.min(5, roundedValue)); // Clamp the value between 2 and 5
        
        stack.addEnchantment(efficiency, v2_5);
        var lore = new LoreComponent(List.of(
            Text.literal("Chiseled Enchanting Table")
            .styled(style -> style.withColor(0x800080))
        ));
        stack.set(DataComponentTypes.LORE, lore);
        return stack;
    }

    public static final MapCodec<DepthBasedEfficiency> CODEC = MapCodec.unit(DepthBasedEfficiency.INSTANCE);

    // Register this function type
    public static void register() {
        TYPE = Registry.register(
                Registries.LOOT_FUNCTION_TYPE,
                ChiseledEnchantingTable.identifier("reprocess_abandoned_mineshaft_loot"),
                new LootFunctionType<>(CODEC));
    }
    
    public static class DepthBasedEfficiencyBuilder implements LootFunction.Builder   {
        public static final DepthBasedEfficiencyBuilder INSTANCE = new DepthBasedEfficiencyBuilder();

        public DepthBasedEfficiency build() {
            return DepthBasedEfficiency.INSTANCE;
        }
    }

}