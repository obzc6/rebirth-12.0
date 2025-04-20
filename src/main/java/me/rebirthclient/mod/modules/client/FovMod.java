/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.client;

import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;

public class FovMod
extends Module {
    public static FovMod INSTANCE;
    private final EnumSetting page = this.add(new EnumSetting("Settings", Page.FOV));
    public final BooleanSetting customFov = this.add(new BooleanSetting("CustomFov", false, v -> this.page.getValue() == Page.FOV).setParent());
    public final SliderSetting fov = this.add(new SliderSetting("FOV", 120.0, 10.0, 180.0, v -> this.page.getValue() == Page.FOV && this.customFov.isOpen()));
    public final BooleanSetting aspectRatio = this.add(new BooleanSetting("AspectRatio", false, v -> this.page.getValue() == Page.Ratio).setParent());
    public final SliderSetting aspectFactor = this.add(new SliderSetting("AspectFactor", (double)1.8f, (double)0.1f, 3.0, v -> this.page.getValue() == Page.Ratio && this.aspectRatio.isOpen()));

    public FovMod() {
        super("FovMod", Module.Category.Client);
        INSTANCE = this;
    }

    public static enum Page {
        FOV,
        Ratio;

        // $FF: synthetic method
        private static FovMod.Page[] $values() {
            return new FovMod.Page[]{FOV, Ratio};
        }
    }
}
