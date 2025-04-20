/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.Monster
 *  net.minecraft.entity.passive.AnimalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.text.StringVisitable
 */
package me.rebirthclient.mod.gui.tabs;

import java.awt.Color;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.modules.client.ClickGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringVisitable;

public class RadarTab
extends ClickGuiTab {
    float getDistance = 50.0f;

    public RadarTab(String title, int x, int y) {
        super(title, x, y);
        this.setWidth(190);
        this.setHeight(190);
        this.inheritHeightFromChildren = false;
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks, Color color) {
        MatrixStack matrixStack = drawContext.getMatrices();
        if (this.drawBorder) {
            Render2DUtil.drawRound(matrixStack, this.x, this.y, this.width, this.height + 30, 4.0f, ClickGui.INSTANCE.bgColor.getValue());
            Render2DUtil.drawRect(matrixStack, (float)this.x, (float)(this.y + 30) + (float)this.height / 2.0f, (float)(this.width - 1), 1.0f, new Color(128, 128, 128));
            Render2DUtil.drawRect(matrixStack, (float)this.x + (float)this.width / 2.0f, (float)(this.y + 30), 1.0f, (float)this.height, new Color(128, 128, 128));
            Render2DUtil.drawBox(matrixStack, this.x + this.width / 2 - 2, this.y + 30 + this.height / 2 - 2, 5, 5, Rebirth.HUD.getColor(), 1.0f);
            float sin_theta = (float)Math.sin(Math.toRadians(-this.mc.player.getRotationClient().y));
            float cos_theta = (float)Math.cos(Math.toRadians(-this.mc.player.getRotationClient().y));
            int center_x = this.x + this.width / 2;
            int center_y = this.y + 28 + this.height / 2;
            for (Entity entity : this.mc.world.getEntities()) {
                if (!(entity instanceof LivingEntity) || entity instanceof PlayerEntity) continue;
                Color c = entity instanceof AnimalEntity ? new Color(0, 255, 0) : (entity instanceof Monster ? new Color(255, 0, 0) : new Color(0, 0, 255));
                float ratio_x = (float)(entity.getX() - this.mc.player.getX()) / this.getDistance;
                float ratio_y = (float)(entity.getZ() - this.mc.player.getZ()) / this.getDistance;
                float fake_x = (float)this.x + (float)this.width / 2.0f - (float)this.width * ratio_x / 2.0f;
                float fake_y = (float)(this.y + 28) + (float)this.height / 2.0f - (float)this.width * ratio_y / 2.0f;
                float radius_x = cos_theta * (fake_x - (float)center_x) - sin_theta * (fake_y - (float)center_y) + (float)center_x;
                float radius_y = sin_theta * (fake_x - (float)center_x) + cos_theta * (fake_y - (float)center_y) + (float)center_y;
                Render2DUtil.drawRect(matrixStack, (float)((int)Math.min((float)(this.x + this.width - 5), Math.max((float)this.x, radius_x))), (float)((int)Math.min((float)(this.y + 25 + this.height), Math.max((float)(this.y + 30), radius_y))), 3.0f, 3.0f, c);
            }
            for (Entity entity : this.mc.world.getPlayers()) {
                if (entity == this.mc.player) continue;
                float ratio_x = (float)(entity.getX() - this.mc.player.getX()) / this.getDistance;
                float ratio_y = (float)(entity.getZ() - this.mc.player.getZ()) / this.getDistance;
                float fake_x = (float)this.x + (float)this.width / 2.0f - (float)this.width * ratio_x / 2.0f;
                float fake_y = (float)(this.y + 28) + (float)this.height / 2.0f - (float)this.width * ratio_y / 2.0f;
                float radius_x = cos_theta * (fake_x - (float)center_x) - sin_theta * (fake_y - (float)center_y) + (float)center_x;
                float radius_y = sin_theta * (fake_x - (float)center_x) + cos_theta * (fake_y - (float)center_y) + (float)center_y;
                Render2DUtil.drawBox(matrixStack, (int)Math.min((float)(this.x + this.width - 5), Math.max((float)this.x, radius_x)), (int)Math.min((float)(this.y + 25 + this.height), Math.max((float)(this.y + 30), radius_y)), 3, 3, new Color(255, 255, 255), 1.0f);
                TextUtil.drawStringWithScale(drawContext, entity.getName().getString(), (float)((int)Math.min((float)(this.x + this.width - 5), Math.max((float)this.x, radius_x))) - (float)this.mc.textRenderer.getWidth((StringVisitable)entity.getName()) * 0.5f, (int)Math.min((float)(this.y + 25 + this.height), Math.max((float)(this.y + 30), radius_y)) - 10, color, 1.0f);
            }
        }
        TextUtil.drawCustomText(drawContext, this.title, (double)(this.x + 8), (double)(this.y + 8), Rebirth.HUD.getColor());
    }
}

