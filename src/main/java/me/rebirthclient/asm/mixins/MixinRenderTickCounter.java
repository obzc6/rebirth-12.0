/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderTickCounter
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={RenderTickCounter.class})
public class MixinRenderTickCounter {
    @Shadow
    public float lastFrameDuration;

    @Inject(at={@At(value="FIELD", target="Lnet/minecraft/client/render/RenderTickCounter;prevTimeMillis:J", opcode=181, ordinal=0)}, method={"beginRenderTick(J)I"})
    public void onBeginRenderTick(long long_1, CallbackInfoReturnable<Integer> cir) {
        this.lastFrameDuration *= Rebirth.TIMER.timer;
    }
}

