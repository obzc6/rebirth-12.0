/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.OtherClientPlayerEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 */
package me.rebirthclient.mod.modules.player;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.UUID;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Blink
extends Module {
    public static Blink INSTANCE = new Blink();
    private final ArrayList<Packet> packetsList = new ArrayList();
    public static OtherClientPlayerEntity fakePlayer;

    public Blink() {
        super("Blink", "Fake lag", Module.Category.Player);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.packetsList.clear();
        if (Blink.nullCheck()) {
            this.disable();
            return;
        }
        fakePlayer = new OtherClientPlayerEntity(Blink.mc.world, new GameProfile(UUID.fromString("11451466-6666-6666-6666-666666666601"), Blink.mc.player.getName().getString()));
        fakePlayer.copyPositionAndRotation((Entity)Blink.mc.player);
        fakePlayer.getInventory().clone(Blink.mc.player.getInventory());
        Blink.mc.world.addPlayer(-5, (AbstractClientPlayerEntity)fakePlayer);
    }

    @Override
    public void onUpdate() {
        if (Blink.mc.player.isDead()) {
            this.packetsList.clear();
            this.disable();
        }
    }

    @Override
    public void onLogin() {
        if (this.isOn()) {
            this.packetsList.clear();
            this.disable();
        }
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        Object t = event.getPacket();
        if (t instanceof PlayerMoveC2SPacket) {
            this.packetsList.add((Packet)event.getPacket());
            event.cancel();
        }
    }

    @Override
    public void onDisable() {
        if (Blink.nullCheck()) {
            this.packetsList.clear();
            this.disable();
            return;
        }
        if (fakePlayer != null) {
            fakePlayer.kill();
            fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
            fakePlayer.onRemoved();
            fakePlayer = null;
        }
        for (Packet packet : this.packetsList) {
            Blink.mc.player.networkHandler.sendPacket(packet);
        }
    }

    @Override
    public String getInfo() {
        return String.valueOf(this.packetsList.size());
    }
}

