/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.asm.accessors.IEntity;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Vec3d;

public class ItemESP
extends Module {
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));

    public ItemESP() {
        super("ItemESP", Module.Category.Render);
        this.setDescription("Allows the player to see items with an ESP.");
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        for (Entity entity : ItemESP.mc.world.getEntities()) {
            if (!(entity instanceof ItemEntity)) continue;
            Render3DUtil.draw3DBox(matrixStack, ((IEntity)entity).getDimensions().getBoxAt(new Vec3d(this.interpolate(entity.lastRenderX, entity.getX(), partialTicks), this.interpolate(entity.lastRenderY, entity.getY(), partialTicks), this.interpolate(entity.lastRenderZ, entity.getZ(), partialTicks))), this.color.getValue());
        }
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double)delta;
    }
}

