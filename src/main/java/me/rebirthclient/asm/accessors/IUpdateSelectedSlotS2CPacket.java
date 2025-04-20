/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package me.rebirthclient.asm.accessors;

import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={UpdateSelectedSlotS2CPacket.class})
public interface IUpdateSelectedSlotS2CPacket {
    @Mutable
    @Accessor(value="selectedSlot")
    public void setslot(int var1);
}

