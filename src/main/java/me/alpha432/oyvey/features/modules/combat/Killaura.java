package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.gui.items.buttons.EntityListButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class Killaura extends Module {

    public Setting<Float> range;
    public Setting<Boolean> rotate;
    public Setting<Boolean> targetPlayer;
    public Setting<Boolean> targetPhantom;

    public Killaura() {
        super("Killaura", "Automatically attacks entities", Category.COMBAT, true, false, false);

        // Settings
        this.range = register(new Setting<>("Range", 4.0f, 1.0f, 6.0f));
        this.rotate = register(new Setting<>("Rotate", true));
        this.targetPlayer = register(new Setting<>("TargetPlayer", true));
        this.targetPhantom = register(new Setting<>("TargetPhantom", false));

        // Add GUI button
        this.addSettings(range, rotate, targetPlayer, targetPhantom);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.Pre event) {
        if (OyVey.mc.player == null || OyVey.mc.world == null) return;

        for (Entity entity : OyVey.mc.world.getEntities()) {
            if (entity == OyVey.mc.player) continue;
            if (entity.getHealth() <= 0) continue;

            if (!isValidTarget(entity)) continue;

            // Rotate if enabled
            if (rotate.getValue()) {
                OyVey.mc.player.lookAt(entity.getPos());
            }

            // Attack
            if (OyVey.mc.player.getAttackCooldownProgress(0f) >= 1.0f) {
                OyVey.mc.player.attack(entity);
                OyVey.mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private boolean isValidTarget(Entity entity) {
        if (entity instanceof PlayerEntity && targetPlayer.getValue()) return true;
        // TODO: Add Phantom or other entity checks if needed
        return false;
    }

    @Override
    public void addSettings(Setting<?>... settings) {
        super.addSettings(settings);
        // This is where the EntityListButton could be added in your GUI system if needed
        // Example: OyVeyGui.getClickGui().addButton(new EntityListButton(this));
    }
}
