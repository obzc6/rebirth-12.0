/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  org.joml.Vector3f
 */
package me.rebirthclient.mod.modules.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.TotemEvent;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class PopChams
extends Module {
    public ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 200)));
    public SliderSetting ySpeed = this.add(new SliderSetting("Y Speed", 0.0, -10.0, 10.0, 1.0));
    public SliderSetting aSpeed = this.add(new SliderSetting("Alpha Speed", 5.0, 1.0, 100.0, 1.0));
    private final CopyOnWriteArrayList<Person> popList = new CopyOnWriteArrayList();

    public PopChams() {
        super("PopChams", Module.Category.Render);
    }

    @Override
    public void onUpdate() {
        this.popList.forEach(person -> person.update(this.popList));
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        this.popList.forEach(person -> {
            person.modelPlayer.leftPants.visible = false;
            person.modelPlayer.rightPants.visible = false;
            person.modelPlayer.leftSleeve.visible = false;
            person.modelPlayer.rightSleeve.visible = false;
            person.modelPlayer.jacket.visible = false;
            person.modelPlayer.hat.visible = false;
            this.renderEntity(matrixStack, (LivingEntity)person.player, (BipedEntityModel<PlayerEntity>)person.modelPlayer, person.getAlpha());
        });
        RenderSystem.disableBlend();
        RenderSystem.depthMask((boolean)true);
    }

    @EventHandler
    public void onTotem(TotemEvent event) {
        PlayerEntity e = event.getPlayer();
        if (e == PopChams.mc.player || e.distanceTo((Entity)PopChams.mc.player) > 20.0f) {
            return;
        }
        PlayerEntity entity = new PlayerEntity((World)PopChams.mc.world, BlockPos.ORIGIN, e.bodyYaw, new GameProfile(e.getUuid(), e.getName().getString())){

            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return false;
            }
        };
        entity.copyPositionAndRotation((Entity)e);
        entity.bodyYaw = e.bodyYaw;
        entity.headYaw = e.headYaw;
        entity.handSwingProgress = e.handSwingProgress;
        entity.handSwingTicks = e.handSwingTicks;
        entity.setSneaking(e.isSneaking());
        entity.limbAnimator.setSpeed(e.limbAnimator.getSpeed());
        this.popList.add(new Person(entity));
    }

    public void renderEntity(MatrixStack matrices, LivingEntity entity, BipedEntityModel<PlayerEntity> modelBase, int alpha) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        Vec3d gpos = entity.getPos().add(0.0, this.ySpeed.getValue() / 50.0, 0.0);
        entity.setPos(gpos.x, gpos.y, gpos.z);
        matrices.push();
        matrices.translate((float)x, (float)y, (float)z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(this.rad(180.0f - entity.bodyYaw)));
        PopChams.prepareScale(matrices);
        modelBase.animateModel((PlayerEntity) entity, entity.limbAnimator.getPos(), entity.limbAnimator.getSpeed(), mc.getTickDelta());
        modelBase.setAngles((PlayerEntity) entity, entity.limbAnimator.getPos(), entity.limbAnimator.getSpeed(), (float)entity.age, entity.headYaw - entity.bodyYaw, entity.getPitch());
        RenderSystem.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.blendFuncSeparate((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SrcFactor)GlStateManager.SrcFactor.ONE, (GlStateManager.DstFactor)GlStateManager.DstFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getParticleProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        modelBase.render(matrices, (VertexConsumer)buffer, 10, 0, (float)this.color.getValue().getRed() / 255.0f, (float)this.color.getValue().getGreen() / 255.0f, (float)this.color.getValue().getBlue() / 255.0f, (float)alpha / 255.0f);
        tessellator.draw();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private static void prepareScale(MatrixStack matrixStack) {
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.scale(1.6f, 1.8f, 1.6f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
    }

    public int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public float rad(float angle) {
        return (float)((double)angle * Math.PI / 180.0);
    }

    public class Person {
        private final PlayerEntity player;
        private final PlayerEntityModel<PlayerEntity> modelPlayer;
        private int alpha;

        public Person(PlayerEntity player) {
            this.player = player;
            this.modelPlayer = new PlayerEntityModel(new EntityRendererFactory.Context(Wrapper.mc.getEntityRenderDispatcher(), Wrapper.mc.getItemRenderer(), Wrapper.mc.getBlockRenderManager(), Wrapper.mc.getEntityRenderDispatcher().getHeldItemRenderer(), Wrapper.mc.getResourceManager(), Wrapper.mc.getEntityModelLoader(), Wrapper.mc.textRenderer).getPart(EntityModelLayers.PLAYER), false);
            this.modelPlayer.getHead().scale(new Vector3f(-0.3f, -0.3f, -0.3f));
            this.alpha = PopChams.this.color.getValue().getAlpha();
        }

        public void update(CopyOnWriteArrayList<Person> arrayList) {
            if (this.alpha <= 0) {
                arrayList.remove(this);
                this.player.kill();
                this.player.remove(Entity.RemovalReason.KILLED);
                this.player.onRemoved();
                return;
            }
            this.alpha = (int)((double)this.alpha - PopChams.this.aSpeed.getValue());
        }

        public int getAlpha() {
            return PopChams.this.clamp(this.alpha, 0, 255);
        }
    }
}

