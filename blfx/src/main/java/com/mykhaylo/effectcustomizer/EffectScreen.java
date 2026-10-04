package com.mykhaylo.effectcustomizer;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class EffectScreen extends Screen {
    private static final int PER_PAGE = 6;
    private static final int TOP = 50;
    private static final int ROW_H = 28;

    private final List<Identifier> ids;
    private int page = 0;

    public EffectScreen() {
        super(Text.literal("Betterlooking Effects"));
        ids = Registries.STATUS_EFFECT.getIds().stream().sorted().toList();
    }

    private int pages() { return (ids.size() + PER_PAGE - 1) / PER_PAGE; }

    @Override
    protected void init() {
        EffectConfig cfg = EffectConfig.get();
        int cx = width / 2;

        int start = page * PER_PAGE;
        int end = Math.min(ids.size(), start + PER_PAGE);
        for (int i = start; i < end; i++) {
            Identifier id = ids.get(i);
            EffectConfig.Entry e = cfg.entry(id.toString());
            int y = TOP + (i - start) * ROW_H;

            addDrawableChild(ButtonWidget.builder(Text.literal(stateLabel(e)), b -> {
                if (!e.hidden && !e.forced) { e.hidden = true; }
                else if (e.hidden) { e.hidden = false; e.forced = true; }
                else { e.forced = false; }
                b.setMessage(Text.literal(stateLabel(e)));
            }).dimensions(cx - 20, y, 54, 20).build());

            TextFieldWidget lvl = new TextFieldWidget(textRenderer, cx + 42, y, 40, 20, Text.empty());
            lvl.setPlaceholder(Text.literal("real"));
            lvl.setMaxLength(3);
            lvl.setText(e.level > 0 ? String.valueOf(e.level) : "");
            lvl.setChangedListener(s -> e.level = parseInt(s, 255));
            addDrawableChild(lvl);

            TextFieldWidget dur = new TextFieldWidget(textRenderer, cx + 90, y, 58, 20, Text.empty());
            dur.setPlaceholder(Text.literal("real"));
            dur.setMaxLength(6);
            dur.setText(e.infinite ? "inf" : (e.durationSeconds > 0 ? String.valueOf(e.durationSeconds) : ""));
            dur.setChangedListener(s -> {
                if (s.trim().equalsIgnoreCase("inf")) { e.infinite = true; e.durationSeconds = 0; }
                else { e.infinite = false; e.durationSeconds = parseInt(s, 999999); }
            });
            addDrawableChild(dur);
        }

        int by = TOP + PER_PAGE * ROW_H + 8;
        addDrawableChild(ButtonWidget.builder(Text.literal("< Prev"), b -> {
            page = (page - 1 + pages()) % pages();
            clearAndInit();
        }).dimensions(cx - 150, by, 60, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Next >"), b -> {
            page = (page + 1) % pages();
            clearAndInit();
        }).dimensions(cx + 90, by, 60, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Hide ALL: " + (cfg.hideAll ? "ON" : "OFF")), b -> {
            cfg.hideAll = !cfg.hideAll;
            b.setMessage(Text.literal("Hide ALL: " + (cfg.hideAll ? "ON" : "OFF")));
        }).dimensions(cx - 85, by, 80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(cx + 0, by, 80, 20).build());
    }

    private static String stateLabel(EffectConfig.Entry e) {
        return e.hidden ? "Hidden" : (e.forced ? "Forced" : "Shown");
    }

    private static int parseInt(String s, int max) {
        try {
            int v = Integer.parseInt(s.trim());
            return Math.max(0, Math.min(max, v));
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta); // draws background + widgets
        int cx = width / 2;
        drawMascot(ctx, cx);
        ctx.drawCenteredTextWithShadow(textRenderer, title, cx, 12, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Shown / Hidden / Forced (show even if you don't have it)  |  blank = real"), cx, 26, 0xAAAAAA);
        ctx.drawTextWithShadow(textRenderer, "Effect", cx - 170, 38, 0xFFFF55);
        ctx.drawTextWithShadow(textRenderer, "Show", cx - 20, 38, 0xFFFF55);
        ctx.drawTextWithShadow(textRenderer, "Level", cx + 42, 38, 0xFFFF55);
        ctx.drawTextWithShadow(textRenderer, "Duration", cx + 90, 38, 0xFFFF55);

        int start = page * PER_PAGE;
        int end = Math.min(ids.size(), start + PER_PAGE);
        for (int i = start; i < end; i++) {
            var eff = Registries.STATUS_EFFECT.get(ids.get(i));
            String name = eff != null ? eff.getName().getString() : ids.get(i).toString();
            ctx.drawTextWithShadow(textRenderer, name, cx - 170, TOP + (i - start) * ROW_H + 6, 0xFFFFFF);
        }
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Page " + (page + 1) + "/" + pages()), cx, TOP + PER_PAGE * ROW_H + 34, 0x888888);
    }

    private static final Identifier MASCOT = Identifier.of("betterlookingeffects", "textures/gui/mascot.png");
    private static final int MASCOT_TEX_W = 376, MASCOT_TEX_H = 697;

    /** Transparent character art on the left of the menu. Only drawn if there's room. */
    private void drawMascot(DrawContext ctx, int cx) {
        int w = 58, h = (int) (w * (MASCOT_TEX_H / (float) MASCOT_TEX_W));
        int x = cx - 238, y = 70;
        if (x < 2) return;
        try {
            ctx.drawTexture(RenderPipelines.GUI_TEXTURED, MASCOT, x, y, 0f, 0f, w, h,
                    MASCOT_TEX_W, MASCOT_TEX_H, MASCOT_TEX_W, MASCOT_TEX_H);
        } catch (Throwable ignored) { }
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void close() {
        EffectConfig.save();
        super.close();
    }
}
