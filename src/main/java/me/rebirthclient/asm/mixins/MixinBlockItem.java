/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.ItemPlacementContext
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.impl.PlaceBlockEvent;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockItem.class})
public class MixinBlockItem {
    @Inject(method={"place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z"}, at={@At(value="HEAD")})
    private void onPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> info) {
        if (context.getWorld().isClient) {
            Rebirth.EVENT_BUS.post(PlaceBlockEvent.get(context.getBlockPos(), state.getBlock()));
        }
    }
}

