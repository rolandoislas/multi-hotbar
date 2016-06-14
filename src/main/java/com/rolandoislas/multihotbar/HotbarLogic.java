package com.rolandoislas.multihotbar;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Rolando on 6/7/2016.
 */
public class HotbarLogic {
    public static int hotbarIndex = 0;
    public static int[] hotbarOrder = new int[Config.MAX_HOTBARS];
    public static boolean showDefault = false;
    private static WorldJson[] worldJsonArray;
    private World world;
    private String worldAddress;
    private World dimWorld;

    public void mouseEvent(MouseEvent event) {
        // Scrolled
        if (event.dwheel != 0) {
            // Handle hotbar selector scroll
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            // Scrolled right
            if (event.dwheel < 0) {
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
        }
    }

    private void moveSelectionToPreviousHotbar() {
        moveSelection(false);
    }

    private void moveSelection(boolean forward) {
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
        // Check hotbar keys
        int slot = KeyBindings.isHotbarKeyDown();
        int currentItem = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
        // Change hotbars
        if (slot > -1 && currentItem == slot) {
            moveSelectionToNextHotbar();
        }
        // Select a slot
        else if (slot > - 1) {
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = slot;
            if (!Config.relativeHotbarKeys)
                moveSelectionToFirstHotbar();
        }
        // Check toggle key
        if (KeyBindings.showDefaultHotbar.isPressed())
            showDefault = !showDefault;
    }

    private void moveSelectionToFirstHotbar() {
        int awayFromZero = HotbarLogic.hotbarIndex;
        if (awayFromZero == 0)
            return;
        for (int i = 0; i < Config.numberOfHotbars - awayFromZero; i++)
            moveSelectionToNextHotbar();
    }

    public static void reset() {
        showDefault = false;
        hotbarIndex = 0;
        for (int i = 0; i < Config.MAX_HOTBARS; i++)
            hotbarOrder[i] = i;
    }

    public void save() {
        if (this.world == null)
            return;
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
        this.dimWorld = this.world; // Backup incase it was just a dimension change
        this.world = null;
    }

    public void load(World world) {
        if (this.world != null)
            return;
        this.world = world;
        String path = Config.config.getConfigFile().getAbsolutePath().replace("cfg", "json");
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(path));
            worldJsonArray = gson.fromJson(reader, WorldJson[].class);
            if (worldJsonArray != null) {
                for (WorldJson worldJson : worldJsonArray) {
                    if (worldJson.getId().equals(getWorldId(world))) {
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

    private String getWorldId() {
        return getWorldId(this.world);
    }

    private String getWorldId(World world) {
        String id = "";
        if (!world.isRemote) {
            id += world.getWorldInfo().getSeed() + world.getWorldInfo().getWorldName() + world.isRemote +
                    world.getSaveHandler().getWorldDirectory().getAbsolutePath();
        }
        else {
            id = worldAddress;
        }
        return id;
    }


    public void setWorldAddress(String worldAddress) {
        this.worldAddress = worldAddress;
    }

    public void playerChangedDimension() {
        load(this.dimWorld);
    }
}
