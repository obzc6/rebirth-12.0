/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.mod.modules.Module;

public class AutoWalk
extends Module {
    public static AutoWalk INSTANCE;

    public AutoWalk() {
        super("AutoWalk", Module.Category.Movement);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        AutoWalk.mc.options.forwardKey.setPressed(false);
    }

    @Override
    public void onUpdate() {
        AutoWalk.mc.options.forwardKey.setPressed(true);
    }
}

