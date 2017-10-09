package com.rolandoislas.multihotbar.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class InventoryHelperServer {
    static final Map<UUID, PlayerHotbar> PLAYER_HOTBARS = new HashMap<>();
    public static final Semaphore PLAYER_MUTEX = new Semaphore(1);

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

    /**
     * Updates the server stored player hotbars to the new order
     * @param player player to update
     * @param order order to save
     * @param index index to save
     */
    public static void updatePlayer(EntityPlayer player, int[] order, int index) {
        PLAYER_HOTBARS.put(player.getUniqueID(), new PlayerHotbar(order, index));
    }

    static class PlayerHotbar {
        private final int[] order;
        private final int index;

        PlayerHotbar(int[] order, int index) {
            this.order = order;
            this.index = index;
        }

        int[] getHotbarOrder() {
            return order;
        }

        int getHotbarIndex() {
            return index;
        }
    }
}
