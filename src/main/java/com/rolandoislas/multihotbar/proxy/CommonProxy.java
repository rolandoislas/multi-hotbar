package com.rolandoislas.multihotbar.proxy;

import com.rolandoislas.multihotbar.data.Config;
import com.rolandoislas.multihotbar.event.EventHandlerCommon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Rolando on 6/6/2016.
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // Load config
        Config.setConfigFile(event.getSuggestedConfigurationFile());
        Config.load();
        // Register event handler
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {}
}
