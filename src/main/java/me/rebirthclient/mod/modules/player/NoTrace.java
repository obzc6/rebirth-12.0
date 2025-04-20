/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.PickaxeItem
 *  net.minecraft.item.SwordItem
 */
package me.rebirthclient.mod.modules.player;

import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;

public class NoTrace
extends Module {
    public static NoTrace INSTANCE;
    public final BooleanSetting onlyPickaxe = this.add(new BooleanSetting("OnlyPickaxe", true));

    public NoTrace() {
        super("NoTrace", Module.Category.Player);
        INSTANCE = this;
    }

    public boolean canWork() {
        if (this.isOff()) {
            return false;
        }
        if (this.onlyPickaxe.getValue()) {
            return NoTrace.mc.player.getMainHandStack().getItem() instanceof PickaxeItem || NoTrace.mc.player.isUsingItem() && !(NoTrace.mc.player.getMainHandStack().getItem() instanceof SwordItem);
        }
        return true;
    }
}

