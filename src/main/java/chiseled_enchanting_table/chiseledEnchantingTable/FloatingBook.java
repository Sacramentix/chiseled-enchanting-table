package chiseled_enchanting_table.chiseledEnchantingTable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FloatingBook {
	public int ticks;
	public float nextPageAngle;
	public float pageAngle;
	public float flipRandom;
	public float flipTurn;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	public float bookRotation;
	public float lastBookRotation;
	public float targetBookRotation;
	private static final Random RANDOM = Random.create();

	public FloatingBook() {
	}


	public static void tick(World world, BlockPos pos, BlockState state, FloatingBook floatingBook) {
		floatingBook.pageTurningSpeed = floatingBook.nextPageTurningSpeed;
		floatingBook.lastBookRotation = floatingBook.bookRotation;
		PlayerEntity playerEntity = world.getClosestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0, false);
		if (playerEntity != null) {
			double d = playerEntity.getX() - (pos.getX() + 0.5);
			double e = playerEntity.getZ() - (pos.getZ() + 0.5);
			floatingBook.targetBookRotation = (float)MathHelper.atan2(e, d);
			floatingBook.nextPageTurningSpeed += 0.1F;
			if (floatingBook.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
				float f = floatingBook.flipRandom;

				do {
					floatingBook.flipRandom = floatingBook.flipRandom + (RANDOM.nextInt(4) - RANDOM.nextInt(4));
				} while (f == floatingBook.flipRandom);
			}
		} else {
			floatingBook.targetBookRotation += 0.02F;
			floatingBook.nextPageTurningSpeed -= 0.1F;
		}

		while (floatingBook.bookRotation >= (float) Math.PI) {
			floatingBook.bookRotation -= (float) (Math.PI * 2);
		}

		while (floatingBook.bookRotation < (float) -Math.PI) {
			floatingBook.bookRotation += (float) (Math.PI * 2);
		}

		while (floatingBook.targetBookRotation >= (float) Math.PI) {
			floatingBook.targetBookRotation -= (float) (Math.PI * 2);
		}

		while (floatingBook.targetBookRotation < (float) -Math.PI) {
			floatingBook.targetBookRotation += (float) (Math.PI * 2);
		}

		float g = floatingBook.targetBookRotation - floatingBook.bookRotation;

		while (g >= (float) Math.PI) {
			g -= (float) (Math.PI * 2);
		}

		while (g < (float) -Math.PI) {
			g += (float) (Math.PI * 2);
		}

		floatingBook.bookRotation += g * 0.4F;
		floatingBook.nextPageTurningSpeed = MathHelper.clamp(floatingBook.nextPageTurningSpeed, 0.0F, 1.0F);
		floatingBook.ticks++;
		floatingBook.pageAngle = floatingBook.nextPageAngle;
		float h = (floatingBook.flipRandom - floatingBook.nextPageAngle) * 0.4F;
		h = MathHelper.clamp(h, -0.2F, 0.2F);
		floatingBook.flipTurn = floatingBook.flipTurn + (h - floatingBook.flipTurn) * 0.9F;
		floatingBook.nextPageAngle = floatingBook.nextPageAngle + floatingBook.flipTurn;
	}

}
