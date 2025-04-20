/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package me.rebirthclient.mod.gui.components;

import java.awt.Color;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.modules.client.ClickGui;
import net.minecraft.client.gui.DrawContext;

public abstract class Component {
    public int defaultHeight;
    protected ClickGuiTab parent;
    private int height;
    public double currentOffset;

    public Component() {
        this.height = this.defaultHeight = 30;
        this.currentOffset = 0.0;
    }

    public boolean isVisible() {
        return true;
    }

    public int getHeight() {
        if (!this.isVisible()) {
            return 0;
        }
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ClickGuiTab getParent() {
        return this.parent;
    }

    public void setParent(ClickGuiTab parent) {
        this.parent = parent;
    }

    public abstract void update(int var1, double var2, double var4, boolean var6);

    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        this.currentOffset = offset;
        return false;
    }

    public static double animate(double current, double endPoint) {
        return Component.animate(current, endPoint, ClickGui.INSTANCE.animationSpeed.getValue());
    }

    public static double animate(double current, double endPoint, double speed) {
        boolean shouldContinueAnimation = endPoint > current;
        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        if (Math.abs(factor) <= 0.001) {
            return endPoint;
        }
        return current + (shouldContinueAnimation ? factor : -factor);
    }
}

