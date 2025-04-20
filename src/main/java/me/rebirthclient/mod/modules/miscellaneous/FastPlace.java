/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.miscellaneous;

import me.rebirthclient.mod.modules.Module;

public class FastPlace
extends Module {
    public FastPlace() {
        super("FastPlace", Module.Category.Miscellaneous);
        this.setDescription("Places blocks exceptionally fast");
    }

    @Override
    public void onUpdate() {
        FastPlace.mc.itemUseCooldown = 0;
    }
}

