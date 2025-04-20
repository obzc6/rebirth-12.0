/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.mod.modules.movement;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.MoveEvent;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.miscellaneous.AutoPeek;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class HoleSnap
extends Module {
    public static HoleSnap INSTANCE;
    public BooleanSetting any = this.add(new BooleanSetting("Any", true));
    private final SliderSetting range = this.add(new SliderSetting("Range", 5, 1, 50));
    private final SliderSetting timeoutTicks = this.add(new SliderSetting("TimeOut", 40, 0, 100));
    public final SliderSetting multiplier = this.add(new SliderSetting("Timer", 1.0, 0.1, 8.0, 0.1));
    public final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));
    public SliderSetting circleSize = this.add(new SliderSetting("CircleSize", 1.0, 0.1f, 2.5));
    public BooleanSetting fade = this.add(new BooleanSetting("Fade", true));
    public SliderSetting segments = this.add(new SliderSetting("Segments", 180, 0, 360));
    boolean resetMove = false;
    private BlockPos holePos;
    private int stuckTicks;
    private int enabledTicks;
    Vec3d targetPos;

    public HoleSnap() {
        super("HoleSnap", "HoleSnap", Module.Category.Movement);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.resetMove = false;
    }

    @Override
    public void onDisable() {
        if (Rebirth.TIMER.get() == this.multiplier.getValueFloat()) {
            Rebirth.TIMER.reset();
        }
        if (this.resetMove) {
            MovementUtil.setMotionX(0.0);
            MovementUtil.setMotionZ(0.0);
        }
        this.holePos = null;
        this.stuckTicks = 0;
        this.enabledTicks = 0;
    }

    @EventHandler
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.disable();
        }
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        Direction facing;
        if (this.multiplier.getValue() != 1.0) {
            Rebirth.TIMER.timer = this.multiplier.getValueFloat();
        }
        ++this.enabledTicks;
        if ((double)this.enabledTicks > this.timeoutTicks.getValue() - 1.0) {
            this.disable();
            return;
        }
        if (!HoleSnap.mc.player.isAlive() || HoleSnap.mc.player.isFallFlying()) {
            this.disable();
            return;
        }
        if (this.stuckTicks > 4) {
            this.disable();
            return;
        }
        this.holePos = CombatUtil.getHole((float)this.range.getValue(), true, this.any.getValue());
        if (this.holePos == null) {
            CommandManager.sendChatMessage("\u00a7e[!] \u00a7fHoles?");
            this.disable();
            return;
        }
        Vec3d playerPos = HoleSnap.mc.player.getPos();
        this.targetPos = new Vec3d((double)this.holePos.getX() + 0.5, HoleSnap.mc.player.getY(), (double)this.holePos.getZ() + 0.5);
        if (CombatUtil.isDoubleHole(this.holePos) && (facing = CombatUtil.is3Block(this.holePos)) != null) {
            this.targetPos = this.targetPos.add(new Vec3d((double)facing.getVector().getX() * 0.5, (double)facing.getVector().getY() * 0.5, (double)facing.getVector().getZ() * 0.5));
        }
        this.resetMove = true;
        float rotation = HoleSnap.getRotationTo((Vec3d)playerPos, (Vec3d)this.targetPos).x;
        float yawRad = rotation / 180.0f * (float)Math.PI;
        double dist = playerPos.distanceTo(this.targetPos);
        double cappedSpeed = Math.min(0.2873, dist);
        double x = (double)(-((float)Math.sin(yawRad))) * cappedSpeed;
        double z = (double)((float)Math.cos(yawRad)) * cappedSpeed;
        event.setX(x);
        event.setZ(z);
        if (Math.abs(x) < 0.1 && Math.abs(z) < 0.1 && playerPos.y <= (double)this.holePos.getY() + 0.5) {
            this.disable();
        }
        this.stuckTicks = HoleSnap.mc.player.horizontalCollision ? ++this.stuckTicks : 0;
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (this.targetPos == null || this.holePos == null) {
            return;
        }
        GL11.glEnable((int)3042);
        Color color = this.color.getValue();
        Vec3d pos = new Vec3d(this.targetPos.x, (double)this.holePos.getY(), this.targetPos.getZ());
        if (this.fade.getValue()) {
            double temp = 0.01;
            for (double i = 0.0; i < this.circleSize.getValue(); i += temp) {
                AutoPeek.doCircle(matrixStack, ColorUtil.injectAlpha(color, (int)Math.min((double)(color.getAlpha() * 2) / (this.circleSize.getValue() / temp), 255.0)), i, pos, this.segments.getValueInt());
            }
        } else {
            AutoPeek.doCircle(matrixStack, color, this.circleSize.getValue(), pos, this.segments.getValueInt());
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glDisable((int)3042);
    }

    public static Vec2f getRotationTo(Vec3d posFrom, Vec3d posTo) {
        Vec3d vec3d = posTo.subtract(posFrom);
        return HoleSnap.getRotationFromVec(vec3d);
    }

    private static Vec2f getRotationFromVec(Vec3d vec) {
        double d = vec.x;
        double d2 = vec.z;
        double xz = Math.hypot(d, d2);
        d2 = vec.z;
        double d3 = vec.x;
        double yaw = HoleSnap.normalizeAngle(Math.toDegrees(Math.atan2(d2, d3)) - 90.0);
        double pitch = HoleSnap.normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
        return new Vec2f((float)yaw, (float)pitch);
    }

    private static double normalizeAngle(double angleIn) {
        double d = 0;
        double angle = angleIn;
        angle %= 360.0;
        if (d >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }
}

