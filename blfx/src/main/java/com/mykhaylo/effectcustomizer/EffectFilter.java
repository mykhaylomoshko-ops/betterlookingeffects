package com.mykhaylo.effectcustomizer;

import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EffectFilter {
    /** Takes the real effects, returns what should be DISPLAYED. Purely visual. */
    public static Collection<StatusEffectInstance> apply(Collection<StatusEffectInstance> real) {
        try {
            return applyUnsafe(real);
        } catch (Throwable t) {
            // Never crash the game over a cosmetic feature: fall back to vanilla display
            return real;
        }
    }

    private static Collection<StatusEffectInstance> applyUnsafe(Collection<StatusEffectInstance> real) {
        EffectConfig cfg = EffectConfig.get();
        if (cfg.hideAll) return List.of();

        List<StatusEffectInstance> out = new ArrayList<>();
        for (StatusEffectInstance inst : real) {
            String id = inst.getEffectType().getKey().map(k -> k.getValue().toString()).orElse("unknown");
            EffectConfig.Entry e = cfg.effects.get(id);
            if (e == null) { out.add(inst); continue; }
            if (e.hidden) continue;

            int amp = e.level > 0 ? e.level - 1 : inst.getAmplifier();
            int dur = e.infinite ? -1 : (e.durationSeconds > 0 ? e.durationSeconds * 20 : inst.getDuration());
            out.add(new StatusEffectInstance(inst.getEffectType(), dur, amp,
                    inst.isAmbient(), inst.shouldShowParticles(), inst.shouldShowIcon()));
        }
        // Forced effects: displayed even when you don't actually have them (e.g. saturation)
        for (var me : cfg.effects.entrySet()) {
            EffectConfig.Entry e = me.getValue();
            if (!e.forced || e.hidden) continue;
            boolean present = false;
            for (StatusEffectInstance inst : real) {
                if (inst.getEffectType().getKey().map(k -> k.getValue().toString()).orElse("").equals(me.getKey())) { present = true; break; }
            }
            if (present) continue;
            try {
                var entry = net.minecraft.registry.Registries.STATUS_EFFECT.getEntry(net.minecraft.util.Identifier.of(me.getKey()));
                if (entry.isEmpty()) continue;
                int amp = e.level > 0 ? e.level - 1 : 0;
                int dur = (e.infinite || e.durationSeconds <= 0) ? -1 : e.durationSeconds * 20;
                out.add(new StatusEffectInstance(entry.get(), dur, amp, false, true, true));
            } catch (Throwable ignored) { }
        }
        return out;
    }
}
