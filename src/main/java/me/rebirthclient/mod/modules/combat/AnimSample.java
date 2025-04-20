/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.combat;

import java.awt.Color;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.mod.gui.components.Component;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class AnimSample
extends Module {
    public final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));
    private final BooleanSetting box = this.add(new BooleanSetting("Box", true));
    private final BooleanSetting outline = this.add(new BooleanSetting("Outline", true));
    public SliderSetting sliderSpeed = this.add(new SliderSetting("SliderSpeed", 0.2, 0.01, 1.0, 0.01));
    private static Vec3d lastVec3d;

    public AnimSample() {
        super("AnimSample", Module.Category.Combat);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (!AnimSample.mc.player.isCreative() && PacketMine.breakPos != null) {
            Vec3d cur = PacketMine.breakPos.toCenterPos();
            lastVec3d = lastVec3d == null ? cur : new Vec3d(Component.animate(lastVec3d.getX(), cur.x, this.sliderSpeed.getValue()), Component.animate(lastVec3d.getY(), cur.y, this.sliderSpeed.getValue()), Component.animate(lastVec3d.getZ(), cur.z, this.sliderSpeed.getValue()));
            Render3DUtil.draw3DBox(matrixStack, new Box(lastVec3d.add(0.5, 0.5, 0.5), lastVec3d.add(-0.5, -0.5, -0.5)), ColorUtil.injectAlpha(this.color.getValue(), this.color.getValue().getAlpha()), this.outline.getValue(), this.box.getValue());
        }
    }
}

