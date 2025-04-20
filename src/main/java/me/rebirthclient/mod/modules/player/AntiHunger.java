/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.asm.accessors.IPlayerMoveC2SPacket;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.modules.combat.SilentDouble;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger
extends Module {
    public static AntiHunger INSTANCE;
    public final BooleanSetting sprint = this.add(new BooleanSetting("Sprint", true));
    public final BooleanSetting ground = this.add(new BooleanSetting("Ground", true));

    public AntiHunger() {
        super("AntiHunger", "lol", Module.Category.Player);
        INSTANCE = this;
    }

    @EventHandler(priority=-100)
    public void onPacketSend(PacketEvent.Send event) {
        Object t = event.getPacket();
        if (t instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket packet = (ClientCommandC2SPacket)t;
            if (this.sprint.getValue() && packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                event.cancel();
            }
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket && this.ground.getValue() && (double)AntiHunger.mc.player.fallDistance < 2.9 && !AntiHunger.mc.interactionManager.isBreakingBlock() && (!SilentDouble.INSTANCE.isOn() || PacketMine.secondPos == null)) {
            ((IPlayerMoveC2SPacket)event.getPacket()).setOnGround(false);
        }
    }
}

