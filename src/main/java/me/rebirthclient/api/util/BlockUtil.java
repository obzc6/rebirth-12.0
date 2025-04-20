/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BedBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.network.PendingUpdateManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ExperienceOrbEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ArrowEntity
 *  net.minecraft.entity.projectile.thrown.ExperienceBottleEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.chunk.WorldChunk
 */
package me.rebirthclient.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.asm.accessors.IClientWorld;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.settings.Placement;
import me.rebirthclient.mod.settings.SwingMode;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.WorldChunk;

public class BlockUtil
implements Wrapper {
    public static final List<Block> shiftBlocks = Arrays.asList(new Block[]{Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.BIRCH_TRAPDOOR, Blocks.BAMBOO_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.CHERRY_TRAPDOOR, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.ACACIA_TRAPDOOR, Blocks.ENCHANTING_TABLE, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX});
    public static final ArrayList<BlockPos> placedPos = new ArrayList();

    public static boolean isAir(BlockPos pos) {
        return BlockUtil.mc.world.isAir(pos);
    }

    public static boolean isMining(BlockPos pos) {
        return Rebirth.BREAK.isMining(pos) || pos.equals((Object)PacketMine.breakPos);
    }

    public static boolean canPlace(BlockPos pos) {
        return BlockUtil.canPlace(pos, 1000.0);
    }

    public static boolean canPlace(BlockPos pos, double distance) {
        if (BlockUtil.getPlaceSide(pos, distance) == null) {
            return false;
        }
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        return !BlockUtil.hasEntity(pos, false);
    }

    public static boolean canPlace(BlockPos pos, double distance, boolean ignoreCrystal) {
        if (BlockUtil.getPlaceSide(pos, distance) == null) {
            return false;
        }
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        return !BlockUtil.hasEntity(pos, ignoreCrystal);
    }

    public static boolean bedCanPlace(BlockPos pos, double distance, boolean ignoreCrystal) {
        if (BlockUtil.getPlaceSide(pos, distance) == null) {
            return false;
        }
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        return !BlockUtil.hasEntity2(pos, ignoreCrystal);
    }

    public static boolean clientCanPlace(BlockPos pos) {
        return BlockUtil.clientCanPlace(pos, false);
    }

    public static boolean clientCanPlace(BlockPos pos, boolean ignoreCrystal) {
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        return !BlockUtil.hasEntity(pos, ignoreCrystal);
    }

    public static boolean hasEntity(BlockPos pos, boolean ignoreCrystal) {
        for (Entity entity : BlockUtil.mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (!entity.isAlive() || entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ExperienceBottleEntity || entity instanceof ArrowEntity || ignoreCrystal && entity instanceof EndCrystalEntity || entity instanceof ArmorStandEntity && Rebirth.HUD.obsMode.getValue()) continue;
            return true;
        }
        return false;
    }

    public static boolean hasEntity2(BlockPos pos, boolean ignoreCrystal) {
        for (Entity entity : BlockUtil.mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (!entity.isAlive() || entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ExperienceBottleEntity || entity instanceof ArrowEntity || ignoreCrystal && entity instanceof EndCrystalEntity || entity instanceof ArmorStandEntity && Rebirth.HUD.obsMode.getValue() || entity instanceof PlayerEntity) continue;
            return true;
        }
        return false;
    }

    public static boolean hasCrystal(BlockPos pos) {
        for (Entity entity : BlockUtil.mc.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(pos))) {
            if (!entity.isAlive() || !(entity instanceof EndCrystalEntity)) continue;
            return true;
        }
        return false;
    }

    public static boolean hasEntityBlockCrystal(BlockPos pos, boolean ignoreCrystal) {
        for (Entity entity : BlockUtil.mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (!entity.isAlive() || ignoreCrystal && entity instanceof EndCrystalEntity || entity instanceof ArmorStandEntity && Rebirth.HUD.obsMode.getValue()) continue;
            return true;
        }
        return false;
    }

    public static boolean hasEntityBlockCrystal(BlockPos pos, boolean ignoreCrystal, boolean ignoreItem) {
        for (Entity entity : BlockUtil.mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (!entity.isAlive() || ignoreItem && entity instanceof ItemEntity || ignoreCrystal && entity instanceof EndCrystalEntity || entity instanceof ArmorStandEntity && Rebirth.HUD.obsMode.getValue()) continue;
            return true;
        }
        return false;
    }

    public static Direction getBestNeighboring(BlockPos pos, Direction facing) {
        for (Direction i : Direction.values()) {
            if (facing != null && pos.offset(i).equals((Object)pos.offset(facing, -1)) || i == Direction.DOWN || BlockUtil.getPlaceSide(pos, false, true) == null) continue;
            return i;
        }
        Direction bestFacing = null;
        double distance = 0.0;
        for (Direction i : Direction.values()) {
            if (facing != null && pos.offset(i).equals((Object)pos.offset(facing, -1)) || i == Direction.DOWN || BlockUtil.getPlaceSide(pos) == null || bestFacing != null && !(BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()) < distance)) continue;
            bestFacing = i;
            distance = BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos());
        }
        return bestFacing;
    }

    public static boolean canPlaceCrystal(BlockPos pos) {
        BlockPos obsPos = pos.down();
        BlockPos boost = obsPos.up();
        return (BlockUtil.getBlock(obsPos) == Blocks.BEDROCK || BlockUtil.getBlock(obsPos) == Blocks.OBSIDIAN) && BlockUtil.getClickSideStrict(obsPos) != null && BlockUtil.getBlock(boost) == Blocks.AIR && !BlockUtil.hasEntityBlockCrystal(boost, false) && !BlockUtil.hasEntityBlockCrystal(boost.up(), false);
    }

    public static void placeCrystal(BlockPos pos, boolean rotate) {
        boolean offhand = BlockUtil.mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL;
        BlockPos obsPos = pos.down();
        Direction facing = BlockUtil.getClickSide(obsPos);
        Vec3d vec = obsPos.toCenterPos().add((double)facing.getVector().getX() * 0.5, (double)facing.getVector().getY() * 0.5, (double)facing.getVector().getZ() * 0.5);
        if (rotate) {
            EntityUtil.faceVector(vec);
        }
        BlockUtil.clickBlock(obsPos, facing, false, offhand ? Hand.OFF_HAND : Hand.MAIN_HAND);
    }

    public static void placeBlock(BlockPos pos, boolean rotate) {
        BlockUtil.placeBlock(pos, rotate, true);
    }

    public static void placeBlock(BlockPos pos, boolean rotate, boolean packet) {
        Direction side;
        if (BlockUtil.airPlace()) {
            for (Direction i : Direction.values()) {
                if (!BlockUtil.mc.world.isAir(pos.offset(i))) continue;
                BlockUtil.clickBlock(pos, i, rotate, Hand.MAIN_HAND, packet);
                return;
            }
        }
        if ((side = BlockUtil.getPlaceSide(pos)) == null) {
            return;
        }
        placedPos.add(pos);
        BlockUtil.clickBlock(pos.offset(side), side.getOpposite(), rotate, Hand.MAIN_HAND, packet);
    }

    public static boolean isHole(BlockPos pos) {
        return BlockUtil.isHole(pos, true, false, false);
    }

    public static boolean isHole(BlockPos pos, boolean canStand, boolean checkTrap, boolean anyBlock) {
        int blockProgress = 0;
        for (Direction i : Direction.values()) {
            if (i == Direction.UP || i == Direction.DOWN || (!anyBlock || BlockUtil.mc.world.isAir(pos.offset(i))) && !CombatUtil.isHard(pos.offset(i))) continue;
            ++blockProgress;
        }
        return (!checkTrap || BlockUtil.getBlock(pos) == Blocks.AIR && BlockUtil.getBlock(pos.add(0, 1, 0)) == Blocks.AIR && BlockUtil.getBlock(pos.add(0, 2, 0)) == Blocks.AIR) && blockProgress > 3 && (!canStand || BlockUtil.getState(pos.add(0, -1, 0)).blocksMovement());
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate) {
        BlockUtil.clickBlock(pos, side, rotate, Hand.MAIN_HAND);
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, Hand hand) {
        Vec3d directionVec = new Vec3d((double)pos.getX() + 0.5 + (double)side.getVector().getX() * 0.5, (double)pos.getY() + 0.5 + (double)side.getVector().getY() * 0.5, (double)pos.getZ() + 0.5 + (double)side.getVector().getZ() * 0.5);
        if (rotate) {
            EntityUtil.faceVector(directionVec);
        }
        EntityUtil.swingHand(hand, (SwingMode)Rebirth.HUD.swingMode.getValue());
        BlockHitResult result = new BlockHitResult(directionVec, side, pos, false);
        BlockUtil.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractBlockC2SPacket(hand, result, BlockUtil.getWorldActionId(BlockUtil.mc.world)));
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, Hand hand, boolean packet) {
        Vec3d directionVec = new Vec3d((double)pos.getX() + 0.5 + (double)side.getVector().getX() * 0.5, (double)pos.getY() + 0.5 + (double)side.getVector().getY() * 0.5, (double)pos.getZ() + 0.5 + (double)side.getVector().getZ() * 0.5);
        if (rotate) {
            EntityUtil.faceVector(directionVec);
        }
        EntityUtil.swingHand(hand, (SwingMode)Rebirth.HUD.swingMode.getValue());
        BlockHitResult result = new BlockHitResult(directionVec, side, pos, false);
        if (packet) {
            BlockUtil.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, BlockUtil.getWorldActionId(BlockUtil.mc.world)));
        } else {
            BlockUtil.mc.interactionManager.interactBlock(BlockUtil.mc.player, Hand.MAIN_HAND, result);
        }
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, Hand hand, SwingMode swingMode) {
        Vec3d directionVec = new Vec3d((double)pos.getX() + 0.5 + (double)side.getVector().getX() * 0.5, (double)pos.getY() + 0.5 + (double)side.getVector().getY() * 0.5, (double)pos.getZ() + 0.5 + (double)side.getVector().getZ() * 0.5);
        if (rotate) {
            EntityUtil.faceVector(directionVec);
        }
        EntityUtil.swingHand(hand, swingMode);
        BlockHitResult result = new BlockHitResult(directionVec, side, pos, false);
        BlockUtil.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractBlockC2SPacket(hand, result, BlockUtil.getWorldActionId(BlockUtil.mc.world)));
    }

    public static Direction getPlaceSide(BlockPos pos) {
        return BlockUtil.getPlaceSide(pos, Rebirth.HUD.placement.getValue() == Placement.Strict, Rebirth.HUD.placement.getValue() == Placement.Legit);
    }

    public static Direction getPlaceSide(BlockPos pos, boolean strict, boolean legit) {
        double dis = 114514.0;
        Direction side = null;
        for (Direction i : Direction.values()) {
            if (!BlockUtil.canClick(pos.offset(i)) || BlockUtil.canReplace(pos.offset(i)) || legit && !EntityUtil.canSee(pos.offset(i), i.getOpposite()) || strict && !BlockUtil.isStrictDirection(pos.offset(i), i.getOpposite())) continue;
            double vecDis = BlockUtil.mc.player.squaredDistanceTo(pos.toCenterPos().add((double)i.getVector().getX() * 0.5, (double)i.getVector().getY() * 0.5, (double)i.getVector().getZ() * 0.5));
            if (side != null && !(vecDis < dis)) continue;
            side = i;
            dis = vecDis;
        }
        if (side == null && BlockUtil.airPlace()) {
            for (Direction i : Direction.values()) {
                if (!BlockUtil.mc.world.isAir(pos.offset(i))) continue;
                return i;
            }
        }
        return side;
    }

    public static double distanceToXZ(double x, double z) {
        double dx = BlockUtil.mc.player.getX() - x;
        double dz = BlockUtil.mc.player.getZ() - z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static Direction getPlaceSide(BlockPos pos, double distance) {
        double dis = 114514.0;
        Direction side = null;
        for (Direction i : Direction.values()) {
            if (!BlockUtil.canClick(pos.offset(i)) || BlockUtil.canReplace(pos.offset(i)) || (Rebirth.HUD.placement.getValue() != Placement.Legit ? Rebirth.HUD.placement.getValue() == Placement.Strict && !BlockUtil.isStrictDirection(pos.offset(i), i.getOpposite()) : !EntityUtil.canSee(pos.offset(i), i.getOpposite()))) continue;
            double vecDis = BlockUtil.mc.player.squaredDistanceTo(pos.toCenterPos().add((double)i.getVector().getX() * 0.5, (double)i.getVector().getY() * 0.5, (double)i.getVector().getZ() * 0.5));
            if ((double)MathHelper.sqrt((float)((float)vecDis)) > distance || side != null && !(vecDis < dis)) continue;
            side = i;
            dis = vecDis;
        }
        if (side == null && BlockUtil.airPlace()) {
            for (Direction i : Direction.values()) {
                if (!BlockUtil.mc.world.isAir(pos.offset(i))) continue;
                return i;
            }
        }
        return side;
    }

    public static Direction getClickSide(BlockPos pos) {
        Direction side = null;
        double range = 100.0;
        for (Direction i : Direction.values()) {
            if (!EntityUtil.canSee(pos, i) || (double)MathHelper.sqrt((float)((float)BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()))) > range) continue;
            side = i;
            range = MathHelper.sqrt((float)((float)BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos())));
        }
        if (side != null) {
            return side;
        }
        side = Direction.UP;
        for (Direction i : Direction.values()) {
            if (Rebirth.HUD.placement.getValue() == Placement.Strict && (!BlockUtil.isStrictDirection(pos, i) || !BlockUtil.isAir(pos.offset(i))) || (double)MathHelper.sqrt((float)((float)BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()))) > range) continue;
            side = i;
            range = MathHelper.sqrt((float)((float)BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos())));
        }
        return side;
    }

    public static Direction getClickSideStrict(BlockPos pos) {
        Direction side = null;
        double range = 100.0;
        for (Direction i : Direction.values()) {
            if (!EntityUtil.canSee(pos, i) || (double)MathHelper.sqrt((float)((float)BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()))) > range) continue;
            side = i;
            range = MathHelper.sqrt((float)((float)BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos())));
        }
        if (side != null) {
            return side;
        }
        side = null;
        for (Direction i : Direction.values()) {
            if (Rebirth.HUD.placement.getValue() == Placement.Strict && (!BlockUtil.isStrictDirection(pos, i) || !BlockUtil.isAir(pos.offset(i))) || (double)MathHelper.sqrt((float)((float)BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()))) > range) continue;
            side = i;
            range = MathHelper.sqrt((float)((float)BlockUtil.mc.player.squaredDistanceTo(pos.offset(i).toCenterPos())));
        }
        return side;
    }

    public static boolean isStrictDirection(BlockPos pos, Direction side) {
        BlockState blockState = BlockUtil.mc.world.getBlockState(pos);
        boolean isFullBox = blockState.getBlock() == Blocks.AIR || blockState.isFullCube((BlockView)BlockUtil.mc.world, pos) || BlockUtil.getBlock(pos) == Blocks.COBWEB;
        return BlockUtil.isStrictDirection(pos, side, isFullBox);
    }

    public static boolean isStrictDirection(BlockPos pos, Direction side, boolean isFullBox) {
        if (EntityUtil.getPlayerPos().getY() - pos.getY() >= 0 && side == Direction.DOWN) {
            return false;
        }
        Vec3d eyePos = EntityUtil.getEyesPos();
        Vec3d blockCenter = pos.toCenterPos();
        ArrayList<Direction> validAxis = new ArrayList<Direction>();
        validAxis.addAll(BlockUtil.checkAxis(eyePos.x - blockCenter.x, Direction.WEST, Direction.EAST, !isFullBox));
        validAxis.addAll(BlockUtil.checkAxis(eyePos.y - blockCenter.y, Direction.DOWN, Direction.UP, BlockUtil.getBlock(pos) != Blocks.COBWEB));
        validAxis.addAll(BlockUtil.checkAxis(eyePos.z - blockCenter.z, Direction.NORTH, Direction.SOUTH, !isFullBox));
        return validAxis.contains((Object)side);
    }

    public static ArrayList<Direction> checkAxis(double diff, Direction negativeSide, Direction positiveSide, boolean bothIfInRange) {
        ArrayList<Direction> valid = new ArrayList<Direction>();
        if (diff < -0.5) {
            valid.add(negativeSide);
        }
        if (diff > 0.5) {
            valid.add(positiveSide);
        }
        if (bothIfInRange) {
            if (!valid.contains((Object)negativeSide)) {
                valid.add(negativeSide);
            }
            if (!valid.contains((Object)positiveSide)) {
                valid.add(positiveSide);
            }
        }
        return valid;
    }

    public static int getWorldActionId(ClientWorld world) {
        PendingUpdateManager pum = BlockUtil.getUpdateManager(world);
        int p = pum.getSequence();
        pum.close();
        return p;
    }

    public static PendingUpdateManager getUpdateManager(ClientWorld world) {
        return ((IClientWorld)world).acquirePendingUpdateManager();
    }

    public static ArrayList<BlockEntity> getTileEntities() {
        return BlockUtil.getLoadedChunks().flatMap(chunk -> chunk.getBlockEntities().values().stream()).collect(Collectors.toCollection(ArrayList::new));
    }

    public static Stream<WorldChunk> getLoadedChunks() {
        int radius = Math.max(2, BlockUtil.mc.options.getClampedViewDistance()) + 3;
        int diameter = radius * 2 + 1;
        ChunkPos center = BlockUtil.mc.player.getChunkPos();
        ChunkPos min = new ChunkPos(center.x - radius, center.z - radius);
        ChunkPos max = new ChunkPos(center.x + radius, center.z + radius);
        return Stream.iterate(min, pos -> {
            int x = pos.x;
            int z = pos.z;
            if (++x > max.x) {
                x = min.x;
                ++z;
            }
            return new ChunkPos(x, z);
        }).limit((long)diameter * (long)diameter).filter(c -> BlockUtil.mc.world.isChunkLoaded(c.x, c.z)).map(c -> BlockUtil.mc.world.getChunk(c.x, c.z)).filter(Objects::nonNull);
    }

    public static ArrayList<BlockPos> getSphere(float range) {
        return BlockUtil.getSphere(range, BlockUtil.mc.player.getEyePos());
    }

    public static ArrayList<BlockPos> getSphere(float range, Vec3d pos) {
        ArrayList<BlockPos> list = new ArrayList<BlockPos>();
        for (double x = pos.getX() - ((double)range + 0.5); x < pos.getX() + ((double)range + 0.5); x += 1.0) {
            for (double y = pos.getY() - ((double)range + 0.5); y < pos.getY() + ((double)range + 0.5); y += 1.0) {
                for (double z = pos.getZ() - ((double)range + 0.5); z < pos.getZ() + ((double)range + 0.5); z += 1.0) {
                    BlockPosX curPos = new BlockPosX(x, y, z);
                    if (!(MathHelper.sqrt((float)((float)pos.squaredDistanceTo(curPos.toCenterPos()))) <= range)) continue;
                    list.add(curPos);
                }
            }
        }
        return list;
    }

    public static BlockState getState(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return BlockUtil.getState(pos).getBlock();
    }

    public static boolean canReplace(BlockPos pos) {
        return BlockUtil.getState(pos).isReplaceable();
    }

    public static boolean canClick(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).isSolid() && (!shiftBlocks.contains((Object)BlockUtil.getBlock(pos)) && !(BlockUtil.getBlock(pos) instanceof BedBlock) || BlockUtil.mc.player.isSneaking());
    }

    public static boolean airPlace() {
        return Rebirth.HUD.placement.getValue() == Placement.AirPlace;
    }

    public static boolean canBlockFacing(BlockPos pos) {
        boolean airCheck = false;
        for (Direction side : Direction.values()) {
            if (!BlockUtil.canClick(pos.offset(side))) continue;
            airCheck = true;
        }
        return airCheck;
    }

    public static boolean canPlaceEnum(BlockPos pos) {
        if (!BlockUtil.canBlockFacing(pos)) {
            return false;
        }
        return BlockUtil.canPlace(pos);
    }
}

