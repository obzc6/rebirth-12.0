/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.slot.SlotActionType
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class Replenish
extends Module {
    private final SliderSetting delay = this.add(new SliderSetting("Delay", 0.5, 0.0, 4.0, 0.01));
    private final SliderSetting min = this.add(new SliderSetting("Min", 16, 1, 64));
    private final Timer timer = new Timer();

    public Replenish() {
        super("Replenish", Module.Category.Player);
    }

    @Override
    public void onUpdate() {
        if (Replenish.mc.currentScreen != null && !(Replenish.mc.currentScreen instanceof ClickGuiScreen)) {
            return;
        }
        if (!this.timer.passedMs((long)(this.delay.getValue() * 1000.0))) {
            return;
        }
        for (int i = 0; i < 9; ++i) {
            if (!this.replenish(i)) continue;
            this.timer.reset();
            return;
        }
    }

    private boolean replenish(int slot) {
        ItemStack stack = Replenish.mc.player.getInventory().getStack(slot);
        if (stack.isEmpty()) {
            return false;
        }
        if (!stack.isStackable()) {
            return false;
        }
        if ((double)stack.getCount() >= this.min.getValue()) {
            return false;
        }
        if (stack.getCount() == stack.getMaxCount()) {
            return false;
        }
        for (int i = 9; i < 36; ++i) {
            ItemStack item = Replenish.mc.player.getInventory().getStack(i);
            if (item.isEmpty() || !this.canMerge(stack, item)) continue;
            Replenish.mc.interactionManager.clickSlot(Replenish.mc.player.playerScreenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)Replenish.mc.player);
            return true;
        }
        return false;
    }

    private boolean canMerge(ItemStack source, ItemStack stack) {
        return source.getItem() == stack.getItem() && source.getName().equals((Object)stack.getName());
    }
}

