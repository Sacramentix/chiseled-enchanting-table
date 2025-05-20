package chiseled_enchanting_table.mixin.chiseled_bookshelf;

import org.spongepowered.asm.mixin.Mixin;

import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledBookshelfTick;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

// Target the vanilla ChiseledBookshelfBlock
@Mixin(ChiseledBookshelfBlock.class)
public abstract class AddRandomTick extends Block {

    // Required constructor for Mixin extending a class with a constructor
    public AddRandomTick(Settings settings) {
        
        super(settings);
    }

    /**
     * Overrides hasRandomTicks to enable random ticking for the chiseled bookshelf.
     * By default, ChiseledBookshelfBlock does not have random ticks enabled in its settings.
     */
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true; // Always enable random ticks for this block
        // You could also make this conditional, e.g., based on a block state property
    }

    /**
     * Implements the random tick logic for the chiseled bookshelf.
     * This method is called on the server side when a random tick occurs for this block.
     */
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        ChiseledBookshelfTick.triggerParticleAndSoundIfEnchantedBook(state, world, pos, random);

    }


}