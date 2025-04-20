/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.fluid.FlowableFluid
 *  net.minecraft.util.math.Direction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package me.rebirthclient.asm.mixins;

import java.util.Iterator;
import me.rebirthclient.mod.modules.movement.Velocity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FlowableFluid.class})
public class MixinFlowableFluid {
    @Redirect(method={"getVelocity"}, at=@At(value="INVOKE", target="Ljava/util/Iterator;hasNext()Z", ordinal=0))
    private boolean getVelocity_hasNext(Iterator<Direction> var9) {
        if (Velocity.INSTANCE.isOn() && Velocity.INSTANCE.waterPush.getValue()) {
            return false;
        }
        return var9.hasNext();
    }
}

