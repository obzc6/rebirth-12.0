/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.api.managers;

public class TimerManager {
    public float timer = 1.0f;

    public void set(float factor) {
        if (factor < 0.1f) {
            factor = 0.1f;
        }
        this.timer = factor;
    }

    public void reset() {
        this.timer = 1.0f;
    }

    public float get() {
        return this.timer;
    }
}

