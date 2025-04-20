/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 */
package me.rebirthclient.mod.modules.movement;

import me.rebirthclient.api.events.eventbus.EventHandler;
import me.rebirthclient.api.events.impl.MoveEvent;
import me.rebirthclient.api.events.impl.RotateEvent;
import me.rebirthclient.api.util.BlockUtil;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.InventoryUtil;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.settings.impl.BooleanSetting;
import me.rebirthclient.mod.settings.impl.SliderSetting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Scaffold
extends Module {
    private final BooleanSetting tower = this.add(new BooleanSetting("Tower", true));
    private final BooleanSetting rotate = this.add(new BooleanSetting("Rotate", false));
    public SliderSetting rotateTime = this.add(new SliderSetting("KeepRotate", 1000.0, 0.0, 3000.0, 10.0));
    private final Timer timer = new Timer();
    private final Timer lastTimer = new Timer();
    private float[] angle = null;
    private Timer timer2 = new Timer();
    private BlockPos lastPos;

    public Scaffold() {
        super("Scaffold", Module.Category.Movement);
    }

    @EventHandler(priority=100)
    public void onRotation(RotateEvent event) {
        if (this.rotate.getValue() && !this.timer.passedMs(this.rotateTime.getValueInt()) && this.angle != null) {
            event.setYaw(this.angle[0]);
            event.setPitch(this.angle[1]);
        }
    }

    @Override
    public void onUpdate() {
        if (Scaffold.nullCheck()) {
            return;
        }
        if (!this.tower.getValue()) {
            return;
        }
        if (Scaffold.mc.options.jumpKey.isPressed() && !MovementUtil.isMoving()) {
            if (this.lastTimer.passed(500L)) {
                this.lastTimer.reset();
                this.lastPos = null;
            }
            if (this.timer2.passed(3000L)) {
                MovementUtil.setMotionY(-0.28);
                this.timer2.reset();
                this.lastPos = null;
            } else if (this.lastPos == null || this.lastPos.equals((Object)EntityUtil.getPlayerPos())) {
                this.lastPos = EntityUtil.getPlayerPos().up();
                MovementUtil.setMotionY(0.42);
                MovementUtil.setMotionX(0.0);
                MovementUtil.setMotionZ(0.0);
            }
        } else {
            this.timer2.reset();
            this.lastPos = null;
        }
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        int block = InventoryUtil.findBlock();
        if (block == -1) {
            return;
        }
        BlockPos placePos = EntityUtil.getPlayerPos().down();
        if (BlockUtil.clientCanPlace(placePos, false)) {
            int old = Scaffold.mc.player.getInventory().selectedSlot;
            if (BlockUtil.getPlaceSide(placePos) == null) {
                double distance = 1000.0;
                BlockPos bestPos = null;
                for (Direction i : Direction.values()) {
                    if (i == Direction.UP || !BlockUtil.canPlace(placePos.offset(i)) || bestPos != null && !(Scaffold.mc.player.squaredDistanceTo(placePos.offset(i).toCenterPos()) < distance)) continue;
                    bestPos = placePos.offset(i);
                    distance = Scaffold.mc.player.squaredDistanceTo(placePos.offset(i).toCenterPos());
                }
                if (bestPos != null) {
                    placePos = bestPos;
                } else {
                    return;
                }
            }
            if (this.rotate.getValue()) {
                Direction side = BlockUtil.getPlaceSide(placePos);
                this.angle = EntityUtil.getLegitRotations(placePos.offset(side).toCenterPos().add((double)side.getOpposite().getVector().getX() * 0.5, (double)side.getOpposite().getVector().getY() * 0.5, (double)side.getOpposite().getVector().getZ() * 0.5));
                this.timer.reset();
            }
            InventoryUtil.doSwap(block);
            BlockUtil.placeBlock(placePos, this.rotate.getValue(), false);
            InventoryUtil.doSwap(old);
            this.lastTimer.reset();
        }
    }
}

