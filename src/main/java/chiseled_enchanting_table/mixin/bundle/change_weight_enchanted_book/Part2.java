package chiseled_enchanting_table.mixin.bundle.change_weight_enchanted_book;

import java.util.List;

import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;


@Mixin(targets = "net.minecraft.component.type.BundleContentsComponent$Builder")
public class Part2 {
    // @Shadow
    // private static final Fraction NESTED_BUNDLE_OCCUPANCY;

    @Redirect(
        method = "add(Lnet/minecraft/item/ItemStack;)I",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/BundleContentsComponent;getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;"
        )
    )
    private static Fraction add(ItemStack stack) {
        return alteredGetOccupancy(stack);
    }

    @Redirect(
        method = "getMaxAllowed(Lnet/minecraft/item/ItemStack;)I",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/BundleContentsComponent;getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;"
        )
    )
    private static Fraction getMaxAllowed(ItemStack stack) {
        return alteredGetOccupancy(stack);
    }

    @Redirect(
        method = "removeFirst()Lnet/minecraft/item/ItemStack;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/BundleContentsComponent;getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;"
        )
    )
    private static Fraction removeFirst(ItemStack stack) {
        return alteredGetOccupancy(stack);
    }


    private static Fraction alteredGetOccupancy(ItemStack stack) {
        BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundleContentsComponent != null) {
            return Fraction.getFraction(1,16).add(bundleContentsComponent.getOccupancy());
        } else {
            List<BeehiveBlockEntity.BeeData> list = (List)stack.getOrDefault(DataComponentTypes.BEES, List.of());
            return !list.isEmpty() ? Fraction.ONE : Fraction.getFraction(1, stack.isOf(Items.ENCHANTED_BOOK)? 16 : stack.getMaxCount());
        }
    }

}