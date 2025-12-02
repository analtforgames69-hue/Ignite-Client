package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WaterClutch extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private boolean placedWater = false;
    private int previousSlot = -1;

    public WaterClutch() {
        super("WaterClutch", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        double fallDistance = mc.player.fallDistance;
        BlockPos below = mc.player.getBlockPos().down();

        // If water is already placed → pick it up
        if (placedWater) {
            if (mc.world.getBlockState(below).getBlock() == Blocks.WATER) {
                int emptyBucketSlot = findEmptyBucketSlot();
                if (emptyBucketSlot != -1) {
                    mc.player.getInventory().selectedSlot = emptyBucketSlot;

                    mc.interactionManager.interactBlock(
                        mc.player,
                        Hand.MAIN_HAND,
                        new BlockHitResult(
                            mc.player.getPos(),
                            Direction.UP,
                            below,
                            false
                        )
                    );
                }
            }

            // restore previous slot
            if (previousSlot != -1) {
                mc.player.getInventory().selectedSlot = previousSlot;
            }

            placedWater = false;
            return;
        }

        // If falling enough → place water
        if (fallDistance >= 4f) {
            int waterBucketSlot = findWaterBucketSlot();
            if (waterBucketSlot == -1) return;

            previousSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = waterBucketSlot;

            // Look straight down
            mc.player.setPitch(90f);

            mc.interactionManager.interactBlock(
                mc.player,
                Hand.MAIN_HAND,
                new BlockHitResult(
                    mc.player.getPos(),
                    Direction.UP,
                    below,
                    false
                )
            );

            placedWater = true;
        }
    }

    private int findWaterBucketSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.WATER_BUCKET) {
                return i;
            }
        }
        return -1;
    }

    private int findEmptyBucketSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.BUCKET) {
                return i;
            }
        }
        return -1;
    }
}
