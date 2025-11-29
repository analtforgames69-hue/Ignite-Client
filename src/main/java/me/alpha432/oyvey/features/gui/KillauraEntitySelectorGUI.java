package me.alpha432.oyvey.features.gui;

import me.alpha432.oyvey.features.modules.combat.TargetType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Text;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class KillauraEntitySelectorGUI {

    private final Map<TargetType, Boolean> targetToggles = new EnumMap<>(TargetType.class);
    private String searchQuery = "";
    private final Minecraft mc = Minecraft.getInstance();

    public KillauraEntitySelectorGUI() {
        for (TargetType type : TargetType.values()) {
            targetToggles.put(type, type == TargetType.PLAYER); // Default: only players
        }
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query.toLowerCase();
    }

    public void toggle(TargetType type) {
        targetToggles.put(type, !targetToggles.get(type));
    }

    public boolean isEnabled(TargetType type) {
        return targetToggles.getOrDefault(type, false);
    }

    public List<TargetType> getVisibleTargets() {
        return Arrays.stream(TargetType.values())
                .filter(t -> t.name().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());
    }

    /**
     * Renders the GUI using GuiGraphics (new in 1.20+ / 1.21.5)
     */
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        int y = 20;

        // Draw search query
        graphics.drawString(mc.font, Text.literal("Search: " + searchQuery), 10, 10, 0xFFFFFF, false);

        // Draw each target toggle
        for (TargetType type : getVisibleTargets()) {
            boolean enabled = isEnabled(type);
            String label = (enabled ? "[X] " : "[ ] ") + type.name();
            graphics.drawString(mc.font, Text.literal(label), 10, y, 0xFFFFFF, false);
            y += 12;
        }
    }

    /**
     * Handles click events on the GUI toggles
     */
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
