package me.alpha432.oyvey.features.gui;

import me.alpha432.oyvey.features.modules.combat.TargetType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.*;

@Environment(EnvType.CLIENT)
public class KillauraEntitySelectorGUI {

    private final Map<TargetType, Boolean> targetToggles = new EnumMap<>(TargetType.class);
    private String searchQuery = "";
    private final Minecraft mc = Minecraft.getInstance();

    public KillauraEntitySelectorGUI() {
        for (TargetType t : TargetType.values()) {
            targetToggles.put(t, t == TargetType.PLAYER); // Default: only player
        }
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q.toLowerCase();
    }

    public void toggle(TargetType t) {
        targetToggles.put(t, !targetToggles.get(t));
    }

    public boolean isEnabled(TargetType t) {
        return targetToggles.getOrDefault(t, false);
    }

    public List<TargetType> getVisibleTargets() {
        return Arrays.stream(TargetType.values())
                .filter(t -> t.name().toLowerCase().contains(searchQuery))
                .toList();
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        int y = 20;

        // Title / Search
        graphics.drawString(
                mc.font,
                Component.literal("Search: " + searchQuery),
                10,
                10,
                0xFFFFFF,
                false
        );

        // Render toggles
        for (TargetType type : getVisibleTargets()) {
            boolean enabled = isEnabled(type);
            String label = (enabled ? "[X] " : "[ ] ") + type.name();

            graphics.drawString(
                    mc.font,
                    Component.literal(label),
                    10,
                    y,
                    0xFFFFFF,
                    false
            );

            y += 12;
        }
    }

    public void handleClick(double mouseX, double mouseY) {
        int y = 20;

        for (TargetType type : getVisibleTargets()) {
            if (mouseX >= 10 && mouseX <= 110 && mouseY >= y && mouseY <= y + 10) {
                toggle(type);
            }
            y += 12;
        }
    }
}
