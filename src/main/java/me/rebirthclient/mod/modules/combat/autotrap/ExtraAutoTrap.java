/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 */
package me.rebirthclient.mod.modules.combat.autotrap;

import java.awt.Color;
import java.util.HashMap;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.ExtraModule;
import me.rebirthclient.mod.modules.combat.autotrap.AutoTrap;
import me.rebirthclient.mod.modules.render.PlaceRender;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class ExtraAutoTrap
extends ExtraModule {
    public static final HashMap<BlockPos, placePosition> PlaceMap = new HashMap();

    public static void addBlock(BlockPos pos) {
        if (BlockUtil.clientCanPlace(pos, true) && !PlaceRender.PlaceMap.containsKey((Object)pos)) {
            PlaceRender.PlaceMap.put(pos, new PlaceRender.placePosition(pos));
        }
    }

    private void drawBlock(BlockPos pos, double alpha, Color color, MatrixStack matrixStack) {
        if (AutoTrap.INSTANCE.sync.getValue()) {
            color = AutoTrap.INSTANCE.color.getValue();
        }
        Render3DUtil.draw3DBox(matrixStack, new Box(pos), ColorUtil.injectAlpha(color, (int)alpha), AutoTrap.INSTANCE.outline.getValue(), AutoTrap.INSTANCE.box.getValue());
    }

    public static class placePosition {
        public final FadeUtils firstFade;
        public final BlockPos pos;
        public final Color posColor;
        public final Timer timer;
        public boolean isAir;

        public placePosition(BlockPos placePos) {
            this.firstFade = new FadeUtils((long)AutoTrap.INSTANCE.fadeTime.getValue());
            this.pos = placePos;
            this.posColor = AutoTrap.INSTANCE.color.getValue();
            this.timer = new Timer();
            this.isAir = true;
            this.timer.reset();
        }
    }
}

