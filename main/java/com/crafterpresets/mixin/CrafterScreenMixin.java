package com.crafterpresets.mixin;

import com.crafterpresets.client.PresetListScreen;
import net.minecraft.client.gui.screen.ingame.CrafterScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrafterScreen.class)
public abstract class CrafterScreenMixin extends HandledScreen<CrafterScreenHandler> {

    public CrafterScreenMixin(CrafterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void crafterpresets$addPresetsButton(CallbackInfo ci) {
        int btnX = this.x + this.backgroundWidth + 2;
        int btnY = this.y;

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("crafterpresets.button.presets"),
                btn -> {
                    boolean[] currentDisabled = getDisabledSlots();
                    this.client.setScreen(new PresetListScreen(
                            this,
                            currentDisabled,
                            loaded -> applyDisabledSlots(loaded)
                    ));
                }
        ).dimensions(btnX, btnY, 60, 20).build());
    }

    private boolean[] getDisabledSlots() {
        boolean[] disabled = new boolean[9];
        CrafterScreenHandler handler = this.getScreenHandler();
        for (int i = 0; i < 9; i++) {
            disabled[i] = handler.isDisabled(i);
        }
        return disabled;
    }

    private void applyDisabledSlots(boolean[] slots) {
        CrafterScreenHandler handler = this.getScreenHandler();
        for (int i = 0; i < 9; i++) {
            boolean shouldBeDisabled = slots[i];
            boolean isCurrentlyDisabled = handler.isDisabled(i);
            if (shouldBeDisabled != isCurrentlyDisabled) {
                assert this.client != null;
                this.client.interactionManager.clickSlot(
                        handler.syncId,
                        i,
                        0,
                        net.minecraft.screen.slot.SlotActionType.PICKUP,
                        this.client.player
                );
            }
        }
    }
}
