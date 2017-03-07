package com.rolandoislas.multihotbar.util;

import com.rolandoislas.multihotbar.data.Config;
import com.rolandoislas.multihotbar.gui.HotbarGuiChest;
import com.rolandoislas.multihotbar.gui.HotbarGuiInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;

/**
 * Created by Rolando on 3/7/2017.
 */
public class GuiUtil {
	/**
	 * Check if the gui should be canceled and replaced with a custom one
	 * @param event
	 */
	public static void guiEvent(GuiScreenEvent event) {
		if (!Config.useCustomInventory)
			return;
		if (event.getGui().getClass() == GuiInventory.class) {
			if (event.isCancelable())
				event.setCanceled(true);
			Minecraft.getMinecraft().displayGuiScreen(new HotbarGuiInventory());
		} else if (Config.useCustomInventory && event.getGui().getClass() == GuiChest.class) {
			if (event.isCancelable())
				event.setCanceled(true);
			Minecraft.getMinecraft().displayGuiScreen(new HotbarGuiChest((GuiChest) event.getGui()));
		}
	}
}
