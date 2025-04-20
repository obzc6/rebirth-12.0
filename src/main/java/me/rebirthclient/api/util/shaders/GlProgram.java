/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.GlUniform
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.render.VertexFormat
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Pair
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package me.rebirthclient.api.util.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import me.rebirthclient.asm.accessors.IShaderProgram;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class GlProgram {
    private static final List<Pair<Function<ResourceFactory, ShaderProgram>, Consumer<ShaderProgram>>> REGISTERED_PROGRAMS = new ArrayList<Pair<Function<ResourceFactory, ShaderProgram>, Consumer<ShaderProgram>>>();
    protected ShaderProgram backingProgram;




    public void use() {
        RenderSystem.setShader(() -> this.backingProgram);
    }

    protected void setup() {
    }

    @Nullable
    protected GlUniform findUniform(String name) {
        return ((IShaderProgram)this.backingProgram).getUniformsHook().get(name);
    }

    @ApiStatus.Internal
    public static void forEachProgram(Consumer<Pair<Function<ResourceFactory, ShaderProgram>, Consumer<ShaderProgram>>> loader) {
        REGISTERED_PROGRAMS.forEach(loader);
    }

    public static class OwoShaderProgram
    extends ShaderProgram {
        private OwoShaderProgram(ResourceFactory factory, String name, VertexFormat format) throws IOException {
            super(factory, name, format);
        }
    }
}

