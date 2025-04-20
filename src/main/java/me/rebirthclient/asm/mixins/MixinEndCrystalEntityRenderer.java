/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.EndCrystalEntityRenderer
 *  net.minecraft.client.render.entity.EnderDragonEntityRenderer
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionf
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.mod.modules.render.CrystalRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EndCrystalEntityRenderer.class})
public abstract class MixinEndCrystalEntityRenderer
extends EntityRenderer<EndCrystalEntity> {
    @Final
    @Shadow
    private static RenderLayer END_CRYSTAL;
    @Final
    @Shadow
    private static float SINE_45_DEGREES;
    @Final
    @Shadow
    private ModelPart core;
    @Final
    @Shadow
    private ModelPart frame;
    @Final
    @Shadow
    private ModelPart bottom;

    protected MixinEndCrystalEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    private float yOffset(EndCrystalEntity crystal, float tickDelta) {
        float f = (float)(CrystalRenderer.INSTANCE.getFloatAge(crystal) + (double)tickDelta) * CrystalRenderer.INSTANCE.floatValue.getValueFloat();
        float g = MathHelper.sin((float)(f * 0.2f)) / 2.0f + 0.5f;
        g = (g * g + g) * 0.4f;
        return g - 1.4f + CrystalRenderer.INSTANCE.floatOffset.getValueFloat();
    }

    @Inject(method={"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void render(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (CrystalRenderer.INSTANCE.isOn() && CrystalRenderer.INSTANCE.setting.getValue()) {
            ci.cancel();
            matrixStack.push();
            float h = this.yOffset(endCrystalEntity, g);
            float j = (float)((CrystalRenderer.INSTANCE.getSpinAge(endCrystalEntity) + (double)g) * 3.0 * CrystalRenderer.INSTANCE.spinValue.getValue());
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
            matrixStack.push();
            matrixStack.scale(2.0f, 2.0f, 2.0f);
            matrixStack.translate(0.0f, -0.5f, 0.0f);
            int k = OverlayTexture.DEFAULT_UV;
            if (endCrystalEntity.shouldShowBottom()) {
                this.bottom.render(matrixStack, vertexConsumer, i, k);
            }
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
            matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
            matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
            this.frame.render(matrixStack, vertexConsumer, i, k);
            matrixStack.scale(0.875f, 0.875f, 0.875f);
            matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
            this.frame.render(matrixStack, vertexConsumer, i, k);
            matrixStack.scale(0.875f, 0.875f, 0.875f);
            matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
            this.core.render(matrixStack, vertexConsumer, i, k);
            matrixStack.pop();
            matrixStack.pop();
            BlockPos blockPos = endCrystalEntity.getBeamTarget();
            if (blockPos != null) {
                float m = (float)blockPos.getX() + 0.5f;
                float n = (float)blockPos.getY() + 0.5f;
                float o = (float)blockPos.getZ() + 0.5f;
                float p = (float)((double)m - endCrystalEntity.getX());
                float q = (float)((double)n - endCrystalEntity.getY());
                float r = (float)((double)o - endCrystalEntity.getZ());
                matrixStack.translate(p, q, r);
                EnderDragonEntityRenderer.renderCrystalBeam((float)(-p), (float)(-q + h), (float)(-r), (float)g, (int)endCrystalEntity.endCrystalAge, (MatrixStack)matrixStack, (VertexConsumerProvider)vertexConsumerProvider, (int)i);
            }
            super.render(endCrystalEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }
}

