/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.SwordItem
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.Criticals;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class AntiWeak
extends Module {
    private final SliderSetting delay = this.add(new SliderSetting("Delay", 100, 0, 500));
    private final EnumSetting swapMode = this.add(new EnumSetting("SwapMode", SwapMode.Bypass));
    private final BooleanSetting onlyCrystal = this.add(new BooleanSetting("OnlyCrystal", true));
    private final Timer delayTimer = new Timer();
    private PlayerInteractEntityC2SPacket lastPacket = null;
    boolean ignore = false;

    public AntiWeak() {
        super("AntiWeak", "anti weak", Module.Category.Combat);
    }

    @Override
    public String getInfo() {
        return this.swapMode.getValue().name();
    }

    @EventHandler(priority=-200)
    public void onPacketSend(PacketEvent.Send event) {
        PlayerInteractEntityC2SPacket packet;
        if (AntiWeak.nullCheck()) {
            return;
        }
        if (event.isCancel()) {
            return;
        }
        if (this.ignore) {
            return;
        }
        if (AntiWeak.mc.player.getStatusEffect(StatusEffects.WEAKNESS) == null) {
            return;
        }
        if (AntiWeak.mc.player.getMainHandStack().getItem() instanceof SwordItem) {
            return;
        }
        if (!this.delayTimer.passedMs(this.delay.getValue())) {
            return;
        }
        Object t = event.getPacket();
        if (t instanceof PlayerInteractEntityC2SPacket && Criticals.getInteractType(packet = (PlayerInteractEntityC2SPacket)t) == Criticals.InteractType.ATTACK) {
            if (this.onlyCrystal.getValue() && !(Criticals.getEntity(packet) instanceof EndCrystalEntity)) {
                return;
            }
            this.lastPacket = (PlayerInteractEntityC2SPacket)event.getPacket();
            this.delayTimer.reset();
            this.ignore = true;
            this.doAnti();
            this.ignore = false;
            event.cancel();
        }
    }

    private void doAnti() {
        if (this.lastPacket == null) {
            return;
        }
        int strong = this.swapMode.getValue() != SwapMode.Bypass ? InventoryUtil.findClass(SwordItem.class) : InventoryUtil.findClassInventorySlot(SwordItem.class);
        if (strong == -1) {
            return;
        }
        int old = AntiWeak.mc.player.getInventory().selectedSlot;
        if (this.swapMode.getValue() != SwapMode.Bypass) {
            InventoryUtil.doSwap(strong);
        } else {
            AntiWeak.mc.interactionManager.clickSlot(AntiWeak.mc.player.currentScreenHandler.syncId, strong, AntiWeak.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AntiWeak.mc.player);
        }
        AntiWeak.mc.player.networkHandler.sendPacket((Packet)this.lastPacket);
        if (this.swapMode.getValue() != SwapMode.Bypass) {
            if (this.swapMode.getValue() != SwapMode.Normal) {
                InventoryUtil.doSwap(old);
            }
        } else {
            AntiWeak.mc.interactionManager.clickSlot(AntiWeak.mc.player.currentScreenHandler.syncId, strong, AntiWeak.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)AntiWeak.mc.player);
            EntityUtil.sync();
        }
    }

    public static enum SwapMode {
        Normal,
        Silent,
        Bypass;

        // $FF: synthetic method
        private static AntiWeak.SwapMode[] $values() {
            return new AntiWeak.SwapMode[]{Normal, Silent, Bypass};
        }
    }
}
