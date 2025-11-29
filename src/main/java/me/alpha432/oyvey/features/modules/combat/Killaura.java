package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.gui.items.buttons.BindButton;
import me.alpha432.oyvey.features.gui.items.buttons.EntityListButton;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import java.util.Comparator;

public class Killaura extends Module {
    public Setting<Float> range = this.register(new Setting<>("Range", 4.0f, 1.0f, 6.0f));
    private LivingEntity target;
    private String targetEntity = "Player"; // default target
    public BindButton bindButton;
    public EntityListButton entityListButton;

public Killaura() {
    super("Killaura", "Automatically attacks entities.", Category.COMBAT);

    // Add BindButton linked to this module's bind
    this.bindButton = new BindButton(this.getBind());

    // Add EntityListButton to select target type
    this.entityListButton = new EntityListButton(this);
}


    public void setTargetEntity(String entityName) {
        this.targetEntity = entityName;
    }

    @Override
    public void onUpdate() {
        if (!this.isOn()) return;

        // Find target
        this.target = OyVey.mc.world.getEntitiesByClass(LivingEntity.class, 
                OyVey.mc.player.getBoundingBox().expand(range.getValue(), range.getValue(), range.getValue()), 
                e -> isValidTarget(e))
            .stream()
            .min(Comparator.comparing(e -> OyVey.mc.player.squaredDistanceTo(e)))
            .orElse(null);

        // Attack if target exists
        if (this.target != null) {
            lookAtEntity(this.target);
            if (OyVey.mc.player.getAttackCooldownProgress(0f) >= 1.0f) {
                OyVey.mc.player.attack(this.target);
                OyVey.mc.player.swingHand(OyVey.mc.player.getActiveHand());
            }
        }
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity == OyVey.mc.player) return false;
        if (entity.isDead()) return false;

        if (targetEntity.equalsIgnoreCase("Player") && entity instanceof PlayerEntity) return true;
        if (targetEntity.equalsIgnoreCase("Phantom") && entity.getType().toString().equalsIgnoreCase("phantom")) return true;
        return false;
    }

    private void lookAtEntity(LivingEntity entity) {
        Vec3d eyesPos = OyVey.mc.player.getEyePos();
        Vec3d targetPos = entity.getPos().add(0, entity.getHeight() / 2.0, 0);
        Vec3d diff = targetPos.subtract(eyesPos);

        float yaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)));

        // Smooth rotation
        OyVey.mc.player.setYaw(OyVey.mc.player.getYaw() + (yaw - OyVey.mc.player.getYaw()) * 0.4f);
        OyVey.mc.player.setPitch(OyVey.mc.player.getPitch() + (pitch - OyVey.mc.player.getPitch()) * 0.4f);
    }
}
