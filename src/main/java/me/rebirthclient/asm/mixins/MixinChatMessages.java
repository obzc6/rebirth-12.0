/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.util.ChatMessages
 *  net.minecraft.client.util.TextCollector
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Style
 *  net.minecraft.util.Language
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.rebirthclient.asm.mixins;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.rebirthclient.mod.modules.client.Chat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ChatMessages.class})
public class MixinChatMessages {
    @Final
    @Shadow
    private static OrderedText SPACES;

    @Shadow
    private static String getRenderedChatMessage(String message) {
        return "";
    }

    @Inject(method={"breakRenderedChatMessageLines"}, at={@At(value="HEAD")}, cancellable=true)
    private static void breakRenderedChatMessageLinesHook(StringVisitable message, int width, TextRenderer textRenderer, CallbackInfoReturnable<List<OrderedText>> cir) {
        TextCollector textCollector = new TextCollector();
        message.visit((style, messagex) -> {
            textCollector.add(StringVisitable.styled((String)MixinChatMessages.getRenderedChatMessage(messagex), (Style)style));
            return Optional.empty();
        }, Style.EMPTY);
        ArrayList list = Lists.newArrayList();
        textRenderer.getTextHandler().wrapLines(textCollector.getCombined(), width, Style.EMPTY, (text, lastLineWrapped) -> {
            OrderedText orderedText = Language.getInstance().reorder(text);
            OrderedText o = lastLineWrapped != false ? OrderedText.concat((OrderedText)SPACES, (OrderedText)orderedText) : orderedText;
            list.add(o);
            Chat.chatMessage.put(o, message);
        });
        cir.setReturnValue(list.isEmpty() ? Lists.newArrayList(OrderedText.EMPTY) : list);
    }
}

