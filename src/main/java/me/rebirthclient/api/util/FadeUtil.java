/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.api.util;

public class FadeUtil {
    public static double animate(double current, double endPoint, double speed) {
        boolean shouldContinueAnimation = endPoint > current;
        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }
}

