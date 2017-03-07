package com.rolandoislas.multihotbar.gui;

import com.rolandoislas.multihotbar.inventory.HotbarContainer;
import com.rolandoislas.multihotbar.inventory.HotbarContainerChest;
import com.rolandoislas.multihotbar.inventory.InventorySlotNeverEmpty;
import com.rolandoislas.multihotbar.util.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Created by Rolando on 3/7/2017.
 */
public class HotbarGuiChest extends GuiChest {
	public HotbarGuiChest(GuiChest gui) {
		super(Minecraft.getMinecraft().player.inventory, getChestInventory(gui));
		inventorySlots = new HotbarContainerChest(Minecraft.getMinecraft().player.inventory,
				getChestInventory(gui), Minecraft.getMinecraft().player);
	}

	private static IInventory getChestInventory(GuiChest gui) {
		return ReflectionHelper.getPrivateValue(GuiChest.class, gui, "lowerChestInventory",
				"field_147015_w");
	}

	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		if (slotIn != null)
			slotId = slotIn.slotNumber;
		if (slotId < ((HotbarContainerChest)inventorySlots).start) {
			super.handleMouseClick(slotIn, slotId, mouseButton, type);
		}
		else {
			slotId -= ((HotbarContainerChest)inventorySlots).start - 9;
			slotId = InventoryHelper.normalSlotToHotbarOrderedSlot(slotId, true);
			slotId += ((HotbarContainerChest)inventorySlots).start - 9;
			this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, mouseButton, type, this.mc.player);
		}
		}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		((HotbarContainerChest)inventorySlots).preDraw();
		super.drawScreen(mouseX, mouseY, partialTicks);
		((HotbarContainerChest)inventorySlots).postDraw();
		// Fix for "Q" throw
		Slot theSlot = getSlotUnderMouse();
		theSlot = new Slot(new InventorySlotNeverEmpty(),
				theSlot != null ? theSlot.slotNumber : 0, 0, 0);
		theSlot.slotNumber = theSlot.getSlotIndex();
		ReflectionHelper.setPrivateValue(GuiContainer.class, this, theSlot,
				"theSlot", "field_147006_u");
	}
}
