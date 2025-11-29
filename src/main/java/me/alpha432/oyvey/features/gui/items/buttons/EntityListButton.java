package me.alpha432.oyvey.features.gui.items.buttons;

import me.alpha432.oyvey.features.modules.combat.Killaura;
import me.alpha432.oyvey.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.List;

public class EntityListButton extends Button {
    private final Killaura module;
    private boolean extended = false;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public EntityListButton(String name, Killaura module) {
        super(name);
        this.module = module;
        this.height = 15;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        // Draw main button box
        RenderUtil.rect(
                context.getMatrices(),
                this.x,
                this.y,
                this.x + this.width,
                this.y + this.height - 0.5f,
                this.isHovering(mouseX, mouseY) ? 0x77111111 : 0x55111111
        );

        // Draw text
        context.drawText(
                mc.textRenderer,
                this.name,
                (int) (this.x + 2.3f),
                (int) (this.y + 2),
                0xFFFFFF,
                false
        );

        // Draw extended buttons
        if (extended) {
            float offsetY = this.y + this.height;

            List<BooleanButton> btns = module.getEntityButtons();
            for (BooleanButton btn : btns) {
                btn.setX(this.x);
                btn.setY(offsetY);
                btn.setWidth(this.width);
                btn.render(context, mouseX, mouseY, delta);

                offsetY += btn.getHeight();
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == 0 && isHovering(mouseX, mouseY)) {
            this.extended = !this.extended;
            mc.getSoundManager().play(
                    PositionedSoundInstance.create(SoundEvents.UI_BUTTON_CLICK)
            );
            return true;
        }

        if (extended) {
            for (BooleanButton btn : module.getEntityButtons()) {
                btn.mouseClicked(mouseX, mouseY, button);
            }
        }

        return false;
    }

    @Override
    public int getHeight() {
        int total = 15;
        if (extended) {
            for (BooleanButton btn : module.getEntityButtons()) {
                total += btn.getHeight();
            }
        }
        return total;
    }
}
