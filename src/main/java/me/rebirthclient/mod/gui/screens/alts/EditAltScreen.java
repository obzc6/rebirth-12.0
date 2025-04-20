/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CheckboxWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.text.Text
 */
package me.rebirthclient.mod.gui.screens.alts;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.alts.Alt;
import me.rebirthclient.mod.gui.screens.alts.AltScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class EditAltScreen
extends Screen {
    private final AltScreen parent;
    private Alt alt;
    private ButtonWidget buttonSaveAlt;
    private CheckboxWidget toggleMicrosoft;
    private TextFieldWidget textFieldAltUsername;

    public EditAltScreen(AltScreen parentScreen, Alt alt) {
        super(Text.of((String)"Alt Manager"));
        this.parent = parentScreen;
        this.alt = alt;
    }

    public void init() {
        super.init();
        this.textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 76, 200, 20,
                Text.of("Enter Name"));
        this.textFieldAltUsername.setText(this.alt == null ? "" : alt.getEmail());
        this.addDrawableChild(this.textFieldAltUsername);

        addDrawableChild(this.buttonSaveAlt = ButtonWidget.builder(Text.of("Save Alt"), b -> this.onButtonAltEditPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 24, 200, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> this.onButtonCancelPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 46, 200, 20).build());
    }

    public void tick() {
        this.textFieldAltUsername.tick();
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(drawContext);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, "Edit Alternate Account", this.width / 2, 20, 0xFFFFFF);
        drawContext.drawTextWithShadow(this.textRenderer, "Username:", this.width / 2 - 100, this.height / 2 - 90, 0xFFFFFF);
        super.render(drawContext, mouseX, mouseY, partialTicks);
    }

    private void onButtonAltEditPressed() {
        this.alt.setEmail(this.textFieldAltUsername.getText());
        Rebirth.ALT.saveAlts();
        this.parent.refreshAltList();
    }

    public void onButtonCancelPressed() {
        this.client.setScreen((Screen)this.parent);
    }
}

