package chiseled_enchanting_table.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import chiseled_enchanting_table.utils.BlockPosStream;
import chiseled_enchanting_table.utils.ChiseledBookshelfLootTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;

@Mixin(WorldChunk.class)
public class ReplaceBookshelfStronghold {
    @Shadow
    private World world;

    // runPostProcessing

    // BlockPos blockPos = ProtoChunk.joinBlockPos(short_, this.sectionIndexToCoord(i), chunkPos);
    // BlockState blockState = this.getBlockState(blockPos);
    // this.world.setBlockState(blockPos, blockState2, Block.NO_REDRAW | Block.FORCE_STATE);
    
    @Inject(
        method = "runPostProcessing()V",
        at = @At(    
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
            shift = At.Shift.AFTER
        )
        
    )
    private void injectBookshelfProcessor(
        CallbackInfo ci,
        @Local(ordinal = 0) LocalRef<BlockPos> localRefBlockPos,
        @Local(ordinal = 0) LocalRef<BlockState> localRefBlockState
    ) {

        if (!(this.world instanceof ServerWorld serverWorld)) return;
        var blockPos    = localRefBlockPos.get();
        var blockState  = localRefBlockState.get();
        if (!(blockState.isOf(Blocks.CHISELED_BOOKSHELF) || blockState.isOf(Blocks.BOOKSHELF))) return;
        var neightboursPos = BlockPosStream.streamHorizontalNeighboursInRandomOrder(serverWorld, blockPos).iterator();
        while (neightboursPos.hasNext()) {
            var pos = neightboursPos.next();
            var targetBlockState = serverWorld.getBlockState(pos);
            if (!targetBlockState.isAir() && !targetBlockState.isOf(Blocks.COBWEB)) continue;
            var facingDirection = pos.subtract(blockPos);
            var newBlockState = Blocks.CHISELED_BOOKSHELF.getDefaultState()
                .with(
                    Properties.HORIZONTAL_FACING, Direction.fromVector(facingDirection.getX(), 0, facingDirection.getZ()));       
            serverWorld.setBlockState(blockPos, newBlockState, Block.SKIP_DROPS);
            ChiseledBookshelfLootTable.fillWithSeededRandomBook(serverWorld, blockPos);
            break;
        }
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
 