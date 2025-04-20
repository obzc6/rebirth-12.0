/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

public class PacketEat
extends Module {
    public static PacketEat INSTANCE;

    public PacketEat() {
        super("PacketEat", Module.Category.Player);
        INSTANCE = this;
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        PlayerActionC2SPacket packet;
        Object t = event.getPacket();
        if (t instanceof PlayerActionC2SPacket && (packet = (PlayerActionC2SPacket)t).getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM && PacketEat.mc.player.getActiveItem().getItem().isFood()) {
            event.cancel();
        }
    }
}

