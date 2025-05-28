package chiseled_enchanting_table.utils;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;

public class InventoryUtils {

    /**
     * Attempts to insert an ItemStack into a player's inventory, prioritizing existing compatible stacks.
     *
     * @param player The player whose inventory to insert into.
     * @param stackToInsert The ItemStack to insert.
     * @return The remaining ItemStack that could not be inserted, or ItemStack.EMPTY if all was inserted.
     */
    public static ItemStack insertItem(PlayerEntity player, ItemStack stackToInsert, Optional<Integer> priority_slot) {
        if (stackToInsert.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Inventory inventory = player.getInventory();
        ItemStack remainingStack = stackToInsert.copy();

        // First, try to merge with existing stacks
        for (int i = 0; i < 36; ++i) {
            if (remainingStack.isEmpty()) {
                break;
            }

            ItemStack currentStack = inventory.getStack(i);
            if (!currentStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(currentStack, remainingStack)) {
                int space = currentStack.getMaxCount() - currentStack.getCount();
                if (space > 0) {
                    int toTransfer = Math.min(remainingStack.getCount(), space);
                    currentStack.increment(toTransfer);
                    remainingStack.decrement(toTransfer);
                    inventory.setStack(i, currentStack); // Mark dirty
                }
            }
        }

        if (priority_slot.isPresent() && inventory.getStack(priority_slot.get()).isEmpty()) {
            inventory.setStack(priority_slot.get(), remainingStack.copy());
            remainingStack = ItemStack.EMPTY;
        }

        // If items still remain, try to place them in empty slots
        if (!remainingStack.isEmpty()) {
            for (int i = 0; i < 36; ++i) {
                if (remainingStack.isEmpty()) {
                    break;
                }
                ItemStack currentStack = inventory.getStack(i);
                if (currentStack.isEmpty()) {
                    inventory.setStack(i, remainingStack.copy());
                    remainingStack = ItemStack.EMPTY;
                    break; 
                }
            }
        }
        
        // If you also want to handle cases where the player's inventory is full and items should be dropped:
        if (!remainingStack.isEmpty()) {
            player.dropItem(remainingStack, false);
            remainingStack = ItemStack.EMPTY;
        }

        return remainingStack;
    }
}