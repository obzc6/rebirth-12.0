/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.Selectable
 *  net.minecraft.client.gui.widget.ElementListWidget
 *  net.minecraft.client.gui.widget.ElementListWidget$Entry
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.util.Util
 *  org.jetbrains.annotations.Nullable
 */
package me.rebirthclient.mod.gui.screens.alts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.alts.Alt;
import me.rebirthclient.mod.gui.screens.alts.AltScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class AltSelectionList
        extends ElementListWidget<AltSelectionList.Entry> {
    private final AltScreen owner;
    private final List<NormalEntry> altList = new ArrayList<NormalEntry>();

    public AltSelectionList(AltScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
        this.owner = ownerIn;
    }

    public void updateAlts() {
        this.clearEntries();
        for (Alt alt : Rebirth.ALT.getAlts()) {
            NormalEntry entry = new NormalEntry(this.owner, alt);
            this.altList.add(entry);
        }
        this.setList();
    }
    private void setList() {
        this.altList.forEach(this::addEntry);
    }

    public void setSelected(@Nullable AltSelectionList.Entry entry) {
        super.setSelected(entry);
    }


    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Entry AltSelectionList$entry = (Entry)this.getSelectedOrNull();
        return AltSelectionList$entry != null && AltSelectionList$entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public class NormalEntry
            extends Entry {
        private final AltScreen owner;
        private final MinecraftClient mc;
        private final Alt alt;
        private long lastClickTime;

        protected NormalEntry(AltScreen ownerIn, Alt alt) {
            this.owner = ownerIn;
            this.alt = alt;
            this.mc = MinecraftClient.getInstance();
        }

        public Alt getAltData() {
            return this.alt;
        }

        public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String description = "Cracked Account";
            TextRenderer textRenderer = this.mc.textRenderer;
            drawContext.drawTextWithShadow(textRenderer, "Username: " + this.alt.getEmail(), x + 32 + 3, y + 2, 0xFFFFFF);
            drawContext.drawTextWithShadow(textRenderer, "Username: " + this.alt.getEmail(), x + 32 + 3, y + 2, 0xFFFFFF);
            drawContext.drawText(textRenderer, description, x + 32 + 3, y + 12, 0xFF0000, true);
        }

        public List<? extends Element> children() {
            return Collections.emptyList();
        }

        public List<? extends Selectable> selectableChildren() {
            return Collections.emptyList();
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double d0 = mouseX - (double)AltSelectionList.this.getRowLeft();
            if (d0 <= 32.0 && d0 < 32.0 && d0 > 16.0) {
                this.owner.setSelected(this);
                this.owner.loginToSelected();
                return true;
            }
            this.owner.setSelected(this);
            if (Util.getMeasuringTimeMs() - this.lastClickTime < 250L) {
                this.owner.loginToSelected();
            }
            this.lastClickTime = Util.getMeasuringTimeMs();
            return false;
        }
    }

    public static abstract class Entry
            extends ElementListWidget.Entry<Entry> {
    }
}

