/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 */
package me.rebirthclient.api.util;

import java.util.HashMap;
import java.util.Map;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class InventoryUtil
implements Wrapper {
    public static void doSwap(int slot) {
        InventoryUtil.mc.player.getInventory().selectedSlot = slot;
        mc.getNetworkHandler().sendPacket((Packet)new UpdateSelectedSlotC2SPacket(slot));
    }

    public static boolean holdingItem(Class clazz) {
        ItemStack stack = InventoryUtil.mc.player.getMainHandStack();
        boolean result = InventoryUtil.isInstanceOf(stack, clazz);
        if (!result) {
            result = InventoryUtil.isInstanceOf(stack, clazz);
        }
        return result;
    }

    public static boolean isInstanceOf(ItemStack stack, Class clazz) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (clazz.isInstance((Object)item)) {
            return true;
        }
        if (item instanceof BlockItem) {
            Block block = Block.getBlockFromItem((Item)item);
            return clazz.isInstance((Object)block);
        }
        return false;
    }

    public static ItemStack getStackInSlot(int i) {
        return InventoryUtil.mc.player.getInventory().getStack(i);
    }

    public static int findItem(Item input) {
        for (int i = 0; i < 9; ++i) {
            Item item = InventoryUtil.getStackInSlot(i).getItem();
            if (Item.getRawId((Item)item) != Item.getRawId((Item)input)) continue;
            return i;
        }
        return -1;
    }

    public static int findClass(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance((Object)stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof BlockItem) || !clazz.isInstance((Object)((BlockItem)stack.getItem()).getBlock())) continue;
            return i;
        }
        return -1;
    }

    public static int findClassInventorySlot(Class clazz) {
        for (int i = 0; i < 45; ++i) {
            ItemStack stack = InventoryUtil.mc.player.getInventory().getStack(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance((Object)stack.getItem())) {
                return i < 9 ? i + 36 : i;
            }
            if (!(stack.getItem() instanceof BlockItem) || !clazz.isInstance((Object)((BlockItem)stack.getItem()).getBlock())) continue;
            return i < 9 ? i + 36 : i;
        }
        return -1;
    }

    public static int findBlock(Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof BlockItem) || ((BlockItem)stack.getItem()).getBlock() != blockIn) continue;
            return i;
        }
        return -1;
    }

    public static int findUnBlock() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.getStackInSlot(i);
            if (stack.getItem() instanceof BlockItem) continue;
            return i;
        }
        return -1;
    }

    public static int findBlock() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.getStackInSlot(i);
            if (!(stack.getItem() instanceof BlockItem) || BlockUtil.shiftBlocks.contains((Object)Block.getBlockFromItem((Item)stack.getItem())) || ((BlockItem)stack.getItem()).getBlock() == Blocks.COBWEB) continue;
            return i;
        }
        return -1;
    }

    public static int findBlockInventorySlot(Block block) {
        return InventoryUtil.findItemInventorySlot(Item.fromBlock((Block)block));
    }

    public static int findItemInventorySlot(Item item) {
        for (int i = 0; i < 45; ++i) {
            ItemStack stack = InventoryUtil.mc.player.getInventory().getStack(i);
            if (stack.getItem() != item) continue;
            return i < 9 ? i + 36 : i;
        }
        return -1;
    }

    public static int findTotem() {
        int t = 0;
        for (int i = 0; i < 45; ++i) {
            ItemStack stack = InventoryUtil.mc.player.getInventory().getStack(i);
            if (stack.getItem() != Items.TOTEM_OF_UNDYING) continue;
            ++t;
        }
        return t;
    }

    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        for (int current = 0; current <= 44; ++current) {
            fullInventorySlots.put(current, InventoryUtil.mc.player.getInventory().getStack(current));
        }
        return fullInventorySlots;
    }
}

