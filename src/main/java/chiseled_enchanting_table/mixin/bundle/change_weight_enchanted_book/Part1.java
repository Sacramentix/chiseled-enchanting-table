package chiseled_enchanting_table.mixin.bundle.change_weight_enchanted_book;

import java.util.List;

import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(BundleContentsComponent.class)
public class Part1 {

    @Shadow
    private static native Fraction getOccupancy(ItemStack stack);


    @Redirect(
        method = "<init>(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/BundleContentsComponent;calculateOccupancy(Ljava/util/List;)Lorg/apache/commons/lang3/math/Fraction;"
        )
    )
    private static Fraction onConstructor(List<ItemStack> stacks) {
        // BundleItem BundleContentsComponent$Builder
        return calculateOccupancyModified(stacks);
    }

    private static Fraction calculateOccupancyModified(List<ItemStack> stacks) {
        var fraction = Fraction.ZERO;
        for (var stack : stacks) {

            var occupancy = 
                stack.isOf(Items.ENCHANTED_BOOK) ? Fraction.getFraction(1, 16) : getOccupancy(stack);

            fraction = fraction.add(
                occupancy
                    .multiplyBy(
                        Fraction.getFraction(stack.getCount(), 1)
                    )
            );
        }
        return fraction;
   }

}