/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 */
package me.rebirthclient.mod.modules.miscellaneous;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.DeathEvent;
import me.rebirthclient.api.events.impl.TotemEvent;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.entity.player.PlayerEntity;

public class PopCounter
extends Module {
    public static PopCounter INSTANCE;
    public final BooleanSetting unPop = this.add(new BooleanSetting("UnPop", true));

    public PopCounter() {
        super("PopCounter", "Counts other players totem pops", Module.Category.Miscellaneous);
        INSTANCE = this;
    }

    @EventHandler
    public void onPlayerDeath(DeathEvent event) {
        PlayerEntity player = event.getPlayer();
        if (Rebirth.POP.popContainer.containsKey(player.getName().getString())) {
            int l_Count = Rebirth.POP.popContainer.get(player.getName().getString());
            if (l_Count == 1) {
                if (player.equals((Object)PopCounter.mc.player)) {
                    this.sendMessage("\u00a7fYou\u00a7r died after popping \u00a7f" + l_Count + "\u00a7r totem.", player.getId());
                } else {
                    this.sendMessage("\u00a7f" + player.getName().getString() + "\u00a7r died after popping \u00a7f" + l_Count + "\u00a7r totem.", player.getId());
                }
            } else if (player.equals((Object)PopCounter.mc.player)) {
                this.sendMessage("\u00a7fYou\u00a7r died after popping \u00a7f" + l_Count + "\u00a7r totems.", player.getId());
            } else {
                this.sendMessage("\u00a7f" + player.getName().getString() + "\u00a7r died after popping \u00a7f" + l_Count + "\u00a7r totems.", player.getId());
            }
        } else if (this.unPop.getValue()) {
            if (player.equals((Object)PopCounter.mc.player)) {
                this.sendMessage("\u00a7fYou\u00a7r died.", player.getId());
            } else {
                this.sendMessage("\u00a7f" + player.getName().getString() + "\u00a7r died.", player.getId());
            }
        }
    }

    @EventHandler
    public void onTotem(TotemEvent event) {
        PlayerEntity player = event.getPlayer();
        int l_Count = 1;
        if (Rebirth.POP.popContainer.containsKey(player.getName().getString())) {
            l_Count = Rebirth.POP.popContainer.get(player.getName().getString());
        }
        if (l_Count == 1) {
            if (player.equals((Object)PopCounter.mc.player)) {
                this.sendMessage("\u00a7fYou\u00a7r popped \u00a7f" + l_Count + "\u00a7r totem.", player.getId());
            } else {
                this.sendMessage("\u00a7f" + player.getName().getString() + " \u00a7rpopped \u00a7f" + l_Count + "\u00a7r totems.", player.getId());
            }
        } else if (player.equals((Object)PopCounter.mc.player)) {
            this.sendMessage("\u00a7fYou\u00a7r popped \u00a7f" + l_Count + "\u00a7r totem.", player.getId());
        } else {
            this.sendMessage("\u00a7f" + player.getName().getString() + " \u00a7rhas popped \u00a7f" + l_Count + "\u00a7r totems.", player.getId());
        }
    }

    public void sendMessage(String message, int id) {
        if (!PopCounter.nullCheck()) {
            CommandManager.sendChatMessageWidthId("\u00a76[!] " + message, id);
        }
    }
}

