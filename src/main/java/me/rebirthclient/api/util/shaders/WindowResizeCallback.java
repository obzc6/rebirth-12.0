/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.event.Event
 *  net.fabricmc.fabric.api.event.EventFactory
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.Window
 */
package me.rebirthclient.api.util.shaders;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public interface WindowResizeCallback {
    public static final Event<WindowResizeCallback> EVENT = EventFactory.createArrayBacked(WindowResizeCallback.class, callbacks -> (client, window) -> {
        for (WindowResizeCallback callback : callbacks) {
            callback.onResized(client, window);
        }
    });

    public void onResized(MinecraftClient var1, Window var2);
}

