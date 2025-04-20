/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 */
package me.rebirthclient.mod.modules.combat.surround;

import java.awt.Color;
import java.util.HashMap;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.ColorUtil;
import me.rebirthclient.api.util.FadeUtils;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.ExtraModule;
import me.rebirthclient.mod.modules.combat.surround.Surround;
import me.rebirthclient.mod.modules.render.PlaceRender;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class ExtraSurround
extends ExtraModule {
    public static final HashMap<BlockPos, placePosition> PlaceMap = new HashMap();
    private BlockPos lastPos = null;

    public static void addBlock(BlockPos pos) {
        if (BlockUtil.clientCanPlace(pos, true)) {
            PlaceRender.PlaceMap.put(pos, new PlaceRender.placePosition(pos));
        }
    }

    private void drawBlock(BlockPos pos, int alpha, Color color, MatrixStack matrixStack) {
        Render3DUtil.draw3DBox(matrixStack, new Box(pos), ColorUtil.injectAlpha(color, alpha), Surround.INSTANCE.outline.getValue(), Surround.INSTANCE.box.getValue());
    }

    public static class placePosition {
        public final FadeUtils firstFade;
        public final BlockPos pos;
        public final Timer timer;
        public boolean isAir;

        public placePosition(BlockPos placePos) {
            this.firstFade = new FadeUtils((long)Surround.INSTANCE.fadeTime.getValue());
            this.pos = placePos;
            this.timer = new Timer();
            this.isAir = true;
            this.timer.reset();
        }
    }
}

