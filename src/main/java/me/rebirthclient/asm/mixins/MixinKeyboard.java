/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Keyboard
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Keyboard.class})
public class MixinKeyboard
implements Wrapper {
    @Inject(method={"onKey"}, at={@At(value="HEAD")}, cancellable=true)
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (MixinKeyboard.mc.currentScreen instanceof ClickGuiScreen && action == 1 && Rebirth.MODULE.setBind(key)) {
            return;
        }
        if (action == 1) {
            Rebirth.MODULE.onKeyPressed(key);
        }
        if (action == 0) {
            Rebirth.MODULE.onKeyReleased(key);
        }
    }
}

