/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ExperienceOrbEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ArrowEntity
 *  net.minecraft.entity.projectile.thrown.ExperienceBottleEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$PositionAndOnGround
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 */
package me.rebirthclient.mod.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.asm.accessors.IPlayerMoveC2SPacket;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoPush;
import me.rebirthclient.mod.modules.render.PlaceRender;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class Burrow
extends Module {
    public static Burrow INSTANCE;
    public final BooleanSetting enderChest = this.add(new BooleanSetting("EnderChest", true));
    public final BooleanSetting antiLag = this.add(new BooleanSetting("AntiLag", true));
    public final BooleanSetting helper = this.add(new BooleanSetting("Helper", true));
    public final BooleanSetting headFill = this.add(new BooleanSetting("HeadFill", true));
    public final SliderSetting multiPlace = this.add(new SliderSetting("MultiPlace", 3.0, 1.0, 4.0, 1.0));
    private final EnumSetting rotate = this.add(new EnumSetting("RotateMode", RotateMode.Bypass));
    private final BooleanSetting breakCrystal = this.add(new BooleanSetting("Break", true));
    private final BooleanSetting noSelfPos = this.add(new BooleanSetting("NoSelfPos", false));
    private final BooleanSetting wait = this.add(new BooleanSetting("Wait", true));
    public final BooleanSetting bypass = this.add(new BooleanSetting("Bypass", true));
    private final BooleanSetting aboveHead = this.add(new BooleanSetting("AboveHead", true).setParent());
    private final BooleanSetting center = this.add(new BooleanSetting("Center", false, v -> this.aboveHead.isOpen()));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true).setParent());
    private final EnumSetting invmode = this.add(new EnumSetting("InventoryMode", InvMode.Inv, v -> this.inventory.isOpen()));
    private final EnumSetting lagMode = this.add(new EnumSetting("LagMode", LagBackMode.Normal));
    private boolean ignore = false;
    int progress = 0;
    List<BlockPos> placePos = new ArrayList<BlockPos>();
    BlockPos movedPos = null;

    public Burrow() {
        super("Burrow", Module.Category.Combat);
        INSTANCE = this;
    }

    @EventHandler
    public void OnPacket(PacketEvent.Receive event) {
        if (Burrow.nullCheck()) {
            return;
        }
        Object t = event.getPacket();
        if (t instanceof PlayerMoveC2SPacket.PositionAndOnGround) {
            PlayerMoveC2SPacket.PositionAndOnGround pack = (PlayerMoveC2SPacket.PositionAndOnGround)t;
            if (this.bypass.getValue() && !this.ignore) {
                Burrow.mc.player.prevY = 0.0;
                Burrow.mc.player.setPosition(Burrow.mc.player.getX(), Burrow.mc.player.getY() - 1.0E-10, Burrow.mc.player.getZ());
                this.ignore = true;
                mc.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY(), Burrow.mc.player.getZ(), false));
                mc.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() + 1000.0, Burrow.mc.player.getZ(), false));
                this.ignore = false;
            }
        }
    }

    @Override
    public void onUpdate() {
        this.movedPos = null;
        if (!Burrow.mc.player.isOnGround()) {
            return;
        }
        if (AutoPush.isInWeb((PlayerEntity)Burrow.mc.player)) {
            return;
        }
        if (this.antiLag.getValue() && !BlockUtil.getState(EntityUtil.getPlayerPos(true).down()).blocksMovement()) {
            return;
        }
        int oldSlot = Burrow.mc.player.getInventory().selectedSlot;
        int block = this.getBlock();
        if (block == -1) {
            CommandManager.sendChatMessage("\u00a7e[?] \u00a7c\u00a7oObsidian" + (this.enderChest.getValue() ? "/EnderChest" : "") + "?");
            this.disable();
            return;
        }
        BlockPosX pos1 = new BlockPosX(Burrow.mc.player.getX() + 0.3, Burrow.mc.player.getY() + 0.5, Burrow.mc.player.getZ() + 0.3);
        BlockPosX pos2 = new BlockPosX(Burrow.mc.player.getX() - 0.3, Burrow.mc.player.getY() + 0.5, Burrow.mc.player.getZ() + 0.3);
        BlockPosX pos3 = new BlockPosX(Burrow.mc.player.getX() + 0.3, Burrow.mc.player.getY() + 0.5, Burrow.mc.player.getZ() - 0.3);
        BlockPosX pos4 = new BlockPosX(Burrow.mc.player.getX() - 0.3, Burrow.mc.player.getY() + 0.5, Burrow.mc.player.getZ() - 0.3);
        BlockPosX pos5 = new BlockPosX(Burrow.mc.player.getX() + 0.3, Burrow.mc.player.getY() + 1.5, Burrow.mc.player.getZ() + 0.3);
        BlockPosX pos6 = new BlockPosX(Burrow.mc.player.getX() - 0.3, Burrow.mc.player.getY() + 1.5, Burrow.mc.player.getZ() + 0.3);
        BlockPosX pos7 = new BlockPosX(Burrow.mc.player.getX() + 0.3, Burrow.mc.player.getY() + 1.5, Burrow.mc.player.getZ() - 0.3);
        BlockPosX pos8 = new BlockPosX(Burrow.mc.player.getX() - 0.3, Burrow.mc.player.getY() + 1.5, Burrow.mc.player.getZ() - 0.3);
        BlockPos playerPos = EntityUtil.getPlayerPos(true);
        boolean head = false;
        if (!(this.canPlace(pos1) || this.canPlace(pos2) || this.canPlace(pos3) || this.canPlace(pos4))) {
            head = true;
            if (!(this.headFill.getValue() && (this.canPlace(pos5) || this.canPlace(pos6) || this.canPlace(pos7) || this.canPlace(pos8)))) {
                if (!this.wait.getValue()) {
                    this.disable();
                }
                return;
            }
        }
        boolean above = false;
        BlockPos headPos = EntityUtil.getPlayerPos().up(2);
        boolean rotate = this.rotate.getValue() == RotateMode.Normal;
        CombatUtil.attackCrystal(Burrow.mc.player.getBoundingBox(), this.rotate.getValue() != RotateMode.None, false);
        if (head || Burrow.mc.player.isInSneakingPose() || this.Trapped(headPos) || this.Trapped(headPos.add(1, 0, 0)) || this.Trapped(headPos.add(-1, 0, 0)) || this.Trapped(headPos.add(0, 0, 1)) || this.Trapped(headPos.add(0, 0, -1)) || this.Trapped(headPos.add(1, 0, -1)) || this.Trapped(headPos.add(-1, 0, -1)) || this.Trapped(headPos.add(1, 0, 1)) || this.Trapped(headPos.add(-1, 0, 1))) {
            above = true;
            if (!this.aboveHead.getValue()) {
                if (!this.wait.getValue()) {
                    this.disable();
                }
                return;
            }
            boolean moved = false;
            BlockPos offPos = playerPos;
            if (!(!this.checkSelf(offPos) || BlockUtil.canReplace(offPos) || this.headFill.getValue() && BlockUtil.canReplace(offPos.up()))) {
                this.gotoPos(offPos);
            } else {
                for (Direction facing : Direction.values()) {
                    if (facing == Direction.UP || facing == Direction.DOWN || !this.checkSelf(offPos = playerPos.offset(facing)) || BlockUtil.canReplace(offPos) || this.headFill.getValue() && BlockUtil.canReplace(offPos.up())) continue;
                    this.gotoPos(offPos);
                    moved = true;
                    break;
                }
                if (!moved) {
                    for (Direction facing : Direction.values()) {
                        if (facing == Direction.UP || facing == Direction.DOWN || !this.checkSelf(offPos = playerPos.offset(facing))) continue;
                        this.gotoPos(offPos);
                        moved = true;
                        break;
                    }
                    if (!moved) {
                        if (!this.center.getValue()) {
                            return;
                        }
                        for (Direction facing : Direction.values()) {
                            if (facing == Direction.UP || facing == Direction.DOWN || !this.canGoto(offPos = playerPos.offset(facing))) continue;
                            this.gotoPos(offPos);
                            moved = true;
                            break;
                        }
                        if (!moved) {
                            if (!this.wait.getValue()) {
                                this.disable();
                            }
                            return;
                        }
                    }
                }
            }
        } else {
            Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() + 0.4199999868869781, Burrow.mc.player.getZ(), false));
            Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() + 0.7531999805212017, Burrow.mc.player.getZ(), false));
            Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() + 0.9999957640154541, Burrow.mc.player.getZ(), false));
            Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() + 1.1661092609382138, Burrow.mc.player.getZ(), false));
        }
        this.doSwap(block);
        this.progress = 0;
        this.placePos.clear();
        if (this.rotate.getValue() == RotateMode.Bypass) {
            EntityUtil.sendYawAndPitch(Burrow.mc.player.getYaw(), 90.0f);
        }
        if (this.helper.getValue()) {
            this.placeBlock(playerPos.down(), rotate);
        }
        this.placeBlock(playerPos, rotate);
        if (this.helper.getValue()) {
            this.placeBlock(pos1.down(), rotate);
        }
        this.placeBlock(pos1, rotate);
        if (this.helper.getValue()) {
            this.placeBlock(pos2.down(), rotate);
        }
        this.placeBlock(pos2, rotate);
        if (this.helper.getValue()) {
            this.placeBlock(pos3.down(), rotate);
        }
        this.placeBlock(pos3, rotate);
        if (this.helper.getValue()) {
            this.placeBlock(pos4.down(), rotate);
        }
        this.placeBlock(pos4, rotate);
        if (this.headFill.getValue() && above) {
            this.placeBlock(pos5, rotate);
            this.placeBlock(pos6, rotate);
            this.placeBlock(pos7, rotate);
            this.placeBlock(pos8, rotate);
        }
        if (this.inventory.getValue() && block < 36 && block > 8) {
            this.doSwap(block);
            EntityUtil.sync();
        } else {
            this.doSwap(oldSlot);
        }
        if (this.inventory.getValue() && this.invmode.getValue() == InvMode.Pick) {
            InventoryUtil.doSwap(oldSlot);
        }
        switch (above ? LagBackMode.Strict : (LagBackMode)this.lagMode.getValue()) {
            case Strict: {
                int i;
                double distance = 0.0;
                BlockPos bestPos = null;
                for (i = 0; i < 10; ++i) {
                    BlockPos pos = EntityUtil.getPlayerPos().up(i);
                    if (!this.canGoto(pos) || MathHelper.sqrt((float)((float)Burrow.mc.player.squaredDistanceTo(pos.toCenterPos()))) < 2.0f || bestPos != null && !(Burrow.mc.player.squaredDistanceTo(pos.toCenterPos()) < distance)) continue;
                    bestPos = pos;
                    distance = Burrow.mc.player.squaredDistanceTo(pos.toCenterPos());
                }
                if (bestPos == null) break;
                Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround((double)bestPos.getX() + 0.5, (double)bestPos.getY(), (double)bestPos.getZ() + 0.5, false));
                break;
            }
            case Normal: {
                Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() + 1.9400880035762786, Burrow.mc.player.getZ(), false));
                Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() - 1.0, Burrow.mc.player.getZ(), false));
                break;
            }
            case Old: {
                Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() + 1.9400880035762786, Burrow.mc.player.getZ(), false));
                break;
            }
            case Void: {
                Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), -68.0, Burrow.mc.player.getZ(), false));
                break;
            }
            case OBSTest: {
                int i;
                double distance = 0.0;
                BlockPos bestPos = null;
                for (i = 5; i < 20; ++i) {
                    BlockPos pos = EntityUtil.getPlayerPos().up(i);
                    if (!this.canGoto(pos) || MathHelper.sqrt((float)((float)Burrow.mc.player.squaredDistanceTo(pos.toCenterPos()))) < 2.0f || bestPos != null && !(Burrow.mc.player.squaredDistanceTo(pos.toCenterPos()) < distance)) continue;
                    bestPos = pos;
                    distance = Burrow.mc.player.squaredDistanceTo(pos.toCenterPos());
                }
                if (bestPos == null) break;
                Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround((double)bestPos.getX() + 0.5, (double)bestPos.getY(), (double)bestPos.getZ() + 0.5, false));
                break;
            }
            case Test: {
                BlockPos pos = this.getFlooredPosition((Entity)Burrow.mc.player).add(0, 3, 0);
                Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround((double)pos.getX() + 0.2, Burrow.mc.player.getY() + 1.0, (double)pos.getZ() + 0.2, true));
                Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), this.qaq(), Burrow.mc.player.getZ(), true));
            }
        }
        this.disable();
    }

    private double qaq() {
        if (!BlockUtil.isAir(new BlockPos((int)Burrow.mc.player.getX(), (int)Burrow.mc.player.getY(), (int)Burrow.mc.player.getZ()).multiply(3))) {
            return 1.2;
        }
        for (int i = 4; i <= 5; ++i) {
            if (BlockUtil.isAir(new BlockPos((int)Burrow.mc.player.getX(), (int)Burrow.mc.player.getY(), (int)Burrow.mc.player.getZ()).multiply(i))) continue;
            return 2.2 + (double)i - 4.0;
        }
        return 10.0;
    }

    private void placeBlock(BlockPos pos, boolean rotate) {
        if (this.canPlace(pos) && !this.placePos.contains((Object)pos) && this.progress < this.multiPlace.getValueInt()) {
            this.placePos.add(pos);
            ++this.progress;
            Direction side = BlockUtil.getPlaceSide(pos);
            if (side == null) {
                return;
            }
            BlockUtil.placedPos.add(pos);
            PlaceRender.PlaceMap.put(pos, new PlaceRender.placePosition(pos));
            BlockUtil.clickBlock(pos.offset(side), side.getOpposite(), rotate);
        }
    }

    private void doSwap(int slot) {
        if (this.inventory.getValue() && slot < 36 && slot > 8) {
            if (this.invmode.getValue() == InvMode.Inv) {
                Burrow.mc.interactionManager.clickSlot(Burrow.mc.player.currentScreenHandler.syncId, slot, Burrow.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)Burrow.mc.player);
            } else {
                Burrow.mc.interactionManager.pickFromInventory(slot);
            }
        } else if (slot < 9) {
            InventoryUtil.doSwap(slot);
        } else {
            InventoryUtil.doSwap(slot - 36);
        }
    }

    private void gotoPos(BlockPos offPos) {
        this.movedPos = offPos;
        if (Math.abs((double)offPos.getX() + 0.5 - Burrow.mc.player.getX()) < Math.abs((double)offPos.getZ() + 0.5 - Burrow.mc.player.getZ())) {
            Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX(), Burrow.mc.player.getY() + 0.2, Burrow.mc.player.getZ() + ((double)offPos.getZ() + 0.5 - Burrow.mc.player.getZ()), true));
        } else {
            Burrow.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Burrow.mc.player.getX() + ((double)offPos.getX() + 0.2 - Burrow.mc.player.getX()), Burrow.mc.player.getY() + 0.2, Burrow.mc.player.getZ(), true));
        }
    }

    private boolean canGoto(BlockPos pos) {
        return !BlockUtil.getState(pos).blocksMovement() && !BlockUtil.getState(pos.up()).blocksMovement();
    }

    private boolean canPlace(BlockPos pos) {
        if (this.noSelfPos.getValue() && EntityUtil.getPlayerPos().equals((Object)pos)) {
            return false;
        }
        if (BlockUtil.getPlaceSide(pos) == null) {
            return false;
        }
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        return !this.hasEntity(pos);
    }

    private boolean hasEntity(BlockPos pos) {
        for (Entity entity : Burrow.mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (entity == Burrow.mc.player || !entity.isAlive() || entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ExperienceBottleEntity || entity instanceof ArrowEntity || entity instanceof EndCrystalEntity && this.breakCrystal.getValue() || entity instanceof ArmorStandEntity && Rebirth.HUD.obsMode.getValue()) continue;
            return true;
        }
        return false;
    }

    private boolean checkSelf(BlockPos pos) {
        return Burrow.mc.player.getBoundingBox().intersects(new Box(pos));
    }

    private boolean Trapped(BlockPos pos) {
        return Burrow.mc.world.canCollide((Entity)Burrow.mc.player, new Box(pos)) && this.checkSelf(pos.down(2));
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

    private BlockPos getFlooredPosition(Entity entity) {
        return new BlockPos((int)Math.floor(entity.getX()), (int)Math.round(entity.getY()), (int)Math.floor(entity.getZ()));
    }

    private static enum RotateMode {
        Bypass,
        Normal,
        None;

        // $FF: synthetic method
        private static Burrow.RotateMode[] $values() {
            return new Burrow.RotateMode[]{Bypass, Normal, None};
        }
    }

    private static enum InvMode {
        Inv,
        Pick;

        // $FF: synthetic method
        private static Burrow.InvMode[] $values() {
            return new Burrow.InvMode[]{Inv, Pick};
        }
    }

    private static enum LagBackMode {
        Normal,
        Strict,
        Void,
        OBSTest,
        Old,
        Test;

        // $FF: synthetic method
        private static Burrow.LagBackMode[] $values() {
            return new Burrow.LagBackMode[]{Normal, Strict, Void, OBSTest, Old, Test};
        }
    }
}
