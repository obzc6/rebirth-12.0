/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.AirBlockItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.BlockView
 */
package me.rebirthclient.mod.modules.combat;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.ClickBlockEvent;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.asm.accessors.IPlayerMoveC2SPacket;
import me.rebirthclient.mod.gui.components.Component;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoAnchor;
import me.rebirthclient.mod.modules.combat.AutoCrystal;
import me.rebirthclient.mod.modules.combat.PullCrystal;
import me.rebirthclient.mod.modules.combat.SilentDouble;
import me.rebirthclient.mod.settings.SwingMode;
import me.rebirthclient.mod.settings.impl.BindSetting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;

public class PacketMine
extends Module {
    public static final List<Block> godBlocks = Arrays.asList(new Block[]{Blocks.COMMAND_BLOCK, Blocks.LAVA_CAULDRON, Blocks.LAVA, Blocks.WATER_CAULDRON, Blocks.WATER, Blocks.BEDROCK, Blocks.BARRIER, Blocks.END_PORTAL, Blocks.NETHER_PORTAL, Blocks.END_PORTAL_FRAME});
    private final SliderSetting delay = this.add(new SliderSetting("Delay", 100.0, 0.0, 1000.0, 1.0));
    private final SliderSetting damage = this.add(new SliderSetting("Damage", (double)0.7f, 0.0, 2.0, 0.01));
    private final SliderSetting range = this.add(new SliderSetting("Range", 7.0, 3.0, 10.0, 0.1));
    private final SliderSetting maxBreak = this.add(new SliderSetting("MaxBreak", 2.0, 0.0, 20.0, 1.0));
    public final BooleanSetting preferWeb = this.add(new BooleanSetting("PreferWeb", true));
    private final BooleanSetting instant = this.add(new BooleanSetting("Instant", false));
    private final BooleanSetting cancelPacket = this.add(new BooleanSetting("CancelPacket", false));
    private final BooleanSetting wait = this.add(new BooleanSetting("Wait", true, v -> !this.instant.getValue()));
    private final BooleanSetting mineAir = this.add(new BooleanSetting("MineAir", true, v -> this.wait.getValue()));
    public final BooleanSetting farCancel = this.add(new BooleanSetting("FarCancel", false));
    public final BooleanSetting hotBar = this.add(new BooleanSetting("HotBarSwap", false));
    public final BooleanSetting ghostHand = this.add(new BooleanSetting("GhostHand", true));
    private final BooleanSetting ignoreFire = this.add(new BooleanSetting("IgnoreFire", true));
    private final BooleanSetting checkGround = this.add(new BooleanSetting("CheckGround", true));
    private final BooleanSetting onlyGround = this.add(new BooleanSetting("OnlyGround", true));
    private final BooleanSetting doubleBreak = this.add(new BooleanSetting("DoubleBreak", true));
    private final BooleanSetting usingPause = this.add(new BooleanSetting("UsingPause", true));
    private final BooleanSetting swing = this.add(new BooleanSetting("Swing", true));
    private final BooleanSetting endSwing = this.add(new BooleanSetting("EndSwing", false));
    private final BooleanSetting bypassGround = this.add(new BooleanSetting("BypassGround", true));
    private final SliderSetting bypassTime = this.add(new SliderSetting("BypassTime", 400, 0, 2000, v -> this.bypassGround.getValue()));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true));
    private final SliderSetting time = this.add(new SliderSetting("Time", 100, 0, 2000, v -> this.rotate.getValue()));
    private final BooleanSetting switchReset = this.add(new BooleanSetting("SwitchReset", false));
    private final BooleanSetting crystal = this.add(new BooleanSetting("Crystal", true).setParent());
    private final BooleanSetting onlyHeadBomber = this.add(new BooleanSetting("OnlyHeadBomber", false, v -> this.crystal.isOpen()));
    private final BooleanSetting waitPlace = this.add(new BooleanSetting("WaitPlace", false, v -> this.crystal.isOpen()));
    private final BooleanSetting fast = this.add(new BooleanSetting("Fast", false, v -> this.crystal.isOpen()));
    private final BooleanSetting afterBreak = this.add(new BooleanSetting("AfterBreak", true, v -> this.crystal.isOpen()));
    private final BooleanSetting checkDamage = this.add(new BooleanSetting("CheckDamage", true, v -> this.crystal.isOpen()));
    private final SliderSetting crystalDamage = this.add(new SliderSetting("CrystalDamage", 0.7f, 0.0, 1.0, 0.01, v -> this.crystal.isOpen() && this.checkDamage.getValue()));
    public final BindSetting obsidian = this.add(new BindSetting("Obsidian", -1));
    private final BindSetting enderChest = this.add(new BindSetting("EnderChest", -1));
    private final SliderSetting placeDelay = this.add(new SliderSetting("PlaceDelay", 300, 0, 1000));
    public SliderSetting sliderSpeed = this.add(new SliderSetting("SliderSpeed", 0.2, 0.01, 1.0, 0.01));
    private final BooleanSetting autoColor = this.add(new BooleanSetting("AutoColor", true));
    public final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));
    public final ColorSetting doubleColor = this.add(new ColorSetting("DoubleColor", new Color(88, 94, 255, 100), v -> this.doubleBreak.getValue()));
    private final EnumSetting animationMode = this.add(new EnumSetting("AnimationMode", Mode.InToOut));
    private final BooleanSetting text = this.add(new BooleanSetting("Text", true)).setParent();
    private final BooleanSetting doubleText = this.add(new BooleanSetting("DoubleText", true, v -> this.text.isOpen()));
    private final BooleanSetting box = this.add(new BooleanSetting("Box", true));
    private final BooleanSetting outline = this.add(new BooleanSetting("Outline", true));
    int lastSlot = -1;
    public static PacketMine INSTANCE;
    public static BlockPos breakPos;
    public static BlockPos secondPos;
    public static double progress;
    private final Timer mineTimer = new Timer();
    private final FadeUtils animationTime = new FadeUtils(1000L);
    private final FadeUtils secondAnim = new FadeUtils(1000L);
    private boolean startMine = false;
    private int breakNumber = 0;
    private static Vec3d lastVec3d;
    public final Timer secondTimer = new Timer();
    private final Timer delayTimer = new Timer();
    private final Timer placeTimer = new Timer();
    public static boolean sendGroundPacket;
    private int oldSlot;

    public PacketMine() {
        super("PacketMine", Module.Category.Combat);
        INSTANCE = this;
    }

    private int findCrystal() {
        if (!this.hotBar.getValue()) {
            return InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL);
        }
        return InventoryUtil.findItem(Items.END_CRYSTAL);
    }

    private int findBlock(Block block) {
        if (!this.hotBar.getValue()) {
            return InventoryUtil.findBlockInventorySlot(block);
        }
        return InventoryUtil.findBlock(block);
    }

    private void doSwap(int slot, int inv) {
        if (this.hotBar.getValue()) {
            InventoryUtil.doSwap(slot);
        } else {
            PacketMine.mc.interactionManager.clickSlot(PacketMine.mc.player.currentScreenHandler.syncId, inv, PacketMine.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)PacketMine.mc.player);
        }
    }

    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        DecimalFormat df;
        double breakTime;
        int slot;
        if (PacketMine.nullCheck()) {
            return;
        }
        if (!PacketMine.mc.player.isCreative()) {
            if (breakPos != null) {
                slot = this.getTool(breakPos);
                if (slot == -1) {
                    slot = PacketMine.mc.player.getInventory().selectedSlot;
                }
                breakTime = this.getBreakTime(breakPos, slot);
                progress = (double)this.mineTimer.getPassedTimeMs() / breakTime;
                this.animationTime.setLength((long)breakTime);
                if (this.text.getValue()) {
                    if (this.isAir(breakPos)) {
                        TextUtil.drawText(drawContext, "Waiting", breakPos.toCenterPos());
                        return;
                    }
                    if ((double)((int)this.mineTimer.getPassedTimeMs()) < breakTime) {
                        df = new DecimalFormat("0.0");
                        TextUtil.drawText(drawContext, df.format(progress * 100.0) + "%", breakPos.toCenterPos());
                    } else {
                        TextUtil.drawText(drawContext, "100.0%", breakPos.toCenterPos());
                    }
                }
            } else {
                progress = 0.0;
            }
        } else {
            progress = 0.0;
        }
        if (!PacketMine.mc.player.isCreative()) {
            if (secondPos != null) {
                slot = this.getTool(secondPos);
                if (slot == -1) {
                    slot = PacketMine.mc.player.getInventory().selectedSlot;
                }
                breakTime = this.getBreakTime(secondPos, slot);
                progress = (double)this.mineTimer.getPassedTimeMs() / breakTime;
                this.animationTime.setLength((long)breakTime);
                if (this.text.getValue() && this.doubleText.getValue()) {
                    if (this.isAir(secondPos)) {
                        return;
                    }
                    if ((double)((int)this.secondTimer.getPassedTimeMs()) < breakTime) {
                        df = new DecimalFormat("0.0");
                        TextUtil.drawText(drawContext, df.format(progress * 100.0) + "%", secondPos.toCenterPos());
                    } else {
                        TextUtil.drawText(drawContext, "100.0%", secondPos.toCenterPos());
                    }
                }
            } else {
                progress = 0.0;
            }
        } else {
            progress = 0.0;
        }
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        this.update();
        if (!PacketMine.mc.player.isCreative()) {
            Box b;
            if (breakPos != null) {
                int slot = this.getTool(breakPos);
                if (slot == -1) {
                    slot = PacketMine.mc.player.getInventory().selectedSlot;
                }
                this.animationTime.setLength((long)this.getBreakTime(breakPos, slot));
                double ease = (1.0 - this.animationTime.easeOutQuad()) * 0.5;
                Color color = this.color.getValue();
                if (this.animationMode.getValue() == Mode.InToOut) {
                    b = new Box(breakPos).shrink(ease, ease, ease).shrink(-ease, -ease, -ease);
                } else {
                    Box bb = new Box(breakPos).shrink(ease, ease, ease).shrink(-ease, -ease, -ease);
                    Box bb2 = new Box(breakPos);
                    b = new Box(bb.minX, bb2.minY, bb.minZ, bb.maxX, bb2.maxY, bb.maxZ);
                }
                Vec3d cur = breakPos.toCenterPos();
                lastVec3d = lastVec3d == null ? cur : new Vec3d(Component.animate(lastVec3d.getX(), cur.x, this.sliderSpeed.getValue()), Component.animate(lastVec3d.getY(), cur.y, this.sliderSpeed.getValue()), Component.animate(lastVec3d.getZ(), cur.z, this.sliderSpeed.getValue()));
                Render3DUtil.draw3DBox(matrixStack, new Box(lastVec3d.add(0.5, 0.5, 0.5), lastVec3d.add(-0.5, -0.5, -0.5)).shrink(ease, ease, ease).shrink(-ease, -ease, -ease), ColorUtil.injectAlpha(this.autoColor.getValue() ? new Color((int)(255.0 * Math.abs(this.animationTime.easeOutQuad() - 1.0)), (int)(255.0 * this.animationTime.easeOutQuad()), 0) : color, color.getAlpha()), this.outline.getValue(), this.box.getValue());
            }
            if (secondPos != null && !secondPos.equals((Object)breakPos)) {
                if (this.isAir(secondPos)) {
                    if (SilentDouble.INSTANCE.isOn() && SilentDouble.swithc2 == 1) {
                        PacketMine.mc.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(SilentDouble.slotMain));
                        EntityUtil.sync();
                    }
                    secondPos = null;
                    return;
                }
                double ease = (1.0 - this.secondAnim.easeOutQuad()) * 0.5;
                if (this.animationMode.getValue() == Mode.InToOut) {
                    b = new Box(secondPos).shrink(ease, ease, ease).shrink(-ease, -ease, -ease);
                } else {
                    Box bb = new Box(secondPos).shrink(ease, ease, ease).shrink(-ease, -ease, -ease);
                    Box bb2 = new Box(secondPos);
                    b = new Box(bb.minX, bb2.minY, bb.minZ, bb.maxX, bb2.maxY, bb.maxZ);
                }
                Render3DUtil.draw3DBox(matrixStack, b, this.doubleColor.getValue(), this.outline.getValue(), this.box.getValue());
            }
        }
    }

    @Override
    public void onLogin() {
        this.startMine = false;
        breakPos = null;
        secondPos = null;
    }

    @Override
    public void onDisable() {
        this.startMine = false;
        breakPos = null;
    }

    @Override
    public String getInfo() {
        return this.instant.getValue() ? "Instant" : "Packet";
    }

    @Override
    public void onUpdate() {
        this.update();
    }

    public void update() {
        if (PacketMine.nullCheck()) {
            return;
        }
        if (PacketMine.mc.player.isDead()) {
            secondPos = null;
        }
        if (secondPos != null) {
            int n = SilentDouble.INSTANCE.isOn() ? (this.getTool(secondPos) == -1 ? PacketMine.mc.player.getInventory().selectedSlot : this.getTool(secondPos)) : PacketMine.mc.player.getInventory().selectedSlot;
            if (this.secondTimer.passed(this.getBreakTime(secondPos, n, 1.5))) {
                if (SilentDouble.INSTANCE.isOn() && SilentDouble.swithc2 == 1) {
                    PacketMine.mc.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(SilentDouble.slotMain));
                    EntityUtil.sync();
                }
                secondPos = null;
            }
        }
        if (secondPos != null && this.isAir(secondPos)) {
            if (SilentDouble.INSTANCE.isOn() && SilentDouble.swithc2 == 1) {
                PacketMine.mc.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(SilentDouble.slotMain));
                EntityUtil.sync();
            }
            secondPos = null;
        }
        if (secondPos != null && this.secondTimer.passed(this.getBreakTime(secondPos, PacketMine.mc.player.getInventory().selectedSlot, 0.8))) {
            PacketMine.mc.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, secondPos, BlockUtil.getClickSide(secondPos)));
        }
        if (PacketMine.mc.player.isCreative()) {
            this.startMine = false;
            this.breakNumber = 0;
            breakPos = null;
            return;
        }
        if (breakPos == null) {
            this.breakNumber = 0;
            this.startMine = false;
            return;
        }
        if (this.isAir(breakPos)) {
            this.breakNumber = 0;
        }
        if ((double)this.breakNumber > this.maxBreak.getValue() - 1.0 && this.maxBreak.getValue() > 0.0 || !this.wait.getValue() && this.isAir(breakPos) && !this.instant.getValue()) {
            if (breakPos.equals((Object)secondPos)) {
                secondPos = null;
            }
            this.startMine = false;
            this.breakNumber = 0;
            breakPos = null;
            return;
        }
        if (godBlocks.contains((Object)PacketMine.mc.world.getBlockState(breakPos).getBlock())) {
            breakPos = null;
            this.startMine = false;
            return;
        }
        if (this.usingPause.getValue() && EntityUtil.isUsing()) {
            return;
        }
        if ((double)MathHelper.sqrt((float)((float)EntityUtil.getEyesPos().squaredDistanceTo(breakPos.toCenterPos()))) > this.range.getValue()) {
            if (this.farCancel.getValue()) {
                this.startMine = false;
                this.breakNumber = 0;
                breakPos = null;
            }
            return;
        }
        if (breakPos.equals((Object)AutoAnchor.INSTANCE.currentPos)) {
            return;
        }
        if (!(this.hotBar.getValue() || PacketMine.mc.currentScreen == null || PacketMine.mc.currentScreen instanceof ChatScreen || PacketMine.mc.currentScreen instanceof InventoryScreen || PacketMine.mc.currentScreen instanceof ClickGuiScreen)) {
            return;
        }
        int slot = this.getTool(breakPos);
        if (slot == -1) {
            slot = PacketMine.mc.player.getInventory().selectedSlot;
        }
        if (this.isAir(breakPos)) {
            if (this.shouldCrystal()) {
                for (Direction facing : Direction.values()) {
                    CombatUtil.attackCrystal(breakPos.offset(facing), this.rotate.getValue(), true);
                }
            }
            if (this.placeTimer.passedMs(this.placeDelay.getValue()) && BlockUtil.canPlace(breakPos) && PacketMine.mc.currentScreen == null) {
                int obsidian;
                if (this.enderChest.isPressed()) {
                    int eChest = this.findBlock(Blocks.ENDER_CHEST);
                    if (eChest != -1) {
                        oldSlot = PacketMine.mc.player.getInventory().selectedSlot;
                        this.doSwap(eChest, eChest);
                        BlockUtil.placeBlock(breakPos, this.rotate.getValue(), true);
                        this.doSwap(oldSlot, eChest);
                        this.placeTimer.reset();
                    }
                } else if (this.obsidian.isPressed() && (obsidian = this.findBlock(Blocks.OBSIDIAN)) != -1) {
                    int hasCrystal = 0;
                    if (this.shouldCrystal()) {
                        for (Entity entity : PacketMine.mc.world.getNonSpectatingEntities(Entity.class, new Box(breakPos.up()))) {
                            if (!(entity instanceof EndCrystalEntity)) continue;
                            hasCrystal = 1;
                            break;
                        }
                    }
                    if (hasCrystal == 0 || this.fast.getValue()) {
                        int oldSlot = PacketMine.mc.player.getInventory().selectedSlot;
                        this.doSwap(obsidian, obsidian);
                        BlockUtil.placeBlock(breakPos, this.rotate.getValue(), true);
                        this.doSwap(oldSlot, obsidian);
                        this.placeTimer.reset();
                    }
                }
            }
            this.breakNumber = 0;
        } else if (PacketMine.canPlaceCrystal(breakPos.up(), true)) {
            if (this.waitPlace.getValue()) {
                for (Direction i : Direction.values()) {
                    if (!breakPos.offset(i).equals((Object)AutoCrystal.placePos)) continue;
                    if (!AutoCrystal.INSTANCE.canPlaceCrystal(AutoCrystal.placePos, false, false)) break;
                    return;
                }
            }
            if (this.shouldCrystal()) {
                if (this.placeTimer.passedMs(this.placeDelay.getValue())) {
                    if (this.checkDamage.getValue()) {
                        int crystal;
                        if ((double)this.mineTimer.getPassedTimeMs() / this.getBreakTime(breakPos, slot) >= this.crystalDamage.getValue() && (crystal = this.findCrystal()) != -1) {
                            oldSlot = PacketMine.mc.player.getInventory().selectedSlot;
                            this.doSwap(crystal, crystal);
                            BlockUtil.placeCrystal(breakPos.up(), this.rotate.getValue());
                            this.doSwap(oldSlot, crystal);
                            this.placeTimer.reset();
                            if (this.waitPlace.getValue()) {
                                return;
                            }
                        }
                    } else {
                        int crystal = this.findCrystal();
                        if (crystal != -1) {
                            oldSlot = PacketMine.mc.player.getInventory().selectedSlot;
                            this.doSwap(crystal, crystal);
                            BlockUtil.placeCrystal(breakPos.up(), this.rotate.getValue());
                            this.doSwap(oldSlot, crystal);
                            this.placeTimer.reset();
                            if (this.waitPlace.getValue()) {
                                return;
                            }
                        }
                    }
                } else if (this.startMine) {
                    return;
                }
            }
        }
        if (!this.delayTimer.passedMs((long)this.delay.getValue())) {
            return;
        }
        if (this.startMine) {
            if (this.isAir(breakPos)) {
                return;
            }
            if (PullCrystal.INSTANCE.isOn() && breakPos.equals((Object)PullCrystal.powerPos) && PullCrystal.crystalPos != null && !BlockUtil.hasCrystal(PullCrystal.crystalPos)) {
                return;
            }
            if (this.onlyGround.getValue() && !PacketMine.mc.player.isOnGround()) {
                return;
            }
            if (this.mineTimer.passedMs((long)this.getBreakTime(breakPos, slot))) {
                boolean shouldSwitch;
                int old = PacketMine.mc.player.getInventory().selectedSlot;
                if (this.hotBar.getValue()) {
                    shouldSwitch = slot != old;
                } else {
                    if (slot < 9) {
                        slot += 36;
                    }
                    boolean bl = shouldSwitch = old + 36 != slot;
                }
                if (shouldSwitch) {
                    if (this.hotBar.getValue()) {
                        InventoryUtil.doSwap(slot);
                    } else {
                        PacketMine.mc.interactionManager.clickSlot(PacketMine.mc.player.currentScreenHandler.syncId, slot, old, SlotActionType.SWAP, (PlayerEntity)PacketMine.mc.player);
                    }
                }
                if (this.rotate.getValue()) {
                    EntityUtil.facePosSide(breakPos, BlockUtil.getClickSide(breakPos));
                }
                if (this.endSwing.getValue()) {
                    EntityUtil.swingHand(Hand.MAIN_HAND, (SwingMode)Rebirth.HUD.swingMode.getValue());
                }
                PacketMine.mc.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, breakPos, BlockUtil.getClickSide(breakPos)));
                if (shouldSwitch && this.ghostHand.getValue()) {
                    if (this.hotBar.getValue()) {
                        InventoryUtil.doSwap(old);
                    } else {
                        PacketMine.mc.interactionManager.clickSlot(PacketMine.mc.player.currentScreenHandler.syncId, slot, old, SlotActionType.SWAP, (PlayerEntity)PacketMine.mc.player);
                        EntityUtil.sync();
                    }
                }
                ++this.breakNumber;
                this.delayTimer.reset();
                if (this.afterBreak.getValue() && this.shouldCrystal()) {
                    for (Direction facing : Direction.values()) {
                        CombatUtil.attackCrystal(breakPos.offset(facing), this.rotate.getValue(), true);
                    }
                }
            }
        } else {
            if (!this.mineAir.getValue() && this.isAir(breakPos)) {
                return;
            }
            this.animationTime.setLength((long)this.getBreakTime(breakPos, slot));
            this.mineTimer.reset();
            if (this.swing.getValue()) {
                EntityUtil.swingHand(Hand.MAIN_HAND, (SwingMode)Rebirth.HUD.swingMode.getValue());
            }
            PacketMine.mc.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, breakPos, BlockUtil.getClickSide(breakPos)));
            this.delayTimer.reset();
        }
    }

    @EventHandler
    public void onAttackBlock(ClickBlockEvent event) {
        if (PacketMine.nullCheck() || PacketMine.mc.player.isCreative()) {
            return;
        }
        event.cancel();
        if (godBlocks.contains((Object)PacketMine.mc.world.getBlockState(event.getBlockPos()).getBlock())) {
            return;
        }
        if (event.getBlockPos().equals((Object)breakPos)) {
            return;
        }
        breakPos = event.getBlockPos();
        this.mineTimer.reset();
        this.animationTime.reset();
        if (godBlocks.contains((Object)PacketMine.mc.world.getBlockState(event.getBlockPos()).getBlock())) {
            return;
        }
        this.startMine();
    }

    public static boolean canPlaceCrystal(BlockPos pos, boolean ignoreItem) {
        BlockPos obsPos = pos.down();
        BlockPos boost = obsPos.up();
        return (BlockUtil.getBlock(obsPos) == Blocks.BEDROCK || BlockUtil.getBlock(obsPos) == Blocks.OBSIDIAN) && BlockUtil.getClickSideStrict(obsPos) != null && PacketMine.noEntity(boost, ignoreItem) && PacketMine.noEntity(boost.up(), ignoreItem);
    }

    public static boolean noEntity(BlockPos pos, boolean ignoreItem) {
        for (Entity entity : PacketMine.mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (entity instanceof ItemEntity && ignoreItem || entity instanceof ArmorStandEntity && Rebirth.HUD.obsMode.getValue()) continue;
            return false;
        }
        return true;
    }

    public void mine(BlockPos pos) {
        if (PacketMine.nullCheck() || PacketMine.mc.player.isCreative()) {
            return;
        }
        if (this.isOff()) {
            return;
        }
        if (godBlocks.contains((Object)PacketMine.mc.world.getBlockState(pos).getBlock())) {
            return;
        }
        if (pos.equals((Object)breakPos)) {
            return;
        }
        if (breakPos != null && this.preferWeb.getValue() && BlockUtil.getBlock(breakPos) == Blocks.COBWEB) {
            return;
        }
        breakPos = pos;
        this.mineTimer.reset();
        this.animationTime.reset();
        this.startMine();
    }

    private boolean shouldCrystal() {
        return this.crystal.getValue() && (!this.onlyHeadBomber.getValue() || this.obsidian.isPressed());
    }

    private void startMine() {
        if (this.rotate.getValue()) {
            Vec3i vec3i = BlockUtil.getClickSide(breakPos).getVector();
            EntityUtil.faceVector(breakPos.toCenterPos().add(new Vec3d((double)vec3i.getX() * 0.5, (double)vec3i.getY() * 0.5, (double)vec3i.getZ() * 0.5)));
        }
        PacketMine.mc.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, breakPos, BlockUtil.getClickSide(breakPos)));
        if (this.doubleBreak.getValue()) {
            if (secondPos == null || this.isAir(secondPos)) {
                int slot = this.getTool(breakPos);
                if (slot == -1) {
                    slot = PacketMine.mc.player.getInventory().selectedSlot;
                }
                double breakTime = this.getBreakTime(breakPos, slot, 1.0);
                this.secondAnim.reset();
                this.secondAnim.setLength((long)breakTime);
                this.secondTimer.reset();
                secondPos = breakPos;
            }
            PacketMine.mc.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, breakPos, BlockUtil.getClickSide(breakPos)));
            PacketMine.mc.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, breakPos, BlockUtil.getClickSide(breakPos)));
        }
        if (this.swing.getValue()) {
            EntityUtil.swingHand(Hand.MAIN_HAND, (SwingMode)Rebirth.HUD.swingMode.getValue());
        }
        this.breakNumber = 0;
    }

    public int getTool(BlockPos pos) {
        if (this.hotBar.getValue()) {
            int index = -1;
            float CurrentFastest = 1.0f;
            for (int i = 0; i < 9; ++i) {
                float destroySpeed;
                float digSpeed;
                ItemStack stack = PacketMine.mc.player.getInventory().getStack(i);
                if (stack == ItemStack.EMPTY || !((digSpeed = (float)EnchantmentHelper.getLevel((Enchantment)Enchantments.EFFICIENCY, (ItemStack)stack)) + (destroySpeed = stack.getMiningSpeedMultiplier(PacketMine.mc.world.getBlockState(pos))) > CurrentFastest)) continue;
                CurrentFastest = digSpeed + destroySpeed;
                index = i;
            }
            return index;
        }
        AtomicInteger slot = new AtomicInteger();
        slot.set(-1);
        float CurrentFastest = 1.0f;
        for (Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            float destroySpeed;
            float digSpeed;
            if (entry.getValue().getItem() instanceof AirBlockItem || !((digSpeed = (float)EnchantmentHelper.getLevel((Enchantment)Enchantments.EFFICIENCY, (ItemStack)entry.getValue())) + (destroySpeed = entry.getValue().getMiningSpeedMultiplier(PacketMine.mc.world.getBlockState(pos))) > CurrentFastest)) continue;
            CurrentFastest = digSpeed + destroySpeed;
            slot.set(entry.getKey());
        }
        return slot.get();
    }

    @EventHandler(priority=-100)
    public void onRotate(RotateEvent event) {
        if (PacketMine.nullCheck() || PacketMine.mc.player.isCreative()) {
            return;
        }
        if (this.onlyGround.getValue() && !PacketMine.mc.player.isOnGround()) {
            return;
        }
        if (this.rotate.getValue() && breakPos != null && !this.isAir(breakPos) && this.time.getValue() > 0.0) {
            double breakTime;
            if ((double)MathHelper.sqrt((float)((float)EntityUtil.getEyesPos().squaredDistanceTo(breakPos.toCenterPos()))) > this.range.getValue()) {
                return;
            }
            int slot = this.getTool(breakPos);
            if (slot == -1) {
                slot = PacketMine.mc.player.getInventory().selectedSlot;
            }
            if ((breakTime = this.getBreakTime(breakPos, slot) - this.time.getValue()) <= 0.0 || this.mineTimer.passedMs((long)breakTime)) {
                PacketMine.facePosFacing(breakPos, BlockUtil.getClickSide(breakPos), event);
            }
        }
    }

    public static void facePosFacing(BlockPos pos, Direction side, RotateEvent event) {
        Vec3d hitVec = pos.toCenterPos().add(new Vec3d((double)side.getVector().getX() * 0.5, (double)side.getVector().getY() * 0.5, (double)side.getVector().getZ() * 0.5));
        PacketMine.faceVector(hitVec, event);
    }

    private static void faceVector(Vec3d vec, RotateEvent event) {
        float[] rotations = EntityUtil.getLegitRotations(vec);
        event.setRotation(rotations[0], rotations[1]);
    }

    @EventHandler(priority=-200)
    public void onPacketSend(PacketEvent.Send event) {
        if (PacketMine.nullCheck() || PacketMine.mc.player.isCreative()) {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            if (this.bypassGround.getValue() && breakPos != null && !this.isAir(breakPos) && this.bypassTime.getValue() > 0.0 && MathHelper.sqrt((float)((float)breakPos.toCenterPos().squaredDistanceTo(EntityUtil.getEyesPos()))) <= this.range.getValueFloat() + 2.0f) {
                double breakTime;
                int slot = this.getTool(breakPos);
                if (slot == -1) {
                    slot = PacketMine.mc.player.getInventory().selectedSlot;
                }
                if ((breakTime = this.getBreakTime(breakPos, slot) - this.bypassTime.getValue()) <= 0.0 || this.mineTimer.passedMs((long)breakTime)) {
                    sendGroundPacket = true;
                    ((IPlayerMoveC2SPacket)event.getPacket()).setOnGround(true);
                }
            } else {
                sendGroundPacket = false;
            }
            return;
        }
        Object t = event.getPacket();
        if (t instanceof UpdateSelectedSlotC2SPacket) {
            UpdateSelectedSlotC2SPacket packet = (UpdateSelectedSlotC2SPacket)t;
            if (packet.getSelectedSlot() != this.lastSlot) {
                this.lastSlot = packet.getSelectedSlot();
                if (this.switchReset.getValue()) {
                    this.startMine = false;
                    this.mineTimer.reset();
                    this.animationTime.reset();
                }
            }
            return;
        }
        if (!(event.getPacket() instanceof PlayerActionC2SPacket)) {
            return;
        }
        if (((PlayerActionC2SPacket)event.getPacket()).getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            if (breakPos == null || !((PlayerActionC2SPacket)event.getPacket()).getPos().equals((Object)breakPos)) {
                if (this.cancelPacket.getValue()) {
                    event.cancel();
                }
                return;
            }
            this.startMine = true;
        } else if (((PlayerActionC2SPacket)event.getPacket()).getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
            if (breakPos == null || !((PlayerActionC2SPacket)event.getPacket()).getPos().equals((Object)breakPos)) {
                if (this.cancelPacket.getValue()) {
                    event.cancel();
                }
                return;
            }
            if (!this.instant.getValue()) {
                this.startMine = false;
            }
        }
    }

    public final double getBreakTime(BlockPos pos, int slot) {
        return this.getBreakTime(pos, slot, this.damage.getValue());
    }

    public final double getBreakTime(BlockPos pos, int slot, double damage) {
        return (double)(1.0f / this.getBlockStrength(pos, PacketMine.mc.player.getInventory().getStack(slot)) / 20.0f * 1000.0f) * damage;
    }

    private boolean canBreak(BlockPos pos) {
        BlockState blockState = PacketMine.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getHardness() != -1.0f;
    }

    public float getBlockStrength(BlockPos position, ItemStack itemStack) {
        BlockState state = PacketMine.mc.world.getBlockState(position);
        float hardness = state.getHardness((BlockView)PacketMine.mc.world, position);
        if (hardness < 0.0f) {
            return 0.0f;
        }
        if (!this.canBreak(position)) {
            return this.getDigSpeed(state, itemStack) / hardness / 100.0f;
        }
        return this.getDigSpeed(state, itemStack) / hardness / 30.0f;
    }

    public float getDigSpeed(BlockState state, ItemStack itemStack) {
        int efficiencyModifier;
        float digSpeed = this.getDestroySpeed(state, itemStack);
        if (digSpeed > 1.0f && (efficiencyModifier = EnchantmentHelper.getLevel((Enchantment)Enchantments.EFFICIENCY, (ItemStack)itemStack)) > 0 && !itemStack.isEmpty()) {
            digSpeed = (float)((double)digSpeed + (StrictMath.pow(efficiencyModifier, 2.0) + 1.0));
        }
        if (PacketMine.mc.player.hasStatusEffect(StatusEffects.HASTE)) {
            digSpeed *= 1.0f + (float)(PacketMine.mc.player.getStatusEffect(StatusEffects.HASTE).getAmplifier() + 1) * 0.2f;
        }
        if (PacketMine.mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            digSpeed *= (switch (PacketMine.mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 8.1E-4f;
            });
        }
        if (PacketMine.mc.player.isSubmergedInWater() && !EnchantmentHelper.hasAquaAffinity((LivingEntity)PacketMine.mc.player)) {
            digSpeed /= 5.0f;
        }
        if (!PacketMine.mc.player.isOnGround() && PacketMine.INSTANCE.checkGround.getValue()) {
            digSpeed /= 5.0f;
        }
        return digSpeed < 0.0f ? 0.0f : digSpeed;
    }

    public float getDestroySpeed(BlockState state, ItemStack itemStack) {
        float destroySpeed = 1.0f;
        if (itemStack != null && !itemStack.isEmpty()) {
            destroySpeed *= itemStack.getMiningSpeedMultiplier(state);
        }
        return destroySpeed;
    }

    private boolean isAir(BlockPos breakPos) {
        return PacketMine.mc.world.isAir(breakPos) || this.ignoreFire.getValue() && BlockUtil.getBlock(breakPos) == Blocks.FIRE && BlockUtil.hasCrystal(breakPos);
    }

    static {
        progress = 0.0;
        sendGroundPacket = false;
    }

    public static enum Mode {
        InToOut,
        Vertical;

        // $FF: synthetic method
        private static PacketMine.Mode[] $values() {
            return new PacketMine.Mode[]{InToOut, Vertical};
        }
    }
}
