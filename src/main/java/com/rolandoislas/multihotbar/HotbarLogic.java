package com.rolandoislas.multihotbar;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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

    private int getFirstEmptyStack() {
        for (int i = 0; i < Config.numberOfHotbars; i++) {
            for (int j = 0; j < 9; j++) {
                int index = hotbarOrder[i] * 9 + j;
                ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
                if (stack == null)
                    return index;
            }
        }
        return -1;
    }

    public void pickupEvent(EntityItemPickupEvent event) {
        // Check if compatible stack is in inventory
        int slot = getFirstCompatibleStack(event.getItem().getEntityItem());
        if (slot >= 0) {
            ItemStack stack = event.getEntityPlayer().inventory.getStackInSlot(slot);
            if (stack == null || stack.stackSize + event.getItem().getEntityItem().stackSize <= stack.getMaxStackSize())
                return;
        }
        // Get the first empty stack
        slot = event.getEntityPlayer().inventory.getFirstEmptyStack();
        if (slot < 0 || slot < 9 && hotbarIndex == 0)
            return;
        this.pickupSlot.add(slot);
    }

    private int getFirstCompatibleStack(ItemStack itemStack) {
        for (int i = 0; i < Config.numberOfHotbars; i++) {
            for (int j = 0; j < 9; j++) {
                int index = hotbarOrder[i] * 9 + j;
                ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
                if (stack != null && stack.isStackable() && stack.isItemEqual(itemStack) &&
                        ItemStack.areItemStackTagsEqual(stack, itemStack) &&
                        stack.stackSize < stack.getMaxStackSize()) {
                    return index;
                }
            }
        }
        return -1;
    }

    void tickEvent(TickEvent.ClientTickEvent event) {
        // Move the picked up item to the correct slot
        if (this.pickupSlot.isEmpty())
            return;
        if (this.pickupSlot.get(0) < 0) {
            this.pickupSlot.remove(0);
            return;
        }
        if (Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(pickupSlot.get(0)) == null)
            return;
        int clickSlotFirst = this.pickupSlot.get(0) >= 9 ? this.pickupSlot.get(0) : 36 + this.pickupSlot.get(0);
        int clickSlotSecond = getFirstEmptyStack() >= 9 ? getFirstEmptyStack() : 36 + getFirstEmptyStack();
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
}
