package com.rolandoislas.multihotbar.net;

import com.rolandoislas.multihotbar.MultiHotbar;
import com.rolandoislas.multihotbar.util.InventoryHelperClient;
import com.rolandoislas.multihotbar.util.InventoryHelperCommon;
import com.rolandoislas.multihotbar.util.InventoryHelperServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Arrays;

public class ReorderPacketHandler implements IMessageHandler<ReorderPacket, IMessage> {

    @Override
    public IMessage onMessage(ReorderPacket message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            InventoryHelperServer.PLAYER_MUTEX.acquireUninterruptibly();
            boolean forceUpdate = Arrays.equals(message.getHotbarOrder(), message.getOrder());
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (!forceUpdate)
                InventoryHelperServer.reorderInventory(message.getHotbarOrder(), message.getOrder(), player);
            InventoryHelperServer.updatePlayer(player, message.getOrder(), message.getHotbarIndex());
            if (!forceUpdate) {
                Container inventory = player.inventoryContainer;
                player.sendAllContents(inventory, inventory.getInventory());
                MultiHotbar.networkChannel.sendTo(message, ctx.getServerHandler().player);
            }
            InventoryHelperServer.PLAYER_MUTEX.release();
        }
        else {
            InventoryHelperCommon.hotbarOrder = message.getHotbarOrder();
            InventoryHelperClient.setSavedIndex(message.getHotbarIndex());
        }
        return null;
    }
}
