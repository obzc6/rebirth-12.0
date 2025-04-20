/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.text.Text
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.mod.gui.screens.alts.AltScreen;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MultiplayerScreen.class})
public class MixinMultiplayerScreen
extends Screen {
    protected MixinMultiplayerScreen(Text title) {
        super(title);
    }

    @Inject(at={@At(value="TAIL")}, method={"init()V"})
    private void onInit(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.of("Alt Manager"), b -> client.setScreen(new AltScreen((MultiplayerScreen)(Object)this)))
                .dimensions(this.width / 2 + 4 + 50, 7, 100, 20).build());
    }
}

