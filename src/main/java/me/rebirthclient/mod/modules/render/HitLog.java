/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.DeathEvent;
import me.rebirthclient.api.events.impl.TotemEvent;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class HitLog
extends Module {
    public static HitLog INSTANCE = new HitLog();
    private final ArrayList<Log> logList = new ArrayList();
    public SliderSetting animationSpeed = this.add(new SliderSetting("AnimationSpeed", 0.2, 0.01, 0.5, 0.01));
    public SliderSetting stayTime = this.add(new SliderSetting("StayTime", 1.0, 0.5, 5.0, 0.1));
    static double y = 0.0;

    public HitLog() {
        super("HitLog", Module.Category.Render);
        INSTANCE = this;
    }

    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        y = (double)mc.getWindow().getHeight() / 4.0 - 40.0;
        this.logList.removeIf(log -> log.timer.passed(this.stayTime.getValue() * 1000.0) && log.alpha <= 10.0);
        for (Log log2 : new ArrayList<Log>(this.logList)) {
            double d;
            boolean end = log2.timer.passed(this.stayTime.getValue() * 1000.0);
            log2.alpha = HitLog.animate(log2.alpha, end ? 0.0 : 255.0, this.animationSpeed.getValue());
            if (end) {
                double d2 = (double)mc.getWindow().getHeight() / 4.0 - 30.0;
                Objects.requireNonNull(HitLog.mc.textRenderer);
                d = d2 + 9.0;
            } else {
                d = y;
            }
            log2.y = HitLog.animate(log2.y, d, this.animationSpeed.getValue());
            drawContext.drawTextWithShadow(HitLog.mc.textRenderer, log2.text, (int)log2.x, (int)log2.y, new Color(255, 255, 255, (int)log2.alpha).getRGB());
            if (end) continue;
            Objects.requireNonNull(HitLog.mc.textRenderer);
            y -= (double)(9 + 2);
        }
    }

    @EventHandler
    public void onPlayerDeath(DeathEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player == HitLog.mc.player || player.distanceTo((Entity)HitLog.mc.player) > 20.0f) {
            return;
        }
        int popCount = Rebirth.POP.popContainer.getOrDefault(player.getName().getString(), 0);
        this.addLog("\u00a74\u00a7m" + player.getName().getString() + "\u00a7f " + popCount);
    }

    @EventHandler
    public void onTotem(TotemEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player == HitLog.mc.player || player.distanceTo((Entity)HitLog.mc.player) > 20.0f) {
            return;
        }
        int popCount = Rebirth.POP.popContainer.getOrDefault(player.getName().getString(), 1);
        this.addLog("\u00a7a" + player.getName().getString() + "\u00a7f " + popCount);
    }

    public void addLog(String text) {
        this.logList.add(new Log(text));
    }

    public static double animate(double current, double endPoint, double speed) {
        boolean shouldContinueAnimation = endPoint > current;
        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        if (Math.abs(factor) <= 0.001) {
            return endPoint;
        }
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    static class Log {
        final Timer timer = new Timer();
        final String text;
        double x;
        double y;
        double alpha;

        public Log(String text) {
            this.text = text;
            this.x = (double)Wrapper.mc.getWindow().getWidth() / 4.0;
            this.y = y - 20.0;
            this.alpha = 0.0;
        }
    }
}

