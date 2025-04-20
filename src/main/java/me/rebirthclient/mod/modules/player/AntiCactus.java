/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.mod.modules.Module;

public class AntiCactus
extends Module {
    public static AntiCactus INSTANCE;

    public AntiCactus() {
        super("AntiCactus", Module.Category.Player);
        this.setDescription("Prevents blocks from hurting you.");
        INSTANCE = this;
    }
}

