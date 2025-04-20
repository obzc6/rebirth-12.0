/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.ChatHud
 *  net.minecraft.client.gui.hud.ChatHudLine
 *  net.minecraft.client.gui.hud.ChatHudLine$Visible
 *  net.minecraft.client.gui.hud.MessageIndicator
 *  net.minecraft.network.message.MessageSignatureData
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.Text
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import java.util.HashMap;
import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.interfaces.IChatHud;
import me.rebirthclient.api.interfaces.IChatHudLine;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.mod.modules.client.Chat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChatHud.class})
public abstract class MixinChatHud
implements IChatHud {
    @Shadow
    @Final
    private List<ChatHudLine.Visible> visibleMessages;
    @Shadow
    @Final
    private List<ChatHudLine> messages;
    @Unique
    private int nextId = 0;
    @Unique
    private final HashMap<ChatHudLine.Visible, FadeUtils> map = new HashMap();
    @Unique
    private ChatHudLine.Visible last;

    @Shadow
    public abstract void addMessage(Text var1);

    @Override
    public void rebirth_nextgen_master$add(Text message, int id) {
        this.nextId = id;
        this.addMessage(message);
        this.nextId = 0;
    }

    @Inject(method={"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"}, at={@At(value="INVOKE", target="Ljava/util/List;add(ILjava/lang/Object;)V", ordinal=0, shift=At.Shift.AFTER)})
    private void onAddMessageAfterNewChatHudLineVisible(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo info) {
        ((IChatHudLine)(Object)this.visibleMessages.get(0)).rebirth_nextgen_master$setId(this.nextId);
    }

    @Inject(method={"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"}, at={@At(value="INVOKE", target="Ljava/util/List;add(ILjava/lang/Object;)V", ordinal=1, shift=At.Shift.AFTER)})
    private void onAddMessageAfterNewChatHudLine(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo info) {
        ((IChatHudLine)(Object)this.messages.get(0)).rebirth_nextgen_master$setId(this.nextId);
    }

    @Inject(at={@At(value="HEAD")}, method={"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"})
    private void onAddMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo info) {
        if (this.nextId != 0) {
            this.visibleMessages.removeIf(msg -> msg == null || ((IChatHudLine)(Object)msg).rebirth_nextgen_master$getId() == this.nextId);
            this.messages.removeIf(msg -> msg == null || ((IChatHudLine)(Object)msg).rebirth_nextgen_master$getId() == this.nextId);
        }
    }

    @Redirect(method={"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"}, at=@At(value="INVOKE", target="Ljava/util/List;size()I", ordinal=2, remap=false))
    public int chatLinesSize(List<ChatHudLine.Visible> list) {
        return Chat.INSTANCE.isOn() && Chat.INSTANCE.infiniteChat.getValue() ? -2147483647 : list.size();
    }

    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"))
    private int drawStringWithShadow(DrawContext drawContext, TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
        if (Chat.chatMessage.containsKey((Object)text) && Chat.chatMessage.get((Object)text).getString().startsWith(CommandManager.syncCode)) {
            return drawContext.drawTextWithShadow(textRenderer, text, x, y, ColorUtil.injectAlpha(Rebirth.HUD.getColor(), color >> 24 & 0xFF).getRGB());
        }
        return drawContext.drawTextWithShadow(textRenderer, text, x, y, color);
    }

    @ModifyArg(method={"render"}, at=@At(value="INVOKE", target="Ljava/util/List;get(I)Ljava/lang/Object;", ordinal=0, remap=false))
    private int get(int i) {
        this.last = this.visibleMessages.get(i);
        if (this.last != null && !this.map.containsKey((Object)this.last)) {
            this.map.put(this.last, new FadeUtils(Chat.INSTANCE.animateTime.getValueInt()).reset());
        }
        return i;
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I", ordinal=0, shift=At.Shift.BEFORE)})
    private void translate(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.map.containsKey((Object)this.last)) {
            context.getMatrices().translate(Chat.INSTANCE.animateOffset.getValue() * (1.0 - this.map.get((Object)this.last).easeOutQuad()), 0.0, 0.0);
        }
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal=1)})
    public void TranslateAgain(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (Chat.INSTANCE.ChatMove.getValue()) {
            context.getMatrices().translate(Chat.INSTANCE.X.getValue(), -1.0 * Chat.INSTANCE.Y.getValue(), 0.0);
        }
    }
}

