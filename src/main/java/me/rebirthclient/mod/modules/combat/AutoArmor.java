/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.enchantment.ProtectionEnchantment
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.ElytraItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 */
package me.rebirthclient.mod.modules.combat;

import java.util.HashMap;
import java.util.Map;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.movement.ElytraFly;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor
extends Module {
    private final BooleanSetting noMove = this.add(new BooleanSetting("NoMove", false));
    private final SliderSetting delay = this.add(new SliderSetting("Delay", 3.0, 0.0, 10.0, 1.0));
    private final BooleanSetting autoElytra = this.add(new BooleanSetting("AutoElytra", true));
    public static AutoArmor INSTANCE;
    private int tickDelay = 0;

    public AutoArmor() {
        super("AutoArmor", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (!(AutoArmor.mc.currentScreen == null || AutoArmor.mc.currentScreen instanceof ChatScreen || AutoArmor.mc.currentScreen instanceof InventoryScreen || AutoArmor.mc.currentScreen instanceof ClickGuiScreen)) {
            return;
        }
        if (AutoArmor.mc.player.playerScreenHandler != AutoArmor.mc.player.currentScreenHandler) {
            return;
        }
        if (MovementUtil.isMoving() && this.noMove.getValue()) {
            return;
        }
        if (this.tickDelay > 0) {
            --this.tickDelay;
            return;
        }
        this.tickDelay = this.delay.getValueInt();
        HashMap<EquipmentSlot, int[]> armorMap = new HashMap<EquipmentSlot, int[]>(4);
        armorMap.put(EquipmentSlot.FEET, new int[]{36, this.getProtection(AutoArmor.mc.player.getInventory().getStack(36)), -1, -1});
        armorMap.put(EquipmentSlot.LEGS, new int[]{37, this.getProtection(AutoArmor.mc.player.getInventory().getStack(37)), -1, -1});
        armorMap.put(EquipmentSlot.CHEST, new int[]{38, this.getProtection(AutoArmor.mc.player.getInventory().getStack(38)), -1, -1});
        armorMap.put(EquipmentSlot.HEAD, new int[]{39, this.getProtection(AutoArmor.mc.player.getInventory().getStack(39)), -1, -1});
        for (int s = 0; s < 36; ++s) {
            if (!(AutoArmor.mc.player.getInventory().getStack(s).getItem() instanceof ArmorItem) && AutoArmor.mc.player.getInventory().getStack(s).getItem() != Items.ELYTRA) continue;
            int protection = this.getProtection(AutoArmor.mc.player.getInventory().getStack(s));
            EquipmentSlot slot = AutoArmor.mc.player.getInventory().getStack(s).getItem() instanceof ElytraItem ? EquipmentSlot.CHEST : ((ArmorItem)AutoArmor.mc.player.getInventory().getStack(s).getItem()).getSlotType();
            for (Map.Entry e : armorMap.entrySet()) {
                if (this.autoElytra.getValue() && ElytraFly.INSTANCE.isOn() && e.getKey() == EquipmentSlot.CHEST) {
                    if (!AutoArmor.mc.player.getInventory().getStack(38).isEmpty() && AutoArmor.mc.player.getInventory().getStack(38).getItem() instanceof ElytraItem && ElytraItem.isUsable((ItemStack)AutoArmor.mc.player.getInventory().getStack(38)) || ((int[])e.getValue())[2] != -1 && !AutoArmor.mc.player.getInventory().getStack(((int[])e.getValue())[2]).isEmpty() && AutoArmor.mc.player.getInventory().getStack(((int[])e.getValue())[2]).getItem() instanceof ElytraItem && ElytraItem.isUsable((ItemStack)AutoArmor.mc.player.getInventory().getStack(((int[])e.getValue())[2])) || AutoArmor.mc.player.getInventory().getStack(s).isEmpty() || !(AutoArmor.mc.player.getInventory().getStack(s).getItem() instanceof ElytraItem) || !ElytraItem.isUsable((ItemStack)AutoArmor.mc.player.getInventory().getStack(s))) continue;
                    ((int[])e.getValue())[2] = s;
                    continue;
                }
                if (protection <= 0 || e.getKey() != slot || protection <= ((int[])e.getValue())[1] || protection <= ((int[])e.getValue())[3]) continue;
                ((int[])e.getValue())[2] = s;
                ((int[])e.getValue())[3] = protection;
            }
        }
        for (Map.Entry equipmentSlotEntry : armorMap.entrySet()) {
            if (((int[])equipmentSlotEntry.getValue())[2] == -1) continue;
            if (((int[])equipmentSlotEntry.getValue())[1] == -1 && ((int[])equipmentSlotEntry.getValue())[2] < 9) {
                if (((int[])equipmentSlotEntry.getValue())[2] != AutoArmor.mc.player.getInventory().selectedSlot) {
                    AutoArmor.mc.player.getInventory().selectedSlot = ((int[])equipmentSlotEntry.getValue())[2];
                    AutoArmor.mc.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(((int[])equipmentSlotEntry.getValue())[2]));
                }
                AutoArmor.mc.interactionManager.clickSlot(AutoArmor.mc.player.currentScreenHandler.syncId, 36 + ((int[])equipmentSlotEntry.getValue())[2], 1, SlotActionType.QUICK_MOVE, (PlayerEntity)AutoArmor.mc.player);
                EntityUtil.sync();
            } else if (AutoArmor.mc.player.playerScreenHandler == AutoArmor.mc.player.currentScreenHandler) {
                int armorSlot = ((int[])equipmentSlotEntry.getValue())[0] - 34 + (39 - ((int[])equipmentSlotEntry.getValue())[0]) * 2;
                int newArmorSlot = ((int[])equipmentSlotEntry.getValue())[2] < 9 ? 36 + ((int[])equipmentSlotEntry.getValue())[2] : ((int[])equipmentSlotEntry.getValue())[2];
                AutoArmor.mc.interactionManager.clickSlot(AutoArmor.mc.player.currentScreenHandler.syncId, newArmorSlot, 0, SlotActionType.PICKUP, (PlayerEntity)AutoArmor.mc.player);
                AutoArmor.mc.interactionManager.clickSlot(AutoArmor.mc.player.currentScreenHandler.syncId, armorSlot, 0, SlotActionType.PICKUP, (PlayerEntity)AutoArmor.mc.player);
                if (((int[])equipmentSlotEntry.getValue())[1] != -1) {
                    AutoArmor.mc.interactionManager.clickSlot(AutoArmor.mc.player.currentScreenHandler.syncId, newArmorSlot, 0, SlotActionType.PICKUP, (PlayerEntity)AutoArmor.mc.player);
                }
                EntityUtil.sync();
            }
            return;
        }
    }

    private int getProtection(ItemStack is) {
        if (is.getItem() instanceof ArmorItem || is.getItem() == Items.ELYTRA) {
            int prot = 0;
            if (is.getItem() instanceof ElytraItem) {
                if (!ElytraItem.isUsable((ItemStack)is)) {
                    return 0;
                }
                prot = 1;
            }
            if (is.hasEnchantments()) {
                for (Map.Entry e : EnchantmentHelper.get((ItemStack)is).entrySet()) {
                    if (!(e.getKey() instanceof ProtectionEnchantment)) continue;
                    prot += ((Integer)e.getValue()).intValue();
                }
            }
            return (is.getItem() instanceof ArmorItem ? ((ArmorItem)is.getItem()).getProtection() : 0) + prot;
        }
        if (!is.isEmpty()) {
            return 0;
        }
        return -1;
    }
}

