/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  org.apache.commons.io.IOUtils
 */
package me.rebirthclient.api.managers;

import com.google.common.base.Splitter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.gui.tabs.ClickGuiTab;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.impl.BindSetting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import org.apache.commons.io.IOUtils;

public class ConfigManager
implements Wrapper {
    public final ArrayList<Setting> SETTINGS = new ArrayList();
    private final File rebirthOptions;
    private final Hashtable<String, String> settings = new Hashtable();

    public ConfigManager() {
        this.rebirthOptions = new File(ConfigManager.mc.runDirectory, "rebirth_options.txt");
        this.readSettings();
    }

    public void loadSettings() {
        for (Setting setting : Rebirth.CONFIG.SETTINGS) {
            setting.loadSetting();
        }
        for (Module module : Rebirth.MODULE.modules) {
            for (Setting setting : module.getSettings()) {
                setting.loadSetting();
            }
            module.setState(Rebirth.CONFIG.getSettingBoolean(module.getName() + "_state", false));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveSettings() {
        PrintWriter printwriter = null;
        try {
            Setting bs;
            printwriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.rebirthOptions), StandardCharsets.UTF_8));
            printwriter.println("prefix:" + Rebirth.PREFIX);
            printwriter.println("ingame_x:" + HudManager.hud.getX());
            printwriter.println("ingame_y:" + HudManager.hud.getY());
            for (ClickGuiTab tab : Rebirth.HUD.tabs) {
                printwriter.println(tab.getTitle() + "_x:" + tab.getX());
                printwriter.println(tab.getTitle() + "_y:" + tab.getY());
            }
            printwriter.println("armor_x:" + Rebirth.HUD.armorHud.getX());
            printwriter.println("armor_y:" + Rebirth.HUD.armorHud.getY());
            for (Setting setting : Rebirth.CONFIG.SETTINGS) {
                if (setting instanceof BooleanSetting) {
                    BooleanSetting bs2 = (BooleanSetting)setting;
                    printwriter.println(bs2.getLine() + ":" + bs2.getValue());
                    continue;
                }
                if (setting instanceof SliderSetting) {
                    SliderSetting ss = (SliderSetting)setting;
                    printwriter.println(ss.getLine() + ":" + ss.getValue());
                    continue;
                }
                if (setting instanceof BindSetting) {
                    bs = (BindSetting)setting;
                    printwriter.println(bs.getLine() + ":" + ((BindSetting)bs).getKey());
                    continue;
                }
                if (setting instanceof EnumSetting) {
                    EnumSetting es = (EnumSetting)setting;
                    printwriter.println(es.getLine() + ":" + es.getValue().name());
                    continue;
                }
                if (!(setting instanceof ColorSetting)) continue;
                ColorSetting cs = (ColorSetting)setting;
                printwriter.println(cs.getLine() + ":" + cs.getValue().getRGB());
                printwriter.println(cs.getLine() + "Rainbow:" + cs.isRainbow);
                if (!cs.injectBoolean) continue;
                printwriter.println(cs.getLine() + "Boolean:" + cs.booleanValue);
            }
            for (Module module : Rebirth.MODULE.modules) {
                for (Setting setting : module.getSettings()) {
                    if (setting instanceof BooleanSetting) {
                        bs = (BooleanSetting)setting;
                        printwriter.println(bs.getLine() + ":" + ((BooleanSetting)bs).getValue());
                        continue;
                    }
                    if (setting instanceof SliderSetting) {
                        SliderSetting ss = (SliderSetting)setting;
                        printwriter.println(ss.getLine() + ":" + ss.getValue());
                        continue;
                    }
                    if (setting instanceof BindSetting) {
                        BindSetting bs3 = (BindSetting)setting;
                        printwriter.println(bs3.getLine() + ":" + bs3.getKey());
                        continue;
                    }
                    if (setting instanceof EnumSetting) {
                        EnumSetting es = (EnumSetting)setting;
                        printwriter.println(es.getLine() + ":" + es.getValue().name());
                        continue;
                    }
                    if (!(setting instanceof ColorSetting)) continue;
                    ColorSetting cs = (ColorSetting)setting;
                    printwriter.println(cs.getLine() + ":" + cs.getValue().getRGB());
                    printwriter.println(cs.getLine() + "Rainbow:" + cs.isRainbow);
                    if (!cs.injectBoolean) continue;
                    printwriter.println(cs.getLine() + "Boolean:" + cs.booleanValue);
                }
                printwriter.println(module.getName() + "_state:" + module.isOn());
            }
            IOUtils.closeQuietly((Writer)printwriter);
        }
        catch (Exception exception) {
            System.out.println("[Rebirth] Failed to save settings");
        }
        finally {
            IOUtils.closeQuietly(printwriter);
        }
    }

    public void readSettings() {
        Splitter COLON_SPLITTER = Splitter.on((char)':');
        try {
            if (!this.rebirthOptions.exists()) {
                return;
            }
            List list = IOUtils.readLines((InputStream)new FileInputStream(this.rebirthOptions), (Charset)StandardCharsets.UTF_8);
            for (Object s : list) {
                try {
                    Iterator iterator = COLON_SPLITTER.limit(2).split((CharSequence)s).iterator();
                    this.settings.put((String)iterator.next(), (String)iterator.next());
                }
                catch (Exception var10) {
                    System.out.println("Skipping bad option: " + s);
                }
            }
        }
        catch (Exception exception) {
            System.out.println("[Rebirth] Failed to load settings");
        }
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isFloat(String str) {
        String pattern = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$";
        return str.matches(pattern);
    }

    public int getSettingInt(String setting, int defaultValue) {
        String s = this.settings.get(setting);
        if (s == null || !ConfigManager.isInteger(s)) {
            return defaultValue;
        }
        return Integer.parseInt(s);
    }

    public float getSettingFloat(String setting, float defaultValue) {
        String s = this.settings.get(setting);
        if (s == null || !ConfigManager.isFloat(s)) {
            return defaultValue;
        }
        return Float.parseFloat(s);
    }

    public boolean getSettingBoolean(String setting) {
        String s = this.settings.get(setting);
        return Boolean.parseBoolean(s);
    }

    public boolean getSettingBoolean(String setting, boolean defaultValue) {
        if (this.settings.get(setting) != null) {
            String s = this.settings.get(setting);
            return Boolean.parseBoolean(s);
        }
        return defaultValue;
    }

    public String getSettingString(String setting) {
        return this.settings.get(setting);
    }

    public String getSettingString(String setting, String defaultValue) {
        if (this.settings.get(setting) == null) {
            return defaultValue;
        }
        return this.settings.get(setting);
    }
}

