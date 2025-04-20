/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.Icons
 *  net.minecraft.client.util.Window
 *  net.minecraft.resource.InputSupplier
 *  net.minecraft.resource.ResourcePack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package me.rebirthclient.asm.mixins;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.Window;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={Window.class})
public class MixinWindow {
    @Redirect(method={"setIcon"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/util/Icons;getIcons(Lnet/minecraft/resource/ResourcePack;)Ljava/util/List;"))
    private List<InputSupplier<InputStream>> setupIcon(Icons instance, ResourcePack resourcePack) throws IOException {
        InputStream stream16 = MixinWindow.class.getResourceAsStream("/assets/rebirth/icon_16x16.png");
        InputStream stream32 = MixinWindow.class.getResourceAsStream("/assets/rebirth/icon_32x32.png");
        if (stream16 == null || stream32 == null) {
            return instance.getIcons(resourcePack);
        }
        return List.of(() -> stream16, () -> stream32);
    }
}

