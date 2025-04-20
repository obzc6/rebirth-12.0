/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.option.Perspective
 *  net.minecraft.client.util.math.MatrixStack
 */
package me.rebirthclient.mod.modules.render;

import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;

public class CameraClip
extends Module {
    public static CameraClip INSTANCE;
    public SliderSetting getDistance = this.add(new SliderSetting("Distance", 4.0, 1.0, 20.0));
    public SliderSetting animateTime = this.add(new SliderSetting("AnimationTime", 200, 0, 1000));
    private final BooleanSetting antiFront = this.add(new BooleanSetting("AntiFront", false));
    private final FadeUtils animation = new FadeUtils(300L);
    boolean first = false;

    public CameraClip() {
        super("CameraClip", Module.Category.Render);
        INSTANCE = this;
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (CameraClip.mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT && this.antiFront.getValue()) {
            CameraClip.mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
        this.animation.setLength(this.animateTime.getValueInt());
        if (CameraClip.mc.options.getPerspective() == Perspective.FIRST_PERSON) {
            if (!this.first) {
                this.first = true;
                this.animation.reset();
            }
        } else if (this.first) {
            this.first = false;
            this.animation.reset();
        }
    }

    public double getDistance() {
        double quad = CameraClip.mc.options.getPerspective() == Perspective.FIRST_PERSON ? 1.0 - this.animation.easeOutQuad() : this.animation.easeOutQuad();
        return 1.0 + (this.getDistance.getValue() - 1.0) * quad;
    }
}

