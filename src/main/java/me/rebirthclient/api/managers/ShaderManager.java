/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  ladysnake.satin.api.managed.ManagedShaderEffect
 *  ladysnake.satin.api.managed.ShaderEffectManager
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.PostEffectProcessor
 *  net.minecraft.util.Identifier
 *  org.jetbrains.annotations.NotNull
 */
package me.rebirthclient.api.managers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import me.rebirthclient.api.interfaces.IShaderEffect;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.render.ShaderChams;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ShaderManager
implements Wrapper {
    private static final List<RenderTask> tasks = new ArrayList<RenderTask>();
    private MyFramebuffer shaderBuffer;
    public float time = 0.0f;
    public static ManagedShaderEffect DEFAULT_OUTLINE;
    public static ManagedShaderEffect SMOKE_OUTLINE;
    public static ManagedShaderEffect GRADIENT_OUTLINE;
    public static ManagedShaderEffect SNOW_OUTLINE;
    public static ManagedShaderEffect DEFAULT;
    public static ManagedShaderEffect SMOKE;
    public static ManagedShaderEffect GRADIENT;
    public static ManagedShaderEffect SNOW;

    public void renderShader(Runnable runnable, Shader mode) {
        tasks.add(new RenderTask(runnable, mode));
    }

    public void renderShaders() {
        tasks.forEach(t -> this.applyShader(t.task(), t.shader()));
        tasks.clear();
    }

    public void applyShader(Runnable runnable, Shader mode) {
        if (this.fullNullCheck()) {
            return;
        }
        Framebuffer MCBuffer = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.assertOnRenderThreadOrInit();
        if (this.shaderBuffer.textureWidth != MCBuffer.textureWidth || this.shaderBuffer.textureHeight != MCBuffer.textureHeight) {
            this.shaderBuffer.resize(MCBuffer.textureWidth, MCBuffer.textureHeight, false);
        }
        GlStateManager._glBindFramebuffer((int)36009, (int)this.shaderBuffer.fbo);
        this.shaderBuffer.beginWrite(true);
        runnable.run();
        this.shaderBuffer.endWrite();
        GlStateManager._glBindFramebuffer((int)36009, (int)MCBuffer.fbo);
        MCBuffer.beginWrite(false);
        ManagedShaderEffect shader = this.getShader(mode);
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        PostEffectProcessor effect = shader.getShaderEffect();
        if (effect != null) {
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufIn", this.shaderBuffer);
        }
        Framebuffer outBuffer = shader.getShaderEffect().getSecondaryTarget("bufOut");
        this.setupShader(mode, shader);
        this.shaderBuffer.clear(false);
        mainBuffer.beginWrite(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SrcFactor)GlStateManager.SrcFactor.ZERO, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.backupProjectionMatrix();
        outBuffer.draw(outBuffer.textureWidth, outBuffer.textureHeight, false);
        RenderSystem.restoreProjectionMatrix();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    public ManagedShaderEffect getShader(@NotNull Shader mode) {
        return switch (mode) {
            case Gradient -> GRADIENT;
            case Smoke -> SMOKE;
            case Snow -> SNOW;
            default -> DEFAULT;
        };
    }

    public ManagedShaderEffect getShaderOutline(@NotNull Shader mode) {
        return switch (mode) {
            case Gradient -> GRADIENT_OUTLINE;
            case Smoke -> SMOKE_OUTLINE;
            case Snow -> SNOW_OUTLINE;
            default -> DEFAULT_OUTLINE;
        };
    }

    private void setup(Shader shader, ManagedShaderEffect effect, boolean glow, ColorSetting outlineColor, SliderSetting fillAlpha, SliderSetting alpha2, SliderSetting lineWidth, SliderSetting octaves, int quality, SliderSetting factor, SliderSetting gradient, SliderSetting speed, ColorSetting smokeGlow, ColorSetting smokeGlow1, ColorSetting fill, ColorSetting fillColor2, ColorSetting fillColor3) {
        if (shader == Shader.Gradient) {
            effect.setUniformValue("alpha0", !glow ? -1.0f : (float)outlineColor.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("alpha1", (float)fillAlpha.getValueInt() / 255.0f);
            effect.setUniformValue("alpha2", (float)alpha2.getValueInt() / 255.0f);
            effect.setUniformValue("lineWidth", lineWidth.getValueInt());
            effect.setUniformValue("oct", octaves.getValueInt());
            effect.setUniformValue("quality", quality);
            effect.setUniformValue("factor", factor.getValueFloat());
            effect.setUniformValue("moreGradient", gradient.getValueFloat());
            effect.setUniformValue("resolution", (float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight());
            effect.setUniformValue("time", this.time);
            effect.render(mc.getTickDelta());
            this.time = (float)((double)this.time + (double)speed.getValueFloat() * 0.002);
        } else if (shader == Shader.Smoke) {
            effect.setUniformValue("alpha0", !glow ? -1.0f : (float)outlineColor.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("alpha1", (float)fillAlpha.getValueInt() / 255.0f);
            effect.setUniformValue("lineWidth", lineWidth.getValueInt());
            effect.setUniformValue("quality", quality);
            effect.setUniformValue("first", (float)outlineColor.getValue().getRed() / 255.0f, (float)outlineColor.getValue().getGreen() / 255.0f, (float)outlineColor.getValue().getBlue() / 255.0f, (float)outlineColor.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("second", (float)smokeGlow.getValue().getRed() / 255.0f, (float)smokeGlow.getValue().getGreen() / 255.0f, (float)smokeGlow.getValue().getBlue() / 255.0f);
            effect.setUniformValue("third", (float)smokeGlow1.getValue().getRed() / 255.0f, (float)smokeGlow1.getValue().getGreen() / 255.0f, (float)smokeGlow1.getValue().getBlue() / 255.0f);
            effect.setUniformValue("ffirst", (float)fill.getValue().getRed() / 255.0f, (float)fill.getValue().getGreen() / 255.0f, (float)fill.getValue().getBlue() / 255.0f, (float)fill.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("fsecond", (float)fillColor2.getValue().getRed() / 255.0f, (float)fillColor2.getValue().getGreen() / 255.0f, (float)fillColor2.getValue().getBlue() / 255.0f);
            effect.setUniformValue("fthird", (float)fillColor3.getValue().getRed() / 255.0f, (float)fillColor3.getValue().getGreen() / 255.0f, (float)fillColor3.getValue().getBlue() / 255.0f);
            effect.setUniformValue("oct", octaves.getValueInt());
            effect.setUniformValue("resolution", (float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight());
            effect.setUniformValue("time", this.time);
            effect.render(mc.getTickDelta());
            this.time = (float)((double)this.time + (double)speed.getValueFloat() * 0.002);
        } else if (shader == Shader.Default) {
            effect.setUniformValue("alpha0", !glow ? -1.0f : (float)outlineColor.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("lineWidth", lineWidth.getValueInt());
            effect.setUniformValue("quality", quality);
            effect.setUniformValue("color", (float)fill.getValue().getRed() / 255.0f, (float)fill.getValue().getGreen() / 255.0f, (float)fill.getValue().getBlue() / 255.0f, (float)fill.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("outlinecolor", (float)outlineColor.getValue().getRed() / 255.0f, (float)outlineColor.getValue().getGreen() / 255.0f, (float)outlineColor.getValue().getBlue() / 255.0f, (float)outlineColor.getValue().getAlpha() / 255.0f);
            effect.render(mc.getTickDelta());
        } else if (shader == Shader.Snow) {
            effect.setUniformValue("color", (float)fill.getValue().getRed() / 255.0f, (float)fill.getValue().getGreen() / 255.0f, (float)fill.getValue().getBlue() / 255.0f, (float)fill.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("quality", quality);
            effect.setUniformValue("resolution", (float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight());
            effect.setUniformValue("time", this.time);
            effect.render(mc.getTickDelta());
            this.time = (float)((double)this.time + (double)speed.getValueFloat() * 0.002);
        }
    }

    public void setupShader(Shader shader, ManagedShaderEffect effect) {
        ShaderChams shaderChams = ShaderChams.INSTANCE;
        this.setup(shader, effect, shaderChams.glow.getValue(), shaderChams.outlineColor, shaderChams.fillAlpha, shaderChams.alpha2, shaderChams.lineWidth, shaderChams.octaves, shaderChams.quality.getValueInt(), shaderChams.factor, shaderChams.gradient, shaderChams.speed, shaderChams.smokeGlow, shaderChams.smokeGlow1, shaderChams.fill, shaderChams.fillColor2, shaderChams.fillColor3);
    }

    public void reloadShaders() {
        DEFAULT = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/outline.json"));
        SMOKE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/smoke.json"));
        GRADIENT = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/gradient.json"));
        SNOW = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/snow.json"));
        DEFAULT_OUTLINE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/outline.json"), managedShaderEffect -> {
            PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
            if (effect == null) {
                return;
            }
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufIn", ShaderManager.mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufOut", ShaderManager.mc.worldRenderer.getEntityOutlinesFramebuffer());
        });
        SMOKE_OUTLINE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/smoke.json"), managedShaderEffect -> {
            PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
            if (effect == null) {
                return;
            }
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufIn", ShaderManager.mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufOut", ShaderManager.mc.worldRenderer.getEntityOutlinesFramebuffer());
        });
        GRADIENT_OUTLINE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/gradient.json"), managedShaderEffect -> {
            PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
            if (effect == null) {
                return;
            }
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufIn", ShaderManager.mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufOut", ShaderManager.mc.worldRenderer.getEntityOutlinesFramebuffer());
        });
        SNOW_OUTLINE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/snow.json"), managedShaderEffect -> {
            PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
            if (effect == null) {
                return;
            }
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufIn", ShaderManager.mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect)effect).rebirth_nextgen_master$addFakeTargetHook("bufOut", ShaderManager.mc.worldRenderer.getEntityOutlinesFramebuffer());
        });
    }

    public boolean fullNullCheck() {
        if (GRADIENT == null || SMOKE == null || DEFAULT == null || this.shaderBuffer == null) {
            if (mc.getFramebuffer() == null) {
                return true;
            }
            this.shaderBuffer = new MyFramebuffer(ShaderManager.mc.getFramebuffer().textureWidth, ShaderManager.mc.getFramebuffer().textureHeight);
            this.reloadShaders();
            return true;
        }
        return false;
    }

    public record RenderTask(Runnable task, Shader shader) {
    }

    public static enum Shader {
        Default,
        Smoke,
        Gradient,
        Snow;

        // $FF: synthetic method
        private static ShaderManager.Shader[] $values() {
            return new ShaderManager.Shader[]{Default, Smoke, Gradient, Snow};
        }
    }


    public static class MyFramebuffer
    extends Framebuffer {
        public MyFramebuffer(int width, int height) {
            super(false);
            RenderSystem.assertOnRenderThreadOrInit();
            this.resize(width, height, true);
            this.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
}

