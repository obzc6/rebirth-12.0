/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 */
package me.rebirthclient.api.events.impl;

import me.rebirthclient.api.events.Event;
import net.minecraft.entity.player.PlayerEntity;

public class TravelEvent
extends Event {
    private PlayerEntity entity;

    public TravelEvent(Event.Stage stage, PlayerEntity entity) {
        super(stage);
        this.entity = entity;
    }

    public PlayerEntity getEntity() {
        return this.entity;
    }
}

