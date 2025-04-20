package chiseled_enchanting_table.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import chiseled_enchanting_table.structureProcessor.BookshelfReplacerProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.structure.StructurePlacementData;

@Mixin(net.minecraft.structure.WoodlandMansionGenerator.Piece.class)
public class ReplaceBookshelfMansion {
    
    @Inject(method = "createPlacementData(Lnet/minecraft/util/BlockMirror;Lnet/minecraft/util/BlockRotation;)Lnet/minecraft/structure/StructurePlacementData;", at = @At("HEAD"), cancellable = true)
    private static void createPlacementData(CallbackInfoReturnable<StructurePlacementData> cir, @Local(ordinal = 0) LocalRef<BlockMirror> mirrorRef, @Local(ordinal = 0)  LocalRef<BlockRotation> rotationRef) {
        cir.setReturnValue(
            new StructurePlacementData()
                .setIgnoreEntities(true)
                .setRotation(rotationRef.get())
                .setMirror(mirrorRef.get())
                .addProcessor(BookshelfReplacerProcessor.INSTANCE)
                .addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS)
        );
        cir.cancel();
    }
}