package com.rolandoislas.multihotbar.proxy;

import com.rolandoislas.multihotbar.data.KeyBindings;
import com.rolandoislas.multihotbar.event.EventHandlerClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Rolando on 6/6/2016.
 */
@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Register overlay event handler
        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
        // Register KeyBindings
        KeyBindings.load();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
