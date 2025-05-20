package chiseled_enchanting_table.chiseledEnchantingTable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class ChiseledBookshelfTick {

    public static void triggerParticleAndSoundIfEnchantedBook(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        var be = world.getBlockEntity(pos);
        if (!(be instanceof ChiseledBookshelfBlockEntity cbsbe)) return;
        var slot = random.nextInt(6);
        var stack = cbsbe.getStack(slot);
        if (!stack.isOf(Items.ENCHANTED_BOOK)) return;
        var facing = state.get(Properties.HORIZONTAL_FACING);
        var xOffset = facing.getOffsetX();
        var zOffset = facing.getOffsetZ();
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        double dx = -xOffset;
        double dz = -xOffset;
        x += xOffset*0.55;
        z += zOffset*0.55;
        
        if (slot < 3) {
            y+=0.25;
        }
        // Play a low glint sound at the block's position
        world.playSound(
            null, pos, net.minecraft.sound.SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
            net.minecraft.sound.SoundCategory.BLOCKS, 1F, 0.8F + random.nextFloat() * 0.4F
        );

        world.spawnParticles(ParticleTypes.ENCHANT, x, y, z, 3, 0, 0.0D, 0, 0.25);
    }
}