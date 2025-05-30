/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider
 *  net.minecraft.client.render.DiffuseLighting
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.text.Text
 *  net.minecraft.util.collection.DefaultedList
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.GL11
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.rebirthclient.asm.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.mod.modules.miscellaneous.ShulkerViewer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={HandledScreen.class})
public abstract class MixinHandledScreen<T extends ScreenHandler>
extends Screen
implements ScreenHandlerProvider<T> {
    @Shadow
    @Nullable
    protected Slot focusedSlot;
    @Shadow
    protected int x;
    @Shadow
    protected int y;

    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Inject(method={"render"}, at={@At(value="TAIL")})
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.focusedSlot != null && !this.focusedSlot.getStack().isEmpty() && this.client.player.playerScreenHandler.getCursorStack().isEmpty() && this.hasItems(this.focusedSlot.getStack()) && ShulkerViewer.INSTANCE.isOn()) {
            this.renderShulkerToolTip(context, mouseX, mouseY, this.focusedSlot.getStack());
        }
    }

    public void renderShulkerToolTip(DrawContext context, int mouseX, int mouseY, ItemStack stack) {
        try {
            NbtCompound compoundTag = stack.getSubNbt("BlockEntityTag");
            DefaultedList itemStacks = DefaultedList.ofSize((int)27, (Object)ItemStack.EMPTY);
            Inventories.readNbt((NbtCompound)compoundTag, (DefaultedList)itemStacks);
            this.draw(context, (DefaultedList<ItemStack>)itemStacks, mouseX, mouseY);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void draw(DrawContext context, DefaultedList<ItemStack> itemStacks, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        GL11.glClear((int)256);
        this.drawBackground(context, mouseX += 8, mouseY -= 82);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        DiffuseLighting.enableGuiDepthLighting();
        int row = 0;
        int i = 0;
        for (ItemStack itemStack : itemStacks) {
            context.drawItem(itemStack, mouseX + 8 + i * 18, mouseY + 7 + row * 18);
            context.drawItemInSlot(Wrapper.mc.textRenderer, itemStack, mouseX + 8 + i * 18, mouseY + 7 + row * 18);
            if (++i < 9) continue;
            i = 0;
            ++row;
        }
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.enableDepthTest();
    }

    private void drawBackground(DrawContext context, int x, int y) {
        Render2DUtil.drawRect(context.getMatrices(), (float)x, (float)y, 176.0f, 67.0f, new Color(0, 0, 0, 120));
    }

    private boolean hasItems(ItemStack itemStack) {
        NbtCompound compoundTag = itemStack.getSubNbt("BlockEntityTag");
        return compoundTag != null && compoundTag.contains("Items", 9);
    }
}

