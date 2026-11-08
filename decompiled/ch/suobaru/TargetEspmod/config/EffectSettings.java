/*
 * Decompiled with CFR 0.152.
 */
package ch.suobaru.TargetEspmod.config;

import ch.suobaru.TargetEspmod.Variant;

public record EffectSettings(float size, float speed, float radius, float waveHeight, int amount, int trailLength) {
    private static final Range STANDARD_SIZE = new Range(0.5, 2.0, 0.05);
    private static final Range GHOSTS_2_SIZE = new Range(0.05, 0.4, 0.01);
    private static final Range STANDARD_SPEED = new Range(0.2, 3.0, 0.05);
    private static final Range ROTATION_SPEED = new Range(0.0, 3.0, 0.05);

    public EffectSettings {
        if (!(Float.isFinite(size) && Float.isFinite(speed) && Float.isFinite(radius) && Float.isFinite(waveHeight))) {
            throw new IllegalArgumentException("Effect settings must contain finite numbers");
        }
        if (amount < 0 || trailLength < 0) {
            throw new IllegalArgumentException("Effect counts must not be negative");
        }
    }

    public static EffectSettings defaultsFor(Variant variant) {
        return switch (EffectSettings.safeVariant(variant)) {
            default -> throw new MatchException(null, null);
            case Variant.IMAGE, Variant.IMAGE_2, Variant.IMAGE_3 -> new EffectSettings(1.15f, 1.2f, 0.0f, 0.0f, 0, 0);
            case Variant.GHOSTS -> new EffectSettings(1.0f, 1.0f, 1.5f, 0.0f, 60, 0);
            case Variant.GHOSTS_2 -> new EffectSettings(0.2f, 2.0f, 0.75f, 0.55f, 3, 14);
            case Variant.CUBES -> new EffectSettings(1.0f, 1.0f, 0.7f, 0.0f, 40, 0);
            case Variant.TRIANGLE -> new EffectSettings(1.15f, 1.2f, 0.0f, 0.0f, 0, 0);
            case Variant.RING -> new EffectSettings(1.0f, 1.0f, 0.5f, 0.0f, 0, 0);
            case Variant.CRYSTALS -> new EffectSettings(1.0f, 1.0f, 1.5f, 0.0f, 18, 0);
            case Variant.OFF -> new EffectSettings(1.0f, 1.0f, 0.0f, 0.0f, 0, 0);
        };
    }

    public static EffectSettings fromRaw(Variant variant, Float size, Float speed, Float radius, Float waveHeight, Integer amount, Integer trailLength) {
        EffectSettings defaults = EffectSettings.defaultsFor(variant);
        EffectSettings candidate = new EffectSettings(EffectSettings.finiteOrDefault(size, defaults.size), EffectSettings.finiteOrDefault(speed, defaults.speed), EffectSettings.finiteOrDefault(radius, defaults.radius), EffectSettings.finiteOrDefault(waveHeight, defaults.waveHeight), amount == null || amount < 0 ? defaults.amount : amount, trailLength == null || trailLength < 0 ? defaults.trailLength : trailLength);
        return EffectSettings.sanitize(variant, candidate);
    }

    public static EffectSettings sanitize(Variant variant, EffectSettings settings) {
        Variant safeVariant = EffectSettings.safeVariant(variant);
        EffectSettings defaults = EffectSettings.defaultsFor(safeVariant);
        if (settings == null) {
            return defaults;
        }
        return new EffectSettings(EffectSettings.usesSize(safeVariant) ? (float)EffectSettings.sizeRange(safeVariant).normalize(settings.size, defaults.size) : defaults.size, EffectSettings.usesSpeed(safeVariant) ? (float)EffectSettings.speedRange(safeVariant).normalize(settings.speed, defaults.speed) : defaults.speed, EffectSettings.usesRadius(safeVariant) ? (float)EffectSettings.radiusRange(safeVariant).normalize(settings.radius, defaults.radius) : defaults.radius, EffectSettings.usesWaveHeight(safeVariant) ? (float)EffectSettings.waveHeightRange(safeVariant).normalize(settings.waveHeight, defaults.waveHeight) : defaults.waveHeight, EffectSettings.usesAmount(safeVariant) ? (int)Math.round(EffectSettings.amountRange(safeVariant).normalize(settings.amount, defaults.amount)) : defaults.amount, EffectSettings.usesTrailLength(safeVariant) ? (int)Math.round(EffectSettings.trailLengthRange(safeVariant).normalize(settings.trailLength, defaults.trailLength)) : defaults.trailLength);
    }

    public EffectSettings withSize(float value) {
        return new EffectSettings(value, this.speed, this.radius, this.waveHeight, this.amount, this.trailLength);
    }

    public EffectSettings withSpeed(float value) {
        return new EffectSettings(this.size, value, this.radius, this.waveHeight, this.amount, this.trailLength);
    }

    public EffectSettings withRadius(float value) {
        return new EffectSettings(this.size, this.speed, value, this.waveHeight, this.amount, this.trailLength);
    }

    public EffectSettings withWaveHeight(float value) {
        return new EffectSettings(this.size, this.speed, this.radius, value, this.amount, this.trailLength);
    }

    public EffectSettings withAmount(int value) {
        return new EffectSettings(this.size, this.speed, this.radius, this.waveHeight, value, this.trailLength);
    }

    public EffectSettings withTrailLength(int value) {
        return new EffectSettings(this.size, this.speed, this.radius, this.waveHeight, this.amount, value);
    }

    public static boolean hasSettings(Variant variant) {
        return EffectSettings.safeVariant(variant) != Variant.OFF;
    }

    public static boolean usesSize(Variant variant) {
        return switch (EffectSettings.safeVariant(variant)) {
            case Variant.IMAGE, Variant.IMAGE_2, Variant.IMAGE_3, Variant.GHOSTS, Variant.GHOSTS_2, Variant.CUBES, Variant.TRIANGLE, Variant.CRYSTALS -> true;
            default -> false;
        };
    }

    public static boolean usesSpeed(Variant variant) {
        return EffectSettings.safeVariant(variant) != Variant.OFF;
    }

    public static boolean usesRadius(Variant variant) {
        return switch (EffectSettings.safeVariant(variant)) {
            case Variant.GHOSTS, Variant.GHOSTS_2, Variant.CUBES, Variant.RING, Variant.CRYSTALS -> true;
            default -> false;
        };
    }

    public static boolean usesWaveHeight(Variant variant) {
        return EffectSettings.safeVariant(variant) == Variant.GHOSTS_2;
    }

    public static boolean usesAmount(Variant variant) {
        return switch (EffectSettings.safeVariant(variant)) {
            case Variant.GHOSTS, Variant.GHOSTS_2, Variant.CUBES, Variant.CRYSTALS -> true;
            default -> false;
        };
    }

    public static boolean usesTrailLength(Variant variant) {
        return EffectSettings.safeVariant(variant) == Variant.GHOSTS_2;
    }

    public static Range sizeRange(Variant variant) {
        return switch (EffectSettings.safeVariant(variant)) {
            case Variant.IMAGE, Variant.IMAGE_2, Variant.IMAGE_3, Variant.GHOSTS, Variant.CUBES, Variant.TRIANGLE, Variant.CRYSTALS -> STANDARD_SIZE;
            case Variant.GHOSTS_2 -> GHOSTS_2_SIZE;
            default -> EffectSettings.unsupported("size", variant);
        };
    }

    public static Range speedRange(Variant variant) {
        return switch (EffectSettings.safeVariant(variant)) {
            case Variant.IMAGE, Variant.IMAGE_2, Variant.IMAGE_3, Variant.TRIANGLE -> ROTATION_SPEED;
            case Variant.GHOSTS, Variant.RING, Variant.CRYSTALS -> STANDARD_SPEED;
            case Variant.GHOSTS_2 -> new Range(0.25, 4.0, 0.05);
            case Variant.CUBES -> new Range(0.5, 2.0, 0.05);
            default -> EffectSettings.unsupported("speed", variant);
        };
    }

    public static Range radiusRange(Variant variant) {
        return switch (EffectSettings.safeVariant(variant)) {
            case Variant.GHOSTS, Variant.CRYSTALS -> new Range(0.5, 2.5, 0.05);
            case Variant.GHOSTS_2, Variant.CUBES -> new Range(0.25, 1.5, 0.05);
            case Variant.RING -> new Range(0.25, 1.25, 0.05);
            default -> EffectSettings.unsupported("radius", variant);
        };
    }

    public static Range waveHeightRange(Variant variant) {
        if (EffectSettings.safeVariant(variant) == Variant.GHOSTS_2) {
            return new Range(0.0, 1.0, 0.05);
        }
        return EffectSettings.unsupported("wave height", variant);
    }

    public static Range amountRange(Variant variant) {
        return switch (EffectSettings.safeVariant(variant)) {
            case Variant.GHOSTS -> new Range(12.0, 96.0, 1.0);
            case Variant.GHOSTS_2 -> new Range(1.0, 3.0, 1.0);
            case Variant.CUBES -> new Range(12.0, 40.0, 1.0);
            case Variant.CRYSTALS -> new Range(6.0, 32.0, 1.0);
            default -> EffectSettings.unsupported("amount", variant);
        };
    }

    public static Range trailLengthRange(Variant variant) {
        if (EffectSettings.safeVariant(variant) == Variant.GHOSTS_2) {
            return new Range(4.0, 28.0, 1.0);
        }
        return EffectSettings.unsupported("trail length", variant);
    }

    private static float finiteOrDefault(Float value, float fallback) {
        return value != null && Float.isFinite(value.floatValue()) ? value.floatValue() : fallback;
    }

    private static Variant safeVariant(Variant variant) {
        return variant == null ? Variant.OFF : variant;
    }

    private static Range unsupported(String setting, Variant variant) {
        throw new IllegalArgumentException(setting + " is not used by " + String.valueOf((Object)EffectSettings.safeVariant(variant)));
    }

    public record Range(double min, double max, double step) {
        public Range {
            if (!Double.isFinite(min) || !Double.isFinite(max) || !Double.isFinite(step) || min > max || step <= 0.0) {
                throw new IllegalArgumentException("Invalid setting range");
            }
        }

        public double normalize(double value, double fallback) {
            double safeValue = Double.isFinite(value) ? value : fallback;
            double clamped = Math.clamp(safeValue, this.min, this.max);
            double snapped = this.min + (double)Math.round((clamped - this.min) / this.step) * this.step;
            return Math.clamp(snapped, this.min, this.max);
        }
    }
}

