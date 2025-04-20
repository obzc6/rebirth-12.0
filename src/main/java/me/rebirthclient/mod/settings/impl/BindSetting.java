/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 */
package me.rebirthclient.mod.settings.impl;

import java.lang.reflect.Field;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.mod.settings.Setting;
import org.lwjgl.glfw.GLFW;

public class BindSetting
extends Setting {
    private boolean isListening = false;
    private int key;
    private boolean isDown = false;

    public BindSetting(String name, int key) {
        super(name, ModuleManager.lastLoadMod.getName() + "_" + name);
        this.key = key;
    }

    @Override
    public void loadSetting() {
        this.setKey(Rebirth.CONFIG.getSettingInt(this.getLine(), this.key));
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getBind() {
        Object kn;
        if (this.key == -1) {
            return "None";
        }
        Object object = kn = this.key > 0 ? GLFW.glfwGetKeyName((int)this.key, (int)GLFW.glfwGetKeyScancode((int)this.key)) : "None";
        if (kn == null) {
            try {
                for (Field declaredField : GLFW.class.getDeclaredFields()) {
                    int a;
                    if (!declaredField.getName().startsWith("GLFW_KEY_") || (a = ((Integer)declaredField.get(null)).intValue()) != this.key) continue;
                    String nb = declaredField.getName().substring("GLFW_KEY_".length());
                    kn = nb.substring(0, 1).toUpperCase() + nb.substring(1).toLowerCase();
                }
            }
            catch (Exception ignored) {
                kn = "None";
            }
        }
        return ((String)kn).toUpperCase();
    }

    public void setListening(boolean set) {
        this.isListening = set;
    }

    public boolean isListening() {
        return this.isListening;
    }

    public void setDown(boolean down) {
        this.isDown = down;
    }

    public boolean isPressed() {
        return this.isDown;
    }
}

