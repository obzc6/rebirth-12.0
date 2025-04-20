/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.BowItem
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.MathUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.item.BowItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

public class SpinBot
extends Module {
    private final EnumSetting pitchMode = this.add(new EnumSetting("PitchMode", Mode.None));
    private final EnumSetting yawMode = this.add(new EnumSetting("YawMode", Mode.None));
    public final SliderSetting yawDelta = this.add(new SliderSetting("YawDelta", 60, -360, 360));
    public final SliderSetting pitchDelta = this.add(new SliderSetting("PitchDelta", 10, -90, 90));
    public final BooleanSetting allowInteract = this.add(new BooleanSetting("AllowInteract", true));
    private float rotationYaw;
    private float rotationPitch;

    public SpinBot() {
        super("SpinBot", "fun", Module.Category.Player);
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        PlayerActionC2SPacket packet;
        Object t = event.getPacket();
        if (t instanceof PlayerActionC2SPacket && (packet = (PlayerActionC2SPacket)t).getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM && SpinBot.mc.player.getActiveItem().getItem() instanceof BowItem) {
            EntityUtil.sendYawAndPitch(SpinBot.mc.player.getYaw(), SpinBot.mc.player.getPitch());
        }
    }

    @EventHandler(priority=200)
    public void onUpdateWalkingPlayerPre(RotateEvent event) {
        if (this.pitchMode.getValue() == Mode.RandomAngle) {
            this.rotationPitch = MathUtil.random(90.0f, -90.0f);
        }
        if (this.yawMode.getValue() == Mode.RandomAngle) {
            this.rotationYaw = MathUtil.random(0.0f, 360.0f);
        }
        if (this.yawMode.getValue() == Mode.Spin) {
            this.rotationYaw = (float)((double)this.rotationYaw + this.yawDelta.getValue());
        }
        if (this.rotationYaw > 360.0f) {
            this.rotationYaw = 0.0f;
        }
        if (this.rotationYaw < 0.0f) {
            this.rotationYaw = 360.0f;
        }
        if (this.pitchMode.getValue() == Mode.Spin) {
            this.rotationPitch = (float)((double)this.rotationPitch + this.pitchDelta.getValue());
        }
        if (this.rotationPitch > 90.0f) {
            this.rotationPitch = -90.0f;
        }
        if (this.rotationPitch < -90.0f) {
            this.rotationPitch = 90.0f;
        }
        if (this.pitchMode.getValue() == Mode.Static) {
            this.rotationPitch = SpinBot.mc.player.getPitch() + this.pitchDelta.getValueFloat();
            this.rotationPitch = MathUtil.clamp(this.rotationPitch, -90.0f, 90.0f);
        }
        if (this.yawMode.getValue() == Mode.Static) {
            this.rotationYaw = SpinBot.mc.player.getYaw() % 360.0f + this.yawDelta.getValueFloat();
        }
        if (this.allowInteract.getValue() && (SpinBot.mc.options.useKey.isPressed() && !EntityUtil.isUsing() || SpinBot.mc.options.attackKey.isPressed())) {
            return;
        }
        if (this.yawMode.getValue() != Mode.None) {
            event.setYaw(this.rotationYaw);
        }
        if (this.pitchMode.getValue() != Mode.None) {
            event.setPitch(this.rotationPitch);
        }
    }

    public static enum Mode {
        None,
        RandomAngle,
        Spin,
        Static;

        // $FF: synthetic method
        private static SpinBot.Mode[] $values() {
            return new SpinBot.Mode[]{None, RandomAngle, Spin, Static};
        }
    }
}
