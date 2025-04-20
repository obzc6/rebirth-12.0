/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.OtherClientPlayerEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.listener.ClientPlayPacketListener
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.miscellaneous;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoAnchor;
import me.rebirthclient.mod.modules.combat.AutoCrystal;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FakePlayer
extends Module {
    public static FakePlayer INSTANCE;
    private final BooleanSetting damage = this.add(new BooleanSetting("Damage", true));
    private final BooleanSetting autoTotem = this.add(new BooleanSetting("AutoTotem", true));
    private final BooleanSetting gApple = this.add(new BooleanSetting("GApple", true));
    public static OtherClientPlayerEntity fakePlayer;
    private final Timer timer = new Timer().reset();
    int pops = 0;

    public FakePlayer() {
        super("FakePlayer", Module.Category.Miscellaneous);
        this.setDescription("Spawn fakeplayer.");
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.pops = 0;
        if (FakePlayer.nullCheck()) {
            this.disable();
            return;
        }
        fakePlayer = new OtherClientPlayerEntity(FakePlayer.mc.world, new GameProfile(UUID.fromString("11451466-6666-6666-6666-666666666600"), "FakePlayer"));
        fakePlayer.copyPositionAndRotation((Entity)FakePlayer.mc.player);
        fakePlayer.getInventory().clone(FakePlayer.mc.player.getInventory());
        FakePlayer.mc.world.addPlayer(-1, (AbstractClientPlayerEntity)fakePlayer);
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 4));
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 1));
    }

    @Override
    public void onUpdate() {
        if (fakePlayer != null) {
            fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
            fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 4));
            if (this.gApple.getValue() && this.timer.passedMs(4000L)) {
                fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 1));
                this.timer.reset();
                fakePlayer.setAbsorptionAmount(16.0f);
            }
            if (this.autoTotem.getValue() && fakePlayer.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                Rebirth.POP.onTotemPop((PlayerEntity)fakePlayer);
                fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack((ItemConvertible)Items.TOTEM_OF_UNDYING));
            }
            if (fakePlayer.isDead() && fakePlayer.tryUseTotem(FakePlayer.mc.world.getDamageSources().generic())) {
                fakePlayer.setHealth(10.0f);
                new EntityStatusS2CPacket((Entity)fakePlayer, (byte) 35).apply((ClientPlayPacketListener)FakePlayer.mc.player.networkHandler);
            }
        } else {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        if (fakePlayer == null) {
            return;
        }
        fakePlayer.kill();
        fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
        fakePlayer.onRemoved();
        fakePlayer = null;
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (this.damage.getValue() && fakePlayer != null && FakePlayer.fakePlayer.hurtTime == 0) {
            Object t;
            if (this.autoTotem.getValue() && fakePlayer.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack((ItemConvertible)Items.TOTEM_OF_UNDYING));
            }
            if ((t = event.getPacket()) instanceof ExplosionS2CPacket) {
                ExplosionS2CPacket explosion = (ExplosionS2CPacket)t;
                Vec3d vec3d = new Vec3d(explosion.getX(), explosion.getY(), explosion.getZ());
                if (MathHelper.sqrt((float)((float)vec3d.squaredDistanceTo(fakePlayer.getPos()))) > 10.0f) {
                    return;
                }
                float damage = BlockUtil.getBlock(new BlockPosX(explosion.getX(), explosion.getY(), explosion.getZ())) == Blocks.RESPAWN_ANCHOR ? (float)AutoAnchor.INSTANCE.getAnchorDamage(new BlockPosX(explosion.getX(), explosion.getY(), explosion.getZ()), (PlayerEntity)fakePlayer, (PlayerEntity)fakePlayer) : AutoCrystal.INSTANCE.calculateDamage(new Vec3d(explosion.getX(), explosion.getY(), explosion.getZ()), (PlayerEntity)fakePlayer, (PlayerEntity)fakePlayer);
                fakePlayer.onDamaged(FakePlayer.mc.world.getDamageSources().generic());
                if (fakePlayer.getAbsorptionAmount() >= damage) {
                    fakePlayer.setAbsorptionAmount(fakePlayer.getAbsorptionAmount() - damage);
                } else {
                    float damage2 = damage - fakePlayer.getAbsorptionAmount();
                    fakePlayer.setAbsorptionAmount(0.0f);
                    fakePlayer.setHealth(fakePlayer.getHealth() - damage2);
                }
            }
            if (fakePlayer.isDead() && fakePlayer.tryUseTotem(FakePlayer.mc.world.getDamageSources().generic())) {
                fakePlayer.setHealth(10.0f);
                new EntityStatusS2CPacket((Entity)fakePlayer, (byte) 35).apply((ClientPlayPacketListener)FakePlayer.mc.player.networkHandler);
            }
        }
    }
}

