/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.api.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Objects;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.gui.font.FontRenderers;
import me.rebirthclient.mod.modules.client.ClickGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class TextUtil
implements Wrapper {
    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();

    public static boolean isCustomFont() {
        return ClickGui.INSTANCE.customFont.getValue() && FontRenderers.Arial != null;
    }

    public static float getCustomWidth(String s) {
        return TextUtil.isCustomFont() ? FontRenderers.Arial.getWidth(s) : (float)TextUtil.mc.textRenderer.getWidth(s);
    }

    public static float getCustomHeight() {
        float f;
        if (TextUtil.isCustomFont()) {
            f = FontRenderers.Arial.getFontHeight();
        } else {
            Objects.requireNonNull(TextUtil.mc.textRenderer);
            f = 9.0f;
        }
        return f * 2.0f;
    }

    public static void drawOutlinedBox(MatrixStack matrixStack, int x, int y, int width, int height, Color color, float alpha) {
        RenderSystem.setShaderColor((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)alpha);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)2929);
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.vertex(matrix, (float)x, (float)y, 0.0f).next();
        bufferBuilder.vertex(matrix, (float)(x + width), (float)y, 0.0f).next();
        bufferBuilder.vertex(matrix, (float)(x + width), (float)(y + height), 0.0f).next();
        bufferBuilder.vertex(matrix, (float)x, (float)(y + height), 0.0f).next();
        tessellator.draw();
        RenderSystem.setShaderColor((float)0.0f, (float)0.0f, (float)0.0f, (float)alpha);
        RenderSystem.setShader(GameRenderer::getParticleProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
        bufferBuilder.vertex(matrix, (float)x, (float)y, 0.0f).next();
        bufferBuilder.vertex(matrix, (float)(x + width), (float)y, 0.0f).next();
        bufferBuilder.vertex(matrix, (float)(x + width), (float)(y + height), 0.0f).next();
        bufferBuilder.vertex(matrix, (float)x, (float)(y + height), 0.0f).next();
        bufferBuilder.vertex(matrix, (float)x, (float)y, 0.0f).next();
        tessellator.draw();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
    }

    public static void drawString(DrawContext drawContext, String text, float x, float y, Color color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 1.0f);
        matrixStack.translate(-x / 2.0f, -y / 2.0f, 0.0f);
        drawContext.drawText(mc.textRenderer, text, (int)x, (int)y, color.getRGB(), true);
        matrixStack.pop();
    }

    public static void drawString(DrawContext drawContext, String text, double x, double y, int color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 1.0f);
        matrixStack.translate(-x / 2.0, -y / 2.0, 0.0);
        drawContext.drawText(mc.textRenderer, text, (int)x, (int)y, color, true);
        matrixStack.pop();
    }

    public static void drawStringWithScale(DrawContext drawContext, String text, float x, float y, Color color, float scale) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        if (scale != 1.0f) {
            matrixStack.push();
            matrixStack.scale(scale, scale, 1.0f);
            if (scale > 1.0f) {
                matrixStack.translate(-x / scale, -y / scale, 0.0f);
            } else {
                matrixStack.translate(x / scale - x, y * scale - y, 0.0f);
            }
        }
        drawContext.drawText(mc.textRenderer, text, (int)x, (int)y, color.getRGB(), true);
        matrixStack.pop();
    }

    public static void drawCustomText(DrawContext drawContext, String text, double x, double y, Color color) {
        TextUtil.drawCustomText(drawContext, text, x, y, color.getRGB());
    }

    public static void drawCustomText(DrawContext drawContext, String text, double x, double y, int color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 1.0f);
        matrixStack.translate(-x / 2.0, -y / 2.0, 0.0);
        if (ClickGui.INSTANCE.customFont.getValue()) {
            FontRenderers.Arial.drawString(drawContext.getMatrices(), text, (float)x, (float)y + 2.0f, color);
        } else {
            drawContext.drawText(mc.textRenderer, text, (int)x, (int)y, color, true);
        }
        matrixStack.pop();
    }

    public static void drawCustomSmallText(DrawContext drawContext, String text, double x, double y, int color) {
        if (ClickGui.INSTANCE.customFont.getValue()) {
            FontRenderers.Arial.drawString(drawContext.getMatrices(), text, (float)x, (float)y + 1.0f, color);
        } else {
            drawContext.drawText(TextUtil.mc.textRenderer, text, (int)x, (int)y, color, true);
        }
    }

    public static Vec3d worldSpaceToScreenSpace(Vec3d pos) {
        Camera camera = TextUtil.mc.getEntityRenderDispatcher().camera;
        int displayHeight = mc.getWindow().getHeight();
        int[] viewport = new int[4];
        GL11.glGetIntegerv((int)2978, (int[])viewport);
        Vector3f target = new Vector3f();
        double deltaX = pos.x - camera.getPos().x;
        double deltaY = pos.y - camera.getPos().y;
        double deltaZ = pos.z - camera.getPos().z;
        Vector4f transformedCoordinates = new Vector4f((float)deltaX, (float)deltaY, (float)deltaZ, 1.0f).mul((Matrix4fc)lastWorldSpaceMatrix);
        Matrix4f matrixProj = new Matrix4f((Matrix4fc)lastProjMat);
        Matrix4f matrixModel = new Matrix4f((Matrix4fc)lastModMat);
        matrixProj.mul((Matrix4fc)matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);
        return new Vec3d((double)target.x / mc.getWindow().getScaleFactor(), (double)((float)displayHeight - target.y) / mc.getWindow().getScaleFactor(), (double)target.z);
    }

    public static void drawText(DrawContext context, String text, Vec3d vector) {
        TextUtil.drawText(context, text, vector, -1);
    }

    public static void drawText(DrawContext context, String text, Vec3d vector, int color) {
        Vec3d preVec = vector;
        vector = TextUtil.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
        if (vector.z > 0.0 && vector.z < 1.0) {
            double posX = vector.x;
            double posY = vector.y;
            double endPosX = Math.max(vector.x, vector.z);
            float scale = (float)Math.max(1.0 - EntityUtil.getEyesPos().distanceTo(preVec) * 0.025, 0.0);
            float diff = (float)(endPosX - posX) / 2.0f;
            float textWidth = (float)TextUtil.mc.textRenderer.getWidth(text) * scale;
            float tagX = (float)((posX + (double)diff - (double)(textWidth / 2.0f)) * 1.0);
            context.getMatrices().push();
            context.getMatrices().scale(scale, scale, scale);
            int n = (int)(tagX / scale);
            Objects.requireNonNull(TextUtil.mc.textRenderer);
            context.drawText(TextUtil.mc.textRenderer, text, n, (int)((posY - 11.0 + 9.0 * 1.2) / (double)scale), color, true);
            context.getMatrices().pop();
        }
    }
}

