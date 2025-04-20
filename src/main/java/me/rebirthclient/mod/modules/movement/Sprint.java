/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.UpdateWalkingEvent;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;

public class Sprint
extends Module {
    public static Sprint INSTANCE;
    private final BooleanSetting legit = this.add(new BooleanSetting("Legit", true));

    public Sprint() {
        super("Sprint", Module.Category.Movement);
        this.setDescription("Permanently keeps player in sprinting mode.");
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (Sprint.nullCheck()) {
            return;
        }
        if (this.legit.getValue()) {
            Sprint.mc.options.sprintKey.setPressed(true);
        }
    }

    @EventHandler
    public void onUpdateWalking(UpdateWalkingEvent event) {
        if (!this.legit.getValue()) {
            if (Sprint.mc.player.getHungerManager().getFoodLevel() <= 6) {
                return;
            }
            Sprint.mc.player.setSprinting(MovementUtil.isMoving() && !Sprint.mc.player.isSneaking());
        }
    }
}

