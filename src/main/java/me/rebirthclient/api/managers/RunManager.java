/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.api.managers;

import java.util.HashMap;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.Event;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.events.impl.UpdateWalkingEvent;
import me.rebirthclient.api.events.impl.WorldBreakEvent;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.MathUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.asm.accessors.IPlayerMoveC2SPacket;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.surround.Surround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RunManager
implements Wrapper {
    public final HashMap<Integer, BlockPos> breakMap = new HashMap();
    public float preYaw = 0.0f;
    public float prePitch = 0.0f;
    public boolean rotating = false;
    public static final Timer ROTATE_TIMER = new Timer();
    public static Vec3d directionVec = null;
    private static float renderPitch;
    private static float renderYawOffset;
    private static float prevPitch;
    private static float prevRenderYawOffset;
    private static float prevRotationYawHead;
    private static float rotationYawHead;
    private int ticksExisted;
    public float lastYaw = 0.0f;
    public float lastPitch = 0.0f;
    boolean worldNull = true;

    public RunManager() {
        Rebirth.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    public void onWorldBreak(WorldBreakEvent event) {
        if (event.getId() == RunManager.mc.player.getId()) {
            return;
        }
        this.breakMap.put(event.getId(), event.getPos());
    }

    @EventHandler(priority=101)
    public void onUpdateWalking(UpdateWalkingEvent event) {
        if (RunManager.mc.player == null) {
            return;
        }
        if (Surround.INSTANCE.enableInHole.getValue() && !Surround.INSTANCE.isOn() && BlockUtil.isHole(EntityUtil.getPlayerPos())) {
            Surround.INSTANCE.enable();
        }
        if (event.getStage() == Event.Stage.Pre) {
            this.preYaw = RunManager.mc.player.getYaw();
            this.prePitch = RunManager.mc.player.getPitch();
            RotateEvent rotateEvent = new RotateEvent(this.preYaw, this.prePitch);
            Rebirth.EVENT_BUS.post(rotateEvent);
            this.rotating = true;
            RunManager.mc.player.setYaw(rotateEvent.getYaw());
            RunManager.mc.player.setPitch(rotateEvent.getPitch());
        } else if (event.getStage() == Event.Stage.Post) {
            RunManager.mc.player.setYaw(this.preYaw);
            RunManager.mc.player.setPitch(this.prePitch);
            this.rotating = false;
        }
    }

    @EventHandler(priority=101)
    public void onRotation(RotateEvent event) {
        if (RunManager.mc.player == null) {
            return;
        }
        if (directionVec != null && !ROTATE_TIMER.passed((long)(Rebirth.HUD.rotateTime.getValue() * 1000.0))) {
            float[] angle = EntityUtil.getLegitRotations(directionVec);
            event.setYaw(angle[0]);
            event.setPitch(MathUtil.clamp(angle[1] + MathUtil.random(-3.0f, 3.0f), -90.0f, 90.0f));
        }
    }

    @EventHandler(priority=-200)
    public void onPacketSend(PacketEvent.Send event) {
        float pitch;
        PlayerMoveC2SPacket packet;
        Object t;
        if (event.isCancel()) {
            return;
        }
        if (directionVec != null && !ROTATE_TIMER.passed((long)(Rebirth.HUD.rotateTime.getValue() * 1000.0)) && !EntityUtil.rotating && Rebirth.HUD.rotatePlus.getValue() && (t = event.getPacket()) instanceof PlayerMoveC2SPacket) {
            packet = (PlayerMoveC2SPacket)t;
            if (!packet.changesLook()) {
                return;
            }
            float yaw2 = packet.getYaw(114514.0f);
            pitch = packet.getPitch(114514.0f);
            if (yaw2 == RunManager.mc.player.getYaw() && pitch == RunManager.mc.player.getPitch()) {
                float[] angle = EntityUtil.getLegitRotations(directionVec);
                ((IPlayerMoveC2SPacket)event.getPacket()).setYaw(angle[0]);
                ((IPlayerMoveC2SPacket)event.getPacket()).setPitch(angle[1]);
            }
        }
        Object yaw2;
        if ((yaw2 = event.getPacket()) instanceof PlayerMoveC2SPacket) {
            packet = (PlayerMoveC2SPacket)yaw2;
            float yaw3 = packet.getYaw(114514.0f);
            pitch = packet.getPitch(114514.0f);
            if (yaw3 == 114514.0f || pitch == 114514.0f) {
                return;
            }
            this.lastYaw = yaw3;
            this.lastPitch = pitch;
            this.set(this.lastYaw, this.lastPitch);
        }
    }

    @EventHandler(priority=100)
    public void onReceivePacket(PacketEvent.Receive event) {
        Object t = event.getPacket();
        if (t instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)t;
            this.lastYaw = packet.getYaw();
            this.lastPitch = packet.getPitch();
            this.set(packet.getYaw(), packet.getPitch());
        }
    }

    @EventHandler
    public void onUpdateWalkingPre(UpdateWalkingEvent event) {
        this.set(this.lastYaw, this.lastPitch);
    }

    private void set(float yaw, float pitch) {
        if (Module.nullCheck()) {
            return;
        }
        if (RunManager.mc.player.age == this.ticksExisted) {
            return;
        }
        this.ticksExisted = RunManager.mc.player.age;
        prevPitch = renderPitch;
        prevRenderYawOffset = renderYawOffset;
        renderYawOffset = this.getRenderYawOffset(yaw, prevRenderYawOffset);
        prevRotationYawHead = rotationYawHead;
        rotationYawHead = yaw;
        renderPitch = pitch;
    }

    public static float getRenderPitch() {
        return renderPitch;
    }

    public static float getRotationYawHead() {
        return rotationYawHead;
    }

    public static float getRenderYawOffset() {
        return renderYawOffset;
    }

    public static float getPrevPitch() {
        return prevPitch;
    }

    public static float getPrevRotationYawHead() {
        return prevRotationYawHead;
    }

    public static float getPrevRenderYawOffset() {
        return prevRenderYawOffset;
    }

    private float getRenderYawOffset(float yaw, float offsetIn) {
        float offset;
        double zDif;
        float result = offsetIn;
        double xDif = RunManager.mc.player.getX() - RunManager.mc.player.prevX;
        if (xDif * xDif + (zDif = RunManager.mc.player.getZ() - RunManager.mc.player.prevZ) * zDif > 0.002500000176951289) {
            offset = (float)MathHelper.atan2((double)zDif, (double)xDif) * 57.295776f - 90.0f;
            float wrap = MathHelper.abs((float)(MathHelper.wrapDegrees((float)yaw) - offset));
            result = 95.0f < wrap && wrap < 265.0f ? offset - 180.0f : offset;
        }
        if (RunManager.mc.player.handSwingProgress > 0.0f) {
            result = yaw;
        }
        if ((offset = MathHelper.wrapDegrees((float)(yaw - (result = offsetIn + MathHelper.wrapDegrees((float)(result - offsetIn)) * 0.3f)))) < -75.0f) {
            offset = -75.0f;
        } else if (offset >= 75.0f) {
            offset = 75.0f;
        }
        result = yaw - offset;
        if (offset * offset > 2500.0f) {
            result += offset * 0.2f;
        }
        return result;
    }

    public void run() {
        if (!Rebirth.loaded) {
            return;
        }
        if (this.worldNull && RunManager.mc.world != null) {
            Rebirth.MODULE.onLogin();
            this.worldNull = false;
        } else if (!this.worldNull && RunManager.mc.world == null) {
            this.worldNull = true;
        }
        Render3DUtil.updateJello();
    }
}

