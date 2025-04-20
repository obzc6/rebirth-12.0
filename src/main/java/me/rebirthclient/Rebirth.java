
package me.rebirthclient;

import java.lang.invoke.MethodHandles;
import me.rebirthclient.api.events.eventbus.EventBus;
import me.rebirthclient.api.managers.AltManager;
import me.rebirthclient.api.managers.BreakManager;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.managers.ConfigManager;
import me.rebirthclient.api.managers.FriendManager;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.managers.ModuleManager;
import me.rebirthclient.api.managers.PingSpoofManager;
import me.rebirthclient.api.managers.PopManager;
import me.rebirthclient.api.managers.RunManager;
import me.rebirthclient.api.managers.ShaderManager;
import me.rebirthclient.api.managers.TimerManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.DrawContext;


public final class Rebirth
implements ModInitializer {
    public static final String LOG_NAME = "Rebirth";
    public static final String VERSION = "1.8";
    public static String PREFIX = ";";
    public static final EventBus EVENT_BUS = new EventBus();
    public static ModuleManager MODULE;
    public static CommandManager COMMAND;
    public static AltManager ALT;
    public static HudManager HUD;
    public static ConfigManager CONFIG;
    public static RunManager RUN;
    public static BreakManager BREAK;
    public static PopManager POP;
    public static FriendManager FRIEND;
    public static TimerManager TIMER;
    public static ShaderManager SHADER;
    public static PingSpoofManager PINGSPOOF;
    public static boolean loaded;

    public void onInitialize() {
        Rebirth.load();
    }

    public static void update() {
        if (!loaded) {
            return;
        }
        MODULE.onUpdate();
        HUD.update();
        POP.update();
    }

    public static void drawHUD(DrawContext context, float partialTicks) {
        if (!loaded) {
            return;
        }
        if (!HUD.isClickGuiOpen()) {
            HUD.draw(context, partialTicks);
        }
    }


    public static void load() {
        System.out.println("[Rebirth] Starting Client");
        System.out.println("[Rebirth] Register eventbus");
        EVENT_BUS.registerLambdaFactory("me.rebirthclient", (lookupInMethod, klass) -> (MethodHandles.Lookup)lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        System.out.println("[Rebirth] Reading Settings");
        CONFIG = new ConfigManager();
        PREFIX = CONFIG.getSettingString("prefix", PREFIX);
        System.out.println("[Rebirth] Initializing Modules");
        MODULE = new ModuleManager();
        System.out.println("[Rebirth] Initializing Commands");
        COMMAND = new CommandManager();
        System.out.println("[Rebirth] Initializing GUI");
        HUD = new HudManager();
        System.out.println("[Rebirth] Loading Alts");
        ALT = new AltManager();
        System.out.println("[Rebirth] Loading Friends");
        FRIEND = new FriendManager();
        System.out.println("[Rebirth] Loading RunManager");
        RUN = new RunManager();
        System.out.println("[Rebirth] Loading BreakManager");
        BREAK = new BreakManager();
        System.out.println("[Rebirth] Loading PopManager");
        POP = new PopManager();
        System.out.println("[Rebirth] Loading TimerManager");
        TIMER = new TimerManager();
        System.out.println("[Rebirth] Loading ShaderManager");
        SHADER = new ShaderManager();
        System.out.println("[Rebirth] Loading PingSpoofManager");
        PINGSPOOF = new PingSpoofManager();
        System.out.println("[Rebirth] Loading Settings");
        CONFIG.loadSettings();
        System.out.println("[Rebirth] Initialized and ready to play!");
        System.out.println(" _______           __         _           _   __   ");
        System.out.println("|_   __ \\         [  |       (_)         / |_[  |    ");
        System.out.println("  | |__) |  .---.  | |.--.   __   _ .--.`| |-'| |--. ");
        System.out.println("  |  __ /  / /__\\\\ | '/'`\\ \\[  | [ `/'`\\]| |  | .-. | ");
        System.out.println(" _| |  \\ \\_| \\__., |  \\__/ | | |  | |    | |, | | | | ");
        System.out.println("|____| |___|'.__.'[__;.__.' [___][___]   \\__/[___]|__] ");
        System.out.println("                                                      ");
        Runtime.getRuntime().addShutdownHook(new Thread(Rebirth::save));
        loaded = true;
    }

    public static void unload() {
        loaded = false;
        System.out.println("[Rebirth] Unloading..");
        Rebirth.EVENT_BUS.listenerMap.clear();
        CONFIG = null;
        MODULE = null;
        COMMAND = null;
        HUD = null;
        ALT = null;
        FRIEND = null;
        RUN = null;
        POP = null;
        TIMER = null;
        PINGSPOOF = null;
        System.out.println("[Rebirth] Unloading success!");
    }

    public static void save() {
        System.out.println("[Rebirth] Saving...");
        CONFIG.saveSettings();
        FRIEND.saveFriends();
        ALT.saveAlts();
        System.out.println("[Rebirth] Saving success!");
    }

    public static String getName() {
        if (HUD == null) {
            return "Rebirth";
        } else {
            switch((HudManager.HackName)HUD.hackName.getValue()) {
                case Rebirth:
                    return "L";
                case MoonGod:
                    return "MoonGod";
                case MoonBlade:
                    return "MoonBlade";
                case RebirthNew:
                    return "ℜ\ud835\udd22\ud835\udd1f\ud835\udd26\ud835\udd2f\ud835\udd31\ud835\udd25";
                case MoonEmoji:
                    return "☽";
                case StarEmoji:
                    return "✷";
                default:
                    return "Rebirth";
            }
        }
    }
}
