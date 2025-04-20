/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.PendingUpdateManager
 *  net.minecraft.client.world.ClientWorld
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package me.rebirthclient.asm.accessors;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ClientWorld.class})
public interface IClientWorld {
    @Accessor(value="pendingUpdateManager")
    public PendingUpdateManager acquirePendingUpdateManager();
}

