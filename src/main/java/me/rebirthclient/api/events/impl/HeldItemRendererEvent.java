/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Hand
 */
package me.rebirthclient.api.events.impl;

import me.rebirthclient.api.events.Event;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class HeldItemRendererEvent
extends Event {
    private final Hand hand;
    private final ItemStack item;
    private float ep;
    private final MatrixStack stack;

    public HeldItemRendererEvent(Hand hand, ItemStack item, float equipProgress, MatrixStack stack) {
        super(Event.Stage.Pre);
        this.hand = hand;
        this.item = item;
        this.ep = equipProgress;
        this.stack = stack;
    }

    public Hand getHand() {
        return this.hand;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public float getEp() {
        return this.ep;
    }

    public MatrixStack getStack() {
        return this.stack;
    }
}

