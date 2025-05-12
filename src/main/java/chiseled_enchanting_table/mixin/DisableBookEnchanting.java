package chiseled_enchanting_table.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;



@Mixin(EnchantmentScreenHandler.class)
public class DisableBookEnchanting {

    @Shadow
    private Inventory inventory;
    @Shadow
    public int[] enchantmentPower;
    @Shadow
    public int[] enchantmentId ;
    @Shadow
    public int[] enchantmentLevel;


    @Inject(method = "onContentChanged(Lnet/minecraft/inventory/Inventory;)V", at = @At("HEAD"), cancellable = true)
    private void disableBookEnchanting(CallbackInfo cir, @Local(ordinal = 0) LocalRef<Inventory> localRef) {
        var inventory = localRef.get();
        if (inventory == this.inventory) {
            var enchantableItem =  inventory.getStack(0);
            if (enchantableItem.isOf(Items.BOOK)) {
                for(int i = 0; i < 3; ++i) {
                    this.enchantmentPower[i] = 0;
                    this.enchantmentLevel[i] = -1;
                }
                cir.cancel();
            }
        }

    }

}