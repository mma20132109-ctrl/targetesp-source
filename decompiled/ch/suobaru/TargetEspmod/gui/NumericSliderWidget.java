/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11908
 *  net.minecraft.class_2561
 *  net.minecraft.class_357
 */
package ch.suobaru.TargetEspmod.gui;

import ch.suobaru.TargetEspmod.config.EffectSettings;
import java.util.Locale;
import java.util.function.DoubleConsumer;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_357;

final class NumericSliderWidget
extends class_357 {
    private final String translationKey;
    private final EffectSettings.Range range;
    private final int decimalPlaces;
    private final DoubleConsumer onChanged;
    private double actualValue;

    NumericSliderWidget(int x, int y, int width, int height, String translationKey, EffectSettings.Range range, double initialValue, int decimalPlaces, DoubleConsumer onChanged) {
        super(x, y, width, height, (class_2561)class_2561.method_43473(), NumericSliderWidget.sliderPosition(range, initialValue));
        this.translationKey = translationKey;
        this.range = range;
        this.decimalPlaces = Math.max(0, decimalPlaces);
        this.onChanged = onChanged;
        this.actualValue = range.normalize(initialValue, range.min());
        this.field_22753 = NumericSliderWidget.sliderPosition(range, this.actualValue);
        this.method_25346();
    }

    protected void method_25346() {
        this.method_25355((class_2561)class_2561.method_43469((String)this.translationKey, (Object[])new Object[]{this.formattedValue()}));
    }

    protected void method_25344() {
        double requested = this.range.min() + (this.range.max() - this.range.min()) * this.field_22753;
        this.actualValue = this.range.normalize(requested, this.actualValue);
        this.field_22753 = NumericSliderWidget.sliderPosition(this.range, this.actualValue);
        this.onChanged.accept(this.actualValue);
    }

    public boolean method_25404(class_11908 input) {
        if (this.field_41796 && (input.method_74232() || input.method_74233())) {
            double direction = input.method_74232() ? -1.0 : 1.0;
            this.actualValue = this.range.normalize(this.actualValue + direction * this.range.step(), this.actualValue);
            this.field_22753 = NumericSliderWidget.sliderPosition(this.range, this.actualValue);
            this.onChanged.accept(this.actualValue);
            this.method_25346();
            return true;
        }
        return super.method_25404(input);
    }

    private String formattedValue() {
        if (this.decimalPlaces == 0) {
            return Long.toString(Math.round(this.actualValue));
        }
        return String.format(Locale.ROOT, "%." + this.decimalPlaces + "f", this.actualValue);
    }

    private static double sliderPosition(EffectSettings.Range range, double actualValue) {
        if (range.max() <= range.min()) {
            return 0.0;
        }
        double normalized = range.normalize(actualValue, range.min());
        return (normalized - range.min()) / (range.max() - range.min());
    }
}

