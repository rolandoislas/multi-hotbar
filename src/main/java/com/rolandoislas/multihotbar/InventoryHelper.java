package com.rolandoislas.multihotbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by Rolando on 6/11/2016.
 */
public class InventoryHelper {
    private static int lastItem = -1;
    public static int waitTicks = 0;

    public static void swapHotbars(int firstIndex, int secondIndex) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        waitTicks = 1000000; // One MILLION ticks!
        lastItem = player.inventory.currentItem;
        boolean slotFound = false;
        int firstSlotIndex = indexToSlot(firstIndex);
        int secondSlotIndex = indexToSlot(secondIndex);
        int firstSlotindex936 = indexToSlot936(firstIndex);
        int secondSlotindex936 = indexToSlot936(secondIndex);
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            ItemStack firstItem = player.inventory.getStackInSlot(firstSlotIndex + i);
            ItemStack secondItem = player.inventory.getStackInSlot(secondSlotIndex + i);
            if (firstItem != null || secondItem != null)
                swapSlot(firstSlotindex936 + i, secondSlotindex936 + i);
            if (Loader.isModLoaded("inventorytweaks")) {
                // Set currentItem to a mull slot or two item slots
                if ((!slotFound) && (firstItem == null || secondItem != null)) {
                    player.inventory.currentItem = i;
                    slotFound = true;
                }
                // Set the current item to an invalid one
                else if ((!slotFound) && i == InventoryPlayer.getHotbarSize() - 1)
                    player.inventory.currentItem = -1;
                waitTicks = 5; // Wait a few ticks so Inventory Tweaks' tick event doesn't catch the move
            }
            else
                waitTicks = 0;
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

    public static void tick() {
        if (waitTicks > 0) {
            waitTicks--;
            return;
        }
        if (lastItem > -1) {
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = lastItem;
            lastItem = -1;
        }
    }

    private static void swapSlot(int firstSlot, int secondSlot) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int window = player.inventoryContainer.windowId;
        Minecraft.getMinecraft().playerController.windowClick(window, firstSlot, 0, ClickType.SWAP, player);
        Minecraft.getMinecraft().playerController.windowClick(window, secondSlot, 0, ClickType.SWAP, player);
        Minecraft.getMinecraft().playerController.windowClick(window, firstSlot, 0, ClickType.SWAP, player);
    }
}
