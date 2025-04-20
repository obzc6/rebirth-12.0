/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.modules.client;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;

public class Title
extends Module {
    public static Title INSTANCE;
    public final BooleanSetting animation = this.add(new BooleanSetting("Animation", true).setParent());
    public final SliderSetting updateTime = this.add(new SliderSetting("updateTime", 300, 0, 1000, v -> this.animation.isOpen()));
    private static String title;
    private static final Timer updateTimer;
    private static int titleLength;
    private static int breakTimer;
    private static String lastTitle;
    private static boolean back;
    private static boolean original;

    public Title() {
        super("Title", Module.Category.Client);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        mc.getWindow().setTitle("Minecraft 1.20.1");
    }

    public static void updateTitle() {
        if (!original) {
            mc.getWindow().setTitle("Minecraft 1.20.1");
            original = true;
        }
        if (INSTANCE.isOff()) {
            return;
        }
        title = (Rebirth.HUD.hackName.getValue() == HudManager.HackName.RebirthNew || Rebirth.HUD.hackName.getValue() == HudManager.HackName.MoonBladeNew ? "Rebirth" : Rebirth.getName()) + "1.8";
        if (lastTitle != null && !lastTitle.equals(title)) {
            updateTimer.reset();
            titleLength = 0;
            breakTimer = 0;
            back = false;
        }
        lastTitle = title;
        if (Title.INSTANCE.animation.getValue()) {
            if (lastTitle != null && updateTimer.passedMs(Title.INSTANCE.updateTime.getValue())) {
                updateTimer.reset();
                mc.getWindow().setTitle(lastTitle.substring(0, lastTitle.length() - titleLength));
                if (titleLength == lastTitle.length() && breakTimer != 2 || titleLength == 0 && breakTimer != 4) {
                    ++breakTimer;
                    return;
                }
                breakTimer = 0;
                if (titleLength == lastTitle.length()) {
                    back = true;
                }
                titleLength = back ? --titleLength : ++titleLength;
                if (titleLength == 0) {
                    back = false;
                }
            }
        } else {
            mc.getWindow().setTitle(lastTitle);
        }
    }

    static {
        updateTimer = new Timer();
        titleLength = 0;
        breakTimer = 0;
        lastTitle = null;
        back = false;
        original = false;
    }
}

