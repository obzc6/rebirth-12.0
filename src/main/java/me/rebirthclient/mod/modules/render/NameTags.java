/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.render.DiffuseLighting
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.GameMode
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector4d
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.mod.gui.font.FontRenderers;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4d;

public class NameTags
        extends Module {
    public static NameTags INSTANCE;
    private final SliderSetting scale = this.add(new SliderSetting("Scale", (double)0.68f, (double)0.1f, 2.0, 0.01));
    private final SliderSetting minScale = this.add(new SliderSetting("MinScale", (double)0.2f, (double)0.1f, 1.0, 0.01));
    private final SliderSetting scaled = this.add(new SliderSetting("Scaled", 1.0, 0.0, 2.0, 0.01));
    private final SliderSetting offset = this.add(new SliderSetting("Offset", (double)0.315f, (double)0.001f, 1.0, 0.001));
    private final SliderSetting height = this.add(new SliderSetting("Height", 0.0, -3.0, 3.0, 0.01f));
    private final BooleanSetting gamemode = this.add(new BooleanSetting("Gamemode", false));
    private final BooleanSetting ping = this.add(new BooleanSetting("Ping", false));
    private final BooleanSetting health = this.add(new BooleanSetting("Health", true));
    private final BooleanSetting getDistance = this.add(new BooleanSetting("Distance", true));
    private final BooleanSetting pops = this.add(new BooleanSetting("TotemPops", true));
    private final BooleanSetting enchants = this.add(new BooleanSetting("Enchants", true));
    private final ColorSetting outline = this.add(new ColorSetting("Outline", new Color(-1711276033, true)).injectBoolean(true));
    private final ColorSetting rect = this.add(new ColorSetting("Rect", new Color(-1728053247, true)).injectBoolean(true));
    private final ColorSetting friendColor = this.add(new ColorSetting("FriendColor", new Color(-14811363, true)));
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(-1, true)));
    public final EnumSetting font = this.add(new EnumSetting("FontMode", Font.Fast));
    private final SliderSetting armorHeight = this.add(new SliderSetting("ArmorHeight", 0.3f, -10.0, 10.0));
    private final SliderSetting armorScale = this.add(new SliderSetting("ArmorScale", (double)0.9f, (double)0.1f, 2.0, 0.01f));
    private final EnumSetting armorMode = this.add(new EnumSetting("ArmorMode", Armor.Full));

    public NameTags() {
        super("NameTags", Module.Category.Render);
        INSTANCE = this;
    }

    @Override
    public void onRender2D(DrawContext context, float tickDelta) {
        for (PlayerEntity ent : NameTags.mc.world.getPlayers()) {
            Vec3d vector;
            if (ent == NameTags.mc.player && NameTags.mc.options.getPerspective().isFirstPerson()) continue;
            double x = ent.prevX + (ent.getX() - ent.prevX) * (double)mc.getTickDelta();
            double y = ent.prevY + (ent.getY() - ent.prevY) * (double)mc.getTickDelta();
            double z = ent.prevZ + (ent.getZ() - ent.prevZ) * (double)mc.getTickDelta();
            Vec3d preVec = vector = new Vec3d(x, y + this.height.getValue() + ent.getBoundingBox().getYLength() + 0.3, z);
            vector = TextUtil.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
            if (!(vector.z > 0.0) || !(vector.z < 1.0)) continue;
            Vector4d position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
            position.x = Math.min(vector.x, position.x);
            position.y = Math.min(vector.y, position.y);
            position.z = Math.max(vector.x, position.z);
            Object final_string = "";
            if (this.ping.getValue()) {
                final_string = (String)final_string + NameTags.getEntityPing(ent) + "ms ";
            }
            if (this.gamemode.getValue()) {
                final_string = (String)final_string + this.translateGamemode(NameTags.getEntityGamemode(ent)) + " ";
            }
            final_string = (String)final_string + Formatting.RESET + ent.getName().getString();
            if (this.health.getValue()) {
                final_string = (String)final_string + " " + this.getHealthColor(ent) + NameTags.round2(ent.getAbsorptionAmount() + ent.getHealth());
            }
            if (this.getDistance.getValue()) {
                final_string = (String)final_string + " " + Formatting.RESET + String.format("%.1f", Float.valueOf(NameTags.mc.player.distanceTo((Entity)ent))) + "m";
            }
            if (this.pops.getValue() && Rebirth.POP.getPop(ent.getName().getString()) != 0) {
                final_string = (String)final_string + " \u00a7bPop " + Formatting.LIGHT_PURPLE + Rebirth.POP.getPop(ent.getName().getString());
            }
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            float diff = (float)(endPosX - posX) / 2.0f;
            float textWidth = this.font.getValue() == Font.Fancy ? FontRenderers.Arial.getWidth((String)final_string) * 1.0f : (float)NameTags.mc.textRenderer.getWidth((String)final_string);
            float tagX = (float)((posX + (double)diff - (double)(textWidth / 2.0f)) * 1.0);
            ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
            stacks.add(ent.getMainHandStack());
            stacks.add((ItemStack)ent.getInventory().armor.get(3));
            stacks.add((ItemStack)ent.getInventory().armor.get(2));
            stacks.add((ItemStack)ent.getInventory().armor.get(1));
            stacks.add((ItemStack)ent.getInventory().armor.get(0));
            stacks.add(ent.getOffHandStack());
            context.getMatrices().push();
            context.getMatrices().translate(tagX - 2.0f + (textWidth + 4.0f) / 2.0f, (float)(posY - 13.0) + 6.5f, 0.0f);
            float size = (float)Math.max(1.0 - (double)MathHelper.sqrt((float)((float)EntityUtil.getEyesPos().squaredDistanceTo(preVec))) * 0.01 * this.scaled.getValue(), 0.0);
            context.getMatrices().scale(Math.max(this.scale.getValueFloat() * size, this.minScale.getValueFloat()), Math.max(this.scale.getValueFloat() * size, this.minScale.getValueFloat()), 1.0f);
            context.getMatrices().translate(0.0f, this.offset.getValueFloat() * MathHelper.sqrt((float)((float)EntityUtil.getEyesPos().squaredDistanceTo(preVec))), 0.0f);
            context.getMatrices().translate(-(tagX - 2.0f + (textWidth + 4.0f) / 2.0f), -((float)(posY - 13.0 + 6.5)), 0.0f);
            float item_offset = 0.0f;
            if (this.armorMode.getValue() != Armor.None) {
                for (ItemStack armorComponent : stacks) {
                    if (!armorComponent.isEmpty()) {
                        context.getMatrices().push();
                        context.getMatrices().translate(tagX - 2.0f + (textWidth + 4.0f) / 2.0f, (float)(posY - 13.0) + 6.5f, 0.0f);
                        context.getMatrices().scale(this.armorScale.getValueFloat(), this.armorScale.getValueFloat(), 1.0f);
                        context.getMatrices().translate(-(tagX - 2.0f + (textWidth + 4.0f) / 2.0f), -((float)(posY - 13.0 + 6.5)), 0.0f);
                        context.getMatrices().translate(posX - 52.5 + (double)item_offset, (double)((float)(posY - 29.0) + this.armorHeight.getValueFloat()), 0.0);
                        float durability = armorComponent.getMaxDamage() - armorComponent.getDamage();
                        int percent = (int)(durability / (float)armorComponent.getMaxDamage() * 100.0f);
                        Color color = percent <= 33 ? Color.RED : (percent <= 66 ? Color.YELLOW : Color.GREEN);
                        switch ((Armor)this.armorMode.getValue()) {
                            case Armor: {
                                DiffuseLighting.disableGuiDepthLighting();
                                context.drawItem(armorComponent, 0, 0);
                                context.drawItemInSlot(NameTags.mc.textRenderer, armorComponent, 0, 0);
                                break;
                            }
                            case Full: {
                                DiffuseLighting.disableGuiDepthLighting();
                                context.drawItem(armorComponent, 0, 0);
                                context.drawItemInSlot(NameTags.mc.textRenderer, armorComponent, 0, 0);
                                if (armorComponent.getMaxDamage() <= 0) break;
                                if (this.font.getValue() == Font.Fancy) {
                                    FontRenderers.Arial.drawString(context.getMatrices(), String.valueOf(percent), 9.0f - FontRenderers.Arial.getWidth(String.valueOf(percent)) / 2.0f, -FontRenderers.Arial.getFontHeight() + 3.0f, color.getRGB());
                                    break;
                                }
                                String string = String.valueOf(percent);
                                int n = 9 - NameTags.mc.textRenderer.getWidth(String.valueOf(percent)) / 2;
                                Objects.requireNonNull(NameTags.mc.textRenderer);
                                context.drawText(NameTags.mc.textRenderer, string, n, -9 + 1, color.getRGB(), true);
                                break;
                            }
                            case Durability: {
                                context.drawItemInSlot(NameTags.mc.textRenderer, armorComponent, 0, 0);
                                if (armorComponent.getMaxDamage() <= 0) break;
                                if (!armorComponent.isItemBarVisible()) {
                                    int i = armorComponent.getItemBarStep();
                                    int j = armorComponent.getItemBarColor();
                                    int k = 2;
                                    int l = 13;
                                    context.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
                                    context.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | 0xFF000000);
                                }
                                if (this.font.getValue() == Font.Fancy) {
                                    FontRenderers.Arial.drawString(context.getMatrices(), String.valueOf(percent), 9.0f - FontRenderers.Arial.getWidth(String.valueOf(percent)) / 2.0f, 7.0f, color.getRGB());
                                    break;
                                }
                                context.drawText(NameTags.mc.textRenderer, String.valueOf(percent), 9 - NameTags.mc.textRenderer.getWidth(String.valueOf(percent)) / 2, 5, color.getRGB(), true);
                            }
                        }
                        context.getMatrices().pop();
                        if (this.enchants.getValue()) {
                            float enchantmentY = 0.0f;
                            NbtList enchants = armorComponent.getEnchantments();
                            block25: for (int index = 0; index < enchants.size(); ++index) {
                                String encName;
                                String id = enchants.getCompound(index).getString("id");
                                short level = enchants.getCompound(index).getShort("lvl");
                                switch (id) {
                                    case "minecraft:blast_protection": {
                                        encName = "B" + level;
                                        break;
                                    }
                                    case "minecraft:protection": {
                                        encName = "P" + level;
                                        break;
                                    }
                                    case "minecraft:thorns": {
                                        encName = "T" + level;
                                        break;
                                    }
                                    case "minecraft:sharpness": {
                                        encName = "S" + level;
                                        break;
                                    }
                                    case "minecraft:efficiency": {
                                        encName = "E" + level;
                                        break;
                                    }
                                    case "minecraft:unbreaking": {
                                        encName = "U" + level;
                                        break;
                                    }
                                    case "minecraft:power": {
                                        encName = "PO" + level;
                                        break;
                                    }
                                    default: {
                                        continue block25;
                                    }
                                }
                                if (this.font.getValue() == Font.Fancy) {
                                    FontRenderers.Arial.drawString(context.getMatrices(), encName, posX - 50.0 + (double)item_offset, (double)((float)posY - 45.0f + enchantmentY), -1);
                                } else {
                                    context.getMatrices().push();
                                    context.getMatrices().translate(posX - 50.0 + (double)item_offset, posY - 45.0 + (double)enchantmentY, 0.0);
                                    context.drawText(NameTags.mc.textRenderer, encName, 0, 0, -1, true);
                                    context.getMatrices().pop();
                                }
                                enchantmentY -= 8.0f;
                            }
                        }
                    }
                    item_offset += 18.0f;
                }
            }
            if (this.rect.booleanValue) {
                Render2DUtil.drawRect(context.getMatrices(), tagX - 2.0f, (float)(posY - 13.0), textWidth + 4.0f, 11.0f, this.rect.getValue());
            }
            if (this.outline.booleanValue) {
                Render2DUtil.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), textWidth + 6.0f, 1.0f, this.outline.getValue());
                Render2DUtil.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 2.0), textWidth + 6.0f, 1.0f, this.outline.getValue());
                Render2DUtil.drawRect(context.getMatrices(), tagX - 3.0f, (float)(posY - 14.0), 1.0f, 12.0f, this.outline.getValue());
                Render2DUtil.drawRect(context.getMatrices(), tagX + textWidth + 2.0f, (float)(posY - 14.0), 1.0f, 12.0f, this.outline.getValue());
            }
            if (this.font.getValue() == Font.Fancy) {
                FontRenderers.Arial.drawString(context.getMatrices(), (String)final_string, tagX, (float)posY - 10.0f, Rebirth.FRIEND.isFriend(ent) ? this.friendColor.getValue().getRGB() : this.color.getValue().getRGB());
            } else {
                context.getMatrices().push();
                context.getMatrices().translate(tagX, (float)posY - 11.0f, 0.0f);
                context.drawText(NameTags.mc.textRenderer, (String)final_string, 0, 0, Rebirth.FRIEND.isFriend(ent) ? this.friendColor.getValue().getRGB() : this.color.getValue().getRGB(), true);
                context.getMatrices().pop();
            }
            context.getMatrices().pop();
        }
    }

    public static String getEntityPing(PlayerEntity entity) {
        if (mc.getNetworkHandler() == null) {
            return "-1";
        }
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        if (playerListEntry == null) {
            return "-1";
        }
        int ping = playerListEntry.getLatency();
        Formatting color = Formatting.GREEN;
        if (ping >= 100) {
            color = Formatting.YELLOW;
        }
        if (ping >= 250) {
            color = Formatting.RED;
        }
        return color.toString() + ping;
    }

    public static GameMode getEntityGamemode(PlayerEntity entity) {
        if (entity == null) {
            return null;
        }
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        return playerListEntry == null ? null : playerListEntry.getGameMode();
    }

    private String translateGamemode(GameMode gamemode) {
        if (gamemode == null) {
            return "\u00a77[BOT]";
        }
        return switch (gamemode) {
            default -> throw new IncompatibleClassChangeError();
            case SURVIVAL -> "\u00a7b[S]";
            case CREATIVE -> "\u00a7c[C]";
            case SPECTATOR -> "\u00a77[SP]";
            case ADVENTURE -> "\u00a7e[A]";
        };
    }

    private Formatting getHealthColor(@NotNull PlayerEntity entity) {
        int health = (int)((float)((int)entity.getHealth()) + entity.getAbsorptionAmount());
        if (health <= 15 && health > 7) {
            return Formatting.YELLOW;
        }
        if (health > 15) {
            return Formatting.GREEN;
        }
        return Formatting.RED;
    }

    public static float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static enum Font {
        Fancy,
        Fast;

        // $FF: synthetic method
        private static NameTags.Font[] $values() {
            return new NameTags.Font[]{Fancy, Fast};
        }
    }

    public static enum Armor {
        None,
        Full,
        Durability,
        Armor;

        // $FF: synthetic method
        private static NameTags.Armor[] $values() {
            return new NameTags.Armor[]{None, Full, Durability, Armor};
        }
    }
}
