package chiseled_enchanting_table.render;

import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantingTableBlockEntityRenderer
		implements BlockEntityRenderer<ChiseledEnchantingTableBlockEntity> {
	public static final SpriteIdentifier BOOK_TEXTURE;
	private final BookModel book;

	public ChiseledEnchantingTableBlockEntityRenderer(Context ctx) {
		this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
	}

	@Override
	public void render(
		ChiseledEnchantingTableBlockEntity enchantingTableBlockEntity, float dt, MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos
	) {
		var floatingBook = enchantingTableBlockEntity.floatingBook;
		matrixStack.push();
		matrixStack.translate(0.5F, 0.75F, 0.5F);
		float g = (float)floatingBook.ticks + dt;
		matrixStack.translate(0.0F, 0.1F + MathHelper.sin(g * 0.1F) * 0.01F, 0.0F);
  
		float h;
		for(h = floatingBook.bookRotation - floatingBook.lastBookRotation; h >= 3.1415927F; h -= 6.2831855F) {
		}
  
		while(h < -3.1415927F) {
		   h += 6.2831855F;
		}
  
		float k = floatingBook.lastBookRotation + h * dt;
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(-k));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));
		float l = MathHelper.lerp(dt, floatingBook.pageAngle, floatingBook.nextPageAngle);
		float m = MathHelper.fractionalPart(l + 0.25F) * 1.6F - 0.3F;
		float n = MathHelper.fractionalPart(l + 0.75F) * 1.6F - 0.3F;
		float o = MathHelper.lerp(dt, floatingBook.pageTurningSpeed, floatingBook.nextPageTurningSpeed);
		this.book.setPageAngles(g, MathHelper.clamp(m, 0.0F, 1.0F), MathHelper.clamp(n, 0.0F, 1.0F), o);
		VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
		this.book.render(matrixStack, vertexConsumer, light, overlay);
		matrixStack.pop();
	}

	static {
		BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
				Identifier.ofVanilla("entity/enchanting_table_book"));
	}

}
