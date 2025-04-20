/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.api.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.gui.font.FontRenderers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class Render3DUtil
implements Wrapper {
    private static float prevCircleStep;
    private static float circleStep;

    public static void drawTextIn3D(String text, Vec3d pos, double offX, double offY, double textOffset, Color color) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = Render3DUtil.mc.gameRenderer.getCamera();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(pos.getX() - camera.getPos().x, pos.getY() - camera.getPos().y, pos.getZ() - camera.getPos().z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        matrices.translate(offX, offY - 0.1, -0.01);
        matrices.scale(-0.025f, -0.025f, 0.0f);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate((BufferBuilder)Tessellator.getInstance().getBuffer());
        FontRenderers.Arial.drawCenteredString(matrices, text, textOffset, 0.0, color.getRGB());
        immediate.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void drawBBFill(MatrixStack matrixStack, Box bb, Color color) {
        Render3DUtil.draw3DBox(matrixStack, bb, color, false, true);
    }

    public static void drawBBBox(MatrixStack matrixStack, Box bb, Color color) {
        Render3DUtil.draw3DBox(matrixStack, bb, color, true, false);
    }

    public static void draw3DBox(MatrixStack matrixStack, Box box, Color color) {
        Render3DUtil.draw3DBox(matrixStack, box, color, true, true);
    }

    public static void draw3DBox(MatrixStack matrixStack, Box box, Color color, boolean outline, boolean fill) {
        GL11.glEnable((int)3042);
        GL11.glDisable((int)2929);
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (outline) {
            RenderSystem.setShaderColor((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).next();
            tessellator.draw();
        }
        if (fill) {
            RenderSystem.setShaderColor((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).next();
            bufferBuilder.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).next();
            tessellator.draw();
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
    }

    public static void drawFilledBox(MatrixStack stack, Box box, Color c) {
        float minX = (float)box.minX;
        float minY = (float)box.minY;
        float minZ = (float)box.minZ;
        float maxX = (float)box.maxX;
        float maxY = (float)box.maxY;
        float maxZ = (float)box.maxZ;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GL11.glEnable((int)3042);
        GL11.glDisable((int)2929);
        RenderSystem.setShaderColor((float)((float)c.getRed() / 255.0f), (float)((float)c.getGreen() / 255.0f), (float)((float)c.getBlue() / 255.0f), (float)((float)c.getAlpha() / 255.0f));
        RenderSystem.setShader(GameRenderer::getParticleProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = stack.peek().getPositionMatrix();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB()).next();
        tessellator.draw();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
    }

    public static void drawFadeLine(MatrixStack matrixStack, Vec3d start, Vec3d end, Color color) {
        GL11.glEnable((int)3042);
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float)start.x, (float)start.y, (float)start.z).color(color.getRGB()).next();
        bufferBuilder.vertex(matrix, (float)end.x, (float)end.y, (float)end.z).color(ColorUtil.injectAlpha(color, 0).getRGB()).next();
        tessellator.draw();
        GL11.glDisable((int)3042);
    }

    public static void drawLine(Box b, Color color, float lineWidth) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        MatrixStack matrices = Render3DUtil.matrixFrom(b.minX, b.minY, b.minZ);
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.lineWidth((float)lineWidth);
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        Box box = b.offset(new Vec3d(b.minX, b.minY, b.minZ).negate());
        float x1 = (float)box.minX;
        float y1 = (float)box.minY;
        float z1 = (float)box.minZ;
        float x2 = (float)box.maxX;
        float y2 = (float)box.maxY;
        float z2 = (float)box.maxZ;
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x1), Float.valueOf(y1), Float.valueOf(z1), Float.valueOf(x2), Float.valueOf(y1), Float.valueOf(z1), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x2), Float.valueOf(y1), Float.valueOf(z1), Float.valueOf(x2), Float.valueOf(y1), Float.valueOf(z2), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x2), Float.valueOf(y1), Float.valueOf(z2), Float.valueOf(x1), Float.valueOf(y1), Float.valueOf(z2), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x1), Float.valueOf(y1), Float.valueOf(z2), Float.valueOf(x1), Float.valueOf(y1), Float.valueOf(z1), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x1), Float.valueOf(y1), Float.valueOf(z2), Float.valueOf(x1), Float.valueOf(y2), Float.valueOf(z2), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x1), Float.valueOf(y1), Float.valueOf(z1), Float.valueOf(x1), Float.valueOf(y2), Float.valueOf(z1), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x2), Float.valueOf(y1), Float.valueOf(z2), Float.valueOf(x2), Float.valueOf(y2), Float.valueOf(z2), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x2), Float.valueOf(y1), Float.valueOf(z1), Float.valueOf(x2), Float.valueOf(y2), Float.valueOf(z1), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x1), Float.valueOf(y2), Float.valueOf(z1), Float.valueOf(x2), Float.valueOf(y2), Float.valueOf(z1), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x2), Float.valueOf(y2), Float.valueOf(z1), Float.valueOf(x2), Float.valueOf(y2), Float.valueOf(z2), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x2), Float.valueOf(y2), Float.valueOf(z2), Float.valueOf(x1), Float.valueOf(y2), Float.valueOf(z2), color);
        Render3DUtil.vertexLine(matrices, (VertexConsumer)buffer, Float.valueOf(x1), Float.valueOf(y2), Float.valueOf(z2), Float.valueOf(x1), Float.valueOf(y2), Float.valueOf(z1), color);
        tessellator.draw();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void vertexLine(MatrixStack matrices, VertexConsumer vertexConsumer, Float x1, Float y1, Float z1, Float x2, Float y2, Float z2, Color lineColor) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        Matrix3f normal = matrices.peek().getNormalMatrix();
        Vector3f normalVec = Render3DUtil.getNormal(x1, y1, z1, x2, y2, z2);
        vertexConsumer.vertex(model, x1.floatValue(), y1.floatValue(), z1.floatValue()).color(lineColor.getRGB()).normal(normal, normalVec.x(), normalVec.y(), normalVec.z()).next();
        vertexConsumer.vertex(model, x2.floatValue(), y2.floatValue(), z2.floatValue()).color(lineColor.getRGB()).normal(normal, normalVec.x(), normalVec.y(), normalVec.z()).next();
    }

    public static Vector3f getNormal(Float x1, Float y1, Float z1, Float x2, Float y2, Float z2) {
        Float xNormal = Float.valueOf(x2.floatValue() - x1.floatValue());
        Float yNormal = Float.valueOf(y2.floatValue() - y1.floatValue());
        Float zNormal = Float.valueOf(z2.floatValue() - z1.floatValue());
        Float normalSqrt = Float.valueOf(MathHelper.sqrt((float)(xNormal.floatValue() * xNormal.floatValue() + yNormal.floatValue() * yNormal.floatValue() + zNormal.floatValue() * zNormal.floatValue())));
        return new Vector3f(xNormal.floatValue() / normalSqrt.floatValue(), yNormal.floatValue() / normalSqrt.floatValue(), zNormal.floatValue() / normalSqrt.floatValue());
    }

    public static MatrixStack matrixFrom(Double x, Double y, Double z) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);
        return matrices;
    }

    public static void updateJello() {
        prevCircleStep = circleStep;
        circleStep += 0.15f;
    }

    public static void drawJello(MatrixStack matrix, Entity target, Color color) {
        double cs = prevCircleStep + (circleStep - prevCircleStep) * mc.getTickDelta();
        double prevSinAnim = Render3DUtil.absSinAnimation(cs - (double)0.45f);
        double sinAnim = Render3DUtil.absSinAnimation(cs);
        double x = target.prevX + (target.getX() - target.prevX) * (double)mc.getTickDelta();
        double y = target.prevY + (target.getY() - target.prevY) * (double)mc.getTickDelta() + prevSinAnim * (double)target.getHeight();
        double z = target.prevZ + (target.getZ() - target.prevZ) * (double)mc.getTickDelta();
        double nextY = target.prevY + (target.getY() - target.prevY) * (double)mc.getTickDelta() + sinAnim * (double)target.getHeight();
        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 30; ++i) {
            float cos = (float)(x + Math.cos((double)i * 6.28 / 30.0) * (target.getBoundingBox().maxX - target.getBoundingBox().minX + (target.getBoundingBox().maxZ - target.getBoundingBox().minZ)) * 0.5);
            float sin = (float)(z + Math.sin((double)i * 6.28 / 30.0) * (target.getBoundingBox().maxX - target.getBoundingBox().minX + (target.getBoundingBox().maxZ - target.getBoundingBox().minZ)) * 0.5);
            bufferBuilder.vertex(matrix.peek().getPositionMatrix(), cos, (float)nextY, sin).color(color.getRGB()).next();
            bufferBuilder.vertex(matrix.peek().getPositionMatrix(), cos, (float)y, sin).color(ColorUtil.injectAlpha(color, 0).getRGB()).next();
        }
        tessellator.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrix.pop();
    }

    private static double absSinAnimation(double input) {
        return Math.abs(1.0 + Math.sin(input)) / 2.0;
    }
}

