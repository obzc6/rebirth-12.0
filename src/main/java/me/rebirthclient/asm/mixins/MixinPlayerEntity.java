/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.Vec3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.Event;
import me.rebirthclient.api.events.impl.TravelEvent;
import me.rebirthclient.api.util.Wrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerEntity.class})
public class MixinPlayerEntity
implements Wrapper {
    @Inject(method={"travel"}, at={@At(value="HEAD")}, cancellable=true)
    private void onTravelPre(Vec3d movementInput, CallbackInfo ci) {
        TravelEvent event = new TravelEvent(Event.Stage.Pre, (PlayerEntity)(Object)this);
        Rebirth.EVENT_BUS.post(event);
        if (event.isCancel()) {
            ci.cancel();
        }
    }

    @Inject(method={"travel"}, at={@At(value="RETURN")}, cancellable=true)
    private void onTravelPost(Vec3d movementInput, CallbackInfo ci) {
        TravelEvent event = new TravelEvent(Event.Stage.Post, (PlayerEntity)(Object)this);
        Rebirth.EVENT_BUS.post(event);
        if (event.isCancel()) {
            ci.cancel();
        }
    }
}

