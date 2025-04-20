/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookAndOnGround
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class RotateBypass
extends Module {
    private final SliderSetting rotateTimer = this.add(new SliderSetting("RotateTime", 300, 0, 1000));
    private final BooleanSetting packet = this.add(new BooleanSetting("Packet", false));
    public final Timer timer = new Timer();
    public Vec3d vec;

    public RotateBypass() {
        super("RotateBypass", Module.Category.Player);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerInteractBlockC2SPacket) {
            Vec3d directionVec = new Vec3d((double)((PlayerInteractBlockC2SPacket)event.getPacket()).getBlockHitResult().getSide().getVector().getX() * 0.5, (double)((PlayerInteractBlockC2SPacket)event.getPacket()).getBlockHitResult().getSide().getVector().getY() * 0.5, (double)((PlayerInteractBlockC2SPacket)event.getPacket()).getBlockHitResult().getSide().getVector().getZ() * 0.5);
            this.faceVector(new Vec3d(((PlayerInteractBlockC2SPacket)event.getPacket()).getBlockHitResult().getPos().toVector3f()).add(0.5, 0.5, 0.5).add(directionVec));
        }
    }

    @EventHandler
    public final void onMotion(RotateEvent event) {
        if (RotateBypass.nullCheck()) {
            return;
        }
        if (!this.timer.passedMs(this.rotateTimer.getValue()) && this.vec != null) {
            this.faceVector(this.vec, event);
        }
    }

    public void faceVector(Vec3d vec) {
        this.vec = vec;
        this.timer.reset();
        float[] rotations = EntityUtil.getLegitRotations(vec);
        if (this.packet.getValue()) {
            RotateBypass.sendPlayerRot(rotations[0], rotations[1], RotateBypass.mc.player.isOnGround());
        }
    }

    public static void sendPlayerRot(float yaw, float pitch, boolean onGround) {
        mc.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround));
    }

    private void faceVector(Vec3d vec, RotateEvent event) {
        float[] rotations = EntityUtil.getLegitRotations(vec);
        event.setRotation(rotations[0], rotations[1]);
    }
}

