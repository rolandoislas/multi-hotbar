package com.rolandoislas.multihotbar.util;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.MultiHotbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;

/**
 * Created by Rolando on 6/11/2016.
 */
public class InventoryHelperClient {
    private static int savedIndex;

    static {
        savedIndex = -1;
    }

    /**
     * @see InventoryHelperCommon#swapHotbars(int, int, EntityPlayer, Class)
     * Uses local player as default
     */
    public static void swapHotbars(int firstIndex, int secondIndex) {
        InventoryHelperCommon.swapHotbars(firstIndex, secondIndex, Minecraft.getMinecraft().thePlayer,
                InventoryHelperClient.class);
    }

    /**
     * Change current item after a few ticks. Reverts to slot that was selected before hotbar swap.
     */
    public static void tick() {
        InvTweaksHelper.tick();
    }

    /**
     * Swaps the contents of two full inventory slots.
     * @param firstSlot Slot index (9-44)
     * @param secondSlot Slot index (9-44)
     */
    @SuppressWarnings("WeakerAccess")
    public static void swapSlot(int firstSlot, int secondSlot, EntityPlayer player) {
        if (player == null)
            return;
        if (player instanceof EntityPlayerSP) {
            // Move items
            int window = player.inventoryContainer.windowId;
            try {
                Minecraft.getMinecraft().playerController.windowClick(window, firstSlot, 0, ClickType.SWAP, player);
                Minecraft.getMinecraft().playerController.windowClick(window, secondSlot, 0, ClickType.SWAP, player);
                Minecraft.getMinecraft().playerController.windowClick(window, firstSlot, 0, ClickType.SWAP, player);
            } catch (IndexOutOfBoundsException ignore) {
            }
            HotbarLogic.ignoreSlot(fullInventoryToMainInventory(firstSlot));
            HotbarLogic.ignoreSlot(fullInventoryToMainInventory(secondSlot));
            // InvTweaks delay
            InvTweaksHelper.addDelay();
        }
        else if (player instanceof EntityPlayerMP) {
            player.inventoryContainer.slotClick(firstSlot, 0, ClickType.SWAP, player);
            player.inventoryContainer.slotClick(secondSlot, 0, ClickType.SWAP, player);
            player.inventoryContainer.slotClick(firstSlot, 0, ClickType.SWAP, player);
        }
    }

    /**
     * @see #swapSlot(int, int, EntityPlayer)
     * Deafults to local player
     */
    public static void swapSlot(int firstSlot, int secondSlot) {
        swapSlot(firstSlot, secondSlot, Minecraft.getMinecraft().thePlayer);
    }

    /**
     * Convert full inventory (9-44) index to main inventory (0-35).
     * @param slotIndex full inventory index (9-44)
     * @return main inventory index (0-35)
     */
    private static int fullInventoryToMainInventory(int slotIndex) {
        return slotIndex <= 35 ? slotIndex : slotIndex - 36;
    }

    /**
     * Convert mainInventory index (0-35) to full inventory (9-44)
     * @param slotIndex mainInventory index
     * @return full inventory index (9-44)
     */
    public static int mainInventoryToFullInventory(int slotIndex) {
        return slotIndex >= 9 ? slotIndex : 36 + slotIndex;
    }

    /**
     * Orders the inventory to the saved row order
     */
    public static void reorderInventoryHotbar() {
        synchronized (InventoryHelperClient.class) {
            if (savedIndex < 0)
                return;
            HotbarLogic.reset(false);
            HotbarLogic.moveSelectionToHotbar(savedIndex);
            savedIndex = -1;
        }
    }

    /**
     * Orders the inventory to the vanilla order
     */
    static void reorderInventoryVanilla() {
        synchronized (InventoryHelperClient.class) {
            savedIndex = HotbarLogic.hotbarIndex;
            reorderInventory(HotbarLogic.hotbarOrder, new int[]{0, 1, 2, 3}, Minecraft.getMinecraft().thePlayer);
        }
    }


    /**
     * @see InventoryHelperCommon#reorderInventory(int[], int[], EntityPlayer, Class)
     */
    private static void reorderInventory(int[] hotbarOrder, int[] order, EntityPlayer player) {
        InventoryHelperCommon.reorderInventory(hotbarOrder, order, player, InventoryHelperClient.class);
    }

    /**
     * Set a hotbar index to be used when resetting the inventory
     * @param savedIndex hotbar index
     */
    public static void setSavedIndex(int savedIndex) {
        InventoryHelperClient.savedIndex = savedIndex;
        if (Minecraft.getMinecraft().currentScreen == null)
            reorderInventoryHotbar();
    }
}
