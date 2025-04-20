/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.settings.impl;

import java.util.function.Predicate;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.mod.settings.Setting;

public class SliderSetting
extends Setting {
    private double value;
    private final double minValue;
    private final double maxValue;
    private final double increment;

    public SliderSetting(String name, double value, double min, double max, double increment) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name);
        this.value = value;
        this.minValue = min;
        this.maxValue = max;
        this.increment = increment;
    }

    public SliderSetting(String name, double value, double min, double max) {
        this(name, value, min, max, 0.1);
    }

    public SliderSetting(String name, int value, int min, int max) {
        this(name, (double)value, (double)min, (double)max, 1.0);
    }

    public SliderSetting(String name, double value, double min, double max, double increment, Predicate visibilityIn) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name, visibilityIn);
        this.value = value;
        this.minValue = min;
        this.maxValue = max;
        this.increment = increment;
    }

    public SliderSetting(String name, double value, double min, double max, Predicate visibilityIn) {
        this(name, value, min, max, 0.1, visibilityIn);
    }

    public SliderSetting(String name, int value, int min, int max, Predicate visibilityIn) {
        this(name, value, min, max, 1.0, visibilityIn);
    }

    public final double getValue() {
        return this.value;
    }

    public final float getValueFloat() {
        return (float)this.value;
    }

    public final int getValueInt() {
        return (int)this.value;
    }

    public final void setValue(double value) {
        this.value = (double)Math.round(value / this.increment) * this.increment;
    }

    public final double getMinimum() {
        return this.minValue;
    }

    public final double getMaximum() {
        return this.maxValue;
    }

    public final double getIncrement() {
        return this.increment;
    }

    public final double getRange() {
        return this.maxValue - this.minValue;
    }

    @Override
    public void loadSetting() {
        this.setValue(Rebirth.CONFIG.getSettingFloat(this.getLine(), (float)this.value));
    }
}

