package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.client.network.ClientPlayerEntity;

public class Killaura extends Module {

    public Setting<Float> range = register(new Setting<>("Range", 4.0f, 1.0f, 6.0f));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", true));

    public Killaura() {
        super("Killaura", "Automatically attacks entities around you", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        World world = mc.world;

        if (player == null || world == null) return;

        for (Entity entity : world.getEntities()) {
            // Skip non-living entities and yourself
            if (!(entity instanceof LivingEntity living) || entity == player) continue;

            // Skip dead entities
            if (!living.isAlive()) continue;

            // Check distance
            if (player.squaredDistanceTo(entity) > range.getValue() * range.getValue()) continue;

            // Rotate towards target if enabled
            if (rotate.getValue()) {
                faceEntity(living);
            }

            // Attack only if the attack cooldown is ready
            if (player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                player.attack(living);
                player.swingHand(player.getActiveHand());
            }
        }
    }

    // Simple rotation helper
    private void faceEntity(LivingEntity entity) {
        double dx = entity.getX() - mc.player.getX();
        double dz = entity.getZ() - mc.player.getZ();
        double dy = entity.getY() + entity.getEyeHeight(mc.player.getPose()) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, distXZ));

        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }

    @Override
    public String getDisplayInfo() {
        return "Aura";
    }
}
