/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import java.awt.Color;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.RunManager;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.MathUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Unique
    private LivingEntity lastEntity;
    @Unique
    private float originalYaw;
    @Unique
    private float originalHeadYaw;
    @Unique
    private float originalBodyYaw;
    @Unique
    private float originalPitch;
    @Unique
    private float originalPrevYaw;
    @Unique
    private float originalPrevHeadYaw;
    @Unique
    private float originalPrevBodyYaw;

    @Inject(method={"render"}, at={@At(value="HEAD")})
    public void onRenderPre(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null && livingEntity == MinecraftClient.getInstance().player && Rebirth.HUD.rotations.getValue()) {
            this.originalYaw = livingEntity.getYaw();
            this.originalHeadYaw = ((LivingEntity)livingEntity).headYaw;
            this.originalBodyYaw = ((LivingEntity)livingEntity).bodyYaw;
            this.originalPitch = livingEntity.getPitch();
            this.originalPrevYaw = ((LivingEntity)livingEntity).prevYaw;
            this.originalPrevHeadYaw = ((LivingEntity)livingEntity).prevHeadYaw;
            this.originalPrevBodyYaw = ((LivingEntity)livingEntity).prevBodyYaw;
            livingEntity.setYaw(RunManager.getRenderYawOffset());
            ((LivingEntity)livingEntity).headYaw = RunManager.getRotationYawHead();
            ((LivingEntity)livingEntity).bodyYaw = RunManager.getRenderYawOffset();
            livingEntity.setPitch(RunManager.getRenderPitch());
            ((LivingEntity)livingEntity).prevYaw = RunManager.getPrevRenderYawOffset();
            ((LivingEntity)livingEntity).prevHeadYaw = RunManager.getPrevRotationYawHead();
            ((LivingEntity)livingEntity).prevBodyYaw = RunManager.getPrevRenderYawOffset();
            ((LivingEntity)livingEntity).prevPitch = RunManager.getPrevPitch();
        }
        this.lastEntity = livingEntity;
    }

    @Inject(method={"render"}, at={@At(value="TAIL")})
    public void onRenderPost(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null && livingEntity == MinecraftClient.getInstance().player && Rebirth.HUD.rotations.getValue()) {
            livingEntity.setYaw(this.originalYaw);
            ((LivingEntity)livingEntity).headYaw = this.originalHeadYaw;
            ((LivingEntity)livingEntity).bodyYaw = this.originalBodyYaw;
            livingEntity.setPitch(this.originalPitch);
            ((LivingEntity)livingEntity).prevYaw = this.originalPrevYaw;
            ((LivingEntity)livingEntity).prevHeadYaw = this.originalPrevHeadYaw;
            ((LivingEntity)livingEntity).prevBodyYaw = this.originalPrevBodyYaw;
            ((LivingEntity)livingEntity).prevPitch = this.originalPitch;
        }
    }

    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void onRenderModel(EntityModel entityModel, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        Color newColor = new Color(red, green, blue, alpha);
        if (NoRender.INSTANCE.isOn() && NoRender.INSTANCE.antiPlayerCollision.getValue() && this.lastEntity != Wrapper.mc.player) {
            float overrideAlpha = (float)(Wrapper.mc.player.squaredDistanceTo(this.lastEntity.getPos()) / 3.0) + 0.07f;
            newColor = ColorUtil.injectAlpha(newColor, (int)(255.0f * MathUtil.clamp(overrideAlpha, 0.0f, 1.0f)));
        }
        entityModel.render(matrices, vertices, light, overlay, (float)newColor.getRed() / 255.0f, (float)newColor.getGreen() / 255.0f, (float)newColor.getBlue() / 255.0f, (float)newColor.getAlpha() / 255.0f);
    }
}

