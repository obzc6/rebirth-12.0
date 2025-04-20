/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CactusBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.Rebirth;
import me.rebirthclient.mod.modules.player.AntiCactus;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={CactusBlock.class})
public abstract class MixinCactusBlock
extends Block {
    public MixinCactusBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Inject(at={@At(value="HEAD")}, method={"getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"}, cancellable=true)
    private void onGetCollisionShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, ShapeContext entityContext_1, CallbackInfoReturnable<VoxelShape> cir) {
        if (Rebirth.MODULE != null && AntiCactus.INSTANCE.isOn()) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }
}

