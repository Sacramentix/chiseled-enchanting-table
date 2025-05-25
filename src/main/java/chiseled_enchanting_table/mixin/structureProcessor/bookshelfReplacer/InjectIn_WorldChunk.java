package chiseled_enchanting_table.mixin.structureProcessor.bookshelfReplacer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import chiseled_enchanting_table.structureProcessor.BookshelfReplacerProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;

@Mixin(WorldChunk.class)
public class InjectIn_WorldChunk {
    @Shadow
    private World world;

    // runPostProcessing

    // BlockPos blockPos = ProtoChunk.joinBlockPos(short_, this.sectionIndexToCoord(i), chunkPos);
    // BlockState blockState = this.getBlockState(blockPos);
    // this.world.setBlockState(blockPos, blockState2, Block.NO_REDRAW | Block.FORCE_STATE);
    
    @Inject(
        method = "runPostProcessing(Lnet/minecraft/server/world/ServerWorld;)V",
        at = @At(    
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
            shift = At.Shift.AFTER
        )
        
    )
    private void injectBookshelfPostProcessing(
        CallbackInfo ci,
        @Local(ordinal = 0) LocalRef<ServerWorld> localRefServerWorld,
        @Local(ordinal = 0) LocalRef<BlockPos> localRefBlockPos,
        @Local(ordinal = 0) LocalRef<BlockState> localRefBlockState
    ) {
        var serverWorld = localRefServerWorld.get();
        var blockPos    = localRefBlockPos.get();
        var blockState  = localRefBlockState.get();
        if (!(blockState.isOf(Blocks.BOOKSHELF))) return;
        BookshelfReplacerProcessor.bookshelfPostProcessing(serverWorld, blockPos);
    }


}





// @Mixin(StructurePlacementData.class)
// public class AddStruturePostProcessing {
//     @Shadow
//     private List<StructureProcessor> processors;

//     @Inject(method = "getProcessors()Ljava/util/List;", at = @At("HEAD"), cancellable = true)
//     private void injectBookshelfProcessor(CallbackInfoReturnable<List<StructureProcessor>>  cir) {
//         var newProcessors = Lists.newArrayList(this.processors);
//         newProcessors.add(BookshelfReplacerProcessor.INSTANCE);
//         cir.setReturnValue(newProcessors);
//         net.minecraft.world.chunk.WorldChunk
//     }
// }
 