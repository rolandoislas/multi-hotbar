package com.rolandoislas.multihotbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

/**
 * Created by Rolando on 6/11/2016.
 */
public class InventoryHelper {

    /**
     * Swap hotbar items
     * @param firstIndex index of first hotbar
     * @param secondIndex index of second hotbar
     */
    public static void swapHotbars(int firstIndex, int secondIndex) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        int firstSlotIndex = indexToSlot(firstIndex);
        int secondSlotIndex = indexToSlot(secondIndex);
        int firstSlotindex936 = indexToSlot936(firstIndex);
        int secondSlotindex936 = indexToSlot936(secondIndex);
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            ItemStack firstItem = player.inventory.getStackInSlot(firstSlotIndex + i);
            ItemStack secondItem = player.inventory.getStackInSlot(secondSlotIndex + i);
            if (firstItem != null || secondItem != null)
                swapSlot(firstSlotindex936 + i, secondSlotindex936 + i);
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
    static void swapSlot(int firstSlot, int secondSlot) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        // Move items
        int window = player.inventoryContainer.windowId;
        try {
            Minecraft.getMinecraft().playerController.windowClick(window, firstSlot, 0, ClickType.SWAP, player);
            Minecraft.getMinecraft().playerController.windowClick(window, secondSlot, 0, ClickType.SWAP, player);
            Minecraft.getMinecraft().playerController.windowClick(window, firstSlot, 0, ClickType.SWAP, player);
        } catch (IndexOutOfBoundsException ignore) {}
        HotbarLogic.ignoreSlot(fullInventoryToMainInventory(firstSlot));
        HotbarLogic.ignoreSlot(fullInventoryToMainInventory(secondSlot));
        // InvTweaks delay
        InvTweaksHelper.addDelay();
    }

    /**
     * Convert full inventory (9-44) index to main inventory (0-35).
     * @param slotIndex full inventory index (9-44)
     * @return main inventory index (0-35)
     */
    static int fullInventoryToMainInventory(int slotIndex) {
        return slotIndex <= 35 ? slotIndex : slotIndex - 36;
    }

    /**
     * Convert mainInventory index (0-35) to full inventory (9-44)
     * @param slotIndex mainInventory index
     * @return full inventory index (9-44)
     */
    static int mainInventoryToFullInventory(int slotIndex) {
        return slotIndex >= 9 ? slotIndex : 36 + slotIndex;
    }
}
