/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen
 *  net.minecraft.text.Text
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.mod.modules.miscellaneous.AutoSign;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AbstractSignEditScreen.class})
public abstract class MixinAbstractSignEditScreen
extends Screen {
    @Shadow
    @Final
    private String[] messages;

    protected MixinAbstractSignEditScreen(Text title) {
        super(title);
    }

    @Inject(at={@At(value="HEAD")}, method={"init()V"})
    private void onInit(CallbackInfo ci) {
        AutoSign mod = AutoSign.INSTANCE;
        String[] newText = mod.getText();
        if (newText != null) {
            for (int i = 0; i < 4; ++i) {
                this.messages[i] = newText[i];
            }
            this.finishEditing();
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"finishEditing()V"})
    private void onEditorClose(CallbackInfo ci) {
        AutoSign mod = AutoSign.INSTANCE;
        if (mod.isOn() && mod.getText() == null) {
            mod.setText(this.messages);
            CommandManager.sendChatMessage("Sign text set!");
        }
    }

    @Shadow
    private void finishEditing() {
    }
}

