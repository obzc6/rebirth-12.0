/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.ShaderStage
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ItemFrameEntity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package me.rebirthclient.asm.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.api.util.shaders.GlProgram;
import me.rebirthclient.mod.modules.client.FovMod;
import me.rebirthclient.mod.modules.player.GhostHand;
import me.rebirthclient.mod.modules.player.NoTrace;
import me.rebirthclient.mod.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={GameRenderer.class})
public class MixinGameRenderer {
    @Shadow
    @Final
    MinecraftClient client;
    @Shadow
    private float zoom;
    @Shadow
    private float zoomX;
    @Shadow
    private float zoomY;
    @Shadow
    private float viewDistance;

    @Inject(method={"showFloatingItem"}, at={@At(value="HEAD")}, cancellable=true)
    private void onShowFloatingItem(ItemStack floatingItem, CallbackInfo info) {
        if (floatingItem.getItem() == Items.TOTEM_OF_UNDYING && NoRender.INSTANCE.isOn() && NoRender.INSTANCE.totem.getValue()) {
            info.cancel();
        }
    }

    @Redirect(method={"renderWorld"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float applyCameraTransformationsMathHelperLerpProxy(float delta, float first, float second) {
        if (NoRender.INSTANCE.isOn() && NoRender.INSTANCE.nausea.getValue()) {
            return 0.0f;
        }
        return MathHelper.lerp((float)delta, (float)first, (float)second);
    }

    @Inject(method={"tiltViewWhenHurt"}, at={@At(value="HEAD")}, cancellable=true)
    private void tiltViewWhenHurtHook(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (NoRender.INSTANCE.isOn() && NoRender.INSTANCE.hurtCam.getValue()) {
            ci.cancel();
        }
    }

    @Inject(at={@At(value="FIELD", target="Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode=180, ordinal=0)}, method={"renderWorld"})
    void render3dHook(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        TextUtil.lastProjMat.set((Matrix4fc)RenderSystem.getProjectionMatrix());
        TextUtil.lastModMat.set((Matrix4fc)RenderSystem.getModelViewMatrix());
        TextUtil.lastWorldSpaceMatrix.set((Matrix4fc)matrix.peek().getPositionMatrix());
    }

    @Inject(method={"renderWorld"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", shift=At.Shift.AFTER)})
    public void postRender3dHook(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        Rebirth.SHADER.renderShaders();
    }

    @Inject(method={"updateTargetedEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;")}, cancellable=true)
    private void onUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        if (NoTrace.INSTANCE.canWork() && this.client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            this.client.getProfiler().pop();
            info.cancel();
        }
    }

    @Inject(method={"updateTargetedEntity"}, at={@At(value="HEAD")}, cancellable=true)
    private void updateTargetedEntityHook(float tickDelta, CallbackInfo ci) {
        ci.cancel();
        this.update(tickDelta);
    }

    @Inject(method={"getBasicProjectionMatrix"}, at={@At(value="TAIL")}, cancellable=true)
    public void getBasicProjectionMatrixHook(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (FovMod.INSTANCE.isOn() && FovMod.INSTANCE.aspectRatio.getValue()) {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.peek().getPositionMatrix().identity();
            if (this.zoom != 1.0f) {
                matrixStack.translate(this.zoomX, -this.zoomY, 0.0f);
                matrixStack.scale(this.zoom, this.zoom, 1.0f);
            }
            matrixStack.peek().getPositionMatrix().mul((Matrix4fc)new Matrix4f().setPerspective((float)(fov * 0.01745329238474369), FovMod.INSTANCE.aspectFactor.getValueFloat(), 0.05f, this.viewDistance * 4.0f));
            cir.setReturnValue(matrixStack.peek().getPositionMatrix());
        }
    }

    @Inject(method={"getFov(Lnet/minecraft/client/render/Camera;FZ)D"}, at={@At(value="TAIL")}, cancellable=true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cb) {
        if (FovMod.INSTANCE.customFov.getValue() && FovMod.INSTANCE.isOn()) {
            if (MinecraftClient.getInstance().player.isSubmergedInWater()) {
                return;
            }
            cb.setReturnValue(FovMod.INSTANCE.fov.getValue());
        }
    }

    @Inject(method={"loadPrograms"}, at={@At(value="INVOKE", target="Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal=0)}, locals=LocalCapture.CAPTURE_FAILHARD)
    void loadAllTheShaders(ResourceFactory factory, CallbackInfo ci, List<ShaderStage> stages, List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shadersToLoad) {
        GlProgram.forEachProgram(loader -> shadersToLoad.add(new Pair((Object)((ShaderProgram)((Function)loader.getLeft()).apply(factory)), (Object)((Consumer)loader.getRight()))));
    }

    @Unique
    public void update(float tickDelta) {
        Entity entity = this.client.getCameraEntity();
        if (entity != null && this.client.world != null) {
            Box box;
            this.client.getProfiler().push("pick");
            this.client.targetedEntity = null;
            double d = this.client.interactionManager.getReachDistance();
            GhostHand.INSTANCE.isActive = GhostHand.INSTANCE.canWork();
            this.client.crosshairTarget = entity.raycast(d, tickDelta, false);
            GhostHand.INSTANCE.isActive = false;
            Vec3d vec3d = entity.getCameraPosVec(tickDelta);
            boolean bl = false;
            double e = d;
            if (this.client.interactionManager.hasExtendedReach()) {
                d = e = 6.0;
            } else if (d > 3.0) {
                bl = true;
            }
            e *= e;
            if (this.client.crosshairTarget != null) {
                e = this.client.crosshairTarget.getPos().squaredDistanceTo(vec3d);
            }
            Vec3d vec3d2 = entity.getRotationVec(1.0f);
            Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
            EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)entity, (Vec3d)vec3d, (Vec3d)vec3d3, (Box)(box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0)), entityx -> !entityx.isSpectator() && entityx.canHit(), (double)e);
            if (entityHitResult != null) {
                Entity entity2 = entityHitResult.getEntity();
                Vec3d vec3d4 = entityHitResult.getPos();
                double g = vec3d.squaredDistanceTo(vec3d4);
                if (bl && g > 9.0) {
                    this.client.crosshairTarget = BlockHitResult.createMissed((Vec3d)vec3d4, (Direction)Direction.getFacing((double)vec3d2.x, (double)vec3d2.y, (double)vec3d2.z), (BlockPos)BlockPos.ofFloored((Position)vec3d4));
                } else if (g < e || this.client.crosshairTarget == null) {
                    this.client.crosshairTarget = entityHitResult;
                    if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
                        this.client.targetedEntity = entity2;
                    }
                }
            }
            this.client.getProfiler().pop();
        }
    }
}

