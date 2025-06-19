package chiseled_enchanting_table.utils;


import net.minecraft.client.gui.DrawContext;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.item.ItemStack;

import org.joml.Matrix3x2f;

import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class CustomDrawContext {
    public static void drawItemInSlotWithColor(DrawContext ctx,TextRenderer textRenderer, ItemStack stack, int x, int y, int color) {
        if (stack.isEmpty()) return;
        var matrices = ctx.getMatrices();
        matrices.pushMatrix();
        matrices.translate(0.0F, 0.0F, new Matrix3x2f());
        var text = String.valueOf(stack.getCount());
        ctx.drawText(textRenderer, text, x + 19 - 2 - textRenderer.getWidth(text), y + 6 + 3, color, true);
        int k;
        int l;
        if (stack.isItemBarVisible()) {
           var i = stack.getItemBarStep();
           var j = stack.getItemBarColor();
           k = x + 2;
           l = y + 13;
           ctx.fill(RenderPipelines.GUI_TEXTURED, k, l, k + 13, l + 2, -16777216);
           ctx.fill(RenderPipelines.GUI_TEXTURED, k, l, k + i, l + 1, j | -16777216);
        }
    
    
        matrices.popMatrix();
    }
}
