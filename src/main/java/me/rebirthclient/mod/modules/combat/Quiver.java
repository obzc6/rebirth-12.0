/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.BowItem
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.item.BowItem;

public class Quiver
extends Module {
    public static Quiver INSTANCE;

    public Quiver() {
        super("Quiver", Module.Category.Combat);
        INSTANCE = this;
    }

    @EventHandler(priority=-101)
    public void onRotate(RotateEvent event) {
        if (Quiver.mc.player.isUsingItem() && Quiver.mc.player.getActiveItem().getItem() instanceof BowItem) {
            event.setPitch(-90.0f);
        }
    }
}

