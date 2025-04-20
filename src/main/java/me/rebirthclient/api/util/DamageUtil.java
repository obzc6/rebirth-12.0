/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.DamageUtil
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.world.World
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.explosion.Explosion$DestructionType
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package me.rebirthclient.api.util;

import java.util.Objects;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.asm.accessors.IExplosion;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageUtil
implements Wrapper {
    public static Explosion explosion = new Explosion((World)DamageUtil.mc.world, null, 0.0, 0.0, 0.0, 6.0f, false, Explosion.DestructionType.DESTROY);

    public static float anchorDamage(BlockPos pos, PlayerEntity target, PlayerEntity predict) {
        if (BlockUtil.getBlock(pos) == Blocks.RESPAWN_ANCHOR) {
            BlockState oldState = BlockUtil.getState(pos);
            DamageUtil.mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            float damage = DamageUtil.calculateDamage(pos.toCenterPos(), target, predict, 5.0f);
            DamageUtil.mc.world.setBlockState(pos, oldState);
            return damage;
        }
        return DamageUtil.calculateDamage(pos.toCenterPos(), target, predict, 5.0f);
    }

    public static float calculateDamage(BlockPos pos, PlayerEntity target, PlayerEntity predict, float power) {
        return DamageUtil.calculateDamage(pos.toCenterPos().add(0.0, -0.5, 0.0), target, predict, power);
    }

    public static float calculateDamage(Vec3d explosionPos, PlayerEntity target, PlayerEntity predict, float power) {
        double zDiff;
        double yDiff;
        double xDiff;
        double diff;
        double distExposure;
        if (DamageUtil.mc.world.getDifficulty() == Difficulty.PEACEFUL) {
            return 0.0f;
        }
        ((IExplosion)explosion).setWorld((World)DamageUtil.mc.world);
        ((IExplosion)explosion).setX(explosionPos.x);
        ((IExplosion)explosion).setY(explosionPos.y);
        ((IExplosion)explosion).setZ(explosionPos.z);
        ((IExplosion)explosion).setPower(power);
        if (!new Box((double)MathHelper.floor((double)(explosionPos.x - 11.0)), (double)MathHelper.floor((double)(explosionPos.y - 11.0)), (double)MathHelper.floor((double)(explosionPos.z - 11.0)), (double)MathHelper.floor((double)(explosionPos.x + 13.0)), (double)MathHelper.floor((double)(explosionPos.y + 13.0)), (double)MathHelper.floor((double)(explosionPos.z + 13.0))).intersects(predict.getBoundingBox())) {
            return 0.0f;
        }
        if (!target.isImmuneToExplosion() && !target.isInvulnerable() && (distExposure = (double)MathHelper.sqrt((float)((float)predict.squaredDistanceTo(explosionPos))) / 12.0) <= 1.0 && (diff = (double)MathHelper.sqrt((float)((float)((xDiff = predict.getX() - explosionPos.x) * xDiff + (yDiff = predict.getY() - explosionPos.y) * yDiff + (zDiff = predict.getX() - explosionPos.z) * zDiff)))) != 0.0) {
            double exposure = Explosion.getExposure((Vec3d)explosionPos, (Entity)predict);
            double finalExposure = (1.0 - distExposure) * exposure;
            float toDamage = (float)Math.floor((finalExposure * finalExposure + finalExposure) / 2.0 * 7.0 * 12.0 + 1.0);
            if (DamageUtil.mc.world.getDifficulty() == Difficulty.EASY) {
                toDamage = Math.min(toDamage / 2.0f + 1.0f, toDamage);
            } else if (DamageUtil.mc.world.getDifficulty() == Difficulty.HARD) {
                toDamage = toDamage * 3.0f / 2.0f;
            }
            toDamage = net.minecraft.entity.DamageUtil.getDamageLeft((float)toDamage, (float)target.getArmor(), (float)((float)Objects.requireNonNull(target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).getValue()));
            if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                int resistance = 25 - (Objects.requireNonNull(target.getStatusEffect(StatusEffects.RESISTANCE)).getAmplifier() + 1) * 5;
                float resistance_1 = toDamage * (float)resistance;
                toDamage = Math.max(resistance_1 / 25.0f, 0.0f);
            }
            if (toDamage <= 0.0f) {
                toDamage = 0.0f;
            } else {
                int protAmount = EnchantmentHelper.getProtectionAmount((Iterable)target.getArmorItems(), (DamageSource)explosion.getDamageSource());
                if (protAmount > 0) {
                    toDamage = net.minecraft.entity.DamageUtil.getInflictedDamage((float)toDamage, (float)protAmount);
                }
            }
            return toDamage;
        }
        return 0.0f;
    }

    public static float calculateDamage(double posX, double posY, double posZ, @NotNull Entity entity, @Nullable Entity predictEntity, float power) {
        if (predictEntity == null) {
            predictEntity = entity;
        }
        float doubleExplosionSize = 12.0f;
        double v = (1.0 - (double)MathHelper.sqrt((float)((float)predictEntity.squaredDistanceTo(posX, posY, posZ))) / (double)doubleExplosionSize) * (double)DamageUtil.getBlockDensity(new Vec3d(posX, posY, posZ), predictEntity.getBoundingBox());
        float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof LivingEntity) {
            finald = DamageUtil.getBlastReduction((LivingEntity)entity, DamageUtil.getDamageMultiplied(damage), new Explosion((World)DamageUtil.mc.world, null, posX, posY, posZ, power, false, Explosion.DestructionType.DESTROY));
        }
        return (float)finald;
    }

    public static float getBlastReduction(LivingEntity entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof PlayerEntity) {
            PlayerEntity ep = (PlayerEntity)entity;
            damage = net.minecraft.entity.DamageUtil.getDamageLeft((float)damage, (float)ep.getArmor(), (float)((float)ep.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue()));
            int k = EnchantmentHelper.getProtectionAmount((Iterable)ep.getArmorItems(), (DamageSource)explosion.getDamageSource());
            float f = MathHelper.clamp((float)k, (float)0.0f, (float)20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
                int resistance = 25 - (Objects.requireNonNull(entity.getStatusEffect(StatusEffects.RESISTANCE)).getAmplifier() + 1) * 5;
                float resistance_1 = damage * (float)resistance;
                damage = Math.max(resistance_1 / 25.0f, 0.0f);
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = net.minecraft.entity.DamageUtil.getDamageLeft((float)damage, (float)entity.getArmor(), (float)((float)entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue()));
        return damage;
    }

    public static float getBlockDensity(Vec3d vec, Box bb) {
        double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double d1 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double d2 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
        if (d0 >= 0.0 && d1 >= 0.0 && d2 >= 0.0) {
            int j2 = 0;
            int k2 = 0;
            float f = 0.0f;
            while (f <= 1.0f) {
                float f1 = 0.0f;
                while (f1 <= 1.0f) {
                    float f2 = 0.0f;
                    while (f2 <= 1.0f) {
                        double d5 = bb.minX + (bb.maxX - bb.minX) * (double)f;
                        double d6 = bb.minY + (bb.maxY - bb.minY) * (double)f1;
                        double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double)f2;
                        if (DamageUtil.mc.world.raycast(new RaycastContext(new Vec3d(d5 + d3, d6, d7 + d4), vec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)DamageUtil.mc.player)) == null) {
                            ++j2;
                        }
                        ++k2;
                        f2 = (float)((double)f2 + d2);
                    }
                    f1 = (float)((double)f1 + d1);
                }
                f = (float)((double)f + d0);
            }
            return (float)j2 / (float)k2;
        }
        return 0.0f;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = DamageUtil.mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }
}

