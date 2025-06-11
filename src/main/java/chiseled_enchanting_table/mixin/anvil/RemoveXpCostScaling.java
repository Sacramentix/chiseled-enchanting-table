package chiseled_enchanting_table.mixin.anvil;

import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// This mixins disable the mechanic of upgrading an echant level using 2 similar enchant of samelevel

@Mixin(AnvilScreenHandler.class)
public class RemoveXpCostScaling {
    
    @Redirect(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getNextCost(I)I"))
    private static int disableMergeEnchant(
        int cost
    ) {
        return cost;
	}
}   