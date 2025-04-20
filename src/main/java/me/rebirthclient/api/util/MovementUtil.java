/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.api.util;

import me.rebirthclient.api.util.Wrapper;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Vec3d;

public class MovementUtil
implements Wrapper {
    public static boolean isMoving() {
        return (double)MovementUtil.mc.player.input.movementForward != 0.0 || (double)MovementUtil.mc.player.input.movementSideways != 0.0;
    }

    public static double getDistance2D() {
        double xDist = MovementUtil.mc.player.getX() - MovementUtil.mc.player.prevX;
        double zDist = MovementUtil.mc.player.getZ() - MovementUtil.mc.player.prevZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static double getJumpSpeed() {
        double defaultSpeed = 0.0;
        if (MovementUtil.mc.player.hasStatusEffect(StatusEffect.byRawId((int)8))) {
            int amplifier = ((StatusEffectInstance)MovementUtil.mc.player.getActiveStatusEffects().get((Object)StatusEffect.byRawId((int)8))).getAmplifier();
            defaultSpeed += (double)(amplifier + 1) * 0.1;
        }
        return defaultSpeed;
    }

    public static double getMoveForward() {
        return MovementUtil.mc.player.input.movementForward;
    }

    public static double getMoveStrafe() {
        return MovementUtil.mc.player.input.movementSideways;
    }

    public static double[] directionSpeed(double speed) {
        float forward = MovementUtil.mc.player.input.movementForward;
        float side = MovementUtil.mc.player.input.movementSideways;
        float yaw = MovementUtil.mc.player.prevYaw + (MovementUtil.mc.player.getYaw() - MovementUtil.mc.player.prevYaw) * mc.getTickDelta();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static double getMotionX() {
        return MovementUtil.mc.player.getVelocity().x;
    }

    public static double getMotionY() {
        return MovementUtil.mc.player.getVelocity().y;
    }

    public static double getMotionZ() {
        return MovementUtil.mc.player.getVelocity().z;
    }

    public static void setMotionX(double x) {
        Vec3d velocity = new Vec3d(x, MovementUtil.mc.player.getVelocity().y, MovementUtil.mc.player.getVelocity().z);
        MovementUtil.mc.player.setVelocity(velocity);
    }

    public static void setMotionY(double y) {
        Vec3d velocity = new Vec3d(MovementUtil.mc.player.getVelocity().x, y, MovementUtil.mc.player.getVelocity().z);
        MovementUtil.mc.player.setVelocity(velocity);
    }

    public static void setMotionZ(double z) {
        Vec3d velocity = new Vec3d(MovementUtil.mc.player.getVelocity().x, MovementUtil.mc.player.getVelocity().y, z);
        MovementUtil.mc.player.setVelocity(velocity);
    }

    public static double getSpeed(boolean slowness) {
        double defaultSpeed = 0.2873;
        return MovementUtil.getSpeed(slowness, defaultSpeed);
    }

    public static double getSpeed(boolean slowness, double defaultSpeed) {
        int amplifier;
        if (MovementUtil.mc.player.hasStatusEffect(StatusEffect.byRawId((int)1))) {
            amplifier = ((StatusEffectInstance)MovementUtil.mc.player.getActiveStatusEffects().get((Object)StatusEffect.byRawId((int)1))).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (slowness && MovementUtil.mc.player.hasStatusEffect(StatusEffect.byRawId((int)2))) {
            amplifier = ((StatusEffectInstance)MovementUtil.mc.player.getActiveStatusEffects().get((Object)StatusEffect.byRawId((int)2))).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (MovementUtil.mc.player.isSneaking()) {
            defaultSpeed /= 5.0;
        }
        return defaultSpeed;
    }
}

