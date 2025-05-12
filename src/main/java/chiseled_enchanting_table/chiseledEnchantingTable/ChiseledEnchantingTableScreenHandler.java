package chiseled_enchanting_table.chiseledEnchantingTable;

import chiseled_enchanting_table.ChiseledEnchantingTable;
import chiseled_enchanting_table.utils.EnchantmentWithLevel;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import chiseled_enchanting_table.registry.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

public class ChiseledEnchantingTableScreenHandler extends ScreenHandler {

	public static final Identifier AVAILABLE_CHISELED_ENCHANTMENT_PACKET_ID = ChiseledEnchantingTable.identifier("available_chiseled_enchantment");

	public record AvailableEnchantmentPayload(
		Set<EnchantmentWithLevel> unlocked_enchantements
	) implements CustomPayload {
        public static final CustomPayload.Id<AvailableEnchantmentPayload> AVAILABLE_CHISELED_ENCHANTMENT_PAYLOAD_ID = new CustomPayload.Id<AvailableEnchantmentPayload>(AVAILABLE_CHISELED_ENCHANTMENT_PACKET_ID);
        public static final PacketCodec<RegistryByteBuf, AvailableEnchantmentPayload> AVAILABLE_CHISELED_ENCHANTMENT_CODEC = 
			CustomPayload.codecOf(
				(payload, buf) -> {
					buf.writeCollection(payload.unlocked_enchantements, (setBuf, enchantmentWithLevel) -> {
						EnchantmentWithLevel.ENCHANTMENT_WITH_LEVEL_CODEC.encode(setBuf, enchantmentWithLevel);
					});
				},
				buf -> {
					var ues = buf.readCollection(HashSet::new, (setBuf) -> 
						EnchantmentWithLevel.ENCHANTMENT_WITH_LEVEL_CODEC.decode(setBuf)
					);
					return new AvailableEnchantmentPayload(ues);
				}
			);

        
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return AVAILABLE_CHISELED_ENCHANTMENT_PAYLOAD_ID;
        }
    }
    public static final Identifier APPLY_CHISELED_ENCHANTMENT_PACKET_ID = ChiseledEnchantingTable.identifier("apply_chiseled_enchantment");

    public record ApplyEnchantmentPayload(Identifier enchantment_id, int enchantment_level) implements CustomPayload {
        public static final CustomPayload.Id<ApplyEnchantmentPayload> APPLY_CHISELED_ENCHANTMENT_PAYLOAD_ID = new CustomPayload.Id<ApplyEnchantmentPayload>(APPLY_CHISELED_ENCHANTMENT_PACKET_ID);
        public static final PacketCodec<RegistryByteBuf, ApplyEnchantmentPayload> APPLY_CHISELED_ENCHANTMENT_CODEC = 
			CustomPayload.codecOf(
				(payload, buf) -> {
					buf.writeInt(payload.enchantment_level);
					buf.writeIdentifier(payload.enchantment_id);
				},
				buf -> {
					var enchantment_level    = buf.readInt();
					var enchantment_id       = buf.readIdentifier();
					return new ApplyEnchantmentPayload(enchantment_id, enchantment_level);
				}
			);



        
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return APPLY_CHISELED_ENCHANTMENT_PAYLOAD_ID;
        }
    }

    public static void init() {
		PayloadTypeRegistry.playC2S().register(
			ApplyEnchantmentPayload.APPLY_CHISELED_ENCHANTMENT_PAYLOAD_ID,
			ApplyEnchantmentPayload.APPLY_CHISELED_ENCHANTMENT_CODEC
		);
		ServerPlayNetworking.registerGlobalReceiver(ApplyEnchantmentPayload.APPLY_CHISELED_ENCHANTMENT_PAYLOAD_ID, (payload, ctx) -> {
            
			var player = ctx.player();
            var server = ctx.server();
            // Run the logic on the server thread
            server.execute(() -> {
                // Verify the player is using the correct ScreenHandler
                if (!(player.currentScreenHandler instanceof ChiseledEnchantingTableScreenHandler chiseledEnchantingTableScreenHandler)) {
                    return; // Ignore the packet if the player is not using the correct GUI
                }
				chiseledEnchantingTableScreenHandler.applyEnchantement(payload);
            });
        });
    }

	public void applyEnchantementFromBook(ApplyEnchantmentPayload payload) {
		var enchanted_book = this.inventory.getStack(COST_SLOT);
		var available_enchantments = 
			EnchantmentHelper.getEnchantments(enchanted_book)
				.getEnchantmentEntries()
				.stream()
				.map(x->{
					return new EnchantmentWithLevel(
						EnchantmentWithLevel.EnchantmentToIdentifier(x.getKey().value(), this.player.getWorld()),
						x.getIntValue()
					);
				});

		var exist = available_enchantments
			.filter((e)-> 
				payload.enchantment_id.equals(e.enchantment_id()) &&
				payload.enchantment_level == e.enchantment_level()
			).findAny();
		if (!exist.isPresent()) return;
		var enchantable_item = this.inventory.getStack(ENCHANTABLE_SLOT);
		var enchantment_level = payload.enchantment_level;
		var enchantment = EnchantmentWithLevel.IdentifierToEnchantment(payload.enchantment_id, this.world);
		var enchantment_entry = EnchantmentWithLevel.IdentifierToRegistryEntryEnchantment(payload.enchantment_id, this.world);
		if (!(enchantable_item.isOf(Items.BOOK) || enchantable_item.isOf(Items.ENCHANTED_BOOK) || enchantment.isSupportedItem(enchantable_item))) return;

		// if (!applyEnchantementCondition(payload)) return;
		var new_enchanted_book_enchants = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
		EnchantmentHelper.getEnchantments(enchanted_book)
			.getEnchantmentEntries().stream()
			.filter(e->!EnchantmentWithLevel.EnchantmentToIdentifier(e.getKey().value(), this.player.getWorld()).equals(payload.enchantment_id))
			.forEach(e->new_enchanted_book_enchants.add(e.getKey(), e.getIntValue()));
		EnchantmentHelper.set(
			enchanted_book,
			new_enchanted_book_enchants.build()
		);

		if (!EnchantmentHelper.hasEnchantments(enchanted_book)) {
			this.inventory.setStack(COST_SLOT, enchanted_book.copyComponentsToNewStack(Items.BOOK, 1));
		}
		
		// Remove conflicting enchantments and adjust levels if necessary
		var existingEnchantments = new HashSet<>(EnchantmentHelper.getEnchantments(enchantable_item).getEnchantmentEntries());
		existingEnchantments.removeIf(existingEnchantment -> !Enchantment.canBeCombined(existingEnchantment.getKey(), enchantment_entry));
		existingEnchantments.add(
			Object2IntMaps.singleton(enchantment_entry, enchantment_level).object2IntEntrySet().iterator().next()
		);
		var newEnchantmentsComponent = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
		existingEnchantments.forEach(e->newEnchantmentsComponent.add(e.getKey(), e.getIntValue()));
		if (enchantable_item.isOf(Items.BOOK)) {
			var stack = enchantable_item.copyComponentsToNewStack(Items.ENCHANTED_BOOK, 1);
			this.inventory.setStack(ENCHANTABLE_SLOT, stack);
			enchantable_item  = this.inventory.getStack(ENCHANTABLE_SLOT);
		}
		EnchantmentHelper.set(enchantable_item, newEnchantmentsComponent.build());
		// enchantable_item.set(DataComponentTypes.ENCHANTMENTS, newEnchantmentsComponent.build());
		this.inventory.markDirty();
		this.player.incrementStat(Stats.ENCHANT_ITEM);
		this.world.playSound(
			null, 
			this.player.getBlockPos(), 
			net.minecraft.sound.SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
			net.minecraft.sound.SoundCategory.PLAYERS, 
			2.0F, 
			this.world.random.nextFloat() * 0.1F + 0.9F
		);
	}

	public void applyEnchantement(ApplyEnchantmentPayload payload) {
		if (this.inventory.getStack(COST_SLOT).isOf(Items.ENCHANTED_BOOK)) {
			applyEnchantementFromBook(payload);
			return;
		}
		var exist = this.unlocked_enchantements.stream()
			.filter((e)-> 
				payload.enchantment_id.equals(e.enchantment_id()) &&
				payload.enchantment_level == e.enchantment_level()
			).findAny();
		if (!exist.isPresent()) return;
		var enchantable_item = this.inventory.getStack(ENCHANTABLE_SLOT);
		var enchantment_level = payload.enchantment_level;
		var enchantment = EnchantmentWithLevel.IdentifierToEnchantment(payload.enchantment_id, this.world);
		var enchantment_entry = EnchantmentWithLevel.IdentifierToRegistryEntryEnchantment(payload.enchantment_id, this.world);
		if (!(enchantable_item.isOf(Items.BOOK) || enchantable_item.isOf(Items.ENCHANTED_BOOK) || enchantment.isSupportedItem(enchantable_item))) return;

		if (!applyEnchantementCondition(payload)) return;

		// Remove conflicting enchantments and adjust levels if necessary
		var existingEnchantments = new HashSet<>(EnchantmentHelper.getEnchantments(enchantable_item).getEnchantmentEntries());
		existingEnchantments.removeIf(existingEnchantment -> !Enchantment.canBeCombined(existingEnchantment.getKey(), enchantment_entry));
		existingEnchantments.add(
			Object2IntMaps.singleton(enchantment_entry, enchantment_level).object2IntEntrySet().iterator().next()
		);
		var newEnchantmentsComponent = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
		existingEnchantments.forEach(e->newEnchantmentsComponent.add(e.getKey(), e.getIntValue()));
		if (enchantable_item.isOf(Items.BOOK)) {
			var stack = enchantable_item.copyComponentsToNewStack(Items.ENCHANTED_BOOK, 1);
			this.inventory.setStack(ENCHANTABLE_SLOT, stack);
			enchantable_item  = this.inventory.getStack(ENCHANTABLE_SLOT);
		}
		EnchantmentHelper.set(enchantable_item, newEnchantmentsComponent.build());
		// enchantable_item.set(DataComponentTypes.ENCHANTMENTS, newEnchantmentsComponent.build());
		this.inventory.markDirty();
		this.player.incrementStat(Stats.ENCHANT_ITEM);
		this.world.playSound(
			null, 
			this.player.getBlockPos(), 
			net.minecraft.sound.SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
			net.minecraft.sound.SoundCategory.PLAYERS, 
			2.0F, 
			this.world.random.nextFloat() * 0.1F + 0.9F
		);
	}

	public float getEnchantableXpCostMultiplier() {
		// var enchantable_item = this.inventory.getStack(ENCHANTABLE_SLOT);
		// if (enchantable_item.isEmpty()) return 0.0F;
		// var material = 
		// 	enchantable_item.getItem() instanceof net.minecraft.item.ToolItem toolItem   ? toolItem.getMaterial()  :
		// 	enchantable_item.getItem() instanceof net.minecraft.item.ArmorItem armorItem ? armorItem.getMaterial() :
		// 	null;
		// if (enchantable_item.getItem() instanceof net.minecraft.item.ToolItem toolItem) {
		// 	return switch (toolItem.getMaterial()) {
		// 		case net.minecraft.item.ToolMaterials.WOOD -> 1.0F;
		// 		case net.minecraft.item.ToolMaterials.STONE -> 2.0F;
		// 		case net.minecraft.item.ToolMaterials.IRON -> 3.0F;
		// 		case net.minecraft.item.ToolMaterials.DIAMOND -> 4.0F;
		// 		case net.minecraft.item.ToolMaterials.GOLD -> 5.0F;
		// 		case net.minecraft.item.ToolMaterials.NETHERITE -> 6.0F;
		// 		default -> 0.0F;
		// 	};
		// } else if (enchantable_item.getItem() instanceof net.minecraft.item.ArmorItem armorItem) {
		// 	return switch (armorItem.getMaterial()) {
		// 		case net.minecraft.item.ArmorMaterials.LEATHER -> 1.0F;
		// 		case net.minecraft.item.ArmorMaterials.CHAIN -> 2.0F;
		// 		case net.minecraft.item.ArmorMaterials.IRON -> 3.0F;
		// 		case net.minecraft.item.ArmorMaterials.DIAMOND -> 4.0F;
		// 		case net.minecraft.item.ArmorMaterials.GOLD -> 5.0F;
		// 		case net.minecraft.item.ArmorMaterials.NETHERITE -> 6.0F;
		// 		default -> 0.0F;
		// 	};
		// }
		return 0.0F;
	}
	
	public int getEnchantmentXpLevelCost(ApplyEnchantmentPayload ewl) {
		return getEnchantmentXpLevelCost(new EnchantmentWithLevel(ewl.enchantment_id, ewl.enchantment_level));
	}


	public int getEnchantmentXpLevelCost(EnchantmentWithLevel ewl) {
		var enchant = EnchantmentWithLevel.IdentifierToEnchantment(ewl.enchantment_id(), this.world);
		
		var L = enchant.getMaxLevel() == 1 ? 2 : ewl.enchantment_level();
		var K = 2;
		var S = 5;
		return L * S + K * (L * (L - 1)) / 2;
		// For L = 1 => cost = 5
		// For L = 2 => cost = 12
		// For L = 3 => cost = 21
		// ...
	}

	public ItemStack getEnchantmentItemCost(ApplyEnchantmentPayload ewl) {
		return getEnchantmentItemCost(new EnchantmentWithLevel(ewl.enchantment_id, ewl.enchantment_level));
	}

	public ItemStack getEnchantmentItemCost(EnchantmentWithLevel ewl) {
		var level = ewl.enchantment_level();
		if (ewl.is("aqua_affinity")) {
			return new ItemStack(Items.COD, 5);
		} if (ewl.is("bane_of_arthropods")) {
			return new ItemStack(Items.SPIDER_EYE, level*2);
		} if (ewl.is("binding_curse")) {
			return new ItemStack(Items.COBWEB,5);
		} if (ewl.is("blast_protection")) {
			return new ItemStack(Items.GUNPOWDER, level*2);
		} if (ewl.is("efficiency")) {
			return new ItemStack(Items.LAPIS_LAZULI, level);
		} if (ewl.is("fire_aspect")) {
			return new ItemStack(Items.BLAZE_POWDER, level*2);
		} if (ewl.is("feather_falling")) {
			return new ItemStack(Items.FEATHER, level*3);
		} if (ewl.is("fire_protection")) {
			return new ItemStack(Items.CLAY_BALL, level*3);
		} if (ewl.is("fortune")) {
			return new ItemStack(Items.GOLD_INGOT, level);
		} if (ewl.is("looting")) {
			return new ItemStack(Items.RABBIT_FOOT, level*2);
		} if (ewl.is("vanishing_curse")) {
			return new ItemStack(Items.FERMENTED_SPIDER_EYE,5);
		} if (ewl.is("sharpness")) {
			return new ItemStack(Items.FLINT, level*2);
		} if (ewl.is("projectile_protection")) {
			return new ItemStack(Items.BONE, level*2);
		} if (ewl.is("thorns")) {
			return new ItemStack(Items.CACTUS, level*3);
		} if (ewl.is("lure")) {
			return new ItemStack(Items.WHEAT_SEEDS, level*2);
		} if (ewl.is("unbreaking")) {
			return new ItemStack(Items.IRON_INGOT, level*2);
		} if (ewl.is("wind_burst")) {
			return new ItemStack(Items.BREEZE_ROD, level);
		} if (ewl.is("frost_walker")) {
			return new ItemStack(Items.BLUE_ICE, level);
		}

		return new ItemStack(net.minecraft.item.Items.LAPIS_LAZULI, ewl.enchantment_level());
	}


	/**
	 * 
	 * @param X the xp level
	 * @return the xp amount to level from 0 to X
	 */
	public int calculateXpCostToLevel(int X) {
		/*
		Experience required =
		2 × current_level + 7 (for levels 0–15)
		5 × current_level – 38 (for levels 16–30)
		9 × current_level – 158 (for levels 31+)
		*/
		int A = Math.min(X, 16);
		int B = Math.min(X, 31);
		int C = X;
		// m for mult, c for constant
		int Am = 2;
		int Ac = 7;
		int Bm = 5;
		int Bc = -38;
		int Cm = 9;
		int Cc = -158;
	
		return (Am * ((A - 1) * A) / 2 + Ac * A) // (for levels 0–15)
			 + (Bm * (((B - 1) * B) / 2 - ((A - 1) * A) / 2) + Bc * (B - A)) // (for levels 16–30)
			 + (Cm * (((C - 1) * C) / 2 - ((B - 1) * B) / 2) + Cc * (C - B)) // (for levels 31+)
		;
	}

	public boolean applyEnchantementCondition(ApplyEnchantmentPayload ewl) {
		var xp_level_cost = getEnchantmentXpLevelCost(ewl);
		if (this.player.experienceLevel < xp_level_cost) return false;

		var cost = getEnchantmentItemCost(ewl);
		var item_cost_stack = this.get_cost_item();
		if (!ItemStack.areItemsAndComponentsEqual(item_cost_stack, cost) || item_cost_stack.getCount() < cost.getCount()) return false;

		var xp_cost = -calculateXpCostToLevel(xp_level_cost);
		this.player.addExperience(xp_cost);
		item_cost_stack.decrement(cost.getCount());
		return true;
	}

	private final Inventory inventory = new SimpleInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			ChiseledEnchantingTableScreenHandler.this.onContentChanged(this);
		}
	};

    public static final int ENCHANTABLE_SLOT = 0;
    public static final int COST_SLOT		 = 1;
	private final World world;
	public final PlayerEntity player;
	public Set<EnchantmentWithLevel> unlocked_enchantements;
	@Nullable
	public Runnable onContentChangedCallback;

	public ChiseledEnchantingTableScreenHandler(int syncId, PlayerInventory playerInventory, AvailableEnchantmentPayload payload) {
		super(ScreenHandlerRegistry.CHISELED_ENCHANTING_TABLE_SCREEN_HANDLER, syncId);
		this.world = playerInventory.player.getWorld();
		this.player = playerInventory.player;
		this.unlocked_enchantements =  payload.unlocked_enchantements;

		var inventorySlotStartX = 172;
		var inventorySlotStartY = 84;
		var slotSize = 16;
		var slotPadding = 1;
		var slotFullSize = slotSize + slotPadding*2;
		var rowLength = 9;
		var colmumnLength = 3;
		var hotbarMargin = 4;

		var cost_slot_gap = 4;
		var enchantable_slot_gap = 12;

		// EnchantmentScreenHandler
        // This is the slot for tools / weapon in GUI

		this.addSlot(
			new Slot(
				this.inventory,
				ENCHANTABLE_SLOT,																			// slot index
				inventorySlotStartX + slotSize + slotPadding*2, 											// X
				inventorySlotStartY - slotSize * 2 - slotPadding*3 - cost_slot_gap - enchantable_slot_gap	// Y
			) {
				@Override
				public int getMaxItemCount() {
					return 1;
				}
			}
		);

        // This is the slot for a stack infuse items by default lapis lazulis

		this.addSlot(
			new Slot(
				this.inventory,
				COST_SLOT,														// slot index
				inventorySlotStartX + slotSize + slotPadding*2,					// X
				inventorySlotStartY - slotSize - slotPadding*2 - cost_slot_gap	// Y
			) {
				@Override
				public boolean canInsert(ItemStack stack) {
					return true;
				}
			}
		);

		// hotbar 0 - 8
		for (var row = 0; row < rowLength; ++row) {
			this.addSlot(
				new Slot(
					playerInventory,
					row,																// index
					inventorySlotStartX + row * slotFullSize, 							// X
					inventorySlotStartY + colmumnLength * slotFullSize + hotbarMargin	// Y
				)
			);
		}
		// main inventory 9 - 35
		for (var row = 0; row < colmumnLength; ++row) {
			for (var column = 0; column < rowLength; ++column) {
				this.addSlot(
					new Slot(
						playerInventory,
						column + (row + 1) * rowLength,					// index
						inventorySlotStartX + column * slotFullSize,	// X
						inventorySlotStartY + row * slotFullSize		// Y
					)
				);
			}
		}
	}

	
	

	@Override
	public void onContentChanged(Inventory inventory) {
		System.out.println("CHANGED !");
		if (this.onContentChangedCallback != null) {
			this.onContentChangedCallback.run();
		}
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
        return true;
	}



	@Override
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		if (actionType == SlotActionType.QUICK_MOVE) {
			ItemStack clickedStack = this.getSlot(slotIndex).getStack();
			if (clickedStack.isEmpty()) return;
			if ((slotIndex == COST_SLOT) || (slotIndex == ENCHANTABLE_SLOT) ) {

				if (!this.insertItem(this.getSlot(slotIndex).getStack(), 2, 38, true)) {
					return;
				}
				this.getSlot(slotIndex).setStack(ItemStack.EMPTY);
				return;
			}
			var clickedStackNoEnchant = clickedStack.copy();
			clickedStackNoEnchant.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
			var isBook = clickedStackNoEnchant.isOf(Items.BOOK) || clickedStackNoEnchant.isOf(Items.ENCHANTED_BOOK);
			// Check if the stack is Enchantable if it would have no enchant
			// Item with enchant are not enchantable by default
			if (clickedStackNoEnchant.isEnchantable() || (isBook && !this.getSlot(ENCHANTABLE_SLOT).hasStack())) {
				var tempStack = clickedStack.copy();
				this.getSlot(slotIndex).setStack(this.getSlot(ENCHANTABLE_SLOT).getStack());
				this.getSlot(ENCHANTABLE_SLOT).setStack(tempStack);

				if (this.getSlot(ENCHANTABLE_SLOT).getStack().getCount() > 1) {
					var overflow = this.getSlot(ENCHANTABLE_SLOT).getStack().copy();
					overflow.setCount(overflow.getCount() - 1);
					this.getSlot(ENCHANTABLE_SLOT).getStack().setCount(1);
					if (!this.getSlot(slotIndex).hasStack()) {
						this.getSlot(slotIndex).setStack(overflow);
					} else {
						if (!this.insertItem(overflow, 2, 38, true)) {
							this.player.dropItem(overflow, false);
						}
					}
				}
			} else {
				if (!this.getSlot(COST_SLOT).hasStack()) {
					this.getSlot(COST_SLOT).setStack(clickedStack.copy());
					clickedStack.setCount(0);
				} else {
					var costSlotStack = this.getSlot(COST_SLOT).getStack();
					if (costSlotStack.isEmpty()) {
						this.getSlot(COST_SLOT).setStack(clickedStack.copy());
						clickedStack.setCount(0);
					} else if (ItemStack.areItemsAndComponentsEqual(clickedStack, costSlotStack)) {
						this.insertItem(clickedStack, COST_SLOT, COST_SLOT+1, false);
					} else {
						// Swap the stacks if they are different
						var tempStack = costSlotStack.copy();
						this.getSlot(COST_SLOT).setStack(clickedStack.copy());
						this.getSlot(slotIndex).setStack(tempStack);
					}

				}
			}
			return;
		}
		super.onSlotClick(slotIndex, button, actionType, player);
	}


	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.dropInventory(player, this.inventory);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return null;
	}

	public ItemStack get_enchantable_item() {
		return this.getSlot(ENCHANTABLE_SLOT).getStack();
	}

	public ItemStack get_cost_item() {
		return this.getSlot(COST_SLOT).getStack();
	}

	public EnchantmentCost getEnchantmentCost(EnchantmentWithLevel enchantWithLevel) {


		return null;
	}

	public record EnchantmentCost(int xp_level_cost, ItemStack item_cost) {}


}