/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.mod.modules.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;

public class ArrayListModule
extends Module {
    public static ArrayListModule INSTANCE;
    private final SliderSetting height = this.add(new SliderSetting("Height", 0, 0, 10));
    private final SliderSetting yOffset = this.add(new SliderSetting("FontOffset", 0, 0, 10));
    private final SliderSetting listX = this.add(new SliderSetting("X", 0, 0, 500));
    private final SliderSetting listY = this.add(new SliderSetting("Y", 10, 0, 500));
    private final SliderSetting animationTime = this.add(new SliderSetting("AnimationTime", 300, 0, 1000));
    private final BooleanSetting forgeHax = this.add(new BooleanSetting("ForgeHax", true));
    private final BooleanSetting reverse = this.add(new BooleanSetting("Reverse", false));
    private final BooleanSetting down = this.add(new BooleanSetting("Down", false));
    private final BooleanSetting onlyBind = this.add(new BooleanSetting("OnlyBind", true));
    private final BooleanSetting animationY = this.add(new BooleanSetting("AnimationY", true));
    private final EnumSetting colorMode = this.add(new EnumSetting("ColorMode", ColorMode.Pulse));
    private final SliderSetting rainbowSpeed = this.add(new SliderSetting("RainbowSpeed", 200, 1, 400, v -> this.colorMode.getValue() == ColorMode.Rainbow || this.colorMode.getValue() == ColorMode.PulseRainbow));
    private final SliderSetting saturation = this.add(new SliderSetting("Saturation", 130.0, 1.0, 255.0, v -> this.colorMode.getValue() == ColorMode.Rainbow || this.colorMode.getValue() == ColorMode.PulseRainbow));
    private final SliderSetting pulseSpeed = this.add(new SliderSetting("PulseSpeed", 100, 1, 400, v -> this.colorMode.getValue() == ColorMode.Pulse || this.colorMode.getValue() == ColorMode.PulseRainbow));
    private final SliderSetting rainbowDelay = this.add(new SliderSetting("Delay", 350, 0, 600, v -> this.colorMode.getValue() == ColorMode.Rainbow));
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 255), v -> this.colorMode.getValue() != ColorMode.Rainbow));
    private final BooleanSetting rect = this.add(new BooleanSetting("Rect", true));
    private final BooleanSetting backGround = this.add(new BooleanSetting("BackGround", true).setParent());
    private final BooleanSetting bgSync = this.add(new BooleanSetting("Sync", false, v -> this.backGround.isOpen()));
    private final ColorSetting bgColor = this.add(new ColorSetting("BGColor", new Color(0, 0, 0, 100), v -> this.backGround.isOpen()));
    private List<Modules> modulesList = new ArrayList<Modules>();
    boolean update = true;
    int progress = 0;
    int pulseProgress = 0;

    public ArrayListModule() {
        super("ArrayList", "", Module.Category.Client);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.update) {
            for (Module module2 : Rebirth.MODULE.modules) {
                this.modulesList.add(new Modules(module2));
            }
            this.modulesList = this.modulesList.stream().sorted(Comparator.comparing(module -> this.getStringWidth(module.module.getName()) * -1)).collect(Collectors.toList());
            this.update = false;
        }
        this.progress -= this.rainbowSpeed.getValueInt();
        this.pulseProgress -= this.pulseSpeed.getValueInt();
    }

    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        int lastY = this.down.getValue() ? mc.getWindow().getHeight() / 2 - this.listY.getValueInt() - this.getFontHeight() : this.listY.getValueInt();
        int counter = 20;
        for (Modules modules : this.modulesList) {
            int showX;
            int y;
            int x;
            double size;
            if (this.onlyBind.getValue() && modules.module.getBind().getKey() == -1) continue;
            modules.fade.setLength(this.animationTime.getValueInt());
            if (modules.module.isOn()) {
                modules.enable();
            } else {
                modules.disable();
            }
            if (!this.reverse.getValue()) {
                if (modules.isEnabled) {
                    size = Math.min(modules.fade.easeOutQuad(), 1.0);
                    x = (int)((double)this.getStringWidth(this.getSuffix(modules.module.getName())) * size);
                    modules.lastY = y = (int)((double)this.getFontHeight() * size);
                    modules.lastX = x;
                    modules.lastSize = size;
                } else {
                    size = 1.0 - Math.min(modules.fade.easeOutQuad(), 1.0);
                    x = (int)((double)modules.lastX * size);
                    y = (int)((double)modules.lastY * size);
                    if (size <= 0.0) {
                        continue;
                    }
                }
            } else if (modules.isEnabled) {
                size = Math.abs(modules.fade.easeOutQuad() - 1.0);
                x = (int)((double)this.getStringWidth(this.getSuffix(modules.module.getName())) * size);
                size = modules.fade.easeOutQuad();
                modules.lastY = y = (int)((double)this.getFontHeight() * size);
                modules.lastX = x;
            } else {
                size = modules.fade.easeOutQuad();
                x = (int)((double)this.getStringWidth(this.getSuffix(modules.module.getName())) * size) + modules.lastX;
                size = Math.abs(modules.fade.easeOutQuad() - 1.0);
                y = (int)((double)modules.lastY * size);
                if (size <= 0.0 || x >= this.getStringWidth(this.getSuffix(modules.module.getName()))) continue;
            }
            ++counter;
            if (!this.reverse.getValue()) {
                showX = mc.getWindow().getWidth() / 2 - x - this.listX.getValueInt() - (this.rect.getValue() ? 2 : 0);
                if (this.backGround.getValue()) {
                    Render2DUtil.drawRect(drawContext.getMatrices(), (float)(showX - 1), (float)(lastY - (this.animationY.getValue() ? Math.abs(y - this.getFontHeight()) : 0) - 1), (float)mc.getWindow().getWidth() / 2.0f - (float)this.listX.getValueInt() + 1.0f - (float)showX + 1.0f, (float)(this.getFontHeight() + this.height.getValueInt()), this.bgSync.getValue() ? ColorUtil.injectAlpha(this.getColor(counter), this.bgColor.getValue().getAlpha()) : this.bgColor.getValue().getRGB());
                }
                if (this.rect.getValue()) {
                    Render2DUtil.drawRect(drawContext.getMatrices(), (float)mc.getWindow().getWidth() / 2.0f - (float)this.listX.getValueInt() - 1.0f, (float)(lastY - (this.animationY.getValue() ? Math.abs(y - this.getFontHeight()) : 0) - 1), 1.0f, (float)(this.getFontHeight() + this.height.getValueInt()), this.getColor(counter));
                }
            } else {
                showX = -x + this.listX.getValueInt() + (this.rect.getValue() ? 2 : 0);
                if (this.backGround.getValue()) {
                    Render2DUtil.drawRect(drawContext.getMatrices(), (float)this.listX.getValueInt(), (float)(lastY - (this.animationY.getValue() ? Math.abs(y - this.getFontHeight()) : 0) - 1), (float)(Math.abs(x - this.getStringWidth(this.getSuffix(modules.module.getName()))) + (this.rect.getValue() ? 2 : 0) + 1), (float)(this.getFontHeight() + this.height.getValueInt()), this.bgSync.getValue() ? ColorUtil.injectAlpha(this.getColor(counter), this.bgColor.getValue().getAlpha()) : this.bgColor.getValue().getRGB());
                }
                if (this.rect.getValue()) {
                    Render2DUtil.drawRect(drawContext.getMatrices(), (float)this.listX.getValueInt(), (float)(lastY - (this.animationY.getValue() ? Math.abs(y - this.getFontHeight()) : 0) - 1), 1.0f, (float)(this.getFontHeight() + this.height.getValueInt()), this.getColor(counter));
                }
            }
            GL11.glEnable((int)3089);
            int tWidth = this.getStringWidth(this.getSuffix(modules.module.getName()));
            if (!this.reverse.getValue()) {
                GL11.glScissor((int)((mc.getWindow().getWidth() / 2 - tWidth - this.listX.getValueInt() - (this.rect.getValue() ? 2 : 0)) * 2), (int)0, (int)(tWidth * 2), (int)mc.getWindow().getHeight());
            } else {
                GL11.glScissor((int)(this.listX.getValueInt() * 2), (int)0, (int)(tWidth * 2 + 2), (int)mc.getWindow().getHeight());
            }
            drawContext.drawTextWithShadow(ArrayListModule.mc.textRenderer, this.getSuffix(modules.module.getName()), showX, lastY - (this.animationY.getValue() ? Math.abs(y - this.getFontHeight()) : 0) + this.yOffset.getValueInt(), this.getColor(counter));
            GL11.glDisable((int)3089);
            size = modules.isEnabled ? Math.min(modules.fade.easeOutQuad(), 1.0) : 1.0 - Math.min(modules.fade.easeOutQuad(), 1.0) * modules.lastSize;
            if (this.down.getValue()) {
                lastY = (int)((double)lastY - (double)(this.getFontHeight() + this.height.getValueInt()) * size);
                continue;
            }
            lastY = (int)((double)lastY + (double)(this.getFontHeight() + this.height.getValueInt()) * size);
        }
    }

    private String getSuffix(String s) {
        if (this.forgeHax.getValue()) {
            if (this.reverse.getValue()) {
                return "\u00a7r>" + s;
            }
            return s + "\u00a7r<";
        }
        return s;
    }

    private int getColor(int counter) {
        if (this.colorMode.getValue() != ColorMode.Custom) {
            return this.rainbow(counter).getRGB();
        }
        return this.color.getValue().getRGB();
    }

    private Color rainbow(int delay) {
        double rainbowState = Math.ceil(((double)this.progress + (double)delay * this.rainbowDelay.getValue()) / 20.0);
        if (this.colorMode.getValue() == ColorMode.Pulse) {
            return this.pulseColor(this.color.getValue(), delay);
        }
        if (this.colorMode.getValue() == ColorMode.Rainbow) {
            return Color.getHSBColor((float)(rainbowState % 360.0 / 360.0), this.saturation.getValueFloat() / 255.0f, 1.0f);
        }
        return this.pulseColor(Color.getHSBColor((float)(rainbowState % 360.0 / 360.0), this.saturation.getValueFloat() / 255.0f, 1.0f), delay);
    }

    private Color pulseColor(Color color, int index) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)((long)this.pulseProgress % 2000L) / Float.intBitsToFloat(Float.floatToIntBits(0.0013786979f) ^ 0x7ECEB56D) + (float)index / 14.0f * Float.intBitsToFloat(Float.floatToIntBits(0.09192204f) ^ 0x7DBC419F)) % Float.intBitsToFloat(Float.floatToIntBits(0.7858098f) ^ 0x7F492AD5) - Float.intBitsToFloat(Float.floatToIntBits(6.46708f) ^ 0x7F4EF252));
        brightness = Float.intBitsToFloat(Float.floatToIntBits(18.996923f) ^ 0x7E97F9B3) + Float.intBitsToFloat(Float.floatToIntBits(2.7958195f) ^ 0x7F32EEB5) * brightness;
        hsb[2] = brightness % Float.intBitsToFloat(Float.floatToIntBits(0.8992331f) ^ 0x7F663424);
        return ColorUtil.injectAlpha(new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2])), color.getAlpha());
    }

    private int getStringWidth(String text) {
        return ArrayListModule.mc.textRenderer.getWidth(text);
    }

    private int getFontHeight() {
        Objects.requireNonNull(ArrayListModule.mc.textRenderer);
        return 9;
    }

    private static enum ColorMode {
        Custom,
        Pulse,
        Rainbow,
        PulseRainbow;

        // $FF: synthetic method
        private static ArrayListModule.ColorMode[] $values() {
            return new ArrayListModule.ColorMode[]{Custom, Pulse, Rainbow, PulseRainbow};
        }
    }


    public static class Modules {
        public final FadeUtils fade;
        public boolean isEnabled = false;
        public final Module module;
        public int lastX = 0;
        public int lastY = 0;
        public double lastSize = 0.0;

        public Modules(Module module) {
            this.module = module;
            this.fade = new FadeUtils(500L).reset();
        }

        public void enable() {
            if (this.isEnabled) {
                return;
            }
            this.isEnabled = true;
            this.fade.reset();
        }

        public void disable() {
            if (!this.isEnabled) {
                return;
            }
            this.isEnabled = false;
            this.fade.reset();
        }
    }
}

