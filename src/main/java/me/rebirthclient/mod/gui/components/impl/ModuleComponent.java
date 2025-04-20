/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.mod.gui.components.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.gui.components.Component;
import me.rebirthclient.mod.gui.components.impl.BindComponent;
import me.rebirthclient.mod.gui.components.impl.BooleanComponent;
import me.rebirthclient.mod.gui.components.impl.ColorComponents;
import me.rebirthclient.mod.gui.components.impl.EnumComponent;
import me.rebirthclient.mod.gui.components.impl.SliderComponent;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.client.ClickGui;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.impl.BindSetting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

public class ModuleComponent
extends Component
implements Wrapper {
    private final String text;
    private final Module module;
    private final ClickGuiTab parent;
    private boolean popped = false;
    private int expandedHeight = this.defaultHeight;
    private final List<Component> settingsList = new ArrayList<Component>();
    boolean hovered = false;
    public boolean isPopped = false;
    public double currentWidth = 0.0;

    public List<Component> getSettingsList() {
        return this.settingsList;
    }

    public ModuleComponent(String text, ClickGuiTab parent, Module module) {
        this.text = text;
        this.parent = parent;
        this.module = module;
        for (Setting setting : this.module.getSettings()) {
            Component c = setting instanceof SliderSetting ? new SliderComponent(this.parent, (SliderSetting)setting) : (setting instanceof BooleanSetting ? new BooleanComponent(this.parent, (BooleanSetting)setting) : (setting instanceof BindSetting ? new BindComponent(this.parent, (BindSetting)setting) : (setting instanceof EnumSetting ? new EnumComponent(this.parent, (EnumSetting)setting) : (setting instanceof ColorSetting ? new ColorComponents(this.parent, (ColorSetting)setting) : null))));
            if (c == null) continue;
            this.settingsList.add(c);
        }
        this.RecalculateExpandedHeight();
    }

    @Override
    public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
        int parentX = this.parent.getX();
        int parentY = this.parent.getY();
        int parentWidth = this.parent.getWidth();
        if (this.popped) {
            int i = offset + this.defaultHeight;
            for (Component children : this.settingsList) {
                children.update(i, mouseX, mouseY, mouseClicked);
                i += children.getHeight();
            }
        }
        boolean bl = this.hovered = mouseX >= (double)parentX && mouseX <= (double)(parentX + parentWidth) && mouseY >= (double)(parentY + offset) && mouseY <= (double)(parentY + offset + this.defaultHeight - 2);
        if (this.hovered && HudManager.currentGrabbed == null) {
            if (mouseClicked) {
                ClickGuiScreen.clicked = false;
                this.module.toggle();
            }
            if (ClickGuiScreen.rightClicked) {
                ClickGuiScreen.rightClicked = false;
                this.popped = !this.popped;
            }
        }
        this.RecalculateExpandedHeight();
        if (this.popped) {
            this.setHeight(this.expandedHeight);
        } else {
            this.setHeight(this.defaultHeight);
        }
    }

    @Override
    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        int parentX = this.parent.getX();
        int parentY = this.parent.getY();
        int parentWidth = this.parent.getWidth();
        MatrixStack matrixStack = drawContext.getMatrices();
        this.currentOffset = ModuleComponent.animate(this.currentOffset, offset);
        if (ClickGui.fade.easeOutQuad() >= 1.0 && ClickGui.INSTANCE.scissor.getValue()) {
            this.setScissorRegion(parentX, (int)((double)parentY + this.currentOffset + (double)this.defaultHeight), parentWidth, mc.getWindow().getHeight() - (int)((double)parentY + this.currentOffset + (double)this.defaultHeight));
        }
        if (this.popped) {
            this.isPopped = true;
            int i = offset + this.defaultHeight;
            for (Component children : this.settingsList) {
                if (children.isVisible()) {
                    children.draw(i, drawContext, partialTicks, color, false);
                    i += children.getHeight();
                    continue;
                }
                if (children instanceof SliderComponent) {
                    SliderComponent sliderComponent = (SliderComponent)children;
                    sliderComponent.renderSliderPosition = 0.0;
                } else if (children instanceof BooleanComponent) {
                    BooleanComponent booleanComponent = (BooleanComponent)children;
                    booleanComponent.currentWidth = 0.0;
                } else if (children instanceof ColorComponents) {
                    ColorComponents colorComponents = (ColorComponents)children;
                    colorComponents.currentWidth = 0.0;
                }
                children.currentOffset = i - this.defaultHeight;
            }
        } else if (this.isPopped) {
            boolean finish2 = true;
            boolean finish = false;
            for (Component children : this.settingsList) {
                if (!children.isVisible()) continue;
                if (!children.draw((int)this.currentOffset, drawContext, partialTicks, color, true)) {
                    finish = true;
                    continue;
                }
                finish2 = false;
            }
            if (finish && finish2) {
                this.isPopped = false;
            }
        } else {
            for (Component children : this.settingsList) {
                children.currentOffset = this.currentOffset;
            }
        }
        if (ClickGui.fade.easeOutQuad() >= 1.0 && ClickGui.INSTANCE.scissor.getValue()) {
            GL11.glDisable((int)3089);
        }
        this.currentWidth = ModuleComponent.animate(this.currentWidth, this.module.isOn() ? (double)parentWidth - 4.0 : 0.0, ClickGui.INSTANCE.booleanSpeed.getValue());
        Render2DUtil.drawRect(matrixStack, (float)(parentX + 2), (float)((int)((double)parentY + this.currentOffset)), (float)this.currentWidth, (float)(this.defaultHeight - 2), ClickGui.INSTANCE.moduleEnable.getValue());
        Render2DUtil.drawRect(matrixStack, (float)(parentX + 2), (float)((int)((double)parentY + this.currentOffset)), (float)(parentWidth - 4), (float)(this.defaultHeight - 2), this.hovered ? ClickGui.INSTANCE.mhColor.getValue() : ClickGui.INSTANCE.mbgColor.getValue());
        TextUtil.drawCustomText(drawContext, this.text, (double)(parentX + 8), (double)((float)((double)(parentY + 8) + this.currentOffset)), this.module.isOn() ? ClickGui.INSTANCE.enableText.getValue().getRGB() : ClickGui.INSTANCE.disableText.getValue().getRGB());
        if (ClickGui.INSTANCE.gear.getValue()) {
            TextUtil.drawCustomText(drawContext, this.popped ? "-" : "+", (double)(parentX + parentWidth - 22), (double)(parentY + 8) + this.currentOffset, ClickGui.INSTANCE.gearColor.getValue().getRGB());
        }
        return true;
    }

    public void setScissorRegion(int x, int y, int width, int height) {
        if (y > mc.getWindow().getHeight()) {
            return;
        }
        double scaledY = mc.getWindow().getHeight() - (y + height);
        GL11.glEnable((int)3089);
        GL11.glScissor((int)x, (int)((int)scaledY), (int)width, (int)height);
    }

    public void RecalculateExpandedHeight() {
        int height = this.defaultHeight;
        for (Component children : this.settingsList) {
            if (children == null || !children.isVisible()) continue;
            height += children.getHeight();
        }
        this.expandedHeight = height;
    }
}

