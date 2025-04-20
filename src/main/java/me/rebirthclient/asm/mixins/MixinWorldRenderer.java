/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.PostEffectProcessor
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.LightmapTextureManager
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.ShaderManager;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.render.ShaderChams;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={WorldRenderer.class})
public abstract class MixinWorldRenderer {
    @Shadow
    protected abstract void renderEndSky(MatrixStack var1);

    @Inject(at={@At(value="RETURN")}, method={"render"})
    private void onRenderWorld(MatrixStack matrixStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        Rebirth.MODULE.render3D(matrixStack);
    }

    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V", ordinal=0))
    void replaceShaderHook(PostEffectProcessor instance, float tickDelta) {
        ShaderManager.Shader shaders = (ShaderManager.Shader)ShaderChams.INSTANCE.mode.getValue();
        if (ShaderChams.INSTANCE.isOn() && Wrapper.mc.world != null) {
            Rebirth.SHADER.setupShader(shaders, Rebirth.SHADER.getShaderOutline(shaders));
        } else {
            instance.render(tickDelta);
        }
    }

    @Inject(method={"renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderSkyHead(MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo info) {
        if (ShaderChams.INSTANCE.isOn() && ShaderChams.INSTANCE.sky.getValue()) {
            Rebirth.SHADER.applyShader(() -> this.renderEndSky(matrices), (ShaderManager.Shader)ShaderChams.INSTANCE.skyMode.getValue());
            info.cancel();
        }
    }
}

