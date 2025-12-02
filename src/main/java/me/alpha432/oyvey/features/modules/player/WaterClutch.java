package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.mixin.PlayerInventoryAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class WaterClutch extends Module {

    private int previousSlot = -1;

    public WaterClutch() {
        super("WaterClutch", "Automatically places water when falling", Category.PLAYER, true, false, false);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        if (!mc.player.isOnGround() && mc.player.fallDistance > 4.0f) {

            // Find water bucket in hotbar
            int waterSlot = -1;
            for (int i = 0; i < 9; i++) {
                if (mc.player.getInventory().getStack(i).getItem() == Items.WATER_BUCKET) {
                    waterSlot = i;
                    break;
                }
            }

            if (waterSlot == -1) return; // No water bucket

            PlayerInventoryAccessor inv = (PlayerInventoryAccessor) mc.player.getInventory();

            // Save previous slot
            if (previousSlot == -1) previousSlot = inv.getSelectedSlot();

            // Switch to water bucket
            inv.setSelectedSlot(waterSlot);

            // Use water bucket
            mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());

            // Switch back
            if (previousSlot != -1) {
                inv.setSelectedSlot(previousSlot);
                previousSlot = -1;
            }
        }
    }
}
