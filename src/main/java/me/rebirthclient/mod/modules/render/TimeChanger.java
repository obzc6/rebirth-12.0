/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
 */
package me.rebirthclient.mod.modules.render;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class TimeChanger
extends Module {
    long oldTime;
    public SliderSetting time = this.add(new SliderSetting("Time", 1.0, 20000.0, -20000.0, 1.0));

    public TimeChanger() {
        super("TimeChanger", Module.Category.Render);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            this.oldTime = ((WorldTimeUpdateS2CPacket)event.getPacket()).getTime();
            event.cancel();
        }
    }

    @Override
    public void onEnable() {
        if (TimeChanger.nullCheck()) {
            return;
        }
        this.oldTime = TimeChanger.mc.world.getTime();
    }

    @Override
    public void onDisable() {
        if (TimeChanger.nullCheck()) {
            return;
        }
        TimeChanger.mc.world.setTimeOfDay(this.oldTime);
    }

    @Override
    public void onUpdate() {
        if (TimeChanger.nullCheck()) {
            return;
        }
        TimeChanger.mc.world.setTimeOfDay((long)this.time.getValueInt());
    }
}

