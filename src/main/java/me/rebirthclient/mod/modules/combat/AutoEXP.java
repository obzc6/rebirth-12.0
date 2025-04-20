/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.collection.DefaultedList
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BindSetting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

public class AutoEXP
extends Module {
    public static AutoEXP INSTANCE;
    public final BindSetting throwBind = this.add(new BindSetting("ThrowBind", -1));
    private final SliderSetting delay = this.add(new SliderSetting("Delay", 3, 0, 5));
    public final BooleanSetting down = this.add(new BooleanSetting("Down", true));
    public final BooleanSetting allowGui = this.add(new BooleanSetting("AllowGui", false));
    public final BooleanSetting onlyBroken = this.add(new BooleanSetting("OnlyBroken", true));
    private final BooleanSetting usingPause = this.add(new BooleanSetting("UsingPause", true));
    private final BooleanSetting onlyGround = this.add(new BooleanSetting("OnlyGround", true));
    private final BooleanSetting inventory = this.add(new BooleanSetting("InventorySwap", true));
    private final Timer delayTimer = new Timer();
    private boolean throwing = false;

    public AutoEXP() {
        super("AutoEXP", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        this.throwing = this.checkThrow();
        if (this.isThrow() && this.delayTimer.passedMs((long)this.delay.getValueInt() * 20L) && (!this.onlyGround.getValue() || AutoEXP.mc.player.isOnGround())) {
            this.throwExp();
        }
    }

    public void throwExp() {
        int newSlot;
        int oldSlot = AutoEXP.mc.player.getInventory().selectedSlot;
        if (this.inventory.getValue() && (newSlot = InventoryUtil.findItemInventorySlot(Items.EXPERIENCE_BOTTLE)) != -1) {
            AutoEXP.mc.interactionManager.clickSlot(AutoEXP.mc.player.currentScreenHandler.syncId, newSlot, AutoEXP.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AutoEXP.mc.player);
            AutoEXP.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, EntityUtil.getWorldActionId(AutoEXP.mc.world)));
            AutoEXP.mc.interactionManager.clickSlot(AutoEXP.mc.player.currentScreenHandler.syncId, newSlot, AutoEXP.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AutoEXP.mc.player);
            EntityUtil.sync();
            this.delayTimer.reset();
        } else {
            newSlot = InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE);
            if (newSlot != -1) {
                InventoryUtil.doSwap(newSlot);
                AutoEXP.mc.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, EntityUtil.getWorldActionId(AutoEXP.mc.world)));
                InventoryUtil.doSwap(oldSlot);
                this.delayTimer.reset();
            }
        }
    }

    @EventHandler(priority=201)
    public void RotateEvent(RotateEvent event) {
        if (!this.down.getValue()) {
            return;
        }
        if (this.isThrow()) {
            event.setPitch(90.0f);
        }
    }

    public boolean isThrow() {
        return this.throwing;
    }

    public boolean checkThrow() {
        if (this.isOff()) {
            return false;
        }
        if (AutoEXP.mc.currentScreen instanceof ChatScreen) {
            return false;
        }
        if (!this.allowGui.getValue() && AutoEXP.mc.currentScreen != null) {
            return false;
        }
        if (this.usingPause.getValue() && AutoEXP.mc.player.isUsingItem()) {
            return false;
        }
        if (!(InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE) != -1 || this.inventory.getValue() && InventoryUtil.findItemInventorySlot(Items.EXPERIENCE_BOTTLE) != -1)) {
            return false;
        }
        if (!this.throwBind.isPressed()) {
            return false;
        }
        if (this.onlyBroken.getValue()) {
            DefaultedList armors = AutoEXP.mc.player.getInventory().armor;
            for (Object armor : armors) {
                if( EntityUtil.getDamagePercent((ItemStack) armor) >= 100) continue;
                return true;
            }
        } else {
            return true;
        }
        return false;
    }
}

