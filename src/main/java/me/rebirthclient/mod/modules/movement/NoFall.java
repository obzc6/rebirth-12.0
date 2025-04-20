/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.asm.accessors.IPlayerMoveC2SPacket;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall
extends Module {
    private final SliderSetting getDistance = new SliderSetting("Distance", 3.0, 0.0, 8.0, 0.1);

    public NoFall() {
        super("NoFall", Module.Category.Movement);
        this.setDescription("Prevents fall damage.");
        this.add(this.getDistance);
    }

    @Override
    public void onUpdate() {
        for (ItemStack is : NoFall.mc.player.getArmorItems()) {
            if (is.getItem() != Items.ELYTRA) continue;
            return;
        }
        if ((double)NoFall.mc.player.fallDistance >= this.getDistance.getValue() - 0.1) {
            // empty if block
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (NoFall.nullCheck()) {
            return;
        }
        for (ItemStack is : NoFall.mc.player.getArmorItems()) {
            if (is.getItem() != Items.ELYTRA) continue;
            return;
        }
        Object t = event.getPacket();
        if (t instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)t;
            if (NoFall.mc.player.fallDistance >= (float)this.getDistance.getValue()) {
                ((IPlayerMoveC2SPacket)packet).setOnGround(true);
            }
        }
    }
}

