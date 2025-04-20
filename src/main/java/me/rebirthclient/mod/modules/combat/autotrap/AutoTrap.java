/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 */
package me.rebirthclient.mod.modules.combat.autotrap;

import java.awt.Color;
import java.util.ArrayList;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.autotrap.ExtraAutoTrap;
import me.rebirthclient.mod.settings.Placement;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class AutoTrap
extends Module {
    final Timer timer = new Timer();
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true));
    private final SliderSetting multiPlace = this.add(new SliderSetting("MultiPlace", 1, 1, 8));
    private final BooleanSetting autoDisable = this.add(new BooleanSetting("AutoDisable", true));
    private final SliderSetting range = this.add(new SliderSetting("Range", 5.0, 1.0, 8.0));
    private final EnumSetting targetMod = this.add(new EnumSetting("TargetMode", TargetMode.Single));
    private final BooleanSetting checkMine = this.add(new BooleanSetting("CheckMine", false));
    private final BooleanSetting helper = this.add(new BooleanSetting("Helper", true));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true));
    private final BooleanSetting extend = this.add(new BooleanSetting("Extend", true));
    private final BooleanSetting antiStep = this.add(new BooleanSetting("AntiStep", false));
    private final BooleanSetting onlyBreak = this.add(new BooleanSetting("OnlyBreak", false, v -> this.antiStep.getValue()));
    private final BooleanSetting head = this.add(new BooleanSetting("Head", true));
    private final BooleanSetting headAnchor = this.add(new BooleanSetting("HeadAnchor", true));
    private final BooleanSetting chestUp = this.add(new BooleanSetting("ChestUp", true));
    private final BooleanSetting onlyBreaking = this.add(new BooleanSetting("OnlyBreaking", false, v -> this.chestUp.getValue()));
    private final BooleanSetting chest = this.add(new BooleanSetting("Chest", true));
    private final BooleanSetting onlyGround = this.add(new BooleanSetting("OnlyGround", false, v -> this.chest.getValue()));
    private final BooleanSetting legs = this.add(new BooleanSetting("Legs", false));
    private final BooleanSetting legAnchor = this.add(new BooleanSetting("LegAnchor", true));
    private final BooleanSetting down = this.add(new BooleanSetting("Down", false));
    private final BooleanSetting onlyHole = this.add(new BooleanSetting("OnlyHole", false));
    private final BooleanSetting breakCrystal = this.add(new BooleanSetting("BreakCrystal", true));
    private final BooleanSetting usingPause = this.add(new BooleanSetting("UsingPause", true));
    public final SliderSetting delay = this.add(new SliderSetting("Delay", 100, 0, 500));
    private final SliderSetting placeRange = this.add(new SliderSetting("PlaceRange", 4.0, 1.0, 6.0));
    private final BooleanSetting selfGround = this.add(new BooleanSetting("SelfGround", true));
    public final BooleanSetting render = this.add(new BooleanSetting("Render", true));
    public final BooleanSetting box = this.add(new BooleanSetting("Box", true, v -> this.render.getValue()));
    public final BooleanSetting outline = this.add(new BooleanSetting("Outline", false, v -> this.render.getValue()));
    public final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100), v -> this.render.getValue()));
    public final SliderSetting fadeTime = this.add(new SliderSetting("FadeTime", 500, 0, 5000, v -> this.render.getValue()));
    public final BooleanSetting pre = this.add(new BooleanSetting("Pre", false, v -> this.render.getValue()));
    public final BooleanSetting sync = this.add(new BooleanSetting("Sync", true, v -> this.render.getValue()));
    public PlayerEntity target;
    public static AutoTrap INSTANCE;
    int progress = 0;
    private final ArrayList<BlockPos> trapList = new ArrayList();
    private final ArrayList<BlockPos> placeList = new ArrayList();

    public AutoTrap() {
        super("AutoTrap", "Automatically trap the enemy", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (AutoTrap.nullCheck()) {
            return;
        }
        this.trapList.clear();
        this.placeList.clear();
        this.progress = 0;
        if (this.selfGround.getValue() && !AutoTrap.mc.player.isOnGround()) {
            this.target = null;
            return;
        }
        if (this.usingPause.getValue() && EntityUtil.isUsing()) {
            this.target = null;
            return;
        }
        if (!this.timer.passedMs((long)this.delay.getValue())) {
            return;
        }
        if (this.targetMod.getValue() == TargetMode.Single) {
            this.target = CombatUtil.getClosestEnemy(this.range.getValue());
            if (this.target == null) {
                if (this.autoDisable.getValue()) {
                    this.disable();
                }
                return;
            }
            this.trapTarget(this.target);
        } else if (this.targetMod.getValue() == TargetMode.Multi) {
            boolean found = false;
            for (PlayerEntity player : CombatUtil.getEnemies(this.range.getValue())) {
                found = true;
                this.target = player;
                this.trapTarget(this.target);
            }
            if (!found) {
                if (this.autoDisable.getValue()) {
                    this.disable();
                }
                this.target = null;
            }
        }
    }

    private void trapTarget(PlayerEntity target) {
        if (this.onlyHole.getValue() && !BlockUtil.isHole(EntityUtil.getEntityPos((Entity)target))) {
            return;
        }
        this.doTrap(EntityUtil.getEntityPos((Entity)target, true));
    }

    private void doTrap(BlockPos pos) {
        BlockPos offsetPos;
        Object z;
        int n;
        Object offsetPos2;
        int n2;
        int n3;
        Object[] arrobject;
        if (this.trapList.contains((Object)pos)) {
            return;
        }
        this.trapList.add(pos);
        if (this.legs.getValue()) {
            arrobject = Direction.values();
            n3 = arrobject.length;
            for (n2 = 0; n2 < n3; ++n2) {
                Direction i = (Direction) arrobject[n2];
                if (i == Direction.DOWN || i == Direction.UP) continue;
                offsetPos2 = pos.offset(i);
                this.placeAnchor((BlockPos)offsetPos2, this.legAnchor.getValue());
                if (BlockUtil.getPlaceSide((BlockPos) offsetPos2) != null || !BlockUtil.clientCanPlace((BlockPos) offsetPos2) || this.getHelper((BlockPos)offsetPos2) == null) continue;
                this.placeBlock(this.getHelper((BlockPos)offsetPos2));
            }
        }
        if (this.head.getValue() && BlockUtil.clientCanPlace(pos.up(2))) {
            if (BlockUtil.getPlaceSide(pos.up(2)) == null) {
                boolean trapChest = true;
                if (this.getHelper(pos.up(2)) != null) {
                    this.placeBlock(this.getHelper(pos.up(2)));
                    trapChest = false;
                }
                if (trapChest) {
                    Direction i;
                    int x;
                    Direction[] arrdirection = Direction.values();
                    n2 = arrdirection.length;
                    for (x = 0; x < n2; ++x) {
                        BlockPos offsetPos3;
                        i = arrdirection[x];
                        if (i == Direction.DOWN || i == Direction.UP || !BlockUtil.clientCanPlace((offsetPos3 = pos.offset(i).up()).up(), false) || !BlockUtil.canPlace(offsetPos3)) continue;
                        this.placeBlock(offsetPos3);
                        trapChest = false;
                        break;
                    }
                    if (trapChest) {
                        arrdirection = Direction.values();
                        n2 = arrdirection.length;
                        for (x = 0; x < n2; ++x) {
                            BlockPos offsetPos4;
                            i = arrdirection[x];
                            if (i == Direction.DOWN || i == Direction.UP || !BlockUtil.clientCanPlace((offsetPos4 = pos.offset(i).up()).up(), false) || BlockUtil.getPlaceSide(offsetPos4) != null || !BlockUtil.clientCanPlace(offsetPos4) || this.getHelper(offsetPos4) == null) continue;
                            this.placeBlock(this.getHelper(offsetPos4));
                            trapChest = false;
                            break;
                        }
                        if (trapChest) {
                            arrdirection = Direction.values();
                            n2 = arrdirection.length;
                            for (x = 0; x < n2; ++x) {
                                BlockPos offsetPos5;
                                i = arrdirection[x];
                                if (i == Direction.DOWN || i == Direction.UP || !BlockUtil.clientCanPlace((offsetPos5 = pos.offset(i).up()).up(), false) || BlockUtil.getPlaceSide(offsetPos5) != null || !BlockUtil.clientCanPlace(offsetPos5, false) || this.getHelper(offsetPos5) == null || BlockUtil.getPlaceSide(offsetPos5.down()) != null || !BlockUtil.clientCanPlace(offsetPos5.down(), false) || this.getHelper(offsetPos5.down()) == null) continue;
                                this.placeBlock(this.getHelper(offsetPos5.down()));
                                break;
                            }
                        }
                    }
                }
            }
            this.placeAnchor(pos.up(2), this.headAnchor.getValue());
        }
        if (this.antiStep.getValue() && (BlockUtil.isMining(pos.up(2)) || !this.onlyBreak.getValue())) {
            if (BlockUtil.getPlaceSide(pos.up(3)) == null && BlockUtil.clientCanPlace(pos.up(3)) && this.getHelper(pos.up(3), Direction.DOWN) != null) {
                this.placeBlock(this.getHelper(pos.up(3)));
            }
            this.placeBlock(pos.up(3));
        }
        if (this.down.getValue()) {
            BlockPos offsetPos6 = pos.down();
            this.placeBlock(offsetPos6);
            if (BlockUtil.getPlaceSide(offsetPos6) == null && BlockUtil.clientCanPlace(offsetPos6) && this.getHelper(offsetPos6) != null) {
                this.placeBlock(this.getHelper(offsetPos6));
            }
        }
        if (this.chestUp.getValue()) {
            Direction[] arrdirection = Direction.values();
            int n5 = arrdirection.length;
            for (n2 = 0; n2 < n5; ++n2) {
                Direction i = arrdirection[n2];
                if (i == Direction.DOWN || i == Direction.UP) continue;
                offsetPos2 = pos.offset(i).up(2);
                if (this.onlyBreaking.getValue() && !BlockUtil.isMining(pos.up(2))) continue;
                this.placeBlock((BlockPos)offsetPos2);
                if (BlockUtil.getPlaceSide((BlockPos) offsetPos2) != null || !BlockUtil.clientCanPlace((BlockPos) offsetPos2)) continue;
                if (this.getHelper((BlockPos)offsetPos2) != null) {
                    this.placeBlock(this.getHelper((BlockPos)offsetPos2));
                    continue;
                }
                if (BlockUtil.getPlaceSide(((BlockPos) offsetPos2).down()) != null || !BlockUtil.clientCanPlace(((BlockPos) offsetPos2).down()) || this.getHelper(((BlockPos) offsetPos2).down()) == null) continue;
                this.placeBlock(this.getHelper(((BlockPos) offsetPos2).down()));
            }
        }
        if (this.chest.getValue() && (!this.onlyGround.getValue() || this.target.isOnGround())) {
            Direction[] arrdirection = Direction.values();
            int n6 = arrdirection.length;
            for (n2 = 0; n2 < n6; ++n2) {
                Direction i = arrdirection[n2];
                if (i == Direction.DOWN || i == Direction.UP) continue;
                offsetPos2 = pos.offset(i).up();
                this.placeBlock((BlockPos)offsetPos2);
                if (BlockUtil.getPlaceSide((BlockPos) offsetPos2) != null || !BlockUtil.clientCanPlace((BlockPos) offsetPos2)) continue;
                if (this.getHelper((BlockPos)offsetPos2) != null) {
                    this.placeBlock(this.getHelper((BlockPos)offsetPos2));
                    continue;
                }
                if (BlockUtil.getPlaceSide(((BlockPos) offsetPos2).down()) != null || !BlockUtil.clientCanPlace(((BlockPos) offsetPos2).down()) || this.getHelper(((BlockPos) offsetPos2).down()) == null) continue;
                this.placeBlock(this.getHelper(((BlockPos) offsetPos2).down()));
            }
        }
        if (this.extend.getValue()) {
            for (int x : new int[]{1, 0, -1}) {
                int[] arrn = new int[]{1, 0, -1};
                int n7 = arrn.length;
                for (n = 0; n < n7; ++n) {
                    z = arrn[n];
                    offsetPos = pos.add(x, 0, (int)z);
                    if (!this.checkEntity(new BlockPos((Vec3i)offsetPos))) continue;
                    this.doTrap(offsetPos);
                }
            }
        }
    }

    @Override
    public String getInfo() {
        if (this.target != null) {
            return this.target.getName().getString();
        }
        return null;
    }

    public BlockPos getHelper(BlockPos pos) {
        if (!this.helper.getValue()) {
            return null;
        }
        for (Direction i : Direction.values()) {
            if (this.checkMine.getValue() && BlockUtil.isMining(pos.offset(i)) || Rebirth.HUD.placement.getValue() == Placement.Strict && !BlockUtil.isStrictDirection(pos.offset(i), i.getOpposite(), true) || !BlockUtil.canPlace(pos.offset(i))) continue;
            return pos.offset(i);
        }
        return null;
    }

    public BlockPos getHelper(BlockPos pos, Direction ignore) {
        if (!this.helper.getValue()) {
            return null;
        }
        for (Direction i : Direction.values()) {
            if (i == ignore || this.checkMine.getValue() && BlockUtil.isMining(pos.offset(i)) || !BlockUtil.isStrictDirection(pos.offset(i), i.getOpposite(), true) || !BlockUtil.canPlace(pos.offset(i))) continue;
            return pos.offset(i);
        }
        return null;
    }

    private boolean checkEntity(BlockPos pos) {
        if (AutoTrap.mc.player.getBoundingBox().intersects(new Box(pos))) {
            return false;
        }
        for (Entity entity : AutoTrap.mc.world.getNonSpectatingEntities(PlayerEntity.class, new Box(pos))) {
            if (!entity.isAlive()) continue;
            return true;
        }
        return false;
    }

    private void placeAnchor(BlockPos pos, boolean anchor) {
        int block;
        if (this.pre.getValue()) {
            ExtraAutoTrap.addBlock(pos);
        }
        if (BlockUtil.isMining(pos)) {
            return;
        }
        if (!BlockUtil.canPlace(pos, 6.0, this.breakCrystal.getValue())) {
            return;
        }
        if (!((double)this.progress < this.multiPlace.getValue())) {
            return;
        }
        if ((double)MathHelper.sqrt((float)((float)EntityUtil.getEyesPos().squaredDistanceTo(pos.toCenterPos()))) > this.placeRange.getValue()) {
            return;
        }
        int old = AutoTrap.mc.player.getInventory().selectedSlot;
        int n = block = anchor && this.getAnchor() != -1 ? this.getAnchor() : this.getBlock();
        if (block == -1) {
            return;
        }
        if (this.placeList.contains((Object)pos)) {
            return;
        }
        if (!this.pre.getValue()) {
            ExtraAutoTrap.addBlock(pos);
        }
        this.placeList.add(pos);
        CombatUtil.attackCrystal(pos, this.rotate.getValue(), this.usingPause.getValue());
        this.doSwap(block);
        BlockUtil.placeBlock(pos, this.rotate.getValue());
        if (this.inventory.getValue()) {
            this.doSwap(block);
            EntityUtil.sync();
        } else {
            this.doSwap(old);
        }
        this.timer.reset();
        ++this.progress;
    }

    private void placeBlock(BlockPos pos) {
        if (this.pre.getValue()) {
            ExtraAutoTrap.addBlock(pos);
        }
        if (BlockUtil.isMining(pos)) {
            return;
        }
        if (!BlockUtil.canPlace(pos, 6.0, this.breakCrystal.getValue())) {
            return;
        }
        if (!((double)this.progress < this.multiPlace.getValue())) {
            return;
        }
        if ((double)MathHelper.sqrt((float)((float)EntityUtil.getEyesPos().squaredDistanceTo(pos.toCenterPos()))) > this.placeRange.getValue()) {
            return;
        }
        int old = AutoTrap.mc.player.getInventory().selectedSlot;
        int block = this.getBlock();
        if (block == -1) {
            return;
        }
        if (this.placeList.contains((Object)pos)) {
            return;
        }
        if (!this.pre.getValue()) {
            ExtraAutoTrap.addBlock(pos);
        }
        this.placeList.add(pos);
        CombatUtil.attackCrystal(pos, this.rotate.getValue(), this.usingPause.getValue());
        this.doSwap(block);
        BlockUtil.placeBlock(pos, this.rotate.getValue());
        if (this.inventory.getValue()) {
            this.doSwap(block);
            EntityUtil.sync();
        } else {
            this.doSwap(old);
        }
        this.timer.reset();
        ++this.progress;
    }

    private void doSwap(int slot) {
        if (this.inventory.getValue()) {
            AutoTrap.mc.interactionManager.clickSlot(AutoTrap.mc.player.currentScreenHandler.syncId, slot, AutoTrap.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AutoTrap.mc.player);
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

    private int getAnchor() {
        if (this.inventory.getValue()) {
            return InventoryUtil.findBlockInventorySlot(Blocks.RESPAWN_ANCHOR);
        }
        return InventoryUtil.findBlock(Blocks.RESPAWN_ANCHOR);
    }

    public static enum TargetMode {
        Single,
        Multi;

        // $FF: synthetic method
        private static AutoTrap.TargetMode[] $values() {
            return new AutoTrap.TargetMode[]{Single, Multi};
        }
    }
}


