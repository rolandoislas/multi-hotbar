package com.rolandoislas.multihotbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Rolando on 6/11/2016.
 */
public class InventoryHelper {
    public static void swapHotbars(int firstIndex, int secondIndex) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        int firstSlotIndex = indexToSlot(firstIndex);
        int secondSlotIndex = indexToSlot(secondIndex);
        int firstSlotindex936 = indexToSlot936(firstIndex);
        int secondSlotindex936 = indexToSlot936(secondIndex);
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            ItemStack firstItem = player.inventory.getStackInSlot(firstSlotIndex + i);
            ItemStack secondItem = player.inventory.getStackInSlot(secondSlotIndex + i);
            if (firstItem != null || secondItem != null) {
                int window = player.inventoryContainer.windowId;
                // window id, slot, right click (int bool), shift (int bool), player
                Minecraft.getMinecraft().playerController.windowClick(window, firstSlotindex936 + i, 0, 0, player);
                Minecraft.getMinecraft().playerController.windowClick(window, secondSlotindex936 + i, 0, 0, player);
                Minecraft.getMinecraft().playerController.windowClick(window, firstSlotindex936 + i, 0, 0, player);
            }
        }
    }

    private static int indexToSlot936(int index) {
        if (index == 0)
            return indexToSlot(4);
        return indexToSlot(index);
    }

    private static int indexToSlot(int index) {
        return index * InventoryPlayer.getHotbarSize();
    }
}
