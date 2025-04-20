/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package me.rebirthclient.mod.gui.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.Objects;
import me.rebirthclient.mod.gui.font.FontAdapter;
import me.rebirthclient.mod.gui.font.RendererFontAdapter;
import org.jetbrains.annotations.NotNull;

public class FontRenderers {
    public static FontAdapter Arial;

    @NotNull
    public static RendererFontAdapter createDefault(float size, String name) throws IOException, FontFormatException {
        return new RendererFontAdapter(Font.createFont(0, Objects.requireNonNull(FontRenderers.class.getClassLoader().getResourceAsStream("assets/rebirth/fonts/" + name + ".ttf"))).deriveFont(0, size / 2.0f), size / 2.0f);
    }

    public static RendererFontAdapter createArial(float size) {
        return new RendererFontAdapter(new Font("Arial", 0, (int)(size / 2.0f)), size / 2.0f);
    }
}

