/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.mob.SlimeEntity
 *  net.minecraft.entity.passive.AnimalEntity
 *  net.minecraft.entity.passive.VillagerEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.item.SwordItem
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.combat;

import java.awt.Color;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.asm.accessors.ILivingEntity;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.Criticals;
import me.rebirthclient.mod.settings.SwingMode;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Aura
extends Module {
    public static Aura INSTANCE;
    public static Entity target;
    public final EnumSetting page = this.add(new EnumSetting("Page", Page.General));
    public final SliderSetting range = this.add(new SliderSetting("Range", 6.0, (double)0.1f, 7.0, v -> this.page.getValue() == Page.General));
    private final BooleanSetting ghost = this.add(new BooleanSetting("SweepingBypass", false, v -> this.page.getValue() == Page.General));
    private final SliderSetting cooldown = this.add(new SliderSetting("Cooldown", 1.1f, 0.0, 1.2f, 0.01, v -> this.page.getValue() == Page.General));
    private final SliderSetting wallRange = this.add(new SliderSetting("WallRange", 6.0, (double)0.1f, 7.0, v -> this.page.getValue() == Page.General));
    private final BooleanSetting whileEating = this.add(new BooleanSetting("WhileUsing", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting weaponOnly = this.add(new BooleanSetting("WeaponOnly", true, v -> this.page.getValue() == Page.General));
    private final EnumSetting swingMode = this.add(new EnumSetting("Swing", SwingMode.Server, v -> this.page.getValue() == Page.General));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true, v -> this.page.getValue() == Page.Rotate).setParent());
    private final BooleanSetting newRotate = this.add(new BooleanSetting("NewRotate", true, v -> this.rotate.isOpen() && this.page.getValue() == Page.Rotate));
    private final SliderSetting yawStep = this.add(new SliderSetting("YawStep", (double)0.3f, (double)0.1f, 1.0, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting checkLook = this.add(new BooleanSetting("CheckLook", true, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final SliderSetting fov = this.add(new SliderSetting("Fov", 5.0, 0.0, 30.0, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.checkLook.getValue() && this.page.getValue() == Page.Rotate));
    private final EnumSetting targetMode = this.add(new EnumSetting("Filter", TargetMode.DISTANCE, v -> this.page.getValue() == Page.Target));
    public final BooleanSetting Players = this.add(new BooleanSetting("Players", true, v -> this.page.getValue() == Page.Target));
    public final BooleanSetting Mobs = this.add(new BooleanSetting("Mobs", true, v -> this.page.getValue() == Page.Target));
    public final BooleanSetting Animals = this.add(new BooleanSetting("Animals", true, v -> this.page.getValue() == Page.Target));
    public final BooleanSetting Villagers = this.add(new BooleanSetting("Villagers", true, v -> this.page.getValue() == Page.Target));
    public final BooleanSetting Slimes = this.add(new BooleanSetting("Slimes", true, v -> this.page.getValue() == Page.Target));
    public final BooleanSetting render = this.add(new BooleanSetting("TargetRender", true, v -> this.page.getValue() == Page.Render).setParent());
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100), v -> this.render.isOpen() && this.page.getValue() == Page.Render));
    public Vec3d directionVec = null;
    private final Timer ghostTimer = new Timer();
    private float lastYaw = 0.0f;
    private float lastPitch = 0.0f;
    public boolean sweeping = false;

    public Aura() {
        super("Aura", "Attacks players in radius", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public String getInfo() {
        return target == null ? null : target.getName().getString();
    }

    @Override
    public void onDisable() {
        this.lastYaw = Rebirth.RUN.lastYaw;
        this.lastPitch = Rebirth.RUN.lastPitch;
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (target != null && this.render.getValue()) {
            Render3DUtil.drawJello(matrixStack, target, this.color.getValue());
        }
    }

    @Override
    public void onUpdate() {
        if (this.check()) {
            target = this.getTarget();
            if (target == null) {
                return;
            }
            this.doAura();
        } else {
            target = null;
        }
    }

    @EventHandler(priority=98)
    public void onRotate(RotateEvent event) {
        if (target != null && this.newRotate.getValue() && this.directionVec != null) {
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

    private boolean check() {
        if (this.weaponOnly.getValue() && !EntityUtil.isHoldingWeapon((PlayerEntity)Aura.mc.player)) {
            return false;
        }
        if (this.ghost.getValue()) {
            if (!this.ghostTimer.passedMs(600L)) {
                return false;
            }
            if (InventoryUtil.findClassInventorySlot(SwordItem.class) == -1) {
                return false;
            }
        }
        return this.whileEating.getValue() || !Aura.mc.player.isUsingItem();
    }

    public static float getAttackCooldownProgressPerTick() {
        return (float)(1.0 / Aura.mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * 20.0);
    }

    private void doAura() {
        if (!this.check()) {
            return;
        }
        if (!((double)Math.max((float)((ILivingEntity)Aura.mc.player).getLastAttackedTicks() / Aura.getAttackCooldownProgressPerTick(), 0.0f) >= this.cooldown.getValue())) {
            return;
        }
        if (this.rotate.getValue() && !this.faceVector(target.getPos().add(0.0, 1.5, 0.0))) {
            return;
        }
        int slot = InventoryUtil.findItemInventorySlot(Items.NETHERITE_SWORD);
        if (this.ghost.getValue()) {
            this.sweeping = true;
            Aura.mc.interactionManager.clickSlot(Aura.mc.player.currentScreenHandler.syncId, slot, Aura.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)Aura.mc.player);
        }
        this.ghostTimer.reset();
        if (!this.ghost.getValue() && Criticals.INSTANCE.isOn()) {
            Criticals.INSTANCE.doCrit();
        }
        Aura.mc.interactionManager.attackEntity((PlayerEntity)Aura.mc.player, target);
        EntityUtil.swingHand(Hand.MAIN_HAND, (SwingMode)this.swingMode.getValue());
        if (this.ghost.getValue()) {
            Aura.mc.interactionManager.clickSlot(Aura.mc.player.currentScreenHandler.syncId, slot, Aura.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)Aura.mc.player);
            this.sweeping = false;
        }
    }

    public boolean faceVector(Vec3d directionVec) {
        if (!this.newRotate.getValue()) {
            EntityUtil.faceVector(directionVec);
            return true;
        }
        this.directionVec = directionVec;
        float[] angle = EntityUtil.getLegitRotations(directionVec);
        if (Math.abs(MathHelper.wrapDegrees((float)(angle[0] - this.lastYaw))) < this.fov.getValueFloat() && Math.abs(MathHelper.wrapDegrees((float)(angle[1] - this.lastPitch))) < this.fov.getValueFloat()) {
            EntityUtil.sendYawAndPitch(angle[0], angle[1]);
            return true;
        }
        return !this.checkLook.getValue();
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = this.range.getValue();
        double maxHealth = 36.0;
        for (Entity entity : Aura.mc.world.getEntities()) {
            if (!this.isEnemy(entity) || !Aura.mc.player.canSee(entity) && (double)Aura.mc.player.distanceTo(entity) > this.wallRange.getValue() || !CombatUtil.isValid(entity, this.range.getValue())) continue;
            if (target == null) {
                target = entity;
                distance = Aura.mc.player.distanceTo(entity);
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (entity instanceof PlayerEntity && EntityUtil.isArmorLow((PlayerEntity)entity, 10)) {
                target = entity;
                break;
            }
            if (this.targetMode.getValue() == TargetMode.HEALTH && (double)EntityUtil.getHealth(entity) < maxHealth) {
                target = entity;
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (this.targetMode.getValue() != TargetMode.DISTANCE || !((double)Aura.mc.player.distanceTo(entity) < distance)) continue;
            target = entity;
            distance = Aura.mc.player.distanceTo(entity);
        }
        return target;
    }

    private boolean isEnemy(Entity entity) {
        if (entity instanceof SlimeEntity && this.Slimes.getValue()) {
            return true;
        }
        if (entity instanceof PlayerEntity && this.Players.getValue()) {
            return true;
        }
        if (entity instanceof VillagerEntity && this.Villagers.getValue()) {
            return true;
        }
        if (!(entity instanceof VillagerEntity) && entity instanceof MobEntity && this.Mobs.getValue()) {
            return true;
        }
        return entity instanceof AnimalEntity && this.Animals.getValue();
    }

    private float[] injectStep(float[] angle, float steps) {
        if (steps < 0.1f) {
            steps = 0.1f;
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
        Rotate,
        Target,
        Render;

        // $FF: synthetic method
        private static Aura.Page[] $values() {
            return new Aura.Page[]{General, Rotate, Target, Render};
        }
    }

    private static enum TargetMode {
        DISTANCE,
        HEALTH;

        // $FF: synthetic method
        private static Aura.TargetMode[] $values() {
            return new Aura.TargetMode[]{DISTANCE, HEALTH};
        }
    }
}