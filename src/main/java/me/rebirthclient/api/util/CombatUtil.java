/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.api.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.MathUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.settings.SwingMode;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CombatUtil
implements Wrapper {
    public static final Timer breakTimer = new Timer();
    public static boolean terrainIgnore = false;

    public static List<PlayerEntity> getEnemies(double range) {
        ArrayList<PlayerEntity> list = new ArrayList<PlayerEntity>();
        for (PlayerEntity player : CombatUtil.mc.world.getPlayers()) {
            if (!CombatUtil.isValid((Entity)player, range)) continue;
            list.add(player);
        }
        return list;
    }

    public static void attackCrystal(BlockPos pos, boolean rotate, boolean eatingPause) {
        block0: {
            Iterator iterator = CombatUtil.mc.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(pos)).iterator();
            if (!iterator.hasNext()) break block0;
            EndCrystalEntity entity = (EndCrystalEntity)iterator.next();
            CombatUtil.attackCrystal((Entity)entity, rotate, eatingPause);
        }
    }

    public static void attackCrystal(Box box, boolean rotate, boolean eatingPause) {
        block0: {
            Iterator iterator = CombatUtil.mc.world.getNonSpectatingEntities(EndCrystalEntity.class, box).iterator();
            if (!iterator.hasNext()) break block0;
            EndCrystalEntity entity = (EndCrystalEntity)iterator.next();
            CombatUtil.attackCrystal((Entity)entity, rotate, eatingPause);
        }
    }

    public static void attackCrystal(Entity crystal, boolean rotate, boolean usingPause) {
        if (!breakTimer.passedMs((long)(Rebirth.HUD.attackDelay.getValue() * 1000.0))) {
            return;
        }
        if (usingPause && EntityUtil.isUsing()) {
            return;
        }
        if (crystal != null) {
            breakTimer.reset();
            CombatUtil.mc.player.networkHandler.sendPacket((Packet)PlayerInteractEntityC2SPacket.attack((Entity)crystal, (boolean)CombatUtil.mc.player.isSneaking()));
            EntityUtil.swingHand(Hand.MAIN_HAND, (SwingMode)Rebirth.HUD.swingMode.getValue());
            if (rotate && Rebirth.HUD.attackRotate.getValue()) {
                EntityUtil.faceVector(new Vec3d(crystal.getX(), crystal.getY() + 0.25, crystal.getZ()));
            }
        }
    }

    public static boolean isValid(Entity entity, double range) {
        boolean invalid = entity == null || !entity.isAlive() || entity.equals((Object)CombatUtil.mc.player) || entity instanceof PlayerEntity && Rebirth.FRIEND.isFriend(entity.getName().getString()) || CombatUtil.mc.player.squaredDistanceTo(entity) > MathUtil.square(range);
        return !invalid;
    }

    public static BlockPos getHole(float range, boolean doubleHole, boolean any) {
        BlockPos bestPos = null;
        double bestDistance = range + 1.0f;
        for (BlockPos pos : BlockUtil.getSphere(range)) {
            if (!BlockUtil.isHole(pos, true, true, any) && (!doubleHole || !CombatUtil.isDoubleHole(pos))) continue;
            double distance = MathHelper.sqrt((float)((float)CombatUtil.mc.player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5)));
            if (bestPos != null && !(distance < bestDistance)) continue;
            bestPos = pos;
            bestDistance = distance;
        }
        return bestPos;
    }

    public static boolean isDoubleHole(BlockPos pos) {
        Direction unHardFacing = CombatUtil.is3Block(pos);
        if (unHardFacing != null) {
            return (unHardFacing = CombatUtil.is3Block(pos = pos.offset(unHardFacing))) != null;
        }
        return false;
    }

    public static Direction is3Block(BlockPos pos) {
        if (!CombatUtil.isHard(pos.down())) {
            return null;
        }
        if (!(BlockUtil.isAir(pos) && BlockUtil.isAir(pos.up()) && BlockUtil.isAir(pos.up(2)))) {
            return null;
        }
        int progress = 0;
        Direction unHardFacing = null;
        for (Direction facing : Direction.values()) {
            if (facing == Direction.UP || facing == Direction.DOWN) continue;
            if (CombatUtil.isHard(pos.offset(facing))) {
                ++progress;
                continue;
            }
            int progress2 = 0;
            for (Direction facing2 : Direction.values()) {
                if (facing2 == Direction.DOWN || facing2 == facing.getOpposite() || !CombatUtil.isHard(pos.offset(facing).offset(facing2))) continue;
                ++progress2;
            }
            if (progress2 == 4) {
                ++progress;
                continue;
            }
            unHardFacing = facing;
        }
        if (progress == 3) {
            return unHardFacing;
        }
        return null;
    }

    public static PlayerEntity getClosestEnemy(double distance) {
        PlayerEntity closest = null;
        for (PlayerEntity player : CombatUtil.getEnemies(distance)) {
            if (closest == null) {
                closest = player;
                continue;
            }
            if (!(CombatUtil.mc.player.getEyePos().squaredDistanceTo(player.getPos()) < CombatUtil.mc.player.squaredDistanceTo((Entity)closest))) continue;
            closest = player;
        }
        return closest;
    }

    public static Vec3d getEntityPosVec(PlayerEntity entity, int ticks) {
        return entity.getPos().add(CombatUtil.getMotionVec((Entity)entity, ticks, true));
    }

    public static Vec3d getMotionVec(Entity entity, int ticks, boolean collision) {
        double dX = entity.getX() - entity.prevX;
        double dY = entity.getY() - entity.prevY;
        double dZ = entity.getZ() - entity.prevZ;
        double entityMotionPosX = 0.0;
        double entityMotionPosY = 0.0;
        double entityMotionPosZ = 0.0;
        if (collision) {
            for (double i = 1.0; i <= (double)ticks && !CombatUtil.mc.world.canCollide(entity, entity.getBoundingBox().offset(new Vec3d(dX * i, dY * i, dZ * i))); i += 0.5) {
                entityMotionPosX = dX * i;
                entityMotionPosY = dY * i;
                entityMotionPosZ = dZ * i;
            }
        } else {
            entityMotionPosX = dX * (double)ticks;
            entityMotionPosY = dY * (double)ticks;
            entityMotionPosZ = dZ * (double)ticks;
        }
        return new Vec3d(entityMotionPosX, entityMotionPosY, entityMotionPosZ);
    }

    public static boolean isHard(BlockPos pos) {
        return BlockUtil.getState(pos).getBlock() == Blocks.OBSIDIAN || BlockUtil.getState(pos).getBlock() == Blocks.ENDER_CHEST || BlockUtil.getState(pos).getBlock() == Blocks.BEDROCK || BlockUtil.getState(pos).getBlock() == Blocks.ANVIL;
    }
}

