package com.rolandoislas.multihotbar.data;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.MultiHotbar;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rolando on 6/6/2016.
 */
public class Config {
    public static final int MAX_HOTBARS = 4;
    public static int numberOfHotbars;
    public static Configuration config;
    public static boolean relativeHotbarKeys;
    public static boolean relativeHotbarPickups;
    //public static int[] inventoryOrder;
    public static boolean stackedHotbars;
    public static boolean shiftChat;

    public static void load() {
        // Check if server and set hotbars to max
        boolean server = FMLCommonHandler.instance().getSide() == Side.SERVER;
        if (server) {
            numberOfHotbars = MAX_HOTBARS;
            return;
        }
        // Handle client config
        config.load();
        numberOfHotbars = config.getInt("Number of Hotbars", Configuration.CATEGORY_GENERAL, 2, 2, MAX_HOTBARS,
                "Defines the amount of hotbars that should be displayed");
        relativeHotbarKeys = config.getBoolean("Relative Hotbar Keys", Configuration.CATEGORY_GENERAL, false,
                "If set to true, pressing the hotbar keys (e.g. 1-9) will move to the slot on the currently " +
                        "selected hotbar instead of the first");
        relativeHotbarPickups = config.getBoolean("Relative Hotbar Pickups", Configuration.CATEGORY_GENERAL, false,
                "When enabled slots are filled starting with the currently selected hotbar. " +
                        "If disabled slots fill starting from the first hotbar.");
        /*inventoryOrder = commaIntStringToArray(config.getString("Inventory Order", Configuration.CATEGORY_GENERAL,
                "0,1,2,3", "Sets the order of the inventory rows\n" +
                        "Expects a no spaces, comma separated list with the values 0-3 each used once."));*/
        stackedHotbars = config.getBoolean("Stacked Hotbars", Configuration.CATEGORY_GENERAL, false,
                "If true there will be only one hotbar per row.");
        shiftChat = config.getBoolean("Shift Chat", Configuration.CATEGORY_GENERAL, false,
                "Shifts chat up in the event there is more than one row of hotbars.");
        config.save();
    }

    private static int[] commaIntStringToArray(String string) {
        String[] split = string.split(",");
        if (split.length != 4)
            return new int[] {0,1,2,3};
        int[] intArray = new int[split.length];
        for (int str = 0; str < split.length; str++) {
            try {
                intArray[str] = Integer.parseInt(split[str]);
            }
            catch (NumberFormatException e) {
                return new int[] {0,1,2,3};
            }
        }
        ArrayList<Integer> found = new ArrayList<Integer>();
        for (int order : intArray) {
            if (found.contains(order) || order < 0 || order > 3)
                return new int[] {0,1,2,3};
            else
                found.add(order);
        }
        return intArray;
    }

    private static void reload() {
        load();
        // Update index if the number of hotbars changes
        if (HotbarLogic.hotbarIndex >= numberOfHotbars)
            HotbarLogic.moveSelectionToHotbar(numberOfHotbars - 1);
    }

    public static void setConfigFile(File configFile) {
        config = new Configuration(configFile);
    }

    public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MultiHotbar.MODID)) {
            Config.config.save();
            Config.reload();
        }
    }
}
