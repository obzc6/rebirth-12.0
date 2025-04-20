/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;

public class Packet
extends Module {
    public Packet() {
        super("Packet", Module.Category.Combat);
    }

    @EventHandler
    public void OnPacket(PacketEvent.Send event) {
        if (Packet.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof ClickSlotC2SPacket) {
            CommandManager.sendChatMessage(event.getPacket().toString() + "Send");
        }
    }

    @EventHandler
    public void OnPacket(PacketEvent.Receive event) {
        if (Packet.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof ClickSlotC2SPacket) {
            CommandManager.sendChatMessage(event.getPacket().toString() + "Receive");
        }
    }
}

