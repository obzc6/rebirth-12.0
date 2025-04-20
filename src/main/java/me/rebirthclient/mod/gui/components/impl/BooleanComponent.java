/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 */
package me.rebirthclient.mod.gui.components.impl;

import java.awt.Color;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.gui.components.Component;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.modules.client.ClickGui;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class BooleanComponent
extends Component {
    BooleanSetting setting;
    boolean hover = false;
    public double currentWidth = 0.0;

    public BooleanComponent(ClickGuiTab parent, BooleanSetting setting) {
        this.parent = parent;
        this.setting = setting;
    }

    @Override
    public boolean isVisible() {
        if (this.setting.visibility != null) {
            return this.setting.visibility.test(null);
        }
        return true;
    }

    @Override
    public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
        int parentX = this.parent.getX();
        int parentY = this.parent.getY();
        int parentWidth = this.parent.getWidth();
        if (mouseX >= (double)(parentX + 2) && mouseX <= (double)(parentX + parentWidth - 2) && mouseY >= (double)(parentY + offset) && mouseY <= (double)(parentY + offset + 26)) {
            this.hover = true;
            if (HudManager.currentGrabbed == null && this.isVisible()) {
                if (mouseClicked) {
                    ClickGuiScreen.clicked = false;
                    this.setting.toggleValue();
                }
                if (ClickGuiScreen.rightClicked) {
                    ClickGuiScreen.rightClicked = false;
                    this.setting.popped = !this.setting.popped;
                }
            }
        } else {
            this.hover = false;
        }
    }

    @Override
    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        this.currentOffset = BooleanComponent.animate(this.currentOffset, offset);
        if (back && Math.abs(this.currentOffset - (double)offset) <= 0.5) {
            this.currentWidth = 0.0;
            return false;
        }
        int x = this.parent.getX();
        int y = (int)((double)this.parent.getY() + this.currentOffset - 2.0);
        int width = this.parent.getWidth();
        MatrixStack matrixStack = drawContext.getMatrices();
        Render2DUtil.drawRect(matrixStack, (float)x + 3.0f, (float)y + 1.0f, (float)width - 6.0f, (float)this.defaultHeight - 2.0f, this.hover ? ClickGui.INSTANCE.shColor.getValue() : ClickGui.INSTANCE.sbgColor.getValue());
        this.currentWidth = BooleanComponent.animate(this.currentWidth, this.setting.getValue() ? (double)width - 6.0 : 0.0, ClickGui.INSTANCE.booleanSpeed.getValue());
        Render2DUtil.drawRect(matrixStack, (float)x + 3.0f, (float)y + 1.0f, (float)this.currentWidth, (float)this.defaultHeight - 2.0f, this.hover ? color.brighter() : color);
        TextUtil.drawCustomText(drawContext, this.setting.getName(), (double)(x + 10), (double)(y + 8), new Color(-1).getRGB());
        if (this.setting.parent) {
            TextUtil.drawCustomText(drawContext, this.setting.popped ? "-" : "+", (double)(x + width - 22), (double)(y + 8), new Color(255, 255, 255).getRGB());
        }
        return true;
    }
}

