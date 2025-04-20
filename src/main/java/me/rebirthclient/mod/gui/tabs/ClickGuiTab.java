/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 */
package me.rebirthclient.mod.gui.tabs;

import java.awt.Color;
import java.util.ArrayList;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.gui.components.Component;
import me.rebirthclient.mod.gui.tabs.Tab;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.client.ClickGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ClickGuiTab
extends Tab {
    protected String title;
    protected boolean drawBorder = true;
    protected boolean inheritHeightFromChildren = true;
    private Module.Category category = null;
    protected ArrayList<Component> children = new ArrayList();
    public double currentHeight = 0.0;

    public ClickGuiTab(String title, int x, int y) {
        this.title = title;
        this.x = Rebirth.CONFIG.getSettingInt(title + "_x", x);
        this.y = Rebirth.CONFIG.getSettingInt(title + "_y", y);
        this.width = 190;
        this.mc = MinecraftClient.getInstance();
    }

    public ClickGuiTab(Module.Category category, int x, int y) {
        this(category.name(), x, y);
        this.category = category;
    }

    public ArrayList<Component> getChildren() {
        return this.children;
    }

    public final String getTitle() {
        return this.title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    @Override
    public final int getX() {
        return this.x;
    }

    @Override
    public final void setX(int x) {
        this.x = x;
    }

    @Override
    public final int getY() {
        return this.y;
    }

    @Override
    public final void setY(int y) {
        this.y = y;
    }

    @Override
    public final int getWidth() {
        return this.width;
    }

    @Override
    public final void setWidth(int width) {
        this.width = width;
    }

    @Override
    public final int getHeight() {
        return this.height;
    }

    @Override
    public final void setHeight(int height) {
        this.height = height;
    }

    public final boolean isGrabbed() {
        return HudManager.currentGrabbed == this;
    }

    public final void addChild(Component component) {
        this.children.add(component);
    }

    @Override
    public void update(double mouseX, double mouseY, boolean mouseClicked) {
        if (this.inheritHeightFromChildren) {
            int tempHeight = 1;
            for (Component child : this.children) {
                tempHeight += child.getHeight();
            }
            this.height = tempHeight;
        }
        this.onMouseClick(mouseX, mouseY, mouseClicked);
    }

    public void onMouseClick(double mouseX, double mouseY, boolean mouseClicked) {
        if (Rebirth.HUD.isClickGuiOpen()) {
            if (HudManager.currentGrabbed == null && mouseX >= (double)(this.x - 5) && mouseX <= (double)(this.x + this.width + 5) && mouseY >= (double)(this.y - 5) && mouseY <= (double)(this.y + 25) && mouseClicked) {
                HudManager.currentGrabbed = this;
            }
            int i = defaultHeight;
            for (Component child : this.children) {
                child.update(i, mouseX, mouseY, mouseClicked);
                i += child.getHeight();
            }
        }
    }

    public static double animate(double current, double endPoint) {
        return ClickGuiTab.animate(current, endPoint, ClickGui.INSTANCE.animationSpeed.getValue());
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

    @Override
    public void draw(DrawContext drawContext, float partialTicks, Color color) {
        if (this.inheritHeightFromChildren) {
            int tempHeight = 1;
            for (Component component : this.children) {
                tempHeight += component.getHeight();
            }
            this.height = tempHeight;
        }
        MatrixStack matrixStack = drawContext.getMatrices();
        this.currentHeight = ClickGuiTab.animate(this.currentHeight, this.height);
        if (this.drawBorder) {
            Render2DUtil.drawRect(matrixStack, (float)(this.x - 4), (float)(this.y - 4), (float)(this.width + 7), 30.0f, color.getRGB());
            Render2DUtil.drawRect(matrixStack, (float)(this.x - 4), (float)(this.y - 4 + 30), (float)(this.width + 7), 1.0f, new Color(38, 38, 38));
            Render2DUtil.drawRect(matrixStack, (float)this.x, (float)(this.y + 28), (float)this.width, (float)((int)this.currentHeight + 1), ClickGui.INSTANCE.bgColor.getValue());
        }
        int i = defaultHeight;
        for (Component child : this.children) {
            if (child.isVisible()) {
                child.draw(i, drawContext, partialTicks, color, false);
                i += child.getHeight();
                continue;
            }
            child.currentOffset = i - defaultHeight;
        }
        TextUtil.drawCustomText(drawContext, this.title, (double)(this.x + 4), (double)(this.y + 4), new Color(255, 255, 255));
        if (this.category != null) {
            String string = "[" + Rebirth.MODULE.categoryModules.get((Object)this.category) + "]";
            TextUtil.drawCustomText(drawContext, string, (double)((float)(this.x + this.width - 4) - TextUtil.getCustomWidth(string) * 2.0f), (double)(this.y + 4), new Color(255, 255, 255));
        }
    }
}

