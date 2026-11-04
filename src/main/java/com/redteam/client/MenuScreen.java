package com.redteam.client;

import com.redteam.client.modules.Module;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Меню в две колонки: большая кнопка - вкл/выкл чит, маленькая «режим» - сменить режим. */
public class MenuScreen extends Screen {
    private static final int ROWS = 7;

    public MenuScreen() { super(Text.literal("RedTeam Test Client")); }

    @Override
    protected void init() {
        int i = 0;
        for (Module m : ModuleManager.MODULES) {
            int col = i / ROWS, row = i % ROWS;
            int x = width / 2 - 208 + col * 212;
            int y = 34 + row * 24;

            ButtonWidget toggle = ButtonWidget.builder(label(m), b -> {
                m.toggle();
                b.setMessage(label(m));
            }).dimensions(x, y, 160, 20).build();
            addDrawableChild(toggle);

            addDrawableChild(ButtonWidget.builder(Text.literal("режим"), b -> {
                m.cycleMode();
                toggle.setMessage(label(m));
            }).dimensions(x + 164, y, 44, 20).build());
            i++;
        }
    }

    private static Text label(Module m) {
        String d = m.getDetails();
        return Text.literal(m.name + ": " + (m.isEnabled() ? "ВКЛ" : "выкл") + (d.isEmpty() ? "" : " [" + d + "]"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "RedTeam Test Client", width / 2, 18, 0xFFFFFF);
    }
}
