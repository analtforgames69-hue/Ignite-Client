package com.oyveyskidded.modules.player;

import com.oyveyskidded.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;

public class WaterClutch extends Module {

    private boolean placedWater = false;
    private int previousSlot = -1;

    public WaterClutch() {
        super("WaterClutch", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        // How far the player is falling
        double fallDistance = mc.player.fallDistance;

        // Look at the block below the player
        BlockPos below = mc.player.getBlockPos().down();

        // If we previously placed water and it's now safe → pick it up
        if (placedWater) {
            if (mc.world.getBlockState(below).getBlock() == Blocks.WATER) {
                int waterBucketSlot = findEmptyBucketSlot();
                if (waterBucketSlot != -1) {
                    mc.player.getInventory().selectedSlot = waterBucketSlot;
                    mc.interactionManager.interactBlock(
                            mc.player,
                            Hand.MAIN_HAND,
                            new net.minecraft.util.hit.BlockHitResult(
                                    mc.player.getPos(),
                                    mc.player.getHorizontalFacing(),
                                    below,
                                    false
                            )
                    );
                }
            }

            // Restore the original slot
            if (previousSlot != -1) {
                mc.player.getInventory().selectedSlot = previousSlot;
            }
            placedWater = false;
            return;
        }

        // If falling more than 4 blocks → try clutch
        if (fallDistance >= 4f) {
            int waterBucketSlot = findWaterBucketSlot();
            if (waterBucketSlot == -1) return;

            // Switch to the bucket
            previousSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = waterBucketSlot;

            // Aim straight down (Oyvey has a rotation util usually called RotationManager)
            mc.player.setPitch(90f);

            // Place water on the block below
            mc.interactionManager.interactBlock(
                    mc.player,
                    Hand.MAIN_HAND,
                    new net.minecraft.util.hit.BlockHitResult(
                            mc.player.getPos(),
                            mc.player.getHorizontalFacing(),
                            below,
                            false
                    )
            );

            placedWater = true;
        }
    }

    private int findWaterBucketSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.p
