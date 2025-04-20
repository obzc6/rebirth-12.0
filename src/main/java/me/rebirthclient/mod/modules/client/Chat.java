/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 */
package me.rebirthclient.mod.modules.client;

import java.util.HashMap;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;

public class Chat
extends Module {
    public static Chat INSTANCE;
    public SliderSetting animateTime = this.add(new SliderSetting("AnimationTime", 300, 0, 1000));
    public SliderSetting animateOffset = this.add(new SliderSetting("AnimationOffset", -40, -200, 100));
    public BooleanSetting keepHistory = this.add(new BooleanSetting("KeepHistory", true));
    public BooleanSetting infiniteChat = this.add(new BooleanSetting("InfiniteChat", true));
    public BooleanSetting ChatMove = this.add(new BooleanSetting("ChatMove", false).setParent());
    public SliderSetting X = this.add(new SliderSetting("ChatX", 0, 0, 600, v -> this.ChatMove.isOpen()));
    public SliderSetting Y = this.add(new SliderSetting("ChatY", 0, 0, 600, v -> this.ChatMove.isOpen()));
    public static HashMap<OrderedText, StringVisitable> chatMessage;

    public Chat() {
        super("Chat", Module.Category.Client);
        INSTANCE = this;
    }

    @Override
    public void enable() {
        this.state = true;
    }

    @Override
    public void disable() {
        this.state = true;
    }

    @Override
    public boolean isOn() {
        return true;
    }

    static {
        chatMessage = new HashMap();
    }
}

