/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.MovementType
 *  net.minecraft.item.ElytraItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.TravelEvent;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ElytraFly
extends Module {
    public static ElytraFly INSTANCE;
    private final BooleanSetting instantFly = this.add(new BooleanSetting("InstantFly", true));
    public SliderSetting upPitch = this.add(new SliderSetting("UpPitch", 0.0, 0.0, 90.0));
    public SliderSetting upFactor = this.add(new SliderSetting("UpFactor", 1.0, 0.0, 10.0));
    public SliderSetting downFactor = this.add(new SliderSetting("DownFactor", 1.0, 0.0, 10.0));
    public SliderSetting speed = this.add(new SliderSetting("Speed", 1.0, 0.1f, 10.0));
    private final SliderSetting sneakDownSpeed = this.add(new SliderSetting("DownSpeed", 1.0, 0.1f, 10.0));
    public final BooleanSetting boostTimer = this.add(new BooleanSetting("Timer", true));
    public BooleanSetting speedLimit = this.add(new BooleanSetting("SpeedLimit", true));
    public SliderSetting maxSpeed = this.add(new SliderSetting("MaxSpeed", 2.5, (double)0.1f, 10.0, v -> this.speedLimit.getValue()));
    public BooleanSetting noDrag = this.add(new BooleanSetting("NoDrag", false));
    private final SliderSetting timeout = this.add(new SliderSetting("Timeout", 0.5, 0.1f, 1.0));
    private boolean hasElytra = false;
    private final Timer instantFlyTimer = new Timer();
    private final Timer strictTimer = new Timer();
    private boolean hasTouchedGround = false;

    public ElytraFly() {
        super("ElytraFly", Module.Category.Movement);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (ElytraFly.mc.player != null) {
            if (!ElytraFly.mc.player.isCreative()) {
                ElytraFly.mc.player.getAbilities().allowFlying = false;
            }
            ElytraFly.mc.player.getAbilities().flying = false;
        }
        this.hasElytra = false;
    }

    @Override
    public void onDisable() {
        Rebirth.TIMER.reset();
        this.hasElytra = false;
        if (ElytraFly.mc.player != null) {
            if (!ElytraFly.mc.player.isCreative()) {
                ElytraFly.mc.player.getAbilities().allowFlying = false;
            }
            ElytraFly.mc.player.getAbilities().flying = false;
        }
    }

    @Override
    public void onUpdate() {
        if (ElytraFly.nullCheck()) {
            return;
        }
        if (ElytraFly.mc.player.isOnGround()) {
            this.hasTouchedGround = true;
        }
        for (ItemStack is : ElytraFly.mc.player.getArmorItems()) {
            if (is.getItem() instanceof ElytraItem) {
                this.hasElytra = true;
                break;
            }
            this.hasElytra = false;
        }
        if (this.strictTimer.passedMs(1500L) && !this.strictTimer.passedMs(2000L) || EntityUtil.isElytraFlying() && (double)Rebirth.TIMER.get() == 0.3) {
            Rebirth.TIMER.timer = 1.0f;
        }
        if (!ElytraFly.mc.player.isFallFlying()) {
            if (this.hasTouchedGround && this.boostTimer.getValue() && !ElytraFly.mc.player.isOnGround()) {
                Rebirth.TIMER.timer = 0.3f;
            }
            if (!ElytraFly.mc.player.isOnGround() && this.instantFly.getValue() && ElytraFly.mc.player.getVelocity().getY() < 0.0) {
                if (!this.instantFlyTimer.passedMs((long)(1000.0 * this.timeout.getValue()))) {
                    return;
                }
                this.instantFlyTimer.reset();
                ElytraFly.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)ElytraFly.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                this.hasTouchedGround = false;
                this.strictTimer.reset();
            }
        }
    }

    protected final Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos((float)g);
        float i = MathHelper.sin((float)g);
        float j = MathHelper.cos((float)f);
        float k = MathHelper.sin((float)f);
        return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
    }

    public final Vec3d getRotationVec(float tickDelta) {
        return this.getRotationVector(-this.upPitch.getValueFloat(), ElytraFly.mc.player.getYaw(tickDelta));
    }

    @EventHandler
    public void onMove(TravelEvent event) {
        if (ElytraFly.nullCheck() || !this.hasElytra || !ElytraFly.mc.player.isFallFlying()) {
            return;
        }
        event.cancel();
        Vec3d lookVec = this.getRotationVec(mc.getTickDelta());
        double lookDist = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);
        double motionDist = Math.sqrt(this.getX() * this.getX() + this.getZ() * this.getZ());
        if (ElytraFly.mc.options.sneakKey.isPressed()) {
            this.setY(-this.sneakDownSpeed.getValue());
        } else if (!ElytraFly.mc.player.input.jumping) {
            this.setY(-3.0E-14 * this.downFactor.getValue());
        }
        if (ElytraFly.mc.player.input.jumping) {
            if (motionDist > this.upFactor.getValue() / this.upFactor.getMaximum()) {
                double rawUpSpeed = motionDist * 0.01325;
                this.setY(this.getY() + rawUpSpeed * 3.2);
                this.setX(this.getX() - lookVec.x * rawUpSpeed / lookDist);
                this.setZ(this.getZ() - lookVec.z * rawUpSpeed / lookDist);
            } else {
                double[] dir = MovementUtil.directionSpeed(this.speed.getValue());
                this.setX(dir[0]);
                this.setZ(dir[1]);
            }
        }
        if (lookDist > 0.0) {
            this.setX(this.getX() + (lookVec.x / lookDist * motionDist - this.getX()) * 0.1);
            this.setZ(this.getZ() + (lookVec.z / lookDist * motionDist - this.getZ()) * 0.1);
        }
        if (!ElytraFly.mc.player.input.jumping) {
            double[] dir = MovementUtil.directionSpeed(this.speed.getValue());
            this.setX(dir[0]);
            this.setZ(dir[1]);
        }
        if (!this.noDrag.getValue()) {
            this.setY(this.getY() * (double)0.99f);
            this.setX(this.getX() * (double)0.98f);
            this.setZ(this.getZ() * (double)0.99f);
        }
        double finalDist = Math.sqrt(this.getX() * this.getX() + this.getZ() * this.getZ());
        if (this.speedLimit.getValue() && finalDist > this.maxSpeed.getValue()) {
            this.setX(this.getX() * this.maxSpeed.getValue() / finalDist);
            this.setZ(this.getZ() * this.maxSpeed.getValue() / finalDist);
        }
        ElytraFly.mc.player.move(MovementType.SELF, ElytraFly.mc.player.getVelocity());
    }

    private void setX(double f) {
        MovementUtil.setMotionX(f);
    }

    private void setY(double f) {
        MovementUtil.setMotionY(f);
    }

    private void setZ(double f) {
        MovementUtil.setMotionZ(f);
    }

    private double getX() {
        return MovementUtil.getMotionX();
    }

    private double getY() {
        return MovementUtil.getMotionY();
    }

    private double getZ() {
        return MovementUtil.getMotionZ();
    }
}

