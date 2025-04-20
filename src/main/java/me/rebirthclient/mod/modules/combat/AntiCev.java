/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 */
package me.rebirthclient.mod.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.render.PlaceRender;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class AntiCev
extends Module {
    public static AntiCev INSTANCE = new AntiCev();
    final Timer timer = new Timer();
    private final SliderSetting delay = this.add(new SliderSetting("Delay", 50, 0, 500));
    private final SliderSetting multiPlace = this.add(new SliderSetting("MultiPlace", 1, 1, 8));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true));
    private final BooleanSetting onlyGround = this.add(new BooleanSetting("OnlyGround", true));
    private final BooleanSetting breakCrystal = this.add(new BooleanSetting("BreakCrystal", true));
    private final BooleanSetting checkMine = this.add(new BooleanSetting("CheckMine", true));
    private final BooleanSetting eatingPause = this.add(new BooleanSetting("EatingPause", true));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true));
    int progress = 0;
    private final List<BlockPos> crystalPos = new ArrayList<BlockPos>();
    private BlockPos pos;

    public AntiCev() {
        super("AntiCev", "Anti cev", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (!this.timer.passedMs(this.delay.getValue())) {
            return;
        }
        if (this.eatingPause.getValue() && EntityUtil.isUsing()) {
            return;
        }
        this.progress = 0;
        if (this.pos != null && !this.pos.equals((Object)EntityUtil.getPlayerPos(true))) {
            this.crystalPos.clear();
        }
        this.pos = EntityUtil.getPlayerPos(true);
        for (Direction i : Direction.values()) {
            BlockPos offsetPos;
            if (i == Direction.DOWN || this.isGod(this.pos.offset(i).up()) || !this.crystalHere(offsetPos = this.pos.offset(i).up(2)) || this.crystalPos.contains((Object)offsetPos)) continue;
            this.crystalPos.add(offsetPos);
        }
        if (this.getBlock() == -1) {
            return;
        }
        if (this.onlyGround.getValue() && !AntiCev.mc.player.isOnGround()) {
            return;
        }
        this.crystalPos.removeIf(pos -> !BlockUtil.clientCanPlace(pos, true));
        for (BlockPos defensePos : this.crystalPos) {
            if (this.crystalHere(defensePos) && this.breakCrystal.getValue()) {
                CombatUtil.attackCrystal(defensePos, this.rotate.getValue(), false);
            }
            if (!BlockUtil.canPlace(defensePos, 6.0, this.breakCrystal.getValue())) continue;
            this.placeBlock(defensePos);
        }
    }

    private boolean crystalHere(BlockPos pos) {
        for (Entity entity : AntiCev.mc.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(pos))) {
            if (!EntityUtil.getEntityPos(entity).equals((Object)pos)) continue;
            return true;
        }
        return false;
    }

    private boolean isGod(BlockPos pos) {
        return AntiCev.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK;
    }

    private void placeBlock(BlockPos pos) {
        if (!((double)this.progress < this.multiPlace.getValue())) {
            return;
        }
        if (this.checkMine.getValue() && BlockUtil.isMining(pos)) {
            return;
        }
        int oldSlot = AntiCev.mc.player.getInventory().selectedSlot;
        int block = this.getBlock();
        if (block == -1) {
            return;
        }
        this.doSwap(block);
        BlockUtil.placeBlock(pos, this.rotate.getValue());
        PlaceRender.addBlock(pos);
        if (this.inventory.getValue()) {
            this.doSwap(block);
            EntityUtil.sync();
        } else {
            this.doSwap(oldSlot);
        }
        ++this.progress;
        this.timer.reset();
    }

    private void doSwap(int slot) {
        if (this.inventory.getValue()) {
            AntiCev.mc.interactionManager.clickSlot(AntiCev.mc.player.currentScreenHandler.syncId, slot, AntiCev.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AntiCev.mc.player);
        } else {
            InventoryUtil.doSwap(slot);
        }
    }

    private int getBlock() {
        if (this.inventory.getValue()) {
            return InventoryUtil.findBlockInventorySlot(Blocks.OBSIDIAN);
        }
        return InventoryUtil.findBlock(Blocks.OBSIDIAN);
    }
}

