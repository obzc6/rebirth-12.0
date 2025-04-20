/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.Box
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import java.util.HashMap;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.BreakManager;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public class BreakESP
extends Module {
    public static BreakESP INSTANCE;
    public BooleanSetting noSelf = this.add(new BooleanSetting("noSelf", true));
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));
    public BooleanSetting outline = this.add(new BooleanSetting("Outline", false));
    public BooleanSetting box = this.add(new BooleanSetting("Box", true));
    public final SliderSetting animationTime = this.add(new SliderSetting("AnimationTime", 500, 0, 2000));

    public BreakESP() {
        super("BreakESP", Module.Category.Render);
        INSTANCE = this;
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (Rebirth.BREAK.breakMap.isEmpty()) {
            return;
        }
        for (BreakManager.BreakData breakData : new HashMap<Integer, BreakManager.BreakData>(Rebirth.BREAK.breakMap).values()) {
            if (breakData == null || breakData.getEntity() == null || this.noSelf.getValue() && breakData.getEntity() == BreakESP.mc.player) continue;
            double size = 0.5 * (1.0 - breakData.fade.easeOutQuad());
            Render3DUtil.draw3DBox(matrixStack, new Box(breakData.pos).shrink(size, size, size).shrink(-size, -size, -size), this.color.getValue(), this.outline.getValue(), this.box.getValue());
        }
    }

    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        for (BreakManager.BreakData breakData : new HashMap<Integer, BreakManager.BreakData>(Rebirth.BREAK.breakMap).values()) {
            if (breakData == null || breakData.getEntity() == null || this.noSelf.getValue() && breakData.getEntity() == BreakESP.mc.player) continue;
            TextUtil.drawText(drawContext, breakData.getEntity().getEntityName(), breakData.pos.toCenterPos().add(0.0, 0.1, 0.0));
            TextUtil.drawText(drawContext, BreakESP.mc.world.isAir(breakData.pos) ? "Broken" : "Breaking", breakData.pos.toCenterPos().add(0.0, -0.1, 0.0), new Color(0, 255, 51).getRGB());
        }
    }
}

