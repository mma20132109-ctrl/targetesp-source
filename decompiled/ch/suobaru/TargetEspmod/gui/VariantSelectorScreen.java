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
import ch.suobaru.TargetEspmod.config.VariantConfig;
import ch.suobaru.TargetEspmod.gui.ColorPickerScreen;
import ch.suobaru.TargetEspmod.gui.VariantSettingsScreen;
import java.util.EnumMap;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public final class VariantSelectorScreen
extends class_437 {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 6;
    private Variant selectedVariant = VariantConfig.getAppliedVariant();
    private boolean hurtEnabled = VariantConfig.isHurtEnabled();
    private int selectedColor = VariantConfig.getColorArgb();
    private final EnumMap<Variant, EffectSettings> settingsDraft = VariantConfig.copySettings();
    private class_4185 variantButton;
    private class_4185 settingsButton;
    private class_4185 hurtButton;

    public VariantSelectorScreen() {
        super((class_2561)class_2561.method_43471((String)"screen.targetespmod.title"));
    }

    protected void method_25426() {
        int x = (this.field_22789 - 200) / 2;
        int totalHeight = 124;
        int topY = (this.field_22790 - totalHeight) / 2;
        int settingsY = topY + 20 + 6;
        int hurtY = settingsY + 20 + 6;
        int colorY = hurtY + 20 + 6;
        int applyY = colorY + 20 + 6;
        this.variantButton = (class_4185)this.method_37063((class_364)class_4185.method_46430((class_2561)this.variantButtonText(), button -> this.selectNextVariant()).method_46434(x, topY, 200, 20).method_46431());
        this.settingsButton = (class_4185)this.method_37063((class_364)class_4185.method_46430((class_2561)this.settingsButtonText(), button -> this.openVariantSettings()).method_46434(x, settingsY, 200, 20).method_46431());
        this.settingsButton.field_22763 = EffectSettings.hasSettings(this.selectedVariant);
        this.hurtButton = (class_4185)this.method_37063((class_364)class_4185.method_46430((class_2561)this.hurtButtonText(), button -> this.toggleHurt()).method_46434(x, hurtY, 200, 20).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)"screen.targetespmod.color"), button -> this.openColorPicker()).method_46434(x, colorY, 200, 20).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)"screen.targetespmod.apply"), button -> this.applySelection()).method_46434(x, applyY, 200, 20).method_46431());
    }

    private void selectNextVariant() {
        this.selectedVariant = this.selectedVariant.next();
        this.variantButton.method_25355(this.variantButtonText());
        this.settingsButton.method_25355(this.settingsButtonText());
        this.settingsButton.field_22763 = EffectSettings.hasSettings(this.selectedVariant);
    }

    private void toggleHurt() {
        this.hurtEnabled = !this.hurtEnabled;
        this.hurtButton.method_25355(this.hurtButtonText());
    }

    private void openColorPicker() {
        if (this.field_22787 != null) {
            this.field_22787.method_1507((class_437)new ColorPickerScreen(this, this.selectedColor, color -> {
                this.selectedColor = color;
            }));
        }
    }

    private void openVariantSettings() {
        if (this.field_22787 == null || !EffectSettings.hasSettings(this.selectedVariant)) {
            return;
        }
        Variant variant = this.selectedVariant;
        EffectSettings currentSettings = this.settingsDraft.getOrDefault((Object)variant, EffectSettings.defaultsFor(variant));
        this.field_22787.method_1507((class_437)new VariantSettingsScreen(this, variant, currentSettings, settings -> this.saveVariantSettings(variant, (EffectSettings)settings)));
    }

    private void saveVariantSettings(Variant variant, EffectSettings settings) {
        EffectSettings sanitized = EffectSettings.sanitize(variant, settings);
        this.settingsDraft.put(variant, sanitized);
        boolean saved = VariantConfig.saveSettings(variant, sanitized);
        if (this.field_22787 != null && this.field_22787.field_1724 != null) {
            this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43469((String)(saved ? "message.targetespmod.settings_saved" : "message.targetespmod.settings_save_failed"), (Object[])new Object[]{variant.displayName()}), true);
        }
    }

    private class_2561 variantButtonText() {
        return class_2561.method_43469((String)"screen.targetespmod.variant_button", (Object[])new Object[]{this.selectedVariant.displayName()});
    }

    private class_2561 settingsButtonText() {
        return class_2561.method_43469((String)"screen.targetespmod.settings", (Object[])new Object[]{this.selectedVariant.displayName()});
    }

    private class_2561 hurtButtonText() {
        return class_2561.method_43469((String)"screen.targetespmod.hurt", (Object[])new Object[]{class_2561.method_43471((String)(this.hurtEnabled ? "options.on" : "options.off"))});
    }

    private void applySelection() {
        VariantConfig.applyAndSave(this.selectedVariant, this.hurtEnabled, this.selectedColor, this.settingsDraft);
        if (this.field_22787 != null) {
            if (this.field_22787.field_1724 != null) {
                this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43469((String)"message.targetespmod.applied", (Object[])new Object[]{this.selectedVariant.displayName()}), true);
            }
            this.field_22787.method_1507(null);
        }
    }

    public void method_25419() {
        if (this.field_22787 != null) {
            this.field_22787.method_1507(null);
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

