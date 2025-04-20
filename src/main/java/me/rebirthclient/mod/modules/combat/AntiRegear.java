/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity
 *  net.minecraft.util.math.MathHelper
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.MathHelper;

public class AntiRegear
extends Module {
    private final SliderSetting safeRange = this.add(new SliderSetting("SafeRange", 2, 0, 8));
    private final SliderSetting range = this.add(new SliderSetting("Range", 5, 0, 8));

    public AntiRegear() {
        super("AntiRegear", "Shulker nuker", Module.Category.Combat);
    }

    @Override
    public void onUpdate() {
        if (PacketMine.breakPos != null && AntiRegear.mc.world.getBlockState(PacketMine.breakPos).getBlock() instanceof ShulkerBoxBlock) {
            return;
        }
        if (this.getBlock() != null) {
            PacketMine.INSTANCE.mine(this.getBlock().getPos());
        }
    }

    private ShulkerBoxBlockEntity getBlock() {
        for (BlockEntity entity : BlockUtil.getTileEntities()) {
            ShulkerBoxBlockEntity shulker;
            if (!(entity instanceof ShulkerBoxBlockEntity) || (double)MathHelper.sqrt((float)((float)AntiRegear.mc.player.squaredDistanceTo((shulker = (ShulkerBoxBlockEntity)entity).getPos().toCenterPos()))) <= this.safeRange.getValue() || !((double)MathHelper.sqrt((float)((float)AntiRegear.mc.player.squaredDistanceTo(shulker.getPos().toCenterPos()))) <= this.range.getValue())) continue;
            return shulker;
        }
        return null;
    }
}

