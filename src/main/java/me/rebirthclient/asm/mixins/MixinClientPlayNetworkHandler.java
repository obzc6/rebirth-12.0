/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayPingS2CPacket
 *  net.minecraft.text.Text
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.impl.SendMessageEvent;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.miscellaneous.SilentDisconnect;
import me.rebirthclient.mod.modules.player.PingSpoof;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayPingS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPlayNetworkHandler.class})
public abstract class MixinClientPlayNetworkHandler {
    @Unique
    private boolean ignoreChatMessage;

    @Shadow
    public abstract void sendChatMessage(String var1);

    @Inject(method={"sendChatMessage"}, at={@At(value="HEAD")}, cancellable=true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (this.ignoreChatMessage) {
            return;
        }
        if (message.startsWith(Rebirth.PREFIX)) {
            Rebirth.COMMAND.command(message.split(" "));
            ci.cancel();
        } else {
            SendMessageEvent event = new SendMessageEvent(message);
            Rebirth.EVENT_BUS.post(event);
            if (event.isCancel()) {
                ci.cancel();
            } else if (!event.message.equals(event.defaultMessage)) {
                this.ignoreChatMessage = true;
                this.sendChatMessage(event.message);
                this.ignoreChatMessage = false;
                ci.cancel();
            }
        }
    }

    @Inject(method={"onDisconnected"}, at={@At(value="HEAD")}, cancellable=true)
    private void onDisconnect(Text reason, CallbackInfo ci) {
        if (Wrapper.mc.player != null && Wrapper.mc.world != null && SilentDisconnect.INSTANCE.isOn()) {
            CommandManager.sendChatMessage("\u00a74[!] \u00a7cYou get kicked! reason: \u00a77" + reason.getString());
            ci.cancel();
        }
    }

    @Inject(method={"onKeepAlive"}, at={@At(value="HEAD")}, cancellable=true)
    private void keepAlive(KeepAliveS2CPacket packet, CallbackInfo ci) {
        if (!PingSpoof.INSTANCE.isOn() || !PingSpoof.INSTANCE.keepAlive.getValue()) {
            return;
        }
        ci.cancel();
        Rebirth.PINGSPOOF.addKeepAlive(packet.getId());
    }

    @Inject(method={"onPing"}, at={@At(value="HEAD")}, cancellable=true)
    private void ping(PlayPingS2CPacket packet, CallbackInfo ci) {
        if (!PingSpoof.INSTANCE.isOn() || !PingSpoof.INSTANCE.pong.getValue()) {
            return;
        }
        ci.cancel();
        Rebirth.PINGSPOOF.addPong(packet.getParameter());
    }
}

