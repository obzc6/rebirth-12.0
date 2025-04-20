/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.WorldChunk
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.api.util.CombatUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.player.GhostHand;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={World.class})
public abstract class MixinWorld {
    @Inject(method={"getBlockState"}, at={@At(value="HEAD")}, cancellable=true)
    public void blockStateHook(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (Wrapper.mc.world != null && Wrapper.mc.world.isInBuildLimit(pos)) {
            WorldChunk worldChunk;
            BlockState tempState;
            if (CombatUtil.terrainIgnore) {
                WorldChunk worldChunk2 = Wrapper.mc.world.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
                BlockState tempState2 = worldChunk2.getBlockState(pos);
                if (tempState2.getBlock() == Blocks.OBSIDIAN || tempState2.getBlock() == Blocks.BEDROCK || tempState2.getBlock() == Blocks.ENDER_CHEST || tempState2.getBlock() == Blocks.RESPAWN_ANCHOR) {
                    return;
                }
                cir.setReturnValue(Blocks.AIR.getDefaultState());
            } else if (GhostHand.INSTANCE.isActive && (tempState = (worldChunk = Wrapper.mc.world.getChunk(pos.getX() >> 4, pos.getZ() >> 4)).getBlockState(pos)).getBlock() == Blocks.BEDROCK) {
                cir.setReturnValue(Blocks.AIR.getDefaultState());
            }
        }
    }
}

