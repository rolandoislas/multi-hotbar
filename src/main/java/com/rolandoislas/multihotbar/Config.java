package com.rolandoislas.multihotbar;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Rolando on 6/6/2016.
 */
public class Config {
    public static int numberOfHotbars;
    private static Configuration config;

    public static void load(File suggestedConfigurationFile) {
        // Check if server and set hotbars to max
        boolean server = FMLCommonHandler.instance().getSide() == Side.SERVER;
        if (server) {
            numberOfHotbars = 4;
            return;
        }
        // Handle client config
        config = new Configuration(suggestedConfigurationFile);
        config.load();
        numberOfHotbars = config.getInt("Number of Hotbars", Configuration.CATEGORY_GENERAL, 2, 1, 4,
                "Defines the amount of hotbars that should be displayed");
        config.save();
    }

    public static void reload() {
        if (config != null)
            load(config.getConfigFile());
        else
            load(null);
    }
}
