/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.rebirthclient.api.util.BlockPosX;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class DefenseESP
extends Module {
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));
    private final BooleanSetting box = this.add(new BooleanSetting("Box", true));
    private final BooleanSetting outline = this.add(new BooleanSetting("Outline", true));
    private final BooleanSetting burrow = this.add(new BooleanSetting("Burrow", true));
    private final BooleanSetting surround = this.add(new BooleanSetting("Surround", true));
    List<BlockPos> renderList = new ArrayList<BlockPos>();

    public DefenseESP() {
        super("DefenseESP", Module.Category.Render);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        this.renderList.clear();
        for (Entity entity : CombatUtil.getEnemies(10.0)) {
            BlockPos pos;
            if (this.burrow.getValue()) {
                float[] offset;
                for (float x : offset = new float[]{-0.3f, 0.0f, 0.3f}) {
                    for (float z : offset) {
                        BlockPosX tempPos = new BlockPosX(entity.getPos().add((double)x, 0.0, (double)z));
                        if (!this.isObsidian(tempPos)) continue;
                        this.renderList.add(tempPos);
                    }
                }
            }
            if (!this.surround.getValue() || !BlockUtil.isHole(pos = EntityUtil.getEntityPos(entity, true))) continue;
            for (Direction i : Direction.values()) {
                if (i == Direction.UP || i == Direction.DOWN || !this.isObsidian(pos.offset(i))) continue;
                this.renderList.add(pos.offset(i));
            }
        }
        for (BlockPos blockPos : this.renderList) {
            Render3DUtil.draw3DBox(matrixStack, new Box(blockPos), this.color.getValue(), this.outline.getValue(), this.box.getValue());
        }
    }

    private boolean isObsidian(BlockPos pos) {
        return (BlockUtil.getBlock(pos) == Blocks.OBSIDIAN || BlockUtil.getBlock(pos) == Blocks.ENDER_CHEST) && !this.renderList.contains((Object)pos);
    }
}

