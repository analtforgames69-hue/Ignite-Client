package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.mixin.ClientPlayerEntityAccessor;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class WaterClutch extends Module {
    private int previousSlot = -1;

    // Optional: store previous rotation to restore
    private float previousPitch, previousYaw;

    public WaterClutch() {
        super("WaterClutch", "Automatically places water to clutch", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        // Find water bucket in hotbar
        int waterSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.WATER_BUCKET) {
                waterSlot = i;
                break;
            }
        }
        if (waterSlot == -1) return;

        // Save previous slot and rotation
        if (previousSlot == -1) {
            previousSlot = mc.player.getInventory().selectedSlot;
            previousPitch = mc.player.getPitch();
            previousYaw = mc.player.getYaw();
        }

        // Select water bucket
        mc.player.getInventory().selectedSlot = waterSlot;

        // Turn head straight down (pitch 90) and forward (yaw unchanged, or set to something)
        ClientPlayerEntityAccessor accessor = (ClientPlayerEntityAccessor) mc.player;
        accessor.setPitch(90f);  // look straight down
        accessor.setYaw(previousYaw); // keep current yaw, or you could force a specific yaw

        // Place water
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

        // Restore previous rotation
        accessor.setPitch(previousPitch);
        accessor.setYaw(previousYaw);

        // Restore previous slot
        mc.player.getInventory().selectedSlot = previousSlot;
        previousSlot = -1;
    }
}
