package com.redteam.client;

import com.redteam.client.modules.Module;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Простое меню: левая кнопка - включить/выключить чит, правая маленькая кнопка - сменить режим. */
public class MenuScreen extends Screen {
    public MenuScreen() { super(Text.literal("RedTeam Test Client")); }

    @Override
    protected void init() {
        int y = 34;
        for (Module m : ModuleManager.MODULES) {
            ButtonWidget toggle = ButtonWidget.builder(label(m), b -> {
                m.toggle();
                b.setMessage(label(m));
            }).dimensions(width / 2 - 100, y, 150, 20).build();
            addDrawableChild(toggle);

            addDrawableChild(ButtonWidget.builder(Text.literal("режим"), b -> {
                m.cycleMode();
                toggle.setMessage(label(m));
            }).dimensions(width / 2 + 54, y, 46, 20).build());
            y += 22;
        }
    }

    private static Text label(Module m) {
        String d = m.getDetails();
        return Text.literal(m.name + ": " + (m.isEnabled() ? "ВКЛ" : "выкл") + (d.isEmpty() ? "" : " [" + d + "]"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "RedTeam Test Client", width / 2, 20, 0xFFFFFF);
    }
}
