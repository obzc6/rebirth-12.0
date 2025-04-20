/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.PistonBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoCrystal;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.modules.render.PlaceRender;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AutoPush
extends Module {
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true));
    private final BooleanSetting yawDeceive = this.add(new BooleanSetting("YawDeceive", true));
    private final BooleanSetting pistonPacket = this.add(new BooleanSetting("PistonPacket", false));
    private final BooleanSetting redStonePacket = this.add(new BooleanSetting("RedStonePacket", true));
    private final BooleanSetting noEating = this.add(new BooleanSetting("NoEating", true));
    private final BooleanSetting attackCrystal = this.add(new BooleanSetting("BreakCrystal", true));
    private final BooleanSetting mine = this.add(new BooleanSetting("Mine", true));
    private final BooleanSetting allowWeb = this.add(new BooleanSetting("AllowWeb", true));
    private final SliderSetting updateDelay = this.add(new SliderSetting("UpdateDelay", 100, 0, 500));
    private final BooleanSetting selfGround = this.add(new BooleanSetting("SelfGround", true));
    private final BooleanSetting onlyGround = this.add(new BooleanSetting("OnlyGround", false));
    private final BooleanSetting checkPiston = this.add(new BooleanSetting("CheckPiston", false));
    private final BooleanSetting noMine = this.add(new BooleanSetting("NoMine", true));
    private final BooleanSetting autoDisable = this.add(new BooleanSetting("AutoDisable", true));
    private final BooleanSetting pullBack = this.add(new BooleanSetting("PullBack", true).setParent());
    private final BooleanSetting onlyBurrow = this.add(new BooleanSetting("OnlyBurrow", true, v -> this.pullBack.isOpen()));
    private final SliderSetting range = this.add(new SliderSetting("Range", 5.0, 0.0, 6.0));
    private final SliderSetting placeRange = this.add(new SliderSetting("PlaceRange", 5.0, 0.0, 6.0));
    private final SliderSetting surroundCheck = this.add(new SliderSetting("SurroundCheck", 2, 0, 4));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true).setParent());
    private final EnumSetting invmode = this.add(new EnumSetting("InventoryMode", InvMode.Inv, v -> this.inventory.isOpen()));
    private final Timer timer = new Timer();
    private PlayerEntity displayTarget = null;

    public AutoPush() {
        super("AutoPush", "use piston push hole fag", Module.Category.Combat);
    }

    @Override
    public void onEnable() {
        AutoCrystal.INSTANCE.lastBreakTimer.reset();
    }

    public static void pistonFacing(Direction i) {
        if (i == Direction.EAST) {
            EntityUtil.sendYawAndPitch(-90.0f, 5.0f);
        } else if (i == Direction.WEST) {
            EntityUtil.sendYawAndPitch(90.0f, 5.0f);
        } else if (i == Direction.NORTH) {
            EntityUtil.sendYawAndPitch(180.0f, 5.0f);
        } else if (i == Direction.SOUTH) {
            EntityUtil.sendYawAndPitch(0.0f, 5.0f);
        }
    }

    static boolean isTargetHere(BlockPos pos, Entity target) {
        return new Box(pos).intersects(target.getBoundingBox());
    }

    public static boolean isInWeb(PlayerEntity player) {
        Vec3d playerPos = player.getPos();
        for (float x : new float[]{0.0f, 0.3f, -0.3f}) {
            for (float z : new float[]{0.0f, 0.3f, -0.3f}) {
                for (float y : new float[]{0.0f, 1.0f, -1.0f}) {
                    BlockPosX pos = new BlockPosX(playerPos.getX() + (double)x, playerPos.getY() + (double)y, playerPos.getZ() + (double)z);
                    if (!AutoPush.isTargetHere(pos, (Entity)player) || AutoPush.mc.world.getBlockState((BlockPos)pos).getBlock() != Blocks.COBWEB) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onUpdate() {
        if (!this.timer.passedMs(this.updateDelay.getValue())) {
            return;
        }
        if (this.selfGround.getValue() && !AutoPush.mc.player.isOnGround()) {
            if (this.autoDisable.getValue()) {
                this.disable();
            }
            return;
        }
        if (this.findBlock(Blocks.REDSTONE_BLOCK) == -1 || this.findClass(PistonBlock.class) == -1) {
            if (this.autoDisable.getValue()) {
                this.disable();
            }
            return;
        }
        if (this.noEating.getValue() && EntityUtil.isUsing()) {
            return;
        }
        this.timer.reset();
        for (PlayerEntity target : CombatUtil.getEnemies(this.range.getValue())) {
            if (!this.canPush(target).booleanValue() || !target.isOnGround() && this.onlyGround.getValue() || AutoPush.isInWeb(target) && !this.allowWeb.getValue()) continue;
            this.displayTarget = target;
            if (!this.doPush(EntityUtil.getEntityPos((Entity)target), target)) continue;
            return;
        }
        if (this.autoDisable.getValue()) {
            this.disable();
        }
        this.displayTarget = null;
    }

    private boolean checkPiston(BlockPos targetPos) {
        for (Direction i : Direction.values()) {
            BlockPos pos;
            if (i == Direction.DOWN || i == Direction.UP || !(this.getBlock((pos = targetPos.up()).offset(i)) instanceof PistonBlock) || ((Direction)this.getBlockState(pos.offset(i)).get((Property)FacingBlock.FACING)).getOpposite() != i) continue;
            for (Direction i2 : Direction.values()) {
                if (this.getBlock(pos.offset(i).offset(i2)) != Blocks.REDSTONE_BLOCK || !this.mine.getValue()) continue;
                this.mine(pos.offset(i).offset(i2));
                if (this.autoDisable.getValue()) {
                    this.disable();
                }
                return true;
            }
        }
        return false;
    }

    public boolean doPush(BlockPos targetPos, PlayerEntity target) {
        if (this.checkPiston.getValue() && this.checkPiston(targetPos)) {
            return true;
        }
        if (!AutoPush.mc.world.getBlockState(targetPos.up(2)).blocksMovement()) {
            BlockPos pos;
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP || !(this.getBlock(pos = targetPos.offset(i).up()) instanceof PistonBlock) || this.getBlockState(pos.offset(i, -2)).blocksMovement() || this.getBlock(pos.offset(i, -2).up()) != Blocks.AIR && this.getBlock(pos.offset(i, -2).up()) != Blocks.REDSTONE_BLOCK || ((Direction)this.getBlockState(pos).get((Property)FacingBlock.FACING)).getOpposite() != i) continue;
                for (Direction i2 : Direction.values()) {
                    if (this.getBlock(pos.offset(i2)) != Blocks.REDSTONE_BLOCK) continue;
                    if (this.mine.getValue()) {
                        this.mine(pos.offset(i2));
                    }
                    if (this.autoDisable.getValue()) {
                        this.disable();
                    }
                    return true;
                }
            }
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP || !(this.getBlock(pos = targetPos.offset(i).up()) instanceof PistonBlock) || this.getBlockState(pos.offset(i, -2)).blocksMovement() || this.getBlock(pos.offset(i, -2).up()) != Blocks.AIR && this.getBlock(pos.offset(i, -2).up()) != Blocks.REDSTONE_BLOCK || ((Direction)this.getBlockState(pos).get((Property)FacingBlock.FACING)).getOpposite() != i || !this.doPower(pos)) continue;
                return true;
            }
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP) continue;
                pos = targetPos.offset(i).up();
                if ((AutoPush.mc.player.getY() - target.getY() <= -1.0 || AutoPush.mc.player.getY() - target.getY() >= 2.0) && BlockUtil.distanceToXZ((double)pos.getX() + 0.5, (double)pos.getZ() + 0.5) < 2.6) continue;
                this.attackCrystal(pos);
                if (!this.isTrueFacing(pos, i) || !BlockUtil.clientCanPlace(pos, false) || this.getBlockState(pos.offset(i, -2)).blocksMovement() || this.getBlockState(pos.offset(i, -2).up()).blocksMovement()) continue;
                if (BlockUtil.getPlaceSide(pos) == null && this.downPower(pos)) break;
                this.doPiston(i, pos, this.mine.getValue());
                return true;
            }
            if (this.getBlock(targetPos) == Blocks.AIR && this.onlyBurrow.getValue() || !this.pullBack.getValue()) {
                if (this.autoDisable.getValue()) {
                    this.disable();
                }
                return true;
            }
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP) continue;
                pos = targetPos.offset(i).up();
                for (Direction i2 : Direction.values()) {
                    if (!(this.getBlock(pos) instanceof PistonBlock) || this.getBlock(pos.offset(i2)) != Blocks.REDSTONE_BLOCK || ((Direction)this.getBlockState(pos).get((Property)FacingBlock.FACING)).getOpposite() != i) continue;
                    this.mine(pos.offset(i2));
                    if (this.autoDisable.getValue()) {
                        this.disable();
                    }
                    return true;
                }
            }
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP) continue;
                pos = targetPos.offset(i).up();
                for (Direction i2 : Direction.values()) {
                    if (!(this.getBlock(pos) instanceof PistonBlock) || this.getBlock(pos.offset(i2)) != Blocks.AIR || ((Direction)this.getBlockState(pos).get((Property)FacingBlock.FACING)).getOpposite() != i) continue;
                    this.attackCrystal(pos.offset(i2));
                    if (this.doPower(pos, i2)) continue;
                    this.mine(pos.offset(i2));
                    return true;
                }
            }
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP) continue;
                pos = targetPos.offset(i).up();
                if ((AutoPush.mc.player.getY() - target.getY() <= -1.0 || AutoPush.mc.player.getY() - target.getY() >= 2.0) && BlockUtil.distanceToXZ((double)pos.getX() + 0.5, (double)pos.getZ() + 0.5) < 2.6) continue;
                this.attackCrystal(pos);
                if (!this.isTrueFacing(pos, i) || !BlockUtil.clientCanPlace(pos, false) || this.downPower(pos)) continue;
                this.doPiston(i, pos, true);
                return true;
            }
        } else {
            BlockPos pos;
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP || !(this.getBlock(pos = targetPos.offset(i).up()) instanceof PistonBlock) || (!AutoPush.mc.world.isAir(pos.offset(i, -2)) || !AutoPush.mc.world.isAir(pos.offset(i, -2).down())) && !AutoPush.isTargetHere(pos.offset(i, 2), (Entity)target) || ((Direction)this.getBlockState(pos).get((Property)FacingBlock.FACING)).getOpposite() != i) continue;
                for (Direction i2 : Direction.values()) {
                    if (this.getBlock(pos.offset(i2)) != Blocks.REDSTONE_BLOCK) continue;
                    if (this.mine.getValue()) {
                        this.mine(pos.offset(i2));
                    }
                    if (this.autoDisable.getValue()) {
                        this.disable();
                    }
                    return true;
                }
            }
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP || !(this.getBlock(pos = targetPos.offset(i).up()) instanceof PistonBlock) || (!AutoPush.mc.world.isAir(pos.offset(i, -2)) || !AutoPush.mc.world.isAir(pos.offset(i, -2).down())) && !AutoPush.isTargetHere(pos.offset(i, 2), (Entity)target) || ((Direction)this.getBlockState(pos).get((Property)FacingBlock.FACING)).getOpposite() != i || !this.doPower(pos)) continue;
                return true;
            }
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP) continue;
                pos = targetPos.offset(i).up();
                if ((AutoPush.mc.player.getY() - target.getY() <= -1.0 || AutoPush.mc.player.getY() - target.getY() >= 2.0) && BlockUtil.distanceToXZ((double)pos.getX() + 0.5, (double)pos.getZ() + 0.5) < 2.6) continue;
                this.attackCrystal(pos);
                if (!this.isTrueFacing(pos, i) || !BlockUtil.clientCanPlace(pos, false) || (!AutoPush.mc.world.isAir(pos.offset(i, -2)) || !AutoPush.mc.world.isAir(pos.offset(i, -2).down())) && !AutoPush.isTargetHere(pos.offset(i, 2), (Entity)target) || this.getBlockState(pos.offset(i, -2).up()).blocksMovement()) continue;
                if (BlockUtil.getPlaceSide(pos) != null || !this.downPower(pos)) {
                    this.doPiston(i, pos, this.mine.getValue());
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private boolean isTrueFacing(BlockPos pos, Direction facing) {
        Vec3d hitVec;
        if (this.yawDeceive.getValue()) {
            return true;
        }
        Direction side = BlockUtil.getPlaceSide(pos);
        if (side == null) {
            side = Direction.UP;
        }
        return Direction.fromRotation((double)EntityUtil.getLegitRotations(hitVec = pos.offset((side = side.getOpposite()).getOpposite()).toCenterPos().add(new Vec3d((double)side.getVector().getX() * 0.5, (double)side.getVector().getY() * 0.5, (double)side.getVector().getZ() * 0.5)))[0]) == facing;
    }

    private boolean doPower(BlockPos pos, Direction i2) {
        if (!BlockUtil.canPlace(pos.offset(i2), this.placeRange.getValue())) {
            return true;
        }
        int old = AutoPush.mc.player.getInventory().selectedSlot;
        int power = this.findBlock(Blocks.REDSTONE_BLOCK);
        this.doSwap(power);
        BlockUtil.placeBlock(pos.offset(i2), this.rotate.getValue(), this.redStonePacket.getValue());
        PlaceRender.addBlock(pos.offset(i2));
        if (this.inventory.getValue() && power < 36 && power > 8) {
            this.doSwap(power);
            EntityUtil.sync();
        } else {
            this.doSwap(old);
        }
        return false;
    }

    private boolean doPower(BlockPos pos) {
        Direction facing = BlockUtil.getBestNeighboring(pos, null);
        if (facing != null) {
            this.attackCrystal(pos.offset(facing));
            if (!this.doPower(pos, facing)) {
                return true;
            }
        }
        for (Direction i2 : Direction.values()) {
            this.attackCrystal(pos.offset(i2));
            if (this.doPower(pos, i2)) continue;
            return true;
        }
        return false;
    }

    private boolean downPower(BlockPos pos) {
        if (BlockUtil.getPlaceSide(pos) == null) {
            boolean noPower = true;
            for (Direction i2 : Direction.values()) {
                if (this.getBlock(pos.offset(i2)) != Blocks.REDSTONE_BLOCK) continue;
                noPower = false;
                break;
            }
            if (noPower) {
                if (!BlockUtil.canPlace(pos.add(0, -1, 0), this.placeRange.getValue())) {
                    return true;
                }
                int old = AutoPush.mc.player.getInventory().selectedSlot;
                int power = this.findBlock(Blocks.REDSTONE_BLOCK);
                if (this.noMine.getValue() && pos.add(0, -1, 0).equals((Object)PacketMine.breakPos)) {
                    PacketMine.breakPos = null;
                }
                this.doSwap(power);
                BlockUtil.placeBlock(pos.add(0, -1, 0), this.rotate.getValue(), this.redStonePacket.getValue());
                PlaceRender.addBlock(pos.add(0, -1, 0));
                if (this.inventory.getValue() && power < 36 && power > 8) {
                    this.doSwap(power);
                    EntityUtil.sync();
                } else {
                    this.doSwap(old);
                }
            }
        }
        return false;
    }

    private void doPiston(Direction i, BlockPos pos, boolean mine) {
        if (BlockUtil.canPlace(pos, this.placeRange.getValue())) {
            int piston = this.findClass(PistonBlock.class);
            Direction side = BlockUtil.getPlaceSide(pos);
            if (this.rotate.getValue()) {
                EntityUtil.facePosSide(pos.offset(side), side.getOpposite());
            }
            if (this.yawDeceive.getValue()) {
                AutoPush.pistonFacing(i);
            }
            int old = AutoPush.mc.player.getInventory().selectedSlot;
            this.doSwap(piston);
            BlockUtil.placeBlock(pos, false, this.pistonPacket.getValue());
            PlaceRender.addBlock(pos);
            if (this.inventory.getValue() && piston < 36 && piston > 8) {
                this.doSwap(piston);
                EntityUtil.sync();
            } else {
                this.doSwap(old);
            }
            if (this.rotate.getValue()) {
                EntityUtil.facePosSide(pos.offset(side), side.getOpposite());
            }
            for (Direction i2 : Direction.values()) {
                if (this.getBlock(pos.offset(i2)) != Blocks.REDSTONE_BLOCK) continue;
                if (mine) {
                    this.mine(pos.offset(i2));
                }
                if (this.autoDisable.getValue()) {
                    this.disable();
                }
                return;
            }
            this.doPower(pos);
        }
    }

    @Override
    public String getInfo() {
        if (this.displayTarget != null) {
            return this.displayTarget.getName().getString();
        }
        return null;
    }

    private void doSwap(int slot) {
        if (this.inventory.getValue() && slot < 36 && slot > 8) {
            if (this.invmode.getValue() == InvMode.Inv) {
                AutoPush.mc.interactionManager.clickSlot(AutoPush.mc.player.currentScreenHandler.syncId, slot, AutoPush.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AutoPush.mc.player);
            } else {
                mc.getNetworkHandler().sendPacket((Packet)new PickFromInventoryC2SPacket(slot));
            }
        } else if (slot < 9) {
            InventoryUtil.doSwap(slot);
        } else {
            InventoryUtil.doSwap(slot - 36);
        }
    }

    public int findBlock(Block blockIn) {
        if (this.inventory.getValue()) {
            return InventoryUtil.findBlockInventorySlot(blockIn);
        }
        return InventoryUtil.findBlock(blockIn);
    }

    public int findClass(Class clazz) {
        if (this.inventory.getValue()) {
            return InventoryUtil.findClassInventorySlot(clazz);
        }
        return InventoryUtil.findClass(clazz);
    }

    private void attackCrystal(BlockPos pos) {
        if (!this.attackCrystal.getValue()) {
            return;
        }
        for (Entity crystal : AutoPush.mc.world.getEntities()) {
            if (!(crystal instanceof EndCrystalEntity) || (double)MathHelper.sqrt((float)((float)crystal.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5))) > 2.0) continue;
            CombatUtil.attackCrystal(crystal, this.rotate.getValue(), false);
            return;
        }
    }

    private void mine(BlockPos pos) {
        PacketMine.INSTANCE.mine(pos);
    }

    private Block getBlock(BlockPos pos) {
        return AutoPush.mc.world.getBlockState(pos).getBlock();
    }

    private BlockState getBlockState(BlockPos pos) {
        return AutoPush.mc.world.getBlockState(pos);
    }

    private Boolean canPush(PlayerEntity player) {
        int progress = 0;
        if (!AutoPush.mc.world.isAir((BlockPos)new BlockPosX(player.getX() + 1.0, player.getY() + 0.5, player.getZ()))) {
            ++progress;
        }
        if (!AutoPush.mc.world.isAir((BlockPos)new BlockPosX(player.getX() - 1.0, player.getY() + 0.5, player.getZ()))) {
            ++progress;
        }
        if (!AutoPush.mc.world.isAir((BlockPos)new BlockPosX(player.getX(), player.getY() + 0.5, player.getZ() + 1.0))) {
            ++progress;
        }
        if (!AutoPush.mc.world.isAir((BlockPos)new BlockPosX(player.getX(), player.getY() + 0.5, player.getZ() - 1.0))) {
            ++progress;
        }
        if (!AutoPush.mc.world.isAir((BlockPos)new BlockPosX(player.getX(), player.getY() + 2.5, player.getZ()))) {
            for (Direction i : Direction.values()) {
                BlockPos pos;
                if (i == Direction.UP || i == Direction.DOWN || (!AutoPush.mc.world.isAir(pos = EntityUtil.getEntityPos((Entity)player).offset(i)) || !AutoPush.mc.world.isAir(pos.up())) && !AutoPush.isTargetHere(pos, (Entity)player)) continue;
                if (!AutoPush.mc.world.isAir((BlockPos)new BlockPosX(player.getX(), player.getY() + 0.5, player.getZ()))) {
                    return true;
                }
                return (double)progress > this.surroundCheck.getValue() - 1.0;
            }
            return false;
        }
        if (!AutoPush.mc.world.isAir((BlockPos)new BlockPosX(player.getX(), player.getY() + 0.5, player.getZ()))) {
            return true;
        }
        return (double)progress > this.surroundCheck.getValue() - 1.0;
    }

    private static enum InvMode {
        Inv,
        Pick;


        private static AutoPush.InvMode[] $values() {
            return new AutoPush.InvMode[]{Inv, Pick};
        }
    }
}
