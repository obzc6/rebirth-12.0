/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.mod.modules.Module;

public class NoSlowdown
extends Module {
    public static NoSlowdown INSTANCE;

    public NoSlowdown() {
        super("NoSlowdown", Module.Category.Movement);
        this.setDescription("Prevents the player from being slowed down by blocks.");
        INSTANCE = this;
    }
}

