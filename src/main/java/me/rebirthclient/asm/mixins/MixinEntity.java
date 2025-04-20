/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.mod.modules.movement.Velocity;
import me.rebirthclient.mod.modules.render.NoInvisible;
import me.rebirthclient.mod.modules.render.NoRender;
import me.rebirthclient.mod.modules.render.ShaderChams;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value={Entity.class})
public class MixinEntity {
    @Inject(at={@At(value="HEAD")}, method={"isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z"}, cancellable=true)
    private void onIsInvisibleCheck(PlayerEntity message, CallbackInfoReturnable<Boolean> cir) {
        if (NoInvisible.INSTANCE.isOn()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method={"isGlowing"}, at={@At(value="HEAD")}, cancellable=true)
    void isGlowingHook(CallbackInfoReturnable<Boolean> cir) {
        if (ShaderChams.INSTANCE.isOn()) {
            cir.setReturnValue(ShaderChams.INSTANCE.shouldRender((Entity) (Object) this));
        }
    }

    @ModifyArgs(method={"pushAwayFrom"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void pushAwayFromHook(Args args) {
        if ((Object)this == MinecraftClient.getInstance().player) {
            double value = 1.0;
            if (Velocity.INSTANCE.isOn() && Velocity.INSTANCE.entityPush.getValue()) {
                value = 0.0;
            }
            args.set(0, (Object)((Double)args.get(0) * value));
            args.set(1, (Object)((Double)args.get(1) * value));
            args.set(2, (Object)((Double)args.get(2) * value));
        }
    }

    @Inject(method={"isOnFire"}, at={@At(value="HEAD")}, cancellable=true)
    void isOnFireHook(CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.INSTANCE.isOn() && NoRender.INSTANCE.fireEntity.getValue()) {
            cir.setReturnValue(false);
        }
    }
}

