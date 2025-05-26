package chiseled_enchanting_table.structureProcessor;

import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import com.mojang.serialization.MapCodec;
import chiseled_enchanting_table.registry.StructureProcessorRegistry;
import chiseled_enchanting_table.utils.BlockPosStream;
import chiseled_enchanting_table.utils.ChiseledBookshelfLootTable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import java.util.concurrent.ConcurrentHashMap;

public class BookshelfReplacerProcessor extends StructureProcessor {

    public static final BookshelfReplacerProcessor INSTANCE = new BookshelfReplacerProcessor();
    public static final MapCodec<BookshelfReplacerProcessor> BOOKSHELF_REPLACER_PROCESSOR_CODEC = MapCodec.unit((Supplier<BookshelfReplacerProcessor>)(() -> BookshelfReplacerProcessor.INSTANCE));;

    @Override
    public StructureProcessorType<?> getType() {
        return StructureProcessorRegistry.BOOKSHELF_REPLACER_PROCESSOR;
    }

    public WeakHashMap<Integer, ConcurrentHashMap<BlockPos, BlockState>> blockStateMapCache = 
        new WeakHashMap<Integer, ConcurrentHashMap<BlockPos, BlockState>>();

    @Override
    public List<StructureTemplate.StructureBlockInfo> reprocess(
		ServerWorldAccess world,
		BlockPos pos,
		BlockPos pivot,
		List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
		List<StructureTemplate.StructureBlockInfo> currentBlockInfos,
		StructurePlacementData data
    ) {
        
        var serverWorld = world.toServerWorld();
        // var blockState = world.getBlockState(pos);
        // List<StructureTemplate.StructureBlockInfo> newBlockInfos = new ArrayList<>(currentBlockInfos);
        for (int i = 0; i < currentBlockInfos.size(); i++) {
            var currentBlockInfo = currentBlockInfos.get(i);
            if (currentBlockInfo.state().isOf(Blocks.BOOKSHELF)) {
                
                var cpos = currentBlockInfo.pos();

                var hash = data.getBoundingBox().hashCode();
                
                if (!blockStateMapCache.containsKey(hash)) {

                    // Second approach
                    var map2 = new ConcurrentHashMap<BlockPos, BlockState>(currentBlockInfos.size());
                    currentBlockInfos.parallelStream().forEach(blockInfo -> 
                        map2.put(blockInfo.pos(), blockInfo.state())
                    );
                    
                    blockStateMapCache.put(hash, map2);
                }
                var blockStateMap = blockStateMapCache.get(hash);

                var neighboursPriority = BlockPosStream.streamHorizontalNeighboursInRandomOrder(serverWorld, cpos)
                    .map(npos -> {
                        var targetBlockState = blockStateMap.get(npos);
                        if (targetBlockState != null) {
                            if (targetBlockState.isOpaque()) {
                                return new BlockPosPriority(npos, 0, targetBlockState);
                            }
                            return new BlockPosPriority(npos, 100, targetBlockState);
                        } else {
                            var outsideBlockState = world.getBlockState(npos);
                            if (outsideBlockState.isOpaque()) {
                                return new BlockPosPriority(npos, 20, outsideBlockState);
                            }
                            return new BlockPosPriority(npos, 80, outsideBlockState);
                        }
                    });

                // var DEBUG_neighboursPriority = neighboursPriority.collect(Collectors.toList());
                
                var selected = neighboursPriority
                    .max((a, b) -> Integer.compare(a.priority(), b.priority()))
                    .orElse(null);
                var selectedPos = selected.pos();
                
                var facingDirection = selectedPos.subtract(cpos);
                var d = Direction.getFacing(facingDirection.getX(), 0, facingDirection.getZ());

                var rotation = data.getRotation();
                if (rotation == BlockRotation.CLOCKWISE_90) {
                    rotation = BlockRotation.COUNTERCLOCKWISE_90;
                } else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
                    rotation =  BlockRotation.CLOCKWISE_90;
                }
                var computedDirection = data.getMirror().apply(rotation.rotate(d));

                var nbtWithBlockState = ChiseledBookshelfLootTable.fillWithSeededRandomBook(serverWorld, cpos);

                var nbt = nbtWithBlockState.nbt();
                var blockstate = nbtWithBlockState.state();
                blockstate = blockstate.with(Properties.HORIZONTAL_FACING, computedDirection);
                    
                currentBlockInfos.set(i, new StructureTemplate.StructureBlockInfo(cpos, blockstate, nbt));

            }
        }
        return currentBlockInfos;
    }

    public static void bookshelfPostProcessing(ServerWorld serverWorld, BlockPos blockPos) {
        var neightboursPos = BlockPosStream.streamHorizontalNeighboursInRandomOrder(serverWorld, blockPos).iterator();
        while (neightboursPos.hasNext()) {
            var pos = neightboursPos.next();
            var targetBlockState = serverWorld.getBlockState(pos);
            if (
                !targetBlockState.isAir() && 
                !targetBlockState.isOf(Blocks.COBWEB) &&
                !targetBlockState.isOf(Blocks.TORCH)
            ) continue;
            var facingDirection = pos.subtract(blockPos);
            var horizontalFacing = Direction.fromVector(facingDirection.getX(), 0, facingDirection.getZ(), Direction.NORTH);
            var newBlockState = Blocks.CHISELED_BOOKSHELF.getDefaultState();

            serverWorld.setBlockState(blockPos, newBlockState, Block.SKIP_DROPS);
            var blockEntity = serverWorld.getBlockEntity(blockPos);
            if (!(blockEntity instanceof ChiseledBookshelfBlockEntity cbsbe)) break;
            var nbtWithBlockState =  ChiseledBookshelfLootTable.fillWithSeededRandomBook(serverWorld, blockPos);
            var nbt = nbtWithBlockState.nbt();
            var blockStateWithBook = nbtWithBlockState.state().with(
                Properties.HORIZONTAL_FACING, horizontalFacing
            );
            serverWorld.setBlockState(blockPos, blockStateWithBook, Block.SKIP_DROPS);
            cbsbe.read(nbt,  serverWorld.getRegistryManager());
            cbsbe.markDirty();
            break;
        }
    }

    public record BlockPosPriority(BlockPos pos, int priority, BlockState state) {}
}