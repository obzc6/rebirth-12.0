/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.asm.accessors.IPlayerPositionLookS2CPacket;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class NoRotate
extends Module {
    public static NoRotate INSTANCE;

    public NoRotate() {
        super("NoRotate", Module.Category.Player);
        INSTANCE = this;
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (NoRotate.nullCheck()) {
            return;
        }
        Object t = event.getPacket();
        if (t instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)t;
            float yaw = NoRotate.mc.player.getYaw();
            float pitch = NoRotate.mc.player.getPitch();
            if (Rebirth.RUN.rotating) {
                yaw = Rebirth.RUN.preYaw;
                pitch = Rebirth.RUN.prePitch;
            }
            ((IPlayerPositionLookS2CPacket)packet).setYaw(yaw);
            ((IPlayerPositionLookS2CPacket)packet).setPitch(pitch);
        }
    }
}

