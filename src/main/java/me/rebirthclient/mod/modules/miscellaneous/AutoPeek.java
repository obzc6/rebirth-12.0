/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.BowItem
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.mod.modules.miscellaneous;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.MoveEvent;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.movement.HoleSnap;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BowItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class AutoPeek
extends Module {
    public final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));
    public SliderSetting circleSize = this.add(new SliderSetting("CircleSize", 1.0, 0.1f, 2.5));
    public BooleanSetting fade = this.add(new BooleanSetting("Fade", true));
    public SliderSetting segments = this.add(new SliderSetting("Segments", 180, 0, 360));
    public BooleanSetting inAir = this.add(new BooleanSetting("InAir", true));
    public BooleanSetting timer = this.add(new BooleanSetting("Timer", true));
    public BooleanSetting teleport = this.add(new BooleanSetting("Teleport", false));
    public BooleanSetting onlyUsing = this.add(new BooleanSetting("OnlyUsing", true));
    private Vec3d pos;
    private boolean back = false;

    public AutoPeek() {
        super("AutoPeek", Module.Category.Miscellaneous);
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        PlayerActionC2SPacket packet;
        Object t = event.getPacket();
        if (t instanceof PlayerActionC2SPacket && (packet = (PlayerActionC2SPacket)t).getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM && AutoPeek.mc.player.getActiveItem().getItem() instanceof BowItem) {
            this.back = this.onlyUsing.getValue();
        }
    }

    @Override
    public void onEnable() {
        if (AutoPeek.nullCheck()) {
            this.disable();
            return;
        }
        this.pos = AutoPeek.mc.player.getPos();
    }

    @Override
    public void onDisable() {
        Rebirth.TIMER.reset();
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        double z;
        double x;
        if (!AutoPeek.mc.player.isAlive() || AutoPeek.mc.player.isFallFlying()) {
            this.disable();
            return;
        }
        if ((MovementUtil.isMoving() && (!this.onlyUsing.getValue() || EntityUtil.isUsing()) || this.onlyUsing.getValue() && EntityUtil.isUsing()) && !this.back || !AutoPeek.mc.player.isOnGround() && !this.inAir.getValue()) {
            Rebirth.TIMER.set(1.0f);
            return;
        }
        if (this.pos == null) {
            this.disable();
            return;
        }
        if (this.timer.getValue()) {
            Rebirth.TIMER.set(3.0f);
        }
        if (this.teleport.getValue()) {
            Vec3d playerPos = AutoPeek.mc.player.getPos();
            x = this.pos.x - playerPos.x;
            z = this.pos.z - playerPos.z;
        } else {
            Vec3d playerPos = AutoPeek.mc.player.getPos();
            float rotation = HoleSnap.getRotationTo((Vec3d)playerPos, (Vec3d)new Vec3d((double)this.pos.x, (double)playerPos.y, (double)this.pos.z)).x;
            float yawRad = rotation / 180.0f * (float)Math.PI;
            double dist = playerPos.distanceTo(new Vec3d(this.pos.x, playerPos.y, this.pos.z));
            double cappedSpeed = Math.min(0.2873, dist);
            x = (double)(-((float)Math.sin(yawRad))) * cappedSpeed;
            z = (double)((float)Math.cos(yawRad)) * cappedSpeed;
        }
        if (Math.abs(x) < 0.1 && Math.abs(z) < 0.1) {
            this.back = false;
            Rebirth.TIMER.set(1.0f);
        }
        event.setX(x);
        event.setZ(z);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (this.pos == null) {
            this.disable();
            return;
        }
        GL11.glEnable((int)3042);
        Color color = this.color.getValue();
        if (this.fade.getValue()) {
            double temp = 0.01;
            for (double i = 0.0; i < this.circleSize.getValue(); i += temp) {
                AutoPeek.doCircle(matrixStack, ColorUtil.injectAlpha(color, (int)Math.min((double)(color.getAlpha() * 2) / (this.circleSize.getValue() / temp), 255.0)), i, this.pos, this.segments.getValueInt());
            }
        } else {
            AutoPeek.doCircle(matrixStack, color, this.circleSize.getValue(), this.pos, this.segments.getValueInt());
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glDisable((int)3042);
    }

    public static void doCircle(MatrixStack matrixStack, Color color, double circleSize, Vec3d pos, int segments) {
        GL11.glDisable((int)2929);
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShaderColor((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
        for (double i = 0.0; i < 360.0; i += 360.0 / (double)segments) {
            double x = Math.sin(Math.toRadians(i)) * circleSize;
            double z = Math.cos(Math.toRadians(i)) * circleSize;
            Vec3d tempPos = new Vec3d(pos.x + x, pos.y, pos.z + z);
            bufferBuilder.vertex(matrix, (float)tempPos.x, (float)tempPos.y, (float)tempPos.z).next();
        }
        tessellator.draw();
        GL11.glEnable((int)2929);
    }
}

