/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_3532
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 */
package ch.suobaru.TargetEspmod.gui;

import java.awt.Color;
import java.util.function.IntConsumer;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public final class ColorPickerScreen
extends class_437 {
    private static final int PICKER_WIDTH = 88;
    private static final int PICKER_HEIGHT = 78;
    private static final int HUE_HEIGHT = 6;
    private static final int HUE_GAP = 5;
    private static final int DONE_WIDTH = 88;
    private static final int DONE_HEIGHT = 20;
    private final class_437 parent;
    private final IntConsumer onDone;
    private float hue;
    private float saturation;
    private float brightness;
    private boolean draggingPicker;
    private boolean draggingHue;
    private int pickerX;
    private int pickerY;
    private int hueY;

    public ColorPickerScreen(class_437 parent, int initialColor, IntConsumer onDone) {
        super((class_2561)class_2561.method_43471((String)"screen.targetespmod.color"));
        this.parent = parent;
        this.onDone = onDone;
        int red = initialColor >> 16 & 0xFF;
        int green = initialColor >> 8 & 0xFF;
        int blue = initialColor & 0xFF;
        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    protected void method_25426() {
        int blockHeight = 119;
        this.pickerX = (this.field_22789 - 88) / 2;
        this.pickerY = (this.field_22790 - blockHeight) / 2;
        this.hueY = this.pickerY + 78 + 5;
        int doneY = this.hueY + 6 + 10;
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)"gui.done"), button -> this.acceptAndClose()).method_46434(this.pickerX, doneY, 88, 20).method_46431());
    }

    private int selectedColor() {
        return 0xFF000000 | Color.HSBtoRGB(this.hue, this.saturation, this.brightness) & 0xFFFFFF;
    }

    private void acceptAndClose() {
        this.onDone.accept(this.selectedColor());
        if (this.field_22787 != null) {
            this.field_22787.method_1507(this.parent);
        }
    }

    public void method_25419() {
        if (this.field_22787 != null) {
            this.field_22787.method_1507(this.parent);
        }
    }

    public boolean method_25402(class_11909 click, boolean doubled) {
        if (click.method_74245() == 0) {
            if (ColorPickerScreen.isHovered(click.comp_4798(), click.comp_4799(), this.pickerX, this.pickerY, 88, 78)) {
                this.draggingPicker = true;
                this.updatePicker(click.comp_4798(), click.comp_4799());
                return true;
            }
            if (ColorPickerScreen.isHovered(click.comp_4798(), click.comp_4799(), this.pickerX, this.hueY, 88, 6)) {
                this.draggingHue = true;
                this.updateHue(click.comp_4798());
                return true;
            }
        }
        return super.method_25402(click, doubled);
    }

    public boolean method_25403(class_11909 click, double offsetX, double offsetY) {
        if (click.method_74245() == 0 && this.draggingPicker) {
            this.updatePicker(click.comp_4798(), click.comp_4799());
            return true;
        }
        if (click.method_74245() == 0 && this.draggingHue) {
            this.updateHue(click.comp_4798());
            return true;
        }
        return super.method_25403(click, offsetX, offsetY);
    }

    public boolean method_25406(class_11909 click) {
        if (click.method_74245() == 0) {
            this.draggingPicker = false;
            this.draggingHue = false;
        }
        return super.method_25406(click);
    }

    private void updatePicker(double mouseX, double mouseY) {
        this.saturation = class_3532.method_15363((float)((float)((mouseX - (double)this.pickerX) / 88.0)), (float)0.0f, (float)1.0f);
        this.brightness = 1.0f - class_3532.method_15363((float)((float)((mouseY - (double)this.pickerY) / 78.0)), (float)0.0f, (float)1.0f);
    }

    private void updateHue(double mouseX) {
        this.hue = class_3532.method_15363((float)((float)((mouseX - (double)this.pickerX) / 88.0)), (float)0.0f, (float)1.0f);
    }

    private static boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= (double)x && mouseX <= (double)(x + width) && mouseY >= (double)y && mouseY <= (double)(y + height);
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        this.drawPicker(context);
        super.method_25394(context, mouseX, mouseY, delta);
        context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, this.pickerY - 16, 0xFFFFFF);
    }

    private void drawPicker(class_332 context) {
        int pureHue = 0xFF000000 | Color.HSBtoRGB(this.hue, 1.0f, 1.0f) & 0xFFFFFF;
        context.method_25294(this.pickerX - 1, this.pickerY - 1, this.pickerX + 88 + 1, this.pickerY + 78 + 1, -15592942);
        for (int column = 0; column < 88; ++column) {
            float amount = (float)column / 87.0f;
            int topColor = ColorPickerScreen.lerpRgb(-1, pureHue, amount);
            context.method_25296(this.pickerX + column, this.pickerY, this.pickerX + column + 1, this.pickerY + 78, topColor, -16777216);
        }
        int knobX = Math.round((float)this.pickerX + this.saturation * 88.0f);
        int knobY = Math.round((float)this.pickerY + (1.0f - this.brightness) * 78.0f);
        context.method_25294(knobX - 4, knobY - 4, knobX + 5, knobY + 5, -1);
        context.method_25294(knobX - 2, knobY - 2, knobX + 3, knobY + 3, this.selectedColor());
        context.method_25294(this.pickerX - 1, this.hueY - 1, this.pickerX + 88 + 1, this.hueY + 6 + 1, -15592942);
        for (int column = 0; column < 88; ++column) {
            int color = 0xFF000000 | Color.HSBtoRGB((float)column / 87.0f, 1.0f, 1.0f) & 0xFFFFFF;
            context.method_25294(this.pickerX + column, this.hueY, this.pickerX + column + 1, this.hueY + 6, color);
        }
        int hueKnobX = Math.round((float)this.pickerX + this.hue * 88.0f);
        context.method_25294(hueKnobX - 1, this.hueY - 2, hueKnobX + 2, this.hueY + 6 + 2, -1);
    }

    private static int lerpRgb(int from, int to, float amount) {
        int red = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * amount);
        int green = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * amount);
        int blue = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * amount);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    public boolean method_25421() {
        return false;
    }
}

