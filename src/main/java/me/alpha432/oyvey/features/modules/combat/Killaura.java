package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.features.gui.items.buttons.BindButton;
import me.alpha432.oyvey.util.EntityUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;

import java.util.List;

public class Killaura extends Module {
    public Setting<Bind> keyBind = this.register(new Setting<>("Keybind", new Bind(-1)));
    public BindButton bindButton;

    private String targetEntity = "Player"; // default
    private LivingEntity target;

    public Killaura() {
        super("Killaura", "Automatically attacks entities", Category.COMBAT, true, false, false);
        bindButton = new BindButton(keyBind); // properly use Setting<Bind>
    }

    @Override
    public void onUpdate() {
        findTarget();
        if (target != null && mc.player.getAttackCooldownProgress(0f) >= 1.0f) {
            attackTarget(target);
            smoothLookAt(target);
        }
    }

    private void findTarget() {
        List<LivingEntity> entities = mc.world.getEntitiesByClass(LivingEntity.class, mc.player.getBoundingBox().expand(6), e -> !e.isDead() && e.isAlive());
        target = entities.stream()
                .filter(e -> {
                    if (targetEntity.equals("Player") && e instanceof PlayerEntity) return true;
                    if (targetEntity.equals("Phantom") && e.getType().toString().contains("phantom")) return true;
                    return false;
                })
                .findFirst().orElse(null);
    }

    private void attackTarget(LivingEntity entity) {
        mc.player.attack(entity);
        mc.player.swingHand(mc.player.getActiveHand());
    }

    private void smoothLookAt(LivingEntity entity) {
        double dx = entity.getX() - mc.player.getX();
        double dy = entity.getY() + entity.getEyeHeight(mc.player.getPose()) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double dz = entity.getZ() - mc.player.getZ();
        double distance = Math.sqrt(dx*dx + dz*dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, distance));

        mc.player.setYaw(mc.player.getYaw() + (yaw - mc.player.getYaw()) * 0.3f); // smooth factor
        mc.player.setPitch(mc.player.getPitch() + (pitch - mc.player.getPitch()) * 0.3f);
    }

    public void setTargetEntity(String entity) {
        this.targetEntity = entity;
    }

    public void disableKillaura() {
        target = null;
        this.disable();
    }
}
