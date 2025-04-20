/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoPush;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.entity.player.PlayerEntity;

public class Step
extends Module {
    private final SliderSetting stepHeight = this.add(new SliderSetting("Height", 1.0, 0.0, 5.0, 0.5));

    public Step() {
        super("Step", "Steps up blocks.", Module.Category.Movement);
    }

    @Override
    public void onDisable() {
        if (Step.nullCheck()) {
            return;
        }
        Step.mc.player.setStepHeight(0.5f);
    }

    @Override
    public void onUpdate() {
        if (Step.nullCheck()) {
            return;
        }
        if (Step.mc.player.isSneaking() || !Step.mc.player.horizontalCollision || Step.mc.player.isInLava() || Step.mc.player.isTouchingWater() || !Step.mc.player.isOnGround() || !EntityUtil.isInsideBlock() && AutoPush.isInWeb((PlayerEntity)Step.mc.player)) {
            Step.mc.player.setStepHeight(0.5f);
            return;
        }
        Step.mc.player.setStepHeight(this.stepHeight.getValueFloat());
    }
}

