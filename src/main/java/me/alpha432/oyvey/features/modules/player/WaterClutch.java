package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.Module.Category;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class WaterClutch extends Module {

    private int previousSlot = -1;

    public WaterClutch() {
        super("WaterClutch", "Automatically places water to prevent fall damage", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        ClientPlayerEntity player = mc.player;

        if (player == null) return;

        // Only trigger if falling 4+ blocks
        if (!player.isOnGround() && player.fallDistance > 4) {
            // Find water bucket in hotbar
            int waterSlot = findWaterBucketSlot(player);
            if (waterSlot == -1) return; // No bucket found

            // Remember previous slot
            if (previousSlot == -1) previousSlot = player.getInventory().selectedSlot;

            // Switch to bucket
            player.getInventory().selectedSlot = waterSlot;

            // Rotate head to look down 90 degrees in third-person
            player.pitch = 90f; // Look straight down
            player.prevPitch = 90f;

            // Use bucket
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            if (stack.getItem() == Items.WATER_BUCKET) {
                mc.interactionManager.interactItem(player, Hand.MAIN_HAND);
            }

            // Restore previous slot
            player.getInventory().selectedSlot = previousSlot;
            previousSlot = -1;
        }
    }

    private int findWaterBucketSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == Items.WATER_BUCKET) return i;
        }
        return -1;
    }
}
