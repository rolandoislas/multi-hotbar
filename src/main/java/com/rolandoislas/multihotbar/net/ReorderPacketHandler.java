package com.rolandoislas.multihotbar.net;

import com.rolandoislas.multihotbar.HotbarLogic;
import com.rolandoislas.multihotbar.MultiHotbar;
import com.rolandoislas.multihotbar.util.InventoryHelperClient;
import com.rolandoislas.multihotbar.util.InventoryHelperServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReorderPacketHandler implements IMessageHandler<ReorderPacket, IMessage> {
    @Override
    public IMessage onMessage(ReorderPacket message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            InventoryHelperServer.reorderInventory(message.getHotbarOrder(), message.getOrder(), player);
            Container inventory = player.inventoryContainer;
            player.sendAllContents(inventory, inventory.getInventory());
            MultiHotbar.networkChannel.sendTo(message, ctx.getServerHandler().player);
        }
        else {
            HotbarLogic.hotbarOrder = message.getHotbarOrder();
            InventoryHelperClient.setSavedIndex(message.getHotbarIndex());
        }
        return null;
    }
}
