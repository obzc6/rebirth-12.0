/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.entity.EndCrystalEntityRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionf
 */
package me.rebirthclient.mod.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class CrystalRenderer
extends Module {
    public static CrystalRenderer INSTANCE;
    public static HashMap<EndCrystalEntity, Double> spinMap;
    public static HashMap<Vec3d, Double> posSpinMap;
    public static HashMap<EndCrystalEntity, Double> floatMap;
    public static HashMap<Vec3d, Double> posFloatMap;
    public static Random random;
    public BooleanSetting cube = this.add(new BooleanSetting("CubeCrystal", true));
    public BooleanSetting cham = this.add(new BooleanSetting("CrystalChams", true)).setParent();
    public BooleanSetting line = this.add(new BooleanSetting("Line", true, v -> this.cham.isOpen()));
    private final ColorSetting crystalColor = this.add(new ColorSetting("CrystalColor", new Color(-1825711896, true), v -> this.cham.isOpen()));
    public BooleanSetting setting = this.add(new BooleanSetting("CrystalSetting", false).setParent());
    public final SliderSetting spinValue = this.add(new SliderSetting("SpinSpeed", 1.0, 0.0, 3.0, 0.01, v -> this.setting.isOpen()));
    public final SliderSetting floatValue = this.add(new SliderSetting("FloatSpeed", 1.0, 0.0, 3.0, 0.01, v -> this.setting.isOpen()));
    public final SliderSetting floatOffset = this.add(new SliderSetting("FloatOffset", 0.0, -1.0, 1.0, 0.01, v -> this.setting.isOpen()));
    public BooleanSetting sync = this.add(new BooleanSetting("Sync", true));
    public final SliderSetting spinAdd = this.add(new SliderSetting("SpinNewAdd", 0.0, 0.0, 100.0, 1.0, v -> this.sync.getValue()));
    private final Identifier crystalTexture2 = new Identifier("textures/end_crystal2.png");

    public CrystalRenderer() {
        super("CrystalRenderer", Module.Category.Render);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (!this.sync.getValue()) {
            return;
        }
        ArrayList<EndCrystalEntity> noSpinAge = new ArrayList<EndCrystalEntity>();
        ArrayList<EndCrystalEntity> noFloatAge = new ArrayList<EndCrystalEntity>();
        for (Entity entity : CrystalRenderer.mc.world.getEntities()) {
            if (!(entity instanceof EndCrystalEntity)) continue;
            EndCrystalEntity crystal = (EndCrystalEntity)entity;
            if (spinMap.getOrDefault((Object)crystal, -1.0) != -1.0) {
                spinMap.put(crystal, spinMap.get((Object)crystal) + 1.0);
                posSpinMap.put(crystal.getPos(), spinMap.get((Object)crystal));
            } else {
                noSpinAge.add(crystal);
            }
            if (floatMap.getOrDefault((Object)crystal, -1.0) != -1.0) {
                floatMap.put(crystal, floatMap.get((Object)crystal) + 1.0);
                posFloatMap.put(crystal.getPos(), floatMap.get((Object)crystal));
                continue;
            }
            noFloatAge.add(crystal);
        }
        for (EndCrystalEntity crystal : noSpinAge) {
            if (spinMap.getOrDefault((Object)crystal, -1.0) != -1.0) continue;
            spinMap.put(crystal, posSpinMap.getOrDefault((Object)crystal.getPos(), Double.valueOf(random.nextInt(10000))) + this.spinAdd.getValue());
        }
        for (EndCrystalEntity crystal : noFloatAge) {
            if (floatMap.getOrDefault((Object)crystal, -1.0) != -1.0) continue;
            floatMap.put(crystal, posFloatMap.getOrDefault((Object)crystal.getPos(), Double.valueOf(random.nextInt(10000))));
        }
    }

    public double getSpinAge(EndCrystalEntity crystal) {
        double age;
        if (!this.sync.getValue()) {
            return crystal.endCrystalAge;
        }
        if (spinMap.getOrDefault((Object)crystal, -1.0) == -1.0) {
            spinMap.put(crystal, posSpinMap.getOrDefault((Object)crystal.getPos(), Double.valueOf(random.nextInt(10000))) + this.spinAdd.getValue());
        }
        if ((age = spinMap.getOrDefault((Object)crystal, posSpinMap.getOrDefault((Object)crystal.getPos(), -1.0)).doubleValue()) != -1.0) {
            return age;
        }
        age = random.nextInt(10000);
        posSpinMap.put(crystal.getPos(), age);
        return age;
    }

    public double getFloatAge(EndCrystalEntity crystal) {
        double age;
        if (!this.sync.getValue()) {
            return crystal.endCrystalAge;
        }
        if (floatMap.getOrDefault((Object)crystal, -1.0) == -1.0) {
            floatMap.put(crystal, posFloatMap.getOrDefault((Object)crystal.getPos(), Double.valueOf(random.nextInt(10000))));
        }
        if ((age = floatMap.getOrDefault((Object)crystal, posFloatMap.getOrDefault((Object)crystal.getPos(), -1.0)).doubleValue()) != -1.0) {
            return age;
        }
        age = random.nextInt(10000);
        posFloatMap.put(crystal.getPos(), age);
        return age;
    }

    public void renderCrystal(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, int i, ModelPart core, ModelPart frame) {
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        if (this.line.getValue()) {
            RenderSystem.setShaderTexture((int)0, (Identifier)this.crystalTexture2);
            RenderSystem.setShader(GameRenderer::getParticleProgram);
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        } else {
            RenderSystem.setShader(GameRenderer::getParticleProgram);
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        }
        matrixStack.push();
        float h = EndCrystalEntityRenderer.getYOffset((EndCrystalEntity)endCrystalEntity, (float)g);
        float j = ((float)endCrystalEntity.endCrystalAge + g) * 3.0f;
        matrixStack.push();
        RenderSystem.setShaderColor((float)((float)this.crystalColor.getValue().getRed() / 255.0f), (float)((float)this.crystalColor.getValue().getGreen() / 255.0f), (float)((float)this.crystalColor.getValue().getBlue() / 255.0f), (float)((float)this.crystalColor.getValue().getAlpha() / 255.0f));
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0f, -0.5f, 0.0f);
        int k = OverlayTexture.DEFAULT_UV;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, (float)Math.sin(0.7853981633974483), (float)Math.sin(0.7853981633974483), (float)Math.sin(0.7853981633974483)));
        frame.render(matrixStack, (VertexConsumer)buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, (float)Math.sin(0.7853981633974483), 0.0f, (float)Math.sin(0.7853981633974483)));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        frame.render(matrixStack, (VertexConsumer)buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, (float)Math.sin(0.7853981633974483), 0.0f, (float)Math.sin(0.7853981633974483)));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        core.render(matrixStack, (VertexConsumer)buffer, i, k);
        matrixStack.pop();
        matrixStack.pop();
        tessellator.draw();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    static {
        spinMap = new HashMap();
        posSpinMap = new HashMap();
        floatMap = new HashMap();
        posFloatMap = new HashMap();
        random = new Random();
    }
}

