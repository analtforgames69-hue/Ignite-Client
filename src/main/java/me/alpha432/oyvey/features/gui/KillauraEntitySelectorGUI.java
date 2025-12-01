package me.alpha432.oyvey.features.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KillauraEntitySelectorGUI {

    private final Minecraft mc = Minecraft.getInstance();
    private final Set<Class<? extends LivingEntity>> selectedEntities = new HashSet<>(List.of(Player.class, LivingEntity.class));

    // Check if the entity is selected for attack
    public boolean isValidTarget(LivingEntity entity) {
        return selectedEntities.contains(entity.getClass()) && entity.isAlive();
    }

    // Render GUI
    public void render(GuiGraphics graphics) {
        int y = 10;
        List<Class<? extends LivingEntity>> types = List.of(Player.class, LivingEntity.class);
        for (Class<? extends LivingEntity> clazz : types) {
            Component text = Component.literal(clazz.getSimpleName() + (selectedEntities.contains(clazz) ? " [X]" : ""));
            graphics.drawString(mc.font, text, 10, y, 0xFFFFFF);
            y += 12;
        }
    }

    // Handle mouse click
    public void onMouseClick(double mouseX, double mouseY, int button) {
        int y = 10;
        List<Class<? extends LivingEntity>> types = List.of(Player.class, LivingEntity.class);
        for (Class<? extends LivingEntity> clazz : types) {
            if (mouseY > y && mouseY < y + 12) {
                if (selectedEntities.contains(clazz)) selectedEntities.remove(clazz);
                else selectedEntities.add(clazz);
            }
            y += 12;
        }
    }
}
