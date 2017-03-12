package com.rolandoislas.multihotbar.inventory;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.data.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

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

	public ArrayList<ItemStack> getOrderedInventory() {
		ArrayList<ItemStack> orderedInventory = new ArrayList<ItemStack>();
		ItemStack[] inventory = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;
		for (int customOrder : Config.inventoryOrder) {
			int start = HotbarLogic.hotbarOrder[customOrder] * 9;
			for (int slot = 0; slot < 9; slot++)
				orderedInventory.add(inventory[start + slot]);
		}
		return orderedInventory;
	}

	public void preDraw() {
		ArrayList<ItemStack> inventory = getOrderedInventory();
		for (int inv = 9; inv <= 35; inv++) {
			inventorySlotsCached[inv] = inventorySlots.get(inv).getStack();
			inventorySlots.get(inv).putStack(inventory.get(inv));
		}
		for (int inv = 36; inv <= 44; inv++) {
			inventorySlotsCached[inv] = inventorySlots.get(inv).getStack();
			inventorySlots.get(inv).putStack(inventory.get(inv - 36));
		}
	}

	public void postDraw() {
		for (int inv = 9; inv <= 44; inv++)
			inventorySlots.get(inv).putStack(inventorySlotsCached[inv]);
	}
}
