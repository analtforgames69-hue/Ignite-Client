package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.gui.KillauraEntitySelectorGUI;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Killaura extends Module {

    private final Minecraft mc = Minecraft.getInstance();
    private boolean enabled = false;
    private final KillauraEntitySelectorGUI gui = new KillauraEntitySelectorGUI();

    public Killaura() {
        super("Killaura", "Automatically attacks nearby entities", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (!enabled || mc.player == null || mc.level == null) return;
        doKillaura();
    }

    private void doKillaura() {
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (entity == mc.player) continue;
            if (!gui.isValidTarget(living)) continue;

            // Attack only when attack cooldown is full
            if (mc.player.getAttackStrengthScale(0.5F) >= 1.0F) {
                rotateTo(living);
                mc.player.swing(InteractionHand.MAIN_HAND);
                mc.player.attack(living);
            }
        }
    }

    private void rotateTo(Entity entity) {
        var eyePos = mc.player.getEyePosition(1.0F);
        var targetPos = entity.position().add(0, entity.getBbHeight() / 2, 0);
        var diff = targetPos.subtract(eyePos);

        float yaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)));

        mc.player.yRot = yaw;
        mc.player.xRot = pitch;
    }

    // GUI access
    public KillauraEntitySelectorGUI getGui() {
        return gui;
    }

    // Toggle the module
    public void toggle() {
        enabled = !enabled;
    }
}
