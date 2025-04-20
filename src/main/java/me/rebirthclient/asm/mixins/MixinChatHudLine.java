/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.hud.ChatHudLine
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package me.rebirthclient.asm.mixins;

import me.rebirthclient.api.interfaces.IChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={ChatHudLine.class})
public abstract class MixinChatHudLine
implements IChatHudLine {
    @Unique
    private int id = 0;

    @Override
    public int rebirth_nextgen_master$getId() {
        return this.id;
    }

    @Override
    public void rebirth_nextgen_master$setId(int id) {
        this.id = id;
    }
}

