/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.world.World
 */
package me.rebirthclient.api.managers;

import java.util.ArrayList;
import java.util.HashMap;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.DeathEvent;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.TotemEvent;
import me.rebirthclient.api.events.impl.UpdateWalkingEvent;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.world.World;

public class PopManager
implements Wrapper {
    public final HashMap<String, Integer> popContainer = new HashMap();
    public final ArrayList<PlayerEntity> deadPlayer = new ArrayList();

    public PopManager() {
        Rebirth.EVENT_BUS.subscribe(this);
    }

    public Integer getPop(String s) {
        return this.popContainer.getOrDefault(s, 0);
    }

    public void update() {
        if (Module.nullCheck()) {
            return;
        }
        for (PlayerEntity player : PopManager.mc.world.getPlayers()) {
            if (player == null || player.isAlive()) {
                this.deadPlayer.remove((Object)player);
                continue;
            }
            if (this.deadPlayer.contains((Object)player)) continue;
            Rebirth.EVENT_BUS.post(new DeathEvent(player));
            this.onDeath(player);
            this.deadPlayer.add(player);
        }
    }

    @EventHandler
    public void updateWalking(UpdateWalkingEvent event) {
        this.update();
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        Entity entity;
        EntityStatusS2CPacket packet;
        if (Module.nullCheck()) {
            return;
        }
        Object t = event.getPacket();
        if (t instanceof EntityStatusS2CPacket && (packet = (EntityStatusS2CPacket)t).getStatus() == 35 && (entity = packet.getEntity((World)PopManager.mc.world)) instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            this.onTotemPop(player);
        }
    }

    public void onDeath(PlayerEntity player) {
        this.popContainer.remove(player.getName().getString());
    }

    public void onTotemPop(PlayerEntity player) {
        int l_Count = 1;
        if (this.popContainer.containsKey(player.getName().getString())) {
            l_Count = this.popContainer.get(player.getName().getString());
            this.popContainer.put(player.getName().getString(), ++l_Count);
        } else {
            this.popContainer.put(player.getName().getString(), l_Count);
        }
        Rebirth.EVENT_BUS.post(new TotemEvent(player));
    }
}

