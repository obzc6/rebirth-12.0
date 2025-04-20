/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.mod.modules.Module;

public class GhostHand
extends Module {
    public static GhostHand INSTANCE;
    public boolean isActive;

    public GhostHand() {
        super("GhostHand", Module.Category.Player);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        this.isActive = false;
    }

    public boolean canWork() {
        return this.isOn() && !GhostHand.mc.options.useKey.isPressed() && !GhostHand.mc.options.sneakKey.isPressed();
    }
}

