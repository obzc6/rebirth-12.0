/*
 * Decompiled with CFR 0.150.
 */
package me.rebirthclient.mod.gui.tabs;

import me.rebirthclient.Rebirth;
import me.rebirthclient.mod.gui.components.impl.BindComponent;
import me.rebirthclient.mod.gui.components.impl.BooleanComponent;
import me.rebirthclient.mod.gui.components.impl.ColorComponents;
import me.rebirthclient.mod.gui.components.impl.EnumComponent;
import me.rebirthclient.mod.gui.components.impl.SliderComponent;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.impl.BindSetting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;

public class OptionsTab
extends ClickGuiTab {
    public OptionsTab(String title, int x, int y) {
        super(title, x, y);
        this.setWidth(180);
    }

    public void addChild(Setting setting) {
        if (setting == null) {
            return;
        }
        Rebirth.CONFIG.SETTINGS.add(setting);
        if (setting instanceof SliderSetting) {
            this.addChild(new SliderComponent(this, (SliderSetting)setting));
        } else if (setting instanceof BooleanSetting) {
            this.addChild(new BooleanComponent(this, (BooleanSetting)setting));
        } else if (setting instanceof EnumSetting) {
            this.addChild(new EnumComponent(this, (EnumSetting)setting));
        } else if (setting instanceof BindSetting) {
            this.addChild(new BindComponent(this, (BindSetting)setting));
        } else if (setting instanceof ColorSetting) {
            this.addChild(new ColorComponents(this, (ColorSetting)setting));
        }
    }
}

