package com.rolandoislas.multihotbar.proxy;

import com.rolandoislas.multihotbar.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Rolando on 6/6/2016.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Register overlay event handler
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }
}
