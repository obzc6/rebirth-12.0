/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.SplashTextRenderer
 *  net.minecraft.client.resource.SplashTextResourceSupplier
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.rebirthclient.asm.mixins;

import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SplashTextResourceSupplier.class})
public class MixinSplashTextResourceSupplier {
    private boolean override = true;
    private final Random random = new Random();
    private final List<String> rebirthSplashes = MixinSplashTextResourceSupplier.getRebirthSplashes();

    @Inject(method={"get"}, at={@At(value="HEAD")}, cancellable=true)
    private void onApply(CallbackInfoReturnable<SplashTextRenderer> cir) {
        if (this.override) {
            cir.setReturnValue(new SplashTextRenderer(this.rebirthSplashes.get(this.random.nextInt(this.rebirthSplashes.size()))));
        }
        this.override = !this.override;
    }

    private static List<String> getRebirthSplashes() {
        return List.of("\u7cd6\u918b\u91d1\u5149\u4eae", "\u8ff7\u4f60\u4e16\u754c, \u542f\u52a8!");
    }
}

