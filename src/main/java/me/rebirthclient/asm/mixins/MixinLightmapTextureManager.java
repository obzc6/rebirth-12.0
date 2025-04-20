/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.LightmapTextureManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.mod.modules.render.Fullbright;
import me.rebirthclient.mod.modules.render.NoRender;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value={LightmapTextureManager.class})
public class MixinLightmapTextureManager {
    @ModifyArgs(method={"update"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void update(Args args) {
        if (Fullbright.INSTANCE.isOn()) {
            args.set(2, (Object)Fullbright.INSTANCE.color.getValue().getRGB());
        }
    }

    @Inject(method={"getDarknessFactor(F)F"}, at={@At(value="HEAD")}, cancellable=true)
    private void getDarknessFactor(float tickDelta, CallbackInfoReturnable<Float> info) {
        if (NoRender.INSTANCE.isOn() && NoRender.INSTANCE.darkness.getValue()) {
            info.setReturnValue(Float.valueOf(0.0f));
        }
    }
}

