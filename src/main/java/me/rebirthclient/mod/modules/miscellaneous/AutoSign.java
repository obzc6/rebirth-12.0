/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.miscellaneous;

import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.mod.modules.Module;

public class AutoSign
extends Module {
    public static AutoSign INSTANCE;
    String[] text;

    public AutoSign() {
        super("AutoSign", Module.Category.Miscellaneous);
        this.setDescription("Automatically places sign.");
        INSTANCE = this;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public String[] getText() {
        return this.text;
    }

    @Override
    public void onEnable() {
        CommandManager.sendChatMessage("i\u00a7e[~] \u00a7fPlace down a sign to set text!");
        this.text = null;
    }
}

