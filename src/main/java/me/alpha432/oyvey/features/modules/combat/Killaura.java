package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.features.gui.items.buttons.BindButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Hand;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class Killaura extends Module {

    public Setting<Bind> bind = register(new Setting<>("Keybind", new Bind(-1)));
    public Setting<TargetMode> targetMode = register(new Setting<>("Target", TargetMode.Players));
    public Setting<Float> rotationSpeed = register(new Setting<>("Speed", 10f, 1f, 20f));

    private BindButton bindButton;

    public Killaura() {
        super("Killaura", "Automatically attacks nearby entities", Category.COMBAT, true, false, false);
        bindButton = new BindButton(bind);
    }

    public enum TargetMode {
        Players,
        Phantoms
    }

    @Override
    public void onUpdate() {
        if (!this.isEnabled()) return;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        World world = MinecraftClient.getInstance().world;

        if (player == null || world == null) return;

        // Only attack when attack cooldown is full
        if (player.getAttackCooldownProgress(0f) < 1.0f) return;

        List<Entity> entities = world.getEntitiesByClass(Entity.class, new Box(
                player.getX() - 4, player.getY() - 4, player.getZ() - 4,
                player.getX() + 4, player.getY() + 4, player.getZ() + 4),
                e -> e != player && isValidTarget(e)
        );

        for (Entity target : entities) {
            lookAtEntitySmooth(player, target, rotationSpeed.getValue());
            attack(target, player);
        }
    }

    private boolean isValidTarget(Entity entity) {
        switch (targetMode.getValue()) {
            case Players:
                return entity instanceof PlayerEntity;
            case Phantoms:
                return entity instanceof PhantomEntity;
        }
        return false;
    }

    private void attack(Entity target, ClientPlayerEntity player) {
        player.attack(target);
        player.swingHand(Hand.MAIN_HAND);
    }

    private void lookAtEntitySmooth(ClientPlayerEntity player, Entity target, float speed) {
        Vec3d diff = new Vec3d(
                target.getX() - player.getX(),
                (target.getY() + target.getEyeHeight()) - (player.getY() + player.getEyeHeight()),
                target.getZ() - player.getZ()
        );

        double distanceXZ = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diff.y, distanceXZ));

        // Smoothly interpolate yaw and pitch
        player.yaw = interpolateRotation(player.yaw, targetYaw, speed);
        player.pitch = interpolateRotation(player.pitch, targetPitch, speed);
    }

    private float interpolateRotation(float current, float target, float speed) {
        float delta = wrapDegrees(target - current);
        if (delta > speed) delta = speed;
        if (delta < -speed) delta = -speed;
        return current + delta;
    }

    private float wrapDegrees(float value) {
        value %= 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }

    public BindButton getBindButton() {
        return bindButton;
    }
}
