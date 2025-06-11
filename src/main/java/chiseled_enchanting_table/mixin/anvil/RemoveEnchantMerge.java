package chiseled_enchanting_table.mixin.anvil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;

import java.util.Iterator;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;

// This mixins disable the mechanic of upgrading an echant level using 2 similar enchant of samelevel

@Mixin(AnvilScreenHandler.class)
public class RemoveEnchantMerge {
    
    @Redirect(method = "updateResult()V", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", ordinal = 0))
    private Iterator<Entry<RegistryEntry<Enchantment>>> disableMergeEnchant(
        Set<Entry<RegistryEntry<Enchantment>>> set
    ) {
        return java.util.Collections.emptyIterator();
	}
}   