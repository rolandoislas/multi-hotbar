package com.rolandoislas.multihotbar.util;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.MultiHotbar;
import com.rolandoislas.multihotbar.data.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InventoryHelperCommon {
    /**
     * Swap hotbar items
     * @param firstIndex index of first hotbar
     * @param secondIndex index of second hotbar
     * @param helper class that controls swapping
     */
    static void swapHotbars(int firstIndex, int secondIndex, EntityPlayer player, Class<?> helper) {
        Method swapSlot = null;
        try {
            swapSlot = helper.getMethod("swapSlot", int.class, int.class, EntityPlayer.class);
        } catch (NoSuchMethodException e) {
            MultiHotbar.logger.error(e);
        }
        if (player == null || firstIndex == secondIndex || swapSlot == null)
            return;
        int firstSlotIndex = indexToSlot(firstIndex);
        int secondSlotIndex = indexToSlot(secondIndex);
        int firstSlotindex936 = indexToSlot936(firstIndex);
        int secondSlotindex936 = indexToSlot936(secondIndex);
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            ItemStack firstItem = player.inventory.getStackInSlot(firstSlotIndex + i);
            ItemStack secondItem = player.inventory.getStackInSlot(secondSlotIndex + i);
            if (firstItem != null || secondItem != null)
                try {
                    swapSlot.invoke(null, firstSlotindex936 + i, secondSlotindex936 + i, player);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    MultiHotbar.logger.error(e);
                }
        }
    }

    /**
     * Get the first slot index for hotbar index
     * @param index Hotbar index
     * @return slot index (9-44) of full inventory
     */
    private static int indexToSlot936(int index) {
        if (index == 0)
            return indexToSlot(4);
        return indexToSlot(index);
    }

    /**
     * Get the first slot index for hotbar index.
     * @param index hotbar index
     * @return slot index (0-35) of items only inventory
     */
    private static int indexToSlot(int index) {
        return index * InventoryPlayer.getHotbarSize();
    }

    /**
     * Reorders the inventory to the given order
     * @param hotbarOrder jumbled hotbar order
     * @param order order to use
     * @param player player to order
     * @param helper class that controls swapping
     */
    static void reorderInventory(int[] hotbarOrder, int[] order, EntityPlayer player, Class<?> helper) {
        printInventory(player);
        // Sort to 0-3 order
        for (int index = 0; index < Config.MAX_HOTBARS; index++) {
            int slot = hotbarOrder[index];
            swapHotbars(index, slot, player, helper);
            for (int newIndex = 0; newIndex < Config.MAX_HOTBARS; newIndex++)
                if (hotbarOrder[newIndex] == index)
                    hotbarOrder[newIndex] = slot;
            hotbarOrder[index] = index;
        }
        MultiHotbar.logger.debug(Arrays.toString(hotbarOrder));
        // Sort to requested order
        MultiHotbar.logger.debug("Sort: " + Arrays.toString(order));
        for (int index = 0; index < Config.MAX_HOTBARS; index++) {
            int slot = order[index];
            if (index == hotbarOrder[slot] || order[index] == hotbarOrder[index] ||
                    order[slot] == hotbarOrder[slot])
                continue;
            MultiHotbar.logger.debug(String.format("%d %d", index, slot));
            swapHotbars(index, slot, player, helper);
            hotbarOrder[slot] = hotbarOrder[index];
            hotbarOrder[index] = slot;
            MultiHotbar.logger.debug(Arrays.toString(hotbarOrder));
        }
        printInventory(player);
    }

    /**
     * Logs the inventory slots to debug logger
     * @param player player that contains the inventory to print
     */
    private static void printInventory(EntityPlayer player) {
        if (player == null)
            return;
        StringBuilder out = new StringBuilder();
        int index = 0;
        for (Object slotObject : player.inventoryContainer.inventorySlots) {
            if (!(slotObject instanceof Slot))
                return;
            Slot slot = (Slot) slotObject;
            ItemStack stack = slot.getStack();
            if (stack != null)
                out.append(slot.getStack().getDisplayName().substring(0, 1));
            else
                out.append("A");
            index++;
            if (index % 9 == 0)
                out.append("\n");
        }
        MultiHotbar.logger.debug(out.toString());
    }

    /**
     * Gets the current item that the player is holding, taking into account the swapped hotbar order
     */
    public static ItemStack getCurrentItem(EntityPlayer player) {
        int hotbar = HotbarLogic.hotbarOrder[HotbarLogic.hotbarIndex];
        return player.inventory.getStackInSlot(hotbar * InventoryPlayer.getHotbarSize() +
                player.inventory.currentItem);
    }
}
