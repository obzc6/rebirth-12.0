/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.settings.impl;

import java.util.function.Predicate;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.mod.settings.EnumConverter;
import me.rebirthclient.mod.settings.Setting;

public class EnumSetting
extends Setting {
    private Enum value;
    public boolean popped = false;

    public EnumSetting(String name, Enum defaultValue) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name);
        this.value = defaultValue;
    }

    public EnumSetting(String name, Enum defaultValue, Predicate visibilityIn) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name, visibilityIn);
        this.value = defaultValue;
    }

    public void increaseEnum() {
        this.value = EnumConverter.increaseEnum(this.value);
    }

    public final Enum getValue() {
        return this.value;
    }

    public void setEnumValue(String value) {
        for (Enum e : (Enum[])this.value.getClass().getEnumConstants()) {
            if (!e.name().equalsIgnoreCase(value)) continue;
            this.value = e;
        }
    }

    @Override
    public void loadSetting() {
        EnumConverter converter = new EnumConverter(this.value.getClass());
        String enumString = Rebirth.CONFIG.getSettingString(this.getLine());
        if (enumString == null) {
            return;
        }
        Enum value = converter.doBackward(enumString);
        if (value != null) {
            this.value = value;
        }
    }
}

