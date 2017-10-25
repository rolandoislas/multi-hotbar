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
    public static int numberOfHotbars;
    public static boolean relativeHotbarKeys;
    public static Integer[] hotbarOrder;
    public static boolean stackedHotbars;
    public static boolean shiftChat;
    public static boolean singleHotbarMode;
    public static boolean singleHotbarModeShowOnModiferKey;
    public static boolean inverseScrollDirection;
    public static boolean doubleTapMovesToNextHotbar;

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
        // Initial load
        config.load();
        populateConfig(true);
        // Delete the old config and save a new one with the loaded values. This ensures that only used keys are stored.
        config.getConfigFile().delete();
        config = new Configuration(config.getConfigFile());
        populateConfig(false);
        config.save();
        // Load again with defaults. This makes sure the GuiConfig has the correct defaults.
        config.load();
        populateConfig(true);
    }

    private static void populateConfig(boolean defaults) {
        config.setCategoryLanguageKey(Configuration.CATEGORY_GENERAL, BASE_LANG + "general");
        numberOfHotbars = config.getInt(
                "number_of_hotbars",
                Configuration.CATEGORY_GENERAL,
                defaults ? 2 : numberOfHotbars, 2, MAX_HOTBARS,
                "",
                BASE_LANG + "general.numberofhotbars");
        relativeHotbarKeys = config.getBoolean(
                "relative_hotbar_keys",
                Configuration.CATEGORY_GENERAL,
                !defaults && relativeHotbarKeys,
                "",
                BASE_LANG + "general.relativehotbarkeys");
        try {
            hotbarOrder = commaIntStringToArray(config.getString(
                    "hotbar_order",
                    Configuration.CATEGORY_GENERAL,
                    defaults ? "0,1,2,3" : intArrayToString(hotbarOrder),
                    "",
                    BASE_LANG + "general.inventoryorder"));
        }
        catch (IllegalArgumentException e) {
            hotbarOrder = new Integer[]{0, 1, 2, 3};
        }
        stackedHotbars = config.getBoolean(
                "stacked_hotbars",
                Configuration.CATEGORY_GENERAL,
                !defaults && stackedHotbars,
                "",
                BASE_LANG + "general.stackedhotbars");
        shiftChat = config.getBoolean(
                "shift_chat",
                Configuration.CATEGORY_GENERAL,
                !defaults && shiftChat,
                "",
                BASE_LANG + "general.shiftchat");
        singleHotbarMode = config.getBoolean(
                "single_hotbar_mode",
                Configuration.CATEGORY_GENERAL,
                !defaults && singleHotbarMode,
                "",
                BASE_LANG + "general.singleHotbarMode");
        singleHotbarModeShowOnModiferKey = config.getBoolean(
                "single_hotbar_preview",
                Configuration.CATEGORY_GENERAL,
                !defaults && singleHotbarModeShowOnModiferKey,
                "",
                BASE_LANG + "general.singleHotbarModeShowOnModiferKey");
        inverseScrollDirection = config.getBoolean(
                "inverse_scroll_direction",
                Configuration.CATEGORY_GENERAL,
                !defaults && inverseScrollDirection,
                "",
                BASE_LANG + "general.inverseScrollDirection"
        );
        doubleTapMovesToNextHotbar = config.getBoolean(
                "double_tap_moves_to_next_hotbar",
                Configuration.CATEGORY_GENERAL,
                defaults || doubleTapMovesToNextHotbar,
                "",
                BASE_LANG + "general.doubleTapMovesToNextHotbar"
        );
    }

    /**
     * Converts an int array to a comma separated string
     * @param array int array
     * @return string of array or empty string
     */
    private static String intArrayToString(Integer[] array) {
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
    private static Integer[] commaIntStringToArray(String string) throws IllegalArgumentException {
        String[] split = string.replace(" ", "").split(",");
        if (split.length != 4)
            throw new IllegalArgumentException();
        Integer[] intArray = new Integer[split.length];
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
                Minecraft.getMinecraft().player.inventory.currentItem = 0;
        }
    }
}
