package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class Killaura extends Module {

    public float range = 4.5f; // configurable attack range
    public float rotationSpeed = 10f; // smooth rotation speed
    public boolean rotate = true; // rotate towards target
    private Entity target;

    public Killaura() {
        super("Killaura", "Automatically attacks enemies", Module.Category.COMBAT, true, false, true);
    }

    @Override
    public void onUpdate() {
        if (!this.isEnabled()) return;

        // Select target
        target = getClosestTarget(range);
        if (target == null) return;

        // Rotate toward target if enabled
        if (rotate) {
            Vec3d eyes = mc.player.getEyePos();
            Vec3d targetPos = target.getEyePos();
            float[] angles = MathUtil.calcAngle(eyes, targetPos);
            float yaw = smoothRotation(mc.player.getYaw(), angles[0], rotationSpeed);
            float pitch = smoothRotation(mc.player.getPitch(), angles[1], rotationSpeed);
            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
        }

        // Attack if ready (uses Minecraft's cooldown system)
        if (mc.player.getAttackCooldownProgress(0f) >= 1.0f && mc.player.distanceTo(target) <= range) {
            mc.player.attack(target);
            mc.player.swingHand(mc.player.getActiveHand());
        }
    }

    private Entity getClosestTarget(double range) {
        Entity closest = null;
        double shortestDistance = range;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player && mc.player.distanceTo(entity) <= range && !entity.isRemoved()) {
                double dist = mc.player.distanceTo(entity);
                if (dist < shortestDistance) {
                    shortestDistance = dist;
                    closest = entity;
                }
            }
        }
        return closest;
    }

    private float smoothRotation(float current, float target, float speed) {
        float delta = MathHelper.wrapDegrees(target - current);
        if (delta > speed) delta = speed;
        if (delta < -speed) delta = -speed;
        return current + delta;
    }
}
