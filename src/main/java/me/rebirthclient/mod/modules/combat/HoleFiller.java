/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package me.rebirthclient.mod.modules.combat;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.render.PlaceRender;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class HoleFiller
extends Module {
    public static HoleFiller INSTANCE;
    public final SliderSetting delay = this.add(new SliderSetting("Delay", 50, 0, 500));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", false));
    private final BooleanSetting packet = this.add(new BooleanSetting("Packet", false));
    private final BooleanSetting webs = this.add(new BooleanSetting("Webs", false));
    private final BooleanSetting usingPause = this.add(new BooleanSetting("UsingPause", true));
    private final BooleanSetting checkMine = this.add(new BooleanSetting("CheckMine", true));
    private final BooleanSetting autoDisable = this.add(new BooleanSetting("AutoDisable", true));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true));
    private final SliderSetting range = this.add(new SliderSetting("Radius", 4.0, 0.0, 6.0));
    private final BooleanSetting smart = this.add(new BooleanSetting("Smart", false).setParent());
    private final EnumSetting logic = this.add(new EnumSetting("Logic", Logic.PLAYER, v -> this.smart.isOpen()));
    private final SliderSetting smartRange = this.add(new SliderSetting("EnemyRange", 4.0, 0.0, 6.0, v -> this.smart.isOpen()));
    private final SliderSetting targetRange = this.add(new SliderSetting("TargetRange", 12.0, 0.0, 20.0, v -> this.smart.isOpen()));
    private final SliderSetting predictTicks = this.add(new SliderSetting("PredictTicks", 4, 0, 10, v -> this.smart.isOpen()));
    private PlayerEntity closestTarget;
    private final Timer timer = new Timer();

    public HoleFiller() {
        super("HoleFiller", "Fills all safe spots in radius", Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        this.closestTarget = null;
    }

    @Override
    public String getInfo() {
        if (this.smart.getValue()) {
            return this.logic.getValue().toString();
        }
        return "Normal";
    }

    @Override
    public void onUpdate() {
        int block = 0;
        if (HoleFiller.mc.world == null) {
            return;
        }
        if (this.usingPause.getValue() && HoleFiller.mc.player.isUsingItem()) {
            return;
        }
        if (!this.timer.passedMs(this.delay.getValueInt())) {
            return;
        }
        if (this.smart.getValue()) {
            this.findClosestTarget();
        }
        List<BlockPos> blocks = this.getPlacePositions();
        BlockPos q = null;
        int obbySlot = this.getBlock(Blocks.OBSIDIAN);
        int eChestSlot = this.getBlock(Blocks.ENDER_CHEST);
        int webSlot = this.getBlock(Blocks.COBWEB);
        int n = this.webs.getValue() ? (webSlot == -1 ? (obbySlot == -1 ? eChestSlot : obbySlot) : webSlot) : (block = obbySlot == -1 ? eChestSlot : obbySlot);
        if (!this.webs.getValue() && obbySlot == -1 && eChestSlot == -1) {
            return;
        }
        if (this.webs.getValue() && webSlot == -1 && obbySlot == -1 && eChestSlot == -1) {
            return;
        }
        int originalSlot = HoleFiller.mc.player.getInventory().selectedSlot;
        for (BlockPos blockPos : blocks) {
            if (BlockUtil.hasEntity(blockPos, false) || this.checkMine.getValue() && Rebirth.BREAK.isMining(blockPos)) continue;
            if (this.smart.getValue() && this.isInRange(blockPos)) {
                q = blockPos;
                continue;
            }
            if (this.smart.getValue() && this.isInRange(blockPos) && this.logic.getValue() == Logic.HOLE && EntityUtil.getEntityPos((Entity)this.closestTarget).getSquaredDistance((Vec3i)blockPos) <= this.smartRange.getValue()) {
                q = blockPos;
                continue;
            }
            q = blockPos;
        }
        if (q != null && HoleFiller.mc.player.isOnGround()) {
            this.doSwap(block);
            PlaceRender.addBlock(q);
            BlockUtil.placeBlock(q, this.rotate.getValue(), this.packet.getValue());
            this.timer.reset();
            if (this.inventory.getValue()) {
                this.doSwap(block);
                EntityUtil.sync();
            } else {
                this.doSwap(originalSlot);
            }
        }
        if (q == null && this.autoDisable.getValue() && !this.smart.getValue()) {
            this.disable();
        }
    }

    private void findClosestTarget() {
        this.closestTarget = null;
        ArrayList<PlayerAndPredict> list = new ArrayList<PlayerAndPredict>();
        for (PlayerEntity target : CombatUtil.getEnemies(this.targetRange.getRange())) {
            list.add(new PlayerAndPredict(target));
        }
        if (list.isEmpty()) {
            return;
        }
        for (PlayerAndPredict pap : list) {
            if (this.closestTarget == null) {
                this.closestTarget = pap.player;
                continue;
            }
            if (!(HoleFiller.mc.player.distanceTo((Entity)pap.player) < HoleFiller.mc.player.distanceTo((Entity)this.closestTarget))) continue;
            this.closestTarget = pap.player;
        }
    }

    private boolean isHole(BlockPos pos) {
        BlockPos boost = pos.add(0, 1, 0);
        BlockPos boost2 = pos.add(0, 0, 0);
        BlockPos boost3 = pos.add(0, 0, -1);
        BlockPos boost4 = pos.add(1, 0, 0);
        BlockPos boost5 = pos.add(-1, 0, 0);
        BlockPos boost6 = pos.add(0, 0, 1);
        BlockPos boost7 = pos.add(0, 2, 0);
        BlockPos boost8 = pos.add((Vec3i)new BlockPosX(0.5, 0.5, 0.5));
        BlockPos boost9 = pos.add(0, -1, 0);
        return !(HoleFiller.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || HoleFiller.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR || HoleFiller.mc.world.getBlockState(boost7).getBlock() != Blocks.AIR || HoleFiller.mc.world.getBlockState(boost3).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost3).getBlock() != Blocks.BEDROCK || HoleFiller.mc.world.getBlockState(boost4).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost4).getBlock() != Blocks.BEDROCK || HoleFiller.mc.world.getBlockState(boost5).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost5).getBlock() != Blocks.BEDROCK || HoleFiller.mc.world.getBlockState(boost6).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost6).getBlock() != Blocks.BEDROCK || HoleFiller.mc.world.getBlockState(boost8).getBlock() != Blocks.AIR || HoleFiller.mc.world.getBlockState(boost9).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost9).getBlock() != Blocks.BEDROCK);
    }

    private boolean isInRange(BlockPos blockPos) {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        positions.addAll(this.getSphere(EntityUtil.getPlayerPos(), this.range.getValueFloat()).stream().filter(this::isHole).toList());
        return positions.contains((Object)blockPos);
    }

    private List<BlockPos> getPlacePositions() {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        if (this.smart.getValue() && this.closestTarget != null) {
            positions.addAll(this.getSphere(EntityUtil.getEntityPos((Entity)this.closestTarget), this.smartRange.getValueFloat()).stream().filter(this::isHole).filter(this::isInRange).collect(Collectors.toList()));
        } else if (!this.smart.getValue()) {
            positions.addAll(this.getSphere(EntityUtil.getPlayerPos(), this.range.getValueFloat()).stream().filter(this::isHole).toList());
        }
        return positions;
    }

    private List<BlockPos> getSphere(BlockPos loc, float r) {
        ArrayList<BlockPos> circleBlocks = new ArrayList<BlockPos>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                float f2;
                float f;
                int y = cy - (int)r;
                while ((f = (float)y) < (f2 = (float)cy + r)) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (cy - y) * (cy - y);
                    if (dist < (double)(r * r)) {
                        BlockPos l = new BlockPos(x, y, z);
                        circleBlocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleBlocks;
    }

    private int getBlock(Block block) {
        if (this.inventory.getValue()) {
            return InventoryUtil.findBlockInventorySlot(block);
        }
        return InventoryUtil.findBlock(block);
    }

    private void doSwap(int slot) {
        if (this.inventory.getValue()) {
            HoleFiller.mc.interactionManager.clickSlot(HoleFiller.mc.player.currentScreenHandler.syncId, slot, HoleFiller.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)HoleFiller.mc.player);
        } else {
            InventoryUtil.doSwap(slot);
        }
    }

    private static enum Logic {
        PLAYER,
        HOLE;

        // $FF: synthetic method
        private static Logic[] $values() {
            return new Logic[]{PLAYER, HOLE};
        }
    }
    public class PlayerAndPredict {
        PlayerEntity player;
        PlayerEntity predict;

        public PlayerAndPredict(PlayerEntity player) {
            this.player = player;
            if (HoleFiller.this.predictTicks.getValueFloat() > 0.0f) {
                this.predict = new PlayerEntity((World)Wrapper.mc.world, player.getBlockPos(), player.getYaw(), new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")){

                    public boolean isSpectator() {
                        return false;
                    }

                    public boolean isCreative() {
                        return false;
                    }
                };
                this.predict.setPosition(player.getPos().add(CombatUtil.getMotionVec((Entity)player, HoleFiller.INSTANCE.predictTicks.getValueInt(), true)));
                this.predict.setHealth(player.getHealth());
                this.predict.prevX = player.prevX;
                this.predict.prevZ = player.prevZ;
                this.predict.prevY = player.prevY;
                this.predict.setOnGround(player.isOnGround());
                this.predict.getInventory().clone(player.getInventory());
                this.predict.setPose(player.getPose());
                for (StatusEffectInstance se : player.getStatusEffects()) {
                    this.predict.addStatusEffect(se);
                }
            } else {
                this.predict = player;
            }
        }
    }
}

