/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.settings.impl;

import java.util.function.Predicate;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.mod.settings.Setting;

public class BooleanSetting
extends Setting {
    public boolean parent = false;
    public boolean popped = false;
    private boolean value;

    public BooleanSetting(String name, boolean defaultValue) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name);
        this.value = defaultValue;
    }

    public BooleanSetting(String name, boolean defaultValue, Predicate visibilityIn) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name, visibilityIn);
        this.value = defaultValue;
    }

    public final boolean getValue() {
        return this.value;
    }

    public final void setValue(boolean value) {
        this.value = value;
    }

    public final void toggleValue() {
        this.value = !this.value;
    }

    public final boolean isOpen() {
        if (this.parent) {
            return this.popped;
        }
        return true;
    }

    @Override
    public void loadSetting() {
        this.value = Rebirth.CONFIG.getSettingBoolean(this.getLine(), this.value);
    }

    public BooleanSetting setParent() {
        this.parent = true;
        return this;
    }
}

