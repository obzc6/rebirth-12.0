/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
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
import net.minecraft.util.math.Vec3d;

public class PlayerESP
extends Module {
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));

    public PlayerESP() {
        super("PlayerESP", Module.Category.Render);
        this.setDescription("Allows the player to see other players with an ESP.");
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        for (Entity player : PlayerESP.mc.world.getPlayers()) {
            if (player == PlayerESP.mc.player) continue;
            Render3DUtil.draw3DBox(matrixStack, ((IEntity)player).getDimensions().getBoxAt(new Vec3d(this.interpolate(player.lastRenderX, player.getX(), partialTicks), this.interpolate(player.lastRenderY, player.getY(), partialTicks), this.interpolate(player.lastRenderZ, player.getZ(), partialTicks))), this.color.getValue());
        }
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double)delta;
    }
}

