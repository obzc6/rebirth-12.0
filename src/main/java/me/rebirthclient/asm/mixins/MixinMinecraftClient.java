/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.RunArgs
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.util.Window
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.thread.ReentrantThreadExecutor
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.shaders.WindowResizeCallback;
import me.rebirthclient.mod.gui.font.FontRenderers;
import me.rebirthclient.mod.modules.client.Title;
import me.rebirthclient.mod.modules.player.MultiTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MinecraftClient.class})
public abstract class MixinMinecraftClient
extends ReentrantThreadExecutor<Runnable> {
    @Shadow
    @Final
    private Window window;
    @Shadow
    public int attackCooldown;
    @Shadow
    public ClientPlayerEntity player;
    @Shadow
    public HitResult crosshairTarget;
    @Shadow
    public ClientPlayerInteractionManager interactionManager;
    @Final
    @Shadow
    public ParticleManager particleManager;
    @Shadow
    public ClientWorld world;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    void postWindowInit(RunArgs args, CallbackInfo ci) {
        try {
            FontRenderers.Arial = FontRenderers.createArial(15.0f);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method={"onResolutionChanged"}, at={@At(value="TAIL")})
    private void captureResize(CallbackInfo ci) {
        ((WindowResizeCallback)WindowResizeCallback.EVENT.invoker()).onResized((MinecraftClient)(Object)this, this.window);
    }

    @Inject(method={"handleBlockBreaking"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleBlockBreaking(boolean breaking, CallbackInfo ci) {
        if (this.attackCooldown <= 0 && this.player.isUsingItem() && MultiTask.INSTANCE.isOn()) {
            if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                Direction direction;
                BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!this.world.getBlockState(blockPos).isAir() && this.interactionManager.updateBlockBreakingProgress(blockPos, direction = blockHitResult.getSide())) {
                    this.particleManager.addBlockBreakingParticles(blockPos, direction);
                    this.player.swingHand(Hand.MAIN_HAND);
                }
            } else {
                this.interactionManager.cancelBlockBreaking();
            }
            ci.cancel();
        }
    }

    public MixinMinecraftClient(String string) {
        super(string);
    }

    @Inject(at={@At(value="HEAD")}, method={"tick()V"})
    public void tick(CallbackInfo info) {
        if (this.world != null) {
            Rebirth.update();
        }
        Rebirth.RUN.run();
        Rebirth.PINGSPOOF.run();
        Title.updateTitle();
    }

    @Inject(method={"updateWindowTitle"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/util/Window;setTitle(Ljava/lang/String;)V")}, cancellable=true)
    private void setTitle(CallbackInfo cl) {
        if (Title.INSTANCE.isOn()) {
            cl.cancel();
        }
    }
}

