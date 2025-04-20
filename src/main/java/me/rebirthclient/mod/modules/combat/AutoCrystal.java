/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.collection.DefaultedList
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.events.impl.UpdateWalkingEvent;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.DamageUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.MeteorDamageUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.asm.accessors.IPlayerMoveC2SPacket;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoAnchor;
import me.rebirthclient.mod.modules.combat.AutoWeb;
import me.rebirthclient.mod.modules.combat.BedAura;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.modules.combat.PistonCrystal;
import me.rebirthclient.mod.modules.combat.PullCrystal;
import me.rebirthclient.mod.settings.SwingMode;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
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

public class AutoCrystal
extends Module {
    public static AutoCrystal INSTANCE;
    public static BlockPos placePos;
    private final EnumSetting page = this.add(new EnumSetting("Page", Page.General));
    private final BooleanSetting noUsing = this.add(new BooleanSetting("NoUsing", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting noAnchor = this.add(new BooleanSetting("NoAnchor", true, v -> this.page.getValue() == Page.General));
    private final EnumSetting calcMode = this.add(new EnumSetting("CalcMode", AutoAnchor.CalcMode.Meteor, v -> this.page.getValue() == Page.General));
    private final EnumSetting swingMode = this.add(new EnumSetting("Swing", SwingMode.Server, v -> this.page.getValue() == Page.General));
    private final SliderSetting switchCooldown = this.add(new SliderSetting("SwitchCooldown", 100, 0, 1000, v -> this.page.getValue() == Page.General));
    private final SliderSetting antiSuicide = this.add(new SliderSetting("AntiSuicide", 3.0, 0.0, 10.0, v -> this.page.getValue() == Page.General));
    private final SliderSetting targetRange = this.add(new SliderSetting("TargetRange", 12.0, 0.0, 20.0, v -> this.page.getValue() == Page.General));
    private final SliderSetting updateDelay = this.add(new SliderSetting("UpdateDelay", 50, 0, 1000, v -> this.page.getValue() == Page.General));
    private final SliderSetting calcDelay = this.add(new SliderSetting("CalcDelay", 200, 0, 1000, v -> this.page.getValue() == Page.General));
    private final SliderSetting breakWall = this.add(new SliderSetting("WallRange", 6.0, 0.0, 6.0, v -> this.page.getValue() == Page.General));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true, v -> this.page.getValue() == Page.Rotate).setParent());
    private final BooleanSetting onBreak = this.add(new BooleanSetting("OnBreak", false, v -> this.rotate.isOpen() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting newRotate = this.add(new BooleanSetting("NewRotate", false, v -> this.rotate.isOpen() && this.page.getValue() == Page.Rotate));
    private final SliderSetting yawStep = this.add(new SliderSetting("YawStep", 0.3f, 0.1f, 1.0, 0.01f, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting sync = this.add(new BooleanSetting("Sync", false, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting checkLook = this.add(new BooleanSetting("CheckLook", true, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final SliderSetting fov = this.add(new SliderSetting("Fov", 5.0, 0.0, 30.0, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.checkLook.getValue() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting place = this.add(new BooleanSetting("Place", true, v -> this.page.getValue() == Page.Place));
    private final SliderSetting placeDelay = this.add(new SliderSetting("PlaceDelay", 300, 0, 1000, v -> this.page.getValue() == Page.Place && this.place.getValue()));
    private final SliderSetting placeRange = this.add(new SliderSetting("PlaceRange", 5.0, 0.0, 6.0, v -> this.page.getValue() == Page.Place && this.place.getValue()));
    private final SliderSetting placeMinDamage = this.add(new SliderSetting("PlaceMin", 5.0, 0.0, 36.0, v -> this.page.getValue() == Page.Place && this.place.getValue()));
    private final SliderSetting placeMaxSelf = this.add(new SliderSetting("PlaceSelf", 12.0, 0.0, 36.0, v -> this.page.getValue() == Page.Place && this.place.getValue()));
    private final EnumSetting autoSwap = this.add(new EnumSetting("AutoSwap", SwapMode.OFF, v -> this.page.getValue() == Page.Place && this.place.getValue()));
    private final BooleanSetting extraPlace = this.add(new BooleanSetting("SpamPlace", true, v -> this.page.getValue() == Page.Place && this.place.getValue()));
    private final SliderSetting ignoreTime = this.add(new SliderSetting("IgnoreTime", 400, 0, 1000, v -> this.page.getValue() == Page.Place));
    private final BooleanSetting Break = this.add(new BooleanSetting("Break", true, v -> this.page.getValue() == Page.Break));
    private final SliderSetting breakDelay = this.add(new SliderSetting("BreakDelay", 300, 0, 1000, v -> this.page.getValue() == Page.Break && this.Break.getValue()));
    private final SliderSetting breakRange = this.add(new SliderSetting("BreakRange", 5.0, 0.0, 6.0, v -> this.page.getValue() == Page.Break && this.Break.getValue()));
    private final SliderSetting breakMinDamage = this.add(new SliderSetting("BreakMin", 4.0, 0.0, 36.0, v -> this.page.getValue() == Page.Break && this.Break.getValue()));
    private final SliderSetting breakMaxSelf = this.add(new SliderSetting("BreakSelf", 12.0, 0.0, 36.0, v -> this.page.getValue() == Page.Break && this.Break.getValue()));
    private final BooleanSetting breakOnlyHasCrystal = this.add(new BooleanSetting("OnlyHasCrystal", false, v -> this.page.getValue() == Page.Break && this.Break.getValue()));
    private final BooleanSetting breakRemove = this.add(new BooleanSetting("BreakRemove", false, v -> this.page.getValue() == Page.Break && this.Break.getValue()));
    private final BooleanSetting render = this.add(new BooleanSetting("Render", true, v -> this.page.getValue() == Page.Render));
    public final BooleanSetting targetrender = this.add(new BooleanSetting("TargetRender", true, v -> this.page.getValue() == Page.Render && this.render.getValue()).setParent());
    private final ColorSetting targetcolor = this.add(new ColorSetting("TargetColor", new Color(255, 255, 255, 100), v -> this.targetrender.isOpen() && this.page.getValue() == Page.Render && this.render.getValue()));
    private final BooleanSetting multi = this.add(new BooleanSetting("Multi", false, v -> this.page.getValue() == Page.Render && this.render.getValue()).setParent());
    private final BooleanSetting both = this.add(new BooleanSetting("Both", true, v -> this.page.getValue() == Page.Render && this.render.getValue() && this.multi.isOpen()));
    private final BooleanSetting shrink = this.add(new BooleanSetting("Shrink", true, v -> this.page.getValue() == Page.Render && this.render.getValue()));
    private final BooleanSetting outline = this.add(new BooleanSetting("Outline", true, v -> this.page.getValue() == Page.Render && this.render.getValue()).setParent());
    private final SliderSetting outlineAlpha = this.add(new SliderSetting("OutlineAlpha", 150, 0, 255, v -> this.outline.isOpen() && this.page.getValue() == Page.Render && this.render.getValue()));
    private final BooleanSetting box = this.add(new BooleanSetting("Box", true, v -> this.page.getValue() == Page.Render && this.render.getValue()).setParent());
    private final SliderSetting boxAlpha = this.add(new SliderSetting("BoxAlpha", 70, 0, 255, v -> this.box.isOpen() && this.page.getValue() == Page.Render && this.render.getValue()));
    private final BooleanSetting reset = this.add(new BooleanSetting("Reset", true, v -> this.page.getValue() == Page.Render && this.render.getValue() && (!this.multi.getValue() || this.both.getValue())));
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255), v -> this.page.getValue() == Page.Render && this.render.getValue()));
    private final SliderSetting animationTime = this.add(new SliderSetting("AnimationTime", 2.0, 0.0, 8.0, v -> this.page.getValue() == Page.Render && this.render.getValue() && (!this.multi.getValue() || this.both.getValue())));
    private final SliderSetting startFadeTime = this.add(new SliderSetting("StartFadeTime", 0.3, 0.0, 2.0, 0.01, v -> this.page.getValue() == Page.Render && this.render.getValue()));
    private final SliderSetting fadeTime = this.add(new SliderSetting("FadeTime", 0.3, 0.0, 2.0, 0.01, v -> this.page.getValue() == Page.Render && this.render.getValue()));
    private final BooleanSetting text = this.add(new BooleanSetting("Text", true, v -> this.page.getValue() == Page.Render && this.render.getValue()));
    private final SliderSetting predictTicks = this.add(new SliderSetting("PredictTicks", 4, 0, 10, v -> this.page.getValue() == Page.Predict));
    private final BooleanSetting terrainIgnore = this.add(new BooleanSetting("TerrainIgnore", true, v -> this.page.getValue() == Page.Predict));
    private final BooleanSetting antiSurround = this.add(new BooleanSetting("AntiSurround", true, v -> this.page.getValue() == Page.Misc).setParent());
    private final SliderSetting antiSurroundMax = this.add(new SliderSetting("MaxDMG", 5.0, 0.0, 36.0, v -> this.page.getValue() == Page.Misc && this.antiSurround.isOpen()));
    private final BooleanSetting slowFace = this.add(new BooleanSetting("SlowFace", true, v -> this.page.getValue() == Page.Misc).setParent());
    private final SliderSetting slowDelay = this.add(new SliderSetting("SlowDelay", 600, 0, 2000, v -> this.page.getValue() == Page.Misc && this.slowFace.isOpen()));
    private final SliderSetting slowMinDamage = this.add(new SliderSetting("SlowMin", 3.0, 0.0, 36.0, v -> this.page.getValue() == Page.Misc && this.slowFace.isOpen()));
    private final BooleanSetting forcePlace = this.add(new BooleanSetting("ForcePlace", true, v -> this.page.getValue() == Page.Misc).setParent());
    private final SliderSetting forceMaxHealth = this.add(new SliderSetting("ForceMaxHealth", 5, 0, 36, v -> this.page.getValue() == Page.Misc && this.forcePlace.isOpen()));
    private final SliderSetting forceMin = this.add(new SliderSetting("ForceMin", 3.0, 0.0, 36.0, v -> this.page.getValue() == Page.Misc && this.forcePlace.isOpen()));
    private final BooleanSetting armorBreaker = this.add(new BooleanSetting("ArmorBreaker", true, v -> this.page.getValue() == Page.Misc).setParent());
    private final SliderSetting maxDura = this.add(new SliderSetting("MaxDura", 8, 0, 100, v -> this.page.getValue() == Page.Misc && this.armorBreaker.isOpen()));
    private final SliderSetting armorBreakerDamage = this.add(new SliderSetting("BreakerDamage", 3.0, 0.0, 36.0, v -> this.page.getValue() == Page.Misc && this.armorBreaker.isOpen()));
    private final BooleanSetting webspam = this.add(new BooleanSetting("WebSpam", true, v -> this.page.getValue() == Page.Misc).setParent());
    private final SliderSetting spamMinDamage = this.add(new SliderSetting("SpamMin", 6.0, 0.0, 36.0, v -> this.page.getValue() == Page.Misc && this.webspam.isOpen()));
    private final Timer switchTimer = new Timer();
    private final Timer delayTimer = new Timer();
    private final Timer calcTimer = new Timer();
    public static final Timer placeTimer;
    public final Timer lastBreakTimer = new Timer();
    private final Timer noPosTimer = new Timer();
    private final FadeUtils fadeUtils = new FadeUtils(500L);
    private final FadeUtils animation = new FadeUtils(500L);
    double lastSize = 0.0;
    private PlayerEntity displayTarget;
    private BlockPos displaypos;
    private float lastYaw = 0.0f;
    private float lastPitch = 0.0f;
    private int lastHotbar = -1;
    public float lastDamage = -1.0f;
    public float maxSelf = -1.0f;
    public Vec3d directionVec = null;
    private BlockPos renderPos = null;
    private Box lastBB = null;
    private Box nowBB = null;
    private final HashMap<BlockPos, renderBlock> renderMap = new HashMap();

    public AutoCrystal() {
        super("sysedgeAura", "Recode", Module.Category.Combat);
        INSTANCE = this;
    }

    public boolean canPlaceCrystal(BlockPos pos, boolean ignoreCrystal, boolean ignoreItem) {
        BlockPos obsPos = pos.down();
        BlockPos boost = obsPos.up();
        return (BlockUtil.getBlock(obsPos) == Blocks.BEDROCK || BlockUtil.getBlock(obsPos) == Blocks.OBSIDIAN) && BlockUtil.getClickSideStrict(obsPos) != null && !BlockUtil.hasEntityBlockCrystal(boost, ignoreCrystal, ignoreItem) && !BlockUtil.hasEntityBlockCrystal(boost.up(), ignoreCrystal, ignoreItem) && (BlockUtil.getBlock(boost) == Blocks.AIR || BlockUtil.hasEntityBlockCrystal(boost, false, ignoreItem) && BlockUtil.getBlock(boost) == Blocks.FIRE);
    }

    public boolean behindWall(BlockPos pos) {
        Vec3d testVec = new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 1.7, (double)pos.getZ() + 0.5);
        BlockHitResult result = AutoCrystal.mc.world.raycast(new RaycastContext(EntityUtil.getEyesPos(), testVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)AutoCrystal.mc.player));
        if (result == null || result.getType() == HitResult.Type.MISS) {
            return false;
        }
        return MathHelper.sqrt((float)((float)EntityUtil.getEyesPos().squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5))) > this.breakWall.getValueFloat();
    }

    @Override
    public void onDisable() {
        this.lastYaw = Rebirth.RUN.lastYaw;
        this.lastPitch = Rebirth.RUN.lastPitch;
    }

    @Override
    public void onEnable() {
        this.lastYaw = Rebirth.RUN.lastYaw;
        this.lastPitch = Rebirth.RUN.lastPitch;
        this.lastBreakTimer.reset();
    }

    @EventHandler
    public void onUpdateWalking(UpdateWalkingEvent event) {
        this.update();
    }

    @Override
    public void onUpdate() {
        this.update();
    }

    @Override
    public String getInfo() {
        return this.displayTarget == null ? null : this.displayTarget.getName().getString() + ", " + placeTimer.getPassedTimeMs() + "ms, " + String.format("%.1f", Float.valueOf(this.lastDamage));
    }

    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        double quad;
        double d = quad = this.noPosTimer.passedMs(this.startFadeTime.getValue() * 1000.0) ? this.fadeUtils.easeOutQuad() : 0.0;
        if (this.text.getValue() && this.nowBB != null && quad < 0.5) {
            DecimalFormat df = new DecimalFormat("0.0");
            TextUtil.drawText(drawContext, df.format(this.lastDamage) + "/" + df.format(this.maxSelf), this.nowBB.getCenter());
        }
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        this.update();
        if (this.displayTarget != null && this.targetrender.getValue()) {
            Render3DUtil.drawJello(matrixStack, (Entity)this.displayTarget, this.targetcolor.getValue());
        }
        if (this.multi.getValue()) {
            boolean clear = true;
            for (renderBlock block : this.renderMap.values()) {
                if (block.pos == null) {
                    return;
                }
                if (block.pos.equals((Object)placePos) && !block.timer.passedMs((long)(this.startFadeTime.getValue() * 1000.0))) {
                    block.fadeUtils.reset();
                }
                if (block.fadeUtils.easeOutQuad() >= 1.0) continue;
                clear = false;
                Box bb = new Box(block.pos.down());
                if (this.shrink.getValue()) {
                    bb = bb.shrink(block.fadeUtils.easeOutQuad() * 0.5, block.fadeUtils.easeOutQuad() * 0.5, block.fadeUtils.easeOutQuad() * 0.5);
                    bb = bb.shrink(-block.fadeUtils.easeOutQuad() * 0.5, -block.fadeUtils.easeOutQuad() * 0.5, -block.fadeUtils.easeOutQuad() * 0.5);
                }
                if (this.box.getValue()) {
                    Render3DUtil.drawBBFill(matrixStack, bb, ColorUtil.injectAlpha(this.color.getValue(), (int)(this.boxAlpha.getValue() * Math.abs(block.fadeUtils.easeOutQuad() - 1.0))));
                }
                if (!this.outline.getValue()) continue;
                Render3DUtil.drawBBBox(matrixStack, bb, ColorUtil.injectAlpha(this.color.getValue(), (int)(this.outlineAlpha.getValue() * Math.abs(block.fadeUtils.easeOutQuad() - 1.0))));
            }
            if (clear) {
                this.renderMap.clear();
            }
        }
        if (!this.multi.getValue() || this.both.getValue()) {
            double quad;
            double d = quad = this.noPosTimer.passedMs(this.startFadeTime.getValue() * 1000.0) ? this.fadeUtils.easeOutQuad() : 0.0;
            if (this.nowBB != null && this.render.getValue() && quad < 1.0) {
                Box bb = this.nowBB;
                if (this.shrink.getValue()) {
                    bb = this.nowBB.shrink(quad * 0.5, quad * 0.5, quad * 0.5);
                    bb = bb.shrink(-quad * 0.5, -quad * 0.5, -quad * 0.5);
                }
                if (this.box.getValue()) {
                    Render3DUtil.drawBBFill(matrixStack, bb, ColorUtil.injectAlpha(this.color.getValue(), (int)(this.boxAlpha.getValue() * Math.abs(quad - 1.0))));
                }
                if (this.outline.getValue()) {
                    Render3DUtil.drawBBBox(matrixStack, bb, ColorUtil.injectAlpha(this.color.getValue(), (int)(this.outlineAlpha.getValue() * Math.abs(quad - 1.0))));
                }
            } else if (this.reset.getValue()) {
                this.nowBB = null;
            }
        }
    }

    @EventHandler
    public void onRotate(RotateEvent event) {
        if (placePos != null && this.newRotate.getValue() && this.directionVec != null) {
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

    private void update() {
        if (AutoCrystal.nullCheck()) {
            return;
        }
        this.animUpdate();
        if (!this.delayTimer.passedMs((long)this.updateDelay.getValue())) {
            return;
        }
        if (this.noUsing.getValue() && EntityUtil.isUsing()) {
            this.lastBreakTimer.reset();
            this.displayTarget = null;
            placePos = null;
            return;
        }
        if (!this.switchTimer.passedMs((long)this.switchCooldown.getValue())) {
            placePos = null;
            return;
        }
        if (this.breakOnlyHasCrystal.getValue() && !AutoCrystal.mc.player.getMainHandStack().getItem().equals((Object)Items.END_CRYSTAL) && !AutoCrystal.mc.player.getOffHandStack().getItem().equals((Object)Items.END_CRYSTAL) && !this.findCrystal()) {
            placePos = null;
            this.displayTarget = null;
            return;
        }
        this.delayTimer.reset();
        if (this.calcTimer.passedMs(this.calcDelay.getValueInt())) {
            float selfDamage;
            float damage;
            BlockPosX breakPos = null;
            this.calcTimer.reset();
            placePos = null;
            this.lastDamage = 0.0f;
            ArrayList<PlayerAndPredict> list = new ArrayList<PlayerAndPredict>();
            for (PlayerEntity target : CombatUtil.getEnemies(this.targetRange.getRange())) {
                list.add(new PlayerAndPredict(target));
            }
            PlayerAndPredict self = new PlayerAndPredict((PlayerEntity)AutoCrystal.mc.player);
            if (list.isEmpty()) {
                this.lastBreakTimer.reset();
            }
            for (Entity crystal : AutoCrystal.mc.world.getEntities()) {
                if (!(crystal instanceof EndCrystalEntity) || EntityUtil.getEyesPos().distanceTo(crystal.getPos()) > this.breakRange.getValue() || !AutoCrystal.mc.player.canSee(crystal) && (double)AutoCrystal.mc.player.distanceTo(crystal) > this.breakWall.getValue()) continue;
                for (PlayerAndPredict pap : list) {
                    damage = this.calculateDamage(crystal.getPos(), pap.player, pap.predict);
                    selfDamage = this.calculateDamage(crystal.getPos(), self.player, self.predict);
                    if ((double)selfDamage > this.breakMaxSelf.getValue() || this.antiSuicide.getValue() > 0.0 && (double)selfDamage > (double)(AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount()) - this.antiSuicide.getValue() || damage < EntityUtil.getHealth((Entity)pap.player) && (double)damage < this.getBreakDamage(pap.player) || breakPos != null && !(damage > this.lastDamage)) continue;
                    this.displayTarget = pap.player;
                    breakPos = new BlockPosX(crystal.getPos());
                    this.lastDamage = damage;
                    this.maxSelf = selfDamage;
                }
            }
            if (AutoCrystal.mc.player.getMainHandStack().getItem().equals((Object)Items.END_CRYSTAL) || AutoCrystal.mc.player.getOffHandStack().getItem().equals((Object)Items.END_CRYSTAL) || this.findCrystal()) {
                for (BlockPos pos : BlockUtil.getSphere((float)this.placeRange.getValue() + 1.0f)) {
                    this.lastBreakTimer.passedMs(this.ignoreTime.getValueInt());
                    if (!this.canPlaceCrystal(pos, !this.lastBreakTimer.passedMs(this.ignoreTime.getValueInt()), false) || this.behindWall(pos) || !this.canTouch(pos.down())) continue;
                    for (PlayerAndPredict pap : list) {
                        damage = this.calculateDamage(pos, pap.player, pap.predict);
                        selfDamage = this.calculateDamage(pos, self.player, self.predict);
                        if ((double)selfDamage > this.placeMaxSelf.getValue() || this.antiSuicide.getValue() > 0.0 && (double)selfDamage > (double)(AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount()) - this.antiSuicide.getValue() || damage < EntityUtil.getHealth((Entity)pap.player) && (double)damage < this.getPlaceDamage(pap.player) || placePos != null && !(damage > this.lastDamage)) continue;
                        this.displaypos = pos;
                        this.displayTarget = pap.player;
                        placePos = pos;
                        breakPos = null;
                        this.lastDamage = damage;
                        this.maxSelf = selfDamage;
                    }
                }
            }
            if (this.antiSurround.getValue() && PacketMine.breakPos != null && PacketMine.progress >= 0.9 && !BlockUtil.hasEntity(PacketMine.breakPos, false) && this.lastDamage <= this.antiSurroundMax.getValueFloat()) {
                for (PlayerAndPredict pap : list) {
                    for (Direction i : Direction.values()) {
                        float selfDamage2;
                        BlockPos offsetPos;
                        if (i == Direction.DOWN || i == Direction.UP || !(offsetPos = new BlockPosX(pap.player.getPos().add(0.0, 0.5, 0.0)).offset(i)).equals((Object)PacketMine.breakPos)) continue;
                        if (this.canPlaceCrystal(offsetPos.offset(i), false, false) && (double)(selfDamage2 = this.calculateDamage(offsetPos.offset(i), self.player, self.predict)) < this.breakMaxSelf.getValue() && (!(this.antiSuicide.getValue() > 0.0) || !((double)selfDamage2 > (double)(AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount()) - this.antiSuicide.getValue()))) {
                            placePos = offsetPos.offset(i);
                            if (this.noAnchor.getValue() && AutoAnchor.INSTANCE.currentPos != null) {
                                return;
                            }
                            this.doCrystal(placePos);
                            return;
                        }
                        for (Direction ii : Direction.values()) {
                            float selfDamage3;
                            if (ii == Direction.DOWN || ii == i || !this.canPlaceCrystal(offsetPos.offset(ii), false, false) || !((double)(selfDamage3 = this.calculateDamage(offsetPos.offset(ii), self.player, self.predict)) < this.breakMaxSelf.getValue()) || this.antiSuicide.getValue() > 0.0 && (double)selfDamage3 > (double)(AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount()) - this.antiSuicide.getValue()) continue;
                            placePos = offsetPos.offset(ii);
                            if (this.noAnchor.getValue() && AutoAnchor.INSTANCE.currentPos != null) {
                                return;
                            }
                            this.doCrystal(placePos);
                            return;
                        }
                    }
                }
            }
            if (breakPos != null) {
                this.doBreak(breakPos);
                if (this.extraPlace.getValue() && placePos != null) {
                    this.doPlace(placePos);
                }
                return;
            }
        }
        if (this.noAnchor.getValue() && AutoAnchor.INSTANCE.currentPos != null) {
            return;
        }
        if (placePos != null) {
            this.doCrystal(placePos);
        }
        if (placePos == null) {
            this.displayTarget = null;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean canTouch(BlockPos pos) {
        Direction side = BlockUtil.getClickSideStrict(pos);
        if (side == null) return false;
        Vec3d vec3d = new Vec3d((double)side.getVector().getX() * 0.5, (double)side.getVector().getY() * 0.5, (double)side.getVector().getZ() * 0.5);
        if (!(pos.toCenterPos().add(vec3d).distanceTo(AutoCrystal.mc.player.getEyePos()) <= this.placeRange.getValue())) return false;
        return true;
    }

    private void animUpdate() {
        if (!this.multi.getValue() || this.both.getValue()) {
            this.fadeUtils.setLength((long)(this.fadeTime.getValue() * 1000.0));
            if (placePos != null) {
                this.lastBB = new Box(new BlockPos((Vec3i)placePos.down()));
                this.noPosTimer.reset();
                if (this.nowBB == null) {
                    this.nowBB = this.lastBB;
                }
                if (this.renderPos == null || !this.renderPos.equals((Object)placePos)) {
                    this.animation.setLength(this.animationTime.getValue() * 1000.0 <= 0.0 ? 0L : (long)(Math.abs(this.nowBB.minX - this.lastBB.minX) + Math.abs(this.nowBB.minY - this.lastBB.minY) + Math.abs(this.nowBB.minZ - this.lastBB.minZ) <= 5.0 ? (double)((long)((Math.abs(this.nowBB.minX - this.lastBB.minX) + Math.abs(this.nowBB.minY - this.lastBB.minY) + Math.abs(this.nowBB.minZ - this.lastBB.minZ)) * (this.animationTime.getValue() * 1000.0))) : this.animationTime.getValue() * 5000.0));
                    this.animation.reset();
                    this.renderPos = placePos;
                }
            }
            if (!this.noPosTimer.passedMs((long)(this.startFadeTime.getValue() * 1000.0))) {
                this.fadeUtils.reset();
            }
            double size = this.animation.easeOutQuad();
            if (this.nowBB != null && this.lastBB != null) {
                if (Math.abs(this.nowBB.minX - this.lastBB.minX) + Math.abs(this.nowBB.minY - this.lastBB.minY) + Math.abs(this.nowBB.minZ - this.lastBB.minZ) > 16.0) {
                    this.nowBB = this.lastBB;
                }
                if (this.lastSize != size) {
                    this.nowBB = new Box(this.nowBB.minX + (this.lastBB.minX - this.nowBB.minX) * size, this.nowBB.minY + (this.lastBB.minY - this.nowBB.minY) * size, this.nowBB.minZ + (this.lastBB.minZ - this.nowBB.minZ) * size, this.nowBB.maxX + (this.lastBB.maxX - this.nowBB.maxX) * size, this.nowBB.maxY + (this.lastBB.maxY - this.nowBB.maxY) * size, this.nowBB.maxZ + (this.lastBB.maxZ - this.nowBB.maxZ) * size);
                    this.lastSize = size;
                }
            }
        }
    }

    public void doCrystal(BlockPos pos) {
        if (this.canPlaceCrystal(pos, false, true)) {
            if (AutoCrystal.mc.player.getMainHandStack().getItem().equals((Object)Items.END_CRYSTAL) || AutoCrystal.mc.player.getOffHandStack().getItem().equals((Object)Items.END_CRYSTAL) || this.findCrystal()) {
                this.doPlace(pos);
            }
        } else {
            this.doBreak(pos);
        }
    }

    public float calculateDamage(BlockPos pos, PlayerEntity player, PlayerEntity predict) {
        return this.calculateDamage(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5), player, predict);
    }

    public float calculateDamage(Vec3d pos, PlayerEntity player, PlayerEntity predict) {
        if (this.terrainIgnore.getValue()) {
            CombatUtil.terrainIgnore = true;
        }
        float damage = 0.0f;
        switch ((AutoAnchor.CalcMode)this.calcMode.getValue()) {
            case Meteor: {
                damage = (float)MeteorDamageUtil.crystalDamage(player, pos, predict);
                break;
            }
            case Thunder: {
                damage = DamageUtil.calculateDamage(pos, player, predict, 6.0f);
            }
        }
        CombatUtil.terrainIgnore = false;
        return damage;
    }

    private double getPlaceDamage(PlayerEntity target) {
        if (!(PacketMine.INSTANCE.obsidian.isPressed() || !this.slowFace.getValue() || !this.lastBreakTimer.passedMs((long)this.slowDelay.getValue()) || PullCrystal.INSTANCE.isOn() || PistonCrystal.INSTANCE.isOn() || BedAura.INSTANCE.isOn() && BedAura.INSTANCE.getBed() != -1)) {
            return this.slowMinDamage.getValue();
        }
        if (this.forcePlace.getValue() && (double)EntityUtil.getHealth((Entity)target) <= this.forceMaxHealth.getValue() && !PullCrystal.INSTANCE.isOn() && !PistonCrystal.INSTANCE.isOn() && !PacketMine.INSTANCE.obsidian.isPressed()) {
            return this.forceMin.getValue();
        }
        if (this.armorBreaker.getValue()) {
            DefaultedList armors = target.getInventory().armor;
            for (Object armor : armors) {
                if ((double)EntityUtil.getDamagePercent((ItemStack) armor) > this.maxDura.getValue()) continue;
                return this.armorBreakerDamage.getValue();
            }
        }
        return this.placeMinDamage.getValue();
    }

    private double getBreakDamage(PlayerEntity target) {
        if (this.slowFace.getValue() && this.lastBreakTimer.passedMs((long)this.slowDelay.getValue()) && !PullCrystal.INSTANCE.isOn() && !PistonCrystal.INSTANCE.isOn() && !PacketMine.INSTANCE.obsidian.isPressed()) {
            return this.slowMinDamage.getValue();
        }
        if (this.forcePlace.getValue() && (double)EntityUtil.getHealth((Entity)target) <= this.forceMaxHealth.getValue() && !PullCrystal.INSTANCE.isOn() && !PistonCrystal.INSTANCE.isOn() && !PacketMine.INSTANCE.obsidian.isPressed()) {
            return this.forceMin.getValue();
        }
        if (this.armorBreaker.getValue()) {
            DefaultedList armors = target.getInventory().armor;
            for (Object armor : armors) {
                if ( (double)EntityUtil.getDamagePercent((ItemStack) armor) > this.maxDura.getValue()) continue;
                return this.armorBreakerDamage.getValue();
            }
        }
        return this.breakMinDamage.getValue();
    }

    private boolean findCrystal() {
        if (this.autoSwap.getValue() == SwapMode.OFF) {
            return false;
        }
        return this.getCrystal() != -1;
    }

    private void doBreak(BlockPos pos) {
        block8: {
            this.lastBreakTimer.reset();
            if (!this.Break.getValue()) {
                return;
            }
            Iterator iterator = AutoCrystal.mc.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))).iterator();
            if (!iterator.hasNext()) break block8;
            EndCrystalEntity entity = (EndCrystalEntity)iterator.next();
            if (this.multi.getValue()) {
                this.renderMap.put(placePos, new renderBlock(placePos, this.fadeTime.getValue()));
            }
            if (this.rotate.getValue() && this.onBreak.getValue() && !this.faceVector(entity.getPos().add(0.0, 0.25, 0.0))) {
                return;
            }
            if (!CombatUtil.breakTimer.passedMs((long)this.breakDelay.getValue())) {
                return;
            }
            CombatUtil.breakTimer.reset();
            AutoCrystal.mc.player.networkHandler.sendPacket((Packet)PlayerInteractEntityC2SPacket.attack((Entity)entity, (boolean)AutoCrystal.mc.player.isSneaking()));
            EntityUtil.swingHand(Hand.MAIN_HAND, (SwingMode)this.swingMode.getValue());
            if (this.breakRemove.getValue()) {
                AutoCrystal.mc.world.removeEntity(entity.getId(), Entity.RemovalReason.KILLED);
            }
            if (this.webspam.getValue() && (double)this.lastDamage > this.spamMinDamage.getValue()) {
                AutoWeb.place = true;
            }
            if (!placeTimer.passedMs((long)this.placeDelay.getValue()) || !this.extraPlace.getValue()) {
                return;
            }
            if (this.lastDamage >= this.placeMinDamage.getValueFloat() && placePos != null) {
                this.doPlace(placePos);
            }
        }
    }

    private void doPlace(BlockPos pos) {
        if (!this.place.getValue()) {
            return;
        }
        if (!(AutoCrystal.mc.player.getMainHandStack().getItem().equals((Object)Items.END_CRYSTAL) || AutoCrystal.mc.player.getOffHandStack().getItem().equals((Object)Items.END_CRYSTAL) || this.findCrystal())) {
            return;
        }
        if (!this.canTouch(pos.down())) {
            return;
        }
        if (this.multi.getValue()) {
            this.renderMap.put(pos, new renderBlock(pos, this.fadeTime.getValue()));
        }
        BlockPos obsPos = pos.down();
        Direction facing = BlockUtil.getClickSide(obsPos);
        Vec3d vec = obsPos.toCenterPos().add((double)facing.getVector().getX() * 0.5, (double)facing.getVector().getY() * 0.5, (double)facing.getVector().getZ() * 0.5);
        if (this.rotate.getValue() && !this.faceVector(vec)) {
            return;
        }
        if (!placeTimer.passedMs((long)this.placeDelay.getValue())) {
            return;
        }
        placeTimer.reset();
        if (AutoCrystal.mc.player.getMainHandStack().getItem().equals((Object)Items.END_CRYSTAL) || AutoCrystal.mc.player.getOffHandStack().getItem().equals((Object)Items.END_CRYSTAL)) {
            this.placeCrystal(pos);
        } else if (this.findCrystal()) {
            int old = AutoCrystal.mc.player.getInventory().selectedSlot;
            int crystal = this.getCrystal();
            if (crystal == -1) {
                return;
            }
            this.doSwap(crystal);
            this.placeCrystal(pos);
            if (this.autoSwap.getValue() == SwapMode.SILENT) {
                this.doSwap(old);
            } else if (this.autoSwap.getValue() == SwapMode.Inventory) {
                this.doSwap(crystal);
                EntityUtil.sync();
            }
        }
    }

    private void doSwap(int slot) {
        if (this.autoSwap.getValue() == SwapMode.SILENT || this.autoSwap.getValue() == SwapMode.NORMAL) {
            InventoryUtil.doSwap(slot);
        } else if (this.autoSwap.getValue() == SwapMode.Inventory) {
            AutoCrystal.mc.interactionManager.clickSlot(AutoCrystal.mc.player.currentScreenHandler.syncId, slot, AutoCrystal.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AutoCrystal.mc.player);
        }
    }

    private int getCrystal() {
        if (this.autoSwap.getValue() == SwapMode.SILENT || this.autoSwap.getValue() == SwapMode.NORMAL) {
            return InventoryUtil.findItem(Items.END_CRYSTAL);
        }
        if (this.autoSwap.getValue() == SwapMode.Inventory) {
            return InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL);
        }
        return -1;
    }

    public void placeCrystal(BlockPos pos) {
        boolean offhand = AutoCrystal.mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL;
        BlockPos obsPos = pos.down();
        Direction facing = BlockUtil.getClickSide(obsPos);
        BlockUtil.clickBlock(obsPos, facing, false, offhand ? Hand.OFF_HAND : Hand.MAIN_HAND, (SwingMode)this.swingMode.getValue());
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

    static {
        placeTimer = new Timer();
    }

    public static enum Page {
        General,
        Rotate,
        Place,
        Break,
        Misc,
        Predict,
        Render;

        // $FF: synthetic method
        private static AutoCrystal.Page[] $values() {
            return new AutoCrystal.Page[]{General, Rotate, Place, Break, Misc, Predict, Render};
        }
    }

    public static enum SwapMode {
        OFF,
        NORMAL,
        SILENT,
        Inventory;

        // $FF: synthetic method
        private static AutoCrystal.SwapMode[] $values() {
            return new AutoCrystal.SwapMode[]{OFF, NORMAL, SILENT, Inventory};
        }
    }
    public static class renderBlock {
        public final BlockPos pos;
        public final FadeUtils fadeUtils;
        public final Timer timer;

        public renderBlock(BlockPos pos, double fadeTime) {
            this.pos = pos;
            this.fadeUtils = new FadeUtils((long)(fadeTime * 1000.0));
            this.fadeUtils.reset();
            this.timer = new Timer();
            this.timer.reset();
        }
    }

    public class PlayerAndPredict {
        PlayerEntity player;
        PlayerEntity predict;

        public PlayerAndPredict(PlayerEntity player) {
            this.player = player;
            if (AutoCrystal.this.predictTicks.getValueFloat() > 0.0f) {
                this.predict = new PlayerEntity((World)Wrapper.mc.world, player.getBlockPos(), player.getYaw(), new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")){

                    public boolean isSpectator() {
                        return false;
                    }

                    public boolean isCreative() {
                        return false;
                    }
                };
                this.predict.setPosition(player.getPos().add(CombatUtil.getMotionVec((Entity)player, AutoCrystal.INSTANCE.predictTicks.getValueInt(), true)));
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

