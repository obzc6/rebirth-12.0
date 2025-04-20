/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.Screen
 */
package me.rebirthclient.mod.modules.client;

import java.awt.Color;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.mod.gui.components.Component;
import me.rebirthclient.mod.gui.components.impl.BooleanComponent;
import me.rebirthclient.mod.gui.components.impl.ColorComponents;
import me.rebirthclient.mod.gui.components.impl.ModuleComponent;
import me.rebirthclient.mod.gui.components.impl.SliderComponent;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.screen.Screen;

public class ClickGui
extends Module {
    public static ClickGui INSTANCE;
    private final EnumSetting page = this.add(new EnumSetting("Page", Pages.General));
    public BooleanSetting gear = this.add(new BooleanSetting("GearToggle", true, v -> this.page.getValue() == Pages.General));
    public EnumSetting mode = this.add(new EnumSetting("ToggleAnimation", Mode.Reset, v -> this.page.getValue() == Pages.General));
    public BooleanSetting showBYD = this.add(new BooleanSetting("Show BYD", true, v -> this.page.getValue() == Pages.General)).setParent();
    public EnumSetting bydMode = this.add(new EnumSetting("BYD Mode", BYDMode.hxy, v -> this.page.getValue() == Pages.General && this.showBYD.isOpen()));
    public BooleanSetting snow = this.add(new BooleanSetting("Snow", true, v -> this.page.getValue() == Pages.General));
    public BooleanSetting scissor = this.add(new BooleanSetting("Scissor", true, v -> this.page.getValue() == Pages.General));
    public SliderSetting animationSpeed = this.add(new SliderSetting("AnimationSpeed", 0.2, 0.01, 1.0, 0.01, v -> this.page.getValue() == Pages.General));
    public SliderSetting sliderSpeed = this.add(new SliderSetting("SliderSpeed", 0.2, 0.01, 1.0, 0.01, v -> this.page.getValue() == Pages.General));
    public SliderSetting booleanSpeed = this.add(new SliderSetting("BooleanSpeed", 0.2, 0.01, 1.0, 0.01, v -> this.page.getValue() == Pages.General));
    public BooleanSetting customFont = this.add(new BooleanSetting("CustomFont", false, v -> this.page.getValue() == Pages.General));
    public ColorSetting color = this.add(new ColorSetting("Main", new Color(140, 146, 255), v -> this.page.getValue() == Pages.Color));
    public ColorSetting gearColor = this.add(new ColorSetting("Gear", new Color(150, 150, 150), v -> this.page.getValue() == Pages.Color));
    public ColorSetting enableText = this.add(new ColorSetting("EnableText", new Color(140, 146, 255), v -> this.page.getValue() == Pages.Color));
    public ColorSetting disableText = this.add(new ColorSetting("DisableText", new Color(255, 255, 255), v -> this.page.getValue() == Pages.Color));
    public ColorSetting mbgColor = this.add(new ColorSetting("Module", new Color(24, 24, 24, 42), v -> this.page.getValue() == Pages.Color));
    public ColorSetting moduleEnable = this.add(new ColorSetting("ModuleEnable", new Color(24, 24, 24, 42), v -> this.page.getValue() == Pages.Color));
    public ColorSetting mhColor = this.add(new ColorSetting("ModuleHover", new Color(152, 152, 152, 123), v -> this.page.getValue() == Pages.Color));
    public ColorSetting sbgColor = this.add(new ColorSetting("Setting", new Color(24, 24, 24, 42), v -> this.page.getValue() == Pages.Color));
    public ColorSetting shColor = this.add(new ColorSetting("SettingHover", new Color(152, 152, 152, 123), v -> this.page.getValue() == Pages.Color));
    public ColorSetting bgColor = this.add(new ColorSetting("Background", new Color(24, 24, 24, 42), v -> this.page.getValue() == Pages.Color));
    public static final FadeUtils fade;

    public ClickGui() {
        super("ClickGui", Module.Category.Client);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (!(ClickGui.mc.currentScreen instanceof ClickGuiScreen)) {
            this.disable();
        }
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue() == Mode.Reset) {
            for (ClickGuiTab tab : Rebirth.HUD.tabs) {
                for (Component component : tab.getChildren()) {
                    component.currentOffset = 0.0;
                    if (!(component instanceof ModuleComponent)) continue;
                    ModuleComponent moduleComponent = (ModuleComponent)component;
                    moduleComponent.isPopped = false;
                    for (Component settingComponent : moduleComponent.getSettingsList()) {
                        settingComponent.currentOffset = 0.0;
                        if (settingComponent instanceof SliderComponent) {
                            SliderComponent sliderComponent = (SliderComponent)settingComponent;
                            sliderComponent.renderSliderPosition = 0.0;
                            continue;
                        }
                        if (settingComponent instanceof BooleanComponent) {
                            BooleanComponent booleanComponent = (BooleanComponent)settingComponent;
                            booleanComponent.currentWidth = 0.0;
                            continue;
                        }
                        if (!(settingComponent instanceof ColorComponents)) continue;
                        ColorComponents colorComponents = (ColorComponents)settingComponent;
                        colorComponents.currentWidth = 0.0;
                    }
                }
                tab.currentHeight = 0.0;
            }
        }
        fade.reset();
        if (ClickGui.nullCheck()) {
            this.disable();
            return;
        }
        mc.setScreen((Screen)HudManager.clickGui);
    }

    @Override
    public void onDisable() {
        if (ClickGui.mc.currentScreen instanceof ClickGuiScreen) {
            mc.setScreen(null);
        }
    }

    static {
        fade = new FadeUtils(400L);
    }

    private static enum Pages {
        General,
        Color;

        // $FF: synthetic method
        private static ClickGui.Pages[] $values() {
            return new ClickGui.Pages[]{General, Color};
        }
    }

    public static enum Mode {
        Scale,
        Pull,
        Scissor,
        Reset,
        None;

        // $FF: synthetic method
        private static ClickGui.Mode[] $values() {
            return new ClickGui.Mode[]{Scale, Pull, Scissor, Reset, None};
        }
    }

    public static enum BYDMode {
        hxy,
        madcat,
        gun,
        little_byd,
        LiJiaLe;

        // $FF: synthetic method
        private static ClickGui.BYDMode[] $values() {
            return new ClickGui.BYDMode[]{hxy, madcat, gun, little_byd, LiJiaLe};
        }
    }
}
