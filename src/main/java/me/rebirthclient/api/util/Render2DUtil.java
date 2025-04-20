/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferBuilder$BuiltBuffer
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.api.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.rebirthclient.api.util.Wrapper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Render2DUtil
implements Wrapper {
    public static void drawBox(MatrixStack matrixStack, int x, int y, int width, int height, Color color, float alpha) {
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
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
    }

    public static void horizontalGradient(MatrixStack matrices, float x1, float y1, float x2, float y2, Color startColor, Color endColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Render2DUtil.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x1, y1, 0.0f).color(startColor.getRGB()).next();
        bufferBuilder.vertex(matrix, x1, y2, 0.0f).color(startColor.getRGB()).next();
        bufferBuilder.vertex(matrix, x2, y2, 0.0f).color(endColor.getRGB()).next();
        bufferBuilder.vertex(matrix, x2, y1, 0.0f).color(endColor.getRGB()).next();
        BufferRenderer.drawWithGlobalProgram((BufferBuilder.BuiltBuffer)bufferBuilder.end());
        Render2DUtil.endRender();
    }

    public static void verticalGradient(MatrixStack matrices, float left, float top, float right, float bottom, Color startColor, Color endColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Render2DUtil.setupRender();
        RenderSystem.setShader(GameRenderer::getParticleProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, left, top, 0.0f).color(startColor.getRGB()).next();
        bufferBuilder.vertex(matrix, left, bottom, 0.0f).color(endColor.getRGB()).next();
        bufferBuilder.vertex(matrix, right, bottom, 0.0f).color(endColor.getRGB()).next();
        bufferBuilder.vertex(matrix, right, top, 0.0f).color(startColor.getRGB()).next();
        BufferRenderer.drawWithGlobalProgram((BufferBuilder.BuiltBuffer)bufferBuilder.end());
        Render2DUtil.endRender();
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, int c) {
        Render2DUtil.drawRect(matrices, x, y, width, height, new Color(c, true));
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color c) {
        if (c.getAlpha() == 0) {
            return;
        }
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Render2DUtil.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y + height, 0.0f).color(c.getRGB()).next();
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0f).color(c.getRGB()).next();
        bufferBuilder.vertex(matrix, x + width, y, 0.0f).color(c.getRGB()).next();
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(c.getRGB()).next();
        Tessellator.getInstance().draw();
        Render2DUtil.endRender();
    }

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
    }

    public static void drawRound(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color) {
        Render2DUtil.renderRoundedQuad(matrices, color, x, y, width + x, height + y, radius, 4.0);
    }

    public static void renderRoundedQuad(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        Render2DUtil.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Render2DUtil.renderRoundedQuadInternal(matrices.peek().getPositionMatrix(), (float)c.getRed() / 255.0f, (float)c.getGreen() / 255.0f, (float)c.getBlue() / 255.0f, (float)c.getAlpha() / 255.0f, fromX, fromY, toX, toY, radius, samples);
        Render2DUtil.endRender();
    }

    public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        double[][] map = new double[][]{{toX - radius, toY - radius, radius}, {toX - radius, fromY + radius, radius}, {fromX + radius, fromY + radius, radius}, {fromX + radius, toY - radius, radius}};
        for (int i = 0; i < 4; ++i) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = (double)i * 90.0; r < 90.0 + (double)i * 90.0; r += 90.0 / samples) {
                float rad1 = (float)Math.toRadians(r);
                float sin = (float)(Math.sin(rad1) * rad);
                float cos = (float)(Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float)current[0] + sin, (float)current[1] + cos, 0.0f).color(cr, cg, cb, ca).next();
            }
            float rad1 = (float)Math.toRadians(90.0 + (double)i * 90.0);
            float sin = (float)(Math.sin(rad1) * rad);
            float cos = (float)(Math.cos(rad1) * rad);
            bufferBuilder.vertex(matrix, (float)current[0] + sin, (float)current[1] + cos, 0.0f).color(cr, cg, cb, ca).next();
        }
        BufferRenderer.drawWithGlobalProgram((BufferBuilder.BuiltBuffer)bufferBuilder.end());
    }

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void endRender() {
        RenderSystem.disableBlend();
    }
}

