/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.util.math.BlockPos;

public class AutoCenter
extends Module {
    public static AutoCenter INSTANCE;

    public AutoCenter() {
        super("AutoCenter", "move center", Module.Category.Movement);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (AutoCenter.nullCheck()) {
            this.disable();
            return;
        }
        this.doCenter();
    }

    @Override
    public void onEnable() {
        if (AutoCenter.nullCheck()) {
            this.disable();
            return;
        }
        this.doCenter();
    }

    private void doCenter() {
        if (EntityUtil.isElytraFlying()) {
            this.disable();
            return;
        }
        BlockPos blockPos = EntityUtil.getPlayerPos();
        if (AutoCenter.mc.player.getX() - (double)blockPos.getX() - 0.5 <= 0.2 && AutoCenter.mc.player.getX() - (double)blockPos.getX() - 0.5 >= -0.2 && AutoCenter.mc.player.getZ() - (double)blockPos.getZ() - 0.5 <= 0.2 && AutoCenter.mc.player.getZ() - 0.5 - (double)blockPos.getZ() >= -0.2) {
            this.disable();
        } else {
            MovementUtil.setMotionX(((double)blockPos.getX() + 0.5 - AutoCenter.mc.player.getX()) / 2.0);
            MovementUtil.setMotionZ(((double)blockPos.getZ() + 0.5 - AutoCenter.mc.player.getZ()) / 2.0);
        }
    }

    @Override
    public void onDisable() {
        MovementUtil.setMotionZ(0.0);
        MovementUtil.setMotionX(0.0);
    }
}

