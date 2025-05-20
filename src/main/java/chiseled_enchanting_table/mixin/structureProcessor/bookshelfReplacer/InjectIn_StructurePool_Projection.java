package chiseled_enchanting_table.mixin.structureProcessor.bookshelfReplacer;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import chiseled_enchanting_table.structureProcessor.BookshelfReplacerProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.pool.StructurePool;

@Mixin(StructurePool.Projection.class)
public class InjectIn_StructurePool_Projection {

    @Shadow
    private ImmutableList<StructureProcessor> processors;
   
    @ModifyReturnValue(method = "getProcessors()Lcom/google/common/collect/ImmutableList;", at = @At("RETURN"))
    private ImmutableList<StructureProcessor> injectBookshelfProcessor(ImmutableList<StructureProcessor> cir) {
        return Stream.concat(cir.stream(), Stream.of(BookshelfReplacerProcessor.INSTANCE))
                      .collect(com.google.common.collect.ImmutableList.toImmutableList());
    }
}
 