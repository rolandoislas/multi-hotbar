package com.rolandoislas.multihotbar.data;

import com.rolandoislas.multihotbar.HotbarLogic;
import net.minecraft.client.Minecraft;
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
    private static final String BASE_LANG = Constants.MODID + ".config.";
    public static Configuration config;
    public static int numberOfHotbars = 2;
    public static boolean relativeHotbarKeys = false;
    public static int[] hotbarOrder = new int[]{0, 1, 2, 3};
    public static boolean stackedHotbars = false;
    public static boolean shiftChat = false;
    public static boolean singleHotbarMode = false;
    public static boolean singleHotbarModeShowOnModiferKey = false;

    /**
     * Loads the config from a file and stores the values in memory. It also re-saves the config, stripping out any
     * values that are not defined in the code.
     */
    public static void load() {
        // Check if server and set hotbars to max
        boolean server = FMLCommonHandler.instance().getSide() == Side.SERVER;
        if (server) {
            numberOfHotbars = MAX_HOTBARS;
            return;
        }
        // Handle client config
        config.load();
        populateConfig();
        // Delete the old config
        config.getConfigFile().delete();
        config = new Configuration(config.getConfigFile());
        populateConfig();
        // Save the new one
        config.save();
    }

    private static void populateConfig() {
        config.setCategoryLanguageKey(Configuration.CATEGORY_GENERAL, BASE_LANG + "general");
        numberOfHotbars = config.getInt(
                "number_of_hotbars",
                Configuration.CATEGORY_GENERAL,
                numberOfHotbars, 2, MAX_HOTBARS,
                "",
                BASE_LANG + "general.numberofhotbars");
        relativeHotbarKeys = config.getBoolean(
                "relative_hotbar_keys",
                Configuration.CATEGORY_GENERAL,
                relativeHotbarKeys,
                "",
                BASE_LANG + "general.relativehotbarkeys");
        try {
            hotbarOrder = commaIntStringToArray(config.getString(
                    "hotbar_order",
                    Configuration.CATEGORY_GENERAL,
                    intArrayToString(hotbarOrder),
                    "",
                    BASE_LANG + "general.inventoryorder"));
        }
        catch (IllegalArgumentException e) {
            hotbarOrder = new int[]{0, 1, 2, 3};
        }
        stackedHotbars = config.getBoolean(
                "stacked_hotbars",
                Configuration.CATEGORY_GENERAL,
                stackedHotbars,
                "",
                BASE_LANG + "general.stackedhotbars");
        shiftChat = config.getBoolean(
                "shift_chat",
                Configuration.CATEGORY_GENERAL,
                shiftChat,
                "",
                BASE_LANG + "general.shiftchat");
        singleHotbarMode = config.getBoolean(
                "single_hotbar_mode",
                Configuration.CATEGORY_GENERAL,
                singleHotbarMode,
                "",
                BASE_LANG + "general.singleHotbarMode");
        singleHotbarModeShowOnModiferKey = config.getBoolean(
                "single_hotbar_preview",
                Configuration.CATEGORY_GENERAL,
                singleHotbarModeShowOnModiferKey,
                "",
                BASE_LANG + "general.singleHotbarModeShowOnModiferKey");
    }

    /**
     * Converts an int array to a comma separated string
     * @param array int array
     * @return string of array or empty string
     */
    private static String intArrayToString(int[] array) {
        if (array == null)
            return "";
        StringBuilder arrayString = new StringBuilder();
        for (int i : array)
            arrayString.append(i).append(",");
        return arrayString.toString().endsWith(",") ? arrayString.deleteCharAt(arrayString.length() - 1).toString() :
                arrayString.toString();
    }

    /**
     * Converts a string og comma separated ints to an int array
     * @param string comma separated ints
     * @return int array
     * @throws IllegalArgumentException the string was invalid
     */
    private static int[] commaIntStringToArray(String string) throws IllegalArgumentException {
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

    /**
     * Sets the config file path
     * @param configFile path
     */
    public static void setConfigFile(File configFile) {
        config = new Configuration(configFile);
    }

    /**
     * Called when the config changes
     * @param event change event
     */
    public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Constants.MODID)) {
            // Save to file
            Config.config.save();
            // Reload into memory
            load();
            // Update index if the number of hotbars changes
            if (Minecraft.getMinecraft().player.inventory.currentItem > HotbarLogic.getHotbarSize() - 1)
                HotbarLogic.moveSelectionToHotbar(numberOfHotbars - 1);
        }
    }
}
