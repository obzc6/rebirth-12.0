/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package me.rebirthclient.api.events.impl;

import me.rebirthclient.api.events.Event;
import net.minecraft.util.math.BlockPos;

public class ClickBlockEvent
extends Event {
    private final BlockPos pos;

    public ClickBlockEvent(BlockPos pos) {
        super(Event.Stage.Pre);
        this.pos = pos;
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }
}

