package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.gui.items.buttons.BooleanButton;
import me.alpha432.oyvey.features.gui.items.buttons.EntityListButton;
import me.alpha432.oyvey.features.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public class Killaura extends Module {

    public Setting<Boolean> targetPlayers;
    public Setting<Boolean> targetPhantoms;

    private final List<BooleanButton> entityButtons = new ArrayList<>();
    public final EntityListButton entityListButton;

    public Killaura() {
        super("Killaura", "Automatically attacks entities around you", Category.COMBAT);

        targetPlayers = register(new Setting<>("Players", true));
        targetPhantoms = register(new Setting<>("Phantoms", false));

        entityButtons.add(new BooleanButton("Players", targetPlayers));
        entityButtons.add(new BooleanButton("Phantoms", targetPhantoms));

        this.entityListButton = new EntityListButton("Target Entities", this);
    }

    public List<BooleanButton> getEntityButtons() {
        return entityButtons;
    }
}
