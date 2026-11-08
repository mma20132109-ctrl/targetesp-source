/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_156
 *  net.minecraft.class_1937
 *  org.jspecify.annotations.Nullable
 */
package ch.suobaru.TargetEspmod.target;

import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1937;
import org.jspecify.annotations.Nullable;

public final class TargetManager {
    public static final long TARGET_TTL_MS = 6000L;
    private class_1309 currentTarget;
    private long attackedAtMs;

    public void setCurrentTarget(class_1297 target) {
        class_1309 living;
        if (target instanceof class_1309 && TargetManager.isEligibleTarget(living = (class_1309)target)) {
            this.currentTarget = living;
            this.attackedAtMs = class_156.method_658();
        } else {
            this.reset();
        }
    }

    public void reset() {
        this.currentTarget = null;
        this.attackedAtMs = 0L;
    }

    public boolean tick(@Nullable class_1937 currentWorld) {
        if (this.currentTarget == null) {
            return false;
        }
        if (!TargetManager.isValidInWorld(this.currentTarget, currentWorld)) {
            this.reset();
            return true;
        }
        if (class_156.method_658() - this.attackedAtMs >= 6000L) {
            this.reset();
        }
        return false;
    }

    public @Nullable class_1309 getLivingTarget() {
        return this.currentTarget;
    }

    public static boolean isEligibleTarget(class_1309 living) {
        if (!living.method_5805() || living.method_31481()) {
            return false;
        }
        if (!living.method_5767()) {
            return true;
        }
        return !living.method_6118(class_1304.field_6169).method_7960() || !living.method_6118(class_1304.field_6174).method_7960() || !living.method_6118(class_1304.field_6172).method_7960() || !living.method_6118(class_1304.field_6166).method_7960();
    }

    private static boolean isValidInWorld(class_1309 living, @Nullable class_1937 currentWorld) {
        return currentWorld != null && living.method_73183() == currentWorld && TargetManager.isEligibleTarget(living);
    }
}

