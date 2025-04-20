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
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.World
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.explosion.Explosion$DestructionType
 */
package me.rebirthclient.api.util;

import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.asm.accessors.IExplosion;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class MeteorDamageUtil
implements Wrapper {
    public static Explosion explosion = new Explosion((World)MeteorDamageUtil.mc.world, null, 0.0, 0.0, 0.0, 6.0f, false, Explosion.DestructionType.DESTROY);

    public static double crystalDamage(PlayerEntity player, BlockPos pos, PlayerEntity predict) {
        return MeteorDamageUtil.explosionDamage(player, pos.toCenterPos().add(0.0, -0.5, 0.0), predict, 6.0f);
    }

    public static double crystalDamage(PlayerEntity player, Vec3d pos, PlayerEntity predict) {
        return MeteorDamageUtil.explosionDamage(player, pos, predict, 6.0f);
    }

    public static double anchorDamage(PlayerEntity player, BlockPos pos, PlayerEntity predict) {
        if (BlockUtil.getBlock(pos) == Blocks.RESPAWN_ANCHOR) {
            BlockState oldState = BlockUtil.getState(pos);
            MeteorDamageUtil.mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            double damage = MeteorDamageUtil.explosionDamage(player, pos.toCenterPos(), predict, 5.0f);
            MeteorDamageUtil.mc.world.setBlockState(pos, oldState);
            return damage;
        }
        return MeteorDamageUtil.explosionDamage(player, pos.toCenterPos(), predict, 5.0f);
    }

    public static double explosionDamage(PlayerEntity player, Vec3d pos, PlayerEntity predict, float power) {
        if (player != null && player.getAbilities().creativeMode) {
            return 0.0;
        }
        double modDistance = Math.sqrt(predict.squaredDistanceTo(pos));
        if (modDistance > 10.0) {
            return 0.0;
        }
        double exposure = Explosion.getExposure((Vec3d)pos, (Entity)predict);
        double impact = (1.0 - modDistance / 10.0) * exposure;
        double damage = (impact * impact + impact) / 2.0 * 7.0 * 10.0 + 1.0;
        damage = MeteorDamageUtil.getDamageForDifficulty(damage);
        damage = MeteorDamageUtil.resistanceReduction((LivingEntity)player, damage);
        damage = DamageUtil.getDamageLeft((float)((float)damage), (float)player.getArmor(), (float)((float)player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue()));
        ((IExplosion)explosion).setWorld((World)MeteorDamageUtil.mc.world);
        ((IExplosion)explosion).setX(pos.x);
        ((IExplosion)explosion).setY(pos.y);
        ((IExplosion)explosion).setZ(pos.z);
        ((IExplosion)explosion).setPower(power);
        damage = MeteorDamageUtil.blastProtReduction((Entity)player, damage, explosion);
        if (damage < 0.0) {
            damage = 0.0;
        }
        return damage;
    }

    private static double getDamageForDifficulty(double damage) {
        return switch (MeteorDamageUtil.mc.world.getDifficulty()) {
            case PEACEFUL -> 0.0;
            case EASY -> Math.min(damage / 2.0 + 1.0, damage);
            case HARD -> damage * 3.0 / 2.0;
            default -> damage;
        };
    }

    private static double blastProtReduction(Entity player, double damage, Explosion explosion) {
        int protLevel = EnchantmentHelper.getProtectionAmount((Iterable)player.getArmorItems(), (DamageSource)MeteorDamageUtil.mc.world.getDamageSources().explosion(explosion));
        if (protLevel > 20) {
            protLevel = 20;
        }
        return (damage *= 1.0 - (double)protLevel / 25.0) < 0.0 ? 0.0 : damage;
    }

    private static double resistanceReduction(LivingEntity player, double damage) {
        if (player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            int lvl = player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1;
            damage *= 1.0 - (double)lvl * 0.2;
        }
        return damage < 0.0 ? 0.0 : damage;
    }
}

