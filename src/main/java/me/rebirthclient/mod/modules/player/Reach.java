/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.SliderSetting;

public class Reach
extends Module {
    public static Reach INSTANCE;
    public final SliderSetting getDistance = this.add(new SliderSetting("Distance", 5.0, 1.0, 15.0, 1.0));

    public Reach() {
        super("Reach", Module.Category.Player);
        INSTANCE = this;
    }
}

