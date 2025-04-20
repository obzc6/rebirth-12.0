/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket
 *  net.minecraft.network.packet.s2c.play.PlayPingS2CPacket
 */
package me.rebirthclient.api.managers;

import java.util.ArrayList;
import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.player.PingSpoof;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayPingS2CPacket;

public class PingSpoofManager
implements Wrapper {
    private final List<DelayedPacket> delayed = new ArrayList<DelayedPacket>();
    private DelayedPacket delayed1 = null;
    private DelayedPacket delayed2 = null;

    public PingSpoofManager() {
        Rebirth.EVENT_BUS.subscribe(this);
    }

    public void run() {
        if (Module.nullCheck()) {
            return;
        }
        ArrayList<DelayedPacket> toSend = new ArrayList<DelayedPacket>();
        if (this.delayed1 != null) {
            this.delayed.add(this.delayed1);
            this.delayed1 = null;
        }
        if (this.delayed2 != null) {
            this.delayed.add(this.delayed2);
            this.delayed2 = null;
        }
        for (DelayedPacket d2 : this.delayed) {
            if (System.currentTimeMillis() <= d2.time) continue;
            toSend.add(d2);
        }
        toSend.forEach(d -> {
            mc.getNetworkHandler().sendPacket(d.packet);
            this.delayed.remove(d);
        });
        toSend.clear();
    }

    public void addKeepAlive(long id) {
        this.delayed1 = new DelayedPacket((Packet<?>)new KeepAliveC2SPacket(id), System.currentTimeMillis() + (long)PingSpoof.INSTANCE.ping.getValueInt());
    }

    public void addPong(int id) {
        this.delayed2 = new DelayedPacket((Packet<?>)new PlayPingS2CPacket(id), System.currentTimeMillis() + (long)PingSpoof.INSTANCE.ping.getValueInt());
    }

    private record DelayedPacket(Packet<?> packet, long time) {
    }
}

