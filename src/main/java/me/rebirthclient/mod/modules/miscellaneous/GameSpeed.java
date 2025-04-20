/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$Full
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$PositionAndOnGround
 */
package me.rebirthclient.mod.modules.miscellaneous;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.AutoEXP;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class GameSpeed
extends Module {
    private final SliderSetting multiplier = this.add(new SliderSetting("Speed", 1.0, (double)0.1f, 10.0, 0.1f));
    public final BooleanSetting rotateControl = this.add(new BooleanSetting("RotateControl", true));
    public static GameSpeed INSTANCE;
    private final Timer packetListReset = new Timer();
    private int normalLookPos;
    private int rotationMode;
    private int normalPos;
    private float lastPitch;
    private float lastYaw;

    public GameSpeed() {
        super("GameSpeed", Module.Category.Miscellaneous);
        this.setDescription("Increases the speed of Minecraft.");
        INSTANCE = this;
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        return startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f ? startInclusive : (float)((double)startInclusive + (double)(endInclusive - startInclusive) * Math.random());
    }

    @EventHandler
    public final void onPacketSend(PacketEvent.Send event) {
        if (GameSpeed.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket.PositionAndOnGround && this.rotationMode == 1) {
            ++this.normalPos;
            if (this.normalPos > 20) {
                this.rotationMode = 2;
            }
        } else if (event.getPacket() instanceof PlayerMoveC2SPacket.Full && this.rotationMode == 2) {
            ++this.normalLookPos;
            if (this.normalLookPos > 20) {
                this.rotationMode = 1;
            }
        }
    }

    @Override
    public void onDisable() {
        Rebirth.TIMER.reset();
    }

    @Override
    public void onEnable() {
        Rebirth.TIMER.reset();
        if (GameSpeed.nullCheck()) {
            return;
        }
        this.lastYaw = GameSpeed.mc.player.getYaw();
        this.lastPitch = GameSpeed.mc.player.getPitch();
        this.packetListReset.reset();
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (this.packetListReset.passedMs(1000L)) {
            this.normalPos = 0;
            this.normalLookPos = 0;
            this.rotationMode = 1;
            this.lastYaw = GameSpeed.mc.player.getYaw();
            this.lastPitch = GameSpeed.mc.player.getPitch();
            this.packetListReset.reset();
        }
        if (this.lastPitch > 85.0f) {
            this.lastPitch = 85.0f;
        }
        if (AutoEXP.INSTANCE.isThrow() && AutoEXP.INSTANCE.down.getValue()) {
            this.lastPitch = 85.0f;
        }
        Rebirth.TIMER.set(this.multiplier.getValueFloat());
    }

    @EventHandler
    public final void RotateEvent(RotateEvent event) {
        if (this.rotateControl.getValue()) {
            switch (this.rotationMode) {
                case 1: {
                    event.setRotation(this.lastYaw, this.lastPitch);
                    break;
                }
                case 2: {
                    event.setRotation(this.lastYaw + GameSpeed.nextFloat(1.0f, 3.0f), this.lastPitch + GameSpeed.nextFloat(1.0f, 3.0f));
                }
            }
        }
    }
}

