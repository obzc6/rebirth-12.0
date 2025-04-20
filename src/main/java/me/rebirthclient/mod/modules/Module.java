/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.util.math.MatrixStack
 */
package me.rebirthclient.mod.modules;

import java.util.ArrayList;
import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.mod.Mod;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.impl.BindSetting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Module
extends Mod {
    private String description;
    private final Category category;
    private final BooleanSetting draw;
    private final EnumSetting toggle;
    private final BindSetting bindSetting;
    public boolean state;
    private final List<Setting> settings = new ArrayList<Setting>();

    public Module(String name, Category category) {
        this(name, "", category);
    }

    public Module(String name, String description, Category category) {
        super(name);
        this.category = category;
        this.description = description;
        ModuleManager.lastLoadMod = this;
        this.bindSetting = this.add(new BindSetting("Bind", name.equalsIgnoreCase("ClickGui") ? 89 : -1));
        this.draw = this.add(new BooleanSetting("Draw", true));
        this.toggle = this.add(new EnumSetting("Toggle", Toggle.Normal));
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return this.category;
    }

    public BindSetting getBind() {
        return this.bindSetting;
    }

    public boolean getDraw() {
        return this.draw.getValue();
    }

    public Enum getToggle() {
        return this.toggle.getValue();
    }

    public String getArrayName() {
        return this.name + this.getArrayInfo();
    }

    public String getArrayInfo() {
        return this.getInfo() == null ? "" : " \u00a77[" + this.getInfo() + "\u00a77]";
    }

    public boolean isOn() {
        return this.state;
    }

    public boolean isOff() {
        return !this.isOn();
    }

    public void toggle() {
        if (this.isOn()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public void enable() {
        if (this.state) {
            return;
        }
        CommandManager.sendChatMessageWidthId("\u00a7a[+] \u00a7f" + this.getName(), -1);
        this.state = true;
        Rebirth.EVENT_BUS.subscribe(this);
        this.onToggle();
        this.onEnable();
    }

    public void disable() {
        if (!this.state) {
            return;
        }
        CommandManager.sendChatMessageWidthId("\u00a74[-] \u00a7f" + this.getName(), -1);
        this.state = false;
        Rebirth.EVENT_BUS.unsubscribe(this);
        this.onToggle();
        this.onDisable();
    }

    public void setState(boolean state) {
        if (this.state == state) {
            return;
        }
        if (state) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public boolean setBind(String rkey) {
        int key;
        if (rkey.equalsIgnoreCase("none")) {
            this.bindSetting.setKey(-1);
            return true;
        }
        try {
            key = InputUtil.fromTranslationKey((String)("key.keyboard." + rkey.toLowerCase())).getCode();
        }
        catch (NumberFormatException e) {
            if (!Module.nullCheck()) {
                CommandManager.sendChatMessage("\u00a7c[!] \u00a7fBad key!");
            }
            return false;
        }
        if (rkey.equalsIgnoreCase("none")) {
            key = -1;
        }
        if (key == 0) {
            return false;
        }
        this.bindSetting.setKey(key);
        return true;
    }

    public void addSetting(Setting setting) {
        this.settings.add(setting);
    }

    public ColorSetting add(ColorSetting setting) {
        this.addSetting(setting);
        return setting;
    }

    public SliderSetting add(SliderSetting setting) {
        this.addSetting(setting);
        return setting;
    }

    public BooleanSetting add(BooleanSetting setting) {
        this.addSetting(setting);
        return setting;
    }

    public EnumSetting add(EnumSetting setting) {
        this.addSetting(setting);
        return setting;
    }

    public BindSetting add(BindSetting setting) {
        this.addSetting(setting);
        return setting;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    public static boolean nullCheck() {
        return Module.mc.player == null || Module.mc.world == null;
    }

    public void onDisable() {
    }

    public void onEnable() {
    }

    public void onToggle() {
    }

    public void onUpdate() {
    }

    public void onLogin() {
    }

    public void onRender2D(DrawContext drawContext, float tickDelta) {
    }

    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
    }

    public void JelloonRender3D(MatrixStack matrixStack, float partialTicks) {
    }

    public final boolean isCategory(Category category) {
        return category == this.category;
    }

    public String getInfo() {
        return null;
    }

    public static enum Category {
        Combat,
        Miscellaneous,
        Movement,
        Render,
        Player,
        Client;

        // $FF: synthetic method
        private static Module.Category[] $values() {
            return new Module.Category[]{Combat, Miscellaneous, Movement, Render, Player, Client};
        }
    }

    public static enum Toggle {
        Normal,
        Disable;

        // $FF: synthetic method
        private static Module.Toggle[] $values() {
            return new Module.Toggle[]{Normal, Disable};
        }
    }
}
