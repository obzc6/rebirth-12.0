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
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class SliderComponent
extends Component {
    private final ClickGuiTab parent;
    private double currentSliderPosition;
    SliderSetting setting;
    private boolean hover = false;
    private boolean firstUpdate = true;
    public double renderSliderPosition = 0.0;

    public SliderComponent(ClickGuiTab parent, SliderSetting setting) {
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
        if (this.firstUpdate) {
            this.currentSliderPosition = (float)((this.setting.getValue() - this.setting.getMinimum()) / this.setting.getRange());
            this.firstUpdate = false;
        }
        int parentX = this.parent.getX();
        int parentY = this.parent.getY();
        int parentWidth = this.parent.getWidth();
        if (mouseX >= (double)(parentX + 2) && mouseX <= (double)(parentX + parentWidth - 2) && mouseY >= (double)(parentY + offset) && mouseY <= (double)(parentY + offset + 26)) {
            this.hover = true;
            if (HudManager.currentGrabbed == null && this.isVisible() && (mouseClicked || ClickGuiScreen.hoverClicked)) {
                ClickGuiScreen.hoverClicked = true;
                ClickGuiScreen.clicked = false;
                this.currentSliderPosition = (float)Math.min((mouseX - (double)(parentX + 2)) / (double)(parentWidth - 6), 1.0);
                this.currentSliderPosition = Math.max(0.0, this.currentSliderPosition);
                this.setting.setValue(this.currentSliderPosition * this.setting.getRange() + this.setting.getMinimum());
            }
        } else {
            this.hover = false;
        }
    }

    @Override
    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        int parentX = this.parent.getX();
        int parentY = this.parent.getY();
        int parentWidth = this.parent.getWidth();
        MatrixStack matrixStack = drawContext.getMatrices();
        this.currentOffset = SliderComponent.animate(this.currentOffset, offset);
        if (back && Math.abs(this.currentOffset - (double)offset) <= 0.5) {
            this.renderSliderPosition = 0.0;
            return false;
        }
        this.renderSliderPosition = SliderComponent.animate(this.renderSliderPosition, Math.floor((double)(parentWidth - 6) * this.currentSliderPosition), ClickGui.INSTANCE.sliderSpeed.getValue());
        Render2DUtil.drawRect(matrixStack, (float)(parentX + 3), (float)((int)((double)parentY + this.currentOffset - 1.0)), (float)((int)this.renderSliderPosition), 28.0f, this.hover ? color.brighter() : color);
        if (this.setting == null) {
            return true;
        }
        TextUtil.drawCustomText(drawContext, this.setting.getName() + ": " + this.setting.getValueFloat(), (double)(parentX + 10), (double)((float)((double)(parentY + 6) + this.currentOffset)), 0xFFFFFF);
        return true;
    }
}

