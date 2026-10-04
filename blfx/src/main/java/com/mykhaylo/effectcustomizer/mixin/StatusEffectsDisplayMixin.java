package com.mykhaylo.effectcustomizer.mixin;

import com.mykhaylo.effectcustomizer.EffectFilter;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;

@Mixin(StatusEffectsDisplay.class)
public class StatusEffectsDisplayMixin {
    // Inventory effect list (1.21.11: the private drawStatusEffects overload that receives the effect collection)
    @ModifyVariable(
            method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;Ljava/util/Collection;IIIII)V",
            at = @At("HEAD"), argsOnly = true)
    private Collection<StatusEffectInstance> effectcustomizer$filter(Collection<StatusEffectInstance> effects) {
        return EffectFilter.apply(effects);
    }
}
