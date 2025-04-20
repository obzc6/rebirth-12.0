/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.BlockBreakingInfo
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.impl.WorldBreakEvent;
import net.minecraft.client.render.BlockBreakingInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockBreakingInfo.class})
public class MixinBlockBreakingInfo {
    @Inject(method={"compareTo"}, at={@At(value="HEAD")})
    public void onCompareTo(BlockBreakingInfo blockBreakingInfo, CallbackInfoReturnable<Integer> cir) {
        Rebirth.EVENT_BUS.post(new WorldBreakEvent(blockBreakingInfo));
    }
}

