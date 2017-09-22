package com.rolandoislas.multihotbar.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiOpenEvent;

/**
 * Created by Rolando on 3/7/2017.
 */
public class GuiUtil {
	/**
	 * Check if the gui should be canceled and replaced with a custom one
	 * @param event
	 */
	public static void guiOpenEvent(GuiOpenEvent event) {
		if (Minecraft.getMinecraft().thePlayer == null)
			return;
		if (event.gui == null)
			InventoryHelperClient.reorderInventoryHotbar();
		else if (event.gui instanceof GuiInventory || event.gui instanceof GuiContainerCreative)
			InventoryHelperClient.reorderInventoryVanilla();
		else if (event.gui instanceof GuiContainer)
			InventoryHelperClient.reorderInventoryVanillaContainer();
	}
}
