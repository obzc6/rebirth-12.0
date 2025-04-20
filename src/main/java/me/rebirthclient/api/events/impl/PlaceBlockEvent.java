/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 */
package me.rebirthclient.api.events.impl;

import me.rebirthclient.api.events.Event;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class PlaceBlockEvent
extends Event {
    private static final PlaceBlockEvent INSTANCE = new PlaceBlockEvent();
    public BlockPos blockPos;
    public Block block;

    public PlaceBlockEvent() {
        super(Event.Stage.Pre);
    }

    public static PlaceBlockEvent get(BlockPos blockPos, Block block) {
        PlaceBlockEvent.INSTANCE.blockPos = blockPos;
        PlaceBlockEvent.INSTANCE.block = block;
        return INSTANCE;
    }
}

