/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 */
package me.rebirthclient.mod.gui.components.impl;

import java.awt.Color;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.gui.components.Component;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class EnumComponent
extends Component {
    private final EnumSetting setting;
    private boolean hover = false;

    @Override
    public boolean isVisible() {
        if (this.setting.visibility != null) {
            return this.setting.visibility.test(null);
        }
        return true;
    }

    public EnumComponent(ClickGuiTab parent, EnumSetting enumSetting) {
        this.parent = parent;
        this.setting = enumSetting;
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
                    this.setting.increaseEnum();
                }
                if (ClickGuiScreen.rightClicked) {
                    this.setting.popped = !this.setting.popped;
                    ClickGuiScreen.rightClicked = false;
                }
            }
        } else {
            this.hover = false;
        }
        if (HudManager.currentGrabbed == null && this.isVisible() && mouseClicked) {
            int cy = parentY + offset - 2;
            if (this.setting.popped) {
                for (Enum o : (Enum[])this.setting.getValue().getClass().getEnumConstants()) {
                    cy = (int)((float)cy + TextUtil.getCustomHeight());
                    if (!(mouseX >= (double)parentX) || !(mouseX <= (double)(parentX + parentWidth)) || !(mouseY >= (double)(14 + cy)) || !(mouseY < (double)(TextUtil.getCustomHeight() + 14.0f + (float)cy))) continue;
                    this.setting.setEnumValue(String.valueOf(o));
                    ClickGuiScreen.clicked = false;
                    break;
                }
            }
        }
    }

    @Override
    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        if (this.setting.popped && !back) {
            int y = 0;
            for (Enum ignored : (Enum[])this.setting.getValue().getClass().getEnumConstants()) {
                y = (int)((float)y + TextUtil.getCustomHeight());
            }
            this.setHeight(this.defaultHeight + y);
        } else {
            this.setHeight(this.defaultHeight);
        }
        this.currentOffset = EnumComponent.animate(this.currentOffset, offset);
        if (back && Math.abs(this.currentOffset - (double)offset) <= 0.5) {
            return false;
        }
        int x = this.parent.getX();
        int y = (int)((double)this.parent.getY() + this.currentOffset - 2.0);
        int width = this.parent.getWidth();
        MatrixStack matrixStack = drawContext.getMatrices();
        Render2DUtil.drawRect(matrixStack, (float)x + 3.0f, (float)y + 1.0f, (float)width - 6.0f, 28.0f, this.hover ? Rebirth.HUD.getColor().brighter() : Rebirth.HUD.getColor());
        TextUtil.drawCustomText(drawContext, this.setting.getName() + ": " + this.setting.getValue().name(), (double)(x + 10), (double)(y + 8), new Color(-1).getRGB());
        TextUtil.drawCustomText(drawContext, this.setting.popped ? "-" : "+", (double)(x + width - 22), (double)(y + 8), new Color(255, 255, 255).getRGB());
        int cy = y;
        if (this.setting.popped && !back) {
            for (Enum o : (Enum[])this.setting.getValue().getClass().getEnumConstants()) {
                cy = (int)((float)cy + TextUtil.getCustomHeight());
                String s = o.toString();
                TextUtil.drawCustomText(drawContext, s, (double)((float)width / 2.0f - TextUtil.getCustomWidth(s) + 2.0f + (float)x), (double)(14 + cy), this.setting.getValue().name().equals(s) ? -1 : new Color(120, 120, 120).getRGB());
            }
        }
        return true;
    }
}

