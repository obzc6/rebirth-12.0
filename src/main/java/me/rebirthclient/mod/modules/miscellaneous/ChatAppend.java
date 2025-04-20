/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.miscellaneous;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.SendMessageEvent;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.EnumSetting;

public class ChatAppend
extends Module {
    public static ChatAppend INSTANCE;
    private final EnumSetting mode = this.add(new EnumSetting("Mode", Mode.Rebirth));
    public static String rebirthSuffix;
    public static String moonbladeSuffix;
    public static String melonSuffix;
    public static String MioSuffix;
    public static String IsolationSuffix;

    public ChatAppend() {
        super("ChatAppend", Module.Category.Miscellaneous);
        INSTANCE = this;
    }

    @EventHandler
    public void onSendMessage(SendMessageEvent event) {
        if (ChatAppend.nullCheck() || event.isCancel()) {
            return;
        }
        Object message = event.message;
        if (((String) message).startsWith("/") || ((String) message).startsWith("!") || ((String) message).endsWith(rebirthSuffix) || ((String) message).endsWith(moonbladeSuffix) || ((String) message).endsWith(melonSuffix)) {
            return;
        }
        String suffix = "";
        switch ((Mode) this.mode.getValue()) {
            case Rebirth: {
                suffix = rebirthSuffix;
                break;
            }
            case MoonBlade: {
                suffix = moonbladeSuffix;
                break;
            }
            case Melon: {
                suffix = melonSuffix;
                break;
            }
            case Mio: {
                suffix = MioSuffix;
            }
        }
        message = message + " " + suffix;
        event.message = (String) message;
    }

    static {
        rebirthSuffix = "\u2737\u211c\ud835\udd22\ud835\udd1f\ud835\udd26\ud835\udd2f\ud835\udd31\ud835\udd25";
        moonbladeSuffix = "\u263d\ud835\udd10\ud835\udd2c\ud835\udd2c\ud835\udd2b\ud835\udd1f\ud835\udd29\ud835\udd1e\ud835\udd21\ud835\udd22";
        melonSuffix = "\ud835\udd10\ud835\udd22\ud835\udd29\ud835\udd2c\ud835\udd2b\ud835\udd05\ud835\udd22\ud835\udd31\ud835\udd1e";
        MioSuffix = "\ud835\udde0\ud835\uddf6\ud835\uddfc";
        IsolationSuffix = "\u00a4\u0197s\u00f8\u026d\u03b1\u0442\u0e40\u0e4f\u0e20C";
    }

    private static enum Mode {
        Rebirth,
        MoonBlade,
        Melon,
        Mio,
        Isolation;

        // $FF: synthetic method
        private static ChatAppend.Mode[] $values() {
            return new ChatAppend.Mode[]{Rebirth, MoonBlade, Melon, Mio, Isolation};
        }
    }
}