package chiseled_enchanting_table.gui;

import chiseled_enchanting_table.ChiseledEnchantingTable;
import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ChiseledEnchantingTableScreen extends HandledScreen<ChiseledEnchantingTableScreenHandler> {

    public  static final Identifier SLOT_TEXTURE    = Identifier.ofVanilla("textures/gui/sprites/container/slot.png");

    private static final Identifier BACKGROUND      = ChiseledEnchantingTable.identifier("gui/chiseled_enchanting_table.png");
    private EnchantementListWidget scrollContainer;

    public int inventory_size;
    public int hotbar_size;

    public ChiseledEnchantingTableScreen(ChiseledEnchantingTableScreenHandler handler, net.minecraft.entity.player.PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.hotbar_size = PlayerInventory.getHotbarSize();
        this.inventory_size = inventory.size();
		this.backgroundWidth 	= 340;
		this.backgroundHeight 	= 166;
		
		handler.onContentChangedCallback = () -> {
			this.scrollContainer.updateEntry(handler.get_enchantable_item(), handler.get_cost_item());
		};
		this.scrollContainer = new EnchantementListWidget(
			MinecraftClient.getInstance(),
			handler,
			153,
			140,
			150,
			25
		);
		this.addDrawableChild(this.scrollContainer);
    }

		@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		scrollContainer.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	protected void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY) {
		super.drawMouseoverTooltip(context, mouseX, mouseY);
		if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
			ItemStack stack = this.focusedSlot.getStack();
			context.drawTooltip(this.textRenderer, this.getTooltipFromItem(stack), mouseX, mouseY);
		}
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		this.addDrawableChild(this.scrollContainer);
	}


	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		this.scrollContainer.keyPressed(keyCode, scanCode, modifiers);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		this.scrollContainer.keyReleased(keyCode, scanCode, modifiers);
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		this.scrollContainer.setX(this.x+7);
		this.scrollContainer.setY(this.y+18);
		super.render(ctx, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(ctx, mouseX, mouseY);
		// context.disableScissor();
	}

	
	@Override
	public void onSlotChangedState(int slotId, int handlerId, boolean newState) {
		super.onSlotChangedState(slotId, handlerId, newState);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
		// context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
	}

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        int i = this.x;
		int j = this.y;
		ctx.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);

    }
}
