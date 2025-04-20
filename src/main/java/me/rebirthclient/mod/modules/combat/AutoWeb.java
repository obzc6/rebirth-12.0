/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.combat;

import java.util.ArrayList;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.modules.render.PlaceRender;
import me.rebirthclient.mod.settings.SwingMode;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AutoWeb
extends Module {
    public final EnumSetting page = this.add(new EnumSetting("Page", Page.General));
    public final SliderSetting delay = this.add(new SliderSetting("Delay", 50, 0, 500, v -> this.page.getValue() == Page.General));
    public final SliderSetting multiPlace = this.add(new SliderSetting("MultiPlace", 2, 1, 10, v -> this.page.getValue() == Page.General));
    public final SliderSetting predictTicks = this.add(new SliderSetting("PredictTicks", 2.0, 0.0, 50.0, 1.0, v -> this.page.getValue() == Page.General));
    private final BooleanSetting checkMine = this.add(new BooleanSetting("CheckMine", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting noMine = this.add(new BooleanSetting("NoMine", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting extend = this.add(new BooleanSetting("Extend", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting face = this.add(new BooleanSetting("Face", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting leg = this.add(new BooleanSetting("Leg", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting down = this.add(new BooleanSetting("Down", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting selfGround = this.add(new BooleanSetting("SelfGround", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting usingPause = this.add(new BooleanSetting("UsingPause", true, v -> this.page.getValue() == Page.General));
    public final SliderSetting placeRange = this.add(new SliderSetting("PlaceRange", 5.0, 0.0, 6.0, 0.1, v -> this.page.getValue() == Page.General));
    public final SliderSetting targetRange = this.add(new SliderSetting("TargetRange", 8.0, 0.0, 8.0, 0.1, v -> this.page.getValue() == Page.General));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true, v -> this.page.getValue() == Page.Rotate).setParent());
    private final SliderSetting yawStep = this.add(new SliderSetting("YawStep", 0.3f, 0.1f, 1.0, 0.01f, v -> this.rotate.isOpen() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting checkLook = this.add(new BooleanSetting("CheckLook", true, v -> this.rotate.isOpen() && this.page.getValue() == Page.Rotate));
    private final SliderSetting fov = this.add(new SliderSetting("Fov", 5.0, 0.0, 30.0, v -> this.rotate.isOpen() && this.checkLook.getValue() && this.page.getValue() == Page.Rotate));
    private final Timer timer = new Timer();
    public Vec3d directionVec = null;
    private float lastYaw = 0.0f;
    private float lastPitch = 0.0f;
    public static boolean place = false;
    int progress = 0;
    private final ArrayList<BlockPos> pos = new ArrayList();

    public AutoWeb() {
        super("AutoWeb", Module.Category.Combat);
    }

    @EventHandler(priority=98)
    public void onRotate(RotateEvent event) {
        if (this.directionVec != null) {
            float[] newAngle = this.injectStep(EntityUtil.getLegitRotations(this.directionVec), this.yawStep.getValueFloat());
            this.lastYaw = newAngle[0];
            this.lastPitch = newAngle[1];
            event.setYaw(this.lastYaw);
            event.setPitch(this.lastPitch);
        } else {
            this.lastYaw = Rebirth.RUN.lastYaw;
            this.lastPitch = Rebirth.RUN.lastPitch;
        }
    }

    @Override
    public void onUpdate() {
        this.pos.clear();
        this.progress = 0;
        if (!this.timer.passedMs(this.delay.getValueInt()) && !place) {
            return;
        }
        if (this.selfGround.getValue() && !AutoWeb.mc.player.isOnGround()) {
            return;
        }
        this.directionVec = null;
        if (this.getWebSlot() == -1) {
            return;
        }
        if (this.usingPause.getValue() && AutoWeb.mc.player.isUsingItem()) {
            return;
        }
        block0: for (PlayerEntity player : CombatUtil.getEnemies(this.targetRange.getValue())) {
            Vec3d playerPos;
            Vec3d vec3d = playerPos = this.predictTicks.getValue() > 0.0 ? CombatUtil.getEntityPosVec(player, this.predictTicks.getValueInt()) : player.getPos();
            if (this.leg.getValue()) {
                this.placeWeb(new BlockPosX(playerPos.getX(), playerPos.getY(), playerPos.getZ()));
            }
            if (this.down.getValue()) {
                this.placeWeb(new BlockPosX(playerPos.getX(), playerPos.getY() - 0.8, playerPos.getZ()));
            }
            if (this.face.getValue()) {
                this.placeWeb(new BlockPosX(playerPos.getX(), playerPos.getY() + 1.2, playerPos.getZ()));
            }
            boolean skip = false;
            if (!this.extend.getValue()) continue;
            for (float x : new float[]{0.0f, 0.3f, -0.3f}) {
                for (float z : new float[]{0.0f, 0.3f, -0.3f}) {
                    for (float y : new float[]{0.0f, 1.0f, -1.0f}) {
                        BlockPosX pos = new BlockPosX(playerPos.getX() + (double)x, playerPos.getY() + (double)y, playerPos.getZ() + (double)z);
                        if (!this.isTargetHere(pos, player) || AutoWeb.mc.world.getBlockState((BlockPos)pos).getBlock() != Blocks.COBWEB) continue;
                        skip = true;
                    }
                }
            }
            if (skip || !this.extend.getValue()) continue;
            for (float x : new float[]{0.0f, 0.3f, -0.3f}) {
                for (float z : new float[]{0.0f, 0.3f, -0.3f}) {
                    BlockPosX pos = new BlockPosX(playerPos.getX() + (double)x, playerPos.getY(), playerPos.getZ() + (double)z);
                    if (!pos.equals((Object)new BlockPosX(playerPos.getX(), playerPos.getY(), playerPos.getZ())) && this.isTargetHere(pos, player) && this.placeWeb(pos)) continue block0;
                }
            }
        }
    }

    private boolean isTargetHere(BlockPos pos, PlayerEntity target) {
        return new Box(pos).intersects(target.getBoundingBox());
    }

    private boolean placeWeb(BlockPos pos) {
        if (this.pos.contains((Object)pos)) {
            return false;
        }
        this.pos.add(pos);
        if (this.progress >= this.multiPlace.getValueInt()) {
            return false;
        }
        if (this.getWebSlot() == -1) {
            return false;
        }
        if (this.checkMine.getValue() && (Rebirth.BREAK.isMining(pos) || !this.noMine.getValue() && pos.equals((Object)PacketMine.breakPos))) {
            return false;
        }
        if (BlockUtil.getPlaceSide(pos, this.placeRange.getValue()) != null && AutoWeb.mc.world.isAir(pos)) {
            int oldSlot = AutoWeb.mc.player.getInventory().selectedSlot;
            int webSlot = this.getWebSlot();
            if (!this.placeBlock(pos, this.rotate.getValue(), webSlot)) {
                return false;
            }
            if (this.noMine.getValue() && pos.equals((Object)PacketMine.breakPos)) {
                PacketMine.breakPos = null;
            }
            ++this.progress;
            this.doSwap(oldSlot);
            this.timer.reset();
            place = false;
            return true;
        }
        return false;
    }

    public boolean placeBlock(BlockPos pos, boolean rotate, int slot) {
        Direction side;
        if (BlockUtil.airPlace()) {
            for (Direction i : Direction.values()) {
                if (!AutoWeb.mc.world.isAir(pos.offset(i))) continue;
                return this.clickBlock(pos, i, rotate, slot);
            }
        }
        if ((side = BlockUtil.getPlaceSide(pos)) == null) {
            return false;
        }
        BlockUtil.placedPos.add(pos);
        PlaceRender.addBlock(pos);
        return this.clickBlock(pos.offset(side), side.getOpposite(), rotate, slot);
    }

    public boolean clickBlock(BlockPos pos, Direction side, boolean rotate, int slot) {
        Vec3d directionVec = new Vec3d((double)pos.getX() + 0.5 + (double)side.getVector().getX() * 0.5, (double)pos.getY() + 0.5 + (double)side.getVector().getY() * 0.5, (double)pos.getZ() + 0.5 + (double)side.getVector().getZ() * 0.5);
        if (rotate && !this.faceVector(directionVec)) {
            return false;
        }
        this.doSwap(slot);
        EntityUtil.swingHand(Hand.MAIN_HAND, (SwingMode)Rebirth.HUD.swingMode.getValue());
        BlockHitResult result = new BlockHitResult(directionVec, side, pos, false);
        AutoWeb.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, BlockUtil.getWorldActionId(AutoWeb.mc.world)));
        return true;
    }

    private boolean faceVector(Vec3d directionVec) {
        this.directionVec = directionVec;
        float[] angle = EntityUtil.getLegitRotations(directionVec);
        if (Math.abs(MathHelper.wrapDegrees((float)(angle[0] - this.lastYaw))) < this.fov.getValueFloat() && Math.abs(MathHelper.wrapDegrees((float)(angle[1] - this.lastPitch))) < this.fov.getValueFloat()) {
            EntityUtil.sendYawAndPitch(angle[0], angle[1]);
            return true;
        }
        return !this.checkLook.getValue();
    }

    private float[] injectStep(float[] angle, float steps) {
        if (steps < 0.01f) {
            steps = 0.01f;
        }
        if (steps > 1.0f) {
            steps = 1.0f;
        }
        if (steps < 1.0f && angle != null) {
            float packetPitch;
            float packetYaw = this.lastYaw;
            float diff = MathHelper.wrapDegrees((float)(angle[0] - packetYaw));
            if (Math.abs(diff) > 90.0f * steps) {
                angle[0] = packetYaw + diff * (90.0f * steps / Math.abs(diff));
            }
            if (Math.abs(diff = angle[1] - (packetPitch = this.lastPitch)) > 90.0f * steps) {
                angle[1] = packetPitch + diff * (90.0f * steps / Math.abs(diff));
            }
        }
        return new float[]{angle[0], angle[1]};
    }

    private void doSwap(int slot) {
        InventoryUtil.doSwap(slot);
    }

    private int getWebSlot() {
        return InventoryUtil.findBlock(Blocks.COBWEB);
    }

    public static enum Page {
        General,
        Rotate;

        // $FF: synthetic method
        private static AutoWeb.Page[] $values() {
            return new AutoWeb.Page[]{General, Rotate};
        }
    }
}
