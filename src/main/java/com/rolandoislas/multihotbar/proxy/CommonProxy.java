package com.rolandoislas.multihotbar.proxy;

import com.rolandoislas.multihotbar.MultiHotbar;
import com.rolandoislas.multihotbar.data.Config;
import com.rolandoislas.multihotbar.event.EventHandlerCommon;
import com.rolandoislas.multihotbar.net.ReorderPacket;
import com.rolandoislas.multihotbar.net.ReorderPacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

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
        // Reorder packet
        MultiHotbar.networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel(MultiHotbar.MODID + ".channel");
        MultiHotbar.networkChannel.registerMessage(ReorderPacketHandler.class, ReorderPacket.class,0, Side.SERVER);
        MultiHotbar.networkChannel.registerMessage(ReorderPacketHandler.class, ReorderPacket.class,1, Side.CLIENT);
    }

    public void postInit(FMLPostInitializationEvent event) {}
}
