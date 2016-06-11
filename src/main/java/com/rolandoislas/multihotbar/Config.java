package com.rolandoislas.multihotbar;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

/**
 * Created by Rolando on 6/6/2016.
 */
public class Config {
    public static final int MAX_HOTBARS = 4;
    public static int numberOfHotbars;
    public static Configuration config;
    public static boolean relativeHotbarKeys;

    public static void load() {
        // Check if server and set hotbars to max
        boolean server = FMLCommonHandler.instance().getSide() == Side.SERVER;
        if (server) {
            numberOfHotbars = MAX_HOTBARS;
            return;
        }
        // Handle client config
        config.load();
        numberOfHotbars = config.getInt("Number of Hotbars", Configuration.CATEGORY_GENERAL, 2, 1, MAX_HOTBARS,
                "Defines the amount of hotbars that should be displayed");
        relativeHotbarKeys = config.getBoolean("Relative Hotbar Keys", Configuration.CATEGORY_GENERAL, false,
                "If set to true, pressing the hotbar keys (e.g. 1-9) will move to the slot on the currently " +
                        "selected hotbar instead of the first");
        config.save();
    }

    public static void reload() {
        load();
    }

    public static void setConfigFile(File configFile) {
        config = new Configuration(configFile);
    }
}
