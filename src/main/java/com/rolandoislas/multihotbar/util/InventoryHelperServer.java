package com.rolandoislas.multihotbar.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;

public class InventoryHelperServer {
    /**
     * @see InventoryHelperCommon#reorderInventory(int[], int[], EntityPlayer, Class)
     */
    public static void reorderInventory(int[] hotbarOrder, int[] order, EntityPlayerMP player) {
        InventoryHelperCommon.reorderInventory(hotbarOrder, order, player, InventoryHelperServer.class);
    }

    /**
     * Swaps the contents of two full inventory slots.
     * @param firstSlot Slot index (9-44)
     * @param secondSlot Slot index (9-44)
     */
    @SuppressWarnings("unused")
    public static void swapSlot(int firstSlot, int secondSlot, EntityPlayer player) {
        if (player == null)
            return;
        player.inventoryContainer.slotClick(firstSlot, 0, ClickType.SWAP, player);
        player.inventoryContainer.slotClick(secondSlot, 0, ClickType.SWAP, player);
        player.inventoryContainer.slotClick(firstSlot, 0, ClickType.SWAP, player);
    }
}
