/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemStack
 */
package me.rebirthclient.mod.gui.elements;

import java.awt.Color;
import java.util.Objects;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.mod.gui.tabs.Tab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ArmorHUD
extends Tab {
    public ArmorHUD() {
        this.width = 170;
        this.height = 64;
        this.x = (int)Rebirth.CONFIG.getSettingFloat("armor_x", 0.0f);
        this.y = (int)Rebirth.CONFIG.getSettingFloat("armor_y", 200.0f);
    }

    @Override
    public void update(double mouseX, double mouseY, boolean mouseClicked) {
        if (HudManager.currentGrabbed == null && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY <= (double)(this.y + this.height) && mouseClicked) {
            HudManager.currentGrabbed = this;
        }
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks, Color color) {
        if (Rebirth.HUD.isClickGuiOpen()) {
            Render2DUtil.drawRect(drawContext.getMatrices(), (float)this.x, (float)this.y, (float)this.width, (float)this.height, new Color(0, 0, 0, 70));
        }
        int xOff = 0;
        for (ItemStack armor : this.mc.player.getInventory().armor) {
            xOff += 20;
            if (armor.isEmpty()) continue;
            MatrixStack matrixStack = drawContext.getMatrices();
            matrixStack.push();
            matrixStack.translate((float)(-this.x), (float)(-this.y - this.height), 0.0f);
            matrixStack.scale(2.0f, 2.0f, 0.0f);
            int damage = EntityUtil.getDamagePercent(armor);
            int yOffset = this.height / 2 + 15;
            drawContext.drawItem(armor, this.x + this.width / 2 - xOff, this.y + yOffset);
            drawContext.drawItemInSlot(this.mc.textRenderer, armor, this.x + this.width / 2 - xOff, this.y + yOffset);
            String string = String.valueOf(damage);
            int n = this.x + this.width / 2 + 8 - xOff - this.mc.textRenderer.getWidth(String.valueOf(damage)) / 2;
            Objects.requireNonNull(this.mc.textRenderer);
            drawContext.drawText(this.mc.textRenderer, string, n, this.y + yOffset - 9 - 2, new Color((int)(255.0f * (1.0f - (float)damage / 100.0f)), (int)(255.0f * ((float)damage / 100.0f)), 0).getRGB(), true);
            matrixStack.pop();
        }
    }
}

