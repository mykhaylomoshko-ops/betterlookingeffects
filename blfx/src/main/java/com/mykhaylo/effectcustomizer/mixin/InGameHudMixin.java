package com.mykhaylo.effectcustomizer.mixin;

import com.mykhaylo.effectcustomizer.EffectFilter;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    // The little icons in the top-right of your screen
    @ModifyVariable(method = "renderStatusEffectOverlay", at = @At("STORE"), ordinal = 0)
    private Collection<StatusEffectInstance> effectcustomizer$filter(Collection<StatusEffectInstance> effects) {
        return EffectFilter.apply(effects);
    }
}
