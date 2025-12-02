package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.mixin.PlayerInventoryAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WaterClutch extends Module {

    private int previousSlot = -1;

    public WaterClutch() {
        super("WaterClutch", "Automatically places water to avoid fall damage", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player.isOnGround()) {
            previousSlot = -1; // reset
            return;
        }

        // Find water bucket in hotbar
        int waterSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.WATER_BUCKET) {
                waterSlot = i;
                break;
            }
        }

        if (waterSlot == -1) return; // no water bucket, do nothing

        // Save previous slot
        if (previousSlot == -1) previousSlot = ((PlayerInventoryAccessor) mc.player.getInventory()).getSelectedSlot();

        // Switch to water bucket
        ((PlayerInventoryAccessor) mc.player.getInventory()).setSelectedSlot(waterSlot);

        // Place water below player
        BlockPos posBelow = mc.player.getBlockPos().down();
        BlockHitResult blockHit = new BlockHitResult(mc.player.getPos(), Direction.UP, posBelow, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHit);

        // Switch back to previous slot
        ((PlayerInventoryAccessor) mc.player.getInventory()).setSelectedSlot(previousSlot);
        previousSlot = -1;
    }
}
