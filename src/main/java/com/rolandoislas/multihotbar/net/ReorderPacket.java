package com.rolandoislas.multihotbar.net;

import com.rolandoislas.multihotbar.data.Config;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ReorderPacket implements IMessage {
    private int[] hotbarOrder;
    private int[] order;
    private int hotbarIndex;

    public ReorderPacket(int[] hotbarOrder, int[] order, int hotbarIndex) {
        this.hotbarOrder = hotbarOrder;
        this.order = order;
        this.hotbarIndex = hotbarIndex;
    }

    @SuppressWarnings("unused")
    public ReorderPacket() {
        hotbarOrder = new int[Config.MAX_HOTBARS];
        order = new int[Config.MAX_HOTBARS];
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        for (int order = 0; order < Config.MAX_HOTBARS; order++)
            hotbarOrder[order] = buf.readInt();
        for (int order = 0; order < Config.MAX_HOTBARS; order++)
            this.order[order] = buf.readInt();
        hotbarIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for (int order : hotbarOrder)
            buf.writeInt(order);
        for (int order : this.order)
            buf.writeInt(order);
        buf.writeInt(hotbarIndex);
    }

    int[] getHotbarOrder() {
        return hotbarOrder;
    }

    int[] getOrder() {
        return order;
    }

    int getHotbarIndex() {
        return hotbarIndex;
    }
}
