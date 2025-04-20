/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ButtonBlock
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.MoveEvent;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.render.PlaceRender;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class OBSClip
extends Module {
    public OBSClip() {
        super("OBSClip", Module.Category.Player);
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        if (OBSClip.nullCheck()) {
            return;
        }
        if (OBSClip.issolid((PlayerEntity)OBSClip.mc.player)) {
            return;
        }
        if (!OBSClip.newBedrockCheck()) {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.cancel();
        }
    }

    public static boolean newBedrockCheck() {
        BlockPos playerBlockPos = EntityUtil.getPlayerPos(true);
        for (int xOffset = -1; xOffset <= 1; ++xOffset) {
            for (int yOffset = -1; yOffset <= 1; ++yOffset) {
                for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                    BlockPos offsetPos = playerBlockPos.add(xOffset, yOffset, zOffset);
                    if (OBSClip.mc.world.getBlockState(offsetPos).getBlock() != Blocks.BEDROCK || !OBSClip.mc.player.getBoundingBox().intersects(new Box(offsetPos))) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        if (OBSClip.nullCheck()) {
            return;
        }
        if (OBSClip.issolid((PlayerEntity)OBSClip.mc.player)) {
            return;
        }
        if (!OBSClip.newBedrockCheck()) {
            return;
        }
        this.PlaceBlock(EntityUtil.getPlayerPos().up());
    }

    public void PlaceBlock(BlockPos pos) {
        if (!OBSClip.canPlace(pos)) {
            return;
        }
        int old = OBSClip.mc.player.getInventory().selectedSlot;
        int block = InventoryUtil.findClass(ButtonBlock.class);
        if (block == -1) {
            return;
        }
        InventoryUtil.doSwap(block);
        BlockUtil.placeBlock(pos, true, true);
        PlaceRender.addBlock(pos);
        InventoryUtil.doSwap(old);
    }

    public static boolean canPlace(BlockPos pos) {
        if (!BlockUtil.canBlockFacing(pos)) {
            return false;
        }
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        return !BlockUtil.hasEntity2(pos, false);
    }

    public static boolean issolid(PlayerEntity player) {
        Vec3d playerPos = player.getPos();
        BlockPosX pos = new BlockPosX(playerPos.getX(), playerPos.getY() + 1.0, playerPos.getZ());
        return OBSClip.mc.world.getBlockState((BlockPos)pos).getBlock() != Blocks.AIR;
    }

    public static boolean isInsideBedrock() {
        for (int y = 0; y < 2; ++y) {
            BlockPos pos = EntityUtil.getPlayerPos().up(y);
            for (Direction i : Direction.values()) {
                if (i == Direction.UP || i == Direction.DOWN || BlockUtil.getBlock(pos.offset(i)) != Blocks.BEDROCK || !OBSClip.mc.player.getBoundingBox().intersects(new Box(pos.offset(i)))) continue;
                return true;
            }
        }
        return false;
    }
}

