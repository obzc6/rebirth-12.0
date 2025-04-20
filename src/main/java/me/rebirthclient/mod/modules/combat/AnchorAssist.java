/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 */
package me.rebirthclient.mod.modules.combat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoAnchor;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AnchorAssist
extends Module {
    public static AnchorAssist INSTANCE;
    private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
    private final BooleanSetting force = new BooleanSetting("Force", true);
    private final BooleanSetting usingPause = new BooleanSetting("UsingPause", true);
    private final BooleanSetting checkMine = new BooleanSetting("CheckMine", false);
    private final SliderSetting range = new SliderSetting("TargetRange", 5.0, 0.0, 6.0, 0.1);
    private final SliderSetting minDamage = new SliderSetting("MinDamage", 6.0, 0.0, 36.0, 0.1);
    private final SliderSetting delay = new SliderSetting("Delay", 0.0, 0.0, 0.5, 0.01);
    private final Timer timer = new Timer().reset();

    public AnchorAssist() {
        super("AnchorAssist", Module.Category.Combat);
        INSTANCE = this;
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType())) continue;
                Setting setting = (Setting)field.get(this);
                this.addSetting(setting);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void onUpdate() {
        BlockPos placePos;
        double bestDamage;
        int anchor = InventoryUtil.findBlock(Blocks.RESPAWN_ANCHOR);
        int glowstone = InventoryUtil.findBlock(Blocks.GLOWSTONE);
        int old = AnchorAssist.mc.player.getInventory().selectedSlot;
        if (anchor == -1) {
            return;
        }
        if (glowstone == -1) {
            return;
        }
        if (AnchorAssist.mc.player.isSneaking()) {
            return;
        }
        if (this.usingPause.getValue() && AnchorAssist.mc.player.isUsingItem()) {
            return;
        }
        if (!this.timer.passed((long)(this.delay.getValueFloat() * 1000.0f))) {
            return;
        }
        this.timer.reset();
        double bestAnchorDamage = bestDamage = AutoAnchor.INSTANCE.minDamage.getValue();
        boolean anchorFound = false;
        ArrayList<AutoAnchor.PlayerAndPredict> list = new ArrayList<AutoAnchor.PlayerAndPredict>();
        for (PlayerEntity player : CombatUtil.getEnemies(this.range.getValue())) {
            list.add(new AutoAnchor.PlayerAndPredict(player));
        }
        if (!this.force.getValue()) {
            for (BlockPos pos : BlockUtil.getSphere(AutoAnchor.INSTANCE.range.getValueFloat())) {
                for (AutoAnchor.PlayerAndPredict pap : list) {
                    double damage;
                    if (BlockUtil.getBlock(pos) != Blocks.RESPAWN_ANCHOR) {
                        if (anchorFound || !BlockUtil.canPlace(pos, AutoAnchor.INSTANCE.range.getValue()) || !((damage = AutoAnchor.INSTANCE.getAnchorDamage(pos, pap.player, pap.predict)) >= bestDamage)) continue;
                        bestDamage = damage;
                        continue;
                    }
                    damage = AutoAnchor.INSTANCE.getAnchorDamage(pos, pap.player, pap.predict);
                    if (!(damage >= bestAnchorDamage)) continue;
                    anchorFound = true;
                    bestAnchorDamage = damage;
                    bestDamage = damage;
                }
            }
            if (bestDamage >= this.minDamage.getValue()) {
                return;
            }
        }
        bestDamage = this.minDamage.getValue();
        BlockPos foundPos = null;
        for (AutoAnchor.PlayerAndPredict pap : list) {
            double damage;
            BlockPos pos = EntityUtil.getEntityPos((Entity)pap.player, true).up(2);
            if (AnchorAssist.mc.world.getBlockState(pos).getBlock() == Blocks.RESPAWN_ANCHOR) {
                return;
            }
            if (BlockUtil.clientCanPlace(pos, false) && (damage = AutoAnchor.INSTANCE.getAnchorDamage(pos, pap.player, pap.predict)) >= bestDamage) {
                bestDamage = damage;
                foundPos = pos;
            }
            for (Direction i : Direction.values()) {
                double damage2;
                if (i == Direction.UP || i == Direction.DOWN || !BlockUtil.clientCanPlace(pos.offset(i), false) || !((damage2 = AutoAnchor.INSTANCE.getAnchorDamage(pos.offset(i), pap.player, pap.predict)) >= bestDamage)) continue;
                bestDamage = damage2;
                foundPos = pos.offset(i);
            }
        }
        if (foundPos != null && BlockUtil.getPlaceSide(foundPos, AutoAnchor.INSTANCE.range.getValue()) == null && (placePos = this.getHelper(foundPos)) != null) {
            InventoryUtil.doSwap(anchor);
            BlockUtil.placeBlock(placePos, this.rotate.getValue());
            InventoryUtil.doSwap(old);
        }
    }

    public BlockPos getHelper(BlockPos pos) {
        for (Direction i : Direction.values()) {
            if (this.checkMine.getValue() && BlockUtil.isMining(pos.offset(i)) || !BlockUtil.isStrictDirection(pos.offset(i), i.getOpposite(), true) || !BlockUtil.canPlace(pos.offset(i))) continue;
            return pos.offset(i);
        }
        return null;
    }
}

