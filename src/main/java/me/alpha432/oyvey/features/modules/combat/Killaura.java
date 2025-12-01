package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.gui.KillauraEntitySelectorGUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;

import net.minecraft.world.InteractionHand;

import java.util.List;

@Environment(EnvType.CLIENT)
public class Killaura extends Module {

    private final Minecraft mc = Minecraft.getInstance();
    public KillauraEntitySelectorGUI selectorGUI;

    public Killaura() {
        super("Killaura", "Attacks entities automatically", Category.COMBAT);
        selectorGUI = new KillauraEntitySelectorGUI();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;
        if (!mc.player.isAlive()) return;

        // Find targets in a 4-block radius (adjust)
        List<LivingEntity> targets = mc.level.getEntitiesOfClass(
                LivingEntity.class,
                mc.player.getBoundingBox().inflate(4),
                e -> e != mc.player && e.isAlive() && isValidTarget(e)
        );

        for (LivingEntity target : targets) {
            attackEntity(target);
        }
    }

    private boolean isValidTarget(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;

        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();

        if (entity instanceof Player && !selectorGUI.isEnabled(TargetType.PLAYER))
            return false;

        if (id.contains("zombie") || id.contains("skeleton") || id.contains("monster")) {
            if (!selectorGUI.isEnabled(TargetType.MOB)) return false;
        }

        if (id.contains("cow") || id.contains("pig") || id.contains("animal")) {
            if (!selectorGUI.isEnabled(TargetType.ANIMAL)) return false;
        }

        return true;
    }

    private void attackEntity(LivingEntity entity) {
        if (mc.player == null || mc.gameMode == null) return;

        // Face target
        mc.player.lookAt(Anchor.EYES, entity.position());

        // Swing main hand
        mc.player.swing(InteractionHand.MAIN_HAND);

        // Attack!
        mc.gameMode.attack(mc.player, entity);
    }
}
