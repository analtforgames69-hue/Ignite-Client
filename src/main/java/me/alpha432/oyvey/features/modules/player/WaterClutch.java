package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.client.util.InputUtil;

public class WaterClutch extends Module {

    private int previousSlot = -1;

    public WaterClutch() {
        super("WaterClutch", "Automatically places water when falling", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        // Only work when player is falling
        if (!mc.player.isOnGround() && OyVey.positionManager.getFallDistance() > 4) {
            int waterSlot = findWaterBucket();

            if (waterSlot == -1) return; // No water bucket found

            // Remember previous slot
            if (previousSlot == -1) previousSlot = mc.player.getInventory().selectedSlot;

            // Switch to water bucket
            mc.player.getInventory().selectedSlot = waterSlot;
            mc.player.updateSelectedSlot();

            // Place water
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND);

            // Switch back to previous slot
            if (previousSlot != -1) {
                mc.player.getInventory().selectedSlot = previousSlot;
                mc.player.updateSelectedSlot();
                previousSlot = -1;
            }
        }
    }

    private int findWaterBucket() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.WATER_BUCKET) return i;
        }
        return -1;
    }
}
