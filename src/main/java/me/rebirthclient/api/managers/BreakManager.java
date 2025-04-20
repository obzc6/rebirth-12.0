/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package me.rebirthclient.api.managers;

import java.util.HashMap;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.render.BreakESP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BreakManager
implements Wrapper {
    public final HashMap<Integer, BreakData> breakMap = new HashMap();

    public BreakManager() {
        Rebirth.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    public void onPacket(PacketEvent.Receive event) {
        Object t = event.getPacket();
        if (t instanceof BlockBreakingProgressS2CPacket) {
            BlockBreakingProgressS2CPacket packet = (BlockBreakingProgressS2CPacket)t;
            if (packet.getPos() == null) {
                return;
            }
            BreakData breakData = new BreakData(packet.getPos(), packet.getEntityId());
            if (this.breakMap.containsKey(packet.getEntityId()) && this.breakMap.get((Object)Integer.valueOf((int)packet.getEntityId())).pos.equals((Object)packet.getPos())) {
                return;
            }
            if (breakData.getEntity() == null) {
                return;
            }
            if (MathHelper.sqrt((float)((float)breakData.getEntity().getEyePos().squaredDistanceTo(packet.getPos().toCenterPos()))) > 8.0f) {
                return;
            }
            this.breakMap.put(packet.getEntityId(), breakData);
        }
    }

    public boolean isMining(BlockPos pos) {
        boolean mining = false;
        for (BreakData breakData : new HashMap<Integer, BreakData>(this.breakMap).values()) {
            if (breakData.getEntity() == null || breakData.getEntity().getEyePos().distanceTo(pos.toCenterPos()) > 8.0 || !breakData.pos.equals((Object)pos)) continue;
            mining = true;
            break;
        }
        return mining;
    }

    public static class BreakData {
        public final BlockPos pos;
        public final int entityId;
        public final FadeUtils fade;

        public BreakData(BlockPos pos, int entityId) {
            this.pos = pos;
            this.entityId = entityId;
            this.fade = new FadeUtils((long)BreakESP.INSTANCE.animationTime.getValue());
        }

        public Entity getEntity() {
            Entity entity = Wrapper.mc.world.getEntityById(this.entityId);
            if (entity instanceof PlayerEntity) {
                return entity;
            }
            return null;
        }
    }
}

