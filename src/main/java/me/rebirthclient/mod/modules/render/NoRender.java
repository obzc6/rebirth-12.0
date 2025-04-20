/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.CampfireSmokeParticle
 *  net.minecraft.client.particle.ElderGuardianAppearanceParticle
 *  net.minecraft.client.particle.ExplosionLargeParticle
 *  net.minecraft.client.particle.FireworksSparkParticle$FireworkParticle
 *  net.minecraft.client.particle.FireworksSparkParticle$Flash
 *  net.minecraft.client.particle.SpellParticle
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.projectile.ArrowEntity
 *  net.minecraft.entity.projectile.thrown.EggEntity
 *  net.minecraft.entity.projectile.thrown.ExperienceBottleEntity
 *  net.minecraft.entity.projectile.thrown.PotionEntity
 *  net.minecraft.network.packet.s2c.play.TitleS2CPacket
 */
package me.rebirthclient.mod.modules.render;

import java.lang.reflect.Field;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.ParticleEvent;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;

public class NoRender
extends Module {
    public static NoRender INSTANCE;
    public BooleanSetting potions = new BooleanSetting("Potions", true);
    public BooleanSetting xp = new BooleanSetting("XP", true);
    public BooleanSetting arrows = new BooleanSetting("Arrows", false);
    public BooleanSetting eggs = new BooleanSetting("Eggs", false);
    public BooleanSetting armor = new BooleanSetting("Armor", false);
    public BooleanSetting hurtCam = new BooleanSetting("HurtCam", true);
    public BooleanSetting fireOverlay = new BooleanSetting("FireOverlay", true);
    public BooleanSetting vignetteOverlay = new BooleanSetting("VignetteOverlay", true);
    public BooleanSetting waterOverlay = new BooleanSetting("WaterOverlay", true);
    public BooleanSetting blockOverlay = new BooleanSetting("BlockOverlay", true);
    public BooleanSetting portal = new BooleanSetting("Portal", true);
    public BooleanSetting totem = new BooleanSetting("Totem", true);
    public BooleanSetting nausea = new BooleanSetting("Nausea", true);
    public BooleanSetting blindness = new BooleanSetting("Blindness", true);
    public BooleanSetting fog = new BooleanSetting("Fog", true);
    public BooleanSetting darkness = new BooleanSetting("Darkness", true);
    public BooleanSetting fireEntity = new BooleanSetting("EntityFire", true);
    public BooleanSetting antiTitle = new BooleanSetting("Title", false);
    public BooleanSetting antiPlayerCollision = new BooleanSetting("PlayerCollision", true);
    public BooleanSetting effect = new BooleanSetting("Effect", true);
    public BooleanSetting elderGuardian = new BooleanSetting("Guardian", false);
    public BooleanSetting explosions = new BooleanSetting("Explosions", true);
    public BooleanSetting campFire = new BooleanSetting("CampFire", false);
    public BooleanSetting fireworks = new BooleanSetting("Fireworks", false);

    public NoRender() {
        super("NoRender", Module.Category.Render);
        this.setDescription("Disables all overlays and potion effects.");
        INSTANCE = this;
        try {
            for (Field field : NoRender.class.getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType())) continue;
                Setting setting = (Setting)field.get(this);
                this.addSetting(setting);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof TitleS2CPacket && this.antiTitle.getValue()) {
            event.setCancel(true);
        }
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        for (Entity ent : NoRender.mc.world.getEntities()) {
            if (ent instanceof PotionEntity && this.potions.getValue()) {
                NoRender.mc.world.removeEntity(ent.getId(), Entity.RemovalReason.KILLED);
            }
            if (ent instanceof ExperienceBottleEntity && this.xp.getValue()) {
                NoRender.mc.world.removeEntity(ent.getId(), Entity.RemovalReason.KILLED);
            }
            if (ent instanceof ArrowEntity && this.arrows.getValue()) {
                NoRender.mc.world.removeEntity(ent.getId(), Entity.RemovalReason.KILLED);
            }
            if (!(ent instanceof EggEntity) || !this.eggs.getValue()) continue;
            NoRender.mc.world.removeEntity(ent.getId(), Entity.RemovalReason.KILLED);
        }
    }

    @EventHandler
    public void onParticle(ParticleEvent.AddParticle event) {
        if (this.elderGuardian.getValue() && event.particle instanceof ElderGuardianAppearanceParticle) {
            event.setCancel(true);
        } else if (this.explosions.getValue() && event.particle instanceof ExplosionLargeParticle) {
            event.setCancel(true);
        } else if (this.campFire.getValue() && event.particle instanceof CampfireSmokeParticle) {
            event.setCancel(true);
        } else if (this.fireworks.getValue() && (event.particle instanceof FireworksSparkParticle.FireworkParticle || event.particle instanceof FireworksSparkParticle.Flash)) {
            event.setCancel(true);
        } else if (this.effect.getValue() && event.particle instanceof SpellParticle) {
            event.cancel();
        }
    }
}

