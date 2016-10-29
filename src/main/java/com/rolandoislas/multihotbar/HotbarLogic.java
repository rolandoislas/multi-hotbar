package com.rolandoislas.multihotbar;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rolando on 6/7/2016.
 */
public class HotbarLogic {
    public static int hotbarIndex = 0;
    public static int[] hotbarOrder = new int[Config.MAX_HOTBARS];
    public static boolean showDefault = false;
    private static WorldJson[] worldJsonArray;
    private String worldAddress;
    private ArrayList<Integer> pickupSlot = new ArrayList<Integer>();
    private boolean isWorldLocal;
    private ItemStack[] inventory;
    private int serverPickupIgnoreTicks = 0;

    public void mouseEvent(MouseEvent event) {
        if (InventoryHelper.waitTicks > 0 || HotbarLogic.showDefault)
            return;
        // Scrolled
        if (event.getDwheel() != 0) {
            // Handle hotbar selector scroll
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            // Scrolled right
            if (event.getDwheel() < 0) {
                if (KeyBindings.scrollModifier.isKeyDown())
                    moveSelectionToNextHotbar();
                else if (player.inventory.currentItem < InventoryPlayer.getHotbarSize() - 1)
                    player.inventory.currentItem++;
                else {
                    player.inventory.currentItem = 0;
                    moveSelectionToNextHotbar();
                }
            }
            // Scrolled left
            else {
                if (KeyBindings.scrollModifier.isKeyDown())
                    moveSelectionToPreviousHotbar();
                else if (player.inventory.currentItem > 0)
                    player.inventory.currentItem--;
                else {
                    player.inventory.currentItem = InventoryPlayer.getHotbarSize() - 1;
                    moveSelectionToPreviousHotbar();
                }
            }
            event.setCanceled(true);
            resetTooltipTicks();
        }
    }

    private void moveSelectionToPreviousHotbar() {
        moveSelection(false);
    }

    private void moveSelection(boolean forward) {
        if (Config.numberOfHotbars == 1)
            return;
        int previousIndex = hotbarIndex;
        hotbarIndex += forward ? 1 : -1; // Change hotbar
        hotbarIndex = hotbarIndex < 0 ? Config.numberOfHotbars - 1 : hotbarIndex; // Loop from first to last
        hotbarIndex = hotbarIndex >= Config.numberOfHotbars ? 0 : hotbarIndex; // Loop from last to first
        InventoryHelper.swapHotbars(0, hotbarOrder[hotbarIndex]);
        // save swapped position
        int orderFirst = hotbarOrder[previousIndex];
        hotbarOrder[previousIndex] = hotbarOrder[hotbarIndex];
        hotbarOrder[hotbarIndex] = orderFirst;
    }

    private void moveSelectionToNextHotbar() {
        moveSelection(true);
    }

    public void keyPressed(InputEvent.KeyInputEvent event) {
        if (InventoryHelper.waitTicks > 0)
            return;
        // Check toggle key
        if (KeyBindings.showDefaultHotbar.isPressed()) {
            showDefault = !showDefault;
            Minecraft.getMinecraft().gameSettings.heldItemTooltips = showDefault;
        }
        if (HotbarLogic.showDefault)
            return;
        // Check hotbar keys
        int slot = KeyBindings.isHotbarKeyDown();
        int currentItem = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
        // Change hotbars
        if (slot > -1 && currentItem == slot && Config.numberOfHotbars > 1) {
            moveSelectionToNextHotbar();
        }
        // Select a slot
        else if (slot > - 1) {
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = slot;
            if (!Config.relativeHotbarKeys)
                moveSelectionToHotbar(0);
        }
        if (slot > -1)
            resetTooltipTicks();
    }

    private void resetTooltipTicks() {
        HotBarRenderer.tooltipTicks = 128;
    }

    private void moveSelectionToHotbar(int index) {
        while (hotbarIndex != index)
            moveSelectionToNextHotbar();
    }

    public static void reset() {
        showDefault = false;
        updateTooltips();
        hotbarIndex = 0;
        for (int i = 0; i < Config.MAX_HOTBARS; i++)
            hotbarOrder[i] = i;
        try {
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = 0;
        } catch (Exception ignore) {}
    }

    private void save() {
        String path = Config.config.getConfigFile().getAbsolutePath().replace("cfg", "json");
        try {
            boolean found = false;
            if (worldJsonArray != null) {
                for (WorldJson worldJson : worldJsonArray) {
                    if (worldJson.getId().equals(getWorldId())) {
                        found = true;
                        worldJson.setIndex(hotbarIndex);
                        worldJson.setOrder(hotbarOrder);
                        break;
                    }
                }
            }
            if ((!found) || worldJsonArray == null){
                if (worldJsonArray == null)
                    worldJsonArray = new WorldJson[1];
                else
                    worldJsonArray = Arrays.copyOf(worldJsonArray, worldJsonArray.length + 1);
                int index = worldJsonArray.length - 1;
                worldJsonArray[index] = new WorldJson();
                worldJsonArray[index].setId(getWorldId());
                worldJsonArray[index].setIndex(hotbarIndex);
                worldJsonArray[index].setOrder(hotbarOrder);
            }
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(path);
            String json = gson.toJson(worldJsonArray);
            writer.write(json);
            writer.close();
        } catch (IOException ignore) {}
    }

    private void load() {
        String path = Config.config.getConfigFile().getAbsolutePath().replace("cfg", "json");
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(path));
            worldJsonArray = gson.fromJson(reader, WorldJson[].class);
            if (worldJsonArray != null) {
                for (WorldJson worldJson : worldJsonArray) {
                    if (worldJson.getId().equals(getWorldId())) {
                        hotbarIndex = worldJson.getIndex();
                        hotbarOrder = worldJson.getOrder();
                        break;
                    } else
                        reset();
                }
            }
            else
                reset();
        } catch (FileNotFoundException ignore) {
            reset();
        }
    }

    private static void updateTooltips() {
        Minecraft.getMinecraft().gameSettings.heldItemTooltips = showDefault;
    }

    private String getWorldId() {
        // Construct unique id or use world address if remote
        String id;
        if (isWorldLocal) {
            World world = Minecraft.getMinecraft().getIntegratedServer().getEntityWorld();
            id = world.getWorldInfo().getSeed() + world.getWorldInfo().getWorldName() +
                    world.getSaveHandler().getWorldDirectory().getAbsolutePath();
        }
        else {
            id = worldAddress;
        }
        // MD5 because the raw id looks horrible with an escaped path and spaces in it
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(id.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array)
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            id = sb.toString();
        } catch (NoSuchAlgorithmException ignore) {}
        return id;
    }

    private int getFirstEmptyStack(InventoryPlayer inventory) {
        for (int i = 0; i < Config.numberOfHotbars; i++) {
            for (int j = 0; j < 9; j++) {
                int index = hotbarOrder[i] * 9 + j;
                ItemStack stack = inventory.getStackInSlot(index);
                if (stack == null)
                    return index;
            }
        }
        return -1;
    }

    public void pickupEvent(EntityItemPickupEvent event) {
        // Check if compatible stack is in inventory
        int slot = getFirstCompatibleStack(event.getItem().getEntityItem(), event.getEntityPlayer().inventory);
        if (slot >= 0) {
            ItemStack stack = event.getEntityPlayer().inventory.getStackInSlot(slot);
            if (stack == null || stack.stackSize + event.getItem().getEntityItem().stackSize <= stack.getMaxStackSize())
                return;
        }
        // Get the first empty stack
        slot = event.getEntityPlayer().inventory.getFirstEmptyStack();
        // No space in inventory
        if (slot < 0)
            return;
        // Does not need a move
        if (slot == getFirstEmptyStack(event.getEntityPlayer().inventory))
            return;
        this.pickupSlot.add(slot);
    }

    private int getFirstCompatibleStack(ItemStack itemStack, InventoryPlayer inventory) {
        for (int i = 0; i < Config.numberOfHotbars; i++) {
            for (int j = 0; j < 9; j++) {
                int index = hotbarOrder[i] * 9 + j;
                ItemStack stack = inventory.getStackInSlot(index);
                if (stack != null && stack.isStackable() && stack.isItemEqual(itemStack) &&
                        ItemStack.areItemStackTagsEqual(stack, itemStack) &&
                        stack.stackSize < stack.getMaxStackSize()) {
                    return index;
                }
            }
        }
        return -1;
    }

    private void reorderPickedupItem(TickEvent.PlayerTickEvent event) {
        // Nothing to move
        if (this.pickupSlot.isEmpty())
            return;
        // Wait for item to appear after an uncertain number of ticks
        if (event.player.inventory.getStackInSlot(pickupSlot.get(0)) == null)
            return;
        // Move the picked up item to the correct slot
        int clickSlotFirst = this.pickupSlot.get(0) >= 9 ? this.pickupSlot.get(0) : 36 + this.pickupSlot.get(0);
        int clickSlotSecond = getFirstEmptyStack(event.player.inventory) >= 9 ? getFirstEmptyStack(event.player.inventory) : 36 + getFirstEmptyStack(event.player.inventory);
        InventoryHelper.swapSlot(clickSlotFirst, clickSlotSecond);
        this.pickupSlot.remove(0);
    }

    void connectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        worldAddress = event.getManager().getRemoteAddress().toString();
        isWorldLocal = event.isLocal();
        load();
    }

    void disconnectedFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        save();
    }

    void deathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (!player.getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID()))
                return;
            if (!player.worldObj.getGameRules().getBoolean("keepInventory"))
                HotbarLogic.reset();
        }
    }

    public void playerTick(TickEvent.PlayerTickEvent event) {
        reorderPickedupItem(event);
        if (isWorldLocal)
            return;
        checkPlayerDeath(event.player);
        checkItemPickedUp(event.player);
    }

	/**
     * Check if the player has picked uo an item.
     * Item pickup event is not called when connected to remote servers.
     * Let the pickup event handler handle this when it can.
     * @param player
     */
    private void checkItemPickedUp(EntityPlayer player) {
        // Set the inventory
        if (inventory == null ||
                // Ignore if inventory is open TODO check inventory movement better
                Minecraft.getMinecraft().currentScreen instanceof GuiInventory ||
                serverPickupIgnoreTicks > 0) {
            inventory = player.inventory.mainInventory.clone();
            if (serverPickupIgnoreTicks > 0)
                serverPickupIgnoreTicks--;
        }
        // Find the changed item
        ArrayList<EntityItem> changed = new ArrayList<EntityItem>();
        ArrayList<Integer> changedSlot = new ArrayList<Integer>();
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            if (
                    // Check if there is an item in a slot that was empty
                    (player.inventory.mainInventory[i] != null && inventory[i] == null) ||
                    // Make sure the slots are equal
                    player.inventory.mainInventory[i] != null &&
                    !(player.inventory.mainInventory[i].isItemEqual(inventory[i]) &&
                    ItemStack.areItemStackTagsEqual(player.inventory.mainInventory[i], inventory[i]))) {
                ItemStack changedStack = player.inventory.mainInventory[i].copy();
                changed.add(new EntityItem(player.worldObj, player.posX, player.posY, player.posY, changedStack));
                changedSlot.add(i);
            }
        }
        // If no item changed it was probably just an item removal.
        if (!changed.isEmpty()) {
            int size = changed.size();
            if (size > 1 && areInventoryItemsSame(player.inventory.mainInventory, inventory))
                return;
            for (int i = 0; i < size; i++) {
                int slot = changedSlot.get(0);
                // Remove item from inventory to emulate the inventory state that the event handler expects
                if (inventory[changedSlot.get(0)] == null)
                    player.inventory.mainInventory[slot].stackSize = 0;
                else
                    player.inventory.mainInventory[slot].stackSize -= inventory[slot].stackSize;
                if (player.inventory.mainInventory[slot].stackSize == 0)
                    player.inventory.removeStackFromSlot(changedSlot.get(0));
                // Create the pickup event
                pickupEvent(new EntityItemPickupEvent(player, changed.get(0)));
                serverPickupIgnoreTicks = 5;
                // Add item back to inventory to emulate the event having already taking place and the tick handler will
                // move it
                player.inventory.setInventorySlotContents(slot, changed.get(0).getEntityItem().copy());
                // Remove from array lists
                changed.remove(0);
                changedSlot.remove(0);
            }
        }
        // Update cached inventory
        inventory = player.inventory.mainInventory.clone();
    }

    private boolean areInventoryItemsSame(ItemStack[] inventory, ItemStack[] inventory2) {
        for (ItemStack item : inventory) {
            boolean found = false;
            for (ItemStack item2 : inventory2)
                if ((item == null && item2 == null) || (item != null && item2 != null && item.isItemEqual(item2) &&
                        ItemStack.areItemStackTagsEqual(item, item2)))
                    found = true;
            if (!found)
                return false;
        }
        return true;
    }

    /***
     * Check for a player death on remote servers.
     * The player death event is not called.
     * Let the death event handler evoke the reset if possible.
     * @param player
     */
    private void checkPlayerDeath(EntityPlayer player) {
        if (!player.isEntityAlive()) {
            for (ItemStack slot : player.inventoryContainer.getInventory())
                if (slot != null)
                    return;
            reset();
        }
    }
}
