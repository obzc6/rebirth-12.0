/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 */
package me.rebirthclient.mod.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Random;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.gui.particle.Snow;
import me.rebirthclient.mod.gui.tabs.Tab;
import me.rebirthclient.mod.modules.client.ClickGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ClickGuiScreen
extends Screen
implements Wrapper {
    private final ArrayList<Snow> snow = new ArrayList();
    public static boolean clicked = false;
    public static boolean rightClicked = false;
    public static boolean hoverClicked = false;
    public Timer byd = new Timer();
    public int xPlus = 0;
    private final Identifier bg = new Identifier("rebirth", "shlt/byd1.png");
    private final Identifier gun = new Identifier("rebirth", "shlt/gun.png");
    private final Identifier cat = new Identifier("rebirth", "shlt/madcat.png");
    private final Identifier little = new Identifier("rebirth", "shlt/little_byd.png");
    private final Identifier ljl = new Identifier("rebirth", "shlt/ljl.png");
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;

    public ClickGuiScreen() {
        super(Text.of((String)"ClickGui"));
    }

    protected void init() {
        this.byd.reset();
        this.xPlus = 0;
        if (mc.getWindow() != null) {
            this.updateResolution();
        }
        super.init();
    }

    public boolean shouldPause() {
        return false;
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, partialTicks);
        if (ClickGui.INSTANCE.snow.getValue()) {
            this.snow.forEach(snow -> snow.drawSnow(drawContext));
        }
        Rebirth.HUD.draw(drawContext, partialTicks);
        if (mc.getWindow() != null) {
            this.updateResolution();
        }
        if (this.byd.passedMs(1L) && this.xPlus <= 200) {
            this.xPlus += 4;
            this.byd.reset();
        }
        if (ClickGui.INSTANCE.showBYD.getValue()) {
            RenderSystem.texParameter((int)3553, (int)10240, (int)9729);
            if (ClickGui.INSTANCE.bydMode.getValue() == ClickGui.BYDMode.hxy) {
                drawContext.drawTexture(this.bg, mc.getWindow().getScaledWidth() - this.xPlus, mc.getWindow().getScaledHeight() - 260, 0.0f, 0.0f, 220, 220, 220, 220);
            }
            if (ClickGui.INSTANCE.bydMode.getValue() == ClickGui.BYDMode.madcat) {
                drawContext.drawTexture(this.cat, mc.getWindow().getScaledWidth() - this.xPlus, mc.getWindow().getScaledHeight() - 260, 0.0f, 0.0f, 220, 220, 220, 220);
            }
            if (ClickGui.INSTANCE.bydMode.getValue() == ClickGui.BYDMode.little_byd) {
                drawContext.drawTexture(this.little, mc.getWindow().getScaledWidth() - this.xPlus, mc.getWindow().getScaledHeight() - 260, 0.0f, 0.0f, 220, 220, 220, 220);
            }
            if (ClickGui.INSTANCE.bydMode.getValue() == ClickGui.BYDMode.gun) {
                drawContext.drawTexture(this.gun, mc.getWindow().getScaledWidth() - this.xPlus, mc.getWindow().getScaledHeight() - 260, 0.0f, 0.0f, 220, 220, 220, 220);
            }
            if (ClickGui.INSTANCE.bydMode.getValue() == ClickGui.BYDMode.LiJiaLe) {
                drawContext.drawTexture(this.ljl, mc.getWindow().getScaledWidth() - this.xPlus, mc.getWindow().getScaledHeight() - 260, 0.0f, 0.0f, 220, 220, 220, 220);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            hoverClicked = false;
            clicked = true;
        } else if (button == 1) {
            rightClicked = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void updateResolution() {
        this.scaledWidth = mc.getWindow().getFramebufferWidth();
        this.scaledHeight = mc.getWindow().getFramebufferHeight();
        this.scaleFactor = 1;
        boolean flag = mc.getWindow().getScaleFactor() > 1.0;
        int i = (Integer)ClickGuiScreen.mc.options.getGuiScale().getValue();
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

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            clicked = false;
            hoverClicked = false;
        } else if (button == 1) {
            rightClicked = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void close() {
        super.close();
        rightClicked = false;
        hoverClicked = false;
        clicked = false;
    }

    public void onDisplayed() {
        super.onDisplayed();
        this.snow.clear();
        Random random = new Random();
        for (int i = 0; i < 100; ++i) {
            for (int y = 0; y < 3; ++y) {
                Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                this.snow.add(snow);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Tab tab : Rebirth.HUD.tabs) {
            tab.setY((int)((double)tab.getY() + amount * 30.0));
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}

