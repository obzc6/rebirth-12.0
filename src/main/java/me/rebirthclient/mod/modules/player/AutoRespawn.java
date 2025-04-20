/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.DeathScreen
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.mod.modules.Module;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawn
extends Module {
    public AutoRespawn() {
        super("AutoRespawn", Module.Category.Player);
        this.setDescription("Automatically respawns when you die.");
    }

    @Override
    public void onUpdate() {
        if (AutoRespawn.mc.currentScreen instanceof DeathScreen) {
            AutoRespawn.mc.player.requestRespawn();
            mc.setScreen(null);
        }
    }
}

