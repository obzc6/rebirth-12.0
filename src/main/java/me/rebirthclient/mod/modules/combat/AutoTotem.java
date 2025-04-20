/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.screen.slot.SlotActionType
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.UpdateWalkingEvent;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem
extends Module {
    private final BooleanSetting mainHand = this.add(new BooleanSetting("MainHand", false));
    private final SliderSetting health = this.add(new SliderSetting("Health", 16.0, 0.0, 36.0, 0.1));
    private final Timer timer = new Timer().reset();

    public AutoTotem() {
        super("AutoTotem", Module.Category.Combat);
        this.setDescription("Automatically replaced totems.");
    }

    @EventHandler
    public void onUpdateWalking(UpdateWalkingEvent event) {
        this.update();
    }

    @Override
    public void onUpdate() {
        this.update();
    }

    @Override
    public String getInfo() {
        return String.valueOf(InventoryUtil.findTotem());
    }

    private void update() {
        if (!(AutoTotem.mc.currentScreen == null || AutoTotem.mc.currentScreen instanceof ChatScreen || AutoTotem.mc.currentScreen instanceof InventoryScreen || AutoTotem.mc.currentScreen instanceof ClickGuiScreen)) {
            return;
        }
        if (!this.timer.passedMs(200L)) {
            return;
        }
        if ((double)(AutoTotem.mc.player.getHealth() + AutoTotem.mc.player.getAbsorptionAmount()) > this.health.getValue()) {
            return;
        }
        if (AutoTotem.mc.player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING || AutoTotem.mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }
        int itemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING);
        if (itemSlot != -1) {
            if (this.mainHand.getValue()) {
                InventoryUtil.doSwap(0);
                if (AutoTotem.mc.player.getInventory().getStack(0).getItem() != Items.TOTEM_OF_UNDYING) {
                    AutoTotem.mc.interactionManager.clickSlot(AutoTotem.mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, (PlayerEntity)AutoTotem.mc.player);
                    AutoTotem.mc.interactionManager.clickSlot(AutoTotem.mc.player.currentScreenHandler.syncId, 36, 0, SlotActionType.PICKUP, (PlayerEntity)AutoTotem.mc.player);
                    AutoTotem.mc.interactionManager.clickSlot(AutoTotem.mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, (PlayerEntity)AutoTotem.mc.player);
                    EntityUtil.sync();
                }
            } else {
                AutoTotem.mc.interactionManager.clickSlot(AutoTotem.mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, (PlayerEntity)AutoTotem.mc.player);
                AutoTotem.mc.interactionManager.clickSlot(AutoTotem.mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, (PlayerEntity)AutoTotem.mc.player);
                AutoTotem.mc.interactionManager.clickSlot(AutoTotem.mc.player.currentScreenHandler.syncId, itemSlot, 0, SlotActionType.PICKUP, (PlayerEntity)AutoTotem.mc.player);
                EntityUtil.sync();
            }
            this.timer.reset();
        }
    }
}

