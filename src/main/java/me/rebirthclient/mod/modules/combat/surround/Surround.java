/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 */
package me.rebirthclient.mod.modules.combat.surround;

import java.awt.Color;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.UpdateWalkingEvent;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.surround.ExtraSurround;
import me.rebirthclient.mod.modules.movement.AutoCenter;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class Surround
extends Module {
    public static Surround INSTANCE = new Surround();
    public final BooleanSetting enableInHole = this.add(new BooleanSetting("EnableInHole", false));
    private final Timer timer = new Timer();
    public final SliderSetting delay = this.add(new SliderSetting("Delay", 50, 0, 500));
    private final SliderSetting multiPlace = this.add(new SliderSetting("MultiPlace", 1, 1, 8));
    private final BooleanSetting spam = this.add(new BooleanSetting("Spam", true));
    private final BooleanSetting mineSpam = this.add(new BooleanSetting("MineSpam", false));
    private final BooleanSetting checkMine = this.add(new BooleanSetting("CheckMine", false));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true));
    private final BooleanSetting helper = this.add(new BooleanSetting("Helper", true));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true).setParent());
    private final EnumSetting invmode = this.add(new EnumSetting("InventoryMode", InvMode.Inv, v -> this.inventory.isOpen()));
    private final BooleanSetting breakCrystal = this.add(new BooleanSetting("BreakCrystal", true));
    private final SliderSetting safeHealth = this.add(new SliderSetting("SafeHealth", 16.0, 0.0, 36.0, v -> this.breakCrystal.getValue()));
    private final BooleanSetting usingPause = this.add(new BooleanSetting("UsingPause", true, v -> this.breakCrystal.getValue()));
    private final BooleanSetting center = this.add(new BooleanSetting("Center", true));
    public final BooleanSetting extend = this.add(new BooleanSetting("Extend", true));
    public final BooleanSetting onlyGround = this.add(new BooleanSetting("OnlyGround", true));
    private final BooleanSetting moveDisable = this.add(new BooleanSetting("MoveDisable", true));
    private final BooleanSetting strictDisable = this.add(new BooleanSetting("StrictDisable", false, v -> this.moveDisable.getValue()));
    private final BooleanSetting isMoving = this.add(new BooleanSetting("isMoving", true, v -> this.moveDisable.getValue()));
    private final BooleanSetting jumpDisable = this.add(new BooleanSetting("JumpDisable", true));
    private final BooleanSetting inMoving = this.add(new BooleanSetting("inMoving", true, v -> this.jumpDisable.getValue()));
    private final BooleanSetting enderChest = this.add(new BooleanSetting("EnderChest", true));
    public final BooleanSetting render = this.add(new BooleanSetting("Render", true));
    public final BooleanSetting box = this.add(new BooleanSetting("Box", true, v -> this.render.getValue()));
    public final BooleanSetting outline = this.add(new BooleanSetting("Outline", false, v -> this.render.getValue()));
    public final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100), v -> this.render.getValue()));
    public final SliderSetting fadeTime = this.add(new SliderSetting("FadeTime", 500, 0, 5000, v -> this.render.getValue()));
    public final BooleanSetting pre = this.add(new BooleanSetting("Pre", false, v -> this.render.getValue()));
    public final BooleanSetting moveReset = this.add(new BooleanSetting("Reset", true, v -> this.render.getValue()));
    double startX = 0.0;
    double startY = 0.0;
    double startZ = 0.0;
    int progress = 0;
    BlockPos startPos = null;

    public Surround() {
        super("Surround", "Surrounds you with Obsidian", Module.Category.Combat);
        INSTANCE = this;
    }

    static boolean checkSelf(BlockPos pos) {
        return Surround.mc.player.getBoundingBox().intersects(new Box(pos));
    }

    @Override
    public void onEnable() {
        this.startPos = EntityUtil.getPlayerPos();
        if (this.startPos == null) {
            this.disable();
            return;
        }
        this.startX = Surround.mc.player.getX();
        this.startY = Surround.mc.player.getY();
        this.startZ = Surround.mc.player.getZ();
        if (this.center.getValue() && this.getBlock() != -1) {
            AutoCenter.INSTANCE.enable();
        }
    }

    @EventHandler
    public void onUpdateWalking(UpdateWalkingEvent event) {
        if (!this.timer.passedMs((long)this.delay.getValue())) {
            return;
        }
        this.progress = 0;
        BlockPos pos = EntityUtil.getPlayerPos();
        if (this.startPos == null || !EntityUtil.getPlayerPos().equals((Object)this.startPos) && this.moveDisable.getValue() && this.strictDisable.getValue() && (!this.isMoving.getValue() || MovementUtil.isMoving()) || (double)MathHelper.sqrt((float)((float)Surround.mc.player.squaredDistanceTo(this.startX, this.startY, this.startZ))) > 1.3 && this.moveDisable.getValue() && !this.strictDisable.getValue() && (!this.isMoving.getValue() || MovementUtil.isMoving()) || this.jumpDisable.getValue() && (this.startY - Surround.mc.player.getY() > 0.5 || this.startY - Surround.mc.player.getY() < -0.5) && (!this.inMoving.getValue() || MovementUtil.isMoving())) {
            this.disable();
            return;
        }
        if (this.getBlock() == -1) {
            CommandManager.sendChatMessage("\u00a7e[?] \u00a7c\u00a7oObsidian" + (this.enderChest.getValue() ? "/EnderChest" : "") + "?");
            this.disable();
            return;
        }
        if (this.onlyGround.getValue() && !Surround.mc.player.isOnGround()) {
            return;
        }
        for (Direction i : Direction.values()) {
            if (i == Direction.UP) continue;
            BlockPos offsetPos = pos.offset(i);
            if (BlockUtil.getPlaceSide(offsetPos) != null) {
                this.placeBlock(offsetPos);
            } else if (BlockUtil.canReplace(offsetPos)) {
                this.placeBlock(this.getHelper(offsetPos));
            }
            if (!Surround.checkSelf(offsetPos) || !this.extend.getValue()) continue;
            for (Direction i2 : Direction.values()) {
                if (i2 == Direction.UP) continue;
                BlockPos offsetPos2 = offsetPos.offset(i2);
                if (Surround.checkSelf(offsetPos2)) {
                    for (Direction i3 : Direction.values()) {
                        if (i3 == Direction.UP) continue;
                        this.placeBlock(offsetPos2);
                        BlockPos offsetPos3 = offsetPos2.offset(i3);
                        this.placeBlock(BlockUtil.getPlaceSide(offsetPos3) != null || !BlockUtil.canReplace(offsetPos3) ? offsetPos3 : this.getHelper(offsetPos3));
                    }
                }
                this.placeBlock(BlockUtil.getPlaceSide(offsetPos2) != null || !BlockUtil.canReplace(offsetPos2) ? offsetPos2 : this.getHelper(offsetPos2));
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (pos == null) {
            return;
        }
        if (this.pre.getValue()) {
            ExtraSurround.addBlock(pos);
        }
        if (this.checkMine.getValue() && BlockUtil.isMining(pos)) {
            return;
        }
        if (!(BlockUtil.canPlace(pos, 6.0, true) || this.mineSpam.getValue() && BlockUtil.isMining(pos))) {
            return;
        }
        if (this.breakCrystal.getValue() && (double)EntityUtil.getHealth((Entity)Surround.mc.player) >= this.safeHealth.getValue()) {
            CombatUtil.attackCrystal(pos, this.rotate.getValue(), this.usingPause.getValue());
        }
        if (!(this.breakCrystal.getValue() && (double)EntityUtil.getHealth((Entity)Surround.mc.player) >= this.safeHealth.getValue() || !BlockUtil.hasEntity(pos, false) || this.spam.getValue())) {
            return;
        }
        if (!((double)this.progress < this.multiPlace.getValue())) {
            return;
        }
        int old = Surround.mc.player.getInventory().selectedSlot;
        int block = this.getBlock();
        if (block == -1) {
            return;
        }
        this.doSwap(block);
        BlockUtil.placeBlock(pos, this.rotate.getValue());
        if (this.inventory.getValue() && block < 36 && block > 8) {
            this.doSwap(block);
            EntityUtil.sync();
        } else {
            this.doSwap(old);
        }
        ++this.progress;
        this.timer.reset();
        ExtraSurround.addBlock(pos);
    }

    private void doSwap(int slot) {
        if (this.inventory.getValue() && slot < 36 && slot > 8) {
            if (this.invmode.getValue() == InvMode.Inv) {
                Surround.mc.interactionManager.clickSlot(Surround.mc.player.currentScreenHandler.syncId, slot, Surround.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)Surround.mc.player);
            } else {
                mc.getNetworkHandler().sendPacket((Packet)new PickFromInventoryC2SPacket(slot));
            }
        } else if (slot < 9) {
            InventoryUtil.doSwap(slot);
        } else {
            InventoryUtil.doSwap(slot - 36);
        }
    }

    private int getBlock() {
        if (this.inventory.getValue()) {
            if (InventoryUtil.findBlockInventorySlot(Blocks.OBSIDIAN) != -1 || !this.enderChest.getValue()) {
                return InventoryUtil.findBlockInventorySlot(Blocks.OBSIDIAN);
            }
            return InventoryUtil.findBlockInventorySlot(Blocks.ENDER_CHEST);
        }
        if (InventoryUtil.findBlock(Blocks.OBSIDIAN) != -1 || !this.enderChest.getValue()) {
            return InventoryUtil.findBlock(Blocks.OBSIDIAN);
        }
        return InventoryUtil.findBlock(Blocks.ENDER_CHEST);
    }

    public BlockPos getHelper(BlockPos pos) {
        if (!this.helper.getValue()) {
            return pos.down();
        }
        for (Direction i : Direction.values()) {
            if (this.checkMine.getValue() && BlockUtil.isMining(pos.offset(i)) || !BlockUtil.isStrictDirection(pos.offset(i), i.getOpposite(), true) || !BlockUtil.canPlace(pos.offset(i))) continue;
            return pos.offset(i);
        }
        return null;
    }

    private static enum InvMode {
        Inv,
        Pick;

        // $FF: synthetic method
        private static Surround.InvMode[] $values() {
            return new Surround.InvMode[]{Inv, Pick};
        }
    }
}
