/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.api.events.eventbus;

public interface IListener {
    public void call(Object var1);

    public Class<?> getTarget();

    public int getPriority();

    public boolean isStatic();
}

