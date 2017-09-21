package com.rolandoislas.multihotbar.proxy;

import com.rolandoislas.multihotbar.command.CommandResetHotbar;
import com.rolandoislas.multihotbar.command.CommandSetHotbarIndex;
import com.rolandoislas.multihotbar.command.CommandSetHotbarOrder;
import com.rolandoislas.multihotbar.event.EventHandlerClient;
import com.rolandoislas.multihotbar.data.KeyBindings;
import net.minecraftforge.client.ClientCommandHandler;
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
        EventHandlerClient eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(eventHandlerClient);
        // Register KeyBindings
        KeyBindings.load();
        // Register client commands
        ClientCommandHandler.instance.registerCommand(new CommandResetHotbar());
        ClientCommandHandler.instance.registerCommand(new CommandSetHotbarIndex());
        ClientCommandHandler.instance.registerCommand(new CommandSetHotbarOrder());
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
