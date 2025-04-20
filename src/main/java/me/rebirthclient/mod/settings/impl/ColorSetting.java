/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.settings.impl;

import java.awt.Color;
import java.util.function.Predicate;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.settings.Setting;

public class ColorSetting
extends Setting {
    public boolean isRainbow = false;
    private Color value;
    public final Timer timer = new Timer().reset();
    public boolean injectBoolean = false;
    public boolean booleanValue = false;
    public float effectSpeed = 4.0f;

    public ColorSetting(String name, Color defaultValue) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name);
        this.value = defaultValue;
    }

    public ColorSetting(String name, Color defaultValue, Predicate visibilityIn) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name, visibilityIn);
        this.value = defaultValue;
    }

    public ColorSetting(String name, int defaultValue) {
        this(name, new Color(defaultValue));
    }

    public ColorSetting(String name, int defaultValue, Predicate visibilityIn) {
        this(name, new Color(defaultValue), visibilityIn);
    }

    public final Color getValue() {
        if (this.isRainbow) {
            float[] HSB = Color.RGBtoHSB(this.value.getRed(), this.value.getGreen(), this.value.getBlue(), null);
            Color preColor = Color.getHSBColor((float)this.timer.getPassedTimeMs() * 0.36f * this.effectSpeed / 20.0f % 361.0f / 360.0f, HSB[1], HSB[2]);
            this.setValue(new Color(preColor.getRed(), preColor.getGreen(), preColor.getBlue(), this.value.getAlpha()));
        }
        return this.value;
    }

    public final void setValue(Color value) {
        this.value = value;
    }

    public final void setValue(int value) {
        this.value = new Color(value, true);
    }

    public final void setRainbow(boolean rainbow) {
        this.isRainbow = rainbow;
    }

    public ColorSetting injectBoolean(boolean value) {
        this.injectBoolean = true;
        this.booleanValue = value;
        return this;
    }

    @Override
    public void loadSetting() {
        this.value = new Color(Rebirth.CONFIG.getSettingInt(this.getLine(), this.value.getRGB()), true);
        this.isRainbow = Rebirth.CONFIG.getSettingBoolean(this.getLine() + "Rainbow");
        if (this.injectBoolean) {
            this.booleanValue = Rebirth.CONFIG.getSettingBoolean(this.getLine() + "Boolean", this.booleanValue);
        }
    }
}

