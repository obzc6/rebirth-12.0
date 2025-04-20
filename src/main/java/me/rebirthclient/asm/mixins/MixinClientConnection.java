/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.packet.Packet
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.impl.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientConnection.class})
public class MixinClientConnection {
    @Inject(at={@At(value="INVOKE", target="Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", ordinal=0)}, method={"channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V"}, cancellable=true)
    protected void onChannelRead(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        Rebirth.EVENT_BUS.post(event);
        if (event.isCancel()) {
            ci.cancel();
        }
    }

    @Inject(method={"send(Lnet/minecraft/network/packet/Packet;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        Rebirth.EVENT_BUS.post(event);
        if (event.isCancel()) {
            info.cancel();
        }
    }
}

