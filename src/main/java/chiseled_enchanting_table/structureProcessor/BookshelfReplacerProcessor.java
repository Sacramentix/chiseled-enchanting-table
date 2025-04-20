package chiseled_enchanting_table.structureProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.serialization.MapCodec;
import chiseled_enchanting_table.registry.StructureProcessorRegistry;
import chiseled_enchanting_table.utils.BlockPosStream;
import chiseled_enchanting_table.utils.ChiseledBookshelfLootTable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldView;

public class BookshelfReplacerProcessor extends StructureProcessor {

    public static final BookshelfReplacerProcessor INSTANCE = new BookshelfReplacerProcessor();
    public static final MapCodec<BookshelfReplacerProcessor> BOOKSHELF_REPLACER_PROCESSOR_CODEC = MapCodec.unit((Supplier<BookshelfReplacerProcessor>)(() -> BookshelfReplacerProcessor.INSTANCE));;

    @Override
    public StructureProcessorType<?> getType() {
        return StructureProcessorRegistry.BOOKSHELF_REPLACER_PROCESSOR;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		StructureTemplate.StructureBlockInfo originalBlockInfo,
		StructureTemplate.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
    ) {
        if (currentBlockInfo.state().isOf(Blocks.BOOKSHELF)) {
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), Blocks.CHISELED_BOOKSHELF.getDefaultState(), null);
        }
        return currentBlockInfo;
    }

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
        List<StructureTemplate.StructureBlockInfo> newBlockInfos = new ArrayList<>(currentBlockInfos);
        for (int i = 0; i < currentBlockInfos.size(); i++) {
            var currentBlockInfo = currentBlockInfos.get(i);
            if (currentBlockInfo.state().isOf(Blocks.BOOKSHELF) || currentBlockInfo.state().isOf(Blocks.CHISELED_BOOKSHELF)) {
                System.out.println("reprocess Blocks.BOOKSHELF found!");
                
                var cpos = currentBlockInfo.pos();
                var neighboursPriority = BlockPosStream.streamHorizontalNeighboursInRandomOrder(serverWorld, cpos)
                    .map(npos -> {
                        var targetBlockState = currentBlockInfos.stream()
                            .filter(info -> info.pos().equals(npos))
                            .map(StructureTemplate.StructureBlockInfo::state)
                            .findFirst()
                            .orElse(null);
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

                var DEBUG_neighboursPriority = neighboursPriority.collect(Collectors.toList());
                
                var selected = DEBUG_neighboursPriority.stream()
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
            
                var newBlockState = Blocks.CHISELED_BOOKSHELF.getDefaultState()
                    .with(
                        Properties.HORIZONTAL_FACING, computedDirection
                    );

                    
                newBlockInfos.set(i, new StructureTemplate.StructureBlockInfo(cpos, newBlockState, null));
                ChiseledBookshelfLootTable.fillWithSeededRandomBook(serverWorld, cpos);

            }
        }
        return newBlockInfos;
    }

    public record BlockPosPriority(BlockPos pos, int priority, BlockState state) {}
}