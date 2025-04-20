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
import me.rebirthclient.api.alts.Alt;
import me.rebirthclient.mod.gui.screens.alts.AltScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AddAltScreen
        extends Screen {
    private final AltScreen parent;
    private TextFieldWidget textFieldAltUsername;

    public AddAltScreen(AltScreen parentScreen) {
        super(Text.of((String)"Alt Manager"));
        this.parent = parentScreen;
    }

    public void init() {
        super.init();

        textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 76, 200, 20,
                Text.of("Enter Name"));
        textFieldAltUsername.setText("");
        addDrawableChild(this.textFieldAltUsername);

        addDrawableChild(ButtonWidget.builder(Text.of("Add Alt"), b -> this.onButtonAltAddPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 24, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> this.onButtonCancelPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 46, 200, 20).build());
    }


    public void tick() {
        this.textFieldAltUsername.tick();
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, "Add Alternate Account", this.width / 2, 20, 0xFFFFFF);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, "Username:", this.width / 2 - 100, this.height / 2 - 90, 0xFFFFFF);
        super.render(drawContext, mouseX, mouseY, delta);
    }

    private void onButtonAltAddPressed() {
        Alt alt = new Alt(this.textFieldAltUsername.getText());
        Rebirth.ALT.addAlt(alt);
        this.parent.refreshAltList();
    }

    public void onButtonCancelPressed() {
        this.client.setScreen((Screen)this.parent);
    }
}

