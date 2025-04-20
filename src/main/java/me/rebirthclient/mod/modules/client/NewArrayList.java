/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
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
import me.rebirthclient.api.util.FadeUtil;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

public class NewArrayList
extends Module {
    public static NewArrayList INSTANCE;
    private final SliderSetting height = this.add(new SliderSetting("Height", 0, 0, 10));
    private final SliderSetting textOffset = this.add(new SliderSetting("TextOffset", 0, 0, 10));
    private final SliderSetting xOffset = this.add(new SliderSetting("XOffset", 0, -500, 500));
    private final SliderSetting yOffset = this.add(new SliderSetting("YOffset", 10, -500, 500));
    public SliderSetting animSpeed = this.add(new SliderSetting("AnimSpeed", 0.2, 0.01, 1.0, 0.01));
    private final BooleanSetting forgeHax = this.add(new BooleanSetting("ForgeHax", true));
    private final BooleanSetting space = this.add(new BooleanSetting("Space", true));
    private final BooleanSetting down = this.add(new BooleanSetting("Down", false));
    private final BooleanSetting animY = this.add(new BooleanSetting("AnimY", true));
    private final BooleanSetting onlyBind = this.add(new BooleanSetting("OnlyBind", true));
    private final EnumSetting colorMode = this.add(new EnumSetting("ColorMode", ColorMode.Pulse));
    private final SliderSetting rainbowSpeed = this.add(new SliderSetting("RainbowSpeed", 200, 1, 400, v -> this.colorMode.getValue() == ColorMode.Rainbow || this.colorMode.getValue() == ColorMode.PulseRainbow));
    private final SliderSetting saturation = this.add(new SliderSetting("Saturation", 130.0, 1.0, 255.0, v -> this.colorMode.getValue() == ColorMode.Rainbow || this.colorMode.getValue() == ColorMode.PulseRainbow));
    private final SliderSetting pulseSpeed = this.add(new SliderSetting("PulseSpeed", 100, 1, 400, v -> this.colorMode.getValue() == ColorMode.Pulse || this.colorMode.getValue() == ColorMode.PulseRainbow));
    private final SliderSetting pulseCounter = this.add(new SliderSetting("PulseCounter", 1, 1, 10, v -> this.colorMode.getValue() == ColorMode.Pulse || this.colorMode.getValue() == ColorMode.PulseRainbow));
    private final SliderSetting rainbowDelay = this.add(new SliderSetting("Delay", 350, 0, 600, v -> this.colorMode.getValue() == ColorMode.Rainbow));
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 255), v -> this.colorMode.getValue() != ColorMode.Rainbow));
    private final BooleanSetting rect = this.add(new BooleanSetting("Rect", true));
    private final BooleanSetting backGround = this.add(new BooleanSetting("BackGround", true).setParent());
    private final BooleanSetting bgSync = this.add(new BooleanSetting("Sync", false, v -> this.backGround.isOpen()));
    private final ColorSetting bgColor = this.add(new ColorSetting("BGColor", new Color(0, 0, 0, 100), v -> this.backGround.isOpen()));
    private List<Modules> modulesList = new ArrayList<Modules>();
    boolean update;
    private final Timer timer = new Timer();
    int progress = 0;
    int pulseProgress = 0;
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;

    public NewArrayList() {
        super("NewArrayList", "", Module.Category.Client);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.modulesList.clear();
        for (Module module : Rebirth.MODULE.modules) {
            this.modulesList.add(new Modules(module));
        }
    }

    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        int startY;
        if (mc.getWindow() == null) {
            return;
        }
        this.updateResolution();
        for (Modules modules : this.modulesList) {
            modules.update();
        }
        if (this.update) {
            this.modulesList = this.modulesList.stream().sorted(Comparator.comparing(module -> this.getStringWidth(module.name) * -1)).collect(Collectors.toList());
            this.update = false;
        }
        if (this.timer.passed(50L)) {
            this.progress -= this.rainbowSpeed.getValueInt();
            this.pulseProgress -= this.pulseSpeed.getValueInt();
            this.timer.reset();
        }
        int lastY = startY = this.down.getValue() ? this.scaledHeight - this.yOffset.getValueInt() - this.getFontHeight() : this.yOffset.getValueInt() + 1;
        int counter = 20;
        for (Modules modules : this.modulesList) {
            if (this.onlyBind.getValue() && modules.module.getBind().getKey() == -1) {
                modules.hide = true;
                continue;
            }
            if (!modules.module.getDraw()) {
                modules.hide = true;
                continue;
            }
            if (modules.module.isOn()) {
                modules.enable();
            } else {
                modules.disable();
            }
            if (modules.isEnabled) {
                modules.x = FadeUtil.animate(modules.x, this.getStringWidth(this.getSuffix(modules.name)), this.animSpeed.getValue());
            } else {
                modules.x = FadeUtil.animate(modules.x, -1.0, this.animSpeed.getValue());
                if (modules.x <= 0.0) {
                    modules.hide = true;
                    continue;
                }
            }
            if (modules.hide) {
                modules.y = this.animY.getValue() ? (double)startY : (double)lastY;
                modules.hide = false;
            }
            modules.y = FadeUtil.animate(modules.y, this.animY.getValue() && !modules.isEnabled ? (double)startY : (double)lastY, this.animSpeed.getValue()) + 1.0;
            counter += this.pulseCounter.getValueInt();
            int textX = (int)((double)(this.scaledWidth / 2) - modules.x - this.xOffset.getValue() - (double)(this.rect.getValue() ? 2 : 0));
            if (this.backGround.getValue()) {
                Render2DUtil.drawRect(drawContext.getMatrices(), (float)(textX - 1), (float)((int)modules.y), (float)this.scaledWidth / 2.0f - (float)this.xOffset.getValueInt() + 1.0f - (float)textX + 1.0f, (float)(this.getFontHeight() + this.height.getValueInt()), this.bgSync.getValue() ? ColorUtil.injectAlpha(this.getColor(counter), this.bgColor.getValue().getAlpha()) : this.bgColor.getValue().getRGB());
            }
            if (this.rect.getValue()) {
                Render2DUtil.drawRect(drawContext.getMatrices(), (float)this.scaledWidth / 2.0f - (float)this.xOffset.getValueInt() - 1.0f, (float)((int)modules.y), 1.0f, (float)(this.getFontHeight() + this.height.getValueInt()), this.getColor(counter));
            }
            drawContext.drawTextWithShadow(NewArrayList.mc.textRenderer, this.getSuffix(modules.name), textX, (int)(modules.y + 1.0 + (double)this.textOffset.getValueInt()), this.getColor(counter));
            if (this.down.getValue()) {
                lastY -= this.getFontHeight() + this.height.getValueInt();
                continue;
            }
            lastY += this.getFontHeight() + this.height.getValueInt();
        }
    }

    private String getSuffix(String s) {
        if (this.forgeHax.getValue()) {
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

    public void updateResolution() {
        this.scaledWidth = mc.getWindow().getFramebufferWidth();
        this.scaledHeight = mc.getWindow().getFramebufferHeight();
        this.scaleFactor = 1;
        boolean flag = mc.getWindow().getScaleFactor() > 1.0;
        int i = (Integer)NewArrayList.mc.options.getGuiScale().getValue();
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        double scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
        double scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
        this.scaledWidth = MathHelper.ceil((double)scaledWidthD);
        this.scaledHeight = MathHelper.ceil((double)scaledHeightD);
    }

    private int getStringWidth(String text) {
        return NewArrayList.mc.textRenderer.getWidth(text);
    }

    private int getFontHeight() {
        Objects.requireNonNull(NewArrayList.mc.textRenderer);
        return 9;
    }

    private static enum ColorMode {
        Custom,
        Pulse,
        Rainbow,
        PulseRainbow;

        // $FF: synthetic method
        private static NewArrayList.ColorMode[] $values() {
            return new NewArrayList.ColorMode[]{Custom, Pulse, Rainbow, PulseRainbow};
        }
    }
    public class Modules {
        public boolean isEnabled = false;
        public final Module module;
        public double x = 0.0;
        public double y = 0.0;
        public boolean hide = true;
        public String lastName = "";
        public String name = "";

        public Modules(Module module) {
            this.module = module;
        }

        public void enable() {
            if (this.isEnabled) {
                return;
            }
            this.isEnabled = true;
        }

        public void disable() {
            if (!this.isEnabled) {
                return;
            }
            this.isEnabled = false;
        }

        public void update() {
            Object name = this.module.getArrayName();
            if (!this.lastName.equals(name)) {
                this.lastName = (String) name;
                if (NewArrayList.this.space.getValue()) {
                    name = this.module.getName().replaceAll("([A-Z])([a-z])", " $1$2");
                    if (((String)name).startsWith(" ")) {
                        name = ((String)name).replaceFirst(" ", "");
                    }
                    name = (String)name + this.module.getArrayInfo();
                }
                this.name = (String) name;
                NewArrayList.this.update = true;
            }
        }
    }
}

