/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.asm.accessors.IUpdateSelectedSlotS2CPacket;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

public class NoSwap
extends Module {
    public static NoSwap INSTANCE;

    public NoSwap() {
        super("NoSwap", Module.Category.Player);
        INSTANCE = this;
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (NoSwap.nullCheck()) {
            return;
        }
        Object t = event.getPacket();
        if (t instanceof UpdateSelectedSlotS2CPacket) {
            UpdateSelectedSlotS2CPacket packet = (UpdateSelectedSlotS2CPacket)t;
            int slot = NoSwap.mc.player.getInventory().selectedSlot;
            ((IUpdateSelectedSlotS2CPacket)packet).setslot(slot);
        }
    }
}

