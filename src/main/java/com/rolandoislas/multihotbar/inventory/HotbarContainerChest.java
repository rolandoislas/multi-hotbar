package com.rolandoislas.multihotbar.inventory;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.data.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Created by Rolando on 3/7/2017.
 */
public class HotbarContainerChest extends ContainerChest {
	private final IInventory chestInventory;
	public final int start;
	private ItemStack[] inventorySlotsCached;

	public HotbarContainerChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer player) {
		super(playerInventory, chestInventory, player);
		this.chestInventory = chestInventory;
		start = chestInventory.getSizeInventory();
		inventorySlotsCached = new ItemStack[chestInventory.getSizeInventory() + 45];
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
				orderedInventory.add(inventory[(start + slot)]);
		}
		return orderedInventory;
	}

	public void preDraw() {
		ArrayList<ItemStack> inventory = getOrderedInventory();
		for (int inv = start; inv <= 26 + start; inv++) {
			inventorySlotsCached[inv] = inventorySlots.get(inv).getStack();
			inventorySlots.get(inv).putStack(inventory.get(inv - start + 9));
		}
		for (int inv = 27 + start; inv <= 35 + start; inv++) {
			inventorySlotsCached[inv] = inventorySlots.get(inv).getStack();
			inventorySlots.get(inv).putStack(inventory.get(inv - start - 27));
		}
	}

	public void postDraw() {
		int start = chestInventory.getSizeInventory();
		for (int inv = start; inv <= 35 + start; inv++)
			inventorySlots.get(inv).putStack(inventorySlotsCached[inv]);
	}
}
