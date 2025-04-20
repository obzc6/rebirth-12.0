/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ItemEntityRenderer
 *  net.minecraft.client.render.item.ItemRenderer
 *  net.minecraft.client.render.model.BakedModel
 *  net.minecraft.client.render.model.json.ModelTransformationMode
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.random.Random
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.render.TwoDItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemEntityRenderer.class})
public abstract class MixinItemEntityRenderer
        extends EntityRenderer<ItemEntity> {
    @Final
    @Shadow
    private ItemRenderer itemRenderer;
    @Final
    @Shadow
    private Random random;

    @Shadow
    private int getRenderedAmount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }
        return i;
    }

    protected MixinItemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method={"render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        float t;
        float s;
        if (!TwoDItem.INSTANCE.isOn()) {
            return;
        }
        ci.cancel();
        matrixStack.push();
        ItemStack itemStack = itemEntity.getStack();
        int j = itemStack.isEmpty() ? 187 : Item.getRawId((Item)itemStack.getItem()) + itemStack.getDamage();
        this.random.setSeed((long)j);
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.getWorld(), null, itemEntity.getId());
        boolean bl = bakedModel.hasDepth();
        int k = this.getRenderedAmount(itemStack);
        float l = MathHelper.sin((float)(((float)itemEntity.getItemAge() + g) / 10.0f + itemEntity.uniqueOffset)) * 0.1f + 0.1f;
        float m = bakedModel.getTransformation().getTransformation((ModelTransformationMode)ModelTransformationMode.GROUND).scale.y();
        matrixStack.translate(0.0f, l + 0.25f * m, 0.0f);
        double cameraX = Wrapper.mc.gameRenderer.getCamera().getPos().x;
        double cameraY = Wrapper.mc.gameRenderer.getCamera().getPos().y;
        double cameraZ = Wrapper.mc.gameRenderer.getCamera().getPos().z;
        double dx = itemEntity.getX() - cameraX;
        double dy = itemEntity.getY() - cameraY;
        double dz = itemEntity.getZ() - cameraZ;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float)MathHelper.wrapDegrees((double)(-Math.toDegrees(Math.atan2(dy, horizontalDistance))));
        double angle = Math.atan2(dz, -dx) * 57.29577951308232 - 90.0;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)angle));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        if (itemStack.getItem() instanceof BlockItem) {
            matrixStack.scale(1.4f, 1.4f, 1.4f);
        } else {
            matrixStack.scale(1.1f, 1.1f, 1.1f);
        }
        float o = bakedModel.getTransformation().ground.scale.x();
        float p = bakedModel.getTransformation().ground.scale.y();
        float q = bakedModel.getTransformation().ground.scale.z();
        if (!bl) {
            float r = -0.0f * (float)(k - 1) * 0.5f * o;
            s = -0.0f * (float)(k - 1) * 0.5f * p;
            t = -0.09375f * (float)(k - 1) * 0.5f * q;
            matrixStack.translate(r, s, t);
        }
        for (int u = 0; u < k; ++u) {
            matrixStack.push();
            if (u > 0) {
                if (bl) {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float v = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    matrixStack.translate(s, t, v);
                } else {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    matrixStack.translate(s, t, 0.0f);
                }
            }
            this.itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
            matrixStack.pop();
            if (bl) continue;
            matrixStack.translate(0.0f * o, 0.0f * p, 0.09375f * q);
        }
        matrixStack.pop();
        super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}

