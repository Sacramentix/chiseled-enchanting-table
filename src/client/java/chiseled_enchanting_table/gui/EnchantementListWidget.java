package chiseled_enchanting_table.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import chiseled_enchanting_table.utils.CustomDrawContext;
import chiseled_enchanting_table.utils.EnchantmentWithLevel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableScreenHandler;
import chiseled_enchanting_table.chiseledEnchantingTable.ChiseledEnchantingTableScreenHandler.ApplyEnchantmentPayload;


public class EnchantementListWidget extends AlwaysSelectedEntryListWidget<EnchantementListWidget.EnchantUiEntry> {
    public Set<EnchantmentWithLevel> unlocked_enchantements = null;
    // public Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> unlocked_enchantements = null;
    public ItemStack enchantable_item;
    public ItemStack cost_item_stack;
    public PlayerEntity player;
    public ChiseledEnchantingTableScreenHandler handler;
	public EnchantementListWidget(
        MinecraftClient client,
        ChiseledEnchantingTableScreenHandler handler,
        int width,
        int height,
        int y,
        int itemHeight
    ) {
		super(client, width, height, y, itemHeight);
        this.handler = handler;
        this.enchantable_item = handler.get_enchantable_item();
        this.cost_item_stack = handler.get_cost_item();
        this.unlocked_enchantements = handler.unlocked_enchantements;
        this.player = handler.player;
        this.headerHeight = 0;
        this.getNavigationFocus();
	}

    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {

    }
    
    
    @Override
    public int getScrollbarX() {
        return this.getX()+this.width;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter key (257 for Enter, 335 for Numpad Enter)
            var focusedEntry = this.getFocused();
            if (focusedEntry instanceof EnchantUiEntry enchantUiEntry) {
                enchantUiEntry.serverSendApplyEnchant();
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    public int saveNavigationIfSameItem(ItemStack enchantable_item, ItemStack cost_item_stack) {
        var previousIsBook = this.cost_item_stack.isOf(Items.ENCHANTED_BOOK) || this.cost_item_stack.isOf(Items.BOOK);
        var nextIsBook     =      cost_item_stack.isOf(Items.ENCHANTED_BOOK) ||      cost_item_stack.isOf(Items.BOOK);
        // we don't save navigation in case of a book in cost slot
        // because we display the enchantment of the book
        // to be added for free
        if (previousIsBook) return -1;
        if (nextIsBook) return -1;
        var focusedIndex = -1;
        if (ItemStack.areItemsEqual(enchantable_item, this.enchantable_item)) {
            var focused = this.getFocused();
            focusedIndex = this.children().indexOf(focused);
        }
        return focusedIndex;
    }

    public void resumeNavigation(int index) {
        if (index >= 0 && index < this.children().size()) {
            var focusedEntry = this.children().get(index);
            this.setFocused(focusedEntry);
        }
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }
    
    // @Override
    // private int getBorderBoxLeft() {
    //     return this.getX() + this.width / 2 - this.getRowWidth() / 2;
    // }
    
    @Override
    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }
    
    // @Override
    // private int getBorderBoxRight() {
    //     return this.getBorderBoxLeft() + this.getRowWidth();
    // }
    
    @Override
    public int getRowTop(int index) {
        return this.getY() + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    public final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("widget/scroller");

    @Override
    protected void renderList(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x = this.getRowLeft();
        int rowWidth = this.getRowWidth();
        int rowHeight = this.itemHeight;
        int entryCount = this.getEntryCount();
  
        for(var index = 0; index < entryCount; ++index) {
           int y = this.getRowTop(index);
           int bottomY = this.getRowBottom(index);
           if (bottomY >= this.getY() && y <= this.getBottom()) {
                // DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight
              this.renderEntry(ctx, mouseX, mouseY, delta, index, x, y, rowWidth, rowHeight);
           }
        }
  
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        return;
        // this.hoveredEntry = this.isMouseOver((double)mouseX, (double)mouseY) ? this.getEntryAtPosition((double)mouseX, (double)mouseY) : null;
        // this.drawMenuListBackground(context);
        // this.enableScissor(context);
        // int i;
        // int j;

        // this.renderList(context, mouseX, mouseY, delta);
        // context.disableScissor();
        // this.drawHeaderAndFooterSeparators(context);
        // if (this.isScrollbarVisible()) {
        //     i = this.getScrollbarX();
        //     j = (int)((float)(this.height * this.height) / (float)this.getMaxPosition());
        //     j = MathHelper.clamp(j, 32, this.height - 8);
        //     int k = (int)this.getScrollAmount() * (this.height - j) / this.getMaxScroll() + this.getY();
        //     if (k < this.getY()) {
        //         k = this.getY();
        //     }

        //     RenderSystem.enableBlend();
        //     // context.drawGuiTexture(SCROLLER_BACKGROUND_TEXTURE, i, this.getY(), 6, this.getHeight());
        //     context.drawGuiTexture(SCROLLER_TEXTURE, i, k, 6, j);
        //     RenderSystem.disableBlend();
        // }

        // this.renderDecorations(context, mouseX, mouseY);
        // RenderSystem.disableBlend();
    }

    @Override
    protected boolean isScrollbarVisible() {
        return this.getMaxScroll() > 0;
    }


    public void updateEntry(ItemStack enchantable_item, ItemStack cost_item_stack) {
        var navigationIndex = saveNavigationIfSameItem(enchantable_item, cost_item_stack);

        this.clearEntries();
        if (navigationIndex == -1) {
            this.setScrollAmount(0);
        }
        this.enchantable_item = enchantable_item;
        var enchantFromBook = cost_item_stack.isOf(Items.ENCHANTED_BOOK);
        var available_enchantements = 
            enchantFromBook ? EnchantmentHelper.getEnchantments(cost_item_stack)
                                .getEnchantmentEntries()
                                .stream()
                                .map(x->{
                                    return new EnchantmentWithLevel(
                                        EnchantmentWithLevel.EnchantmentToIdentifier(x.getKey().value(), this.player.getWorld()),
                                        x.getIntValue()
                                    );
                                })
                                .collect(Collectors.toSet()) :
            cost_item_stack.isOf(Items.BOOK) ? new HashSet<EnchantmentWithLevel>() :
            unlocked_enchantements;

        available_enchantements.stream().filter(enchantWithLevel->{
            var enchant = EnchantmentWithLevel.IdentifierToEnchantment(enchantWithLevel.enchantment_id(), this.player.getWorld());
            return enchantable_item.isOf(Items.BOOK) || enchantable_item.isOf(Items.ENCHANTED_BOOK) || enchant.isSupportedItem(enchantable_item);
        }).sorted((e1, e2) -> {
            var name1 = Enchantment.getName(
                EnchantmentWithLevel.IdentifierToRegistryEntryEnchantment(e1.enchantment_id(), this.player.getWorld()), 1
            ).toString();
            var name2 = Enchantment.getName(
                EnchantmentWithLevel.IdentifierToRegistryEntryEnchantment(e2.enchantment_id(), this.player.getWorld()), 1
            ).toString();
            int nameComparison = name1.compareTo(name2);
            if (nameComparison != 0) return nameComparison; // Sort by name alphabetically
            return Integer.compare(e1.enchantment_level(), e2.enchantment_level()); // Sort by level numerically
        }).forEach(enchantWithLevel -> {
            var cost = enchantFromBook ? ItemStack.EMPTY : this.handler.getEnchantmentItemCost(enchantWithLevel); 
            var xpCost = enchantFromBook ? 0 : this.handler.getEnchantmentXpLevelCost(enchantWithLevel); // Example XP cost, replace with actual logic
            this.addEntry(new EnchantUiEntry(client, this.handler, enchantWithLevel, cost, xpCost));
        });
        resumeNavigation(navigationIndex);
    }


    

	public static class EnchantUiEntry extends AlwaysSelectedEntryListWidget.Entry<EnchantUiEntry> {
        public ItemStack enchantable_item;
        public ItemStack cost_item_stack;
        public int enchant_level;
        public RegistryEntry<Enchantment> enchantment;
        public Identifier enchantment_id;
        public ItemStack cost;
        public int xp_level_cost;
        public MinecraftClient client;
        public PlayerEntity player;
        public ArrayList<EnchantmentWithLevel> overridenEnchant;
        public OptionalInt sameEnchantLevelDiff = OptionalInt.empty();
        public ChiseledEnchantingTableScreenHandler handler;
        public EnchantUiEntry(
            MinecraftClient client,
            ChiseledEnchantingTableScreenHandler handler,
            EnchantmentWithLevel enchantWithLevel,
            ItemStack cost,
            int xp_level_cost
        ) {
            super();
            this.client = client;
            this.handler = handler;
            this.enchantable_item = handler.get_enchantable_item();
            this.cost_item_stack = handler.get_cost_item();
            this.player = handler.player;
            this.enchantment_id = enchantWithLevel.enchantment_id();
            this.enchant_level = enchantWithLevel.enchantment_level();
            this.cost = cost;
            this.xp_level_cost = xp_level_cost;
            this.enchantment = EnchantmentWithLevel.IdentifierToRegistryEntryEnchantment(enchantWithLevel.enchantment_id(), this.player.getWorld());
            this.overridenEnchant = EnchantmentHelper.getEnchantments(enchantable_item)
                .getEnchantmentEntries()
                .stream()
                .filter(enchant_x_level->
                    !Enchantment.canBeCombined(enchant_x_level.getKey(), this.enchantment) // || enchant_x_level.getKey().equals(this.enchantment)
                ).map(enchant_x_level->
                    new EnchantmentWithLevel(
                        EnchantmentWithLevel.EnchantmentToIdentifier(enchant_x_level.getKey().value(), this.player.getWorld()),
                        enchant_x_level.getIntValue()
                    )
                )
                .collect(Collectors.toCollection(ArrayList::new));
            var overridenFirst = overridenEnchant.stream().findFirst();
            if (overridenFirst.isPresent()) {
                var enchant = overridenFirst.get();
                if (enchant.enchantment_id().equals(this.enchantment_id)) {
                    this.sameEnchantLevelDiff = OptionalInt.of(this.enchant_level - enchant.enchantment_level());
                }
            }
            
        }
        public boolean enchantAlreadyExist() {
            return sameEnchantLevelDiff.isPresent() && sameEnchantLevelDiff.getAsInt() == 0;
        }
        
        public boolean canEnchant() {
            return xpConditionMet() && itemCostConditionMet() && !enchantAlreadyExist();
        }

        public boolean xpConditionMet() {
            return player.experienceLevel >= xp_level_cost;
        }

        public boolean itemCostConditionMet() {
            return cost.isEmpty() || ItemStack.areItemsAndComponentsEqual(cost, cost_item_stack) && cost_item_stack.getCount() >= cost.getCount();
        }

        public boolean serverSendApplyEnchant() {
            if (!this.canEnchant()) return false;

			ClientPlayNetworking.send(new ApplyEnchantmentPayload(enchantment_id, enchant_level));
            return true;
        }

	
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return serverSendApplyEnchant();
		}

        // // @Override
        // public List<? extends Element> children() {
        //     return List.of();
        // }

        // // @Override
        // public List<? extends Selectable> selectableChildren() {
        //     return List.of();
        // }
        
        @Override
        public void render(
            DrawContext ctx, int i,
            int dy, int dx,
            int dw, int dh,
            int mouseX, int mouseY,
            boolean hovered, float tickDelta
        ) {
            var tr = client.textRenderer;
            var x = dx +1;
            var w = dw-2;
            var h = dh;
            var y = dy;
            
            var lightGrey   = 0xFF919191;
            var grey        = 0xFF717171;
            var darkGrey    = 0xFF313131;
            var backgroundColor =
                !canEnchant()   ? darkGrey  :
                hovered         ? lightGrey : 
                                  grey;
            var xpGreenColor    = 0xFF55FF55;
            var redColor        = 0xFFEE0000;
            var whiteColor      = 0xFFFFFFFF;
            var cyanColor       = 0xFF00FFFF;
            var lightRedColor   = 0xFFFFCCCC;
            var lightGreenColor = 0xFFCCFFCC;
            
            var xpCostText = String.valueOf(xp_level_cost);
            
            var textWidth = client.textRenderer.getWidth(xpCostText);
            var textX = x + w - 5 - textWidth; // Align to the right with a 5-pixel padding
            var textY = y + (h - client.textRenderer.fontHeight) / 2; // Center vertically

            var itemX = x + 4; // Align to the left with a 5-pixel padding
            var itemY = y + 4; // Center vertically (assuming item size is 16x16)

            var enchantmentName = Enchantment.getName(this.enchantment, enchant_level).getString();
            var enchantmentX = itemX + 20; // Position after the item slot
            var enchantmentY = y + 2 + client.textRenderer.fontHeight / 3; // Center vertically
            
            var firstOverridenEnchant = this.overridenEnchant.stream().findFirst();

            //  we draw a grey rectangle with the item cost at the left, the enchant name at the center and the xp cost at the right
            ctx.fill(x,y,x+w,y+h, backgroundColor);
            draw3dBorder(ctx,x, y, w, h);
            ctx.drawItem(cost, itemX, itemY);
            CustomDrawContext.drawItemInSlotWithColor(ctx, tr, cost, itemX, itemY, itemCostConditionMet() ? whiteColor : redColor);
            renderText(ctx, enchantmentName, enchantmentX, enchantmentY, (Float)2.0f/3.0f, whiteColor);
            var overridenEnchantmentX = itemX + 20;
            var overridenEnchantmentY = y + h - 2 - client.textRenderer.fontHeight;
            if (enchantAlreadyExist()) {
                
                renderText(ctx, "✔ " + enchantmentName, overridenEnchantmentX, overridenEnchantmentY, (Float)2.0f/3.0f, cyanColor);

            } else if (sameEnchantLevelDiff.isPresent() && sameEnchantLevelDiff.getAsInt() > 0) {

                var enchant = firstOverridenEnchant.get();
                var enchant_entry = EnchantmentWithLevel.IdentifierToRegistryEntryEnchantment(enchant.enchantment_id(), this.player.getWorld());
                var overridenEnchantmentName =  Enchantment.getName(enchant_entry, enchant.enchantment_level()).getString();
                renderText(ctx, "⬆ "+overridenEnchantmentName, overridenEnchantmentX, overridenEnchantmentY, (Float)2.0f/3.0f, lightGreenColor);

            } else if (sameEnchantLevelDiff.isPresent() && sameEnchantLevelDiff.getAsInt() < 0) {

                var enchant = firstOverridenEnchant.get();
                var enchant_entry = EnchantmentWithLevel.IdentifierToRegistryEntryEnchantment(enchant.enchantment_id(), this.player.getWorld());
                var overridenEnchantmentName =  Enchantment.getName(enchant_entry, enchant.enchantment_level()).getString();
                renderText(ctx, "⬇ "+overridenEnchantmentName, overridenEnchantmentX, overridenEnchantmentY, (Float)2.0f/3.0f, lightRedColor);

            } else if (firstOverridenEnchant.isPresent() && !enchantable_item.isOf(Items.ENCHANTED_BOOK)) {

                var enchant = firstOverridenEnchant.get();
                var enchant_entry = EnchantmentWithLevel.IdentifierToRegistryEntryEnchantment(enchant.enchantment_id(), this.player.getWorld());
                var overridenEnchantmentName =  Enchantment.getName(enchant_entry, enchant.enchantment_level()).getString();
                renderText(ctx, "✖ "+overridenEnchantmentName, overridenEnchantmentX, overridenEnchantmentY, (Float)2.0f/3.0f, lightRedColor); 

            }
            ctx.drawTextWithShadow(client.textRenderer, xpCostText, textX, textY, xpConditionMet() ? xpGreenColor : redColor);

        }

        public void draw3dBorder(DrawContext ctx, int x, int y, int width, int height) {
            // var darkColor = (color & 0xFF000000) | 
            //     ((int)((color >> 16 & 0xFF) * 0.75) << 16) |
            //     ((int)((color >> 8  & 0xFF) * 0.75) << 8) |
            //     (int)((color        & 0xFF) * 0.75) |
            //     0xFF000000;
            // var lightColor = (color & 0xFF000000) | 
            //     Math.min((int)((color >> 16 & 0xFF) * 1.25), 255) << 16 | 
            //     Math.min((int)((color >> 8  & 0xFF) * 1.25), 255) << 8 | 
            //     Math.min((int)((color       & 0xFF) * 1.25), 255) |
            //     0xFF000000;

            var darkColor   = this.isFocused() ? 0xff888811 : 0xff111111;
            var cornerColor = this.isFocused() ? 0xffaaaa22 : 0xff333333;
            var lightColor  = this.isFocused() ? 0xffcccc22 : 0xff444444;

            ctx.fill(x, y, x + width, y + 1, darkColor);
            ctx.fill(x, y + 1, x + 1, y + height - 1, darkColor);

            ctx.fill(x, y + height - 1, x + width, y + height, lightColor);
            ctx.fill(x + width - 1, y + 1, x + width, y + height - 1, lightColor);

            ctx.fill(x + width-1, y, x + width, y + 1, cornerColor);
            ctx.fill(x, y + height - 1, x + 1, y + height, cornerColor);

         }

        public void renderText(DrawContext drawContext, String text, int x, int y, float scale, int color) {
            var matrixStack = drawContext.getMatrices();

            // Push the current matrix state
            matrixStack.push();

            // Apply scaling
            matrixStack.scale(scale, scale, 1.0f);
            matrixStack.translate(0.0F, 0.0F, 500.0F);
            // Adjust the position to account for scaling
            var scaledX = Math.round(x / scale);
            var scaledY = Math.round(y / scale);

            // Draw the text with shadow
            drawContext.drawTextWithShadow(client.textRenderer, text, (int) scaledX, (int) scaledY, color);
            // Pop the matrix state to restore it
            matrixStack.pop();
        }

        @Override
        public Text getNarration() {
            return Enchantment.getName(this.enchantment, enchant_level);
        }
	}
}
