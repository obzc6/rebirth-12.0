/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.text.Text
 */
package me.rebirthclient.mod.gui.screens.alts;

import me.rebirthclient.Rebirth;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class TokenLoginScreen
extends Screen {
    private final Screen parent;
    private TextFieldWidget textFieldAltName;
    private TextFieldWidget textFieldAltToken;
    private TextFieldWidget textFieldAltUID;

    protected TokenLoginScreen(Screen parent) {
        super(Text.of((String)"Token Login"));
        this.parent = parent;
    }

    public void init() {
        super.init();

        this.textFieldAltName = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 + 4, 200, 20, Text.of("Enter Name"));
        this.addDrawableChild(this.textFieldAltName);

        this.textFieldAltToken = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 76, 200, 20, Text.of("Enter Token"));
        this.textFieldAltToken.setMaxLength(Integer.MAX_VALUE);
        this.addDrawableChild(this.textFieldAltToken);

        this.textFieldAltUID = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 36, 200, 20, Text.of("Enter UID"));
        this.textFieldAltUID.setMaxLength(Integer.MAX_VALUE);
        this.addDrawableChild(this.textFieldAltUID);

        addDrawableChild(ButtonWidget.builder(Text.of("Login"), b -> this.onButtonLoginPressed()).dimensions(this.width / 2 - 100, this.height / 2 + 24 + 8, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(this.parent))
                .dimensions(this.width / 2 - 100, this.height / 2 + 46 + 8, 200, 20).build());
    }

    public void tick() {
        this.textFieldAltName.tick();
        this.textFieldAltToken.tick();
        this.textFieldAltUID.tick();
    }

    private void onButtonLoginPressed() {
        Rebirth.ALT.loginToken(this.textFieldAltName.getText(), this.textFieldAltToken.getText(), this.textFieldAltUID.getText());
        this.client.setScreen(this.parent);
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(drawContext);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, this.title.getString(), this.width / 2, 20, 0xFFFFFF);
        drawContext.drawTextWithShadow(this.textRenderer, "Enter Token", this.width / 2 - 100, this.height / 2 - 90, 0xFFFFFF);
        drawContext.drawTextWithShadow(this.textRenderer, "Enter UUID", this.width / 2 - 100, this.height / 2 - 50, 0xFFFFFF);
        drawContext.drawTextWithShadow(this.textRenderer, "Enter Name", this.width / 2 - 100, this.height / 2 - 10, 0xFFFFFF);
        this.textFieldAltName.render(drawContext, mouseX, mouseY, partialTicks);
        this.textFieldAltToken.render(drawContext, mouseX, mouseY, partialTicks);
        this.textFieldAltUID.render(drawContext, mouseX, mouseY, partialTicks);
        super.render(drawContext, mouseX, mouseY, partialTicks);
    }
}

