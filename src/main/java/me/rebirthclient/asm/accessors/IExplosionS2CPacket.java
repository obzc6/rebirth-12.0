/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package me.rebirthclient.asm.accessors;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ExplosionS2CPacket.class})
public interface IExplosionS2CPacket {
    @Mutable
    @Accessor(value="playerVelocityX")
    public void setX(float var1);

    @Mutable
    @Accessor(value="playerVelocityY")
    public void setY(float var1);

    @Mutable
    @Accessor(value="playerVelocityZ")
    public void setZ(float var1);

    @Accessor(value="playerVelocityX")
    public float getX();

    @Accessor(value="playerVelocityY")
    public float getY();

    @Accessor(value="playerVelocityZ")
    public float getZ();
}

