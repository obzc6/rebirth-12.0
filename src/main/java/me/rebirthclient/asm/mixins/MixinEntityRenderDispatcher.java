/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.EntityRenderDispatcher
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  org.spongepowered.asm.mixin.Mixin
 */
package me.rebirthclient.asm.mixins;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
    public void renderHook(EntityRenderer instance, Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity instanceof EndCrystalEntity) {
            instance.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        } else {
            instance.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }
}

