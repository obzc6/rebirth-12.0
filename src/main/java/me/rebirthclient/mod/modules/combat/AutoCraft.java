/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.AirBlockItem
 *  net.minecraft.item.BedItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.Recipe
 *  net.minecraft.screen.CraftingScreenHandler
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.rebirthclient.mod.modules.combat;

import me.rebirthclient.Rebirth;
import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.combat.BedAura;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.EnumSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.BedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AutoCraft
extends Module {
    public static AutoCraft INSTANCE;
    public final EnumSetting page = this.add(new EnumSetting("Page", Page.General));
    private final SliderSetting range = this.add(new SliderSetting("Range", 5, 0, 8, v -> this.page.getValue() == Page.General));
    private final SliderSetting Delay = this.add(new SliderSetting("CraftDelay", 100, 0, 500, v -> this.page.getValue() == Page.General));
    private final BooleanSetting selfGround = this.add(new BooleanSetting("SelfGround", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting disable = this.add(new BooleanSetting("Disable", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting bdisable = this.add(new BooleanSetting("BedDisable", true, v -> this.page.getValue() == Page.General));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", true, v -> this.page.getValue() == Page.Rotate).setParent());
    private final BooleanSetting newRotate = this.add(new BooleanSetting("NewRotate", false, v -> this.rotate.isOpen() && this.page.getValue() == Page.Rotate));
    private final SliderSetting yawStep = this.add(new SliderSetting("YawStep", 0.3f, 0.1f, 1.0, 0.01f, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting sync = this.add(new BooleanSetting("Sync", false, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final BooleanSetting checkLook = this.add(new BooleanSetting("CheckLook", true, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.page.getValue() == Page.Rotate));
    private final SliderSetting fov = this.add(new SliderSetting("Fov", 5.0, 0.0, 30.0, v -> this.rotate.isOpen() && this.newRotate.getValue() && this.checkLook.getValue() && this.page.getValue() == Page.Rotate));
    private BlockPos bestPos = null;
    private float lastYaw = 0.0f;
    private float lastPitch = 0.0f;
    public Vec3d directionVec = null;
    private final Timer timer = new Timer();

    public AutoCraft() {
        super("AutoCraft", Module.Category.Combat);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        this.lastYaw = Rebirth.RUN.lastYaw;
        this.lastPitch = Rebirth.RUN.lastPitch;
    }

    @Override
    public void onEnable() {
        this.lastYaw = Rebirth.RUN.lastYaw;
        this.lastPitch = Rebirth.RUN.lastPitch;
    }

    @EventHandler
    public void onRotate(RotateEvent event) {
        if (this.bestPos != null && this.newRotate.getValue() && this.directionVec != null) {
            float[] newAngle = this.injectStep(EntityUtil.getLegitRotations(this.directionVec), this.yawStep.getValueFloat());
            this.lastYaw = newAngle[0];
            this.lastPitch = newAngle[1];
            event.setYaw(this.lastYaw);
            event.setPitch(this.lastPitch);
        } else {
            this.lastYaw = Rebirth.RUN.lastYaw;
            this.lastPitch = Rebirth.RUN.lastPitch;
        }
    }

    @Override
    public void onUpdate() {
        if (AutoCraft.getEmptySlots() == 0) {
            if (AutoCraft.mc.player.currentScreenHandler instanceof CraftingScreenHandler) {
                AutoCraft.mc.player.closeHandledScreen();
            }
            if (this.disable.getValue()) {
                this.disable();
            }
            return;
        }
        if (AutoCraft.mc.player.currentScreenHandler instanceof CraftingScreenHandler) {
            boolean craft = false;
            block0: for (RecipeResultCollection recipeResult : AutoCraft.mc.player.getRecipeBook().getOrderedResults()) {
                for (Recipe recipe : recipeResult.getRecipes(true)) {
                    if (!(recipe.getOutput(AutoCraft.mc.world.getRegistryManager()).getItem() instanceof BedItem)) continue;
                    for (int i = 0; i < AutoCraft.getEmptySlots(); ++i) {
                        this.doCraft(recipe);
                        craft = true;
                    }
                    continue block0;
                }
            }
            if (!craft) {
                if (AutoCraft.mc.player.currentScreenHandler instanceof CraftingScreenHandler) {
                    AutoCraft.mc.player.closeHandledScreen();
                }
                if (this.bdisable.getValue() && BedAura.INSTANCE.isOn() && BedAura.INSTANCE.getBed() == -1) {
                    BedAura.INSTANCE.disable();
                }
                if (this.disable.getValue()) {
                    this.disable();
                }
            }
        } else {
            if (this.selfGround.getValue() && !AutoCraft.mc.player.isOnGround()) {
                return;
            }
            this.doPlace();
        }
    }

    private void doPlace() {
        this.bestPos = null;
        double distance = 100.0;
        boolean place = true;
        for (BlockPos pos : BlockUtil.getSphere(this.range.getValueFloat())) {
            if (AutoCraft.mc.world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE && BlockUtil.getClickSideStrict(pos) != null) {
                place = false;
                this.bestPos = pos;
                break;
            }
            if (!BlockUtil.canPlace(pos) || this.bestPos != null && !((double)MathHelper.sqrt((float)((float)AutoCraft.mc.player.squaredDistanceTo(pos.toCenterPos()))) < distance)) continue;
            this.bestPos = pos;
            distance = MathHelper.sqrt((float)((float)AutoCraft.mc.player.squaredDistanceTo(pos.toCenterPos())));
        }
        if (this.bestPos != null) {
            if (this.rotate.getValue()) {
                Direction side = BlockUtil.getClickSide(this.bestPos);
                Vec3d directionVec = new Vec3d((double)this.bestPos.getX() + 0.5 + (double)side.getVector().getX() * 0.5, (double)this.bestPos.getY() + 0.5 + (double)side.getVector().getY() * 0.5, (double)this.bestPos.getZ() + 0.5 + (double)side.getVector().getZ() * 0.5);
                if (!this.faceVector(directionVec)) {
                    return;
                }
            }
            if (!place) {
                BlockUtil.clickBlock(this.bestPos, BlockUtil.getClickSide(this.bestPos), this.rotate.getValue());
                this.bestPos = null;
            } else {
                if (InventoryUtil.findItem(Item.fromBlock((Block)Blocks.CRAFTING_TABLE)) == -1) {
                    return;
                }
                int old = AutoCraft.mc.player.getInventory().selectedSlot;
                InventoryUtil.doSwap(InventoryUtil.findItem(Item.fromBlock((Block)Blocks.CRAFTING_TABLE)));
                BlockUtil.placeBlock(this.bestPos, this.rotate.getValue());
                InventoryUtil.doSwap(old);
            }
        }
    }

    public void doCraft(Recipe recipe) {
        if (!this.timer.passedMs((long)this.Delay.getValue())) {
            return;
        }
        AutoCraft.mc.interactionManager.clickRecipe(AutoCraft.mc.player.currentScreenHandler.syncId, recipe, false);
        AutoCraft.mc.interactionManager.clickSlot(AutoCraft.mc.player.currentScreenHandler.syncId, 0, 1, SlotActionType.QUICK_MOVE, (PlayerEntity)AutoCraft.mc.player);
        this.timer.reset();
    }

    public static int getEmptySlots() {
        int emptySlots = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack itemStack = AutoCraft.mc.player.getInventory().getStack(i);
            if (itemStack != null && !(itemStack.getItem() instanceof AirBlockItem)) continue;
            ++emptySlots;
        }
        return emptySlots;
    }

    public boolean faceVector(Vec3d directionVec) {
        if (!this.newRotate.getValue()) {
            EntityUtil.faceVector(directionVec);
            return true;
        }
        this.directionVec = directionVec;
        float[] angle = EntityUtil.getLegitRotations(directionVec);
        if (Math.abs(MathHelper.wrapDegrees((float)(angle[0] - this.lastYaw))) < this.fov.getValueFloat() && Math.abs(MathHelper.wrapDegrees((float)(angle[1] - this.lastPitch))) < this.fov.getValueFloat()) {
            if (this.sync.getValue()) {
                EntityUtil.sendYawAndPitch(angle[0], angle[1]);
            }
            return true;
        }
        return !this.checkLook.getValue();
    }

    private float[] injectStep(float[] angle, float steps) {
        if (steps < 0.01f) {
            steps = 0.01f;
        }
        if (steps > 1.0f) {
            steps = 1.0f;
        }
        if (steps < 1.0f && angle != null) {
            float packetPitch;
            float packetYaw = this.lastYaw;
            float diff = MathHelper.wrapDegrees((float)(angle[0] - this.lastYaw));
            if (Math.abs(diff) > 90.0f * steps) {
                angle[0] = packetYaw + diff * (90.0f * steps / Math.abs(diff));
            }
            if (Math.abs(diff = angle[1] - (packetPitch = this.lastPitch)) > 90.0f * steps) {
                angle[1] = packetPitch + diff * (90.0f * steps / Math.abs(diff));
            }
        }
        return new float[]{angle[0], angle[1]};
    }

    public static enum Page {
        General,
        Rotate;

        // $FF: synthetic method
        private static AutoCraft.Page[] $values() {
            return new AutoCraft.Page[]{General, Rotate};
        }
    }
}
