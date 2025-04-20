package chiseled_enchanting_table.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;


@Mixin(StructurePiece.class)
public class MarkBookshelfForPostProcessing {
    // net.minecraft.world.StructureWorldAccess net.minecraft.block.BlockState int int int net.minecraft.util.math.BlockBox
    @Inject(method = "addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V", at = @At("HEAD"))
    private void replaceBookShelf(CallbackInfo  ci, @Local(ordinal = 0) LocalRef<BlockState> localRef) {
        var block = localRef.get();
        if (block.isOf(Blocks.BOOKSHELF)) {
            localRef.set(Blocks.CHISELED_BOOKSHELF.getDefaultState());
        }
    }
    // boolean net.minecraft.world.ModifiableWorld.setBlockState(BlockPos pos, BlockState state, int flags)
    // boolean net.minecraft.structure.StructurePiece.canAddBlock(WorldView world, int x, int y, int z, BlockBox box)
    @Inject(
        method = "addBlock(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/StructureWorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
            shift = At.Shift.AFTER 
        )
    )
    private void addPostProcessing(
        CallbackInfo  ci,
        @Local(ordinal = 0) LocalRef<StructureWorldAccess> localRefWorld,
        @Local(ordinal = 0) LocalRef<BlockPos> localRefBlockPos,
        @Local(ordinal = 0) LocalRef<BlockState> localRefBlockState
    ) {
        var blockPos = localRefBlockPos.get();
        var blockState = localRefBlockState.get();
        var world = localRefWorld.get();
        if (blockState.isOf(Blocks.CHISELED_BOOKSHELF)) {
            world.getChunk(blockPos).markBlockForPostProcessing(blockPos);
        }
    }
}