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
import me.rebirthclient.mod.settings.impl.BindSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class BindComponent
extends Component {
    private final BindSetting setting;
    boolean hover = false;

    public BindComponent(ClickGuiTab parent, BindSetting setting) {
        this.setting = setting;
        this.parent = parent;
    }

    @Override
    public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
        int parentX = this.parent.getX();
        int parentY = this.parent.getY();
        int parentWidth = this.parent.getWidth();
        if (HudManager.currentGrabbed == null && this.isVisible()) {
            if (mouseX >= (double)(parentX + 2) && mouseX <= (double)(parentX + parentWidth - 2) && mouseY >= (double)(parentY + offset) && mouseY <= (double)(parentY + offset + 24)) {
                this.hover = true;
                if (mouseClicked) {
                    ClickGuiScreen.clicked = false;
                    this.setting.setListening(!this.setting.isListening());
                }
            } else {
                this.hover = false;
            }
        } else {
            this.hover = false;
        }
    }

    @Override
    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        if (back) {
            this.setting.setListening(false);
        }
        int parentX = this.parent.getX();
        int parentY = this.parent.getY();
        this.currentOffset = BindComponent.animate(this.currentOffset, offset);
        if (back && Math.abs(this.currentOffset - (double)offset) <= 0.5) {
            return false;
        }
        int y = (int)((double)this.parent.getY() + this.currentOffset - 2.0);
        int width = this.parent.getWidth();
        MatrixStack matrixStack = drawContext.getMatrices();
        String text = this.setting.isListening() ? "Press Key.." : this.setting.getBind();
        if (this.hover) {
            Render2DUtil.drawRect(matrixStack, (float)parentX + 3.0f, (float)y + 1.0f, (float)width - 6.0f, 28.0f, ClickGui.INSTANCE.shColor.getValue());
        }
        TextUtil.drawCustomText(drawContext, this.setting.getName() + ": " + text, (double)(parentX + 10), (double)((float)((double)(parentY + 8) + this.currentOffset)), 0xFFFFFF);
        return true;
    }
}

