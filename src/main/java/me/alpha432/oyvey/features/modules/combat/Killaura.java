package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class Killaura extends Module {

    public Setting<Float> range = register(new Setting<>("Range", 4.0f, 1.0f, 6.0f));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", true));

    public Killaura() {
        super("Killaura", "Attacks entities automatically", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        PlayerEntity player = mc.player;
        World world = mc.world;

        if (player == null || world == null) return;

        // Create a box around player for attack range
        Box attackBox = player.getBoundingBox().expand(range.getValue());

        // Get all living entities in range except player
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, attackBox, e -> e != player);

        for (LivingEntity target : entities) {
            if (!target.isAlive()) continue;

            if (rotate.getValue()) faceEntity(target);

            // Attack when attack cooldown is ready
            if (player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                player.attack(target);
                player.swingHand(player.getActiveHand());
            }
        }
    }

    private void faceEntity(LivingEntity entity) {
        // Simplified rotation towards entity
        double dx = entity.getX() - mc.player.getX();
        double dz = entity.getZ() - mc.player.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        mc.player.setYaw(yaw);
        mc.player.setPitch((float) -Math.toDegrees(Math.atan2(entity.getY() - mc.player.getEyeY(), Math.sqrt(dx*dx + dz*dz))));
    }

    @Override
    public String getDisplayInfo() {
        return "Packet";
    }
}
