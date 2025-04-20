package chiseled_enchanting_table.utils;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;


public class BlockPosStream {
    /**
     * Stream all BlockPos in the radius of the origin in a shape of a cube
     * @param origin BlockPos to start iterate from
     * @param radius in block
     * @return a Stream of all BlockPos in the radius
     */
    public static Stream<BlockPos> streamPosInRadius(BlockPos origin, int radius) {
        int startX  = origin.getX()-radius, 
            startY  = origin.getY()-radius, 
            startZ  = origin.getZ()-radius;

        int endX    = origin.getX()+radius, 
            endY    = origin.getY()+radius, 
            endZ    = origin.getZ()+radius;
        
        return Stream.iterate(new BlockPos(startX, startY, startZ), pos -> {
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            if (z < endZ) {
                return new BlockPos(x, y, z+1);
            } else if (y < endY) {
                return new BlockPos(x, y+1, startZ);
            } else if (x < endX) {
                return new BlockPos(x+1, startY,startZ);
            } else {
                return null; // End of stream
            }
        }).takeWhile(coord -> coord != null);
    }

    public static Stream<BlockPos> streamHorizontalNeighboursInRandomOrder(ServerWorld World, BlockPos blockPos) {
        var seed = World.getSeed();
        var directions = List.of(
            blockPos.north(),
            blockPos.south(),
            blockPos.east(),
            blockPos.west()
        );
        var combinedSeed = seed ^ (blockPos.getX() * 31L + blockPos.getZ() * 17L);
        var random = new java.util.Random(combinedSeed);

        return directions.stream()
            .sorted((pos1, pos2) -> Long.compare(random.nextLong(), random.nextLong()));
    }
}