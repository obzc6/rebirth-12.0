/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ShulkerBoxScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 */
package me.rebirthclient.mod.modules.combat;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import me.rebirthclient.api.managers.CommandManager;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.PacketMine;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class AutoRegear
extends Module {
    private final BooleanSetting autoDisable = this.add(new BooleanSetting("AutoDisable", true));
    private final SliderSetting disableTime = this.add(new SliderSetting("DisableTime", 500, 0, 1000));
    public final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true));
    private final BooleanSetting place = this.add(new BooleanSetting("Place", true));
    private final BooleanSetting preferOpen = this.add(new BooleanSetting("PerferOpen", true));
    private final BooleanSetting open = this.add(new BooleanSetting("Open", true));
    private final SliderSetting range = this.add(new SliderSetting("Range", 4.0, 0.0, 6.0));
    private final SliderSetting minRange = this.add(new SliderSetting("MinRange", 1.0, 0.0, 3.0));
    private final BooleanSetting mine = this.add(new BooleanSetting("Mine", true));
    private final BooleanSetting take = this.add(new BooleanSetting("Take", true));
    private final BooleanSetting smart = this.add(new BooleanSetting("Smart", true, v -> this.take.getValue()).setParent());
    private final SliderSetting crystal = this.add(new SliderSetting("Crystal", 256, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting exp = this.add(new SliderSetting("Exp", 256, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting totem = this.add(new SliderSetting("Totem", 6, 0, 36, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting gapple = this.add(new SliderSetting("Gapple", 128, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting endChest = this.add(new SliderSetting("EndChest", 64, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting web = this.add(new SliderSetting("Web", 64, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting glowstone = this.add(new SliderSetting("Glowstone", 256, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting anchor = this.add(new SliderSetting("Anchor", 256, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting pearl = this.add(new SliderSetting("Pearl", 16, 0, 64, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting planks = this.add(new SliderSetting("Planks", 256, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    private final SliderSetting wool = this.add(new SliderSetting("Wool", 256, 0, 512, v -> this.take.getValue() && this.smart.isOpen()));
    final int[] stealCountList = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final Timer timer = new Timer();
    BlockPos placePos = null;
    private final Timer disableTimer = new Timer();
    boolean opend = false;

    public AutoRegear() {
        super("AutoRegear", "Auto place shulker and replenish", Module.Category.Combat);
    }

    public int findShulker() {
        AtomicInteger atomicInteger = new AtomicInteger(-1);
        if (InventoryUtil.findClass(ShulkerBoxBlock.class) != -1) {
            atomicInteger.set(InventoryUtil.findClass(ShulkerBoxBlock.class));
        }
        return atomicInteger.get();
    }

    @Override
    public void onEnable() {
        this.disableTimer.reset();
        this.placePos = null;
        if (AutoRegear.nullCheck()) {
            return;
        }
        int oldSlot = AutoRegear.mc.player.getInventory().selectedSlot;
        if (!this.place.getValue()) {
            return;
        }
        double distance = 100.0;
        BlockPos bestPos = null;
        for (BlockPos pos : BlockUtil.getSphere((float)this.range.getValue())) {
            if (!BlockUtil.isAir(pos.up())) continue;
            if (this.preferOpen.getValue() && AutoRegear.mc.world.getBlockState(pos).getBlock() instanceof ShulkerBoxBlock) {
                return;
            }
            if ((double)MathHelper.sqrt((float)((float)AutoRegear.mc.player.squaredDistanceTo(pos.toCenterPos()))) < this.minRange.getValue() || !BlockUtil.clientCanPlace(pos, false) || !BlockUtil.isStrictDirection(pos.offset(Direction.DOWN), Direction.UP) || !BlockUtil.canClick(pos.offset(Direction.DOWN)) || bestPos != null && !((double)MathHelper.sqrt((float)((float)AutoRegear.mc.player.squaredDistanceTo(pos.toCenterPos()))) < distance)) continue;
            distance = MathHelper.sqrt((float)((float)AutoRegear.mc.player.squaredDistanceTo(pos.toCenterPos())));
            bestPos = pos;
        }
        if (bestPos != null) {
            if (this.findShulker() == -1) {
                CommandManager.sendChatMessage("\u00a7c[!] No shulkerbox found");
                return;
            }
            InventoryUtil.doSwap(this.findShulker());
            this.placeBlock(bestPos);
            this.placePos = bestPos;
            InventoryUtil.doSwap(oldSlot);
            this.timer.reset();
        } else {
            CommandManager.sendChatMessage("\u00a7c[!] No place pos found");
        }
    }

    private void update() {
        this.stealCountList[0] = (int)(this.crystal.getValue() - (double)AutoRegear.getItemCount(Items.END_CRYSTAL));
        this.stealCountList[1] = (int)(this.exp.getValue() - (double)AutoRegear.getItemCount(Items.EXPERIENCE_BOTTLE));
        this.stealCountList[2] = (int)(this.totem.getValue() - (double)AutoRegear.getItemCount(Items.TOTEM_OF_UNDYING));
        this.stealCountList[3] = (int)(this.gapple.getValue() - (double)AutoRegear.getItemCount(Items.ENCHANTED_GOLDEN_APPLE));
        this.stealCountList[4] = (int)(this.endChest.getValue() - (double)AutoRegear.getItemCount(Item.fromBlock((Block)Blocks.ENDER_CHEST)));
        this.stealCountList[5] = (int)(this.web.getValue() - (double)AutoRegear.getItemCount(Item.fromBlock((Block)Blocks.COBWEB)));
        this.stealCountList[6] = (int)(this.glowstone.getValue() - (double)AutoRegear.getItemCount(Item.fromBlock((Block)Blocks.GLOWSTONE)));
        this.stealCountList[7] = (int)(this.anchor.getValue() - (double)AutoRegear.getItemCount(Item.fromBlock((Block)Blocks.RESPAWN_ANCHOR)));
        this.stealCountList[8] = (int)(this.pearl.getValue() - (double)AutoRegear.getItemCount(Items.ENDER_PEARL));
        this.stealCountList[9] = (int)(this.planks.getValue() - (double)AutoRegear.getItemCount(Item.fromBlock((Block)Blocks.BIRCH_PLANKS)));
        this.stealCountList[10] = (int)(this.wool.getValue() - (double)AutoRegear.getItemCount(Item.fromBlock((Block)Blocks.WHITE_WOOL)));
    }

    @Override
    public void onDisable() {
        this.opend = false;
        if (this.mine.getValue() && this.placePos != null) {
            PacketMine.INSTANCE.mine(this.placePos);
        }
    }

    @Override
    public void onUpdate() {
        if (this.smart.getValue()) {
            this.update();
        }
        if (!(AutoRegear.mc.currentScreen instanceof ShulkerBoxScreen)) {
            if (this.opend) {
                this.opend = false;
                if (this.autoDisable.getValue()) {
                    this.disable2();
                }
                return;
            }
            if (this.open.getValue()) {
                if (this.placePos != null && (double)MathHelper.sqrt((float)((float)AutoRegear.mc.player.squaredDistanceTo(this.placePos.toCenterPos()))) <= this.range.getValue() && AutoRegear.mc.world.isAir(this.placePos.up()) && (!this.timer.passedMs(500L) || AutoRegear.mc.world.getBlockState(this.placePos).getBlock() instanceof ShulkerBoxBlock)) {
                    if (AutoRegear.mc.world.getBlockState(this.placePos).getBlock() instanceof ShulkerBoxBlock) {
                        BlockUtil.clickBlock(this.placePos, BlockUtil.getClickSide(this.placePos), this.rotate.getValue());
                    }
                } else {
                    boolean found = false;
                    for (BlockPos pos : BlockUtil.getSphere((float)this.range.getValue())) {
                        if (!BlockUtil.isAir(pos.up()) || !(AutoRegear.mc.world.getBlockState(pos).getBlock() instanceof ShulkerBoxBlock)) continue;
                        BlockUtil.clickBlock(pos, BlockUtil.getClickSide(pos), this.rotate.getValue());
                        found = true;
                        break;
                    }
                    if (!found && this.autoDisable.getValue()) {
                        this.disable2();
                    }
                }
            } else if (!this.take.getValue() && this.autoDisable.getValue()) {
                this.disable2();
            }
            return;
        }
        this.opend = true;
        if (!this.take.getValue()) {
            if (this.autoDisable.getValue()) {
                this.disable2();
            }
            return;
        }
        boolean take = false;
        ScreenHandler screenHandler = AutoRegear.mc.player.currentScreenHandler;
        if (screenHandler instanceof ShulkerBoxScreenHandler) {
            ShulkerBoxScreenHandler shulker = (ShulkerBoxScreenHandler)screenHandler;
            for (Slot slot : shulker.slots) {
                if (slot.id >= 27 || slot.getStack().isEmpty() || this.smart.getValue() && !this.needSteal(slot.getStack())) continue;
                AutoRegear.mc.interactionManager.clickSlot(shulker.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)AutoRegear.mc.player);
                take = true;
            }
        }
        if (this.autoDisable.getValue() && !take) {
            this.disable2();
        }
    }

    private void disable2() {
        if (this.disableTimer.passedMs(this.disableTime.getValueInt())) {
            this.disable();
        }
    }

    private boolean needSteal(ItemStack i) {
        if (i.getItem().equals((Object)Items.END_CRYSTAL) && this.stealCountList[0] > 0) {
            this.stealCountList[0] = this.stealCountList[0] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Items.EXPERIENCE_BOTTLE) && this.stealCountList[1] > 0) {
            this.stealCountList[1] = this.stealCountList[1] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Items.TOTEM_OF_UNDYING) && this.stealCountList[2] > 0) {
            this.stealCountList[2] = this.stealCountList[2] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Items.ENCHANTED_GOLDEN_APPLE) && this.stealCountList[3] > 0) {
            this.stealCountList[3] = this.stealCountList[3] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Item.fromBlock((Block)Blocks.ENDER_CHEST)) && this.stealCountList[4] > 0) {
            this.stealCountList[4] = this.stealCountList[4] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Item.fromBlock((Block)Blocks.COBWEB)) && this.stealCountList[5] > 0) {
            this.stealCountList[5] = this.stealCountList[5] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Item.fromBlock((Block)Blocks.GLOWSTONE)) && this.stealCountList[6] > 0) {
            this.stealCountList[6] = this.stealCountList[6] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Item.fromBlock((Block)Blocks.RESPAWN_ANCHOR)) && this.stealCountList[7] > 0) {
            this.stealCountList[7] = this.stealCountList[7] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Items.ENDER_PEARL) && this.stealCountList[8] > 0) {
            this.stealCountList[8] = this.stealCountList[8] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Item.fromBlock((Block)Blocks.BIRCH_PLANKS)) && this.stealCountList[9] > 0) {
            this.stealCountList[9] = this.stealCountList[9] - i.getCount();
            return true;
        }
        if (i.getItem().equals((Object)Item.fromBlock((Block)Blocks.WHITE_WOOL)) && this.stealCountList[10] > 0) {
            this.stealCountList[10] = this.stealCountList[10] - i.getCount();
            return true;
        }
        return false;
    }

    public static int getItemCount(Item item) {
        int count = 0;
        for (Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            if (entry.getValue().getItem() != item) continue;
            count += entry.getValue().getCount();
        }
        return count;
    }

    private void placeBlock(BlockPos pos) {
        BlockUtil.clickBlock(pos.offset(Direction.DOWN), Direction.UP, this.rotate.getValue());
    }
}

