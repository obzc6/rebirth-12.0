/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;

public class PingSpoof
extends Module {
    public static PingSpoof INSTANCE;
    public final BooleanSetting keepAlive = this.add(new BooleanSetting("KeepAlive", true));
    public final BooleanSetting pong = this.add(new BooleanSetting("Pong", true));
    public final SliderSetting ping = this.add(new SliderSetting("Ping", 300, 0, 1000));

    public PingSpoof() {
        super("PingSpoof", Module.Category.Player);
        INSTANCE = this;
    }

    @Override
    public String getInfo() {
        return String.valueOf(this.ping.getValueInt());
    }
}

