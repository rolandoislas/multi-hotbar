package com.rolandoislas.multihotbar.proxy;

import com.rolandoislas.multihotbar.EventHandlerClient;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Rolando on 6/6/2016.
 */
@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Register overlay event handler
        EventHandlerClient eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(eventHandlerClient);
        FMLCommonHandler.instance().bus().register(eventHandlerClient);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }
}
