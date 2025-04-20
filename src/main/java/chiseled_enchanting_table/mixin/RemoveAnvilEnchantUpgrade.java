package chiseled_enchanting_table.mixin;

import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

// This mixins disable the mechanic of upgrading an echant level using 2 similar enchant of samelevel

@Mixin(AnvilScreenHandler.class)
public class RemoveAnvilEnchantUpgrade {

    // void updateResult()                                  <-- method = "updateResult()V",

    /*
    RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
    int q = builder.getLevel(registryEntry);                <-- target = "Lnet/minecraft/component/type/ItemEnchantmentsComponent$Builder;getLevel(Lnet/minecraft/registry/entry/RegistryEntry;)I"
    int r = entry.getIntValue();
    r = q == r ? r + 1 : Math.max(r, q);                    <-- r + 1 --> constant = @Constant(intValue = 1),
    Enchantment enchantment = registryEntry.value();
    boolean bl4 = enchantment.isAcceptableItem(itemStack);  <-- target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"
     */
    @ModifyConstant(
        method = "updateResult()V",
        constant = @Constant(intValue = 1),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/component/type/ItemEnchantmentsComponent$Builder;getLevel(Lnet/minecraft/registry/entry/RegistryEntry;)I"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"
            )
        )
        
    )
    private int injected(int value) {
        return 0;
	}
} 