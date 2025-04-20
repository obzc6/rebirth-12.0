/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.api.events.eventbus;

import me.rebirthclient.api.events.eventbus.ICancellable;
import me.rebirthclient.api.events.eventbus.IListener;
import me.rebirthclient.api.events.eventbus.LambdaListener;

public interface IEventBus {
    public void registerLambdaFactory(String var1, LambdaListener.Factory var2);

    public <T> T post(T var1);

    public <T extends ICancellable> T post(T var1);

    public void subscribe(Object var1);

    public void subscribe(Class<?> var1);

    public void subscribe(IListener var1);

    public void unsubscribe(Object var1);

    public void unsubscribe(Class<?> var1);

    public void unsubscribe(IListener var1);
}

