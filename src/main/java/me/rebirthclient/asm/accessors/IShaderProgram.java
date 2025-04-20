/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.GlUniform
 *  net.minecraft.client.gl.ShaderProgram
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package me.rebirthclient.asm.accessors;

import java.util.Map;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ShaderProgram.class})
public interface IShaderProgram {
    @Accessor(value="loadedUniforms")
    public Map<String, GlUniform> getUniformsHook();
}

