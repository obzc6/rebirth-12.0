/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.ColorSetting;

public class Fullbright
extends Module {
    public static Fullbright INSTANCE;
    public ColorSetting color = this.add(new ColorSetting("Color", new Color(-1, true)));

    public Fullbright() {
        super("FullBright", Module.Category.Render);
        this.setDescription("Maxes out the brightness.");
        INSTANCE = this;
    }
}

