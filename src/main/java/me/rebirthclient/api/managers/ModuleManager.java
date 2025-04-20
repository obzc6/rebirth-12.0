/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 */
package me.rebirthclient.api.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.Mod;
import me.rebirthclient.mod.gui.screens.ClickGuiScreen;
import me.rebirthclient.mod.modules.ExtraModule;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.client.ArrayListModule;
import me.rebirthclient.mod.modules.client.Chat;
import me.rebirthclient.mod.modules.client.ClickGui;
import me.rebirthclient.mod.modules.client.FovMod;
import me.rebirthclient.mod.modules.client.NewArrayList;
import me.rebirthclient.mod.modules.client.Title;
import me.rebirthclient.mod.modules.combat.AnchorAssist;
import me.rebirthclient.mod.modules.combat.AntiCev;
import me.rebirthclient.mod.modules.combat.AntiPiston;
import me.rebirthclient.mod.modules.combat.AntiRegear;
import me.rebirthclient.mod.modules.combat.AntiWeak;
import me.rebirthclient.mod.modules.combat.Aura;
import me.rebirthclient.mod.modules.combat.AutoAnchor;
import me.rebirthclient.mod.modules.combat.AutoArmor;
import me.rebirthclient.mod.modules.combat.AutoCity;
import me.rebirthclient.mod.modules.combat.AutoCraft;
import me.rebirthclient.mod.modules.combat.AutoCrystal;
import me.rebirthclient.mod.modules.combat.AutoEXP;
import me.rebirthclient.mod.modules.combat.AutoPush;
import me.rebirthclient.mod.modules.combat.AutoRegear;
import me.rebirthclient.mod.modules.combat.AutoTotem;
import me.rebirthclient.mod.modules.combat.AutoWeb;
import me.rebirthclient.mod.modules.combat.BedAura;
import me.rebirthclient.mod.modules.combat.Burrow;
import me.rebirthclient.mod.modules.combat.Criticals;
import me.rebirthclient.mod.modules.combat.HoleFiller;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.modules.combat.PistonCrystal;
import me.rebirthclient.mod.modules.combat.PullCrystal;
import me.rebirthclient.mod.modules.combat.Quiver;
import me.rebirthclient.mod.modules.combat.SilentDouble;
import me.rebirthclient.mod.modules.combat.autotrap.AutoTrap;
import me.rebirthclient.mod.modules.combat.autotrap.ExtraAutoTrap;
import me.rebirthclient.mod.modules.combat.surround.ExtraSurround;
import me.rebirthclient.mod.modules.combat.surround.Surround;
import me.rebirthclient.mod.modules.miscellaneous.AutoPeek;
import me.rebirthclient.mod.modules.miscellaneous.AutoSign;
import me.rebirthclient.mod.modules.miscellaneous.ChatAppend;
import me.rebirthclient.mod.modules.miscellaneous.FakePlayer;
import me.rebirthclient.mod.modules.miscellaneous.FastPlace;
import me.rebirthclient.mod.modules.miscellaneous.GameSpeed;
import me.rebirthclient.mod.modules.miscellaneous.PopCounter;
import me.rebirthclient.mod.modules.miscellaneous.ShulkerViewer;
import me.rebirthclient.mod.modules.miscellaneous.SilentDisconnect;
import me.rebirthclient.mod.modules.movement.AutoCenter;
import me.rebirthclient.mod.modules.movement.AutoWalk;
import me.rebirthclient.mod.modules.movement.BlockStrafe;
import me.rebirthclient.mod.modules.movement.DamageFlyTest;
import me.rebirthclient.mod.modules.movement.ElytraFly;
import me.rebirthclient.mod.modules.movement.FastWeb;
import me.rebirthclient.mod.modules.movement.Flight;
import me.rebirthclient.mod.modules.movement.GuiMove;
import me.rebirthclient.mod.modules.movement.HoleSnap;
import me.rebirthclient.mod.modules.movement.NoFall;
import me.rebirthclient.mod.modules.movement.NoSlowdown;
import me.rebirthclient.mod.modules.movement.Scaffold;
import me.rebirthclient.mod.modules.movement.Speed;
import me.rebirthclient.mod.modules.movement.Sprint;
import me.rebirthclient.mod.modules.movement.Step;
import me.rebirthclient.mod.modules.movement.Velocity;
import me.rebirthclient.mod.modules.player.AntiCactus;
import me.rebirthclient.mod.modules.player.AntiHunger;
import me.rebirthclient.mod.modules.player.AutoRespawn;
import me.rebirthclient.mod.modules.player.Blink;
import me.rebirthclient.mod.modules.player.GhostHand;
import me.rebirthclient.mod.modules.player.HitboxDesync;
import me.rebirthclient.mod.modules.player.MCP;
import me.rebirthclient.mod.modules.player.MultiTask;
import me.rebirthclient.mod.modules.player.NoRotate;
import me.rebirthclient.mod.modules.player.NoSwap;
import me.rebirthclient.mod.modules.player.NoTrace;
import me.rebirthclient.mod.modules.player.OBSClip;
import me.rebirthclient.mod.modules.player.PacketEat;
import me.rebirthclient.mod.modules.player.PingSpoof;
import me.rebirthclient.mod.modules.player.Reach;
import me.rebirthclient.mod.modules.player.Replenish;
import me.rebirthclient.mod.modules.player.RotateBypass;
import me.rebirthclient.mod.modules.player.SpinBot;
import me.rebirthclient.mod.modules.player.XCarry;
import me.rebirthclient.mod.modules.render.Ambience;
import me.rebirthclient.mod.modules.render.BreakESP;
import me.rebirthclient.mod.modules.render.CameraClip;
import me.rebirthclient.mod.modules.render.ChestESP;
import me.rebirthclient.mod.modules.render.CrystalRenderer;
import me.rebirthclient.mod.modules.render.DefenseESP;
import me.rebirthclient.mod.modules.render.FaceESP;
import me.rebirthclient.mod.modules.render.Fullbright;
import me.rebirthclient.mod.modules.render.HitLog;
import me.rebirthclient.mod.modules.render.ItemESP;
import me.rebirthclient.mod.modules.render.LogoutSpots;
import me.rebirthclient.mod.modules.render.NameTags;
import me.rebirthclient.mod.modules.render.NoInvisible;
import me.rebirthclient.mod.modules.render.NoRender;
import me.rebirthclient.mod.modules.render.PlaceRender;
import me.rebirthclient.mod.modules.render.PlayerESP;
import me.rebirthclient.mod.modules.render.PopChams;
import me.rebirthclient.mod.modules.render.ShaderChams;
import me.rebirthclient.mod.modules.render.TimeChanger;
import me.rebirthclient.mod.modules.render.TwoDItem;
import me.rebirthclient.mod.modules.render.ViewModel;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.impl.BindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class ModuleManager
implements Wrapper {
    public ArrayList<Module> modules = new ArrayList();
    public ArrayList<ExtraModule> extraModules = new ArrayList();
    public HashMap<Module.Category, Integer> categoryModules = new HashMap();
    public static Mod lastLoadMod;

    public ModuleManager() {
        this.addExtraModule(new ExtraAutoTrap());
        this.addExtraModule(new ExtraSurround());
        this.addExtraModule(new PlaceRender());
        this.addModule(new Ambience());
        this.addModule(new AnchorAssist());
        this.addModule(new AntiCactus());
        this.addModule(new AntiHunger());
        this.addModule(new AntiPiston());
        this.addModule(new AntiRegear());
        this.addModule(new AntiWeak());
        this.addModule(new ArrayListModule());
        this.addModule(new Aura());
        this.addModule(new AutoAnchor());
        this.addModule(new AutoArmor());
        this.addModule(new FaceESP());
        this.addModule(new AutoCenter());
        this.addModule(new AutoCraft());
        this.addModule(new AutoCrystal());
        this.addModule(new AutoEXP());
        this.addModule(new AutoCity());
        this.addModule(new AutoPush());
        this.addModule(new AutoPeek());
        this.addModule(new AutoRegear());
        this.addModule(new AutoRespawn());
        this.addModule(new AutoSign());
        this.addModule(new AutoTotem());
        this.addModule(new AutoTrap());
        this.addModule(new AutoWalk());
        this.addModule(new AutoWeb());
        this.addModule(new BedAura());
        this.addModule(new Blink());
        this.addModule(new BlockStrafe());
        this.addModule(new BreakESP());
        this.addModule(new Burrow());
        this.addModule(new CameraClip());
        this.addModule(new Chat());
        this.addModule(new ChatAppend());
        this.addModule(new ChestESP());
        this.addModule(new ClickGui());
        this.addModule(new Criticals());
        this.addModule(new CrystalRenderer());
        this.addModule(new ElytraFly());
        this.addModule(new FakePlayer());
        this.addModule(new FastPlace());
        this.addModule(new FastWeb());
        this.addModule(new Flight());
        this.addModule(new FovMod());
        this.addModule(new Fullbright());
        this.addModule(new GameSpeed());
        this.addModule(new GhostHand());
        this.addModule(new GuiMove());
        this.addModule(new AntiCev());
        this.addModule(new HitboxDesync());
        this.addModule(new HitLog());
        this.addModule(new HoleFiller());
        this.addModule(new HoleSnap());
        this.addModule(new ItemESP());
        this.addModule(new LogoutSpots());
        this.addModule(new MCP());
        this.addModule(new MultiTask());
        this.addModule(new NameTags());
        this.addModule(new TimeChanger());
        this.addModule(new DamageFlyTest());
        this.addModule(new NewArrayList());
        this.addModule(new NoFall());
        this.addModule(new NoInvisible());
        this.addModule(new NoRender());
        this.addModule(new NoRotate());
        this.addModule(new NoSlowdown());
        this.addModule(new NoSwap());
        this.addModule(new NoTrace());
        this.addModule(new OBSClip());
        this.addModule(new PacketEat());
        this.addModule(new PacketMine());
        this.addModule(new PingSpoof());
        this.addModule(new PistonCrystal());
        this.addModule(new PlayerESP());
        this.addModule(new PopChams());
        this.addModule(new PopCounter());
        this.addModule(new PullCrystal());
        this.addModule(new Quiver());
        this.addModule(new Reach());
        this.addModule(new Replenish());
        this.addModule(new RotateBypass());
        this.addModule(new Scaffold());
        this.addModule(new ShaderChams());
        this.addModule(new ShulkerViewer());
        this.addModule(new SilentDisconnect());
        this.addModule(new SilentDouble());
        this.addModule(new Speed());
        this.addModule(new SpinBot());
        this.addModule(new Sprint());
        this.addModule(new Step());
        this.addModule(new Surround());
        this.addModule(new Title());
        this.addModule(new TwoDItem());
        this.addModule(new Velocity());
        this.addModule(new ViewModel());
        this.addModule(new DefenseESP());
        this.addModule(new XCarry());
    }

    public boolean setBind(int eventKey) {
        if (eventKey == -1 || eventKey == 0) {
            return false;
        }
        AtomicBoolean set = new AtomicBoolean(false);
        this.modules.forEach(module -> {
            for (Setting setting : module.getSettings()) {
                BindSetting bindSetting;
                if (!(setting instanceof BindSetting) || !(bindSetting = (BindSetting)setting).isListening()) continue;
                bindSetting.setKey(eventKey);
                bindSetting.setListening(false);
                if (bindSetting.getBind().equals("DELETE")) {
                    bindSetting.setKey(-1);
                }
                set.set(true);
            }
        });
        return set.get();
    }

    public void onKeyReleased(int eventKey) {
        if (eventKey == -1 || eventKey == 0 || ModuleManager.mc.currentScreen instanceof ClickGuiScreen) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey && module.getToggle() == Module.Toggle.Disable) {
                module.disable();
            }
            for (Setting setting : module.getSettings()) {
                BindSetting bindSetting;
                if (!(setting instanceof BindSetting) || (bindSetting = (BindSetting)setting).getKey() != eventKey) continue;
                bindSetting.setDown(false);
            }
        });
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == -1 || eventKey == 0 || ModuleManager.mc.currentScreen instanceof ClickGuiScreen) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey && ModuleManager.mc.currentScreen == null) {
                module.toggle();
            }
            for (Setting setting : module.getSettings()) {
                BindSetting bindSetting;
                if (!(setting instanceof BindSetting) || (bindSetting = (BindSetting)setting).getKey() != eventKey) continue;
                bindSetting.setDown(true);
            }
        });
    }

    public void onUpdate() {
        for (Module module : this.modules) {
            if (!module.isOn()) continue;
            module.onUpdate();
        }
    }

    public void onLogin() {
        for (Module module : this.modules) {
            if (!module.isOn()) continue;
            module.onLogin();
        }
    }

    public void render3D(MatrixStack matrixStack) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2884);
        GL11.glDisable((int)2929);
        matrixStack.push();
        Vec3d camPos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getPos();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
        for (Module module : this.modules) {
            if (!module.isOn()) continue;
            module.onRender3D(matrixStack, MinecraftClient.getInstance().getTickDelta());
        }
        for (ExtraModule extraModule : this.extraModules) {
            extraModule.onRender3D(matrixStack, MinecraftClient.getInstance().getTickDelta());
        }
        matrixStack.pop();
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
    }

    public void render2D(DrawContext drawContext) {
        for (Module module : this.modules) {
            if (!module.isOn()) continue;
            module.onRender2D(drawContext, MinecraftClient.getInstance().getTickDelta());
        }
    }

    public void addExtraModule(ExtraModule module) {
        this.extraModules.add(module);
    }

    public void addModule(Module module) {
        this.modules.add(module);
        Collections.sort(this.modules, Comparator.comparing(item -> item.name));
        this.categoryModules.put(module.getCategory(), this.categoryModules.getOrDefault((Object)module.getCategory(), 0) + 1);
    }

    public void disableAll() {
        for (Module module : this.modules) {
            module.disable();
        }
    }

    public Module getModuleByName(String string) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(string)) continue;
            return module;
        }
        return null;
    }
}

