/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.particle.ParticleEffect
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.impl.ParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ParticleManager.class})
public class MixinParticleManager {
    @Inject(at={@At(value="HEAD")}, method={"addParticle(Lnet/minecraft/client/particle/Particle;)V"}, cancellable=true)
    public void onAddParticle(Particle particle, CallbackInfo ci) {
        ParticleEvent.AddParticle event = new ParticleEvent.AddParticle(particle);
        Rebirth.EVENT_BUS.post(event);
        if (event.isCancel()) {
            ci.cancel();
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;)V"}, cancellable=true)
    public void onAddEmmiter(Entity entity, ParticleEffect particleEffect, CallbackInfo ci) {
        ParticleEvent.AddEmmiter event = new ParticleEvent.AddEmmiter(particleEffect);
        Rebirth.EVENT_BUS.post(event);
        if (event.isCancel()) {
            ci.cancel();
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V"}, cancellable=true)
    public void onAddEmmiterAged(Entity entity, ParticleEffect particleEffect, int maxAge, CallbackInfo ci) {
        ParticleEvent.AddEmmiter event = new ParticleEvent.AddEmmiter(particleEffect);
        Rebirth.EVENT_BUS.post(event);
        if (event.isCancel()) {
            ci.cancel();
        }
    }
}

