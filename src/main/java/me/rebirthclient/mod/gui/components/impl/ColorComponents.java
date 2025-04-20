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
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.MathUtil;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.gui.components.Component;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.modules.client.ClickGui;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ColorComponents
extends Component {
    private float hue;
    private float saturation;
    private float brightness;
    private int alpha;
    private boolean afocused;
    private boolean hfocused;
    private boolean sbfocused;
    private float spos;
    private float bpos;
    private float hpos;
    private float apos;
    private Color prevColor;
    private boolean firstInit;
    private final ColorSetting colorSetting;
    boolean clicked = false;
    boolean popped = false;
    double currentHeight = this.defaultHeight;
    boolean hover = false;
    boolean rainbowHovered = false;
    public double currentWidth = 0.0;

    public ColorSetting getColorSetting() {
        return this.colorSetting;
    }

    public ColorComponents(ClickGuiTab parent, ColorSetting setting) {
        this.parent = parent;
        this.colorSetting = setting;
        this.prevColor = this.getColorSetting().getValue();
        this.updatePos();
        this.firstInit = true;
    }

    @Override
    public boolean isVisible() {
        if (this.colorSetting.visibility != null) {
            return this.colorSetting.visibility.test(null);
        }
        return true;
    }

    private void updatePos() {
        float[] hsb = Color.RGBtoHSB(this.getColorSetting().getValue().getRed(), this.getColorSetting().getValue().getGreen(), this.getColorSetting().getValue().getBlue(), null);
        this.hue = -1.0f + hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.alpha = this.getColorSetting().getValue().getAlpha();
    }

    private void setColor(Color color) {
        this.getColorSetting().setValue(color.getRGB());
        this.prevColor = color;
    }

    @Override
    public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
        int x = this.parent.getX();
        int y = (int)((double)this.parent.getY() + this.currentOffset) - 2;
        int width = this.parent.getWidth();
        double cx = x + 6;
        double cy = y + this.defaultHeight;
        double cw = width - 38;
        double ch = this.getHeight() - 34;
        this.rainbowHovered = Render2DUtil.isHovered(mouseX, mouseY, x + width - 81, y + 4, 40.0, 10.0);
        this.hover = Render2DUtil.isHovered(mouseX, mouseY, (float)x + 5.0f, (float)y + 2.0f, (float)width - 10.0f, 28.0);
        if (this.hover && HudManager.currentGrabbed == null && this.isVisible() && ClickGuiScreen.rightClicked) {
            ClickGuiScreen.rightClicked = false;
            boolean bl = this.popped = !this.popped;
        }
        if (this.popped) {
            this.setHeight(90 + this.defaultHeight);
        } else {
            this.setHeight(this.defaultHeight);
        }
        if (mouseClicked || ClickGuiScreen.hoverClicked) {
            if (!this.clicked) {
                if (Render2DUtil.isHovered(mouseX, mouseY, cx + cw + 17.0, cy, 8.0, ch)) {
                    this.afocused = true;
                }
                if (Render2DUtil.isHovered(mouseX, mouseY, cx + cw + 4.0, cy, 8.0, ch)) {
                    this.hfocused = true;
                }
                if (Render2DUtil.isHovered(mouseX, mouseY, cx, cy, cw, ch)) {
                    this.sbfocused = true;
                }
                if (HudManager.currentGrabbed == null && this.isVisible()) {
                    if (this.rainbowHovered) {
                        this.getColorSetting().setRainbow(!this.getColorSetting().isRainbow);
                    } else if (this.hover && this.getColorSetting().injectBoolean) {
                        this.getColorSetting().booleanValue = !this.getColorSetting().booleanValue;
                    }
                }
            }
            this.clicked = true;
            ClickGuiScreen.hoverClicked = true;
            mouseClicked = false;
        } else {
            this.clicked = false;
            this.sbfocused = false;
            this.afocused = false;
            this.hfocused = false;
        }
        if (!this.popped) {
            return;
        }
        if (HudManager.currentGrabbed == null && this.isVisible()) {
            Color value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
            if (this.sbfocused) {
                this.saturation = (float)((double)MathUtil.clamp((float)(mouseX - cx), 0.0f, (float)cw) / cw);
                this.brightness = (float)((ch - (double)MathUtil.clamp((float)(mouseY - cy), 0.0f, (float)ch)) / ch);
                value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
                this.setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
            }
            if (this.hfocused) {
                this.hue = (float)(-((ch - (double)MathUtil.clamp((float)(mouseY - cy), 0.0f, (float)ch)) / ch));
                value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
                this.setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
            }
            if (this.afocused) {
                this.alpha = (int)((ch - (double)MathUtil.clamp((float)(mouseY - cy), 0.0f, (float)ch)) / ch * 255.0);
                this.setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
            }
        }
    }

    @Override
    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        this.currentHeight = ColorComponents.animate(this.currentHeight, back ? (double)this.defaultHeight : (double)this.getHeight());
        this.currentOffset = ColorComponents.animate(this.currentOffset, offset);
        if (back && Math.abs(this.currentOffset - (double)offset) <= 0.5) {
            this.currentWidth = 0.0;
            return false;
        }
        int x = this.parent.getX();
        int y = (int)((double)this.parent.getY() + this.currentOffset - 2.0);
        int width = this.parent.getWidth();
        MatrixStack matrixStack = drawContext.getMatrices();
        Render2DUtil.drawRect(matrixStack, (float)x + 3.0f, (float)y + 1.0f, (float)width - 6.0f, (float)this.currentHeight - 2.0f, this.hover ? ClickGui.INSTANCE.shColor.getValue() : ClickGui.INSTANCE.sbgColor.getValue());
        if (this.colorSetting.injectBoolean) {
            this.currentWidth = ColorComponents.animate(this.currentWidth, this.colorSetting.booleanValue ? (double)width - 6.0 : 0.0, ClickGui.INSTANCE.booleanSpeed.getValue());
            Render2DUtil.drawRect(matrixStack, (float)x + 3.0f, (float)y + 1.0f, (float)this.currentWidth, (float)this.currentHeight - 2.0f, this.hover ? color.brighter() : color);
        }
        TextUtil.drawCustomText(drawContext, this.colorSetting.getName(), (double)(x + 10), (double)(y + 8), new Color(-1).getRGB());
        Render2DUtil.drawRound(matrixStack, x + width - 34, y + 8, 22.0f, 14.0f, 1.0f, ColorUtil.injectAlpha(this.getColorSetting().getValue(), 255));
        TextUtil.drawCustomSmallText(drawContext, "Rainbow", x + width - 79, y + 6, this.getColorSetting().isRainbow ? color.getRGB() : (this.rainbowHovered ? new Color(-1543503873, true).getRGB() : Color.WHITE.getRGB()));
        if (back) {
            return true;
        }
        if (!this.popped && Math.abs(this.currentHeight - (double)this.getHeight()) <= 0.2) {
            return true;
        }
        double cx = x + 6;
        double cy = y + this.defaultHeight;
        double cw = width - 38;
        double ch = this.currentHeight - 32.0;
        if (this.prevColor != this.getColorSetting().getValue()) {
            this.updatePos();
            this.prevColor = this.getColorSetting().getValue();
        }
        if (this.firstInit) {
            this.spos = (float)(cx + cw - (cw - cw * (double)this.saturation));
            this.bpos = (float)(cy + (ch - ch * (double)this.brightness));
            this.hpos = (float)(cy + (ch - 3.0 + (ch - 3.0) * (double)this.hue));
            this.apos = (float)(cy + (ch - 3.0 - (ch - 3.0) * (double)((float)this.alpha / 255.0f)));
            this.firstInit = false;
        }
        this.spos = (float)ColorComponents.animate(this.spos, (float)(cx + cw - (cw - cw * (double)this.saturation)), 0.6f);
        this.bpos = (float)ColorComponents.animate(this.bpos, (float)(cy + (ch - ch * (double)this.brightness)), 0.6f);
        this.hpos = (float)ColorComponents.animate(this.hpos, (float)(cy + (ch - 3.0 + (ch - 3.0) * (double)this.hue)), 0.6f);
        this.apos = (float)ColorComponents.animate(this.apos, (float)(cy + (ch - 3.0 - (ch - 3.0) * (double)((float)this.alpha / 255.0f))), 0.6f);
        Color colorA = Color.getHSBColor(this.hue, 0.0f, 1.0f);
        Color colorB = Color.getHSBColor(this.hue, 1.0f, 1.0f);
        Color colorC = new Color(0, 0, 0, 0);
        Color colorD = new Color(0, 0, 0);
        Render2DUtil.horizontalGradient(matrixStack, (float)cx + 2.0f, (float)cy, (float)(cx + cw), (float)(cy + ch), colorA, colorB);
        Render2DUtil.verticalGradient(matrixStack, (float)(cx + 2.0), (float)cy, (float)(cx + cw), (float)(cy + ch), colorC, colorD);
        float i = 1.0f;
        while ((double)i < ch - 2.0) {
            float curHue = (float)(1.0 / (ch / (double)i));
            Render2DUtil.drawRect(matrixStack, (float)(cx + cw + 4.0), (float)(cy + (double)i), 8.0f, 1.0f, Color.getHSBColor(curHue, 1.0f, 1.0f));
            i += 1.0f;
        }
        Render2DUtil.drawRect(matrixStack, (float)(cx + cw + 17.0), (float)(cy + 1.0), 8.0f, (float)(ch - 3.0), new Color(-1));
        Render2DUtil.verticalGradient(matrixStack, (float)(cx + cw + 17.0), (float)(cy + (double)0.8f), (float)(cx + cw + 25.0), (float)(cy + ch - 2.0), new Color(this.getColorSetting().getValue().getRed(), this.getColorSetting().getValue().getGreen(), this.getColorSetting().getValue().getBlue(), 255), new Color(0, 0, 0, 0));
        Render2DUtil.drawRect(matrixStack, (float)(cx + cw + 3.0), this.hpos + 0.5f, 10.0f, 1.0f, Color.WHITE);
        Render2DUtil.drawRect(matrixStack, (float)(cx + cw + 16.0), this.apos + 0.5f, 10.0f, 1.0f, Color.WHITE);
        Render2DUtil.drawRound(matrixStack, this.spos - 1.5f, this.bpos - 1.5f, 3.0f, 3.0f, 1.5f, new Color(-1));
        return true;
    }
}

