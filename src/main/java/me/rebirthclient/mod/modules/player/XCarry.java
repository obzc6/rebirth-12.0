/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.GameMenuScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry
extends Module {
    Screen lastScreen = null;

    public XCarry() {
        super("XCarry", Module.Category.Player);
    }

    @Override
    public void onUpdate() {
        if (!(XCarry.mc.currentScreen == null || XCarry.mc.currentScreen instanceof GameMenuScreen || XCarry.mc.currentScreen instanceof ChatScreen || XCarry.mc.currentScreen instanceof ClickGuiScreen)) {
            this.lastScreen = XCarry.mc.currentScreen;
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (this.lastScreen instanceof InventoryScreen && event.getPacket() instanceof CloseHandledScreenC2SPacket) {
            event.cancel();
        }
    }
}

