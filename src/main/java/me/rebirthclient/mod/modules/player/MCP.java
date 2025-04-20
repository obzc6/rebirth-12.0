/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookAndOnGround
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class MCP
extends Module {
    public static MCP INSTANCE;
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true));
    boolean click = false;

    public MCP() {
        super("MCP", Module.Category.Player);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (MCP.nullCheck()) {
            return;
        }
        if (MCP.mc.mouse.wasMiddleButtonClicked()) {
            if (!this.click) {
                int pearl;
                if (MCP.mc.player.getMainHandStack().getItem() == Items.ENDER_PEARL) {
                    EntityUtil.sendLook(new PlayerMoveC2SPacket.LookAndOnGround(MCP.mc.player.getYaw(), MCP.mc.player.getPitch(), MCP.mc.player.isOnGround()));
                    MCP.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, EntityUtil.getWorldActionId(MCP.mc.world)));
                } else if (this.inventory.getValue() && (pearl = InventoryUtil.findItemInventorySlot(Items.ENDER_PEARL)) != -1) {
                    MCP.mc.interactionManager.clickSlot(MCP.mc.player.currentScreenHandler.syncId, pearl, MCP.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)MCP.mc.player);
                    EntityUtil.sendLook(new PlayerMoveC2SPacket.LookAndOnGround(MCP.mc.player.getYaw(), MCP.mc.player.getPitch(), MCP.mc.player.isOnGround()));
                    MCP.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, EntityUtil.getWorldActionId(MCP.mc.world)));
                    MCP.mc.interactionManager.clickSlot(MCP.mc.player.currentScreenHandler.syncId, pearl, MCP.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)MCP.mc.player);
                    EntityUtil.sync();
                } else {
                    pearl = InventoryUtil.findItem(Items.ENDER_PEARL);
                    if (pearl != -1) {
                        int old = MCP.mc.player.getInventory().selectedSlot;
                        InventoryUtil.doSwap(pearl);
                        EntityUtil.sendLook(new PlayerMoveC2SPacket.LookAndOnGround(MCP.mc.player.getYaw(), MCP.mc.player.getPitch(), MCP.mc.player.isOnGround()));
                        MCP.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, EntityUtil.getWorldActionId(MCP.mc.world)));
                        InventoryUtil.doSwap(old);
                    }
                }
                this.click = true;
            }
        } else {
            this.click = false;
        }
    }
}

