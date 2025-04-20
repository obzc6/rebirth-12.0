/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.util.math.BlockPos
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.PacketMine;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;

public class SilentDouble
extends Module {
    public static int slotMain;
    public static int swithc2;
    public static SilentDouble INSTANCE;

    public SilentDouble() {
        super("SilentDouble", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        this.update();
    }

    public void update() {
        if (!PacketMine.INSTANCE.isOn()) {
            CommandManager.sendChatMessage("\u00a7e[?] \u00a7c\u00a7oPacketMine?");
            this.disable();
            return;
        }
        if (PacketMine.secondPos != null && !PacketMine.INSTANCE.secondTimer.passed(PacketMine.INSTANCE.getBreakTime(PacketMine.secondPos, this.getTool(PacketMine.secondPos), 0.89))) {
            slotMain = SilentDouble.mc.player.getInventory().selectedSlot;
        }
        if (PacketMine.secondPos != null && PacketMine.INSTANCE.secondTimer.passed(PacketMine.INSTANCE.getBreakTime(PacketMine.secondPos, this.getTool(PacketMine.secondPos), 0.9))) {
            if (SilentDouble.mc.player.getMainHandStack().getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                if (!SilentDouble.mc.options.useKey.isPressed()) {
                    SilentDouble.mc.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(this.getTool(PacketMine.secondPos)));
                    swithc2 = 1;
                } else if (swithc2 == 1) {
                    SilentDouble.mc.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(slotMain));
                    EntityUtil.sync();
                }
            } else {
                SilentDouble.mc.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(this.getTool(PacketMine.secondPos)));
                swithc2 = 1;
            }
        }
        if (PacketMine.secondPos != null && PacketMine.INSTANCE.secondTimer.passed(PacketMine.INSTANCE.getBreakTime(PacketMine.secondPos, this.getTool(PacketMine.secondPos), 1.2)) && swithc2 == 1) {
            SilentDouble.mc.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(slotMain));
            EntityUtil.sync();
        }
    }

    public int getTool(BlockPos pos) {
        return PacketMine.INSTANCE.getTool(pos) == -1 ? SilentDouble.mc.player.getInventory().selectedSlot : PacketMine.INSTANCE.getTool(pos);
    }
}

