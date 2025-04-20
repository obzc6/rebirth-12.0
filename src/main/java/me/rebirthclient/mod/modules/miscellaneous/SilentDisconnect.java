/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.miscellaneous;

import me.rebirthclient.mod.modules.Module;

public class SilentDisconnect
extends Module {
    public static SilentDisconnect INSTANCE = new SilentDisconnect();

    public SilentDisconnect() {
        super("SilentDisconnect", Module.Category.Miscellaneous);
        INSTANCE = this;
    }
}

