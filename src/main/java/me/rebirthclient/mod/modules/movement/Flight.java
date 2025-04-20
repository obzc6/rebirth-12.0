/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.MoveEvent;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.SliderSetting;

public class Flight
extends Module {
    public static Flight INSTANCE;
    public SliderSetting speed = this.add(new SliderSetting("Speed", 1.0, 0.1f, 10.0));
    private final SliderSetting sneakDownSpeed = this.add(new SliderSetting("DownSpeed", 1.0, 0.1f, 10.0));
    private final SliderSetting upSpeed = this.add(new SliderSetting("UpSpeed", 1.0, 0.1f, 10.0));
    public SliderSetting downFactor = this.add(new SliderSetting("DownFactor", 0.0, 0.0, 1.0, 1.0E-6f));
    private MoveEvent event;

    public Flight() {
        super("Flight", "me", Module.Category.Movement);
        INSTANCE = this;
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        if (Flight.nullCheck()) {
            return;
        }
        this.event = event;
        if (!Flight.mc.options.sneakKey.isPressed() || !Flight.mc.player.input.jumping) {
            if (Flight.mc.options.sneakKey.isPressed()) {
                this.setY(-this.sneakDownSpeed.getValue());
            } else if (Flight.mc.player.input.jumping) {
                this.setY(this.upSpeed.getValue());
            } else {
                this.setY(-this.downFactor.getValue());
            }
        } else {
            this.setY(0.0);
        }
        double[] dir = MovementUtil.directionSpeed(this.speed.getValue());
        this.setX(dir[0]);
        this.setZ(dir[1]);
    }

    private void setX(double f) {
        this.event.setX(f);
        MovementUtil.setMotionX(f);
    }

    private void setY(double f) {
        this.event.setY(f);
        MovementUtil.setMotionY(f);
    }

    private void setZ(double f) {
        this.event.setZ(f);
        MovementUtil.setMotionZ(f);
    }
}

