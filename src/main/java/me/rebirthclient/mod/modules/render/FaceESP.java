/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Vector4d
 */
package me.rebirthclient.mod.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4d;

public class FaceESP
extends Module {
    public SliderSetting xPlus = this.add(new SliderSetting("x+", 0.0, -100.0, 100.0, 1.0));
    public SliderSetting yPlus = this.add(new SliderSetting("y+", 0.0, -100.0, 100.0, 1.0));
    public SliderSetting scale = this.add(new SliderSetting("scale", 1.0, 1.0, 100.0, 0.1));
    private final Identifier ljl = new Identifier("rebirth", "shlt/ljl.png");

    public FaceESP() {
        super("FaceESP", Module.Category.Render);
    }

    @Override
    public void onRender2D(DrawContext context, float tickDelta) {
        for (PlayerEntity player : FaceESP.mc.world.getPlayers()) {
            if (player == null || player.isDead() || player == FaceESP.mc.player) continue;
            double x = player.prevX + (player.getX() - player.prevX) * (double)mc.getTickDelta();
            double y = player.prevY + (player.getY() - player.prevY) * (double)mc.getTickDelta();
            double z = player.prevZ + (player.getZ() - player.prevZ) * (double)mc.getTickDelta();
            Vec3d vector = new Vec3d(x, y + player.getBoundingBox().getYLength() + 0.3, z);
            vector = TextUtil.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
            if (!(vector.z > 0.0) || !(vector.z < 1.0)) continue;
            Vector4d position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
            position.x = Math.min(vector.x, position.x);
            position.y = Math.min(vector.y, position.y);
            position.z = Math.max(vector.x, position.z);
            float diff = (float)(position.z - position.x) / 2.0f;
            float tagX = (float)((position.x + (double)diff) * 1.0);
            RenderSystem.texParameter((int)3553, (int)10240, (int)9729);
            context.drawTexture(this.ljl, (int)((double)((int)tagX) + this.xPlus.getValue()), (int)((double)((int)position.y) + this.yPlus.getValue()), 0.0f, 0.0f, (int)(12.0 * this.scale.getValue()), (int)(12.0 * this.scale.getValue()), (int)(12.0 * this.scale.getValue()), (int)(12.0 * this.scale.getValue()));
        }
    }
}

