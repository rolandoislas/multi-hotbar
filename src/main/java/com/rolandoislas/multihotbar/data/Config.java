package com.rolandoislas.multihotbar.data;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.util.InventoryHelperCommon;
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
    public static int[] hotbarOrder;
    public static boolean stackedHotbars;
    public static boolean shiftChat;
    private static final String BASE_LANG = Constants.MODID + ".config.";
    public static boolean singleHotbarMode;
    public static boolean singleHotbarModeShowOnModiferKey;

    public static void load() {
        // Check if server and set hotbars to max
        boolean server = FMLCommonHandler.instance().getSide() == Side.SERVER;
        if (server) {
            numberOfHotbars = MAX_HOTBARS;
            return;
        }
        // Handle client config
        config.load();
        // TODO add lang entries
        config.setCategoryLanguageKey(Configuration.CATEGORY_GENERAL, BASE_LANG + "general");
        numberOfHotbars = config.getInt("Number of Hotbars", Configuration.CATEGORY_GENERAL, 2, 2, MAX_HOTBARS,
                "",
                BASE_LANG + "general.numberofhotbars");
        relativeHotbarKeys = config.getBoolean("Relative Hotbar Keys", Configuration.CATEGORY_GENERAL, false,
                "",
                BASE_LANG + "general.relativehotbarkeys");
        relativeHotbarPickups = config.getBoolean("Relative Hotbar Pickups", Configuration.CATEGORY_GENERAL, false,
                "",
                BASE_LANG + "general.relativehotbarpickups");
        try {
            hotbarOrder = commaIntStringToArray(config.getString("Hotbar Order", Configuration.CATEGORY_GENERAL,
                    "0,1,2,3", "",
                    BASE_LANG + "general.inventoryorder"));
        }
        catch (IllegalArgumentException e) {
            hotbarOrder = new int[]{0, 1, 2, 3};
        }
        stackedHotbars = config.getBoolean("Stacked Hotbars", Configuration.CATEGORY_GENERAL, false,
                "",
                BASE_LANG + "general.stackedhotbars");
        shiftChat = config.getBoolean("Shift Chat", Configuration.CATEGORY_GENERAL, false,
                "",
                BASE_LANG + "general.shiftchat");
        singleHotbarMode = config.getBoolean("Single Hotbar Mode", Configuration.CATEGORY_GENERAL,
                false,
                "",
                BASE_LANG + "general.singleHotbarMode");
        singleHotbarModeShowOnModiferKey = config.getBoolean("Single Hotbar Preview",
                Configuration.CATEGORY_GENERAL, false,
                "",
                BASE_LANG + "general.singleHotbarModeShowOnModiferKey");
        config.save();
    }

    public static int[] commaIntStringToArray(String string) throws IllegalArgumentException {
        String[] split = string.replace(" ", "").split(",");
        if (split.length != 4)
            throw new IllegalArgumentException();
        int[] intArray = new int[split.length];
        for (int str = 0; str < split.length; str++) {
            try {
                intArray[str] = Integer.parseInt(split[str]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        }
        ArrayList<Integer> found = new ArrayList<>();
        for (int order : intArray) {
            if (found.contains(order) || order < 0 || order > 3)
                throw new IllegalArgumentException();
            else
                found.add(order);
        }
        return intArray;
    }

    private static void reload() {
        load();
        // Update index if the number of hotbars changes
        if (InventoryHelperCommon.hotbarIndex >= numberOfHotbars)
            HotbarLogic.moveSelectionToHotbar(numberOfHotbars - 1);
    }

    public static void setConfigFile(File configFile) {
        config = new Configuration(configFile);
    }

    public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Constants.MODID)) {
            Config.config.save();
            Config.reload();
        }
    }
}
