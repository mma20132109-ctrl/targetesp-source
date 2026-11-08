/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 */
package ch.suobaru.TargetEspmod;

import java.util.Arrays;
import net.minecraft.class_2561;

public enum Variant {
    OFF("off", "variant.targetespmod.off"),
    IMAGE("image", "variant.targetespmod.image"),
    IMAGE_2("image_2", "variant.targetespmod.image_2"),
    IMAGE_3("image_3", "variant.targetespmod.image_3"),
    GHOSTS("ghosts", "variant.targetespmod.ghosts"),
    GHOSTS_2("ghosts_2", "variant.targetespmod.ghosts_2"),
    CUBES("cubes", "variant.targetespmod.cubes"),
    TRIANGLE("triangle", "variant.targetespmod.triangle"),
    RING("ring", "variant.targetespmod.ring"),
    CRYSTALS("crystals", "variant.targetespmod.crystals");

    private static final Variant[] VALUES;
    private final String id;
    private final String translationKey;

    private Variant(String id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public String id() {
        return this.id;
    }

    public class_2561 displayName() {
        return class_2561.method_43471((String)this.translationKey);
    }

    public Variant next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public static Variant fromId(String id) {
        if (id == null) {
            return OFF;
        }
        return Arrays.stream(VALUES).filter(variant -> variant.id.equalsIgnoreCase(id)).findFirst().orElse(OFF);
    }

    static {
        VALUES = Variant.values();
    }
}

