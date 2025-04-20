/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import net.minecraft.client.gui.DrawContext;

public class Ambience
extends Module {
    public ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 0, 0, 30)));

    public Ambience() {
        super("Ambience", Module.Category.Render);
    }

    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        Render2DUtil.drawRect(drawContext.getMatrices(), 0.0f, 0.0f, (float)mc.getWindow().getWidth(), (float)mc.getWindow().getHeight(), this.color.getValue());
    }
}

