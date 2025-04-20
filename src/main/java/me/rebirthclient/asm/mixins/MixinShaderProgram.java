/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gl.ShaderProgram
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.api.util.shaders.GlProgram;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value={ShaderProgram.class})
public class MixinShaderProgram {
    @ModifyArg(method={"<init>"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"), require=0)
    private String fixIdentifier(String id) {
        String[] splitName = id.split(":");
        if (splitName.length != 2 || !splitName[0].startsWith("shaders/core/")) {
            return id;
        }
        return splitName[0].replace("shaders/core/", "") + ":shaders/core/" + splitName[1];
    }
}

