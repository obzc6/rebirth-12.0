/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.PistonBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.DamageUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.modules.render.PlaceRender;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class PullCrystal
extends Module {
    private final SliderSetting range = this.add(new SliderSetting("Range", 5.0, 1.0, 8.0));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true));
    private final BooleanSetting fire = this.add(new BooleanSetting("Fire", true));
    public final BooleanSetting checkMine = this.add(new BooleanSetting("CheckMine", true));
    private final BooleanSetting noEating = this.add(new BooleanSetting("NoEating", true));
    private final BooleanSetting multiPlace = this.add(new BooleanSetting("MultiPlace", false));
    private final BooleanSetting onlyGround = this.add(new BooleanSetting("NoAir", true));
    private final BooleanSetting onlyStatic = this.add(new BooleanSetting("NoMoving", true));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true));
    private final SliderSetting updateDelay = this.add(new SliderSetting("UpdateDelay", 100, 0, 500));
    private final SliderSetting pistonDelay = this.add(new SliderSetting("PistonDelay", 200, 0, 500));
    private PlayerEntity target = null;
    public static PullCrystal INSTANCE;
    private final Timer timer = new Timer();
    public static BlockPos crystalPos;
    public static BlockPos powerPos;

    public PullCrystal() {
        super("PullCrystal", "use piston pull crystal and boom", Module.Category.Combat);
        INSTANCE = this;
    }

    private void doSwap(int slot) {
        if (this.inventory.getValue()) {
            PullCrystal.mc.interactionManager.clickSlot(PullCrystal.mc.player.currentScreenHandler.syncId, slot, PullCrystal.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)PullCrystal.mc.player);
        } else {
            InventoryUtil.doSwap(slot);
        }
    }

    public int findItem(Item itemIn) {
        if (this.inventory.getValue()) {
            return InventoryUtil.findItemInventorySlot(itemIn);
        }
        return InventoryUtil.findItem(itemIn);
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

    @Override
    public void onUpdate() {
        if (!this.timer.passedMs((long)this.updateDelay.getValue())) {
            return;
        }
        if (this.noEating.getValue() && EntityUtil.isUsing()) {
            this.target = null;
            return;
        }
        if (this.check(this.onlyStatic.getValue(), !PullCrystal.mc.player.isOnGround(), this.onlyGround.getValue())) {
            this.target = null;
            return;
        }
        this.target = CombatUtil.getClosestEnemy(this.range.getValue());
        if (this.target == null) {
            return;
        }
        BlockPos pos = EntityUtil.getEntityPos((Entity)this.target);
        if (this.breakCrystal(pos.up(0))) {
            this.timer.reset();
            return;
        }
        if (this.breakCrystal(pos.up(1))) {
            this.timer.reset();
            return;
        }
        if (this.breakCrystal(pos.up(2))) {
            this.timer.reset();
            return;
        }
        if (this.breakCrystal(pos.up(3))) {
            this.timer.reset();
            return;
        }
        if (this.doPullCrystal(pos)) {
            this.timer.reset();
            return;
        }
        if (this.doPullCrystal(new BlockPosX(this.target.getX() + 0.1, this.target.getY() + 0.5, this.target.getZ() + 0.1))) {
            this.timer.reset();
            return;
        }
        if (this.doPullCrystal(new BlockPosX(this.target.getX() - 0.1, this.target.getY() + 0.5, this.target.getZ() + 0.1))) {
            this.timer.reset();
            return;
        }
        if (this.doPullCrystal(new BlockPosX(this.target.getX() + 0.1, this.target.getY() + 0.5, this.target.getZ() - 0.1))) {
            this.timer.reset();
            return;
        }
        if (this.doPullCrystal(new BlockPosX(this.target.getX() - 0.1, this.target.getY() + 0.5, this.target.getZ() - 0.1))) {
            this.timer.reset();
            return;
        }
    }

    private boolean doPullCrystal(BlockPos pos) {
        if (this.pull(pos.up(2))) {
            return true;
        }
        if (this.pull(pos.up())) {
            return true;
        }
        if (this.power(pos.up(2))) {
            return true;
        }
        if (this.power(pos.up())) {
            return true;
        }
        if (this.timer.passedMs(this.pistonDelay.getValueInt())) {
            if (this.piston(pos.up(2))) {
                return true;
            }
            return this.piston(pos.up());
        }
        return false;
    }

    public boolean check(boolean onlyStatic, boolean onGround, boolean onlyGround) {
        if (MovementUtil.isMoving() && onlyStatic) {
            return true;
        }
        if (onGround && onlyGround) {
            return true;
        }
        if (this.findBlock(Blocks.REDSTONE_BLOCK) == -1) {
            return true;
        }
        if (this.findClass(PistonBlock.class) == -1) {
            return true;
        }
        return this.findItem(Items.END_CRYSTAL) == -1;
    }

    private static boolean canFire(BlockPos pos) {
        if (BlockUtil.canReplace(pos.down()) || !BlockUtil.canClick(pos.down())) {
            return false;
        }
        return PullCrystal.mc.world.isAir(pos);
    }

    private void doFire(BlockPos pos, Direction facing) {
        if (!this.fire.getValue()) {
            return;
        }
        int fire = this.findItem(Items.FLINT_AND_STEEL);
        if (fire == -1) {
            return;
        }
        int old = PullCrystal.mc.player.getInventory().selectedSlot;
        int[] xOffset = new int[]{0, facing.getOffsetZ(), -facing.getOffsetZ()};
        int[] yOffset = new int[]{0, 1};
        int[] zOffset = new int[]{0, facing.getOffsetX(), -facing.getOffsetX()};
        for (int x : xOffset) {
            for (int y : yOffset) {
                for (int z : zOffset) {
                    if (BlockUtil.getBlock(pos.add(x, y, z)) != Blocks.FIRE) continue;
                    return;
                }
            }
        }
        for (int x : xOffset) {
            for (int y : yOffset) {
                for (int z : zOffset) {
                    if (!PullCrystal.canFire(pos.add(x, y, z))) continue;
                    this.doSwap(this.findItem(Items.FLINT_AND_STEEL));
                    this.placeFire(pos.add(x, y, z));
                    if (this.inventory.getValue()) {
                        this.doSwap(fire);
                        EntityUtil.sync();
                    } else {
                        this.doSwap(old);
                    }
                    return;
                }
            }
        }
    }

    private void placeFire(BlockPos pos) {
        BlockPos neighbour = pos.offset(Direction.DOWN);
        BlockUtil.clickBlock(neighbour, Direction.UP, this.rotate.getValue());
        PlaceRender.addBlock(neighbour);
    }

    @Override
    public String getInfo() {
        if (this.target != null) {
            return this.target.getName().getString();
        }
        return null;
    }

    private boolean pistonActive(BlockPos pos, Direction facing, BlockPos oPos) {
        if (this.pistonActive(pos, facing, oPos, false)) {
            return true;
        }
        return this.pistonActive(pos, facing, oPos, true);
    }

    private boolean pistonActive(BlockPos pos, Direction facing, BlockPos oPos, boolean up) {
        if (up) {
            pos = pos.up();
        }
        if (!BlockUtil.canPlaceCrystal(oPos.offset(facing, -1)) && !BlockUtil.hasCrystal(oPos.offset(facing, -1))) {
            return false;
        }
        if (!(BlockUtil.getBlock(pos) instanceof PistonBlock)) {
            return false;
        }
        if (((Direction)BlockUtil.getState(pos).get((Property)FacingBlock.FACING)).getOpposite() != facing) {
            return false;
        }
        if (BlockUtil.getBlock(pos.offset(facing, -1)) == Blocks.MOVING_PISTON) {
            return true;
        }
        if (BlockUtil.getBlock(pos.offset(facing, -1)) != Blocks.PISTON_HEAD) {
            return false;
        }
        for (Direction i : Direction.values()) {
            if (BlockUtil.getBlock(pos.offset(i)) != Blocks.REDSTONE_BLOCK) continue;
            if (!BlockUtil.hasCrystal(oPos.offset(facing, -1))) {
                int old = PullCrystal.mc.player.getInventory().selectedSlot;
                crystalPos = oPos.offset(facing, -1);
                int crystal = this.findItem(Items.END_CRYSTAL);
                this.doSwap(crystal);
                BlockUtil.placeCrystal(oPos.offset(facing, -1), this.rotate.getValue());
                if (this.inventory.getValue()) {
                    this.doSwap(crystal);
                    EntityUtil.sync();
                } else {
                    this.doSwap(old);
                }
            }
            this.doFire(oPos, facing);
            powerPos = pos.offset(i);
            PacketMine.INSTANCE.mine(pos.offset(i));
            return true;
        }
        return false;
    }

    private boolean power(BlockPos pos) {
        for (Direction i : Direction.values()) {
            if (i == Direction.DOWN || i == Direction.UP) continue;
            int offsetX = pos.offset(i).getX() - pos.getX();
            int offsetZ = pos.offset(i).getZ() - pos.getZ();
            if (this.placePower(pos.offset(i, -2), i, pos)) {
                return true;
            }
            if (this.placePower(pos.offset(i, -2).add(-offsetZ, 0, -offsetX), i, pos)) {
                return true;
            }
            if (this.placePower(pos.offset(i, -2).add(offsetZ, 0, offsetX), i, pos)) {
                return true;
            }
            if (this.placePower(pos.offset(i, 1).add(offsetZ, 0, offsetX), i, pos)) {
                return true;
            }
            if (this.placePower(pos.offset(i, 1).add(-offsetZ, 0, -offsetX), i, pos)) {
                return true;
            }
            if (this.placePower(pos.offset(i, -1).add(offsetZ, 0, offsetX), i, pos)) {
                return true;
            }
            if (!this.placePower(pos.offset(i, -1).add(-offsetZ, 0, -offsetX), i, pos)) continue;
            return true;
        }
        return false;
    }

    private boolean piston(BlockPos pos) {
        for (Direction i : Direction.values()) {
            if (i == Direction.DOWN || i == Direction.UP) continue;
            int offsetX = pos.offset(i).getX() - pos.getX();
            int offsetZ = pos.offset(i).getZ() - pos.getZ();
            if (this.placePiston(pos.offset(i, -2), i, pos)) {
                return true;
            }
            if (this.placePiston(pos.offset(i, -2).add(-offsetZ, 0, -offsetX), i, pos)) {
                return true;
            }
            if (this.placePiston(pos.offset(i, -2).add(-offsetZ, 0, -offsetX), i, pos)) {
                return true;
            }
            if (this.placePiston(pos.offset(i, 1).add(offsetZ, 0, offsetX), i, pos)) {
                return true;
            }
            if (this.placePiston(pos.offset(i, 1).add(-offsetZ, 0, -offsetX), i, pos)) {
                return true;
            }
            if (this.placePiston(pos.offset(i, -1).add(offsetZ, 0, offsetX), i, pos)) {
                return true;
            }
            if (!this.placePiston(pos.offset(i, -1).add(-offsetZ, 0, -offsetX), i, pos)) continue;
            return true;
        }
        return false;
    }

    private boolean breakCrystal(BlockPos pos) {
        for (EndCrystalEntity entity : PullCrystal.mc.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(pos))) {
            float damage = DamageUtil.calculateDamage(entity.getPos(), this.target, this.target, 6.0f);
            if (!(damage > 6.0f)) continue;
            CombatUtil.attackCrystal((Entity)entity, this.rotate.getValue(), true);
            return true;
        }
        return false;
    }

    private boolean placePower(BlockPos pos, Direction facing, BlockPos oPos) {
        if (this.placePower(pos, facing, oPos, false)) {
            return true;
        }
        return this.placePower(pos, facing, oPos, true);
    }

    private boolean placePower(BlockPos pos, Direction facing, BlockPos oPos, boolean up) {
        if (up) {
            pos = pos.up();
        }
        if (!BlockUtil.canPlaceCrystal(oPos.offset(facing, -1)) && !BlockUtil.hasCrystal(oPos.offset(facing, -1))) {
            return false;
        }
        if (!(BlockUtil.getBlock(pos) instanceof PistonBlock)) {
            return false;
        }
        if (((Direction)BlockUtil.getState(pos).get((Property)FacingBlock.FACING)).getOpposite() != facing) {
            return false;
        }
        if (BlockUtil.getBlock(pos.offset(facing, -1)) == Blocks.PISTON_HEAD || BlockUtil.getBlock(pos.offset(facing, -1)) == Blocks.MOVING_PISTON) {
            return true;
        }
        if (!PullCrystal.mc.world.isAir(pos.offset(facing, -1)) && BlockUtil.getBlock(pos.offset(facing, -1)) != Blocks.PISTON_HEAD && BlockUtil.getBlock(pos.offset(facing, -1)) != Blocks.MOVING_PISTON && BlockUtil.getBlock(pos.offset(facing, -1)) != Blocks.FIRE) {
            return false;
        }
        int old = PullCrystal.mc.player.getInventory().selectedSlot;
        return this.placeRedStone(pos, facing, old, oPos);
    }

    private boolean placePiston(BlockPos pos, Direction facing, BlockPos oPos) {
        if (this.placePiston(pos, facing, oPos, false)) {
            return true;
        }
        return this.placePiston(pos, facing, oPos, true);
    }

    private boolean placePiston(BlockPos pos, Direction facing, BlockPos oPos, boolean up) {
        if (up) {
            pos = pos.up();
        }
        if (!BlockUtil.canPlaceCrystal(oPos.offset(facing, -1)) && !BlockUtil.hasCrystal(oPos.offset(facing, -1))) {
            return false;
        }
        if (!this.canPlace(pos) && !(BlockUtil.getBlock(pos) instanceof PistonBlock)) {
            return false;
        }
        if (BlockUtil.getBlock(pos) instanceof PistonBlock && ((Direction)BlockUtil.getState(pos).get((Property)FacingBlock.FACING)).getOpposite() != facing) {
            return false;
        }
        if (BlockUtil.getBlock(pos.offset(facing, -1)) == Blocks.PISTON_HEAD || BlockUtil.getBlock(pos.offset(facing, -1)) == Blocks.MOVING_PISTON) {
            return true;
        }
        if (!PullCrystal.mc.world.isAir(pos.offset(facing, -1)) && BlockUtil.getBlock(pos.offset(facing, -1)) != Blocks.PISTON_HEAD && BlockUtil.getBlock(pos.offset(facing, -1)) != Blocks.MOVING_PISTON) {
            return false;
        }
        if ((PullCrystal.mc.player.getY() - (double)pos.down().getY() <= -1.0 || PullCrystal.mc.player.getY() - (double)pos.down().getY() >= 2.0) && BlockUtil.distanceToXZ((double)pos.getX() + 0.5, (double)pos.getZ() + 0.5) < 2.6) {
            return false;
        }
        int old = PullCrystal.mc.player.getInventory().selectedSlot;
        if (this.canPlace(pos)) {
            Direction side = BlockUtil.getPlaceSide(pos);
            EntityUtil.facePosSide(pos, side);
            PullCrystal.pistonFacing(facing);
            int piston = this.findClass(PistonBlock.class);
            this.doSwap(piston);
            BlockUtil.placeBlock(pos, false);
            PlaceRender.addBlock(pos);
            if (this.inventory.getValue()) {
                this.doSwap(piston);
                EntityUtil.sync();
            } else {
                this.doSwap(old);
            }
            EntityUtil.facePosSide(pos, side);
            if (this.multiPlace.getValue() && this.placeRedStone(pos, facing, old, oPos)) {
                return true;
            }
            return true;
        }
        return this.placeRedStone(pos, facing, old, oPos);
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

    private boolean placeRedStone(BlockPos pos, Direction facing, int old, BlockPos oPos) {
        for (Direction i : Direction.values()) {
            if (BlockUtil.getBlock(pos.offset(i)) != Blocks.REDSTONE_BLOCK) continue;
            powerPos = pos.offset(i);
            return true;
        }
        Direction bestNeighboring = BlockUtil.getBestNeighboring(pos, facing);
        int power = this.findBlock(Blocks.REDSTONE_BLOCK);
        if (bestNeighboring != null && !pos.offset(bestNeighboring).equals((Object)oPos.offset(facing, -1)) && !pos.offset(bestNeighboring).equals((Object)oPos.offset(facing, -1).up()) && BlockUtil.canPlace(powerPos = pos.offset(bestNeighboring))) {
            this.doSwap(power);
            BlockUtil.placeBlock(pos.offset(bestNeighboring), this.rotate.getValue());
            PlaceRender.addBlock(pos.offset(bestNeighboring));
            if (this.inventory.getValue()) {
                this.doSwap(power);
                EntityUtil.sync();
            } else {
                this.doSwap(old);
            }
            return true;
        }
        for (Direction i : Direction.values()) {
            if (pos.offset(i).equals((Object)pos.offset(facing, -1)) || pos.offset(i).equals((Object)oPos.offset(facing, -1)) || pos.offset(i).equals((Object)oPos.offset(facing, -1).up()) || !BlockUtil.canPlace(pos.offset(i))) continue;
            powerPos = pos.offset(i);
            this.doSwap(power);
            BlockUtil.placeBlock(pos.offset(i), true);
            PlaceRender.addBlock(pos.offset(i));
            if (this.inventory.getValue()) {
                this.doSwap(power);
                EntityUtil.sync();
            } else {
                this.doSwap(old);
            }
            return true;
        }
        return false;
    }

    private boolean canPlace(BlockPos pos) {
        if (this.checkMine.getValue() && BlockUtil.isMining(pos)) {
            return false;
        }
        return BlockUtil.canPlace(pos, this.range.getValueFloat());
    }

    private boolean pull(BlockPos pos) {
        for (Direction i : Direction.values()) {
            if (i == Direction.DOWN || i == Direction.UP) continue;
            int offsetX = pos.offset(i).getX() - pos.getX();
            int offsetZ = pos.offset(i).getZ() - pos.getZ();
            if (this.pistonActive(pos.offset(i, -2), i, pos)) {
                return true;
            }
            if (this.pistonActive(pos.offset(i, -2).add(-offsetZ, 0, -offsetX), i, pos)) {
                return true;
            }
            if (this.pistonActive(pos.offset(i, -2).add(offsetZ, 0, offsetX), i, pos)) {
                return true;
            }
            if (this.pistonActive(pos.offset(i, 1).add(offsetZ, 0, offsetX), i, pos)) {
                return true;
            }
            if (this.pistonActive(pos.offset(i, 1).add(-offsetZ, 0, -offsetX), i, pos)) {
                return true;
            }
            if (this.pistonActive(pos.offset(i, -1).add(offsetZ, 0, offsetX), i, pos)) {
                return true;
            }
            if (!this.pistonActive(pos.offset(i, -1).add(-offsetZ, 0, -offsetX), i, pos)) continue;
            return true;
        }
        return false;
    }
}

