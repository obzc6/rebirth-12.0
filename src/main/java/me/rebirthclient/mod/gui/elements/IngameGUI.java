/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.util.Window
 *  net.minecraft.client.util.math.MatrixStack
 */
package me.rebirthclient.mod.gui.elements;

import java.awt.Color;
import java.util.ArrayList;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.gui.tabs.Tab;
import me.rebirthclient.mod.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class IngameGUI
extends Tab {
    private KeyBinding keybindUp;
    private KeyBinding keybindDown;
    private KeyBinding keybindLeft;
    private KeyBinding keybindRight;
    int index = 0;
    int indexMods = 0;
    boolean isCategoryMenuOpen = false;
    Module.Category[] categories;
    ArrayList<Module> modules = new ArrayList();

    public IngameGUI() {
        this.keybindUp = new KeyBinding("key.tabup", 265, "key.categories.aoba");
        this.keybindDown = new KeyBinding("key.tabdown", 264, "key.categories.aoba");
        this.keybindLeft = new KeyBinding("key.tableft", 263, "key.categories.aoba");
        this.keybindRight = new KeyBinding("key.tabright", 262, "key.categories.aoba");
        this.categories = Module.Category.values();
        this.x = Rebirth.CONFIG.getSettingInt("ingame_x", 0);
        this.y = Rebirth.CONFIG.getSettingInt("ingame_y", 30);
        this.width = 150;
        this.height = 30;
    }

    @Override
    public void update(double mouseX, double mouseY, boolean mouseClicked) {
        if (Rebirth.HUD.tabGui.getValue()) {
            if (Rebirth.HUD.isClickGuiOpen() && HudManager.currentGrabbed == null && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY <= (double)(this.y + this.height) && mouseClicked) {
                HudManager.currentGrabbed = this;
            }
            if (this.keybindUp.isPressed()) {
                if (!this.isCategoryMenuOpen) {
                    this.index = this.index == 0 ? this.categories.length - 1 : --this.index;
                } else {
                    this.indexMods = this.indexMods == 0 ? this.modules.size() - 1 : --this.indexMods;
                }
                this.keybindUp.setPressed(false);
            } else if (this.keybindDown.isPressed()) {
                if (!this.isCategoryMenuOpen) {
                    this.index = (this.index + 1) % this.categories.length;
                } else {
                    this.indexMods = (this.indexMods + 1) % this.modules.size();
                }
                this.keybindDown.setPressed(false);
            } else if (this.keybindRight.isPressed()) {
                if (!this.isCategoryMenuOpen && this.x != -this.width) {
                    this.isCategoryMenuOpen = true;
                    if (this.modules.isEmpty()) {
                        for (Module module : Rebirth.MODULE.modules) {
                            if (!module.isCategory(this.categories[this.index])) continue;
                            this.modules.add(module);
                        }
                    }
                } else {
                    this.modules.get(this.indexMods).toggle();
                }
                this.keybindRight.setPressed(false);
            } else if (this.keybindLeft.isPressed()) {
                if (this.isCategoryMenuOpen) {
                    this.indexMods = 0;
                    this.modules.clear();
                    this.isCategoryMenuOpen = false;
                }
                this.keybindLeft.setPressed(false);
            }
        }
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks, Color color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        Window window = mc.getWindow();
        matrixStack.push();
        matrixStack.scale(1.0f / (float)((Integer)mc.options.getGuiScale().getValue()).intValue(), 1.0f / (float)((Integer)mc.options.getGuiScale().getValue()).intValue(), 1.0f);
        TextUtil.drawString(drawContext, Rebirth.getName() + " 1.8", 8.0f, 8.0f, color);
        if (Rebirth.HUD.tabGui.getValue()) {
            int i;
            TextUtil.drawOutlinedBox(matrixStack, this.x, this.y, this.width, this.height * this.categories.length, new Color(30, 30, 30), 0.4f);
            for (i = 0; i < this.categories.length; ++i) {
                if (this.index == i) {
                    TextUtil.drawString(drawContext, "> \u00a7f" + this.categories[i].name(), this.x + 8, this.y + this.height * i + 8, color);
                    continue;
                }
                TextUtil.drawString(drawContext, this.categories[i].name(), (double)(this.x + 8), (double)(this.y + this.height * i + 8), 0xFFFFFF);
            }
            if (this.isCategoryMenuOpen) {
                TextUtil.drawOutlinedBox(matrixStack, this.x + this.width, this.y + this.height * this.index, 165, this.height * this.modules.size(), new Color(30, 30, 30), 0.4f);
                for (i = 0; i < this.modules.size(); ++i) {
                    if (this.indexMods == i) {
                        if (this.modules.get(i).isOn()) {
                            TextUtil.drawString(drawContext, "> " + this.modules.get(i).getName(), (double)(this.x + this.width + 5), (double)(this.y + i * this.height + this.index * this.height + 8), color.getRGB());
                            continue;
                        }
                        TextUtil.drawString(drawContext, "> \u00a7f" + this.modules.get(i).getName(), (double)(this.x + this.width + 5), (double)(this.y + i * this.height + this.index * this.height + 8), color.getRGB());
                        continue;
                    }
                    TextUtil.drawString(drawContext, this.modules.get(i).getName(), (double)(this.x + this.width + 5), (double)(this.y + i * this.height + this.index * this.height + 8), this.modules.get(i).isOn() ? color.getRGB() : 0xFFFFFF);
                }
            }
        }
        matrixStack.pop();
    }
}

