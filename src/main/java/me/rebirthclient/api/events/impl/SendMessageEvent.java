/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.api.events.impl;

import me.rebirthclient.api.events.Event;

public class SendMessageEvent
extends Event {
    public String message;
    public final String defaultMessage;

    public SendMessageEvent(String message) {
        super(Event.Stage.Pre);
        this.defaultMessage = message;
        this.message = message;
    }
}

