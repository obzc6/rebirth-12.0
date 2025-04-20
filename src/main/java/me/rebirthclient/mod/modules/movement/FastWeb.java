/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoPush;
import me.rebirthclient.mod.modules.movement.ElytraFly;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.entity.player.PlayerEntity;

public class FastWeb
extends Module {
    public final EnumSetting mode = this.add(new EnumSetting("Page", Mode.FAST));
    private final SliderSetting fastSpeed = this.add(new SliderSetting("FastSpeed", 3.0, 0.0, 5.0, v -> this.mode.getValue() == Mode.FAST));
    private final BooleanSetting onlySneak = this.add(new BooleanSetting("OnlySneak", false));

    public FastWeb() {
        super("FastWeb", "So you don't need to keep timer on keybind", Module.Category.Movement);
    }

    @Override
    public void onDisable() {
        Rebirth.TIMER.reset();
    }

    @Override
    public void onUpdate() {
        if (AutoPush.isInWeb((PlayerEntity)FastWeb.mc.player)) {
            if (this.mode.getValue() == Mode.FAST && FastWeb.mc.options.sneakKey.isPressed() || !this.onlySneak.getValue()) {
                Rebirth.TIMER.reset();
                MovementUtil.setMotionY(MovementUtil.getMotionY() - this.fastSpeed.getValue());
            } else if (this.mode.getValue() == Mode.STRICT && !FastWeb.mc.player.isOnGround() && FastWeb.mc.options.sneakKey.isPressed() || !this.onlySneak.getValue()) {
                Rebirth.TIMER.set(8.0f);
            } else if (ElytraFly.INSTANCE.isOff() || !ElytraFly.INSTANCE.boostTimer.getValue()) {
                Rebirth.TIMER.reset();
            }
        } else if (ElytraFly.INSTANCE.isOff() || !ElytraFly.INSTANCE.boostTimer.getValue()) {
            Rebirth.TIMER.reset();
        }
    }

    private static enum Mode {
        FAST,
        STRICT;

        // $FF: synthetic method
        private static FastWeb.Mode[] $values() {
            return new FastWeb.Mode[]{FAST, STRICT};
        }
    }
}
