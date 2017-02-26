package com.rolandoislas.multihotbar.gui;

import com.rolandoislas.multihotbar.inventory.HotbarContainer;
import com.rolandoislas.multihotbar.inventory.InventorySlotNeverEmpty;
import com.rolandoislas.multihotbar.util.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Created by Rolando on 2/26/2017.
 */
public class HotbarGuiInventory extends GuiInventory {
	public HotbarGuiInventory() {
		super(Minecraft.getMinecraft().player);
		inventorySlots = new HotbarContainer(Minecraft.getMinecraft().player.inventory, true,
				Minecraft.getMinecraft().player);
	}

	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		if (slotIn != null)
			slotId = slotIn.slotNumber;
		slotId = InventoryHelper.normalSlotToHotbarOrderedSlot(slotId, true);
		this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, mouseButton, type, this.mc.player);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		((HotbarContainer)inventorySlots).preDraw();
		super.drawScreen(mouseX, mouseY, partialTicks);
		((HotbarContainer)inventorySlots).postDraw();
		// Fix for "Q" throw
		Slot theSlot = getSlotUnderMouse();
		theSlot = new Slot(new InventorySlotNeverEmpty(),
				theSlot != null ? theSlot.slotNumber : 0, 0, 0);
		theSlot.slotNumber = theSlot.getSlotIndex();
		ReflectionHelper.setPrivateValue(GuiContainer.class, this, theSlot,
				"theSlot", "field_147006_u");
	}
}
