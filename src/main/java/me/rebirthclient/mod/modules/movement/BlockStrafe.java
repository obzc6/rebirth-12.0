/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.MoveEvent;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoAnchor;
import me.rebirthclient.mod.modules.combat.AutoPush;
import me.rebirthclient.mod.modules.combat.BedAura;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.entity.player.PlayerEntity;

public class BlockStrafe
extends Module {
    public static BlockStrafe INSTANCE;
    private final SliderSetting speed = this.add(new SliderSetting("Speed", 10.0, 0.0, 20.0, 1.0));
    private final SliderSetting aSpeed = this.add(new SliderSetting("AnchorSpeed", 3.0, 0.0, 20.0, 1.0));
    private final SliderSetting aForward = this.add(new SliderSetting("AnchorForward", 1.0, 0.0, 20.0, 0.25));
    private final SliderSetting bSpeed = this.add(new SliderSetting("BedSpeed", 3.0, 0.0, 20.0, 1.0));

    public BlockStrafe() {
        super("BlockStrafe", Module.Category.Movement);
        INSTANCE = this;
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        if (!EntityUtil.isInsideBlock()) {
            return;
        }
        if (AutoPush.isInWeb((PlayerEntity)BlockStrafe.mc.player)) {
            return;
        }
        double speed = AutoAnchor.INSTANCE.currentPos == null ? (BedAura.placePos == null ? this.speed.getValue() : this.bSpeed.getValue()) : this.aSpeed.getValue();
        double moveSpeed = 0.002873 * speed;
        double n = BlockStrafe.mc.player.input.movementForward;
        double n2 = BlockStrafe.mc.player.input.movementSideways;
        double n3 = BlockStrafe.mc.player.getYaw();
        if (n == 0.0 && n2 == 0.0) {
            if (AutoAnchor.INSTANCE.currentPos == null) {
                event.setX(0.0);
                event.setZ(0.0);
            } else {
                moveSpeed = 0.002873 * this.aForward.getValue();
                event.setX(1.0 * moveSpeed * -Math.sin(Math.toRadians(n3)));
                event.setZ(1.0 * moveSpeed * Math.cos(Math.toRadians(n3)));
            }
            return;
        }
        if (n != 0.0 && n2 != 0.0) {
            n *= Math.sin(0.7853981633974483);
            n2 *= Math.cos(0.7853981633974483);
        }
        event.setX(n * moveSpeed * -Math.sin(Math.toRadians(n3)) + n2 * moveSpeed * Math.cos(Math.toRadians(n3)));
        event.setZ(n * moveSpeed * Math.cos(Math.toRadians(n3)) - n2 * moveSpeed * -Math.sin(Math.toRadians(n3)));
    }
}

