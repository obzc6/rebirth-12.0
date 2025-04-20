/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$PositionAndOnGround
 *  org.jetbrains.annotations.NotNull
 */
package me.rebirthclient.mod.modules.combat;

import io.netty.buffer.Unpooled;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.Aura;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.jetbrains.annotations.NotNull;

public class Criticals
extends Module {
    public static Criticals INSTANCE;
    public EnumSetting mode = this.add(new EnumSetting("Mode", Mode.Normal));

    public Criticals() {
        super("Criticals", Module.Category.Combat);
        INSTANCE = this;
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        PlayerInteractEntityC2SPacket packet;
        Object t;
        if (!Aura.INSTANCE.sweeping && (t = event.getPacket()) instanceof PlayerInteractEntityC2SPacket && Criticals.getInteractType(packet = (PlayerInteractEntityC2SPacket)t) == InteractType.ATTACK && !(Criticals.getEntity(packet) instanceof EndCrystalEntity)) {
            this.doCrit();
        }
    }

    public void doCrit() {
        if ((Criticals.mc.player.isOnGround() || Criticals.mc.player.getAbilities().flying) && !Criticals.mc.player.isInLava() && !Criticals.mc.player.isSubmergedInWater()) {
            if (this.mode.getValue() == Mode.Strict && Criticals.mc.world.getBlockState(Criticals.mc.player.getBlockPos()).getBlock() != Blocks.COBWEB) {
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY() + 0.062600301692775, Criticals.mc.player.getZ(), false));
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY() + 0.07260029960661, Criticals.mc.player.getZ(), false));
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY(), Criticals.mc.player.getZ(), false));
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY(), Criticals.mc.player.getZ(), false));
            } else if (this.mode.getValue() == Mode.NCP) {
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY() + 0.0625, Criticals.mc.player.getZ(), false));
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY(), Criticals.mc.player.getZ(), false));
            } else if (this.mode.getValue() == Mode.Normal) {
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY() + 1.058293536E-5, Criticals.mc.player.getZ(), false));
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY() + 9.16580235E-6, Criticals.mc.player.getZ(), false));
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY() + 1.0371854E-7, Criticals.mc.player.getZ(), false));
            } else if (this.mode.getValue() == Mode.New2b2t) {
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY() + 2.71875E-7, Criticals.mc.player.getZ(), false));
                Criticals.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Criticals.mc.player.getX(), Criticals.mc.player.getY(), Criticals.mc.player.getZ(), false));
            }
        }
    }

    public static Entity getEntity(@NotNull PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);
        return Criticals.mc.world.getEntityById(packetBuf.readVarInt());
    }

    public static InteractType getInteractType(@NotNull PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);
        packetBuf.readVarInt();
        return (InteractType)packetBuf.readEnumConstant(InteractType.class);
    }

    private static enum Mode {
        NCP,
        Strict,
        Normal,
        New2b2t;

        // $FF: synthetic method
        private static Criticals.Mode[] $values() {
            return new Criticals.Mode[]{NCP, Strict, Normal, New2b2t};
        }
    }

    public static enum InteractType {
        INTERACT,
        ATTACK,
        INTERACT_AT;

        // $FF: synthetic method
        private static Criticals.InteractType[] $values() {
            return new Criticals.InteractType[]{INTERACT, ATTACK, INTERACT_AT};
        }
    }
}
