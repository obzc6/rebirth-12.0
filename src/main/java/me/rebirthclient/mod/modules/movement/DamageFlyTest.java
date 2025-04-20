/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class DamageFlyTest
extends Module {
    public SliderSetting boostTicks = this.add(new SliderSetting("Ticks", 8, 0, 40));
    public SliderSetting hurtTime = this.add(new SliderSetting("HurtTime", 9, 1, 10));
    private boolean canBoost;
    private boolean damage;
    private boolean isVelocity;
    private int ticks;
    private double motionX;
    private double motionY;
    private double motionZ;

    public DamageFlyTest() {
        super("DamageFly", Module.Category.Movement);
    }

    @EventHandler(priority=100)
    public void onPacketReceive(PacketEvent.Receive e) {
        Object t = e.getPacket();
        if (t instanceof EntityVelocityUpdateS2CPacket) {
            EntityVelocityUpdateS2CPacket velPacket = (EntityVelocityUpdateS2CPacket)t;
            if (velPacket.getVelocityY() > 0) {
                this.isVelocity = true;
            }
            if ((double)velPacket.getVelocityY() / 8000.0 > 0.2) {
                this.motionX = (double)velPacket.getVelocityX() / 8000.0;
                this.motionY = (double)velPacket.getVelocityY() / 8000.0;
                this.motionZ = (double)velPacket.getVelocityZ() / 8000.0;
                this.canBoost = true;
            }
        }
    }

    @Override
    public void onUpdate() {
        if ((double)DamageFlyTest.mc.player.hurtTime == this.hurtTime.getValue()) {
            this.damage = true;
        }
        if (this.damage && this.isVelocity) {
            if (this.canBoost) {
                DamageFlyTest.mc.player.setVelocity(this.motionX, this.motionY, this.motionZ);
                ++this.ticks;
            }
            if ((double)this.ticks >= this.boostTicks.getValue()) {
                this.isVelocity = false;
                this.canBoost = false;
                this.damage = false;
                this.ticks = 0;
            }
        }
    }

    @Override
    public void onEnable() {
        this.damage = false;
        this.canBoost = false;
        this.ticks = 0;
    }
}

