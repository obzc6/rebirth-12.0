/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.PistonBlock
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.render.PlaceRender;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AntiPiston
extends Module {
    public static AntiPiston INSTANCE;
    public final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true));
    public final BooleanSetting packet = this.add(new BooleanSetting("Packet", true));
    public final BooleanSetting helper = this.add(new BooleanSetting("Helper", true));
    public final BooleanSetting trap = this.add(new BooleanSetting("Trap", true).setParent());
    private final BooleanSetting onlyBurrow = this.add(new BooleanSetting("OnlyBurrow", true, v -> this.trap.isOpen()).setParent());
    private final BooleanSetting whenDouble = this.add(new BooleanSetting("WhenDouble", true, v -> this.onlyBurrow.isOpen()));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true));

    public AntiPiston() {
        super("AntiPiston", "Trap self when piston kick", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (!AntiPiston.mc.player.isOnGround()) {
            return;
        }
        this.block();
    }

    private void block() {
        BlockPos pos = EntityUtil.getPlayerPos();
        if (this.getBlock(pos.up(2)) == Blocks.OBSIDIAN || this.getBlock(pos.up(2)) == Blocks.BEDROCK) {
            return;
        }
        int progress = 0;
        if (this.whenDouble.getValue()) {
            for (Direction i : Direction.values()) {
                if (i == Direction.DOWN || i == Direction.UP || !(this.getBlock(pos.offset(i).up()) instanceof PistonBlock) || ((Direction)AntiPiston.mc.world.getBlockState(pos.offset(i).up()).get((Property)FacingBlock.FACING)).getOpposite() != i) continue;
                ++progress;
            }
        }
        for (Direction i : Direction.values()) {
            if (i == Direction.DOWN || i == Direction.UP || !(this.getBlock(pos.offset(i).up()) instanceof PistonBlock) || ((Direction)AntiPiston.mc.world.getBlockState(pos.offset(i).up()).get((Property)FacingBlock.FACING)).getOpposite() != i) continue;
            this.placeBlock(pos.up().offset(i, -1));
            if (this.trap.getValue() && (this.getBlock(pos) != Blocks.AIR || !this.onlyBurrow.getValue() || progress >= 2)) {
                this.placeBlock(pos.up(2));
                if (!BlockUtil.canPlaceEnum(pos.up(2))) {
                    for (Direction i2 : Direction.values()) {
                        if (!AntiPiston.canPlace(pos.offset(i2).up(2))) continue;
                        this.placeBlock(pos.offset(i2).up(2));
                        break;
                    }
                }
            }
            if (BlockUtil.canPlaceEnum(pos.up().offset(i, -1)) || !this.helper.getValue()) continue;
            if (BlockUtil.canPlaceEnum(pos.offset(i, -1))) {
                this.placeBlock(pos.offset(i, -1));
                continue;
            }
            this.placeBlock(pos.offset(i, -1).down());
        }
    }

    private Block getBlock(BlockPos block) {
        return AntiPiston.mc.world.getBlockState(block).getBlock();
    }

    private void placeBlock(BlockPos pos) {
        if (!AntiPiston.canPlace(pos)) {
            return;
        }
        int old = AntiPiston.mc.player.getInventory().selectedSlot;
        int block = this.findBlock(Blocks.OBSIDIAN);
        if (block == -1) {
            return;
        }
        this.doSwap(block);
        BlockUtil.placeBlock(pos, this.rotate.getValue(), this.packet.getValue());
        PlaceRender.addBlock(pos);
        if (this.inventory.getValue()) {
            this.doSwap(block);
            EntityUtil.sync();
        } else {
            this.doSwap(old);
        }
    }

    public static boolean canPlace(BlockPos pos) {
        if (!BlockUtil.canBlockFacing(pos)) {
            return false;
        }
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        return !BlockUtil.hasEntity(pos, false);
    }

    public int findBlock(Block blockIn) {
        if (this.inventory.getValue()) {
            return InventoryUtil.findBlockInventorySlot(blockIn);
        }
        return InventoryUtil.findBlock(blockIn);
    }

    private void doSwap(int slot) {
        if (this.inventory.getValue()) {
            AntiPiston.mc.interactionManager.clickSlot(AntiPiston.mc.player.currentScreenHandler.syncId, slot, AntiPiston.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AntiPiston.mc.player);
        } else {
            InventoryUtil.doSwap(slot);
        }
    }
}

