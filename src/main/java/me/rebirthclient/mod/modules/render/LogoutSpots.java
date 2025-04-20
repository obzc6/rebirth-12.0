/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerListS2CPacket$Action
 *  net.minecraft.network.packet.s2c.play.PlayerListS2CPacket$Entry
 *  net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.render;

import com.google.common.collect.Maps;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.Render2DUtil;
import me.rebirthclient.api.util.Render3DUtil;
import me.rebirthclient.api.util.TextUtil;
import me.rebirthclient.asm.accessors.IEntity;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.ColorSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LogoutSpots
        extends Module {
    private final ColorSetting color = this.add(new ColorSetting("Color", new Color(255, 255, 255, 100)));
    private final BooleanSetting box = this.add(new BooleanSetting("Box", true));
    private final BooleanSetting outline = this.add(new BooleanSetting("Outline", true));
    private final BooleanSetting text = this.add(new BooleanSetting("Text", true));
    private final SliderSetting textScaled = this.add(new SliderSetting("TextScaled", 1.0, 0.0, 2.0, 0.1, v -> this.text.getValue()));
    private final BooleanSetting rect = this.add(new BooleanSetting("Rect", true, v -> this.text.getValue()));
    private final BooleanSetting message = this.add(new BooleanSetting("Message", true));
    private final Map<UUID, PlayerEntity> playerCache = Maps.newConcurrentMap();
    private final Map<UUID, PlayerEntity> logoutCache = Maps.newConcurrentMap();

    public LogoutSpots() {
        super("LogoutSpots", Module.Category.Render);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        PlayerEntity player;
        PlayerListS2CPacket packet;
        Object object = event.getPacket();
        if (object instanceof PlayerListS2CPacket) {
            packet = (PlayerListS2CPacket)object;
            if (packet.getActions().contains((Object)PlayerListS2CPacket.Action.ADD_PLAYER)) {
                object = packet.getPlayerAdditionEntries().iterator();
                while (((Iterator<?>) object).hasNext()) {
                    PlayerListS2CPacket.Entry addedPlayer = (PlayerListS2CPacket.Entry) ((Iterator<?>) object).next();
                    for (UUID uuid : this.logoutCache.keySet()) {
                        if (!uuid.equals(addedPlayer.profile().getId())) continue;
                        player = this.logoutCache.get(uuid);
                        if (this.message.getValue()) {
                            CommandManager.sendChatMessage("\u00a7e[!] \u00a7b" + player.getName().getString() + " \u00a7alogged back at X: " + (int)player.getX() + " Y: " + (int)player.getY() + " Z: " + (int)player.getZ());
                        }
                        this.logoutCache.remove(uuid);
                    }
                }
            }
            this.playerCache.clear();
        }
        if ((object = event.getPacket()) instanceof PlayerRemoveS2CPacket) {
            packet = (PlayerListS2CPacket) object;
                for (UUID uuid : this.playerCache.keySet()) {
                    UUID uuid2 = null;
                    if (!uuid.equals(uuid2)) continue;
                    player = this.playerCache.get(uuid);
                    if (this.logoutCache.containsKey(uuid)) continue;
                    if (this.message.getValue()) {
                        CommandManager.sendChatMessage("\u00a7e[!] \u00a7b" + player.getName().getString() + " \u00a7clogged out at X: " + (int)player.getX() + " Y: " + (int)player.getY() + " Z: " + (int)player.getZ());
                    }
                    this.logoutCache.put(uuid, player);
                }

            this.playerCache.clear();
        }
    }

    @Override
    public void onEnable() {
        this.playerCache.clear();
        this.logoutCache.clear();
    }

    @Override
    public void onUpdate() {
        for (PlayerEntity player : LogoutSpots.mc.world.getPlayers()) {
            if (player == null || player.equals((Object)LogoutSpots.mc.player)) continue;
            this.playerCache.put(player.getGameProfile().getId(), player);
        }
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        for (UUID uuid : this.logoutCache.keySet()) {
            PlayerEntity data = this.logoutCache.get(uuid);
            if (data == null) continue;
            Render3DUtil.draw3DBox(matrixStack, ((IEntity)data).getDimensions().getBoxAt(data.getPos()), this.color.getValue(), this.outline.getValue(), this.box.getValue());
        }
    }

    @Override
    public void onRender2D(DrawContext drawContext, float tickDelta) {
        if (!this.text.getValue()) {
            return;
        }
        for (UUID uuid : this.logoutCache.keySet()) {
            PlayerEntity player = this.logoutCache.get(uuid);
            if (player == null) continue;
            this.drawText(drawContext, player.getName().getString() + " \u00a7a" + new DecimalFormat("0.0").format(EntityUtil.getHealth((Entity)player)) + " \u00a7clogged", player.getPos().add(0.0, player.getBoundingBox().getYLength() + 0.4, 0.0), Rebirth.FRIEND.isFriend(player));
        }
    }

    public void drawText(DrawContext context, String text, Vec3d vector, boolean friend) {
        Vec3d preVec = vector;
        vector = TextUtil.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
        if (vector.z > 0.0 && vector.z < 1.0) {
            double posX = vector.x;
            double posY = vector.y;
            double endPosX = Math.max(vector.x, vector.z);
            float scale = (float)Math.max(1.0 - (double)MathHelper.sqrt((float)((float)EntityUtil.getEyesPos().squaredDistanceTo(preVec))) * 0.01 * this.textScaled.getValue(), 0.0);
            float diff = (float)(endPosX - posX) / 2.0f;
            float textWidth = (float)LogoutSpots.mc.textRenderer.getWidth(text) * scale;
            float tagX = (float)(posX + (double)diff - (double)(textWidth / 2.0f));
            context.getMatrices().push();
            context.getMatrices().scale(scale, scale, scale);
            Objects.requireNonNull(LogoutSpots.mc.textRenderer);
            double y = (posY - 11.0 + 9.0 * 1.2) / (double)scale;
            if (this.rect.getValue()) {
                Render2DUtil.drawRect(context.getMatrices(), (float)((int)(tagX / scale) - 2), (float)((int)y - 3), (float)(LogoutSpots.mc.textRenderer.getWidth(text) + 4), 14.0f, new Color(-1728053247, true));
            }
            context.drawText(LogoutSpots.mc.textRenderer, text, (int)(tagX / scale), (int)y, friend ? new Color(0, 255, 0).getRGB() : -1, true);
            context.getMatrices().pop();
        }
    }
}

