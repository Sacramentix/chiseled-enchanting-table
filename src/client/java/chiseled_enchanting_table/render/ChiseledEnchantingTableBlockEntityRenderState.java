package chiseled_enchanting_table.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantingTableBlockEntityRenderState extends BlockEntityRenderState {
   public float ticks;
   public float bookRotationDegrees;
   public float pageAngle;
   public float pageTurningSpeed;

   public ChiseledEnchantingTableBlockEntityRenderState() {
   }
}
