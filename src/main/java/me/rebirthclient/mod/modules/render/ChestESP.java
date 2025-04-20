/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.Box
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public class ChestESP
extends Module {
    public final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));

    public ChestESP() {
        super("ChestESP", Module.Category.Render);
        this.setDescription("Allows the player to see Chests with an ESP.");
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        ArrayList<BlockEntity> blockEntities = BlockUtil.getTileEntities();
        for (BlockEntity blockEntity : blockEntities) {
            if (!(blockEntity instanceof ChestBlockEntity)) continue;
            Box box = new Box(blockEntity.getPos());
            Render3DUtil.draw3DBox(matrixStack, box, this.color.getValue());
        }
    }
}

