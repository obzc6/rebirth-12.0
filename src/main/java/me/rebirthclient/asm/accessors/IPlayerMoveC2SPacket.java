/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package me.rebirthclient.asm.accessors;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={PlayerMoveC2SPacket.class})
public interface IPlayerMoveC2SPacket {
    @Mutable
    @Accessor(value="onGround")
    public void setOnGround(boolean var1);

    @Mutable
    @Accessor(value="pitch")
    public void setPitch(float var1);

    @Mutable
    @Accessor(value="yaw")
    public void setYaw(float var1);

    @Mutable
    @Accessor(value="y")
    public void setY(double var1);
}

