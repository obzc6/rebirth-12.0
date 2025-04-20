/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 */
package me.rebirthclient.api.events.impl;

import me.rebirthclient.api.events.Event;
import net.minecraft.entity.player.PlayerEntity;

public class TotemEvent
extends Event {
    private final PlayerEntity player;

    public TotemEvent(PlayerEntity player) {
        super(Event.Stage.Post);
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}

