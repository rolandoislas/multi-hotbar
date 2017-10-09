package com.rolandoislas.multihotbar.util;

import com.rolandoislas.multihotbar.HotbarLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiOpenEvent;

import java.util.Objects;

/**
 * Created by Rolando on 3/7/2017.
 */
public class GuiUtil {
	private static GuiScreen keepOpen;

	/**
	 * Check if the gui should be canceled and replaced with a custom one
	 * @param event
	 */
	public static void guiOpenEvent(GuiOpenEvent event) {
		if (Minecraft.getMinecraft().player == null)
			return;
		if (event.getGui() == null && keepOpen != null &&
				Objects.equals(Minecraft.getMinecraft().currentScreen, keepOpen) && event.isCancelable()) {
			event.setCanceled(true);
			keepOpen = null;
			return;
		}
		if (event.getGui() == null)
			InventoryHelperClient.reorderInventoryHotbar();
		else if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative)
			InventoryHelperClient.reorderInventoryVanilla();
		else if (event.getGui() instanceof GuiContainer) {
			if (!HotbarLogic.isWorldLocal && InventoryHelperCommon.hotbarIndex != 0)
				keepOpen = event.getGui();
			InvTweaksHelper.addDelay();
			InventoryHelperClient.reorderInventoryVanillaContainer();
			InventoryHelperServer.PLAYER_MUTEX.acquireUninterruptibly();
			InventoryHelperServer.PLAYER_MUTEX.release();
		}
	}
}
