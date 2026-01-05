package chiseled_enchanting_table.render;

import org.jetbrains.annotations.Nullable;

import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantingTableBlockEntityRenderer
		implements BlockEntityRenderer<ChiseledEnchantingTableBlockEntity, ChiseledEnchantingTableBlockEntityRenderState> {
	public static final SpriteIdentifier BOOK_TEXTURE;
	private final SpriteHolder spriteHolder;
	private final BookModel book;

	public ChiseledEnchantingTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.spriteHolder = ctx.spriteHolder();
		this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
	}
	@Override
	public ChiseledEnchantingTableBlockEntityRenderState createRenderState() {
		return new ChiseledEnchantingTableBlockEntityRenderState();
	}

	public void updateRenderState(ChiseledEnchantingTableBlockEntity CETBE, ChiseledEnchantingTableBlockEntityRenderState CETBERS, float f, Vec3d vec3d, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
		BlockEntityRenderer.super.updateRenderState(CETBE, CETBERS, f, vec3d, crumblingOverlayCommand);
		CETBERS.pageAngle = MathHelper.lerp(f, CETBE.floatingBook.pageAngle, CETBE.floatingBook.nextPageAngle);
		CETBERS.pageTurningSpeed = MathHelper.lerp(f, CETBE.floatingBook.pageTurningSpeed, CETBE.floatingBook.nextPageTurningSpeed);
		CETBERS.ticks = (float)CETBE.floatingBook.ticks + f;

		float g;
		for(g = CETBE.floatingBook.bookRotation - CETBE.floatingBook.lastBookRotation; g >= 3.1415927F; g -= 6.2831855F) {
		}

		while(g < -3.1415927F) {
			g += 6.2831855F;
		}

		CETBERS.bookRotationDegrees = CETBE.floatingBook.lastBookRotation + g * f;
	}
	@Override
	public void render(ChiseledEnchantingTableBlockEntityRenderState CETBERS, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
		matrixStack.push();
		matrixStack.translate(0.5F, 0.75F, 0.5F);
		matrixStack.translate(0.0F, 0.1F + MathHelper.sin((double)(CETBERS.ticks * 0.1F)) * 0.01F, 0.0F);
		float f = CETBERS.bookRotationDegrees;
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(-f));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));
		float g = MathHelper.fractionalPart(CETBERS.pageAngle + 0.25F) * 1.6F - 0.3F;
		float h = MathHelper.fractionalPart(CETBERS.pageAngle + 0.75F) * 1.6F - 0.3F;
		BookModel.BookModelState bookModelState = new BookModel.BookModelState(CETBERS.ticks, MathHelper.clamp(g, 0.0F, 1.0F), MathHelper.clamp(h, 0.0F, 1.0F), CETBERS.pageTurningSpeed);
		orderedRenderCommandQueue.submitModel(this.book, bookModelState, matrixStack, BOOK_TEXTURE.getRenderLayer(RenderLayers::entitySolid), CETBERS.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, this.spriteHolder.getSprite(BOOK_TEXTURE), 0, CETBERS.crumblingOverlay);
		matrixStack.pop();
	}

	static {
		BOOK_TEXTURE = TexturedRenderLayers.ENTITY_SPRITE_MAPPER.mapVanilla("enchanting_table_book");
	}

}

