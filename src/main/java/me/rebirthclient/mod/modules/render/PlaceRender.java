/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 */
package me.rebirthclient.mod.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.HashMap;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PlaceBlockEvent;
import me.rebirthclient.api.managers.HudManager;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.mod.modules.ExtraModule;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class PlaceRender
extends ExtraModule {
    public static final HashMap<BlockPos, placePosition> PlaceMap = new HashMap();

    @EventHandler
    public void onEvent(PlaceBlockEvent event) {
        PlaceRender.addBlock(event.blockPos);
    }

    public static void addBlock(BlockPos pos) {
        if (!PlaceMap.containsKey((Object)pos)) {
            PlaceMap.put(pos, new placePosition(pos));
        }
    }

    private void drawBlock(BlockPos pos, double alpha, Color color, MatrixStack matrixStack) {
        if (!Rebirth.HUD.render.getValue()) {
            return;
        }
        Render3DUtil.draw3DBox(matrixStack, new Box(pos), ColorUtil.injectAlpha(color, (int)alpha), Rebirth.HUD.outline.getValue(), Rebirth.HUD.box.getValue());
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (PlaceMap.isEmpty() || !Rebirth.HUD.render.getValue()) {
            return;
        }
        if (Rebirth.HUD.rendermode.getValue() == HudManager.RenderMode.Fade) {
            boolean shouldClear = true;
            for (placePosition placePosition2 : PlaceMap.values()) {
                if (placePosition2.firstFade.easeOutQuad() == 1.0) continue;
                shouldClear = false;
                this.drawBlock(placePosition2.pos, (double)Rebirth.HUD.color.getValue().getAlpha() * (1.0 - placePosition2.firstFade.easeOutQuad()), Rebirth.HUD.color.getValue(), matrixStack);
            }
            if (shouldClear) {
                PlaceMap.clear();
            }
        } else {
            for (placePosition placePosition3 : PlaceMap.values()) {
                if (placePosition3.firstFade.isEnd()) {
                    PlaceMap.remove((Object)placePosition3.pos);
                    return;
                }
                double ease = (1.0 - placePosition3.firstFade.easeOutQuad()) * 0.5;
                if (Rebirth.HUD.glidemode.getValue() == HudManager.GlideMode.OutToIn) {
                    ease = placePosition3.firstFade.easeOutQuad() * 0.5;
                } else if (Rebirth.HUD.glidemode.getValue() == HudManager.GlideMode.Test) {
                    ease = placePosition3.firstFade.easeOutQuad();
                }
                Box box = new Box(placePosition3.pos).shrink(ease, ease, ease).shrink(-ease, -ease, -ease);
                boolean filled = Rebirth.HUD.box.getValue();
                boolean outline = Rebirth.HUD.outline.getValue();
                boolean tracer = Rebirth.HUD.through.getValue();
                if (!(filled || outline || tracer)) {
                    return;
                }
                GlStateManager._disableDepthTest();
                RenderSystem.lineWidth((float)2.0f);
                if (outline) {
                    Render3DUtil.drawLine(box, Rebirth.HUD.coloroutline.getValue(), 2.0f);
                }
                if (!filled) continue;
                Render3DUtil.drawFilledBox(matrixStack, box, Rebirth.HUD.color.getValue());
            }
        }
    }

    public static class placePosition {
        public final FadeUtils firstFade;
        public final BlockPos pos;

        public placePosition(BlockPos placePos) {
            this.firstFade = new FadeUtils((long)Rebirth.HUD.fadeTime.getValue());
            this.pos = placePos;
        }
    }
}

