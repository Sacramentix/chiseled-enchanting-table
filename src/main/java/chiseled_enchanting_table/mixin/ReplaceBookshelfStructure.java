package chiseled_enchanting_table.mixin;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;
import chiseled_enchanting_table.structureProcessor.BookshelfReplacerProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.pool.StructurePool;

@Mixin(StructurePool.Projection.class)
public class ReplaceBookshelfStructure {

    @Shadow
    private ImmutableList<StructureProcessor> processors;
   
    @Inject(method = "getProcessors()Lcom/google/common/collect/ImmutableList;", at = @At("HEAD"), cancellable = true)
    private void injectBookshelfProcessor(CallbackInfoReturnable<List<StructureProcessor>>  cir) {
        var newProcessors = Stream.concat(this.processors.stream(), Stream.of(BookshelfReplacerProcessor.INSTANCE))
                      .collect(com.google.common.collect.ImmutableList.toImmutableList());
        cir.setReturnValue(newProcessors);
    }
}
 