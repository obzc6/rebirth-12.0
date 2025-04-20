/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.world.World
 */
package me.rebirthclient.mod.modules.combat;

import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.events.impl.UpdateWalkingEvent;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.DamageUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.MeteorDamageUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.asm.accessors.IPlayerMoveC2SPacket;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoCrystal;
import me.rebirthclient.mod.settings.SwingMode;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class AutoAnchor
extends Module {
    public static AutoAnchor INSTANCE;
    public final EnumSetting page = this.add(new EnumSetting("Page", Page.General));
    public final SliderSetting range = this.add(new SliderSetting("Range", 5.0, 0.0, 6.0, 0.1, v -> this.page.getValue() == Page.General));
    public final SliderSetting targetRange = this.add(new SliderSetting("TargetRange", 8.0, 0.0, 16.0, 0.1, v -> this.page.getValue() == Page.General));
    private final BooleanSetting breakCrystal = this.add(new BooleanSetting("BreakCrystal", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting mineSpam = this.add(new BooleanSetting("MineSpam", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting spam = this.add(new BooleanSetting("Spam", false, v -> this.page.getValue() == Page.General));
    private final BooleanSetting spamPlace = this.add(new BooleanSetting("SpamPlace", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting inSpam = this.add(new BooleanSetting("InSpam", true, v -> this.page.getValue() == Page.General && this.spamPlace.getValue()));
    private final BooleanSetting variableSpeed = this.add(new BooleanSetting("VariableSpeed", false, v -> this.page.getValue() == Page.General));
    private final BooleanSetting usingPause = this.add(new BooleanSetting("UsingPause", true, v -> this.page.getValue() == Page.General));
    private final EnumSetting swingMode = this.add(new EnumSetting("Swing", SwingMode.Server, v -> this.page.getValue() == Page.General));
    private final SliderSetting placeDelay = this.add(new SliderSetting("PlaceDelay", 0.0, 0.0, 0.5, 0.01, v -> this.page.getValue() == Page.General));
    private final SliderSetting chargingDelay = this.add(new SliderSetting("ChargingDelay", 0.0, 0.0, 0.5, 0.01, v -> this.page.getValue() == Page.General));
    private final SliderSetting breakDelay = this.add(new SliderSetting("BreakDelay", 0.0, 0.0, 0.5, 0.01, v -> this.page.getValue() == Page.General));
    private final SliderSetting spamDelay = this.add(new SliderSetting("SpamDelay", 0.0, 0.0, 0.5, 0.01, v -> this.page.getValue() == Page.General));
    private final SliderSetting calcDelay = this.add(new SliderSetting("CalcDelay", 0.0, 0.0, 0.5, 0.01, v -> this.page.getValue() == Page.General));
    private final SliderSetting updateDelay = this.add(new SliderSetting("UpdateDelay", 50, 0, 1000, v -> this.page.getValue() == Page.General));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true, v -> this.page.getValue() == Page.Rotate).setParent());
    private final BooleanSetting newRotate = this.add(new BooleanSetting("NewRotate", true, v -> this.rotate.isOpen() && this.page.getValue() == Page.Rotate));
    private final SliderSetting yawStep = this.add(new SliderSetting("YawStep", 0.3f, 0.1f, 1.0, 0.01, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting sync = this.add(new BooleanSetting("Sync", false, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting checkLook = this.add(new BooleanSetting("CheckLook", true, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final SliderSetting fov = this.add(new SliderSetting("Fov", 5.0, 0.0, 30.0, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.checkLook.getValue() && this.page.getValue() == Page.Rotate));
    private final EnumSetting calcMode = this.add(new EnumSetting("CalcMode", CalcMode.Meteor, v -> this.page.getValue() == Page.Calc));
    private final BooleanSetting noSuicide = this.add(new BooleanSetting("NoSuicide", true, v -> this.page.getValue() == Page.Calc));
    private final BooleanSetting terrainIgnore = this.add(new BooleanSetting("TerrainIgnore", true, v -> this.page.getValue() == Page.Calc));
    public final SliderSetting minDamage = this.add(new SliderSetting("PlaceMin", 4.0, 0.0, 36.0, 0.1, v -> this.page.getValue() == Page.Calc));
    public final SliderSetting breakMin = this.add(new SliderSetting("BreakMin", 4.0, 0.0, 36.0, 0.1, v -> this.page.getValue() == Page.Calc));
    public final SliderSetting headDamage = this.add(new SliderSetting("HeadDamage", 7.0, 0.0, 36.0, 0.1, v -> this.page.getValue() == Page.Calc));
    private final SliderSetting minPrefer = this.add(new SliderSetting("MinPrefer", 7.0, 0.0, 36.0, 0.1, v -> this.page.getValue() == Page.Calc));
    private final SliderSetting maxSelfDamage = this.add(new SliderSetting("MaxSelf", 8.0, 0.0, 36.0, 0.1, v -> this.page.getValue() == Page.Calc));
    public final SliderSetting predictTicks = this.add(new SliderSetting("PredictTicks", 2.0, 0.0, 50.0, 1.0, v -> this.page.getValue() == Page.Calc));
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100), v -> this.page.getValue() == Page.Render));
    private final BooleanSetting outline = this.add(new BooleanSetting("Outline", false, v -> this.page.getValue() == Page.Render));
    private final BooleanSetting box = this.add(new BooleanSetting("Fill", true, v -> this.page.getValue() == Page.Render));
    private final Timer updateTimer = new Timer().reset();
    private final Timer delayTimer = new Timer().reset();
    private final Timer calcTimer = new Timer().reset();
    public Vec3d directionVec = null;
    private float lastYaw = 0.0f;
    private float lastPitch = 0.0f;
    private final ArrayList<BlockPos> chargeList = new ArrayList();
    public BlockPos currentPos;

    public AutoAnchor() {
        super("AutoAnchor", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        this.update();
        if (this.currentPos != null) {
            Render3DUtil.draw3DBox(matrixStack, new Box(this.currentPos), this.color.getValue(), this.outline.getValue(), this.box.getValue());
        }
    }

    @EventHandler
    public void onRotate(RotateEvent event) {
        if (this.currentPos != null && this.newRotate.getValue() && this.directionVec != null) {
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

    @EventHandler(priority=-199)
    public void onPacketSend(PacketEvent.Send event) {
        Object t;
        if (event.isCancel()) {
            return;
        }
        if (this.newRotate.getValue() && this.currentPos != null && this.directionVec != null && !EntityUtil.rotating && Rebirth.HUD.rotatePlus.getValue() && (t = event.getPacket()) instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)t;
            if (!packet.changesLook()) {
                return;
            }
            float yaw = packet.getYaw(114514.0f);
            float pitch = packet.getPitch(114514.0f);
            if (yaw == AutoAnchor.mc.player.getYaw() && pitch == AutoAnchor.mc.player.getPitch()) {
                float[] angle = this.injectStep(EntityUtil.getLegitRotations(this.directionVec), this.yawStep.getValueFloat());
                ((IPlayerMoveC2SPacket)event.getPacket()).setYaw(angle[0]);
                ((IPlayerMoveC2SPacket)event.getPacket()).setPitch(angle[1]);
            }
        }
    }

    @Override
    public void onDisable() {
        this.lastYaw = Rebirth.RUN.lastYaw;
        this.lastPitch = Rebirth.RUN.lastPitch;
        this.currentPos = null;
    }

    @Override
    public void onEnable() {
        this.lastYaw = Rebirth.RUN.lastYaw;
        this.lastPitch = Rebirth.RUN.lastPitch;
        this.currentPos = null;
    }

    @EventHandler
    public void onUpdateWalking(UpdateWalkingEvent event) {
        this.update();
    }

    @Override
    public void onUpdate() {
        this.update();
    }

    public boolean onInvis(PlayerEntity enemy) {
        return this.currentPos != enemy.getBlockPos().add(1, 0, 0) && this.currentPos != enemy.getBlockPos().add(-1, 0, 0) && this.currentPos != enemy.getBlockPos().add(0, 0, 1) && this.currentPos != enemy.getBlockPos().add(0, 0, -1) && this.currentPos != enemy.getBlockPos().add(0, 2, 0) && this.currentPos != enemy.getBlockPos().add(0, -1, 0);
    }

    public void update() {
        boolean invis = false;
        if (!this.updateTimer.passedMs((long)this.updateDelay.getValue())) {
            return;
        }
        int anchor = InventoryUtil.findBlock(Blocks.RESPAWN_ANCHOR);
        int glowstone = InventoryUtil.findBlock(Blocks.GLOWSTONE);
        int old = AutoAnchor.mc.player.getInventory().selectedSlot;
        if (anchor == -1) {
            this.currentPos = null;
            return;
        }
        if (glowstone == -1) {
            this.currentPos = null;
            return;
        }
        int unBlock = InventoryUtil.findUnBlock();
        if (unBlock == -1) {
            this.currentPos = null;
            return;
        }
        if (AutoAnchor.mc.player.isSneaking()) {
            this.currentPos = null;
            return;
        }
        if (this.usingPause.getValue() && AutoAnchor.mc.player.isUsingItem()) {
            this.currentPos = null;
            return;
        }
        this.updateTimer.reset();
        PlayerAndPredict selfPredict = new PlayerAndPredict((PlayerEntity)AutoAnchor.mc.player);
        if (this.calcTimer.passed((long)(this.calcDelay.getValueFloat() * 1000.0f))) {
            this.calcTimer.reset();
            this.currentPos = null;
            double placeDamage = this.minDamage.getValue();
            double breakDamage = this.breakMin.getValue();
            boolean anchorFound = false;
            List<PlayerEntity> enemies = CombatUtil.getEnemies(this.targetRange.getValue());
            ArrayList<PlayerAndPredict> list = new ArrayList<PlayerAndPredict>();
            for (PlayerEntity player : enemies) {
                list.add(new PlayerAndPredict(player));
            }
            for (PlayerAndPredict pap : list) {
                double selfDamage;
                BlockPos pos = EntityUtil.getEntityPos((Entity)pap.player, true).up(2);
                if (!BlockUtil.canPlace(pos, this.range.getValue(), this.breakCrystal.getValue()) && (BlockUtil.getBlock(pos) != Blocks.RESPAWN_ANCHOR || BlockUtil.getClickSideStrict(pos) == null) || (selfDamage = this.getAnchorDamage(pos, selfPredict.player, selfPredict.predict)) > this.maxSelfDamage.getValue() || this.noSuicide.getValue() && selfDamage > (double)(AutoAnchor.mc.player.getHealth() + AutoAnchor.mc.player.getAbsorptionAmount()) || !(this.getAnchorDamage(pos, pap.player, pap.predict) > (double)this.headDamage.getValueFloat())) continue;
                invis = this.onInvis(pap.predict);
                this.currentPos = pos;
                break;
            }
            if (this.currentPos == null) {
                for (BlockPos pos : BlockUtil.getSphere(this.range.getValueFloat())) {
                    for (PlayerAndPredict pap : list) {
                        double selfDamage;
                        BlockState preState2 = BlockUtil.getState(pos);
                        AutoAnchor.mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        boolean skip2 = AutoAnchor.behind(pos.toCenterPos(), pap.predict.getPos());
                        AutoAnchor.mc.world.setBlockState(pos, preState2);
                        if (skip2 || (selfDamage = this.getAnchorDamage(pos, selfPredict.player, selfPredict.predict)) > this.maxSelfDamage.getValue() || this.noSuicide.getValue() && selfDamage > (double)(AutoAnchor.mc.player.getHealth() + AutoAnchor.mc.player.getAbsorptionAmount())) continue;
                        if (BlockUtil.getBlock(pos) != Blocks.RESPAWN_ANCHOR) {
                            double damage;
                            if (anchorFound || !BlockUtil.canPlace(pos, this.range.getValue(), this.breakCrystal.getValue())) continue;
                            BlockState preState = BlockUtil.getState(pos);
                            AutoAnchor.mc.world.setBlockState(pos, Blocks.RESPAWN_ANCHOR.getDefaultState());
                            boolean skip = BlockUtil.getClickSideStrict(pos) == null;
                            AutoAnchor.mc.world.setBlockState(pos, preState);
                            if (skip || !((damage = this.getAnchorDamage(pos, pap.player, pap.predict)) >= placeDamage) || AutoCrystal.placePos != null && !AutoCrystal.INSTANCE.isOff() && !((double)AutoCrystal.INSTANCE.lastDamage < damage)) continue;
                            invis = this.onInvis(pap.predict);
                            placeDamage = damage;
                            this.currentPos = pos;
                            continue;
                        }
                        double damage = this.getAnchorDamage(pos, pap.player, pap.predict);
                        if (BlockUtil.getClickSideStrict(pos) == null || !(damage >= breakDamage)) continue;
                        if (damage >= this.minPrefer.getValue()) {
                            anchorFound = true;
                        }
                        if (!anchorFound && damage < placeDamage || AutoCrystal.placePos != null && !AutoCrystal.INSTANCE.isOff() && !((double)AutoCrystal.INSTANCE.lastDamage < damage)) continue;
                        invis = this.onInvis(pap.predict);
                        breakDamage = damage;
                        this.currentPos = pos;
                    }
                }
            }
        }
        if (this.currentPos != null) {
            boolean spam;
            if (this.breakCrystal.getValue()) {
                CombatUtil.attackCrystal(new BlockPos((Vec3i)this.currentPos), this.rotate.getValue(), false);
            }
            boolean bl = spam = this.spam.getValue() || this.mineSpam.getValue() && Rebirth.BREAK.isMining(this.currentPos);
            if (spam) {
                if (!this.delayTimer.passed((long)(this.spamDelay.getValueFloat() * 1000.0f))) {
                    return;
                }
                this.delayTimer.reset();
                if (BlockUtil.canPlace(this.currentPos, this.range.getValue(), this.breakCrystal.getValue())) {
                    this.placeBlock(this.currentPos, this.rotate.getValue(), anchor);
                }
                if (!this.chargeList.contains((Object)this.currentPos)) {
                    this.delayTimer.reset();
                    this.clickBlock(this.currentPos, BlockUtil.getClickSide(this.currentPos), this.rotate.getValue(), glowstone);
                    this.chargeList.add(this.currentPos);
                }
                this.chargeList.remove((Object)this.currentPos);
                this.clickBlock(this.currentPos, BlockUtil.getClickSide(this.currentPos), this.rotate.getValue(), unBlock);
                if (this.variableSpeed.getValue()) {
                    if (this.spamPlace.getValue() && !invis) {
                        BlockState preState = BlockUtil.getState(this.currentPos);
                        AutoAnchor.mc.world.setBlockState(this.currentPos, Blocks.AIR.getDefaultState());
                        this.placeBlock(this.currentPos, this.rotate.getValue(), anchor);
                        AutoAnchor.mc.world.setBlockState(this.currentPos, preState);
                    }
                } else if (this.spamPlace.getValue() && !this.inSpam.getValue()) {
                    BlockState preState = BlockUtil.getState(this.currentPos);
                    AutoAnchor.mc.world.setBlockState(this.currentPos, Blocks.AIR.getDefaultState());
                    this.placeBlock(this.currentPos, this.rotate.getValue(), anchor);
                    AutoAnchor.mc.world.setBlockState(this.currentPos, preState);
                }
            } else if (BlockUtil.canPlace(this.currentPos, this.range.getValue(), this.breakCrystal.getValue())) {
                if (!this.delayTimer.passed((long)(this.placeDelay.getValueFloat() * 1000.0f))) {
                    return;
                }
                this.delayTimer.reset();
                this.placeBlock(this.currentPos, this.rotate.getValue(), anchor);
            } else if (BlockUtil.getBlock(this.currentPos) == Blocks.RESPAWN_ANCHOR) {
                if (!this.chargeList.contains((Object)this.currentPos)) {
                    if (!this.delayTimer.passed((long)(this.chargingDelay.getValueFloat() * 1000.0f))) {
                        return;
                    }
                    this.delayTimer.reset();
                    this.clickBlock(this.currentPos, BlockUtil.getClickSide(this.currentPos), this.rotate.getValue(), glowstone);
                    this.chargeList.add(this.currentPos);
                } else {
                    if (!this.delayTimer.passed((long)(this.breakDelay.getValueFloat() * 1000.0f))) {
                        return;
                    }
                    this.delayTimer.reset();
                    this.chargeList.remove((Object)this.currentPos);
                    this.clickBlock(this.currentPos, BlockUtil.getClickSide(this.currentPos), this.rotate.getValue(), unBlock);
                    if (this.spamPlace.getValue()) {
                        BlockState preState = BlockUtil.getState(this.currentPos);
                        AutoAnchor.mc.world.setBlockState(this.currentPos, Blocks.AIR.getDefaultState());
                        this.placeBlock(this.currentPos, this.rotate.getValue(), anchor);
                        AutoAnchor.mc.world.setBlockState(this.currentPos, preState);
                    }
                }
            }
            InventoryUtil.doSwap(old);
        }
    }

    public static boolean behind(Vec3d from, Vec3d to) {
        BlockHitResult result = AutoAnchor.mc.world.raycast(new RaycastContext(from, to, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)AutoAnchor.mc.player));
        return result != null && result.getType() != HitResult.Type.MISS;
    }

    public double getAnchorDamage(BlockPos anchorPos, PlayerEntity target, PlayerEntity predict) {
        if (this.terrainIgnore.getValue()) {
            CombatUtil.terrainIgnore = true;
        }
        double damage = 0.0;
        switch ((CalcMode)this.calcMode.getValue()) {
            case Meteor: {
                damage = MeteorDamageUtil.anchorDamage(target, anchorPos, predict);
                break;
            }
            case Thunder: {
                damage = DamageUtil.anchorDamage(anchorPos, target, predict);
            }
        }
        CombatUtil.terrainIgnore = false;
        return damage;
    }

    public void placeBlock(BlockPos pos, boolean rotate, int slot) {
        Direction side;
        if (BlockUtil.airPlace()) {
            for (Direction i : Direction.values()) {
                if (!AutoAnchor.mc.world.isAir(pos.offset(i))) continue;
                this.clickBlock(pos, i, rotate, slot);
                return;
            }
        }
        if ((side = BlockUtil.getPlaceSide(pos)) == null) {
            return;
        }
        BlockUtil.placedPos.add(pos);
        this.clickBlock(pos.offset(side), side.getOpposite(), rotate, slot);
    }

    public void clickBlock(BlockPos pos, Direction side, boolean rotate, int slot) {
        Vec3d directionVec = new Vec3d((double)pos.getX() + 0.5 + (double)side.getVector().getX() * 0.5, (double)pos.getY() + 0.5 + (double)side.getVector().getY() * 0.5, (double)pos.getZ() + 0.5 + (double)side.getVector().getZ() * 0.5);
        if (rotate && !this.faceVector(directionVec)) {
            return;
        }
        InventoryUtil.doSwap(slot);
        EntityUtil.swingHand(Hand.MAIN_HAND, (SwingMode)this.swingMode.getValue());
        BlockHitResult result = new BlockHitResult(directionVec, side, pos, false);
        AutoAnchor.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, BlockUtil.getWorldActionId(AutoAnchor.mc.world)));
    }

    public boolean faceVector(Vec3d directionVec) {
        if (!this.newRotate.getValue()) {
            EntityUtil.faceVector(directionVec);
            return true;
        }
        this.directionVec = directionVec;
        float[] angle = EntityUtil.getLegitRotations(directionVec);
        if (Math.abs(MathHelper.wrapDegrees((float)(angle[0] - this.lastYaw))) < this.fov.getValueFloat() && Math.abs(MathHelper.wrapDegrees((float)(angle[1] - this.lastPitch))) < this.fov.getValueFloat()) {
            if (this.sync.getValue()) {
                EntityUtil.sendYawAndPitch(angle[0], angle[1]);
            }
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

    public static enum Page {
        General,
        Calc,
        Rotate,
        Render;

        // $FF: synthetic method
        private static AutoAnchor.Page[] $values() {
            return new AutoAnchor.Page[]{General, Calc, Rotate, Render};
        }
    }

    public static enum CalcMode {
        Meteor,
        Thunder;

        // $FF: synthetic method
        private static AutoAnchor.CalcMode[] $values() {
            return new AutoAnchor.CalcMode[]{Meteor, Thunder};
        }
    }

    public static class PlayerAndPredict {
        PlayerEntity player;
        PlayerEntity predict;

        public PlayerAndPredict(PlayerEntity player) {
            this.player = player;
            if (AutoAnchor.INSTANCE.predictTicks.getValueFloat() > 0.0f) {
                this.predict = new PlayerEntity((World)Wrapper.mc.world, player.getBlockPos(), player.getYaw(), new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")){

                    public boolean isSpectator() {
                        return false;
                    }

                    public boolean isCreative() {
                        return false;
                    }
                };
                this.predict.setPosition(player.getPos().add(CombatUtil.getMotionVec((Entity)player, AutoAnchor.INSTANCE.predictTicks.getValueInt(), true)));
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

