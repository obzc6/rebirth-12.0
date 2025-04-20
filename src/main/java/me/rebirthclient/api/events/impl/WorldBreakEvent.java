/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.BlockBreakingInfo
 *  net.minecraft.util.math.BlockPos
 */
package me.rebirthclient.api.events.impl;

import me.rebirthclient.api.events.Event;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;

public class WorldBreakEvent
extends Event {
    private final BlockBreakingInfo blockBreakingInfo;

    public WorldBreakEvent(BlockBreakingInfo pos) {
        super(Event.Stage.Pre);
        this.blockBreakingInfo = pos;
    }

    public BlockPos getPos() {
        return this.blockBreakingInfo.getPos();
    }

    public int getId() {
        return this.blockBreakingInfo.getActorId();
    }
}

