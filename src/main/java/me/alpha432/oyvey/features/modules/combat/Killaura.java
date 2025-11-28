package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.gui.items.buttons.BooleanButton;
import me.alpha432.oyvey.features.gui.items.buttons.EntityListButton;
import me.alpha432.oyvey.features.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public class Killaura extends Module {

    // Settings (replace Setting with your correct Settings class if needed)
    public Setting<Boolean> targetPlayers;
    public Setting<Boolean> targetPhantoms;

    // List of BooleanButtons for the entity list
    private final List<BooleanButton> entityButtons = new ArrayList<>();
    public EntityListButton entityListButton;

    public Killaura() {
        super("Killaura", "Automatically attacks entities around you", Module.Category.COMBAT);

        // Initialize settings
        targetPlayers = register(new Setting<>("Players", true));
        targetPhantoms = register(new Setting<>("Phantoms", false));

        // Initialize entity buttons
        entityButtons.add(new BooleanButton("Players", targetPlayers));
        entityButtons.add(new BooleanButton("Phantoms", targetPhantoms));

        // Initialize entity list button
        this.entityListButton = new EntityListButton("Target Entities", this);
    }

    // Getter for entity buttons
    public List<BooleanButton> getEntityButtons() {
        return entityButtons;
    }
}
