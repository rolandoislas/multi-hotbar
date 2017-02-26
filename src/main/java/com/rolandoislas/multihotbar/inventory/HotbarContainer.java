package com.rolandoislas.multihotbar.inventory;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.data.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Created by Rolando on 2/27/2017.
 */
public class HotbarContainer extends ContainerPlayer {

	private ItemStack[] inventorySlotsCached = new ItemStack[45];

	public HotbarContainer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer playerIn) {
		super(playerInventory, localWorld, playerIn);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public NonNullList<ItemStack> getOrderedInventory() {
		NonNullList<ItemStack> orderedInventory = NonNullList.create();
		NonNullList<ItemStack> inventory = Minecraft.getMinecraft().player.inventory.mainInventory;
		for (int customOrder : Config.inventoryOrder) {
			int start = HotbarLogic.hotbarOrder[customOrder] * 9;
			for (int slot = 0; slot < 9; slot++)
				orderedInventory.add(inventory.get(start + slot));
		}
		return orderedInventory;
	}

	public void preDraw() {
		NonNullList<ItemStack> inventory = getOrderedInventory();
		for (int inv = 9; inv <= 35; inv++) {
			inventorySlotsCached[inv] = inventorySlots.get(inv).getStack().copy();
			inventorySlots.get(inv).putStack(inventory.get(inv).copy());
		}
		for (int inv = 36; inv <= 44; inv++) {
			inventorySlotsCached[inv] = inventorySlots.get(inv).getStack().copy();
			inventorySlots.get(inv).putStack(inventory.get(inv - 36).copy());
		}
	}

	public void postDraw() {
		for (int inv = 9; inv <= 44; inv++)
			inventorySlots.get(inv).putStack(inventorySlotsCached[inv]);
	}
}
