/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.network.PendingUpdateManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.AxeItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.SwordItem
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookAndOnGround
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 */
package me.rebirthclient.api.util;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.RunManager;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.asm.accessors.IClientWorld;
import me.rebirthclient.mod.settings.SwingMode;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class EntityUtil
implements Wrapper {
    public static boolean rotating = false;

    public static boolean isHoldingWeapon(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof SwordItem || player.getMainHandStack().getItem() instanceof AxeItem;
    }

    public static boolean isUsing() {
        return EntityUtil.mc.player.isUsingItem();
    }

    public static boolean isInsideBlock() {
        if (BlockUtil.getBlock(EntityUtil.getPlayerPos(true)) == Blocks.ENDER_CHEST) {
            return true;
        }
        return EntityUtil.mc.world.canCollide((Entity)EntityUtil.mc.player, EntityUtil.mc.player.getBoundingBox());
    }

    public static int getDamagePercent(ItemStack stack) {
        return (int)((double)(stack.getMaxDamage() - stack.getDamage()) / Math.max(0.1, (double)stack.getMaxDamage()) * 100.0);
    }

    public static boolean isArmorLow(PlayerEntity player, int durability) {
        for (ItemStack piece : player.getArmorItems()) {
            if (piece == null || piece.isEmpty()) {
                return true;
            }
            if (EntityUtil.getDamagePercent(piece) >= durability) continue;
            return true;
        }
        return false;
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = EntityUtil.getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{EntityUtil.mc.player.getYaw() + MathHelper.wrapDegrees((float)(yaw - EntityUtil.mc.player.getYaw())), EntityUtil.mc.player.getPitch() + MathHelper.wrapDegrees((float)(pitch - EntityUtil.mc.player.getPitch()))};
    }

    public static float getHealth(Entity entity) {
        if (entity.isLiving()) {
            LivingEntity livingBase = (LivingEntity)entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static BlockPos getPlayerPos() {
        if (EntityUtil.mc.player == null) {
            return null;
        }
        return new BlockPosX(EntityUtil.mc.player.getPos());
    }

    public static BlockPos getEntityPos(Entity entity) {
        return new BlockPosX(entity.getPos());
    }

    public static BlockPos getPlayerPos(boolean fix) {
        return new BlockPosX(EntityUtil.mc.player.getPos(), fix);
    }

    public static BlockPos getEntityPos(Entity entity, boolean fix) {
        return new BlockPosX(entity.getPos(), fix);
    }

    public static Vec3d getEyesPos() {
        return EntityUtil.mc.player.getEyePos();
    }

    public static boolean canSee(BlockPos pos, Direction side) {
        Vec3d testVec = pos.toCenterPos().add((double)side.getVector().getX() * 0.5, (double)side.getVector().getY() * 0.5, (double)side.getVector().getZ() * 0.5);
        BlockHitResult result = EntityUtil.mc.world.raycast(new RaycastContext(EntityUtil.getEyesPos(), testVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)EntityUtil.mc.player));
        return result == null || result.getType() == HitResult.Type.MISS;
    }

    public static void sendYawAndPitch(float yaw, float pitch) {
        EntityUtil.sendLook(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, EntityUtil.mc.player.isOnGround()));
    }

    public static void faceVector(Vec3d directionVec) {
        RunManager.ROTATE_TIMER.reset();
        RunManager.directionVec = directionVec;
        float[] angle = EntityUtil.getLegitRotations(directionVec);
        if (angle[0] == Rebirth.RUN.lastYaw && angle[1] == Rebirth.RUN.lastPitch) {
            return;
        }
        EntityUtil.sendLook(new PlayerMoveC2SPacket.LookAndOnGround(angle[0], angle[1], EntityUtil.mc.player.isOnGround()));
    }

    public static void sendLook(PlayerMoveC2SPacket.LookAndOnGround lookAndOnGround) {
        if (lookAndOnGround.getYaw(114514.0f) == Rebirth.RUN.lastYaw && lookAndOnGround.getPitch(114514.0f) == Rebirth.RUN.lastPitch) {
            return;
        }
        rotating = true;
        EntityUtil.mc.player.networkHandler.sendPacket((Packet)lookAndOnGround);
        rotating = false;
    }

    public static void facePosSide(BlockPos pos, Direction side) {
        Vec3d hitVec = pos.toCenterPos().add(new Vec3d((double)side.getVector().getX() * 0.5, (double)side.getVector().getY() * 0.5, (double)side.getVector().getZ() * 0.5));
        EntityUtil.faceVector(hitVec);
    }

    public static int getWorldActionId(ClientWorld world) {
        PendingUpdateManager pum = EntityUtil.getUpdateManager(world);
        int p = pum.getSequence();
        pum.close();
        return p;
    }

    public static boolean isElytraFlying() {
        return EntityUtil.mc.player.isFallFlying();
    }

    static PendingUpdateManager getUpdateManager(ClientWorld world) {
        return ((IClientWorld)world).acquirePendingUpdateManager();
    }

    public static void swingHand(Hand hand, SwingMode mode) {
        switch (mode) {
            case Normal: {
                EntityUtil.mc.player.swingHand(hand);
                break;
            }
            case Client: {
                EntityUtil.mc.player.swingHand(hand, false);
                break;
            }
            case Server: {
                EntityUtil.mc.player.networkHandler.sendPacket((Packet)new HandSwingC2SPacket(hand));
            }
        }
    }

    public static void sync() {
        if (Rebirth.HUD.inventorySync.getValue()) {
            EntityUtil.mc.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(EntityUtil.mc.player.currentScreenHandler.syncId));
        }
    }
}

