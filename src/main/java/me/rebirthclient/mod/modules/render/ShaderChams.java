/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.SpawnGroup
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.MathHelper
 */
package me.rebirthclient.mod.modules.render;

import java.awt.Color;
import java.lang.reflect.Field;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.managers.ShaderManager;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.Setting;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class ShaderChams
extends Module {
    public static ShaderChams INSTANCE;
    private final EnumSetting page = new EnumSetting("Page", Page.Shader);
    public EnumSetting mode = new EnumSetting("Mode", ShaderManager.Shader.Default, v -> this.page.getValue() == Page.Shader);
    public EnumSetting handsMode = new EnumSetting("HandsMode", ShaderManager.Shader.Default, v -> this.page.getValue() == Page.Shader);
    public EnumSetting skyMode = new EnumSetting("SkyMode", ShaderManager.Shader.Default, v -> this.page.getValue() == Page.Shader);
    public final SliderSetting speed = new SliderSetting("Speed", 4.0, 0.0, 20.0, 0.1, v -> this.page.getValue() == Page.Shader);
    public final BooleanSetting glow = new BooleanSetting("Glow", true, v -> this.page.getValue() == Page.Shader);
    public final SliderSetting quality = new SliderSetting("Quality", 3, 0, 20, v -> this.glow.getValue() && this.page.getValue() == Page.Shader);
    public final SliderSetting lineWidth = new SliderSetting("LineWidth", 2, 0, 20, v -> this.page.getValue() == Page.Shader);
    public final SliderSetting factor = new SliderSetting("GradientFactor", 2.0, 0.0, 20.0, v -> this.page.getValue() == Page.Shader);
    public final SliderSetting gradient = new SliderSetting("Gradient", 2.0, 0.0, 20.0, v -> this.page.getValue() == Page.Shader);
    public final SliderSetting alpha2 = new SliderSetting("GradientAlpha", 170, 0, 255, v -> this.page.getValue() == Page.Shader);
    public final SliderSetting octaves = new SliderSetting("Octaves", 10, 5, 30, v -> this.page.getValue() == Page.Shader);
    public final SliderSetting fillAlpha = new SliderSetting("Alpha", 170, 0, 255, v -> this.page.getValue() == Page.Shader);
    public final ColorSetting outlineColor = new ColorSetting("Outline", new Color(255, 255, 255), v -> this.page.getValue() == Page.Shader);
    public final ColorSetting smokeGlow = new ColorSetting("SmokeGlow", new Color(255, 255, 255), v -> this.page.getValue() == Page.Shader);
    public final ColorSetting smokeGlow1 = new ColorSetting("SmokeGlow", new Color(255, 255, 255), v -> this.page.getValue() == Page.Shader);
    public final ColorSetting fillColor2 = new ColorSetting("SmokeFill", new Color(255, 255, 255), v -> this.page.getValue() == Page.Shader);
    public final ColorSetting fillColor3 = new ColorSetting("SmokeFil2", new Color(255, 255, 255), v -> this.page.getValue() == Page.Shader);
    public final ColorSetting fill = new ColorSetting("Fill", new Color(255, 255, 255), v -> this.page.getValue() == Page.Shader);
    public final BooleanSetting sky = new BooleanSetting("Sky[!]", false, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting hands = new BooleanSetting("Hands", true, v -> this.page.getValue() == Page.Target);
    public final SliderSetting maxRange = new SliderSetting("MaxRange", 64, 16, 512, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting self = new BooleanSetting("Self", true, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting players = new BooleanSetting("Players", true, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting friends = new BooleanSetting("Friends", true, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting crystals = new BooleanSetting("Crystals", true, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting creatures = new BooleanSetting("Creatures", false, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting monsters = new BooleanSetting("Monsters", false, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting ambients = new BooleanSetting("Ambients", false, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting items = new BooleanSetting("Items", true, v -> this.page.getValue() == Page.Target);
    private final BooleanSetting others = new BooleanSetting("Others", false, v -> this.page.getValue() == Page.Target);

    public ShaderChams() {
        super("ShaderChams", Module.Category.Render);
        try {
            for (Field field : ShaderChams.class.getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType())) continue;
                Setting setting = (Setting)field.get(this);
                this.addSetting(setting);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        INSTANCE = this;
    }

    public boolean shouldRender(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (ShaderChams.mc.player == null) {
            return false;
        }
        if ((double)MathHelper.sqrt((float)((float)ShaderChams.mc.player.squaredDistanceTo(entity.getPos()))) > this.maxRange.getValue()) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            if (entity == ShaderChams.mc.player) {
                return this.self.getValue();
            }
            if (Rebirth.FRIEND.isFriend((PlayerEntity)entity)) {
                return this.friends.getValue();
            }
            return this.players.getValue();
        }
        if (entity instanceof EndCrystalEntity) {
            return this.crystals.getValue();
        }
        if (entity instanceof ItemEntity) {
            return this.items.getValue();
        }
        return switch (entity.getType().getSpawnGroup()) {
            case CREATURE, WATER_CREATURE -> this.creatures.getValue();
            case MONSTER -> this.monsters.getValue();
            case AMBIENT, WATER_AMBIENT -> this.ambients.getValue();
            default -> this.others.getValue();
        };
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        if (this.hands.getValue()) {
            Rebirth.SHADER.renderShader(() -> ShaderChams.mc.gameRenderer.renderHand(matrixStack, ShaderChams.mc.gameRenderer.getCamera(), mc.getTickDelta()), (ShaderManager.Shader)this.handsMode.getValue());
        }
    }

    @Override
    public void onDisable() {
        Rebirth.SHADER.reloadShaders();
    }

    private static enum Page {
        Shader,
        Target;

        // $FF: synthetic method
        private static ShaderChams.Page[] $values() {
            return new ShaderChams.Page[]{Shader, Target};
        }
    }
}


