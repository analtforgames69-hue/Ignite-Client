package me.alpha432.oyvey.features.gui.items.buttons;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.gui.items.Item;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.combat.Killaura;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class EntityListButton extends Item {

    private final Killaura module;
    private final List<String> entities = new ArrayList<>();
    private boolean extended;

    public EntityListButton(Module module) {
        super("Entities");
        if (!(module instanceof Killaura)) {
            throw new IllegalArgumentException("EntityListButton can only be used with Killaura!");
        }
        this.module = (Killaura) module;
        this.height = 15;

        // default selection
        if (this.module.targetPlayer.getValue()) entities.add("Player");
        if (this.module.targetPhantom.getValue()) entities.add("Phantom");
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        int color = !this.isHovering(mouseX, mouseY) ? OyVey.colorManager.getColorWithAlpha(OyVey.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())
                : OyVey.colorManager.getColorWithAlpha(OyVey.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue());

        RenderUtil.rect(context.getMatrices(), this.x, this.y, this.x + this.width, this.y + this.height - 0.5f, color);

        // Main label
        drawString(context, "Entities Selected: " + entities.size() + " +", this.x + 2.3f, this.y - 2.0f - OyVey.getTextManager().getFontHeight(), -1);

        // Draw dropdown if extended
        if (extended) {
            int yOffset = 12;
            for (String entity : List.of("Player", "Phantom")) {
                boolean selected = entities.contains(entity);
                int optionColor = selected ? 0xFF00FF00 : 0xFFAAAAAA;
                drawString(context, entity, this.x + 5f, this.y + yOffset, optionColor);
                yOffset += 12;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
            extended = !extended;
        }

        if (extended && mouseButton == 0) {
            int yOffset = 12;
            for (String entity : List.of("Player", "Phantom")) {
                if (isHoveringOption(mouseX, mouseY, yOffset)) {
                    toggleEntity(entity);
                }
                yOffset += 12;
            }
        }
    }

    private void toggleEntity(String entity) {
        if (entities.contains(entity)) {
            entities.remove(entity);
            setModuleValue(entity, false);
        } else {
            entities.add(entity);
            setModuleValue(entity, true);
        }
    }

    private void setModuleValue(String entity, boolean value) {
        switch (entity) {
            case "Player" -> module.targetPlayer.setValue(value);
            case "Phantom" -> module.targetPhantom.setValue(value);
        }
    }

    private boolean isHoveringOption(int mouseX, int mouseY, int yOffset) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + yOffset && mouseY <= this.y + yOffset + 12;
    }

    @Override
    public int getHeight() {
        return extended ? 15 + 24 : 15; // 15 for main button + 12px per entity option
    }
}
