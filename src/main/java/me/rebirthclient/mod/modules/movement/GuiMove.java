/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.util.math.MatrixStack
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.UpdateWalkingEvent;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.movement.AutoWalk;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;

public class GuiMove
extends Module {
    private final BooleanSetting sneak = new BooleanSetting("Sneak", false);

    public GuiMove() {
        super("GuiMove", Module.Category.Movement);
        this.setDescription("Walk in inventory.");
        this.add(this.sneak);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (GuiMove.mc.currentScreen != null && !(GuiMove.mc.currentScreen instanceof ChatScreen)) {
            for (KeyBinding k : new KeyBinding[]{GuiMove.mc.options.backKey, GuiMove.mc.options.leftKey, GuiMove.mc.options.rightKey, GuiMove.mc.options.jumpKey, GuiMove.mc.options.sprintKey}) {
                k.setPressed(InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)InputUtil.fromTranslationKey((String)k.getBoundKeyTranslationKey()).getCode()));
            }
            GuiMove.mc.options.forwardKey.setPressed(AutoWalk.INSTANCE.isOn() || InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)InputUtil.fromTranslationKey((String)GuiMove.mc.options.forwardKey.getBoundKeyTranslationKey()).getCode()));
            if (this.sneak.getValue()) {
                GuiMove.mc.options.sneakKey.setPressed(InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)InputUtil.fromTranslationKey((String)GuiMove.mc.options.sneakKey.getBoundKeyTranslationKey()).getCode()));
            }
        }
    }

    @EventHandler
    public void onUpdateWalking(UpdateWalkingEvent event) {
        this.update();
    }

    @Override
    public void onUpdate() {
        this.update();
    }

    private void update() {
        if (!(GuiMove.mc.currentScreen instanceof ChatScreen)) {
            for (KeyBinding k : new KeyBinding[]{GuiMove.mc.options.backKey, GuiMove.mc.options.leftKey, GuiMove.mc.options.rightKey, GuiMove.mc.options.jumpKey, GuiMove.mc.options.sprintKey}) {
                k.setPressed(InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)InputUtil.fromTranslationKey((String)k.getBoundKeyTranslationKey()).getCode()));
            }
            GuiMove.mc.options.forwardKey.setPressed(AutoWalk.INSTANCE.isOn() || InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)InputUtil.fromTranslationKey((String)GuiMove.mc.options.forwardKey.getBoundKeyTranslationKey()).getCode()));
            if (this.sneak.getValue()) {
                GuiMove.mc.options.sneakKey.setPressed(InputUtil.isKeyPressed((long)mc.getWindow().getHandle(), (int)InputUtil.fromTranslationKey((String)GuiMove.mc.options.sneakKey.getBoundKeyTranslationKey()).getCode()));
            }
        }
    }
}

