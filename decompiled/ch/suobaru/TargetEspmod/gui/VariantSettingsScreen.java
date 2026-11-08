/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 */
package ch.suobaru.TargetEspmod.gui;

import ch.suobaru.TargetEspmod.Variant;
import ch.suobaru.TargetEspmod.config.EffectSettings;
import ch.suobaru.TargetEspmod.gui.NumericSliderWidget;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public final class VariantSettingsScreen
extends class_437 {
    private static final int CONTROL_WIDTH = 220;
    private static final int CONTROL_HEIGHT = 20;
    private static final int CONTROL_GAP = 4;
    private static final int FOOTER_GAP = 4;
    private final class_437 parent;
    private final Variant variant;
    private final Consumer<EffectSettings> onDone;
    private EffectSettings workingSettings;
    private int nextControlY;
    private int settingControlHeight = 20;
    private int settingControlGap = 4;

    public VariantSettingsScreen(class_437 parent, Variant variant, EffectSettings initialSettings, Consumer<EffectSettings> onDone) {
        super((class_2561)class_2561.method_43469((String)"screen.targetespmod.settings_title", (Object[])new Object[]{Objects.requireNonNull(variant, "variant").displayName()}));
        this.parent = Objects.requireNonNull(parent, "parent");
        this.variant = variant;
        this.workingSettings = EffectSettings.sanitize(variant, initialSettings);
        this.onDone = Objects.requireNonNull(onDone, "onDone");
    }

    protected void method_25426() {
        int settingCount = this.settingCount();
        int footerY = this.field_22790 - 28;
        int availableHeight = Math.max(0, footerY - 32);
        if (settingCount > 0) {
            this.settingControlHeight = Math.clamp((long)(availableHeight / settingCount), 14, 20);
            int remainingForGaps = availableHeight - settingCount * this.settingControlHeight;
            this.settingControlGap = settingCount == 1 ? 0 : Math.clamp((long)(remainingForGaps / (settingCount - 1)), 0, 4);
        }
        int settingsHeight = settingCount == 0 ? 0 : settingCount * this.settingControlHeight + (settingCount - 1) * this.settingControlGap;
        this.nextControlY = 32 + Math.max(0, (availableHeight - settingsHeight) / 2);
        if (EffectSettings.usesSize(this.variant)) {
            this.addFloatSlider("screen.targetespmod.setting.size", EffectSettings.sizeRange(this.variant), this.workingSettings.size(), 2, value -> {
                this.workingSettings = this.workingSettings.withSize((float)value);
            });
        }
        if (EffectSettings.usesSpeed(this.variant)) {
            this.addFloatSlider("screen.targetespmod.setting.speed", EffectSettings.speedRange(this.variant), this.workingSettings.speed(), 2, value -> {
                this.workingSettings = this.workingSettings.withSpeed((float)value);
            });
        }
        if (EffectSettings.usesRadius(this.variant)) {
            this.addFloatSlider("screen.targetespmod.setting.radius", EffectSettings.radiusRange(this.variant), this.workingSettings.radius(), 2, value -> {
                this.workingSettings = this.workingSettings.withRadius((float)value);
            });
        }
        if (EffectSettings.usesWaveHeight(this.variant)) {
            this.addFloatSlider("screen.targetespmod.setting.wave_height", EffectSettings.waveHeightRange(this.variant), this.workingSettings.waveHeight(), 2, value -> {
                this.workingSettings = this.workingSettings.withWaveHeight((float)value);
            });
        }
        if (EffectSettings.usesAmount(this.variant)) {
            this.addIntegerSlider(this.amountTranslationKey(), EffectSettings.amountRange(this.variant), this.workingSettings.amount(), value -> {
                this.workingSettings = this.workingSettings.withAmount((int)Math.round(value));
            });
        }
        if (EffectSettings.usesTrailLength(this.variant)) {
            this.addIntegerSlider("screen.targetespmod.setting.trail_length", EffectSettings.trailLengthRange(this.variant), this.workingSettings.trailLength(), value -> {
                this.workingSettings = this.workingSettings.withTrailLength((int)Math.round(value));
            });
        }
        int left = (this.field_22789 - 220) / 2;
        int halfWidth = 108;
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)"screen.targetespmod.reset"), button -> this.resetSettings()).method_46434(left, footerY, halfWidth, 20).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)"screen.targetespmod.save"), button -> this.acceptAndClose()).method_46434(left + halfWidth + 4, footerY, halfWidth, 20).method_46431());
    }

    private void addFloatSlider(String translationKey, EffectSettings.Range range, float value, int decimalPlaces, DoubleConsumer onChanged) {
        this.addSlider(translationKey, range, value, decimalPlaces, onChanged);
    }

    private void addIntegerSlider(String translationKey, EffectSettings.Range range, int value, DoubleConsumer onChanged) {
        this.addSlider(translationKey, range, value, 0, onChanged);
    }

    private void addSlider(String translationKey, EffectSettings.Range range, double value, int decimalPlaces, DoubleConsumer onChanged) {
        int x = (this.field_22789 - 220) / 2;
        this.method_37063((class_364)new NumericSliderWidget(x, this.nextControlY, 220, this.settingControlHeight, translationKey, range, value, decimalPlaces, onChanged));
        this.nextControlY += this.settingControlHeight + this.settingControlGap;
    }

    private int settingCount() {
        int count = 0;
        count += EffectSettings.usesSize(this.variant) ? 1 : 0;
        count += EffectSettings.usesSpeed(this.variant) ? 1 : 0;
        count += EffectSettings.usesRadius(this.variant) ? 1 : 0;
        count += EffectSettings.usesWaveHeight(this.variant) ? 1 : 0;
        count += EffectSettings.usesAmount(this.variant) ? 1 : 0;
        return count += EffectSettings.usesTrailLength(this.variant) ? 1 : 0;
    }

    private String amountTranslationKey() {
        return switch (this.variant) {
            case Variant.GHOSTS -> "screen.targetespmod.setting.ghost_particles";
            case Variant.GHOSTS_2 -> "screen.targetespmod.setting.trails";
            case Variant.CUBES -> "screen.targetespmod.setting.cube_limit";
            case Variant.CRYSTALS -> "screen.targetespmod.setting.crystals";
            default -> "screen.targetespmod.setting.amount";
        };
    }

    private void resetSettings() {
        if (this.field_22787 != null) {
            this.field_22787.method_1507((class_437)new VariantSettingsScreen(this.parent, this.variant, EffectSettings.defaultsFor(this.variant), this.onDone));
        }
    }

    private void acceptAndClose() {
        this.onDone.accept(EffectSettings.sanitize(this.variant, this.workingSettings));
        if (this.field_22787 != null) {
            this.field_22787.method_1507(this.parent);
        }
    }

    public void method_25419() {
        if (this.field_22787 != null) {
            this.field_22787.method_1507(this.parent);
        }
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        super.method_25394(context, mouseX, mouseY, delta);
        context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 16, 0xFFFFFF);
    }

    public boolean method_25421() {
        return false;
    }
}

