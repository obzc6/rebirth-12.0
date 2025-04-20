/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.PostEffectPass
 *  net.minecraft.client.gl.PostEffectProcessor
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.rebirthclient.api.interfaces.IShaderEffect;
import me.rebirthclient.asm.accessors.IPostProcessShader;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PostEffectProcessor.class})
public class MixinShaderEffect
implements IShaderEffect {
    @Unique
    private final List<String> fakedBufferNames = new ArrayList<String>();
    @Shadow
    @Final
    private Map<String, Framebuffer> targetsByName;
    @Shadow
    @Final
    private List<PostEffectPass> passes;

    @Override
    public void rebirth_nextgen_master$addFakeTargetHook(String name, Framebuffer buffer) {
        Framebuffer previousFramebuffer = this.targetsByName.get(name);
        if (previousFramebuffer == buffer) {
            return;
        }
        if (previousFramebuffer != null) {
            for (PostEffectPass pass : this.passes) {
                if (pass.input == previousFramebuffer) {
                    ((IPostProcessShader)pass).setInput(buffer);
                }
                if (pass.output != previousFramebuffer) continue;
                ((IPostProcessShader)pass).setOutput(buffer);
            }
            this.targetsByName.remove(name);
            this.fakedBufferNames.remove(name);
        }
        this.targetsByName.put(name, buffer);
        this.fakedBufferNames.add(name);
    }

    @Inject(method={"close"}, at={@At(value="HEAD")})
    void deleteFakeBuffersHook(CallbackInfo ci) {
        for (String fakedBufferName : this.fakedBufferNames) {
            this.targetsByName.remove(fakedBufferName);
        }
    }
}

