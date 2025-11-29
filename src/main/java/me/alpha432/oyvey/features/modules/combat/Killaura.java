package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.models.Timer;
import me.alpha432.oyvey.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class Killaura extends Module {
    private final Timer attackTimer = new Timer();

    public Setting<Float> range = this.register(new Setting<>("Range", 5.0f, 1.0f, 10.0f));
    public Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public Setting<TargetMode> targetMode = this.register(new Setting<>("TargetMode", TargetMode.CLOSEST));

    public Killaura() {
        super("Killaura", "Attacks nearby players automatically", Category.COMBAT);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (player == mc.player) continue; // skip self
            if (mc.player.squaredDistanceTo(player) > range.get() * range.get()) continue;

            // Rotation
            if (rotate.get()) {
                float[] rotations = MathUtil.calculateLookAt(player.getX(), player.getY() + player.getEyeHeight(player.getPose()), player.getZ(), mc.player);
                mc.player.setYaw(MathUtil.smoothRotation(mc.player.getYaw(), rotations[0], 10f));
                mc.player.setPitch(MathUtil.smoothRotation(mc.player.getPitch(), rotations[1], 10f));
            }

            // Attack
            if (attackTimer.passedMs(100)) { // 10 CPS max
                mc.player.attack(entity);
                mc.player.swingHand(mc.player.getActiveHand());
                attackTimer.reset();
            }
        }
    }

    public enum TargetMode {
        CLOSEST,
        HEALTH
    }
}
