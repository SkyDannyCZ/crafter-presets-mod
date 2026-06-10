package com.crafterpresets.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class PresetListScreen extends Screen {

    private final Screen parent;
    private final boolean[] currentSlots;
    private final Consumer<boolean[]> onLoad;

    private TextFieldWidget nameField;
    private int selectedIndex = -1;
    private int scrollOffset = 0;

    private static final int ITEM_HEIGHT = 22;
    private static final int LIST_TOP = 60;
    private static final int LIST_BOTTOM_MARGIN = 80;
    private static final int PANEL_WIDTH = 260;

    public PresetListScreen(Screen parent, boolean[] currentSlots, Consumer<boolean[]> onLoad) {
        super(Text.translatable("crafterpresets.screen.title"));
        this.parent = parent;
        this.currentSlots = currentSlots.clone();
        this.onLoad = onLoad;
    }

    @Override
    protected void init() {
        PresetStorage.load();

        int centerX = this.width / 2;
        int listBottom = this.height - LIST_BOTTOM_MARGIN;
        int panelLeft = centerX - PANEL_WIDTH / 2;

        nameField = new TextFieldWidget(
                this.textRenderer,
                panelLeft,
                listBottom + 8,
                PANEL_WIDTH - 90,
                16,
                Text.translatable("crafterpresets.label.name")
        );
        nameField.setMaxLength(32);
        nameField.setPlaceholder(Text.literal("Preset name..."));
        this.addDrawableChild(nameField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("crafterpresets.button.save"),
                btn -> savePreset()
        ).dimensions(panelLeft + PANEL_WIDTH - 86, listBottom + 6, 86, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("crafterpresets.button.load"),
                btn -> loadSelected()
        ).dimensions(panelLeft, listBottom + 32, (PANEL_WIDTH / 2) - 2, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("crafterpresets.button.delete"),
                btn -> deleteSelected()
        ).dimensions(panelLeft + (PANEL_WIDTH / 2) + 2, listBottom + 32, (PANEL_WIDTH / 2) - 2, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("crafterpresets.button.back"),
                btn -> this.close()
        ).dimensions(centerX - 60, this.height - 24, 120, 16).build());
    }

    private void savePreset() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) return;
        PresetStorage.addPreset(name, currentSlots);
        nameField.setText("");
        selectedIndex = PresetStorage.getPresets().size() - 1;
    }

    private void loadSelected() {
        List<PresetStorage.Preset> presets = PresetStorage.getPresets();
        if (selectedIndex < 0 || selectedIndex >= presets.size()) return;
        onLoad.accept(presets.get(selectedIndex).disabledSlots);
        this.close();
    }

    private void deleteSelected() {
        if (selectedIndex < 0 || selectedIndex >= PresetStorage.getPresets().size()) return;
        PresetStorage.deletePreset(selectedIndex);
        selectedIndex = Math.min(selectedIndex, PresetStorage.getPresets().size() - 1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int panelLeft = centerX - PANEL_WIDTH / 2;
        int listBottom = this.height - LIST_BOTTOM_MARGIN;
        List<PresetStorage.Preset> presets = PresetStorage.getPresets();

        for (int i = 0; i < presets.size(); i++) {
            int itemY = LIST_TOP + (i - scrollOffset) * ITEM_HEIGHT;
            if (itemY >= LIST_TOP && itemY + ITEM_HEIGHT <= listBottom) {
                if (mouseX >= panelLeft && mouseX <= panelLeft + PANEL_WIDTH
                        && mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT) {
                    selectedIndex = i;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int visibleItems = (this.height - LIST_BOTTOM_MARGIN - LIST_TOP) / ITEM_HEIGHT;
        int maxScroll = Math.max(0, PresetStorage.getPresets().size() - visibleItems);
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount));
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int panelLeft = centerX - PANEL_WIDTH / 2;
        int listBottom = this.height - LIST_BOTTOM_MARGIN;

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                centerX, 12, 0xFFFFAA00
        );

        context.fill(panelLeft - 2, LIST_TOP - 2, panelLeft + PANEL_WIDTH + 2, listBottom + 2, 0x88000000);

        List<PresetStorage.Preset> presets = PresetStorage.getPresets();

        if (presets.isEmpty()) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("crafterpresets.label.empty"),
                    centerX,
                    LIST_TOP + 10,
                    0xAAAAAA
            );
        } else {
            for (int i = 0; i < presets.size(); i++) {
                int itemY = LIST_TOP + (i - scrollOffset) * ITEM_HEIGHT;
                if (itemY < LIST_TOP || itemY + ITEM_HEIGHT > listBottom) continue;

                PresetStorage.Preset preset = presets.get(i);
                boolean isSelected = i == selectedIndex;

                int bgColor = isSelected ? 0xFF334433 : (i % 2 == 0 ? 0xFF1A1A1A : 0xFF222222);
                context.fill(panelLeft, itemY, panelLeft + PANEL_WIDTH, itemY + ITEM_HEIGHT, bgColor);

                if (isSelected) {
                    context.drawBorder(panelLeft, itemY, PANEL_WIDTH, ITEM_HEIGHT, 0xFFFFAA00);
                }

                drawMiniGrid(context, panelLeft + 4, itemY + 3, preset.disabledSlots);

                context.drawTextWithShadow(
                        this.textRenderer,
                        preset.name,
                        panelLeft + 44,
                        itemY + 4,
                        0xFFFFFF
                );

                int disabled = preset.countDisabled();
                String countStr = disabled + " " + Text.translatable("crafterpresets.label.slots").getString()
                        + " " + Text.translatable("crafterpresets.label.disabled").getString();
                context.drawTextWithShadow(
                        this.textRenderer,
                        countStr,
                        panelLeft + 44,
                        itemY + 13,
                        0x888888
                );
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawMiniGrid(DrawContext context, int x, int y, boolean[] slots) {
        int cellSize = 5;
        int gap = 1;
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            int cx = x + col * (cellSize + gap);
            int cy = y + row * (cellSize + gap);
            int color = slots[i] ? 0xFF880000 : 0xFF445544;
            context.fill(cx, cy, cx + cellSize, cy + cellSize, color);
        }
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
