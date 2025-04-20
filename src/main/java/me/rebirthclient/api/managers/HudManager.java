/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.api.managers;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.mod.Mod;
import me.rebirthclient.mod.gui.components.impl.ModuleComponent;
import me.rebirthclient.mod.gui.elements.ArmorHUD;
import me.rebirthclient.mod.gui.elements.IngameGUI;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.gui.tabs.OptionsTab;
import me.rebirthclient.mod.gui.tabs.Tab;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.client.ClickGui;
import me.rebirthclient.mod.settings.Placement;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.SwingMode;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import static me.rebirthclient.mod.modules.client.ClickGui.Mode.*;

public class HudManager
extends Mod {
    public ArrayList<ClickGuiTab> tabs = new ArrayList();
    public static ClickGuiScreen clickGui = new ClickGuiScreen();
    public static IngameGUI hud;
    public ArmorHUD armorHud;
    public static Tab currentGrabbed;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private int mouseX;
    private int mouseY;
    public OptionsTab optionsTab;
    private final EnumSetting page;
    public BooleanSetting rotatePlus;
    public BooleanSetting rotations;
    public BooleanSetting attackRotate;
    public EnumSetting placement;
    public SliderSetting rotateTime;
    public SliderSetting attackDelay;
    public BooleanSetting inventorySync;
    public EnumSetting swingMode;
    public BooleanSetting obsMode;
    public final BooleanSetting render;
    public final EnumSetting rendermode;
    public final EnumSetting glidemode;
    public final BooleanSetting box;
    public final BooleanSetting outline;
    public final BooleanSetting through;
    public final ColorSetting color;
    public final ColorSetting coloroutline;
    public final SliderSetting fadeTime;
    public final EnumSetting hackName;
    public BooleanSetting tabGui;
    public BooleanSetting onlyBind;
    public BooleanSetting ah;

    public HudManager() {
        super("HudManager");
        ModuleManager.lastLoadMod = this;
        this.page = new EnumSetting("Page", Pages.Combat);
        this.hackName = new EnumSetting("HackName", HackName.RebirthNew, v -> this.page.getValue() == Pages.HUD);
        this.tabGui = new BooleanSetting("TabGui", true, v -> this.page.getValue() == Pages.HUD);
        this.onlyBind = new BooleanSetting("OnlyBind", false, v -> this.page.getValue() == Pages.HUD);
        this.ah = new BooleanSetting("ArmorHUD", true, v -> this.page.getValue() == Pages.HUD);
        this.attackRotate = new BooleanSetting("AttackRotate", false, v -> this.page.getValue() == Pages.Combat);
        this.rotations = new BooleanSetting("ShowRotations", true, v -> this.page.getValue() == Pages.Combat);
        this.placement = new EnumSetting("Placement", Placement.Vanilla, v -> this.page.getValue() == Pages.Combat);
        this.rotateTime = new SliderSetting("RotateTime", 0.5, 0.0, 1.0, 0.01, v -> this.page.getValue() == Pages.Combat);
        this.attackDelay = new SliderSetting("AttackDelay", 0.2, 0.0, 1.0, 0.01, v -> this.page.getValue() == Pages.Combat);
        this.inventorySync = new BooleanSetting("InventorySync", false, v -> this.page.getValue() == Pages.Combat);
        this.swingMode = new EnumSetting("SwingMode", SwingMode.Server, v -> this.page.getValue() == Pages.Combat);
        this.obsMode = new BooleanSetting("OBSServer", false, v -> this.page.getValue() == Pages.Combat);
        this.rotatePlus = new BooleanSetting("RotateSync", true, v -> this.page.getValue() == Pages.Combat);
        this.render = new BooleanSetting("PlaceRender", true, v -> this.page.getValue() == Pages.Render);
        this.rendermode = new EnumSetting("RenderMode", RenderMode.Fade, v -> this.page.getValue() == Pages.Render);
        this.glidemode = new EnumSetting("GlideMode", GlideMode.InToOut, v -> this.page.getValue() == Pages.Render && this.rendermode.getValue() == RenderMode.Glide);
        this.box = new BooleanSetting("Box", true, v -> this.render.getValue() && this.page.getValue() == Pages.Render);
        this.outline = new BooleanSetting("Outline", false, v -> this.render.getValue() && this.page.getValue() == Pages.Render);
        this.through = new BooleanSetting("Through", false, v -> this.render.getValue() && this.page.getValue() == Pages.Render && this.rendermode.getValue() == RenderMode.Glide);
        this.color = new ColorSetting("Color", new Color(255, 255, 255, 100), v -> this.render.getValue() && this.page.getValue() == Pages.Render);
        this.coloroutline = new ColorSetting("OutlineColor", new Color(255, 255, 255, 100), v -> this.render.getValue() && this.page.getValue() == Pages.Render && this.rendermode.getValue() == RenderMode.Glide);
        this.fadeTime = new SliderSetting("FadeTime", 500, 0, 5000, v -> this.render.getValue() && this.page.getValue() == Pages.Render);
        hud = new IngameGUI();
        this.armorHud = new ArmorHUD();
        this.optionsTab = new OptionsTab("Options", 10, 500);
        try {
            for (Field field : HudManager.class.getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType())) continue;
                Setting setting = (Setting)field.get(this);
                this.optionsTab.addChild(setting);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.tabs.add(this.optionsTab);
        int xOffset = 200;
        for (Module.Category category : Module.Category.values()) {
            ClickGuiTab tab = new ClickGuiTab(category, xOffset, 1);
            for (Module module : Rebirth.MODULE.modules) {
                if (module.getCategory() != category) continue;
                ModuleComponent button = new ModuleComponent(module.getName(), tab, module);
                tab.addChild(button);
            }
            this.tabs.add(tab);
            xOffset += tab.getWidth() + 20;
        }
    }

    public Color getColor() {
        return ClickGui.INSTANCE.color.getValue();
    }

    public void update() {
        if (this.isClickGuiOpen()) {
            for (ClickGuiTab tab : this.tabs) {
                tab.update(this.mouseX, this.mouseY, ClickGuiScreen.clicked);
            }
            if (this.ah.getValue()) {
                this.armorHud.update(this.mouseX, this.mouseY, ClickGuiScreen.clicked);
            }
        }
    }

    public void draw(DrawContext drawContext, float tickDelta) {
        MatrixStack matrixStack = drawContext.getMatrices();
        boolean mouseClicked = ClickGuiScreen.clicked;
        this.mouseX = (int)Math.ceil(HudManager.mc.mouse.getX());
        this.mouseY = (int)Math.ceil(HudManager.mc.mouse.getY());
        hud.update(this.mouseX, this.mouseY, mouseClicked);
        if (this.isClickGuiOpen()) {
            int dx = (int)((double)this.mouseX);
            int dy = (int)((double)this.mouseY);
            if (!mouseClicked) {
                currentGrabbed = null;
            }
            if (currentGrabbed != null) {
                currentGrabbed.moveWindow(this.lastMouseX - dx, this.lastMouseY - dy);
            }
            this.lastMouseX = dx;
            this.lastMouseY = dy;
        }
        GL11.glDisable((int)2884);
        GL11.glBlendFunc((int)770, (int)771);
        hud.draw(drawContext, tickDelta, this.getColor());
        matrixStack.push();
        matrixStack.scale(1.0f / (float)((Integer)HudManager.mc.options.getGuiScale().getValue()).intValue(), 1.0f / (float)((Integer)HudManager.mc.options.getGuiScale().getValue()).intValue(), 1.0f);
        if (this.ah.getValue()) {
            this.armorHud.draw(drawContext, tickDelta, this.getColor());
        }
        if (this.isClickGuiOpen()) {
            double quad = ClickGui.fade.easeOutQuad();
            if (quad < 1.0) {
                switch ((ClickGui.Mode)ClickGui.INSTANCE.mode.getValue()) {
                    case Pull: {
                        quad = 1.0 - quad;
                        matrixStack.translate(0.0, -100.0 * quad, 0.0);
                        break;
                    }
                    case Scale: {
                        matrixStack.scale((float)quad, (float)quad, 1.0f);
                        break;
                    }
                    case Scissor: {
                        this.setScissorRegion(0, 0, mc.getWindow().getWidth(), (int)((double)mc.getWindow().getHeight() * quad));
                    }
                }
            }
            for (ClickGuiTab tab : this.tabs) {
                tab.draw(drawContext, tickDelta, this.getColor());
            }
        }
        GL11.glDisable((int)3089);
        matrixStack.pop();
        GL11.glEnable((int)2884);
    }

    public void setScissorRegion(int x, int y, int width, int height) {
        double scaledY = mc.getWindow().getHeight() - (y + height);
        GL11.glEnable((int)3089);
        GL11.glScissor((int)x, (int)((int)scaledY), (int)width, (int)height);
    }

    public boolean isClickGuiOpen() {
        return HudManager.mc.currentScreen instanceof ClickGuiScreen;
    }

    static {
        currentGrabbed = null;
    }


    private static enum Pages {
        HUD,
        Combat,
        Render;

        // $FF: synthetic method
        private static HudManager.Pages[] $values() {
            return new HudManager.Pages[]{HUD, Combat, Render};
        }
    }

    public static enum HackName {
        MoonEmoji,
        StarEmoji,
        RebirthNew,
        Rebirth,
        MoonBladeNew,
        MoonBlade,
        MoonGod,
        GenShin,
        Mio,
        Isolation,
        MIniWorld;

        // $FF: synthetic method
        private static HudManager.HackName[] $values() {
            return new HudManager.HackName[]{MoonEmoji, StarEmoji, RebirthNew, Rebirth, MoonBladeNew, MoonBlade, MoonGod, GenShin, Mio, Isolation, MIniWorld};
        }
    }

    public static enum RenderMode {
        Fade,
        Glide;

        // $FF: synthetic method
        private static HudManager.RenderMode[] $values() {
            return new HudManager.RenderMode[]{Fade, Glide};
        }
    }

    public static enum GlideMode {
        InToOut,
        OutToIn,
        Test;

        // $FF: synthetic method
        private static HudManager.GlideMode[] $values() {
            return new HudManager.GlideMode[]{InToOut, OutToIn, Test};
        }
    }
}
