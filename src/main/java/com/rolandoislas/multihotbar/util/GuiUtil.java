package com.rolandoislas.multihotbar.util;

import net.minecraft.client.Minecraft;
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
		if (Minecraft.getMinecraft().player == null)
			return;
		if (event.getGui() == null)
			InventoryHelper.reorderInventoryHotbar();
		else
			InventoryHelper.reorderInventoryVanilla();
	}
}
